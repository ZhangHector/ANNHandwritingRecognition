package GUI;

import java.io.IOException;

import NeuralNetwork.BackPropagation;
import mnist.tools.MnistManager;
import Utils.Utils;

/* Runner
 * Thread object that will work the neural network.	
 * Is capable of both training and testing and does image processing as well.
 * It reports results back to its parent, which is a GUI.
 * Gets all the required settings for the network from the parent.
 * The thread dies when finished, or when interrupted.
 */ 
/*	THE MNIST DATABASE of handwritten digits
 * The MNIST database of handwritten digits, available from this page, has a training set of 60,000 examples, and a test set of 10,000 examples. 
 * It is a subset of a larger set available from NIST. The digits have been size-normalized and centered in a fixed-size image.
 * It is a good database for people who want to try learning techniques and pattern recognition methods on real-world data while spending minimal efforts on preprocessing and formatting.
 * 		train-images-idx3-ubyte.gz:  training set images (9912422 bytes) 
 *		train-labels-idx1-ubyte.gz:  training set labels (28881 bytes) 
 *		t10k-images-idx3-ubyte.gz:   test set images (1648877 bytes) 
 *		t10k-labels-idx1-ubyte.gz:   test set labels (4542 bytes)
 * The original black and white (bilevel) images from NIST were size normalized to fit in a 20x20 pixel box while preserving their aspect ratio. 
 * The resulting images contain grey levels as a result of the anti-aliasing technique used by the normalization algorithm. 
 * The images were centered in a 28x28 image by computing the center of mass of the pixels, and translating the image so as to position this point at the center of the 28x28 field.
 *	[LeCun et al., 1998a]
 *		Y. LeCun, L. Bottou, Y. Bengio, and P. Haffner. "Gradient-based learning applied to document recognition." Proceedings of the IEEE, 86(11):2278-2324, November 1998.
 */
public class Runner extends Thread {
	private GUI parent;
	private MnistManager m;
	private boolean train;
	BackPropagation network;
	
	public Runner(GUI parent, BackPropagation network) {
		this.parent = parent;
		this.network = network;
	}

	public void run() {
		try {
			m = new MnistManager(parent.getImageFile(), parent.getLabelFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
    	parent.Log(String.format(Utils.LOG_RUN, Utils.getMode(train), network.printTopology()));
    	if (!train) 
    		network.loadOptimalWeights(); 
    	for (int run=0; run<parent.getNumRuns(); run++) {
	    	for (int example=1; example<parent.getNumExamples(); example++) { 
	    		m.setCurrent(example); /* load image */
				/* pre-process image according to setting defined by user*
				/* the correct answers (what the network should answer) */
				try {
					if (train)
						network.train(processImage(m.readImage(), parent.getCanvasProcessor(), parent.getScaleProcessor()), m.readLabel());
					else
						network.test(processImage(m.readImage(), parent.getCanvasProcessor(), parent.getScaleProcessor()), m.readLabel());
					if (example % parent.getStepSize() == 0) {
						parent.Log(String.format(Utils.LOG_AVERAGE_ERROR_VALUE, m.readLabel(), example, parent.getStepSize(), network.getCurStepError()));
						parent.addValue(network.getCurStepError(), train);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (interrupted()) {
					parent.Log(Utils.LOG_INTERRUPTED);
					return;
				}
			}
    	}
    	parent.enableAll(true);
    }
	
	 /* preprocess the image according to the values of the preProcessor, see GUI.java for explanation about this technique */
    private double[] processImage(int image[][], boolean canvas, boolean scaled) {
    	int imgScaled[][] = scaled ? scaleImage(image) : image;
    	int imgCanvas = canvas ? 4 : 0;
    	imgCanvas = scaled ? imgCanvas/2 : imgCanvas;
    	int dim1 = imgScaled.length-(imgCanvas*2);
    	int dim2 = imgScaled[0].length-(imgCanvas*2);
		double ret[] = new double[(dim1*dim2)];
		for (int i=0; i<dim1; i++)
			for (int j=0; j<dim2; j++)
				/* first scale values to [0,1], enter values in the input nodes */
				ret[j + dim2*i] = scale(imgScaled[i+imgCanvas][j+imgCanvas]);
		return ret;
	}
    
    private int[][] scaleImage(int image[][]) {
    	int img[][] = new int[image.length/2][image[0].length/2];
    	for (int i=0; i<img.length; i+=2)
    		for (int j=0; j<img[0].length; j+=2)
    	  		img[i][j] = (image[i][j] + image[i+1][j] + image[i][j+1] + image[i+1][j+1])/4;
    	return img;
    }
    
    /* use this to scale the inputs to [0,1] */
	private double scale(double num) 	{	return num / 255;		}
	public void setTrain(boolean train) {	this.train = train;		}
}