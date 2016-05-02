package hw6;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.concurrent.*;


public class MyRemoteService implements Service {
    private final int MAX_TASKS = 10;
    private int activeTasks = 0;
    private Random rnd = new Random();
    private ExecutorService executorService;
    private int threadPoolSize = MAX_TASKS;



    public static void main(String[] args) {
        String name = "FutureService";
        //System.setProperty("java.security.policy", "file:./server.policy");
        try {

            Service myService = new MyRemoteService();

            // export remote service to make it available to accept calls from clients
            Service stub = (Service) UnicastRemoteObject.exportObject(myService, 0);
            Registry registry = LocateRegistry.createRegistry(10000);
            registry.rebind(name, stub);
            System.out.println("bound");
            for(String entry: registry.list()) {
                System.out.println(entry);
            }

        } catch (Exception e) {
            System.err.println("buhu exception:");
            e.printStackTrace();
        }
    }

    public MyRemoteService() {
        executorService = Executors.newFixedThreadPool(threadPoolSize);

    }

    private synchronized boolean newTaskIfPossible() {
        if(activeTasks >= MAX_TASKS) {
            return false;
        }
        ++activeTasks;
        return true;
    }

    private synchronized void taskCompleted() {
        assert(activeTasks > 0);    // kann eigentlich nie vorkommen
        --activeTasks;
    }


    @Override
    public <T> IJob<T> submit(Callable<T> callable) throws RemoteException {
        if(!newTaskIfPossible()) return null;

        Job<T> job = new Job<T>();
        executorService.submit((Runnable) () -> {
            try {
                job.onJobDone(callable.call()); //once the answer is computed, set it in the job via callback
                taskCompleted();
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
        return job;
    }

}
