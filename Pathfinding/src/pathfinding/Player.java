package pathfinding;


public class Player extends Hazard {

	private float speed;
	private boolean hasFlag;
	
	public Player(float ix, float iy, float speed, float fear, float caution) {
		super(ix, iy, fear, caution);
		this.speed = speed;
		hasFlag = false;
	}
	
	public boolean hasFlag(){
		return hasFlag;
	}
	
	public void setHasFlag(boolean hasFlag){
		if (hasFlag) speed /= 2;
		else speed *= 2;
		this.hasFlag = hasFlag;
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
	
	/*private float distance(float x1, float y1, float x2, float y2){
		return (float)Math.sqrt(square(x1 - x2) + square(y1 - y2));
	}*/
	
	/*for (Hazard h : hazards){
		if (h != this && distance(x + speed, y, h.x, h.y) < h.getRadius()){
			x = h.x - (float)Math.sqrt(Math.abs(square(h.getRadius()) - square(y - h.y)));
			return;
		}
	}*/

}
