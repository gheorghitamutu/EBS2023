package org.project.bolts;

import org.apache.log4j.Logger;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.project.models.ProtoSimplePublication;

import java.text.MessageFormat;
import java.util.Map;

public class SimplePublicationBolt extends BaseRichBolt {

    public static final String ID = SimplePublicationBolt.class.toString();
    private static final Logger LOG = Logger.getLogger(SimplePublicationBolt.class);
    private int eventsReceived;
    private OutputCollector collector;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector collector) {
        this.eventsReceived = 0;
        this.collector = collector;
    }

    enum AnomalyType {
        NONE,
        TEMPERATURE,
        RAIN,
        WIND
    }

    public AnomalyType isAnomaly(ProtoSimplePublication.SimplePublication sp) {
        final double MAX_TEMPERATURE = 44.0;
        final double MIN_TEMPERATURE = 25.0;
        final double MAX_WIND = 30.0;
        final double MAX_RAIN = 0.8;

        final var temperature = sp.getTemperature();
        if (temperature >= MAX_TEMPERATURE || temperature <= MIN_TEMPERATURE) {
            return AnomalyType.TEMPERATURE;
        }

        if (sp.getWind() >= MAX_WIND) {
            return AnomalyType.WIND;
        }

        if (sp.getRain() >= MAX_RAIN) {
            return AnomalyType.RAIN;
        }

        return AnomalyType.NONE;
    }

    public String anomalyTypeToString(AnomalyType anomalyType) {
        switch (anomalyType) {
            case TEMPERATURE:
                return "Temperature";
            case WIND:
                return "Wind";
            case RAIN:
                return "Rain";
            default:
                return "None";
        }
    }

    @Override
    public void execute(Tuple input) {
        var oldCount = eventsReceived;
        input.getFields().forEach((f) -> {
            eventsReceived++;
            var sp = (ProtoSimplePublication.SimplePublication)(input.getValueByField(f));
            LOG.info(MessageFormat.format("Input Field: <{0}> Event received: #{1}\n{2}", f, eventsReceived, sp));

            var anomaly = isAnomaly(sp);
            if (anomaly != AnomalyType.NONE) {
                LOG.info(MessageFormat.format("Anomaly detected: {0}", isAnomaly(sp)));
                this.collector.emit(input, new Values(anomalyTypeToString(anomaly), sp));
            } else {
                LOG.info("No anomaly detected!");
            }
        });
        this.collector.ack(input);
        LOG.info(MessageFormat.format("Processed <{0}> value(s)!", eventsReceived - oldCount));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("AnomalyType", "SimplePublication"));
    }

    @Override
    public void cleanup() {
        LOG.info(MessageFormat.format("Events received: {0}!", this.eventsReceived));
    }
}
