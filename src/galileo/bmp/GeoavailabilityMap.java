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

public class GeoavailabilityMap<T> extends GeoavailabilityGrid {

    private Map<Integer, List<T>> points;

    public GeoavailabilityMap(String baseGeohash, int precision) {
        super(baseGeohash, precision);
        points = new HashMap<>();
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
