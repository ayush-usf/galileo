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

package galileo.test.dht;

import java.util.ArrayList;
import java.util.List;

import galileo.comm.DebugEvent;
import galileo.comm.GalileoEventMap;
import galileo.event.EventContext;
import galileo.event.EventHandler;
import galileo.event.EventReactor;
import galileo.net.ClientMessageRouter;
import galileo.net.GalileoMessage;
import galileo.net.NetworkDestination;

public class DebugClient {

    private int replies = 0;
    private ClientMessageRouter router;
    private List<NetworkDestination> dests = new ArrayList<>();
    private GalileoEventMap eventMap = new GalileoEventMap();
    private EventReactor reactor;

    public DebugClient() throws Exception {
        router = new ClientMessageRouter();
        reactor = new EventReactor(this, eventMap);
        router.addListener(reactor);

        dests.add(new NetworkDestination("lattice-0", 5555));
        dests.add(new NetworkDestination("lattice-1", 5555));
        dests.add(new NetworkDestination("lattice-2", 5555));
        dests.add(new NetworkDestination("lattice-3", 5555));
        dests.add(new NetworkDestination("lattice-4", 5555));
        dests.add(new NetworkDestination("lattice-5", 5555));
        dests.add(new NetworkDestination("lattice-6", 5555));
        dests.add(new NetworkDestination("lattice-7", 5555));
        dests.add(new NetworkDestination("lattice-8", 5555));
        dests.add(new NetworkDestination("lattice-9", 5555));
        dests.add(new NetworkDestination("lattice-10", 5555));
        dests.add(new NetworkDestination("lattice-11", 5555));
        dests.add(new NetworkDestination("lattice-12", 5555));
        dests.add(new NetworkDestination("lattice-13", 5555));
        dests.add(new NetworkDestination("lattice-14", 5555));
        dests.add(new NetworkDestination("lattice-15", 5555));
        dests.add(new NetworkDestination("lattice-16", 5555));
        dests.add(new NetworkDestination("lattice-17", 5555));
        dests.add(new NetworkDestination("lattice-18", 5555));
        dests.add(new NetworkDestination("lattice-19", 5555));
        dests.add(new NetworkDestination("lattice-20", 5555));
        dests.add(new NetworkDestination("lattice-21", 5555));
        dests.add(new NetworkDestination("lattice-22", 5555));
        dests.add(new NetworkDestination("lattice-23", 5555));
        dests.add(new NetworkDestination("lattice-24", 5555));
        dests.add(new NetworkDestination("lattice-25", 5555));
        dests.add(new NetworkDestination("lattice-26", 5555));
        dests.add(new NetworkDestination("lattice-27", 5555));
        dests.add(new NetworkDestination("lattice-28", 5555));
        dests.add(new NetworkDestination("lattice-29", 5555));
        dests.add(new NetworkDestination("lattice-30", 5555));
        dests.add(new NetworkDestination("lattice-31", 5555));
        dests.add(new NetworkDestination("lattice-32", 5555));
        dests.add(new NetworkDestination("lattice-33", 5555));
        dests.add(new NetworkDestination("lattice-34", 5555));
        dests.add(new NetworkDestination("lattice-35", 5555));
        dests.add(new NetworkDestination("lattice-36", 5555));
        dests.add(new NetworkDestination("lattice-37", 5555));
        dests.add(new NetworkDestination("lattice-38", 5555));
        dests.add(new NetworkDestination("lattice-39", 5555));
        dests.add(new NetworkDestination("lattice-40", 5555));
        dests.add(new NetworkDestination("lattice-41", 5555));
        dests.add(new NetworkDestination("lattice-42", 5555));
        dests.add(new NetworkDestination("lattice-43", 5555));
        dests.add(new NetworkDestination("lattice-44", 5555));
        dests.add(new NetworkDestination("lattice-45", 5555));
        dests.add(new NetworkDestination("lattice-46", 5555));
        dests.add(new NetworkDestination("lattice-47", 5555));
        dests.add(new NetworkDestination("lattice-48", 5555));
        dests.add(new NetworkDestination("lattice-49", 5555));
        dests.add(new NetworkDestination("lattice-50", 5555));
        dests.add(new NetworkDestination("lattice-51", 5555));
        dests.add(new NetworkDestination("lattice-52", 5555));
        dests.add(new NetworkDestination("lattice-53", 5555));
        dests.add(new NetworkDestination("lattice-54", 5555));
        dests.add(new NetworkDestination("lattice-55", 5555));
        dests.add(new NetworkDestination("lattice-56", 5555));
        dests.add(new NetworkDestination("lattice-57", 5555));
        dests.add(new NetworkDestination("lattice-58", 5555));
        dests.add(new NetworkDestination("lattice-59", 5555));
        dests.add(new NetworkDestination("lattice-60", 5555));
        dests.add(new NetworkDestination("lattice-61", 5555));
        dests.add(new NetworkDestination("lattice-62", 5555));
        dests.add(new NetworkDestination("lattice-63", 5555));
        dests.add(new NetworkDestination("lattice-64", 5555));
        dests.add(new NetworkDestination("lattice-65", 5555));
        dests.add(new NetworkDestination("lattice-66", 5555));
        dests.add(new NetworkDestination("lattice-67", 5555));
        dests.add(new NetworkDestination("lattice-68", 5555));
        dests.add(new NetworkDestination("lattice-69", 5555));
        dests.add(new NetworkDestination("lattice-70", 5555));
        dests.add(new NetworkDestination("lattice-71", 5555));
        dests.add(new NetworkDestination("lattice-72", 5555));
        dests.add(new NetworkDestination("lattice-73", 5555));
        dests.add(new NetworkDestination("lattice-74", 5555));
        dests.add(new NetworkDestination("lattice-75", 5555));
        dests.add(new NetworkDestination("lattice-76", 5555));
        dests.add(new NetworkDestination("lattice-77", 5555));
    }

    public void start() throws Exception {
        DebugEvent de = new DebugEvent(new byte[1000]);
        GalileoMessage msg = reactor.wrapEvent(de);
        router.broadcastMessage(dests, msg);

        while (true) {
            reactor.processNextEvent();
            if (replies >= dests.size()) {
                System.out.println("Complete!");
                return;
            }
        }
    }

    @EventHandler
    public void handleReply(DebugEvent event, EventContext context) {
        replies++;
        System.out.println("Received reply from " + context.getSource());
    }

    public static void main(String[] args) throws Exception {
        DebugClient dc = new DebugClient();
        dc.start();
    }
}
