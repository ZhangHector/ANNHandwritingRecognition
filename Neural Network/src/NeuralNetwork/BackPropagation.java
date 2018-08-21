package NeuralNetwork;

import Acvtivation.Function;
import Element.Line;
import Element.LineLayer;
import Element.Node;
import Element.NodeLayer;
import Element.OutputNode;
import Utils.Utils;

/* BackPropagation
 * BackPropagation is an implementation of the FeedForward Neural Network that
 * propagates the input through the network, depending on the weights of the
 * Lines that connect the Nodes. Then it determines an error term at the output 
 * and propagates this error term back through the network. From these error terms, 
 * the weights of the Lines are being updated. This process iterates until an optimal
 * values has been found.
 * As you can see, this implementation has a lot more functionality that the FeedForward
 * algorithm. I want to make clear that all these functionalities are not a property of the
 * Neural Network, but a property of Back propagation. That is why I have not implemented
 * this method in the Node and Line classes. They only have very simple values, and all
 * the calculations are done in this class.
 * I did use additional classes for the error, sigmoid and weight functions. This allows
 * one to easily change them without having to dig in this class.
 */
public class BackPropagation extends FeedForward {
	/* algorithm variables - are being initialized by the GUI */
	protected double LEARNING_RATE 	= 0.5;
	protected double MOMENTUM 		= 0.3;
	protected double MIN_WEIGHT 	= -0.05;
	protected double MAX_WEIGHT 	= 0.05;
	/* error feedback variables */
	protected int rounds = 0, stepSize = 100;
	protected double[][] previousDeltaWeight;
	protected double[][] averageWeight;
	protected double[][] minErrorWeight;
	protected double minError;
	protected double curStepError;
	
	/* create network topology based on networkLayers, initialize the momentum and the counters for error feedback */
	public BackPropagation(int[] networkLayers) {
		super(networkLayers);
		initMomentum();
		initCounters();
		initWeights();
	}

	public void train(double[] image, int answer) {
		inputExample(image);
		// set target of all output values
		setOutputTargets(answer);
		// now computer output of every unit
		// first reset all the netto values and errors in the nodes to 0
		resetNodes();
		// propagate input forward through the network
		propagateInput();
		// propagate errors backward through the network
		propagateError();
		// process these error for error feedback
		updateStepError();		
	}
	
	public void test(double[] image, int answer) {	
		// very similar to train() method, except that we don't
		// propagate the error back since we are only interested in
		// the error values at the output Nodes.
		inputExample(image);
		setOutputTargets(answer);
		propagateInput();
		// different update step because we don't keep track of anything 
		updateTestStep();
	}
	
	/* propagate the output value of the input Nodes all the way to the output Nodes using the Sigmoid function */
	private void propagateInput() {
		for (int i=0; i<lineLayers.length; i++) {	
			for (int j=0; j<lineLayers[i].size(); j++) {
				Line curLine = lineLayers[i].get(j);
				// propagate the output of the fromNode to the toNode
				// the output depends on the weight of this line.
				// toNode(output) += fromNode(output) * weight
				// NOTE: the values are being added for all lines in this Line Layer,
				//       only after all the lines have done this, the sigmoid function
				//       is being used. this has to advantage that the sum of the inputs
				//       of the Nodes and the output can be stored in one variable.
				Node toNode = curLine.getToNode();
				Node fromNode = curLine.getFromNode();
				toNode.incOutput(fromNode.getOutput() * curLine.getWeight());				
			}
			// now use the sigmoid function to calculate output values of the Nodes in this Layer
			// if there are no more hidden layers, we are connecting it to the output layer.
			NodeLayer curLayer = (i < hiddenLayers.length) ? hiddenLayers[i] : outputLayer;
			for (int j=0; j<curLayer.size(); j++)
				curLayer.get(j).setOutput(Function.Sigmoid.compute(curLayer.get(j).getOutput()));
		}
	}

	/* propagate the errors back through the network, starting at the output nodes */
	private void propagateError() {	
		// for each network output unit, calculate its error term
		for (int i=0; i<outputLayer.size(); i++) {
			OutputNode curNode = ((OutputNode) outputLayer.get(i));
			double curOutput = curNode.getOutput();
			double errorValue = Function.Error.computeOutput(curOutput, curNode.getTarget());
			curNode.setError(errorValue);
		}
		LineLayer curLineLayer;
		for (int i=(lineLayers.length-1); i>=0; i--) {
			// calculate error values of the nodes by propagating previous error using the lines
			curLineLayer = lineLayers[i];
			for (int j=0; j<curLineLayer.size(); j++) {
				Line curLine = curLineLayer.get(j);
				Node toNode = curLine.getToNode();
				Node fromNode = curLine.getFromNode();
				fromNode.incError(toNode.getError() * curLine.getWeight());
				// and update the weight of this line
				// NOTE: the line update function was previously in a separated method.
				//       I have put it here because a network can consist of many Lines, and
				//       it is a big bottleneck if I would use another method to iterate over
				//       all the lines again, while I can also do it here and catch two flies
				//       in once (is that an English expression?)
				double deltaWeight = LEARNING_RATE * toNode.getError() * fromNode.getOutput();
			    curLine.setWeight(Function.Weight.compute(LEARNING_RATE, MOMENTUM, toNode.getError(), fromNode.getOutput(), curLine.getWeight(), previousDeltaWeight[i][j]));
			    previousDeltaWeight[i][j] = deltaWeight;
			}
			// use error function to calculate error values of the nodes of this layer
			// if we are at the last lineLayer, then use the inputLayer. else, if there
			// are more than one lineLayers, we must have hiddenLayers.
			NodeLayer curLayer = (i > 0) ? hiddenLayers[i - 1] : inputLayer;
			for (int j=0; j<curLayer.size(); j++) {
				Node curNode = curLayer.get(j);				
				curNode.setError(Function.Error.compute(curNode.getOutput(), curNode.getError()));
			}
		}
	}

