package galileo.samples.net;

import java.io.IOException;

import galileo.event.Event;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

public class UglyEvent implements Event {

    private String str1;
    private String str2;

    public UglyEvent(String str1, String str2) {
        this.str1 = str1;
        this.str2 = str2;
    }

    public String getFirstString() {
        return str1;
    }

    public String getSecondString() {
        return str2;
    }

    @Deserialize
    public UglyEvent(SerializationInputStream in)
    throws IOException {
        str1 = in.readString();
        str2 = in.readString();
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeString(str1);
        out.writeString(str2);
    }

}
