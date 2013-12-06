package structure;

import java.util.LinkedList;

import geom.Segment;
import geom.Pt;

public class QMap implements QNode {
	
	private QuadTreeNode[][] map;
	private int width = 1;
	private int height = 1;
	
	static final int minimum = 64;
	static final int nodeWidth = 128;
	static final int nodeHeight = 128;
	
	
	public QMap(){
		map = new QuadTreeNode[height][width];
		buildMap();
		//ADD POLYGONS
		partitionMap();
	}
	
	public QMap(int width, int height){
		this.width = width;
		this.height = height;
		map = new QuadTreeNode[height][width];
		buildMap();
		//ADD POLYGONS
		partitionMap();
	}
	
	public int width()
	{
		return width;
	}
	
	public int height()
	{
		return height;
	}
	
	public void buildMap()
	{
		for(int y = 0 ; y < height; y++){
			for(int x = 0 ; x < width; x++){
				QuadTreeNode topNode = new QuadTreeNode(x * nodeWidth, y * nodeHeight, nodeWidth, nodeHeight);
				map[y][x] = topNode;
			}
		}
	}
	
	public void partitionMap()
	{
		for(int y = 0 ; y < height; y++){
			for(int x = 0 ; x < width; x++){
				QuadTreeNode topNode = map[y][x];
				partitionNode(topNode);
			}
		}
	}

	private void partitionNode(QuadTreeNode node)
	{
		
		double area = node.getBoundary().area();
		if(area > minimum){
			//if(node.hasData()){
			if(Math.random() > .2){
				
				Segment bounds = node.getBoundary();
				Pt bP = bounds.getPoint();
				Pt bV = bounds.getVector();
				
				node.addData(new Pt(bP.x + Math.random() * bV.x, bP.y + Math.random() * bV.y));
				
				
				if(!node.hasChildren()){
					node.divide();
					node.distributeData();
				}
				QuadTreeNode[] children = node.getChildren();
				for(int i = 0 ; i < children.length; i++){
					QuadTreeNode child = children[i];
					partitionNode(child);
				}
			}
		}

	}
	
	public QuadTreeNode getNodeAtLocation(int x, int y) {
		
		int aX = x / nodeWidth;
		int aY = y / nodeHeight;
		
		QuadTreeNode n = getNode(aX, aY);
		
		if(n != null){
			return n.getNode(new Pt(x, y));
		}
		
		return null;
	}
	
	//the array returned will not always have 4 nodes as a subchild can have many neighbors
	public LinkedList<QuadTreeNode> getNeighbors(QuadTreeNode node)
	{
		Segment bounds = node.getBoundary();
		Pt p = bounds.getPoint();
		
		int x = (int) (p.x / nodeWidth);
		int y = (int) (p.y / nodeHeight);
		
		Pt[] direction = 	{
								new Pt(x, y),
								new Pt(x - 1, y),
								new Pt(x + 1, y),
								new Pt(x, y - 1),
								new Pt(x, y + 1)
							};
		LinkedList<QuadTreeNode> neighbors = new LinkedList<QuadTreeNode>();
		
		for(int i = 0 ; i < direction.length ; i++){
			Pt pt = direction[i];
			int getX = (int) pt.x;
			int getY = (int) pt.y;
			QuadTreeNode nodeA = getNode(getX, getY);
			if(nodeA != null){
				LinkedList<QuadTreeNode> children = nodeA.getNeighbors(node);
				neighbors.addAll(children);
			}
		}
		
		return neighbors;
	}
	
	public QuadTreeNode getNode(int x, int y){
		if(x >= 0 && y >= 0 && x < width && y < height){
			return map[y][x];
		}
		return null;
	}
}
