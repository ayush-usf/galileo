package galileo.dataset.feature;

import galileo.util.Counter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FeatureArray {

    private int[] dimensions;
    private int[] offsets;

    private List<Feature> features;

    private boolean named;
    private String name;

    private boolean typed;
    private FeatureType type;

    /**
     * Creates a new FeatureArray with the specified dimensions and a specific
     * {@link FeatureType} and Feature name.  A FeatureArray created with these
     * parameters will not allow variable FeatureTypes to be inserted into the
     * array, and will not allow variable Feature names to be used.  Specifying
     * the Feature name and FeatureType ahead of time reduces the amount of
     * information tracked by the array, as well as the amount of information
     * that must be serialized.
     *
     * @param dimensions A list of dimensions.  For example, passing {10} would
     * create a 1D array with 10 elements.  Passing {10, 100} would create a 2D
     * array with a total of 1000 elements.
     */
    public FeatureArray(String name, FeatureType type, int... dimensions) {
        this(type, dimensions);

        this.named = true;
        this.name = name;
    }

    /**
     * Creates a new FeatureArray with the specified dimensions and a specific
     * {@link FeatureType}.  A FeatureArray created with these parameters will
     * not allow variable FeatureTypes to be inserted into the array.
     *
     * @param dimensions A list of dimensions.  For example, passing {10} would
     * create a 1D array with 10 elements.  Passing {10, 100} would create a 2D
     * array with a total of 1000 elements.
     */
    public FeatureArray(FeatureType type, int... dimensions) {
        this(dimensions);

        this.typed = true;
        this.type = type;
    }

    /**
     * Creates a new FeatureArray with the specified dimensions.  FeatureArrays
     * created in this way will allow variable names and FeatureTypes of its
     * children Features, which offers the most flexibility but also consumes
     * the most memory and/or serialization processing time.
     *
     * @param dimensions A list of dimensions.  For example, passing {10} would
     * create a 1D array with 10 elements.  Passing {10, 100} would create a 2D
     * array with a total of 1000 elements.
     */
    public FeatureArray(int... dimensions) {
        constructBackingStore(dimensions);
    }

    private void constructBackingStore(int... dimensions) {
        this.dimensions = dimensions;

        /* Determine the overall size of the (collapsed) array */
        int size = 1;
        for (int i = 0; i < dimensions.length; ++i) {
            if (dimensions[i] <= 0) {
                throw new IllegalArgumentException("Invalid array dimension");
            }
            size = size * dimensions[i];
        }
        Feature nullFeature = new Feature();
        features = new ArrayList<>(Collections.nCopies(size, nullFeature));

        /* Determine array offsets */
        offsets = new int[dimensions.length];
        for (int i = 0; i < dimensions.length; ++i) {
            offsets[i] = 1;
            for (int j = i + 1; j < dimensions.length; ++j) {
                offsets[i] = offsets[i] * dimensions[j];
            }
        }
    }

    /**
     * Creates a FeatureArray from a Java array.  The type of the array is
     * ascertained from the Java type.
     *
     * @param name Name of the Features in this FeatureArray
     * @param features Java array containing feature values
     */
    public FeatureArray(String name, Object features) {
        int[] dimensions = getMaxDimensions(features);
        constructBackingStore(dimensions);
        System.out.println("dims=" + this.getSize());
        convertNativeArray(features);
    }

    private void convertNativeArray(Object array) {
        AtomicInteger counter = new AtomicInteger();
        convertNativeArray(counter, array);
    }

    private void convertNativeArray(AtomicInteger counter, Object array) {
        try {
            Array.getLength(array);
        } catch (Exception e) {
            /* Not an array */
            int index = counter.getAndIncrement();
            Feature feature;
            try {
                feature = new Feature(array);
            } catch (NullPointerException npe) {
                feature = new Feature();
            }
            features.set(index, feature);
            return;
        }

        Object[] castedArray = ((Object[]) array);
        for (int i = 0; i < castedArray.length; ++i) {
            convertNativeArray(counter, castedArray[i]);
        }
    }

    private int[] getMaxDimensions(Object features) {
        List<Integer> maxes = new ArrayList<>();
        getMaxDimensions(0, maxes, features);

        int[] dimensions = new int[maxes.size()];
        for (int i = 0; i < maxes.size(); ++i) {
            dimensions[i] = maxes.get(i);
        }
        return dimensions;
    }

    private void getMaxDimensions(
            int level, List<Integer> maxes, Object features) {
        int length;
        try {
            length = Array.getLength(features);
        } catch (Exception e) {
            /* Not an array */
            return;
        }

        if (maxes.size() < level + 1) {
            maxes.add(length);
        } else if (maxes.get(level) < length) {
            maxes.set(level, length);
        }

        Object[] castedFeatures = (Object[]) features;
        for (int i = 0; i < castedFeatures.length; ++i) {
            getMaxDimensions(level + 1, maxes, castedFeatures[i]);
        }
    }

    /**
     * Converts multidimensional array indices into the raw 1D array index used
     * to represent the array.
     *
     * @param indices list of multidimensional array indices.
     *
     * @return raw index corresponding to the indices in the backing store.
     */
    private int getIndex(int... indices) {
        if (indices.length != dimensions.length) {
            throw new IllegalArgumentException("Index array must match "
                    + "array rank.");
        }
        checkIndexBounds(indices);

        int index = 0;
        for (int i = 0; i < indices.length; ++i) {
            index = index + offsets[i] * indices[i];
        }
        return index;
    }

    private void checkIndexBounds(int... indices) {
        for (int i = 0; i < dimensions.length; ++i) {
            if (indices[i] >= dimensions[i]) {
                throw new IndexOutOfBoundsException();
            }
        }
    }

    public Feature get(int... indices) {
        int index = getIndex(indices);
        System.out.println(index);
        return features.get(index);
    }

    public void set(Feature feature, int... indices) {
        int index = getIndex(indices);
        features.set(index, feature);
    }

    public void erase(int... indices) {
        int index = getIndex(indices);
        features.set(index, new Feature());
    }

    public int getRank() {
        return dimensions.length;
    }

    public int[] getDimensions() {
        return dimensions;
    }

    public int getSize() {
        return features.size();
    }
}
