package hw9.server;

import hw9.sockets.Client;
import hw9.sockets.Protocol;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

/**
 * Created by christina on 23.05.16.
 */
public class Benchmark {
    int arg1 = 3548;
    int arg2 = 4354;


    public void benchmarkWS() {
        System.out.println("benchmarking web service");
        URL url = null;
        try {
            url = new URL("http://localhost:8001/hw9/server?wsdl");
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            System.err.println("URL ERROR");
        }

        QName qname = new QName("http://server.hw9/", "CompImplService");

        Service service = Service.create(url, qname);

        Comp benchmark = service.getPort(Comp.class);

        long time1=System.currentTimeMillis();
        for(int i=0; i<100; i++){
            benchmark.add(arg1, arg2);
        }
        long time2 = System.currentTimeMillis();
        System.out.println("1 Addition: " + (double)(time2 - time1)/100 + " ms");
    }

    public void benchmarkDirect() {
        System.out.println("benchmarking direct call to member function");
        long time1=System.currentTimeMillis();
        for(int i=0; i<100; i++){
            add(arg1, arg2);
        }
        long time2 = System.currentTimeMillis();
        System.out.println("1 Addition: " + (double)(time2 - time1)/100 + " ms");
    }


    public void benchmarkRMI() {
        String name = "MyRemoteService";
        Registry registry = null;
        hw5.Service service = null;
        try {
            registry = LocateRegistry.getRegistry("localhost", 10000);
            service = (hw5.Service) registry.lookup(name);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }


        System.out.println("benchmarking RMI");
        long time1=System.currentTimeMillis();
        for(int i=0; i<100; i++){
            try {
                service.plus(arg1, arg2);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        long time2 = System.currentTimeMillis();
        System.out.println("1 Addition: " + (double)(time2 - time1)/100 + " ms");
    }

    public void benchmarkSockets() {
        System.out.println("benchmarking sockets");
        Client client = new Client();
        long time1=System.currentTimeMillis();
        for(int i=0; i<100; i++){
            client.request(new InetSocketAddress("localhost", Protocol.SERVER_PORT), "+", arg1, arg2);
        }
        long time2 = System.currentTimeMillis();
        System.out.println("1 Addition: " + (double)(time2 - time1)/100 + " ms");
    }

    public int add(int x, int y) {
        return x+y;
    }
}
