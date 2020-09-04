package de.victorswelt.Server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client implements Runnable {
	Socket socket;
	Thread thread;
	
	public Client(Socket sock) {
		socket = sock;
		
		// create a new thread
		thread = new Thread(this);
		thread.start();
	}

	public void run() {
		try {
			// get the i/o streams
			InputStream is  = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			
			//
			os.write('b');
			
		} catch(Exception e) {
			
		}
	}
}
