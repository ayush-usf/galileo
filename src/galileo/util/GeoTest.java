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
