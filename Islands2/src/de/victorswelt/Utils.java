package de.victorswelt;

public class Utils {
	private Utils() {}
	
	
	public static boolean checkCollision(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
		if(y1 > y2 + h2) return false;
		if(y2 > y1 + h1) return false;
		if(x1 > x2 + w2) return false;
		if(x2 > x1 + w1) return false;

		return true;
	}
}
