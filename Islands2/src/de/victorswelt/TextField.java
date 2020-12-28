package de.victorswelt;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class TextField extends UIComponent implements KeyboardInterface, MouseInterface {
	int x, y, width, height;
	String text = "";
	boolean focused, enabled;
	
	public TextField(int x, int y, int width, int height) {
		KeyboardHandler.getInstance().add_interface(this);
		MouseHandler.getInstance().add_interface(this);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	
	public void render(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.drawRect(x, y, width, height);
		
		// draw the background
		if(!focused)
			g.setColor(Color.BLACK);
		else
			g.setColor(Color.DARK_GRAY);
		g.fillRect(x+1, y+1, width-2, height-2);
		
		// draw the text
		FontMetrics fm = g.getFontMetrics();
		g.setColor(Color.RED);
		g.drawString(text,  10 + x, y + height / 2 + fm.getHeight() / 2);
	}
	

	public void onKeyTyped(char c) {
		if(focused && enabled) {
			if(c == KeyEvent.VK_BACK_SPACE) {
				if(text.length() > 0)
					text = text.substring(0, text.length()-1);
			}
			else
			if(c>31 && c<127) {
				text = text + c;
			}
		}
	}
	
	public void setEnabled(boolean b) {
		enabled = b;
	}
	
	public void onKeyPressed(char c) {
		
	}

	public void onKeyReleased(char c) {
	}



	public void onMouseClick(int x, int y) {
		if(Utils.checkCollision(this.x, this.y, width, height, x, y, 1, 1)) {
			focused = true;
		}
		else
			focused = false;
	}

	public void onMouseMove(int x, int y) {}
}
