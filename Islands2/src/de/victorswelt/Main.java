package de.victorswelt;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.net.InetSocketAddress;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;
	
	public static final int GAME_TICK_LENGTH = 12;
	public static final int TICKS_PER_SECOND = 1000 / 12;
	
	public static final int SCREEN_WIDTH = 640;
	public static final int SCREEN_HEIGHT = 480;
	
	private static final byte STATE_INITIALIZING = -128;
	private static final byte STATE_CLEANUP = -127;
	private static final byte STATE_ERROR_MESSAGE        = -126;
	
	private static final byte STATE_MAIN_MENU            = 0;
	private static final byte STATE_LEVEL_MENU           = 1;
	private static final byte STATE_GAME                 = 2;
	public static final byte STATE_MP_SERVER_SELECT      = 3; // MP = Multiplayer
	private static final byte STATE_MP_SERVER_CONNECTING = 4;
	private static final byte STATE_MP_SELECT_TEAM       = 5;
	private static final byte STATE_MP_SERVER_GAME       = 6;

	static JFrame frame;
	boolean fullscreen;
	VolatileImage screen;
	Image loading_banner;
	int state;
	
	Game game;
	MainMenu main_menu;
	LevelMenu level_menu;
	ServerSelectMenu server_select_menu;
	MultiplayerConnectionWindow multiplayer_connection_window;
	ErrorWindow error_window;
	TeamSelectorPane team_selector_pane;
	
	public static void main(String[] args) {
		frame = new JFrame();
		Main m = new Main();
		frame.setSize(640, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(m);
		frame.setVisible(true);
		
		// set the proper size
		Insets frame_insets = frame.getInsets();
		frame.setSize(640 + frame_insets.bottom + frame_insets.top, 480 + frame_insets.left + frame_insets.right);
		frame.setLocationRelativeTo(null);
		frame.toFront();
		
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
		
		// initialize the KeyboardHandler
		KeyboardHandler.init();
		addKeyListener(KeyboardHandler.getInstance());
		requestFocusInWindow();
		
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
		
		// initialize the FastMath library
		FastMath.init();
		
		try {
			SpriteManager.init();
			game = new Game();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		level_menu = new LevelMenu(game);
		main_menu = new MainMenu();
		server_select_menu = new ServerSelectMenu();
		multiplayer_connection_window = new MultiplayerConnectionWindow();
		error_window = new ErrorWindow();
		team_selector_pane = new TeamSelectorPane();
		
		// when the initialization is finished, change the state to the cleanup one
		state = STATE_CLEANUP;
		
		// show an info message
		error_window.setup("Note: this is a test build", STATE_MAIN_MENU);
		state = STATE_ERROR_MESSAGE;
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
			
			case STATE_ERROR_MESSAGE: {
				if(error_window.update()) {
					error_window.setEnabled(false);
					state = error_window.getReturnState();
				}
			} break;
			
			case STATE_MAIN_MENU: {
				switch(main_menu.update()) {
					case 0: // singleplayer button
						state = STATE_LEVEL_MENU;
						main_menu.setEnabled(false);
						break;
					case 1: // multiplayer button
						state = STATE_MP_SERVER_SELECT;
						main_menu.setEnabled(false);
						break;
					case 2: // fullscreen button
						main_menu.setEnabled(false);
						setFullscreen(!fullscreen, true);
						break;
					case 3: // quit button
						System.exit(0);
						break;
				};
				break;
			}
			
			case STATE_LEVEL_MENU: {
				switch(level_menu.update()) {
					case LevelMenu.RESPONSE_IDLE:
						break;
					
					case LevelMenu.RESPONSE_BACK_TO_MAIN_MENU:
						state = STATE_MAIN_MENU;
						level_menu.setEnabled(false);
						break;
						
					case LevelMenu.RESPONSE_LEVEL_SELECTED:
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
			
			case STATE_MP_SERVER_SELECT: {
				switch(server_select_menu.update()) {
					case ServerSelectMenu.RESPONSE_IDLE:
						break;
					case ServerSelectMenu.RESPONSE_BACK_TO_MAIN_MENU:
						state = STATE_MAIN_MENU;
						server_select_menu.setEnabled(false);
						break;
					case ServerSelectMenu.RESPONSE_LEVEL_SELECTED:
						server_select_menu.setEnabled(false);
						
						// read the username
						String username = server_select_menu.name_field.text;
						if(username.isEmpty()) {
							displayErrorStateMessage(Main.STATE_MP_SERVER_SELECT, "Please specify a username!");
							break;
						}
						
						// create a socket address
						InetSocketAddress socketAddress = null;
						try {
							String addr = server_select_menu.ip_field.text;
							if(addr == null || addr.isEmpty())
								break;
							
							multiplayer_connection_window.address = addr;
							String split[] = addr.split(":", 2);
							if(split.length == 1) {
								socketAddress = InetSocketAddress.createUnresolved(split[0], 53456);
							}
							else
								socketAddress = InetSocketAddress.createUnresolved(split[0], Integer.parseInt(split[1]));
						} catch(Exception e) {
							displayErrorStateMessage(Main.STATE_MP_SERVER_SELECT, "Cannot parse server address!");
							e.printStackTrace();
							break;
						}
						
						// TODO do the multiplayer stuff
						
						// create a multiplayer level
						System.out.println(socketAddress);
						MultiplayerLevel ml = new MultiplayerLevel(this, username, socketAddress);
						game.setLevel(ml);
						state = STATE_MP_SERVER_CONNECTING;
						break;
				}
					
				break;
			}
			
			case STATE_MP_SERVER_CONNECTING: {
				MultiplayerLevel level = (MultiplayerLevel) game.getLevel();
				if(!level.isLoading()) {
					multiplayer_connection_window.setEnabled(false);
					state = STATE_MP_SELECT_TEAM;
					
					// update the team panel
					team_selector_pane.setAvailableTeams(level.getAvailableTeams());
					
					break;
				}
				
				if(multiplayer_connection_window.update()) {
					multiplayer_connection_window.setEnabled(false);
					state = STATE_MP_SERVER_SELECT;
				}
			} break;
			
			case STATE_MP_SELECT_TEAM: {
				int ret = team_selector_pane.update();
				if(ret != -1) {
					MultiplayerLevel level = (MultiplayerLevel) game.getLevel();
					level.playerTeam = ret;
					team_selector_pane.setEnabled(false);
					state = STATE_MP_SERVER_GAME;
				}
			} break;
			
			case STATE_MP_SERVER_GAME: {
				MultiplayerLevel level = (MultiplayerLevel) game.getLevel();
				
				// if the connection was closed, return
				if(!level.isListenerRunning()) {
					displayErrorStateMessage(STATE_MP_SERVER_SELECT, "Disconnected from server");
				}
					
				game.update();
			} break;
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
			g.setFont(Utils.FONT_DEFAULT);
			
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
				
				case STATE_ERROR_MESSAGE: {
					error_window.render(g, SCREEN_WIDTH, SCREEN_HEIGHT);
				} break;
				
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
					break;
				}
				
				case STATE_MP_SERVER_SELECT: {
					server_select_menu.render(g, SCREEN_WIDTH, SCREEN_HEIGHT);
					break;
				}
				
				case STATE_MP_SERVER_CONNECTING: {
					multiplayer_connection_window.render(g, SCREEN_WIDTH, SCREEN_HEIGHT);
				} break;
				
				case STATE_MP_SELECT_TEAM: {
					team_selector_pane.render(g, SCREEN_WIDTH, SCREEN_HEIGHT);
				} break;
				
				case STATE_MP_SERVER_GAME: {
					game.render(g, SCREEN_WIDTH, SCREEN_HEIGHT);
				} break;
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
	
	public void displayErrorStateMessage(byte returnState, String message) {
		error_window.setup(message, returnState);
		state = STATE_ERROR_MESSAGE;
	}
	
	public void setFullscreen(boolean b, boolean performModeSwitch) {
		try {
			GraphicsDevice display = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			display.setFullScreenWindow(frame);
			
			if(performModeSwitch) {
				// get the avaible display modes
				DisplayMode modes[] = display.getDisplayModes();
				
				// get the one closest to 640x480
				DisplayMode bestMode = null;
				
				for(int i = 0; i<modes.length; i++) {
					if(modes[i].getWidth() == 640 && modes[i].getWidth() == 480 && (bestMode == null || modes[i].getRefreshRate() > bestMode.getRefreshRate()))
						bestMode = modes[i];
				}
				
				if(bestMode != null)
					display.setDisplayMode(bestMode);
				
				fullscreen = true;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
