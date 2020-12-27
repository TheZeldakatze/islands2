package de.victorswelt.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.victorswelt.FastMath;
import de.victorswelt.Island;
import de.victorswelt.Level;
import de.victorswelt.LevelAbstract;
import de.victorswelt.Obstacle;
import de.victorswelt.Transport;

public class Map extends LevelAbstract {
	Server server;
	String initializationString = "i 96 224 0 128\ni 181 139 1 64\ni 179 306 1 64\ni 269 225 2 32\ni 267 81 2 32\ni 271 387 2 32\ni 350 146 1 16\ni 344 310 1 16\ni 263 144 0 32\ni 270 311 0 32\ni 346 39 3 16\ni 345 423 3 16\ni 415 390 0 8\ni 422 86 0 8\no 183 224\no 343 220\n";
	
	public Map(Server s) {
		server = s;
		
		// create a synchronized list for the transports
		transports = Collections.synchronizedList(new ArrayList());
		
		loadNew(initializationString);
	}
	
	public void update() {
		super.update();
	}
	
	public void loadNew(String mapString) {
		// hacky solution: copy the arrays from a single player level
		Level l = Level.createLevel(initializationString);
		islands = l.getIslands();
		obstacles = l.getObstacles();
		transports.clear();
	}
	
	public String getMapString() {
		String out = "";
		for(int i = 0; i<islands.length;i++) {
			Island s = islands[i];
			out = out + "i " + s.x + " " + s.y + " " + s.team + " " + s.population + "\n";
		}
		for(int i = 0; i<obstacles.length;i++) {
			Obstacle s = obstacles[i];
			out = out + "o " + s.x + " " + s.y + "\n";
		}
		
		return out;
	}
	
	public boolean isGameOver() {
		int firstTeam = -1;
		for(int i = 0; i<islands.length; i++) {
			if(firstTeam == -1)
				firstTeam = islands[i].team;
			else
				if(firstTeam != islands[i].team)
					return false;
		}
		
		return true;
	}
	
	public Island getIsland(int i) {
		if(i<0 || i>=islands.length)
			return null;
		return islands[i];
	}
	
	public int getIslandId(Island s) {
		for(int i = 0; i<islands.length;i++) {
			if(islands[i] == s)
				return i;
		}
		return -1;
	}
	
	public void addTransport(int source, int target) {
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
			
			// calculate the size of the transport
			int size = source_island.population/2;
			source_island.population-=size;
			
			// create it
			transports.add(new ServerTransport(server, this, source_island.x, source_island.y, dx, dy, source_island.team, size, target_island));
			
			// update the island population
			List clients = server.getClients();
			for(int i = 0; i<clients.size(); i++) {
				Client client = (Client) clients.get(i);
				client.addTransport(source, target);
				client.sendIslandPopulationUpdate(source, source_island.population);
				
			}
		}
	}

	public Obstacle[] getObstacles() {
		return obstacles;
	}

	public boolean isReady() {
		return true;
	}
}
