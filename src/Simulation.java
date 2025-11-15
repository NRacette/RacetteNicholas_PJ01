import java.util.Random;
/**
 * Manages and runs the entire grocery store simulation.
 * Contains the main loop, all simulation state, constants, and statistics.
 * This class handles customer arrivals, line management, and result reporting.
 */
public class Simulation {

    // === SIMULATION CONSTANTS ===
    public final int SIMULATION_DURATION = 10000; // Total time steps to run
    public final int NUM_LINES = 5;               //[cite_start] As required by the project [cite: 8]
    public final int MAX_CUSTOMERS = 20000;     // Max customers for the allCustomers array
    public final double ARRIVAL_PROBABILITY = 0.30; // 30% chance of arrival per time step

    // --- Customer Behavior Constants ---
    public final int MIN_SERVICE_TIME = 60;   // 1 minute
    public final int MAX_SERVICE_TIME = 360;  // 6 minutes
    public final int MIN_MAX_WAIT = 600;      // 10 minutes
    public final int MAX_MAX_WAIT = 1800;     // 30 minutes
    public final int MIN_CHECK_INTERVAL = 30;   // 30 seconds
    public final int MAX_CHECK_INTERVAL = 120;  // 2 minutes

    // --- Line Behavior Constants ---
    // Customer will "balk" (not join) if all lines are this long [cite: 12]
    public final int BALK_LINE_LENGTH = 10;

    // === SIMULATION STATE ===
    private CashierLine[] lines;
    private Customer[] allCustomers;
    private int customerCount;
    private Random rand;

    // === STATISTICS ===
    private int totalCustomersServed;
    private int totalCustomersBalked;
    private int totalCustomersReneged; //[cite_start] Left line due to patience [cite: 13]
    private long totalWaitTime;

    /**
     * Constructor: Initializes the simulation state.
     * Creates the cashier lines, customer array, and random number generator.
     */
    public Simulation() {
        // Initialize cashier lines
        lines = new CashierLine[NUM_LINES];
        for (int i = 0; i < NUM_LINES; i++) {
            lines[i] = new CashierLine(i);
        }

        // Initialize customer tracking
        allCustomers = new Customer[MAX_CUSTOMERS];
        customerCount = 0;

        // Initialize random number generator
        rand = new Random();

        // Reset statistics
        totalCustomersServed = 0;
        totalCustomersBalked = 0;
        totalCustomersReneged = 0;
        totalWaitTime = 0;
    }

    /**
     * The main public method to run the entire simulation from start to finish.
     * It iterates through each time step, processes events, and then prints results.
     */
    public void run() {
        // Run the main simulation loop
        for (int currentTime = 0; currentTime < SIMULATION_DURATION; currentTime++) {
            // Order of operations is important!
            
            // 1. New customers arrive and pick lines
            handleCustomerArrival(currentTime);

            // 2. Customers in line check patience and switch lines [cite: 13, 14]
            handleInLineActions(currentTime);

            // 3. Cashiers serve customers at the front of the line
            handleCashierService(currentTime);
        }

        // 4. Print final results
        printResults();
    }

    // --- Private Helper Methods (The Core Logic) ---

    /**
     * Step 1: (Potentially) create a new customer and add them to a line.
     * Handles customer creation and "balking" logic.
     *
     * @param currentTime The current simulation time step.
     */
    private void handleCustomerArrival(int currentTime) {
        if (rand.nextDouble() > ARRIVAL_PROBABILITY) {
            return; // No customer arrives this step
        }

        if (customerCount >= MAX_CUSTOMERS) {
            System.out.println("WARN: Max customer limit reached. No new arrivals.");
            return;
        }

        // Create the new customer with random properties
        int serviceTime = rand.nextInt(MAX_SERVICE_TIME - MIN_SERVICE_TIME + 1) + MIN_SERVICE_TIME;
        int maxWait = rand.nextInt(MAX_MAX_WAIT - MIN_MAX_WAIT + 1) + MIN_MAX_WAIT;
        int checkInterval = rand.nextInt(MAX_CHECK_INTERVAL - MIN_CHECK_INTERVAL + 1) + MIN_CHECK_INTERVAL;

        Customer newCustomer = new Customer(customerCount, currentTime, serviceTime, maxWait, checkInterval);

        // Find the shortest line for the customer [cite: 9]
        int chosenLineIndex = findShortestLineIndex();

        // Check for "Balking" (avoiding long lines) [cite: 12]
        if (lines[chosenLineIndex].size() >= BALK_LINE_LENGTH) {
            totalCustomersBalked++;
            // Don't add customer to allCustomers, they left immediately
            return; 
        }

        // Add customer to the simulation
        allCustomers[customerCount] = newCustomer; // Store the customer
        lines[chosenLineIndex].addCustomer(newCustomer.getId()); // Add ID to queue
        
        // Update customer's state
        newCustomer.setLineIndex(chosenLineIndex);
        newCustomer.setTimeEnteredLine(currentTime);

        customerCount++; // Increment *after* using customerCount as the ID
    }

