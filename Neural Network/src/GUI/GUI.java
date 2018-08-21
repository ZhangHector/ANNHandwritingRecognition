package GUI;

/*********************************
 *   GUI.java - RUN THIS METHOD  *
 *********************************
 *
 * The start method of the application.
 * Contains the GUI, where the user can set the properties of the neural network.
 * 
 * ---------- FUNCTIONS OF THE GUI EXPLAINED
 *
 * [1] NETWORK TOPOLOGY
 * 
 * Input nodes:   not editable by the user. the number of nodes depend on the input image.
 *                can be changed by preprocessing the image (enabling the checkboxes will
 *                change the input node value);
 *              
 * Hidden nodes:  the number of hidden nodes per layer, examples:
 *                3       creates 1 hidden layer of 3 nodes.
 *                []      not entering a value will create a network without any hidden layers.
 *                       this will lead to underfitting; the nodes cannot hold all the information.
 *                2 4 1   creates 3 hidden layers of respectively 2, 4 and 1 nodes, where the first
 *                       layer will connect to the input layer and the last to the output
 *              
 * Output nodes:  the number of output nodes, is always 10.
 *                every number from the input file is represented in 10 output nodes, where for every
 *                number a different output node will have a target value of 1 and all the others a 
 *                target value of 0.
 * 
 * [2] ALGORTIHM SPECIFICATIONS
 * 
 * Algorithm:     backprop & other
 * 
 * Momentum:      Making the weight update on the nth iteration depend partially on the update that
 *                occured during the (n-1)th iteration.
 *                Set to 0 for no momentum. Values between 0.1 and 0.3 are suggested.
 * 
 * Learning rate: values between 0.05 and 0.1 are suggested
 *                a lower values makes the simulation slower, but more stable.
 *      
 * Initial Weight:the boundaries of the initial values of the weights of the lines.
 *                values separated by a ,
 *                [0,5]
 *                [-1,1]
 *                [13,24.1]
 * 
 * Learning method: x
 * 
 * [3] SIMULATION SPECIFICATIONS
 * 
 * Simulation method: Training or test. first perform a training before doing any test.
 * 					  the correct files for the simulation method are selected automatically, but can
 *                    be changed by choosing another file.
 * 
 * Nr of Examples:    number of examples that the network should traing/test.
 * Nr of Runs:        how often these examples should be inserted into the network.
 * 
 * Decrease canvas:   crop the canvas from 28x28 pixels to 20x20 pixels.
 * 						" the original black and white (bilevel) images from NIST were size normalized to fit in a 
 *     					  20x20 pixel box while preserving their aspect ratio. the images were centered in a 28x28 
 *     					  image by computing the center of mass of the pixels, and translating the image so as to 
 *     					  position this point at the center of the 28x28 field."
 *  
 *     					--- source [http://yann.lecun.com/exdb/mnist/]
 *     
 * Scale image:       take a square of four pixels and reduce it to one pixel.
 *                    this was actually an experiment to speed up the training process,
 *                    it turns out that the results are very poor though.
 *                    
 * Start:             start simulation
 *                    when started, press again to stop. 
 *                    when stopped, press again to resume.
 *
 * Reset:             erase all the values of the current network, aswell as the plot.
 * 
 * [4] PLOT
 * 
 * The plot is an instance of the PTPlot Java Plotter
 * http://ptolemy.eecs.berkeley.edu/java/ptplot5.7/ptolemy/plot/doc/index.htm
 * 
 * The plot will auto adjust to the domain of the current values, and the user can zoom by click-drag-releasing
 * on the plot.
 * 
 * [5] DEBUG WINDOW
 * 
 * Will print the error values every [x] steps. These values are also being plotted.
 * It is suggested to set this to a value around 100, since a value of 1 will plot every individual result.
 * This will create a very messy plot. When a step size of 100 is entered, the average error term over 100
 * examples will be printed and will thus be much more constant.
 * 
 * Also, additional messages are printed in this window.
 * 
 * stepSize can be set in the variables below (global int stepSize)
 * 
 */

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import NeuralNetwork.BackPropagation;
import Utils.Utils;

