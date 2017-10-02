package substratenetwork;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import virtualnetwork.SurvivalVirtualNetwork;
import virtualnetwork.VirtualNetwork;

public class SubstrateNetwork implements Cloneable {
  public int nodeSize;
  public int[] nodeComputationCapacity;
  public int[] nodeComputation4Former;
  public int[] nodeComputation4SurvivalUnsharedBackupSum;
  public int[] nodeComputation4Temp;
  public Vector<Vector<Integer>> virNetIndexSet4sNode;
  public Vector<Vector<Integer>> surVirNetIndexSet4sNode;

  public boolean[][] topology;
  public int[][] edgeBandwithCapacity;
  public int[][] edgeBandwith4Former;
  public int[][] edgeBandwith4SurvivalUnsharedBackupSum;
  public int[][] edgeBandwith4Temp;
  public Vector<Vector<Vector<Integer>>> virNetIndexSet4Edge;
  public Vector<Vector<Vector<Integer>>> surVirNetIndexSet4Edge;

  public int serviceNum;
  public boolean[][] boolServiceTypeSet;
  public Vector<Vector<Integer>> vectorServiceTypeSet;

  public String[] node2Label;
  public Map<String, Integer> label2Node;

  public Vector<VirtualNetwork> virNetCollection;
  public Vector<SurvivalVirtualNetwork> surVirNetSet;

  // acceptionRatio
  public int virNetSuceedEmbedSum;
  public int surVirNetSuceedEmbedSum;
  // EmbeddingCost
  public int virNetSum;

  /**
   * SubstrateNetwork.
   * 
   * @param snp
   *          snp
   */
  public SubstrateNetwork(SubStrateNetworkParameter snp) {
    // node
    this.nodeSize = snp.getNodeSize();
    this.nodeComputationCapacity = new int[nodeSize];
    this.nodeComputation4Former = new int[nodeSize];
    this.nodeComputation4SurvivalUnsharedBackupSum = new int[nodeSize];
    this.nodeComputation4Temp = new int[nodeSize];
    this.virNetIndexSet4sNode = new Vector<Vector<Integer>>();
    this.surVirNetIndexSet4sNode = new Vector<Vector<Integer>>();
    for (int i = 0; i < nodeSize; i++) {
      virNetIndexSet4sNode.addElement(new Vector<Integer>());
      surVirNetIndexSet4sNode.addElement(new Vector<Integer>());
    }

    // edge
    this.topology = new boolean[nodeSize][nodeSize];
    this.edgeBandwithCapacity = new int[nodeSize][nodeSize];
    edgeBandwith4Former = new int[nodeSize][nodeSize];
    edgeBandwith4SurvivalUnsharedBackupSum = new int[nodeSize][nodeSize];
    edgeBandwith4Temp = new int[nodeSize][nodeSize];
    this.virNetIndexSet4Edge = new Vector<Vector<Vector<Integer>>>();
    this.surVirNetIndexSet4Edge = new Vector<Vector<Vector<Integer>>>();
    for (int i = 0; i < nodeSize; i++) {
      this.virNetIndexSet4Edge.addElement(new Vector<Vector<Integer>>());
      this.surVirNetIndexSet4Edge.addElement(new Vector<Vector<Integer>>());
      for (int j = 0; j < nodeSize; j++) {
        this.virNetIndexSet4Edge.get(i).add(new Vector<Integer>());
        this.surVirNetIndexSet4Edge.get(i).add(new Vector<Integer>());
      }
    }

    // service
    this.serviceNum = snp.getServiceNumber();
    this.boolServiceTypeSet = new boolean[nodeSize][serviceNum];
    this.vectorServiceTypeSet = new Vector<Vector<Integer>>();
    for (int i = 0; i < this.nodeSize; i++) {
      this.vectorServiceTypeSet.addElement(new Vector<Integer>());
    }

    // label
    this.node2Label = new String[nodeSize];
    this.label2Node = new HashMap<String, Integer>();

    this.virNetCollection = new Vector<VirtualNetwork>();
    this.surVirNetSet = new Vector<SurvivalVirtualNetwork>();
    if (snp.isSampleInit()) {
      faultSetResourceDistribution();
    } else {
      setResourceDistribution(snp);
    }

    this.virNetSuceedEmbedSum = 0;
    this.surVirNetSuceedEmbedSum = 0;
    this.virNetSum = 0;
  }

