package org.project.bolts.toAMQP;

import com.rabbitmq.client.*;
import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.project.models.ProtoComplexPublication;
import org.project.models.ProtoSimplePublication;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.project.cofiguration.GlobalConfiguration.*;

public class ComplexPublicationBolt extends BaseRichBolt {

    public static final String ID = ComplexPublicationBolt.class.toString();
    private static final Logger LOG = Logger.getLogger(ComplexPublicationBolt.class);
    private OutputCollector collector;
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private final String host;
    private final int port;

    public ComplexPublicationBolt(String host, int port) {
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
            this.channel.exchangeDeclare(COMPLEX_PUBLICATION_EXCHANGE_NAME, "direct");
            this.channel.queueDeclare(COMPLEX_PUBLICATION_QUEUE_NAME, true, false, false, null);
            this.channel.queueBind(COMPLEX_PUBLICATION_QUEUE_NAME, COMPLEX_PUBLICATION_EXCHANGE_NAME, COMPLEX_PUBLICATION_ROUTING_KEY);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execute(Tuple input) {
        var cp = (ProtoComplexPublication.ComplexPublication) input.getValueByField("ComplexPublication");
        try {
            channel.basicPublish(
                    COMPLEX_PUBLICATION_EXCHANGE_NAME, COMPLEX_PUBLICATION_ROUTING_KEY,
                    MessageProperties.PERSISTENT_BASIC,
                    cp.toByteArray());
            channel.waitForConfirmsOrDie(AMQP_ACK_TIMEOUT);
            LOG.info(" [x] Sent: " + cp);
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
