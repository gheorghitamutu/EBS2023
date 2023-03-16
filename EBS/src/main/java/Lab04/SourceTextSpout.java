package Lab04;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

public class SourceTextSpout extends BaseRichSpout {
	
	private SpoutOutputCollector collector;
	private String[] sourcetext = {
			"text one",
			"text two",
			"text three",
			"text four",
			"too much text after one",
			"drop this text"
	};
	private int i = 0;
	private String task;
	
	private int globalcounter = 0;
	private List<Integer> direct_target_tasks;
	
	// for resending in case of lost tuples - anchored example
	private int id = 0;
	private HashMap<Integer,Values> tobeconfirmed;

	@Override
	public void open(Map<String, Object> conf, TopologyContext context, SpoutOutputCollector collector) {
		
		this.collector = collector;
		this.task = context.getThisComponentId()+" "+context.getThisTaskId();
		System.out.println("----- Started spout task: "+this.task);
		
		// choosing destination bolt for direct grouping
		this.direct_target_tasks = context.getComponentTasks("terminal_bolt");
		
		// for the fault tolerant version
		this.tobeconfirmed = new HashMap<Integer, Values>();
	}

	@Override
	public void nextTuple() {
		
		// continuous emit example
		/*
		this.collector.emit(new Values(sourcetext[i]));
		i++;
		if (i >= sourcetext.length) {
			i = 0;
		}
		*/
		
		// limited emit example
		
		if (i < sourcetext.length) {
			 //this.collector.emit(new Values(sourcetext[i]));
			 //i++;
			 
			 // direct grouping example	
			 // this will emit directly to the first task - index 0 - of the chosen destination, 
			 // for the "secondary" stream, sending a tuple with one field (globalcount):
			 // globalcounter++;
			 // this.collector.emitDirect(direct_target_tasks.get(0), "secondary", new Values(globalcounter));
			 
			 // anchored version example - fault tolerant
			 this.collector.emit(new Values(sourcetext[i]), id);		
			 this.tobeconfirmed.put(id, new Values(sourcetext[i]));
			 id++;
			 i++;
		}
		
		
		/*
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
		// the implicit data events stream
		declarer.declare(new Fields("words"));
		// a different data stream named "secondary"
		// defined as a direct stream by the second boolean parameter
		declarer.declareStream("secondary", true, new Fields("globalcount"));
	}
	
	public void ack(Object id) {
		System.out.println("----- ACKED detected at "+ this.task +" for "+id.toString()+this.tobeconfirmed.get(id).toString());
		this.tobeconfirmed.remove(id);
	}
	
	public void fail(Object id) {
		System.out.println("----- FAILED detected at "+ this.task +" - re-emitting "+id.toString()+this.tobeconfirmed.get(id).toString());
		this.collector.emit(this.tobeconfirmed.get(id), id);
	}

}
