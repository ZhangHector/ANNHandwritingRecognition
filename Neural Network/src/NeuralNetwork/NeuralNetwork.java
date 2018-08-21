package NeuralNetwork;

import Element.LineLayer;
import Element.NodeLayer;

public interface NeuralNetwork {
	void addNodes(int[] networkLayers);
	void connectNodes();
	LineLayer connectLayers(NodeLayer layer1, NodeLayer layer2);
	void train(double[] image, int answer);
	void test(double[] image, int answer);
}
