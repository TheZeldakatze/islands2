package de.victorswelt;

public class FastMath {
	private static final short LOOKUP_TABLE_PRECISION = 360;
	private static final float DOUBLE_PI = (float) (Math.PI*2);
	private static final float HALF_PI = (float) (Math.PI/2);
	
	private static float sinLookupTable[];
	private static float gapSize;
	
	public static void init() {
		
		// initialize the table
		sinLookupTable = new float[LOOKUP_TABLE_PRECISION];
		
		gapSize = (float) DOUBLE_PI / LOOKUP_TABLE_PRECISION;
		for(int i = 0; i<LOOKUP_TABLE_PRECISION;i++) {
			sinLookupTable[i] = (float) Math.sin(i*gapSize);
		}
	}
	
	public static float sin(float rad) {
		int entry = (int) (warpRad(rad) / gapSize);
		if(entry >= LOOKUP_TABLE_PRECISION) entry = 0;
		return sinLookupTable[(int) (entry)];
	}
	
	public static float cos(float rad) {
		return sin(HALF_PI - rad);
	}
	
	private static float warpRad(float rad) {
		
		// if needed, wrap the angle
		float mod;
		if(rad >= DOUBLE_PI || rad <= -DOUBLE_PI)
			mod = (float) (rad % DOUBLE_PI); 
		else
			mod = rad;
		
		if(mod < 0)
			mod = (float) (DOUBLE_PI+mod);
		return mod;
	}
}
