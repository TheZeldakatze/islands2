package de.victorswelt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import de.victorswelt.Server.PacketType;
import de.victorswelt.Server.Server;

public class MultiplayerLevel extends LevelAbstract {
	private Main main;
	private boolean loading = true, gameover, listenerRunning = true;
	private Socket socket;
	private DataInputStream data_in;
	private DataOutputStream data_out;
	
	public MultiplayerLevel(Main m, final String username, final InetSocketAddress s) {
		main = m;
		playerTeam = 1;
		transports = new ArrayList();
		teams = new ArrayList();
		
		Obstacle obs[] = {};
		obstacles = obs;
		
		Island isl[] = {};
		islands = isl;
		
		// create a listener thread
		new Thread(new Runnable() {
			public void run() {
				networkListenerThread(s, username);
			}
		}, "Multiplayer Connection Thread").start();
		
	}
	
	public boolean isLoading() {
		return loading;
	}
	
	

	public boolean isGameOver() {
		return gameover;
	}
	
	public boolean isListenerRunning() {
		return listenerRunning;
	}
	
	public void disconnect() {
		
	}
	
	public Island getIsland(int i) {
		if(i<0 || i>=islands.length)
			return null;
		return islands[i];
	}
	
	public void update() {
		super.update();	
	}
	
	
	// TODO this code is just copied from the single player save system.
	// I should probably replace it with a compact way to reduce bandwidth usage
	private void downloadMap() throws NumberFormatException, IOException {
		byte buf[];
		synchronized(data_in) {
			synchronized (data_out) {
				data_out.writeByte(PacketType.CLIENT_GET_MAP);
				int length = Utils.decodeVarNum(data_in);
				buf = new byte[length];
				data_in.readFully(buf);
			}
		}
		
		String text = new String(buf);
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
	
	private void networkListenerThread(InetSocketAddress networkAddress, String username) {
		// initialize
		// create a socket
		try {
			
			// create a socket
			socket = new Socket(networkAddress.getHostString(), networkAddress.getPort());
			data_in  = new DataInputStream(socket.getInputStream());
			data_out = new DataOutputStream(socket.getOutputStream());
			
			// send the connection type
			data_out.writeByte(PacketType.CONNECTION_TYPE_JOIN);
			
			// get the protocol version
			int protocolVersion = data_in.readInt();
			if(Server.PROTOCOL_VERSION != protocolVersion) {
				main.displayErrorStateMessage(Main.STATE_MP_SERVER_SELECT, "Wrong Protocol Version: " + protocolVersion +" (Server) != " + Server.PROTOCOL_VERSION + " (Client)");
				socket.close();
			}
			
			// send the username
			Utils.encodeVarInt(data_out, username.length());
			data_out.write(username.getBytes());
			
			downloadMap();
			
			// end the loading phase
			loading = false;
			
			// listen
			while(true) {
				byte packetType = data_in.readByte();
				
				switch(packetType) {
				
					// create a ghost transport
					case PacketType.SERVER_ADD_TRANSPORT: {
						int source = Utils.decodeVarNum(data_in);
						int target = Utils.decodeVarNum(data_in);
						createClientTransport(source, target);
					} break;
					
					case PacketType.SERVER_SET_POPULATION: {
						int islnum = Utils.decodeVarNum(data_in);
						Island is = getIsland(islnum);
						int population = Utils.decodeVarNum(data_in);
						if(is != null)
							is.population = population;
					} break;
					
					case PacketType.SERVER_SET_POPULATION_AND_TEAM: {
						int islnum = Utils.decodeVarNum(data_in);
						Island is = getIsland(islnum);
						int population = Utils.decodeVarNum(data_in);
						int team = Utils.decodeVarNum(data_in);
						
						if(is != null) {
							is.population = population;
							is.team = team;
						}
					} break;
					
					case PacketType.SERVER_REQUEST_MAP_DOWNLOAD: {
						downloadMap();
					} break;
				}
				
			}
			
		} catch (UnknownHostException e) {
			main.displayErrorStateMessage(Main.STATE_MP_SERVER_SELECT, "Unknown Host");
		} catch(ConnectException e) {
			main.displayErrorStateMessage(Main.STATE_MP_SERVER_SELECT, "Can't connect: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		listenerRunning = false;
		
	}
	
	private void createClientTransport(int source, int target) {
		Island source_island = getIsland(source), target_island = getIsland(target);
		
		if(source_island != null && target_island != null) {
			int delta_x = target_island.x-source_island.x;
			int delta_y = target_island.y-source_island.y;
			
			// get the angle
			float angle = (float) (Math.atan((float) delta_y / delta_x) - Math.PI/2);
			float sin = (float) FastMath.sin(angle);
			float cos = (float) FastMath.cos(angle);
			
			// a matrix calculation. it´s a simplified version of:
			// dx = x * cos - y * sin
			// dy = x * sin + y * cos
			float dx = -Transport.TRANSPORT_SPEED*sin;
			float dy = Transport.TRANSPORT_SPEED *cos;
			
			// FIX for some reason, the values are flipped when delta_x is negative
			if(delta_x<0) {
				dx=-dx;
				dy=-dy;
			}
			
			// create it
			transports.add(new Transport(this, source_island.x, source_island.y, dx, dy, source_island.team, 0, target_island));
		}
	}
	
	/**
	 * the method creates a troop transport
	 **/
	public void addTransport(int source, int target) {
		System.out.println("Sending transport creation packet with (source,target) (" + source + ","+ target + ")");
		if(source < 0 || target < 0)
			return;
		
		synchronized (data_out) {
			try {
				data_out.writeByte(PacketType.CLIENT_ADD_TRANSPORT);
				Utils.encodeVarInt(data_out, source);
				Utils.encodeVarInt(data_out, target);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Sent transport creation packet with (source,target) (" + source + ","+ target + ")");
	}
	
	/**
	 * returns all available teams */
	public int[] getAvailableTeams() {
		ArrayList teams = new ArrayList();
		for(int i = 0; i<islands.length; i++) {
			int t = islands[i].team;
			boolean found = false;
			for(int j = 0;j<teams.size();j++) {
				if(((Integer) teams.get(j)).intValue() == t) {
					found = true;
					break;
				}
			}
			if(!found)
				teams.add(new Integer(t));
		}
		
		// create an array
		int ret[] = new int[teams.size()];
		for(int i = 0; i<ret.length;i++) {
			ret[i] = ((Integer) teams.get(i)).intValue();
		}
		
		return ret;
	}

	public boolean isReady() {
		return !loading;
	}
}
