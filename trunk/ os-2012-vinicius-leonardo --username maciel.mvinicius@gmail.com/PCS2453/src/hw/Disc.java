package hw;
import java.util.LinkedList;
import os.Job;

public class Disc {
	private boolean status;
	private LinkedList<Job> queue;

	private static boolean FREE = true;
	private static boolean BUSY = false;
	
	public Disc() {
		status = FREE;
		queue = new LinkedList<Job>();
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
	 * Removes the job from the top of the queue, and executes the I/O request.
	 */
	public void dequeue() {
		if(!this.queue.isEmpty()) {
			Job job = this.queue.removeFirst();
			// TODO: issue
		}
	}
}
