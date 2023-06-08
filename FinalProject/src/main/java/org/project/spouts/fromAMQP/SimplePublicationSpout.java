/*
    Initial code for this class has been taken from the book:
    Practical Real-time Data Processing and Analytics:
    Distributed Computing and Event Processing using Apache Spark, Flink, Storm, and Kafka
    by Shilpi Saxena, Saurabh Gupta

    The code has been modified to suit the needs of this project.
 */

package org.project.spouts.fromAMQP;

import com.rabbitmq.client.*;
import org.apache.log4j.Logger;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.project.cofiguration.GlobalConfiguration.*;

public class SimplePublicationSpout implements IRichSpout {
    private static final org.apache.log4j.Logger LOG = Logger.getLogger(SimplePublicationSpout.class);
    public static final String ID = SimplePublicationSpout.class.toString();
    private final String amqpHost;
    private final int amqpPort;
    private final String amqpUsername;
    private final String amqpPasswd;
    private final String amqpVhost;
    private final boolean requeueOnFail;
    private final boolean autoAck;
    private int prefetchCount;
    private SpoutOutputCollector collector;
    private Connection amqpConnection;
    private Channel amqpChannel;
    private QueueingConsumer amqpConsumer;
    private String amqpConsumerTag;
    private boolean spoutActive;

    // The constructor where we set initialize all properties
    public SimplePublicationSpout(String host, int port, String username, String password, String vhost, boolean requeueOnFail, boolean autoAck) {
        this.amqpHost = host;
        this.amqpPort = port;
        this.amqpUsername = username;
        this.amqpPasswd = password;
        this.amqpVhost = vhost;
        this.requeueOnFail = requeueOnFail;
        this.autoAck = autoAck;
    }

    /*
     * Open method of the spout , here we initialize the prefetch count, this
     * parameter specified how many messages would be prefetched from the queue
     * by the spout - to increase the efficiency of the solution
     */
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        Long prefetchCount = (Long) conf.get(CONFIG_PREFETCH_COUNT);
        if (prefetchCount == null) {
            prefetchCount = DEFAULT_PREFETCH_COUNT;
        } else if (prefetchCount < 1) {
            throw new IllegalArgumentException(CONFIG_PREFETCH_COUNT + " must be at least 1");
        }
        this.prefetchCount = prefetchCount.intValue();

        try {
            this.collector = collector;

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

    /**
     * Reconnect to an AMQP broker.in case the connection breaks at some
     * point
     *
     * @throws TimeoutException
     */
    private void reconnect() throws TimeoutException {
        LOG.info("Reconnecting to AMQP broker...");
        try {
            setupAMQP();
        } catch (IOException e) {
            LOG.warn("Failed to reconnect to AMQP broker", e);
        }
    }

    /**
     * Set up a connection with an AMQP broker.
     *
     * @throws IOException      This is the method where we actually connect to the queue
     *                          using AMQP client APIs
     * @throws TimeoutException
     */
    private void setupAMQP() throws IOException, TimeoutException {
        final int prefetchCount = this.prefetchCount;
        final ConnectionFactory connectionFactory = new ConnectionFactory() {
            public void configureSocket(Socket socket) throws IOException {
                socket.setTcpNoDelay(false);
                socket.setReceiveBufferSize(20 * 1024);
                socket.setSendBufferSize(20 * 1024);
            }
        };
        connectionFactory.setHost(amqpHost);
        connectionFactory.setPort(amqpPort);
        connectionFactory.setUsername(amqpUsername);
        connectionFactory.setPassword(amqpPasswd);
        connectionFactory.setVirtualHost(amqpVhost);
        this.amqpConnection = connectionFactory.newConnection();
        this.amqpChannel = amqpConnection.createChannel();
        this.amqpChannel.basicQos(prefetchCount);
        this.amqpChannel.exchangeDeclare(SIMPLE_PUBLICATION_EXCHANGE_NAME, "direct");
        this.amqpChannel.queueDeclare(SIMPLE_PUBLICATION_QUEUE_NAME, true, false, false, null);
        this.amqpChannel.queueBind(SIMPLE_PUBLICATION_QUEUE_NAME, SIMPLE_PUBLICATION_EXCHANGE_NAME, "");
        this.amqpConsumer = new QueueingConsumer(amqpChannel);
        this.amqpConsumerTag = amqpChannel.basicConsume(SIMPLE_PUBLICATION_QUEUE_NAME, this.autoAck, amqpConsumer);
    }

    /*
     * Cancels the queue subscription, and disconnects from the AMQP broker.
     */
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
        try {
            if (amqpConnection != null) {
                amqpConnection.close();
            }
        } catch (IOException e) {
            LOG.warn("Error closing AMQP connection: ", e);
        }
    }

    /*
     * Emit message received from queue into collector
     */
    public void nextTuple() {
        if (spoutActive && amqpConsumer != null) {
            try {
                final QueueingConsumer.Delivery delivery = amqpConsumer.nextDelivery(WAIT_FOR_NEXT_MESSAGE);
                if (delivery == null) return;
                final long deliveryTag = delivery.getEnvelope().getDeliveryTag();
                String message = new String(delivery.getBody());
                if (message.length() > 0) {
                    collector.emit(new Values(message), deliveryTag);
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

    /*
     * ack method to acknowledge the message that is successfully processed
     */
    public void ack(Object msgId) {
        if (msgId instanceof Long) {
            final long deliveryTag = (Long) msgId;
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
            LOG.warn(String.format("don't know how to ack(%s: %s)", msgId.getClass().getName(), msgId));
        }
    }

    public void fail(Object msgId) {
        if (msgId instanceof Long) {
            final long deliveryTag = (Long) msgId;
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
            LOG.warn(String.format("don't know how to reject(%s: %s)", msgId.getClass().getName(), msgId));
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("messages"));
    }

    public void activate() {
    }

    public void deactivate() {
    }

    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}