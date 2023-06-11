package org.project.bolts.transformers;

import org.apache.log4j.Logger;
import org.apache.storm.metric.api.AssignableMetric;
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
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static org.project.cofiguration.GlobalConfiguration.*;

public class SimplePublicationAggregatorBolt extends BaseWindowedBolt {

    public static final String ID = SimplePublicationAggregatorBolt.class.getCanonicalName();
    private static final Logger LOG = Logger.getLogger(SimplePublicationAggregatorBolt.class);

    private int eventsReceived;
    private OutputCollector collector;

    private transient AssignableMetric latencyForGeneration;
    private transient AssignableMetric latencyForStorage;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext context, OutputCollector collector) {
        this.eventsReceived = 0;
        this.collector = collector;

        //http://<storm-ui-host>:<ui-port>/api/v1/topology/<topology-id>/component/<component-id>/metric/<metric-name>
        // http://localhost:8080/api/v1/topology/weather-topology-10-1686397266/component/org.project.bolts.firstLevel.SimplePublicationAggregator/metric/latency-generation
        latencyForGeneration = new AssignableMetric(0);
        latencyForStorage = new AssignableMetric(0);

        context.registerMetric(METRICS_LATENCY_COMPLEX_PUBLICATION_GENERATION, latencyForGeneration, TIME_BUCKET_SIZE_IN_SECS);
        context.registerMetric(METRICS_LATENCY_COMPLEX_PUBLICATION_STORAGE, latencyForStorage, TIME_BUCKET_SIZE_IN_SECS);
    }

    public List<ProtoComplexPublication.ComplexPublication> buildComplexPublications(
            List<ProtoSimplePublication.SimplePublication> sps) {

        List<ProtoComplexPublication.ComplexPublication> cps = new ArrayList<>();

        var cities = sps.stream().map(ProtoSimplePublication.SimplePublication::getCity).collect(Collectors.toSet());
        cities.forEach((city) -> {
            var spsForCity = sps.stream().filter((sp) -> sp.getCity().equals(city)).collect(Collectors.toUnmodifiableSet());

            var avgTemperature = spsForCity.stream().mapToDouble(ProtoSimplePublication.SimplePublication::getTemperature).average().orElse(0.0);
            var avgWind = spsForCity.stream().mapToDouble(ProtoSimplePublication.SimplePublication::getWind).average().orElse(0.0);
            var avgRain = spsForCity.stream().mapToDouble(ProtoSimplePublication.SimplePublication::getRain).average().orElse(0.0);

            cps.add(ProtoComplexPublication.ComplexPublication.newBuilder()
                    .setCity(city)
                    .setAvgTemperature(avgTemperature)
                    .setAvgWind(avgWind)
                    .setAvgRain(avgRain)
                    .setPublicationsCount(spsForCity.size())
                    .setTimestamp(System.currentTimeMillis())
                    .build());
                }
        );

        return cps;
    }

    @Override
    public void execute(TupleWindow input) {
        long start = System.currentTimeMillis();

        var oldCount = eventsReceived;
        List<ProtoSimplePublication.SimplePublication> sps = new ArrayList<>();

        for (var tuple: input.get()) {
            tuple.getFields().forEach((f) -> {
                eventsReceived++;
                var sp = (ProtoSimplePublication.SimplePublication)(tuple.getValueByField(f));
                // LOG.info(MessageFormat.format("Input Field: <{0}> Event received: #{1}\n{2}", f, eventsReceived, sp));
                sps.add(sp);
                this.collector.ack(tuple);
            });
            this.collector.ack(tuple);
        }

        var cps = buildComplexPublications(sps);
        latencyForGeneration.setValue((Long)abs(System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        cps.forEach((cp) -> this.collector.emit(input.get(), new Values(cp)));
        latencyForStorage.setValue((Long)abs(System.currentTimeMillis() - start));

        // LOG.info(MessageFormat.format("Processed <{0}> value(s)!", eventsReceived - oldCount));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("ComplexPublication"));
    }

    @Override
    public void cleanup() {
        LOG.info(MessageFormat.format("Events received: {0}!", this.eventsReceived));
    }

    public void getComponentPageInfo() {
        LOG.info(MessageFormat.format("Component ID: {0}!", ID));
        LOG.info(MessageFormat.format("Component Type: {0}!", this.getClass().toString()));
    }
}
