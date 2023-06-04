package org.project.cofiguration;

import org.apache.storm.Config;

public class GlobalConfiguration {
    public static final String TOPOLOGY_NAME = "weather-topology";
    public static final int LOCAL_CLUSTER_RUN_TIME = 10 * 60 * 100;
    public static final int WINDOW_LENGTH = 30;
    public static final int SLIDING_INTERVAL = 10;
    public static final int TOPOLOGY_EXECUTOR_RECEIVE_BUFFER_SIZE = 2048;
    public static final int TOPOLOGY_TRANSFER_BATCH_SIZE = 100;
    public static final int TOPOLOGY_PRODUCER_BATCH_SIZE = 100;
    public static final double TOPOLOGY_STATS_SAMPLE_RATE = 0.001;
    public static final boolean TOPOLOGY_DEBUG = false;
    public static final int TOPOLOGY_WORKERS = 1;
    public static final String LOCAL_CLUSTER_ARGUMENT = "--local_cluster";
    public static final int SIMPLE_PUBLICATION_COUNT = 10000;
    public static final int SIMPLE_PUBLICATION_TIME_INTERVAL = 3; // in minutes
    public static final double SIMPLE_PUBLICATION_INTERVAL = (SIMPLE_PUBLICATION_TIME_INTERVAL * 60.0) / SIMPLE_PUBLICATION_COUNT;
    public static final long MAX_TIME = 10 * 60 * 1000; // 10 minutes
    public static final int SIMPLE_SUBSCRIPTION_COUNT = 10000;
    public static final int COMPLEX_SUBSCRIPTION_COUNT = 10000;
}
