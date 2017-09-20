/**
 * 
 */
package evsnr;

/**
 * @author franz
 *
 */
public class EVSNR {

	// Parameter
	public final static int addNewNodeCost = 100000;
	public final static int transformExistedNodeCost = 0;
	public final static int addNodeComputaionCost = 300;
	public final static int addEdgeBandwithCost = 100;
	public final static int RelativeCostbetweenComputingBandwidth = addNodeComputaionCost / addEdgeBandwithCost;

	public final static boolean FailureDependent = true;
	public final static boolean FailureIndependent = false;

	// Algorithm
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
//	/home/franz/franzDocuments/eclipse4cworkspace/EeVSNR
	public final static String FileAbsolutePath = "C:\\Users\\Taoheng\\Desktop\\NFT4VNR";

	
	public final static boolean IsReleaseVNafterEVNFailure=false;
	public final static int ExperimentTimes = 10;

	public final static long SubstrateNewtorkRunTimeInterval =1000;// 30000
	public final static long unitTimeInterval = 1;
	public final static double requestAppearProbability = 0.1;// 0.1
	public final static long VNRequestsDuration = 1;
	public final static long VNRequestsContinueTimeMinimum = 50;
	public final static long VNRequestsContinueTimeMaximum = 500;
	// ExperimentPicture
	public final static int ExperimentPicturePlotNumber = 25;

	// service parameter
	public final static int ServiceNumber = 20;
	public final static double SerivecProbability = 0.25;

	// SubStrate Network Parameter
	// node parameter
	public final static int SubStrateNodeSize = 50;//100
	public final static int SubStrateNodeComputationMinimum = 20;//50
	public final static int SubStrateNodeComputationMaximum = 40;//100
	// edge parameter
	public final static double SubStrateNodenodeProbability = 0.5;
	public final static int SubStrateEdgeBandwithMinimum = 500;
	public final static int SubStrateEdgeBandwithMaximum = 1000;

	// Virtual Network
	// node parameter //2-5 5-8 5-10
	public final static int VirtualNodeSizeMinimum =5;//2
	public final static int VirtualNodeSizeMaximum = 15;//10
	public final static int VirtualNodeComputationMinimum = 1;//1
	public final static int VirtualNodeComputationMaximum = 5;//5
	// edge parameter
	public final static double VirtualNodenodeProbability = 0.5;//0.5
	public final static int VirtualEdgeBandwithMinimum = 5;//10
	public final static int VirtualEdgeBandwithMaximum = 25;//20

	public final static int SubStrateFacilityNodeFailDuration = 2000;

}
