package hw5;

import java.rmi.RemoteException;
import java.util.Random;

import org.w3c.dom.ranges.RangeException;

public class ClientThread implements Runnable {
        Service service;
        int id;
        int numberOfRequests;
        
        ClientThread(Service service, int id, int numberOfRequests) {
            this.service = service;
            this.id = id;
            this.numberOfRequests = numberOfRequests;
        }
        
        @Override
        public void run() {
            System.out.println(id + " started");
            System.out.println("Client <"+id+"> sends "+numberOfRequests+" requests ...");
            try {
                
            	for(int i = 0; i < numberOfRequests; i++){
            		rndOprndNumb();
            		Thread.sleep(200);
            	}
            	System.out.println();
                System.out.println("******Client <"+ id +"> all requests done****** \n");
            } catch (RemoteException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    

        public void rndOprndNumb () throws RemoteException{
        	
        	Random rnd = new Random();
        	int a = rnd.nextInt(15);
        	int b = rnd.nextInt(15);
        	int op = rnd.nextInt(5);
        	     	
        	switch(op){
        	case 0:
        		System.out.println("Plus "+a+" + "+b+" = "+service.plus(a, b)+" ... Client<"+id+">");
        		break;
        	case 1:
        		System.out.println("Minus "+a+" - "+b+" = "+service.minus(a, b)+" ... Client<"+id+">");
        		break;
        	case 2:
        		System.out.println("Multiply "+a+" * "+b+" = "+service.multiply(a, b)+" ... Client<"+id+">");
        		break;
        	case 3:
        		System.out.println("Lucas " + a + " = "+service.lucas(a)+" ... Client<"+id+">");
        		break;
        	case 4:
        		System.out.println("Deep Thought " + a + " = "+service.longOp(a)+" ... Client<"+id+">");
        		break;
        	}
        
}
}