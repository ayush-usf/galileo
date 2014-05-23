package galileo.samples.net;

import java.io.IOException;

import java.util.Random;

import galileo.event.Event;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

public class BadEvent implements Event {

    private int myNumber;

    public BadEvent() {
        Random random = new Random();
        myNumber = random.nextInt();
    }

    public int getBadness() {
        return myNumber;
    }

    @Deserialize
    public BadEvent(SerializationInputStream in)
    throws IOException {
        myNumber = in.readInt();
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeInt(myNumber);
    }

}
