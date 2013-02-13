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

/**
 * Handles various generic string conversion functionality.
 */
public class StringConverter {

    /**
     * Converts a byte array to a hexadecimal String representation.
     *
     * @param buf
     *     Byte array to convert to hex.
     *
     * @return
     *     String containing the hexadecimal representation of the byte array
     *     input.
     */
    public static String convertBytesToHex(byte[] buf) {
        StringBuffer strBuf = new StringBuffer();

        for(int i = 0; i < buf.length; i++) {
            int byteValue = (int) buf[i] & 0xff;

            if (byteValue <= 15) {
                strBuf.append("0");
            }

            strBuf.append(Integer.toString(byteValue, 16));
        }

        return strBuf.toString();
    }

    /**
     * Converts a hexadecimal in String format to a byte array.
     *
     * @param hexString
     *     The hex String to convert
     *
     * @return
     *     A byte array representation of the hexadecimal String input.
     */
    public static byte[] convertHexToBytes(String hexString)
    {
        int size = hexString.length();
        byte[] buf = new byte[size / 2];

        for(int j = 0, i = 0; i < size; i = i + 2, ++j) {
            String a = hexString.substring(i, i + 2);
            int valA = Integer.parseInt(a, 16);
            buf[j] = (byte) valA;
        }
        return buf;
    }
}
