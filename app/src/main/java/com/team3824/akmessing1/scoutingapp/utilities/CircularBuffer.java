package com.team3824.akmessing1.scoutingapp.utilities;

import java.util.NoSuchElementException;

/**
 * Thread safe fixed size circular buffer implementation. Backed by an array.
 *
 * @author brad
 */
public class CircularBuffer {
    // internal data storage
    private String[] data;
    // indices for inserting and removing from queue
    private int front = 0;
    private int insertLocation = 0;
    // number of elements in queue
    private int size = 0;
    /**
     * Creates a circular buffer with the specified size.
     *
     * @param bufferSize
     *      - the maximum size of the buffer
     */
    public CircularBuffer(int bufferSize) {
        data = new String[bufferSize];
    }
    /**
     * Inserts an item at the end of the queue. If the queue is full, the oldest
     * value will be removed and head of the queue will become the second oldest
     * value.
     *
     * @param item
     *      - the item to be inserted
     */
    public void insert(String item) {
        data[insertLocation] = item;
        insertLocation = (insertLocation + 1) % data.length;
        /**
         * If the queue is full, this means we just overwrote the front of the
         * queue. So increment the front location.
         */
        if (size == data.length) {
            front = (front + 1) % data.length;
        } else {
            size++;
        }
    }
    /**
     * Returns the number of elements in the buffer
     *
     * @return int - the number of elements inside this buffer
     */
    public int size() {
        return size;
    }
    /**
     * Returns the head element of the queue.
     *
     * @return T
     */
    public String removeFront() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        String retValue = data[front];
        front = (front + 1) % data.length;
        size--;
        return retValue;
    }
    /**
     * Returns the head of the queue but does not remove it.
     *
     * @return
     */
    public String peekFront() {
        if (size == 0) {
            return null;
        } else {
            return data[front];
        }
    }
    /**
     * Returns the last element of the queue but does not remove it.
     *
     * @return T - the most recently added value
     */
    public String peekLast() {
        if (size == 0) {
            return null;
        } else {
            int lastElement = insertLocation - 1;
            if (lastElement < 0) {
                lastElement = data.length - 1;
            }
            return data[lastElement];
        }
    }

    public String toString()
    {
        String output = "";
        for(int i = 0; i < size; i++)
        {
            output += data[(front + i) % data.length];
        }

        return output;
    }
}