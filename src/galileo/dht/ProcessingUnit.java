
package galileo.dht;

/**
 * Represents a single unit of processing at a Galileo StorageNode.  Client
 * requests, gossip messages, and control messages are all scheduled and
 * executed as ProcessingUnits in the system.
 *
 * @author malensek
 */
public interface ProcessingUnit extends Runnable {

}
