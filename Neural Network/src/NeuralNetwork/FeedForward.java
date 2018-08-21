package NeuralNetwork;

import Element.Line;
import Element.LineLayer;
import Element.Node;
import Element.NodeLayer;
import Element.OutputNode;
import Utils.Utils;

public abstract class FeedForward implements NeuralNetwork {	
	protected NodeLayer inputLayer;
	protected NodeLayer[] hiddenLayers;
	protected NodeLayer outputLayer;
	protected LineLayer[] lineLayers;
	
	public abstract void train(double[] image, int answer);
	public abstract void test(double[] image, int answer);
		
	public FeedForward(int[] networkLayers) {
		addNodes(networkLayers);
		connectNodes();
	}

	public void addNodes(int[] networkLayers) {
		// first initialize all the layers with their corresponding size (this is
		// what i hate about arrays)
		inputLayer = new NodeLayer(networkLayers[0]);
		hiddenLayers = new NodeLayer[networkLayers.length-2];
		outputLayer = new NodeLayer(networkLayers[networkLayers.length-1]);
		// now add the Nodes to the input layer
		for (int i=0; i<networkLayers[0]; i++)
			inputLayer.add(new Node(), i);
		// then all hidden nodes to their corresponding layer
		for (int i=1; i<networkLayers.length - 1; i++) {
			NodeLayer tempNodeLayer = new NodeLayer(networkLayers[i]);
			for (int j=0; j<networkLayers[i]; j++)
				tempNodeLayer.add(new Node(), j);
			hiddenLayers[i-1] = tempNodeLayer;
		}
		/* finally add output nodes */
		for (int i=0; i<networkLayers[networkLayers.length-1]; i++)
			outputLayer.add(new OutputNode(), i);
	}
	
	public void connectNodes() {
		NodeLayer connectingOutputLayer;
		// lineLayers: an array of LineLayer that contain all the layers
		// of the lines. first determine the size of this array
		// connect at least the input to the output (1), for every
		// hidden layer we need an extra line layer
		lineLayers = new LineLayer[hiddenLayers.length + 1];
		// connect input node to next layer, is this a hidden layer?
		if (hiddenLayers.length > 0) {
			// there is at least one hidden node layer, so connect every node in the first hidden layer to all input nodes
			lineLayers[0] = connectLayers(inputLayer, hiddenLayers[0]);
			// now iterate over all hidden layers and connect them to each other
			for (int i=1; i<hiddenLayers.length; i++)
				lineLayers[i] = connectLayers(hiddenLayers[i-1], hiddenLayers[i]);
			// make sure that the output layer will connect to the last hidden layer			
			connectingOutputLayer = hiddenLayers[hiddenLayers.length - 1];
		} else {
			// there is no hidden layer, so let the output layer connect to the input layer
			connectingOutputLayer = inputLayer;
		}
		// connect every output node to all the nodes in the connectingOutputLayer
		lineLayers[lineLayers.length - 1] = connectLayers(connectingOutputLayer, outputLayer);
	}
		
	/* the core of the FeedForward algorithm: connect every node of layer1 to all the nodes of layer2 */
	public LineLayer connectLayers(NodeLayer layer1, NodeLayer layer2) {
		/* first determine the size of the LineLayer */
		LineLayer tempLineLayer = new LineLayer(layer1.size() * layer2.size());
		for (int i=0; i<layer2.size(); i++)
			for (int j=0; j<layer1.size(); j++)
				/* iterate over all the Nodes in layer1 and connect them to every node of layer 2. first determine position in the array. then add the line to the LineLayer */
				tempLineLayer.add(new Line(layer1.get(j), layer2.get(i)), layer1.size() * i + j);
		return tempLineLayer;	
	}
				
	public void inputExample(double[] image) {	
		for (int i=0; i<image.length; i++) 
			inputLayer.get(i).setOutput(image[i]);
	}
		
	public String printTopology() {
		String str = "";
		str += String.format(Utils.TOPOLOGY_INPUT_NODES, inputLayer.size());
		str += String.format(Utils.TOPOLOGY_CONNECTIONS, lineLayers[0].size());
		for (int i=0; i<hiddenLayers.length; i++) {
			str += String.format(Utils.TOPOLOGY_HIDDEN_NODES, i, hiddenLayers[i].size());
			str += String.format(Utils.TOPOLOGY_CONNECTIONS, lineLayers[i + 1].size());
		}
		str += String.format(Utils.TOPOLOGY_OUTPUT_NODES, outputLayer.size());
		return str;
	}
	
	protected void resetNodes() {
		for (int i=0; i<hiddenLayers.length; i++)
			for (int j=0; j<hiddenLayers[i].size(); j++)
				hiddenLayers[i].get(j).reset();		
		for (int i=0; i<outputLayer.size(); i++)
			this.outputLayer.get(i).reset();
	}
}