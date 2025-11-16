import java.util.NoSuchElementException;
/**
 * Description: Implements a circular queue of integers using a dynamic array.
 * This class provides basic queue operations (enqueue, dequeue, peek)
 * and automatically handles resizing when full.
 *
 * @author Nicholas Racette
 * @contact: Nick.Racette@century.edu
 * @since: 11/15/2025
 *
 * Course: CSCI 2082-70
 * Institution: Century College
 * Instructor: Mathew Nyamgawa
 */
public class IntArrayQueue {
    
    // PROPERTIES
    private int[] data;  // array of integers
    private int front;   // index of front
    private int rear;   // index of rear
    private int manyItems;   // number of elements

    /**
     * Default constructor.
     * Initializes the queue with an initial capacity of 10.
     */
    public IntArrayQueue() {
        final int INITIAL_CAPACITY = 10;
        this.data = new int[INITIAL_CAPACITY];
        this.manyItems = 0;
    }

    /**
     * Constructor with specified capacity.
     * Initializes the queue with the given capacity.
     *
     * @param initialCapacity The initial size of the internal array.
     */
    public IntArrayQueue(int INITIAL_CAPACITY) {
        this.data = new int[INITIAL_CAPACITY];
        this.manyItems = 0;
    }

    // MEMBER METHODS
    /**
     * Adds a new item to the rear of the queue.
     * If the array is full, it calls increaseCapacity() to resize.
     *
     * @param item The integer to be added to the queue.
     */
    public void enqueue(int item) {
        // Check if the Array is full
        if (this.manyItems == data.length) {
            increaseCapacity((this.manyItems * 2 ) + 1);
        }
        //Check whether the array is empty
        if (this.manyItems == 0) {
            this.front = 0;
            this.rear = 0;
        } else {
            this.rear = nextIndex(this.rear);
        }
        this.data[this.rear] = item;
        this.manyItems++;
    }

    /**
     * Removes and returns the item from the front of the queue.
     *
     * @return The integer at the front of the queue.
     * @throws NoSuchElementException If the queue is empty.
     */
    public int dequeue() {
        int item;
        // Check if the Array is empty
        if (this.manyItems == 0) {
            throw new NoSuchElementException("Queue underflow");
        }
        // Get the index to remove item
        item = this.data[front];

        // Point front to next index
        this.front = nextIndex(this.front);
        this.manyItems--;
        return item;
        
    }
    /**
     * Calculates the next index in the circular array.
     * Wraps to 0 if the index reaches the end of the array.
     *
     * @param index The current index (e.g., front or rear).
     * @return The next valid index in the array.
     */
    public int nextIndex(int index) {
        index = index + 1;
        if (index == this.data.length) {
            return 0;
        } 
        else {
            return index;
        }
    }

    /**
     * Gets the current index of the front of the queue.
     *
     * @return The array index of the front item.
     */
    public int getFront() {
        return this.front;
    }

    /**
     * Gets the current index of the rear of the queue.
     *
     * @return The array index of the rear item.
     */
    public int getRear() {
        return this.rear;
    }

    /**
     * Checks if the queue is empty.
     *
     * @return true if the queue has no items, false otherwise.
     */
    public boolean isEmpty() {
        return this.manyItems == 0;
    }

    /**
     * Returns the number of items in the queue.
     *
     * @return The count of items.
     */
    public int size() {
        return this.manyItems;
    }

    /**
     * Returns the total capacity of the internal array.
     *
     * @return The length of the data array.
     */
    public int capacity() {
        return this.data.length;
    }

    /**
     * Resizes the internal array to a new, larger capacity.
     * This method correctly handles the "wrap-around" case in a circular
     * queue by copying the data into a new, linear array.
     *
     * @param minCapacity The minimum new capacity required.
     * @return The new capacity (length of the data array).
     */
    public int increaseCapacity(int minCapacity) {
        int[] biggerArray;

        if (this.data.length >= minCapacity) {
            return this.data.length; // No change needed
        } else if (this.front <= this.rear) {
            // Create the biggerArray and copy from data[front] to data[rear]
            biggerArray = new int[minCapacity];
            System.arraycopy(this.data, this.front, biggerArray, this.front, this.manyItems);
            this.data = biggerArray;
        } else {
            // This is when this.rear is towards the beginning of the this.data array
            // this.front is toward the back of the this.data array.

            // Create the biggerArray
            biggerArray = new int[minCapacity];
            // Copy from this.data[front] to end of this.data array
            System.arraycopy(this.data, this.front, biggerArray, 0, (this.data.length - this.front));
            
            // Copy from beginning of this.data array to this.data[rear]
            System.arraycopy(this.data, 0, biggerArray, (this.data.length - this.front), (this.rear + 1));
            this.data = biggerArray;
            // After copying, reset front and rear
            this.front = 0;
            this.rear = this.manyItems - 1;
        }
        return this.data.length;
    }

    /**
     * Generates a string representation of the queue.
     *
     * @return A string showing the array's raw state.
     */
    public String toString() {
        String result = "";
        if(this.front <= this.rear) {
            result += "Enter Array as is:\n {";
            for(int i = 0; i < this.data.length; i++) {
                result += this.data[i] + ", ";
            }
            result += "}\n";
        } else {
        // Print array as repesented in queue. Show when rear has circled around.
            result += "\nDisplay Array as is:\n {";
            for(int i = 0; i < this.data.length; i++) 
                result += this.data[i] + ", ";
            result += "}\n";

        // Print starting from this.front to this.rear
            result += "Display Array items only:\n {";
                for(int i = this.front; i <= this.rear; i++) 
                    result += this.data[i] + ", ";
                
                    for(int i = 0; i <= this.rear; i++) 
                        result += this.data[i] + ", ";

            result += "}\n";
        }
        return result;
    }
}


