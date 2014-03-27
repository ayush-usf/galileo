/*
Copyright (c) 2014, Colorado State University
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

package galileo.bmp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import galileo.dataset.Coordinates;
import galileo.dataset.Point;
import galileo.dataset.SpatialRange;
import galileo.util.GeoHash;

public class GeoavailabilityGrid<T> {

    private static final Logger logger = Logger.getLogger("galileo");

    private int width, height;

    private Bitmap<EWAHBitmap> bmp;
    private Map<Integer, List<T>> points;

    private SpatialRange baseRange;
    private float xDegreesPerPixel;
    private float yDegreesPerPixel;

    public GeoavailabilityGrid(String baseGeohash, int precision) {

        this.points = new HashMap<Integer, List<T>>();

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

        /* Determine the number of degrees in the x and y directions for the
         * base spatial range this geoavailability grid represents */
        float xDegrees = baseRange.getUpperBoundForLongitude()
            - baseRange.getLowerBoundForLongitude();
        float yDegrees = baseRange.getLowerBoundForLatitude()
            - baseRange.getUpperBoundForLatitude();

        /* Determine the number of degrees represented by each grid pixel */
        xDegreesPerPixel = xDegrees / (float) this.width;
        yDegreesPerPixel = yDegrees / (float) this.width;

        logger.log(Level.INFO, "Created geoavailability grid: "
                + "geohash={0}, precision={1}, width={2}, height={3}, "
                + "baseRange={6}, xDegreesPerPixel={4}, yDegreesPerPixel={5}",
                new Object[] { baseGeohash, precision, width, height,
                    xDegreesPerPixel, yDegreesPerPixel, baseRange});
    }

    private Point<Integer> coordinatesToXY(Coordinates coords) {

        /* Assuming (x, y) coordinates for the geoavailability grids, latitude
         * will decrease as y increases, and longitude will increase as x
         * increases. This is reflected in how we compute the differences
         * between the base points and the coordinates in question. */
        float xDiff = coords.getLongitude()
            - baseRange.getLowerBoundForLongitude();

        float yDiff = baseRange.getLowerBoundForLatitude()
            - coords.getLatitude();

        int x = (int) (xDiff / xDegreesPerPixel);
        int y = (int) (yDiff / yDegreesPerPixel);

        return new Point<>(x, y);
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
    public boolean intersects(GeoavailabilityQuery query)
    throws BitmapException {
        Bitmap<EWAHBitmap> queryBitmap = query.toBitmap();
        return this.bmp.intersects(queryBitmap);
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
    public void query(GeoavailabilityQuery query)
    throws BitmapException {
        //this.bmp.and(queryBits);

        return;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public SpatialRange getBaseRange() {
        return new SpatialRange(baseRange);
    }
}
