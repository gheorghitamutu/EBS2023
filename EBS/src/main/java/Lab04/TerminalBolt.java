package Lab04;

import java.util.HashMap;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

public class TerminalBolt extends BaseRichBolt {
	
	private HashMap<String, Integer> count;
	
	private OutputCollector collector;

	@Override
	public void prepare(Map<String, Object> topoConf, TopologyContext context, OutputCollector collector) {
		// TODO Auto-generated method stub
		this.count = new HashMap<String, Integer>();
		
		// required for the anchored version
		this.collector = collector;
	}

	@Override
	public void execute(Tuple input) {
		// TODO Auto-generated method stub
		// direct grouping example
		if (input.getSourceStreamId().equals("secondary")) {
			Integer globalcount = input.getIntegerByField("globalcount");
			System.out.println("----- Terminal Bolt globalcount: "+ globalcount + " source "+input.getSourceTask());
		}
		else {
			String word = input.getStringByField("word");
			Integer count = input.getIntegerByField("count");
			this.count.put(word, count);
			
			// the anchored version - fault tolerant
			this.collector.ack(input);
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub

	}
	
	public void cleanup() {
		System.out.println("Topology Result:");
		for (Map.Entry<String, Integer> entry : this.count.entrySet()) {
			System.out.println(entry.getKey()+" - "+entry.getValue());
		}
	}

}
