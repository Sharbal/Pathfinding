package backup;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class CaptureTheFlag extends Game{
	
	private Color bgColor;
	
	private Player player;
	private boolean moveLeft, moveRight, moveUp, moveDown;
	
	private ArrayList<Enemy> enemies;
	private ArrayList<Hazard> hazards;
	
	public static void main(String[] args){
		new CaptureTheFlag().start();
	}
	
	@Override
	public void init(){
		super.init();
		setTitle("Pathfinding in Two Dimensions");
		
		bgColor = new Color(48, 216, 98);
		
		hazards = new ArrayList<Hazard>();
		enemies = new ArrayList<Enemy>();
		
		player = new Player(0, 0, 5, 5, 20);
		
		enemies.add(new Enemy(getWidth()/2, getHeight()/2, 2, 10, 20));
		enemies.add(new Enemy(getWidth(), getHeight(), 1, 15, 30));
		
		hazards.add(new Hazard(250, 500, 25, 50));
		hazards.add(new Hazard(500, 500, 50, 100));
		
		hazards.add(player);
		for (Enemy e : enemies) hazards.add(e);
		
	}
	
	@Override
	public void update(){
		super.update();
				
		if (moveLeft) player.moveLeft();
		if (moveRight) player.moveRight();
		if (moveUp) player.moveUp();
		if (moveDown) player.moveDown();
		
		for (Enemy e : enemies){
			e.moveLinear(player.getX(), player.getY(), hazards);
		}
		
		//addEnemy();
	}
	
	@Override
	public void render(Graphics2D g){
		g.setColor(bgColor);
		g.fillRect(getOrigin().x, getOrigin().y, getWidth(), getHeight());
		for (Hazard h : hazards){
			float r = h.getRadius();
			if (h.getClass() == Enemy.class){
				g.setColor(Color.black);
				g.fillOval(getOrigin().x + (int)(h.getX() - r/2),
						getOrigin().y + (int)(h.getY() - r/2),
						(int)r, (int)r);
			}
			else if (h.getClass() == Player.class){
				g.setColor(Color.blue);
				g.fillOval(getOrigin().x + (int)(h.getX() - r/2),
						getOrigin().y + (int)(h.getY() - r/2),
						(int)r, (int)r);
			}
			else{
				g.setColor(Color.red);
				g.drawOval(getOrigin().x + (int)(h.getX() - r/2),
						getOrigin().y + (int)(h.getY() - r/2),
						(int)r, (int)r);
			}
			
		}
	}
	
	@Override
	public void click(MouseEvent e){
		//TODO
	}
	
	@Override
	public void unclick(MouseEvent e){
		//TODO
	}
	
	@Override
	public void drag(MouseEvent e){
		//TODO
	}
	
	@Override
	public void move(MouseEvent e) {
		//TODO
	}

	@Override
	public void type(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void press(KeyEvent e) {
		switch (e.getKeyCode()){
		case KeyEvent.VK_LEFT:
			moveLeft = true;
			break;
		case KeyEvent.VK_RIGHT:
			moveRight = true;
			break;
		case KeyEvent.VK_UP:
			moveUp = true;
			break;
		case KeyEvent.VK_DOWN:
			moveDown = true;
			break;
		}
	}

	@Override
	public void release(KeyEvent e) {
		switch (e.getKeyCode()){
		case KeyEvent.VK_LEFT:
			moveLeft = false;
			break;
		case KeyEvent.VK_RIGHT:
			moveRight = false;
			break;
		case KeyEvent.VK_UP:
			moveUp = false;
			break;
		case KeyEvent.VK_DOWN:
			moveDown = false;
			break;
		}
	}
	
	private BufferedImage readImage(String filepath){
		try {
			return ImageIO.read(new File(filepath));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void addEnemy(){
		Enemy en = new Enemy(getWidth()/2, getHeight()/2, 2, 10, 20);
		enemies.add(en);
		hazards.add(en);
	}
	
}
