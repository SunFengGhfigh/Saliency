package algorithm;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import main.Common;
import tool.Num;
import tool.SImage;

public class BL_weak_saliency{
	
	// 原图像
	private static SImage image;
	// 所有超像素簇
	private static List<Cluster> clusters;
	// 不同的点对应的超像素簇
	private static Map<Position, Cluster> label;
	// 原图像的RGB，已经归一化
	private static double[][][] RGB;
	// 原图像的Lab，已经归一化
	private static double[][][] Lab;
	// 边界超像素数组
	private static List<Cluster> border_clusters;
	// 三类超像素数组
	private static ArrayList<Cluster> class_clusters[];
	private static int width;
	private static int height;
	private static Random random = new Random();
	// GCD与GSD
	private static List<Cluster> GCD[];
	private static List<Cluster> GSD[];
	// 基于背景得到的显著图
	private static List<Cluster> Sbg;
	// GCD与GSD计算的参数
	private static final double sigma1 = 0.2d;
	private static final double beta = 10.0d;
	private static final double sigma2 = 1.3d;
	// 影响因子矩阵
	private static double[][] F;
	// 用于计算影响因子矩阵
	private static int[][] edges;
	private static final double theta = 10.0d;
	private static double[] weights;
	private static double[][] seg_vals;
	private static double[][] D;
	// 归一化的影响因子矩阵
	private static double[][] F_normal;
	// 用于计算置信度矩阵所用参数
	private static final double a = 0.6d;
	private static final double b = 0.2d;
	// 记录每个像素所归属的超像素类
	private static int[][] sulabel;
	// 置信度矩阵
	private static double[][] C_normal; 
	
	public static BufferedImage deal(BufferedImage input) {
		input = SLIC.deal(input);
		// 初始化各种变量
		initial(input);
		// 获取边缘的超像素
		get_border_cluster();
		// 这个K聚类还有一定的问题，随机性没有完全的解决
		k_means(10);
		// 计算GCD
		initial_GCD();
		// 计算GSD
		initial_GSD();
		// 利用GCD和GSD计算Sbg
		initail_Sbg();
		
		for (Cluster c : Sbg) {
			for (Position p : c.pixels) {
				image.setRGB(p.w, p.h, (int)(c.R*255), (int)(c.G*255), (int)(c.B*255));
			}
		}

		
//		// 获取元胞组 即 影响因子矩阵
//		int[][] neighbour = initial_neighbour();
//		
//		double[][] F = initial_F(neighbour);
//		
//		double[][] D = initial_D(F);
//		
//		double[][] D_1 = Num.inv(D);
//		double[][] F_1 = Num.mul(D_1, F);
//		
//		double[][] C = initial_C(F);
//		C = normalize_C(C); 
//		
//		double[][] S = restrain(C, F_1); 
////		S = prominent(S, C, F_1);
//////		S = update(S, C, F_1);
//		for (Cluster c : Sbg) {
//			for (Position p : c.pixels) {
//				int v = (int)(S[c.id][0] * 255);
//				image.setRGB(p.w, p.h, v, v, v);
//				
////				int v = (int)(c.R * 255);
////				image.setRGB(p.w, p.h, v, v, v);
//			}
//		}
		
		return image.getImage();
	}
	
