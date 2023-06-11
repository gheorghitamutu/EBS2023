package org.project.bolts.toAMQP;

import com.rabbitmq.client.*;
import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.project.rabbit.ConnectionManager;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.project.cofiguration.GlobalConfiguration.*;

public class AnomalyBolt extends BaseRichBolt {

    public static final String ID = AnomalyBolt.class.getCanonicalName();
    private static final Logger LOG = Logger.getLogger(AnomalyBolt.class);
    private OutputCollector collector;
    private int eventsReceived;
    private Channel channelSimpleAnomaly;
    private Channel channelComplexAnomaly;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        this.eventsReceived = 0;

        final ConnectionManager cm = ConnectionManager.getInstance();
        this.channelSimpleAnomaly = cm.GetChannel(
                (int) DEFAULT_PREFETCH_COUNT,
                SIMPLE_ANOMALY_EXCHANGE_NAME,
                SIMPLE_ANOMALY_QUEUE_NAME,
                SIMPLE_ANOMALY_ROUTING_KEY);
        this.channelComplexAnomaly = cm.GetChannel(
                (int) DEFAULT_PREFETCH_COUNT,
                COMPLEX_ANOMALY_EXCHANGE_NAME,
                COMPLEX_ANOMALY_QUEUE_NAME,
                COMPLEX_ANOMALY_ROUTING_KEY);
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
                            MessageProperties.PERSISTENT_BASIC,
                            sp.toByteArray());
                    this.channelSimpleAnomaly.waitForConfirmsOrDie(AMQP_ACK_TIMEOUT);
                } catch (IOException | InterruptedException | TimeoutException e) {
                    // collector.reportError(e);
                    this.collector.fail(input);
                    LOG.error(e.getMessage());
                    return;
                }
            }

            if (input.contains("ComplexPublication")) {
                var cp = (org.project.models.ProtoComplexPublication.ComplexPublication) input.getValueByField("ComplexPublication");
                // LOG.info(MessageFormat.format("Complex Publication Event received: #{0}\n{1}\n{2}", eventsReceived, anomalyType, cp));

                try {
                    this.channelComplexAnomaly.basicPublish(
                            COMPLEX_ANOMALY_EXCHANGE_NAME,
                            COMPLEX_ANOMALY_ROUTING_KEY,
                            MessageProperties.PERSISTENT_BASIC,
                            cp.toByteArray());
                    this.channelComplexAnomaly.waitForConfirmsOrDie(AMQP_ACK_TIMEOUT);
                } catch (IOException | InterruptedException | TimeoutException e) {
                    // collector.reportError(e);
                    this.collector.fail(input);
                    LOG.error(e.getMessage());
                    return;
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
