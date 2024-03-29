/**
 * 
 */
package virtualnetwork;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * @author franz
 *
 */
public class VirtualNetwork {
  private boolean isRunning;
  private int index;
  private int leaveTime;

  public int nodeSize;
  public int []virNode2subNode;
  public int[] nodeComputationDemand;
  public int[] nodeComputationCapacity;

  public boolean [][]topology;
  public int [][]edgeBandwithDemand;
  public Vector<Vector<Vector<Integer>>> virEdge2subPath;

  public int serviceNum;
  public int []nodeServiceType;

  public String []node2Label;

  public Map<String, Integer> label2Node;

  public boolean isSampleInit;

  public SurvivalVirtualNetwork surVirNet;

  /**
   * VirtualNetwork.
   * @param vnp vnp
   */
  public VirtualNetwork(VirtualNetworkParameter vnp) {
    this.nodeSize = (int) (vnp.nodeSizeMinimum
        + Math.random() * (vnp.nodeSizeMaximum - vnp.nodeSizeMinimum));
    this.serviceNum = vnp.serviceNumber;
    instantiation();

  }

  /**
   * VirtualNetwork.
   * @param sameVirNet sameVirNet
   */
  public VirtualNetwork(VirtualNetwork sameVirNet) {
    this.nodeSize = sameVirNet.nodeSize;
    this.serviceNum = sameVirNet.serviceNum;
    instantiation();
  }

  /**
   * instantiation.
   */
  private void instantiation() {
    this.virNode2subNode = new int[nodeSize];
    this.nodeComputationDemand = new int[nodeSize];
    this.nodeComputationCapacity = new int[nodeSize];

    this.topology = new boolean[nodeSize][nodeSize];
    this.edgeBandwithDemand = new int[nodeSize][nodeSize];

    this.virEdge2subPath = new Vector<Vector<Vector<Integer>>>();
    for (int i = 0; i < this.nodeSize; i++) {
      virEdge2subPath.addElement(new Vector<Vector<Integer>>());
      for (int j = 0; j < this.nodeSize; j++) {
        virEdge2subPath.get(i).addElement(new Vector<Integer>());
      }
    }

    this.nodeServiceType = new int[nodeSize];

    this.node2Label = new String[nodeSize];
    this.label2Node = new HashMap<String, Integer>();
    setNodeLabel();

  }

  /**
   * 
   */
  public void destructerResource() {
    for (int i = 0; i < this.nodeSize; i++) {
      for (int j = 0; j < this.nodeSize; j++) {
        virEdge2subPath.get(i).get(j).clear();

      }
      virEdge2subPath.get(i).clear();
    }
    virEdge2subPath.clear();
    label2Node.clear();
  }

  /**
   * @return the nodeSize
   */
  public int getNodeSize() {
    return nodeSize;
  }

  /**
   * @param nodeSize
   *          the nodeSize to set
   */
  public void setNodeSize(int nodeSize) {
    this.nodeSize = nodeSize;
  }

  /**
   * @return the vNode2sNode
   */
  public int[] getvNode2sNode() {
    return virNode2subNode;
  }

  /**
   * @param vNode2sNode
   *          the vNode2sNode to set
   */
  public void setvNode2sNode(int[] vNode2sNode) {
    this.virNode2subNode = vNode2sNode;
  }

  /**
   * @return the nodeComputationDemand
   */
  public int[] getNodeComputationDemand() {
    return nodeComputationDemand;
  }

  /**
   * @param nodeComputationDemand
   *          the nodeComputationDemand to set
   */
  public void setNodeComputationDemand(int[] nodeComputationDemand) {
    this.nodeComputationDemand = nodeComputationDemand;
  }

  /**
   * @return the nodeComputationCapacity4embeded
   */
  public int[] getNodeComputationCapacity4embeded() {
    return nodeComputationCapacity;
  }

  /**
   * @param nodeComputationCapacity4embeded
   *          the nodeComputationCapacity4embeded to set
   */
  public void setNodeComputationCapacity4embeded(int[] nodeComputationCapacity4embeded) {
    this.nodeComputationCapacity = nodeComputationCapacity4embeded;
  }

  /**
   * @return the topology
   */
  public boolean[][] getTopology() {
    return topology;
  }

  /**
   * @param topology
   *          the topology to set
   */
  public void setTopology(boolean[][] topology) {
    this.topology = topology;
  }

  /**
   * @return the edgeBandwithDemand
   */
  public int[][] getEdgeBandwithDemand() {
    return edgeBandwithDemand;
  }

