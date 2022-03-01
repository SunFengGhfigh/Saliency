package tool;

import java.awt.image.BufferedImage;

public class R {

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
				int c = (int)(RGB[i][j][0] * 255);
				image.setRGB(i, j, c, c, c);
			}
		}
		
//		//----------------------------------------------------------------------------
		System.out.println(image.RGB[0][0][0] + " " + image.RGB[0][0][1] + " " + image.RGB[0][0][2]);
		System.out.println(image.HED[0][0][0] + " " + image.HED[0][0][1] + " " + image.HED[0][0][2]);
//		System.out.println(image.YIQ[0][0][0] + " " + image.YIQ[0][0][1] + " " + image.YIQ[0][0][2]);
//		System.out.println(image.Lab[0][0][0] + " " + image.Lab[0][0][1] + " " + image.Lab[0][0][2]);
//		System.out.println(image.LCH[0][0][0] + " " + image.LCH[0][0][1] + " " + image.LCH[0][0][2]);
//		//----------------------------------------------------------------------------
		
		
		
		return image.getImage();
	}

}
