import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description: Class implementing a store customer. 
 * Class holds a unique ID for each customer. The ID gets generated automatically
 * by a static AtomicInteger object, in the constructor.
 *
 * @author Nicholas Racette
 * @contact: Nick.Racette@century.edu
 * @since: 11/16/2025
 *
 * Course: CSCI 2082-70
 * Institution: Century College
 * Instructor: Mathew Nyamgawa
 */
public class Customer {

    // PROPERTIES
    private String id;
    
    // Static atomic integer to generate unique IDs works with multiple threads
    private static AtomicInteger idGenerator = new AtomicInteger(0);

    // CONSTRUCTOR
    
    /**
     * Constructs a new Customer.
     * Automatically assigns a new, unique ID in a thread-safe way.
     */
    public Customer() {
        // Increment the atomic generator and format it as a String
        this.id = String.format("%03d", idGenerator.incrementAndGet());
    }

    /**
     * Gets the customer's unique ID.
     * 
     * @return The customer's ID string.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns a string id of the Customer.
     * 
     * @return A string with the customer's ID.
     */
    @Override
    public String toString() {
        return "Customer{id='" + id + "'}";
    }
}