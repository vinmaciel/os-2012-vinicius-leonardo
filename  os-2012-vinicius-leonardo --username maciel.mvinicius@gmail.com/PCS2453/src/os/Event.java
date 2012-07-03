package os;

import java.util.LinkedList;
import java.util.ListIterator;

public class Event {
	// constants
	static final String[] EVENTS = {"XXXXXXXXXXXXXXXXXXXXXXXXXXXXX", "Arrival                      ", "Request Memory               ", "Request Processor            ",
		"Release Processor (Issue I/O)", "Request I/O                  ", "Complete I/O                 ", "Completion                   ",
		"Time-out                     "};
	static final int INVALID = 0;
	static final int ARRIVAL = 1;
	static final int REQUEST_MEMORY = 2;
	static final int REQUEST_PROCESSOR = 3;
	static final int ISSUE_IO = 4;
	static final int REQUEST_IO = 5;
	static final int RELEASE_IO = 6;
	static final int COMPLETION = 7;
	static final int TIME_OUT = 8;
	
	// attributes
	private Job job;
	private int time;
	private int type;
	
	public Event(Job job, int time, int type) {
		this.job = job;
		this.time = time;
		this.type = type;
	}

	/**
	 * Gets this event's job.
	 * 
	 * @return event's job.
	 */
	public Job getJob() {
		return this.job;
	}
	
	/**
	 * Gets this event's arrival time.
	 * 
	 * @return event's arrival time.
	 */
	public int getTime() {
		return this.time;
	}
	
	/**
	 * Gets this event's type.
	 * 
	 * @return event's type.
	 */
	public int getType() {
		return this.type;
	}
	
	/**
	 * Gets this event's type name, formated to fix program output.
	 * 
	 * @return event's type name.
	 */
	public String getType_toString() {
		return Event.EVENTS[this.getType()];
	}
		
	/**
	 * Add this event into the list, in order of arrival. 
	 * 
	 * @param eventList stores all events.
	 */
	void insert(LinkedList<Event> eventList) {
		ListIterator<Event> eventIterator = eventList.listIterator();

		boolean earlier = false;
		while(eventIterator.hasNext() && !earlier) {
			Event event = eventIterator.next();
			if(this.time < event.getTime()) {
				eventIterator.previous();
				earlier = true;
			}
		}
		eventIterator.add(this);
	}
	
	/**
	 * Gets the first event to happen.
	 * 
	 * @param eventList stores all events.
	 * @return the next event to happen.
	 */
	static Event getNew(LinkedList<Event> eventList) {
		if(!eventList.isEmpty())
			return eventList.removeFirst();
		else
			return null;
	}
}
