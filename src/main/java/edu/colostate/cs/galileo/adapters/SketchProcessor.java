package edu.colostate.cs.galileo.adapters;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import edu.colostate.cs.galileo.dataset.Metadata;
import edu.colostate.cs.galileo.dataset.analysis.Quantizer;
import edu.colostate.cs.galileo.dataset.feature.Feature;
import edu.colostate.cs.galileo.graph2.FeatureHierarchy;
import edu.colostate.cs.galileo.graph2.GraphException;
import edu.colostate.cs.galileo.graph2.GraphMetrics;
import edu.colostate.cs.galileo.graph2.Path;
import edu.colostate.cs.galileo.graph2.Sketch;
import edu.colostate.cs.galileo.query2.Expression;
import edu.colostate.cs.galileo.query2.Operator;
import edu.colostate.cs.galileo.query2.PartitionQuery;
import edu.colostate.cs.galileo.serialization.SerializationOutputStream;
import edu.colostate.cs.galileo.serialization.Serializer;

public class SketchProcessor /*extends AbstractGeoSpatialStreamProcessor*/ {

    private Sketch sketch;
    private Sketch diff;
    private FeatureHierarchy hierarchy;
    private Set<String> activeFeatures = new HashSet<>();

    public SketchProcessor() {
        /* Populate the feature hierarchy */
        try {
            hierarchy = new FeatureHierarchy();
            for (String featureName : TestConfiguration.FEATURE_NAMES) {
                hierarchy.addFeature(featureName, FeatureType.FLOAT);
            }
            hierarchy.addFeature("location", FeatureType.STRING);
            this.sketch = new Sketch(hierarchy);
            this.diff = new Sketch(hierarchy);

            for (String featureName : TestConfiguration.FEATURE_NAMES) {
                activeFeatures.add(featureName);
            }
        } catch (GraphException e) {
            System.out.println("Could not initialize sketch graph hierarchy");
            e.printStackTrace();
        }
    }

    public GraphMetrics getGraphMetrics() {
        System.out.println(this.sketch.getRoot().numDescendants() + "," + this.sketch.getRoot().numLeaves());
        return this.sketch.getMetrics();
    }

    protected void process(GeoHashIndexedRecord event) {
        Metadata eventMetadata = null;
        try {
            byte[] payload = event.getPayload();
            eventMetadata = Serializer.deserialize(Metadata.class, payload);
        } catch (Exception e) {
            System.out.println("Could not deserialize event payload");
            e.printStackTrace();
        }

        try {
            Path path = new Path(this.activeFeatures.size() + 1);
            for (Feature f : eventMetadata.getAttributes()) {
                String featureName = f.getName();
                if (activeFeatures.contains(featureName) == false) {
                    continue;
                }

                Quantizer q = TestConfiguration.quantizers.get(featureName);
                if (q == null) {
                    continue;
                }

                Feature quantizedFeature = q.quantize(f);
                path.add(new Feature(f.getName().intern(), quantizedFeature));
            }

            String shortLocation = event.getGeoHash().substring(0, 4);
            path.add(new Feature("location", shortLocation));
            this.sketch.addPath(path);
            this.diff.addPath(path);
        } catch (Exception e) {
            System.out.println("Failed to insert graph path");
            e.printStackTrace();
        }
    }

    public byte[] split(String prefix) {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();

        try {
            SerializationOutputStream out
                = new SerializationOutputStream(new GZIPOutputStream(byteOut));

            PartitionQuery pq = new PartitionQuery();
            pq.addExpression(
                    new Expression(
                        Operator.STR_PREFIX, new Feature("location", prefix)));
            pq.execute(sketch.getRoot());
            pq.serializeResults(sketch.getRoot(), out);
            out.close();

            this.sketch.geoTrie.remove(prefix);
        } catch (Exception e) {
            System.out.println("Failed to split sketch");
            e.printStackTrace();
        }

        return byteOut.toByteArray();
    }

    public void merge(String prefix, byte[] serializedSketch) {
        try {
            SerializationInputStream in = new SerializationInputStream(
                    new BufferedInputStream(
                        new GZIPInputStream(
                            new ByteArrayInputStream(serializedSketch))));

            this.sketch.merge(in);
            in.close();
        } catch (Exception e) {
            System.out.println("Failed to merge sketch");
            e.printStackTrace();
        }
    }

    public double getMemoryConsumptionForPrefix(String prefix) {
        CountContainer cc = this.sketch.geoTrie.query(prefix);
        long vertices = cc.a;
        long leaves = cc.b;
        return estimateMemoryUsage(vertices, leaves);
    }

    public double getMemoryConsumptionForAllPrefixes() {
        GraphMetrics gm = this.sketch.getMetrics();
        return estimateMemoryUsage(gm.getVertexCount(), gm.getLeafCount());
    }

    private double estimateMemoryUsage(long vertices, long leaves) {
        int bytesPerVertex = 16;

        int numFeatures = sketch.getFeatureHierarchy().size();
        int bytesPerLeaf = 8 + (8 * numFeatures * 4)
            + (8 * ((numFeatures * (numFeatures - 1)) / 2));

        return ((bytesPerVertex * vertices) + (bytesPerLeaf * leaves)) * 1.7;
    }

    public byte[] getSketchDiff() {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try {
            SerializationOutputStream out
                = new SerializationOutputStream(new GZIPOutputStream(byteOut));
            diff.getRoot().serialize(out);
            diff = new Sketch(hierarchy);
        } catch (Exception e) {
            System.out.println("Could not produce sketch diff");
            e.printStackTrace();
        }

        return byteOut.toByteArray();
    }

    public void populateSketch(String baseDirPath) {
        this.sketch = new Sketch(this.hierarchy);
        try {
            List<File> files = Files.walk(Paths.get(baseDirPath))
                .filter(Files::isRegularFile)
                .map(java.nio.file.Path::toFile)
                .collect(Collectors.toList());

            for (File file : files) {
                SerializationInputStream in = new SerializationInputStream(
                        new BufferedInputStream(
                            new GZIPInputStream(
                                new FileInputStream(file))));
                this.sketch.merge(in);
                in.close();
            }
        } catch (Exception e) {
            System.out.println("Error during diff restore");
            e.printStackTrace();
        }
    }
}
