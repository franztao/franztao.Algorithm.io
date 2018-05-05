/**
 * 
 */

package sevn;

import gurobi.GRB;

/*
 * Survivable Virtual Network Design and Embedding to Survive a Facility Node Failure
 */
public class Parameter
{

    // SNDLib topology number is 12
    public static final int ExperimentTimes = 12;

    public static final char LinearProgramType = GRB.CONTINUOUS;// GRB.BINARY
    // VirutualNetworkEmbeddingAlgorithm
    public static final int VNERandom = 0;
    public static final int VNEILP = 1;
    public static final int VNEAlgorithm = VNEILP;

    // resource shared
    public static final int FailureDependent = 0;
    public static final int FailureIndependent = 1;
    public static final int One2OneProtection = 2;

    // star match method
    public static final int MatchMethodIP = 1;
    public static final int MatchMethodDP = 2;
    public static final int MatchMethod = MatchMethodIP;

    // VNR node fail sequence
    public static final int Ran = 0;
    public static final int Min = 1;

    // /home/franz/franzDocuments/eclipse4cworkspace/EeVSNR
    public static final String FileAbsolutePath = "C:\\Users\\Taoheng\\Desktop\\SeVN";

    //
    public static final boolean isNewCriter = true;

    // Parameter
    public static final int addNewVirNodeNewSubNodeCost = 10000000;
    public static final int addNewVirNodeCost = 100000;// 100
    public static final int transformExistedNodeCost = 100;// 100000
    public static final int addNodeComputaionCost = 3;
    public static final int addEdgeBandwithCost = 1;
    public static final int RelativeCostbetweenComputingBandwidth = addNodeComputaionCost / addEdgeBandwithCost;

    public static final boolean IsReleaseVNafterEVNFailure = false;
    public static final boolean IsMultipleNodeMapOneNode = true;
    public static final boolean IsConsiderEdgeBandwith = true;

    public static final long SubstrateNewtorkRunTimeInterval = 1000;// 30000
    public static final long unitTimeInterval = 1;

    // RequestType
    public static final int RequestTypeGeometric = 1;
    public static final int RequestTypePossion = 0;
    public static final int RequestType = RequestTypePossion;
    // GeometricDistribution
    public static final double RequestAppearProbability = 0.1;// 0.1
    public static final long RequestPerTimeAppearNum = 1;// 1
    public static final long VirNetDuration = 1;
    // possion distribution
    public static final long PossionMean = 5;

    public static final int VNRequestsLeaseTypeUniform = 0;
    public static final int VNRequestsLeaseTypeExponential = 1;
    public static final int VNRequestsLeaseType = VNRequestsLeaseTypeExponential;
    // uniform
    public static final long VNRequestsContinueTimeMinimum = 1;
    public static final long VNRequestsContinueTimeMaximum = 100;
    public static final long VNRequestsContinueTimeAverage = VNRequestsContinueTimeMaximum / 2;
    // Exponential
    public static final long VNRequestsContinueTimeExponentialMean = SubstrateNewtorkRunTimeInterval / 10;

    // ExperimentPicture
    public static final int ExperimentPicturePlotNumber = 25;

    // service parameter
    public static final int ServiceNumber = 15;// 15
    public static final double FunctionTypeProbability = 0.1;// 0.5

    // Virtual Network
    // node parameter //2-5 5-8 5-10
    public static final int VirtualNodeSizeMinimum = 3;// 2
    public static final int VirtualNodeSizeMaximum = 10;// 10
    public static final int VirtualNodeComputationMinimum = 1;// 1
    public static final int VirtualNodeComputationMaximum = 5;// 5
    // edge parameter
    public static final double VirtualNodenodeProbability = 0.5;// 0.5
    public static final int VirtualEdgeBandwithMinimum = 1;// 10
    public static final int VirtualEdgeBandwithMaximum = 10;// 20

    // failure migration
    public static final long SubStrateFacilityNodeFailDuration = SubstrateNewtorkRunTimeInterval
            / ExperimentPicturePlotNumber;

    // -1 sample
    // 1 random
    // 2 data center
    // 0 SNDLib
    public static final int TopologyTypeSNDLib = 0;
    public static final int TopologyTypeSample = -1;
    public static final int TopologyTypeRandom = 1;
    public static final int TopologyTypeDataCenter = 2;
    public static final int TopologyType = TopologyTypeSNDLib;

    public static final int SubStrateNodeComputationMinimum = 10;// 50
    public static final int SubStrateNodeComputationMaximum = 20;// 100
    public static final int SubStrateEdgeBandwithMinimum = 10;// 100
    public static final int SubStrateEdgeBandwithMaximum = 50;// 200

    // Random
    // node parameter
    public static final int SubStrateNodeSize = 30;// 100
    // edge parameter
    public static final double SubStrateNodenodeProbability = 0.75;

    // SNDlib
    public static final String SNDLibFile = "data\\sndlib-networks-native";

    // DataCenter
    public static final int DataCenterLevel = 4;// 4
    public static final int DataCenterAry = 3;// 3
    public static final int DataCenterPMSlots = 10;// 10
    public static final int DataCenterToR2PM = 20;// 20
    public static final int DataCenterCore2Aggregation = 100;// 100
    public static final int DataCenterVNSize = 4;// 4
    public static final int DataCenterVNBandWidth = 1;// 1

}
