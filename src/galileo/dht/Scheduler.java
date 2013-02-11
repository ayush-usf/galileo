
package galileo.dht;

/**
 * Defines required functionality for creating Galileo StorageNode schedulers.
 *
 * @author malensek
 */
public interface Scheduler {

    public void schedule(ProcessingUnit unit);
}
