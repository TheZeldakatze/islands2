package de.victorswelt;

import java.util.List;

public abstract class LevelAbstract {
	int playerTeam;
	protected Island islands[];
	protected Obstacle obstacles[];
	protected transient List transports;
	protected transient List teams;
	
	public abstract boolean isGameOver();
	
	public Island[] getIslands() {
		return islands;
	}
	
	public List getTransports() {
		return transports;
	}
	
	public Obstacle[] getObstacles() {
		return obstacles;
	}
	
	public abstract Island getIsland(int i);
	public void update() {
		// update the islands
		for(int i = 0; i<islands.length; i++)
			islands[i].update();
		
		// update the transports
		for(int i = 0; i<transports.size(); i++)
			if(((Transport) transports.get(i)).update())
				transports.remove(i);
		
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
	public abstract void addTransport(int source, int target);
	public abstract boolean isReady();
}
