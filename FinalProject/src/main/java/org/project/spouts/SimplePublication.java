package org.project.spouts;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.project.models.ProtoSimplePublication;

import java.util.Map;

public class SimplePublication extends BaseRichSpout {

    private SpoutOutputCollector collector;

    public static final String ID = SimplePublication.class.toString();

    @Override
    public void open(Map<String, Object> map, TopologyContext topologyContext, SpoutOutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void nextTuple() {
        var sp = ProtoSimplePublication.SimplePublication.newBuilder()
                .setStationId("12345")
                .setCity("San Francisco")
                .setTemperature(65.3)
                .setRain(0.12)
                .setWind(10.2)
                .setDirection("NE")
                .setDate("2023-05-06")
                .build();

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
}
