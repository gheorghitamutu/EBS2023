package org.project.bolts;

import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.project.models.ProtoComplexPublication;

import java.text.MessageFormat;
import java.util.Map;

public class AnomalyComplexBolt  extends BaseRichBolt {

    public static final String ID = AnomalyComplexBolt.class.toString();
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
        input.getFields().forEach((f) -> {
            var value = input.getValueByField(f);
            if (f.equals("AnomalyType")) {
                LOG.info(MessageFormat.format("Anomaly Field <{0}> Value <{1}>", f, value));
            }
            else if (f.equals("ComplexPublication")){
                var cp = (ProtoComplexPublication.ComplexPublication)(value);
                LOG.info(MessageFormat.format("Anomaly Field <{0}> Value <{1}>", f, cp));
            }
            else {
                LOG.info(MessageFormat.format("Anomaly Field (Unknown!) <{0}> Value (Unknown!) <{1}>", f, value));
            }
        });

        LOG.info(MessageFormat.format("Processed anomaly <{0}>!", input));
        this.collector.ack(input);

        eventsReceived++;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // TODO: nothing?
    }

    @Override
    public void cleanup() {
        LOG.info(MessageFormat.format("Complex anomaly events received: {0}!", this.eventsReceived));
    }
}
