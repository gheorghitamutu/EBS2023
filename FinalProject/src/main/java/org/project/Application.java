package org.project;

import com.esotericsoftware.kryo.Kryo;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.ConfigurableTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.project.bolts.*;
import org.project.filters.Operator;
import org.project.filters.SimplePublicationFilter;
import org.project.spouts.SimplePublicationSpout;

import java.util.List;

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
        var anomalySimpleBolt = new AnomalySimpleBolt();

        var filterSimpleAnomalyBolt = new FilterSimpleAnomalyBolt(
                SimplePublicationFilter.composedFilter(
                        List.of(
                                SimplePublicationFilter.filterByCity(Operator.Type.EQUAL, "San Francisco")
                        )
                )
        );

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
                FilterSimpleAnomalyBolt.ID,
                filterSimpleAnomalyBolt,
                1
        ).shuffleGrouping(SimplePublicationSpout.ID);

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
                        AnomalySimpleBolt.ID,
                        anomalySimpleBolt,
                        1)
                .shuffleGrouping(SimplePublicationBolt.ID);

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