public class GUI extends Applet implements  ActionListener, ItemListener {
    private static final long serialVersionUID = 1L;
    /* interface variables */
    private Plotter plot;
    private NetworkDrawing drawingPanel;
    private Button startButton, resetButton;
    private TextField trainImagesFile, trainLabelsFile, testImagesFile, testLabelsFile;
    private TextField inputNodes, hiddenNodes, outputNodes;
    private TextField momentumTxt, learningRateTxt, weightBoundaries;
    private TextField numExamples, numRuns;
    private TextArea logPanel;
    private Panel databasePanel, plotPanel, actionContainer, specificationPanel;
    private Panel displayPanel, networkPanel, algorithmPanel, simulationPanel;
    private Panel computationPanel, demonstrationPanel;
    private Choice simulationModeCombo;
	private Checkbox decreaseCanvas, scaleDownPixels;
	
    
    /*  neural network variables */
    private BackPropagation network;
    private int networkLayers[];
    private boolean training  = true;
    private int stepSize = 100;
    private String imageFile = Utils.FILE_TRAIN_IMAGES;
    private String labelFile = Utils.FILE_TRAIN_LABELS;
	/* run in the background and communicate with the [network] object but send results to this class */
    private Runner runThread;
    private Drawer drawThread;
    
    public void init() {
    	setLayout(new BorderLayout());
    	((Frame)getParent().getParent()).setTitle(Utils.ANN);
        initComponents();
        initListener();
    	initPanels();
    	addDatabase();
    	addAction();
    	addNetworkSpecs();
        add(demonstrationPanel, BorderLayout.CENTER);
        add(computationPanel, BorderLayout.SOUTH);
    	resize(1280,640);  
    }
    
    private void initComponents(){
        drawingPanel = new NetworkDrawing();
        plot = new Plotter(stepSize);
        logPanel = new TextArea(Utils.LOG_INITIAL, 5, 20, TextArea.SCROLLBARS_VERTICAL_ONLY);
        trainImagesFile = new TextField(Utils.FILE_TRAIN_IMAGES, 30);
    	trainLabelsFile = new TextField(Utils.FILE_TRAIN_LABELS, 30);
    	testImagesFile = new TextField(Utils.FILE_TEST_IMAGES, 30);
    	testLabelsFile = new TextField(Utils.FILE_TEST_LABELS, 30);
    	trainImagesFile.setEnabled(false);
    	trainLabelsFile.setEnabled(false);
    	testImagesFile.setEnabled(false);
    	testLabelsFile.setEnabled(false);
    	decreaseCanvas = new Checkbox(Utils.STRING_DECREASE_CANVAS);
    	scaleDownPixels = new Checkbox(Utils.STRING_SCALE_DOWN);
		inputNodes = new TextField(Integer.toString(Utils.INPUT_LAYER_NODES), 5);
    	hiddenNodes = new TextField(Integer.toString(Utils.HIDDEN_LAYER_NODES), 15);
    	outputNodes = new TextField(Integer.toString(Utils.OUTPUT_LAYER_NODES), 5);
    	inputNodes.setEnabled(false);
    	outputNodes.setEnabled(false);
        momentumTxt = new TextField(Double.toString(Utils.MOMENTUM), 5);
        learningRateTxt = new TextField(Double.toString(Utils.LEARNING_RATE), 5);
        weightBoundaries = new TextField(Utils.INIT_WEIGHT_BOUNDARIES[0] + Utils.COMMA + Utils.INIT_WEIGHT_BOUNDARIES[1], 5);
        startButton = new Button(Utils.STRING_START);
    	resetButton = new Button(Utils.STRING_RESET);
    	decreaseCanvas.setState(true);
    	scaleDownPixels.setState(false);
    	simulationModeCombo = new Choice();
        simulationModeCombo.addItem(Utils.STRING_TRAIN);
        simulationModeCombo.addItem(Utils.STRING_TEST);
    	numExamples = new TextField(Integer.toString(Utils.NUM_EXAMPLES));
    	numRuns = new TextField(Integer.toString(Utils.NUM_ROUNDS));
    }

    private void initListener(){
        startButton.addActionListener(this);
        resetButton.addActionListener(this);
        decreaseCanvas.addItemListener(this);
        scaleDownPixels.addItemListener(this);
        simulationModeCombo.addItemListener(this);
    }
    
