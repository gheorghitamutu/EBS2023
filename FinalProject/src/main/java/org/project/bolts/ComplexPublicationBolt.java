package org.project.bolts;

import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

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
        input.getFields().forEach((f) -> {
            var value = input.getValueByField(f);
            if (f.equals("ComplexPublication")) {
                LOG.info(MessageFormat.format("Complex Publication Field <{0}> Value <{1}>", f, value));
            }
            else {
                LOG.info(MessageFormat.format("Complex Publication Field (Unknown!) <{0}> Value (Unknown!) <{1}>", f, value));
            }
        });
        this.collector.ack(input);
        eventsReceived++;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }

    @Override
    public void cleanup() {
        LOG.info(MessageFormat.format("Complex publication events received: {0}!", this.eventsReceived));
    }
}
