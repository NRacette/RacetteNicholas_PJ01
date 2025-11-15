import java.util.NoSuchElementException;

public class IntArrayQueue {
    
    // PROPERTIES
    private int[] data;
    private int front;   // index of front
    private int rear;

    private int manyItems;   // number of elements

    // CONSTRUCTOR
    // Default constructor
    public IntArrayQueue() {
        final int INITIAL_CAPACITY = 10;
        this.data = new int[INITIAL_CAPACITY];
        this.manyItems = 0;
    }

    public IntArrayQueue(int INITIAL_CAPACITY) {
        this.data = new int[INITIAL_CAPACITY];
        this.manyItems = 0;
    }

    // MEMBER METHODS
    //@Override
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

    public int nextIndex(int index) {
        index = index + 1;
        if (index == this.data.length) {
            return 0;
        } 
        else {
            return index;
        }
    }

    public int getFront() {
        return this.front;
    }

    public int getRear() {
        return this.rear;
    }

    public boolean isEmpty() {
        return this.manyItems == 0;
    }

    public int size() {
        return this.manyItems;
    }

    public int capacity() {
        return this.data.length;
    }

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

        // Pring starting from this.frong to this.rear
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


