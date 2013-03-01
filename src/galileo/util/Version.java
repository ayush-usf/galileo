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

package galileo.util;

public class Version {

    public static final String PRODUCT_NAME = "Galileo";

    public static final int VERSION_MAJOR = 0;
    public static final int VERSION_MINOR = 4;
    public static final String VERSION = VERSION_MAJOR + "." + VERSION_MINOR;

    public static final String RELEASE = "2012-12-12";

    public Version() {

    }

    public static String getVersion() {
        return (PRODUCT_NAME + " " + VERSION + " : " + RELEASE);
    }


    public void printVersionInformation() {
        System.out.println(getVersion());
    }

    public static void printSplash() {
        System.out.println();
        System.out.println("      ____       _ _ _"               );
        System.out.println("     / ___| __ _| (_) | ___  ___"     );
        System.out.println("    | |  _ / _` | | | |/ _ \\/ _ \\"  );
        System.out.println("    | |_| | (_| | | | |  __/ (_) |"   );
        System.out.println("     \\____|\\__,_|_|_|_|\\___|\\___/");
        System.out.println();
        System.out.println("       Version " + VERSION + " : " + RELEASE);
        System.out.println();
    }
}
