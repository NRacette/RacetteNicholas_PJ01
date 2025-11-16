/**
 * Description: A queue of Customer objects using an array. This class provides basic queue operations.
 * enqueue() will add an item and notify any waiting threads.
 * dequeue() will wait() if the queue is empty, until an item is added by another thread.
 *
 * @author Nicholas Racette
 * @contact: Nick.Racette@century.edu
 * @since: 11/16/2025
 *
 * Course: CSCI 2082-70
 * Institution: Century College
 * Instructor: Mathew Nyamgawa
 */
public class CustomerArrayQueue {
    
    // PROPERTIES
    private Customer[] data; // Holds Customer objects
    private int front;
    private int rear;
    private int manyItems;

    // CONSTRUCTORS
    
    /**
     * Default constructor.
     * Initializes the queue with an initial capacity of 10.
     */
    public CustomerArrayQueue() {
        this(10); // Calls the other constructor
    }

    /**
     * Constructor with specified capacity.
     *
     * @param initialCapacity The initial size of the array.
     */
    public CustomerArrayQueue(int initialCapacity) {
        if (initialCapacity <= 0) {
            initialCapacity = 10;
        }
        this.data = new Customer[initialCapacity];
        this.manyItems = 0;
    }

    // MEMBER METHODS

    /**
     * Adds a new item to the rear of the queue (thread-safe).
     * After adding, it notifies all waiting threads (e.g., a cashier
     * waiting for a customer).
     *
     * @param item The Customer to be added to the queue.
     */
    public synchronized void enqueue(Customer item) {
        // Check if the array is full and resize if necessary
        if (this.manyItems == data.length) {
            increaseCapacity((this.manyItems * 2 ) + 1); // Double capacity
        }

        // Check whether the array is empty
        if (this.manyItems == 0) {
            this.front = 0;
            this.rear = 0;
        } else {
            // Move rear to the next index
            this.rear = nextIndex(this.rear);
        }

        // Add the new customer
        this.data[this.rear] = item;
        this.manyItems++;

        //Start up any threads that are wait() on this queue
        this.notifyAll();
    }

    /**
     * Removes and returns the item from the front of the queue.
     * If the queue is empty, this thread will wait() until another thread enqueue().
     * Synchronized for working with threads.
     *
     * @return Customer at the front of the queue.
     * @throws InterruptedException If the thread is interrupted while waiting.
     */
    public synchronized Customer dequeue() throws InterruptedException {
        // Check if the queue is empty
        while (this.manyItems == 0) {

            this.wait();
            // When woken up, the loop re-checks if manyItems > 0
        }

        // Get the item to return
        Customer item = this.data[front];
        this.data[front] = null; // Help garbage collector

        // Point front to the next index (wraps around)
        this.front = nextIndex(this.front);
        this.manyItems--;
        return item;
    }

    /**
     * Returns the number of items in the queue.
     * Synchronized for working with threads.
     *
     * @return The count of items.
     */
    public synchronized int size() {
        return this.manyItems;
    }

    /**
     * Moves the next index in the array queue.
     * 
     * @param index The current index.
     * @return The next index.
     */
    public int nextIndex(int index) {
        index = index + 1;
        // Move to front if we reach the end of the array
        if (index == this.data.length) {
            return 0; 
        } 
        else {
            return index;
        }
    }

    /**
     * Resizes the array to a new, larger capacity.
     * Copies existing items to the new array.
     * 
     * @param minCapacity The minimum required capacity.
     */
    public void increaseCapacity(int minCapacity) {
        Customer[] biggerArray;

        if (this.data.length >= minCapacity) {
            return;
        } 
        
        biggerArray = new Customer[minCapacity];

        if (this.front <= this.rear) {
            System.arraycopy(this.data, this.front, biggerArray, 0, this.manyItems);
        } 
        else {
            int itemsFromFront = this.data.length - this.front;
            System.arraycopy(this.data, this.front, biggerArray, 0, itemsFromFront);
            
            int itemsFromStart = this.rear + 1;
            System.arraycopy(this.data, 0, biggerArray, itemsFromFront, itemsFromStart);
        }
        
        this.data = biggerArray;
        this.front = 0;
        this.rear = this.manyItems - 1;
    }
}