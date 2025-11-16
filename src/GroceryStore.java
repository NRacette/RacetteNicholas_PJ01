import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 * Manages the overall grocery store simulation. This class is a thread
 * itself (the "producer" thread) whose only job is to create customers
 * at random intervals and add them to the shortest cashier line.
 * <p>
 * In its constructor, it creates and starts an array of 5 Cashier threads.
 * It also tracks simulation-wide statistics.
 *
 * @author Nicholas Racette
 * @contact: Nick.Racette@century.edu
 * @since: 11/15/2025
 *
 * Course: CSCI 2082-70
 * Institution: Century College
 * Instructor: Mathew Nyamgawa
 */
public class GroceryStore extends Thread {

    // === PROPERTIES ===
    public final int NUM_CASHIERS = 5;
    private Cashier[] cashiers;
    private Random rand;

    // Customer arrival time (producer sleep)
    private final int MIN_ARRIVAL_MS = 500;  // 0.5 seconds
    private final int MAX_ARRIVAL_MS = 1000; // 1.0 seconds

    // === STATISTICS ===
    /** Thread-safe counter for total customers created */
    private AtomicInteger totalCustomersGenerated;
    /** Thread-safe counter for total customers served */
    private AtomicInteger totalCustomersServed;

    // === CONSTRUCTOR ===
    
    /**
     * Constructor for the GroceryStore.
     * Initializes the array of 5 Cashier objects and starts
     * each of their threads.
     */
    public GroceryStore() {
        this.cashiers = new Cashier[NUM_CASHIERS];
        this.rand = new Random();
        this.totalCustomersGenerated = new AtomicInteger(0);
        this.totalCustomersServed = new AtomicInteger(0);

        // Create and start all the cashier threads (consumers)
        for (int i = 0; i < NUM_CASHIERS; i++) {
            // Pass a reference of this store to each cashier
            // so they can report back when they serve a customer.
            cashiers[i] = new Cashier("Cashier-" + (i + 1), this);
            cashiers[i].start(); // Start the cashier thread
        }
    }

    // === CORE THREAD LOGIC ===
    
    /**
     * The main loop for the GroceryStore (producer) thread.
     * (This method is unchanged)
     */
    @Override
    public void run() {
        System.out.println("GroceryStore customer-arrival thread is running.");
        // The loop now checks the thread's "interrupted" status
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // 1. "Wait" for a new customer to arrive
                int arrivalTime = rand.nextInt(MAX_ARRIVAL_MS - MIN_ARRIVAL_MS + 1) + MIN_ARRIVAL_MS;
                Thread.sleep(arrivalTime);

                // 2. Add the customer to the shortest queue
                addCustomerToQueue();

            } catch (InterruptedException e) {
                // Interruption received (from stopSimulation())
                Thread.currentThread().interrupt(); // Re-set flag
                break; // Exit the loop
            }
        }
        System.out.println("GroceryStore thread is stopping.");
    }

    /**
     * Finds the shortest cashier line and adds a new customer to it.
     * (This method is unchanged)
     */
    private void addCustomerToQueue() {
        // Find the line (cashier) with the fewest customers
        int shortestLineIndex = 0;
        int minQueueSize = cashiers[0].getQueueSize();

        for (int i = 1; i < NUM_CASHIERS; i++) {
            if (cashiers[i].getQueueSize() < minQueueSize) {
                minQueueSize = cashiers[i].getQueueSize();
                shortestLineIndex = i;
            }
        }

        // Create the new customer
        Customer newCustomer = new Customer();
        totalCustomersGenerated.incrementAndGet(); // Increment stat
        
        // Add them to the chosen line
        cashiers[shortestLineIndex].addCustomer(newCustomer);

        System.out.printf("Customer %s joined line for %s (queue size: %d)\n",
                newCustomer.getId(),
                cashiers[shortestLineIndex].getName(),
                cashiers[shortestLineIndex].getQueueSize());
    }

    // === STATS & CONTROL METHODS ===

    /**
     * Thread-safe method for cashiers to report when they serve a customer.
     * (This method is unchanged)
     */
    public void incrementCustomersServed() {
        totalCustomersServed.incrementAndGet();
    }

    /**
     * Stop Simulation Method
     * (This method is unchanged)
     */
    public void stopSimulation() {
        // 1. Interrupt this (producer) thread
        this.interrupt();

        // 2. Interrupt all cashier (consumer) threads
        for (Cashier c : cashiers) {
            c.interrupt();
        }
    }

    /**
     * NEW: Prints the final summary of the simulation.
     * This is called by the Driver after the simulation is stopped.
     */
    public void printFinalSummary() {
        System.out.println("\n---------------------------------------");
        System.out.println("--- Final Simulation Results ---");
        System.out.println("---------------------------------------");
        System.out.println("Total customers generated: " + totalCustomersGenerated.get());
        System.out.println("Total customers served:    " + totalCustomersServed.get());
        
        System.out.println("\n--- Individual Cashier Stats ---"); // NEW: Section for individual stats
        
        int totalRemaining = 0;
        for (int i = 0; i < NUM_CASHIERS; i++) {
            int remaining = cashiers[i].getQueueSize();
            // NEW: Get the individual cashier's served count
            int served = cashiers[i].getCustomersServed(); 
            
            // NEW: Updated print statement
            System.out.printf("  %s: %d customers served (left %d in queue)\n", 
                    cashiers[i].getName(), served, remaining);
            
            totalRemaining += remaining;
        }
        System.out.println("\nTotal customers left in lines: " + totalRemaining);
        System.out.println("---------------------------------------");
    }
}