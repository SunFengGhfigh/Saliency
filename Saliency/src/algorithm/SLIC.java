package algorithm;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import main.Common;
import tool.SImage;

class Position {
	public int w;
	public int h;
	public Position(int w, int h) {
		this.w = w;
		this.h = h;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + h;
		result = prime * result + w;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		Position other = (Position) obj;
		if (h != other.h)
			return false;
		if (w != other.w)
			return false;
		return true;
	}
	
}

class Cluster{
	
	public int h;
	public int w;
	public double l;
	public double a;
	public double b;
	public double R;
	public double G;
	public double B;
	public int id;
	List<Position> pixels = new ArrayList<Position>();
	
	public Cluster(int w, int h, double l, double a, double b, double R, double G, double B) {
		this.h = h;
		this.w = w;
		this.l = l;
		this.a = a;
		this.b = b;
		this.R = R;
		this.G = G;
		this.B = B;
		this.id = -1;
	}
	
	public void update(int w, int h, double l, double a, double b, double R, double G, double B) {
		this.h = h;
		this.w = w;
		this.l = l;
		this.a = a;
		this.b = b;
		this.R = R;
		this.G = G;
		this.B = B;
		this.id = -1;
	}

	public Cluster(int h, int w, double l, double a, double b, double r, double g, double b2, List<Position> pixels) {
		super();
		this.h = h;
		this.w = w;
		this.l = l;
		this.a = a;
		this.b = b;
		R = r;
		G = g;
		B = b2;
		this.pixels = pixels;
		this.id = -1;
	}

	public Cluster(int h, int w, double l, double a, double b, double r, double g, double b2, int id,
			List<Position> pixels) {
		super();
		this.h = h;
		this.w = w;
		this.l = l;
		this.a = a;
		this.b = b;
		R = r;
		G = g;
		B = b2;
		this.id = id;
		this.pixels = pixels;
	}

	@Override
	public String toString() {
		return "Cluster [h=" + h + ", w=" + w + ", l=" + l + ", a=" + a + ", b=" + b + ", R=" + R + ", G=" + G + ", B="
				+ B + ", id=" + id + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(B);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(G);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(R);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(a);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(b);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + h;
		result = prime * result + id;
		temp = Double.doubleToLongBits(l);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((pixels == null) ? 0 : pixels.hashCode());
		result = prime * result + w;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cluster other = (Cluster) obj;
		if (Double.doubleToLongBits(B) != Double.doubleToLongBits(other.B))
			return false;
		if (Double.doubleToLongBits(G) != Double.doubleToLongBits(other.G))
			return false;
		if (Double.doubleToLongBits(R) != Double.doubleToLongBits(other.R))
			return false;
		if (Double.doubleToLongBits(a) != Double.doubleToLongBits(other.a))
			return false;
		if (Double.doubleToLongBits(b) != Double.doubleToLongBits(other.b))
			return false;
		if (h != other.h)
			return false;
		if (id != other.id)
			return false;
		if (Double.doubleToLongBits(l) != Double.doubleToLongBits(other.l))
			return false;
		if (pixels == null) {
			if (other.pixels != null)
				return false;
		} else if (!pixels.equals(other.pixels))
			return false;
		if (w != other.w)
			return false;
		return true;
	}
	
}

public class SLIC {
	
	private static SImage image;
	private static int width;
	private static int height;
	// 像素总数
	private static int N;
	// 步长
	private static int S;
	// 分类个数
	public static int K = 300;
	// 公式参数，取值在[10-40]，可更改
	public static int M = 20;
	// 存储每个像素的rgb值
	private static double RGB[][][];
	// 存储每个像素的Lab值
	private static double Lab[][][];
	// 存储超像素簇
	private static List<Cluster> clusters;
	// 某像素对应某簇
	private static Map<Position, Cluster> label;
	private static double dis[][];
	//
	private static int[][] sulabel;
	
	public static BufferedImage deal(BufferedImage input) {
		image = new SImage(input);
		initial();
		initial_cluster();
		move_cluster();
		for (int i = 0; i < 5; i++) {
			assignment();
			update_cluster();
		}
		initial_sulabel();
		
		draw_color();
		switch (Common.current_color_space) {
			case "RGB": Lab = image.RGB; break;
			case "Lab": Lab = image.Lab; break;
			case "XYZ": Lab = image.XYZ; break;
			case "HSV": Lab = image.HSV; break;
			case "YUV": Lab = image.YUV; break;
			case "YIQ": Lab = image.YIQ; break;
			case "YCbCr": Lab = image.YCbCr; break;
			case "LCH": Lab = image.LCH; break;
		}
		
		for (Cluster c : clusters) {
			c.l = Lab[c.w][c.h][0];
			c.a = Lab[c.w][c.h][1];
			c.b = Lab[c.w][c.h][2];
		}
		
		return image.getImage();
	}
	
