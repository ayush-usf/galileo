
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
