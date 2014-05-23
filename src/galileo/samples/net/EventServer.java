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

package galileo.samples.net;

import java.io.IOException;

import galileo.event.EventContext;
import galileo.event.EventHandler;
import galileo.event.EventReactor;
import galileo.net.ServerMessageRouter;

public class EventServer {

    public static final int LISTEN_PORT = 7777;

    private int port;

    private ServerMessageRouter messageRouter;

    private EventReactor eventReactor;
    private SampleEventMap eventMap;

    public EventServer(int port) {
        this.port = port;
    }

    public void start()
    throws IOException {
        eventMap = new SampleEventMap();
        eventReactor = new EventReactor(this, this.eventMap);
        messageRouter = new ServerMessageRouter(this.port);
        messageRouter.addListener(eventReactor);
        messageRouter.listen();

        while (true) {
            try {
                eventReactor.processNextEvent();
            } catch (Exception e) {
                System.out.println("Error processing event!");
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void processGoodEvent(GoodEvent event, EventContext context) {
        System.out.println("We got a GoodEvent!");
        System.out.println("Let's see what it says.");
        System.out.println("Contents: " + event.getData());
    }

    public static void main(String[] args)
    throws IOException {
        EventServer server = new EventServer(LISTEN_PORT);
        server.start();
    }
}
