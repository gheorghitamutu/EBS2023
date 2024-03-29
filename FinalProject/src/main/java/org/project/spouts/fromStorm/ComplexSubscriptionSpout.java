package org.project.spouts.fromStorm;

import org.apache.log4j.Logger;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.project.data.SubscriptionGenerator;
import org.project.models.ProtoComplexSubscription;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.project.cofiguration.GlobalConfiguration.COMPLEX_SUBSCRIPTION_COUNT;
import static org.project.cofiguration.GlobalConfiguration.GENERATE_COMPLEX_SUBSCRIPTIONS;

public class ComplexSubscriptionSpout extends BaseRichSpout {

    private SpoutOutputCollector collector;
    private String taskName;
    private Map<String, ProtoComplexSubscription.ComplexSubscription> unconfirmed;
    private static final AtomicInteger complexSubscriptionCount = new AtomicInteger(0);
    private static final Logger LOG = Logger.getLogger(ComplexSubscriptionSpout.class);
    public static final String ID = ComplexSubscriptionSpout.class.getCanonicalName();

    @Override
    public void open(Map<String, Object> conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        this.taskName = MessageFormat.format("<{0} <-> {0}>", context.getThisComponentId(), context.getThisTaskId());
        this.unconfirmed = new HashMap<>();
    }

    @Override
    public void nextTuple() {
        if (!GENERATE_COMPLEX_SUBSCRIPTIONS) {
            return;
        }

        if (complexSubscriptionCount.get() >= COMPLEX_SUBSCRIPTION_COUNT) {
            return;
        }

        complexSubscriptionCount.incrementAndGet();

        var cs = SubscriptionGenerator.getInstance().generateComplex();
        unconfirmed.put(cs.getSubscriptionId(), cs);

        this.collector.emit(new Values(cs), cs.getSubscriptionId());
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
        declarer.declare(new Fields("ComplexSubscription"));
    }

    @Override
    public void close() {
        LOG.info(MessageFormat.format("ComplexSubscriptionSpout {0} has generated {1} ComplexSubscriptions", this.taskName, this.complexSubscriptionCount));
    }
}
