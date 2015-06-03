package pathfinding;

import java.util.ArrayList;

public class AI extends Hazard{
	
	private float maxSpeed, speed, k = 2.5f;
	private float oldx, oldy, dxdt, dydt;
	private Entity goal;
	private ArrayList<Hazard> staticHazards;
	private ArrayList<AI> dynamicHazards;
	private boolean home, hasFlag, invincible, inJail, goingHome;
	private AI closestEnemy;
	private boolean isPlayer;
	
	public AI(float ix, float iy, float speed, float fear, float caution){
		super(ix, iy, fear, caution);
		x = ix;
		y = iy;
		this.speed = maxSpeed = speed;
		dynamicHazards = new ArrayList<AI>();
		staticHazards = new ArrayList<Hazard>();
		home = true;
		hasFlag = inJail = invincible = isPlayer = false;
	}
	
	public void setGoal(Entity goal){
		this.goal = goal;
	}
	
	public Entity getGoal(){
		return goal;
	}
	
	public void setStaticHazards(ArrayList<Hazard> sh){
		staticHazards = sh;
	}
	
	public void addHazards(ArrayList<AI> hazards){
		dynamicHazards.addAll(hazards);
	}
	
	public void clearHazards(){
		dynamicHazards.clear();
	}
	
	public void setGoingHome(boolean goingHome){
		this.goingHome = goingHome;
	}
	
	public boolean isGoingHome(){
		return goingHome;
	}
	
	public boolean isPlayer(){
		return isPlayer;
	}
	
	public void setPlayer(boolean player){
		isPlayer = player;
	}
	
	public void moveLinear(){ //Cone
		if (goal != null){
			oldx = x;
			oldy = y;
			int loops = 0;
			while (loops < 50 && distance(oldx, oldy, x, y) < speed){
				dxdt = -speed * (x - goal.x) / (float)(c + Math.sqrt(square(y - goal.y) + square(x - goal.x)));
				dydt = -speed * (y - goal.y) / (float)(c + Math.sqrt(square(y - goal.y) + square(x - goal.x)));
				for (Hazard h : dynamicHazards){
					dxdt -= k*h.termX(x, y);
					dydt -= k*h.termY(x, y);
				}
				for (Hazard h : staticHazards){
					dxdt -= k*h.termX(x, y); //TODO k/3 temp?
					dydt -= k*h.termY(x, y);
				}
				x += dxdt;
				y += dydt;
				++loops;
			}
		}
	}
	
	public void runHome(float middleX){
		oldx = x;
		oldy = y;
		int loops = 0;
		while (loops < 50 && distance(oldx, oldy, x, y) < speed){
			dxdt = -speed*(x - middleX)/Math.abs(x - middleX);
			dydt = 0;
			for (Hazard h : dynamicHazards){
				dxdt -= k*h.termX(x, y);
				dydt -= k*h.termY(x, y);
			}
			for (Hazard h : staticHazards){
				dxdt -= k*h.termX(x, y); //TODO k/3 temp?
				dydt -= k*h.termY(x, y);
			}
			x += dxdt;
			y += dydt;
			++loops;
		}
		
	}
	
	public float goalDistance(){
		if (goal != null) return distance(x, y, goal.x, goal.y);
		return -1;
	}
	
	public boolean isHome(){
		return home;
	}
	
	public boolean hasFlag(){
		return hasFlag;
	}
	
	public boolean isInvincible(){
		return invincible;
	}
	
	public void setInvincible(boolean invincible){
		if (invincible) speed = maxSpeed/2;
		else speed = maxSpeed;
		this.invincible = invincible;
		clearHazards();
	}
	
	public void setHasFlag(boolean hasFlag){
		if (hasFlag) speed = maxSpeed/2;
		else speed = maxSpeed;
		this.hasFlag = hasFlag;
	}
	
	public void setHome(boolean home){
		this.home = home;
	}
	
	public void setClosestEnemy(AI enemy){
		closestEnemy = enemy;
	}
	
	public AI getClosestEnemy(){
		return closestEnemy;
	}
	
	public boolean isInJail(){
		return inJail;
	}
	
	public void setInJail(boolean inJail){
		if (inJail) speed = maxSpeed/2;
		else speed = maxSpeed;
		this.inJail = inJail;
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
	
	/*public void move(float goalX, float goalY, ArrayList<Hazard> hazards){
		oldx = x;
		oldy = y;
		int loops = 0;
		while (loops < 50 && distance(oldx, oldy, x, y) < speed){ //num loops? TODO 10?
			dxdt = -speed*k*2*(x - goalX);
			dydt = -speed*k*2*(y - goalY);
			for (Hazard h : hazards){
				if (h != this){
					dxdt -= h.termX(x, y);
					dydt -= h.termY(x, y);
				}
			}
				x += dxdt;
				y += dydt;
				++loops;
		}
		
		//Normalized velocity - produces jitters
		dxdt = -k*2*(x - goalX);
		dydt = -k*2*(y - goalY);
		for (Hazard h : hazards){
			if (h != this){
				dxdt -= h.termX(x, y);
				dydt -= h.termY(x, y);
			}
		}
		mag = magnitude(dxdt, dydt);
		x += speed*dxdt/mag;
		y += speed*dydt/mag;
	}*/
	
	/*private float magnitude(float x, float  y){
		return (float)Math.sqrt(square(x) + square(y));
	}*/
	
	
	/*public float getSpeed(){
		return speed;
	}*/

}
