package hw5;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Client2 implements Listener, Serializable {

    private static int id;
    private String answer;
    private Service service;

    public static void main(String args[]) {
            Client2 client2 = new Client2();

    }

    public Client2() {
        try {
            String name = "MyRemoteService";
            Registry registry = LocateRegistry.getRegistry("localhost", 10000);
            service = (Service) registry.lookup(name);

            String listenerName = "Listener" + id++;

            // export
            Listener stub = (Listener) UnicastRemoteObject.exportObject(this, 0);
            registry.rebind(listenerName, stub);
            System.out.println("bound listener");
            for(String entry: registry.list()) {
                System.out.println(entry);
            }

            askFundamentalQuestion();

            while(answer == null) {
                System.out.println("No answer yet. You could do something useful in the meantime.");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            System.out.println(answer);

            registry.unbind(listenerName);
            UnicastRemoteObject.unexportObject(this, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void askFundamentalQuestion() {
        // ask fundamental question
        try {
            service.deepThought("What will the weather be like?", this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onThinkingCompleted(String answer) throws RemoteException {
        this.answer = answer;
    }
}
