package Lab04;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class SplitTextBolt extends BaseRichBolt {
	
	private OutputCollector collector;
	private String task;
	private int task_id;
	private int processed_tuples = 0;
	
	// for the custom grouping example
	// private int drop_task;
	
	// for the anchored version example - fault tolerant
	int fail = 0;

	@Override
	public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
		// TODO Auto-generated method stub
		this.collector = collector;
		this.task_id = context.getThisTaskId();
		this.task = context.getThisComponentId()+" "+this.task_id;
		System.out.println("----- Started task: "+this.task);
		
		// for custom grouping example
		//this.drop_task = context.getComponentTasks(context.getThisComponentId()).get(0);

	}

	@Override
	public void execute(Tuple input) {
		// TODO Auto-generated method stub
		String sourcetext = input.getStringByField("words");
		
		processed_tuples++;
		System.out.println("----- "+ this.task +
						   " received "+ sourcetext + 
						   " from "+ input.getSourceComponent() + " " + input.getSourceTask() +
						   " processed "+ processed_tuples);	
		
		// for custom grouping example
		//if (task_id == drop_task) return;
		
		// normal processing example
		String[] words = sourcetext.split(" ");
		//for(String word : words) {
		//	this.collector.emit(new Values(word));
		//}
		
		// anchored version - fault tolerant
		if (fail % 3 == 0) {
			this.collector.fail(input);
			System.out.println("----- "+ this.task + " failed at " + input.toString());
			fail++;	
		}
		else {		
			for(String word : words) {
				this.collector.emit(input, new Values(word));
			}
						
			this.collector.ack(input);
			fail++;
		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("word"));

	}

}