  /**
   * getSubstrateRemainComputaion4EVN.
   * 
   * @param nodeloc
   *          nodeloc
   * @param isShared
   *          isShared
   * @return int
   */
  public int getSubstrateRemainComputaion4SurVirNet(int nodeloc, boolean isShared) {
    int remainComputation;
    if (isShared) {
      remainComputation = this.nodeComputationCapacity[nodeloc]
          - this.nodeComputation4Former[nodeloc] - this.nodeComputation4Temp[nodeloc];
    } else {
      remainComputation = this.nodeComputationCapacity[nodeloc]
          - this.nodeComputation4Former[nodeloc] - this.nodeComputation4Temp[nodeloc]
          - this.nodeComputation4SurvivalUnsharedBackupSum[nodeloc];
    }
    return remainComputation;
  }

  /**
   * getSubstrateRemainComputaion4VirNet.
   * 
   * @param nodeloc
   *          nodeloc
   * @param isShared
   *          isShared
   * @return RemainComputaion
   */
  public int getSubstrateRemainComputaion4VirNet(int nodeloc, boolean isShared) {
    int remainComputation = 0;
    if (isShared) {
      int maxShare = 0;
      for (int i = 0; i < this.surVirNetIndexSet4sNode.get(nodeloc).size(); i++) {
        int ithSurVirNet = this.surVirNetIndexSet4sNode.get(nodeloc).get(i);
        int temp = 0;

        if (this.virNetCollection.get(ithSurVirNet).getIsRunning()
            && this.surVirNetSet.get(ithSurVirNet).isSucceedEmbed) {
          for (int j = 0; j < this.surVirNetSet.get(ithSurVirNet).getNodeSize(); j++) {
            if (nodeloc == this.surVirNetSet.get(ithSurVirNet).surNode2subNode[j]) {
              // one substrate network node map multiple virtual
              // network request and network node
              temp += this.surVirNetSet.get(ithSurVirNet).nodeComputation4Backup[j];
            }
          }
        }

        maxShare = Math.max(maxShare, temp);
      }
      remainComputation = this.nodeComputationCapacity[nodeloc]
          - this.nodeComputation4Former[nodeloc] - this.nodeComputation4Temp[nodeloc] - maxShare;

    } else {
      remainComputation = this.nodeComputationCapacity[nodeloc]
          - this.nodeComputation4Former[nodeloc] - this.nodeComputation4Temp[nodeloc]
          - this.nodeComputation4SurvivalUnsharedBackupSum[nodeloc];
    }
    return remainComputation;
  }

  /**
   * getSubStrateRemainBandwith4SurVirNet.
   * 
   * @param from
   *          from
   * @param to
   *          to
   * @param isShared
   *          isShared
   * @return int
   */
  public int getSubStrateRemainBandwith4SurVirNet(int from, int to, boolean isShared) {
    int remain = 0;
    if (isShared) {
      remain = this.edgeBandwithCapacity[from][to] - this.edgeBandwith4Former[from][to]
          - this.edgeBandwith4Temp[from][to];
    } else {
      remain = this.edgeBandwithCapacity[from][to] - this.edgeBandwith4Former[from][to]
          - this.edgeBandwith4Temp[from][to]
          - this.edgeBandwith4SurvivalUnsharedBackupSum[from][to];
    }
    return remain;
  }

  /**
   * @param from
   * @param to
   * @param isShared
   * @return
   */
  public int getSubStrateRemainBandwith4VN(int from, int to, boolean isShared) {
    int remain = 0;
    if (isShared) {
      int maxShare = 0;
      for (int i = 0; i < this.surVirNetIndexSet4Edge.get(from).get(to).size(); i++) {
        int ithEVN = this.surVirNetIndexSet4Edge.get(from).get(to).get(i);

        int tempBandwith = 0;
        if (this.virNetCollection.get(ithEVN).getIsRunning()
            && this.surVirNetSet.get(ithEVN).isSucceedEmbed) {
          for (int p = 0; p < this.surVirNetSet.get(ithEVN).getNodeSize(); p++) {
            for (int q = 0; q < this.surVirNetSet.get(ithEVN).getNodeSize(); q++) {
              for (int t = 0; t < (this.surVirNetSet.get(ithEVN).surEdge2SubPath.get(p).get(q)
                  .size() - 1); t++) {
                if (from == this.surVirNetSet.get(ithEVN).surEdge2SubPath.get(p).get(q).get(t)
                    && (to == this.surVirNetSet.get(ithEVN).surEdge2SubPath.get(p).get(q)
                        .get(t + 1))) {

                  tempBandwith += this.surVirNetSet.get(ithEVN).edgeBandwith4Backup[p][q];
                }
              }
            }
          }
        }
        maxShare = Math.max(maxShare, tempBandwith);
      }
      remain = this.edgeBandwithCapacity[from][to] - this.edgeBandwith4Former[from][to]
          - this.edgeBandwith4Temp[from][to] - maxShare;
    } else {
      remain = this.edgeBandwithCapacity[from][to] - this.edgeBandwith4Former[from][to]
          - this.edgeBandwith4Temp[from][to]
          - this.edgeBandwith4SurvivalUnsharedBackupSum[from][to];
    }
    return remain;
  }

