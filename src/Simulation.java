import java.util.Random;

/**
 * Manages and runs the entire grocery store simulation.
 * Contains the main loop, all simulation state, constants, and statistics.
 * This class handles customer arrivals, line management, and result reporting.
 *
 * @author Nicholas Racette
 * @contact: Nick.Racette@century.edu
 * @since: 11/15/2025
 *
 * Course: CSCI 2082-70
 * Institution: Century College
 * Instructor: Mathew Nyamgawa
 */
public class Simulation {

    // === SIMULATION CONSTANTS ===
    public final int SIMULATION_DURATION = 10000; // Total time steps to run
    public final int NUM_LINES = 5;              // As required by the project
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
   // Customer will "balk" (not join) if all lines are this long 
    public final int BALK_LINE_LENGTH = 10;

    // === SIMULATION STATE ===
    private CashierLine[] lines;
    private Customer[] allCustomers; // Master array holding all customer objects
    private int customerCount;       // Counter for total customers created
    private Random rand;             // Random number generator

    // === STATISTICS ===
    private int totalCustomersServed;
    private int totalCustomersBalked;
    private int totalCustomersReneged;// Left line due to patience 
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

           // 2. Customers in line check patience and switch lines 
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
     * Delegates complex logic to helper methods.
     *
     * @param currentTime The current simulation time step.
     */
    private void handleCustomerArrival(int currentTime) {
        // Check if a customer arrives based on probability
        if (rand.nextDouble() > ARRIVAL_PROBABILITY) {
            return; // No customer arrives this step
        }

        // Check if simulation has reached its customer capacity
        if (customerCount >= MAX_CUSTOMERS) {
            System.out.println("WARN: Max customer limit reached. No new arrivals.");
            return;
        }

        // 1. Create the customer
        Customer newCustomer = createNewCustomer(currentTime, customerCount);

        // 2. Find the best line
        int chosenLineIndex = findShortestLineIndex();

        // 3. Place them (or handle balking)
        placeCustomer(newCustomer, chosenLineIndex, currentTime);
    }

    /**
     * Creates a new customer with randomized properties.
     *
     * @param currentTime The arrival time (and current time).
     * @param customerId The new customer's unique ID.
     * @return A newly constructed Customer object.
     */
    private Customer createNewCustomer(int currentTime, int customerId) {
        int serviceTime = rand.nextInt(MAX_SERVICE_TIME - MIN_SERVICE_TIME + 1) + MIN_SERVICE_TIME;
        int maxWait = rand.nextInt(MAX_MAX_WAIT - MIN_MAX_WAIT + 1) + MIN_MAX_WAIT;
        int checkInterval = rand.nextInt(MAX_CHECK_INTERVAL - MIN_CHECK_INTERVAL + 1) + MIN_CHECK_INTERVAL;

        return new Customer(customerId, currentTime, serviceTime, maxWait, checkInterval);
    }

    /**
     * Places a customer in their chosen line, or handles balking.
     * Increments customerCount if the customer successfully joins a line.
     *
     * @param newCustomer The customer to place.
     * @param chosenLineIndex The index of the line they chose.
     * @param currentTime The current simulation time.
     */
    private void placeCustomer(Customer newCustomer, int chosenLineIndex, int currentTime) {
       // Check for "Balking" (avoiding long lines) 
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

        // Increment *after* using customerCount as the ID
        customerCount++; 
    }


    /**
     * Step 2: Check all customers in lines for patience or line-switching.
     * Iterates through each line and each customer, rebuilding the queue
     * with only the customers who stay.
     *
     * @param currentTime The current simulation time step.
     */
    private void handleInLineActions(int currentTime) {
        for (int i = 0; i < NUM_LINES; i++) {
            IntArrayQueue currentQueue = lines[i].getCustomerQueue();
            // Create a temporary queue to hold customers who stay
            IntArrayQueue tempQueue = new IntArrayQueue(currentQueue.capacity());
            int lineSize = currentQueue.size();

            // Iterate through each customer in the line *without* breaking the queue
            for (int j = 0; j < lineSize; j++) {
                // Get customer from the front
                int custId = currentQueue.dequeue();
                Customer c = allCustomers[custId];

               // Check for "Reneging" (leaving line) 
                if (handleCustomerReneging(c, currentTime)) {
                    continue; // Customer left, do not re-enqueue
                }

               // Check for "Jockeying" (switching lines) 
                if (handleCustomerJockeying(c, i, currentTime)) {
                    continue; // Customer switched, do not re-enqueue
                }

                // If they didn't leave or switch, put them back in the temp queue
                tempQueue.enqueue(custId);
            }
            
            // The original queue is now empty.
            // Replace it with the tempQueue holding all customers who stayed.
            lines[i].setCustomerQueue(tempQueue);
        }
    }

    /**
     * Checks if a customer has exceeded their patience and leaves the line.
     *
     * @param c The customer to check.
     * @param currentTime The current simulation time.
     * @return true if the customer left (reneged), false otherwise.
     */
    private boolean handleCustomerReneging(Customer c, int currentTime) {
        if (c.hasExceededPatience(currentTime)) {
            totalCustomersReneged++;
            c.setLineIndex(-1); // Mark as out of line
            return true; // Customer left
        }
        return false; // Customer stayed
    }

    /**
     * Checks if a customer should look for a shorter line and switches them.
     *
     * @param c The customer to check.
     * @param currentLineIndex The index of the customer's current line.
     * @param currentTime The current simulation time.
     * @return true if the customer switched (jockeyed), false otherwise.
     */
    private boolean handleCustomerJockeying(Customer c, int currentLineIndex, int currentTime) {
        if (c.shouldCheckOtherLine(currentTime)) {
            // Schedule the next check, regardless of whether they switch
            c.scheduleNextCheck(currentTime);
            
            int bestLineIndex = findShortestLineIndex();
            
            // Switch if the *new* line is shorter than the *current* one
            if (bestLineIndex != currentLineIndex && lines[bestLineIndex].size() < lines[currentLineIndex].size()) {
                lines[bestLineIndex].addCustomer(c.getId()); // Add to new line
                c.setLineIndex(bestLineIndex);
                c.setTimeEnteredLine(currentTime); // Reset wait time
                return true; // Left this line
            }
        }
        return false; // Did not switch
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
     *If lines are equal, picks the first available one (lowest index).
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
     *Prints the final simulation statistics in an attractive format.
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