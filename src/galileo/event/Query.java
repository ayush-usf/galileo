
package galileo.event;

import java.io.IOException;

import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

/**
 * Encapsulates query information submitted by clients to be processed by
 * StorageNodes.
 */
public class Query implements GalileoEvent {
    private String replySynopsis;
    private String query;

    public Query(String query, String replySynopsis) {
        this.query      = query;
        this.replySynopsis = replySynopsis;
    }

    /**
     * Returns the query String this <code>Query</code> represents.
     *
     * @return query String
     */
    public String getQueryString() {
        return query;
    }

    @Override
    public EventType getType() {
        return EventType.QUERY;
    }

    /**
     * (Re)construct a query from a SerializationStream.
     *
     * @param SerializationInputStream stream to deserialize from.
     */
    public Query(SerializationInputStream in)
    throws IOException {
        replySynopsis = in.readString();
        query = in.readString();
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeString(replySynopsis);
        out.writeString(query);
    }
}
