package GUI;

import ptolemy.plot.PlotLive;
import Utils.Utils;


/* Plotter
 * The plot is an instance of the PTPlot Java Plotter
 * http://ptolemy.eecs.berkeley.edu/java/ptplot5.7/ptolemy/plot/doc/index.htm
 * The plot will auto adjust to the domain of the current values, and the user can zoom by click-drag-releasing
 * on the plot.
 */
public class Plotter extends PlotLive {
	private static final long serialVersionUID = 1L;
	/* variable to determine how many examples (x-axis) we've had already */
	private int counter;
	/* how much the x-axis should increase at every step */
	private int stepSize;
	public Plotter(int stepSize) {
		this.resetAll();
		this.setTitle(Utils.PLOTTER_TITLE);
		this.setTitleFont(Utils.PLOTTER_TITLEFONT);
		this.setXLabel(Utils.PLOTTER_XLABEL);
		this.setYLabel(Utils.PLOTTER_YLABEL);
		this.setXRange(0, 300);
		this.stepSize = stepSize;
		this.addLegend(0, Utils.PLOTTER_LEGEND_TRAIN);
		this.addLegend(1, Utils.PLOTTER_LEGEND_TEST);		
	}
	
	public void addToPlot(double value, int dataset) {
		/* automatically zoom out 200% when the x-axis reaches its maximum */
		if (counter >= this.getXRange()[1])
			this.zoom(0, 0, counter*2, 1);
		this.addPoint(dataset, counter, value, true);
		counter += stepSize;
	}

	public void addPoints() 	{					}
	public void resetCounter()	{	counter = 0;	}
	public void resetAll() {
		this.resetCounter();
		this.setYRange(0, 1);
		this.clear(0);
		this.clear(1);
	}
	
}