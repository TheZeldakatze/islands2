package de.victorswelt;

public class Transport {
	public static final int TRANSPORT_SPEED = 2;
	
	Level level;
	Island target;
	float x,y,dx,dy;
	int size,team;
	
	public Transport(LevelAbstract l, int nx, int ny, float ndx, float ndy, int nteam, int nsize, Island ntarget) {
		target = ntarget;
		x = nx;
		y = ny;
		dx = ndx;
		dy = ndy;
		team = nteam;
		size = nsize;
	}
	
	/**
	 * Should be called every tick
	 * @return true if transport should be removed from the list
	 * */
	public boolean update() {
		x+=dx;
		y+=dy;
		
		// check for a collision with the target
		if(Utils.checkCollision((int) x, (int) y, SpriteManager.TRANSPORT_WIDTH, SpriteManager.TRANSPORT_HEIGHT, target.x, target.y, SpriteManager.TRANSPORT_WIDTH, SpriteManager.TRANSPORT_HEIGHT)) {
			
			// TODO apply the transport
			if(target.team == team) {
				target.population+=size;
			}
			else {
				target.population -= size;
				
				if(target.population<0) {
					target.team = team;
					target.population = -target.population;
				}
			}
			
			return true;
		}
		
		// TODO check for a collision with an obstacle
		
		return false;
	}
}
