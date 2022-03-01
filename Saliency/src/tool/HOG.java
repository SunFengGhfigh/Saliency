package tool;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class HOG {
	
	private static SImage image;
	private static int width;
	private static int height;
	private static double[][][] RGB;
	private static double[][] Gradient_Direction;
	private static double[][] Gradient_Magnitude;
	
	public static BufferedImage deal(BufferedImage input) {
		initial(input);
		cal_gradient();
		
		double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
		for (int i = 1; i < width - 1; i++) {
			for (int j = 1; j < height - 1; j++) {
				if (Gradient_Magnitude[i][j] > max) max = Gradient_Magnitude[i][j];
				if (Gradient_Magnitude[i][j] < min) min = Gradient_Magnitude[i][j];
			}
		}
		for (int i = 1; i < width - 1; i++) {
			for (int j = 1; j < height - 1; j++) {
				double a = Gradient_Magnitude[i][j];
				a -= min; a /= (max - min);
				a *= 255;
				int b = (int)a;
				image.setRGB(i, j, b, b, b);
			}
		}
		
//		for (int i = 1; i < width-1; i += 8) {
//			int start_x = i, end_x = i + 8 > width-1 ? width-1 : i + 8;
//			for (int j = 1; j < height-1; j += 8) {
//				int start_y = j, end_y = j + 8 > height-1 ? height-1 : j + 8;
//				
//				double[] HG = new double[9];
//				for (int x = start_x; x < end_x; x++) {
//					for (int y = start_y; y < end_y; y++) {
//						HG[(int)(Gradient_Direction[x][y] / 20) + 4] += Gradient_Magnitude[x][y];
//					}
//				}
//				
//				HG = Num.normalize(HG);
//				
//				for (int x = start_x; x < end_x; x++) {
//					for (int y = start_y; y < end_y; y++) {
//						int r = (int)(HG[(int)(Gradient_Direction[x][y] / 20) + 4] * 255);
//						image.setRGB(x, y, r, r, r);
//					}
//				}
//				
//			}
//		}
		
		return image.getImage();
	}
	
	private static void initial(BufferedImage input) {
		input = Gamma.deal(input);
		image = new SImage(input);
		width = image.width;
		height = image.height;
		RGB = image.getRGBMAtrix();
		Gradient_Direction = new double[width][height];
		Gradient_Magnitude = new double[width][height];
	}
	
	private static void cal_gradient() {
		for (int i = 0; i < width; i++) {
			if (i == 0 || i == width - 1) continue;
			for (int j = 0; j < height; j++) {
				if (j == 0 || j == height - 1) continue;
				double Gx = RGB[i+1][j][0] - RGB[i-1][j][0];
				double Gy = RGB[i][j+1][0] - RGB[i][j-1][0];
				double Gradient = Math.sqrt(Gx * Gx + Gy * Gy);
				double theta = Math.atan(Gy / Gx) * 57.29578;
				if (Double.isNaN(theta) && Gy > 0) {
					theta = 90;
				} else if (Double.isNaN(theta) && Gy < 0) {
					theta = -90;
				}
				Gradient_Direction[i][j] = theta;
				Gradient_Magnitude[i][j] = Gradient;
			}
		}
	}

}
