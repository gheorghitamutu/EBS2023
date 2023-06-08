package org.project.bolts.toAMQP;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.project.cofiguration.GlobalConfiguration.*;

public class AnomalyBolt extends BaseRichBolt {

    public static final String ID = AnomalyBolt.class.toString();
    private static final Logger LOG = Logger.getLogger(AnomalyBolt.class);
    private OutputCollector collector;
    private int eventsReceived;
    private final String host;
    private final int port;
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channelSimpleAnomaly;
    private Channel channelComplexAnomaly;

    public AnomalyBolt(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void prepare(Map<String, Object> map, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        this.eventsReceived = 0;

        this.factory = new ConnectionFactory();
        Address[] address = { new Address(this.host, this.port) };
        try {
            this.connection = this.factory.newConnection(address);

            this.channelSimpleAnomaly = this.connection.createChannel();
            this.channelSimpleAnomaly.confirmSelect();
            this.channelSimpleAnomaly.exchangeDeclare(SIMPLE_ANOMALY_EXCHANGE_NAME, "direct");
            this.channelSimpleAnomaly.queueDeclare(SIMPLE_ANOMALY_QUEUE_NAME, true, false, false, null);
            this.channelSimpleAnomaly.queueBind(SIMPLE_ANOMALY_QUEUE_NAME, SIMPLE_ANOMALY_EXCHANGE_NAME, SIMPLE_ANOMALY_ROUTING_KEY);

            this.channelComplexAnomaly = this.connection.createChannel();
            this.channelComplexAnomaly.confirmSelect();
            this.channelComplexAnomaly.exchangeDeclare(COMPLEX_ANOMALY_EXCHANGE_NAME, "direct");
            this.channelComplexAnomaly.queueDeclare(COMPLEX_ANOMALY_QUEUE_NAME, true, false, false, null);
            this.channelComplexAnomaly.queueBind(COMPLEX_ANOMALY_QUEUE_NAME, COMPLEX_ANOMALY_EXCHANGE_NAME, COMPLEX_ANOMALY_ROUTING_KEY);
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void execute(Tuple input) {
        if (input.contains("AnomalyType")) {
            var anomalyType = input.getValueByField("AnomalyType");
            if (input.contains("SimplePublication")) {
                var sp = (org.project.models.ProtoSimplePublication.SimplePublication) input.getValueByField("SimplePublication");
                // LOG.info(MessageFormat.format("Simple Publication Event received: #{0}\n{1}\n{2}", eventsReceived, anomalyType, sp));

                try {
                    this.channelSimpleAnomaly.basicPublish(
                            SIMPLE_ANOMALY_EXCHANGE_NAME,
                            SIMPLE_ANOMALY_ROUTING_KEY,
                            null,
                            sp.toByteArray());
                    this.channelSimpleAnomaly.waitForConfirmsOrDie(AMQP_ACK_TIMEOUT);
                } catch (IOException | InterruptedException | TimeoutException e) {
                    throw new RuntimeException(e);
                }
            }

            if (input.contains("ComplexPublication")) {
                var cp = (org.project.models.ProtoComplexPublication.ComplexPublication) input.getValueByField("ComplexPublication");
                // LOG.info(MessageFormat.format("Complex Publication Event received: #{0}\n{1}\n{2}", eventsReceived, anomalyType, cp));

                try {
                    this.channelComplexAnomaly.basicPublish(
                            COMPLEX_ANOMALY_EXCHANGE_NAME,
                            COMPLEX_ANOMALY_ROUTING_KEY,
                            null,
                            cp.toByteArray());
                    this.channelComplexAnomaly.waitForConfirmsOrDie(AMQP_ACK_TIMEOUT);
                } catch (IOException | InterruptedException | TimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else {
            LOG.info(MessageFormat.format("Unknown fields: {0}", input.getFields()));
        }

        this.collector.ack(input);
        eventsReceived++;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // TODO: nothing to declare
    }

    @Override
    public void cleanup() {
        LOG.info(MessageFormat.format("Anomaly events received: {0}!", this.eventsReceived));
    }
}
