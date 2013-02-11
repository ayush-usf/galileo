
package galileo.dht;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implements a very simple (queue-based) scheduler using a fixed-sized thread
 * pool.
 *
 * @author malensek
 */
public class QueueScheduler implements Scheduler {
    ExecutorService executor;

    public QueueScheduler(int numThreads) {
        executor = Executors.newFixedThreadPool(numThreads);
    }

    @Override
    public void schedule(ProcessingUnit unit) {
        //ClientRequest request = new ClientRequest(client);
        executor.submit(unit);
    }
}
