package org.project.spouts.fromStorm;

import org.apache.log4j.Logger;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.project.data.City;
import org.project.models.ProtoSimpleSubscription;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import static java.util.UUID.randomUUID;
import static org.project.cofiguration.GlobalConfiguration.SIMPLE_SUBSCRIPTION_COUNT;

public class SimpleSubscriptionSpout extends BaseRichSpout {

    private SpoutOutputCollector collector;
    private String taskName;
    private Map<String, ProtoSimpleSubscription.SimpleSubscription> unconfirmed;
    private int simpleSubscriptionCount;
    private static final Logger LOG = Logger.getLogger(SimpleSubscriptionSpout.class);
    public static final String ID = SimpleSubscriptionSpout.class.toString();

    @Override
    public void open(Map<String, Object> conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        this.simpleSubscriptionCount = 0;
        this.taskName = MessageFormat.format("<{0} <-> {0}>", context.getThisComponentId(), context.getThisTaskId());
        this.unconfirmed = new HashMap<>();
    }

    @Override
    public void nextTuple() {
        if (simpleSubscriptionCount >= SIMPLE_SUBSCRIPTION_COUNT) {
            return;
        }

        simpleSubscriptionCount++;

        var ss = SimpleSubscriptionSpout.SimpleSubscriptionGenerator.generateSimpleSubscription();
        unconfirmed.put(ss.getSubscriptionId(), ss);

        this.collector.emit(new Values(ss), ss.getSubscriptionId());
    }

    @Override
    public void ack(Object id) {
        var uuid = (String)id;
        // LOG.info(MessageFormat.format("ACKED detected at {0} for {1}!", this.taskName, uuid));
        this.unconfirmed.remove(uuid);
    }

    @Override
    public void fail(Object id) {
        var uuid = (String)id;
        // LOG.info(MessageFormat.format("FAILURE detected at {0} for {1}!", this.taskName, uuid));
        this.collector.emit(new Values(this.unconfirmed.get(uuid)), uuid);
    }

    public static class SimpleSubscriptionGenerator {
        public static ProtoSimpleSubscription.SimpleSubscription generateSimpleSubscription() {
            return ProtoSimpleSubscription.SimpleSubscription.newBuilder()
                    .setSubscriptionId(randomUUID().toString())
                    .setConditions(
                            ProtoSimpleSubscription.SimplePublicationCondition.newBuilder()
                                    .setCity(
                                            ProtoSimpleSubscription.ConditionString.newBuilder()
                                                    .setOperatorValue(ProtoSimpleSubscription.Operator.EQUAL_VALUE)
                                                    .setValue(new City(City.Name.SAN_FRANCISCO).ToString())
                                                    .build()
                                    )
                                    .setTemperature(
                                            ProtoSimpleSubscription.ConditionDouble.newBuilder()
                                                    .setOperatorValue(ProtoSimpleSubscription.Operator.NONE_VALUE)
                                                    .build()
                                    )
                                    .setRain(
                                            ProtoSimpleSubscription.ConditionDouble.newBuilder()
                                                    .setOperatorValue(ProtoSimpleSubscription.Operator.NONE_VALUE)
                                                    .build()
                                    )
                                    .setWind(
                                            ProtoSimpleSubscription.ConditionDouble.newBuilder()
                                                    .setOperatorValue(ProtoSimpleSubscription.Operator.NONE_VALUE)
                                                    .build()
                                    )
                                    .build()
                    )
                    .setTimestamp(System.currentTimeMillis())
                    .build();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("SimpleSubscription"));
    }

    @Override
    public void close() {
        LOG.info(MessageFormat.format("SimpleSubscriptionSpout {0} has generated {1} SimpleSubscriptions", this.taskName, this.simpleSubscriptionCount));
    }
}
