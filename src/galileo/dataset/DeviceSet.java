
package galileo.dataset;

import java.util.HashMap;
import java.util.Map;

//TODO this should implement Map<String, Device>
public class DeviceSet {

    private Map<String, Device> devices = new HashMap<String, Device>();

    public DeviceSet() { }

    public void put(Device device) {
        devices.put(device.getName(), device);
    }

    public Device get(String name) {
        return devices.get(name);
    }
}
