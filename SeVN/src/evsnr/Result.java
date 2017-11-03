/**
 * 
 */

package evsnr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import virtualnetwork.VirtualNetwork;

/**
 * Result.
 * 
 * @author Taoheng
 *
 */
public class Result {

  String fileAbsolutePath = Parameter.FileAbsolutePath;//
  String dataFilePathString = "/Data/";
  String prefix = "\\newcommand{\\";

  double[] arrayAcceptionRatioSvn;
  double[] arrayAcceptionRatioVn;
  int acceptionRatioDataLength;

  public int[] arrayarrayCostEdgeBwAcc;
  public int[] arrayCostNodeCpAcc;
  public int[] arrayUtilizationNodeAcc;

  private int[] arrayUtilizationSubNode;
  private int[] arrayCostNodeCp;
  private int[] arrayCostEdgeBw;

  private int[] arrayUtilizationEdgeAcc;
  private int[] arrayUtilizationSubEdge;

  public int recordDataLength;

  public int[] arrayUtilizationVirNodeAcc;
  public int[] arrayRevenueEdgeBwAcc;
  public int[] arrayRevenueNodeCpAcc;
  private int[] arrayRevenueNodeCp;
  private int[] arrayRevenueEdgeBw;
  private int[] arrayUtilizationVirNode;

  public int revenueDataLength;
  private int[] arrayUtilizationVirEdgeAcc;
  private int[] arrayUtilizationVirEdge;

  private int[] arrayVirNetReqAcc;
  private int[] arraySurNetReqAcc;
  private int[] arrayVirNetReq;
  private int[] arraySurNetReq;

  public int revenueUsedNode;
  public int revenueUsedEdge;
  public int revenueNodeComputation;
  public int revenueEdgeBandwith;
  public int revenueVirNetNum;
  public int revenueSubNetNum;

  public int costUsedNode;
  public int costUsedEdge;
  public int costNodeComputation;
  public int costEdgeBandwith;

  public int revenueUsedNodeSum;
  public int revenueUsedEdgeSum;
  public int revenueNodeComputationSum;
  public int revenueEdgeBandwithSum;
  public int revenueVirNetNumSum;
  public int revenueSubNetNumSum;

  public int costUsedNodeSum;
  public int costUsedEdgeSum;
  public int costNodeComputationSum;
  public int costEdgeBandwithSum;

  /**
   * Result.
   */
  public Result() {
    arrayAcceptionRatioSvn = new double[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayAcceptionRatioVn = new double[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];

    arrayarrayCostEdgeBwAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayCostNodeCpAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayUtilizationNodeAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];

    arrayCostEdgeBw = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayCostNodeCp = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayUtilizationSubNode = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];

    arrayUtilizationEdgeAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayUtilizationSubEdge = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];

    arrayUtilizationVirNodeAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayRevenueEdgeBwAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayRevenueNodeCpAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];

    arrayRevenueNodeCp = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayRevenueEdgeBw = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayUtilizationVirNode = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];

    arrayVirNetReqAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arraySurNetReqAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayVirNetReq = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arraySurNetReq = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayUtilizationVirEdgeAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayUtilizationVirEdge = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    acceptionRatioDataLength = 0;
    recordDataLength = 0;
  }

  void recordTexParameter() {
    try {
      File flTexFileWriter = new File(fileAbsolutePath + dataFilePathString + "number.tex");

      if (!flTexFileWriter.exists()) {
        try {
          flTexFileWriter.createNewFile();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      FileWriter fwTexFileWriter = new FileWriter(flTexFileWriter);
      fwTexFileWriter.write(prefix + "addNewNodeCost" + "}{" + Parameter.addNewVirNodeCost + "}\n");
      fwTexFileWriter.write(
          prefix + "transformExistedNodeCost" + "}{" + Parameter.transformExistedNodeCost + "}\n");
      fwTexFileWriter
          .write(prefix + "addNodeComputaionCost" + "}{" + Parameter.addNodeComputaionCost + "}\n");
      fwTexFileWriter
          .write(prefix + "addEdgeBandwithCost" + "}{" + Parameter.addEdgeBandwithCost + "}\n");
      fwTexFileWriter.write(prefix + "RelativeCostbetweenComputingBandwidth" + "}{"
          + Parameter.RelativeCostbetweenComputingBandwidth + "}\n");

      fwTexFileWriter.write(prefix + "SubstrateNewtorkRunTimeInterval" + "}{"
          + Parameter.SubstrateNewtorkRunTimeInterval + "}\n");
      fwTexFileWriter
          .write(prefix + "unitTimeInterval" + "}{" + Parameter.unitTimeInterval + "}\n");
      fwTexFileWriter.write(
          prefix + "requestAppearProbability" + "}{" + Parameter.RequestAppearProbability + "}\n");
      fwTexFileWriter.write(
          prefix + "RequestPerTimeAppearNum" + "}{" + Parameter.RequestPerTimeAppearNum + "}\n");
      fwTexFileWriter
          .write(prefix + "VNRequestsDuration" + "}{" + Parameter.VirNetDuration + "}\n");
      fwTexFileWriter.write(prefix + "VNRequestsContinueTimeMinimum" + "}{"
          + Parameter.VNRequestsContinueTimeMinimum + "}\n");
      fwTexFileWriter.write(prefix + "VNRequestsContinueTimeMaximum" + "}{"
          + Parameter.VNRequestsContinueTimeMaximum + "}\n");
      fwTexFileWriter.write(prefix + "VNRequestsContinueTimeAverage" + "}{"
          + Parameter.VNRequestsContinueTimeAverage + "}\n");
      fwTexFileWriter
          .write(prefix + "SubStrateNodeSize" + "}{" + Parameter.SubStrateNodeSize + "}\n");
      fwTexFileWriter.write(prefix + "SubStrateNodeComputationMinimum" + "}{"
          + Parameter.SubStrateNodeComputationMinimum + "}\n");
      fwTexFileWriter.write(prefix + "SubStrateNodeComputationMaximum" + "}{"
          + Parameter.SubStrateNodeComputationMaximum + "}\n");
      fwTexFileWriter.write(prefix + "SubStrateNodenodeProbability" + "}{"
          + Parameter.SubStrateNodenodeProbability + "}\n");
      fwTexFileWriter.write(prefix + "SubStrateEdgeBandwithMinimum" + "}{"
          + Parameter.SubStrateEdgeBandwithMinimum + "}\n");
      fwTexFileWriter.write(prefix + "SubStrateEdgeBandwithMaximum" + "}{"
          + Parameter.SubStrateEdgeBandwithMaximum + "}\n");

      fwTexFileWriter.write(
          prefix + "VirtualNodeSizeMinimum" + "}{" + Parameter.VirtualNodeSizeMinimum + "}\n");
      fwTexFileWriter.write(
          prefix + "VirtualNodeSizeMaximum" + "}{" + Parameter.VirtualNodeSizeMaximum + "}\n");
      fwTexFileWriter.write(prefix + "VirtualNodeComputationMinimum" + "}{"
          + Parameter.VirtualNodeComputationMinimum + "}\n");
      fwTexFileWriter.write(prefix + "VirtualNodeComputationMaximum" + "}{"
          + Parameter.VirtualNodeComputationMaximum + "}\n");
      fwTexFileWriter.write(prefix + "VirtualNodenodeProbability" + "}{"
          + Parameter.VirtualNodenodeProbability + "}\n");
      fwTexFileWriter.write(prefix + "VirtualEdgeBandwithMinimum" + "}{"
          + Parameter.VirtualEdgeBandwithMinimum + "}\n");
      fwTexFileWriter.write(prefix + "VirtualEdgeBandwithMaximum" + "}{"
          + Parameter.VirtualEdgeBandwithMaximum + "}\n");
      fwTexFileWriter.write(prefix + "SubStrateFacilityNodeFailDuration" + "}{"
          + Parameter.SubStrateFacilityNodeFailDuration + "}\n");
      fwTexFileWriter.write(prefix + "ExperimentTimes" + "}{"
          + Parameter.ExperimentTimes + "}\n");
      
      fwTexFileWriter.flush();
      fwTexFileWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * recordExperimentData.
   * 
   * @param experimentTimes
   *          experimentTimes
   * @param algorithm
   *          algorithm
   * @param time
   * 
   */
  public void recordExperimentData(int experimentTimes, Algorithm algorithm, int time) {
    recordExperimentData4AcceptionRatio(experimentTimes, algorithm, time);
    recordExperimentData4SubNetworkMapCost(experimentTimes, algorithm, time);
    recordExperimentData4VirNetworkMapRevenue(experimentTimes, algorithm, time);
    // recordExperimentData4MigrationFrequence(experimentTimes, algorithm,
    // time);
  }

  /**
   * recordExperimentData4EveryAlgorithmMappingCost.
   * 
   * @param experimentTimes
   *          experimentTimes
   * @param algorithm
   *          algorithm
   * @param time
   *          time
   */
  private void recordExperimentData4VirNetworkMapRevenue(int experimentTimes, Algorithm algorithm,
      int time) {

    arrayUtilizationVirNodeAcc[revenueDataLength] = this.revenueUsedNodeSum;
    arrayUtilizationVirEdgeAcc[revenueDataLength] = this.revenueUsedEdgeSum;
    arrayRevenueNodeCpAcc[revenueDataLength] = this.revenueNodeComputationSum;
    arrayRevenueEdgeBwAcc[revenueDataLength] = this.revenueEdgeBandwithSum;

    arrayVirNetReqAcc[revenueDataLength] = this.revenueVirNetNumSum;
    arraySurNetReqAcc[revenueDataLength] = this.revenueSubNetNumSum;

    arrayUtilizationVirNode[revenueDataLength] = this.revenueUsedNode;
    arrayUtilizationVirEdge[revenueDataLength] = this.revenueUsedEdge;
    arrayRevenueNodeCp[revenueDataLength] = this.revenueNodeComputation;
    arrayRevenueEdgeBw[revenueDataLength] = this.revenueEdgeBandwith;

    arrayVirNetReq[revenueDataLength] = this.revenueVirNetNum;
    arraySurNetReq[revenueDataLength] = this.revenueSubNetNum;

    revenueDataLength++;

  }

  /**
   * recordExperimentData4MigrationFrequence.
   * 
   * @param experimentTimes
   *          experimentTimes
   * @param algorithm
   *          algorithm
   * @param time
   *          time
   */
  private void recordExperimentData4MigrationFrequence(int experimentTimes, Algorithm algorithm,
      int time) {
    File f_MappingCost_node = new File(fileAbsolutePath + dataFilePathString
        + "MigrationFrequence_node" + algorithm.algorithmName + ".txt");
    File f_MappingCost_edge = new File(fileAbsolutePath + dataFilePathString
        + "MigrationFrequence_edge" + algorithm.algorithmName + ".txt");
    FileWriter fw_MigrationFrequence_node;
    FileWriter fw_MigrationFrequence_edge;

    if (!f_MappingCost_node.exists()) {
      try {
        f_MappingCost_node.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (!f_MappingCost_edge.exists()) {
      try {
        f_MappingCost_edge.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    try {
      if ((time == 0) && (experimentTimes == 0)) {
        fw_MigrationFrequence_node = new FileWriter(f_MappingCost_node);
        fw_MigrationFrequence_edge = new FileWriter(f_MappingCost_edge);
      } else {
        fw_MigrationFrequence_node = new FileWriter(f_MappingCost_node, true);
        fw_MigrationFrequence_edge = new FileWriter(f_MappingCost_edge, true);
      }

      if ((time == 0)) {
        fw_MigrationFrequence_node.write("0\n");
        fw_MigrationFrequence_edge.write("0\n");
      } else {

        int failnode = (int) (Math.random() * (algorithm.getSn().nodeSize - 1));
        int migrate = 0;
        for (int i = 0; i < algorithm.getSn().virNetCollection.size(); i++) {
          if (algorithm.getSn().virNetCollection.get(i).getIsRunning()) {
            for (int j = 0; j < algorithm.getSn().virNetCollection.get(i).nodeSize; j++) {
              if (algorithm.getSn().virNetCollection.get(i).virNode2subNode[j] == failnode) {
                migrate++;
              }
            }
          }
        }
        fw_MigrationFrequence_node.write(migrate + "\n");
      }

      fw_MigrationFrequence_node.flush();
      fw_MigrationFrequence_edge.flush();

      fw_MigrationFrequence_node.close();
      fw_MigrationFrequence_edge.close();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * writeExperimentData4AcceptionRatio.
   * 
   * @param experimentTimes
   *          experimentTimes
   * @param algorithm
   *          algorithm
   */
  private void writeExperimentData4AcceptionRatio(int experimentTimes, Algorithm algorithm) {

    writeExperimentData(experimentTimes, algorithm, "AcceptionRatio_VN_",
        this.arrayAcceptionRatioVn, acceptionRatioDataLength);
    writeExperimentData(experimentTimes, algorithm, "AcceptionRatio_SVN_",
        this.arrayAcceptionRatioSvn, acceptionRatioDataLength);
    acceptionRatioDataLength = 0;

  }

  /**
   * writeExperimentData.
   * 
   * @param experimentTimes
   *          experimentTimes
   * @param algorithm
   *          algorithm
   * @param filename
   *          filename
   * @param recordData
   *          recordData
   * @param dataLength
   *          dataLength
   */
  private void writeExperimentData(int experimentTimes, Algorithm algorithm, String filename,
      double[] recordData, int dataLength) {
    File fl = new File(
        fileAbsolutePath + dataFilePathString + filename + algorithm.algorithmName + ".txt");
    FileWriter fw;
    if (!fl.exists()) {
      try {
        fl.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try {
      if (experimentTimes == 0) {
        fw = new FileWriter(fl);
      } else {
        fw = new FileWriter(fl, true);
      }
      for (int i = 0; i < dataLength; i++) {
        fw.write(recordData[i] + "\n");
      }
      fw.flush();

      fw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * recordExperimentData4AcceptionRatio.
   * 
   * @param experimentTimes
   *          experimentTimes
   * @param time
   *          time
   * @param algorithm
   *          algorithm
   * 
   */
  private void recordExperimentData4AcceptionRatio(int experimentTimes, Algorithm algorithm,
      int time) {

    if (0 == algorithm.getSn().virNetCollection.size()) {
      arrayAcceptionRatioSvn[acceptionRatioDataLength] = 1.0;
      arrayAcceptionRatioVn[acceptionRatioDataLength] = 1.0;

    } else {
      arrayAcceptionRatioSvn[acceptionRatioDataLength] = (1.0
          * algorithm.getSn().surVirNetSuceedEmbedSum / algorithm.getSn().virNetSum);
      arrayAcceptionRatioVn[acceptionRatioDataLength] = (1.0
          * algorithm.getSn().virNetSuceedEmbedSum / algorithm.getSn().virNetSum);

    }
    acceptionRatioDataLength++;

  }

  /**
   * recordExperimentParameter.
   * 
   * @param experimentTimes
   *          experimentTimes
   * @param algorithms
   *          algorithms
   * 
   */
  public void recordExperimentParameter(int experimentTimes, Vector<Algorithm> algorithms) {
    File flParameter = new File(fileAbsolutePath + dataFilePathString + "Parameter.txt");
    FileWriter fwParameter;

    if (!flParameter.exists()) {
      try {
        flParameter.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (experimentTimes == 0) {
      try {
        fwParameter = new FileWriter(flParameter);
        fwParameter.write(Parameter.ExperimentTimes + "\n");
        fwParameter.write((Parameter.ExperimentPicturePlotNumber + 1) + "\n");
        fwParameter.write(Parameter.SubstrateNewtorkRunTimeInterval + "\n");
        fwParameter.write(algorithms.size() + "\n");
        fwParameter.write(Parameter.RelativeCostbetweenComputingBandwidth + "\n");
        fwParameter.write(Parameter.addNewVirNodeCost + "\n");
        fwParameter.flush();
        fwParameter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
  }

  /**
   * recordExperimentData4WholeSubNetworkMappingCost.
   * 
   * @param experimentTimes
   *          experimentTimes
   * @param algorithm
   *          algorithm
   * @param time
   *          time
   */
  private void recordExperimentData4SubNetworkMapCost(int experimentTimes, Algorithm algorithm,
      int time) {

    arrayUtilizationNodeAcc[recordDataLength] = this.costUsedNodeSum;
    arrayUtilizationEdgeAcc[recordDataLength] = this.costUsedEdgeSum;
    arrayCostNodeCpAcc[recordDataLength] = this.costNodeComputationSum;
    arrayarrayCostEdgeBwAcc[recordDataLength] = this.costEdgeBandwithSum;

    arrayUtilizationSubNode[recordDataLength] = this.costUsedNode;
    arrayUtilizationSubEdge[recordDataLength] = this.costUsedEdge;
    arrayCostNodeCp[recordDataLength] = this.costNodeComputation;
    arrayCostEdgeBw[recordDataLength] = this.costEdgeBandwith;
    recordDataLength++;

  }

  void writeExperimentData(int experimentTimes, Algorithm algorithm, String filename,
      int[] recordData, int datalength) {
    File fl = new File(
        fileAbsolutePath + dataFilePathString + filename + algorithm.algorithmName + ".txt");
    FileWriter fw;
    if (!fl.exists()) {
      try {
        fl.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try {
      if (experimentTimes == 0) {
        fw = new FileWriter(fl);
      } else {
        fw = new FileWriter(fl, true);
      }
      for (int i = 0; i < datalength; i++) {
        fw.write(recordData[i] + "\n");
      }
      fw.flush();

      fw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * writeExperimentData4MappingCost.
   * 
   * @param experimentTimes
   *          experimentTimes
   * @param algorithm
   *          algorithm
   */
  public void writeExperimentData4SubNetwork(int experimentTimes, Algorithm algorithm) {

    writeExperimentData(experimentTimes, algorithm, "Utilization_SubNode_",
        this.arrayUtilizationSubNode, recordDataLength);
    writeExperimentData(experimentTimes, algorithm, "Utilization_SubEdge_",
        this.arrayUtilizationSubEdge, recordDataLength);
    writeExperimentData(experimentTimes, algorithm, "Cost_NodeCp_", this.arrayCostNodeCp,
        recordDataLength);
    writeExperimentData(experimentTimes, algorithm, "Cost_NdgeBw_", this.arrayCostEdgeBw,
        recordDataLength);

    writeExperimentData(experimentTimes, algorithm, "Utilization_SubNode_Accumulate",
        this.arrayUtilizationNodeAcc, recordDataLength);
    writeExperimentData(experimentTimes, algorithm, "Utilization_SubEdge_Accumulate",
        this.arrayUtilizationEdgeAcc, recordDataLength);
    writeExperimentData(experimentTimes, algorithm, "Cost_NodeCp_Accumulate",
        this.arrayCostNodeCpAcc, recordDataLength);
    writeExperimentData(experimentTimes, algorithm, "Cost_EdgeBw_Accumulate",
        this.arrayarrayCostEdgeBwAcc, recordDataLength);
    recordDataLength = 0;
  }

  /**
   * writeExperimentData4EveryAlgMappingCost.
   * 
   * @param experimentTimes
   *          experimentTimes
   * @param algorithm
   *          algorithm
   */
  public void writeExperimentData4VirNetwork(int experimentTimes, Algorithm algorithm) {

    writeExperimentData(experimentTimes, algorithm, "Utilization_VirNode_",
        this.arrayUtilizationVirNode, revenueDataLength);
    writeExperimentData(experimentTimes, algorithm, "Utilization_VirEdge_",
        this.arrayUtilizationVirEdge, revenueDataLength);
    writeExperimentData(experimentTimes, algorithm, "Revenue_NodeCp_", this.arrayRevenueNodeCp,
        revenueDataLength);
    writeExperimentData(experimentTimes, algorithm, "Revenue_EdgeBw_", this.arrayRevenueEdgeBw,
        revenueDataLength);
    // Request_VirNet_Accumulate_ MapRevenue_virnet_
    writeExperimentData(experimentTimes, algorithm, "Request_VirNet_", this.arrayVirNetReq,
        revenueDataLength);
    // MapRevenue_subnet_
    writeExperimentData(experimentTimes, algorithm, "Request_SurNet_", this.arraySurNetReq,
        revenueDataLength);

    writeExperimentData(experimentTimes, algorithm, "Utilization_VirNode_Accumulate",
        this.arrayUtilizationVirNodeAcc, revenueDataLength);
    writeExperimentData(experimentTimes, algorithm, "Utilization_VirEdge_Accumulate",
        this.arrayUtilizationVirEdgeAcc, revenueDataLength);
    writeExperimentData(experimentTimes, algorithm, "Revenue_NodeCp_Accumulate",
        this.arrayRevenueNodeCpAcc, revenueDataLength);
    writeExperimentData(experimentTimes, algorithm, "Revenue_EdgeBw_Accumulate",
        this.arrayRevenueEdgeBwAcc, revenueDataLength);
    // MapRevenue_virnet_Sum_
    writeExperimentData(experimentTimes, algorithm, "Request_VirNet_Accumulate_",
        this.arrayVirNetReqAcc, revenueDataLength);
    // MapRevenue_subnet_Sum_
    writeExperimentData(experimentTimes, algorithm, "Request_SurNet_Accumulate_",
        this.arraySurNetReqAcc, revenueDataLength);

    revenueDataLength = 0;

  }

  /**
   * writeExperimentData.
   * 
   * @param experimentTimes
   *          experimentTimes
   * @param algorithm
   *          algorithm
   */
  public void writeExperimentDatatoFile(int experimentTimes, Algorithm algorithm) {
    writeExperimentData4AcceptionRatio(experimentTimes, algorithm);
    writeExperimentData4SubNetwork(experimentTimes, algorithm);
    writeExperimentData4VirNetwork(experimentTimes, algorithm);

  }

  /**
   * updateExperimentData.
   * 
   * @param algorithm
   *          algorithm
   */
  public void updateExperimentData(Algorithm algorithm) {
    // node
    int usedNode = 0;
    int usedEdge = 0;
    int nodeComputation = 0;
    int edgeBandwith = 0;
    for (int i = 0; i < algorithm.getSn().nodeSize; i++) {
      int nc = algorithm.getSn().nodeComputationCapacity[i]
          - algorithm.getSn().getSubstrateRemainComputaion4VirNet(i, algorithm.isShared());
      if (nc != 0) {
        usedNode++;
      }
      if (nc > 0) {
        nodeComputation += nc;
      }

      if (nc < 0) {
        System.out.println("Error nc");
      }
      for (int j = 0; j < i; j++) {
        int bc = algorithm.getSn().edgeBandwithCapacity[i][j]
            - algorithm.getSn().getSubStrateRemainBandwith4VN(i, j, algorithm.isShared());
        if (bc != 0) {
          usedEdge++;
        }
        if (bc > 0) {
          edgeBandwith += bc;
        }
        if (bc < 0) {
          System.out.println("Error bc");
        }
      }
    }

    this.costEdgeBandwith = edgeBandwith;
    this.costUsedEdge = usedEdge;
    this.costNodeComputation = nodeComputation;
    this.costUsedNode = usedNode;

    usedNode = 0;
    usedEdge = 0;
    nodeComputation = 0;
    edgeBandwith = 0;
    int vnNum = 0;
    int svnNum = 0;
    for (int i = 0; i < algorithm.getSn().virNetCollection.size(); i++) {
      VirtualNetwork vn = algorithm.getSn().virNetCollection.get(i);
      if (vn.getIsRunning()) {
        vnNum++;
        for (int j = 0; j < vn.nodeSize; j++) {
          if (vn.nodeComputationDemand[j] > 0) {
            usedNode++;
            nodeComputation += vn.nodeComputationDemand[j];
          }
          for (int k = 0; k < j; k++) {
            if (vn.edgeBandwithDemand[j][k] > 0) {
              usedEdge++;
              edgeBandwith += vn.edgeBandwithDemand[j][k];
            }
          }
        }
      }
      if (vn.getIsRunning() && vn.surVirNet.isSucceedEmbed) {
        svnNum++;
        for (int j = vn.nodeSize; j < vn.surVirNet.nodeSize; j++) {
          if (vn.surVirNet.nodeComputation4Backup[j] > 0) {
            usedNode++;
            nodeComputation += vn.surVirNet.nodeComputation4Backup[j];
          }
          for (int k = 0; k < j; k++) {
            if (vn.surVirNet.edgeBandwith4Backup[j][k] > 0) {
              usedEdge++;
              edgeBandwith += vn.surVirNet.edgeBandwith4Backup[j][k];
            }
          }
        }
      }
    }

    this.revenueEdgeBandwith = edgeBandwith;
    this.revenueUsedEdge = usedEdge;
    this.revenueNodeComputation = nodeComputation;
    this.revenueUsedNode = usedNode;
    this.revenueSubNetNum = svnNum;
    this.revenueVirNetNum = vnNum;
  }

  /**
   * updateExperimentDataAccumulate.
   * 
   * @param algorithm
   *          algorithm
   */
  public void updateExperimentDataAccumulate(Algorithm algorithm) {
    // node
    int usedNode = 0;
    int usedEdge = 0;
    int nodeComputation = 0;
    int edgeBandwith = 0;
    for (int i = 0; i < algorithm.getSn().nodeSize; i++) {
      int nc = algorithm.getSn().nodeComputationCapacity[i]
          - algorithm.getSn().getSubstrateRemainComputaion4VirNet(i, algorithm.isShared());
      if (nc != 0) {
        usedNode++;
      }
      if (nc > 0) {
        nodeComputation += nc;
      }

      if (nc < 0) {
        System.out.println("Error nc");
      }
      for (int j = 0; j < i; j++) {
        int bc = algorithm.getSn().edgeBandwithCapacity[i][j]
            - algorithm.getSn().getSubStrateRemainBandwith4VN(i, j, algorithm.isShared());
        if (bc != 0) {
          usedEdge++;
        }
        if (bc > 0) {
          edgeBandwith += bc;
        }
        if (bc < 0) {
          System.out.println("Error bc");
        }
      }
    }

    this.costEdgeBandwithSum += (edgeBandwith - this.costEdgeBandwith);
    this.costUsedEdgeSum += (usedEdge - this.costUsedEdge);
    this.costNodeComputationSum += (nodeComputation - this.costNodeComputation);
    this.costUsedNodeSum += (usedNode - this.costUsedNode);

    this.costEdgeBandwith = edgeBandwith;
    this.costUsedEdge = usedEdge;
    this.costNodeComputation = nodeComputation;
    this.costUsedNode = usedNode;

    usedNode = 0;
    usedEdge = 0;
    nodeComputation = 0;
    edgeBandwith = 0;
    int vnNum = 0;
    int svnNum = 0;
    for (int i = 0; i < algorithm.getSn().virNetCollection.size(); i++) {
      VirtualNetwork vn = algorithm.getSn().virNetCollection.get(i);
      if (vn.getIsRunning()) {
        vnNum++;
        for (int j = 0; j < vn.nodeSize; j++) {
          if (vn.nodeComputationDemand[j] > 0) {
            usedNode++;
            nodeComputation += vn.nodeComputationDemand[j];
          }
          for (int k = 0; k < j; k++) {
            if (vn.edgeBandwithDemand[j][k] > 0) {
              usedEdge++;
              edgeBandwith += vn.edgeBandwithDemand[j][k];
            }
          }
        }
      }
      if (vn.getIsRunning() && vn.surVirNet.isSucceedEmbed) {
        svnNum++;
        for (int j = vn.nodeSize; j < vn.surVirNet.nodeSize; j++) {
          if (vn.surVirNet.nodeComputation4Backup[j] > 0) {
            usedNode++;
            nodeComputation += vn.surVirNet.nodeComputation4Backup[j];
          }
          for (int k = 0; k < j; k++) {
            if (vn.surVirNet.edgeBandwith4Backup[j][k] > 0) {
              usedEdge++;
              edgeBandwith += vn.surVirNet.edgeBandwith4Backup[j][k];
            }
          }
        }
      }
    }

    this.revenueEdgeBandwithSum += (edgeBandwith - this.revenueEdgeBandwith);

    this.revenueUsedEdgeSum += (usedEdge - this.revenueUsedEdge);
    this.revenueNodeComputationSum += (nodeComputation - this.revenueNodeComputation);
    this.revenueUsedNodeSum += (usedNode - this.revenueUsedNode);

    this.revenueVirNetNumSum += (vnNum - this.revenueSubNetNum);
    if (algorithm.algorithmName.equals("VirNet")) {
      this.revenueSubNetNumSum = this.revenueVirNetNumSum;
    } else {
      this.revenueSubNetNumSum += (svnNum - this.revenueSubNetNum);
    }

    this.revenueEdgeBandwith = edgeBandwith;
    this.revenueUsedEdge = usedEdge;
    this.revenueNodeComputation = nodeComputation;
    this.revenueUsedNode = usedNode;
    if (algorithm.algorithmName.equals("VirNet")) {
      this.revenueSubNetNum = vnNum;
    } else {
      this.revenueSubNetNum = svnNum;
    }
    this.revenueVirNetNum = vnNum;
  }

}
