package org.project;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.ConfigurableTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.project.bolts.*;
import org.project.data.AnomalyComplexPublication;
import org.project.data.AnomalySimplePublication;
import org.project.filters.ComplexPublicationFilter;
import org.project.filters.Operator;
import org.project.filters.SimplePublicationFilter;
import org.project.models.ProtoComplexPublication;
import org.project.models.ProtoSimplePublication;
import org.project.spouts.SimplePublicationSpout;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class Application extends ConfigurableTopology {

    private static final String TOPOLOGY_NAME = "project_topology";
    private static final int LOCAL_CLUSTER_RUN_TIME = 10 * 60 * 100;
    private static final int WINDOW_LENGTH = 30;
    private static final int SLIDING_INTERVAL = 10;

    public static void main(String[] args) throws Exception {

        if (args.length == 1 && args[0].equals("--local_cluster")) {
            runLocalCluster();
            return;
        }

        ConfigurableTopology.start(new Application(), args);
    }

    static void runLocalCluster() throws Exception {
        var builder = buildTopology();

        try (LocalCluster cluster = new LocalCluster()) {
            StormTopology topology = builder.createTopology();

            Config config = new Config();
            setConfig(config);

            cluster.submitTopology(TOPOLOGY_NAME, config, topology);

            try {
                Thread.sleep(LOCAL_CLUSTER_RUN_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            cluster.killTopology(TOPOLOGY_NAME);
            cluster.shutdown();
        }
    }

    static TopologyBuilder buildTopology() {
        var builder = new TopologyBuilder();
        var source = new SimplePublicationSpout();
        var simplePublicationAggregatorBolt =
                new SimplePublicationAggregatorBolt()
                        .withWindow(
                                new BaseWindowedBolt.Count(WINDOW_LENGTH),
                                new BaseWindowedBolt.Count(SLIDING_INTERVAL));
        var complexPublicationBolt = new ComplexPublicationBolt();
        var simplePublicationBolt = new SimplePublicationBolt();

        var filterSimpleAnomalyBolt =
                new FilterSimpleAnomalyBolt(
                        List.of(
                                (Predicate<ProtoSimplePublication.SimplePublication> & Serializable) (n) ->
                                        SimplePublicationFilter.composedFilter(
                                            List.of(SimplePublicationFilter.filterByTemperature(Operator.Type.GREATER_THAN, AnomalySimplePublication.MAX_TEMPERATURE))).test(n),
                                (Predicate<ProtoSimplePublication.SimplePublication> & Serializable) (n) ->
                                        SimplePublicationFilter.composedFilter(
                                            List.of(SimplePublicationFilter.filterByTemperature(Operator.Type.LOWER_THAN, AnomalySimplePublication.MIN_TEMPERATURE))).test(n),
                                (Predicate<ProtoSimplePublication.SimplePublication> & Serializable) (n) ->
                                        SimplePublicationFilter.composedFilter(
                                            List.of(SimplePublicationFilter.filterByWind(Operator.Type.GREATER_THAN, AnomalySimplePublication.MAX_WIND))).test(n),
                                (Predicate<ProtoSimplePublication.SimplePublication> & Serializable) (n) ->
                                        SimplePublicationFilter.composedFilter(
                                            List.of(SimplePublicationFilter.filterByRain(Operator.Type.GREATER_THAN, AnomalySimplePublication.MAX_RAIN))).test(n)
                        ));

        var filterComplexAnomalyBolt =
                new FilterComplexAnomalyBolt(
                        List.of(
                                (Predicate<ProtoComplexPublication.ComplexPublication> & Serializable) (n) ->
                                        ComplexPublicationFilter.composedFilter(
                                            List.of(ComplexPublicationFilter.filterByAvgTemperature(Operator.Type.GREATER_THAN, AnomalyComplexPublication.MAX_AVG_TEMPERATURE))).test(n),
                                (Predicate<ProtoComplexPublication.ComplexPublication> & Serializable) (n) ->
                                        ComplexPublicationFilter.composedFilter(
                                            List.of(ComplexPublicationFilter.filterByAvgTemperature(Operator.Type.LOWER_THAN, AnomalyComplexPublication.MIN_AVG_TEMPERATURE))).test(n),
                                (Predicate<ProtoComplexPublication.ComplexPublication> & Serializable) (n) ->
                                        ComplexPublicationFilter.composedFilter(
                                            List.of(ComplexPublicationFilter.filterByAvgWind(Operator.Type.GREATER_THAN, AnomalyComplexPublication.MAX_AVG_WIND))).test(n),
                                (Predicate<ProtoComplexPublication.ComplexPublication> & Serializable) (n) ->
                                        ComplexPublicationFilter.composedFilter(
                                            List.of(ComplexPublicationFilter.filterByAvgRain(Operator.Type.GREATER_THAN, AnomalyComplexPublication.MAX_AVG_RAIN))).test(n)
                        ));

        var anomalySimpleBolt = new AnomalySimpleBolt();
        var anomalyComplexBolt = new AnomalyComplexBolt();
        var anomalyTerminalBolt = new AnomalyTerminalBolt();

        builder.setSpout(
                SimplePublicationSpout.ID,
                source,
                1);

        builder.setBolt(
                        SimplePublicationAggregatorBolt.ID,
                        simplePublicationAggregatorBolt,
                        2)
                .shuffleGrouping(SimplePublicationSpout.ID);

        builder.setBolt(
                        ComplexPublicationBolt.ID,
                        complexPublicationBolt,
                        2)
                .shuffleGrouping(SimplePublicationAggregatorBolt.ID);

        builder.setBolt(
                        SimplePublicationBolt.ID,
                        simplePublicationBolt,
                        2)
                .shuffleGrouping(SimplePublicationSpout.ID);

        builder.setBolt(
                FilterSimpleAnomalyBolt.ID,
                filterSimpleAnomalyBolt,
                1
        ).shuffleGrouping(SimplePublicationBolt.ID);

        builder.setBolt(
                        AnomalySimpleBolt.ID,
                        anomalySimpleBolt,
                        2)
                .shuffleGrouping(FilterSimpleAnomalyBolt.ID);

        builder.setBolt(
                FilterComplexAnomalyBolt.ID,
                filterComplexAnomalyBolt,
                1
        ).shuffleGrouping(ComplexPublicationBolt.ID);

        builder.setBolt(
                        AnomalyComplexBolt.ID,
                        anomalyComplexBolt,
                        1)
                .shuffleGrouping(FilterComplexAnomalyBolt.ID);

        builder.setBolt(
                AnomalyTerminalBolt.ID,
                anomalyTerminalBolt,
                1
                )
                .fieldsGrouping(AnomalySimpleBolt.ID, "simple_anomaly_stream", new Fields("AnomalyType", "SimplePublication"))
                .fieldsGrouping(AnomalyComplexBolt.ID, "complex_anomaly_stream", new Fields("AnomalyType", "ComplexPublication"));

        return builder;
    }

    static void setConfig(Config config) {
        // fine tuning => https://storm.apache.org/releases/2.4.0/Performance.html
        config.put(Config.TOPOLOGY_EXECUTOR_RECEIVE_BUFFER_SIZE, 2048);
        config.put(Config.TOPOLOGY_TRANSFER_BATCH_SIZE, 100);
        config.put(Config.TOPOLOGY_PRODUCER_BATCH_SIZE, 100);
        config.put(Config.TOPOLOGY_STATS_SAMPLE_RATE, 0.001);

        config.setDebug(false);
        config.setNumWorkers(1);
    }

    @Override
    protected int run(String[] args) {
        var builder = buildTopology();
        setConfig(conf);
        return submit(TOPOLOGY_NAME, conf, builder);
    }
}