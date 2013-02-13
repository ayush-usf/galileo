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

package galileo.serialization;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.lang.reflect.Constructor;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides convenience functions to make the Serialization and
 * Deserialization process easier.
 *
 * In brief, the static methods in this class will initialize proper streams for
 * reading or creating objects, do the work, and then close the streams.
 */
public class Serializer {

    private static final Logger logger = Logger.getLogger("galileo");

    /**
     * Dumps a ByteSerializable object to a portable byte array.
     *
     * @param obj The ByteSerializable object to serialize.
     *
     * @return binary byte array representation of the object.
     */
    public static byte[] serialize(ByteSerializable obj)
    throws IOException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        BufferedOutputStream buffOut = new BufferedOutputStream(byteOut);

        SerializationOutputStream serialOut =
            new SerializationOutputStream(buffOut);

        serialOut.writeSerializable(obj);
        serialOut.close();
        return byteOut.toByteArray();
    }

    /**
     * Loads a ByteSerializable object's binary form and then instantiates a new
     * object using the SerializationInputStream constructor.
     *
     * @param type The type of object to create (deserialize).
     *             For example, Something.class.
     *
     * @param bytes Binary form of the object being loaded.
     */
    public static
    <T extends ByteSerializable> T deserialize(Class<T> type, byte[] bytes)
    throws IOException, SerializationException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
        BufferedInputStream buffIn = new BufferedInputStream(byteIn);

        SerializationInputStream serialIn =
            new SerializationInputStream(buffIn);

        /* ABANDON HOPE, ALL YE WHO ENTER HERE... */
        T obj = null;
        try {
            Constructor<T> constructor =
                type.getConstructor(SerializationInputStream.class);
            obj = (T) constructor.newInstance(serialIn);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to deserialize object.", e);

            throw new SerializationException("Could not instantiate object " +
                    "for deserialization.");
        } finally {
            serialIn.close();
        }

        return obj;
    }
}
