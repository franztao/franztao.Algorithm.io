/**
 * 
 */

package evsnr;

public class Parameter {

  // Parameter
  public static final int addNewVirNodeNewSubNodeCost = 10000000;
  public static final int addNewVirNodeCost = 100000;
  public static final int transformExistedNodeCost = 0;
  public static final int addNodeComputaionCost = 1000;
  public static final int addEdgeBandwithCost = 1;
  public static final int RelativeCostbetweenComputingBandwidth = addNodeComputaionCost
      / addEdgeBandwithCost;

  public static final boolean FailureDependent = true;
  public static final boolean FailureIndependent = false;

  public static final int MatchMethodIP = 1;
  public static final int MatchMethodDP = 2;
  public static final int MatchMethod = MatchMethodIP;

  public static final int Ran = 0;
  public static final int Min = 1;

  // /home/franz/franzDocuments/eclipse4cworkspace/EeVSNR
  public static final String FileAbsolutePath = "C:\\Users\\Taoheng\\Desktop\\NFT4VNR";

  public static final boolean IsSameVirNet4EveryTime = true;
  public static final boolean IsReleaseVNafterEVNFailure = false;
  public static final boolean IsMultopleNodeMapOneNode = true;
  public static final int ExperimentTimes = 5;

  public static final long SubstrateNewtorkRunTimeInterval = 500;// 30000
  public static final long unitTimeInterval = 1;
  public static final double RequestAppearProbability = 1;// 0.1
  public static final long VirNetDuration = 1;
  public static final long VNRequestsContinueTimeMinimum = 50;
  public static final long VNRequestsContinueTimeMaximum = 500;
  // ExperimentPicture
  public static final int ExperimentPicturePlotNumber = 50;

  // service parameter
  public static final int ServiceNumber = 1;//15
  public static final double SerivecProbability = 1;//0.5

  // SubStrate Network Parameter
  // node parameter
  public static final int SubStrateNodeSize = 50;// 100
  public static final int SubStrateNodeComputationMinimum = 15;// 50
  public static final int SubStrateNodeComputationMaximum = 30;// 100
  // edge parameter
  public static final double SubStrateNodenodeProbability = 0.75;
  public static final int SubStrateEdgeBandwithMinimum = 1000;
  public static final int SubStrateEdgeBandwithMaximum = 2000;

  // Virtual Network
  // node parameter //2-5 5-8 5-10
  public static final int VirtualNodeSizeMinimum = 2;// 2
  public static final int VirtualNodeSizeMaximum = 6;// 10
  public static final int VirtualNodeComputationMinimum = 5;// 1
  public static final int VirtualNodeComputationMaximum = 10;// 5
  // edge parameter
  public static final double VirtualNodenodeProbability = 0.5;// 0.5
  public static final int VirtualEdgeBandwithMinimum = 5;// 10
  public static final int VirtualEdgeBandwithMaximum = 10;// 20

  public static final int SubStrateFacilityNodeFailDuration = 2000;

}
