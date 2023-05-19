package org.project.bolts;

import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.project.models.ProtoSimplePublication;

import java.text.MessageFormat;
import java.util.Map;

public class SimplePublicationBolt extends BaseRichBolt {

    public static final String ID = SimplePublicationBolt.class.toString();
    private static final Logger LOG = Logger.getLogger(SimplePublicationBolt.class);

    private int eventsReceived;
    private OutputCollector collector;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector collector) {
        this.eventsReceived = 0;
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        var oldCount = eventsReceived;
        input.getFields().forEach((f) -> {
            eventsReceived++;
            var sp = (ProtoSimplePublication.SimplePublication)(input.getValueByField(f));
            // LOG.info(MessageFormat.format("Input Field: <{0}> Event received: #{1}\n{2}", f, eventsReceived, sp));
        });
        this.collector.ack(input);
        // LOG.info(MessageFormat.format("Processed <{0}> value(s)!", eventsReceived - oldCount));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        // TODO: this needs to declare an anomaly
    }

    @Override
    public void cleanup() {
        LOG.info("cleanup");
        LOG.info(MessageFormat.format("Events received: {0}!", this.eventsReceived));
    }
}
