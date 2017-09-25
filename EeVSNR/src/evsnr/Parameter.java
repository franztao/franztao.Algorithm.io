/**
 * 
 */

package evsnr;

public class Parameter {

  // Parameter
  public static final int addNewVirNodeNewSubNodeCost = 1000000;
  public static final int addNewVirNodeCost = 10000;
  public static final int transformExistedNodeCost = 100;
  public static final int addNodeComputaionCost = 3;
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
  public static final int ExperimentTimes = 5;

  public static final long SubstrateNewtorkRunTimeInterval = 500;// 30000
  public static final long unitTimeInterval = 1;
  public static final double requestAppearProbability = 0.1;// 0.1
  public static final long VNRequestsDuration = 1;
  public static final long VNRequestsContinueTimeMinimum = 25;
  public static final long VNRequestsContinueTimeMaximum = 250;
  // ExperimentPicture
  public static final int ExperimentPicturePlotNumber = 25;

  // service parameter
  public static final int ServiceNumber = 1;
  public static final double SerivecProbability = 1;

  // SubStrate Network Parameter
  // node parameter
  public static final int SubStrateNodeSize = 100;// 100
  public static final int SubStrateNodeComputationMinimum = 50;// 50
  public static final int SubStrateNodeComputationMaximum = 100;// 100
  // edge parameter
  public static final double SubStrateNodenodeProbability = 0.7;
  public static final int SubStrateEdgeBandwithMinimum = 500;
  public static final int SubStrateEdgeBandwithMaximum = 1000;

  // Virtual Network
  // node parameter //2-5 5-8 5-10
  public static final int VirtualNodeSizeMinimum = 5;// 2
  public static final int VirtualNodeSizeMaximum = 20;// 10
  public static final int VirtualNodeComputationMinimum = 15;// 1
  public static final int VirtualNodeComputationMaximum = 30;// 5
  // edge parameter
  public static final double VirtualNodenodeProbability = 0.6;// 0.5
  public static final int VirtualEdgeBandwithMinimum = 5;// 10
  public static final int VirtualEdgeBandwithMaximum = 25;// 20

  public static final int SubStrateFacilityNodeFailDuration = 2000;

}
