package de.victorswelt;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class MultiplayerConnectionWindow {
	private static final String FRAMES[] = {"/","-","\\","|"};
	
	int frame;
	String address;
	private Button cancelButton;
	
	public MultiplayerConnectionWindow() {
		cancelButton = new Button("cancel", 280, 280, 80, 40);
	}
	
	public boolean update() {
		if(!cancelButton.enabled)
			cancelButton.setEnabled(true);
		return cancelButton.wasPressed;
	}
	
	public void render(Graphics2D g, int width, int height) {
		// draw the background
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		g.setFont(Utils.FONT_DEFAULT);
		
		FontMetrics fm = g.getFontMetrics();
		
		// draw the text
		String text = "Connecting to the server ";
		g.setColor(Color.WHITE);
		int text_width = fm.stringWidth(text);
		g.drawString(text, width / 2 - text_width / 2, height / 2 - 16);
		g.setFont(Utils.FONT_MONO);
		g.drawString(FRAMES[frame/10], width / 2 + text_width / 2, height / 2 - 16);
		g.setFont(Utils.FONT_DEFAULT);
		
		frame++;
		if(frame / 10 == FRAMES.length) frame = 0;
		
		text = "Server Address: " + address;
		g.drawString(text, width / 2 - fm.stringWidth(text) / 2, height / 2 + 16);
		
		// draw the button
		g.setFont(Utils.FONT_DEFAULT);
		cancelButton.render(g);
	}
	
	public void setEnabled(boolean b) {
		cancelButton.setEnabled(b);
	}
}
