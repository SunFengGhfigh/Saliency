package tool;

import java.awt.image.BufferedImage;

public class SImage {
	
	private static BufferedImage image;
	public static int width;
	public static int height;
	public static double[][][] RGB;
	public static double[][][] XYZ;
	public static double[][][] HSV;
	public static double[][][] Lab;
	public static double[][][] YUV;
	public static double[][][] YIQ;
	public static double[][][] YCbCr;
	public static double[][][] LCH;
	public static double[][][] HED;
	
	public SImage(BufferedImage image) {
		this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();
		initial_RGB();
		initial_XYZ();
		initial_HSV();
		initial_Lab();
		initial_YUV();
		initial_YIQ();
		initial_YCbCr();
		initial_LCH();
		initial_HED();
	}
	
	/**
	 * RGB->YCbCr
	 */
	private void initial_YCbCr() {
		YCbCr = new double[width][height][3];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				double r = RGB[i][j][0];
				double g = RGB[i][j][1];
				double b = RGB[i][j][2];
				double Y = 0.257d * r + 0.564d * g + 0.098d * b;
				double Cb = (-0.148d) * r + (-0.291d) * g + 0.439d * b;
				double Cr = 0.439d * r + (-0.368d) * g + 0.071d * b;
				YCbCr[i][j][0] = Y;
				YCbCr[i][j][1] = Cb;
				YCbCr[i][j][2] = Cr;
			}
		}
	}
	
	/**
	 * 得到RGB矩阵
	 */
	private void initial_RGB() {
		RGB = new double[width][height][3];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int pixel = image.getRGB(i, j);
				int r = (pixel & 0xff0000) >> 16;
				int g = (pixel & 0xff00) >> 8;
				int b = (pixel & 0xff);
				RGB[i][j][0] = r * 1.0d;
				RGB[i][j][1] = g * 1.0d;
				RGB[i][j][2] = b * 1.0d;
			}
		}
	}
	
	/**
	 * 从Lab得到LCH矩阵
	 */
	private void initial_LCH() {
		LCH = new double[width][height][3];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				double l = Lab[i][j][0];
				double a = Lab[i][j][1];
				double b = Lab[i][j][2];
				double c = Math.sqrt(a*a + b*b);
				double h = Math.atan2(b, a);
				h = Math.toDegrees(h);
				if (h < 0) {
					h += 360;
				}
				LCH[i][j][0] = l;
				LCH[i][j][1] = c;
				LCH[i][j][2] = h;
			}
		}
	}
	
	/**
	 * RGB->YUV
	 */
	private void initial_YUV() {
		YUV = new double[width][height][3];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				double r = RGB[i][j][0];
				double g = RGB[i][j][1];
				double b = RGB[i][j][2];
				double Y = 0.299d * r + 0.587d * g + 0.114d * b;
				double U = -0.147d * r + (-0.289d) * g + 0.436 * b;
				double V = 0.615d * r + (-0.515d) * g + (-0.1d) * b;
				YUV[i][j][0] = Y;
				YUV[i][j][1] = U;
				YUV[i][j][2] = V;
			}
		}
	}
	
	/**
	 * RGB->HED
	 */
	private void initial_HED() {
		HED = new double[width][height][3];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				double r = -1.0d * Math.log10(RGB[i][j][0]);
				double g = -1.0d * Math.log10(RGB[i][j][1]);
				double b = -1.0d * Math.log10(RGB[i][j][2]);
				double H = 1.878d * r - 0.0659d * g - 0.6019d * b;
				double E = -1.0077d * r + 1.1347d * g - 0.4804 * b;
				double D = -0.5561d * r - 0.1355d * g + 1.5736d * b;
				HED[i][j][0] = H;
				HED[i][j][1] = E;
				HED[i][j][2] = D;
			}
		}
	}
	
	/**
	 * RGB->YIQ
	 */
	private void initial_YIQ() {
		YIQ = new double[width][height][3];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				double r = RGB[i][j][0];
				double g = RGB[i][j][1];
				double b = RGB[i][j][2];
				double Y = 0.299d * r + 0.587d * g + 0.114d * b;
				double I = 0.596d * r + (-0.275d) * g + (-0.321d) * b;
				double Q = 0.212d * r + (-0.523d) * g + 0.311d * b;
				YIQ[i][j][0] = Y;
				YIQ[i][j][1] = I;
				YIQ[i][j][2] = Q;
			}
		}
	}
	
	/**
	 * 从RGB得到XYZ矩阵
	 */
	private void initial_XYZ() {
		XYZ = new double[width][height][3];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				double r = RGB[i][j][0] / 255.0d;
				double g = RGB[i][j][1] / 255.0d;
				double b = RGB[i][j][2] / 255.0d;
				double R = gamma(r);
				double G = gamma(g);
				double B = gamma(b);
				double X = 100.0d * (0.436052025d*R + 0.385081593d*G + 0.143087414d*B);
				double Y = 100.0d * (0.222491598d*R + 0.716886060d*G + 0.060621486d*B);
				double Z = 100.0d * (0.013929122d*R + 0.097097002d*G + 0.714185470d*B);
				XYZ[i][j][0] = X;
				XYZ[i][j][1] = Y;
				XYZ[i][j][2] = Z;
			}
		}
	}
	
	// 从属于XYZ方法
	private static double gamma(double x) {
		if (x > 0.04045d) {
			return Math.pow(((x+0.055)/(1.055)), 2.4d);
		} else {
			return x / 12.92d;
		}
	}
	
	/**
	 * 从RGB得到HSV
	 */
	private void initial_HSV() {
		HSV = new double[width][height][3];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				double r = RGB[i][j][0] / 255.0d;
				double g = RGB[i][j][1] / 255.0d;
				double b = RGB[i][j][2] / 255.0d;
				double Cmax = max_3(r, g, b);
				double Cmin = min_3(r, g, b);
				double delta = Cmax - Cmin;
				double H, S, V;
				if (delta == 0) {
					H = 0;
				}else if (Cmax == r) {
					H = 60.0d * ((g - b)/delta);
				} else if (Cmax == g) {
					H = 60.0d * ((b - r)/delta + 2);
				} else {
					H = 60.0d * ((r - g)/delta + 4);
				}
				if (Cmax == 0) {
					S = 0;
				} else {
					S = delta / Cmax;
				}
				V = Cmax;
				S *= 100.0d;
				V *= 100.0d;
				HSV[i][j][0] = H;
				HSV[i][j][1] = S;
				HSV[i][j][2] = V;
			}
		}
	}
	
	// 从属于HSV
	private static double max_3(double a, double b, double c) {
		if (a >= b && a >= c) {
			return a;
		} else if (b >= a && b >= c) {
			return b;
		} else {
			return c;
		}
	}
	
	// 从属于HSV
	private static double min_3(double a, double b, double c) {
		if (a <= b && a <= c) {
			return a;
		} else if (b <= a && b <= c) {
			return b;
		} else {
			return c;
		}
	}
	
	/**
	 * 计算Lab矩阵
	 */
	private void initial_Lab() {
		Lab = new double[width][height][3];
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
	}
	
	public BufferedImage getImage() {
		return this.image;
	}
	
	public int getR(int x, int y) {
		int pixel = image.getRGB(x, y);
		return (pixel & 0xff0000) >> 16;
	}
	
	public int getG(int x, int y) {
		int pixel = image.getRGB(x, y);
		return (pixel & 0xff00) >> 8;
	}
	
	public int getB(int x, int y) {
		int pixel = image.getRGB(x, y);
		return (pixel & 0xff);
	}
	
	// 获得特殊格式的RGB值，比如(255, 41, 6) -> 255041006
	public Integer getRGB(int x, int y) {
		int pixel = image.getRGB(x, y);
		int r = (pixel & 0xff0000) >> 16;
		int g = (pixel & 0xff00) >> 8;
		int b = (pixel & 0xff);
		String R = getRGB_deal(r);
		String G = getRGB_deal(g);
		String B = getRGB_deal(b);
		return Integer.parseInt(R + G + B);
	}
	
	private String getRGB_deal(int a) {
		if (a < 10) {
			return "00" + a;
		} else if (a < 100) {
			return "0" + a;
		} else {
			return "" + a;
		}
	}
	
	// 设置某xy处的RGB值
	public void setRGB(int x, int y, int r, int g, int b) {
		int newRgb = ((r << 16) & 0xff0000) + ((g << 8) & 0xff00) + b;
		if (newRgb > 0) {
			newRgb -= 16777216;
		}
		image.setRGB(x, y, newRgb);
	}
	
	// RGB转XYZ 由于BufferedImage里面只能存放整数的RGB，所以返回一个三维的xyz数组
	public static double[][][] RGB2XYZ() {
		double XYZ[][][] = new double[width][height][3];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int pixel = image.getRGB(i, j);
				double r = ((pixel & 0xff0000) >> 16) * 1.0d;
				double g = ((pixel & 0xff00) >> 8) * 1.0d;
				double b = (pixel & 0xff) * 1.0d;
				r /= 255.0d;
				g /= 255.0d;
				b /= 255.0d;
				double R = gamma(r);
				double G = gamma(g);
				double B = gamma(b);
				double X = 100.0d * (0.436052025d*R + 0.385081593d*G + 0.143087414d*B);
				double Y = 100.0d * (0.222491598d*R + 0.716886060d*G + 0.060621486d*B);
				double Z = 100.0d * (0.013929122d*R + 0.097097002d*G + 0.714185470d*B);
				XYZ[i][j][0] = X;
				XYZ[i][j][1] = Y;
				XYZ[i][j][2] = Z;
			}
		}
		return XYZ;
	}
	
	
	// XYZ 转 Lab
	private static final double Xn = 95.047d;
	private static final double Yn = 100.0d;
	private static final double Zn = 108.88d;
	private static double[][][] XYZ2LAB(double[][][] input){
		double Lab[][][] = new double[width][height][3];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				double X = input[i][j][0];
				double Y = input[i][j][1];
				double Z = input[i][j][2];
				double L = 116 * f(Y / Yn) - 16;
				double a = 500 * (f(X / Xn) - f(Y / Yn));
				double b = 200 * (f(Y / Yn) - f(Z / Zn));
				Lab[i][j][0] = L;
				Lab[i][j][1] = a;
				Lab[i][j][2] = b;
			}
		}
		return Lab;
	}
	private static double f(double t) {
		double flag = Math.pow((6.0d / 29d), 3);
		if (t > flag) {
			return Math.pow(t, 1.0d / 3);
		} else {
			return 1.0d / 3 * Math.pow(29.0d/6, 2) * t + 4.0d / 29;
		}
	}
	
	// RGB转Lab
	public static double[][][] RGB2Lab(){
		double XYZ[][][] = RGB2XYZ();
		double Lab[][][] = XYZ2LAB(XYZ);
		return Lab;
	}
	
	// 获得rgb矩阵
	public static double[][][] getRGBMAtrix(){
		double rgb[][][] = new double[width][height][3];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int pixel = image.getRGB(i, j);
				double r = ((pixel & 0xff0000) >> 16) * 1.0d;
				double g = ((pixel & 0xff00) >> 8) * 1.0d;
				double b = (pixel & 0xff) * 1.0d;
				rgb[i][j][0] = r;
				rgb[i][j][1] = g;
				rgb[i][j][2] = b;
			}
		}
		return rgb;
	}
	
	// 获得rgb矩阵
	public static double[][][] getRGBMAtrixNormalize(){
		double rgb[][][] = getRGBMAtrix();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				rgb[i][j][0] /= 255;
				rgb[i][j][1] /= 255;
				rgb[i][j][2] /= 255;
			}
		}
		return rgb;
	}
	
	public static double[] Lab2RGB(double L, double a, double b) {
		double[] xyz = Lab2XYZ(L, a, b);
		double X = xyz[0];
		double Y = xyz[1];
		double Z = xyz[2];
		double R = 3.240497 * X - 1.537150 * Y - 0.498535 * Z;
		double G = -0.969256 * X + 1.875992 * Y + 0.041556 * Z;
		double B = 0.055648 * X - 0.204043 * Y + 1.057311 * Z;
		double[] RGB = {R, G, B};
		return RGB;
	}
	
	public static double[] Lab2XYZ(double L, double a, double b) {
		double Y = Yn * f2(1.0d / 116.0d * (L + 16));
		double X = Xn * f2(1.0d / 116.0d * (L + 16) + 1.0d / 500.0d * a);
		double Z = Zn * f2(1.0d / 116.0d * (L + 16) - 1.0d / 200.0d * b);
		double[] re = {X, Y, Z};
		return re;
	}
	
	// 用于Lab转RGB
	private static double f2(double t) {
		double thre = 6.0d / 29.0d;
		if (t > thre) {
			return Math.pow(t, 3);
		} else {
			return t * Math.pow(thre, 2) * (t - 4.0d / 29.0d);
		}
	}
	
	public static void main(String[] args) {
		double[] rgb = Lab2RGB(34, -16, 36);
		System.out.println(rgb[0] + " " + rgb[1] + " " + rgb[2]);
	}
	
	

}
