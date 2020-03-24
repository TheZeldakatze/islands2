package de.victorswelt;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class Game implements MouseInterface {
	private static final Color SEA_COLOUR = new Color(0, 113, 188);
	
	private static final Font FONT_POPULATION_INFO = new Font(Font.MONOSPACED, Font.PLAIN, 10);
	
	Level level;
	int playerTeam;
	Attack playerAttack;
	
	public Game() {
		Island i[] = {
				new Island("Seen", 128, 128, 2, 56), new Island("Seen", 128, 256, 2, 56), new Island("Seen", 128, 0, 2, 56),
				new Island("Seen", 256, 128, 2, 56), new Island("Seen", 256, 256, 2, 56), new Island("Seen", 256, 0, 2, 56),
				new Island("Seen", 384, 128, 2, 56), new Island("Seen", 384, 256, 2, 56), new Island("Seen", 384, 0, 2, 56),
		};
		playerAttack = new Attack();
		playerAttack.source = 0;
		level = new Level(i);
		playerAttack = new Attack();
		
		// add the object to the MouseHandler list
		MouseHandler.getInstance().add_interface(this);
	}
	
	public void update() {
		level.update();
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
		g.setColor(Color.RED);
		ArrayList transports = level.getTransports();
		for(int i = 0; i<transports.size(); i++) {
			Transport t = (Transport) transports.get(i);
			g.fillRect((int) t.x, (int) t.y, SpriteManager.TRANSPORT_WIDTH, SpriteManager.TRANSPORT_HEIGHT);
		}
		
		// TODO draw the hud
		g.drawImage(SpriteManager.getInstance().hud, 0, height - SpriteManager.getInstance().hud.getHeight(null), null);
		
	}

	public void onMouseClick(int x, int y) {
		Island islands[] = level.getIslands();
		
		// check if an island was selected
		for(int i = 0; i<islands.length; i++) {
			if(Utils.checkCollision(islands[i].x, islands[i].y, 32, 32, x, y, 1, 1)) {
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
