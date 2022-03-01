package tool;

import java.awt.image.BufferedImage;

public class G {

	private static SImage image;
	private static int width;
	private static int height;
	private static double[][][] RGB;
	
	public static BufferedImage deal(BufferedImage input) {
		image = new SImage(input);
		width = image.width;
		height = image.height;
		RGB = image.getRGBMAtrixNormalize();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int c = (int)(RGB[i][j][1] * 255);
				image.setRGB(i, j, c, c, c);
			}
		}
		return image.getImage();
	}

}
