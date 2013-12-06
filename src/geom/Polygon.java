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
	
	private double sign(Pt p1, Pt p2, Pt p3)
	{
		return (p1.x - p3.x) * (p2.y - p3.y) - (p2.x - p3.x) * (p1.y - p3.y);
	}
	
	//Tests if point is inside a triangle
	private boolean insideTriangle(Pt pt)
	{
		Pt a = p.clonePoint();
		a.add(v.get(0));
		Pt b = a.clonePoint();
		b.add(v.get(1));
		Pt c = b.clonePoint();
		c.add(v.get(2));
		
		boolean b1 = sign(pt, a, b) < 0.0f;
		boolean b2 = sign(pt, b, c) < 0.0f;
		boolean b3 = sign(pt, c, a) < 0.0f;
		
		return ((b1 == b2) && (b2 == b3));
	}
	
	public String toString() 
	{
		String s = "Polygon " + hashCode() + " {";
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
	
	private boolean segmentIntersectRectangle(int a_rectangleMinX, int a_rectangleMinY, int a_rectangleMaxX, int a_rectangleMaxY, int a_p1x, int a_p1y, int a_p2x, int a_p2y)
	{
		// Find min and max X for the segment
		
		double minX = a_p1x;
		double maxX = a_p2x;
		
		if(a_p1x > a_p2x)
		{
		minX = a_p2x;
		maxX = a_p1x;
		}
		
		// Find the intersection of the segment's and rectangle's x-projections
		
		if(maxX > a_rectangleMaxX)
		{
			maxX = a_rectangleMaxX;
		}
		
		if(minX < a_rectangleMinX)
		{
			minX = a_rectangleMinX;
		}
		
		if(minX > maxX) // If their projections do not intersect return false
		{
			return false;
		}
		
		// Find corresponding min and max Y for min and max X we found before
		
		double minY = a_p1y;
		double maxY = a_p2y;
		
		double dx = a_p2x - a_p1x;
		
		if(Math.abs(dx) > 0)
		{
			double a = (a_p2y - a_p1y) / dx;
			double b = a_p1y - a * a_p1x;
			minY = a * minX + b;
			maxY = a * maxX + b;
		}
		
		if(minY > maxY)
		{
			double tmp = maxY;
			maxY = minY;
			minY = tmp;
		}
		
		// Find the intersection of the segment's and rectangle's y-projections
		
		if(maxY > a_rectangleMaxY)
		{
			maxY = a_rectangleMaxY;
		}
		
		if(minY < a_rectangleMinY)
		{
			minY = a_rectangleMinY;
		}
		
		if(minY > maxY) // If Y-projections do not intersect return false
		{
			return false;
		}
		
		return true;
	}
	
	public boolean intersect(int x1, int y1, int x2, int y2) {
		/*
		for each edge in the polygon
		  if (edge intersects rectangle) 
		    return true
		end for

		// Catch the "polygon completely within the box" case
		if (polygon.first_point is in rectangle) 
		    return true

		// Catch the "box completely within the polygon" case
		if (box.upper_left_corner is in polygon) 
		  return true
		*/
		int t = 0;
		if(x1 > x2){
			t = x1;
			x1 = x2;
			x2 = t;
		}
		if(y1 > y2){
			t = y1;
			y1 = y2;
			y2 = t;
		}
		
		int i;

		Pt point = p.clonePoint();
		Vr vector = v.get(0);
		point.add(vector);
		Pt firstPt = point.clonePoint();
		int p1x, p1y, p2x, p2y;
		for (i = 1; i <= v.size(); i++) {
			if(i == v.size()){
				p1x = (int) point.x;
				p1y = (int) point.y;
				p2x = (int) firstPt.x;
				p2y = (int) firstPt.y;
			}else{
				vector = v.get(i);
				p1x = (int) point.x;
				p1y = (int) point.y;
				point.add(vector);
				p2x = (int) point.x;
				p2y = (int) point.y;
			}
			if(segmentIntersectRectangle(x1, y1, x2, y2, p1x, p1y, p2x, p2y)){
				return true;
			}
			
		}
		
		point = p.clonePoint();
		for (i = 0; i < v.size(); i++) {
			vector = v.get(i);
			point.add(vector);
			
			if(aab((int) point.x, (int) point.y, x1, y1, x2, y2)){
				return true;
			}
		}
		
		if(pointInPolygon(new Pt(x1, y1))) {
			return true;
		}
		if(pointInPolygon(new Pt(x1, y2))) {
			return true;
		}
		
		if(pointInPolygon(new Pt(x2, y1))) {
			return true;
		}
		if(pointInPolygon(new Pt(x2, y2))) {
			return true;
		}
		
		return false;
	}
	private boolean aab(int x, int y, int a_rectangleMinX, int a_rectangleMinY, int a_rectangleMaxX, int a_rectangleMaxY)
	{
		if(a_rectangleMinX <= x && a_rectangleMinY <= y && x < a_rectangleMaxX && y < a_rectangleMaxY){
			return true;
		}
		
		return false;
	}
}