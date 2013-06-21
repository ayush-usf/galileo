
package galileo.test;

import galileo.dataset.BlockMetadata;
import galileo.dataset.Device;
import galileo.dataset.DeviceSet;
import galileo.dataset.Feature;
import galileo.dataset.FeatureSet;
import galileo.dataset.SpatialProperties;
import galileo.dataset.SpatialRange;
import galileo.dataset.TemporalProperties;

import java.io.File;
import java.io.IOException;

import java.util.List;

import ucar.ma2.*;
import ucar.nc2.*;

import ucar.nc2.dataset.NetcdfDataset;

import ucar.nc2.dt.grid.GridDataset;

import ucar.nc2.dt.GridCoordSystem;
import ucar.nc2.dt.GridDatatype;
import ucar.unidata.geoloc.LatLonPoint;

public class ReadData {
    public static void main(String[] args)
    throws Exception {
        File dir = new File(args[0]);
        for (File f : dir.listFiles()) {
            // Make sure we only try to read grib files
            String fileName = f.getName();
            String ext = fileName.substring(fileName.lastIndexOf('.') + 1,
                    fileName.length());
            if (ext.equals("grb")) {
                ReadData.readFile(f.getAbsolutePath());
            }
        }
    }

    public static void readFile(String file)
    throws Exception {
        NetcdfFile n = NetcdfFile.open(file);
        Array humi = getVals(n, "Maximum_Relative_Humumidity");
        Array temp = getVals(n, "Temperature_surface");
        Array wind = getVals(n, "Surface_wind_gust");
        Array snow = getVals(n, "Snow_depth");


        if (humi == null) {
            // humidity varies in this dataset
            humi = getVals(n, "Relative_humidity_height_above_ground");
        }

        // If we don't have all the dimensions we want, just ignore this item.
        if (humi == null || temp == null || wind == null || snow == null) {
            return;
        }

        NetcdfDataset dataset = new NetcdfDataset(n);
        GridDataset grid = new GridDataset(dataset);
        GridDatatype datatype = grid.findGridDatatype("Temperature_surface");
        GridCoordSystem coords = datatype.getCoordinateSystem();

        // Get the dimensions of the grid of readings
        int x = dataset.findDimension("x").getLength();
        int y = dataset.findDimension("y").getLength();

//        for (int i = 0; i < x; ++i) {
//            for (int j = 0; j < y; ++j) {
//                // Print out the lat,lon coords for each sample
//                System.out.println(coords.getLatLon(i, j));
//            }
//        }

        LatLonPoint upperPt = coords.getLatLon(0, 0);
        LatLonPoint lowerPt = coords.getLatLon(x - 1, y - 1);
        SpatialRange range = new SpatialRange(
                (float) upperPt.getLatitude(), (float) upperPt.getLongitude(),
                (float) lowerPt.getLatitude(), (float) lowerPt.getLongitude());

        // Print out each feature sample
        while (humi.hasNext() && temp.hasNext()
                && wind.hasNext() && snow.hasNext()) {

            double h = humi.nextDouble();
            double t = temp.nextDouble();
            double w = wind.nextDouble();
            double s = snow.nextDouble();

            System.out.println("h=" + h +", t=" + t + ", w=" + w + ", s=" + s);


            // Let's create some metadata
            SpatialProperties spatialProps = new SpatialProperties(range);
            TemporalProperties temporalProps
                = new TemporalProperties(System.nanoTime());

            FeatureSet features = new FeatureSet();
            features.put(new Feature("humidity", h));
            features.put(new Feature("temperature", t));
            features.put(new Feature("wind_speed", w));
            features.put(new Feature("snow_depth", s));

            DeviceSet devices = new DeviceSet();
            devices.put(new Device("test_device"));

            BlockMetadata meta = new BlockMetadata(temporalProps, spatialProps,
                    features, devices);
        }
    }

    public static Array getVals(NetcdfFile file, String varName)
    throws IOException, InvalidRangeException {
        Variable v = file.findVariable(varName);
        if (v == null) {
            return null;
        }
        List<Dimension> dims = v.getDimensions();
        int numDims = dims.size();

        int[] origin = new int[numDims];
        for (int i = 0; i < numDims; ++i) {
            origin[i] = 0;
        }

        int[] size = new int[numDims];
        for (int i = 0; i < numDims; ++i) {
            size[i] = dims.get(i).getLength();
        }

        return v.read(origin, size);
    }
}
