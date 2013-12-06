package geom;

public class Pt implements Body{
	public double x = 0;
	public double y = 0;
	
	public Pt() 
	{
		
	}
	
	public Pt(double x1, double y1) 
	{
		x = x1;
		y = y1;
	}
	
	public Pt getPoint()
	{
		return this;
	}
	
	public Segment getBoundary()
	{
		return new Segment( new Pt(x, y) , new Vr() );
	}
	
	public String toString() {
		return "[" + x + "," + y + "]";
	}
	
	public void add(Pt p) {
		x += p.x;
		y += p.y;
	}
	
	public void subtract(Pt p) {
		x -= p.x;
		y -= p.y;
	}
	
	public void scalar(double n) {
		x *= n;
		y *= n;
	}
	
	public double dot(Vr v) {
		return x * v.x + y * v.y;
	}
	
	public double distance(Pt p) {
		return Math.sqrt(Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2));
	}
	
	public double magnitude() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	
	public void normalize() {
		double l = magnitude();
		x = x / l;
		y = y / l;
	}
	
	public Pt clonePoint()
	{
		return new Pt(x, y);
	}
	
	public Vr cloneVector()
	{
		return new Vr(x, y);
	}
}
