/*
Copyright (c) 2013, Colorado State University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
*/

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
