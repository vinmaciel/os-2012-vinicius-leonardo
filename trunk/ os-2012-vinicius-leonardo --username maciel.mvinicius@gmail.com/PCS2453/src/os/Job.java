package os;

public class Job {
	private int id;
	private int memorySpace;
	private int processingTime;
	private int ioRequests;
	private int interrequestTime;
	private int recordLength;
	
	public Job(int id, int procTime, int mem, int io) {
		this.id = id;
		this.processingTime = procTime;
		this.memorySpace = mem;
		this.ioRequests = io;
		
		this.recordLength = 100; // value guessed...
		if(this.ioRequests > 0)
			this.interrequestTime = this.processingTime/this.ioRequests;
		else
			this.interrequestTime = 0;
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
		return this.processingTime;
	}
	
	/**
	 * Gets this job's quantity of I/O requests.
	 * 
	 * @return job's i/o requests.
	 */
	public int getIoRequests() {
		return this.ioRequests;
	}
	
	/**
	 * Gets time taken to the next I/O issue.
	 * 
	 * @return job's interrequest time.
	 */
	public int getInterrequestTime() {
		return this.interrequestTime;
	}
	
	/**
	 * Gets time taken to process an I/O request.
	 * 
	 * @return job's record length.
	 */
	public int getRecordLength() {
		return this.recordLength;
	}
	
	/**
	 * Reduces the I/O requests remaining for this job to complete execution.
	 */
	public void issuedIo() {
		this.ioRequests--;
	}
	
	/**
	 * Reduces the remaining time to process this job.
	 */
	public void partialProcessed() {
		this.processingTime -= this.interrequestTime;
	}
	
	/**
	 * Removes the remaining time to process this job.
	 */
	public void fullProcessed() {
		this.processingTime = 0;
	}
}
