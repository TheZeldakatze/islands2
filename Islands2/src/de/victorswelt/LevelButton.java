package de.victorswelt;

public class LevelButton extends Button {
	private String level;

	public LevelButton(String ntext, int nx, int ny, int w, int h, String nlevel) {
		super(ntext, nx, ny, w, h);
		level = nlevel;
	}
	
	public String getLevelName() {
		return level;
	}
	
}
