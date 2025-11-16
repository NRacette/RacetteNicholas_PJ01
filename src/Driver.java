/**
 * Name: [Your Name]
 * Due Date: [Due Date]
 * * Description:
 * This program drives a discrete-event simulation of cashier lines
 * at a grocery store. It follows the objective of using
 * fundamental data structures (like an ArrayQueue) to solve a problem.
 * * The main method simply creates and runs a Simulation object.
 */
public class Driver {

    /**
     * Main entry point for the program.
     * Creates and runs the grocery store simulation.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("--- Simulation Starting ---");
        System.out.println("Simulator run by: [Your Name]"); 

        // 1. Create a new simulation object
        Simulation grocerySim = new Simulation();

        // 2. Tell it to run
        grocerySim.run();

        System.out.println("\n--- Simulation Complete ---");
    }
}