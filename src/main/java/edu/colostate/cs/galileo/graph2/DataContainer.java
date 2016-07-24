package edu.colostate.cs.galileo.graph2;

import java.io.IOException;

import edu.colostate.cs.galileo.serialization.ByteSerializable;
import edu.colostate.cs.galileo.serialization.SerializationInputStream;
import edu.colostate.cs.galileo.serialization.SerializationOutputStream;
import edu.colostate.cs.galileo.stat.RunningStatisticsND;

public class DataContainer implements ByteSerializable {

    public RunningStatisticsND statistics;

    public DataContainer() {
        this.statistics = new RunningStatisticsND();
    }

    public DataContainer(RunningStatisticsND statistics) {
        this.statistics = statistics;
    }

    public void merge(DataContainer container) {
        statistics.merge(container.statistics);
    }

    public void clear() {
        statistics.clear();
    }

    @Deserialize
    public DataContainer(SerializationInputStream in)
    throws IOException {
        statistics = new RunningStatisticsND(in);
    }

    @Override
    public void serialize(SerializationOutputStream out)
    throws IOException {
        statistics.serialize(out);
    }
}
