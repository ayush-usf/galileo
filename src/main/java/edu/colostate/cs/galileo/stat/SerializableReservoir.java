package edu.colostate.cs.galileo.stat;

import edu.colostate.cs.galileo.serialization.ByteSerializable;
import edu.colostate.cs.galileo.serialization.SerializationInputStream;
import edu.colostate.cs.galileo.serialization.SerializationOutputStream;

public class SerializableReservoir<T extends ByteSerializable>
extends Reservoir<T> implements ByteSerializable {

    @Deserialize
    public SerializableReservoir(SerializationInputStream in) {
        super(0);
    }

    @Override
    public void serialize(SerializationOutputStream out) {

    }
}