	private void initMomentum() {
		int maxLines = 0;
		// find the maximal amount of Lines in a Line Layer
		for (int i=0; i<lineLayers.length; i++) 
			maxLines = (lineLayers[i].size() > maxLines) ? lineLayers[i].size() : maxLines;
		// initialize the array to this value
		// NOTE: this leads to empty calls in the array, but still it is much faster than using a VectorList
		previousDeltaWeight = new double[lineLayers.length][maxLines];
		// initialize all previous weights to 0
		for (int i=0; i<lineLayers.length; i++)	
			for (int j=0; j<lineLayers[i].size(); j++) 
				previousDeltaWeight[i][j] = 0.0;
	}

	/* initialize counter for error feedback */
	private void initCounters() {
		curStepError = 0;
		rounds = 0;
		stepSize = 100;
		minError = 1;
		averageWeight = new double[lineLayers.length][lineLayers[0].size()];
		minErrorWeight = new double[lineLayers.length][lineLayers[0].size()];
	}

	public void initWeights() {
		for (int i=0; i<lineLayers.length; i++)
			for (int j=0; j<lineLayers[i].size(); j++) 
				lineLayers[i].get(j).setWeight(MIN_WEIGHT + Utils.random().nextDouble() * (Math.abs(MIN_WEIGHT) + Math.abs(MAX_WEIGHT)));
	}

	private void saveCurrentWeight() {
		for (int i=0; i<lineLayers.length; i++)
			for (int j=0; j<lineLayers[i].size(); j++)
				minErrorWeight[i][j] = averageWeight[i][j] / stepSize;
	}
	
	public void increaseAverageWeight() {
		for (int i=0; i<lineLayers.length; i++)
			for (int j=0; j<lineLayers[i].size(); j++)
				averageWeight[i][j] += lineLayers[i].get(j).getWeight();
	}
	
	public void loadOptimalWeights() {
		for (int i=0; i<lineLayers.length; i++)
			for (int j=0; j<lineLayers[i].size(); j++)
				lineLayers[i].get(j).setWeight(minErrorWeight[i][j]);
	}

	private void updateStepError() {
		// update only if we have run for <stepSize> times
		// rounds > 0, because 0 % x = 0
		if ((rounds % stepSize == 0) && (rounds > 0)) {
			if (curStepError < minError) {
				saveCurrentWeight();
				minError = curStepError;
			}
			curStepError = getCurrentError();
			rounds = 0;
		} else {
			double curError = getCurrentError();
			// update the average error by multiplying the previous error level
			// with the amount of runs, adding the current error level to it
			// and dividing this by the number of rounds+1
			curStepError = (curError + curStepError * rounds) / (rounds + 1);
			increaseAverageWeight();
		}
		rounds++;
	}

	private void updateTestStep() {
		if (rounds % stepSize == 0) {
			curStepError = getCurrentError();
			rounds = 0;
		} else {
			curStepError = (getCurrentError() + curStepError * rounds) / (rounds + 1);
			increaseAverageWeight();
		}
		rounds++;
	}
	
	private void setOutputTargets(int answer) {
		for (int i=0; i<outputLayer.size(); i++) 
			((OutputNode) outputLayer.get(i)).setTarget((i==answer)?1:0);
	}

	public double getCurrentError() {
		/* get the overall error of this network by applying the standard error function: 0.5 * (target - output) ^ 2 */
		double sum = 0;
		for (int i=0; i<outputLayer.size(); i++)
			sum += Math.pow(((OutputNode) outputLayer.get(i)).getTarget() - ((OutputNode) outputLayer.get(i)).getOutput(), 2);
		return sum * 0.5;
	}

	public void setInitMinWeight(double MIN_WEIGHT) {	this.MIN_WEIGHT = MIN_WEIGHT;	}
	public void setInitMaxWeight(double MAX_WEIGHT) {	this.MAX_WEIGHT = MAX_WEIGHT;	}
	public void setStepSize(int stepSize) 			{	this.stepSize = stepSize;		}
	public void setMomentum(double momentum) 		{	this.MOMENTUM = momentum;		}
	public double getCurStepError() 				{	return curStepError;			}
	public double[][] getOptimalWeights() 			{	return minErrorWeight;			}
	public String printTopology() 					{	return String.format(Utils.TOPOLOGY_MOMENTUM, super.printTopology(), MOMENTUM);	}
}