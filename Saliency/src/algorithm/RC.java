package algorithm;

import java.awt.image.BufferedImage;
import java.util.List;

import tool.Num;
import tool.SImage;

public class RC {
	
	public static BufferedImage deal(BufferedImage input) {
		input = SLIC.deal(input);
		SImage image = new SImage(input);
		int width = image.width;
		int height = image.height;
		List<Cluster> clusters = SLIC.getClusters();
		double[] cluster_dis = new double[clusters.size()];
		
		for (int i = 0; i < clusters.size(); i++) {
			double temp_dis = 0;
			for (int j = 0; j < clusters.size(); j++) {
				if (i == j) continue;
				temp_dis += dis(clusters.get(i), clusters.get(j));
			}
			cluster_dis[i] = temp_dis;
		}
		
		double t = Num.max(cluster_dis) - Num.min(cluster_dis);
		cluster_dis = Num.sub(cluster_dis, Num.min(cluster_dis));
		cluster_dis = Num.div(cluster_dis, t);
		cluster_dis = Num.mul(cluster_dis, 255);
		
		for (int i = 0; i < clusters.size(); i++) {
			Cluster cluster = clusters.get(i);
			for (int j = 0; j < cluster.pixels.size(); j++) {
				Position p = cluster.pixels.get(j);
				int c = (int)cluster_dis[i];
				image.setRGB(p.w, p.h, c, c, c);
			}
		}
		
		return image.getImage();
	}
	
	private static double dis(Cluster p1, Cluster p2) {
		double L1 = p1.l;
		double L2 = p2.l;
		double a1 = p1.a;
		double a2 = p2.a;
		double b1 = p1.b;
		double b2 = p2.b;
		double Dc = Math.sqrt(Math.pow(L1-L2, 2) + Math.pow(a1-a2, 2) + Math.pow(b1-b2, 2));
		return Dc;
	}

}
