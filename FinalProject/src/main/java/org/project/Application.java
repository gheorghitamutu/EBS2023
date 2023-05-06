package org.project;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
import org.project.bolts.DummyTerminal;
import org.project.spouts.SimplePublication;

public class Application {

    private static final String TOPOLOGY_NAME = "project_topology";

    public static void main(String[] args) throws Exception {
        var builder = new TopologyBuilder();
        var source = new SimplePublication();
        var terminal = new DummyTerminal();

        builder.setSpout(SimplePublication.ID, source);
        builder.setBolt(DummyTerminal.ID, terminal).globalGrouping(SimplePublication.ID);

        Config config = new Config();
        try (LocalCluster cluster = new LocalCluster()) {
            StormTopology topology = builder.createTopology();

            // fine tuning
            config.put(Config.TOPOLOGY_EXECUTOR_RECEIVE_BUFFER_SIZE, 1024);
            // config.put(Config.TOPOLOGY_DISRUPTOR_BATCH_SIZE,1);
            config.put(Config.TOPOLOGY_TRANSFER_BATCH_SIZE, 1);

            cluster.submitTopology(TOPOLOGY_NAME, config, topology);

            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            cluster.killTopology(TOPOLOGY_NAME);
            cluster.shutdown();
        }
    }
}