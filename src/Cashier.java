import java.util.Random;

/**
 * Description: Class implementing a cashier, which runs as its own thread.
 * Each Cashier has their own line / queue using CustomerArrayQueue. Has a run() method
 * that infinitely loops, removing a customer from the queue which simulates serving,
 * serving takes a random amount of time. This loop continues until the thread is stopped.
 *
 * @author Nicholas Racette
 * @contact: Nick.Racette@century.edu
 * @since: 11/16/2025
 *
 * Course: CSCI 2082-70
 * Institution: Century College
 * Instructor: Mathew Nyamgawa
 */
public class Cashier extends Thread {

    // PROPERTIES 
    private CustomerArrayQueue queue;
    private Random rand;
    // Used to report stats back to GroceryStore
    private GroceryStore store;
    // Counter for total customers served by this cashier
    private int customersServed;
    // Customer service time range
    private final int MIN_SERVICE_MS = 3000; // 3.0 seconds
    private final int MAX_SERVICE_MS = 6000; // 6.0 seconds

    // CONSTRUCTOR 
    
    /**
     * Constructs a new Cashier thread.
     * Initializes the cashier's queue and random number generator.
     * 
     * @param name The name of the thread.
     * @param store A reference to the main GroceryStore for stat reporting.
     */
    public Cashier(String name, GroceryStore store) {
        // Set the thread name
        super(name); 
        this.store = store;
        // Initial capacity of 10
        this.queue = new CustomerArrayQueue(10); 
        this.rand = new Random();
        // Initialize the counter
        this.customersServed = 0; 
    }

    // MEMBER METHODS
    
    /**
     * The main loop for the Cashier thread.
     * It continuously adds customers and removes them from the queue / line,
     * until the thread is stopped.
     */
    @Override
    public void run() {
        System.out.println(this.getName() + " Thread is running.");
        // Runs cashier loop until thread is interrupted
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Remove a customer 
                Customer customer = removeCustomer();

                // Generate a random serviceTime using the range constants
                int serviceTime = rand.nextInt(MAX_SERVICE_MS - MIN_SERVICE_MS + 1) + MIN_SERVICE_MS;
                
                // Print that the cashier is starting to serve the customer for simulation logging
                System.out.printf("... %s started serving Customer %s (will take %dms)\n",
                        this.getName(), customer.getId(), serviceTime);

                // Simulate serving the customer by sleeping
                Thread.sleep(serviceTime); 

                store.increaseCustomersServed(); // Report to global counter
                this.customersServed++; // Increase local counter

                // Print that the cashier has finished serving the customer for simulation logging
                System.out.printf(">>> %s finished with Customer %s (queue size: %d)\n",
                        this.getName(), customer.getId(), this.getQueueSize());

            } catch (InterruptedException e) {
                // Stop thread requested from stopSimulation()
                Thread.currentThread().interrupt(); 
                break; // Exit the loop
            }
        }
        System.out.println(this.getName() + " thread is stopping.");
    }

    /**
     * Waits and then removes a customer from this cashiers queue / line.
     * If the queue is empty, will wait until a customer is in the queue.
     *
     * @return The Customer from the front of the queue.
     * @throws InterruptedException For when the thread is interrupted.
     */
    private Customer removeCustomer() throws InterruptedException {
        // Dequeue the next customer will wait if queue is empty.
        return queue.dequeue();
    }

    // MEMBER METHODS
    /**
     * Adds a customer to the cashier queue.
     * 
     * @param customer The customer to add to queue.
     */
    public void addCustomer(Customer customer) {
        queue.enqueue(customer);
    }

    /**
     * Gets the current number of items in the queue.
     * 
     * @return The size of the queue.
     */
    public int getQueueSize() {
        return queue.size();
    }

    /**
     * Gets the total number of customers served / removed by this specific cashier.
     *
     * @return The total customers removed count.
     */
    public int getCustomersServed() {
        return this.customersServed;
    }
}