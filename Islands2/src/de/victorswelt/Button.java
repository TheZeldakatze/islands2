package de.victorswelt;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class Button extends UIComponent implements MouseInterface {
	String text;
	int x,y,width,height;
	boolean enabled = true, selected, wasPressed;
	protected Color background_colour = Color.GRAY, highlight_colour = Color.LIGHT_GRAY;
	
	public Button(String ntext, int nx, int ny, int w, int h) {
		text = ntext;
		x = nx;
		y = ny;
		width = w;
		height = h;
		MouseHandler.getInstance().add_interface(this);
	}

	public void setEnabled(boolean b) {
		enabled = b;
		selected = false;
		wasPressed = false;
	}
	
	public void render(Graphics2D g) {
		if(selected || !enabled)
			g.setColor(highlight_colour);
		else
			g.setColor(background_colour);
		g.fillRect(x, y, width, height);
		FontMetrics fm = g.getFontMetrics();
		int fontWidth = fm.stringWidth(text);
		g.setColor(Color.WHITE);
		g.drawRect(x, y, width, height);
		g.drawString(text, x + width / 2 - fontWidth / 2, y + (height / 2 - fm.getHeight() / 2) + fm.getAscent());
	}
	
	public void onMouseClick(int x, int y) {
		if(enabled) {
			if(Utils.checkCollision(this.x, this.y, width, height, x, y, 1, 1)) {
				wasPressed = true;
				SoundManager.SOUND_BUTTON_PRESSED.play();
			}
		}
	}

	public void onMouseMove(int x, int y) {
		if(enabled) {
			if(Utils.checkCollision(this.x, this.y, width, height, x, y, 1, 1)) {
				selected = true;
			}
			else
				selected = false;
		}
	}
	
	
}
