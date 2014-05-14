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

package galileo.test.net;

import java.io.IOException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import galileo.net.GalileoMessage;
import galileo.net.MessageListener;
import galileo.net.NetworkDestination;
import galileo.net.ServerMessageRouter;

public class ScaleTestServer implements MessageListener {

    protected static final int PORT = 5050;

    protected static final int QUERY_SIZE = 64;
    protected static final int REPLY_SIZE = 4096;

    private int connections;

    private ServerMessageRouter messageRouter;
    private BlockingQueue<GalileoMessage> eventQueue
        = new LinkedBlockingQueue<>();

    public void listen()
    throws IOException {
        messageRouter = new ServerMessageRouter(PORT);
        messageRouter.addListener(this);
        messageRouter.listen();
        System.out.println("Listening...");
    }

    @Override
    public void onConnect(NetworkDestination endpoint) {
        System.out.println("Connections: " + (++connections));
    }

    @Override
    public void onDisconnect(NetworkDestination endpoint) {
        System.out.println("Connections: " + (--connections));
    }

    @Override
    public void onMessage(GalileoMessage message) {
        try {
            eventQueue.put(message);
        } catch (InterruptedException e) {
            Thread.interrupted();
            e.printStackTrace();
        }
    }

    public void processMessages() throws Exception {
        while (true) {
            GalileoMessage message = eventQueue.take();

            messageRouter.sendMessage(
                    message.getSelectionKey(),
                    new GalileoMessage(new byte[REPLY_SIZE]));
        }
    }

    public static void main(String[] args) throws Exception {
        ScaleTestServer sts = new ScaleTestServer();
        sts.listen();

        sts.processMessages();
    }
}
