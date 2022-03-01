package algorithm;

import java.awt.image.BufferedImage;

import tool.SImage;

public class Grey {
	
	public static BufferedImage deal(BufferedImage input) {
		SImage image = new SImage(input);
		for (int x = 0; x < image.width; x++) {
			for (int y = 0; y < image.height; y++) {
				int r = image.getR(x, y);
				int g = image.getG(x, y);
				int b = image.getB(x, y);
				int grey = (int)(r * 0.3d + g * 0.6d + b * 0.1d);
				image.setRGB(x, y, grey, grey, grey);
			}
		}
		return image.getImage();
	}

}
