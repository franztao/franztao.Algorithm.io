/**
 * 
 */
package evsnr;

/**
 * @author franz
 *
 */
public class EVSNR {
	
	public final static String FileAbsolutePath="C:\\Users\\Taoheng\\Desktop\\NFT4VNR\\";
	
	public final static int ExperimentTimes=1;
	
	//Parameter
	public final static int addNewNodeCost = 10000000;
	public final static int transformExistedNodeCost = 100000;
	public final static int addNodeComputaionCost = 3000;
	public final static int addEdgeBandwithCost = 1000;
	public final static int RelativeCostbetweenComputingBandwidth=addNodeComputaionCost/addEdgeBandwithCost;
	
	public final static boolean FailureDependent = true;
	public final static boolean FailureIndependent = false;

	

	public final static long SubstrateNewtorkRunTimeInterval = 500;//50000
	public final static long unitTimeInterval = 1;
	public final static double requestAppearProbability = 1;// 0.1
	public final static long VNRequestsDuration = 10;
	public final static long VNRequestsContinueTimeMinimum = 100;//1
	public final static long VNRequestsContinueTimeMaximum = 120;//1000
	//ExperimentPicture
	public final static int ExperimentPicturePlotNumber=10;
	
	//Algorithm
	public final static int AlgorithmFranz = 0;
	public final static int AlgorithmMILP = 1;
	
	public final static int MatchMethodILP = 1;
	public final static int MatchMethodDP = 2;
	public static final int MatchMethod = MatchMethodILP;
	
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
	public final static int SubStrateNodeSize = 20;
	public final static int SubStrateNodeComputationMinimum = 30;
	public final static int SubStrateNodeComputationMaximum = 30;
	// edge parameter
	public final static double SubStrateNodenodeProbability = 0.4;
	public final static int SubStrateEdgeBandwithMinimum = 30;
	public final static int SubStrateEdgeBandwithMaximum = 30;
	
	//Virtual Network
	// node parameter //2-5 5-8 5-10
	public final static int VirtualNodeSizeMinimum = 2;
	public final static int VirtualNodeSizeMaximum = 5;
	public final static int VirtualNodeComputationMinimum = 2;
	public final static int VirtualNodeComputationMaximum = 10;
		// edge parameter
	public final static double VirtualNodenodeProbability = 0.5;
	public final static int VirtualEdgeBandwithMinimum = 2;
	public final static int VirtualEdgeBandwithMaximum = 10;
	
	public final static int SubStrateFacilityNodeFailDuration=2000;

	
}
