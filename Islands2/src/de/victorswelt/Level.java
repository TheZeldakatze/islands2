package de.victorswelt;

import java.util.ArrayList;

public class Level {
	private Island islands[];
	private transient ArrayList transports;
	
	public Level(Island i[]) {
		transports = new ArrayList();
		islands = i;
	}
	
	public Island[] getIslands() {
		return islands;
	}
	
	public ArrayList getTransports() {
		return transports;
	}
	
	public Island getIsland(int i) {
		if(i<0 || i>=islands.length)
			return null;
		return islands[i];
	}
	
	public void update() {
		for(int i = 0; i<islands.length; i++)
			islands[i].update();
		
		for(int i = 0; i<transports.size(); i++)
			((Transport) transports.get(i)).update();
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
			float sin = (float) Math.sin(angle);
			float cos = (float) Math.cos(angle);
			
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
