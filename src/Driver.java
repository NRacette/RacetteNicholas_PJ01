/**
 * Description:
 * This program drives a multi-threaded, event-driven simulation of 
 * cashier lines at a grocery store. It creates a single "GroceryStore" 
 * object, which in turn creates and manages 5 "Cashier" threads. 
 * The GroceryStore thread produces customers, and the Cashier threads 
 * consume them.
 *
 * This simulation is time-based and will run for a fixed duration
 * set in this class, then print a final summary.
 *
 * @author Nicholas Racette
 * @contact: Nick.Racette@century.edu
 * @since: 11/15/2025
 *
 * Course: CSCI 2082-70
 * Institution: Century College
 * Instructor: Mathew Nyamgawa
 */
public class Driver {

    /**
     * The total time (in milliseconds) the simulation will run.
     * 30,000 ms = 30 seconds.
     */
    private static final int SIMULATION_DURATION_MS = 30000;

    /**
     * Main entry point for the program.
     * Creates and starts the grocery store simulation thread.
     * Waits for a set time, then sends the stop signal and prints results.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("--- Simulation Starting ---");
        System.out.println("Program by: Nicholas Racette"); // Display name
        System.out.printf("Simulation will run for %d seconds.\n\n", SIMULATION_DURATION_MS / 1000);

        // 1. Create and start the store (which starts the cashiers)
        GroceryStore store = new GroceryStore();
        store.start();

        try {
            // 2. This is the main simulation timer. The main thread
            //    will sleep here, letting the simulation run.
            Thread.sleep(SIMULATION_DURATION_MS);

        } catch (InterruptedException e) {
            System.err.println("Main driver thread was interrupted.");
            Thread.currentThread().interrupt();
        }

        // 3. Time's up! Send the stop signal.
        System.out.println("\n--- Simulation Timer Expired ---");
        store.stopSimulation();
        System.out.println("--- Simulation Stopping Signal Sent ---");

        // 4. Wait a moment for threads to fully stop
        try {
            // This brief sleep gives the threads time to exit their loops
            // and stop printing "stopping" messages before we print the summary.
            Thread.sleep(500); 
        } catch (InterruptedException e) {
            // This thread shouldn't be interrupted, but good practice
            Thread.currentThread().interrupt();
        }

        // 5. Print the final statistical summary 
        store.printFinalSummary();
    }
}