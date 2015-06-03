package pathfinding;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

public class GraphicsManager {
	
	private GraphicsDevice device;
	private JFrame frame;
	private int width;
	private int height;
	
	public GraphicsManager(){
		device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		frame = new JFrame();
		frame.setUndecorated(true);
		frame.setIgnoreRepaint(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		device.setFullScreenWindow(frame);
		frame.setVisible(true);
		frame.createBufferStrategy(2);
		width = device.getFullScreenWindow().getWidth();
		height = device.getFullScreenWindow().getHeight();
	}
	
	public Graphics2D getGraphics(){
		return (Graphics2D)device.getFullScreenWindow().getBufferStrategy().getDrawGraphics();
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public JFrame getFrame(){
		return frame;
	}
	
	public void setTitle(String title){
		frame.setTitle(title);
	}
	
	public void update(){
		BufferStrategy strategy = device.getFullScreenWindow().getBufferStrategy();
		if (!strategy.contentsLost()){
			strategy.show();
		}
		Toolkit.getDefaultToolkit().sync();
	}
	
	public void clear(Graphics2D g){
		g.setBackground(Color.BLACK);
		g.clearRect(0, 0, width, height);
	}

}
