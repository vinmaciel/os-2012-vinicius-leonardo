package os;

public class Event {
	private Job job;
	private int arrivalTime;
	
	public Event(Job job, int arrivalTime) {
		this.job = job;
		this.arrivalTime = arrivalTime;
	}
	
	public int getArrivalTime() {
		return this.arrivalTime;
	}
}
