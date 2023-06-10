package org.project.bolts.middle;

import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

public class FilterComplexSubscriptionBolt extends BaseRichBolt {

    public static final String ID = FilterComplexSubscriptionBolt.class.getCanonicalName();
    private static final Logger LOG = Logger.getLogger(FilterComplexSubscriptionBolt.class);
    private OutputCollector collector;

    @Override
    public void prepare(Map<String, Object> configuration, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        // LOG.info("Complex subscription events received!");
        collector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
