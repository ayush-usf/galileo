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

package galileo.samples;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import galileo.client.EventPublisher;
import galileo.comm.QueryEvent;
import galileo.dataset.feature.Feature;
import galileo.net.ClientMessageRouter;
import galileo.net.NetworkDestination;
import galileo.query.Expression;
import galileo.query.Operation;
import galileo.query.Operator;
import galileo.query.Query;

public class RandomQuery {

    public static Random random = new Random();

    public static Operator randomOperator() {
        int i = random.nextInt(Operator.values().length - 2) + 1;
        return Operator.fromInt(i);
    }

    public static Feature randomFeature(String name, float min, float max) {
        float diff = max - min;
        float rand = random.nextFloat() * diff;
        float value = min + rand;
        return new Feature(name, value);
    }

    public static Query randomQuery() {
        Query q = new Query();

        List<Feature> features = new ArrayList<>();
        features.add(randomFeature("pressure", 1, 100));
        features.add(randomFeature("visibility", 1, 100));
        features.add(randomFeature("total_precipitation", 1, 100));
        features.add(randomFeature("temperature_surface", 1, 100));


        List<Expression> expressions = new ArrayList<>();
        for (Feature f : features) {
            expressions.add(new Expression(randomOperator(), f));
        }

        Operation op = new Operation(expressions.toArray(
                    new Expression[expressions.size()]));

        q.addOperation(op);

        return q;
    }

    public static void main(String[] args)
    throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: galileo.samples.RandomQuery host port");
            System.exit(1);
        }
        String serverHostName = args[0];
        int serverPort = Integer.parseInt(args[1]);

        ClientMessageRouter messageRouter = new ClientMessageRouter();
        NetworkDestination server = new NetworkDestination(
                serverHostName, serverPort);
        messageRouter.connectTo(server);

        byte[] id = new byte[1024];
        random.nextBytes(id);
        BigInteger bi = new BigInteger(id);

        Query q = randomQuery();
        QueryEvent qe = new QueryEvent(bi.toString(16), q);
        messageRouter.sendMessage(server, EventPublisher.wrapEvent(qe));
    }
}
