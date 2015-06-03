package pathfinding;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.FontRenderContext;

import javax.swing.JPanel;

public abstract class Game {
	
	private GraphicsManager gm;
	private int width = 1024;
	private int height = 768;
	private Point origin;
	private Graphics2D g;
	
	private long lastUpdateTime;
	private long currentTime;
	private int elapsedTime;
	
	public void setTitle(String title){
		gm.setTitle(title);
	}
	
	public void start(){
		init();
		loop();
	}
	
	public void init(){
		gm = new GraphicsManager();
		origin = new Point(gm.getWidth()/2 - width/2, gm.getHeight()/2 - height/2);
		
	//Set up input panel
		JPanel inputPanel = new JPanel();
		inputPanel.setSize(width, height);
		inputPanel.setLocation(origin);
		inputPanel.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				click(e);
			}
			@Override
			public void mouseReleased(MouseEvent e){
				unclick(e);
			}
		});
		inputPanel.addMouseMotionListener(new MouseMotionAdapter(){
			@Override
			public void mouseDragged(MouseEvent e){
				drag(e);
			}
			@Override
			public void mouseMoved(MouseEvent e){
				move(e);
			}
		});
		gm.getFrame().addKeyListener(new KeyListener(){
			@Override
			public void keyTyped(KeyEvent e) {
				type(e);
			}
			@Override
			public void keyPressed(KeyEvent e) {
				press(e);
			}
			@Override
			public void keyReleased(KeyEvent e) {
				release(e);
			}
		});
				
	//Add input panel to frame
		gm.getFrame().setContentPane(inputPanel);
		final JPanel inputPanelFinal = inputPanel;
		gm.getFrame().addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent e) {
				inputPanelFinal.setLocation(origin);
			}
			@Override public void focusLost(FocusEvent e) {}
		});
		
		lastUpdateTime = System.currentTimeMillis();
	}
	
	private void loop(){
		while (true){
			g = gm.getGraphics();
			gm.clear(g);
			render(g);
			g.dispose();
			update();
			try{
				Thread.sleep(20);
			} catch(InterruptedException ex) { }
		}
	}
	
	public abstract void render(Graphics2D g);
	
	public void update(){
		currentTime = System.currentTimeMillis();
		elapsedTime = (int)(currentTime - lastUpdateTime);
		lastUpdateTime = currentTime;
		gm.update();
	}
	
	public abstract void click(MouseEvent e);
	public abstract void unclick(MouseEvent e);
	public abstract void drag(MouseEvent e);
	public abstract void move(MouseEvent e);
	public abstract void type(KeyEvent e);
	public abstract void press(KeyEvent e);
	public abstract void release(KeyEvent e);
	
	public FontRenderContext getFontRenderContext(){ //TODO in use?
		Graphics2D g = gm.getGraphics();
		FontRenderContext frc = g.getFontRenderContext();
		g.dispose();
		return frc;
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getElapsedTime() {
		return elapsedTime;
	}

	public Point getOrigin() {
		return origin;
	}
	
}
