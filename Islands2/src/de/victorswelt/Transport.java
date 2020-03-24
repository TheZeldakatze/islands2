package de.victorswelt;

public class Transport {
	Level level;
	Island target;
	int x,y,dx,dy,amount,team;
	
	public Transport(Level l, int nx, int ny, int ndx, int ndy, int nteam, int ntarget) {
		target = l.getIsland(ntarget);
		x = nx;
		y = ny;
		dx = ndx;
		dy = ndy;
		team = nteam;
	}
	
	/**
	 * Should be called every tick
	 * @return true if transport should be removed from the list
	 * */
	public boolean update() {
		x+=dx;
		y+=dy;
		
		// check for a collision with the target
		if(Utils.checkCollision(x, y, SpriteManager.TRANSPORT_WIDTH, SpriteManager.TRANSPORT_HEIGHT, target.x, target.y, SpriteManager.TRANSPORT_WIDTH, SpriteManager.TRANSPORT_HEIGHT)) {
			
			// TODO apply the transport
			
			return true;
		}
		
		// TODO check for a collision with an obstacle
		
		return false;
	}
}
