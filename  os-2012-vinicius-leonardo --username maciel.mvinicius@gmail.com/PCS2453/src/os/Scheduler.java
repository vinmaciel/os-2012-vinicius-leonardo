package os;

import hw.*;
import java.util.LinkedList;

public class Scheduler {
	private static final int MEMORY_RELOCATING_TIME = 20;
	private static final int MEMORY_SIZE = 80;
	private static final int DISC_POSITIONING_TIME = 5;
	private static final int DISC_LATENCY_TIME = 5;
	private static final int DISC_TRANSFER_RATE = 40;
	
	private int initialTime;
	private int finalTime;
	
	private Memory memory;
	private Processor processor;
	private Disc disc;
	
	private LinkedList<Event> eventList;
	private LinkedList<Job> jobTable;
		
	public Scheduler() {
		int[] timing = new int[2];
		
		eventList = new LinkedList<Event>();
		jobTable = new LinkedList<Job>();
		try {
			Input.read("input.txt", timing, jobTable, eventList);
		}
		catch(InputReadException e) {
			e.printStackTrace();
		}
		initialTime = timing[0];
		finalTime = timing[1];
		
		memory = new Memory(MEMORY_SIZE, MEMORY_RELOCATING_TIME);
		processor = new Processor();
		disc = new Disc(DISC_POSITIONING_TIME, DISC_LATENCY_TIME, DISC_TRANSFER_RATE);
	}
	
