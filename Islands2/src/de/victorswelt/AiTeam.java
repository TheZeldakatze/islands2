package de.victorswelt;

public class AiTeam extends Team {
	private int ticks_to_next_second;
	
	public AiTeam(int nid) {super(nid);}
	
	public void update(Level l) {
		ticks_to_next_second--;
		if(ticks_to_next_second <= 0) {
			ticks_to_next_second = (int) (Main.TICKS_PER_SECOND * 5);
			
			Island islands[] = l.getIslands();
			
			// find the island with the highest population
			int bestSourceIsland = -1;
			for(int i = 0; i<islands.length; i++) {
				if(islands[i].team == id && (bestSourceIsland == -1 || islands[bestSourceIsland].population<islands[i].population))
					bestSourceIsland = i;
			}
			
			if(bestSourceIsland != -1) {
				// find the island with the lowest population
				int bestTargetIsland = -1;
				for(int i = 0; i<islands.length; i++) {
					if(islands[i].team != id && (bestTargetIsland == -1 || islands[bestTargetIsland].population>islands[i].population))
						bestTargetIsland = i;
				}
				
				if(bestTargetIsland != -1) {
					l.addTransport(bestSourceIsland, bestTargetIsland);
				}
			}
		}
	}
}
