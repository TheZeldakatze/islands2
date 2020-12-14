package de.victorswelt.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import de.victorswelt.Utils;

public class Client implements Runnable {
	Server server;
	Socket socket;
	Thread thread;
	private DataInputStream data_in;
	private DataOutputStream data_out;
	
	public Client(Server s, Socket sock) {
		server = s;
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
			data_in  = new DataInputStream(is);
			data_out = new DataOutputStream(os);
			
			Utils.encodeVarInt(data_out, 9);
			Utils.encodeVarInt(data_out, 257);
			Utils.encodeVarInt(data_out, 655456);
			Utils.encodeVarInt(data_out, Integer.MAX_VALUE);
			
			while(true) {
				
			}
		} catch(Exception e) {
			
		}
		
		// remove the client
		server.removeClient(this);
	}
	
	public void kick() {
		
	}
	
	 static byte[] createPacket(byte packetType, String content) {
		short length = (short) content.length();
		byte byteLength[] = {
			((byte) length),
			((byte) (length << 8)),
		};
		
		short decodedLength = (short) (((short) ((char) byteLength[0])) | ((short) (byteLength[1] >> 8)));
		
		System.out.println(length + ";" + ((short) (char) byteLength[0]) + " " + byteLength[1] + ";" + decodedLength);
		
		return null;
	}
}
