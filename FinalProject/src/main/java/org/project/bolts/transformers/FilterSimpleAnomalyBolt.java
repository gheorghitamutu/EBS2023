package org.project.bolts.transformers;

import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.project.data.AnomalySimplePublication;
import org.project.models.ProtoSimplePublication;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class FilterSimpleAnomalyBolt extends BaseRichBolt {

    public static final String ID = FilterSimpleAnomalyBolt.class.getCanonicalName();
    private static final Logger LOG = Logger.getLogger(FilterSimpleAnomalyBolt.class);
    final private List<Predicate<ProtoSimplePublication.SimplePublication>> predicates;
    private OutputCollector collector;

    public FilterSimpleAnomalyBolt(List<Predicate<ProtoSimplePublication.SimplePublication>> predicates) {
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
            if (f.equals("SimplePublication")) {
                var sp = (ProtoSimplePublication.SimplePublication) (value);
                if (this.predicates.stream().map(p -> p.test(sp)).reduce(false, (a, b) -> a || b)) {
                    var anomalyType = AnomalySimplePublication.ToString(AnomalySimplePublication.isAnomaly(sp));
                    this.collector.emit(input, new Values(anomalyType, sp));
                } else {
                    // LOG.info("Field <" + f + "> Value <" + sp + "> (Filtered!)");
                }
            } else {
                LOG.info("Field (Unknown!) <" + f + "> Value (Unknown!) <" + value + ">");
            }
        });

        this.collector.ack(input);
    }

    @Override
    public void cleanup() {
        LOG.info("Simple anomaly events received!");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("AnomalyType", "SimplePublication"));
    }
}
