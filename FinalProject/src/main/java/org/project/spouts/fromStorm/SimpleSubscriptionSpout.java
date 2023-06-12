package org.project.spouts.fromStorm;

import org.apache.log4j.Logger;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.project.data.City;
import org.project.data.SubscriptionGenerator;
import org.project.models.ProtoSimpleSubscription;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.UUID.randomUUID;
import static org.project.cofiguration.GlobalConfiguration.SIMPLE_SUBSCRIPTION_COUNT;

public class SimpleSubscriptionSpout extends BaseRichSpout {

    private SpoutOutputCollector collector;
    private String taskName;
    private Map<String, ProtoSimpleSubscription.SimpleSubscription> unconfirmed;

    // we need this one shared between spouts
    private static final AtomicInteger simpleSubscriptionCount = new AtomicInteger(0);
    private static final Logger LOG = Logger.getLogger(SimpleSubscriptionSpout.class);
    public static final String ID = SimpleSubscriptionSpout.class.getCanonicalName();

    @Override
    public void open(Map<String, Object> conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        this.taskName = MessageFormat.format("<{0} <-> {0}>", context.getThisComponentId(), context.getThisTaskId());
        this.unconfirmed = new HashMap<>();
    }

    @Override
    public void nextTuple() {
        if (simpleSubscriptionCount.get() >= SIMPLE_SUBSCRIPTION_COUNT) {
            return;
        }

        simpleSubscriptionCount.set(simpleSubscriptionCount.get() + 1);

        var ss = SubscriptionGenerator.getInstance().generateSimple();
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

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("SimpleSubscription"));
    }

    @Override
    public void close() {
        LOG.info(MessageFormat.format("SimpleSubscriptionSpout {0} has generated {1} SimpleSubscriptions", this.taskName, this.simpleSubscriptionCount));
    }
}
