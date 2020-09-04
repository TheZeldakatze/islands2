package de.victorswelt;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Game implements MouseInterface {
	public static final Color SEA_COLOUR = new Color(0, 113, 188);
	
	public static final Font FONT_POPULATION_INFO = new Font(Font.MONOSPACED, Font.PLAIN, 10);
	
	LevelAbstract level;
	Attack playerAttack;
	AffineTransform transform, transform2;
	
	public Game() {
		/*Island i[] = {
				new Island(128, 128, 0, 56), new Island(128, 256, 1, 56), new Island(128, 0, 2, 56),
				new Island(256, 128, 1, 56), new Island(256, 256, 0, 56), new Island(256, 0, 0, 56),
				new Island(384, 128, 2, 56), new Island(384, 256, 1, 56), new Island(384, 0, 2, 56),
		};level = new Level(i);*/
		
		
		// get the level data
		String level_string = "";
		InputStream is = Game.class.getResourceAsStream("/de/victorswelt/level/test.lvl");
		try {
			while(is.available()>0) {
				level_string = level_string + ((char) is.read());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// create a level
		level = Level.createLevel(level_string);
		
		playerAttack = new Attack();
		playerAttack.source = 0;
		playerAttack = new Attack();
		transform = new AffineTransform();
		transform2 = new AffineTransform();
		
		// add the object to the MouseHandler list
		MouseHandler.getInstance().add_interface(this);
		
	}
	
	public boolean update() {
		level.update();
		return level.isGameOver();
	}
	
	public void setLevel(LevelAbstract l) {
		level = l;
	}
	
	public void render(Graphics2D g, int width, int height) {
		
		// draw the background
		g.setColor(SEA_COLOUR);
		g.fillRect(0, 0, width, height);
		
		// draw every island
		g.setColor(Color.WHITE);
		g.setFont(FONT_POPULATION_INFO);
		
		Island islands[] = level.getIslands();
		for(int i = 0; i<islands.length; i++) {
			Island island = islands[i];
			g.drawImage(SpriteManager.getInstance().getMapIslandImage(island.team), island.x, island.y, null);
			g.drawString("" + island.population, island.x + 12, island.y + FONT_POPULATION_INFO.getSize());
		}
		
		// draw the obstacles
		Obstacle obstacles[] = level.getObstacles();
		for(int i = 0; i<obstacles.length; i++) {
			Obstacle o = obstacles[i];
			g.drawImage(SpriteManager.getInstance().obstacle, o.x, o.y, null);
		}
		
		// draw the circles visualizing the player selection
		if(playerAttack.source != -1) {
			// find the island
			Island i = level.getIsland(playerAttack.source);
			if(i != null) {
				g.setColor(Color.GREEN);
				g.drawOval(i.x - 8, i.y - 8, 48, 48);
			}
		}
		if(playerAttack.target != -1) {
			// find the island
			Island i = level.getIsland(playerAttack.target);
			if(i != null) {
				
				g.setColor(Color.RED);
				g.drawOval(i.x - 16, i.y - 16, 64, 64);
			}
		}
		
		// draw the transports
		ArrayList transports = level.getTransports();
		//AffineTransform save_transform = g.getTransform();
		for(int i = 0; i<transports.size(); i++) {
			Transport t = (Transport) transports.get(i);
			/*transform.setToTranslation(t.x, t.y);
			
			transform2.setToRotation(Math.atan(t.y / t.x));
			transform.concatenate(transform2);
			g.setTransform(transform);*/
			if(t.dx == 0) {
				g.drawImage(SpriteManager.getInstance().getPlaneImage(t.team, 0), (int) t.x, (int) t.y, null);
			}
			else
				g.drawImage(SpriteManager.getInstance().getPlaneImage(t.team, (float) Math.atan2(((float) t.dy), ((float) t.dx))), (int) t.x, (int) t.y, null);
		}
		//g.setTransform(save_transform);
		
		// TODO draw the hud
		g.setColor(Color.GREEN);
		g.fillRect(0, height-20, width, 20);
		//g.drawImage(SpriteManager.getInstance().hud, 0, height - SpriteManager.getInstance().hud.getHeight(null), null); 
		
	}

	public void onMouseClick(int x, int y) {
		Island islands[] = level.getIslands();
		
		// check if an island was selected
		for(int i = 0; i<islands.length; i++) {
			if(Utils.checkCollision(islands[i].x, islands[i].y, 32, 32, x, y, 1, 1)) {
				
				if(!(playerAttack.source == -1 && islands[i].team != level.playerTeam) && // don´t set islands which aren´t in the player team as sources
						!(playerAttack.source == i)) 
					playerAttack.setIsland(i);
				return;
			}
		}
		
		// a temporary trigger
		// TODO remove
		level.addTransport(playerAttack.source, playerAttack.target);
		playerAttack.reset();
	}

	public void onMouseMove(int x, int y) {}

}
