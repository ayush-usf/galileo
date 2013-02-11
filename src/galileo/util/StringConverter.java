
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
