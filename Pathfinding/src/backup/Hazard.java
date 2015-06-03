package backup;

public class Hazard {

	protected float x, y, f, c, r, k = 0.05f;
	
	public Hazard(float x, float y, float fear, float caution){
		this.x = x;
		this.y = y;
		f = fear;
		c = caution;
		r = (float)(Math.sqrt(c*c*(k*10-1/f)));
	}
	
	public float termX(float ex, float ey){
		return 20*c*c*f*f*(x-ex)/square(f*(x*x-2*x*ex+y*y-2*y*ey+ey*ey+ex*ex)+c*c);
	}
	
	public float termY(float ex, float ey){
		return 20*c*c*f*f*(y-ey)/square(f*(y*y-2*y*ey+x*x-2*x*ex+ex*ex+ey*ey)+c*c);
	}
	
	private float square(float a){
		return a*a;
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
	
	public float getRadius(){
		return r;
	}
	
}
