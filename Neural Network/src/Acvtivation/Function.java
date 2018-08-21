package Acvtivation;

public final class Function {

	/* Standard Sigmoid Function */
	public final static class Sigmoid {
		public static final double compute(double val) 	{	return 1 / (1 + Math.pow(Math.E, -val));	}
	}

	/* Standard Error Function */ 
	public final static class Error {	
		/* Calculate the error values of the nodes */
		public static double compute(double output, double error) 			{	return output * (1 - output) * error;				}
		 /* Notice that the output nodes primarily define the error values as the difference 
		  * between the target value and the actual output 
		  * All the other nodes simply propagate these errors base on their own output. 
		  */
		public static double computeOutput(double output, double target) 	{	return output * (1 - output) * (target - output);	}	
	}
	
	/* Standard Weight Function */
	public final static class Weight {
		/* Calculate the new weight values of the lines; Momentum can be ignored by entering 0 for it. All other values (except previousWeight) are required. */
		public static final double compute(double learningRate, double momentum, double toError, double fromOutput, double lineWeight, double previousWeight) {
			double deltaWeight = learningRate * toError * fromOutput;
		    return lineWeight + deltaWeight + momentum * previousWeight;
		}
	}
	
}
