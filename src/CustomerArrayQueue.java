/**
 * Description: Implements a **thread-safe, blocking** circular queue 
 * of Customer objects using a dynamic array.
 * This class provides basic queue operations (enqueue, dequeue).
 * - enqueue() will add an item and notify any waiting threads.
 * - dequeue() will wait() if the queue is empty, until an item
 * is added by another thread.
 *
 * @author Nicholas Racette
 * @contact: Nick.Racette@century.edu
 * @since: 11/15/2025
 *
 * Course: CSCI 2082-70
 * Institution: Century College
 * Instructor: Mathew Nyamgawa
 */
public class CustomerArrayQueue {
    
    // PROPERTIES
    private Customer[] data; // Holds Customer objects, not ints
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
     * @param initialCapacity The initial size of the internal array.
     */
    public CustomerArrayQueue(int initialCapacity) {
        if (initialCapacity <= 0) {
            initialCapacity = 10;
        }
        this.data = new Customer[initialCapacity];
        this.manyItems = 0;
    }

    // === CORE THREAD-SAFE METHODS ===

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
            // Use nextIndex to wrap around if needed
            this.rear = nextIndex(this.rear);
        }

        // Add the new item
        this.data[this.rear] = item;
        this.manyItems++;

        // **IMPORTANT:** Wake up any threads that are wait()ing on this queue
        this.notifyAll();
    }

    /**
     * Removes and returns the item from the front of the queue (thread-safe).
     * **This is a blocking method.** If the queue is empty, this thread
     * will wait() until another thread calls enqueue().
     *
     * @return The Customer at the front of the queue.
     * @throws InterruptedException if the thread is interrupted while waiting.
     */
    public synchronized Customer dequeue() throws InterruptedException {
        // **IMPORTANT:** Use a while loop to wait.
        // If the queue is empty, release the lock and wait.
        while (this.manyItems == 0) {
            // System.out.println("Queue is empty, thread is waiting...");
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
     * Returns the number of items in the queue (thread-safe).
     *
     * @return The count of items.
     */
    public synchronized int size() {
        return this.manyItems;
    }

    // === PRIVATE HELPER METHODS ===

    /**
     * Calculates the next index in the circular array.
     * (Doesn't need to be synchronized as it's only called by
     * synchronized methods).
     */
    private int nextIndex(int index) {
        index = index + 1;
        if (index == this.data.length) {
            return 0; // Wrap around to the start
        } 
        else {
            return index;
        }
    }

    /**
     * Resizes the internal array to a new, larger capacity.
     * (Doesn't need to be synchronized as it's only called by
     * synchronized methods).
     */
    private void increaseCapacity(int minCapacity) {
        Customer[] biggerArray;

        if (this.data.length >= minCapacity) {
            return; // No change needed
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