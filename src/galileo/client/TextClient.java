
package galileo.client;

import java.io.IOException;

import java.net.UnknownHostException;

import java.util.Calendar;
import java.util.Random;

import galileo.dataset.BlockMetadata;
import galileo.dataset.BlockMetadataImpl;
import galileo.dataset.Device;
import galileo.dataset.DeviceSet;
import galileo.dataset.FeatureImpl;
import galileo.dataset.FeatureSet;
import galileo.dataset.FileBlock;
import galileo.dataset.SpatialRange;
import galileo.dataset.TemporalRange;
import galileo.dataset.TemporalRangeImpl;

import galileo.event.StorageEvent;

import galileo.net.ClientMessageRouter;
import galileo.net.GalileoMessage;
import galileo.net.MessageListener;

import galileo.util.GeoHash;

public class TextClient implements MessageListener {

    private static Random randomGenerator = new Random(System.nanoTime());

    private ClientMessageRouter messageRouter = new ClientMessageRouter();
    private EventPublisher publisher = new EventPublisher(messageRouter);

    public TextClient() {
        messageRouter.addListener(this);
    }

    public void connect(String hostname, int port)
    throws UnknownHostException, IOException {
        messageRouter.connectTo(hostname, port);
    }

    @Override
    public void onMessage(GalileoMessage message) {
        if (message == null) {
            /* Connection was terminated */
            messageRouter.shutdown();
            return;
        }
    }

    public void store(FileBlock fb)
    throws Exception {
        StorageEvent store = new StorageEvent(fb);
        publisher.publish(store);
    }

    public static int randomInt(int start, int end) {
        return randomGenerator.nextInt(end - start + 1) + start;
    }

    public static float randomFloat() {
        return randomGenerator.nextFloat();
    }

    public static void main(String[] args)
    throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: galileo.client.TextClient " +
                    "<server-hostname> <server-port> ");
            return;
        }

        String serverHostName = args[0];
        int serverPort = Integer.parseInt(args[1]);

        TextClient client = new TextClient();
        client.connect(serverHostName, serverPort);

        /* Let's make some data to store. */
        
        /* First, a temporal range for this data "sample" */
        Calendar calendar = Calendar.getInstance();
        int year, month, day;

        year = client.randomInt(2010, 2013);
        month = randomInt(0, 11);
        day = client.randomInt(1, 28);

        calendar.set(year, month, day);

        /* Convert the random values to a start time, then add 1ms for the end
         * time.  This simulates 1ms worth of data. */
        long startTime = calendar.getTimeInMillis();
        long endTime   = startTime + 1;

        TemporalRange tempRange = new TemporalRangeImpl(startTime, endTime);


        /* The continental US */
        String[] geoRand = { "c2", "c8", "cb", "f0", "f2",
                             "9r", "9x", "9z", "dp", "dr",
                             "9q", "9w", "9y", "dn", "dq",
                             "9m", "9t", "9v", "dj" };

        String geoPre = geoRand[randomInt(0, geoRand.length - 1)];
        String hash = geoPre;

        for (int i = 0; i < 10; ++i) {
            int random = client.randomInt(0, GeoHash.charMap.length - 1);
            hash += GeoHash.charMap[random];
        }

        SpatialRange spatialRange = GeoHash.decodeHash(hash);

        String[] featSet = { "wind_speed", "wind_direction", "condensation",
                             "temperature", "humidity" };

        FeatureSet features = new FeatureSet();
        for (int i = 0; i < 5; ++i) {
            String featureName = featSet[randomInt(0, featSet.length - 1)];
            features.put(new FeatureImpl(featureName, randomFloat() * 100));
        }

        Device d = new Device("my-gps");
        DeviceSet devices = new DeviceSet();
        devices.put(d);

        BlockMetadata metadata =
            new BlockMetadataImpl(tempRange, spatialRange, features, devices);

        /* Now let's make some "data" to associate with our metadata. */
        Random r = new Random(System.nanoTime());
        byte[] blockData = new byte[8000];
        r.nextBytes(blockData);

        FileBlock b = new FileBlock(blockData, metadata);

        client.store(b);
    }
}
