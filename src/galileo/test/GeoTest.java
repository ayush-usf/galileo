
package galileo.test;

import galileo.dataset.SpatialRange;
import galileo.util.GeoHash;

public class GeoTest {
    public static void main(String[] args) {
        SpatialRange srange = GeoHash.decodeHash("9xjqb4pnb1w6c");
        System.out.println(srange.getLowerBoundForLatitude());
        System.out.println(srange.getUpperBoundForLatitude());
        System.out.println(srange.getLowerBoundForLongitude());
        System.out.println(srange.getLowerBoundForLongitude());

        String hash = GeoHash.encode(srange, 12);
        System.out.println(hash);
    }
}
