package de.victorswelt;

import java.util.ArrayList;

public class Level {
	Island islands[];
	transient ArrayList transports;
	
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
}
