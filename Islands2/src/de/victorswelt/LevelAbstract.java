package de.victorswelt;

import java.util.ArrayList;

public abstract class LevelAbstract {
	int playerTeam;
	protected Island islands[];
	protected Obstacle obstacles[];
	protected transient ArrayList transports;
	protected transient ArrayList teams;
	
	public abstract boolean isGameOver();
	public abstract Island[] getIslands();
	public abstract ArrayList getTransports();
	public abstract Obstacle[] getObstacles();
	public abstract Island getIsland(int i);
	public abstract void update();
	public abstract void addTransport(int source, int target);
	
}
