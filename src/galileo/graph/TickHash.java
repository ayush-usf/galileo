
package galileo.graph;

import galileo.dataset.feature.Feature;

import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * Represents the buckets described by "Tick Marks" in the system.  For example,
 * a TickHash with values 0, 10, 20, and 30 would output "30" for an input of
 * 33, "0" for an input of 5, etc.  It allows incoming samples to be placed into
 * coarser-grained buckets.  Additionally, the first and last ticks stretch off
 * to infinity; Feature-specific constraints on the tick marks should be managed
 * by classes using TickHash.
 *
 * @author malensek
 */
public class TickHash {

    private NavigableSet<Feature> tickSet = new TreeSet<>();
    private Feature low;
    private Feature high;

    public TickHash(Feature... features) {
        for (Feature f : features) {
            addTick(f);
        }
    }

    public void addTick(Feature feature) {
        if (low == null || feature.less(low)) {
            low = feature;
        }

        if (high == null || feature.greater(high)) {
            high = feature;
        }

        tickSet.add(feature);
    }

    public NavigableSet<Feature> getTicks() {
        return tickSet;
    }

    public Feature getBucket(Feature feature) {
        if (feature.greater(high)) {
            return high;
        }

        if (feature.less(low)) {
            return low;
        }

        return tickSet.floor(feature);
    }

    @Override
    public String toString() {
        String str = "";
        for (Feature f : tickSet) {
            str += f.dataToString() + " - ";
        }
        return str.substring(0, str.length() - 3);
    }
}
