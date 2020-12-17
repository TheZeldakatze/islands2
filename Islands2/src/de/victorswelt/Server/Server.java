package de.victorswelt.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server implements Runnable {
	static Server server;
	ArrayList clients;
	ServerSocket socket;
	Thread server_thread;
	int port = 53456;
	boolean running = true;
	
	public static void main(String args[]) {
		try {
			server = new Server();
		} catch (IOException e) {
			System.out.println("[Error] could not create server! Exiting.");
			e.printStackTrace();
			System.exit(1);
		}
		
		// listen for console commands
		Scanner scanner = new Scanner(System.in);
		while(server.running && scanner.hasNextLine()) {
			String line = scanner.nextLine();
			
			// parse the input
			// I can't use a switch statement here since I want to use String.startsWith(String)
			if(line.startsWith("stop")) {
				server.closeServer();
			}
			else
				System.out.println("[INFO] command \"" + line + "\" not known!");
		}
		
		// close the scanner
		scanner.close();
		
		// exit
		System.exit(0);
	}
	
	private Server() throws IOException {
		// initialize the connection
		socket = new ServerSocket(port);
		clients = new ArrayList();
		System.out.println("[INFO] Listening on Port: " + port);
		
		// start the listener thread 
		server_thread = new Thread(this);
		server_thread.start();
	}
	
	private void closeServer() {
		running = false;
		
		// kick every client
		for(int i = 0; i<clients.size(); i++) {
			Client c = (Client) clients.get(i);
			c.kick();
		}
		
		// close the server
		try {socket.close();} catch (IOException e) {e.printStackTrace();}
	}
	
	public void removeClient(Client c) {
		System.out.println("[DISCONNECT] " + c.socket.getInetAddress().getHostAddress() + " disconnected!");
		clients.remove(c);
	}

	public void run() {
		try {
			// continously listen for new connections
			while(true) {
				Client c = new Client(this, socket.accept());
				clients.add(c);
				
				System.out.println("[CONNECT] " + c.socket.getInetAddress().getHostAddress() + " connected!");
			} 
			
		} catch (IOException e) {
			if(!running) {
				// end this thread
				return;
			}
			else
				e.printStackTrace();
		}
	}

}
