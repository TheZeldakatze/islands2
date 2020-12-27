package de.victorswelt.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import de.victorswelt.FastMath;
import de.victorswelt.Island;

public class Server implements Runnable {
	public static final int PROTOCOL_VERSION = 3;
	
	static Server server;
	List clients;
	ServerSocket socket;
	Thread server_thread, game_thread;
	int port = 53456;
	boolean running = true;
	Map map;
	
	public static void main(String args[]) {
		FastMath.init();
		
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
			} else if(line.startsWith("listislands")) {
				Island islands[] = server.map.getIslands();
				for(int i = 0; i<islands.length; i++) {
					System.out.println("Island #" + i + " has a population of " + islands[i].population + " and is in team " + islands[i].team);
				}
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
		clients = Collections.synchronizedList(new ArrayList());
		System.out.println("[INFO] Listening on Port: " + port);
		
		// initialize the map
		map = new Map(this);
		
		// start the listener thread 
		server_thread = new Thread(this);
		server_thread.start();
		
		game_thread = new Thread(new Runnable() {
			
			public void run() {
				try {
					gameUpdateThread();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		game_thread.start();
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
	
	private void gameUpdateThread() throws InterruptedException {
		while(true) {
			map.update();
			
			Thread.sleep(12);
		}
	}
	
	public Map getMap() {
		return map;
	}
	
	public List getClients() {
		return clients;
	}
}
