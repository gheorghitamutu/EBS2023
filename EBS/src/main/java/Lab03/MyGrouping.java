package Lab03;

import java.util.ArrayList;
import java.util.List;

import org.apache.storm.generated.GlobalStreamId;
import org.apache.storm.grouping.CustomStreamGrouping;
import org.apache.storm.task.WorkerTopologyContext;

public class MyGrouping implements CustomStreamGrouping {
	
	ArrayList<Integer> targetTasks;
	int taskcounter = 1;

	@Override
	public void prepare(WorkerTopologyContext context, GlobalStreamId stream, List<Integer> targetTasks) {
		
		this.targetTasks = new ArrayList<>(targetTasks);
		for (Integer targetTask : targetTasks) {
			System.out.println("----- CUSTOM GROUPING TASK - " + targetTask);
		}

	}

	@Override
	public List<Integer> chooseTasks(int taskId, List<Object> values) {
		
		ArrayList<Integer> chosenTasks = new ArrayList<>();
		
		String value = values.get(0).toString();
		
		int chosen;
		
		if (value.equals("drop this text")) {
			chosenTasks.add(targetTasks.get(0));
			chosen = targetTasks.get(0);
		}
		else {
			if (taskcounter < targetTasks.size()-1) {
				taskcounter++;
			}
			else {
				taskcounter=1;
			}
			chosenTasks.add(targetTasks.get(taskcounter));
			chosen = targetTasks.get(taskcounter);
		}
		
		System.out.println("----- CHOSEN TASK "+chosen+" for "+value);
		return chosenTasks;
	}

}
