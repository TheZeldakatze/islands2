package de.victorswelt;

import java.util.ArrayList;

public class Level {
	int playerTeam;
	private Island islands[];
	private transient ArrayList transports;
	private transient ArrayList teams;
	
	public Level(Island nislands[]) {
		playerTeam = 0;
		transports = new ArrayList();
		islands = nislands;
		
		// create the teams
		teams = new ArrayList();
		
		// iterate through all islands
		for(int i = 0; i<islands.length;i++) {
			
			// check if the island is in a team
			boolean hasTeam = false;
			for(int j = 0; j<teams.size(); j++) {
				Team t = (Team) teams.get(j);
				if(t.getId() == islands[i].team) {
					hasTeam = true;
					break;
				}
			}
			
			
			// if the team does not exist, create one
			if(!hasTeam) {
				if(islands[i].team == playerTeam)
					teams.add(new Team(islands[i].team));
				else
					teams.add(new AiTeam(islands[i].team));
			}
		}
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
		// update the islands
		for(int i = 0; i<islands.length; i++)
			islands[i].update();
		
		// update the transports
		for(int i = 0; i<transports.size(); i++)
			if(((Transport) transports.get(i)).update())
				transports.remove(i);
		
		// update the teams (and AIs)
		for(int j = 0; j<teams.size(); j++)
			((Team) teams.get(j)).update(this);
			
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
	
	public static Level createLevel(String level) {
		
		// get the lines
		String lines[] = level.split("\n");
		
		// create the islands
		ArrayList islands = new ArrayList();
		for(int i = 0; i<lines.length; i++) {
			// split the line
			String parts[] = lines[i].split(" ");
			
			// check for the length
			if(parts.length == 4) {
				try {
					int x          = Integer.parseInt(parts[0]);
					int y          = Integer.parseInt(parts[1]);
					int team       = Integer.parseInt(parts[2]);
					int population = Integer.parseInt(parts[3]);
					
					islands.add(new Island(x, y, team, population));
				} catch(Exception e) {
					System.out.println("Level: malformed line: " + i + " (not a number)");
				}
			}
			else
				System.out.println("Level: malformed line: " + i + " (invalid length)");
		}
		
		// create an array
		Island arr[] = new Island[islands.size()];
		for(int i = 0; i<arr.length;i++)
			arr[i] = (Island) islands.get(i);
		
		return new Level(arr);
	}
}
