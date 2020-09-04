package de.victorswelt;

import java.net.Socket;
import java.util.ArrayList;

public class MultiplayerLevel extends LevelAbstract {
	
	
	public MultiplayerLevel(Socket s) {
		playerTeam = 0;
		transports = new ArrayList();
		teams = new ArrayList();
		
		Obstacle obs[] = {};
		obstacles = obs;
		
		Island isl[] = {
				new Island(5, 5, 0, 21),
				new Island(115, 115, 1, 21),
		};
		islands = isl;
	}
	
	public boolean isLoading() {
		return false;
	}

	public boolean isGameOver() {
		return false;
	}
	
	public Island[] getIslands() {
		return islands;
	}
	
	public ArrayList getTransports() {
		return transports;
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
	
	/**
	 * the method creates a troop transport
	 **/
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
			transports.add(new Transport(this, source_island.x, source_island.y, dx, dy, source_island.team, size, target_island));
		}
	}
}
