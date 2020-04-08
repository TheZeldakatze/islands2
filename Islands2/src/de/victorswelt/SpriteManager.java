package de.victorswelt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteManager {
	public static final int ISLAND_WIDTH = 32;
	public static final int ISLAND_HEIGHT = 32;
	
	public static final int TRANSPORT_WIDTH = 16;
	public static final int TRANSPORT_HEIGHT = 16;
	
	private static final Color ISLAND_COLOR_KEY = new Color(255,0,255);
	
	private static final Color islandColours[] = {
		new Color(255, 0, 0),
		new Color(0, 0, 255),
		new Color(0, 255, 0),
		new Color(255, 255, 0),
		new Color(255, 255, 255),
		new Color(255, 0, 255),
		new Color(0, 255, 255),
		new Color(0, 0, 0),
		
	};
	
	ColourReplacementFilter replacementFilter;
	Image islandMapIcons[];
	Image planeIcons[];
	Image hud;
	
	private SpriteManager() throws IOException {
		replacementFilter = new ColourReplacementFilter();
		
		// initialize the island images
		Image island_map_icon = ImageIO.read(Main.class.getResource("sprite/island.png"));
		Image plane_icon      = ImageIO.read(Main.class.getResource("sprite/plane.png"));
		
		// initialize the arrays
		islandMapIcons = new Image[islandColours.length];
		planeIcons     = new Image[islandColours.length];
		
		// create the individual images
		for(int i = 0; i<islandColours.length;i++) {
			islandMapIcons[i] = replaceColourInImage(island_map_icon, ISLAND_COLOR_KEY, islandColours[i]);
			planeIcons[i]     = replaceColourInImage(plane_icon     , ISLAND_COLOR_KEY, islandColours[i]);
		}
		
		// load the hud
		hud = ImageIO.read(Main.class.getResource("sprite/hud.png"));
	}
	
	public Image getMapIslandImage(int team) {
		if(team<0 || team > islandColours.length-1)
			return islandMapIcons[0];
		return islandMapIcons[team];
	}
	
	public Image getPlaneImage(int team) {
		if(team<0 || team > islandColours.length-1)
			return planeIcons[0];
		return planeIcons[team];
	}
	
	private Image replaceColourInImage(Image i, Color original, Color replacement) {
		replacementFilter.setOriginalColor(original);
		replacementFilter.setReplacementColor(replacement);
		Image img = replacementFilter.processImage(i);
		Image newImg = new BufferedImage(i.getWidth(null), i.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics g = newImg.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return newImg;
	}
	
	// 
	private static SpriteManager INSTANCE;
	public static void init() throws IOException {INSTANCE = new SpriteManager();}
	public static SpriteManager getInstance() {return INSTANCE;}
}
