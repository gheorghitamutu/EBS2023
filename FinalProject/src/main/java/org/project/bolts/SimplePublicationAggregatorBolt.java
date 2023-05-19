package org.project.bolts;

import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseWindowedBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.windowing.TupleWindow;
import org.project.models.ProtoSimplePublication;

import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimplePublicationAggregatorBolt extends BaseWindowedBolt {

    public static final String ID = SimplePublicationAggregatorBolt.class.toString();
    private static final Logger LOG = Logger.getLogger(SimplePublicationAggregatorBolt.class);

    private int eventsReceived;
    private OutputCollector collector;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector collector) {
        this.eventsReceived = 0;
        this.collector = collector;
    }

    @Override
    public void execute(TupleWindow input) {
        var oldCount = eventsReceived;
        for (var tuple: input.get()) {
            AtomicBoolean failed = new AtomicBoolean(false);
            tuple.getFields().forEach((f) -> {
                eventsReceived++;
                var sp = (ProtoSimplePublication.SimplePublication)(tuple.getValueByField(f));
                // LOG.info(MessageFormat.format("Input Field: <{0}> Event received: #{1}\n{2}", f, eventsReceived, sp));

                try {
                    // TODO: this needs to construct a complex publication
                    this.collector.emit(tuple, new Values(sp));
                } catch (Exception e) {
                    failed.set(true);
                    eventsReceived = oldCount;
                    LOG.error(MessageFormat.format("Failed to re-emit simple publication {0}!", e.getMessage()));
                    this.collector.fail(tuple);
                }
            });

            if (failed.get()) {
                return;
            }

            this.collector.ack(tuple);
        }
        // LOG.info(MessageFormat.format("Processed <{0}> value(s)!", eventsReceived - oldCount));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // TODO: this needs to declare a complex publication
        declarer.declare(new Fields("SimplePublication"));
    }

    @Override
    public void cleanup() {
        LOG.info(MessageFormat.format("Events received: {0}!", this.eventsReceived));
    }
}
