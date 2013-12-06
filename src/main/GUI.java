package main;

import geom.Pt;
import geom.Segment;
import geom.Vr;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;

import structure.QAStar;
import structure.QMap;
import structure.QuadTreeNode;

public class GUI extends JFrame implements MouseListener {
	
	private static final long serialVersionUID = 1L;
	
	Canvas canvas;
	QMap map;
	
	boolean direction = true;
	
	public GUI(){
		
		map = new QMap(4,4);
		
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setMinimumSize(new java.awt.Dimension(530, 555));
		addMouseListener(this);
		
		canvas = new Canvas(map);
		
		add(canvas);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Pt p = new Pt(e.getX() - canvas.getX() - 8, e.getY() - canvas.getY() - 30);
		
		if(direction){
			canvas.setFrom(p);
		}else{
			canvas.setTo(p);
		}
		direction = !direction;
		
		if(canvas.from != null && canvas.to != null){
			canvas.findPath();
		}
		
		canvas.repaint();
	}
	
}

class Canvas extends JComponent {
	
	private static final long serialVersionUID = 1L;
	
	QMap map;
	QAStar pathfind;
	
	ArrayList<QuadTreeNode> path;
	
	Pt from;
	Pt to;
	QuadTreeNode startNode = null;
	QuadTreeNode endNode = null;
	
	public Canvas(QMap map) {
		this.map = map;
		pathfind = new QAStar(map);
	}
	
	public void findPath(){
		if(from != null && to != null){
			QuadTreeNode node = map.getNodeAtLocation((int) from.x, (int) from.y);
			
			if(node != null){
				startNode = innerChild(node);
			}
		
			QuadTreeNode eNode = map.getNodeAtLocation((int) to.x, (int) to.y);
			
			if(eNode != null){
				endNode = innerChild(eNode);
			}
			
			if(startNode != null && endNode != null){
				//long duration = System.currentTimeMillis();
				path = pathfind.getPath(startNode, endNode);
				//System.out.println("Pathfinding time: " + System.currentTimeMillis() - duration + "ms");
			}
		}
	}
	
	public void setFrom(Pt from)
	{
		this.from = from;
	}
	
	public void setTo(Pt to)
	{
		this.to = to;
	}
	
	public void drawNode(Graphics2D p, QuadTreeNode node){
		if(node.hasChildren()){
			QuadTreeNode[] nodes = node.getChildren();
			for(int i = 0 ; i < nodes.length; i++){
				drawNode(p, nodes[i]);
			}
		}
		Segment bounds = node.getBoundary();
		Pt pt = bounds.getPoint();
		Vr vr = bounds.getVector();
		
		Color c = p.getColor();
		if(node.hasData()){
			p.setColor(new Color(200, 200, 200));
			p.fillRect((int) pt.x,(int) pt.y,(int) vr.x,(int) vr.y);
		}
		p.setColor(c);
		p.drawRect((int) pt.x,(int) pt.y,(int) vr.x,(int) vr.y);
	}
	
	private QuadTreeNode innerChild(QuadTreeNode node){
		if(node.hasChildren()){
			return innerChild(node.getChildren()[(int)(Math.random()*4)]);
		}
		return node;
	}
	
	public void drawLine(Graphics2D p, QuadTreeNode a, QuadTreeNode b){
		Segment aSeg = a.getBoundary();
		Segment bSeg = b.getBoundary();
		
		Pt aP = aSeg.getPoint();
		Vr aV = aSeg.getVector();
		Pt bP = bSeg.getPoint();
		Vr bV = bSeg.getVector();
		
		int aX = (int) (aP.x + aV.x/2);
		int aY = (int) (aP.y + aV.y/2);
		int bX = (int) (bP.x + bV.x/2);
		int bY = (int) (bP.y + bV.y/2);
		
		p.drawLine(aX, aY, bX, bY);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		Graphics2D p = (Graphics2D) g;
		p.clearRect(0, 0, getWidth(), getHeight());

		p.setColor(new Color(50, 50, 50));
		p.fillRect(0, 0, getWidth(), getHeight());
		
		p.setColor(new Color(150, 150, 150));
		
		int height = map.height();
		int width = map.width();
		for(int y = 0 ; y < height; y++)
		{
			for(int x = 0 ; x < width; x++)
			{
				QuadTreeNode node = map.getNode(x, y);
				drawNode(p, node);
			}
		}
		
		if(path != null){
			
			QuadTreeNode last = path.get(0);
			for(int i = 0 ; i < path.size(); i++){
				QuadTreeNode traceNode = path.get(i);
				p.setColor(new Color(0, 255, 0));
				drawNode(p, traceNode);
				p.setColor(new Color(255, 0, 0));
				drawLine(p, last, traceNode);
				last = traceNode;
			}
			
			p.setColor(new Color(255, 0, 0));
			drawNode(p, startNode);
			p.setColor(new Color(0, 0, 255));
			drawNode(p, endNode);
		}
		
		revalidate();
	}
}
