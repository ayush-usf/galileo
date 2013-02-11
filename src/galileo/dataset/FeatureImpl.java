
package galileo.dataset;

public class FeatureImpl implements Feature {

    private String name;
    private String description;
    private double value;

    public FeatureImpl(String name, double value) {
        this.name = name;
        this.value = value;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public double getValue() {
        return value;
    }
}
