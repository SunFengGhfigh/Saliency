package algorithm;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import tool.SImage;

public class Uniform_LBP {

	private static SImage image;
	private static int width;
	private static int height;
	// mapping������ӳ�䲻ͬ�����ֵ�59ά��ȥ
	private static Map<Integer, Integer> mapping;
	private static double[][][] RGB;
	// �Ҷ�ͼ
	private static double[][] grey;
	
//	private static 
	
	public static BufferedImage deal(BufferedImage input) {
		initial(input);
		return image.getImage();
	}
	
	/**
	 * ����һ�������ش��е�LBP
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
	 * ��������Ϊx��y�����ص�LBPֵ
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
	 * ��ʼ���Ҷ�ͼ
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
	 * ��ʼ�����б���
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
	 * ��ʼ��ӳ���飬��Ҫ��0~256ӳ�䵽59ά�ȣ�Ҳ����[0,58]
	 * ʵ����mapping��sizeֻ��58����[0,57]�������ǲ���mapping����Ķ�ӳ��Ϊ58
	 * ��Ϊ����mapping����Ķ���01�任2�����ϵ�
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
	 * ����һ��8λ�����У�0��1�任�Ĵ���
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