	private static double[][] prominent(double[][] S, double[][] C, double[][] F_1){
		int bd_size = border_clusters.size();
		int size = clusters.size();
		for (int i = 0; i < bd_size; i++) {
			int bd_id = border_clusters.get(i).id;
			S[bd_id][0] -= 0.6d;
			if (S[bd_id][0] <= 0) {
				S[bd_id][0] = 0.001d;
			}
		}
		List<Integer> most = new ArrayList<>();
		for (int i = 0; i < S.length; i++) {
			if (S[i][0] > 0.93) most.add(i);
		}
		System.out.println(most.size() + " " + 0.02d * size);
		if (most.size() < 0.02d * size) {
			System.out.println("yes");
			double min = Double.MAX_VALUE;
			double max = Double.MIN_VALUE;
			for (int i = 0; i < size; i++) {
				if (most.contains(i)) continue;
				if (S[i][0] > max) max = S[i][0];
				if (S[i][0] < min) min = S[i][0];
			}
			for (int i = 0; i < size; i++) {
				if (most.contains(i)) continue;
				S[i][0] -= min;
				S[i][0] /= (max - min);
			}
		}
		double[][] I = Num.I(size);
		double[][] temp1 = Num.mul(C, S);
		double[][] temp2 = Num.sub(I, C);
		double[][] temp3 = Num.mul(temp2, F_1);
		double[][] temp4 = Num.mul(temp3, S);
		S = Num.add(temp1, temp4);
		S = normalize_S(S);
		return S;
	}
	
	private static double[][] restrain(double[][] C, double[][] F_1) {
		int bd_size = border_clusters.size();
		int size = clusters.size();
		double[][] S_0 = new double[size][1];
		for (Cluster c : Sbg) {
			S_0[c.id][0] = c.R;
		}
		for (int i = 0; i < bd_size; i++) {
			int bd_id = border_clusters.get(i).id;
			S_0[bd_id][0] -= 0.6d;
			if (S_0[bd_id][0] <= 0) {
				S_0[bd_id][0] = 0.001d;
			}
		}
		double[][] I = Num.I(size);
		double[][] temp1 = Num.mul(C, S_0);
		double[][] temp2 = Num.sub(I, C);
		double[][] temp3 = Num.mul(temp2, F_1);
		double[][] temp4 = Num.mul(temp3, S_0);
		double[][] S = Num.add(temp1, temp4);
		S = normalize_S(S);
		
		for (int iter = 0; iter < 4; iter++) {
			for (int i = 0; i < bd_size; i++) {
				int bd_id = border_clusters.get(i).id;
				S[bd_id][0] -= 0.6d;
				if (S[bd_id][0] <= 0) {
					S[bd_id][0] = 0.001d;
				}
			}
			I = Num.I(size);
			temp1 = Num.mul(C, S);
			temp2 = Num.sub(I, C);
			temp3 = Num.mul(temp2, F_1);
			temp4 = Num.mul(temp3, S);
			S = Num.add(temp1, temp4);
			S = normalize_S(S);
		}
		
		return S;
	}
	
	private static double[][] update(double[][] S, double[][] C, double[][] F_1) {
		int size = clusters.size();
		for (int i = 0; i < 10; i++) {
			double[][] I = Num.I(size);
			double[][] temp1 = Num.mul(C, S);
			double[][] temp2 = Num.sub(I, C);
			double[][] temp3 = Num.mul(temp2, F_1);
			double[][] temp4 = Num.mul(temp3, S);
			S = Num.add(temp1, temp4);
			S = normalize_S(S);
		}
		return S;
	}
	
