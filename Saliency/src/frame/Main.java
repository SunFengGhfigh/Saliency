package frame;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class Main {
	
	public static void main(String[] args) {
EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				JFrame frame = new MainFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
			
		});
	}

}
