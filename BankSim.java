// Author:	Joshua Bowen
// Class:	CSCI 2540
// Program:	4
// Date:	11/14/2016
/* This program is a bank simulation that simulates the transactions between customers and a teller in a bank.
   It uses events to simulate this process, arrival events, and departure events and processes these events
   into a bank queue which is used to simulate a line. It then calculates statistics of these transactions
   can be used for analysis results for employers. */
import java.util.*;
public class BankSim {
	
	static int current_time = 	 0;		// Current time of simulation
	static int totalProcessed =  0;		// Total number of people processed
	static int totalWaitTime = 	 0;		// Total wait time
	static int maxWaitTime = 	 0;		// Max wait time
	static int minTransTime = 	 0;		// Minimum transaction time
	static int maxTransTime = 	 0;		// Maximum transaction time
	static int maxLineLength = 	 0;		// Maximum line length
	static int totalLineLength = 0;		// Total line lengths
	static int queueSize =		 0;
	
	// Calculates the average time of a total totalTime and the number of customers numCustomers.
	
	public static double calculateAvgTime(double totalTime, double numCustomers)
	{
		return totalTime / numCustomers;
	}
	
	// Processes an arrival event event in eventList, and stores information into the queue
	// of customers bankQueue.
	// Also reads next input(if any) of a new arrival event.
	
	public static void processArrival(EventItem event, Queue<EventItem> bankQueue, 
								List<EventItem> eventList, Scanner keyb)
	{
		boolean atFront = bankQueue.peek() == null;	// If new customer will be at front of line.
		eventList.remove(event);	// Removes the event from eventList.
		if (atFront)	// If customer is at front of line, add a departure event.
		{
			int departureTime = current_time + event.getDuration();
			EventItem newEvent = new EventItem(departureTime);
			insertEvent(newEvent, eventList);
		}
		bankQueue.add(event);	// Add new customer to bankQueue.
		queueSize+= 1;
		maxLineLength = Math.max(maxLineLength, queueSize);	// Calculation of max line length.
		if (keyb.hasNext())	// If more input(arrival event) read next arrival event and place in eventList.
		{
			int arrivalTime = keyb.nextInt();
			int durationTime = keyb.nextInt();
			EventItem newEvent = new EventItem(arrivalTime, durationTime);
			insertEvent(newEvent, eventList);
		}
		totalProcessed += 1;	// Calculation of total number of people processed.
		minTransTime = Math.min(minTransTime, event.getDuration());	// Calculation of minimum transaction time.
		maxTransTime = Math.max(maxTransTime, event.getDuration());	// Calculation of maximum transaction time.
	}
	
	// Processes a departure event event in eventList and updates the queue of customers bankQueue.
	
	public static void processDeparture(EventItem event, Queue<EventItem> bankQueue,
								List<EventItem> eventList)
	{
		eventList.remove(event);	// Remove event from eventList.
		bankQueue.remove();		// Remove customer from bankQueue.
		queueSize-= 1;
		if (bankQueue.peek() != null)		// If bankQueue isn't empty create a departure event for first
		{								// customer in line and insert into eventList.
			EventItem firstCustomer = bankQueue.peek();	// First customer in line.
			int departureTime = current_time + firstCustomer.getDuration();	// Departure time for first customer.
			EventItem newEvent = new EventItem(departureTime);	// Departure event for first customer.	
			totalWaitTime += current_time - firstCustomer.getTime();	// Calculation of total wait time.
			maxWaitTime = Math.max(maxWaitTime, current_time - firstCustomer.getTime());	// Calculation of max wait time.
			insertEvent(newEvent, eventList);			}
	}
	
	// Inserts an event event into eventList at the appropriate position based on time of event.
	
	public static void insertEvent(EventItem event, List<EventItem> eventList)
	{
		if (eventList.isEmpty())	// If eventList is empty simply insert event into eventList.
		{
			eventList.add(event);
		}
		else
		{
			for (int i = 0; i < eventList.size(); i++)	// Loop to see if event time is less than an event's in eventList.
			{
				if ((event.compareTo(eventList.get(i)) == -1 || event.compareTo(eventList.get(i)) == 0))
				{
					eventList.add(i, event);
					return;
				}
			}
			eventList.add(event);	// Event time is larger than all events so insert at end.
		}
	}
	
	// Displays various statistics about the bank simulation process.
	
	public static void displayStatistics()
	{
		System.out.println("\nFinal Statistics:");
		System.out.printf("%-39s %d\n","Total number of people processed:", totalProcessed);
		System.out.printf("%-39s %1.1f\n","Average amount of time spent waiting:", calculateAvgTime(totalWaitTime, totalProcessed));
		System.out.printf("%-39s %d\n","Maximum wait time:", maxWaitTime);
		System.out.printf("%-39s %d\n","Minumum transaction time:", minTransTime);
		System.out.printf("%-39s %d\n","Maximum transaction time:", maxTransTime);
		System.out.printf("%-39s %d\n","Maximum line length:", maxLineLength);
		System.out.printf("%-39s %1.1f\n","Average line length:", calculateAvgTime(totalLineLength, current_time));
	}
	
	public static void main(String[] args)
	{
		Queue<EventItem> bank_queue = new LinkedList<EventItem>();	// Bank Queue
		List<EventItem> event_list = new LinkedList<EventItem>();	// Event List
		int arrivalTime, durationTime;	// Arrival time and duration of first event.
		Scanner kb = new Scanner(System.in);	// Scanner for input.
		arrivalTime = kb.nextInt();
		durationTime = kb.nextInt();
		EventItem firstEvent = new EventItem(arrivalTime, durationTime);	// First event to add from input.
		event_list.add(0, firstEvent);	// Add first event to event list.
		minTransTime = firstEvent.getDuration();	// Initial transaction time for minimum transaction time calculation.
		maxTransTime = firstEvent.getDuration();	// Initial transaction time for maximum transaction time calculation.
		System.out.println("Simulation Begins");
		while (!event_list.isEmpty())	// Process events until empty.
		{
			int pastTime = current_time;	// For calculation of average line length.
			EventItem newEvent = event_list.get(0);	// New event to process.
			current_time = newEvent.getTime();	// Updating current time of simulation.
			totalLineLength = totalLineLength + (bank_queue.size() * (current_time - pastTime)); // Calculation of total line lengths for average calculation.
			if (newEvent.isArrival())	// If event to process is an arrival
			{
				processArrival(newEvent, bank_queue, event_list, kb);
				System.out.printf("%-39s %d\n", "Processing an arrival event at time:", current_time);
			}
			else 	// Event is a departure event
			{
				processDeparture(newEvent, bank_queue, event_list);
				System.out.printf("%-39s %d\n", "Processing a departure event at time:", current_time);
			} 
		}
		System.out.println("Simulation Ends");
		displayStatistics();
		kb.close();
	}
}
