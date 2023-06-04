package org.project;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.ConfigurableTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.project.bolts.*;
import org.project.cofiguration.GlobalConfiguration;
import org.project.data.AnomalyComplexPublication;
import org.project.data.AnomalySimplePublication;
import org.project.filters.ComplexPublicationFilter;
import org.project.filters.SimplePublicationFilter;
import org.project.models.ProtoComplexPublication;
import org.project.models.ProtoComplexSubscription;
import org.project.models.ProtoSimplePublication;
import org.project.models.ProtoSimpleSubscription;
import org.project.spouts.ComplexSubscriptionSpout;
import org.project.spouts.SimplePublicationSpout;
import org.project.spouts.SimpleSubscriptionSpout;

import java.io.Serializable;
import java.util.List;
import java.util.function.Predicate;

import static org.project.cofiguration.GlobalConfiguration.*;

public class Application extends ConfigurableTopology {

    public static void main(String[] args) throws Exception {

        if (args.length == 1 && args[0].equals(LOCAL_CLUSTER_ARGUMENT)) {
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

        // handle anomalies

        var filterSimpleAnomalyBolt =
                new FilterSimpleAnomalyBolt(
                        List.of(
                                (Predicate<ProtoSimplePublication.SimplePublication> & Serializable) (n) ->
                                        SimplePublicationFilter.composedFilter(
                                            List.of(SimplePublicationFilter.filterByTemperature(ProtoSimpleSubscription.Operator.GREATER_THAN, AnomalySimplePublication.MAX_TEMPERATURE))).test(n),
                                (Predicate<ProtoSimplePublication.SimplePublication> & Serializable) (n) ->
                                        SimplePublicationFilter.composedFilter(
                                            List.of(SimplePublicationFilter.filterByTemperature(ProtoSimpleSubscription.Operator.LOWER_THAN, AnomalySimplePublication.MIN_TEMPERATURE))).test(n),
                                (Predicate<ProtoSimplePublication.SimplePublication> & Serializable) (n) ->
                                        SimplePublicationFilter.composedFilter(
                                            List.of(SimplePublicationFilter.filterByWind(ProtoSimpleSubscription.Operator.GREATER_THAN, AnomalySimplePublication.MAX_WIND))).test(n),
                                (Predicate<ProtoSimplePublication.SimplePublication> & Serializable) (n) ->
                                        SimplePublicationFilter.composedFilter(
                                            List.of(SimplePublicationFilter.filterByRain(ProtoSimpleSubscription.Operator.GREATER_THAN, AnomalySimplePublication.MAX_RAIN))).test(n)
                        ));

        var filterComplexAnomalyBolt =
                new FilterComplexAnomalyBolt(
                        List.of(
                                (Predicate<ProtoComplexPublication.ComplexPublication> & Serializable) (n) ->
                                        ComplexPublicationFilter.composedFilter(
                                            List.of(ComplexPublicationFilter.filterByAvgTemperature(ProtoComplexSubscription.Operator.GREATER_THAN, AnomalyComplexPublication.MAX_AVG_TEMPERATURE))).test(n),
                                (Predicate<ProtoComplexPublication.ComplexPublication> & Serializable) (n) ->
                                        ComplexPublicationFilter.composedFilter(
                                            List.of(ComplexPublicationFilter.filterByAvgTemperature(ProtoComplexSubscription.Operator.LOWER_THAN, AnomalyComplexPublication.MIN_AVG_TEMPERATURE))).test(n),
                                (Predicate<ProtoComplexPublication.ComplexPublication> & Serializable) (n) ->
                                        ComplexPublicationFilter.composedFilter(
                                            List.of(ComplexPublicationFilter.filterByAvgWind(ProtoComplexSubscription.Operator.GREATER_THAN, AnomalyComplexPublication.MAX_AVG_WIND))).test(n),
                                (Predicate<ProtoComplexPublication.ComplexPublication> & Serializable) (n) ->
                                        ComplexPublicationFilter.composedFilter(
                                            List.of(ComplexPublicationFilter.filterByAvgRain(ProtoComplexSubscription.Operator.GREATER_THAN, AnomalyComplexPublication.MAX_AVG_RAIN))).test(n)
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
                .fieldsGrouping(AnomalySimpleBolt.ID,  new Fields("AnomalyType", "SimplePublication"))
                .fieldsGrouping(AnomalyComplexBolt.ID,  new Fields("AnomalyType", "ComplexPublication"));

        // handle subscribers

        var simpleSubscriptionSpout = new SimpleSubscriptionSpout();
        var complexSubscriptionSpout = new ComplexSubscriptionSpout();

        builder.setSpout(
                SimpleSubscriptionSpout.ID,
                simpleSubscriptionSpout,
                1);
        builder.setSpout(
                ComplexSubscriptionSpout.ID,
                complexSubscriptionSpout,
                1);

        var filterSimpleSubscriptionBolt = new FilterSimpleSubscriptionBolt();
        var filterComplexSubscriptionBolt = new FilterComplexSubscriptionBolt();

        builder.setBolt(
                FilterSimpleSubscriptionBolt.ID,
                filterSimpleSubscriptionBolt,
                1)
                .fieldsGrouping(SimplePublicationSpout.ID,  new Fields("SimplePublication"))
                .fieldsGrouping(SimpleSubscriptionSpout.ID, new Fields("SimpleSubscription"));

        builder.setBolt(
                FilterComplexSubscriptionBolt.ID,
                filterComplexSubscriptionBolt,
                1)
                .fieldsGrouping(ComplexPublicationBolt.ID,  new Fields("ComplexPublication"))
                .fieldsGrouping(ComplexSubscriptionSpout.ID, new Fields("ComplexSubscription"));

        return builder;
    }

    static void setConfig(Config config) {
        // fine tuning => https://storm.apache.org/releases/2.4.0/Performance.html
        config.put(Config.TOPOLOGY_EXECUTOR_RECEIVE_BUFFER_SIZE, GlobalConfiguration.TOPOLOGY_EXECUTOR_RECEIVE_BUFFER_SIZE);
        config.put(Config.TOPOLOGY_TRANSFER_BATCH_SIZE, GlobalConfiguration.TOPOLOGY_TRANSFER_BATCH_SIZE);
        config.put(Config.TOPOLOGY_PRODUCER_BATCH_SIZE, GlobalConfiguration.TOPOLOGY_PRODUCER_BATCH_SIZE);
        config.put(Config.TOPOLOGY_STATS_SAMPLE_RATE, GlobalConfiguration.TOPOLOGY_STATS_SAMPLE_RATE);

        config.setDebug(TOPOLOGY_DEBUG);
        config.setNumWorkers(TOPOLOGY_WORKERS);
    }

    @Override
    protected int run(String[] args) {
        var builder = buildTopology();
        setConfig(conf);
        return submit(TOPOLOGY_NAME, conf, builder);
    }
}