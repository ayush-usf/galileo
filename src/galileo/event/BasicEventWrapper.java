
package galileo.event;

import galileo.net.GalileoMessage;

import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BasicEventWrapper implements EventWrapper {

    @Override
    public GalileoMessage wrap(Event e)
    throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        SerializationOutputStream sOut = new SerializationOutputStream(
                new BufferedOutputStream(bOut));

        //sOut.writeInt(e.getTypeMap().toInt());
        sOut.writeSerializable(e);
        sOut.close();

        byte[] payload = bOut.toByteArray();
        GalileoMessage msg = new GalileoMessage(payload);
        return msg;
    }

    @Override
    public Event unwrap(GalileoMessage msg) {
        ByteArrayInputStream byteIn
            = new ByteArrayInputStream(msg.getPayload());
        BufferedInputStream buffIn = new BufferedInputStream(byteIn);
        SerializationInputStream sIn = new SerializationInputStream(buffIn);

        return null;
    }
}