	private static void initial_sulabel() {
		sulabel = new int[width][height];
		HashSet<Cluster> set = new HashSet<>();
		// 更新cluster的id
		int c_id = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (label.get(new Position(i, j)).id == -1) {
					label.get(new Position(i, j)).id = c_id++; 
				}
				set.add(label.get(new Position(i, j)));
			}
		}
		clusters.clear();
		int[] asd = new int[set.size()];
		int asdid = 0;
		for (Cluster c : set) {
			clusters.add(c);
			asd[asdid++] = c.id;
		}
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				sulabel[i][j] = label.get(new Position(i, j)).id;
			}
		}
	}
	
	// 上色
	private static void draw_color() {
		for (Cluster c : clusters) {
			c.R = 0; c.G = 0; c.B = 0;
			c.l = 0; c.a = 0; c.b = 0;
			for (Position p : c.pixels) {
				c.R += RGB[p.w][p.h][0]; c.G += RGB[p.w][p.h][1]; c.B += RGB[p.w][p.h][2];
				c.l += Lab[p.w][p.h][0]; c.a += Lab[p.w][p.h][1]; c.b += Lab[p.w][p.h][2];
			}
			c.R /= c.pixels.size(); c.G /= c.pixels.size(); c.B /= c.pixels.size();
			c.l /= c.pixels.size(); c.a /= c.pixels.size(); c.b /= c.pixels.size();
		}
		for (int i = 0; i < clusters.size(); i++) {
			Cluster cluster = clusters.get(i);
			for (int j = 0; j < cluster.pixels.size(); j++) {
				Position p = cluster.pixels.get(j);
				image.setRGB(p.w, p.h, (int)cluster.R, (int)cluster.G, (int)cluster.B);
			}
		}
	}
	
	// 分配
	private static void assignment() {
		for (int i = 0; i < clusters.size(); i++) {
			Cluster cluster = clusters.get(i);
			for (int w = cluster.w - 2 * S; w < cluster.w + 2 * S; w++) {
				if (w < 0 || w >= width) continue;
				for (int h = cluster.h - 2 * S; h < cluster.h + 2 * S; h++) {
					if (h < 0 || h >= height) continue;
					double L = Lab[w][h][0];
					double A = Lab[w][h][1];
					double B = Lab[w][h][2];
					double Dc = Math.sqrt(Math.pow(L - cluster.l, 2) + Math.pow(A - cluster.a, 2) + Math.pow(B - cluster.b, 2));
					double Ds = Math.sqrt(Math.pow(h - cluster.h, 2) + Math.pow(w - cluster.w, 2));
					double D = Math.sqrt(Math.pow(Dc / M, 2) + Math.pow(Ds / S, 2));
					if (D < dis[w][h]) {
						Position p = new Position(w, h);
						if (label.get(p) == null) {
							label.put(p, cluster);
							cluster.pixels.add(p);
						} else {
							label.get(p).pixels.remove(p);
							label.put(p, cluster);
							cluster.pixels.add(p);
						}
						dis[w][h] = D;
					}
				}
			}
		}
	}
	
	// 更新簇
	private static void update_cluster() {
		for (int i = 0; i < clusters.size(); i++) {
			Cluster cluster = clusters.get(i);
			double sum_h = 0, sum_w = 0, number = 0;
			for (int j = 0; j < cluster.pixels.size(); j++) {
				Position p = cluster.pixels.get(j);
				sum_h += p.h;
				sum_w += p.w;
				number++;
			}
			int _h = (int)(sum_h / number);
			int _w = (int)(sum_w / number);
			cluster.update(_w, _h, Lab[_w][_h][0], Lab[_w][_h][1], Lab[_w][_h][2], RGB[_w][_h][0], RGB[_w][_h][1], RGB[_w][_h][2]);
		}
	}
	
	// 变量初始化
	private static void initial() {
		width = image.width;
		height = image.height;
		N = width * height;
		S = (int)Math.sqrt(N / K);
		RGB = image.getRGBMAtrix();
		Lab = image.Lab;
		clusters = new ArrayList<>();
		label = new HashMap<Position, Cluster>();
		dis = new double[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				dis[i][j] = Double.MAX_VALUE;
			}
		}
	}
	
	// 均匀散布簇中心点
	private static void initial_cluster() {
		int h = S / 2;
		int w = S / 2;
		while (w < width) {
			while (h < height) {
				clusters.add(new Cluster(w, h, 0, 0, 0, 0, 0, 0));
				h += S;
			}
			h = S / 2;
			w += S;
		}
	}
	
	// 移动中心点，移动至梯度最小处
	private static void move_cluster() {
		for (int i = 0; i < clusters.size(); i++) {
			Cluster cluster = clusters.get(i);
			double cluster_gradient = get_gradient(cluster.w, cluster.h);
			for (int dw = -1; dw < 2; dw++) {
				for (int dh = -1; dh < 2; dh++) {
					int _w = cluster.w + dw;
					int _h = cluster.h + dh;
					if (_w >= width-1 || _h >= height-1) continue;
					double new_gradient = get_gradient(_w, _h);
					if (new_gradient < cluster_gradient) {
						cluster.update(_w, _h, Lab[_w][_h][0], Lab[_w][_h][1], Lab[_w][_h][2], RGB[_w][_h][0], RGB[_w][_h][1], RGB[_w][_h][2]);
						cluster_gradient = new_gradient;
					}
				}
			}
		}
	}
	
	// 得到该处梯度
	private static double get_gradient(int w, int h) {
		if (w >= width-1) w = width - 2;
		if (h >= height-1) h = height - 2;
		double gradient = Lab[w+1][h+1][0] - Lab[w][h][0] + 
				Lab[w+1][h+1][1] - Lab[w][h][1] +
				Lab[w+1][h+1][2] - Lab[w][h][2];
		return gradient;
		
	}
	
	// RGB转LAB
	private static double[][][] RGB2Lab(){
		double[][][] Lab = new double[width][height][3];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				double R = RGB[i][j][0] / 255.0d;
				double G = RGB[i][j][1] / 255.0d;
				double B = RGB[i][j][2] / 255.0d;
				if (R > 0.04045d) {
					R = Math.pow((R + 0.055) / 1.055, 2.4d);
				} else {
					R /= 12.92;
				}
				if (G > 0.04045d) {
					G = Math.pow((G + 0.055) / 1.055, 2.4d);
				} else {
					G /= 12.92;
				}
				if (B > 0.04045d) {
					B = Math.pow((B + 0.055) / 1.055, 2.4d);
				} else {
					B /= 12.92;
				}
				double X = 0.436052025 * R + 0.385081593 * G + 0.143087414 * B;
				double Y = 0.222491598 * R + 0.716886060 * G + 0.060621486 * B;
				double Z = 0.013929122 * R + 0.097097002 * G + 0.714185470 * B;
				X = X * 100.000d;
			    Y = Y * 100.000d;
			    Z = Z * 100.000d;
			    double ref_X = 96.4221d;
			    double ref_Y = 100.000d;
			    double ref_Z = 82.5211d;
			    X = X / ref_X;
			    Y = Y / ref_Y;
			    Z = Z / ref_Z;
				if (X > 0.008856d) {
					X = Math.pow(X, 1/3.00d);
				} else {
					X = (7.787 * X) + (16.0d / 116.0d);
				}
				if (Y > 0.008856d) {
					Y = Math.pow(Y, 1/3.00d);
				} else {
					Y = (7.787 * Y) + (16.0d / 116.0d);
				}
				if (Z > 0.008856d) {
					Z = Math.pow(Z, 1/3.00d);
				} else {
					Z = (7.787 * Z) + (16.0d / 116.0d);
				}
				Lab[i][j][0] = (116.0d * Y ) - 16.0d;
				Lab[i][j][1] = 500.0d * (X - Y);
				Lab[i][j][2] = 200.0d * (Y - Z);
			}
		}
		return Lab;
	}

	public static double[][][] getRGB() {
		return RGB;
	}

	public static double[][][] getLab() {
		return Lab;
	}

	public static List<Cluster> getClusters() {
		return clusters;
	}

	public static Map<Position, Cluster> getLabel() {
		return label;
	}

	public static int[][] getSulabel() {
		return sulabel;
	}

}
