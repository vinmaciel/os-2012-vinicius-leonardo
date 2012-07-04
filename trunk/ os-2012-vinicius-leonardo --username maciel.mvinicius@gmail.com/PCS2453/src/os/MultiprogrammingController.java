package os;
import java.util.LinkedList;

public class MultiprogrammingController {
	private final int jobsLimit;
	private int concurrentJobs;
	private LinkedList<Job> queue;
	
	public MultiprogrammingController(int limit) {
		this.jobsLimit = limit;
		this.queue = new LinkedList<Job>();
	}
	
	/**
	 * Verifies if another job can execute now.
	 * 
	 * @return true if it can; false otherwise.
	 */
	public boolean canRun() {
		if(this.concurrentJobs < this.jobsLimit)
			return true;
		else
			return false;
	}
	
	/**
	 * Add another job to the execution.
	 */
	public void run() {
		this.concurrentJobs++;
	}
	
	/**
	 * Removes a job from execution.
	 */
	public void finish() {
		this.concurrentJobs--;
	}
	
	/**
	 * Verifies if there are no jobs waiting to run.
	 * 
	 * @return true if there are no jobs enqueued; false if there are.
	 */
	public boolean hasEmptyQueue() {
		return this.queue.isEmpty();
	}
	
	/**
	 * Insert the job in the queue if the number of jobs reached the limit.
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
