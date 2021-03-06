package de.victorswelt;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.List;

public class Game implements MouseInterface, KeyboardInterface {
	private static final boolean SHOW_DEBUG_MAP_OBJECT_ID = false;
	
	public static final Color SEA_COLOUR     = new Color(0, 113, 188);
	public static final Color OVERLAY_COLOUR = new Color(0, 0, 0, 100);
	
	public static final Font FONT_POPULATION_INFO = new Font(Font.MONOSPACED, Font.PLAIN, 10);
	
	LevelAbstract level;
	Attack playerAttack;
	
	private boolean showOverlay = false;
	
	private Button exitButton = new Button("Exit Game", 540, 430, 80, 40);
	
	public Game() {
		/*Island i[] = {
				new Island(128, 128, 0, 56), new Island(128, 256, 1, 56), new Island(128, 0, 2, 56),
				new Island(256, 128, 1, 56), new Island(256, 256, 0, 56), new Island(256, 0, 0, 56),
				new Island(384, 128, 2, 56), new Island(384, 256, 1, 56), new Island(384, 0, 2, 56),
		};level = new Level(i);*/
		
		
		// get the level data
		String level_string = Utils.readStringFromResource(Game.class.getResourceAsStream("/de/victorswelt/level/test.lvl"));
		
		// create a level
		level = Level.createLevel(level_string);
		playerAttack = new Attack();
		
		// add the object to the MouseHandler list
		MouseHandler.getInstance().add_interface(this);
		KeyboardHandler.getInstance().add_interface(this);
		
	}
	
	public boolean update() {
		level.update();
		
		if(exitButton.wasPressed) {
			exitButton.setEnabled(false);
			return true;
		}
		
		return level.isGameOver();
	}
	
	public void setLevel(LevelAbstract l) {
		level = l;
	}
	
	public LevelAbstract getLevel() {
		return level;
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
			
			// show the island's id if the debug mode is on
			if(SHOW_DEBUG_MAP_OBJECT_ID)
				g.drawString("" + i, island.x + 12, island.y + FONT_POPULATION_INFO.getSize()*3);
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
		List transports = level.getTransports();
		for(int i = 0; i<transports.size(); i++) {
			Transport t = (Transport) transports.get(i);
			
			if(t.dx == 0) {
				g.drawImage(SpriteManager.getInstance().getPlaneImage(t.team, 0), (int) t.x, (int) t.y, null);
			}
			else
				g.drawImage(SpriteManager.getInstance().getPlaneImage(t.team, (float) Math.atan2(((float) t.dy), ((float) t.dx))), (int) t.x, (int) t.y, null);
			
			// show the size of the transport if the debug mode is on
			if(SHOW_DEBUG_MAP_OBJECT_ID)
				g.drawString(""+ t.size, t.x, t.y+FONT_POPULATION_INFO.getSize());
		}
		
		// draw the hud
		g.setColor(Color.GREEN);
		g.fillRect(0, height-20, width, 20);
		
		// draw the player's team indicator
		g.setColor(Color.BLACK);
		g.drawRect(width - 15, height - 15, 10, 10);
		g.drawString("Your colour:", width - 90, height - 5);
		g.setColor(SpriteManager.getInstance().getTeamColor(level.playerTeam));
		g.fillRect(width - 14, height - 14, 9, 9);
		
		// draw the overlay
		if(showOverlay) {
			g.setColor(OVERLAY_COLOUR);
			g.fillRect(0, 0, width, height);
			
			exitButton.render(g);
			
			if(level instanceof MultiplayerLevel) {
				
				g.setColor(Color.WHITE);
				List players = ((MultiplayerLevel) level).playerList;
				for(int i = 0; i<players.size(); i++) {
					g.drawString((String) players.get(i), 5, 30 + 20 * i);
				}
			}
		}
		
		// TODO randomly play sounds
		
	}

	public void onMouseClick(int x, int y) {
		if(level == null || !level.isReady())
			return;
		
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

	public void onKeyTyped(char c) {
		// TODO Auto-generated method stub
		
	}

	public void onKeyPressed(char c) {
		if(c == KeyEvent.VK_ESCAPE) {
			showOverlay = !showOverlay;
			exitButton.setEnabled(showOverlay);
		}
	}

	public void onKeyReleased(char c) {
		// TODO Auto-generated method stub
		
	}

}
