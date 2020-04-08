package de.victorswelt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
	private static final byte STATE_GAME         = 1;
	
	VolatileImage screen;
	Image loading_banner;
	int state;
	
	Game game;
	
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
		screen = createVolatileImage(SCREEN_WIDTH, SCREEN_HEIGHT);
		
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
				break;
			}
			
			case STATE_MAIN_MENU: {
				break;
			}
			
			case STATE_GAME: {
				game.update();
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
				screen = createVolatileImage(SCREEN_WIDTH, SCREEN_HEIGHT);
			
			// create a graphics context
			Graphics2D g = screen.createGraphics();
			
			switch(state) {
				case STATE_INITIALIZING: {
					g.setColor(Color.WHITE);
					g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
					g.drawImage(loading_banner, 160, 210, null);
					break;
				}
				
				case STATE_CLEANUP: {
					loading_banner.flush();
					System.gc(); // I know that you should not use System.gc(), but here would be an excellent time for a garbage collection
					state = STATE_GAME;
					break;
				}
				
				case STATE_MAIN_MENU: {
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
		Graphics panel_graphics = getGraphics();
		panel_graphics.drawImage(screen, 0, 0, getWidth(), getHeight(), null);
		panel_graphics.dispose();
	}

}