	// 赋值元胞邻居数组
	private static int[][] initial_neighbour() {
		int bd_size = border_clusters.size();
		int size = clusters.size();
		int[][] neighbour = new int[size][size];

		for (int i = 1; i < width-1; i++) {
			for (int j = 1; j < height-1; j++) {
				int t = sulabel[i][j];
				int t1 = sulabel[i-1][j];
				int t2 = sulabel[i-1][j-1];
				int t3 = sulabel[i][j-1];
				int t4 = sulabel[i+1][j-1];
				int t5 = sulabel[i+1][j];
				int t6 = sulabel[i+1][j+1];
				int t7 = sulabel[i][j+1];
				int t8 = sulabel[i-1][j+1];
				int[] arr = {t1, t2, t3, t4, t5, t6, t7, t8};
				for (int p = 0; p < 8; p++) {
					if (t != arr[p]) {
						neighbour[t][arr[p]] = 1;
						neighbour[arr[p]][t] = 1;
					}
				}
			}
		}
		
		for (int i = 0; i < bd_size - 1; i++) {
			Cluster ci = border_clusters.get(i);
			for (int j = i + 1; j < bd_size; j++) {
				Cluster cj = border_clusters.get(j);
				neighbour[ci.id][cj.id] = 1;
				neighbour[cj.id][ci.id] = 1;
			}
		}
		
		
		int[][] nei = new int[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (neighbour[i][j] == 1) {
					for (int p = 0; p < size; p++) {
						if (neighbour[j][p] == 1 && p != i) {
							nei[i][p] = 1;
							nei[p][i] = 1;
						}
					}
				}
			}
		}
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (nei[i][j] == 1 && i != j) {
					neighbour[i][j] = 1;
				}
			}
		}
		
		return neighbour;
	}
	
	private static double[][] normalize_S(double[][] S){
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		for (int i = 0; i < S.length; i++) {
			double v = S[i][0];
			if (v > max) max = v;
			if (v < min) min = v;
		}
		for (int i = 0; i < S.length; i++) {
			S[i][0] -= min;
			S[i][0] /= (max - min);
		}
		return S;
	}
	
	private static double[][] normalize_C(double[][] C){
		int size = clusters.size();
		double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
		for (int i = 0; i < size; i++) {
			if (C[i][i] > max) max = C[i][i];
			if (C[i][i] < min) min = C[i][i];
		}
		for (int i = 0; i < size; i++) {
			C[i][i] -= min;
			C[i][i] /= (max - min);
			C[i][i] *= 0.6;
			C[i][i] += 0.2;
		}
		return C;
	}
	
	private static double[][] initial_C(double[][] F){
		int size = clusters.size();
		double[][] C = new double[size][size];
		for (int i = 0; i < size; i++) {
			double max = Double.MIN_VALUE;
			for (int j = 0; j < size; j++) {
				if (F[i][j] > max) max = F[i][j];
			}
			double ci = 1/max;
			C[i][i] = ci;
		}
		return C;
	}
	
	private static double[][] initial_D(double[][] F){
		int size = clusters.size();
		double[][] D = new double[size][size];
		for (int i = 0; i < size; i++) {
			double d = 0;
			for (int j = 0; j < size; j++) {
				d += F[i][j];
			}
			D[i][i] = d;
		}
		return D;
	}
	
	private static double[][] initial_F(int[][] neighbour){
		int size = clusters.size();
		double[][] F = new double[size][size];
		HashMap<Position, Double> map = new HashMap<>();
		for (int i = 0; i < size; i++) {
			Cluster c1 = clusters.get(i);
			for (int j = 0; j < size; j++) {
				if (i == j || neighbour[i][j] == 0) {
					F[i][j] = 0;
					continue;
				} 
				Cluster c2 = clusters.get(j);
				map.put(new Position(i, j), distance_base_Lab_by_cluster(c1, c2));
			}
		}
		double min_dis = Double.MAX_VALUE;
		double max_dis = Double.MIN_VALUE;
		for (Position p : map.keySet()) {
			double v = map.get(p);
			if (v < min_dis) min_dis = v;
			if (v > max_dis) max_dis = v;
		}
		for (Position p : map.keySet()) {
			double v = map.get(p);
			v -= min_dis;
			v /= (max_dis - min_dis);
			v *= (-10.0d);
			F[p.w][p.h] = Math.pow(Math.E, v);
		}
		return F;
	}
	
	// 初始化变量
	private static void initial(BufferedImage input) {
		image = new SImage(input);
		clusters = SLIC.getClusters();
		label = SLIC.getLabel();
		width = image.width;
		height = image.height;
		RGB = SLIC.getRGB();
		
		switch (Common.current_color_space) {
			case "RGB": for (Cluster c : clusters) {c.l = image.RGB[c.w][c.h][0]; c.a = image.RGB[c.w][c.h][1]; c.b = image.RGB[c.w][c.h][2];}; break;
			case "Lab": for (Cluster c : clusters) {c.l = image.Lab[c.w][c.h][0]; c.a = image.Lab[c.w][c.h][1]; c.b = image.Lab[c.w][c.h][2];}; break;
			case "XYZ": for (Cluster c : clusters) {c.l = image.XYZ[c.w][c.h][0]; c.a = image.XYZ[c.w][c.h][1]; c.b = image.XYZ[c.w][c.h][2];}; break;
			case "HSV": for (Cluster c : clusters) {c.l = image.HSV[c.w][c.h][0]; c.a = image.HSV[c.w][c.h][1]; c.b = image.HSV[c.w][c.h][2];}; break;
			case "YUV": for (Cluster c : clusters) {c.l = image.YUV[c.w][c.h][0]; c.a = image.YUV[c.w][c.h][1]; c.b = image.YUV[c.w][c.h][2];}; break;
			case "YIQ": for (Cluster c : clusters) {c.l = image.YIQ[c.w][c.h][0]; c.a = image.YIQ[c.w][c.h][1]; c.b = image.YIQ[c.w][c.h][2];}; break;
			case "YCbCr": for (Cluster c : clusters) {c.l = image.YCbCr[c.w][c.h][0]; c.a = image.YCbCr[c.w][c.h][1]; c.b = image.YCbCr[c.w][c.h][2];}; break;
			case "LCH": for (Cluster c : clusters) {c.l = image.LCH[c.w][c.h][0]; c.a = image.LCH[c.w][c.h][1]; c.b = image.LCH[c.w][c.h][2];}; break;
		}
		
		sulabel = SLIC.getSulabel();
		border_clusters = new ArrayList<Cluster>();
		class_clusters = new ArrayList[4];
		class_clusters[1] = new ArrayList<>();
		class_clusters[2] = new ArrayList<>();
		class_clusters[3] = new ArrayList<>();
		GCD = new ArrayList[4];
		GCD[1] = new ArrayList<>();
		GCD[2] = new ArrayList<>();
		GCD[3] = new ArrayList<>();
		GSD = new ArrayList[4];
		GSD[1] = new ArrayList<>();
		GSD[2] = new ArrayList<>();
		GSD[3] = new ArrayList<>();
		Sbg = new ArrayList<>();
		F = new double[clusters.size()][clusters.size()];
		// 默认是空的
		edges = new int[0][2];
		weights = new double[0];
		seg_vals = new double[clusters.size()][3];
		initial_seg_vals();
		D = new double[clusters.size()][clusters.size()];
		F_normal = new double[clusters.size()][clusters.size()];
	}
	
	// 初始化seg_vals，用于复现源Matlab代码所用，命名也是源码命名
	private static void initial_seg_vals() {
		for (int i = 0; i < clusters.size(); i++) {
			Cluster cluster = clusters.get(i);
			seg_vals[cluster.id][0] = cluster.l;
			seg_vals[cluster.id][1] = cluster.a;
			seg_vals[cluster.id][2] = cluster.b;
		}
	}
	
	// 基于背景的显著性图
	private static void initail_Sbg() {
		double min_re = Double.MAX_VALUE, max_re = Double.MIN_VALUE;
		for (int i = 0; i < clusters.size(); i++) {
			double re = 0;
			for (int j = 1; j <= 3; j++) {
				double r1 = GCD[j].get(i).R;
				double r2 = GSD[j].get(i).R;
				re += r1 * r2;
			}
			Cluster c = clusters.get(i);
			Cluster cluster = clone(c);
			cluster.R = re;
			if (re < min_re) min_re = re;
			if (re > max_re) max_re = re;
			Sbg.add(cluster);
		}
		
		for (Cluster cluster : Sbg) {
			cluster.R -= min_re;
			cluster.R /= (max_re - min_re);
			cluster.G = cluster.R;
			cluster.B = cluster.R;
		}
		
	}
	
	// GSD
	private static void initial_GSD() {
		for (int k = 1; k <= 3; k++) {
			for (int i = 0; i < clusters.size(); i++) {
				double re = 0d;
				Cluster c = clusters.get(i);
				Cluster ci = clone(c);
				for (int j = 0; j < class_clusters[k].size(); j++) {
					Cluster cj = class_clusters[k].get(j);
					double frac = distance_base_position_by_cluster_no(ci, cj);
					frac *= frac;
					frac *= -1.0d;
					frac /= (2 * sigma2 * sigma2);
					frac = Math.pow(Math.E, frac);
					re += frac;
				}
				re /= class_clusters[k].size();
				ci.R = re; ci.G = re; ci.B = re;
				GSD[k].add(ci);
			}
		}
		
		for (int k = 1; k <= 3; k++) {
			double min_re = Double.MAX_VALUE;
			double max_re = Double.MIN_VALUE; 
			for (Cluster c : GSD[k]) {
				if (c.R < min_re) min_re = c.R;
				if (c.R > max_re) max_re = c.R;
			}
			for (Cluster c : GSD[k]) {
				c.R -= min_re;
			}
			for (Cluster c : GSD[k]) {
				c.R /= (max_re - min_re);
				c.B = c.R;
				c.G = c.R;
			}
		}
	}
	
	// 计算GCD
	private static void initial_GCD() {
		for (int k = 1; k <= 3; k++) {
			for (int i = 0; i < clusters.size(); i++) {
				double re = 0d;
				Cluster c = clusters.get(i);
				Cluster ci = clone(c);
				for (int j = 0; j < class_clusters[k].size(); j++) {
					Cluster cj = class_clusters[k].get(j);
					double frac = -1.0d * distance_base_Lab_by_cluster_no(ci, cj) / (2 * sigma1 * sigma1);
					frac = Math.pow(Math.E, frac);
					frac += beta;
					re += (1.0d / frac);
				}
				re /= class_clusters[k].size();
				ci.R = re; ci.G = re; ci.B = re;
				GCD[k].add(ci);
			}
		}
		
		for (int k = 1; k <= 3; k++) {
			double min_re = Double.MAX_VALUE;
			double max_re = Double.MIN_VALUE;
			for (Cluster c : GCD[k]) {
				if (c.R < min_re) min_re = c.R;
				if (c.R > max_re) max_re = c.R;
			}
			for (Cluster c : GCD[k]) {
				c.R -= min_re;
			}
			for (Cluster c : GCD[k]) {
				c.R /= (max_re - min_re);
				c.B = c.R;
				c.G = c.R;
			}
		}
		
	}
	
	// 获得边界的超像素
	private static void get_border_cluster() {
		for (int i = 0; i < width; i++) {
			Cluster cluster = label.get(new Position(i, 0));
			if (!border_clusters.contains(cluster)) {
				border_clusters.add(cluster);
			}
			cluster = label.get(new Position(i, height-1));
			if (!border_clusters.contains(cluster)) {
				border_clusters.add(cluster);
			}
		}
		
		for (int j = 0; j < height; j++) {
			Cluster cluster = label.get(new Position(0, j));
			if (!border_clusters.contains(cluster)) {
				border_clusters.add(cluster);
			}
			cluster = label.get(new Position(width-1, j));
			if (!border_clusters.contains(cluster)) {
				border_clusters.add(cluster);
			}
		}
	}

	private static void k_means(int iter) {
		int bd_size = border_clusters.size();
		int fa = 0, fb = 0, fc = 0;
		double max_dis_f = Double.MIN_VALUE;
		for (int a = 0; a < bd_size; a++) {
			double max_dis = Double.MIN_VALUE;
			Cluster ac = border_clusters.get(a);
			int b = 0;
			for (int i = 0; i < bd_size; i++) {
				Cluster bc = border_clusters.get(i);
				double dis = distance_base_Lab_by_cluster(ac, bc);
				if (dis > max_dis) {
					max_dis = dis;
					b = i;
				}
			}
			int c = 0;
			Cluster bc = border_clusters.get(b);
			max_dis = Double.MIN_VALUE;
			for (int i = 0; i < bd_size; i++) {
				Cluster cc = border_clusters.get(i);
				double dis1 = distance_base_Lab_by_cluster(ac, cc);
				double dis2 = distance_base_Lab_by_cluster(bc, cc);
				double dis = dis1 *dis2;
				if (dis > max_dis) {
					max_dis = dis;
					c = i;
				}
			}
			Cluster cc = border_clusters.get(c);
			
			double dis1 = distance_base_Lab_by_cluster(ac, cc);
			double dis2 = distance_base_Lab_by_cluster(bc, cc);
			double dis3 = distance_base_Lab_by_cluster(ac, bc);
			double dis = dis1 * dis2 * dis3;
			if (dis > max_dis_f) {
				max_dis_f = dis;
				fa = a; fb = b; fc = c;
			}
		}
		int a = fa, b = fb, c = fc;

		HashSet<Cluster> as = new HashSet<>();
		HashSet<Cluster> bs = new HashSet<>();
		HashSet<Cluster> cs = new HashSet<>();
		
		Cluster ac = border_clusters.get(a);
		Cluster bc = border_clusters.get(b);
		Cluster cc = border_clusters.get(c);
		
		for (int i = 0; i < bd_size; i++) {
			Cluster current_c = border_clusters.get(i);
			double dis1 = distance_base_Lab_by_cluster(ac, current_c);
			double dis2 = distance_base_Lab_by_cluster(bc, current_c);
			double dis3 = distance_base_Lab_by_cluster(cc, current_c);
			if (dis1 <= dis2 && dis1 <= dis3) {
				as.add(current_c);
			} else if (dis2 <= dis1 && dis2 <= dis3) {
				bs.add(current_c);
			} else {
				cs.add(current_c);
			}
		}

		for (int p = 0; p < iter; p++) {
			Cluster a_center = new Cluster(0, 0, 0, 0, 0, 0, 0, 0);
			Cluster b_center = new Cluster(0, 0, 0, 0, 0, 0, 0, 0);
			Cluster c_center = new Cluster(0, 0, 0, 0, 0, 0, 0, 0);
			for (Cluster c1 : as) {
				a_center.l += c1.l; a_center.a += c1.a; a_center.b += c1.b;
			}
			a_center.l /= as.size(); a_center.a /= as.size(); a_center.b /= as.size();
			
			for (Cluster c1 : bs) {
				b_center.l += c1.l; b_center.a += c1.a; b_center.b += c1.b;
			}
			b_center.l /= bs.size(); b_center.a /= bs.size(); b_center.b /= bs.size();
			
			for (Cluster c1 : cs) {
				c_center.l += c1.l; c_center.a += c1.a; c_center.b += c1.b;
			}
			c_center.l /= cs.size(); c_center.a /= cs.size(); c_center.b /= cs.size();
			
			for (int i = 0; i < bd_size; i++) {
				Cluster current_c = border_clusters.get(i);
				double dis1 = distance_base_Lab_by_cluster(a_center, current_c);
				double dis2 = distance_base_Lab_by_cluster(b_center, current_c);
				double dis3 = distance_base_Lab_by_cluster(c_center, current_c);
				if (dis1 <= dis2 && dis1 <= dis3) {
					as.add(current_c);
					if (as.contains(current_c)) continue;
					else as.add(current_c);
					if (bs.contains(current_c)) bs.remove(current_c);
					if (cs.contains(current_c)) cs.remove(current_c);
				} else if (dis2 <= dis1 && dis2 <= dis3) {
					if (as.contains(current_c)) as.remove(current_c);
					if (bs.contains(current_c)) continue;
					else bs.add(current_c);
					if (cs.contains(current_c)) cs.remove(current_c);
				} else {
					if (as.contains(current_c)) as.remove(current_c);
					if (bs.contains(current_c)) bs.remove(current_c);
					if (cs.contains(current_c)) continue;
					else cs.add(current_c);
				}
			}
			
		}
		
		for (Cluster t : as) {
			class_clusters[1].add(t);
		}
		for (Cluster t : bs) {
			class_clusters[2].add(t);
		}
		for (Cluster t : cs) {
			class_clusters[3].add(t);
		}
		
	}

	
	// 计算两个簇之间的颜色距离 归一化
	private static double distance_base_Lab_by_cluster_no(Cluster c1, Cluster c2) {
		double l1 = c1.l / 255;
		double a1 = c1.a / 128;
		double b1 = c1.b / 128;
		double l2 = c2.l / 255;
		double a2 = c2.a / 128;
		double b2 = c2.b / 128;
		double t1 = l1 - l2;
		double t2 = a1 - a2;
		double t3 = b1 - b2;
		t1 *= t1;
		t2 *= t2;
		t3 *= t3;
		return Math.sqrt(t1 + t2 + t3);
	}
	
	// 计算两个簇之间的颜色距离
	private static double distance_base_Lab_by_cluster(Cluster c1, Cluster c2) {
		double l1 = c1.l;
		double a1 = c1.a;
		double b1 = c1.b;
		double l2 = c2.l;
		double a2 = c2.a;
		double b2 = c2.b;
		double t1 = l1 - l2;
		double t2 = a1 - a2;
		double t3 = b1 - b2;
		t1 *= t1;
		t2 *= t2;
		t3 *= t3;
		return Math.sqrt(t1 + t2 + t3);
	}
	
