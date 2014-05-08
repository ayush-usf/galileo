/*
Copyright (c) 2014, Colorado State University
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

package galileo.event;

import galileo.net.GalileoMessage;

import galileo.serialization.SerializationInputStream;
import galileo.serialization.SerializationOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BasicEventWrapper implements EventWrapper {

    @Override
    public GalileoMessage wrap(Event e)
    throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        SerializationOutputStream sOut = new SerializationOutputStream(
                new BufferedOutputStream(bOut));

        //sOut.writeInt(e.getTypeMap().toInt());
        sOut.writeSerializable(e);
        sOut.close();

        byte[] payload = bOut.toByteArray();
        GalileoMessage msg = new GalileoMessage(payload);
        return msg;
    }

    @Override
    public Event unwrap(GalileoMessage msg) {
        ByteArrayInputStream byteIn
            = new ByteArrayInputStream(msg.getPayload());
        BufferedInputStream buffIn = new BufferedInputStream(byteIn);
        SerializationInputStream sIn = new SerializationInputStream(buffIn);

        return null;
    }
}