package de.victorswelt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteManager {
	public static final int ISLAND_WIDTH = 32;
	public static final int ISLAND_HEIGHT = 32;
	
	public static final int TRANSPORT_WIDTH = 16;
	public static final int TRANSPORT_HEIGHT = 16;
	
	private static final Color ISLAND_COLOR_KEY = new Color(255,0,255);
	
	private static final int PLANE_ROTATIONS = 90;
	
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
	Image planeIcons[][];
	Image logo, hud, obstacle;
	
	private SpriteManager() throws IOException {
		replacementFilter = new ColourReplacementFilter();
		
		// initialize the island images
		Image island_map_icon = createCompatibleImage(ImageIO.read(Main.class.getResource("sprite/island.png")));
		Image plane_icon      = createCompatibleImage(ImageIO.read(Main.class.getResource("sprite/plane.png")));
		
		// initialize the arrays
		islandMapIcons = new Image[islandColours.length];
		planeIcons     = new Image[PLANE_ROTATIONS][islandColours.length];
		
		// create the individual images
		for(int i = 0; i<islandColours.length;i++) {
			islandMapIcons[i] = replaceColourInImage(island_map_icon, ISLAND_COLOR_KEY, islandColours[i]);
			planeIcons[0][i]     = replaceColourInImage(plane_icon     , ISLAND_COLOR_KEY, islandColours[i]);
		}
		
		// rotate every image
		double plane_rotation_step = (Math.PI * 2) / PLANE_ROTATIONS;
		for(int i = 0; i<islandColours.length;i++) {
			for(int j = 1; j<PLANE_ROTATIONS; j++) {
				planeIcons[j][i] = rotateImage(planeIcons[0][i], j*plane_rotation_step);
			}
		}
		
		// load the hud
		hud = createCompatibleImage(ImageIO.read(Main.class.getResource("sprite/hud.png")));
		logo = createCompatibleImage(ImageIO.read(Main.class.getResource("sprite/logo.gif")));
		obstacle = createCompatibleImage(ImageIO.read(Main.class.getResource("sprite/obstacle.png")));
	}
	
	public Image getMapIslandImage(int team) {
		if(team<0 || team > islandColours.length-1)
			return islandMapIcons[0];
		return islandMapIcons[team];
	}
	
	public Image getPlaneImage(int team, float rotation) {
		int index = (int) Math.min((((Math.abs(rotation) % Math.PI * 2) / Math.PI * 2) * (PLANE_ROTATIONS)), PLANE_ROTATIONS-1);
		
		if(team<0 || team > islandColours.length-1)
			return planeIcons[index][0];
		return planeIcons[index][team];
	}
	
	private BufferedImage replaceColourInImage(Image i, Color original, Color replacement) {
		replacementFilter.setOriginalColor(original);
		replacementFilter.setReplacementColor(replacement);
		Image img = replacementFilter.processImage(i);
		BufferedImage newImg = new BufferedImage(i.getWidth(null), i.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics g = newImg.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return newImg;
	}
	
	// a function for rotating images
	private BufferedImage rotateImage(Image img, double rad) {
		
		// get the values
		//double rad = Math.toRadians(degress);
		double sin = Math.abs(Math.sin(rad));
		double cos = Math.abs(Math.cos(rad));
		int width = img.getWidth(null);
		int height= img.getHeight(null);
		int nw = (int) Math.floor(width * cos + height * sin);
		int nh = (int) Math.floor(height * cos + width * sin);
		
		// create the new image
		BufferedImage new_img = new BufferedImage(nw,nh, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g = new_img.createGraphics();
		AffineTransform t = new AffineTransform();
		t.translate((nw-width)/2, (nh-height)/2);
		t.rotate(rad, width/2, height/2);
		g.setTransform(t);
		g.drawImage(img, 0, 0, null);
		
		// clear stuff up
		g.dispose();
		
		return new_img;
	}
	
	// a method for creating compatible images
	private BufferedImage createCompatibleImage(BufferedImage input) {
		// get the graphics configuration
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		
		// if the image has the correct colour model, return it
		if(input.getColorModel().equals(gc.getColorModel())) {
			return input;
		}
		
		// otherwise create a compatible image
		BufferedImage img = gc.createCompatibleImage(input.getWidth(), input.getHeight(), input.getTransparency());
		Graphics2D g = img.createGraphics();
		g.drawImage(input, 0, 0, null);
		g.dispose();
		
		return img;
	}
	
	// 
	private static SpriteManager INSTANCE;
	public static void init() throws IOException {INSTANCE = new SpriteManager();}
	public static SpriteManager getInstance() {return INSTANCE;}
}