//	private static double CIE94(Cluster c1, Cluster c2) {
//		double L_1 = c1.l;
//		double a_1 = c1.a;
//		double b_1 = c1.b;
//		double L_2 = c2.l;
//		double a_2 = c2.a;
//		double b_2 = c2.b;
//		double delta_L = L_2 - L_1;
//		double C_1 = Math.sqrt(a_1 * a_1 + b_1 * b_1);
//		double C_2 = Math.sqrt(a_2 * a_2 + b_2 * b_2);
//		double delta_Cab = C_1 - C_2;
//		double delta_a = a_1 - a_2;
//		double delta_b = b_1 - b_2;
//		double delta_Hab = Math.sqrt(delta_a * delta_a + delta_b * delta_b - delta_Cab * delta_Cab);
//		double t1 = Math.pow(delta_L, 2);
//		double Sc = 1 + 0.045d * Math.sqrt(C_1 * C_2);
//		double Sh = 1 + 0.015d * Math.sqrt(C_1 * C_2);
//		double t2 = Math.pow(delta_Cab / Sc, 2);
//		double t3 = Math.pow(delta_Hab / Sh, 2);
//		double delta_E = Math.sqrt(t1 + t2 + t3);
//		return delta_E;
//	}
	
	// 计算两个簇之间的距离
	private static double distance_base_position_by_cluster(Cluster c1, Cluster c2) {
		double x1 = c1.w;
		double x2 = c2.w;
		double y1 = c1.h;
		double y2 = c2.h;
		double t1 = x1 - x2;
		double t2 = y1 - y2;
		t1 *= t1;
		t2 *= t2;
		return Math.sqrt(t1 + t2);
	}
	
	// 计算两个簇之间的距离 归一化
	private static double distance_base_position_by_cluster_no(Cluster c1, Cluster c2) {
		double x1 = c1.w * 1.0d / width;
		double x2 = c2.w * 1.0d / width;
		double y1 = c1.h * 1.0d / height;
		double y2 = c2.h * 1.0d / height;
		double t1 = x1 - x2;
		double t2 = y1 - y2;
		t1 *= t1;
		t2 *= t2;
		return Math.sqrt(t1 + t2);
	}
	
	// 深克隆
	private static Cluster clone(Cluster c) {
		int h, w, id;
		double l, a, b, R, G, B;
		h = c.h; w = c.w;
		l = c.l; a = c.a; b = c.b; R = c.R; G = c.G; B = c.B;
		id = c.id;
		List<Position> list = new ArrayList<>();
		for (Position p : c.pixels) {
			Position p2 = new Position(p.w, p.h);
			list.add(p2);
		}
		Cluster c2 = new Cluster(h, w, l, a, b, R, G, B, id, list);
		return c2;
	}
	
	
}