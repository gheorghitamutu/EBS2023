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
    public static final String AMQP_HOST = "localhost";
    public static final int AMQP_PORT = 5672;
    public static final String AMQP_USERNAME = "guest";
    public static final String AMQP_PASSWORD = "guest";
    public static final String AMQP_VHOST = "/";
    public static final boolean AMQP_REQUEUE_ON_FAIL = true;
    public static final boolean AMQP_AUTO_ACK = false;

    public static final long AMQP_ACK_TIMEOUT = 5_000;
}