    /**
     * Step 2: Check all customers in lines for patience or line-switching.
     * This method iterates through every customer in every line.
     *
     * @param currentTime The current simulation time step.
     */
    private void handleInLineActions(int currentTime) {
        for (int i = 0; i < NUM_LINES; i++) {
            IntArrayQueue currentQueue = lines[i].getCustomerQueue();
            IntArrayQueue tempQueue = new IntArrayQueue(currentQueue.capacity());
            int lineSize = currentQueue.size();

            // Iterate through each customer in the line *without* breaking the queue
            for (int j = 0; j < lineSize; j++) {
                int custId = currentQueue.dequeue();
                Customer c = allCustomers[custId];
                boolean hasLeftOrSwitched = false;

                // Check for "Reneging" (leaving line due to patience) [cite: 13]
                if (c.hasExceededPatience(currentTime)) {
                    totalCustomersReneged++;
                    c.setLineIndex(-1); // Mark as out of line
                    hasLeftOrSwitched = true; // Don't re-enqueue

                // Check for "Jockeying" (switching lines) [cite: 14, 15]
                } else if (c.shouldCheckOtherLine(currentTime)) {
                    int bestLineIndex = findShortestLineIndex();
                    
                    // Switch if the *new* line is shorter than the *current* one
                    if (bestLineIndex != i && lines[bestLineIndex].size() < lines[i].size()) {
                        lines[bestLineIndex].addCustomer(c.getId()); // Add to new line
                        c.setLineIndex(bestLineIndex);
                        c.setTimeEnteredLine(currentTime); // Reset wait time
                        hasLeftOrSwitched = true; // Left this line
                    }
                    c.scheduleNextCheck(currentTime);
                }

                // If they didn't leave or switch, put them back in line
                if (!hasLeftOrSwitched) {
                    tempQueue.enqueue(custId);
                }
            }
            // The original queue is now empty.
            // Replace it with the tempQueue holding all customers who stayed.
            lines[i].setCustomerQueue(tempQueue);
        }
    }

    /**
     * Step 3: Tell each cashier to process one customer if they are ready.
     *
     * @param currentTime The current simulation time step.
     */
    private void handleCashierService(int currentTime) {
        for (int i = 0; i < NUM_LINES; i++) {
            // This method checks if the cashier is free and if a customer is waiting
            int servedCustomerId = lines[i].processCashier(currentTime, allCustomers);

            if (servedCustomerId != -1) {
                // A customer finished! Record stats.
                Customer c = allCustomers[servedCustomerId];
                totalCustomersServed++;
                totalWaitTime += c.getWaitingTime(currentTime);
                c.setLineIndex(-1); // Mark as served and out of system
            }
        }
    }

    /**
     * Finds the index of the shortest line.
     * If lines are equal, picks the first available one (lowest index)[cite: 10].
     *
     * @return The index (0-4) of the shortest line.
     */
    private int findShortestLineIndex() {
        int shortestIndex = 0;
        int minLength = lines[0].size();

        for (int i = 1; i < NUM_LINES; i++) {
            if (lines[i].size() < minLength) {
                minLength = lines[i].size();
                shortestIndex = i;
            }
        }
        return shortestIndex;
    }

    /**
     * Prints the final simulation statistics in an attractive format[cite: 32].
     */
    private void printResults() {
        System.out.println("\n--- Simulation Results ---");
        System.out.println("Total time steps run: " + SIMULATION_DURATION);
        System.out.println("Total customers generated: " + customerCount);
        System.out.println("---------------------------------------");
        System.out.println("Total customers served: " + totalCustomersServed);
        System.out.println("Total customers who left (balked): " + totalCustomersBalked);
        System.out.println("Total customers who left (reneged): " + totalCustomersReneged);
        System.out.println("---------------------------------------");

        if (totalCustomersServed > 0) {
            double avgWaitSeconds = (double) totalWaitTime / totalCustomersServed;
            double avgWaitMinutes = avgWaitSeconds / 60.0;
            System.out.printf("Average wait time: %.2f seconds (%.2f minutes)\n", avgWaitSeconds, avgWaitMinutes);
        } else {
            System.out.println("Average wait time: N/A (No customers served)");
        }
        
        System.out.println("\nCustomers remaining in lines at end:");
        int totalRemaining = 0;
        for (int i = 0; i < NUM_LINES; i++) {
            int remaining = lines[i].size();
            System.out.println("Line " + (i + 1) + ": " + remaining + " customers");
            totalRemaining += remaining;
        }
        System.out.println("Total customers left in lines: " + totalRemaining);
    }

}
