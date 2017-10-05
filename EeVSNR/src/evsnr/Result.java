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

  public int[] arrayCostEdgeBwSum;
  public int[] arrayCostNodeCpSum;
  public int[] arrayCostUsedNodeSum;

  private int[] arrayCostUsedNode;
  private int[] arrayCostNodeCp;
  private int[] arrayCostEdgeBw;

  private int[] arrayCostUsedEdgeSum;
  private int[] arrayCostUsedEdge;

  public int costDataLength;

  public int[] arrayRevenueUsedNodeSum;
  public int[] arrayRevenueEdgeBwSum;
  public int[] arrayRevenueNodeCpSum;
  private int[] arrayRevenueNodeCp;
  private int[] varrayRevenueEdgeBw;
  private int[] arrayRevenueUsedNode;

  public int revenueDataLength;
  private int[] arrayRevenueUsedEdgeSum;
  private int[] arrayRevenueUsedEdge;

  private int[] arrayRevenueVirNetNumSum;
  private int[] arrayRevenueSurNetNumSum;
  private int[] arrayRevenueVirNetNum;
  private int[] arrayRevenueSurNetNum;

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

    arrayCostEdgeBwSum = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayCostNodeCpSum = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayCostUsedNodeSum = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];

    arrayCostEdgeBw = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayCostNodeCp = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayCostUsedNode = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];

    arrayCostUsedEdgeSum = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayCostUsedEdge = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];

    arrayRevenueUsedNodeSum = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayRevenueEdgeBwSum = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayRevenueNodeCpSum = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];

    arrayRevenueNodeCp = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    varrayRevenueEdgeBw = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayRevenueUsedNode = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];

    arrayRevenueVirNetNumSum = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayRevenueSurNetNumSum = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayRevenueVirNetNum = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayRevenueSurNetNum = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayRevenueUsedEdgeSum = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayRevenueUsedEdge = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    acceptionRatioDataLength = 0;
    costDataLength = 0;
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

    arrayRevenueUsedNodeSum[revenueDataLength] = this.revenueUsedNodeSum;
    arrayRevenueUsedEdgeSum[revenueDataLength] = this.revenueUsedEdgeSum;
    arrayRevenueNodeCpSum[revenueDataLength] = this.revenueNodeComputationSum;
    arrayRevenueEdgeBwSum[revenueDataLength] = this.revenueEdgeBandwithSum;

    arrayRevenueVirNetNumSum[revenueDataLength] = this.revenueVirNetNumSum;
    arrayRevenueSurNetNumSum[revenueDataLength] = this.revenueSubNetNumSum;

    arrayRevenueUsedNode[revenueDataLength] = this.revenueUsedNode;
    arrayRevenueUsedEdge[revenueDataLength] = this.revenueUsedEdge;
    arrayRevenueNodeCp[revenueDataLength] = this.revenueNodeComputation;
    varrayRevenueEdgeBw[revenueDataLength] = this.revenueEdgeBandwith;

    arrayRevenueVirNetNum[revenueDataLength] = this.revenueVirNetNum;
    arrayRevenueSurNetNum[revenueDataLength] = this.revenueSubNetNum;

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
    writeExperimentData(experimentTimes, algorithm, "AcceptionRatio_EVN_",
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

    arrayCostUsedNodeSum[costDataLength] = this.costUsedNodeSum;
    arrayCostUsedEdgeSum[costDataLength] = this.costUsedEdgeSum;
    arrayCostNodeCpSum[costDataLength] = this.costNodeComputationSum;
    arrayCostEdgeBwSum[costDataLength] = this.costEdgeBandwithSum;

    arrayCostUsedNode[costDataLength] = this.costUsedNode;
    arrayCostUsedEdge[costDataLength] = this.costUsedEdge;
    arrayCostNodeCp[costDataLength] = this.costNodeComputation;
    arrayCostEdgeBw[costDataLength] = this.costEdgeBandwith;
    costDataLength++;

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
  public void writeExperimentData4SubNetworkMapCost(int experimentTimes, Algorithm algorithm) {

    writeExperimentData(experimentTimes, algorithm, "MapCost_usedNode_", this.arrayCostUsedNode,
        costDataLength);
    writeExperimentData(experimentTimes, algorithm, "MapCost_usedEdge_", this.arrayCostUsedEdge,
        costDataLength);
    writeExperimentData(experimentTimes, algorithm, "MapCost_nodeCp_", this.arrayCostNodeCp,
        costDataLength);
    writeExperimentData(experimentTimes, algorithm, "MapCost_edgeBw_", this.arrayCostEdgeBw,
        costDataLength);

    writeExperimentData(experimentTimes, algorithm, "MapCost_usedNode_Sum_",
        this.arrayCostUsedNodeSum, costDataLength);
    writeExperimentData(experimentTimes, algorithm, "MapCost_usedEdge_Sum_",
        this.arrayCostUsedEdgeSum, costDataLength);
    writeExperimentData(experimentTimes, algorithm, "MapCost_nodeCp_Sum_", this.arrayCostNodeCpSum,
        costDataLength);
    writeExperimentData(experimentTimes, algorithm, "MapCost_edgeBw_Sum_", this.arrayCostEdgeBwSum,
        costDataLength);
    costDataLength = 0;
  }

  /**
   * writeExperimentData4EveryAlgMappingCost.
   * 
   * @param experimentTimes
   *          experimentTimes
   * @param algorithm
   *          algorithm
   */
  public void writeExperimentData4VirNetworkMapRevenue(int experimentTimes, Algorithm algorithm) {

    writeExperimentData(experimentTimes, algorithm, "MapRevenue_usedNode_",
        this.arrayRevenueUsedNode, revenueDataLength);
    writeExperimentData(experimentTimes, algorithm, "MapRevenue_usedEdge_",
        this.arrayRevenueUsedEdge, revenueDataLength);
    writeExperimentData(experimentTimes, algorithm, "MapRevenue_nodeCp_", this.arrayRevenueNodeCp,
        revenueDataLength);
    writeExperimentData(experimentTimes, algorithm, "MapRevenue_edgeBw_", this.varrayRevenueEdgeBw,
        revenueDataLength);
    writeExperimentData(experimentTimes, algorithm, "MapRevenue_virnet_",
        this.arrayRevenueVirNetNum, revenueDataLength);
    writeExperimentData(experimentTimes, algorithm, "MapRevenue_subnet_",
        this.arrayRevenueSurNetNum, revenueDataLength);

    writeExperimentData(experimentTimes, algorithm, "MapRevenue_usedNode_Sum_",
        this.arrayRevenueUsedNodeSum, revenueDataLength);
    writeExperimentData(experimentTimes, algorithm, "MapRevenue_usedEdge_Sum_",
        this.arrayRevenueUsedEdgeSum, revenueDataLength);
    writeExperimentData(experimentTimes, algorithm, "MapRevenue_nodeCp_Sum_",
        this.arrayRevenueNodeCpSum, revenueDataLength);
    writeExperimentData(experimentTimes, algorithm, "MapRevenue_edgeBw_Sum_",
        this.arrayRevenueEdgeBwSum, revenueDataLength);

    writeExperimentData(experimentTimes, algorithm, "MapRevenue_virnet_Sum_",
        this.arrayRevenueVirNetNumSum, revenueDataLength);
    writeExperimentData(experimentTimes, algorithm, "MapRevenue_subnet_Sum_",
        this.arrayRevenueSurNetNumSum, revenueDataLength);

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
    writeExperimentData4SubNetworkMapCost(experimentTimes, algorithm);
    writeExperimentData4VirNetworkMapRevenue(experimentTimes, algorithm);

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
    this.revenueSubNetNum = svnNum;
    this.revenueVirNetNum = vnNum;
  }

}
