package de.victorswelt;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class KeyboardHandler implements KeyListener {
	ArrayList handlers;
	
	public KeyboardHandler() {
		handlers = new ArrayList();
		
	}
	
	public void keyTyped(KeyEvent e) {
		char c = e.getKeyChar();
		for(int i = 0; i<handlers.size(); i++) ((KeyboardInterface) handlers.get(i)).onKeyTyped(c);
	}

	public void keyPressed(KeyEvent e) {
		char c = e.getKeyChar();
		for(int i = 0; i<handlers.size(); i++) ((KeyboardInterface) handlers.get(i)).onKeyPressed(c);
	}

	public void keyReleased(KeyEvent e) {
		char c = e.getKeyChar();
		for(int i = 0; i<handlers.size(); i++) ((KeyboardInterface) handlers.get(i)).onKeyReleased(c);
	}
	
	public void add_interface(KeyboardInterface ki) {
		handlers.add(ki);
	}
	
	public void remove_interface(KeyboardInterface ki) {
		handlers.remove(ki);
	}
	
	// SINGLETON stuff
	private static KeyboardHandler OBJECT;
	public static void init() {OBJECT = new KeyboardHandler();}
	public static KeyboardHandler getInstance() {return OBJECT;}
}
