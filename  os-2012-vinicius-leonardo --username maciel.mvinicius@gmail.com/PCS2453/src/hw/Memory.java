package hw;
import java.util.LinkedList;
import os.Job;

public class Memory {
	private int totalSize;
	private int occupiedSize;
	private LinkedList<Job> queue;

	Memory(int size) {
		totalSize = size;
		queue = new LinkedList<Job>();
	}
	
	/**
	 * Verifies if there is enough space for memory allocation.
	 * 
	 * @param size to be allocated.
	 * @return true, if there is enough space; false, otherwise.
	 */
	public boolean thereIsFreeSpace(int size){
		if(size <= this.totalSize - this.occupiedSize)
			return true;
		else
			return false;
	}
	
	/**
	 * Allocates a part of the memory for the job.
	 * 
	 * @param size to be allocated.
	 */
	public void allocate(int size) {
		this.occupiedSize += size;
	}
	
	/**
	 * Releases a part of the memory from the job.
	 * 
	 * @param size to be release from memory.
	 */
	public void release(int size) {
		this.occupiedSize -= size;
	}
	
	/**
	 * Insert the job in the queue if memory is full.
	 * 
	 * @param job to be inserted.
	 */
	public void enqueue(Job job) {
		this.queue.addLast(job);
	}
	
	/**
	 * Removes the job from the top of the queue, and allocates memory for it.
	 */
	public void dequeue() {
		if(!this.queue.isEmpty()) {
			Job job = this.queue.removeFirst();
			// TODO: allocation
		}
	}
}
