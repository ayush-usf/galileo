
package galileo.dataset;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;

import galileo.serialization.ByteSerializable;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

public class MetaArray implements Iterable<BlockMetadata>, ByteSerializable {
    private ArrayList<BlockMetadata> metas =
        new ArrayList<BlockMetadata>();

    public MetaArray() { }

    public void add(BlockMetadata meta) {
        metas.add(meta);
    }

    public void addAll(MetaArray array) {
        metas.addAll(array.metas);
    }

    public int size() {
        return metas.size();
    }

    @Override
    public Iterator<BlockMetadata> iterator() {
        return metas.iterator();
    }

    public MetaArray(SerializationInputStream in)
    throws IOException {
        int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            BlockMetadata meta = new BlockMetadataImpl(in);
            metas.add(meta);
        }
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeInt(metas.size());
        for (BlockMetadata meta : metas) {
            out.writeSerializable(meta);
        }
    }
}
