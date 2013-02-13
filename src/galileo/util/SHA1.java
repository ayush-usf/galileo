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
