
package galileo.test;

import java.util.ArrayList;
import java.util.List;

public class Group {
    public List<Machine> machines;

    public Group(int numMachines) {
        machines = new ArrayList<Machine>();

        for (int i = 0; i < numMachines; ++i) {
            machines.add(new Machine());
        }
    }
}


