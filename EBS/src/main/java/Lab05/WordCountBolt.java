package Lab05;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class WordCountBolt extends BaseRichBolt {

    private OutputCollector collector;
    private HashMap<String, Integer> count;
    private String task;

    private CustomMessage test_message;
    private Color test_color;

    @Override
    public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
        // TODO Auto-generated method stub
        this.collector = collector;
        this.count = new HashMap<>();
        this.task = context.getThisComponentId()+" "+context.getThisTaskId();
        System.out.println("----- Started task: "+this.task);

        this.test_message = new CustomMessage();
        test_message.set_name("John Doe");
        test_message.set_phone_numbers(new ArrayList<>(Arrays.asList("111", "222", "333")));

        this.test_color = new Color(55,75,95);
    }

    @Override
    public void execute(Tuple input) {
        // TODO Auto-generated method stub
        String word = input.getStringByField("word");
        Integer wordcount = this.count.get(word);

        System.out.println("----- "+ this.task +
                " received "+ word +
                " from "+ input.getSourceComponent() + " " + input.getSourceTask());

        if (wordcount == null) {
            wordcount = 0;
        }
        wordcount++;
        this.count.put(word, wordcount);

        // normal emit example
        //this.collector.emit(new Values(word,wordcount));

        // anchored version example - fault tolerant
        this.collector.emit(input, new Values(word,wordcount,test_message,test_color));
        this.collector.ack(input);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // TODO Auto-generated method stub
        declarer.declare(new Fields("word","count","custom","color"));
    }

}
