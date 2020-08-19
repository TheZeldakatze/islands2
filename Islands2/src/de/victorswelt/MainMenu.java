package de.victorswelt;

import java.awt.Color;
import java.awt.Graphics2D;

public class MainMenu {
	private boolean enabled;
	private Button buttons[] = {
			new Button("Play!", 230, 150, 180, 40),
			new Button("Multiplayer", 230, 200, 180, 40)
	};
	
	TextField test = new TextField(220, 400, 200, 30);
	
	public int update() {
		
		// if the menu is disabled, enable it
		if(!enabled)
			setEnabled(true);
		
		for(int i = 0; i<buttons.length; i++)
			if(buttons[i].wasPressed) {
				return i;
			}
		
		return -1;
	}
	
	public void render(Graphics2D g, int width, int height) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		
		// draw the logo
		g.drawImage(SpriteManager.getInstance().logo, width / 2 - 160, 10, null);
		
		// draw every button
		for(int i = 0; i<buttons.length; i++)
			buttons[i].render(g);
		
		test.render(g);
	}
	
	public void setEnabled(boolean b) {
		enabled = b;
		for(int i = 0; i<buttons.length; i++) {
			buttons[i].setEnabled(b);
		}
		test.setEnabled(b);
	}
}
