package org.project.bolts;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.project.models.ProtoSimplePublication;

import java.text.MessageFormat;
import java.util.Map;

public class DummyTerminal extends BaseRichBolt {

    public static final String ID = DummyTerminal.class.toString();

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        System.out.println("prepare");
    }

    @Override
    public void execute(Tuple input) {
        input.getFields().forEach((f) -> {
            var sp = (ProtoSimplePublication.SimplePublication)(input.getValueByField(f));
            System.out.println(MessageFormat.format("Input Field: <{0}>\n{1}", f, sp));
        });

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        System.out.println("declareOutputFields");
    }

    @Override
    public void cleanup() {
        System.out.println("cleanup");
    }
}
