package hw;
import java.util.LinkedList;
import java.util.ListIterator;
import os.Job;
import os.Segment;

public class Memory {
	private final int totalSize;
	private final int relocatingTime;
	private LinkedList<Job> queue;
	private LinkedList<Segment> segmentMap;

	public Memory(int size, int rt) {
		this.totalSize = size;
		this.relocatingTime = rt;
		this.queue = new LinkedList<Job>();
		this.segmentMap = new LinkedList<Segment>();
	}
	
	/**
	 * Searches for a point to create a new segment.
	 * 
	 * @param size to be allocated.
	 * @return the position of free space; -1 if not found.
	 */
	public int getFreeSpacePosition(int offset, int size) {
		ListIterator<Segment> segmentIterator = segmentMap.listIterator();
		Segment segment = null;
		int pos = offset;
		
		// if there is no saved segment
		if(!segmentIterator.hasNext())
			return offset;
		
		// if there is space between other segments
		while(segmentIterator.hasNext()) {
			segment = segmentIterator.next();

			if(pos+size <= segment.getPosition())
				return pos;
			
			if(segment.getPosition()+segment.getSize() > pos)
				pos = segment.getPosition()+segment.getSize();
		}
		
		// if there is space at the end of the memory
		if(pos+size < this.totalSize)
			return pos;
		else
			return -1;
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
	 * @param segment to be allocated.
	 * @return true if allocated.
	 */
	public boolean allocate(Segment segment) {
		int position = getFreeSpacePosition(0, segment.getSize());
		
		if(position == -1)
			return false;
		else {
			segment.insert(segmentMap, position);
			return true;
		}
	}
	
	/**
	 * Allocates a number of segments on this memory for the job.
	 * 
	 * @param segments to be allocated.
	 * @return true if all segments were allocated.
	 */
	public boolean allocate(Segment[] segments) {
		int[] position = new int[segments.length];
		
		for(int i = 0; i < segments.length; i++) {
			if(i > 0)
				position[i] = getFreeSpacePosition(position[i-1]+segments[i-1].getSize(), segments[i].getSize());
			else
				position[i] = getFreeSpacePosition(0, segments[i].getSize());
			
			if(position[i] == -1)
				return false;
		}

		for(int i = 0; i < segments.length; i++) {
			segments[i].insert(segmentMap, position[i]);
		}
		return true;
	}
	
	/**
	 * Releases a part of the memory from the job.
	 * 
	 * @param segments stores all segment got from the job.
	 */
	public void release(Segment[] segments) {
		for(int i = 0; i < segments.length; i++)
			segmentMap.remove(segments[i]);
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
	 * Gets the segments that describes the memory space required for the first job of the queue.
	 * 
	 * @return the segment list of the next job.
	 */
	public Segment[] nextSegmentsRequest() {
		return this.queue.getFirst().getSegmentList();
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
	
	public void printSegmentMap() {
		ListIterator<Segment> iterator = this.segmentMap.listIterator();
		
		while(iterator.hasNext()) {
			Segment segment = iterator.next();
			System.out.println("Position: " + segment.getPosition() + "\tSize: " + segment.getSize());
		}
	}
}
