
package galileo.util;

import java.io.File;

/**
 * Utility class for working with file names.
 *
 * @author malensek
 */
public class FileNames {

    public static Pair<String, String> splitExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');

        if (lastDot == -1) {
            return new Pair<>(name, "");
        }

        String pre = name.substring(0, lastDot);
        String ext = name.substring(lastDot + 1);

        return new Pair<>(pre, ext);
    }
}
