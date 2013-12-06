package structure;

import java.util.LinkedList;

public interface QNode {
	public LinkedList<QuadTreeNode> getNeighbors(QuadTreeNode node);
}
