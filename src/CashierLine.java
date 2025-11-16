    
/**
 * Represents a single cashier line in the simulation.
 * It holds a queue of customer IDs and manages the cashier's busy status.
 * This class wraps the IntArrayQueue.
 *
 * @author Nicholas Racette
 * @contact: Nick.Racette@century.edu
 * @since: 11/15/2025
 *
 * Course: CSCI 2082-70
 * Institution: Century College
 * Instructor: Mathew Nyamgawa
 */
public class CashierLine {

    // === PROPERTIES ===

    /** The queue holding the IDs of customers in this line. */
    private IntArrayQueue customerQueue;

    /** The simulation time step when this cashier will be free. */
    private int cashierFreeTime;

    /** The index (0, 1, 2, 3, or 4) of this line, for reference. */
    private int lineIndex;

    // === CONSTRUCTOR ===

    /**
     * Constructs a new, empty cashier line.
     *
     * @param lineIndex The index of this line (e.g., 0 through 4).
     */
    public CashierLine(int lineIndex) {
        this.lineIndex = lineIndex;
        this.customerQueue = new IntArrayQueue(); // Uses default capacity
        this.cashierFreeTime = 0; // Cashier is free at the start.
    }

    // === CORE METHODS ===

    /**
     * Adds a customer to the end of this line's queue.
     *
     * @param customerId The ID of the customer to add.
     */
    public void addCustomer(int customerId) {
        this.customerQueue.enqueue(customerId);
    }

    /**
     * Checks if the cashier is free and, if so, serves the next customer.
     * If a customer is served, this method updates the cashier's free time
     * and returns the ID of the customer who finished.
     *
     * @param currentTime The current simulation time.
     * @param allCustomers The master array of all Customer objects.
     * @return The ID of the served customer, or -1 if no customer was served.
     */
    public int processCashier(int currentTime, Customer[] allCustomers) {
        // Case 1: Cashier is busy. Do nothing.
        if (currentTime < this.cashierFreeTime) {
            return -1;
        }

        // Case 2: Cashier is free, but the line is empty. Do nothing.
        if (this.customerQueue.isEmpty()) {
            return -1;
        }

        // Case 3: Cashier is free AND there is a customer. Serve them.
        int customerId = this.customerQueue.dequeue();
        Customer servedCustomer = allCustomers[customerId];

        // Set the new time this cashier will be busy until.
        // The transaction time is random.
        this.cashierFreeTime = currentTime + servedCustomer.getServiceTime();

        // Return the ID of the customer who was just served.
        return customerId;
    }

    // === GETTERS AND SETTERS ===

    /**
     * Gets the number of customers currently in this line.
     * @return The size of the queue.
     */
    public int size() {
        return this.customerQueue.size();
    }

    /**
     * Checks if this line is empty.
     * @return true if the queue is empty, false otherwise.
     */
    public boolean isEmpty() {
        return this.customerQueue.isEmpty();
    }

    /**
     * Gets the raw queue of customer IDs.
     * Needed by the Simulation class for line-switching/leaving logic.
     * @return The IntArrayQueue for this line.
     */
    public IntArrayQueue getCustomerQueue() {
        return this.customerQueue;
    }

    /**
     * Gets the index of this line.
     * @return The line index (e.g., 0 through 4).
     */
    public int getLineIndex() {
        return this.lineIndex;
    }

    /**
     * Replaces this line's queue with a new one.
     * Needed by the Simulation class after processing line-switching/leaving.
     * @param newQueue The new queue of customer IDs.
     */
    public void setCustomerQueue(IntArrayQueue newQueue) {
        this.customerQueue = newQueue;
    }
}