  /**
   * setResourceDistribution.
   * 
   * @param snp
   *          snp
   */
  private void setResourceDistribution(SubStrateNetworkParameter snp) {
    // node computation
    for (int i = 0; i < this.nodeSize; i++) {
      this.nodeComputationCapacity[i] = (int) (snp.getNodeComputationMinimum()
          + Math.random() * (snp.getNodeComputationMaximum() - snp.getNodeComputationMinimum()));

    }

    // edge bandwith
    for (int i = 0; i < this.nodeSize; i++) {
      for (int j = 0; j < i; j++) {
        if (Math.random() < snp.getNode2nodeProbability()) {
          this.topology[i][j] = this.topology[j][i] = true;
        }
      }
    }
    for (int i = 0; i < this.nodeSize; i++) {
      for (int j = 0; j < i; j++) {
        if (this.topology[i][j]) {
          this.edgeBandwithCapacity[i][j] = (int) (snp.getEdgeBandwithMinimum()
              + Math.random() * (snp.getEdgeBandwithMaximum() - snp.getEdgeBandwithMinimum()));
          this.edgeBandwithCapacity[j][i] = this.edgeBandwithCapacity[i][j];
        }
      }
    }

    // service
    for (int i = 0; i < this.nodeSize; i++) {
      for (int j = 0; j < this.serviceNum; j++) {
        if (Math.random() < snp.getSerivecProbability()) {
          this.boolServiceTypeSet[i][j] = true;
          vectorServiceTypeSet.get(i).addElement(j);
        }
      }
      if (vectorServiceTypeSet.get(i).size() == 0) {
        int index = (int) ((this.serviceNum - 1) * Math.random());
        this.boolServiceTypeSet[i][index] = true;
        vectorServiceTypeSet.get(i).addElement(index);
      }
    }

    // label
    String str = "SN";
    for (int i = 0; i < this.nodeSize; i++) {
      node2Label[i] = (str + (i + 1));
      label2Node.put((str + (1 + i)), i);
    }
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    SubstrateNetwork sn = (SubstrateNetwork) super.clone();
    sn.nodeComputationCapacity = new int[sn.nodeSize];
    sn.nodeComputationCapacity = Arrays.copyOf(this.nodeComputationCapacity, this.nodeSize);
    sn.nodeComputation4Former = new int[sn.nodeSize];
    sn.nodeComputation4SurvivalUnsharedBackupSum = new int[sn.nodeSize];
    sn.nodeComputation4Temp = new int[sn.nodeSize];

    // sn.VNQIndexSet4sNode = (Vector<Vector<Integer>>) ((SubstrateNetwork)
    // super.clone()).VNQIndexSet4sNode.clone();
    // sn.EVNIndexSet4sNode = (Vector<Vector<Integer>>) ((SubstrateNetwork)
    // super.clone()).VNQIndexSet4sNode.clone();
    sn.virNetIndexSet4sNode = new Vector<Vector<Integer>>();
    sn.surVirNetIndexSet4sNode = new Vector<Vector<Integer>>();
    for (int i = 0; i < sn.nodeSize; i++) {
      sn.virNetIndexSet4sNode.addElement(new Vector<Integer>());
      sn.surVirNetIndexSet4sNode.addElement(new Vector<Integer>());
    }

    sn.topology = new boolean[sn.nodeSize][sn.nodeSize];
    sn.edgeBandwithCapacity = new int[sn.nodeSize][sn.nodeSize];
    for (int i = 0; i < this.nodeSize; i++) {
      sn.topology[i] = Arrays.copyOf(this.topology[i], this.nodeSize);
      sn.edgeBandwithCapacity[i] = Arrays.copyOf(this.edgeBandwithCapacity[i], this.nodeSize);
    }
    sn.edgeBandwith4Former = new int[sn.nodeSize][sn.nodeSize];
    sn.edgeBandwith4SurvivalUnsharedBackupSum = new int[sn.nodeSize][sn.nodeSize];
    sn.edgeBandwith4Temp = new int[sn.nodeSize][sn.nodeSize];

    // sn.VNCollection = (Vector<VirtualNetwork>) ((SubstrateNetwork)
    // super.clone()).VNCollection.clone();
    // sn.EVNCollection = (Vector<EnhancedVirtualNetwork>)
    // ((SubstrateNetwork) super.clone()).EVNCollection.clone();

    sn.virNetCollection = new Vector<VirtualNetwork>();
    sn.surVirNetSet = new Vector<SurvivalVirtualNetwork>();

    // sn.VNQIndexSet4Edge = (Vector<Vector<Vector<Integer>>>)
    // ((SubstrateNetwork) super.clone()).VNQIndexSet4Edge
    // .clone();
    // sn.EVNIndexSet4Edge = (Vector<Vector<Vector<Integer>>>)
    // ((SubstrateNetwork) super.clone()).EVNIndexSet4Edge
    // .clone();
    sn.virNetIndexSet4Edge = new Vector<Vector<Vector<Integer>>>();
    sn.surVirNetIndexSet4Edge = new Vector<Vector<Vector<Integer>>>();

    for (int i = 0; i < nodeSize; i++) {
      sn.virNetIndexSet4Edge.addElement(new Vector<Vector<Integer>>());
      sn.surVirNetIndexSet4Edge.addElement(new Vector<Vector<Integer>>());
      for (int j = 0; j < nodeSize; j++) {
        sn.virNetIndexSet4Edge.get(i).add(new Vector<Integer>());
        sn.surVirNetIndexSet4Edge.get(i).add(new Vector<Integer>());
      }
    }
    return sn;
  }

