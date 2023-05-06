package org.project.spouts;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.project.models.ProtoSimplePublication;

import java.time.Duration;
import java.util.*;

public class SimplePublication extends BaseRichSpout {

    private SpoutOutputCollector collector;
    private int simplePublicationCount;

    public static final String ID = SimplePublication.class.toString();
    private static final int MAX_SIMPLE_PUBLICATION_COUNT = 1000;

    @Override
    public void open(Map<String, Object> map, TopologyContext topologyContext, SpoutOutputCollector collector) {
        this.collector = collector;
        this.simplePublicationCount = 0;
    }

    @Override
    public void nextTuple() {
        if (simplePublicationCount >= MAX_SIMPLE_PUBLICATION_COUNT) {
            return;
        }
        simplePublicationCount++;

        var sp = SimplePublicationGenerator.generateSamplePublication();

        // System.out.println(sp);
        // var buffer = sp.toByteArray();

        /*
        this.collector.emit(
                new Values(
                        sp.getStationId(),
                        sp.getCity(),
                        sp.getTemperature(),
                        sp.getRain(),
                        sp.getWind(),
                        sp.getDirection(),
                        sp.getDate()));
         */

        this.collector.emit(new Values(sp));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("SimplePublication"));

        /*
        declarer.declare(
                new Fields(
                        "station_id",
                        "city",
                        "temperature",
                        "rain",
                        "wind",
                        "direction",
                        "date"));
         */
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
            double temperature = RANDOM.nextDouble() * 50 + 50;
            double rain = RANDOM.nextDouble() * 0.5;
            double wind = RANDOM.nextDouble() * 30;
            String direction = DIRECTIONS[RANDOM.nextInt(DIRECTIONS.length)];
            Date date = Date.from(new Date(System.currentTimeMillis()).toInstant().plus(Duration.ofDays(RANDOM.nextInt(30) + 1)));

            return ProtoSimplePublication.SimplePublication.newBuilder()
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
