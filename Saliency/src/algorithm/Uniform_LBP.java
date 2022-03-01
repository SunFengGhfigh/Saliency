package algorithm;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import tool.SImage;

public class Uniform_LBP {

	private static SImage image;
	private static int width;
	private static int height;
	// mapping是用于映射不同的数字到59维中去
	private static Map<Integer, Integer> mapping;
	private static double[][][] RGB;
	// 灰度图
	private static double[][] grey;
	
//	private static 
	
	public static BufferedImage deal(BufferedImage input) {
		initial(input);
		return image.getImage();
	}
	
	/**
	 * 计算一个超像素簇中的LBP
	 * @param cluster
	 * @return
	 */
	private static int[] cal_uniform_LBP_for_Superpixel(Cluster cluster) {
		int[] re = new int[59];
		for (Position p : cluster.pixels) {
			int key = singal_LBP_value(p.w, p.h);
			if (mapping.get(key) != null) {
				re[mapping.get(key)]++;
			} else {
				re[58]++;
			}
		}
		return re;
	}
	
	/**
	 * 计算坐标为x和y处像素的LBP值
	 * @param x
	 * @param y
	 * @return
	 */
	private static int singal_LBP_value(int x, int y) {
		if (x == 0 || x == width-1 || y == 0 || y == height-1) {
			return (int)grey[x][y];
		}
		int start_x = x - 1;
		int end_x = x + 1;
		int start_y = y - 1;
		int end_y = y + 1;
		int num_index = 0;
		int[] num = new int[8];
		double c = grey[x][y];
		for (int i = start_x; i <= end_x; i++) {
			for (int j = start_y; j <= end_y; j++) {
				if (i == x && j == y) continue;
				num[num_index++] = grey[i][j] > c ? 1 : 0;
			}
		}
		int v = 0;
		for (int p = 0; p < 8; p++) {
			if (num[p] == 1) {
				v += (2 << p);
			}
		}
		return v;
	}
	
	/**
	 * 初始化灰度图
	 */
	private static void initial_grey() {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				double R = RGB[i][j][0];
				double G = RGB[i][j][1];
				double B = RGB[i][j][2];
				double c = R * 0.3d + G * 0.6d + B * 0.1d;
				grey[i][j] = c;
			}
		}
	}
	
	/**
	 * 初始化所有变量
	 * @param input
	 */
	private static void initial(BufferedImage input) {
		input = SLIC.deal(input);
		image = new SImage(input);
		width = image.width;
		height = image.height;
		mapping = new HashMap<Integer, Integer>();
		RGB = image.getRGBMAtrix();
		initial_grey();
		initial_mapping();
	}
	
	/**
	 * 初始化映射组，主要讲0~256映射到59维度，也就是[0,58]
	 * 实际上mapping的size只有58（即[0,57]），凡是不在mapping里面的都映射为58
	 * 因为不在mapping里面的都是01变换2次以上的
	 */
	private static void initial_mapping() {
		int in = 0;
		for (int i = 0; i < (2 << 7); i++) {
			int index = 8;
			int t = i;
			int[] num = new int[8];
			while (index != 0) {
				index--;
				num[index] = t % 2;
				t >>= 1;
			}
			int re = cal_arrays_LBP_num(num);
			if (re == 0 || re == 1 || re == 2) {
				mapping.put(i, in++);
			}
		}
	}
	
	/**
	 * 计算一个8位数组中，0和1变换的次数
	 * @param num
	 * @return
	 */
	private static int cal_arrays_LBP_num(int[] num) {
		int sum = 0;
		for (int i = 0; i < 7; i++) {
			if (num[i] != num[i+1]) {
				sum++;
			}
		}
		return sum;
	}
	
}
