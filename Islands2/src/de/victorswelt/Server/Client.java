package de.victorswelt.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import de.victorswelt.NetworkPacketReader;
import de.victorswelt.Utils;

public class Client implements Runnable {
	Server server;
	Socket socket;
	Thread thread;
	private DataInputStream data_in;
	private DataOutputStream data_out;
	private NetworkPacketReader packet;
	
	public Client(Server s, Socket sock) {
		server = s;
		socket = sock;
		packet = new NetworkPacketReader();
		
		// create a new thread
		thread = new Thread(this);
		thread.start();
	}

	public void run() {
		try {
			// get the i/o streams
			InputStream is  = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			data_in  = new DataInputStream(is);
			data_out = new DataOutputStream(os);
			
			while(true) {
				packet.read(data_in);
				
				switch(packet.type) {
					case PacketType.CLIENT_GET_MAP: {
						String map = "i 405 151 0 40\ni 39 56 0 40\ni 202 250 0 40\ni 538 240 0 40\ni 342 58 2 40\ni 223 117 2 40\ni 377 244 2 40\ni 531 152 2 40\ni 41 253 2 40\ni 176 24 1 50";
						System.out.println("[INFO] sending map:"+map.length()+":"+map);
						Utils.encodeVarInt(data_out, map.length());
						data_out.writeUTF(map);
					} break;
				}
			}
		} catch(Exception e) {
			
		}
		
		// remove the client
		server.removeClient(this);
	}
	
	public void kick() {
		
	}
}
