
package galileo.event;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;

import java.io.IOException;

import galileo.net.GalileoMessage;
import galileo.serialization.SerializationOutputStream;

public class EventWrapper {

    public static GalileoMessage wrap(Event e)
    throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        SerializationOutputStream sOut = new SerializationOutputStream(
                new BufferedOutputStream(bOut));

        sOut.writeInt(e.getTypeMap().toInt());
        sOut.writeSerializable(e);
        sOut.close();

        byte[] payload = bOut.toByteArray();
        GalileoMessage msg = new GalileoMessage(payload);
        return msg;
    }
}