  /**
   * @param edgeBandwithDemand
   *          the edgeBandwithDemand to set
   */
  public void setEdgeBandwithDemand(int[][] edgeBandwithDemand) {
    this.edgeBandwithDemand = edgeBandwithDemand;
  }

  /**
   * @return the serviceNumber
   */
  public int getServiceNumber() {
    return serviceNum;
  }

  /**
   * @param serviceNumber
   *          the serviceNumber to set
   */
  public void setServiceNumber(int serviceNumber) {
    this.serviceNum = serviceNumber;
  }

  /**
   * @return the nodeServiceType
   */
  public int[] getNodeServiceType() {
    return nodeServiceType;
  }

  /**
   * @param nodeServiceType
   *          the nodeServiceType to set
   */
  public void setNodeServiceType(int[] nodeServiceType) {
    this.nodeServiceType = nodeServiceType;
  }

  /**
   * @return the node2Label
   */
  public String[] getNode2Label() {
    return node2Label;
  }

  /**
   * @param node2Label
   *          the node2Label to set
   */
  public void setNode2Label(String[] node2Label) {
    this.node2Label = node2Label;
  }

  /**
   * @return the label2Node
   */
  public Map<String, Integer> getLabel2Node() {
    return label2Node;
  }

  /**
   * @param label2Node
   *          the label2Node to set
   */
  public void setLabel2Node(Map<String, Integer> label2Node) {
    this.label2Node = label2Node;
  }

  /**
   * @return the sampleInit
   */
  public boolean isSampleInit() {
    return isSampleInit;
  }

  /**
   * @param sampleInit
   *          the sampleInit to set
   */
  public void setSampleInit(boolean sampleInit) {
    this.isSampleInit = sampleInit;
  }

  /**
   * 
   */
  private void setNodeLabel() {
    String str = "VN_";
    for (int i = 0; i < this.nodeSize; i++) {
      this.node2Label[i] = str + (i + 1);
      label2Node.put(str + (i + 1), 0);
    }
  }

  /**
   * @return the index
   */
  public int getIndex() {
    return index;
  }

  /**
   * @param index
   *          the index to set
   */
  public void setIndex(int index) {
    this.index = index;
  }

  public void initSample1() {
    nodeComputationDemand[0] = 2;
    nodeComputationDemand[1] = 3;
    nodeComputationDemand[2] = 5;
    nodeComputationDemand[3] = 6;
    nodeComputationCapacity[0] = 5;
    nodeComputationCapacity[1] = 7;
    nodeComputationCapacity[2] = 7;
    nodeComputationCapacity[3] = 10;
    virNode2subNode[0] = 0;
    virNode2subNode[1] = 1;
    virNode2subNode[2] = 2;
    virNode2subNode[3] = 3;

    edgeBandwithDemand[0][1] = 4;
    edgeBandwithDemand[0][2] = 5;
    edgeBandwithDemand[0][3] = 3;
    edgeBandwithDemand[1][2] = 6;
    edgeBandwithDemand[1][0] = 4;
    edgeBandwithDemand[2][0] = 5;
    edgeBandwithDemand[3][0] = 3;
    edgeBandwithDemand[2][1] = 6;

    for (int i = 0; i < nodeSize; i++) {
      for (int j = 0; j < nodeSize; j++) {
        if (0 != edgeBandwithDemand[i][j]) {
          topology[i][j] = true;
        }
      }
    }

    nodeServiceType[0] = 0;
    nodeServiceType[1] = 1;
    nodeServiceType[2] = 2;
    nodeServiceType[3] = 3;
  }

  public void initSample2() {
    nodeComputationDemand[0] = 3;
    nodeComputationDemand[1] = 4;

    edgeBandwithDemand[0][1] = 3;
    edgeBandwithDemand[1][0] = 3;

    for (int i = 0; i < nodeSize; i++) {
      for (int j = 0; j < nodeSize; j++) {
        if (0 != edgeBandwithDemand[i][j]) {
          topology[i][j] = true;
        }
      }
    }

    nodeServiceType[0] = 0;
    nodeServiceType[1] = 1;
  }

  public void initSample3() {
    nodeComputationDemand[0] = 3;

    nodeServiceType[0] = 0;
  }

  /**
   * @return the leaveTime
   */
  public int getLeaveTime() {
    return leaveTime;
  }

  /**
   * @param leaveTime
   *          the leaveTime to set
   */
  public void setLeaveTime(int leaveTime) {
    this.leaveTime = leaveTime;
  }

  public boolean getIsRunning() {
    return isRunning;
  }

  public void setIsRunning(boolean isRunning) {
    this.isRunning = isRunning;
  }

}
