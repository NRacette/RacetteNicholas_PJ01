/**
 * Description: This program uses threading, to simulation cashier lines at a grocery store. 
 * It creates a single "GroceryStore" object, which in turn creates and manages 5 Cashier threads. 
 * The GroceryStore thread produces customers, and the Cashier threads consume them.
 * This simulation is time-based and will run for a fixed duration
 * set in this class, then print a simulation summary.
 *
 * CONSTRAINTS:
 * SIMULATION_DURATION_MS Simulation duration.
 *
 * COMPUTATION:
 * 1. Creates and starts a single GroceryStore thread.
 * 2. The GroceryStore thread creates and starts 5 Cashier threads.
 * 3. The GroceryStore thread generates Customer objects at random intervals.
 * 4. Cashier threads dequeue Customers once serving them.
 * 5. After the simulation duration, the Driver calls stopSimulation() to stop all threads.
 *
 * OUTPUT:
 * 1. Real-time logging for customers arrive and are served.
 * 2. A final summary of simulation results, including total customers and total served.
 *
 * @author Nicholas Racette
 * @contact: Nick.Racette@century.edu
 * @since: 11/16/2025
 *
 * Course: CSCI 2082-70
 * Institution: Century College
 * Instructor: Mathew Nyamgawa
 */
public class Driver {

    // The total time simulation will run 30,000 ms = 30 seconds.
    private static final int SIMULATION_DURATION_MS = 30000;

    /**
     * Main run point for the program.
     * Creates and starts the grocery store simulation threads.
     * Waits for a 30 seconds, then stops threads and prints results of simulation.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("--- Simulation Starting ---");
        System.out.println("Program by: Nicholas Racette"); 
        System.out.printf("Simulation will run for %d seconds.\n\n", SIMULATION_DURATION_MS / 1000);

        // Create and store object 
        GroceryStore store = new GroceryStore();
        // Starts store thread
        store.start();

        // Main simulation timer
        try {
            Thread.sleep(SIMULATION_DURATION_MS);
        } catch (InterruptedException e) {
            System.err.println("Thread was interrupted.");
            Thread.currentThread().interrupt();
        }

        // Call the stopSimulation method on the store.
        System.out.println("\n-------- Simulation Over --------");
        store.stopSimulation();
        System.out.println("--- Simulation Stopping Called ---");

        // Give some time for threads to stop
        try {
            Thread.sleep(500); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Print the simulation summary
        store.printSummary();
    }
}