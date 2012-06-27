package os;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.ListIterator;

class InputReadException extends Exception {
	public InputReadException(String fileName) {
		super("File " + fileName + " with incorrect definition.");
	}
}

public class Input {

	/**
	 * Reads the definition file.
	 * 
	 * @param fileName is the file to be read.
	 * @param initialTime marks the current time at the beginning of the execution.
	 * @param endTime marks the current time at the end of the execution;
	 * @param eventList stores the events described on the file.
	 * @throws InputReadException if the definition input file doesn't follow the model.
	 */
	public static void read(String fileName, int initialTime, int endTime, LinkedList<Event> eventList,
			LinkedList<Job> jobList) throws InputReadException {
		try {
			// open file
			FileInputStream inStream = new FileInputStream(fileName);
			
			// get buffer from opened file
			DataInputStream in = new DataInputStream (inStream);
			BufferedReader reader = new BufferedReader (new InputStreamReader(in));
			String line;
			
			// get initial and end time
			line = reader.readLine();
			initialTime = Integer.parseInt(line);
			line = reader.readLine();
			endTime = Integer.parseInt(line);
			
			// get a blank line
			line = reader.readLine();
			
			// get the jobs definition
			line = reader.readLine();
			while(!line.isEmpty()) {	// while not EOF
				if(line.charAt(0) != ';') {	//if not a comment
					// get job attributes
					String[] definition = line.split(" ");
					int jobId = Integer.parseInt(definition[0]);
					int processingTime = Integer.parseInt(definition[1]);
					int memorySize = Integer.parseInt(definition[2]);
					int ioRequests = Integer.parseInt(definition[3]);
					
					// create job and stores it onto the list
					Job job = new Job(jobId, processingTime, memorySize, ioRequests);
					jobList.add(job);
					
					System.out.println("Id = " + jobId + "  Processing time: = " + processingTime + 
							" Memory required = " + memorySize + " I/O requests = " + ioRequests); 
				}
				
				line = reader.readLine();
			}
			
			// get a blank line
			line = reader.readLine();
			
			// get jobs arrival time
			line = reader.readLine();
			while(!line.isEmpty()) {	// while not EOF
				if(line.charAt(0) != ';') {	//if not a comment
					// get arrival attributes
					String[] arrival = line.split(" ");
					int jobId = Integer.parseInt(arrival[0]);
					int arrivalTime = Integer.parseInt(arrival[1]);
					
					// search for the job
					Job job = null;
					ListIterator<Job> jobIterator = jobList.listIterator();
					boolean found = false;
					while(jobIterator.hasNext() && !found) {
						job = jobIterator.next();
						if(jobId == job.getId())
							found = true;
					}
					if(found) {
						// if found, create the event and stores it onto the list in arrival time order
						boolean earlier = false;
						Event event = new Event(job, arrivalTime);
						ListIterator<Event> eventIterator = eventList.listIterator();
						while(eventIterator.hasNext() && !earlier) {
							Event eventAux = eventIterator.next();
							if(arrivalTime < eventAux.getArrivalTime()) {
								eventAux = eventIterator.previous();
								earlier = true;
							}
						}
						eventIterator.add(event);
					}
					else
						System.out.println("Invalid job with id " + jobId + ".");
				}
				
				line = reader.readLine();
			}
			
			// close file
			in.close();
			inStream.close();
		}
		catch(Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}
}
