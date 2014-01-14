package galileo.util;

/**
 * Implements a mutable counter that can be passed among methods/threads/etc.
 *
 * @author malensek
 */
public class Counter {
    private int count = 0;

    /**
     * Creates a Counter that begins counting from zero.
     */
    public Counter() { }

    /**
     * Creates a Counter with a particular starting count.
     */
    public Counter(int start) {
        this.count = start;
    }

    /**
     * Increments the Counter and returns the resulting integer.  Similar to
     * performing something like ++counter;
     */
    public int increment() {
        count += 1;
        return count;
    }

    /**
     * Returns the current count, and then increments the Counter.  Similar to
     * performing something like counter++.
     */
    public int postIncrement() {
        int temp = count;
        increment();
        return temp;
    }

    /**
     * Decrements the Counter and returns the resulting integer.  Similar to
     * performing something like --counter;
     */
    public int decrement() {
        count -= 1;
        return count;
    }

    /**
     * Returns the current count, and then decrements the Counter.  Similar to
     * performing something like counter--.
     */
    public int postDecrement() {
        int temp = count;
        decrement();
        return temp;
    }

    public int getCount() {
        return count;
    }

    public String toString() {
        return "" + count;
    }
}