    private void initPanels(){
    	plotPanel = new Panel(new GridLayout(0, 1));
    	databasePanel  = new Panel(new GridLayout(0, 1));
        displayPanel = new Panel(new BorderLayout());
        demonstrationPanel = new Panel(new GridLayout(0, 2));
        actionContainer = new Panel(new GridLayout(1, 0));
        networkPanel  = new Panel(new GridLayout(0, 3));
        algorithmPanel  = new Panel(new GridLayout(0, 3));
        simulationPanel  = new Panel(new GridLayout(0, 3));
        specificationPanel = new Panel(new GridLayout(1, 0));
        computationPanel = new Panel(new BorderLayout());
        displayPanel.add(databasePanel, BorderLayout.NORTH);
        displayPanel.add(plotPanel, BorderLayout.CENTER);
        displayPanel.add(logPanel, BorderLayout.SOUTH);
        demonstrationPanel.add(drawingPanel);
        demonstrationPanel.add(displayPanel);
        specificationPanel.add(networkPanel);
        specificationPanel.add(algorithmPanel);
        specificationPanel.add(simulationPanel);
        computationPanel.add(new Label(Utils.STRING_NETWORK_TOPOLOGY), BorderLayout.NORTH);
        computationPanel.add(specificationPanel, BorderLayout.CENTER);
        computationPanel.add(actionContainer, BorderLayout.SOUTH);
    }
    
    private void addDatabase(){
        plotPanel.add(plot);
    	databasePanel.add(new Label(Utils.STRING_DATABASE_SPECS));
    	databasePanel.add(trainImagesFile);
    	databasePanel.add(testImagesFile);
    	databasePanel.add(trainLabelsFile);
    	databasePanel.add(testLabelsFile);
    }

    private void addAction(){
        actionContainer.add(decreaseCanvas);
        actionContainer.add(scaleDownPixels);
        actionContainer.add(startButton);
        actionContainer.add(resetButton);
    }
    
    private void addNetworkSpecs() {
    	networkPanel.add(new Label(Utils.STRING_INPUT_NODES));
    	networkPanel.add(new Label(Utils.STRING_HIDDEN_NODES));
    	networkPanel.add(new Label(Utils.STRING_OUTPUT_NODES));
    	networkPanel.add(inputNodes);
    	networkPanel.add(hiddenNodes);
    	networkPanel.add(outputNodes);
		algorithmPanel.add(new Label(Utils.STRING_MOMENTUM));
        algorithmPanel.add(new Label(Utils.STRING_LEARNING_RATE));
        algorithmPanel.add(new Label(Utils.STRING_INIT_WEIGHT));
		algorithmPanel.add(momentumTxt);
        algorithmPanel.add(learningRateTxt);
        algorithmPanel.add(weightBoundaries);
        simulationPanel.add(new Label(Utils.STRING_NUM_EXAMPLES));
    	simulationPanel.add(new Label(Utils.STRING_NUM_RUNS));
    	simulationPanel.add(new Label(Utils.STRING_SIMULATION_MODE));
        simulationPanel.add(numExamples);
    	simulationPanel.add(numRuns);
    	simulationPanel.add(simulationModeCombo);
    }
    
    public void addValue(double value, boolean train) {
    	/* red plot for train and blue plot for test */
    	plot.addToPlot(value, (train) ? 0 : 1);
    	drawThread = new Drawer(networkLayers, network.getOptimalWeights(), drawingPanel);
    	drawThread.start();
    }
    
    private void startTraining() {
		networkLayers = getNetworkLayers();
		if (network == null) {
			network = new BackPropagation(networkLayers);
			network.setStepSize(getStepSize());
			network.setInitMinWeight(getInitMinWeight());
			network.setInitMaxWeight(getInitMaxWeight());
			network.setMomentum(getMomentum());
			network.printTopology();
			drawingPanel.addNodes(networkLayers);
		}
		if (!training) {
			plot.resetCounter();
			training = true;
		}
    	runThread = new Runner(this, network);
    	runThread.setTrain(true);
    	runThread.start();
    }
    
