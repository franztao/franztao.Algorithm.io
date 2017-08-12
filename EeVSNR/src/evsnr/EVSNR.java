/**
 * 
 */
package evsnr;

/**
 * @author franz
 *
 */
public class EVSNR {
	
	public final static int MatchMethodILP = 1;
	public final static int MatchMethodDP = 2;
	
	public final static int addNewNodeCost = 10000000;
	public final static int transformExistedNodeCost = 100000;
	public final static int addNodeComputaionCost = 1000;
	public final static int addEdgeBandwithCost = 1;
	
	public final static boolean FailureDependent = true;
	public final static boolean FailureIndependent = false;

	public final static int AlgorithmFranz=0;
	public final static int AlgorithmMILP=1;
	
	public final static long SubstrateNewtorkRunTimeInterval=3000;
	public final static long unitTimeInterval=1;
	public final static double requestAppearProbability=1;//0.1
	public final static long VNRequestsDuration=3000;
	public final static long VNRequestsContinueTimeMinimum=1500;
	public final static long VNRequestsContinueTimeMaximum=1500;
	
	public final static int FailureDependentHeuriticAlgorithm=0;
	public final static int FailureIndependentHeuriticAlgorithm=1;
	public final static int IPAlgorithm=2;
	
	public final static int Ran=0;
	public final static int Min=1;
	
}
