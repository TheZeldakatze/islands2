package de.victorswelt;

public class Island {
	String name;
	int x, y, team, population, ticks_to_next_second;
	
	public Island(String name, int nx, int ny, int nteam, int npopulation) {
		x = nx;
		y = ny;
		team = nteam;
		population = npopulation;
	}
	
	public void update() {
		
		// increment the population every second
		if(ticks_to_next_second == 0) {
			ticks_to_next_second = Main.TICKS_PER_SECOND;
			population++;
		}
		ticks_to_next_second--;
	}
}
