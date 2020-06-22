package de.victorswelt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.VolatileImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;
	
	public static final int GAME_TICK_LENGTH = 12;
	public static final int TICKS_PER_SECOND = 1000 / 12;
	
	private static final int SCREEN_WIDTH = 640;
	private static final int SCREEN_HEIGHT = 480;
	
	private static final byte STATE_INITIALIZING = -128;
	private static final byte STATE_CLEANUP = -127;
	
	private static final byte STATE_MAIN_MENU    = 0;
	private static final byte STATE_LEVEL_MENU   = 1;
	private static final byte STATE_GAME         = 2;
	
	VolatileImage screen;
	Image loading_banner;
	int state;
	
	Game game;
	MainMenu main_menu;
	LevelMenu level_menu;
	
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		Main m = new Main();
		frame.setSize(640, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(m);
		frame.setVisible(true);
		m.start();
	}
	
	public Main() {
		
	}
	
	public void start() {
		state = STATE_INITIALIZING;
		
		// initialize the offscreen
		screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleVolatileImage(SCREEN_WIDTH, SCREEN_HEIGHT, Transparency.OPAQUE);
		
		// initialize the MouseHandler
		MouseHandler.init();
		addMouseListener(MouseHandler.getInstance());
		addMouseMotionListener(MouseHandler.getInstance());
		MouseHandler.getInstance().setWindowSize(getWidth(), getHeight());
		MouseHandler.getInstance().setCanvasSize(SCREEN_WIDTH, SCREEN_HEIGHT);
		addComponentListener(new ComponentListener() {
			public void componentShown(ComponentEvent e) {}
			
			public void componentResized(ComponentEvent e) {
				MouseHandler.getInstance().setWindowSize(getWidth(), getHeight());
			}
			
			public void componentMoved(ComponentEvent e) {}
			public void componentHidden(ComponentEvent e) {}
		});
		
		// initialize the game
		// try drawing the banner
		try {
			loading_banner = ImageIO.read(Main.class.getResourceAsStream("sprite/banner.gif"));
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// start the game thread
		new Thread(this, "GameThread").start();
		
		try {
			SpriteManager.init();
			game = new Game();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		level_menu = new LevelMenu(game);
		main_menu = new MainMenu();
		
		// when the initialization is finished, change the state to the cleanup one
		state = STATE_CLEANUP;
	}

	public void run() {
		
		// the game loop
		while(true) {
			gameRoutine();
			graphicsRoutine();
			
			try {
				Thread.sleep(12);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void gameRoutine() {
		switch(state) {
			case STATE_INITIALIZING: {
				break;
			}
			
			case STATE_CLEANUP: {
				loading_banner.flush();
				System.gc(); // I know that you should not use System.gc(), but here would be an excellent time for a garbage collection
				state = STATE_MAIN_MENU;
				break;
			}
			
			case STATE_MAIN_MENU: {
				switch(main_menu.update()) {
					case 0:
						state = STATE_LEVEL_MENU;
						main_menu.setEnabled(false);
						break;
				};
				break;
			}
			
			case STATE_LEVEL_MENU: {
				switch(level_menu.update()) {
					case 0:
						state = STATE_GAME;
						level_menu.setEnabled(false);
						break;
				};
				break;
			}
			
			case STATE_GAME: {
				if(game.update()) {
					// the game is over
					state = STATE_MAIN_MENU;
				}
				
				break;
			}
		}
	}
	
	/**
	 * Draws all the stuff.
	 * It also initializes some things */
	private void graphicsRoutine() {
		do {
			// if the screen is not initialized or incompatible with the graphics configuration
			if(screen == null || screen.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE)
				screen =  GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleVolatileImage(SCREEN_WIDTH, SCREEN_HEIGHT, Transparency.OPAQUE);
			
			// create a graphics context
			Graphics2D g = screen.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
			g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
			g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			
			switch(state) {
				case STATE_INITIALIZING: {
					g.setColor(Color.WHITE);
					g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
					g.drawImage(loading_banner, 160, 210, null);
					break;
				}
				
				case STATE_CLEANUP: {
					break;
				}
				
				case STATE_MAIN_MENU: {
					main_menu.render(g, SCREEN_WIDTH, SCREEN_HEIGHT);
					break;
				}
				
				case STATE_LEVEL_MENU: {
					level_menu.render(g, SCREEN_WIDTH, SCREEN_HEIGHT);
					break;
				}
				
				case STATE_GAME: {
					game.render(g, SCREEN_WIDTH, SCREEN_HEIGHT);
				}
				
			}
			
			// dispose the graphics object
			g.dispose();
		} while(screen.contentsLost());
		
		// draw the screen onto the panel
		Graphics2D panel_graphics = (Graphics2D) getGraphics();
		
		// turn off all the fancy drawing stuff
		panel_graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		panel_graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		panel_graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		panel_graphics.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
		panel_graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		panel_graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		panel_graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		panel_graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		panel_graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		
		// draw the screen
		panel_graphics.drawImage(screen, 0, 0, getWidth(), getHeight(), null);
		panel_graphics.dispose();
	}

}