	/**
	 * Executes all the events stored into the eventList.
	 */
	public void run() {
		// starts timing
		int currentTime = this.initialTime;
		
		// print table header
		System.out.println("\n\nExecution:");
		System.out.println("Time\tEvent\tJob\tAction                       \tResults");
		System.out.println(currentTime + "\t\t\t\t\t\t\tStarting...");
		
		// get the first event arriving
		Event currentEvent = Event.getNew(eventList);
		Event nextEvent = null;
		
		// while there are more events, and current time didn't surpass the end of times
		while(currentEvent != null && currentTime <= this.finalTime) {
			System.out.print(currentTime);
			Job currentJob = currentEvent.getJob();
			currentTime = currentEvent.getTime();
			
			switch(currentEvent.getType()) {
			// job arrives
			case Event.ARRIVAL:
				nextEvent = new Event(currentJob, currentTime, Event.REQUEST_MEMORY);
				nextEvent.insert(eventList);
				System.out.println("\t" + Event.ARRIVAL + "\t" + currentJob.getId() + "\t" + Event.EVENTS[Event.ARRIVAL] + "\tJob arrived at the system.");
				break;
				
			// job requests memory allocation
			case Event.REQUEST_MEMORY:
				if(memory.thereIsFreeSpace(currentJob.getSize())) {
					memory.allocate(currentJob.getSize());
					nextEvent = new Event(currentJob, currentTime + memory.getRelocatingTime(), Event.REQUEST_PROCESSOR);
					nextEvent.insert(eventList);
					System.out.println("\t" + Event.REQUEST_MEMORY + "\t" + currentJob.getId() + "\t" + Event.EVENTS[Event.REQUEST_MEMORY] + "\tMemory allocated to the job.");
				}
				else {
					memory.enqueue(currentJob);
					System.out.println("\t" + Event.REQUEST_MEMORY + "\t" + currentJob.getId() + "\t" + Event.EVENTS[Event.REQUEST_MEMORY] + "\tJob entered memory queue.");
				}
				break;
			
			// job requests processor execution
			case Event.REQUEST_PROCESSOR:
				if(processor.isFree()) {
					processor.assign();
					if(currentJob.getIoRequests() > 0) {
						nextEvent = new Event(currentJob, currentTime + disc.getProcessingTime(currentJob.getRecordLength()), Event.ISSUE_IO);
						currentJob.partialProcessed();
					}
					else {
						nextEvent = new Event(currentJob, currentTime + currentJob.getProcessingTime(), Event.COMPLETION);
						currentJob.fullProcessed();
					}
					nextEvent.insert(eventList);
					System.out.println("\t" + Event.REQUEST_PROCESSOR + "\t" + currentJob.getId() + "\t" + Event.EVENTS[Event.REQUEST_PROCESSOR] + "\tProcessor assigned to the job.");
				}
				else {
					processor.enqueue(currentJob);
					System.out.println("\t" + Event.REQUEST_PROCESSOR + "\t" + currentJob.getId() + "\t" + Event.EVENTS[Event.REQUEST_PROCESSOR] + "\tJob entered processor queue.");
				}
				break;
			
			// job releases the processor, and requests I/O operation
			case Event.ISSUE_IO:
				processor.release();
				nextEvent = new Event(currentJob, currentTime, Event.REQUEST_IO);
				nextEvent.insert(eventList);
				System.out.println("\t" + Event.ISSUE_IO + "\t" + currentJob.getId() + "\t" + Event.EVENTS[Event.ISSUE_IO] + "\tJob released the processor and issued the disc.");

				// if there are another job waiting for the processor, requests it
				if(!processor.hasEmptyQueue()) {
					Job jobAux = processor.dequeue();
					nextEvent = new Event(jobAux, currentTime + Processor.OVERHEAD_TIME, Event.REQUEST_PROCESSOR);
					nextEvent.insert(eventList);
				}
				break;
			
			// job releases disc, returning to processor
			case Event.REQUEST_IO:
				if(disc.isFree()) {
					disc.assign();
					currentJob.issuedIo();
					nextEvent = new Event(currentJob, currentTime + currentJob.getRecordLength(), Event.RELEASE_IO);
					nextEvent.insert(eventList);
					System.out.println("\t" + Event.REQUEST_IO + "\t" + currentJob.getId() + "\t" + Event.EVENTS[Event.REQUEST_IO] + "\tDisc assigned to the job.");
				}
				else {
					disc.enqueue(currentJob);
					System.out.println("\t" + Event.REQUEST_IO + "\t" + currentJob.getId() + "\t" + Event.EVENTS[Event.REQUEST_IO] + "\tJob entered disc queue.");
				}
				break;
			
			// job releases memory and processor
			case Event.RELEASE_IO:
				disc.release();
				nextEvent = new Event(currentJob, currentTime, Event.REQUEST_PROCESSOR);
				nextEvent.insert(eventList);
				System.out.println("\t" + Event.RELEASE_IO + "\t" + currentJob.getId() + "\t" + Event.EVENTS[Event.RELEASE_IO] + "\tJob releases the disc.");
				
				// if there are another job waiting for the disc, requests it
				if(!disc.hasEmptyQueue()) {
					Job jobAux = disc.dequeue();
					nextEvent = new Event(jobAux, currentTime, Event.REQUEST_IO);
					nextEvent.insert(eventList);
				}
				break;
			
			// job completes its execution, releasing memory and the processor
			case Event.COMPLETION:
				processor.release();
				memory.release(currentJob.getSize());
				System.out.println("\t" + Event.COMPLETION + "\t" + currentJob.getId() + "\t" + Event.EVENTS[Event.COMPLETION] + "\tJob releases processor and memory.");
				
				// if there are another job waiting for the processor, requests it
				if(!processor.hasEmptyQueue()) {
					Job jobAux = processor.dequeue();
					nextEvent = new Event(jobAux, currentTime + Processor.OVERHEAD_TIME, Event.REQUEST_PROCESSOR);
					nextEvent.insert(eventList);
				}
				
				// if there are another job waiting for the memory, requests it
				if(!memory.hasEmptyQueue() && memory.thereIsFreeSpace(memory.nextSizeRequest())) {
					Job jobAux = memory.dequeue();
					nextEvent = new Event(jobAux, currentTime + memory.getRelocatingTime(), Event.REQUEST_MEMORY);
					nextEvent.insert(eventList);
				}
				break;
			
			// others...
			default:
				System.out.println("\t" + Event.INVALID + "\t" + currentJob.getId() + "\t" + Event.EVENTS[Event.INVALID] + "\tInvalid event.");
			}
			
			// get the following event
			currentEvent = Event.getNew(eventList);
		}

		System.out.println(this.finalTime + "\t\t\t\t\t\t\tFinishing...");
	}
}
