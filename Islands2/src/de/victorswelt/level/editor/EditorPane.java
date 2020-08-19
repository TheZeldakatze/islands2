package de.victorswelt.level.editor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.VolatileImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import de.victorswelt.Game;
import de.victorswelt.Island;
import de.victorswelt.Main;
import de.victorswelt.MouseHandler;
import de.victorswelt.MouseInterface;
import de.victorswelt.Obstacle;
import de.victorswelt.SpriteManager;
import de.victorswelt.Utils;

public class EditorPane extends JPanel implements MouseInterface, Runnable {
	private static final long serialVersionUID = 1L;

	private static final Color transparent = new Color(255, 255, 255, 100);
	
	static final int TOOL_ISLAND = 0;
	static final int TOOL_OBSTACLE = 1;
	static final int TOOL_REMOVE = 2;
	
	int currentTool = 0;
	int toolX, toolY, island_team, island_size;
	boolean needsRepaint;
	VolatileImage screen;
	
	ArrayList islands, obstacles;
	
	public EditorPane() {
		// initialize the mouse listener
		MouseHandler.init();
		addMouseListener(MouseHandler.getInstance());
		addMouseMotionListener(MouseHandler.getInstance());
		MouseHandler.getInstance().add_interface(this);
		
		MouseHandler.getInstance().setWindowSize(getWidth(), getHeight());
		MouseHandler.getInstance().setCanvasSize(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
		addComponentListener(new ComponentListener() {
			public void componentShown(ComponentEvent e) {}
			
			public void componentResized(ComponentEvent e) {
				MouseHandler.getInstance().setWindowSize(getWidth(), getHeight());
			}
			
			public void componentMoved(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});		
		
		// initialize the lists
		islands   = new ArrayList();
		obstacles = new ArrayList();
		
		// start the repaint thread
		new Thread(this).start();
	}
	
	public void paintComponent(Graphics pane_graphics) {
		do {
			// if the screen is not initialized or incompatible with the graphics configuration
			if(screen == null || screen.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE)
				screen =  GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleVolatileImage(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, Transparency.OPAQUE);

			// create a graphics context
			Graphics2D g = screen.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
			g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
			g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			
			// draw the background
			g.setColor(Game.SEA_COLOUR);
			g.fillRect(0, 0, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
			
			// draw every island
			g.setColor(Color.WHITE);
			g.setFont(Game.FONT_POPULATION_INFO);
			
			for(int i = 0; i<islands.size(); i++) {
				Island island = (Island) islands.get(i);
				g.drawImage(SpriteManager.getInstance().getMapIslandImage(island.team), island.x, island.y, null);
				g.drawString("" + island.population, island.x + 12, island.y + Game.FONT_POPULATION_INFO.getSize());
			}
			
			// draw the obstacles
			for(int i = 0; i<obstacles.size(); i++) {
				Obstacle o = (Obstacle) obstacles.get(i);
				g.drawImage(SpriteManager.getInstance().obstacle, o.x, o.y, null);
			}
			
			// draw the tool
			g.setColor(transparent);
			switch(currentTool) {
				case TOOL_ISLAND: {
					g.fillRect(toolX, toolY, 32, 32);
				}
				case TOOL_OBSTACLE: {
					g.fillRect(toolX, toolY, 32, 32);
				}
			}
			
		} while(screen.contentsLost());
		
		// turn off all the fancy drawing stuff
		Graphics2D panel_graphics = (Graphics2D) pane_graphics;
		panel_graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		panel_graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		panel_graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		panel_graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
		panel_graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		panel_graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		panel_graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		panel_graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		panel_graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		
		// draw the screen
		panel_graphics.drawImage(screen, 0, 0, getWidth(), getHeight(), null);
		panel_graphics.dispose();
	
	}

	public void onMouseClick(int x, int y) {
		toolX = x;
		toolY = y;
		needsRepaint = true;
		
		if(x > 0 && y > 0 && x<Main.SCREEN_WIDTH && y < Main.SCREEN_HEIGHT) {
			switch(currentTool) {
				case TOOL_ISLAND: {
					islands.add(new Island(x, y, island_team, island_size));
				} break;
				case TOOL_OBSTACLE: {
					obstacles.add(new Obstacle(x, y));
				} break;
				case TOOL_REMOVE: {
					// remove the selected islands
					for(int i = 0; i<islands.size(); i++) {
						Island is = (Island) islands.get(i);
						if(Utils.checkCollision(is.x, is.y, SpriteManager.ISLAND_WIDTH, SpriteManager.ISLAND_HEIGHT, x, y, 1, 1)) {
							islands.remove(i);
						}
					}
					
					// remove the selected obstacles
					for(int i = 0; i<obstacles.size(); i++) {
						Obstacle is = (Obstacle) obstacles.get(i);
						if(Utils.checkCollision(is.x, is.y, SpriteManager.ISLAND_WIDTH, SpriteManager.ISLAND_HEIGHT, x, y, 1, 1)) {
							obstacles.remove(i);
						}
					}
				} break;
			}
		}
	}

	public void onMouseMove(int x, int y) {
		toolX = x;
		toolY = y;
		needsRepaint = true;
	}
	
	public String serialize() {
		String ret = "";
		
		// serialize all the islands
		for(int i = 0; i<islands.size(); i++) {
			Island is = (Island) islands.get(i);
			ret = ret + "i " + is.x + " " + is.y + " " + is.team + " " + is.population + "\n";
		}
		
		// serialize all the obstacles
		for(int i = 0; i<obstacles.size(); i++) {
			Obstacle is = (Obstacle) obstacles.get(i);
			ret = ret + "o " + is.x + " " + is.y + "\n";
		}
		
		return ret;
	}
	
	public void deserialize(String in) {
		// clear the lists
		islands.clear();
		obstacles.clear();
		
		// get the lines
		String lines[] = in.split("\n");
		
		// create the islands
		for(int i = 0; i<lines.length; i++) {
			// split the line
			String parts[] = lines[i].split(" ");
			
			// check if it is an island
			if(parts[0].equalsIgnoreCase("i")) {
				// check for the length
				if(parts.length == 5) {
					try {
						int x          = Integer.parseInt(parts[1]);
						int y          = Integer.parseInt(parts[2]);
						int team       = Integer.parseInt(parts[3]);
						int population = Integer.parseInt(parts[4]);
						
						islands.add(new Island(x, y, team, population));
					} catch(Exception e) {
						System.out.println("Level: malformed line: " + i + " (not a number)");
					}
				}
				else
					System.out.println("Level: malformed line: " + i + " (invalid length)");
			}
			
			// check if it is an obstacle
			else if(parts[0].equalsIgnoreCase("o")) {
				if(parts.length == 3) {
					try {
						int x = Integer.parseInt(parts[1]);
						int y = Integer.parseInt(parts[2]);
						obstacles.add(new Obstacle(x, y));
					} catch(Exception e) {
						System.out.println("Level: malformed line: " + i + " (invalid length)");
					}
				}
			}
			
		}
	}

	public void run() {
		while(true) {
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(needsRepaint)
				repaint();
		}
	}
}
