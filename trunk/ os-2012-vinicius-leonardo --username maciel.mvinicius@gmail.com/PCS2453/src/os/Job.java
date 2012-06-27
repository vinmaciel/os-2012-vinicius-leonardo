package os;

public class Job {
	private int id;
	private int memorySpace;
	private int processigTime;
	private int ioRequests;
	
	public Job(int id, int procTime, int mem, int io) {
		this.id = id;
		this.processigTime = procTime;
		this.memorySpace = mem;
		this.ioRequests = io;
	}
	
	public int getId() {
		return this.id;
	}
}
