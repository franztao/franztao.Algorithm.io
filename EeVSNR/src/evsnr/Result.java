/**
 * 
 */

package evsnr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

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

  double[] arrayAcceptionRatioEvn;
  double[] arrayAcceptionRatioVn;
  int acceptionRatioDataLength;

  public int[] subNetMapEdgeCost;
  public int[] subNetMapNodeCost;
  public int[] subNetMapUsedNodeCost;
  public int subNetMapDataLength;

  public int[] virNetMapNodeCost;
  public int[] virNetMapEdgeCost;
  public int[] virNetMapUsedNodeCost;
  public int virNetMapCostDataLength;

  /**
   * Result.
   */
  public Result() {
    arrayAcceptionRatioEvn = new double[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    arrayAcceptionRatioVn = new double[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];

    subNetMapEdgeCost = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    subNetMapNodeCost = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    subNetMapUsedNodeCost = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];

    virNetMapNodeCost = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    virNetMapEdgeCost = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];
    virNetMapUsedNodeCost = new int[(Parameter.ExperimentPicturePlotNumber + 1)
        * Parameter.ExperimentTimes];

    acceptionRatioDataLength = 0;
    subNetMapDataLength = 0;
  }

  void recordTexParameter() {
    try {
      File f_TexFileWriter = new File(fileAbsolutePath + dataFilePathString + "number.tex");

      if (!f_TexFileWriter.exists()) {
        try {
          f_TexFileWriter.createNewFile();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      FileWriter TexFileWriter = new FileWriter(f_TexFileWriter);
      TexFileWriter.write(prefix + "addNewNodeCost" + "}{" + Parameter.addNewVirNodeCost + "}\n");
      TexFileWriter.write(
          prefix + "transformExistedNodeCost" + "}{" + Parameter.transformExistedNodeCost + "}\n");
      TexFileWriter
          .write(prefix + "addNodeComputaionCost" + "}{" + Parameter.addNodeComputaionCost + "}\n");
      TexFileWriter
          .write(prefix + "addEdgeBandwithCost" + "}{" + Parameter.addEdgeBandwithCost + "}\n");
      TexFileWriter.write(prefix + "RelativeCostbetweenComputingBandwidth" + "}{"
          + Parameter.RelativeCostbetweenComputingBandwidth + "}\n");

      TexFileWriter.write(prefix + "SubstrateNewtorkRunTimeInterval" + "}{"
          + Parameter.SubstrateNewtorkRunTimeInterval + "}\n");
      TexFileWriter.write(prefix + "unitTimeInterval" + "}{" + Parameter.unitTimeInterval + "}\n");
      TexFileWriter.write(
          prefix + "requestAppearProbability" + "}{" + Parameter.RequestAppearProbability + "}\n");
      TexFileWriter.write(prefix + "VNRequestsDuration" + "}{" + Parameter.VirNetDuration + "}\n");
      TexFileWriter.write(prefix + "VNRequestsContinueTimeMinimum" + "}{"
          + Parameter.VNRequestsContinueTimeMinimum + "}\n");
      TexFileWriter.write(prefix + "VNRequestsContinueTimeMaximum" + "}{"
          + Parameter.VNRequestsContinueTimeMaximum + "}\n");

      TexFileWriter
          .write(prefix + "SubStrateNodeSize" + "}{" + Parameter.SubStrateNodeSize + "}\n");
      TexFileWriter.write(prefix + "SubStrateNodeComputationMinimum" + "}{"
          + Parameter.SubStrateNodeComputationMinimum + "}\n");
      TexFileWriter.write(prefix + "SubStrateNodeComputationMaximum" + "}{"
          + Parameter.SubStrateNodeComputationMaximum + "}\n");
      TexFileWriter.write(prefix + "SubStrateNodenodeProbability" + "}{"
          + Parameter.SubStrateNodenodeProbability + "}\n");
      TexFileWriter.write(prefix + "SubStrateEdgeBandwithMinimum" + "}{"
          + Parameter.SubStrateEdgeBandwithMinimum + "}\n");
      TexFileWriter.write(prefix + "SubStrateEdgeBandwithMaximum" + "}{"
          + Parameter.SubStrateEdgeBandwithMaximum + "}\n");

      TexFileWriter.write(
          prefix + "VirtualNodeSizeMinimum" + "}{" + Parameter.VirtualNodeSizeMinimum + "}\n");
      TexFileWriter.write(
          prefix + "VirtualNodeSizeMaximum" + "}{" + Parameter.VirtualNodeSizeMaximum + "}\n");
      TexFileWriter.write(prefix + "VirtualNodeComputationMinimum" + "}{"
          + Parameter.VirtualNodeComputationMinimum + "}\n");
      TexFileWriter.write(prefix + "VirtualNodeComputationMaximum" + "}{"
          + Parameter.VirtualNodeComputationMaximum + "}\n");
      TexFileWriter.write(prefix + "VirtualNodenodeProbability" + "}{"
          + Parameter.VirtualNodenodeProbability + "}\n");
      TexFileWriter.write(prefix + "VirtualEdgeBandwithMinimum" + "}{"
          + Parameter.VirtualEdgeBandwithMinimum + "}\n");
      TexFileWriter.write(prefix + "VirtualEdgeBandwithMaximum" + "}{"
          + Parameter.VirtualEdgeBandwithMaximum + "}\n");
      TexFileWriter.write(prefix + "SubStrateFacilityNodeFailDuration" + "}{"
          + Parameter.SubStrateFacilityNodeFailDuration + "}\n");

      TexFileWriter.flush();
      TexFileWriter.close();
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
    recordExperimentData4VirNetworkMapCost(experimentTimes, algorithm, time);
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
  private void recordExperimentData4VirNetworkMapCost(int experimentTimes,
      Algorithm algorithm, int time) {

    virNetMapNodeCost[virNetMapCostDataLength] = algorithm.consumedNodeNum;
    virNetMapEdgeCost[virNetMapCostDataLength] = algorithm.consumedEdgeBandwith;
    virNetMapUsedNodeCost[virNetMapCostDataLength] = algorithm.consumedNodeNum;
    virNetMapCostDataLength++;

  }

  /**
   * @param experimentTimes
   * @param algorithm
   * @param time
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
   * @param experimentTimes
   * @param algorithm
   */
  private void writeExperimentData4AcceptionRatio(int experimentTimes, Algorithm algorithm) {
    File f_AcceptionRatio_evn = new File(fileAbsolutePath + dataFilePathString
        + "AcceptionRatio_EVN_" + algorithm.algorithmName + ".txt");
    File f_AcceptionRatio_vn = new File(fileAbsolutePath + dataFilePathString + "AcceptionRatio_VN_"
        + algorithm.algorithmName + ".txt");
    FileWriter fw_AcceptionRatio_evn;
    FileWriter fw_AcceptionRatio_vn;

    if (!f_AcceptionRatio_evn.exists()) {
      try {
        f_AcceptionRatio_evn.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (!f_AcceptionRatio_vn.exists()) {
      try {
        f_AcceptionRatio_vn.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    try {
      if (experimentTimes == 0) {
        fw_AcceptionRatio_evn = new FileWriter(f_AcceptionRatio_evn);
        fw_AcceptionRatio_vn = new FileWriter(f_AcceptionRatio_vn);
      } else {
        fw_AcceptionRatio_evn = new FileWriter(f_AcceptionRatio_evn, true);
        fw_AcceptionRatio_vn = new FileWriter(f_AcceptionRatio_vn, true);
      }
      for (int i = 0; i < acceptionRatioDataLength; i++) {
        fw_AcceptionRatio_vn.write(arrayAcceptionRatioVn[i] + "\n");
        fw_AcceptionRatio_evn.write(arrayAcceptionRatioEvn[i] + "\n");

      }
      acceptionRatioDataLength = 0;
      fw_AcceptionRatio_vn.flush();
      fw_AcceptionRatio_evn.flush();

      fw_AcceptionRatio_vn.close();
      fw_AcceptionRatio_evn.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * @param experimentTimes
   * @param time
   * @param algorithm
   * 
   */
  private void recordExperimentData4AcceptionRatio(int experimentTimes, Algorithm algorithm,
      int time) {

    if (0 == algorithm.getSn().virNetCollection.size()) {
      arrayAcceptionRatioEvn[acceptionRatioDataLength] = 1.0;
      arrayAcceptionRatioVn[acceptionRatioDataLength] = 1.0;

    } else {
      arrayAcceptionRatioEvn[acceptionRatioDataLength] = (1.0
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
  private void recordExperimentData4SubNetworkMapCost(int experimentTimes,
      Algorithm algorithm, int time) {

    int nodeCompution = 0;
    int nodeUsed = 0;
    for (int i = 0; i < algorithm.getSn().nodeSize; i++) {
      int compution = (algorithm.getSn().nodeComputationCapacity[i]
          - algorithm.getSn().getSubstrateRemainComputaion4VirNet(i, algorithm.isShared()));
      if (compution > 0) {
        nodeCompution += compution;
        nodeUsed++;
      }

    }

    int edgeBandwith = 0;
    for (int i = 0; i < algorithm.getSn().nodeSize; i++) {
      for (int j = 0; j < i; j++) {
        edgeBandwith += (algorithm.getSn().edgeBandwithCapacity[i][j]
            - algorithm.getSn().getSubStrateRemainBandwith4VN(i, j, algorithm.isShared()));
      }
    }

    subNetMapNodeCost[subNetMapDataLength] = nodeCompution;
    subNetMapEdgeCost[subNetMapDataLength] = edgeBandwith;
    subNetMapUsedNodeCost[subNetMapDataLength] = nodeUsed;
    subNetMapDataLength++;

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
    File flMapCostNode = new File(fileAbsolutePath + dataFilePathString + "MappingCost_node_"
        + algorithm.algorithmName + ".txt");
    File flMapCostNodeNumUsed = new File(fileAbsolutePath + dataFilePathString
        + "MappingCost_nodeUsed_" + algorithm.algorithmName + ".txt");
    File flMapCostEdge = new File(fileAbsolutePath + dataFilePathString + "MappingCost_edge_"
        + algorithm.algorithmName + ".txt");
    FileWriter fwMapCostNode;
    FileWriter fwMapCostEdge;
    FileWriter fwMapCostNodeNumUsed;

    if (!flMapCostNode.exists()) {
      try {
        flMapCostNode.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (!flMapCostNodeNumUsed.exists()) {
      try {
        flMapCostNodeNumUsed.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (!flMapCostEdge.exists()) {
      try {
        flMapCostEdge.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    try {
      if (experimentTimes == 0) {
        fwMapCostNode = new FileWriter(flMapCostNode);
        fwMapCostNodeNumUsed = new FileWriter(flMapCostNodeNumUsed);

        fwMapCostEdge = new FileWriter(flMapCostEdge);
      } else {
        fwMapCostNode = new FileWriter(flMapCostNode, true);
        fwMapCostEdge = new FileWriter(flMapCostEdge, true);
        fwMapCostNodeNumUsed = new FileWriter(flMapCostNodeNumUsed, true);
      }
      for (int i = 0; i < subNetMapDataLength; i++) {
        fwMapCostEdge.write(subNetMapEdgeCost[i] + "\n");
        fwMapCostNode.write(subNetMapNodeCost[i] + "\n");
        fwMapCostNodeNumUsed.write(subNetMapUsedNodeCost[i] + "\n");

      }
      subNetMapDataLength = 0;

      fwMapCostEdge.flush();
      fwMapCostNode.flush();
      fwMapCostNodeNumUsed.flush();

      fwMapCostEdge.close();
      fwMapCostNode.close();
      fwMapCostNodeNumUsed.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * writeExperimentData4EveryAlgMappingCost.
   * 
   * @param experimentTimes
   *          experimentTimes
   * @param algorithm
   *          algorithm
   */
  public void writeExperimentData4VirNetworkMapCost(int experimentTimes, Algorithm algorithm) {
    File flMapCostNode = new File(fileAbsolutePath + dataFilePathString
        + "VirNetworkMapCost_node_" + algorithm.algorithmName + ".txt");
    File flMapCostNodeNumUsed = new File(fileAbsolutePath + dataFilePathString
        + "VirNetworkMapCost_nodeUsed_" + algorithm.algorithmName + ".txt");
    File flMapCostEdge = new File(fileAbsolutePath + dataFilePathString
        + "VirNetworkMapCost_edge_" + algorithm.algorithmName + ".txt");
    FileWriter fwMapCostNode;
    FileWriter fwMapCostEdge;
    FileWriter fwMapCostNodeNumUsed;

    if (!flMapCostNode.exists()) {
      try {
        flMapCostNode.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (!flMapCostNodeNumUsed.exists()) {
      try {
        flMapCostNodeNumUsed.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    if (!flMapCostEdge.exists()) {
      try {
        flMapCostEdge.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    try {
      if (experimentTimes == 0) {
        fwMapCostNode = new FileWriter(flMapCostNode);
        fwMapCostNodeNumUsed = new FileWriter(flMapCostNodeNumUsed);

        fwMapCostEdge = new FileWriter(flMapCostEdge);
      } else {
        fwMapCostNode = new FileWriter(flMapCostNode, true);
        fwMapCostEdge = new FileWriter(flMapCostEdge, true);
        fwMapCostNodeNumUsed = new FileWriter(flMapCostNodeNumUsed, true);
      }
      for (int i = 0; i < virNetMapCostDataLength; i++) {
        fwMapCostEdge.write(virNetMapEdgeCost[i] + "\n");
        fwMapCostNode.write(virNetMapNodeCost[i] + "\n");
        fwMapCostNodeNumUsed.write(virNetMapUsedNodeCost[i] + "\n");
      }
      virNetMapCostDataLength = 0;

      fwMapCostEdge.flush();
      fwMapCostNode.flush();
      fwMapCostNodeNumUsed.flush();

      fwMapCostEdge.close();
      fwMapCostNode.close();
      fwMapCostNodeNumUsed.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * writeExperimentData.
   * 
   * @param experimentTimes
   *          experimentTimes
   * @param algorithm
   *          algorithm
   */
  public void writeExperimentData(int experimentTimes, Algorithm algorithm) {
    writeExperimentData4SubNetworkMapCost(experimentTimes, algorithm);
    writeExperimentData4VirNetworkMapCost(experimentTimes, algorithm);
    writeExperimentData4AcceptionRatio(experimentTimes, algorithm);
  }

}
