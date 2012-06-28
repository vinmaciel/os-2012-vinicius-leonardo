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
	
	/**
	 * Gets this job's numeric identification.
	 * 
	 * @return job's id.
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Gets this job's memory space required for execution.
	 * 
	 * @return job's memory space required.
	 */
	public int getSize() {
		return this.memorySpace;
	}
	
	/**
	 * Gets this job's total processing time.
	 * 
	 * @return job's processing time.
	 */
	public int getProcessingTime() {
		return this.processigTime;
	}
	
	/**
	 * Gets this job's quantity of I/O requests.
	 * 
	 * @return job's i/o requests.
	 */
	public int getIoRequests() {
		return this.ioRequests;
	}
}
