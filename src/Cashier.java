import java.util.Random;

/**
 * Description:
 * Represents a single cashier, which runs as its own thread (a "consumer").
 * Each Cashier has their own CustomerArrayQueue. Its run() method
 * consists of an infinite loop of removing a customer from the queue
 * (waiting if empty), and then "serving" them by sleeping for a
 * random amount of time. This loop continues until the thread is
 * interrupted.
 *
 * @author Nicholas Racette
 * @contact: Nick.Racette@century.edu
 * @since: 11/15/2025
 *
 * Course: CSCI 2082-70
 * Institution: Century College
 * Instructor: Mathew Nyamgawa
 */
public class Cashier extends Thread {

    // === PROPERTIES ===
    private CustomerArrayQueue queue;
    private Random rand;
    
    /** A reference back to the main store to report stats */
    private GroceryStore store;

    /** NEW: Counter for this specific cashier's total */
    private int customersServed;

    // Customer service time (consumer sleep)
    private final int MIN_SERVICE_MS = 3000; // 3.0 seconds
    private final int MAX_SERVICE_MS = 6000; // 6.0 seconds

    // === CONSTRUCTOR ===
    
    /**
     * Constructs a new Cashier thread.
     * @param name The name of the thread (e.g., "Cashier-1")
     * @param store A reference to the main GroceryStore for stat reporting
     */
    public Cashier(String name, GroceryStore store) {
        super(name); // Set the thread's name
        this.store = store;
        this.queue = new CustomerArrayQueue(10); // Initial capacity of 10
        this.rand = new Random();
        this.customersServed = 0; // NEW: Initialize the counter
    }

    // === CORE THREAD LOGIC ===
    
    /**
     * The main loop for the Cashier (consumer) thread.
     * It continuously dequeues a customer and serves them
     * until the thread is interrupted.
     */
    @Override
    public void run() {
        System.out.println(this.getName() + " thread is running.");
        // The loop now checks the thread's "interrupted" status
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // 1. Remove a customer (this will wait if queue is empty)
                Customer customer = removeCustomer();

                // 2. "Serve" the customer by sleeping
                int serviceTime = rand.nextInt(MAX_SERVICE_MS - MIN_SERVICE_MS + 1) + MIN_SERVICE_MS;
                
                System.out.printf("... %s started serving Customer %s (will take %dms)\n",
                        this.getName(), customer.getId(), serviceTime);

                // 3. Sleep for the service time
                Thread.sleep(serviceTime); 

                // 4. Report that a customer was served
                store.incrementCustomersServed(); // Report to global counter
                this.customersServed++;           // NEW: Increment local counter

                System.out.printf(">>> %s finished with Customer %s (queue size: %d)\n",
                        this.getName(), customer.getId(), this.getQueueSize());

            } catch (InterruptedException e) {
                // Interruption received (from stopSimulation())
                Thread.currentThread().interrupt(); // Re-set flag
                break; // Exit the loop
            }
        }
        System.out.println(this.getName() + " thread is stopping.");
    }

    /**
     * Waits for and removes a customer from this cashier's queue.
     *
     * @return The Customer from the front of the queue.
     * @throws InterruptedException if the thread is interrupted while waiting.
     */
    private Customer removeCustomer() throws InterruptedException {
        // dequeue() is synchronized and will wait() if the queue is empty.
        // If interrupted during wait(), it will throw InterruptedException.
        return queue.dequeue();
    }

    // === PUBLIC METHODS (Called by GroceryStore) ===
    
    /**
     * Adds a customer to this cashier's queue.
     * @param customer The customer to add.
     */
    public void addCustomer(Customer customer) {
        queue.enqueue(customer);
    }

    /**
     * Gets the current number of items in the queue.
     * @return The size of the queue.
     */
    public int getQueueSize() {
        return queue.size();
    }

    /**
     * NEW: Gets the total number of customers served by this specific cashier.
     * This is only read by the main thread *after* this thread has stopped,
     * so it doesn't need to be synchronized.
     *
     * @return The total customers served count.
     */
    public int getCustomersServed() {
        return this.customersServed;
    }
}