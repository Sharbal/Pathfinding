package backup;


public class Player extends Hazard {

	private float speed;
	
	public Player(float ix, float iy, float speed, float fear, float caution) {
		super(ix, iy, fear, caution);
		this.speed = speed;
	}
	
	public void moveRight(){
		x += speed;
	}
	
	public void moveLeft(){
		x -= speed;
	}
	
	public void moveDown(){
		y += speed;
	}
	
	public void moveUp(){
		y -= speed;
	}
	
	private float distance(float x1, float y1, float x2, float y2){
		return (float)Math.sqrt(square(x1 - x2) + square(y1 - y2));
	}
	
	private float square(float a){
		return a*a;
	}
	
	/*for (Hazard h : hazards){
		if (h != this && distance(x + speed, y, h.x, h.y) < h.getRadius()){
			x = h.x - (float)Math.sqrt(Math.abs(square(h.getRadius()) - square(y - h.y)));
			return;
		}
	}*/

}
