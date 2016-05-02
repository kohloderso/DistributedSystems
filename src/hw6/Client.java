package hw6;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


public class Client {

    private String answer;

    public static void main(String args[]) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 10000);
            Service service = (Service) registry.lookup("FutureService");

            List<IJob<String>> jobs = new ArrayList<IJob<String>>();
            for(int i = 0; i < 15; i++) {
                System.out.println("Client submitting job " + i);
                Callable<String> test = new CallableTest();
                IJob<String> job = service.submit(test);

                if(job == null) {
                    System.out.println(i + " couldn't be submitted");
                }
                else jobs.add(job);
            }

            while(!jobs.isEmpty()) {
                if(jobs.get(0).isDone()) {
                    System.out.println("Done");
                    jobs.remove(0);
                }
            }

            System.out.println("all jobs done");

            Callable<String> test = new CallableTest();
            IJob<String> job = service.submit(test);
            System.out.println("one last job...");
            if(job == null) {
                System.out.println("couldn't be submitted");
            } else {
                System.out.println("submitted!");
            }

            
        } catch (Exception e) {
            System.err.println("bind exception:");
            e.printStackTrace();
        }

    }

}