  /**
   * clearTempResource.
   */
  public void clearTempResource() {
    for (int i = 0; i < this.nodeSize; i++) {
      this.nodeComputation4Temp[i] = 0;
      for (int j = 0; j < i; j++) {
        this.edgeBandwith4Temp[i][j] = this.edgeBandwith4Temp[j][i] = 0;
      }
    }
  }

  public void faultSetResourceDistribution() {
    nodeComputationCapacity[0] = 5;
    nodeComputationCapacity[1] = 7;
    nodeComputationCapacity[2] = 7;
    nodeComputationCapacity[3] = 10;
    nodeComputationCapacity[4] = 6;
    nodeComputationCapacity[5] = 9;
    nodeComputationCapacity[6] = 8;
    nodeComputationCapacity[7] = 9;
    nodeComputationCapacity[8] = 8;

    for (int i = 0; i < nodeSize; i++) {
      for (int j = 0; j < nodeSize; j++) {
        topology[i][j] = true;
        edgeBandwithCapacity[i][j] = 30;
      }
    }
    boolServiceTypeSet[0][0] = true;
    boolServiceTypeSet[1][1] = true;
    boolServiceTypeSet[1][2] = true;
    boolServiceTypeSet[2][2] = true;
    boolServiceTypeSet[3][3] = true;

    boolServiceTypeSet[4][0] = true;
    boolServiceTypeSet[4][1] = true;
    boolServiceTypeSet[5][0] = true;
    boolServiceTypeSet[5][3] = true;
    boolServiceTypeSet[6][1] = true;
    boolServiceTypeSet[6][2] = true;
    boolServiceTypeSet[7][0] = true;
    boolServiceTypeSet[7][3] = true;
    boolServiceTypeSet[8][3] = true;

    node2Label[0] = "S1";
    node2Label[1] = "S2";
    node2Label[2] = "S3";
    node2Label[3] = "S4";
    node2Label[4] = "S5";
    node2Label[5] = "S6";
    node2Label[6] = "S7";
    node2Label[7] = "S8";
    node2Label[8] = "S9";

    label2Node.put("S1", 0);
    label2Node.put("S2", 1);
    label2Node.put("S3", 2);
    label2Node.put("S4", 3);
    label2Node.put("S5", 4);
    label2Node.put("S6", 5);
    label2Node.put("S7", 6);
    label2Node.put("S8", 7);
    label2Node.put("S9", 8);
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

}
