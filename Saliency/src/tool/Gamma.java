package tool;

import java.awt.image.BufferedImage;

import algorithm.Grey;

public class Gamma {
	
	private static SImage image;
	private static int width;
	private static int height;
	private static double[][][] RGB;
	
	// Gamma 0.5
	public static BufferedImage deal(BufferedImage input) {
		input = Grey.deal(input);
		image = new SImage(input);
		width = image.width;
		height = image.height;
		RGB = image.getRGBMAtrix();
		double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				for (int c = 0; c < 3; c++) {
					double a = sqrt(RGB[i][j][c]);
					if (a > max) max = a; if (a < min) min = a;
					RGB[i][j][c] = a;
				}
			}
		}
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				for (int c = 0; c < 3; c++) {
					RGB[i][j][c] -= min;
					RGB[i][j][c] /= (max - min);
					RGB[i][j][c] *= 255;
				}
				image.setRGB(i, j, (int)RGB[i][j][0], (int)RGB[i][j][1], (int)RGB[i][j][2]);
			}
		}
		return image.getImage();
	}
	
	private static double sqrt(double a) {
		return Math.sqrt(a);
	}

}
