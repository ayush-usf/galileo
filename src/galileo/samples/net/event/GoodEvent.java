package galileo.samples.net.event;

import java.io.IOException;

import galileo.event.Event;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

public class GoodEvent implements Event {

    private String myData;

    public GoodEvent() {
        myData = "What a good event I am!";
    }

    public String getData() {
        return myData;
    }

    @Deserialize
    public GoodEvent(SerializationInputStream in)
    throws IOException {
        myData = in.readString();
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeString(myData);
    }

}
