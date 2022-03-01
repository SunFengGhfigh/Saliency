package frame;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import algorithm.BFS;
import algorithm.BL_weak_saliency;
import algorithm.BSCA;
import algorithm.Grey;
import algorithm.HC;
import algorithm.LC;
import algorithm.Naive_LBP;
import algorithm.RC;
import algorithm.SLIC;
import main.Common;
import tool.B;
import tool.G;
import tool.Gamma;
import tool.Gaussian_Blur;
import tool.HOG;
import tool.Pink;
import tool.R;

public class MainFrame extends JFrame{
	
	private BufferedImage image;
	private Image img;
	private BufferedImage image2;
	private Image img2;
	private static final int DEFAULT_WIDTH = 1226;
	private static final int DEFAULT_HEIGHT = 689;
	private static String file_name;
	
	public MainFrame() {
		setTitle("Saliency");
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		
		// 添加画板
		add(new JComponent() {
			public void paintComponent(Graphics g) {
				if (image != null) {
					g.drawImage(image, 100, 100, null);
				}
				if (image2 != null) {
					g.drawImage(image2, 700, 100, null);
				}
			}
			
		});
		
		// 设置菜单栏
		JMenuBar menuBar = new JMenuBar();
		
		// 文件菜单
		JMenu fileMenu = new JMenu("File");
		// 打开文件
		JMenuItem openItem = new JMenuItem("Open");
		openItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				openFile();
			}
			
		});
		
		// 保存
		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
			
		});
		
		// 算法菜单
		JMenu alMenu = new JMenu("Algorithm");
		// 灰度化
		JMenuItem greyItem = new JMenuItem("Grey");
		greyItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				image2 = Grey.deal(image2);
				repaint();
			}
		});
		
		// LC算法 最差算法
		JMenuItem lcItem = new JMenuItem("LC");
		lcItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				long startTime =  System.currentTimeMillis();
				image2 = LC.deal(image2);
				long endTime =  System.currentTimeMillis();
				long usedTime = (endTime-startTime);
				setTitle("Current Color Space: " + Common.current_color_space + " Time: " + usedTime + "ms");
				repaint();
			}
		});
		
		// HC算法 是LC的升级版
		JMenuItem hcItem = new JMenuItem("HC");
		hcItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				long startTime =  System.currentTimeMillis();
				image2 = HC.deal(image2);
				long endTime =  System.currentTimeMillis();
				long usedTime = (endTime-startTime);
				setTitle("Current Color Space: " + Common.current_color_space + " Time: " + usedTime + "ms");
				repaint();
			}
		});
		
		// 超像素分割
		JMenuItem SLICItem = new JMenuItem("SLIC");
		SLICItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				long startTime =  System.currentTimeMillis();
				image2 = SLIC.deal(image2);
				long endTime =  System.currentTimeMillis();
				long usedTime = (endTime-startTime);
				setTitle("Current Color Space: " + Common.current_color_space + " Time: " + usedTime + "ms");
				repaint();
			}
		});
		
		
		// RC 是基于区域的算法
		JMenuItem rcItem = new JMenuItem("RC");
		rcItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				long startTime =  System.currentTimeMillis();
				image2 = RC.deal(image2);
				long endTime =  System.currentTimeMillis();
				long usedTime = (endTime-startTime);
				setTitle("Current Color Space: " + Common.current_color_space + " Time: " + usedTime + "ms");
				repaint();
			}
		});
		
		// BSCA
		JMenuItem bscaItem = new JMenuItem("BSCA");
		bscaItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				long startTime =  System.currentTimeMillis();
				image2 = BSCA.deal(image2);
				long endTime =  System.currentTimeMillis();
				long usedTime = (endTime-startTime);
				setTitle("Current Color Space: " + Common.current_color_space + " Time: " + usedTime + "ms");
				repaint();
			}
		});
		
		// BFS
		JMenuItem bfsItem = new JMenuItem("BFS");
		bfsItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				long startTime =  System.currentTimeMillis();
				image2 = BFS.deal(image2);
				long endTime =  System.currentTimeMillis();
				long usedTime = (endTime-startTime);
				setTitle("Current Color Space: " + Common.current_color_space + " Time: " + usedTime + "ms");
				repaint();
			}
		});
		
		// BL_weak_saliency
		JMenuItem bl_weak_Item = new JMenuItem("Initial Saliency");
		bl_weak_Item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				long startTime =  System.currentTimeMillis();
				image2 = BL_weak_saliency.deal(image2);
				long endTime =  System.currentTimeMillis();
				long usedTime = (endTime-startTime);
				setTitle("Current Color Space: " + Common.current_color_space + " Time: " + usedTime + "ms");
				repaint();
			}
		});
		
		// 工具菜单
		JMenu toolMenu = new JMenu("Tool");
		// 朴素LBP 二进制局部模式
		JMenuItem naive_LBP_Item = new JMenuItem("Naive LBP");
		naive_LBP_Item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				image2 = Naive_LBP.deal(image2);
				repaint();
			}
		});
		
		// 高斯模糊
		JMenuItem gaussian_blur_Item = new JMenuItem("Gaussian Blur");
		gaussian_blur_Item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				image2 = Gaussian_Blur.deal(image2);
				repaint();
			}
		});
		
		JMenuItem R_Item = new JMenuItem("R");
		R_Item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				image2 = R.deal(image2);
				repaint();
			}
		});
		
		JMenuItem G_Item = new JMenuItem("G");
		G_Item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				image2 = G.deal(image2);
				repaint();
			}
		});
		
		JMenuItem B_Item = new JMenuItem("B");
		B_Item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				image2 = B.deal(image2);
				repaint();
			}
		});
		
		JMenuItem Pink_Item = new JMenuItem("Pink");
		Pink_Item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				image2 = Pink.deal(image2);
				repaint();
			}
		});
		
		JMenuItem Gamma_Item = new JMenuItem("Gamma 0.5");
		Gamma_Item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				image2 = Gamma.deal(image2);
				repaint();
			}
		});
		
		JMenuItem HOG_Item = new JMenuItem("HOG");
		HOG_Item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				image2 = HOG.deal(image2);
				repaint();
			}
		});
		
		JMenu csMenu = new JMenu("Color Space");
		JMenuItem RGB_Item = new JMenuItem("RGB");
		RGB_Item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Common.current_color_space = "RGB";
				setTitle("Current Color Space: " + Common.current_color_space);
			}
		});
		
		JMenuItem Lab_Item = new JMenuItem("CIELab");
		Lab_Item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Common.current_color_space = "Lab";
				setTitle("Current Color Space: " + Common.current_color_space);
			}
		});
		
		JMenuItem XYZ_Item = new JMenuItem("XYZ");
		XYZ_Item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Common.current_color_space = "XYZ";
				setTitle("Current Color Space: " + Common.current_color_space);
			}
		});
		
		JMenuItem HSV_Item = new JMenuItem("HSV");
		HSV_Item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Common.current_color_space = "HSV";
				setTitle("Current Color Space: " + Common.current_color_space);
			}
		});
		
		JMenuItem YUV_Item = new JMenuItem("YUV");
		YUV_Item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Common.current_color_space = "YUV";
				setTitle("Current Color Space: " + Common.current_color_space);
			}
		});
		
		JMenuItem YIQ_Item = new JMenuItem("YIQ");
		YIQ_Item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Common.current_color_space = "YIQ";
				setTitle("Current Color Space: " + Common.current_color_space);
			}
		});
		
		JMenuItem YCbCr_Item = new JMenuItem("YCbCr");
		YCbCr_Item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Common.current_color_space = "YCbCr";
				setTitle("Current Color Space: " + Common.current_color_space);
			}
		});
		
		JMenuItem LCH_Item = new JMenuItem("LCH");
		LCH_Item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Common.current_color_space = "LCH";
				setTitle("Current Color Space: " + Common.current_color_space);
			}
		});
		
		JMenu dbMenu = new JMenu("Database");
		JMenuItem MSRAB_Item = new JMenuItem("MSRA-B");
		MSRAB_Item.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Common.current_database = "MSRA-B";
			}
		});
		
		JMenuItem ECSSD_Item = new JMenuItem("ECSSD");
		ECSSD_Item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Common.current_database = "ECSSD";
			}
		});
		
		JMenuItem HKUIS_Item = new JMenuItem("HKU-IS");
		HKUIS_Item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Common.current_database = "HKU-IS";
			}
		});
		
		JMenuItem PASCALS_Item = new JMenuItem("PASCAL-S");
		PASCALS_Item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Common.current_database = "PASCAL-S";
			}
		});
		
		JMenuItem DUTOMRON_Item = new JMenuItem("DUT-OMRON");
		DUTOMRON_Item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Common.current_database = "DUT-OMRON";
			}
		});
		
		
		fileMenu.add(openItem);
		fileMenu.add(saveItem);
		alMenu.add(lcItem);
		alMenu.add(hcItem);
		alMenu.add(rcItem);
		alMenu.add(bscaItem);
		alMenu.add(bfsItem);
		alMenu.add(bl_weak_Item);
		toolMenu.add(greyItem);
		toolMenu.add(SLICItem);
		toolMenu.add(naive_LBP_Item);
		toolMenu.add(gaussian_blur_Item);
		toolMenu.add(R_Item);
		toolMenu.add(G_Item);
		toolMenu.add(B_Item);
		toolMenu.add(Pink_Item);
		toolMenu.add(Gamma_Item);
		toolMenu.add(HOG_Item);
		csMenu.add(RGB_Item);
		csMenu.add(Lab_Item);
		csMenu.add(XYZ_Item);
		csMenu.add(HSV_Item);
		csMenu.add(YUV_Item);
		csMenu.add(YIQ_Item);
		csMenu.add(YCbCr_Item);
		csMenu.add(LCH_Item);
		dbMenu.add(MSRAB_Item);
		dbMenu.add(ECSSD_Item);
		dbMenu.add(HKUIS_Item);
		dbMenu.add(PASCALS_Item);
		dbMenu.add(DUTOMRON_Item);
		menuBar.add(fileMenu);
		menuBar.add(csMenu);
		menuBar.add(dbMenu);
		menuBar.add(toolMenu);
		menuBar.add(alMenu);
		setJMenuBar(menuBar);
		
	}
	
	// 打开文件
	public void openFile() {
		JFileChooser chooser = new JFileChooser();
		// 默认目录
		switch (Common.current_database) {
			case "MSRA-B": Common.IMAGE_FILE_LIST = "D://Saliency//Dataset//MSRA-B//RGB//"; break;
			case "ECSSD": Common.IMAGE_FILE_LIST = "D://Saliency//Dataset//ECSSD//RGB//"; break;
			case "HKU-IS": Common.IMAGE_FILE_LIST = "D://Saliency//Dataset//HKU-IS//RGB//"; break;
			case "PASCAL-S": Common.IMAGE_FILE_LIST = "D://Saliency//Dataset//PASCAL-S//RGB//"; break;
			case "DUT-OMRON": Common.IMAGE_FILE_LIST = "D://Saliency//Dataset//DUT-OMRON//RGB//"; break;
		}
		chooser.setCurrentDirectory(new File(Common.IMAGE_FILE_LIST));
		String[] extensions = ImageIO.getReaderFileSuffixes();
		chooser.setFileFilter(new FileNameExtensionFilter("Image files", extensions));
		int r = chooser.showOpenDialog(this);
		if (r != JFileChooser.APPROVE_OPTION) {
			return;
		}
		// 显示于主页
		try {
			img = ImageIO.read(chooser.getSelectedFile());
			img2 = ImageIO.read(chooser.getSelectedFile());
			Common.FILE_NAME = chooser.getSelectedFile().getName();
			file_name = chooser.getSelectedFile().getName();
			image = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
			image.getGraphics().drawImage(img, 0, 0, null);
			image2 = new BufferedImage(img2.getWidth(null), img2.getHeight(null), BufferedImage.TYPE_INT_RGB);
			image2.getGraphics().drawImage(img2, 0, 0, null);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e);
		}
		// 更新画板
		repaint();
	}
	
	/**
	 * 保存文件
	 */
	public void saveFile() {
		try {
			ImageIO.write(image2, "png", new File("D:/" + file_name.replace(".jpg", ".png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
