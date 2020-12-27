package de.victorswelt;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;

public class TeamSelectorPane {
	private static final String TITLE = "Select your team";
	private static final int TEAM_DISPLAY_OFFSET_X = 310;
	private static final int TEAM_DISPLAY_OFFSET_Y = 280;
	
	
	boolean enabled;
	int team = -1;
	
	ArrayList buttons;
	Button select;
	
	public TeamSelectorPane() {
		buttons = new ArrayList();
		select = new Button("Select Team", 260, 420, 120, 40);
		create_team_buttons(new int[] {0,1,2,3,4,5,6,7});
		
		setEnabled(false);
	}
	
	public int update() {
		if(!enabled)
			setEnabled(true);
		
		for(int i = 0; i<buttons.size(); i++) {
			TeamButton b = ((TeamButton) buttons.get(i));
			if(b.wasPressed) {
				team = b.team;
				b.wasPressed = false;
			}
		}
		
		if(team != -1 && !select.enabled)
			select.setEnabled(true);
		
		if(!select.wasPressed)
			return -1;
		return team;
	}
	
	public void render(Graphics2D g, int width, int height) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		
		// draw the title
		g.setFont(Utils.FONT_HEADER);
		FontMetrics fm = g.getFontMetrics();
		g.setColor(Color.WHITE);
		g.drawString(TITLE, width / 2 - fm.stringWidth(TITLE) / 2, 50);		
		
		// render the team buttons
		g.setFont(Utils.FONT_DEFAULT);
		for(int i = 0; i<buttons.size(); i++) {
			((TeamButton) buttons.get(i)).render(g);
		}
		
		select.render(g);
		
		// render the selected team
		if(team == -1) {
			g.setColor(Color.RED);
			g.drawLine(TEAM_DISPLAY_OFFSET_X, TEAM_DISPLAY_OFFSET_Y + 32, TEAM_DISPLAY_OFFSET_X + 32, TEAM_DISPLAY_OFFSET_Y);
			
		}
		else {
			g.setColor(SpriteManager.getInstance().getTeamColor(team));
			g.fillRect(TEAM_DISPLAY_OFFSET_X + 1, TEAM_DISPLAY_OFFSET_Y + 1, 31, 31);
		}
		
		g.setColor(Color.WHITE);
		g.drawRect(TEAM_DISPLAY_OFFSET_X, TEAM_DISPLAY_OFFSET_Y, 32, 32);
	}
	
	public void setEnabled(boolean b) {
		team = -1;
		for(int i = 0; i<buttons.size(); i++) {
			((TeamButton) buttons.get(i)).setEnabled(b);
		}
		select.setEnabled(false);
		
		enabled = b;
	}
	
	public void create_team_buttons(int[] availble_teams) {
		int tbutton_x_off = 640 / 2 - availble_teams.length * 32 / 2 + 8;
		for(int i = 0; i<availble_teams.length; i++) {
			int t = availble_teams[i];
			
			buttons.add(new TeamButton(t, tbutton_x_off + i * 32, 200, 20, 20));
		}
	}
}

class TeamButton extends Button {
	int team;
	
	public TeamButton(int team, int nx, int ny, int w, int h) {
		super("" + team, nx, ny, w, h);
		this.team = team;
		background_colour = SpriteManager.getInstance().getTeamColor(team);
		highlight_colour  = new Color(brightenValue(background_colour.getRed(), 150, 255), 
				brightenValue(background_colour.getGreen(), 150, 255),
				brightenValue(background_colour.getBlue(), 150, 255));
	}
	
	private int brightenValue(int val, int amount, int max) {
		return (int) Math.min(val + amount, max);
		
	}
}
