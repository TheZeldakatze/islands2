package de.victorswelt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class LevelMenu {
	private static final int LEVELS_PER_PAGE = 5;
	
	private Game game;
	private boolean enabled = false;
	private LevelPage pages[];
	private int current_page;
	
	private Button previous_page, next_page;
	
	public LevelMenu(Game g) {
		game = g;
		
		// initialize the navigation buttons
		previous_page = new Button("Previous Page", 30, 400, 100, 30);
		next_page     = new Button("Next Page", 500, 400, 100, 30);
		
		// load the level list
		String list_string = "";
		InputStream is = Game.class.getResourceAsStream("/de/victorswelt/level/LevelList.lst");
		try {
			while(is.available()>0) {
				list_string = list_string + ((char) is.read());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// split it into lines
		String lines[] = list_string.split("\n");
		
		// create the buttons
		ArrayList buttons = new ArrayList();
		
		for(int l = 0; l < lines.length; l++) {
			
			String split[] = lines[l].split(" ", 2);
			
			if(split.length == 2) {
				buttons.add(new LevelButton(split[1], 160, (l % LEVELS_PER_PAGE) * 40 + 30, 320, 30, split[0]));
			}
			else
				System.out.println("Could not parse entry in LevelList.lst! line: " + l + " (invalid length: " + split.length + ")");
		}
		
		// create the pages
		pages = new LevelPage[(int) (Math.floor(buttons.size() / LEVELS_PER_PAGE) + 1)];
		
		for(int p = 0; p<pages.length; p++) {
			
			// map 4 buttons to an array
			int limit = Math.min(LEVELS_PER_PAGE, buttons.size());
			LevelButton pageButtons[] = new LevelButton[limit];
			for(int i = 0; i<limit; i++) {
				pageButtons[i] = (LevelButton) buttons.get(0);
				buttons.remove(0);
			}
			
			// create a new page
			pages[p] = new LevelPage(pageButtons, g);
		}
	}
	
	public int update() {
		
		// check if a navigation button was pressed
		if(next_page.wasPressed) {
			// reset it
			next_page.wasPressed = false;
			
			// check if there is a next page
			if(current_page < pages.length - 1) {
				// disable the current page
				pages[current_page].setEnabled(false);
				
				// increment the page index
				current_page++;
				
				// enable the current page
				pages[current_page].setEnabled(true);
			}
		}
		
		if(previous_page.wasPressed) {
			previous_page.wasPressed = false;
			if(current_page>0) {
				// disable the current page
				pages[current_page].setEnabled(false);
				
				// decrement the page index
				current_page--;
				
				// enable the current page
				pages[current_page].setEnabled(true);
			}
		}
		
		switch(pages[current_page].update()) {
			case LevelPage.UPDATE_DEFAULT:
				return -1;
			case LevelPage.UPDATE_LEVEL_SELECTED: {
				return 0;
			}
		}
		
		return -1;
	}
	
	public void render(Graphics2D g, int width, int height) {
		
		// draw the page
		pages[current_page].render(g, width, height);
		
		// draw the navigation
		previous_page.render(g);
		next_page.render(g);
		
		// draw the index
		g.drawString("Page: " + (current_page + 1) + " / " + pages.length, 300, 420);
		
	}
	
	public void setEnabled(boolean b) {
		previous_page.setEnabled(b);
		next_page.setEnabled(b);
		
		// reset the menu
		pages[current_page].setEnabled(false);
		current_page = 0;
		pages[current_page].setEnabled(false);
		enabled = b;
	}
}

class LevelPage {
	static final int UPDATE_DEFAULT = 0;
	static final int UPDATE_LEVEL_SELECTED = 3;
	
	private boolean enabled = false;
	
	private Game game;
	
	LevelButton buttons[];
	
	public LevelPage(LevelButton nbuttons[], Game ngame) {
		buttons = nbuttons;
		game = ngame;
	}
	
	public int update()  {
		
		// if the menu was disabled, enable it
		if(!enabled)
			setEnabled(true);
		
		// check if the user pressed a level button
		for(int i = 0; i<buttons.length; i++) {
			if(buttons[i].wasPressed) {
				try {
					
					// try loading the level
					// get the level data
					String level_string = "";
					InputStream is = Game.class.getResourceAsStream("/de/victorswelt/level/" + buttons[i].getLevelName());
					try {
						while(is.available()>0) {
							level_string = level_string + ((char) is.read());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					// create the level
					Level l = Level.createLevel(level_string);
					
					// apply the level to the game
					game.setLevel(l);
					return UPDATE_LEVEL_SELECTED;
					
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		return UPDATE_DEFAULT;
	}
	
	public void render(Graphics2D g, int width, int height) {
		// draw the background
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		
		// draw the level buttons
		for(int i = 0; i<buttons.length; i++) {
			buttons[i].render(g);
		}
		
	}
	
	public void setEnabled(boolean b) {
		for(int i = 0; i<buttons.length; i++) {
			if(buttons[i] != null)
				buttons[i].setEnabled(b);
		}
		enabled = b;
	}
}