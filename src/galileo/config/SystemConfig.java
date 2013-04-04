/*
Copyright (c) 2013, Colorado State University
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

This software is provided by the copyright holders and contributors "as is" and
any express or implied warranties, including, but not limited to, the implied
warranties of merchantability and fitness for a particular purpose are
disclaimed. In no event shall the copyright holder or contributors be liable for
any direct, indirect, incidental, special, exemplary, or consequential damages
(including, but not limited to, procurement of substitute goods or services;
loss of use, data, or profits; or business interruption) however caused and on
any theory of liability, whether in contract, strict liability, or tort
(including negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
*/

package galileo.config;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides general system configuration information.  The settings contained
 * within are guaranteed to not change during execution unless the reload()
 * method is called explicitly.
 *
 * @author malensek
 */
public class SystemConfig {

    private static final Logger logger = Logger.getLogger("galileo");

    private final static String networkDir = "network";

    private static String storageRoot;
    private static String configurationDirectory;

    /**
     * Retrieves the system storage root.  This directory is where Galileo
     * stores files.
     */
    public static String getStorageRoot() {
        return storageRoot;
    }

    /**
     * Retrieves the system configuration directory, which contains all Galileo
     * configuration directives.
     */
    public static String getConfDir() {
        return configurationDirectory;
    }

    /**
     * Retrives the network configuration directory, which describes the DHT
     * groups and the nodes assigned to them.
     */
    public static String getNetworkConfDir() {
        return configurationDirectory + "/" + networkDir;
    }

    /**
     * Reloads the Galileo system configuration.
     */
    public void reload() {
        logger.log(Level.CONFIG, "Reloading system configuration");
        load();
    }

    /**
     * Loads all system configuration settings.
     */
    private static void load() {
        String storageDir = System.getenv("GALILEO_ROOT");
        if (storageDir == null) {
            storageDir = System.getProperty("storageDirectory", ".");
        }
        storageRoot = storageDir;

        String configDir = System.getenv("GALILEO_CONF");
        if (configDir == null) {
            configDir = System.getProperty("configDirectory", "./config");
        }
        configurationDirectory = configDir;
    }

    static {
        load();
    }
}
