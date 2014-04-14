
package galileo.fs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;

import galileo.dataset.feature.Feature;
import galileo.dataset.feature.FeatureType;
import galileo.graph.FeaturePath;
import galileo.graph.Vertex;
import galileo.serialization.SerializationException;
import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;
import galileo.util.Pair;

public class PathJournal {

    private static final Logger logger = Logger.getLogger("galileo");

    private String pathFile;
    private String indexFile;

    private DataOutputStream pathStore;
    private DataOutputStream indexStore;

    private Map<String, Integer> featureNames = new HashMap<>();
    private Map<Integer, Pair<String, FeatureType>> featureIndex
        = new HashMap<>();
    private int nextId;

    private boolean running = false;

    public PathJournal(String pathFile) {
        this.pathFile = pathFile;
        this.indexFile = pathFile + ".index";
    }

    public boolean recover(List<FeaturePath<String>> paths)
    throws IOException {
        boolean clean = true;

        try {
            recoverIndex();
        } catch (EOFException e) {
            logger.info("Reached end of path journal index.");
        } catch (FileNotFoundException e) {
            logger.info("Could not locate journal index.  Journal recovery is "
                    + "not possible.");
            return false;
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error reading path index!", e);
            clean = false;
        }
        logger.log(Level.INFO, "Features read: {0}", featureNames.size());

        try {
            recoverPaths(paths);
        } catch (EOFException e) {
            logger.info("Reached end of path journal.");
        } catch (SerializationException e) {
            logger.log(Level.WARNING, "Error deserializing path!", e);
            clean = false;
        }
        logger.log(Level.INFO, "Recovered {0} paths.", paths.size());

        return clean;
    }

    private void recoverIndex()
    throws FileNotFoundException, IOException {
        DataInputStream indexIn = new DataInputStream(
                new BufferedInputStream(
                    new FileInputStream(indexFile)));

        while (true) {
            long check = indexIn.readLong();
            int entryLength = indexIn.readInt();

            byte[] entry = new byte[entryLength];
            int read = indexIn.read(entry);
            if (read != entry.length) {
                logger.info("Reached end of journal index");
                /* Did not read a complete entry, we're done. */
                break;
            }

            CRC32 crc = new CRC32();
            crc.update(entry);
            if (crc.getValue() != check) {
                logger.warning("Detected checksum mismatch in journal index; "
                        + "ignoring entry.");
                continue;
            }

            SerializationInputStream sIn = new SerializationInputStream(
                    new ByteArrayInputStream(entry));

            int featureId = sIn.readInt();
            FeatureType type = FeatureType.fromInt(sIn.readInt());
            String name  = sIn.readString();

            newFeature(featureId, type, name);
            sIn.close();
        }

        indexIn.close();
    }

    private void recoverPaths(List<FeaturePath<String>> paths)
    throws IOException, SerializationException {
        DataInputStream pathIn = new DataInputStream(
                new BufferedInputStream(
                    new FileInputStream(pathFile)));

        while (true) {
            long check = pathIn.readLong();
            int pathSize = pathIn.readInt();

            byte[] pathBytes = new byte[pathSize];
            int read = pathIn.read(pathBytes);
            if (read != pathSize) {
                logger.info("Reached end of path index");
                break;
            }

            CRC32 crc = new CRC32();
            crc.update(pathBytes);
            if (crc.getValue() != check) {
                logger.warning("Detected checksum mismatch; ignoring path.");
                continue;
            }

            SerializationInputStream sIn = new SerializationInputStream(
                    new ByteArrayInputStream(pathBytes));

            int vertices = sIn.readInt();
            FeaturePath<String> fp = new FeaturePath<>();
            for (int i = 0; i < vertices; ++i) {
                int featureId = sIn.readInt();
                Pair<String, FeatureType> featureInfo
                    = featureIndex.get(featureId);

                Feature f = new Feature(featureInfo.a, featureInfo.b, sIn);
                fp.add(f);
            }

            int payloads = sIn.readInt();
            for (int i = 0; i < payloads; ++i) {
                String payload = sIn.readString();
                fp.addPayload(payload);
            }

            paths.add(fp);
            sIn.close();
        }

        pathIn.close();
    }

    public void start()
    throws IOException {
        OutputStream out = Files.newOutputStream(Paths.get(pathFile),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND);
        pathStore = new DataOutputStream(new BufferedOutputStream(out));

        OutputStream indexOut = Files.newOutputStream(Paths.get(indexFile),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND,
                StandardOpenOption.DSYNC);
        indexStore = new DataOutputStream(new BufferedOutputStream(indexOut));

        running = true;
    }

    private void checkIndex(Feature feature) {
        String featureName = feature.getName();

        if (featureNames.get(featureName) != null) {
            return;
        } else {
            int featureId = newFeature(feature);
            try {
                writeIndex(featureId, feature);
            } catch (IOException e) {
                logger.log(Level.SEVERE,
                        "Could not write to path journal index!", e);
            }
        }
    }

    private int newFeature(Feature feature) {
        int featureId = nextId;
        FeatureType type = feature.getType();
        String name = feature.getName();

        return newFeature(featureId, type, name);
    }

    private int newFeature(int featureId, FeatureType type, String name) {
        logger.log(Level.INFO, "Adding new Feature to path journal index: {0}",
                name);

        featureNames.put(name, featureId);
        featureIndex.put(featureId, new Pair<>(name, type));

        nextId = featureId + 1;

        return featureId;
    }

    private void writeIndex(int featureId, Feature feature)
    throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        SerializationOutputStream sOut = new SerializationOutputStream(bOut);
        sOut.writeInt(featureId);
        sOut.writeInt(feature.getType().toInt());
        sOut.writeString(feature.getName());
        sOut.close();
        byte[] entry = bOut.toByteArray();

        CRC32 crc = new CRC32();
        crc.update(entry);
        long check = crc.getValue();

        indexStore.writeLong(check);
        indexStore.writeInt(entry.length);
        indexStore.write(entry);
        indexStore.flush();
    }

    public void persistPath(FeaturePath<String> path)
    throws FileSystemException, IOException {
        if (running == false) {
            throw new FileSystemException("Path Journal has not been started!");
        }

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        SerializationOutputStream sOut = new SerializationOutputStream(bOut);
        sOut.writeInt(path.size());
        for (Vertex<Feature, String> v : path.getVertices()) {
            Feature f = v.getLabel();
            checkIndex(f);
            int featureId = featureNames.get(f.getName());
            sOut.writeInt(featureId);
            sOut.writeSerializable(f.getDataContainer());
        }
        sOut.writeInt(path.getPayload().size());
        for (String s : path.getPayload()) {
            sOut.writeString(s);
        }
        sOut.close();
        byte[] pathBytes = bOut.toByteArray();

        CRC32 crc = new CRC32();
        crc.update(pathBytes);
        long check = crc.getValue();

        pathStore.writeLong(check);
        pathStore.writeInt(pathBytes.length);
        pathStore.write(pathBytes);
        pathStore.flush();
    }

    public void erase()
    throws IOException {
        shutdown();

        new File(indexFile).delete();
        new File(pathFile).delete();
    }

    public void shutdown()
    throws IOException {
        if (running == false) {
            return;
        }

        indexStore.close();
        pathStore.close();
    }
}

