package algorithm;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import tool.Num;
import tool.SImage;

/**
 * 主要完成BFS算法
 * @author Sun
 *
 */
public class BFS {
	
	private static SImage image;
	private static int width;
	private static int height;
	private static double[][][] RGB;
	private static double[][][] Lab;
	private static List<Cluster> clusters;
	private static Map<Position, Cluster> label;
	private static int[][] sulabel;
	private static List<Cluster> border_clusters;
	
	public static BufferedImage deal(BufferedImage input) {
		initial_vari(input);
		double[][] saliency = compute_saliency();
		saliency = Num.normalize(saliency);
		HashSet<Double> set = new HashSet<>();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int c = (int)(saliency[i][j] * 255);
				image.setRGB(i, j, c, c, c);
			}
		}
		for (Double d : set) {
			System.out.println(d);
		}
		return input;
	}
	
	private static double[][] compute_saliency() {
		int size = clusters.size();
		// 区域颜色量
		double[] color_volume = new double[size];
		// 记录每个簇的Lab
		double[][] colorareas = new double[size][3];
		// 记录每个簇的坐标
		double[] csumX = new double[size];
		double[] csumY = new double[size];
		
		double[] count = new double[size];
		
		double[][] saliency = new double[width][height];
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				double l = Lab[i][j][0];
				double a = Lab[i][j][1];
				double b = Lab[i][j][2];
				int sp_id = sulabel[i][j];
				
				colorareas[sp_id][0] += l;
				colorareas[sp_id][1] += a;
				colorareas[sp_id][2] += b;
				
				count[sp_id] += 1.0d;
				
				color_volume[sp_id] += (4.0d / 3.0d) * Math.PI * l * a * b;
				csumX[sp_id] += (double)i / (width * 1.0d);
				csumY[sp_id] += (double)j / (height * 1.0d);
			}
		}
		
		for (int i = 0; i < size; i++) {
			if (count[i] <= 0) {
				count[i] = 1.0d;
			}
			for (int k = 0; k < 3; k++) {
				colorareas[i][k] /= count[i];
			}
			csumX[i] /= count[i];
			csumY[i] /= count[i];
			
			color_volume[i] /= count[i];
		}
		
		double[][] Boundary = new double[size][size];
		double[] Centermap = new double[size];
		
		for (int i = 0; i < size; i++) {
			double dist = 0.0d;
			double Tcolor = 0.0d;
			double centerWeight = 0.0d;
			double spatialWeight = 0.0d;
			int Clabel = sulabel[width / 2][height / 2];
			
			for (int j = 0; j < size; j++) {
				centerWeight = 1.0d - sqrt(pow(csumX[i]-0.5d, 2) + pow(csumY[i]-0.5d, 2)) / sqrt(0.5);
				spatialWeight = 1.0d - sqrt(pow(csumX[i]-csumX[j], 2) + pow(csumY[i]-csumY[j], 2)) / sqrt(0.5);
				Tcolor = pow(colorareas[i][0] - colorareas[j][0], 2) 
						+ pow(colorareas[i][1] - colorareas[j][1], 2)
						+ pow(colorareas[i][2] - colorareas[j][2], 2);
				Cluster c1 = clusters.get(j);
				if (border_clusters.contains(c1)) {
					Boundary[i][j] = spatialWeight * centerWeight * Tcolor;
				}
			}
			
			Tcolor = pow(colorareas[i][0] - colorareas[Clabel][0], 2)
					+ pow(colorareas[i][1] - colorareas[Clabel][1], 2)
					+ pow(colorareas[i][2] - colorareas[Clabel][2], 2);
			Centermap[i] = pow(1.0d - centerWeight, 6) * Tcolor;
		}
		
		double[] sumvalues = new double[size];
		double[] maxvalues = new double[size];
		
		for (int m = 0; m < size; m++) {
			sumvalues[m] = Num.sum(Boundary[m]);
			maxvalues[m] = Num.max(Boundary[m]);
		}
		
		double[] Backmap = new double[size];
		
		for (int m = 0; m < size; m++) {
			Backmap[m] = (sumvalues[m] - 2.0d * maxvalues[m]);
		}
		
		double meanvalues = Num.sum(Backmap) / size;
		
		for (int m = 0; m < size; m++) {
			Backmap[m] = Math.max(Backmap[m], meanvalues);
		}
		
		double[] CENTER = Num.normalize(Centermap);
		double[] Foreground = Num.normalize(color_volume);
		double[] Background = Num.normalize(Backmap);
		
		double[][] cms = new double[width][height];
//		double[][] sms = new double[width][height];
		
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int sp_id = sulabel[i][j];
				double minv = Math.min(Foreground[sp_id], Math.min(Background[sp_id], CENTER[sp_id]));
//				double maxv = Math.max(Foreground[sp_id], Math.max(Background[sp_id], CENTER[sp_id]));
				double BCD = Math.abs(CENTER[sp_id] - Background[sp_id]);
				if (CENTER[sp_id] >= Background[sp_id]) {
					if (minv == 0) {
						cms[i][j] = 0;
					} else {
						cms[i][j] = BCD - (Foreground[sp_id] * Background[sp_id]);
					}
				}
				if (CENTER[sp_id] < Background[sp_id]) {
					cms[i][j] = 2.0d * BCD - (Foreground[sp_id] * Background[sp_id]);
				}
				
				saliency[i][j] = Math.tanh(6.0d * pow(cms[i][j], 6) + 1.0d);
			}
		}
		
		return saliency;
		
	}
	
	private static double sqrt(double a) {
		return Math.sqrt(a);
	}
	
	private static double pow(double a, double b) {
		return Math.pow(a, b);
	}
	
	/**
	 * 初始化变量
	 * @param input
	 */
	private static void initial_vari(BufferedImage input) {
		image = new SImage(input);
		width = image.width;
		height = image.height;
		RGB = image.RGB;
		Lab = image.Lab;
		SLIC.K = 20;
		SLIC.M = 20;
		BufferedImage sp_image = SLIC.deal(input);
		clusters = SLIC.getClusters();
		label = SLIC.getLabel();
		sulabel = SLIC.getSulabel();
		initial_border_clusters();
	}
	
	/**
	 * 寻找边界超像素
	 */
	private static void initial_border_clusters() {
		HashSet<Cluster> set = new HashSet<>();
		border_clusters = new ArrayList<Cluster>();
		for (int i = 0; i < width; i++) {
			Cluster c1 = label.get(new Position(i, 0));
			set.add(c1);
			c1 = label.get(new Position(i, height-1));
			set.add(c1);
		}
		for (int i = 0; i < height; i++) {
			Cluster c1 = label.get(new Position(0, i));
			set.add(c1);
			c1 = label.get(new Position(width-1, i));
			set.add(c1);
		}
		for (Cluster c : set) {
			border_clusters.add(c);
		}
	}
	
}
