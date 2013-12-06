package geom;

public class Vr extends Pt{
	public Vr() 
	{
	}
	
	public Vr(double x1, double y1) 
	{
		super(x1, y1);
	}
	
	public double crossProductLength(Vr v) {
		return (x*v.y) - (y*v.x);
	}
	
	public Vr crossProduct() {
		return new Vr(-y, x);
	}
}
