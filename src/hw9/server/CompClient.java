package hw9.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import javax.xml.namespace.QName;

import javax.xml.ws.Service;

public class CompClient implements Runnable{

	private String name;
	
	public CompClient(String name){
		this.name = name;
	}
	
	public static void main(String[] args) throws Exception{
		Thread client1 = new Thread(new CompClient("Client 1"));
		Thread client2 = new Thread(new CompClient("Client 2"));
		Thread client3 = new Thread(new CompClient("Client 3"));
				
		//client1.start();
		//client2.start();
		//client3.start();

//		client1.join();
//		client2.join();
//		client3.join();
		
		System.out.println("--------Starting Benchmark----------");

		CompClient client4 = new CompClient("Client 4");
		client4.benchmark();
		
	}

	public void benchmark(){
		Benchmark b = new Benchmark();
		b.benchmarkWS();
		b.benchmarkDirect();
		b.benchmarkRMI();
		b.benchmarkSockets();
	}
	
	@Override
	public void run() {
		Random rand = new Random();
		URL url = null;
		try {
			url = new URL("http://localhost:8001/hw9/server?wsdl");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			System.err.println("URL ERROR");
		}

		// qualified name
		QName qname = new QName("http://server.hw9/", "CompImplService");
		
		Service service = Service.create(url, qname);
		
		Comp comp = service.getPort(Comp.class);
		
		System.out.println(name + " Test add with 12 and 34, result: " + comp.add(12, 34));
		System.out.println(name + " Test sub with 12 and 34, result: " + comp.sub(12, 34));
		System.out.println(name + " Test mul with 12 and 34, result: " + comp.mul(12, 34));
		System.out.println(name + " Test Lucas function with parameter 150, result: " + comp.lucas(150));
		
		while(true){
			int op = rand.nextInt(4);
			int param1 = rand.nextInt(10000);
			int param2 = rand.nextInt(10000);
			int param3 = rand.nextInt(100);
			
			
			switch(op){
			case 0:
				System.out.println(name + " Add " + param1 + " and " + param2 + " is " + comp.add(param1, param2));
				break;
			case 1:
				System.out.println(name + " Sub " + param1 + " and " + param2 + " is " + comp.sub(param1, param2));
				break;
			case 2:
				System.out.println(name + " Mult " + param1 + " and " + param2 + " is " + comp.mul(param1, param2));
				break;
			case 3:
				System.out.println(name + " Lucas of " + param3 + " is " + comp.lucas(param3));
				break;
			}
			
			if(rand.nextInt(100) == 0)
				break;
			
		}
		
	}

}
