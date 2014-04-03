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

import galileo.bmp.Bitmap;

import galileo.bmp.BitmapVisualization;
import galileo.bmp.GeoavailabilityGrid;
import galileo.bmp.GeoavailabilityMap;
import galileo.bmp.GeoavailabilityQuery;
import galileo.bmp.QueryTransform;
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

        System.out.println("Adding points...");
        for (String str : metaMap.keySet()) {
            if (str.toLowerCase().substring(0, 2).equals("9x")) {
                /* We found a sample for our particular region */
                Coordinates coords
                    = metaMap.get(str).getSpatialProperties().getCoordinates();
                gg.addPoint(coords);
            }
        }

        /* What does this grid look like? */
        BufferedImage b
            = BitmapVisualization.drawGeoavailabilityGrid(gg, Color.BLACK);
        BitmapVisualization.imageToFile(b, "NetCDF-GeoavailabilityGrid.gif");

        List<Coordinates> poly = new ArrayList<>();
        poly.add(new Coordinates(43.79f, -105.00f));
        poly.add(new Coordinates(40.96f, -103.50f));
        poly.add(new Coordinates(39.98f, -108.47f));
        GeoavailabilityQuery gq = new GeoavailabilityQuery(poly);

        /* Let's see what the polygon looks like too... */
        Bitmap queryBitamp = QueryTransform.queryToGridBitmap(gq, gg);
        BufferedImage polyImage
            = BitmapVisualization.drawBitmap(queryBitamp,
                    gg.getWidth(), gg.getHeight(), Color.RED);
        BitmapVisualization.imageToFile(
                polyImage, "NetCDF-GeoavailabilityQuery.gif");

        /* Does this polygon overlap the grid? */
        boolean intersects = gg.intersects(gq);
        System.out.println("Polygon intersects the grid: " + intersects);

        /* Alternatively, a little 'polygon' that won't intersect: */
        poly = new ArrayList<>();
        poly.add(new Coordinates(43.79f, -105.39f));
        gq = new GeoavailabilityQuery(poly);

        intersects = gg.intersects(gq);
        System.out.println("Polygon intersects the grid: " + intersects);

        /* GeoavailabilityMap tests */
        GeoavailabilityMap<String> gm
            = new GeoavailabilityMap<>("9x", 20);

        System.out.println("Adding points to Map");
        for (String str : metaMap.keySet()) {
            if (str.toLowerCase().substring(0, 2).equals("9x")) {
                /* We found a sample for our particular region */
                Coordinates coords
                    = metaMap.get(str).getSpatialProperties().getCoordinates();
                /* We'll store the point's coordinates.  Generally, the item
                 * stored with the point should be a unique identifier or
                 * something of that nature. */
                gm.addPoint(coords, str);
            }
        }

        poly = new ArrayList<>();
        poly.add(new Coordinates(43.79f, -105.00f));
        poly.add(new Coordinates(40.96f, -103.50f));
        poly.add(new Coordinates(39.98f, -108.47f));
        gq = new GeoavailabilityQuery(poly);

        System.out.println("Intersecting points:");
        Map<Integer, List<String>> results = gm.query(gq);
        for (int i : results.keySet()) {
            System.out.print(i + ", ");
        }
        System.out.println();
        System.out.println("Total points: " + results.keySet().size());
        /* Homework: see if the total points in the result set matches the
         * number of points in the outputted gifs */
    }
}
