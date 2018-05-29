/**
 * 
 */

package sevn;

import org.apache.log4j.Level;

import gurobi.GRB;

/*
 * Survivable Virtual Network Design and Embedding to Survive a Facility Node Failure
 */
public class Parameter
{
    public static final Level logLevel=Level.WARN;
    //
    public static final boolean isNewCriter = true;

    // Parameter
    public static final int addNewVirNodeNewSubNodeCost = 10000000;
    public static final int addNewVirNodeCost = 100;// 100
    public static final int transformExistedNodeCost = 100000;// 100000
    public static final int addNodeComputaionCost = 3;
    public static final int addEdgeBandwithCost = 1;
    public static final int RelativeCostbetweenComputingBandwidth = addNodeComputaionCost / addEdgeBandwithCost;

    public static final boolean IsReleaseVNafterEVNFailure = true;
    public static final boolean IsMultipleNodeMapOneNode = true;
    public static final boolean IsConsiderEdgeBandwith = true;
    
    public static final boolean IsIncrementSum=false;
    
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
    
    public static final char LinearProgramType = GRB.BINARY;// GRB.BINARY
    // VirutualNetworkEmbeddingAlgorithm
    public static final int VNERandom = 0;
    public static final int VNEILP = 1;
    public static final int VNEAlgorithm = VNEILP;
  
    // SNDLib topology number is 12
    public static final int ExperimentTimes = 1;
    public static final long SubstrateNewtorkRunTimeInterval = 600;// 30000
    public static final long unitTimeInterval = 1;
    // ExperimentPicture
    public static final int ExperimentPicturePlotNumber = 20;
    
    // RequestType
    public static final int RequestTypeGeometric = 1;
    public static final int RequestTypePossion = 0;
    public static final int RequestType = RequestTypeGeometric;
    // GeometricDistribution
    public static final double RequestAppearProbability = 0.5;// 0.1
    public static final long RequestPerTimeAppearNum = 1;// 1
    public static final long VirNetDuration = 1;
    // possion distribution
    public static  long PossionMean = 5;

    public static final int VNRequestsLeaseTypeUniform = 0;
    public static final int VNRequestsLeaseTypeExponential = 1;
    public static final int VNRequestsLeaseType = VNRequestsLeaseTypeExponential;
    // uniform
    public static final long VNRequestsContinueTimeMinimum = 1;
    public static final long VNRequestsContinueTimeMaximum = 100;
    public static final long VNRequestsContinueTimeAverage = VNRequestsContinueTimeMaximum / 2;
    // Exponential
    public static long VNRequestsContinueTimeExponentialMean = SubstrateNewtorkRunTimeInterval / 10;
   

    // service parameter
    public static final int FunctionNumber = 15;// 15
    public static final double FunctionTypeProbability = 0.5;// 0.5

    // Virtual Network
    // node parameter //2-5 5-8 5-10
    public static final int VirtualNodeSizeMinimum = 3;// 2
    public static final int VirtualNodeSizeMaximum = 10;// 10
    public static final int VirtualNodeComputationMinimum = 1;// 1
    public static final int VirtualNodeComputationMaximum = 10;// 5
    // edge parameter
    public static final double VirtualNodenodeProbability = 0.5;// 0.5
    public static final int VirtualEdgeBandwithMinimum = 10;// 10
    public static final int VirtualEdgeBandwithMaximum = 20;// 20

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

    public static final int SubStrateNodeComputationMinimum = 20;// 50
    public static final int SubStrateNodeComputationMaximum = 50;// 100
    public static final int SubStrateEdgeBandwithMinimum = 50;// 100
    public static final int SubStrateEdgeBandwithMaximum = 100;// 200

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
    
    
    public static final boolean AllExperiment=true;
    
    public static final int BasicExperiment=1;
    
    public static final int PossionMeanExperiment=2;
    public static final int PossionMeanStart=1;
    public static final int PossionMeanEnd=10;
    public static final int PossionMeanAdd=1;
    
    public static final int ExponentialMeanExperiment=3;
    public static final int ExponentialMeanStart=10;
    public static final int ExponentialMeanEnd=100;
    public static final int ExponentialMeanAdd=10;
    
    public static final int ExperimentType=ExponentialMeanExperiment;
    
    public static String ExperimentFileString;
    
}
