package org.project;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.ConfigurableTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.project.bolts.SimplePublicationBolt;
import org.project.bolts.SimplePublicationAggregatorBolt;
import org.project.spouts.SimplePublicationSpout;

public class Application extends ConfigurableTopology {

    private static final String TOPOLOGY_NAME = "project_topology";

    public static void main(String[] args) throws Exception {
        ConfigurableTopology.start(new Application(), args);
    }

    @Override
    protected int run(String[] args) throws Exception {
        var builder = new TopologyBuilder();
        var source = new SimplePublicationSpout();
        var simplePublicationAggregatorBolt =
                new SimplePublicationAggregatorBolt()
                        .withWindow(
                                new BaseWindowedBolt.Count(30),
                                new BaseWindowedBolt.Count(10));
        var simplePublicationBolt = new SimplePublicationBolt();

        builder.setSpout(
                SimplePublicationSpout.ID,
                source,
                1);

        builder.setBolt(
                SimplePublicationAggregatorBolt.ID,
                simplePublicationAggregatorBolt,
                1)
                .shuffleGrouping(SimplePublicationSpout.ID);

        builder.setBolt(
                SimplePublicationBolt.ID,
                simplePublicationBolt,
                1)
                .shuffleGrouping(SimplePublicationSpout.ID);

        // fine tuning => https://storm.apache.org/releases/2.4.0/Performance.html
        conf.put(Config.TOPOLOGY_EXECUTOR_RECEIVE_BUFFER_SIZE, 1024);
        conf.put(Config.TOPOLOGY_TRANSFER_BATCH_SIZE, 10);
        conf.put(Config.TOPOLOGY_PRODUCER_BATCH_SIZE, 10);
        conf.put(Config.TOPOLOGY_STATS_SAMPLE_RATE, 0.001);

        conf.setDebug(true);
        conf.setNumWorkers(1);

        /*
        Config config = new Config();
        try (LocalCluster cluster = new LocalCluster()) {
            StormTopology topology = builder.createTopology();

            // fine tuning => https://storm.apache.org/releases/2.4.0/Performance.html
            config.put(Config.TOPOLOGY_EXECUTOR_RECEIVE_BUFFER_SIZE, 1024);
            config.put(Config.TOPOLOGY_TRANSFER_BATCH_SIZE, 10);
            config.put(Config.TOPOLOGY_PRODUCER_BATCH_SIZE, 10);
            config.put(Config.TOPOLOGY_STATS_SAMPLE_RATE, 0.001);

            config.setDebug(true);
            config.setNumWorkers(1);

            cluster.submitTopology(TOPOLOGY_NAME, config, topology);

            try {
                Thread.sleep(10 * 60 * 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            cluster.killTopology(TOPOLOGY_NAME);
            cluster.shutdown();
        }
        */

        return submit(TOPOLOGY_NAME, conf, builder);
    }
}