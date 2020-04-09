package de.victorswelt;

import java.awt.Color;
import java.awt.Graphics2D;

public class MainMenu {
	private Button buttons[] = {
			new Button("Play!", 180, 50, 320, 40),
	};
	
	public int update() {
		for(int i = 0; i<buttons.length; i++)
			if(buttons[i].wasPressed)
				return i;
		
		return -1;
	}
	
	public void render(Graphics2D g, int width, int height) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		for(int i = 0; i<buttons.length; i++)
			buttons[i].render(g);
	}
	
	public void setEnabled(boolean b) {
		for(int i = 0; i<buttons.length; i++)
			buttons[i].setEnabled(b);
		for(int i = 0; i<buttons.length; i++)
			buttons[i].wasPressed = false;
	}
}
