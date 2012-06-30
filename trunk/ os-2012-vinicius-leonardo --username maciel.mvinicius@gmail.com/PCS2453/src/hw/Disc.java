package hw;
import java.util.LinkedList;
import os.Job;

public class Disc {
	private static boolean FREE = true;
	private static boolean BUSY = false;
	
	private boolean status;
	private final int positioningTime;
	private final int latencyTime;
	private final int transferRatio;
	private LinkedList<Job> queue;

	public Disc(int pt, int lt, int transferRatio) {
		this.status = FREE;
		this.positioningTime = pt;
		this.latencyTime = lt;
		this.transferRatio = transferRatio;
		queue = new LinkedList<Job>();
	}
	
	/**
	 * Verifies disc's status.
	 * 
	 * @return true if FREE; false if BUSY.
	 */
	public boolean isFree() {
		return this.status;
	}
	
	/**
	 * Gets the time taken to this disc to issue a I/O operation.
	 * 
	 * @param recordSize to be processed.
	 * @return disc's processing time.
	 */
	public int getProcessingTime(int recordSize) {
		return (this.positioningTime + this.latencyTime + this.transferRatio*recordSize);
	}
	
	/**
	 * Processes an I/O request, busying it.
	 */
	public void assign() {
		this.status = BUSY;
	}
	
	/**
	 * Finishes the I/O request, getting the disc free.
	 */
	public void release() {
		this.status = FREE;
	}
	
	/**
	 * Verifies if there are no jobs waiting for the disc.
	 * 
	 * @return true if there are no jobs enqueued; false if there are.
	 */
	public boolean hasEmptyQueue() {
		return this.queue.isEmpty();
	}
	
	/**
	 * Insert the job in the queue if disc is busy.
	 * 
	 * @param job to be inserted.
	 */
	public void enqueue(Job job) {
		this.queue.addLast(job);
	}
	
	/**
	 * Removes the job from the top of the queue.
	 */
	public Job dequeue() {
		if(!this.queue.isEmpty())
			return this.queue.removeFirst();
		else
			return null;
	}
}
