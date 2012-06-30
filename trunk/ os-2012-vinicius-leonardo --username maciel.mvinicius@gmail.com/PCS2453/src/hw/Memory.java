package hw;
import java.util.LinkedList;
import os.Job;

public class Memory {
	private final int totalSize;
	private int occupiedSize;
	private final int relocatingTime;
	private LinkedList<Job> queue;

	public Memory(int size, int rt) {
		this.totalSize = size;
		this.occupiedSize = 0;
		this.relocatingTime = rt;
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
	 * Gets the time necessary for the this memory to allocate memory to the job.
	 * 
	 * @return memory's relocating time.
	 */
	public int getRelocatingTime() {
		return this.relocatingTime;
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
	 * Verifies if there are no jobs waiting for the memory.
	 * 
	 * @return true if there are no jobs enqueued; false if there are.
	 */
	public boolean hasEmptyQueue() {
		return this.queue.isEmpty();
	}
	
	/**
	 * Gets the memory space required for the first job of the queue.
	 * 
	 * @return next dequeued job's memory space required.
	 */
	public int nextSizeRequest() {
		return this.queue.getFirst().getSize();
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
	 * Removes the job from the top of the queue.
	 */
	public Job dequeue() {
		if(!this.queue.isEmpty()) {
			return this.queue.removeFirst();
		}
		else
			return null;
	}
}
