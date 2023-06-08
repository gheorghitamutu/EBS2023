package org.project.bolts.toAMQP;

import com.rabbitmq.client.*;
import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.project.models.ProtoSimplePublication;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.project.cofiguration.GlobalConfiguration.*;

public class SimplePublicationBolt extends BaseRichBolt {

    public static final String ID = SimplePublicationBolt.class.toString();
    private static final Logger LOG = Logger.getLogger(SimplePublicationBolt.class);
    private OutputCollector collector;
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private final String host;
    private final int port;

    public SimplePublicationBolt(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void prepare(Map<String, Object> configuration, TopologyContext context, OutputCollector collector) {
        this.collector = collector;

        this.factory = new ConnectionFactory();
        Address[] address = { new Address(this.host, this.port) };
        try {
            this.connection = this.factory.newConnection(address);
            this.channel = this.connection.createChannel();
            this.channel.confirmSelect();
            this.channel.exchangeDeclare(SIMPLE_PUBLICATION_EXCHANGE_NAME, "direct");
            this.channel.queueDeclare(SIMPLE_PUBLICATION_QUEUE_NAME, true, false, false, null);
            this.channel.queueBind(SIMPLE_PUBLICATION_QUEUE_NAME, SIMPLE_PUBLICATION_EXCHANGE_NAME, SIMPLE_PUBLICATION_ROUTING_KEY);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execute(Tuple input) {
        var sp = (ProtoSimplePublication.SimplePublication) input.getValueByField("SimplePublication");
        try {
            channel.basicPublish(
                    SIMPLE_PUBLICATION_EXCHANGE_NAME, SIMPLE_PUBLICATION_ROUTING_KEY,
                    MessageProperties.PERSISTENT_BASIC,
                    sp.toByteArray());
            channel.waitForConfirmsOrDie(AMQP_ACK_TIMEOUT);
            LOG.info(" [x] Sent: " + sp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.collector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // TODO: nothing to declare
    }

    @Override
    public void cleanup() {
        try {
            this.channel.close();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        try {
            this.connection.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
