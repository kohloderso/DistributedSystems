package hw5;

import java.math.BigDecimal;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class Client {

    public static void main(String args[]) {
        try {
            String name = "MyRemoteService";
            Registry registry = LocateRegistry.getRegistry("localhost", 10000);
            Service service = (Service) registry.lookup(name);
            int test = service.minus(3,2);
            System.out.println("Test: " + test);
        } catch (Exception e) {
            System.err.println("bind exception:");
            e.printStackTrace();
        }


    }

}
