package Element;

/* LineLayer.java
 * The LineLayer is simply a collection of lines.
 * The size is static (not dynamic like a VectorList), because of efficiency consideration.
 * Please read FeedForward.java for more details on this subject.
 */
public class LineLayer {
	private Line[] lines;
	public LineLayer(int length) 			{	this.lines = new Line[length];	}
	public void add(Line line, int index) 	{	this.lines[index] = line;		}
	public Line get(int i) 					{	return this.lines[i];			}
	public int size() 						{	return this.lines.length;		}
}