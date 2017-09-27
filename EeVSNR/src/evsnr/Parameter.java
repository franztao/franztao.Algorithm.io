/**
 * 
 */

package evsnr;

public class Parameter {

  // Parameter
  public static final int addNewVirNodeNewSubNodeCost = 1000000;
  public static final int addNewVirNodeCost = 10000;
  public static final int transformExistedNodeCost = 0;
  public static final int addNodeComputaionCost = 30;
  public static final int addEdgeBandwithCost = 1;
  public static final int RelativeCostbetweenComputingBandwidth = addNodeComputaionCost / addEdgeBandwithCost;

  public static final boolean FailureDependent = true;
  public static final boolean FailureIndependent = false;

  // Algorithm
  public static final int AlgorithmFranz = 0;
  public static final int AlgorithmMILP = 1;

  public static final int MatchMethodILP = 1;
  public static final int MatchMethodDP = 2;
  public static final int MatchMethod = MatchMethodILP;

  public static final int Ran = 0;
  public static final int Min = 1;

  // /home/franz/franzDocuments/eclipse4cworkspace/EeVSNR
  public static final String FileAbsolutePath = "C:\\Users\\Taoheng\\Desktop\\NFT4VNR";

  public static final boolean isSameVNQ4EveryTime = true;
  public static final boolean IsReleaseVNafterEVNFailure = false;
  public static final int ExperimentTimes = 10;

  public static final long SubstrateNewtorkRunTimeInterval = 1000;// 30000
  public static final long unitTimeInterval = 1;
  public static final double requestAppearProbability = 0.1;// 0.1
  public static final long VNRequestsDuration = 1;
  public static final long VNRequestsContinueTimeMinimum = 25;
  public static final long VNRequestsContinueTimeMaximum = 500;
  // ExperimentPicture
  public static final int ExperimentPicturePlotNumber = 25;

  // service parameter
  public static final int ServiceNumber = 6;
  public static final double SerivecProbability = 0.5;

  // SubStrate Network Parameter
  // node parameter
  public static final int SubStrateNodeSize = 100;// 100
  public static final int SubStrateNodeComputationMinimum = 30;// 50
  public static final int SubStrateNodeComputationMaximum = 80;// 100
  // edge parameter
  public static final double SubStrateNodenodeProbability = 0.5;
  public static final int SubStrateEdgeBandwithMinimum = 500;
  public static final int SubStrateEdgeBandwithMaximum = 1000;

  // Virtual Network
  // node parameter //2-5 5-8 5-10
  public static final int VirtualNodeSizeMinimum = 5;// 2
  public static final int VirtualNodeSizeMaximum = 15;// 10
  public static final int VirtualNodeComputationMinimum = 15;// 1
  public static final int VirtualNodeComputationMaximum = 30;// 5
  // edge parameter
  public static final double VirtualNodenodeProbability = 0.5;// 0.5
  public static final int VirtualEdgeBandwithMinimum = 30;// 10
  public static final int VirtualEdgeBandwithMaximum = 50;// 20

  public static final int SubStrateFacilityNodeFailDuration = 2000;

}
