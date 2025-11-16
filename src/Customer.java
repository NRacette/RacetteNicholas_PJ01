import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 * Represents a single grocery store customer. This is a simple data
 * class that holds a unique ID. The ID is generated automatically
 * by a static, thread-safe counter in the constructor.
 *
 * @author Nicholas Racette
 * @contact: Nick.Racette@century.edu
 * @since: 11/15/2025
 *
 * Course: CSCI 2082-70
 * Institution: Century College
 * Instructor: Mathew Nyamgawa
 */
public class Customer {

    // === PROPERTIES ===
    private String id;
    
    /** Thread-safe static counter to ensure unique customer IDs */
    private static AtomicInteger idGenerator = new AtomicInteger(0);

    // === CONSTRUCTOR ===
    
    /**
     * Constructs a new Customer.
     * Automatically assigns a new, unique ID in a thread-safe way.
     */
    public Customer() {
        // Increment the atomic generator and format it as a String
        this.id = String.format("C%04d", idGenerator.incrementAndGet());
    }

    // === GETTERS ===

    /**
     * Gets the customer's unique ID.
     * @return The customer's ID string.
     */
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Customer{id='" + id + "'}";
    }
}