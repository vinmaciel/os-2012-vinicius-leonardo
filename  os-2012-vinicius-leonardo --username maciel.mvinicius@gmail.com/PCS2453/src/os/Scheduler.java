package os;

import hw.*;
import user.Parameter;
import java.util.LinkedList;
import user.Parameter;
@SuppressWarnings("unused")

public class Scheduler {
	
	private int initialTime;
	private int finalTime;
	
	private MultiprogrammingController multiprogrammingController;
	
	private Memory memory;
	private Processor processor;
	private Disc disc;
	
	private LinkedList<Event> eventList;
	private LinkedList<Job> jobTable;
		
	public Scheduler() {
		int[] timing = new int[2];
		
		this.eventList = new LinkedList<Event>();
		this.jobTable = new LinkedList<Job>();
		try {
			Input.read("input.txt", timing, jobTable, eventList);
		}
		catch(InputReadException e) {
			e.printStackTrace();
		}
		this.initialTime = timing[0];
		this.finalTime = timing[1];
		
		this.multiprogrammingController = new MultiprogrammingController(Parameter.MULTIPROGRAMMING_LIMIT);
		
		this.memory = new Memory(Parameter.MEMORY_SIZE, Parameter.MEMORY_RELOCATING_TIME);
		this.processor = new Processor(Parameter.PROCESSOR_QUANTUM);
		this.disc = new Disc(Parameter.DISC_POSITIONING_TIME, Parameter.DISC_LATENCY_TIME, Parameter.DISC_TRANSFER_RATE);
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
			if(Parameter.MEMORY_PRINT_SEGMENTS) memory.printSegmentMap();
			Job currentJob = currentEvent.getJob();
			currentTime = currentEvent.getTime();
			System.out.print(currentTime);
			
			switch(currentEvent.getType()) {
			// job arrives
			case Event.ARRIVAL:
				if(multiprogrammingController.canRun()) {
					multiprogrammingController.run();
					
					nextEvent = new Event(currentJob, currentTime, Event.REQUEST_MEMORY);
					nextEvent.insert(eventList);
					System.out.println("\t" + Event.ARRIVAL + "\t" + currentJob.getId() + "\t" + Event.EVENTS[Event.ARRIVAL] + "\tJob arrived at the system.");
				}
				else {
					multiprogrammingController.enqueue(currentJob);
					System.out.println("\t" + Event.ARRIVAL + "\t" + currentJob.getId() + "\t" + Event.EVENTS[Event.ARRIVAL] + "\tJob entered multiprogramming queue.");
				}
				break;
				
			// job requests memory allocation
			case Event.REQUEST_MEMORY:
				if(memory.allocate(currentJob.getSegmentList())) {
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
					
					if(currentJob.getTimeToNextRelease() <= processor.getQuantum()) {
						// processes until the release (to IO request or completion)
						if(currentJob.getIoRequests() > 0) {
							nextEvent = new Event(currentJob, currentTime + currentJob.getTimeToNextRelease(), Event.ISSUE_IO);
							currentJob.partialProcessed(currentJob.getTimeToNextRelease());
						}
						else {
							nextEvent = new Event(currentJob, currentTime + currentJob.getProcessingTime(), Event.COMPLETION);
							currentJob.fullyProcessed();
						}
					}
					else {
						// processes until the end of the time slice
						nextEvent = new Event(currentJob, currentTime + processor.getQuantum(), Event.TIME_OUT);
						currentJob.partialProcessed(processor.getQuantum());
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
					nextEvent = new Event(currentJob, currentTime + disc.getProcessingTime(currentJob.getRecordLength()), Event.RELEASE_IO);
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
				System.out.println("\t" + Event.RELEASE_IO + "\t" + currentJob.getId() + "\t" + Event.EVENTS[Event.RELEASE_IO] + "\tJob released the disc.");
				
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
				memory.release(currentJob.getSegmentList());
				multiprogrammingController.finish();
				System.out.println("\t" + Event.COMPLETION + "\t" + currentJob.getId() + "\t" + Event.EVENTS[Event.COMPLETION] + "\tJob released processor and memory.");
				
				// if there is another job waiting for the processor, requests it
				if(!processor.hasEmptyQueue()) {
					Job jobAux = processor.dequeue();
					nextEvent = new Event(jobAux, currentTime + Processor.OVERHEAD_TIME, Event.REQUEST_PROCESSOR);
					nextEvent.insert(eventList);
				}
				
				// if there is another job waiting for the memory, requests it
				if(!memory.hasEmptyQueue() && memory.allocate(memory.nextSegmentsRequest())) {
					memory.release(memory.nextSegmentsRequest());
					Job jobAux = memory.dequeue();
					nextEvent = new Event(jobAux, currentTime, Event.REQUEST_MEMORY);
					nextEvent.insert(eventList);
				}
				
				// if there is another job waiting for multiprogramming, executes it
				if(!multiprogrammingController.hasEmptyQueue()) {
					Job jobAux = multiprogrammingController.dequeue();
					nextEvent = new Event(jobAux, currentTime, Event.REQUEST_MEMORY);
					nextEvent.insert(eventList);
				}
				break;
			
			// job completes its time slice, releasing the processor and requesting it at the end of the queue
			case Event.TIME_OUT:
				processor.release();
				// if there are another job waiting for the processor, requests it
				if(!processor.hasEmptyQueue()) {
					Job jobAux = processor.dequeue();
					nextEvent = new Event(jobAux, currentTime + Processor.OVERHEAD_TIME, Event.REQUEST_PROCESSOR);
					nextEvent.insert(eventList);
				}
				
				nextEvent = new Event(currentJob, currentTime, Event.REQUEST_PROCESSOR);
				nextEvent.insert(eventList);
				System.out.println("\t" + Event.TIME_OUT + "\t" + currentJob.getId() + "\t" + Event.EVENTS[Event.TIME_OUT] + "\tJob released processor.");
				break;
				
			//others...
			default:
				System.out.println("\t" + Event.INVALID + "\t" + currentJob.getId() + "\t" + Event.EVENTS[Event.INVALID] + "\tInvalid event.");
			}
			
			// get the following event
			currentEvent = Event.getNew(eventList);
		}

		if(currentEvent == null)
			System.out.println(currentTime + "\t\t\t\t\t\t\tNo more jobs to simulate.");
		else
			System.out.println(this.finalTime + "\t\t\t\t\t\t\tEnd of simulation.");
	}
}
