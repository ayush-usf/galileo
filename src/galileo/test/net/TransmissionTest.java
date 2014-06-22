package galileo.test.net;

import galileo.net.Transmission;

public class TransmissionTest {

    public class OtherThread implements Runnable {

        private Transmission t;

        public OtherThread(Transmission t) {
            this.t = t;
        }

        public void run() {
            System.out.println("Sleeping for 5 seconds");
            try {
                Thread.sleep(5000);
            } catch (Exception e) { e.printStackTrace(); }
            //t.setFinished();
        }
    }

    public TransmissionTest() throws Exception {
        Transmission t = new Transmission();
        OtherThread ot = new OtherThread(t);
        new Thread(ot).start();
        t.finish();
        System.out.println("Transmission complete");
    }

    public static void main(String[] args) throws Exception {
        TransmissionTest tt = new TransmissionTest();

    }
}
