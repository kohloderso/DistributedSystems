package hw5;

import java.rmi.RemoteException;

public class ClientThread implements Runnable {
        Service service;
        int id;
        
        ClientThread(Service service, int id) {
            this.service = service;
            this.id = id;
        }
        
        @Override
        public void run() {
            System.out.println(id + " started");
            try {
                service.longOp(0);
                System.out.println(id + " longOp done");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }