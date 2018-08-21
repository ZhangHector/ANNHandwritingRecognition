package Element;

/* Line.java
 * A line in the network.
 * A line is simply a connection between two nodes, and contains a weight.
 * This weight can be requested and altered. The two nodes that it connects are also
 * saved as pointers.
 */
public class Line {
	private double weight = 0;	
	private Node   fromNode;
	private Node   toNode;
	public Line(Node fromNode, Node toNode) {	this(fromNode, toNode, 0);	}
	public Line(Node fromNode, Node toNode, double weight) {
		this.fromNode = fromNode;
		this.toNode   = toNode;
		this.weight = weight;
	}
	public void setWeight(double weight) 	{	this.weight = weight;	}
	public double getWeight() 				{	return this.weight;		}
	public Node getFromNode()				{	return this.fromNode;	}
	public Node getToNode() 				{	return this.toNode;		}
}
