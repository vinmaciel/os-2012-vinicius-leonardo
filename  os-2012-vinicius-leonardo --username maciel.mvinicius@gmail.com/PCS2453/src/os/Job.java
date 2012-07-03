package os;

public class Job {
	private int id;
	private int memorySpace;
	private int processingTime;
	private int ioRequests;
	private int interrequestTime;
	private int recordLength;
	
	private int timeToNextRelease;
	
	public Job(int id, int procTime, int mem, int io, int rl) {
		this.id = id;
		this.processingTime = procTime;
		this.memorySpace = mem;
		this.ioRequests = io;
		
		this.recordLength = rl;
		if(this.ioRequests > 0) {
			this.interrequestTime = this.processingTime/(this.ioRequests +1);
			this.timeToNextRelease = this.interrequestTime;
		}
		else {
			this.interrequestTime = 0;
			this.timeToNextRelease = this.processingTime;
		}
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
	 * Gets time taken until this job issues another I/O operation, has its quantum finished, or end its processing.
	 * 
	 * @return job's time to the next processor release.
	 */
	public int getTimeToNextRelease() {
		return this.timeToNextRelease;
	}
	
	/**
	 * Reduces the I/O requests remaining for this job to complete execution.
	 */
	public void issuedIo() {
		this.ioRequests--;
		this.timeToNextRelease = this.interrequestTime;
	}
	
	/**
	 * Reduces the remaining time to process this job.
	 */
	public void partialProcessed(int time) {
		this.timeToNextRelease -= time;
		this.processingTime -= time;
	}
	
	/**
	 * Removes the remaining time to process this job.
	 */
	public void fullyProcessed() {
		this.timeToNextRelease = 0;
		this.processingTime = 0;
	}
}
