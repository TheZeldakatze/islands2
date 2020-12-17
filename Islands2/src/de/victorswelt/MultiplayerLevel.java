package de.victorswelt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import de.victorswelt.Server.PacketType;

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
	
	private void downloadMap() throws NumberFormatException, IOException {
		Utils.createPacket(data_out, PacketType.CLIENT_GET_MAP, "");
		int length = Utils.decodeVarNum(data_in);
		byte buf[] = new byte[length];
		data_in.readFully(buf);
		String text = new String(buf);
		System.out.println(length + ":" + text);
		// parse everything
		// get the lines
		String lines[] = text.split("\n");
		
		// create the islands
		ArrayList islands = new ArrayList();
		ArrayList obstacles = new ArrayList();
		for(int i = 0; i<lines.length; i++) {
			// split the line
			String parts[] = lines[i].split(" ");
			
			// check if it is an island
			if(parts[0].equalsIgnoreCase("i")) {
				// check for the length
				if(parts.length == 5) {
					try {
						int x          = Integer.parseInt(parts[1]);
						int y          = Integer.parseInt(parts[2]);
						int team       = Integer.parseInt(parts[3]);
						int population = Integer.parseInt(parts[4]);
						
						islands.add(new Island(x, y, team, population));
					} catch(Exception e) {
						System.out.println("Level: malformed line: " + i + " (not a number)");
					}
				}
				else
					System.out.println("Level: malformed line: " + i + " (invalid length)");
			}
			
			// check if it is an obstacle
			else if(parts[0].equalsIgnoreCase("o")) {
				if(parts.length == 3) {
					try {
						int x = Integer.parseInt(parts[1]);
						int y = Integer.parseInt(parts[2]);
						obstacles.add(new Obstacle(x, y));
					} catch(Exception e) {
						System.out.println("Level: malformed line: " + i + " (invalid length)");
					}
				}
			}
			
		}
		
		// create an array
		this.islands = new Island[islands.size()];
		for(int i = 0; i<this.islands.length;i++)
			this.islands[i] = (Island) islands.get(i);
		this.obstacles = new Obstacle[obstacles.size()];
		for(int i = 0; i<this.obstacles.length; i++)
			this.obstacles[i] = (Obstacle) obstacles.get(i);
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
			
			downloadMap();
			
			// end the loading phase
			loading = false;
			
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
