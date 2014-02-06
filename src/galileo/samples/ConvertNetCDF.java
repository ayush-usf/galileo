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

import galileo.dataset.Coordinates;
import galileo.dataset.Metadata;
import galileo.dataset.SpatialProperties;
import galileo.dataset.TemporalProperties;
import galileo.dataset.feature.Feature;
import galileo.serialization.Serializer;

import galileo.util.FileNames;
import galileo.util.GeoHash;
import galileo.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ucar.ma2.*;
import ucar.nc2.*;
import ucar.nc2.dataset.NetcdfDataset;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;
import ucar.nc2.util.DiskCache;
import ucar.unidata.geoloc.LatLonPoint;

/**
 * Converts NetCDF files from the NOAA NAM project to Galileo Metadata.  A file
 * containing a single grid is converted to many files resprenting single
 * points.  This process can be fairly memory-intensive.
 *
 * @author malensek
 */
public class ConvertNetCDF {
    public static void main(String[] args)
    throws Exception {
        DiskCache.setCachePolicy(true);

        File dir = new File(args[0]);
        for (File f : dir.listFiles()) {
            Pair<String, String> nameParts = FileNames.splitExtension(f);
            String ext = nameParts.b;
            if (ext.equals("grb") || ext.equals("bz2") || ext.equals("gz")) {
                Map<String, Metadata> metaMap
                        = ConvertNetCDF.readFile(f.getAbsolutePath());

                /* Don't cache more than 500 MB: */
                DiskCache.cleanCache(524288000, null);

                /* Write converted files to disk */
                System.out.print("Writing converted files");
                int processed = 0;
                int increment = metaMap.keySet().size() / 50;
                for (String g : metaMap.keySet()) {
                    Metadata meta = metaMap.get(g);
                    Coordinates coords
                        = meta.getSpatialProperties().getCoordinates();
                    String location = GeoHash.encode(
                            coords.getLatitude(), coords.getLongitude(), 10);

                    String outputDir = args[1];
                    String outputName = nameParts.a + "-" + location;
                    String subDir = location.substring(0, 2) + "/"
                        + location.substring(2, 5);
                    File sub = new File(outputDir + "/" + subDir);
                    if (!sub.exists()) {
                        if (!sub.mkdirs()) {
                            throw new IOException(
                                    "Failed to create directory " + subDir);
                        }
                    }

                    FileOutputStream fOut = new FileOutputStream(outputDir
                            + "/" + subDir + "/" + outputName + ".gmeta");
                    fOut.write(Serializer.serialize(meta));
                    fOut.close();

                    if (++processed % increment == 0) {
                        System.out.print('.');
                    }
                }

            }

        }
    }

    /**
     * Converts a 3D variable to a {@link Metadata} representation
     */
    private static void convert3DVariable(GridDatatype g, Date date,
            Map<String, Metadata> metaMap) throws IOException {

        Variable v = g.getVariable();
        System.out.println("Reading: " + v.getFullName());
        Array values = v.read();

        int h = v.getShape(1);
        int w = v.getShape(2);

        for (int i = 0; i < h; ++i) {
            for (int j = 0; j < w; ++j) {
                LatLonPoint pt = g.getCoordinateSystem().getLatLon(j, i);
                String hash = GeoHash.encode(
                        (float) pt.getLatitude(),
                        (float) pt.getLongitude(), 10).toLowerCase();

                Metadata meta = metaMap.get(hash);
                if (meta == null) {
                    /* We need to create Metadata for this location */
                    meta = new Metadata();

                    SpatialProperties location = new SpatialProperties(
                            (float) pt.getLatitude(),
                            (float) pt.getLongitude());
                    meta.setSpatialProperties(location);

                    TemporalProperties time
                        = new TemporalProperties(date.getTime());
                    meta.setTemporalProperties(time);

                    metaMap.put(hash, meta);
                }

                String featureName = v.getFullName();
                float featureValue = values.getFloat(i * w + j);
                Feature feature = new Feature(featureName, featureValue);
                meta.putAttribute(feature);
            }
        }
    }

    public static Map<String, Metadata> readFile(String file)
    throws Exception {
        NetcdfFile n = NetcdfFile.open(file);
        System.out.println("Opened: " + file);

        /* Determine the size of our grid */
        int xLen = n.findDimension("x").getLength();
        int yLen = n.findDimension("y").getLength();
        System.out.println("Grid size: " + xLen + "x" + yLen);

        /* What time is this set of readings for? */
        Variable timeVar = n.findVariable("time");
        String timeStr = timeVar.getUnitsString().toUpperCase();
        timeStr = timeStr.replace("HOURS SINCE ", "");

        /* Find the base date (the day) the reading was taken */
        Date baseDate
            = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").parse(timeStr);

        /* Get the number of hours since the base date this reading was taken */
        int offset = timeVar.read().getInt(0);

        /* Generate the actual date for this reading */
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(baseDate);
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + offset);
        System.out.println("Time: " + calendar.getTime());

        /* We'll keep a mapping of geolocations -> Galileo Metadata */
        Map<String, Metadata> metaMap = new HashMap<>();

        /* Determine the lat, lon coordinates for the grid points, and get each
         * reading at each grid point. */
        NetcdfDataset dataset = new NetcdfDataset(n);
        GridDataset gridData = new GridDataset(dataset);
        for (GridDatatype g : gridData.getGrids()) {
            /* Let's look at 3D variables: these have WxH dimensions, plus a
             * single plane.  A 4D variable would contain elevation
             * and multiple planes as a result */
            if (g.getShape().length == 3) {
                convert3DVariable(g, calendar.getTime(), metaMap);
            }
        }

        return metaMap;

    }
}
