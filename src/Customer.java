/**
 * Represents a single grocery store customer in the cashier line simulation.
 * A Customer has an id, arrival time, service time, patience, and some
 * information about line switching/checking.
 */
public class Customer {

    // === PROPERTIES ===
    /** Unique id for this customer (used as key in queues). */
    private int id;

    /** Time step at which the customer arrives in the store. */
    private int arrivalTime;

    /** Total amount of time the cashier needs to serve this customer. */
    private int serviceTime;

    /** Time step when the customer entered a line (for waiting-time calculations). */
    private int timeEnteredLine;

    /** Maximum time the customer is willing to wait in line before leaving. */
    private int maxWaitTime;

    /** How often (in time units) the customer will check if there is a shorter line. */
    private int checkInterval;

    /** Next time step at which this customer will check for a shorter line. */
    private int nextCheckTime;

    /** Index of the line this customer is currently in (-1 if not in any line). */
    private int lineIndex;


    // === CONSTRUCTOR ===

    /**
     * Constructs a new Customer.
     *
     * @param id             Unique id for this customer.
     * @param arrivalTime    Time the customer arrives in the store.
     * @param serviceTime    Time needed at the cashier to complete checkout.
     * @param maxWaitTime    Maximum waiting time before the customer leaves.
     * @param checkInterval  How often the customer checks for a shorter line.
     */
    public Customer(int id, int arrivalTime, int serviceTime,
                    int maxWaitTime, int checkInterval) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
        this.maxWaitTime = maxWaitTime;
        this.checkInterval = checkInterval;

        this.timeEnteredLine = -1;    // not in line yet
        this.nextCheckTime = arrivalTime + checkInterval;
        this.lineIndex = -1;          // not in any line yet
    }


    // === GETTERS (and simple setters where needed) ===

    public int getId() {
        return id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }

    public int getTimeEnteredLine() {
        return timeEnteredLine;
    }

    public void setTimeEnteredLine(int timeEnteredLine) {
        this.timeEnteredLine = timeEnteredLine;
    }

    public int getMaxWaitTime() {
        return maxWaitTime;
    }

    public int getCheckInterval() {
        return checkInterval;
    }

    public int getNextCheckTime() {
        return nextCheckTime;
    }

    public void setNextCheckTime(int nextCheckTime) {
        this.nextCheckTime = nextCheckTime;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public void setLineIndex(int lineIndex) {
        this.lineIndex = lineIndex;
    }


    // === HELPER METHODS USED BY THE SIMULATION ===

    /**
     * Returns the time this customer has been waiting in the current line.
     *
     * @param currentTime The current time step in the simulation.
     * @return The waiting time, or 0 if they haven't entered a line yet.
     */
    public int getWaitingTime(int currentTime) {
        if (timeEnteredLine < 0) {
            return 0; // not in line yet
        }
        return currentTime - timeEnteredLine;
    }

    /**
     * Returns true if the customer has waited longer than their maxWaitTime.
     *
     * @param currentTime The current time step in the simulation.
     * @return true if the customer should leave the line.
     */
    public boolean hasExceededPatience(int currentTime) {
        return getWaitingTime(currentTime) > maxWaitTime;
    }

    /**
     * Returns true if it is time for this customer to check for a shorter line.
     *
     * @param currentTime The current time step in the simulation.
     * @return true if they should look for a different (shorter) line.
     */
    public boolean shouldCheckOtherLine(int currentTime) {
        return currentTime >= nextCheckTime;
    }

    /**
     * Schedules the next time this customer will check for a shorter line.
     *
     * @param currentTime The current time step in the simulation.
     */
    public void scheduleNextCheck(int currentTime) {
        this.nextCheckTime = currentTime + checkInterval;
    }

    @Override
    public String toString() {
        return "Customer{id=" + id +
                ", arrival=" + arrivalTime +
                ", service=" + serviceTime +
                ", maxWait=" + maxWaitTime +
                ", checkEvery=" + checkInterval +
                ", line=" + lineIndex + "}";
    }
}
