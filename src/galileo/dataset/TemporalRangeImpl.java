
package galileo.dataset;

import java.io.IOException;
import java.util.Date;
import java.text.DateFormat;
import java.text.ParseException;

import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

public class TemporalRangeImpl implements TemporalRange {

    private Date start;
    private Date end;

    /**
     * Creates a simple temporal range from a start and end time pair,
     * represented in miliseconds from the Unix epoch.
     *
     * @param start Start of the temporal range
     * @param end   End of the temporal range
     */
    public TemporalRangeImpl(long start, long end)
    throws IllegalArgumentException {
        this.start = new Date(start);
        this.end   = new Date(end);

        verifyRange();
    }

    /**
     * Creates a simple temporal range from a start and end time pair,
     * represented as Strings.
     *
     * @param start Start of the temporal range
     * @param end   End of the temporal range
     */
    public TemporalRangeImpl(String start, String end)
    throws ParseException, IllegalArgumentException {
        DateFormat formatter = DateFormat.getDateInstance(DateFormat.LONG);
        this.start = formatter.parse(start);
        this.end   = formatter.parse(end);

        verifyRange();
    }

    /**
     * Ensure the start time comes before the end time for this Temporal range.
     */
    private void verifyRange() throws IllegalArgumentException {
        if (end.getTime() - start.getTime() < 0) {
            throw new IllegalArgumentException("Upper bound of temporal range" +
                " must be larger than the lower bound.");
        }
    }

    /**
     * Retrieves the upper bound of this temporal range.
     */
    @Override
    public Date getUpperBound() {
        return end;
    }

    /**
     * Retrieves the lower bound of this temporal range.
     */
    @Override
    public Date getLowerBound() {
        return start;
    }

    public TemporalRangeImpl(SerializationInputStream in)
    throws IOException {
        start = new Date(in.readLong());
        end = new Date(in.readLong());
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        out.writeLong(start.getTime());
        out.writeLong(end.getTime());
    }
}
