package geom;

import java.util.Vector;

public class Polygon implements Body {
	private Pt p = null;
	private Vector<Vr> v = new Vector<Vr>();
	public  Polygon() {
		p = new Pt();
		v = new Vector<Vr>();
	}
	
	public  Polygon(Vector<Vr> v1, Pt pt) 
	{
		v = v1;
		p = pt;
	}
	
	public void setPoint(Pt pt) 
	{
		p = pt;
	}
	
	public Pt getPoint()
	{
		return p;
	}
	
	public void addVector(Vr v1) 
	{
		v.add(v1);
	}
	
	public void removeVector(int i) {
		v.remove(i);
	}
	
	public Vr getVector(int i)
	{
		return v.get(i);
	}
	
	public Pt getVectorPoint(int i) 
	{
		Pt point = p.clonePoint();
		for (int n = 0; n <= i; n++) {
			point.add(v.get(n));
		}
		return point;
	}
	
	public int numVectors() 
	{
		return v.size();
	}
	
	public Segment getBoundary()
	{
		double minX = Integer.MAX_VALUE;
		double maxX = Integer.MIN_VALUE;
		double minY = Integer.MAX_VALUE;
		double maxY = Integer.MIN_VALUE;
		Pt point = p.clonePoint();
		for (int n = 0; n < v.size(); n++) {
			point.add(v.get(n));
			if (point.x < minX) minX = point.x;
			if (point.y < minY) minY = point.y;
			if (point.x > maxX) maxX = point.x;
			if (point.y > maxY) maxY = point.y;
		}
		return new Segment(new Pt(minX, minY), new Vr(maxX - minX, maxY - minY));
	}
	
	private int sign(double x)
	{
		return x < 0 ? -1:1;
	}
	
	//Checks if polygon is convex
	public Boolean convex()
	{
		if (v.size() < 3)
		{
			return false;
		}
		int xCh = 0;
		int yCh = 0;
		
		//Start from last to first
		Vr a = v.get(v.size() - 1).cloneVector();
		a.subtract(v.get(0));
		
		for (int i = 0; i < v.size() - 1; i++)
		{
			Vr b = v.get(i).cloneVector();;
			b.subtract(v.get(i + 1));
			
			//Check if sign changes more than twice
			if (sign(a.x) != sign(b.x)) xCh++;
			if (sign(a.y) != sign(b.y)) yCh++;
			
			//Update previous
			a = b;
		}
		//trace(xCh, yCh, xCh<=2 && yCh<=2, v);
		return xCh<=2 && yCh<=2;
	}
	
	//Will slice up the polygon into convex polygons if the polygon is concave
	public Vector<Polygon> trianglate() 
	{
		int n = v.size();
		
		int[] index = new int[n];
		
		//Ignore this  if already a triangle
		if (n < 3) {
			Vector<Polygon> t = new Vector<Polygon>();
			t.add(this);
			return t;
		}
		
		//Return value(Array of polygons)
		Vector<Polygon> r = new Vector<Polygon>();
		
		//CW and CCW
		int i;
		if (0 < area())
		{
			for (i = 0; i < n; i++) {
				index[i] = i;
			}
		}else {
			for (i = 0; i < n; i++) {
				index[i] = (n - 1) - i;
			}
		}
		
		int nv = n;
		int count = 2 * nv;
		
		int vi = nv - 1;
		while (nv > 2) 
		{
			if ( 0 >= (count--)) {
				System.out.println("BAD POLYGON");
				Vector<Polygon> t = new Vector<Polygon>();
				t.add(this);
				return t;
			}
			int u = vi;
			if (nv <= u)
				u = 0;
			vi = u + 1;
			if (nv <= vi)
				vi = 0;
			int w = vi + 1;
			if (nv <= w)
				w = 0;
			
			//Checks if it is an ear
			if (snip(index[u], index[vi], index[w]))
			{
				int a, b, c, s, t;
				a = index[u];
				b = index[vi];
				c = index[w];
				
				//Converting to new polygon
				Pt ap = getVectorPoint(a);
				Pt bp = getVectorPoint(b);
				Pt cp = getVectorPoint(c);
				
				Vr av = ap.cloneVector();
				av.subtract(p);
				Vr bv = bp.cloneVector();
				bv.subtract(ap);
				Vr cv = cp.cloneVector();
				cv.subtract(bp);
				
				Vector<Vr> tri = new Vector<Vr>(3);
				tri.add(av);
				tri.add(bv);
				tri.add(cv);
				Polygon triangle = new Polygon(tri, p);
				
				r.add(triangle);
				
				for (s = vi, t = vi + 1; t < nv; s++, t++)
					index[s] = index[t];
				nv--;
				count = 2 * nv;
			}
		}
		
		return r;
	}
	
