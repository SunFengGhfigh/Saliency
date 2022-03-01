package algorithm;

import java.awt.image.BufferedImage;

import tool.SImage;

/**
 * 朴素的LBP（局部二值模式 Local Binary Pattern）算法
 * 将像素的像素值与周围8个点进行比较，比中心点大的，记为1，小的，记为0，有词得到8个二进制
 * 由这8个二进制，得到一个0-255之间的值
 * @author Sun
 *
 */
public class Naive_LBP {
	
	private static SImage image;
	private static int width;
	private static int height;
	private static int[][] LBP;
	private static double[][][] RGB;
	
	public static BufferedImage deal(BufferedImage input) {
		initial(input);
		initial_LBP();
		clouring();
		return image.getImage();
	}
	
	private static void clouring() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int c = LBP[i][j];
				image.setRGB(i, j, c, c, c);
			}
		}
	}

	private static void initial_LBP() {
		// 最外围不处理
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int[] num = new int[8];
				if (i == 0 || i == width-1 || j == 0 || j == height-1) {
					LBP[i][j] = (int)(RGB[i][j][0]*255);
					continue;
				}
				double c = RGB[i][j][0];
				int start_x = i-1, end_x = i+1, start_y = j-1, end_y = j+1;
				int num_index = 0;
				for (int x = start_x; x <= end_x; x++) {
					for (int y = start_y; y <= end_y; y++) {
						if (x == i && y == j) continue;
						num[num_index++] = RGB[x][y][0] > c ? 1 : 0;
					}
				}
				int v = 0;
				for (int p = 0; p < 8; p++) {
					if (num[p] == 1) {
						v += (2 << p);
					}
				}
				LBP[i][j] = v;
			}
		}
	}
	
	// 初始化变量
	private static void initial(BufferedImage input) {
		// 图像是经过灰度化的
		input = Grey.deal(input);
		image = new SImage(input);
		width = image.width;
		height = image.height;
		LBP = new int[width][height];
		RGB = image.getRGBMAtrixNormalize();
	}

}
