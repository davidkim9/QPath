package geom;

public class Circle implements Body
{
	private Pt p = null;
	private double r = 0;
	public Circle(Pt point, double radius) 
	{
		p = point;
		r = radius;
	}
	public void setPoint(Pt point)
	{
		p = point;
	}
	
	public void setRadius(double radius)
	{
		r = radius;
	}
	public Pt getPoint()
	{
		return p;
	}
	
	public double getRadius()
	{
		return r;
	}
	
	public Segment getBoundary()
	{
		return new Segment( new Pt(p.x - r, p.y - r), new Vr(r * 2, r * 2) );
	}
}