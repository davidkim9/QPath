package structure;

import java.util.LinkedList;
import java.util.Vector;

import geom.Body;
import geom.Pt;
import geom.Segment;
import geom.Vr;

/**
 * QuadTreeNode
 * @author David Kim
 */
public class QuadTreeNode implements QNode, Comparable<QuadTreeNode> {
	private int x = 0;
	private int y = 0;
	private int width = 0;
	private int height = 0;
	private boolean children = false;
	private QuadTreeNode nodes[];
	private Vector<Body> data;
	
	public QuadTreeNode(int x1, int y1, int w, int h) 
	{
		nodes = new QuadTreeNode[4];
		x = x1;
		y = y1;
		width = w;
		height = h;
	}
	
	public void resize(int x1, int y1, int w1, int h1) 
	{
		if (children) 
		{
			x = x1;
			y = y1;
			width = w1;
			height = h1;
			
			int w = width / 2;
			int h = height / 2;
			
			//top left
			nodes[0].resize(x, y, w, h);
			//top right
			nodes[1].resize(x + w, y, w, h);
			//bottom right
			nodes[2].resize(x + w, y + h, w, h);
			//bottom left
			nodes[3].resize(x, y + h, w, h);
		}else {
			x = x1;
			y = y1;
			width = w1;
			height = h1;
		}
	}
	
	public void divide()
	{
		if (children) 
		{
			for (int i = 0; i < 4; i++) 
			{
				nodes[i].divide();
			}
		}else {
			int w = width / 2;
			int h = height / 2;
			
			//top left
			nodes[0] = new QuadTreeNode(x, y, w, h);
			//top right
			nodes[1] = new QuadTreeNode(x + w, y, w, h);
			//bottom right
			nodes[2] = new QuadTreeNode(x + w, y + h, w, h);
			//bottom left
			nodes[3] = new QuadTreeNode(x, y + h, w, h);
			
			children = true;
		}
	}
	
	public void distributeData()
	{
		if (children)
		{
			if(data != null){
				
				for (int i = 0; i < data.size(); i++)
				{
					Body b = data.get(i);
					addData(b);
				}
				
			}
			
			data = null;
		}
	}
	
	public void addData(Body body)
	{
		//If a node is a branch, it may not have data
		//Pass to children
		if (children) 
		{
			for (int i = 0; i < 4; i++)
			{
				QuadTreeNode n = nodes[i];
				
				n.addData(body);
			}
		}else {
			if(checkData(body)){
				if (data == null) {
					data = new Vector<Body>();
				}
				if (data.indexOf(body) == -1) {
					data.add(body);
				}
			}
		}
	}
	
	public void removeData(Body body)
	{
		if (children)
		{
			for (int i = 0; i < 4; i++)
			{
				QuadTreeNode n = nodes[i];
				n.removeData(body);
			}
		}else {
			int dataIndex = data.indexOf(body);
			if(dataIndex >= 0){
				data.remove(dataIndex);
			}
		}
	}
	
	public void clearData()
	{
		if (children) 
		{
			for (int i = 0; i < 4; i++)
			{
				QuadTreeNode n = nodes[i];
				n.clearData();
				nodes[i] = null;
				n = null;
			}
			
			children = false;
		}else {
			data = null;
		}
	}
	
	public Vector<Body> getData()
	{
		if (children) 
		{
			Vector<Body> returnData = new Vector<Body>();
			for (int i = 0; i < 4; i++)
			{
				QuadTreeNode n = nodes[i];
				Vector<Body> d = n.getData();
				if(d != null){
					concatData(returnData, d);
				}
			}
			return returnData;
		}else {
			return data;
		}
	}
	
	public Vector<Body> getObjects(Body body)
	{
		//Graphic.drawDebug(getBoundary().toRectangle());
		if (children) 
		{
			Vector<Body> returnData = new Vector<Body>();
			for (int i = 0; i < 4; i++)
			{
				QuadTreeNode n = nodes[i];
				
				if (n.getBoundary().aabb(body.getBoundary())) {
					Vector<Body> d = n.getObjects(body);
					if (d != null) {
						concatData(returnData, d);
					}
				}
			}
			return returnData;
		}else {
			
			if (getBoundary().aabb(body.getBoundary())) 
			{
				return data;
			}
			
			return null;
		}
	}
	
