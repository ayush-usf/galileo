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

package galileo.test;

import java.math.BigInteger;

import java.security.NoSuchAlgorithmException;

import java.util.Random;

import galileo.util.Checksum;

/**
 * Generates data packets that can be used to verify reliable transmission
 * across the network.  Galileo is designed to work with different transport
 * mechanisms, hence the creation of this class to ensure communications are
 * being handled reliably.
 *
 * This class is <em>NOT</em> thread safe, so you should create an instance per
 * thread.
 *
 * @author malensek
 */
public class ChecksumPayload {

    private Random random = new Random(System.nanoTime());
    private Checksum check;

    public ChecksumPayload(String algorithm) {
         try {
            check = new Checksum(algorithm);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Could not create Checksum generator!");
            e.printStackTrace();
        }
    }
    /**
     * Generates a random data packet with a SHA-1 check prefix.
     *
     * @param size the size (in bytes) of the generated message.
     *
     * @return the message of requested size, plus its check appended to the
     * beginning of the array.
     */
    public byte[] generate(int size) {
        /* Generate some random bytes for our payload. */
        byte[] payload = new byte[size];
        random.nextBytes(payload);

        /* Hash it. */
        byte[] hash = check.hash(payload);

        /* Combine the two byte arrays */
        byte[] message = new byte[hash.length + payload.length];
        System.arraycopy(hash, 0, message, 0, hash.length);
        System.arraycopy(payload, 0, message, hash.length, payload.length);

        return message;
    }

    /**
     * Verifies the integrity of a received check data packet.
     *
     * @param message the check + data packet to verify.
     *
     * @return true if the message is valid; false if corrupt.
     */
    public boolean verify(byte[] message) {
        int digestLen = check.getMessageDigest().getDigestLength();
        byte[] remoteChecksum = new byte[digestLen];
        byte[] payload = new byte[message.length - digestLen];
        System.arraycopy(message, 0, remoteChecksum, 0, digestLen);
        System.arraycopy(message, digestLen, payload, 0, payload.length);

        byte[] localChecksum = check.hash(payload);

        BigInteger remoteVal = new BigInteger(1, remoteChecksum);
        BigInteger localVal = new BigInteger(1, localChecksum);

        return remoteVal.equals(localVal);
    }
}
