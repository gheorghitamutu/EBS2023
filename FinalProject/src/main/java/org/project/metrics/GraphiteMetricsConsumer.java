package org.project.metrics;

import com.codahale.metrics.*;
import com.codahale.metrics.Timer;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import org.apache.log4j.Logger;
import org.apache.storm.metric.api.IMetricsConsumer;
import org.apache.storm.task.IErrorReporter;
import org.apache.storm.task.TopologyContext;

import java.net.InetSocketAddress;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.project.cofiguration.GlobalConfiguration.*;

public class GraphiteMetricsConsumer implements IMetricsConsumer {
    private static final Logger LOG = Logger.getLogger(GraphiteMetricsConsumer.class);
    private final List<Long> complexPublicationGeneration = new ArrayList<>();
    private final List<Long> complexPublicationStorage = new ArrayList<>();
    private final List<Long> simplePublicationFullFlow = new ArrayList<>();
    private final List<Long> complexPublicationFullFlow = new ArrayList<>();

    MetricRegistry metricRegistry = null;
    Counter counterComplexPublicationGeneration = null;
    Meter meterComplexPublicationGeneration = null;
    Timer timerComplexPublicationGeneration = null;
    Histogram histogramComplexPublicationGeneration = null;
    Counter counterComplexPublicationStorage= null;
    Meter meterComplexPublicationStorage = null;
    Timer timerComplexPublicationStorage = null;
    Histogram histogramComplexPublicationStorage = null;
    Counter counterSimplePublicationFullFlow = null;
    Meter meterSimplePublicationFullFlow = null;
    Timer timerSimplePublicationFullFlow = null;
    Histogram histogramSimplePublicationFullFlow = null;
    Counter counterComplexPublicationFullFlow= null;
    Meter meterComplexPublicationFullFlow = null;
    Timer timerComplexPublicationFullFlow = null;
    Histogram histogramComplexPublicationFullFlow = null;

    public Double computeAverage(List<Long> times) {
        return times.stream().mapToDouble(Long::longValue).sum() / times.size();
    }

    @Override
    public void prepare(Map<String, Object> configuration, Object registrationArgument, TopologyContext context, IErrorReporter errorReporter) {
        LOG.info("Preparing MetricsConsumer...");

        this.metricRegistry = new MetricRegistry();
        Graphite graphite = new Graphite(new InetSocketAddress(GRAPHITE_HOST, GRAPHITE_PORT));
        GraphiteReporter reporter = GraphiteReporter.forRegistry(metricRegistry)
                .prefixedWith(
                        MessageFormat.format(
                                RUNNING_LOCALLY ? "local.{0}" : "{0}",
                                configuration.get("storm.id")))
                .build(graphite);
        reporter.start(1, TimeUnit.SECONDS);

        this.counterComplexPublicationGeneration = metricRegistry.counter("counter.cpg");
        this.meterComplexPublicationGeneration = metricRegistry.meter("meter.cpg");
        this.timerComplexPublicationGeneration = metricRegistry.timer("timer.cpg");
        this.histogramComplexPublicationGeneration = metricRegistry.histogram("histogram.cpg");
        this.metricRegistry.register("gauge.cpg", (Gauge<Double>) () -> computeAverage(complexPublicationGeneration));

        this.counterComplexPublicationStorage = metricRegistry.counter("counter.cps");
        this.meterComplexPublicationStorage = metricRegistry.meter("meter.cps");
        this.timerComplexPublicationStorage = metricRegistry.timer("timer.cps");
        this.histogramComplexPublicationStorage = metricRegistry.histogram("histogram.cps");
        this.metricRegistry.register("gauge.cps", (Gauge<Double>) () -> computeAverage(complexPublicationStorage));

        this.counterSimplePublicationFullFlow = metricRegistry.counter("counter.spff");
        this.meterSimplePublicationFullFlow = metricRegistry.meter("meter.spff");
        this.timerSimplePublicationFullFlow = metricRegistry.timer("timer.spff");
        this.histogramSimplePublicationFullFlow = metricRegistry.histogram("histogram.spff");
        this.metricRegistry.register("gauge.spff", (Gauge<Double>) () -> computeAverage(simplePublicationFullFlow));

        this.counterComplexPublicationFullFlow = metricRegistry.counter("counter.cpff");
        this.meterComplexPublicationFullFlow = metricRegistry.meter("meter.cpff");
        this.timerComplexPublicationFullFlow = metricRegistry.timer("timer.cpff");
        this.histogramComplexPublicationFullFlow = metricRegistry.histogram("histogram.cpff");
        metricRegistry.register("gauge.cpff", (Gauge<Double>) () -> computeAverage(complexPublicationFullFlow));
    }

    @Override
    public void handleDataPoints(TaskInfo taskInfo, Collection<DataPoint> dataPoints) {
        for (DataPoint dataPoint : dataPoints) {
            final String name = dataPoint.name;
            switch (name) {
                case METRICS_LATENCY_COMPLEX_PUBLICATION_GENERATION:
                    this.complexPublicationGeneration.add((Long) dataPoint.value);
                    this.counterComplexPublicationGeneration.inc();
                    this.meterComplexPublicationGeneration.mark();
                    this.timerComplexPublicationGeneration.update((Long) dataPoint.value, TimeUnit.MILLISECONDS);
                    this.histogramComplexPublicationGeneration.update((Long) dataPoint.value);
                    break;
                case METRICS_LATENCY_COMPLEX_PUBLICATION_STORAGE:
                    this.complexPublicationStorage.add((Long) dataPoint.value);
                    this.counterComplexPublicationStorage.inc();
                    this.meterComplexPublicationStorage.mark();
                    this.timerComplexPublicationStorage.update((Long) dataPoint.value, TimeUnit.MILLISECONDS);
                    this.histogramComplexPublicationStorage.update((Long) dataPoint.value);
                    break;
                case METRICS_LATENCY_SIMPLE_PUBLICATION_FULL_FLOW:
                    this.simplePublicationFullFlow.add((Long) dataPoint.value);
                    this.counterSimplePublicationFullFlow.inc();
                    this.meterSimplePublicationFullFlow.mark();
                    this.timerSimplePublicationFullFlow.update((Long) dataPoint.value, TimeUnit.MILLISECONDS);
                    this.histogramSimplePublicationFullFlow.update((Long) dataPoint.value);
                    break;
                case METRICS_LATENCY_COMPLEX_PUBLICATION_FULL_FLOW:
                    this.complexPublicationFullFlow.add((Long) dataPoint.value);
                    this.counterComplexPublicationFullFlow.inc();
                    this.meterComplexPublicationFullFlow.mark();
                    this.timerComplexPublicationFullFlow.update((Long) dataPoint.value, TimeUnit.MILLISECONDS);
                    this.histogramComplexPublicationFullFlow.update((Long) dataPoint.value);
                    break;
            }
        }
    }

    @Override
    public void cleanup() {
        LOG.info(MessageFormat.format("Average latency for complex publication generation: {0}ms", computeAverage(complexPublicationGeneration)));
        LOG.info(MessageFormat.format("Average latency for complex publication storage: {0}ms", computeAverage(complexPublicationStorage)));
        LOG.info(MessageFormat.format("Average latency for simple publication full flow: {0}ms", computeAverage(simplePublicationFullFlow)));
        LOG.info(MessageFormat.format("Average latency for complex publication full flow: {0}ms", computeAverage(complexPublicationFullFlow)));

        LOG.info("Cleaning up ComplexPublicationCreationMetricConsumer...");
    }
}
