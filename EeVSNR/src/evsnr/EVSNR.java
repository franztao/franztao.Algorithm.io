/**
 * 
 */
package evsnr;

/**
 * @author franz
 *
 */
public class EVSNR {
	//Parameter
	public final static int addNewNodeCost = 10000000;
	public final static int transformExistedNodeCost = 100000;
	public final static int addNodeComputaionCost = 1000;
	public final static int addEdgeBandwithCost = 1;

	public final static boolean FailureDependent = true;
	public final static boolean FailureIndependent = false;

	

	public final static long SubstrateNewtorkRunTimeInterval = 3000;
	public final static long unitTimeInterval = 1;
	public final static double requestAppearProbability = 1;// 0.1
	public final static long VNRequestsDuration = 3000;
	public final static long VNRequestsContinueTimeMinimum = 10;
	public final static long VNRequestsContinueTimeMaximum = 100;

	
	//Algorithm
	public final static int AlgorithmFranz = 0;
	public final static int AlgorithmMILP = 1;
	
	public final static int MatchMethodILP = 1;
	public final static int MatchMethodDP = 2;
	public static final int MatchMethod = MatchMethodDP;
	
	public final static int FailureDependentHeuriticAlgorithm = 0;
	public final static int FailureIndependentHeuriticAlgorithm = 1;
	public final static int IPAlgorithm = 2;

	public final static int Ran = 0;
	public final static int Min = 1;

	
	// service parameter
	public final static int ServiceNumber = 5;
	public final static double SerivecProbability = 0.5;

	// SubStrate Network Parameter
	// node parameter
	public final static int SubStrateNodeSize = 40;
	public final static int SubStrateNodeComputationMinimum = 500;
	public final static int SubStrateNodeComputationMaximum = 1000;
	// edge parameter
	public final static double SubStrateNode2nodeProbability = 0.4;
	public final static int SubStrateEdgeBandwithMinimum = 500;
	public final static int SubStrateEdgeBandwithMaximum = 1000;
	
	//Virtual Network
	// node parameter
	public final static int VirtualNodeSize = 4;
	public final static int VirtualNodeSizeMinimum = 2;
	public final static int VirtualNodeSizeMaximum = 10;
	public final static int VirtualNodeComputationMinimum = 2;
	public final static int VirtualNodeComputationMaximum = 10;
		// edge parameter
	public final static double VirtualNode2nodeProbability = 0.4;
	public final static int VirtualEdgeBandwithMinimum = 2;
	public final static int VirtualEdgeBandwithMaximum = 10;
	

}
