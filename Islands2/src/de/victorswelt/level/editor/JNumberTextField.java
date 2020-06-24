package de.victorswelt.level.editor;

import java.awt.event.KeyEvent;

import javax.swing.JTextField;

public class JNumberTextField extends JTextField {
	public void processKeyEvent(KeyEvent e) {
		char c = e.getKeyChar();
		if(Character.isDigit(c) || c == KeyEvent.VK_BACK_SPACE)
			super.processKeyEvent(e);
		e.consume();
		return;
	}
	
	public int getInt() {
		try {
			String text = getText();
			if(text == null || text.equals(""))
				return 0;
			return Integer.parseInt(text);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
