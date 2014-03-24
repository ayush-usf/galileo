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

import galileo.dataset.SpatialRange;
import galileo.util.GeoHash;

public class GeoavailabilityGrid {

    int width, height;

    private SpatialRange baseRange;
    private Bitmap<EWAHBitmap> bmp;

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

        System.out.println(baseRange);
        System.out.println(width);
        System.out.println(height);
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
    public void query(GeoavailabilityQuery query)
    throws BitmapException {
        //this.bmp.and(queryBits);

        return;
    }
}
