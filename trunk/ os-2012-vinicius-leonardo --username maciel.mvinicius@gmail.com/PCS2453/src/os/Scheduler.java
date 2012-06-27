package os;

import hw.*;
import java.util.LinkedList;

public class Scheduler {
	static final String[] ACTIONS = {};
	
	private int initialTime;
	private int finalTime;
	
	private Memory memory;
	private Processor processor;
	private Disc disc;
	
	private LinkedList<Event> eventList;
	private LinkedList<Job> jobList;
	
	private static final int MEMORY_SIZE = 64;
	
	Scheduler() {
		eventList = new LinkedList<Event>();
		try {
			Input.read("input.txt", initialTime, finalTime, jobList, eventList);
		}
		catch(InputReadException e) {
			e.printStackTrace();
		}
		memory = new Memory(MEMORY_SIZE);
		processor = new Processor();
		disc = new Disc();
	}
	
	/**
	 * Executes all the events stored into the eventList.
	 */
	public void run() {
		// starts timing
		int currentTime = this.initialTime;
		
		// get the first event arriving
		Event currentEvent = Event.getNew(eventList);
		currentTime = currentEvent.getTime();
		
		// print table header
		System.out.println("\n\nExecution:");
		System.out.println("Time   Event                            Job  Action       Results");
		
		// while there are more events, and current time didn't surpass the end of times
		while(currentEvent != null || currentTime > this.finalTime) {
			switch(currentEvent.getType()) {
			// job arrives
			case Event.ARRIVAL:
				System.out.println();
				break;
				
			// job requests memory allocation
			case Event.REQUEST_MEMORY:
				
				break;
			
			// job requests processor execution
			case Event.REQUEST_PROCESSOR:
				
				break;
			
			// job requests I/O operation
			case Event.REQUEST_DISC:
				
				break;
			
			// job releases disc, returning to processor
			case Event.RELEASE_DISC:
				
				break;
			
			// job releases memory and processor
			case Event.RELEASE_MEMORY:
				
				break;
			
			// job completes its execution
			case Event.COMPLETE:
				
				break;
			
			// others...
			default:
				System.out.println("Invalid event.");
			}
			
			// get the following event
			currentEvent = Event.getNew(eventList);
		}
	}
}
