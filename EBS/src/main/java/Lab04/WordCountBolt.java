package Lab04;

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

	@Override
	public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
		// TODO Auto-generated method stub
		this.collector = collector;
		this.count = new HashMap<String, Integer>();
		this.task = context.getThisComponentId()+" "+context.getThisTaskId();
		System.out.println("----- Started task: "+this.task);
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
		this.collector.emit(input, new Values(word,wordcount));
		this.collector.ack(input);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("word","count"));
	}

}
