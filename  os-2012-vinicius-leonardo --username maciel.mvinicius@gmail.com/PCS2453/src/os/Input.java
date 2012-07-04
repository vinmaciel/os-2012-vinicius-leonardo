package os;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

@SuppressWarnings("serial")
class InputReadException extends Exception {
	public InputReadException(String fileName) {
		super("File " + fileName + " with incorrect definition.");
	}
}

public class Input {
	
	private static final int JOB_RECORD_LENGTH = 100;

	/**
	 * Reads the definition file.
	 * 
	 * @param fileName is the file to be read.
	 * @param initialTime marks the current time at the beginning of the execution.
	 * @param endTime marks the current time at the end of the execution;
	 * @param jobList stores all the jobs the computer can run.
	 * @param eventList stores the events described on the file.
	 * @throws InputReadException if the definition input file doesn't follow the model.
	 */
	public static void read(String fileName, int[] timing, LinkedList<Job> jobList,
			LinkedList<Event> eventList, ArrayList<File> filesList) throws InputReadException {
		try {
			// open file
			FileInputStream inStream = new FileInputStream(fileName);
			
			// get buffer from opened file
			DataInputStream in = new DataInputStream (inStream);
			BufferedReader reader = new BufferedReader (new InputStreamReader(in));
			String line;
			
			// get the first comment line
			line = reader.readLine();
			
			// get initial and end time
			line = reader.readLine();
			timing[0] = Integer.parseInt(line);
			line = reader.readLine();
			timing[1] = Integer.parseInt(line);
			
			// get a blank line
			line = reader.readLine();
			
			// fill files list
			fillFiles(filesList);
			
			// get the jobs definition
			line = reader.readLine();
			while(!line.isEmpty()) {	// while not EOF
				if(line.charAt(0) != ';') {	//if not a comment
					// get job attributes
					String[] definition = line.split(" ");
					int jobId = Integer.parseInt(definition[0]);
					int processingTime = Integer.parseInt(definition[1]);
					int segmentsNumber = Integer.parseInt(definition[2]);
					
					int i;
					int[] segmentSizes = new int[segmentsNumber];
					for(i = 0; i < segmentsNumber; i++)
						segmentSizes[i] = Integer.parseInt(definition[3+i]);
			
					
					int ioRequests = Integer.parseInt(definition[3+i]);
					
					
					
					int numberOfFiles = Integer.parseInt(definition[4+i]);
					
					ArrayList<String> filesUsed = new ArrayList<String>();
					for (int j = 0; j < numberOfFiles; j++) {
						filesUsed.add(definition[5+i+j]);
					}
					

					// create job and stores it onto the list


					Job job = new Job(jobId, processingTime, ioRequests, JOB_RECORD_LENGTH, segmentSizes, filesUsed);
					jobList.add(job);
					
					ListIterator<String> itr = filesUsed.listIterator();
					while (itr.hasNext()) {
				    	String element = itr.next();
				    	File.getFile(element, filesList).addOwner(job);
			    	}
					
					System.out.println("Id: " + jobId + "\tProcessing time: " + processingTime + 
							"\tNumber of segments: " + segmentsNumber + "\tI/O requests: " + ioRequests +
							" \tNumber of files: " + numberOfFiles); 
					
					if (numberOfFiles != 0) {
						System.out.print("Files: ");
						ListIterator<String> itr2 = filesUsed.listIterator();
					    while (itr2.hasNext()) {
					    	String element = itr2.next();
					    	System.out.print(element + " ");
					    }
					    System.out.println();
					}
					System.out.println();
				}
				
				line = reader.readLine();
			}
			System.out.println("");
			
			
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
					Job job = findJob(jobList, jobId);
					
					if(job != null) {
						// if found, create the event and stores it onto the list in arrival time order
						Event event = new Event(job, arrivalTime, Event.ARRIVAL);
						event.insert(eventList);

						System.out.println("Job: " + jobId + "\tArrival time: " + arrivalTime);
					}
					else
						System.out.println("Invalid job with id: " + jobId + ".");
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
	
	/**
	 * Searches if the job was already defined.
	 * 
	 * @param jobList contains all jobs created.
	 * @param jobId identifies the job being searched.
	 * @return the job with jobId; or null if not found.
	 */
	private static Job findJob(LinkedList<Job> jobList, int jobId) {
		Job job = null;
		ListIterator<Job> jobIterator = jobList.listIterator();
		
		while(jobIterator.hasNext()) {
			job = jobIterator.next();
			if(jobId == job.getId())
				return job;
		}
		
		return job;
	}
	
	private static void fillFiles(ArrayList<File> filesList) {

		filesList.add(new File("calc", 32));
		filesList.add(new File("pad", 16));
		filesList.add(new File("minesweeper", 20));
		filesList.add(new File("salt", 5));
		filesList.add(new File("pepper", 10));
		filesList.add(new File("tea", 48));
		filesList.add(new File("coffee", 32));
		
	}
}
