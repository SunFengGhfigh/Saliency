package tool;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import algorithm.BSCA;
import main.Common;

public class BatchGeneration {
	
	public static void main(String[] args) throws Exception {
		String dataset = "MSRA-B";
//		String[] cs = {"HSV", "Lab", "LCH", "RGB", "XYZ", "YCbCr", "YIQ", "YUV", "HED"};
		String[] cs = {"HED"};
		for (String color_space : cs) {
			String root = "D://MySaliency//Dataset//" + dataset + "//RGB//";
			Common.current_color_space = color_space;
			File[] files = getFiles(root);
			String out_put_path = "D://MySaliency//Dataset//" + dataset + "//" + color_space + "//";
			if (color_space.equals("RGB")) {
				out_put_path = "D://MySaliency//Dataset//" + dataset + "//RGB2//";
			}
			for (int i = 0; i < files.length; i++) {
				BufferedImage img = ImageIO.read(files[i]);
				img = BSCA.deal(img);
				ImageIO.write(img, "png", new File(out_put_path + files[i].getName().replace(".jpg", ".png")));
				System.out.println(color_space + "-->" + i);
			}
		}
	}
	
	private static File[] getFiles(String path){
		File file = new File(path);
		File[] files = file.listFiles();
		return files;
	}
	
}
