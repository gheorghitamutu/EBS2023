package org.project.bolts.toAMQP;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.apache.log4j.Logger;
import org.apache.storm.metric.api.AssignableMetric;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.project.models.ProtoComplexPublication;
import org.project.models.ProtoSimplePublication;
import org.project.rabbit.ConnectionManager;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static java.lang.Math.abs;
import static org.project.cofiguration.GlobalConfiguration.*;

public class PublicationViaSubscriptionBolt extends BaseRichBolt {

    public static final String ID = PublicationViaSubscriptionBolt.class.getCanonicalName();
    private static final Logger LOG = Logger.getLogger(PublicationViaSubscriptionBolt.class);
    private OutputCollector collector;
    private int eventsReceived;
    private Channel channelSimplePublication;
    private Channel channelComplexPublication;

    private AssignableMetric latencyForDeliverySimplePublication;
    private AssignableMetric latencyForDeliveryComplexPublication;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        this.eventsReceived = 0;

        final ConnectionManager cm = ConnectionManager.getInstance();
        this.channelSimplePublication = cm.GetChannel(
                (int) DEFAULT_PREFETCH_COUNT,
                SIMPLE_PUBLICATION_VIA_SUBSCRIPTION_EXCHANGE_NAME,
                SIMPLE_PUBLICATION_VIA_SUBSCRIPTION_QUEUE_NAME,
                SIMPLE_PUBLICATION_VIA_SUBSCRIPTION_ROUTING_KEY);
        this.channelComplexPublication = cm.GetChannel(
                (int) DEFAULT_PREFETCH_COUNT,
                COMPLEX_PUBLICATION_VIA_SUBSCRIPTION_EXCHANGE_NAME,
                COMPLEX_PUBLICATION_VIA_SUBSCRIPTION_QUEUE_NAME,
                COMPLEX_PUBLICATION_VIA_SUBSCRIPTION_ROUTING_KEY);

        this.latencyForDeliverySimplePublication = new AssignableMetric(0);
        this.latencyForDeliveryComplexPublication = new AssignableMetric(0);

        context.registerMetric(METRICS_LATENCY_SIMPLE_PUBLICATION_DELIVERY, latencyForDeliverySimplePublication, TIME_BUCKET_SIZE_IN_SECS);
        context.registerMetric(METRICS_LATENCY_COMPLEX_PUBLICATION_DELIVERY, latencyForDeliveryComplexPublication, TIME_BUCKET_SIZE_IN_SECS);
    }

    @Override
    public void execute(Tuple input) {
        var fields = input.getFields();
        if (fields.size() != 2)
        {
            var msg = MessageFormat.format("Expected 2 fields, got {0}", fields.size());
            this.collector.fail(input);
            LOG.error(msg);
            return;
        }

        var f0 = fields.get(0);
        var f1 = fields.get(1);

        if (!f0.equals("Subscribers")) {
            var msg = MessageFormat.format("Expected field 0 to be Subscribers, got {0}", f0);
            this.collector.fail(input);
            LOG.error(msg);
            return;
        }

        if (!f1.equals("SimplePublication") && !f1.equals("ComplexPublication")) {
            var msg = MessageFormat.format("Expected field 1 to be SimplePublication or ComplexPublication, got {0}", f1);
            this.collector.fail(input);
            LOG.error(msg);
            return;
        }

        var subscribers = (List<String>) input.getValueByField("Subscribers");
        var pair = new HashMap<List<String>, String>()
        {{
            put(subscribers, input.getValueByField(f1).toString());
        }};

        ObjectMapper objectMapper = new ObjectMapper();
        var json = "";
        try {
            json = objectMapper.writeValueAsString(pair);
        } catch (IOException e) {
            // collector.reportError(e);
            this.collector.fail(input);
            LOG.error(e.getMessage());
            return;
        }

        if (f1.equals("SimplePublication")) {
            try {
                this.channelSimplePublication.basicPublish(
                        SIMPLE_PUBLICATION_VIA_SUBSCRIPTION_EXCHANGE_NAME,
                        SIMPLE_PUBLICATION_VIA_SUBSCRIPTION_ROUTING_KEY,
                        null,
                        json.getBytes());
                this.channelSimplePublication.waitForConfirmsOrDie(AMQP_ACK_TIMEOUT);

                var sp = (ProtoSimplePublication.SimplePublication) input.getValueByField(f1);
                this.latencyForDeliverySimplePublication.setValue((Long)abs(System.currentTimeMillis() - sp.getGenerationTimestamp()));
            } catch (IOException | InterruptedException | TimeoutException e) {
                // collector.reportError(e);
                this.collector.fail(input);
                LOG.error(e.getMessage());
                return;
            }
        }
        else {
            try {
                this.channelComplexPublication.basicPublish(
                        COMPLEX_PUBLICATION_VIA_SUBSCRIPTION_EXCHANGE_NAME,
                        COMPLEX_PUBLICATION_VIA_SUBSCRIPTION_ROUTING_KEY,
                        null,
                        json.getBytes());
                this.channelComplexPublication.waitForConfirmsOrDie(AMQP_ACK_TIMEOUT);

                var cp = (ProtoComplexPublication.ComplexPublication) input.getValueByField(f1);
                this.latencyForDeliveryComplexPublication.setValue((Long)abs(System.currentTimeMillis() - cp.getTimestamp()));
            } catch (IOException | InterruptedException | TimeoutException e) {
                // collector.reportError(e);
                this.collector.fail(input);
                LOG.error(e.getMessage());
                return;
            }
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
        LOG.info(MessageFormat.format("Subscribed publication events received: {0}!", this.eventsReceived));
    }
}
