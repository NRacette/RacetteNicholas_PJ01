import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description: Runs the grocery store simulation. This class is a thread that creates customers
 * at random intervals and adds them to the shortest cashier line.
 * In its constructor, it creates and starts an array of 5 Cashier threads.
 * It also tracks simulation customer creation and serving.
 *
 * @author Nicholas Racette
 * @contact: Nick.Racette@century.edu
 * @since: 11/16/2025
 *
 * Course: CSCI 2082-70
 * Institution: Century College
 * Instructor: Mathew Nyamgawa
 */
public class GroceryStore extends Thread {

    // PROPERTIES
    public final int NUM_CASHIERS = 5;
    private Cashier[] cashiers;
    private Random rand;
    // Customer arrival time range
    private final int MIN_ARRIVAL_MS = 500;  // 0.5 seconds
    private final int MAX_ARRIVAL_MS = 1000; // 1.0 seconds

    // Counter for total customers created
    private AtomicInteger totalCustomersGenerated;
    // Counter for total customers served
    private AtomicInteger totalCustomersServed;

    // CONSTRUCTOR
    
    /**
     * Constructor for the GroceryStore.
     * Initializes the array of 5 Cashier and starts each thread.
     */
    public GroceryStore() {
        // Initialize variables
        this.cashiers = new Cashier[NUM_CASHIERS];
        this.rand = new Random();
        this.totalCustomersGenerated = new AtomicInteger(0);
        this.totalCustomersServed = new AtomicInteger(0);

        // Create and start all the cashier threads
        for (int i = 0; i < NUM_CASHIERS; i++) {
            // Create cashier with a different name
            cashiers[i] = new Cashier("Cashier-" + (i + 1), this);
            // Start the thread for cashier index i
            cashiers[i].start(); 
        }
    }

    // MEMBER METHODS
    
    /**
     * The main loop for the GroceryStore simulation thread.
     * Generates customers at random interval range and adds them to the shortest queue.
     * The loop continues until the thread is interrupted by stopSimulation() method.
     */
    @Override
    public void run() {
        System.out.println("GroceryStore thread is running.");
        // Loop checks the thread status timing component
        // While not interrupted, simulate customer arrivals
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Initalize random arrival time between min and max
                int arrivalTime = rand.nextInt(MAX_ARRIVAL_MS - MIN_ARRIVAL_MS + 1) + MIN_ARRIVAL_MS;
                // Wait for the next customer arrival
                Thread.sleep(arrivalTime);

                // Add the customer to the queue
                addCustomerToQueue();

            } catch (InterruptedException e) {
                // Interruption received
                Thread.currentThread().interrupt();
                break; // Exit the loop
            }
        }
        System.out.println("GroceryStore thread is stopping.");
    }

    /**
     * Finds the shortest cashier line and adds a new customer to the queue.
     * Increases the total customers generated counter.
     */
    private void addCustomerToQueue() {
        // Initialize shortest line index at 0
        int shortestLineIndex = 0;
        // Initialize first cashier size
        int minQueueSize = cashiers[0].getQueueSize();
        
        // Find the line/queue with the fewest customers
        for (int i = 1; i < NUM_CASHIERS; i++) {
            // Check for smaller queue
            if (cashiers[i].getQueueSize() < minQueueSize) {
                minQueueSize = cashiers[i].getQueueSize();
                shortestLineIndex = i;
            }
        }

        // Create the new customer object
        Customer newCustomer = new Customer();
        // Increase total customers generated counter
        totalCustomersGenerated.incrementAndGet(); 
        
        // Add them to the shortest line
        cashiers[shortestLineIndex].addCustomer(newCustomer);

        // Print the event in console
        System.out.printf("Customer %s joined line for %s - queue size: %d\n",
                newCustomer.getId(),
                cashiers[shortestLineIndex].getName(),
                cashiers[shortestLineIndex].getQueueSize());
    }

    /**
     * Tracks when cashiers serve a customer.
     * Increases the total customers served counter.
     */
    public void increaseCustomersServed() {
        totalCustomersServed.incrementAndGet();
    }

    /**
     * Stop Simulation Method -
     * Interrupts the grocery store thread and all 
     * cashier threads to stop the simulation.
     */
    public void stopSimulation() {
        // Interrupt grocery store thread
        this.interrupt();

        // Interrupt all cashier threads
        for (Cashier c : cashiers) {
            c.interrupt();
        }
    }

    /**
     * Prints the summary of the simulation.
     * Includes total customers generated, served,
     * and individual cashier stats.
     */
    public void printSummary() {
        // Print total summary of simulation.
        System.out.println("\n---------------------------------------");
        System.out.println("|          Simulation Results         |");
        System.out.println("---------------------------------------");
        System.out.println("Total customers generated: " + totalCustomersGenerated.get());
        System.out.println("Total customers served:    " + totalCustomersServed.get());
        
        // Print each cashier's stats.
        System.out.println("\n----------- Cashier Stats -----------"); 
        int totalRemaining = 0;
        // Loop through each cashier.
        for (int i = 0; i < NUM_CASHIERS; i++) {
            int remaining = cashiers[i].getQueueSize();
            // Gets the cashier served count..
            int served = cashiers[i].getCustomersServed(); 
            
            // Prints each cashier stats.
            System.out.printf("  %s: %d customers served (left %d in queue)\n", 
                    cashiers[i].getName(), served, remaining);
            
            totalRemaining += remaining;
        }
        System.out.println("\nTotal customers left in lines: " + totalRemaining);
        System.out.println("---------------------------------------");
    }
}