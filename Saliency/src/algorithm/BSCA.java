package algorithm;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import main.Common;
import tool.Num;
import tool.SImage;

public class BSCA{
	
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
	// 记录元胞邻居 是源码中的impfactor
	private static int[][] neighbour;
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
		// K聚类				
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
		
		// 获取元胞组 即 影响因子矩阵
		initial_neighbour();
		
		initial_edges();
		makeweights(edges, seg_vals, theta);
		
		double[] SN = SCA();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				double r = SN[sulabel[i][j]];
				int c = (int)(r*255);
				image.setRGB(i, j, c, c, c);
			}
		}
		
		return image.getImage();
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
		neighbour = new int[clusters.size()][clusters.size()];
		F = new double[clusters.size()][clusters.size()];
		// 默认是空的
		edges = new int[0][2];
		weights = new double[0];
		seg_vals = new double[clusters.size()][3];
		initial_seg_vals();
		D = new double[clusters.size()][clusters.size()];
		F_normal = new double[clusters.size()][clusters.size()];
	}
	
	private static double[] SCA() {
		int[] dif = new int[clusters.size() - border_clusters.size()];
		int[] boundsp = new int[border_clusters.size()];
		int[] setdiff = new int[clusters.size()]; 
		for (int i = 0; i < boundsp.length; i++) {
			boundsp[i] = border_clusters.get(i).id;
			setdiff[boundsp[i]] = 1;
		}
		int dif_index = 0;
		for (int i = 0; i < clusters.size(); i++) {
			if (setdiff[i] == 0) dif[dif_index++] = i;
		}
		Arrays.sort(boundsp);
		double[] S_bg = new double[Sbg.size()];
		for (Cluster c : Sbg) {
			S_bg[c.id] = c.R;
		}
		double[] S_N1 = S_bg;
		
		// 压制边界超像素
		for (int lap = 0; lap < 5; lap++) {
			for (int i = 0; i < boundsp.length; i++) {
				S_N1[boundsp[i]] -= 0.6;
			}
			for (int i = 0; i < S_N1.length; i++) {
				if (S_N1[i] <= 0) S_N1[i] = 0.001d;
			}
			double[][] T_S_N1 = new double[S_N1.length][1];
			for (int i = 0; i <T_S_N1.length; i++) {
				T_S_N1[i][0] = S_N1[i];
			}
			double[][] C_normal_S_N1 = Num.mul(C_normal, T_S_N1);
			double[][] C_normal_2 = new double[C_normal.length][C_normal[0].length];
			for (int p = 0; p < C_normal.length; p++) {
				for (int q = 0; q < C_normal[0].length; q++) {
					if (p == q) {
						C_normal_2[p][q] = 1 - C_normal_2[p][q];
					}
				}
			}
			double[][] temp = Num.mul(C_normal_2, F_normal);
			double[][] temp2 = Num.mul(temp, T_S_N1);
			double[][] new_S_N1 = Num.add(C_normal_S_N1, temp2);
			for (int i = 0; i < new_S_N1.length; i++) {
				S_N1[i] = new_S_N1[i][0];
			}
			double S_N1_dif_min = Double.MAX_VALUE;
			double S_N1_dif_max = Double.MIN_VALUE;
			for (int i = 0; i < dif.length; i++) {
				if (S_N1[dif[i]] < S_N1_dif_min) S_N1_dif_min = S_N1[dif[i]];
				if (S_N1[dif[i]] > S_N1_dif_max) S_N1_dif_max = S_N1[dif[i]];
			}
			for (int i = 0; i < dif.length; i++) {
				S_N1[dif[i]] -= S_N1_dif_min;
				S_N1[dif[i]] /= (S_N1_dif_max - S_N1_dif_min);
			}
		}
		
		// 凸显前景 记得把5改回来
		for (int lap = 0; lap < 5; lap++) {
			for (int i = 0; i < boundsp.length; i++) {
				S_N1[boundsp[i]] -= 0.6;
			}
			for (int i = 0; i < S_N1.length; i++) {
				if (S_N1[i] <= 0) S_N1[i] = 0.001d;
			}
			int[] most_sal_sup = Num.find_greater_than(S_N1, 0.93);
			
			if (most_sal_sup.length < 0.02d*clusters.size()) {
				int[] sal_diff = Num.setdiff(clusters.size(), most_sal_sup);
				double S_N1_sal_diff_min = Double.MAX_VALUE;
				double S_N1_sal_diff_max = Double.MIN_VALUE;
				for (int i = 0; i < sal_diff.length; i++) {
					if (S_N1[sal_diff[i]] < S_N1_sal_diff_min) S_N1_sal_diff_min = S_N1[sal_diff[i]];
					if (S_N1[sal_diff[i]] > S_N1_sal_diff_max) S_N1_sal_diff_max = S_N1[sal_diff[i]];
				}

				for (int i = 0; i < sal_diff.length; i++) {
					S_N1[sal_diff[i]] -= S_N1_sal_diff_min;
					S_N1[sal_diff[i]] /= (S_N1_sal_diff_max - S_N1_sal_diff_min);
				}
			}
			
			double[][] T_S_N1 = new double[S_N1.length][1];
			for (int i = 0; i <T_S_N1.length; i++) {
				T_S_N1[i][0] = S_N1[i];
			}
			double[][] C_normal_S_N1 = Num.mul(C_normal, T_S_N1);
			double[][] C_normal_2 = new double[C_normal.length][C_normal[0].length];
			for (int p = 0; p < C_normal.length; p++) {
				for (int q = 0; q < C_normal[0].length; q++) {
					if (p == q) {
						C_normal_2[p][q] = 1 - C_normal_2[p][q];
					}
				}
			}
			double[][] temp = Num.mul(C_normal_2, F_normal);
			double[][] temp2 = Num.mul(temp, T_S_N1);
			double[][] new_S_N1 = Num.add(C_normal_S_N1, temp2);
			for (int i = 0; i < new_S_N1.length; i++) {
				S_N1[i] = new_S_N1[i][0];
			}
			double S_N1_dif_min = Double.MAX_VALUE;
			double S_N1_dif_max = Double.MIN_VALUE;
			for (int i = 0; i < dif.length; i++) {
				if (S_N1[dif[i]] < S_N1_dif_min) S_N1_dif_min = S_N1[dif[i]];
				if (S_N1[dif[i]] > S_N1_dif_max) S_N1_dif_max = S_N1[dif[i]];
			}
			for (int i = 0; i < dif.length; i++) {
				S_N1[dif[i]] -= S_N1_dif_min;
				S_N1[dif[i]] /= (S_N1_dif_max - S_N1_dif_min);
			}
		}
		
		// 根据自动元胞机的规则更新
		for (int lap = 0; lap < 10; lap++) {
			double[][] T_S_N1 = new double[S_N1.length][1];
			for (int i = 0; i <T_S_N1.length; i++) {
				T_S_N1[i][0] = S_N1[i];
			}
			double[][] C_normal_S_N1 = Num.mul(C_normal, T_S_N1);
			double[][] C_normal_2 = new double[C_normal.length][C_normal[0].length];
			for (int p = 0; p < C_normal.length; p++) {
				for (int q = 0; q < C_normal[0].length; q++) {
					if (p == q) {
						C_normal_2[p][q] = 1 - C_normal_2[p][q];
					}
				}
			}
			double[][] temp = Num.mul(C_normal_2, F_normal);
			double[][] temp2 = Num.mul(temp, T_S_N1);
			double[][] new_S_N1 = Num.add(C_normal_S_N1, temp2);
			for (int i = 0; i < new_S_N1.length; i++) {
				S_N1[i] = new_S_N1[i][0];
			}
			double S_N1_min = Double.MAX_VALUE;
			double S_N1_max = Double.MIN_VALUE;
			for (int i = 0; i < S_N1.length; i++) {
				if (S_N1[i] < S_N1_min) S_N1_min = S_N1[i];
				if (S_N1[i] > S_N1_max) S_N1_max = S_N1[i];
			}
			for (int i = 0; i < S_N1.length; i++) {
				S_N1[i] -= S_N1_min;
				S_N1[i] /= (S_N1_max - S_N1_min);
			}
		}
		
		return S_N1;
	}
	
	// 仍是原Matlab代码中的方法，复现
	private static void makeweights(int[][] edges, double[][] vals, double theta) {
		int[] edge__1 = new int[edges.length];
		int[] edge__2 = new int[edges.length];
		for (int i = 0; i < edges.length; i++) {
			edge__1[i] = edges[i][0];
			edge__2[i] = edges[i][1];
		}
		double[][] vals_edges__1 = new double[edge__1.length][3];
		double[][] vals_edges__2 = new double[edge__2.length][3];
		for (int i = 0; i < vals_edges__1.length; i++) {
			vals_edges__1[i][0] = vals[edge__1[i]][0];
			vals_edges__1[i][1] = vals[edge__1[i]][1];
			vals_edges__1[i][2] = vals[edge__1[i]][2];
			vals_edges__2[i][0] = vals[edge__2[i]][0];
			vals_edges__2[i][1] = vals[edge__2[i]][1];
			vals_edges__2[i][2] = vals[edge__2[i]][2];
		}
		double[][] re = Num.sub(vals_edges__1, vals_edges__2);
		re = Num.square(re);
		double[] valDistances = Num.sum(re);
		valDistances = Num.sqrt(valDistances);
		valDistances = Num.normalize(valDistances);
		valDistances = Num.mul(valDistances, -1.0d*theta);
		weights = Num.exp(valDistances);
		
		for (int i = 0; i < edges.length; i++) {
			F[edges[i][0]][edges[i][1]] = weights[i];
			F[edges[i][1]][edges[i][0]] = weights[i];
		}
		double[] D_sam = Num.sum(F);
		for (int i = 0; i < D_sam.length; i++) {
			D[i][i] = D_sam[i];
		}
		double[][] inv_D = Num.inv(D);
		F_normal = Num.mul(inv_D, F);
		double[][] F_ = Num.T(F);
		double[] max_F = Num.max(F_);
		max_F = Num.normalize(max_F);
		double[] C = Num.mul(max_F, a);
		C = Num.add(C, b);
		C_normal = new double[C.length][C.length];
		for (int i = 0; i < C.length; i++) {
			C_normal[i][i] = C[i];
		}
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
	
	// 这个方法在原文中没有提及，所以就按照源码直接照搬
	// 命名也跟他一样，所以会和之前的命名有些不一样
	private static void initial_edges() {
		int supNum = clusters.size();
		for (int i = 0; i < supNum; i++) {
			// 默认是空的
			int[] impfactor_temp = neighbour[i];
			int[] ind = Num.find(impfactor_temp, 1);
			int[] indext = ind;
			for (int j = 0; j < ind.length; j++) {
				int[] indj = Num.find(neighbour[ind[j]], 1);
				indext = Num.merge(indext, indj);
			}
			indext = Num.arrays_greater_than(indext, i);
			indext = Num.unique(indext);
			Arrays.sort(indext);
			if (indext.length > 0) {
				int[][] ed = Num.ones(indext.length, 2);
				ed = Num.assign_column(ed, 1, i);
				ed = Num.assign_column(ed, 0, indext);
				edges = Num.merge(edges, ed);
			}
		}
	}
	
	// 赋值元胞邻居数组
	private static void initial_neighbour() {
		int bd_clusters_size = border_clusters.size();
		int size = clusters.size();
		int[][] M = Num.T(sulabel);
		for (int i = 0; i < height-1; i++) {
			for (int j = 0; j < width-1; j++) {
				if (M[i][j] != M[i][j+1]) {
					neighbour[M[i][j]][M[i][j+1]] = 1;
					neighbour[M[i][j+1]][M[i][j]] = 1;
				}
				if (M[i][j] != M[i+1][j]) {
					neighbour[M[i][j]][M[i+1][j]] = 1;
					neighbour[M[i+1][j]][M[i][j]] = 1;
				}
				if (M[i][j] != M[i+1][j+1]) {
					neighbour[M[i][j]][M[i+1][j+1]] = 1;
					neighbour[M[i+1][j+1]][M[i][j]] = 1;
				}
				if (M[i+1][j] != M[i][j+1]) {
					neighbour[M[i+1][j]][M[i][j+1]] = 1;
					neighbour[M[i][j+1]][M[i+1][j]] = 1;
				}
			}
		}
		
		// 边缘超像素
		for (int i = 0; i < bd_clusters_size; i++) {
			for (int j = i + 1; j < bd_clusters_size; j++) {
				Cluster ci = border_clusters.get(i);
				Cluster cj = border_clusters.get(j);
				neighbour[ci.id][cj.id] = 1;
				neighbour[cj.id][ci.id] = 1;
			}
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
		}
		for (Cluster cluster : Sbg) {
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
	
	// 取三个double中最小值
	private static double min(double a, double b, double c) {
		if (a <= b && a <= c) {
			return a;
		} else if (b <= a && b <= c) {
			return b;
		} else {
			return c;
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