
package galileo.dataset;

import java.util.Date;

import galileo.serialization.ByteSerializable;

public interface TemporalRange extends ByteSerializable {

    public Date getLowerBound();

    public Date getUpperBound();
}
