
package galileo.test;

import galileo.dataset.BlockMetadata;

public interface FormatPlugin {
    public BlockMetadata metadataFromFile(String file);
}
