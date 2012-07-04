package os;

import java.util.LinkedList;
import java.util.ListIterator;

public class Segment {
	private static final boolean REAL = true;
	private static final boolean VIRTUAL = false;
	
	private boolean status;
	private int memoryPosition;
	private int size;
	
	public Segment(int offset) {
		this.status = VIRTUAL;
		this.memoryPosition = 0;
		this.size = offset;
	}
	
	/**
	 * Verifies if the segment is allocated in the memory.
	 * 
	 * @return true if it is; false otherwise.
	 */
	public boolean isAllocated() {
		return this.status;
	}
	
	/**
	 * Gets the initial position in the memory for this segment.
	 * 
	 * @return segment's position.
	 */
	public int getPosition() {
		return this.memoryPosition;
	}
	
	/**
	 * Gets the size of this segment.
	 * 
	 * @return segment's size.
	 */
	public int getSize() {
		return this.size;
	}
	
	/**
	 * Verifies if this segment is equal to that one.
	 * 
	 * @param that segment to be compared to.
	 * @return true if they are equal; false if that is null or they differ their parameters.
	 */
	public boolean equals(Segment that) {
		if(that == null) 
			return false;
		else if(this.memoryPosition == that.memoryPosition && this.size == that.size)
			return true;
		else
			return false;
	}
	
	/**
	 * Add this segment into the list, in order of address. 
	 * 
	 * @param segmentList stores all used segments.
	 */
	public void insert(LinkedList<Segment> segmentList, int position) {
		ListIterator<Segment> segmentIterator = segmentList.listIterator();

		this.status = REAL;
		this.memoryPosition = position;
		
		boolean earlier = false;
		while(segmentIterator.hasNext() && !earlier) {
			Segment segment = segmentIterator.next();
			if(this.memoryPosition < segment.getPosition()) {
				segmentIterator.previous();
				earlier = true;
			}
		}
		segmentIterator.add(this);
	}
	
	/**
	 * Finds and removes this segment from the list.
	 * 
	 * @param segmentList stores all used segments.
	 */
	public void remove(LinkedList<Segment> segmentList) {
		segmentList.remove(this);
		
		this.status = VIRTUAL;
		this.memoryPosition = 0;
	}
}
