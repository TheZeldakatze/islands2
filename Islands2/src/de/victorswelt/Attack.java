package de.victorswelt;

public class Attack {
	int source,target;
	
	public Attack() {reset();}
	
	public void reset() {
		source = -1;
		target = -1;
	}
	
	public void setIsland(int i) {
		if(source == -1) {
			source = i;
			return;
		}
		
		target = i;
	}
}
