package Element;

/* OutputNode.java
 * Simple extension to the Node class, an output node should also contain a target value
 */
public class OutputNode extends Node  {	
	private double target  = 0;
	public void setTarget(double target) 	{	this.target = target;	}
	public double getTarget() 				{	return this.target;		}
}
