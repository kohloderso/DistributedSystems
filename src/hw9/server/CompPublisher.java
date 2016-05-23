package hw9.server;

import javax.xml.ws.Endpoint;

public class CompPublisher {
	
	public static void main(String[] args){
		Endpoint endpoint = Endpoint.publish("http://localhost:8001/hw9/server", new CompImpl());
		System.out.println(endpoint);
	}
}