package algorithm;

import java.awt.image.BufferedImage;

import tool.Num;
import tool.SImage;

public class LC {
	
	public static BufferedImage deal(BufferedImage input) {
		// 灰度处理
		input = Grey.deal(input);
		SImage image = new SImage(input);
		int width = image.width;
		int height = image.height;
		
		int total_pixel = width * height;
		int color_num[] = new int[256];
		double color_frequency[] = new double[256];
		double value[] = new double[256];
		
		// 统计每个灰度值有多少个像素
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				color_num[image.getR(x, y)] ++;
			}
		}
		// 计算每个灰度值的频率
		for (int i = 0; i < 256; i++) {
			color_frequency[i] = color_num[i] * 1.0d / total_pixel;
		}
		// 计算每个值和其他值之间的距离
		for (int i = 0; i < 256; i++) {
			double temp = 0d;
			for (int j = 0; j < 256; j++) {
				temp += Math.abs(i - j) * color_frequency[j];
			}
			value[i] = temp;
		}
		value = Num.sub(value, Num.min(value));
		value = Num.div(value, Num.max(value));
		value = Num.mul(value, 256);
		// 将该值替代原先的像素值
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int v = (int)value[image.getR(x, y)];
				image.setRGB(x, y, v, v, v);
			}
		}
		return image.getImage();
	}

}
