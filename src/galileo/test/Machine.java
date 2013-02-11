
package galileo.test;

import java.util.HashMap;
import java.util.Map;

public class Machine {
    public Map<String, Integer> stuff = new HashMap<String, Integer>();

    public Map<Integer, Integer> rec = new HashMap<Integer, Integer>();

    public void record(double d) {
        int a = (int) d;
        Integer val = rec.get(a);
        if (val == null) {
            System.out.println(a);
            rec.put(a, 1);
        } else {
            rec.put(a, val + 1);
        }
    }
}


