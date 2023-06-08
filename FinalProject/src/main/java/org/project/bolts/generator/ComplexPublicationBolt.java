package org.project.bolts.generator;

import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.text.MessageFormat;
import java.util.Map;

public class ComplexPublicationBolt extends BaseRichBolt {
    public static final String ID = ComplexPublicationBolt.class.toString();
    private static final Logger LOG = Logger.getLogger(ComplexPublicationBolt.class);
    private int eventsReceived;
    private OutputCollector collector;
    @Override
    public void prepare(Map<String, Object> map, TopologyContext context, OutputCollector collector) {
        this.eventsReceived = 0;
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        this.collector.emit(input, new Values(input.getValueByField("ComplexPublication")));
        this.collector.ack(input);
        eventsReceived++;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("ComplexPublication"));
    }

    @Override
    public void cleanup() {
        LOG.info(MessageFormat.format("Complex publication events received: {0}!", this.eventsReceived));
    }
}
