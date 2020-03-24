package de.victorswelt;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.RGBImageFilter;

public class ColourReplacementFilter extends RGBImageFilter {
	private Color original, replacement;
	
	public void setOriginalColor(Color c) {
		original = c;
	}
	
	public void setReplacementColor(Color c) {
		replacement = c;
	}
	
	public Image processImage(Image input) {
		return Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(input.getSource(), this));
	}
	
	public int filterRGB(int x, int y, int rgb) {
		if(rgb == original.getRGB())
			return replacement.getRGB();
		return rgb;
	}
}
