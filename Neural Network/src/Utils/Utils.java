package Utils;

import java.util.Random;

public class Utils {
	
	public final static String ANN 								= "Artificial Neural Network";
	
    /* Database Path */
	public final static String FILE_TRAIN_IMAGES				= "../MNIST/train-images.idx3-ubyte";
	public final static String FILE_TRAIN_LABELS				= "../MNIST/train-labels.idx1-ubyte";
	public final static String FILE_TEST_IMAGES 				= "../MNIST/t10k-images.idx3-ubyte";
	public final static String FILE_TEST_LABELS 				= "../MNIST/t10k-labels.idx1-ubyte";
	
    /* the initial values of the setting of the neural network. */
	public final static int INPUT_LAYER_NODES					= 400;
	public final static int HIDDEN_LAYER_NODES					= 100;
	public final static int OUTPUT_LAYER_NODES					= 10;
	public final static double MOMENTUM 						= 0.3;
	public final static double LEARNING_RATE 					= 0.05;
	public final static double INIT_WEIGHT_BOUNDARIES[] 		= { -0.05, 0.05 };
	public final static int NUM_EXAMPLES 						= 60000;
	public final static int NUM_TEST_EXAMPLES 					= 10000;
	public final static int NUM_ROUNDS   						= 3;   
	
	/* Plotter - String*/
	public final static String PLOTTER_TITLE 					= "Error-Correction Learning";
	public final static String PLOTTER_TITLEFONT				= "Arial Rounded MT Bold";
	public final static String PLOTTER_XLABEL					= "Number of Examples";
	public final static String PLOTTER_YLABEL					= "Error Rate";
	public final static String PLOTTER_LEGEND_TRAIN				= "Training";
	public final static String PLOTTER_LEGEND_TEST				= " Test   ";
	
	/* GUI - Check boxes' String */
	public final static String STRING_DECREASE_CANVAS		 	= "Decrease canvas to 20x20";
	public final static String STRING_SCALE_DOWN 				= "Scale down pixels";
	
	/* GUI - Buttons' String */
	public final static String STRING_TRAIN 					= "TRAIN";
	public final static String STRING_TEST 						= "TEST";
	public final static String STRING_START 					= "START";
	public final static String STRING_STOP 						= "STOP";
	public final static String STRING_RESET 					= "RESET";
	
	/* GUI - Labels' String */
	public final static String STRING_NETWORK_TOPOLOGY 			= "Neural Network Specifications";
	public final static String STRING_INPUT_NODES 				= "Input nodes: ";
	public final static String STRING_HIDDEN_NODES 				= "Hidden nodes: ";
	public final static String STRING_OUTPUT_NODES 				= "Output nodes: ";
	public final static String STRING_MOMENTUM 					= "Momentum (0 = off): ";
	public final static String STRING_LEARNING_RATE 			= "Learning Rate: ";
	public final static String STRING_INIT_WEIGHT				= "Init Weight (w1,w2): ";
	public final static String STRING_DATABASE_SPECS			= "Databse Details";
	public final static String STRING_TRAIN_DATABASE			= "Train Database: ";
	public final static String STRING_TEST_DATABASE				= "Test Database: ";
	public final static String STRING_SIMULATION_MODE			= "Simulation Mode: ";
	public final static String STRING_NUM_EXAMPLES				= "Number of examples: ";
	public final static String STRING_NUM_RUNS					= "Number of runs: ";

	/* Log Message */
	public final static String LOG_INITIAL			 			= ">>  Choose your settings and Train or Test the network.\n>>  First Train and then Test.";
	public final static String LOG_RUN							= "\n>> %s Mode %s\n>>  printing error rate. Graph is updated after training.";
	public final static String LOG_INTERRUPTED					= "\n>> run halted by user.";
	public final static String LOG_AVERAGE_ERROR_VALUE			= "[Digit: %d, Image: %6d] Average Error Value of the Network (Every %3d times): %.8f";
	
	/* Topology Message*/
	public final static String TOPOLOGY_INPUT_NODES				= "input layer:  %d";
	public final static String TOPOLOGY_HIDDEN_NODES			= "\n\thidden layer (%d): %d";
	public final static String TOPOLOGY_OUTPUT_NODES			= "\n\toutput nodes:  %d\n";
	public final static String TOPOLOGY_CONNECTIONS				= "\n\t\tconnections: %d";
	public final static String TOPOLOGY_MOMENTUM				= "\n\t%s\tmomentum: %s";
	
	/* Warning Message */
	public final static String WARNING_TRAINING_FIRST 			= "Cannot test without training first!";
	
	/* Symbol */
	public final static String COMMA		= ",";
	public final static String NEW_LINE		= "\n";
	public final static String EMPTY		= "";
	public final static String SEMICOLON 	= ":";
	public final static String SPACE		= " ";
	
	static Random random = new Random();
	public static Random random() 						{ return random; }
	public final static String getMode(boolean train)	{ return (train ? "Training" : "Testing"); }
}
