package os;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

public class File {
	private static boolean PRIVATE = true;
	private static boolean PUBLIC = false;
	
	// attributes
	public String name;
	public boolean privacy;
	private int size;
	private ArrayList<Job> owner;
	private LinkedList<Job> queue;
	
	public File (String name, int size) {
		this.privacy = PUBLIC;
		this.name = name;
		this.size = size;
		this.owner = new ArrayList<Job>();
		queue = new LinkedList<Job>();
	}
	
	
	/**
	 * Gets the name of the file
	 * 
	 * @return filename
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Gets privacy level of the file
	 * 
	 * @return true if file is private, false if file is public
	 */
	public boolean isPrivate() {
		return this.privacy;
	}
		
	/**
	 * Gets file size
	 * 
	 * @return file size
	 */
	public int getSize() {
		return this.size;
	}
	
	/**
	 * Verifies if specified job owns the file
	 * 
	 * @param job Possible owner of file
	 * @return true if job is owner, false if job is not owner
	 */
	public boolean isOwner(Job job) {
		if (owner.contains(job))
			return true;
		else return false;
	}
	
	/**
	 * Adds new owner to the file
	 * @param job new owner
	 */
	public void addOwner(Job job) {
		this.owner.add(job);
	}
	
	/**
	 * Gets file based on a file name and the files list
	 * @param name
	 * @param fileList list of existent files in disc
	 * @return File searched. Null if non-existent
	 */
	public static File getFile(String name, ArrayList<File> fileList) {
		ListIterator<File> itr = fileList.listIterator();
	    while (itr.hasNext()) {
	    	File element = itr.next();
	    	if (element.name.equals(name)) {
	    		return element;
	    	}
    	}
	    return null;
	}
	
	
	/**
	 * Verifies if there are no jobs waiting for the processor.
	 * 
	 * @return true if there are no jobs enqueued; false if there are.
	 */
	public boolean hasEmptyQueue() {
		return this.queue.isEmpty();
	}
	
	
	/**
	 * Insert the job in the queue if file is busy.
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
