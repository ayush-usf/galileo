
package galileo.dataset;

import java.util.HashMap;
import java.util.Map;

//TODO this should implement Map<String, Feature>
public class FeatureSet {
    private Map<String, Feature> features = new HashMap<String, Feature>();

    public FeatureSet() { }

    public void put(Feature feature) {
        features.put(feature.getName(), feature);
    }

    public Feature get(String name) {
        return features.get(name);
    }
}
