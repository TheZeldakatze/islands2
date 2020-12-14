package de.victorswelt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MultiplayerLevel extends LevelAbstract {
	private boolean loading = true, gameover, listenerRunning = true;
	private Socket socket;
	private DataInputStream data_in;
	private DataOutputStream data_out;
	
	public MultiplayerLevel(final InetSocketAddress s) {
		playerTeam = 0;
		transports = new ArrayList();
		teams = new ArrayList();
		
		Obstacle obs[] = {};
		obstacles = obs;
		
		Island isl[] = {};
		islands = isl;
		
		// create a listener thread
		new Thread(new Runnable() {
			public void run() {
				networkListenerThread(s);
			}
		}, "Multiplayer Connection Thread").start();
		
	}
	
	public boolean isLoading() {
		return loading;
	}

	public boolean isGameOver() {
		return gameover;
	}
	
	public Island[] getIslands() {
		return islands;
	}
	
	public ArrayList getTransports() {
		return transports;
	}
	
	public void disconnect() {
		
	}
	
	public Obstacle[] getObstacles() {
		return obstacles;
	}
	
	public Island getIsland(int i) {
		if(i<0 || i>=islands.length)
			return null;
		return islands[i];
	}
	
	public void update() {
		
		// update the transports
		for(int i = 0; i<transports.size(); i++)
			if(((Transport) transports.get(i)).update())
				transports.remove(i);
		
		// update the teams (and AIs)
		for(int j = 0; j<teams.size(); j++)
			((Team) teams.get(j)).update(this);
		
		// check for a collision between  an obstacle and an island
		for(int i = 0; i<obstacles.length; i++) {
			Obstacle o = obstacles[i];
			for(int j = 0; j<transports.size(); j++) {
				Transport p = (Transport) transports.get(j);
				
				if(Utils.checkCollision(o.x, o.y, 32, 32, (int) p.x, (int) p.y, SpriteManager.TRANSPORT_WIDTH, SpriteManager.TRANSPORT_HEIGHT)) {
					transports.remove(j);
				}
			}
		}	
	}
	
	private void networkListenerThread(InetSocketAddress networkAddress) {
		// initialize
		// create a socket
		try {
			System.out.println("connecting!");
			// create a socket
			socket = new Socket(networkAddress.getHostString(), networkAddress.getPort());
			data_in  = new DataInputStream(socket.getInputStream());
			data_out = new DataOutputStream(socket.getOutputStream());
			System.out.println("connected!");
			
			// end the loading phase
			loading = false;
			
			// read a var int
			// TODO remove
			System.out.println("VarInt content: " + Utils.decodeVarNum(data_in));
			System.out.println("VarInt content: " + Utils.decodeVarNum(data_in));
			System.out.println("VarInt content: " + Utils.decodeVarNum(data_in));
			System.out.println("VarInt content: " + Utils.decodeVarNum(data_in));
			
			
			// listen
			while(true) {
				
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * the method creates a troop transport
	 **/
	public void addTransport(int source, int target) {
		
	}
}
