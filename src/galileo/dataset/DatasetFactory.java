
package galileo.dataset;

public interface DatasetFactory {

    /**
     * The constraints will specify the temporal and spatial range of the
     * interactions.
     */
    public Dataset createDataset(String constraints);


    public boolean updateTemporalRange(Dataset dataset,
            TemporalRange temporalRange)
    throws DatasetException;


    public boolean updateSpatialRange(Dataset dataset,
            SpatialRange spatialRange)
    throws DatasetException;

}
