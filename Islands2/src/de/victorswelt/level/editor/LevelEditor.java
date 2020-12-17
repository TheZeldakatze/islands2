package de.victorswelt.level.editor;

import java.awt.CardLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import de.victorswelt.SpriteManager;

public class LevelEditor extends JFrame {
	private static final long serialVersionUID = 1L;
	
	EditorPane editor_pane;
	
	public static void main(String[] args) {
		new LevelEditor();
	}
	
	public LevelEditor() {
		// initialize the sprite manager
		try {
			SpriteManager.init();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// create a jframe for the editor pane
		final JFrame editor_frame = new JFrame("Islands2 Editor");
		editor_frame.getContentPane().setLayout(new CardLayout());
		editor_pane = new EditorPane();
		editor_frame.getContentPane().add(editor_pane);
		
		// set the size
		Insets editor_frame_insets = editor_frame.getInsets();
		editor_frame.setSize(640 + editor_frame_insets.bottom + editor_frame_insets.top, 480 + editor_frame_insets.left + editor_frame_insets.right);
		editor_frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		editor_frame.setLocationRelativeTo(null);
		editor_frame.toFront();
		
		// create the editor menu
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		// the tool selector
		final JButton toolSelectButton = ((JButton) getContentPane().add(new JButton("Tool: Island")));
		toolSelectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newName = "Tool: ";
				
				// increment the tool type index
				editor_pane.currentTool++;
				if(editor_pane.currentTool>2)
					editor_pane.currentTool = 0;
				
				// set the tool name
				switch(editor_pane.currentTool) {
					case 0: newName = newName + "Island"; break;
					case 1: newName = newName + "Obstacle"; break;
					case 2: newName = newName + "remove"; break;
				}
				
				// set the buttons name
				toolSelectButton.setText(newName);
			}
		});
		
		// the save button
		final JFileChooser chooser = new JFileChooser();
		((JButton) getContentPane().add(new JButton("Save"))).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int val = chooser.showSaveDialog(editor_frame);
				if(val == JFileChooser.APPROVE_OPTION) {
					// get the file
					File f = chooser.getSelectedFile();
					String levelData = editor_pane.serialize();
					
					try {
						FileOutputStream fos = new FileOutputStream(f);
						fos.write(levelData.getBytes());
						fos.close();
						
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		// the load button
		((JButton) getContentPane().add(new JButton("Load"))).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int val = chooser.showOpenDialog(editor_frame);
				if(val == JFileChooser.APPROVE_OPTION) {
					// get the file
					File f = chooser.getSelectedFile();
					String in = "";
					
					// read it
					try {
						FileInputStream fis = new FileInputStream(f);
						while(fis.available() > 0)
							in = in + (char) fis.read();
						fis.close();
						
					} catch (FileNotFoundException e1) {e1.printStackTrace();} catch (IOException e1) {e1.printStackTrace();}
					
					// deserialize the level
					editor_pane.deserialize(in);
				}
			}
		});
		
		// the clear button
		((JButton) getContentPane().add(new JButton("Clear"))).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int input = JOptionPane.showConfirmDialog(null, "Are you sure you want to clear the field?", "Confirmation", JOptionPane.YES_NO_OPTION);
				if(input == JOptionPane.YES_OPTION) {
					editor_pane.islands.clear();
					editor_pane.obstacles.clear();
				}
			}
		});
		
		// the population field
		add(new JLabel("Island population:"));
		final JNumberTextField populationField = new JNumberTextField();
		add(populationField);
		
		populationField.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {change();}
			public void insertUpdate(DocumentEvent e) {change();}
			public void changedUpdate(DocumentEvent e) {change();}
			void change() {
				try {
					editor_pane.island_size = populationField.getInt();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		// the team field
		add(new JLabel("team index number:"));
		final JNumberTextField teamField = new JNumberTextField();
		add(teamField);
		teamField.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent e) {change();}
			public void insertUpdate(DocumentEvent e) {change();}
			public void changedUpdate(DocumentEvent e) {change();}
			void change() {
				try {
					editor_pane.island_team = teamField.getInt();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		// show the frames
		pack();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(true);
		toFront();
		editor_frame.setVisible(true);
	}
}
