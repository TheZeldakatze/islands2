package de.victorswelt;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class MouseHandler implements MouseListener, MouseMotionListener {
	int window_width, window_height, draw_width, draw_height;
	ArrayList handlers;
	
	private MouseHandler() {
		handlers = new ArrayList(1);
	}
	
	public void mouseDragged(MouseEvent e) {}

	public void mouseMoved(MouseEvent e) {
		int x = scale_x(e.getX());
		int y = scale_y(e.getY());
		for(int i = 0; i<handlers.size(); i++) ((MouseInterface) handlers.get(i)).onMouseMove(x, y);
	}

	public void mouseClicked(MouseEvent e) {
		int x = scale_x(e.getX());
		int y = scale_y(e.getY());
		for(int i = 0; i<handlers.size(); i++) ((MouseInterface) handlers.get(i)).onMouseClick(x, y);
	}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}
	
	private int scale_x(int x) {
		if(window_width < 1)
			return 0;

		return x * draw_width / window_width;
	}
	
	private int scale_y(int y) {
		if(window_height < 1)
			return 0;

		return y * draw_height / window_height;
	}

	public void setWindowSize(int width, int height) {
		window_width = width;
		window_height = height;
	}
	
	public void setCanvasSize(int width, int height) {
		draw_width = width;
		draw_height = height;
	}
	
	public void add_interface(MouseInterface m) {
		handlers.add(m);
	}
	
	// SINGLETON stuff
	private static MouseHandler INSTANCE;
	public static void init() {INSTANCE = new MouseHandler();}
	public static MouseHandler getInstance() {return INSTANCE;}
}
