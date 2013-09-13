
package galileo.samples;

import java.io.File;
import java.io.IOException;

import java.util.List;

import ucar.ma2.*;
import ucar.nc2.*;

public class ReadNetCDF {
    public static void main(String[] args)
    throws Exception {
        File dir = new File(args[0]);
        for (File f : dir.listFiles()) {
            // Make sure we only try to read grib files
            String fileName = f.getName();
            String ext = fileName.substring(fileName.lastIndexOf('.') + 1,
                    fileName.length());
            if (ext.equals("grb") || ext.equals("bz2")) {
                ReadNetCDF.readFile(f.getAbsolutePath());
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

        if (humi == null || temp == null || wind == null || snow == null) {
            return;
        }

        while (humi.hasNext() && temp.hasNext()
                && wind.hasNext() && snow.hasNext()) {

            double h = humi.nextDouble();
            double t = temp.nextDouble();
            double w = wind.nextDouble();
            double s = snow.nextDouble();

            System.out.println("h=" + h +", t=" + t + ", w=" + w + ", s=" + s);
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
