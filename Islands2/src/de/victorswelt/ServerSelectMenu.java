package de.victorswelt;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class ServerSelectMenu {
	private static final String TITLE = "Multiplayer Test";
	
	public static final int RESPONSE_IDLE = -1;
	public static final int RESPONSE_LEVEL_SELECTED = 0;
	public static final int RESPONSE_BACK_TO_MAIN_MENU = 1;
	
	Button back, connect;
	TextField ip_field;
	boolean enabled;
	
	public ServerSelectMenu() {
		back    =  new Button("Back", 10, 10, 50, 20);
		connect =  new Button("Connect", 230, 250, 180, 40);
		ip_field = new TextField(230, 190, 180, 40);
	}
	
	public int update() {
		
		// automaticly enable the menu
		if(!enabled)
			setEnabled(true);
		
		// send the response code
		if(back.wasPressed)    return RESPONSE_BACK_TO_MAIN_MENU;
		if(connect.wasPressed) return RESPONSE_LEVEL_SELECTED;
		return RESPONSE_IDLE;
	}
	
	public void render(Graphics2D g, int width, int height) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		
		back.render(g);
		connect.render(g);
		ip_field.render(g);
		
		// draw the title
		g.setFont(Utils.FONT_HEADER);
		FontMetrics fm = g.getFontMetrics();
		g.setColor(Color.WHITE);
		g.drawString(TITLE, width / 2 - fm.stringWidth(TITLE) / 2, 100);
	}
	
	public void setEnabled(boolean b) {
		back.setEnabled(b);
		connect.setEnabled(b);
		ip_field.setEnabled(b);
		enabled = b;
	}
}
