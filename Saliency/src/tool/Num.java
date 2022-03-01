package tool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Num {

	// MIN VALUE
	public static int min(int a[]) {
		int min  = a[0];
		for (int i = 1; i < a.length; i++) {
			if (a[i] < min) {
				min = a[i];
			}
		}
		return min;
	}
	
	private static double min(double[][] a) {
		double min = a[0][0];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				double v = a[i][j];
				if (v < min) min = v;
			}
		}
		return min;
	}
	
	public static double min(double a[]) {
		double min  = a[0];
		for (int i = 1; i < a.length; i++) {
			if (a[i] < min) {
				min = a[i];
			}
		}
		return min;
	}
	
	public static double min(int start, double a[]) {
		double min  = a[start];
		for (int i = start; i < a.length; i++) {
			if (a[i] < min) {
				min = a[i];
			}
		}
		return min;
	}
	
	// MAX VALUE
	public static int max(int a[]) {
		int max  = a[0];
		for (int i = 1; i < a.length; i++) {
			if (a[i] > max) {
				max = a[i];
			}
		}
		return max;
	}
	
	public static int max(int a[][]) {
		int width = a.length;
		int height = a[0].length;
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (a[i][j] > max) max = a[i][j];
			}
		}
		return max;
	}
	
	public static double max(double a[]) {
		double max  = a[0];
		for (int i = 1; i < a.length; i++) {
			if (a[i] > max) {
				max = a[i];
			}
		}
		return max;
	}
	
	public static double max(int start, double a[]) {
		double max  = a[start];
		for (int i = start; i < a.length; i++) {
			if (a[i] > max) {
				max = a[i];
			}
		}
		return max;
	}
	
	// SUB
	public static int[] sub(int a[], int c) {
		for (int i = 0; i < a.length; i++) {
			a[i] -= c;
		}
		return a;
	}
	
	public static double[] sub(double a[], double c) {
		for (int i = 0; i < a.length; i++) {
			a[i] -= c;
		}
		return a;
	}
	
	// 二维数组减法，前者剪后者
	public static double[][] sub(double[][] a, double[][] b){
		int len = a.length;
		double[][] re = new double[a.length][a[0].length];
		for (int i = 0; i < len; i++) {
			for (int j = 0; j < a[0].length; j++) {
				re[i][j] = a[i][j] - b[i][j];
			}
		}
		return re;
	}
	
	public static double[] sub(int start, double a[], double c) {
		for (int i = start; i < a.length; i++) {
			a[i] -= c;
		}
		return a;
	}
	
	// DIV
	public static int[] div(int a[], int c) {
		for (int i = 0; i < a.length; i++) {
			a[i] /= c;
		}
		return a;
	}
	
	public static double[] div(double a[], double c) {
		for (int i = 0; i < a.length; i++) {
			a[i] /= c;
		}
		return a;
	}
	
	public static double[] div(int start, double a[], double c) {
		for (int i = start; i < a.length; i++) {
			a[i] /= c;
		}
		return a;
	}
	
	// MUL
	public static int[] mul(int a[], int c) {
		for (int i = 0; i < a.length; i++) {
			a[i] *= c;
		}
		return a;
	}
	
	public static double[] mul(double a[], double c) {
		for (int i = 0; i < a.length; i++) {
			a[i] *= c;
		}
		return a;
	}
	
	public static double[] mul(int start, double a[], double c) {
		for (int i = start; i < a.length; i++) {
			a[i] *= c;
		}
		return a;
	}
	
	// SUM
	public static int sum(int a[]) {
		int sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += a[i];
		}
		return sum;
	}
	
	public static double sum(double a[]) {
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += a[i];
		}
		return sum;
	}
	
	// 从数组a中找到和目标c一样值的索引，将索引打包成数组返回
	public static int[] find(int[] a, int c) {
		int len = a.length;
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < len; i++) {
			if (a[i] == c) list.add(i);
		}
		int[] re = new int[list.size()];
		for (int i = 0; i < re.length; i++) {
			re[i] = list.get(i);
		}
		return re;
	}
	
	// 合并两个一维数组
	public static int[] merge(int[] a, int[] b) {
		int a_len = a.length;
		int b_len = b.length;
		int[] re = new int[a_len + b_len];
		for (int i = 0; i < a_len; i++) {
			re[i] = a[i];
		}
		for (int i = 0; i < b_len; i++) {
			re[a_len+i] = b[i];
		}
		return re;
	}
	
	// 合并两个二维数组
	public static int[][] merge(int[][] a, int [][] b){
		int a_len = a.length;
		int b_len = b.length;
		int[][] re = new int[a_len + b_len][b[0].length];
		for (int i = 0; i < a_len; i++) {
			for (int j = 0; j < b[0].length; j++) {
				re[i][j] = a[i][j];
			}
		}
		for (int i = 0; i < b_len; i++) {
			for (int j = 0; j < b[0].length; j++) {
				re[a_len+i][j] = b[i][j];
			}
		}
		return re;
	}
	
	// 去除重复的数字
	public static int[] unique(int[] a) {
		HashSet<Integer> set = new HashSet<>();
		for (int i = 0; i < a.length; i++) {
			set.add(a[i]);
		}
		int[] re = new int[set.size()];
		int i = 0;
		for (Integer num : set) {
			re[i++] = num;
		}
		return re;
	}

	// 取出数组中大于c的存在
	public static int[] arrays_greater_than(int[] a, int c) {
		ArrayList<Integer> list = new ArrayList<>();
		for (int i = 0; i < a.length; i++) {
			if (a[i] > c) {
				list.add(a[i]);
			}
		}
		int[] re = new int[list.size()];
		for (int i = 0; i < re.length; i++) {
			re[i] = list.get(i);
		}
		return re;
	}
	
	// 找出数组中小于v的值
	public static int[] find_less_than(double[] a, double v) {
		ArrayList<Integer> list = new ArrayList<>();
		for (int i = 0; i < a.length; i++) {
			if (a[i] < v) {
				list.add(i);
			}
		}
		int[] re = new int[list.size()];
		for (int i = 0; i < re.length; i++) {
			re[i] = list.get(i);
		}
		return re;
	}
	
	// 找出数组中小于v的值
	public static int[] find_greater_than(double[] a, double v) {
		ArrayList<Integer> list = new ArrayList<>();
		for (int i = 0; i < a.length; i++) {
			if (a[i] > v) {
				list.add(i);
			}
		}
		int[] re = new int[list.size()];
		for (int i = 0; i < re.length; i++) {
			re[i] = list.get(i);
		}
		return re;
	}
	
	// 获得一个指定大小的全为1的数组
	public static int[][] ones(int a, int b) {
		int[][] re = new int[a][b];
		for (int i = 0; i < a; i++) {
			for (int j = 0; j < b; j++) {
				re[i][j] = 1;
			}
		}
		return re;
	}
	
	// 给二维数组某一列赋值
	public static int[][] assign_column(int[][] a, int c, int v){
		for (int i = 0; i < a.length; i++) {
			a[i][c] = v;
		}
		return a;
	}
	
	// 给二维数组某一列赋值，赋值内容是一个数组
	public static int[][] assign_column(int[][] a, int c, int[] v){
		for (int i = 0; i < a.length; i++) {
			a[i][c] = v[i];
		}
		return a;
	}
	
	// 二维数组的和，行求和
	public static double[] sum(double[][] a) {
		double[] re = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				re[i] += a[i][j];
			}
		}
		return re;
	}
	
	// 二维数组中每一个值都平方
	public static double[][] square(double[][] a){
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				a[i][j] *= a[i][j];
			}
		}
		return a;
	}
	
	// 一维数组，所有数字开方
	public static double[] sqrt(double[] a) {
		for (int i = 0; i < a.length; i++) {
			a[i] = Math.sqrt(a[i]);
		}
		return a;
	}
	
	// 一维数组，归一化
	public static double[] normalize(double[] a) {
		Double min = Double.MAX_VALUE;
		Double max = Double.MIN_VALUE;
		for (int i = 0; i < a.length; i++) {
			if (a[i] > max) max = a[i];
			if (a[i] < min) min = a[i];
		}
		for (int i = 0; i < a.length; i++) {
			a[i] = (a[i]-min)/(max-min);
		}
		return a;
	}
	
	public static double[][] normalize(double[][] a) {
		Double min = Double.MAX_VALUE;
		Double max = Double.MIN_VALUE;
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				if (a[i][j] > max) max = a[i][j];
				if (a[i][j] < min) min = a[i][j];
			}
		}
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				a[i][j] -= min;
				a[i][j] /= (max - min);
			}
		}
		return a;
	}
	
	// 一维数组，以e为底的次方
	public static double[] exp(double[] a) {
		for (int i = 0; i < a.length; i++) {
			a[i] = Math.pow(Math.E, a[i]);
		}
		return a;
	}
	
	// 对角矩阵求逆
	public static double[][] inv(double[][] a){
		for (int i = 0; i < a.length; i++) {
			a[i][i] = 1.0d / a[i][i];
 		}
		return a;
	}
	
	// 矩阵相乘
	public static double[][] mul(double[][] a, double[][] b){
		if (a[0].length != b.length) {
			System.err.println("矩阵相乘出错");
		}
		double[][] re = new double[a.length][b[0].length];
		for (int i = 0; i < re.length; i++) {
			double[] r = row(a, i);
			for (int j = 0; j < b[0].length; j++) {
				double[] c = cloumn(b, j);
				double temp = matrix_sub_mul(r, c);
				re[i][j] = temp;
			}
		}
		return re;
	}
	
	// 获取二维数组的第r+1行
	private static double[] row(double[][] a, int r) {
		double[] re = new double[a[0].length];
		for (int i = 0; i < a[0].length; i++) {
			re[i] = a[r][i];
		}
		return re;
	}
	
	// 获取二维数组的第c+1列
	private static double[] cloumn(double[][] a, int c) {
		double[] re = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			re[i] = a[i][c];
		}
		return re;
	}
	
	// 用于矩阵相乘
	private static double matrix_sub_mul(double[] a, double[] b) {
		double sum = 0d;
		for (int i = 0; i < a.length; i++) {
			sum += (a[i] * b[i]);
		}
		return sum;
	}
	
	// 返回一个一维数组
	public static double[] max(double[][] a) {
		double re[] = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			double max = Double.MIN_VALUE;
			for (int j = 0; j < a[0].length; j++){
				if (a[i][j] > max) max = a[i][j];
			}
			re[i] = max;
		}
		return re;
	}
	
	// 加法
	public static double[] add(double[] a, double v) {
		for (int i = 0; i < a.length; i++) {
			a[i] += v;
		}
		return a;
	}
	
	// 转置
	public static double[][] T(double[][] a){
		double[][] b = new double[a[0].length][a.length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				b[j][i] = a[i][j];
			}
		}
		return b;
	}
	
	// 转置
	public static int[][] T(int[][] a){
		int[][] b = new int[a[0].length][a.length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				b[j][i] = a[i][j];
			}
		}
		return b;
	}

	public static double[][] I(int size){
		double[][] I = new double[size][size];
		for (int i = 0; i < size; i++) {
			I[i][i] = 1;
		}
		return I;
	}
	
	// 矩阵相加
	public static double[][] add(double[][] a, double[][] b){
		if (a.length != b.length || a[0].length != b[0].length) {
			System.err.println("矩阵相加出错");
			return null;
		}
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				a[i][j] += b[i][j];
			}
		}
		return a;
	}
	
	public static int[] setdiff(int num, int[] a) {
		int[] b = new int[num];
		for (int i = 0; i < a.length; i++) {
			b[a[i]] = 1;
		}
		int[] re = new int[num-a.length];
		int reid = 0;
		for (int i = 0; i < b.length; i++) {
			if (b[i] == 0) re[reid++] = i;
		}
		return re;
	}
	
}
