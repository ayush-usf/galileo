
package galileo.dataset;

public interface Feature {

    /**
     * Retrieves the name of this Feature.
     *
     * @return Name of this Feature
     */
    public String getName();

    /**
     * Retrieves the description of this Feature.
     *
     * @return Feature description
     */
    public String getDescription();

    // TODO: this should change.
    public double getValue();
}
