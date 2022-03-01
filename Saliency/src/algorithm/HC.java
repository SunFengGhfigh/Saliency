package algorithm;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;

import tool.SImage;

public class HC {
	
	public static BufferedImage deal(BufferedImage input) {
		SImage image = new SImage(input);
		int width = image.width;
		int height = image.height;
		int total_pixel = width * height;
		// 越小，精准度越高
		final int threshold = 20;
		
		HashMap<Integer, Integer> color_dic = new HashMap<>();
		HashMap<Integer, Integer> color2color = new HashMap<>();
		HashMap<Integer, Integer> color_num = new HashMap<>();
		HashMap<Integer, Double> color_frequency = new HashMap<>();
		HashMap<Integer, Double> value = new HashMap<>();
		
		// 统计不同颜色的个数
		// 格式如： 255241006 78
		//		 128054116 95
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Integer key = image.getRGB(x, y);
				if (color_dic.get(key) == null) {
					color_dic.put(key, 1);
				} else {
					color_dic.put(key, color_dic.get(key) + 1);
				}
			}
		}
		
		int sort[] = new int[color_dic.size()];
		int sort_index = -1;
		for (Integer key : color_dic.keySet()){
			sort[++sort_index] = key; 
		}
		// 针对颜色排序
		Arrays.sort(sort);
		
		for (int i = 0; i < sort.length - 1; i++) {
			if (sort[i] == -1) {
				continue;
			}
			int num = color_dic.get(sort[i]);
			for (int j = i+1; j < sort.length; j++) {
				if (sort[j] == -1) {
					continue;
				}
				double dis = lenOfRGB(sort[i], sort[j]);
				if (dis < threshold) {
					color2color.put(sort[j], sort[i]);
					num += color_dic.get(sort[j]);
					sort[j] = -1;
				}
			}
			color_num.put(sort[i], num);
			color2color.put(sort[i], sort[i]);
		}
		
		// 压缩像素
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Integer key = image.getRGB(x, y);
				Integer _2key = color2color.get(key);
				int r = _2key / 1000000;
				int g = _2key / 1000 % 1000;
				int b = _2key % 1000;
				image.setRGB(x, y, r, g, b);
			}
		}
		
		// 计算频率
		for (Integer key : color_num.keySet()){
			double fre = color_num.get(key) * 1.0d / total_pixel;
			color_frequency.put(key, fre);
		}
		
		for (Integer key : color_num.keySet()) {
			double v = 0.0d;
			for (Integer key2 : color_num.keySet()) {
				if (key.equals(key2)) {
					continue;
				}
				v += lenOfRGB(key, key2) * 1.0d * color_frequency.get(key2);
			}
			value.put(key, v);
		}
		
		// 获得最小最大值
		double min = 1e10d, max = -1.0d;
		for (Integer key : value.keySet()) {
			double v = value.get(key);
			if (v < min) {
				min = value.get(key);
			}
			if (v > max) {
				max = v;
			}
		}
		
		// 归一化
		for (Integer key : value.keySet()) {
			double v = value.get(key);
			v -= min;
			v /= (max - min);
			v *= 256;
			value.put(key, v);
		}
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Integer key = image.getRGB(x, y);
				int v = value.get(key).intValue();
				image.setRGB(x, y, v, v, v);
			}
		}
		
		return image.getImage();
	}
	
	private static double lenOfRGB(int a, int b) {
		double x_r = a / 1000000 * 1.0d;
		double x_g = a / 1000 % 1000 * 1.0d;
		double x_b = a % 1000 * 1.0d;
		double y_r = b / 1000000 * 1.0d;
		double y_g = b / 1000 % 1000 * 1.0d;
		double y_b = b % 1000 * 1.0d;
		return Math.sqrt((x_r - y_r) * (x_r - y_r) + (x_g - y_g) * (x_g - y_g) + (x_b - y_b) * (x_b - y_b));
	}

}