    private void startTest() {
    	if (network == null) {
    		Log(Utils.WARNING_TRAINING_FIRST);
    	} else {
    		if (training) {
    			plot.resetCounter();
    			training = false;
    		}
    		runThread = new Runner(this, network);
        	runThread.setTrain(false);
        	runThread.start();
    	}
    }
    
    public void enableAll(boolean enable) {
    	hiddenNodes.setEnabled(enable);
    	momentumTxt.setEnabled(enable);
    	learningRateTxt.setEnabled(enable);
    	weightBoundaries.setEnabled(enable);
    	numExamples.setEnabled(enable);
    	numRuns.setEnabled(enable);
    	simulationModeCombo.setEnabled(enable);
    	decreaseCanvas.setEnabled(enable);
    	scaleDownPixels.setEnabled(enable);
    	resetButton.setEnabled(enable);
    	startButton.setLabel( enable ? Utils.STRING_START : Utils.STRING_STOP);
    }

	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == startButton) {
			if (startButton.getLabel() == Utils.STRING_START) {
				enableAll(false);
				if (simulationModeCombo.getSelectedItem() == Utils.STRING_TRAIN)
					startTraining();
				else
					startTest();
			} else {
				enableAll(true);
				runThread.interrupt();
			}
		} else if (evt.getSource() == resetButton) {
			network = null;
			plot.resetAll();
			logPanel.setText(Utils.EMPTY);
			drawingPanel.reset();
		}
	}

	public void itemStateChanged(ItemEvent e) {	
		if ((e.getSource() == decreaseCanvas) || (e.getSource() == scaleDownPixels)) {
			int inputs = (decreaseCanvas.getState()) ? 400 : 784;
			inputs = (scaleDownPixels.getState()) ? inputs/2 : inputs;
			inputNodes.setText(Integer.toString(inputs));
		} else if (e.getSource() == simulationModeCombo) {
			if (simulationModeCombo.getSelectedItem().equals(Utils.STRING_TRAIN)) {
				imageFile = Utils.FILE_TRAIN_IMAGES;
				labelFile = Utils.FILE_TRAIN_LABELS;
				numExamples.setText(Integer.toString(Utils.NUM_EXAMPLES));
			} else if (simulationModeCombo.getSelectedItem().equals(Utils.STRING_TEST)) {
				imageFile = Utils.FILE_TEST_IMAGES;
				labelFile = Utils.FILE_TEST_LABELS;
				numExamples.setText(Integer.toString(Utils.NUM_TEST_EXAMPLES));
			} else {
				plotPanel = null;
				repaint();
			}
		}
	}

    /* an algorithm to translate the nodes in the GUI to an array of nodes */
    public int[] getNetworkLayers() {
    	String[] hiddens = hiddenNodes.getText().split("\\s+");
    	int numHiddens = (hiddens.length == 1 && hiddens[0].length() == 0) ? 0 : hiddens.length;
    	int[] nodes = new int[numHiddens + 2];
    	nodes[0] = Integer.parseInt(inputNodes.getText());
    	for (int i=0; i<numHiddens; i++)
    		nodes[i+1] = Integer.parseInt(hiddens[i]);
    	nodes[nodes.length-1] = Integer.parseInt(outputNodes.getText());
    	return nodes;
    }

    public void paint( Graphics g ) 	{ }
    public int getStepSize() 			{	return stepSize;	}
    public String getImageFile()		{	return imageFile;	}
    public String getLabelFile()		{	return labelFile;	}
    public boolean getCanvasProcessor() {	return decreaseCanvas.getState(); 	}
    public boolean getScaleProcessor() 	{	return scaleDownPixels.getState();	}
    public int getNumRuns() 				{	return Integer.parseInt(numRuns.getText());	}
    public int getNumExamples() 			{	return Integer.parseInt(numExamples.getText());	}
    public void Log(String log) 		{	logPanel.append(log + Utils.NEW_LINE);	}
    public double getMomentum() 		{	return Double.parseDouble(momentumTxt.getText());	}
    public double getLearningRate() 	{	return Double.parseDouble(learningRateTxt.getText());	}
    public double getInitMinWeight()	{	return Double.parseDouble(weightBoundaries.getText().split("\\s*,\\s*")[0]);	}
    public double getInitMaxWeight()	{	return Double.parseDouble(weightBoundaries.getText().split("\\s*,\\s*")[1]);	}

}