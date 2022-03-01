package tool;

import java.awt.image.BufferedImage;

public class Pink {
	
	private static SImage image;
	private static int width;
	private static int height;
	private static double[][][] RGB;
	
	public static BufferedImage deal(BufferedImage input) {
		image = new SImage(input);
		width = image.width;
		height = image.height;
		RGB = image.getRGBMAtrixNormalize();
		double[][] re = new double[width][height];
		double m = 1;
		double n = .5;
		double p = 1;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				double r = RGB[i][j][0];
				double g = RGB[i][j][1];
				double b = RGB[i][j][2];
				double distance = Math.sqrt((r-m)*(r-m) + (g-n)*(g-n) + (b-p)*(b-p));
				re[i][j] = distance;
			}
		}
		re = Num.normalize(re);
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				re[i][j] = 1 - re[i][j];
				int c = (int)(re[i][j] * 255);
				image.setRGB(i, j, c, c, c);
			}
		}
		return image.getImage();
	}
	
}
