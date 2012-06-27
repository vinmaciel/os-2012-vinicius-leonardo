package os;

import hw.*;
import java.util.LinkedList;

public class Scheduler {
	private int initialTime;
	private int finalTime;
	
	private Memory memory;
	private Processor processor;
	private Disc disc;
	
	private LinkedList<Event> eventList;
	private LinkedList<Job> jobList;
	
	private static final int MEMORY_SIZE = 512;
	
	Scheduler() {
		eventList = new LinkedList<Event>();
		try {
			Input.read("input.txt", initialTime, finalTime, eventList, jobList);
		}
		catch(InputReadException e) {
			e.printStackTrace();
		}
		memory = new Memory(MEMORY_SIZE);
		processor = new Processor();
		disc = new Disc();
	}
}
