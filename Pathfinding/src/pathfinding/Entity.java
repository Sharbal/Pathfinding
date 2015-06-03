package pathfinding;

public class Entity {
	
	protected float x, y;
	
	public Entity(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public void moveTo(Entity e){
		x = e.x;
		y = e.y;
	}
	
	public float distanceFrom(Entity e){
		return distance(x, y, e.x, e.y);
	}
	
	public float distance(float x1, float y1, float x2, float y2){
		return (float)Math.sqrt(square(x1 - x2) + square(y1 - y2));
	}
	
	public float square(float a){
		return a*a;
	}

}
