
package galileo.util;

import java.io.IOException;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import galileo.serialization.ByteSerializable;
import galileo.serialization.Serializer;

/**
 * Provides some convenience functions for dealing with the SHA-1 hashing
 * algorithm.
 */
public class SHA1 {
    private static MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Could not initialize SHA-1 MessageDigest.");
            e.printStackTrace();
        }
    }

    /** Creates a SHA-1 hash from ByteSerializable object.
     *
     * @param data The ByteSerializable object to hash.
     *
     * @return Hash of the input object.
     */
    public static BigInteger fromByteSerializable(ByteSerializable data)
    throws IOException {
        byte[] bytes = Serializer.serialize(data);
        byte[] hash  = digest.digest(bytes);
        BigInteger bigInt = new BigInteger(hash);

        return bigInt;
    }

    /**
     * Creates a SHA-1 hash from a byte array.
     *
     * @param bytes Bytes to hash
     *
     * @return Hash of the input bytes.
     */
    public static BigInteger fromBytes(byte[] bytes) {
        byte[] hash = digest.digest(bytes);
        BigInteger bigInt = new BigInteger(hash);

        return bigInt;
    }

    /**
     * Creates a SHA-1 hash from a String.
     *
     * @param string String to hash.
     *
     * @return Hash of the input String.
     */
    public static BigInteger fromString(String string) {
        byte[] stringHash = digest.digest(string.getBytes());
        BigInteger bigInt = new BigInteger(stringHash);

        return bigInt;
    }
}
