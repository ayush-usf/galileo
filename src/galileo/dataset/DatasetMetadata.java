
package galileo.dataset;

import java.util.Iterator;

public interface DatasetMetadata {

    public long getNumberOfDataBlocks();

    public Iterator<String> getListOfNodeIdentifiers();

    public long getSizeOfDataset();
}
