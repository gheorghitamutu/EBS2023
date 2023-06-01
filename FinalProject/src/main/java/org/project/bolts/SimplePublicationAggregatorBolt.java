package org.project.bolts;

import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;
import org.project.models.ProtoComplexPublication;
import org.project.models.ProtoSimplePublication;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimplePublicationAggregatorBolt extends BaseWindowedBolt {

    public static final String ID = SimplePublicationAggregatorBolt.class.toString();
    private static final Logger LOG = Logger.getLogger(SimplePublicationAggregatorBolt.class);

    private int eventsReceived;
    private OutputCollector collector;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector collector) {
        this.eventsReceived = 0;
        this.collector = collector;
    }

    public ProtoComplexPublication.ComplexPublication buildComplexPublication(
            List<ProtoSimplePublication.SimplePublication> sps) {

        var avgTemperature = sps.stream().mapToDouble(ProtoSimplePublication.SimplePublication::getTemperature).average().orElse(0.0);
        var avgWind = sps.stream().mapToDouble(ProtoSimplePublication.SimplePublication::getWind).average().orElse(0.0);
        var avgRain = sps.stream().mapToDouble(ProtoSimplePublication.SimplePublication::getRain).average().orElse(0.0);

        return ProtoComplexPublication.ComplexPublication.newBuilder()
                .setAvgTemperature(avgTemperature)
                .setAvgWind(avgWind)
                .setAvgRain(avgRain)
                .build();
    }

    @Override
    public void execute(TupleWindow input) {
        var oldCount = eventsReceived;
        List<ProtoSimplePublication.SimplePublication> sps = new ArrayList<>();

        for (var tuple: input.get()) {
            tuple.getFields().forEach((f) -> {
                eventsReceived++;
                var sp = (ProtoSimplePublication.SimplePublication)(tuple.getValueByField(f));
                LOG.info(MessageFormat.format("Input Field: <{0}> Event received: #{1}\n{2}", f, eventsReceived, sp));
                sps.add(sp);
                this.collector.ack(tuple);
            });
            this.collector.ack(tuple);
        }

        var cp = buildComplexPublication(sps);
        this.collector.emit(input.get(), new Values(cp));
        LOG.info(MessageFormat.format("Processed <{0}> value(s)!", eventsReceived - oldCount));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("ComplexPublication"));
    }

    @Override
    public void cleanup() {
        LOG.info(MessageFormat.format("Events received: {0}!", this.eventsReceived));
    }
}
