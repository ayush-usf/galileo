
package galileo.bmp;

import galileo.dataset.SpatialRange;
import galileo.util.GeoHash;

public class GeoavailabilityGrid {

    int width, height;

    private SpatialRange baseRange;
    private Bitmap bmp;

    public GeoavailabilityGrid(String baseGeohash, int precision) {
        this.baseRange = GeoHash.decodeHash(baseGeohash);

        /*
         * height, width calculated like so:
         * width = 2^(floor(precision / 2))
         * height = 2^(ceil(precision / 2))
         */
        int w = precision / 2;
        int h = precision / 2;
        if (precision % 2 != 0) {
            h += 1;
        }

        this.width = (1 << w); /* = 2^w */
        this.height = (1 << h); /* = 2^h */
    }

    /**
     * Reports whether or not the supplied {@link GeoavailabilityQuery}
     * instance intersects with the bits set in this geoavailability grid.  This
     * operation can be much faster than performing a full query.
     *
     * @param query The query geometry to test for intersection.
     *
     * @return true if the supplied {@link GeoavailabilityQuery} intersects with
     * the data in the geoavailability grid.
     */
    public boolean intersects(GeoavailabilityQuery query) {

        return false;
    }

    /**
     * Queries the geoavailability grid, which involves performing a logical AND
     * operation and reporting the resulting Bitmap.
     *
     * @param query The query geometry to evaluate against the geoavailability
     * grid.
     *
     * @return Bitmap with matching bits set.
     */
    public Bitmap query(GeoavailabilityQuery query)
    throws BitmapException {
        Bitmap queryBits = query.toBitmap();
        this.bmp.and(queryBits);

        return null;
    }
}
