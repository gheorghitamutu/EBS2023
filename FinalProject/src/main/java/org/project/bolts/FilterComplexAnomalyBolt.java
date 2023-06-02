package org.project.bolts;

import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.project.data.AnomalyComplexPublication;
import org.project.models.ProtoComplexPublication;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class FilterComplexAnomalyBolt extends BaseRichBolt {

    public static final String ID = FilterComplexAnomalyBolt.class.toString();
    private static final Logger LOG = Logger.getLogger(FilterComplexAnomalyBolt.class);
    final private List<Predicate<ProtoComplexPublication.ComplexPublication>> predicates;
    private OutputCollector collector;

    public FilterComplexAnomalyBolt(List<Predicate<ProtoComplexPublication.ComplexPublication>> predicates) {
        this.predicates = predicates;
    }

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector collector) {
        topologyContext.registerCounter(ID + "_received_tuples");
        topologyContext.registerHistogram(ID + "_received_tuples_per_second_histogram");
        topologyContext.registerMeter(ID + "_received_tuples_per_second_meter");

        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        input.getFields().forEach((f) -> {
            var value = input.getValueByField(f);
            if (f.equals("ComplexPublication")) {
                var cp = (ProtoComplexPublication.ComplexPublication) (value);
                if (this.predicates.stream().map(p -> p.test(cp)).reduce(false, (a, b) -> a || b)) {
                    var anomalyType = AnomalyComplexPublication.ToString(AnomalyComplexPublication.isAnomaly(cp));
                    this.collector.emit(input, new Values(anomalyType, cp));
                } else {
                    LOG.info("Field <" + f + "> Value <" + cp + "> (Filtered!)");
                }
            } else {
                LOG.info("Field (Unknown!) <" + f + "> Value (Unknown!) <" + value + ">");
            }
        });

        this.collector.ack(input);
    }

    @Override
    public void cleanup() {
        LOG.info("Complex anomaly events received!");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("AnomalyType", "ComplexPublication"));
    }
}
