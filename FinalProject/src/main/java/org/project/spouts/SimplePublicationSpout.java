package org.project.spouts;

import org.apache.log4j.Logger;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.project.models.ProtoSimplePublication;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.*;

public class SimplePublicationSpout extends BaseRichSpout {

    private SpoutOutputCollector collector;
    private String taskName;
    private Map<String, ProtoSimplePublication.SimplePublication> unconfirmed;
    private int simplePublicationCount;

    private static final Logger LOG = Logger.getLogger(SimplePublicationSpout.class);
    public static final String ID = SimplePublicationSpout.class.toString();
    private static final int MAX_SIMPLE_PUBLICATION_COUNT = 1000000;

    private static final long MAX_TIME = 10 * 60 * 1000; // 10 minutes
    private static final long START_TIME = System.currentTimeMillis();

    @Override
    public void open(Map<String, Object> map, TopologyContext topologyContext, SpoutOutputCollector collector) {
        this.collector = collector;
        this.simplePublicationCount = 0;
        this.taskName = MessageFormat.format("<{0} <-> {0}>", topologyContext.getThisComponentId(), topologyContext.getThisTaskId());
        this.unconfirmed = new HashMap<>();
    }

    @Override
    public void nextTuple() {
        if (System.currentTimeMillis() - START_TIME > MAX_TIME) {
            return;
        }

        // if (simplePublicationCount >= MAX_SIMPLE_PUBLICATION_COUNT) {
        //    return;
        // }

        simplePublicationCount++;

        var sp = SimplePublicationGenerator.generateSamplePublication();
        unconfirmed.put(sp.getUuid(), sp);

        this.collector.emit(new Values(sp), sp.getUuid());
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("SimplePublication"));
    }

    @Override
    public void ack(Object id) {
        var uuid = (String)id;
        LOG.info(MessageFormat.format("ACKED detected at {0} for {1}!", this.taskName, uuid));
        this.unconfirmed.remove(uuid);
    }

    @Override
    public void fail(Object id) {
        var uuid = (String)id;
        LOG.info(MessageFormat.format("FAILURE detected at {0} for {1}!", this.taskName, uuid));
        this.collector.emit(new Values(this.unconfirmed.get(uuid)), uuid);
    }

    public static class SimplePublicationGenerator {

        private static final String[] CITIES = { "San Francisco", "New York", "London", "Paris", "Tokyo" };
        private static final Map<String, List<String>> STATION_IDS = new HashMap<>() {{
            put(CITIES[0], new ArrayList<>() {{
                add("0");
                add("1");
            }});
            put(CITIES[1], new ArrayList<>() {{
                add("0");
                add("1");
            }});
            put(CITIES[2], new ArrayList<>() {{
                add("0");
                add("1");
            }});
            put(CITIES[3], new ArrayList<>() {{
                add("0");
                add("1");
            }});
            put(CITIES[4], new ArrayList<>() {{
                add("0");
                add("1");
            }
            });
        }};
        private static final String[] DIRECTIONS = { "N", "NE", "E", "SE", "S", "SW", "W", "NW" };
        private static final Random RANDOM = new Random();

        public static ProtoSimplePublication.SimplePublication generateSamplePublication() {
            String city = CITIES[RANDOM.nextInt(CITIES.length)];
            String stationId = STATION_IDS.get(city).get(RANDOM.nextInt(STATION_IDS.get(city).size()));
            double temperature = RANDOM.nextDouble() * 50;
            if (RANDOM.nextDouble() > 0.5) {
                temperature *= -1;
            }
            double rain = RANDOM.nextDouble() * 0.5;
            double wind = RANDOM.nextDouble() * 30;
            String direction = DIRECTIONS[RANDOM.nextInt(DIRECTIONS.length)];
            Date date = Date.from(
                    new Date(System.currentTimeMillis()).toInstant()
                            .plus(Duration.ofDays(RANDOM.nextInt(30) + 1))
                            .plus(Duration.ofMinutes(RANDOM.nextInt(720) + 1)));

            return ProtoSimplePublication.SimplePublication.newBuilder()
                    .setUuid(UUID.randomUUID().toString())
                    .setStationId(stationId)
                    .setCity(city)
                    .setTemperature(temperature)
                    .setRain(rain)
                    .setWind(wind)
                    .setDirection(direction)
                    .setDate(date.toString())
                    .build();
        }
    }
}
