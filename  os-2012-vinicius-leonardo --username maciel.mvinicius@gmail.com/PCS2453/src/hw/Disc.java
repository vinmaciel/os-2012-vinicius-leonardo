package hw;
import java.util.LinkedList;
import os.Job;

public class Disc {
	public static boolean FREE = true;
	public static boolean BUSY = false;
	
	private boolean status;
	private LinkedList<Job> queue;

	public Disc() {
		status = FREE;
		queue = new LinkedList<Job>();
	}
	
	/**
	 * Verifies disc's status.
	 * 
	 * @return true if FREE; false if BUSY.
	 */
	public boolean getStatus() {
		return this.status;
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
