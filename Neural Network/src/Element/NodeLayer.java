package Element;

/* NodeLayer.java
 * The NodeLayer is simply a collection of nodes.
 * The size is static (not dynamic like a VectorList), because of efficiency consideration.
 * Please read FeedForward.java for more details on this subject.
 */
public class NodeLayer {
	private Node[] nodes;
	public NodeLayer(int length) 			{	this.nodes = new Node[length];	}
	public void add(Node node, int index) 	{	this.nodes[index] = node;		}
	public Node get(int i) 					{	return this.nodes[i];			}
	public int size() 						{	return this.nodes.length;		}
}