	//Area of contour
	public double area() 
	{
		int n = v.size();
		double a = 0.0;
		int q = 0;
		for (int p = n - 1; q < n; p = q++)
		{
			Vr pval = v.get(p);
			Vr qval = v.get(q);
			a += pval.crossProductLength(qval);
		}
		return a;
	}
	
	//Check if we need to clip
	public Boolean snip(int a1, int b1, int c1)
	{
		Pt a = getVectorPoint(a1);
		Pt b = getVectorPoint(b1);
		Pt c = getVectorPoint(c1);
		
		if (0.0001 > (((b.x - a.x) * (c.y - a.y)) - ((b.y - a.y) * (c.x - a.x))))
		{
			return false;
		}
		
		//CONVERT TO TRIANGLE
		Vr av = a.cloneVector();
		av.subtract(p);
		Vr bv = b.cloneVector();
		bv.subtract(a);
		Vr cv = c.cloneVector();
		cv.subtract(b);
		
		Vector<Vr> tri = new Vector<Vr>(3);
		tri.add(av);
		tri.add(bv);
		tri.add(cv);
		Polygon triangle = new Polygon(tri, p);
		
		//Main.debugBody(triangle);
		
		for (int i = 0; i < v.size(); i++)
		{
			//Skip the ones that are the same
			if (i != a1 && i != b1 && i != c1) {
				//Check if the point is in the triangle
				Pt pt = getVectorPoint(i);
				if (triangle.insideTriangle(pt)){
					return false;
				}
			}
		}
		return true;
	}
	
	//Will use triangle  if polygon is a triangle(faster)
	public Boolean pointInPolygon(Pt pt) {
		if (v.size() == 3) {
			return insideTriangle(pt);
		}else if (v.size() > 3) {
			return insidePolygon(pt);
		}
		//Invalid Polygon
		return false;
	}
	
	//Tests if point is inside polygon
	private Boolean insidePolygon(Pt pt) {
		Boolean c = false;
		int i, j;
		for (i = 0, j = v.size() - 1; i < v.size(); j = i++) {
			Pt viP = getVectorPoint(i);
			Pt vjP = getVectorPoint(j);
			
			if ((((viP.y <= pt.y) && (pt.y < vjP.y)) ||
			((vjP.y <= pt.y) && (pt.y < viP.y))) &&
			(pt.x < (vjP.x - viP.x) * (pt.y - viP.y) / (vjP.y - viP.y) + viP.x)){
				c = !c;
			}
		}
		return c;
	}
	
	//Tests if point is inside a triangle
	private Boolean insideTriangle(Pt pt)
	{
		double ax, ay, bx, by, cx, cy, apx, apy, bpx, bpy, cpx, cpy, cCrossap, bCrosscp, aCrossbp;
		Pt a = p.clonePoint();
		a.add(v.get(0));
		Pt b = a.clonePoint();
		b.add(v.get(1));
		Pt c = b.clonePoint();
		c.add(v.get(2));
		
		ax = c.x - b.x;
		ay = c.y - b.y;
		bx = a.x - c.x;
		by = a.y - c.y;
		cx = b.x - a.x;
		cy = b.y - a.y;
		apx = pt.x - a.x;
		apy = pt.y - a.y;
		bpx = pt.x - b.x;
		bpy = pt.y - b.y;
		cpx = pt.x - c.x;
		cpy = pt.y - c.y;
		
		aCrossbp = ax * bpy - ay * bpx;
		cCrossap = cx * apy - cy * apx;
		bCrosscp = bx * cpy - by * cpx;
		
		return aCrossbp >= 0 && bCrosscp >= 0 && cCrossap >= 0;
	}
	
	public String toString() 
	{
		String s = "Polygon {";
		s += "x = " + p.x + ", y = " + p.y;
		for(int i = 0; i < v.size(); i++) {
			Vr v1 = v.get(i);
			s += v1;
		}
		s += "}";
		return s;
	}

	public Pt randomPoint() {
		Segment bounds = getBoundary();
		Pt randPoint = bounds.getPoint().clonePoint();
		Vr vector = bounds.getVector();
		randPoint.x += vector.x * Math.random();
		randPoint.y += vector.y * Math.random();
		while(!pointInPolygon(randPoint)){
			randPoint = bounds.getPoint().clonePoint();
			randPoint.x += vector.x * Math.random();
			randPoint.y += vector.y * Math.random();
		}
		return randPoint;
	}
}