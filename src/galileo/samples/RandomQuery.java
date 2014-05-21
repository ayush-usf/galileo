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
import galileo.comm.StorageRequest;
import galileo.dataset.feature.Feature;
import galileo.net.ClientMessageRouter;
import galileo.net.GalileoMessage;
import galileo.net.MessageListener;
import galileo.net.NetworkDestination;
import galileo.query.Expression;
import galileo.query.Operation;
import galileo.query.Operator;
import galileo.query.Query;
import galileo.util.PerformanceTimer;

public class RandomQuery {

//    private static final double STORAGE_RATIO = 0.25;
    private static boolean noNotEqual = true;
    private static boolean reverseBigRanges = true;

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
        features.add(randomFeature("humidity", 1, 100));
        features.add(randomFeature("wind_direction", 1, 100));
        features.add(randomFeature("condensation", 1, 100));
        features.add(randomFeature("temperature", 1, 100));

        List<Expression> expressions = new ArrayList<>();
        for (Feature f : features) {
            Operator op = randomOperator();
            if (noNotEqual == true && op == Operator.NOTEQUAL) {
                op = Operator.EQUAL;
            }
            if (reverseBigRanges == true) {
                if ((op == Operator.GREATER || op == Operator.GREATEREQUAL)
                        && f.getFloat() <= 35.0f) {
                    op = Operator.LESS;
                }
                if ((op == Operator.LESS || op == Operator.LESSEQUAL)
                        && f.getFloat() >= 65.0f) {
                    op = Operator.GREATER;
                }
            }
            expressions.add(new Expression(op, f));
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

        class Listener implements MessageListener {

            private int counter;
            private PerformanceTimer pt = new PerformanceTimer();

            @Override
            public void onConnect(NetworkDestination endpoint) { }

            @Override
            public void onDisconnect(NetworkDestination endpoint) { }

            @Override
            public void onMessage(GalileoMessage message) {
                if (counter == 0) {
                    pt.start();
                }
                counter++;
                if (counter == 10000) {
                    pt.stopAndPrint();
                    System.exit(0);
                }
                //System.out.println(counter++);
            }
        }
        messageRouter.addListener(new Listener());

        for (int i = 0; i < 10000; ++i) {
            byte[] id = new byte[1024];
            random.nextBytes(id);
            BigInteger bi = new BigInteger(id);

            Query q = randomQuery();
            QueryEvent qe = new QueryEvent(bi.toString(16), q);
            messageRouter.sendMessage(server, EventPublisher.wrapEvent(qe));
        }
        System.out.println("Test complete.");
        messageRouter.shutdown();
    }
}
