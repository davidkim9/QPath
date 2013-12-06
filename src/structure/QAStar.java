package structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;

import geom.Pt;
import geom.Segment;
import geom.Vr;

public class QAStar {
	QMap map;
	
	private static final int max_cache = 200;
	private Map<String, ArrayList<QuadTreeNode>> cache = new LinkedHashMap<String, ArrayList<QuadTreeNode>>(max_cache, .75F, true) {
		private static final long serialVersionUID = -7007851668709610447L;
		protected boolean removeEldestEntry(Map.Entry<String, ArrayList<QuadTreeNode>> eldest) {
	        return size() > max_cache;
	    }
	};
	
	public QAStar(QMap map){
		this.map = map;
	}
	
	private ArrayList<QuadTreeNode> tracePath(QuadTreeNode node)
	{
		ArrayList<QuadTreeNode> nodeList = new ArrayList<QuadTreeNode>();
		nodeList.add(node);
		
		QuadTreeNode current = node.getParent();
		node.setParent(null);
		while(current != null){
			nodeList.add(current);
			QuadTreeNode parent = current.getParent();
			
			current.gScore = 0;
			current.hScore = 0;
			current.fScore = 0;
			
			current = parent;
		}
		return nodeList;
	}
	
	private float heuristicCost(QuadTreeNode a, QuadTreeNode b){
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
		//return (float) Math.sqrt( Math.pow(aX-bX, 2) +Math.pow(aY-bY, 2));	
		//return (aX + aY) - (bX + bY);
		
		return ( Math.abs(aX-bX) + Math.abs(aY-bY) );
	}
	
	public ArrayList<QuadTreeNode> getPath(QuadTreeNode start, QuadTreeNode end) {
		String key = start.toString() +"-"+ end.toString();
		ArrayList<QuadTreeNode> path = cache.get(key);
		if(path != null) {
			//Refresh cache
			cache.put(key, path);
			return path;
		}
		
		PriorityQueue<QuadTreeNode> open = new PriorityQueue<QuadTreeNode>();
		open.add(start);
		
		ArrayList<QuadTreeNode> closed = new ArrayList<QuadTreeNode>();
		end.setParent(null);
		QuadTreeNode current = null;
		
		start.setParent(null);
		start.gScore = 0;
		start.hScore = heuristicCost(start, end);
		start.fScore = start.gScore + start.hScore;
		
		while(open.size() > 0){
			
			current = open.remove();
			
			if(current == end)
			{
				path = tracePath(end);
				//Save cache
				cache.put(key, path);
				return path;
			}
			
			closed.add(current);
			
			LinkedList<QuadTreeNode> list = map.getNeighbors(current);
			
			Iterator<QuadTreeNode> itr = list.iterator();  
			while(itr.hasNext()){  
				
				QuadTreeNode neighbor = itr.next();  
				
				if(closed.contains(neighbor)){
					continue;
				}
				
				if(!neighbor.hasData()){
					//wage score
					float gScore = current.gScore + heuristicCost(current, neighbor);
					
					boolean tentative = false;
					
					if (!open.contains(neighbor)){
						open.add(neighbor);
						tentative = true;
					}else if(gScore < neighbor.gScore ){
						tentative = true;
					}
					
					if(tentative){
						neighbor.gScore = gScore;
						neighbor.hScore = heuristicCost(neighbor, end);
						neighbor.fScore = neighbor.gScore + neighbor.hScore;
						neighbor.setParent(current);
					}
					
				}
			}  
		}
		
		//Fail
		cache.put(key, null);
		return null;
	}
}
