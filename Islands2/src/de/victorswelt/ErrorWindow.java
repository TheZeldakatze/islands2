package de.victorswelt;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class ErrorWindow {
	private byte returnState;
	private String message;
	private Button returnButton;
	private boolean enabled;
	
	public ErrorWindow() {
		returnButton = new Button("Return", 280, 280, 80, 40);
	}
	
	public void setup(String message, byte returnState) {
		this.returnState = returnState;
		this.message = message;
	}
	
	public void setEnabled(boolean b) {
		enabled = b;
		returnButton.setEnabled(b);
	}
	
	public boolean update() {
		if(!enabled)
			setEnabled(true);
		return returnButton.wasPressed;
	}
	
	public void render(Graphics2D g, int width, int height) {
		// draw the background
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		g.setFont(Utils.FONT_DEFAULT);
		
		FontMetrics fm = g.getFontMetrics();
		
		// draw the text
		g.setColor(Color.WHITE);
		int text_width = fm.stringWidth(message);
		g.drawString(message, width / 2 - text_width / 2, height / 2 - 16);
		
		// draw the button
		g.setFont(Utils.FONT_DEFAULT);
		returnButton.render(g);
	}
	
	public byte getReturnState() {
		return returnState;
	}
}
