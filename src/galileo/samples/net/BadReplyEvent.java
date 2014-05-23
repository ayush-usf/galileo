package galileo.samples.net;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import galileo.event.Event;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

public class BadReplyEvent implements Event {

    private List<String> stringList = new ArrayList<>();

    public BadReplyEvent(String... strings) {
        for (String string : strings) {
            stringList.add(string);
        }
    }

    @Deserialize
    public BadReplyEvent(SerializationInputStream in)
    throws IOException {
        int num = in.readInt();
        for (int i = 0; i < num; ++i) {
            String str = in.readString();
            stringList.add(str);
        }
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeInt(stringList.size());
        for (String string : stringList) {
            out.writeString(string);
        }
    }

}
