
package galileo.dataset;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;

import galileo.serialization.ByteSerializable;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

public class BlockArray implements ByteSerializable, Iterable<FileBlock> {
    private ArrayList<FileBlock> blocks =
        new ArrayList<FileBlock>();

    public BlockArray() {

    }

    public void add(FileBlock block) {
        blocks.add(block);
    }

    public int size() {
        return blocks.size();
    }

    @Override
    public Iterator<FileBlock> iterator() {
        return blocks.iterator();
    }

    public BlockArray(SerializationInputStream in)
    throws IOException {
        int numBlocks = in.readInt();
        for (int i = 0; i < numBlocks; ++i) {
            FileBlock block = new FileBlock(in);
            blocks.add(block);
        }
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeInt(blocks.size());
        for (FileBlock block : blocks) {
            block.serialize(out);
        }
    }
}
