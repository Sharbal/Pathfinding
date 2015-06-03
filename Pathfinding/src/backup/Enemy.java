package backup;

import java.util.ArrayList;

public class Enemy extends Hazard{
	
	private float speed, k = 0.01f;
	private float oldx, oldy, dxdt, dydt;
	
	public Enemy(float ix, float iy, float speed, float fear, float caution){
		super(ix, iy, fear, caution);
		x = ix;
		y = iy;
		this.speed = speed;
	}
	
	public void moveLinear(float goalX, float goalY, ArrayList<Hazard> hazards){
		oldx = x;
		oldy = y;
		int loops = 0;
		while (loops < 50 && distance(oldx, oldy, x, y) < speed){
			dxdt = -speed * (x - goalX) / (float)(c + Math.sqrt(square(y - goalY) + square(x - goalX)));
			dydt = -speed * (y - goalY) / (float)(c + Math.sqrt(square(y - goalY) + square(x - goalX)));
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
	}
	
	private float square(float a){
		return a*a;
	}
	
	private float distance(float x1, float y1, float x2, float y2){
		return (float)Math.sqrt(square(x1 - x2) + square(y1 - y2));
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
