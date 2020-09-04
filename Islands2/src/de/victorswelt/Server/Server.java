package de.victorswelt.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server {
	ArrayList clients;
	ServerSocket socket;
	
	public static void main() {
		
	}
	
	private Server() {
		try {
			socket = new ServerSocket(53456);
			clients = new ArrayList();
			System.out.println("Listening on Port: " + 53456);
			
			// continously listen for new connections
			while(true) {
				clients.add(new Client(socket.accept()));
			} 
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