	public Vector<Body> updateData()
	{
		if (children) 
		{
			Vector<Body> dataArray = new Vector<Body>();
			for (int i = 0; i < 4; i++)
			{
				QuadTreeNode n = nodes[i];
				Vector<Body> nData = n.updateData();
				if (nData != null) {
					concatData(dataArray, nData);
				}
			}
			
			for (int i = 0; i < dataArray.size(); i++)
			{
				Body b = dataArray.get(i);
				addData(b);
			}
			
			return dataArray;
			
		}else {
			Vector<Body> newData = new Vector<Body>();
			for (int i = 0; i < data.size(); i++)
			{
				Body b = data.get(i);
				if (!checkData(b)) {
					newData.add(b);
					removeData(b);
				}
			}
			return newData;
		}
	}
	
	private void concatData(Vector<Body> a, Vector<Body> b)
	{
		for (int i = 0; i < b.size(); i++)
		{
			Body body = b.get(i);
			if (a.indexOf(body) == -1) {
				a.add(body);
			}
		}
	}
	
	public Segment getBoundary()
	{
		return new Segment(new Pt(x, y), new Vr(width, height));
	}
	
	//Checks data if it should be put into this node
	public boolean checkData(Body body)
	{
		Segment a = body.getBoundary();
		Segment b = getBoundary();
		return a.aabb(b);
	}
	
	public boolean hasData()
	{
		return data != null && data.size() > 0;
	}
	
	public boolean hasChildren(){
		return children;
	}
	
	public QuadTreeNode[] getChildren()
	{
		return nodes;
	}
	
	public QuadTreeNode getNode(Pt p)
	{
		Segment bounds = getBoundary();
		
		if(bounds.aabb(p.getBoundary())){
			if (children) 
			{
				for (int i = 0; i < 4; i++) 
				{
					QuadTreeNode node = nodes[i];
					QuadTreeNode child = node.getNode(p);
					
					if(child != null) {
						return child;
					}
				}
			}else{
				return this;
			}
		}
		return null;
	}
	
	public LinkedList<QuadTreeNode> getNodes(Segment s)
	{
		Segment bounds = getBoundary();
		
		LinkedList<QuadTreeNode> list = new LinkedList<QuadTreeNode>();
		
		if(bounds.aabb(s)){
			if (children) 
			{
				for (int i = 0; i < 4; i++) 
				{
					QuadTreeNode node = nodes[i];
					LinkedList<QuadTreeNode> childList = node.getNodes(s);
					
					list.addAll(childList);
				}
			}else{
				list.add(this);
			}
		}
		return list;
	}
	
	public LinkedList<QuadTreeNode> getNeighbors(QuadTreeNode node)
	{
		Segment[] neighbors =	{
									new Segment(new Pt(node.x, node.y - 1), new Vr(node.width - 1, 0)),
									new Segment(new Pt(node.x - 1, node.y), new Vr(0, node.height - 1)),
									new Segment(new Pt(node.x + node.width + 1, node.y), new Vr(0, node.height - 1)),
									new Segment(new Pt(node.x, node.y + node.height + 1), new Vr(node.width - 1, 0))
								};
		
		LinkedList<QuadTreeNode> list = new LinkedList<QuadTreeNode>();
		
		for(int i = 0 ; i < neighbors.length; i++){
			LinkedList<QuadTreeNode> children = getNodes(neighbors[i]);
			list.addAll(children);
			
		}
		
		return list;
	}
	/*
	public String toString(){
		return "QuadTreeNode[ x=" + x + " y=" + y + " w=" + width + " h=" + height + "] ";
	}
	*/
	//Pathing stuff
	QuadTreeNode parent;
	
	public float gScore = 0;
	public float hScore = 0;
	public float fScore = 0;

	public void setParent(QuadTreeNode parent)
	{
		this.parent = parent;
	}
	
	public QuadTreeNode getParent()
	{
		return parent;
	}

	public int compareTo(QuadTreeNode n) {
		return (int) (fScore - n.fScore);
	}
}