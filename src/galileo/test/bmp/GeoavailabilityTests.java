
package galileo.test.bmp;

import static org.junit.Assert.*;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import galileo.bmp.GeoavailabilityGrid;
import galileo.bmp.Visualization;
import galileo.dataset.Coordinates;
import galileo.dataset.Point;
import galileo.util.GeoHash;
import galileo.util.Pair;

import org.junit.Test;

public class GeoavailabilityTests {

    private boolean draw = false;

    public GeoavailabilityTests() {
        this.draw = Boolean.parseBoolean(System.getProperty(
                "galileo.test.bmp.GeoavailabilityTests.draw",
                "false"));
    }

//    @Test
//    public void coordinateTransform() {
//        GeoavailabilityGrid gg = new GeoavailabilityGrid("9x", 10);
//
//    }

    @Test
    public void testBitmapCorners() throws IOException {
        GeoavailabilityGrid gg = new GeoavailabilityGrid("9x", 10);
        gg.addPoint(new Coordinates(44.819f, -112.242f));
        gg.addPoint(new Coordinates(44.819f, -101.514f));
        gg.addPoint(new Coordinates(39.496f, -112.242f));
        gg.addPoint(new Coordinates(39.496f, -101.514f));

        if (draw) {
            BufferedImage b = Visualization.drawGeoavailabilityGrid(
                    gg, Color.BLACK);
            Visualization.imageToFile(b, "BitmapCorners.gif");
        }
    }

    /**
     * Ensures out-of-bounds X points are not inserted into the
     * GeoavailabilityGrid.
     */
    @Test
    public void testXOutOfBounds() {
        GeoavailabilityGrid gg = new GeoavailabilityGrid("9x", 10);
        assertEquals(gg.addPoint(new Coordinates(44.819f, -113.350f)), false);
        assertEquals(gg.addPoint(new Coordinates(44.819f, -100.684f)), false);
        assertEquals(gg.addPoint(new Coordinates(39.496f, -113.350f)), false);
        assertEquals(gg.addPoint(new Coordinates(39.496f, -100.684f)), false);
        assertEquals(gg.addPoint(new Coordinates(0.0f, 0.0f)), false);
    }

    /**
     * Ensures out-of-bounds Y points are not inserted into the
     * GeoavailabilityGrid.
     */
    @Test
    public void testYOutOfBounds() {
        GeoavailabilityGrid gg = new GeoavailabilityGrid("9x", 10);
        assertEquals(gg.addPoint(new Coordinates(45.333f, -112.242f)), false);
        assertEquals(gg.addPoint(new Coordinates(45.360f, -101.514f)), false);
        assertEquals(gg.addPoint(new Coordinates(38.992f, -112.242f)), false);
        assertEquals(gg.addPoint(new Coordinates(38.992f, -101.514f)), false);
        assertEquals(gg.addPoint(new Coordinates(0.0f, 0.0f)), false);
    }

    /**
     * Tests the update procedure for bits that are inserted out of order.
     */
    @Test
    public void testUpdates() throws Exception {
        /* Insert points in indexed order */
        GeoavailabilityGrid g1 = new GeoavailabilityGrid("9x", 10);
        g1.addPoint(new Coordinates(44.819f, -112.242f));
        g1.addPoint(new Coordinates(44.819f, -101.514f));
        g1.addPoint(new Coordinates(39.496f, -112.242f));
        g1.addPoint(new Coordinates(39.496f, -101.514f));

        /* Insert points out of order */
        GeoavailabilityGrid g2 = new GeoavailabilityGrid("9x", 10);
        g2.addPoint(new Coordinates(39.496f, -101.514f));
        g2.addPoint(new Coordinates(39.496f, -112.242f));
        g2.addPoint(new Coordinates(44.819f, -101.514f));
        g2.addPoint(new Coordinates(44.819f, -112.242f));

        if (draw) {
            BufferedImage b1 = Visualization.drawGeoavailabilityGrid(g1);
            BufferedImage b2 = Visualization.drawGeoavailabilityGrid(g2);
            Visualization.imageToFile(b1, "Updates1.gif");
            Visualization.imageToFile(b2, "Updates2.gif");
        }
        assertEquals(true, g1.equals(g2));
    }
}
