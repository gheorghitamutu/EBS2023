package org.project.bolts;

import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.util.Map;

public class FilterSimpleSubscriptionBolt extends BaseRichBolt {

    public static final String ID = FilterSimpleSubscriptionBolt.class.toString();
    private static final Logger LOG = Logger.getLogger(FilterSimpleSubscriptionBolt.class);
    private OutputCollector collector;

    @Override
    public void prepare(Map<String, Object> configuration, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        LOG.info("Simple subscription events received!");
        collector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }
}
