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
		Event nextEvent = null;
		
		// print table header
		System.out.println("\n\nExecution:");
		System.out.println("Time     Event  Job  Action       Results");
		
		// while there are more events, and current time didn't surpass the end of times
		while(currentEvent != null || currentTime > this.finalTime) {
			System.out.format("%-8d ", currentTime);
			
			switch(currentEvent.getType()) {
			// job arrives
			case Event.ARRIVAL:
				nextEvent = new Event(currentEvent.getJob(), currentTime, Event.REQUEST_MEMORY);
				nextEvent.insert(eventList);
				System.out.format("%-6d %-4d " + Event.EVENTS[Event.ARRIVAL] + "Job arrived at the system.", Event.ARRIVAL, currentEvent.getJob().getId());
				break;
				
			// job requests memory allocation
			case Event.REQUEST_MEMORY:
				if(memory.thereIsFreeSpace(currentEvent.getJob().getSize())) {
					memory.allocate(currentEvent.getJob().getSize());
					nextEvent = new Event(currentEvent.getJob(), currentTime, Event.REQUEST_PROCESSOR);
					nextEvent.insert(eventList);
					System.out.format("%-6d %-4d " + Event.EVENTS[Event.REQUEST_MEMORY] + "Memory allocated to the job.", Event.REQUEST_MEMORY, currentEvent.getJob().getId());
				}
				else {
					memory.enqueue(currentEvent.getJob());
					System.out.format("%-6d %-4d " + Event.EVENTS[Event.REQUEST_MEMORY] + "Job entered memory queue.", Event.REQUEST_MEMORY, currentEvent.getJob().getId());
				}
				break;
			
			// job requests processor execution
			case Event.REQUEST_PROCESSOR:
				if(processor.isFree()) {
					processor.assign();
					// TODO: nextEvent = new Event(currentEvent.getJob(), ?????, Event.?????);
					nextEvent.insert(eventList);
					System.out.format("%-6d %-4d " + Event.EVENTS[Event.REQUEST_PROCESSOR] + "Processor assigned to the job.", Event.REQUEST_PROCESSOR, currentEvent.getJob().getId());
				}
				else {
					processor.enqueue(currentEvent.getJob());
					System.out.format("%-6d %-4d " + Event.EVENTS[Event.REQUEST_PROCESSOR] + "Job entered processor queue.", Event.REQUEST_PROCESSOR, currentEvent.getJob().getId());
				}
				break;
			
			// job requests I/O operation
			case Event.REQUEST_DISC:
				if(disc.isFree()) {
					disc.assign();
					// TODO: nextEvent = new Event(currentEvent.getJob(), ?????, Event.RELEASE_DISC);
					nextEvent.insert(eventList);
					System.out.format("%-6d %-4d " + Event.EVENTS[Event.REQUEST_DISC] + "Disc assigned to the job.", Event.REQUEST_DISC, currentEvent.getJob().getId());
				}
				else {
					disc.enqueue(currentEvent.getJob());
					System.out.format("%-6d %-4d " + Event.EVENTS[Event.REQUEST_DISC] + "Job entered disc queue.", Event.REQUEST_DISC, currentEvent.getJob().getId());
				}
				break;
			
			// job releases disc, returning to processor
			case Event.RELEASE_DISC:
				disc.release();
				nextEvent = new Event(currentEvent.getJob(), currentTime, Event.REQUEST_PROCESSOR);
				nextEvent.insert(eventList);
				System.out.format("%-6d %-4d " + Event.EVENTS[Event.RELEASE_DISC] + "Job releases the disc.", Event.RELEASE_DISC, currentEvent.getJob().getId());
				
				// if there are another job waiting for the disc, requests it
				if(!disc.hasEmptyQueue()) {
					Job jobAux = disc.dequeue();
					nextEvent = new Event(jobAux, currentTime, Event.REQUEST_DISC);
					nextEvent.insert(eventList);
				}
				break;
			
			// job releases memory and processor
			case Event.RELEASE_MEMORY:
				processor.release();
				memory.release(currentEvent.getJob().getSize());
				nextEvent = new Event(currentEvent.getJob(), currentTime, Event.COMPLETE);
				nextEvent.insert(eventList);
				System.out.format("%-6d %-4d " + Event.EVENTS[Event.RELEASE_MEMORY] + "Job releases processor and memory.", Event.RELEASE_MEMORY, currentEvent.getJob().getId());
				
				// if there are another job waiting for the processor, requests it
				if(!processor.hasEmptyQueue()) {
					Job jobAux = processor.dequeue();
					nextEvent = new Event(jobAux, currentTime, Event.REQUEST_PROCESSOR);
					nextEvent.insert(eventList);
				}
				
				// if there are another job waiting for the memory, requests it
				if(!memory.hasEmptyQueue() && memory.thereIsFreeSpace(memory.nextSizeRequest())) {
					Job jobAux = memory.dequeue();
					nextEvent = new Event(jobAux, currentTime, Event.REQUEST_MEMORY);
					nextEvent.insert(eventList);
				}
				break;
			
			// job completes its execution
			case Event.COMPLETE:
				System.out.format("%-6d %-4d " + Event.EVENTS[Event.COMPLETE] + "Job leaves the system.", Event.COMPLETE, currentEvent.getJob().getId());
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
