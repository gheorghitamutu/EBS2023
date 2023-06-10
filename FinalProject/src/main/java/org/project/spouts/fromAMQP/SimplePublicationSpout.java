/*
    Initial code for this class has been taken from the book:
    Practical Real-time Data Processing and Analytics:
    Distributed Computing and Event Processing using Apache Spark, Flink, Storm, and Kafka
    by Shilpi Saxena, Saurabh Gupta

    The code has been modified to suit the needs of this project.
 */

package org.project.spouts.fromAMQP;

import com.google.protobuf.InvalidProtocolBufferException;
import com.rabbitmq.client.*;
import org.apache.log4j.Logger;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;
import org.project.models.ProtoSimplePublication;
import org.project.rabbit.ConnectionManager;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.project.cofiguration.GlobalConfiguration.*;

public class SimplePublicationSpout implements IRichSpout {
    private static final org.apache.log4j.Logger LOG = Logger.getLogger(SimplePublicationSpout.class);
    public static final String ID = SimplePublicationSpout.class.getCanonicalName();
    private String taskName;
    private final boolean requeueOnFail;
    private final boolean autoAck;
    private int prefetchCount;
    private SpoutOutputCollector collector;
    private Channel amqpChannel;
    private QueueingConsumer amqpConsumer;
    private String amqpConsumerTag;

    public SimplePublicationSpout(boolean requeueOnFail, boolean autoAck) {
        this.requeueOnFail = requeueOnFail;
        this.autoAck = autoAck;
    }

    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        this.taskName = MessageFormat.format("<{0} <-> {0}>", context.getThisComponentId(), context.getThisTaskId());

        Long prefetchCount = (Long) conf.get(CONFIG_PREFETCH_COUNT);
        if (prefetchCount == null) {
            prefetchCount = DEFAULT_PREFETCH_COUNT;
        } else if (prefetchCount < 1) {
            throw new IllegalArgumentException(CONFIG_PREFETCH_COUNT + " must be at least 1");
        }
        this.prefetchCount = prefetchCount.intValue();

        try {
            setupAMQP();
        } catch (IOException e) {
            LOG.error("AMQP setup failed", e);
            LOG.warn("AMQP setup failed, will attempt to reconnect...");
            Utils.sleep(WAIT_AFTER_SHUTDOWN_SIGNAL);
            try {
                reconnect();
            } catch (TimeoutException e1) {
                e1.printStackTrace();
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void reconnect() throws TimeoutException {
        LOG.info("Reconnecting to AMQP broker...");
        try {
            setupAMQP();
        } catch (IOException e) {
            LOG.warn("Failed to reconnect to AMQP broker", e);
        }
    }

    private void setupAMQP() throws IOException, TimeoutException {
        final ConnectionManager cm = ConnectionManager.getInstance();
        this.amqpChannel = cm.GetChannel(
                this.prefetchCount,
                SIMPLE_PUBLICATION_EXCHANGE_NAME,
                SIMPLE_PUBLICATION_QUEUE_NAME,
                "");
        this.amqpConsumer = new QueueingConsumer(amqpChannel);
        this.amqpConsumerTag = amqpChannel.basicConsume(SIMPLE_PUBLICATION_QUEUE_NAME, this.autoAck, amqpConsumer);
    }

    public void close() {
        try {
            if (amqpChannel != null) {
                if (amqpConsumerTag != null) {
                    amqpChannel.basicCancel(amqpConsumerTag);
                }
                amqpChannel.close();
            }
        } catch (IOException e) {
            LOG.warn("Error closing AMQP channel: ", e);
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    public void nextTuple() {
        if (amqpConsumer != null) {
            try {
                final QueueingConsumer.Delivery delivery = amqpConsumer.nextDelivery(WAIT_FOR_NEXT_MESSAGE);
                if (delivery == null) return;
                final long deliveryTag = delivery.getEnvelope().getDeliveryTag();
                if (delivery.getBody().length > 0) {
                    ProtoSimplePublication.SimplePublication sp;
                    try {
                        sp = ProtoSimplePublication.SimplePublication.parseFrom(delivery.getBody());
                    } catch (InvalidProtocolBufferException e) {
                        throw new RuntimeException(e);
                    }
                    collector.emit(new Values(sp), deliveryTag);
                } else {
                    LOG.debug("Malformed deserialized message, null or zero - length." + deliveryTag);
                    if (!this.autoAck) {
                        ack(deliveryTag);
                    }
                }
            } catch (ShutdownSignalException e) {
                LOG.warn("AMQP connection dropped, will attempt to reconnect...");
                Utils.sleep(WAIT_AFTER_SHUTDOWN_SIGNAL);
                try {
                    reconnect();
                } catch (TimeoutException e1) {
                    e1.printStackTrace();
                }
            } catch (ConsumerCancelledException e) {
                LOG.warn("AMQP consumer cancelled, will attempt to reconnect...");
                Utils.sleep(WAIT_AFTER_SHUTDOWN_SIGNAL);
                try {
                    reconnect();
                } catch (TimeoutException e1) {
                    e1.printStackTrace();
                }
            } catch (InterruptedException e) {
                LOG.error("Interrupted while reading a message, with Exception :" + e);
            }
        }
    }

    public void ack(Object id) {
        // LOG.info(MessageFormat.format("ACKED detected at {0} for {1}!", this.taskName, id));

        if (id instanceof Long) {
            final long deliveryTag = (Long) id;
            if (amqpChannel != null) {
                try {
                    amqpChannel.basicAck(deliveryTag, false);
                } catch (IOException e) {
                    LOG.warn("Failed to ack delivery-tag " + deliveryTag, e);
                } catch (ShutdownSignalException e) {
                    LOG.warn("AMQP connection failed. Failed to ack delivery - tag" + deliveryTag, e);
                }
            }
        } else {
            LOG.warn(String.format("don't know how to ack(%s: %s)", id.getClass().getName(), id));
        }
    }

    public void fail(Object id) {
        // LOG.info(MessageFormat.format("FAILURE detected at {0} for {1}!", this.taskName, id));

        if (id instanceof Long) {
            final long deliveryTag = (Long) id;
            if (amqpChannel != null) {
                try {
                    if (amqpChannel.isOpen()) {
                        if (!this.autoAck) {
                            amqpChannel.basicReject(deliveryTag, requeueOnFail);
                        }
                    } else {
                        reconnect();
                    }
                } catch (IOException e) {
                    LOG.warn("Failed to reject delivery-tag " + deliveryTag, e);
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        } else {
            LOG.warn(String.format("Don't know how to reject(%s: %s)", id.getClass().getName(), id));
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("SimplePublication"));
    }

    public void activate() {
    }

    public void deactivate() {
    }

    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}