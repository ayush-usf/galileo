
package galileo.util;

import java.util.ArrayList;

public class GeoTest {
    public static void main(String[] args) {
        //GeoHash.hashToCoordinates("ezs42");
        //GeoHash.hashToCoordinates("9xjqb4pnb1w6c");
        //GeoHash.hashToCoordinates("gcpuvnseewpmz");
        //GeoHash.hashToCoordinates("6gkzwgjzn82011234821111");
        //GeoHash.hashToCoordinates("6gkzwgjzn82011234821111ggggk");

        //ArrayList<Boolean> bits = GeoHash.getBits("ezs42");
        //for (int i = 0; i < bits.size(); ++i) {
        //    System.out.println(bits.get(i));
        //}
        //GeoHash.decodeToBoundingBox(bits);
        String hash = GeoHash.encode(57.64911f, 10.40744f, 12);

        System.out.println(hash);

    }
}
