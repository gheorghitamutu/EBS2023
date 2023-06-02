package org.project.bolts;

import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

import java.text.MessageFormat;
import java.util.Map;

public class AnomalyTerminalBolt extends BaseRichBolt {

    public static final String ID = AnomalyTerminalBolt.class.toString();
    private static final Logger LOG = Logger.getLogger(AnomalyTerminalBolt.class);
    private OutputCollector collector;
    private int eventsReceived;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        this.eventsReceived = 0;
    }

    @Override
    public void execute(Tuple input) {
        if (input.contains("AnomalyType")) {
            var anomalyType = input.getValueByField("AnomalyType");
            if (input.contains("SimplePublication")) {
                var sp = (org.project.models.ProtoSimplePublication.SimplePublication) input.getValueByField("SimplePublication");
                LOG.info(MessageFormat.format("Simple Publication Event received: #{0}\n{1}\n{2}", eventsReceived, anomalyType, sp));
            }

            if (input.contains("ComplexPublication")) {
                var cp = (org.project.models.ProtoComplexPublication.ComplexPublication) input.getValueByField("ComplexPublication");
                LOG.info(MessageFormat.format("Complex Publication Event received: #{0}\n{1}\n{2}", eventsReceived, anomalyType, cp));
            }
        }
        else {
            LOG.info(MessageFormat.format("Unknown fields: {0}", input.getFields()));
        }

        this.collector.ack(input);
        eventsReceived++;
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // TODO: nothing?
    }

    @Override
    public void cleanup() {
        LOG.info(MessageFormat.format("Anomaly events received: {0}!", this.eventsReceived));
    }
}
