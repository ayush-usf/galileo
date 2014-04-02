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

package galileo.samples;

import galileo.bmp.GeoavailabilityGrid;

import galileo.bmp.GeoavailabilityQuery;
import galileo.bmp.Visualization;
import galileo.dataset.Block;
import galileo.dataset.Coordinates;
import galileo.dataset.Metadata;
import galileo.dataset.SpatialProperties;
import galileo.dataset.TemporalProperties;
import galileo.dataset.feature.Feature;
import galileo.serialization.Serializer;
import galileo.util.FileNames;
import galileo.util.GeoHash;
import galileo.util.Pair;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ucar.ma2.*;
import ucar.nc2.*;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;
import ucar.nc2.util.DiskCache;
import ucar.unidata.geoloc.LatLonPoint;

/**
 * Demonstrates the use of a {@link GeoavailabilityGrid} in determining whether
 * or not information is available in a particular region.
 *
 * @author malensek
 */
public class GeoavailabilityTest {
    public static void main(String[] args)
    throws Exception {
        String file = args[0];
        System.out.println("Reading NetCDF file: " + file + "...");
        Map<String, Metadata> metaMap = ConvertNetCDF.readFile(file);

        /* Let's construct a Geo Grid for the 9x Geohash region with a precision
         * of 20.  That's 2^20 total grid points. */
        GeoavailabilityGrid gg = new GeoavailabilityGrid("9x", 20);

        for (String str : metaMap.keySet()) {
            if (str.toLowerCase().substring(0, 2).equals("9x")) {
                /* We found a sample for our particular region */
                Coordinates coords
                    = metaMap.get(str).getSpatialProperties().getCoordinates();
                gg.addPoint(coords);
                System.out.println("Adding point: " + coords);
            }
        }

        /* What does this grid look like? */
        BufferedImage b
            = Visualization.drawGeoavailabilityGrid(gg, Color.BLACK);
        Visualization.imageToFile(b, "NetCDF-GeoavailabilityGrid.gif");

        List<Coordinates> poly = new ArrayList<>();
        poly.add(new Coordinates(43.79f, -105.00f));
        poly.add(new Coordinates(40.96f, -103.50f));
        poly.add(new Coordinates(39.98f, -108.47f));
        GeoavailabilityQuery gq = new GeoavailabilityQuery(poly);

        /* Does this polygon overlap the grid? */
        boolean intersects = gg.intersects(gq);
        System.out.println("Polygon intersects the grid: " + intersects);

        /* Alternatively, a little polygon that shouldn't intersect: */
        poly = new ArrayList<>();
        poly.add(new Coordinates(43.79f, -105.39f));
        //poly.add(new Coordinates(43.96f, -105.50f));
        //poly.add(new Coordinates(43.78f, -105.47f));
        gq = new GeoavailabilityQuery(poly);

        intersects = gg.intersects(gq);
        System.out.println("Polygon intersects the grid: " + intersects);
    }
}
