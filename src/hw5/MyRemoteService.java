package hw5;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class MyRemoteService implements Service {

    public static void main(String[] args) {
        String name = "MyRemoteService";
        //System.setProperty("java.security.policy", "file:./server.policy");
        try {

            Service myService = new MyRemoteService();

            // export remote service to make it available to accept calls from clients
            Service stub = (Service) UnicastRemoteObject.exportObject(myService, 0);
            Registry registry = LocateRegistry.createRegistry(10000);
            registry.rebind(name, stub);
            System.out.println("bound");
            System.out.println(registry.list().toString());
            System.out.println(registry.lookup("MyRemoteService"));
        } catch (Exception e) {
            System.err.println("buhu exception:");
            e.printStackTrace();
        }
    }

    @Override
    public int plus(int x, int y) throws RemoteException {
        return x + y;
    }

    @Override
    public int minus(int x, int y) throws RemoteException {
        return x - y;
    }

    @Override
    public int multiply(int x, int y) throws RemoteException {
        return x * y;
    }

    @Override
    public int lucas(int x) throws RemoteException {
        return lucasTailRec(2, 1, x);
    }

    @Override
    public int longOp(int x) throws RemoteException {
        try {
            System.out.println("called longOp");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("longOp done");
        return 42;
    }

    @Override
    public <T> T computationTask(IComputationTask<T> t) throws RemoteException {
        return t.executeTask();
    }

    private static int lucasTailRec(final int a, final int b, final int n)
    {
        return n < 1 ? a : n == 1 ?  b : lucasTailRec(b, a + b, n - 1);
    }

}
