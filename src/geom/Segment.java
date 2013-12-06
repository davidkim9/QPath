package geom;

import java.util.Vector;

public class Segment implements Body{
	private Pt p = null;
	private Vr v = null;
	public Segment(Pt p1, Vr v1) 
	{
		p = p1;
		v = v1;
	}
	
	public Pt getPoint()
	{
		return p;
	}
	
	public Segment getBoundary()
	{
		return this;
	}
	
	public void setPoint(Pt p1)
	{
		p = p1;
	}
	
	public Vr getVector()
	{
		return v;
	}
	
	public void setVector(Vr v1)
	{
		v = v1;
	}
	
	public Pt getPoint2()
	{
		Pt r = p.clonePoint();
		r.add(v);
		return r;
	}
	
	public boolean aabb(Segment b)
	{
		double x = p.x;
		double y = p.y;
		double w = p.x + v.x;
		double h = p.y + v.y;
		
		double bx = b.p.x;
		double by = b.p.y;
		double bw = b.p.x + b.v.x;
		double bh = b.p.y + b.v.y;
		
		if ( x > bw )
			return false;
		
		if ( w <= bx )
			return false;
		
		if ( y > bh )
			return false;
		
		if ( h <= by )
			return false;
		
		return true;
	}
	
	public Polygon toRectangle()
	{
		Vector<Vr> vr = new Vector<Vr>();
		vr.add(new Vr(0,0));
		vr.add(new Vr(v.x,0));
		vr.add(new Vr(0,v.y));
		vr.add(new Vr(-v.x,0));
		return new Polygon(vr, p);
	}
	
	public double area(){
		return Math.abs(v.x * v.y);
	}
	
	public String toString()
	{
		return "[" + p +"," + v +"]";
	}
}
