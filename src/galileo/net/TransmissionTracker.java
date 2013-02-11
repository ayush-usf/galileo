
package galileo.net;

import java.nio.ByteBuffer;

import java.nio.channels.SelectionKey;

import java.util.Queue;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Tracks transmission processing operations; helps convert TCP streams into
 * individual messages that can be consumed by the system or client
 * applications.
 *
 * @author malensek
 */
public class TransmissionTracker {

    /** Contains a list of pending write operations for this client. */
    private Queue<ByteBuffer> pendingWrites = new LinkedBlockingQueue<>(100);

    /** Read pointer for the message size prefix */
    public int prefixPointer;

    /** Array to store payload size prefix information */
    public byte[] prefix = new byte[4];

    /** Read pointer for the message payload */
    public int readPointer;

    /** Array to store received payload data */
    public byte[] payload;

    /** The size of the complete payload in bytes */
    public int expectedBytes;

    public TransmissionTracker() { }

    /**
     * Allocates a buffer for the incoming payload once the message size prefix
     * has been read.
     */
    public void allocatePayload() {
        payload = new byte[expectedBytes];
    }

    /**
     * Restores the TransmissionTracker to its original state, ready to process
     * another message from a stream.
     */
    public void resetCounters() {
        prefixPointer = 0;
        readPointer = 0;
        expectedBytes = 0;
    }

    public Queue<ByteBuffer> getPendingWriteQueue() {
        return pendingWrites;
    }

    /**
     * Retrieves a TransmissionTracker attachment from a SelectionKey.
     */
    public static TransmissionTracker fromKey(SelectionKey key) {
        return (TransmissionTracker) key.attachment();
    }
}
