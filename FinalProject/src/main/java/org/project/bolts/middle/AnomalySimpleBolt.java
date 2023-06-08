package org.project.bolts.middle;

import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.project.models.ProtoSimplePublication;

import java.text.MessageFormat;
import java.util.Map;

public class AnomalySimpleBolt extends BaseRichBolt {

    public static final String ID = AnomalySimpleBolt.class.toString();
    private static final Logger LOG = Logger.getLogger(AnomalySimpleBolt.class);
    private OutputCollector collector;
    private int eventsReceived;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        this.eventsReceived = 0;
    }

    @Override
    public void execute(Tuple input) {

        var anomalyType = input.getStringByField("AnomalyType");
        var sp = (ProtoSimplePublication.SimplePublication)(input.getValueByField("SimplePublication"));
        this.collector.emit(input, new Values(anomalyType, sp));

        LOG.info(MessageFormat.format("Processed anomaly <{0}>!", input));
        this.collector.ack(input);
        eventsReceived++;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("AnomalyType", "SimplePublication"));
    }

    @Override
    public void cleanup() {
        LOG.info(MessageFormat.format("Simple anomaly events received: {0}!", this.eventsReceived));
    }
}
