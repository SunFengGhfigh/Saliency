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
	
	// ��ά���������ǰ�߼�����
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
	
	// ������a���ҵ���Ŀ��cһ��ֵ����������������������鷵��
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
	
	// �ϲ�����һά����
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
	
	// �ϲ�������ά����
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
	
	// ȥ���ظ�������
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

	// ȡ�������д���c�Ĵ���
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
	
	// �ҳ�������С��v��ֵ
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
	
	// �ҳ�������С��v��ֵ
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
	
	// ���һ��ָ����С��ȫΪ1������
	public static int[][] ones(int a, int b) {
		int[][] re = new int[a][b];
		for (int i = 0; i < a; i++) {
			for (int j = 0; j < b; j++) {
				re[i][j] = 1;
			}
		}
		return re;
	}
	
	// ����ά����ĳһ�и�ֵ
	public static int[][] assign_column(int[][] a, int c, int v){
		for (int i = 0; i < a.length; i++) {
			a[i][c] = v;
		}
		return a;
	}
	
	// ����ά����ĳһ�и�ֵ����ֵ������һ������
	public static int[][] assign_column(int[][] a, int c, int[] v){
		for (int i = 0; i < a.length; i++) {
			a[i][c] = v[i];
		}
		return a;
	}
	
	// ��ά����ĺͣ������
	public static double[] sum(double[][] a) {
		double[] re = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				re[i] += a[i][j];
			}
		}
		return re;
	}
	
	// ��ά������ÿһ��ֵ��ƽ��
	public static double[][] square(double[][] a){
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				a[i][j] *= a[i][j];
			}
		}
		return a;
	}
	
	// һά���飬�������ֿ���
	public static double[] sqrt(double[] a) {
		for (int i = 0; i < a.length; i++) {
			a[i] = Math.sqrt(a[i]);
		}
		return a;
	}
	
	// һά���飬��һ��
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
	
	// һά���飬��eΪ�׵Ĵη�
	public static double[] exp(double[] a) {
		for (int i = 0; i < a.length; i++) {
			a[i] = Math.pow(Math.E, a[i]);
		}
		return a;
	}
	
	// �ԽǾ�������
	public static double[][] inv(double[][] a){
		for (int i = 0; i < a.length; i++) {
			a[i][i] = 1.0d / a[i][i];
 		}
		return a;
	}
	
	// �������
	public static double[][] mul(double[][] a, double[][] b){
		if (a[0].length != b.length) {
			System.err.println("������˳���");
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
	
	// ��ȡ��ά����ĵ�r+1��
	private static double[] row(double[][] a, int r) {
		double[] re = new double[a[0].length];
		for (int i = 0; i < a[0].length; i++) {
			re[i] = a[r][i];
		}
		return re;
	}
	
	// ��ȡ��ά����ĵ�c+1��
	private static double[] cloumn(double[][] a, int c) {
		double[] re = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			re[i] = a[i][c];
		}
		return re;
	}
	
	// ���ھ������
	private static double matrix_sub_mul(double[] a, double[] b) {
		double sum = 0d;
		for (int i = 0; i < a.length; i++) {
			sum += (a[i] * b[i]);
		}
		return sum;
	}
	
	// ����һ��һά����
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
	
	// �ӷ�
	public static double[] add(double[] a, double v) {
		for (int i = 0; i < a.length; i++) {
			a[i] += v;
		}
		return a;
	}
	
	// ת��
	public static double[][] T(double[][] a){
		double[][] b = new double[a[0].length][a.length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[0].length; j++) {
				b[j][i] = a[i][j];
			}
		}
		return b;
	}
	
	// ת��
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
	
	// �������
	public static double[][] add(double[][] a, double[][] b){
		if (a.length != b.length || a[0].length != b[0].length) {
			System.err.println("������ӳ���");
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
