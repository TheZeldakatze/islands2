package de.victorswelt.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import de.victorswelt.Utils;

public class Client implements Runnable {
	Server server;
	Socket socket;
	Thread thread;
	String username;
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
			
			// get the connection request type
			switch(data_in.readByte()) {
				case PacketType.CONNECTION_TYPE_JOIN: {
					
				} break;
				
				case PacketType.CONNECTION_TYPE_INFO: {
					
					socket.close();
				} break;
				
				default: {
					System.out.println("[WARN] Invalid connection type recieved from " + socket.getInetAddress());
					socket.close();
				}
			}
			
			// write the protocol version
			data_out.writeInt(Server.PROTOCOL_VERSION);
			
			// read the username
			int length = Utils.decodeVarNum(data_in);
			byte buffer[] = new byte[length];
			data_in.readFully(buffer);
			username = new String(buffer);
			System.out.println("[info] " + socket.getInetAddress() + " registered with username " + username);
			
			while(true) {
				byte packetType = data_in.readByte();
				
				switch(packetType) {
					case PacketType.CLIENT_GET_MAP: {
						String map = server.getMap().getMapString();
						synchronized (data_out) {
							Utils.encodeVarInt(data_out, map.length());
							data_out.write(map.getBytes());
						}
					} break;
					
					case PacketType.CLIENT_ADD_TRANSPORT: {
						int source = Utils.decodeVarNum(data_in);
						int target = Utils.decodeVarNum(data_in);
						server.getMap().addTransport(source, target);
					} break;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// make sure the socket is closed
		try {socket.close();} catch(Exception e) {};
		
		// remove the client
		server.removeClient(this);
	}
	
	public void sendIslandPopulationUpdate(int island, int population) {
		try {
			synchronized (data_out) {
				data_out.writeByte(PacketType.SERVER_SET_POPULATION);
				Utils.encodeVarInt(data_out, island);
				Utils.encodeVarInt(data_out, population);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMapDownloadRequest() {
		try {
			synchronized (data_out) {
				data_out.writeByte(PacketType.SERVER_REQUEST_MAP_DOWNLOAD);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendIslandPopulationAndTeamUpdate(int island, int population, int team) {
		try {
			synchronized (data_out) {
				data_out.writeByte(PacketType.SERVER_SET_POPULATION_AND_TEAM);
				Utils.encodeVarInt(data_out, island);
				Utils.encodeVarInt(data_out, population);
				Utils.encodeVarInt(data_out, team);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addTransport(int source, int target) {
		try {
			synchronized (data_out) {
				data_out.writeByte(PacketType.SERVER_ADD_TRANSPORT);
				Utils.encodeVarInt(data_out, source);
				Utils.encodeVarInt(data_out, target);
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void kick() {
		
	}
}
