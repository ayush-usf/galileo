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

package edu.colostate.cs.galileo.samples;

import java.util.List;

import edu.colostate.cs.galileo.client.EventPublisher;
import edu.colostate.cs.galileo.comm.GalileoEventMap;
import edu.colostate.cs.galileo.comm.QueryEvent;
import edu.colostate.cs.galileo.comm.QueryResponse;
import edu.colostate.cs.galileo.dataset.feature.Feature;
import edu.colostate.cs.galileo.event.BasicEventWrapper;
import edu.colostate.cs.galileo.event.EventWrapper;
import edu.colostate.cs.galileo.graph.Path;
import edu.colostate.cs.galileo.net.ClientMessageRouter;
import edu.colostate.cs.galileo.net.GalileoMessage;
import edu.colostate.cs.galileo.net.MessageListener;
import edu.colostate.cs.galileo.net.NetworkEndpoint;
import edu.colostate.cs.galileo.query.Expression;
import edu.colostate.cs.galileo.query.Operation;
import edu.colostate.cs.galileo.query.Query;

public class QueryDemo implements MessageListener {

    private static GalileoEventMap eventMap = new GalileoEventMap();
    private static EventWrapper wrapper = new BasicEventWrapper(eventMap);

    @Override
    public void onConnect(NetworkEndpoint endpoint) { }

    @Override
    public void onDisconnect(NetworkEndpoint endpoint) { }

    @Override
    public void onMessage(GalileoMessage message) {
        try {
            QueryResponse response = (QueryResponse) wrapper.unwrap(message);
            System.out.println(response.getResults().size()
                    + " results received");

            System.out.println("First 5 results:");
            List<Path<Feature, String>> results = response.getResults();
            int limit = 5;
            if (results.size() < 5) {
                limit = results.size();
            }
            int counter = 0;
            for (Path<Feature, String> path : results) {
                if (path.size() > 1) {
                    System.out.println(path);
                    counter++;
                }
                if (counter == limit) {
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: galileo.samples.Query host port");
            System.exit(1);
        }
        String serverHostName = args[0];
        int serverPort = Integer.parseInt(args[1]);
        NetworkEndpoint server
            = new NetworkEndpoint(serverHostName, serverPort);

        QueryDemo qd = new QueryDemo();

        ClientMessageRouter messageRouter = new ClientMessageRouter();
        messageRouter.addListener(qd);

        /* Each expression chained together in an operation produces a logical
         * AND. Each operation added to the query is like a logical OR. */

        /* This query checks for total_precipitation values equal to 83.30055 */
        Query q = new Query();
        Operation o = new Operation(new Expression(
                    "==", new Feature("temperature", 0.94320774f)));
        q.addOperation(o);

        /* This query checks for total_precipitation = 83.30055, OR for
         * wind_speed values less than 99.8 */
        Query q2 = new Query();
        Operation o1 = new Operation(new Expression(
                    "==", new Feature("temperature", 0.94320774f)));
        Operation o2 = new Operation(new Expression(
                    "<", new Feature("wind_speed", 30.0f)));
        q2.addOperation(o1);
        q2.addOperation(o2);

        /* This query checks for total_precipitation = 83.30055, AND a
         * condensation value less than 50. */
        Query q3 = new Query();
        Operation op = new Operation();
        Expression e1 = new Expression(
                ">", new Feature("temperature", 32.0f));
        Expression e2 = new Expression(
                "<=", new Feature("condensation", 50.0f));
        op.addExpressions(e1, e2);
        q3.addOperation(op);
 

        System.out.println("Query 1: " + q);
        System.out.println("Query 2: " + q2);
        System.out.println("Query 3: " + q3);

        QueryEvent qe1 = new QueryEvent("query1", q);
        QueryEvent qe2 = new QueryEvent("query2", q2);
        QueryEvent qe3 = new QueryEvent("query3", q3);

        messageRouter.sendMessage(server, EventPublisher.wrapEvent(qe1));
        messageRouter.sendMessage(server, EventPublisher.wrapEvent(qe2));
        messageRouter.sendMessage(server, EventPublisher.wrapEvent(qe3));
    }
}
