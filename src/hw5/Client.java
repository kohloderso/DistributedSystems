package hw5;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Client {

    public static void main(String args[]) {
        try {
            String name = "MyRemoteService";
            Registry registry = LocateRegistry.getRegistry("localhost", 10000);
            Service service = (Service) registry.lookup(name);

//            ExecutorService ex = Executors.newFixedThreadPool(6);
//            for(int i = 0; i < 6; i++) {
//                ex.execute(new ClientThread(service, i));
//            }
//            int test = service.minus(3,2);
//            System.out.println("Test: " + test);
//            int longOp = service.longOp(0);
//            System.out.println("longOp: " + longOp);
//            int minus = service.minus(8,4);
//            System.out.println(minus);


            IComputationTask<Integer>  testOp = new TestOp();
            int res = service.computationTask(testOp);
            System.out.println("computation task returns: " + res);
            IComputationTask<String>  testOp2 = new TestOpNew();
            String res2 = service.computationTask(testOp2);
            System.out.println("computation task returns: " + res2);
            //ex.shutdown();
        } catch (Exception e) {
            System.err.println("bind exception:");
            e.printStackTrace();
        }




    }



}
