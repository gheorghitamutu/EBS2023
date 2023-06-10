package org.project.cofiguration;

public class GlobalConfiguration {
    public static final String TOPOLOGY_NAME = "weather-topology";
    public static boolean RUNNING_LOCALLY = false;
    public static final int LOCAL_CLUSTER_RUN_TIME = 10 * 60 * 100;
    public static final int MINIMUM_NODE_COUNT = 2;
    public static final int WINDOW_LENGTH = 30;
    public static final int SLIDING_INTERVAL = 10;
    public static final int TOPOLOGY_EXECUTOR_RECEIVE_BUFFER_SIZE = 2048;
    public static final int TOPOLOGY_TRANSFER_BATCH_SIZE = 100;
    public static final int TOPOLOGY_PRODUCER_BATCH_SIZE = 100;
    // To force Storm to count everything exactly to achieve accurate numbers
    // at the cost of a big performance hit to your topology you can set the sampling rate to 100%
    public static final double TOPOLOGY_STATS_SAMPLE_RATE = 1.0;
    public static final boolean TOPOLOGY_DEBUG = false;
    public static final int TOPOLOGY_WORKERS = 1;
    public static final String LOCAL_CLUSTER_ARGUMENT = "--local_cluster";
    public static final int SIMPLE_PUBLICATION_COUNT = 10000;
    public static final int SIMPLE_PUBLICATION_TIME_INTERVAL = 3; // in minutes
    public static final double SIMPLE_PUBLICATION_INTERVAL = (SIMPLE_PUBLICATION_TIME_INTERVAL * 60.0) / SIMPLE_PUBLICATION_COUNT;
    public static final long MAX_TIME = 10 * 60 * 1000; // 10 minutes
    public static final int SIMPLE_SUBSCRIPTION_COUNT = 10000;
    public static final int COMPLEX_SUBSCRIPTION_COUNT = 10000;

    /* AMQP */
    public static final long CONFIG_PREFETCH_COUNT = 0;
    public static final long DEFAULT_PREFETCH_COUNT = 0;
    public static final long WAIT_AFTER_SHUTDOWN_SIGNAL = 0;
    public static final long WAIT_FOR_NEXT_MESSAGE = 1L;
    public static final String SIMPLE_PUBLICATION_EXCHANGE_NAME = "ExchangeSP";
    public static final String SIMPLE_PUBLICATION_QUEUE_NAME = "QueueSP";
    public static final String SIMPLE_PUBLICATION_ROUTING_KEY = "QueueSP";
    public static final String COMPLEX_PUBLICATION_EXCHANGE_NAME = "ExchangeCP";
    public static final String COMPLEX_PUBLICATION_QUEUE_NAME = "QueueCP";
    public static final String COMPLEX_PUBLICATION_ROUTING_KEY = "QueueCP";
    public static final String SIMPLE_ANOMALY_EXCHANGE_NAME = "ExchangeSA";
    public static final String SIMPLE_ANOMALY_QUEUE_NAME = "QueueSA";
    public static final String SIMPLE_ANOMALY_ROUTING_KEY = "QueueSA";
    public static final String COMPLEX_ANOMALY_EXCHANGE_NAME = "ExchangeCA";
    public static final String COMPLEX_ANOMALY_QUEUE_NAME = "QueueCA";
    public static final String COMPLEX_ANOMALY_ROUTING_KEY = "QueueCA";
    public static String AMQP_HOST = "EBS-rabbit";
    public static int AMQP_PORT = 5672;
    public static final String AMQP_USERNAME = "guest";
    public static final String AMQP_PASSWORD = "guest";
    public static final String AMQP_VHOST = "/";
    public static final boolean AMQP_REQUEUE_ON_FAIL = true;
    public static final boolean AMQP_AUTO_ACK = false;

    public static final long AMQP_ACK_TIMEOUT = 5_000;

    // Metrics
    public static final int TIME_BUCKET_SIZE_IN_SECS = 2;
    public static final String METRICS_LATENCY_COMPLEX_PUBLICATION_GENERATION = "latency_complex_publication_generation";
    public static final String METRICS_LATENCY_COMPLEX_PUBLICATION_STORAGE = "latency_complex_publication_storage";
    public static final String METRICS_LATENCY_SIMPLE_PUBLICATION_FULL_FLOW = "latency_simple_publication_full_flow";
    public static final String METRICS_LATENCY_COMPLEX_PUBLICATION_FULL_FLOW = "latency_complex_publication_full_flow";

    // Graphite
    public static String GRAPHITE_HOST = "EBS-graphite";
    public static int GRAPHITE_PORT = 2003;
}
