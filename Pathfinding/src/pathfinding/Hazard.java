package pathfinding;

public class Hazard extends Entity{

	protected float f, c, r, a = 0.05f;
	
	public Hazard(float x, float y, float fear, float caution){
		super(x, y);
		f = fear;
		c = caution;
		r = (float)(Math.sqrt(c*c*(a*10-1/f)));
	}
	
	public float termX(float ex, float ey){
		return 20*c*c*f*f*(x-ex)/square(f*(x*x-2*x*ex+y*y-2*y*ey+ey*ey+ex*ex)+c*c);
	}
	
	public float termY(float ex, float ey){
		return 20*c*c*f*f*(y-ey)/square(f*(y*y-2*y*ey+x*x-2*x*ex+ex*ex+ey*ey)+c*c);
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	/*public float getCaution(){
		return c;
	}*/
	
	/*public float getRadius(){
		return r;
	}*/
	
}
