package org.project;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.ConfigurableTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.project.bolts.firstLevel.*;
import org.project.bolts.toAMQP.PublicationViaSubscriptionBolt;
import org.project.bolts.toSubscribers.FilterComplexSubscriptionBolt;
import org.project.bolts.toSubscribers.FilterSimpleSubscriptionBolt;
import org.project.bolts.transformers.*;
import org.project.bolts.toAMQP.AnomalyBolt;
import org.project.bolts.toAMQP.SimplePublicationBolt;
import org.project.cofiguration.GlobalConfiguration;
import org.project.data.AnomalyComplexPublication;
import org.project.data.AnomalySimplePublication;
import org.project.filters.ComplexPublicationFilter;
import org.project.filters.SimplePublicationFilter;
import org.project.metrics.GraphiteMetricsConsumer;
import org.project.models.ProtoComplexPublication;
import org.project.models.ProtoComplexSubscription;
import org.project.models.ProtoSimplePublication;
import org.project.models.ProtoSimpleSubscription;
import org.project.spouts.fromAMQP.ComplexPublicationSpout;
import org.project.spouts.fromAMQP.SimplePublicationSpout;
import org.project.spouts.fromStorm.ComplexSubscriptionSpout;
import org.project.spouts.fromStorm.SimpleSubscriptionSpout;

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

        // override settings if ran from local cluster
        RUNNING_LOCALLY = true;

        AMQP_HOST = "localhost";
        AMQP_PORT = 5673;

        GRAPHITE_HOST = "localhost";
        GRAPHITE_PORT = 2003;

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
        var source = new org.project.spouts.fromStorm.SimplePublicationSpout();
        var simplePublicationAggregatorBolt =
                new SimplePublicationAggregatorBolt()
                        .withWindow(
                                new BaseWindowedBolt.Count(WINDOW_LENGTH),
                                new BaseWindowedBolt.Count(SLIDING_INTERVAL));
        var complexPublicationBolt = new ComplexPublicationBolt();
        var simplePublicationBolt = new org.project.bolts.firstLevel.SimplePublicationBolt();

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

        builder.setSpout(
                org.project.spouts.fromStorm.SimplePublicationSpout.ID,
                source,
                MINIMUM_NODE_COUNT);

        builder.setBolt(
                        SimplePublicationAggregatorBolt.ID,
                        simplePublicationAggregatorBolt,
                        MINIMUM_NODE_COUNT)
                .shuffleGrouping(org.project.spouts.fromStorm.SimplePublicationSpout.ID);

        builder.setBolt(
                        ComplexPublicationBolt.ID,
                        complexPublicationBolt,
                        MINIMUM_NODE_COUNT)
                .shuffleGrouping(SimplePublicationAggregatorBolt.ID);

        builder.setBolt(
                        org.project.bolts.firstLevel.SimplePublicationBolt.ID,
                        simplePublicationBolt,
                        MINIMUM_NODE_COUNT)
                .shuffleGrouping(org.project.spouts.fromStorm.SimplePublicationSpout.ID);

        builder.setBolt(
                FilterSimpleAnomalyBolt.ID,
                filterSimpleAnomalyBolt,
                MINIMUM_NODE_COUNT
        ).shuffleGrouping(org.project.bolts.firstLevel.SimplePublicationBolt.ID);

        builder.setBolt(
                        AnomalySimpleBolt.ID,
                        anomalySimpleBolt,
                        MINIMUM_NODE_COUNT)
                .shuffleGrouping(FilterSimpleAnomalyBolt.ID);

        builder.setBolt(
                FilterComplexAnomalyBolt.ID,
                filterComplexAnomalyBolt,
                MINIMUM_NODE_COUNT)
            .shuffleGrouping(ComplexPublicationBolt.ID);

        builder.setBolt(
                        AnomalyComplexBolt.ID,
                        anomalyComplexBolt,
                        MINIMUM_NODE_COUNT)
                .shuffleGrouping(FilterComplexAnomalyBolt.ID);

        var anomalyBolt = new org.project.bolts.toAMQP.AnomalyBolt();
        builder.setBolt(
                        AnomalyBolt.ID,
                        anomalyBolt,
                        MINIMUM_NODE_COUNT)
                .shuffleGrouping(AnomalySimpleBolt.ID)
                .shuffleGrouping(AnomalyComplexBolt.ID);

        // handle subscribers

        var simpleSubscriptionSpout = new SimpleSubscriptionSpout();
        var complexSubscriptionSpout = new ComplexSubscriptionSpout();

        builder.setSpout(
                SimpleSubscriptionSpout.ID,
                simpleSubscriptionSpout,
                MINIMUM_NODE_COUNT);
        builder.setSpout(
                ComplexSubscriptionSpout.ID,
                complexSubscriptionSpout,
                MINIMUM_NODE_COUNT);

        var filterSimpleSubscriptionBolt = new FilterSimpleSubscriptionBolt();
        var filterComplexSubscriptionBolt = new FilterComplexSubscriptionBolt();

        builder.setBolt(
                FilterSimpleSubscriptionBolt.ID,
                filterSimpleSubscriptionBolt,
                        MINIMUM_NODE_COUNT)
                .shuffleGrouping(org.project.spouts.fromAMQP.SimplePublicationSpout.ID)
                .shuffleGrouping(SimpleSubscriptionSpout.ID);

        builder.setBolt(
                FilterComplexSubscriptionBolt.ID,
                filterComplexSubscriptionBolt,
                        MINIMUM_NODE_COUNT)
                .shuffleGrouping(ComplexPublicationSpout.ID)
                .shuffleGrouping(ComplexSubscriptionSpout.ID);

        builder.setSpout(
                SimplePublicationSpout.ID,
                new SimplePublicationSpout(
                        AMQP_REQUEUE_ON_FAIL,
                        AMQP_AUTO_ACK),
                MINIMUM_NODE_COUNT);

        builder.setSpout(
                ComplexPublicationSpout.ID,
                new ComplexPublicationSpout(
                        AMQP_REQUEUE_ON_FAIL,
                        AMQP_AUTO_ACK),
                MINIMUM_NODE_COUNT);

        builder.setBolt(
                SimplePublicationBolt.ID,
                new SimplePublicationBolt(),
                        MINIMUM_NODE_COUNT)
                .shuffleGrouping(org.project.spouts.fromStorm.SimplePublicationSpout.ID);

        builder.setBolt(
                        org.project.bolts.toAMQP.ComplexPublicationBolt.ID,
                        new org.project.bolts.toAMQP.ComplexPublicationBolt(),
                        MINIMUM_NODE_COUNT)
                .shuffleGrouping(org.project.bolts.firstLevel.ComplexPublicationBolt.ID);

        var publicationViaSubscriptionBolt = new org.project.bolts.toAMQP.PublicationViaSubscriptionBolt();
        builder.setBolt(
                        PublicationViaSubscriptionBolt.ID,
                        publicationViaSubscriptionBolt,
                        MINIMUM_NODE_COUNT)
                .shuffleGrouping(FilterSimpleSubscriptionBolt.ID)
                .shuffleGrouping(FilterComplexSubscriptionBolt.ID);

        return builder;
    }

    static void setConfig(Config config) {
        // fine tuning => https://storm.apache.org/releases/2.4.0/Performance.html
        config.put(Config.TOPOLOGY_EXECUTOR_RECEIVE_BUFFER_SIZE, GlobalConfiguration.TOPOLOGY_EXECUTOR_RECEIVE_BUFFER_SIZE);
        config.put(Config.TOPOLOGY_TRANSFER_BATCH_SIZE, GlobalConfiguration.TOPOLOGY_TRANSFER_BATCH_SIZE);
        config.put(Config.TOPOLOGY_PRODUCER_BATCH_SIZE, GlobalConfiguration.TOPOLOGY_PRODUCER_BATCH_SIZE);
        config.put(Config.TOPOLOGY_STATS_SAMPLE_RATE, GlobalConfiguration.TOPOLOGY_STATS_SAMPLE_RATE);

        config.registerMetricsConsumer(GraphiteMetricsConsumer.class, 1);
        config.registerMetricsConsumer(org.apache.storm.metric.LoggingMetricsConsumer.class, 1);

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