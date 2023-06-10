package org.project.bolts.middle;

import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.project.models.ProtoComplexPublication;

import java.text.MessageFormat;
import java.util.Map;

public class AnomalyComplexBolt  extends BaseRichBolt {

    public static final String ID = AnomalyComplexBolt.class.getCanonicalName();
    private static final Logger LOG = Logger.getLogger(AnomalyComplexBolt.class);
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
        var cp = (ProtoComplexPublication.ComplexPublication)(input.getValueByField("ComplexPublication"));
        this.collector.emit(input, new Values(anomalyType, cp));

        // LOG.info(MessageFormat.format("Processed anomaly <{0}>!", input));
        this.collector.ack(input);
        eventsReceived++;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("AnomalyType", "ComplexPublication"));
    }

    @Override
    public void cleanup() {
        LOG.info(MessageFormat.format("Complex anomaly events received: {0}!", this.eventsReceived));
    }
}
