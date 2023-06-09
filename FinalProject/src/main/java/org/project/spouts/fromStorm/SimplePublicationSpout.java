package org.project.spouts.fromStorm;

import org.apache.log4j.Logger;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.project.data.City;
import org.project.data.PublicationGenerator;
import org.project.models.ProtoSimplePublication;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.*;

import static org.project.cofiguration.GlobalConfiguration.MAX_TIME;

public class SimplePublicationSpout extends BaseRichSpout {

    private SpoutOutputCollector collector;
    private String taskName;
    private Map<String, ProtoSimplePublication.SimplePublication> unconfirmed;
    private int simplePublicationCount;
    private static final Logger LOG = Logger.getLogger(SimplePublicationSpout.class);

    public static final String ID = SimplePublicationSpout.class.toString();
    private static final long START_TIME = System.currentTimeMillis();

    @Override
    public void open(Map<String, Object> map, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
        this.simplePublicationCount = 0;
        this.taskName = MessageFormat.format("<{0} <-> {0}>", context.getThisComponentId(), context.getThisTaskId());
        this.unconfirmed = new HashMap<>();
    }

    @Override
    public void nextTuple() {
        if (System.currentTimeMillis() - START_TIME > MAX_TIME) {
            return;
        }

        simplePublicationCount++;

        var sp = PublicationGenerator.getInstance().generate();
        unconfirmed.put(sp.getUuid(), sp);

        this.collector.emit(new Values(sp), sp.getUuid());
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("SimplePublication"));
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
    public void close() {
        LOG.info(MessageFormat.format("SimplePublicationSpout {0} has generated {1} SimplePublications", this.taskName, this.simplePublicationCount));
    }
}
