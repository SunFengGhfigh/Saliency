package tool;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;


public class Gaussian_Blur {
	
	private static BufferedImage soucr_image;
	
	public static BufferedImage deal(BufferedImage input) {
		soucr_image = input;
		float[] elements = {0.0947416f, 0.118318f, 0.0947416f, 0.118318f, 0.147761f, 0.118318f, 0.0947416f, 0.118318f, 0.0947416f};
		convolve(elements);
		return soucr_image;
	}
	
	private static void convolve(float[] elements) {
		Kernel kernel = new Kernel(3, 3, elements);
		ConvolveOp op = new ConvolveOp(kernel);
		filter(op);
	}
	
	private static void filter(BufferedImageOp op) {
		soucr_image = op.filter(soucr_image, null);
	}

}
