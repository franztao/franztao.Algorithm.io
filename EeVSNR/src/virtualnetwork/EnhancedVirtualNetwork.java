/**
 * 
 */

package virtualnetwork;

import evsnr.Parameter;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import multipleknapsack.MulitpleKnapsack;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import substratenetwork.BackupNode;
import substratenetwork.SubstrateNetwork;

/**
 * EVN.
 * 
 * @author franz
 *
 */
public class EnhancedVirtualNetwork {

  private Logger loggerEnhancedVirtualNetwork = Logger.getLogger(EnhancedVirtualNetwork.class);

  public final int matchMethod = Parameter.MatchMethod;

  // nodeSize=enhacnedNodeSize+backupNodeSize
  public int nodeSize;
  public int nodeSize4Embeded;
  public int nodeSize4Backup;
  // nodeComputationCapacity-usedNodeCurrentComputationCapacity
  public int nodeComputationCapacity[];
  public int nodeComputationUsed[];
  public int nodeComputationEnhanced[];
  public boolean[] isSubNodeCompEmpty;

  public boolean topology[][];
  // edgeBandwithCapacity-usedEdgeCurrentBandwithCapacity
  public int edgeBandwithUsed[][];
  public int edgeBandwithEnhanced[][];
  public Vector<Vector<Vector<Integer>>> eEdge2sPath;

  public int serviceNumber;
  public boolean boolServiceTypeSet[][];

  public String node2Label[];
  public Map<String, Integer> label2Node;

  public int vNode2eNode[];
  public int eNode2sNode[];

  public VirtualNetwork VN;

  // public boolean sampleInit;

  class StarEdgeStructure {
    int neighborVNID;
    int neighborEnhancedVNID;
    int neighborNodeType;
    int neighborEdgeBandwithCapacity;
    // public int neighborUsedEdgeBandwithCapacity;

  }

  class starStructure {
    int starNodeServiceType;
    int starNodeComputation;
    int starNodeEnhancedVNID;
    Vector<StarEdgeStructure> neighborEdge;
    // Vector<Integer> neighborEnhancedVNID;
    // Vector<Integer> neighborEnhancedVNType;
    // Vector<Integer> neighborEdgeBandwith;
  }

  class ConsumeResource {
    int initNodeNumber;
    int initNodeComputation;
    int initEdgeBandwith;

    int consumedNodeNumber;
    int consumedNodeComputation;
    int consumeEdgeBandwith;
  }

  ConsumeResource consumedResource;

  starStructure Items[];
  Vector<starStructure> Knapsacks;

  public boolean isSucceed;

  /**
   * @param vn
   * @param path
   * @param bn
   */
  public EnhancedVirtualNetwork(SubstrateNetwork sn, VirtualNetwork vn, BackupNode bn) {
    PropertyConfigurator.configure("log4j.properties");
    this.VN = vn;
    this.isSucceed = false;
    // node
    this.nodeSize4Embeded = vn.nodeSize;
    this.nodeSize4Backup = bn.backupNodeSize;
    this.nodeSize = this.nodeSize4Embeded + this.nodeSize4Backup;
    this.nodeComputationCapacity = new int[this.nodeSize];
    this.nodeComputationUsed = new int[this.nodeSize];
    this.nodeComputationEnhanced = new int[this.nodeSize];
    this.isSubNodeCompEmpty = new boolean[this.nodeSize];
    // edge
    this.topology = new boolean[this.nodeSize][this.nodeSize];
    this.edgeBandwithUsed = new int[this.nodeSize][this.nodeSize];
    this.edgeBandwithEnhanced = new int[this.nodeSize][this.nodeSize];
    eEdge2sPath = new Vector<Vector<Vector<Integer>>>();
    for (int i = 0; i < this.nodeSize; i++) {
      eEdge2sPath.addElement(new Vector<Vector<Integer>>());
      for (int j = 0; j < this.nodeSize; j++) {
        eEdge2sPath.get(i).addElement(new Vector<Integer>());
      }
    }
    // service
    this.serviceNumber = vn.serviceNumber;
    this.boolServiceTypeSet = new boolean[nodeSize][serviceNumber];
    // label
    this.node2Label = new String[nodeSize];
    this.label2Node = new HashMap<String, Integer>();
    // embed function
    this.vNode2eNode = new int[this.VN.nodeSize];
    this.eNode2sNode = new int[this.nodeSize];
    // knapsack problem
    this.Items = new starStructure[this.VN.nodeSize];

    this.consumedResource = new ConsumeResource();

    constrcutEVN(sn, vn, bn);
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
   * @param vn
   * @param path
   * @param bn
   */
  private void constrcutEVN(SubstrateNetwork sn, VirtualNetwork vn, BackupNode bn) {
    // node
    for (int i = 0; i < this.nodeSize; i++) {
      if (i < this.nodeSize4Embeded) {
        this.nodeComputationCapacity[i] = vn.nodeComputationCapacity[i];
        this.nodeComputationUsed[i] = vn.nodeComputationDemand[i];
        this.eNode2sNode[i] = vn.vNode2sNode[i];

      } else {
        this.nodeComputationCapacity[i] = bn.nodeComputationCapacity[i - this.nodeSize4Embeded];
        this.nodeComputationUsed[i] = 0;
        this.eNode2sNode[i] = bn.bNode2sNode[i - this.nodeSize4Embeded];
        if (bn.isHaveSubstrateNodeResource4buNode[i - this.nodeSize4Embeded]) {
          this.isSubNodeCompEmpty[i] = true;
        }
      }
    }
    for (int i = 0; i < this.nodeSize4Embeded; i++) {
      this.vNode2eNode[i] = i;
    }
    // edge
    for (int i = 0; i < this.nodeSize; i++) {
      for (int j = 0; j < i; j++) {
        if ((i < this.nodeSize4Embeded) && (vn.edgeBandwithDemand[this.vNode2eNode[i]][this.vNode2eNode[j]] > 0)) {
          this.topology[this.vNode2eNode[i]][this.vNode2eNode[j]] = this.topology[this.vNode2eNode[j]][this.vNode2eNode[i]] = true;
          this.edgeBandwithUsed[this.vNode2eNode[i]][this.vNode2eNode[j]] = this.edgeBandwithUsed[this.vNode2eNode[j]][this.vNode2eNode[i]] = vn.edgeBandwithDemand[this.vNode2eNode[i]][this.vNode2eNode[j]];
        }
      }
    }

    // service
    for (int i = 0; i < this.nodeSize; i++) {
      for (int j = 0; j < this.serviceNumber; j++) {
        if (i < this.nodeSize4Embeded) {
          this.boolServiceTypeSet[i][j] = sn.boolServiceTypeSet[vn.vNode2sNode[i]][j];
        } else {
          this.boolServiceTypeSet[i][j] = sn.boolServiceTypeSet[bn.bNode2sNode[i - this.nodeSize4Embeded]][j];
        }
      }
    }

    // label
    String estr = "EN_";
    String bstr = "BN_";
    for (int i = 0; i < this.nodeSize; i++) {
      if (i < this.nodeSize4Embeded) {
        this.node2Label[i] = estr + i;
        this.label2Node.put(estr + i, i);
      } else {
        this.node2Label[i] = bstr + (i - this.nodeSize4Embeded);
        this.label2Node.put(bstr + (i - this.nodeSize4Embeded), i);
      }
    }

    for (int i = 0; i < this.nodeSize; i++) {
      if (this.nodeComputationUsed[i] > 0) {
        consumedResource.initNodeNumber++;
        consumedResource.consumedNodeNumber++;

        consumedResource.initNodeComputation += this.nodeComputationUsed[i];
        consumedResource.consumedNodeComputation += this.nodeComputationUsed[i];
      }
    }

    for (int i = 0; i < this.nodeSize; i++) {
      for (int j = 0; j < this.nodeSize; j++) {
        consumedResource.consumeEdgeBandwith += this.edgeBandwithUsed[i][j];
        consumedResource.initEdgeBandwith += this.edgeBandwithUsed[i][j];
      }
    }
    consumedResource.consumeEdgeBandwith /= 2;
    consumedResource.initEdgeBandwith /= 2;

  }

  public void initSample1() {
    nodeComputationCapacity[0] = 5;
    nodeComputationCapacity[1] = 7;
    nodeComputationCapacity[2] = 7;
    nodeComputationCapacity[3] = 10;
    nodeComputationCapacity[4] = 6;
    nodeComputationCapacity[5] = 9;
    nodeComputationCapacity[6] = 8;

    nodeComputationUsed[0] = 2;
    nodeComputationUsed[1] = 3;
    nodeComputationUsed[2] = 5;
    nodeComputationUsed[3] = 6;

    edgeBandwithUsed[0][1] = 4;
    edgeBandwithUsed[0][2] = 5;
    edgeBandwithUsed[0][3] = 3;
    edgeBandwithUsed[1][2] = 6;
    edgeBandwithUsed[1][0] = 4;
    edgeBandwithUsed[2][0] = 5;
    edgeBandwithUsed[3][0] = 3;
    edgeBandwithUsed[2][1] = 6;

    for (int i = 0; i < nodeSize; i++) {
      for (int j = 0; j < nodeSize; j++) {
        if (0 != edgeBandwithUsed[i][j]) {
          topology[i][j] = true;
        }
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

    node2Label[0] = "E1";
    node2Label[1] = "E2";
    node2Label[2] = "E3";
    node2Label[3] = "E4";
    node2Label[4] = "B1";
    node2Label[5] = "B2";
    node2Label[6] = "B3";

    label2Node.put("E1", 0);
    label2Node.put("E2", 1);
    label2Node.put("E3", 2);
    label2Node.put("E4", 3);
    label2Node.put("B1", 4);
    label2Node.put("B2", 5);
    label2Node.put("B3", 6);

    // there may be some virtual node located into the same enhanced node
    vNode2eNode[0] = 0;
    vNode2eNode[1] = 1;
    vNode2eNode[2] = 2;
    vNode2eNode[3] = 3;

    nodeSize4Backup = 3;
    nodeSize4Embeded = 4;

    consumedResource.initEdgeBandwith = 18;
    consumedResource.initNodeComputation = 16;
    consumedResource.initNodeNumber = 4;

    consumedResource.consumeEdgeBandwith = 18;
    consumedResource.consumedNodeComputation = 16;
    consumedResource.consumedNodeNumber = 4;

  }

  /**
   * 
   */
  public void initSample2() {
    nodeComputationCapacity[0] = 7;
    nodeComputationCapacity[1] = 5;
    nodeComputationCapacity[2] = 3;
    nodeComputationCapacity[3] = 7;

    edgeBandwithUsed[0][1] = 3;
    edgeBandwithUsed[1][0] = 3;

    for (int i = 0; i < nodeSize; i++) {
      for (int j = 0; j < nodeSize; j++) {
        if (0 != edgeBandwithUsed[i][j]) {
          topology[i][j] = true;
        }
      }
    }

    nodeComputationUsed[0] = 3;
    nodeComputationUsed[1] = 4;

    boolServiceTypeSet[0][0] = true;
    boolServiceTypeSet[0][1] = true;
    boolServiceTypeSet[1][1] = true;
    boolServiceTypeSet[2][0] = true;
    boolServiceTypeSet[3][0] = true;
    boolServiceTypeSet[3][1] = true;

    node2Label[0] = "E1";
    node2Label[1] = "E2";
    node2Label[2] = "B1";
    node2Label[3] = "B2";

    label2Node.put("E1", 0);
    label2Node.put("E2", 1);
    label2Node.put("B1", 2);
    label2Node.put("B2", 3);

    // there may be some virtual node located into the same enhanced node
    vNode2eNode[0] = 0;
    vNode2eNode[1] = 1;

    nodeSize4Backup = 2;
    nodeSize4Embeded = 2;

    consumedResource.initEdgeBandwith = 3;
    consumedResource.initNodeComputation = 7;
    consumedResource.initNodeNumber = 2;

    consumedResource.consumeEdgeBandwith = 3;
    consumedResource.consumedNodeComputation = 7;
    consumedResource.consumedNodeNumber = 2;
  }

  /**
   * 
   */
  public void initSample3() {
    nodeComputationCapacity[0] = 5;
    nodeComputationCapacity[1] = 5;

    nodeComputationUsed[0] = 3;

    boolServiceTypeSet[0][0] = true;
    boolServiceTypeSet[1][0] = true;
    boolServiceTypeSet[1][1] = true;

    node2Label[0] = "E1";
    node2Label[1] = "E2";

    label2Node.put("E1", 0);
    label2Node.put("B1", 1);

    // there may be some virtual node located into the same enhanced node
    vNode2eNode[0] = 0;

    nodeSize4Backup = 1;
    nodeSize4Embeded = 1;

    consumedResource.initEdgeBandwith = 0;
    consumedResource.initNodeComputation = 3;
    consumedResource.initNodeNumber = 1;

    consumedResource.consumeEdgeBandwith = 0;
    consumedResource.consumedNodeComputation = 3;
    consumedResource.consumedNodeNumber = 1;
  }

  public void obtainItems() {
    for (int i = 0; i < this.VN.nodeSize; i++) {
      Items[i] = new starStructure();
      // Items[i].starNodeVNID = i;
      Items[i].starNodeEnhancedVNID = this.vNode2eNode[i];
      Items[i].starNodeComputation = this.VN.nodeComputationDemand[i];
      Items[i].starNodeServiceType = this.VN.nodeServiceType[i];
      Items[i].neighborEdge = new Vector<StarEdgeStructure>();
      for (int j = 0; j < this.VN.nodeSize; j++) {
        if (this.VN.topology[i][j]) {
          StarEdgeStructure edge = new StarEdgeStructure();
          edge.neighborVNID = j;
          edge.neighborEnhancedVNID = this.vNode2eNode[j];
          edge.neighborEdgeBandwithCapacity = this.VN.edgeBandwithDemand[i][j];
          edge.neighborNodeType = this.VN.nodeServiceType[j];
          Items[i].neighborEdge.addElement(edge);
        }
      }
    }
  }

  void constructKnapsacks(int failureNodeID) {
    Knapsacks = new Vector<starStructure>();
    for (int i = 0; i < this.nodeSize; i++) {
      if (i != failureNodeID) {
        if ((i < this.nodeSize4Embeded)
            && (this.eNode2sNode[this.vNode2eNode[i]] != this.eNode2sNode[this.vNode2eNode[failureNodeID]])) {
          continue;
        }
        for (int j = 0; j < this.serviceNumber; j++) {
          if (this.boolServiceTypeSet[i][j]) {
            starStructure bag = new starStructure();
            bag.starNodeServiceType = j;
            bag.starNodeEnhancedVNID = i;
            bag.starNodeComputation = this.nodeComputationUsed[i];
            // ineffective value could be erased
            // bag.starNodeVNID = i;
            bag.neighborEdge = new Vector<StarEdgeStructure>();
            for (int k = 0; k < this.VN.nodeSize; k++) {
              if (failureNodeID != this.vNode2eNode[k]) {
                StarEdgeStructure edge = new StarEdgeStructure();
                edge.neighborVNID = k;
                edge.neighborEnhancedVNID = this.vNode2eNode[k];
                if (i == this.vNode2eNode[k])
                  edge.neighborEdgeBandwithCapacity = Integer.MAX_VALUE;
                else
                  edge.neighborEdgeBandwithCapacity = this.edgeBandwithUsed[i][this.vNode2eNode[k]];
                edge.neighborNodeType = this.VN.nodeServiceType[k];
                bag.neighborEdge.addElement(edge);
              }
            }
            Knapsacks.addElement(bag);
          }
        }
      }

    }
  }

  public int failIthNode(int failureNodeID, boolean failurtype, int[] solution) throws GRBException {
    constructKnapsacks(failureNodeID);
    MulitpleKnapsack MKP = new MulitpleKnapsack(this.VN.nodeSize, Knapsacks.size(), this.nodeSize);
    constructMultipleKnapsackProbem(MKP, failurtype);
    int optimalResult = -1;
    if (this.matchMethod == Parameter.MatchMethodDP) {
      optimalResult = MKP.optimalSoutionDP(solution);
      if (optimalResult == -1) {
        loggerEnhancedVirtualNetwork.warn("Failure node: " + failureNodeID + ", there is not solution");
        return -1;
      }
    }

    if (this.matchMethod == Parameter.MatchMethodILP) {
      optimalResult = MKP.optimalSoutionILP(solution);
      if (optimalResult == -1) {
        loggerEnhancedVirtualNetwork.warn("Failure node: " + failureNodeID + ", there is not solution");
        return -1;
      }
    }

    return optimalResult;

  }

  /**
   * @param solution
   */
  private void augmentNodeEdge(int[] solution, int failurenodeID, int Method) {

    int virutialNode2NewEnhancedVirtualNode[] = new int[this.VN.nodeSize];
    if (Method == Parameter.MatchMethodILP) {
      for (int i = 0; i < this.VN.nodeSize; i++) {
        virutialNode2NewEnhancedVirtualNode[i] = Knapsacks.elementAt(solution[i]).starNodeEnhancedVNID;
      }
    }
    if (Method == Parameter.MatchMethodDP) {
      for (int i = 0; i < this.VN.nodeSize; i++) {
        virutialNode2NewEnhancedVirtualNode[i] = solution[i];
        // System.out.println(virutialNode2NewVirtualNode[i] + 1);
      }
    }

    for (int i = 0; i < this.VN.nodeSize; i++) {
      // add node computation
      if (this.nodeComputationUsed[virutialNode2NewEnhancedVirtualNode[i]] < Items[i].starNodeComputation)
        this.nodeComputationUsed[virutialNode2NewEnhancedVirtualNode[i]] = Items[i].starNodeComputation;
      for (int j = 0; j < Items[i].neighborEdge.size(); j++) {
        int neighborVNNode = Items[i].neighborEdge.elementAt(j).neighborVNID;
        // if (failurenodeID !=
        // virutialNode2NewVirtualNode[neighborVNNode]) {
        if (virutialNode2NewEnhancedVirtualNode[i] != virutialNode2NewEnhancedVirtualNode[neighborVNNode])
          if (this.edgeBandwithUsed[virutialNode2NewEnhancedVirtualNode[i]][virutialNode2NewEnhancedVirtualNode[neighborVNNode]] < Items[i].neighborEdge
              .elementAt(j).neighborEdgeBandwithCapacity) {
            this.edgeBandwithUsed[virutialNode2NewEnhancedVirtualNode[i]][virutialNode2NewEnhancedVirtualNode[neighborVNNode]] = Items[i].neighborEdge
                .elementAt(j).neighborEdgeBandwithCapacity;
            this.edgeBandwithUsed[virutialNode2NewEnhancedVirtualNode[neighborVNNode]][virutialNode2NewEnhancedVirtualNode[i]] = Items[i].neighborEdge
                .elementAt(j).neighborEdgeBandwithCapacity;
            this.topology[virutialNode2NewEnhancedVirtualNode[i]][virutialNode2NewEnhancedVirtualNode[neighborVNNode]] = this.topology[virutialNode2NewEnhancedVirtualNode[neighborVNNode]][virutialNode2NewEnhancedVirtualNode[i]] = true;
            // }
          }
      }
    }
  }

  boolean constructMultipleKnapsackProbem(MulitpleKnapsack multKnaP, boolean failureType) {
    for (int i = 0; i < this.VN.nodeSize; i++) {
      for (int j = 0; j < Knapsacks.size(); j++) {
        multKnaP.matchMatrix[i][j] = Integer.MAX_VALUE;
        if ((failureType == Parameter.FailureIndependent)
            && (Knapsacks.elementAt(j).starNodeEnhancedVNID < this.nodeSize4Embeded)
            && (Knapsacks.elementAt(j).starNodeEnhancedVNID != Items[i].starNodeEnhancedVNID)) {
          continue;
        }
        if (Items[i].starNodeServiceType == Knapsacks.elementAt(j).starNodeServiceType) {
          if (Items[i].starNodeComputation <= this.nodeComputationCapacity[Knapsacks
              .elementAt(j).starNodeEnhancedVNID]) {
            multKnaP.matchMatrix[i][j] = 0;
            if (!this.isSubNodeCompEmpty[Knapsacks.elementAt(j).starNodeEnhancedVNID]) {
              multKnaP.matchMatrix[i][j] += Parameter.addNewVirNodeNewSubNodeCost;
            }

            if (0 == this.nodeComputationUsed[Knapsacks.elementAt(j).starNodeEnhancedVNID]) {
              multKnaP.matchMatrix[i][j] += Parameter.addNewVirNodeCost;
            }
            if (Items[i].starNodeEnhancedVNID != Knapsacks.elementAt(j).starNodeEnhancedVNID) {
              multKnaP.matchMatrix[i][j] += Parameter.transformExistedNodeCost;
            }
            if (Items[i].starNodeComputation > this.nodeComputationUsed[Knapsacks.elementAt(j).starNodeEnhancedVNID]) {
              multKnaP.matchMatrix[i][j] += (Parameter.addNodeComputaionCost * (Items[i].starNodeComputation
                  - this.nodeComputationUsed[Knapsacks.elementAt(j).starNodeEnhancedVNID]));
            }
            for (int k = 0; k < Items[i].neighborEdge.size(); k++) {
              boolean isExistedEdge = true;
              for (int l = 0; l < Knapsacks.elementAt(j).neighborEdge.size(); l++) {
                if (Items[i].neighborEdge.elementAt(k).neighborVNID == Knapsacks.elementAt(j).neighborEdge
                    .elementAt(l).neighborVNID) {
                  isExistedEdge = false;
                  if (Items[i].neighborEdge
                      .elementAt(k).neighborEdgeBandwithCapacity > Knapsacks.elementAt(j).neighborEdge
                          .elementAt(l).neighborEdgeBandwithCapacity) {
                    multKnaP.matchMatrix[i][j] += (Items[i].neighborEdge.elementAt(k).neighborEdgeBandwithCapacity
                        - Knapsacks.elementAt(j).neighborEdge.elementAt(l).neighborEdgeBandwithCapacity);
                  }
                }
              }
              if (isExistedEdge) {
                multKnaP.matchMatrix[i][j] += Items[i].neighborEdge.elementAt(k).neighborEdgeBandwithCapacity;
              }
            }

          }
        }

      }
    }
    for (int i = 0; i < Knapsacks.size(); i++) {
      multKnaP.ithKapsack2ithUnionKnapsack[i] = Knapsacks.elementAt(i).starNodeEnhancedVNID;
      // System.out.print((mKP.ithKapsack2ithUnionKnapsack[i] + 1) + "\t
      // ");
    }
    for (int i = 0; i < this.nodeSize; i++) {
      multKnaP.unionKnapsackCapacity[i] = this.nodeComputationCapacity[i];
      // System.out.print((i + 1) + ":" + mKP.unionKnapsackCapacity[i] +
      // "\t ");
    }
    // System.out.println();
    for (int i = 0; i < this.VN.nodeSize; i++) {
      multKnaP.capacityItem[i] = this.VN.nodeComputationDemand[i];
      // System.out.print(mKP.capacityItem[i] + "\t ");
    }
    // System.out.println();

    // for (int i = 0; i < this.VN.nodeSize; i++) {
    // for (int j = 0; j < Knapsacks.size(); j++) {
    // System.out.print("i: "+i+"j: "+j+" --"+matchingMatrix[i][j]);
    // if (mKP.matchMatrix[i][j] != Integer.MAX_VALUE) {
    // // System.out.print("00\t ");
    // // else {
    // // final int addNewNodeCost = 10000000;
    // // final int transformExistedNodeCost = 100000;
    // // final int addNodeComputaionCost = 1000;
    // // final int addEdgeBandwithCost = 1;
    // int printint = mKP.matchMatrix[i][j];
    // if ((mKP.matchMatrix[i][j] / EVSNR.addNewNodeCost) > 0)
    // // System.out.print("N+");
    // printint = printint % EVSNR.addNewNodeCost;
    // if ((printint / EVSNR.transformExistedNodeCost) > 0)
    // // System.out.print("M+");
    // printint = printint % EVSNR.transformExistedNodeCost;
    // if ((printint / EVSNR.addNodeComputaionCost) > 0)
    // // System.out.print("(" + (printint /
    // // EVSNR.addNodeComputaionCost) + ")+");
    // printint = printint % EVSNR.addNodeComputaionCost;
    // // System.out.print(printint + "\t ");
    // }
    // }
    // System.out.println("");
    // }
    return false;

  }

  public void computeUsedResource() {
    int nodenumber = 0;
    int nodecomputaiton = 0;
    for (int i = 0; i < this.nodeSize; i++) {
      if (this.nodeComputationUsed[i] > 0) {
        nodecomputaiton += this.nodeComputationUsed[i];
        nodenumber++;
      }
    }

    int edgebandwith = 0;
    for (int i = 0; i < this.nodeSize; i++) {
      for (int j = 0; j < i; j++) {
        edgebandwith += this.edgeBandwithUsed[i][j];
      }
    }
    loggerEnhancedVirtualNetwork.info((nodenumber - this.consumedResource.consumedNodeNumber) + " Node + "
        + (nodecomputaiton - this.consumedResource.consumedNodeComputation) + " Computation + "
        + (edgebandwith - this.consumedResource.consumeEdgeBandwith) + " Bandwith \n");
    this.consumedResource.consumedNodeNumber = nodenumber;
    this.consumedResource.consumedNodeComputation = nodecomputaiton;
    this.consumedResource.consumeEdgeBandwith = edgebandwith;
  }

  public boolean HeursitcAlgorithm4Survivability(boolean failurtype, int sequence) throws GRBException {
    // 1 node+2 node computaion+12 bandwidth
    // 0 node+1 node computaion+5 bandwidth
    // 0 node+2 node computaion+3 bandwidth
    // 1 node+6 node computaion+3 bandwidth
    // =2 node+11 node computaion+23 bandwidth

    // loggerEnhancedVirtualNetwork.info("init resource:" +
    // this.consumedResource.initNodeNumber + " Node + "
    // + (this.consumedResource.initNodeComputation) + " Computation + "
    // + (this.consumedResource.initEdgeBandwith) + " Bandwith \n");
    if (sequence == Parameter.Ran) {
      for (int i = 0; i < this.nodeSize4Embeded; i++) {
        int[] solution = new int[this.nodeSize4Embeded];
        if (failIthNode(i, failurtype, solution) == -1) {
          return false;
        } else {
          augmentNodeEdge(solution, i, matchMethod);
        }
      }
    }
    if (sequence == Parameter.Min) {
      boolean[] isFailed = new boolean[this.nodeSize4Embeded];
      for (int i = 0; i < this.nodeSize4Embeded; i++) {
        int[][] solution = new int[this.nodeSize4Embeded][this.nodeSize4Embeded];
        int[] solutionCost = new int[this.nodeSize4Embeded];
        for (int j = 0; j < this.nodeSize4Embeded; j++) {
          if (!isFailed[j]) {
            solutionCost[j] = failIthNode(j, failurtype, solution[j]);
            if (solutionCost[j] == -1) {
              return false;
            }
          }
        }
        int optimalSolutionNode = -1;
        int optimalSolutionVal = Integer.MAX_VALUE;
        for (int j = 0; j < this.nodeSize4Embeded; j++) {
          if (!isFailed[j]) {
            if (solutionCost[j] < optimalSolutionVal) {
              optimalSolutionNode = j;
              optimalSolutionVal = solutionCost[j];
            }
          }
        }
        augmentNodeEdge(solution[optimalSolutionNode], optimalSolutionNode, matchMethod);
        isFailed[optimalSolutionNode] = true;
      }
    }
    computeUsedResource();
    return true;

  }

  /**
   * 
   */
  private void initComputeResource() {
    // TODO Auto-generated method stub

  }

  /**
   * @return
   * 
   */
  public boolean OptimalAlgorithmIP4Survivability() {
    try {
      GRBEnv env = new GRBEnv();
      GRBModel model = new GRBModel(env);
      model.getEnv().set(GRB.IntParam.OutputFlag, 0);
      // Create variables
      GRBVar TransfromMatrix[][][];
      TransfromMatrix = new GRBVar[this.nodeSize4Embeded + 1][this.VN.nodeSize][this.nodeSize];
      for (int i = 0; i <= this.nodeSize4Embeded; i++) {
        for (int j = 0; j < this.VN.nodeSize; j++) {
          for (int k = 0; k < this.nodeSize; k++) {
            if (0 == i) {
              if (k == this.vNode2eNode[j]) {
                TransfromMatrix[i][j][k] = model.addVar(1.0, 1.0, 0.0, GRB.CONTINUOUS, "T" + i + " r:" + j + " c:" + k);
              } else {
                TransfromMatrix[i][j][k] = model.addVar(0.0, 0.0, 0.0, GRB.CONTINUOUS, "T" + i + " r:" + j + " c:" + k);
              }
            } else {
              // 3
              if ((i - 1) == k) {
                TransfromMatrix[i][j][k] = model.addVar(0.0, 0.0, 0.0, GRB.CONTINUOUS, "T" + i + " r:" + j + " c:" + k);
              } else {
                TransfromMatrix[i][j][k] = model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, "T" + i + " r:" + j + " c:" + k);
              }
            }
          }
        }
      }

      GRBVar EnhancedGraphBandwithMatrix[][];
      EnhancedGraphBandwithMatrix = new GRBVar[this.nodeSize][this.nodeSize];
      for (int j = 0; j < this.nodeSize; j++) {
        for (int k = 0; k < this.nodeSize; k++) {
          EnhancedGraphBandwithMatrix[j][k] = model.addVar(0.0, Integer.MAX_VALUE, 0.0, GRB.CONTINUOUS,
              "MBG" + " r:" + j + " c:" + k);
        }
      }

      GRBVar EnhancedGraphComputationMatrix[];
      EnhancedGraphComputationMatrix = new GRBVar[this.nodeSize];
      for (int j = 0; j < this.nodeSize; j++) {
        EnhancedGraphComputationMatrix[j] = model.addVar(0.0, this.nodeComputationCapacity[j], 0.0, GRB.CONTINUOUS,
            "MBC" + " r:" + j);
      }

      GRBVar usedNodeVector[];
      usedNodeVector = new GRBVar[this.nodeSize];
      for (int j = 0; j < this.nodeSize; j++) {
        if (j < this.nodeSize4Embeded)
          usedNodeVector[j] = model.addVar(1.0, 1.0, 0.0, GRB.CONTINUOUS, "usedNodeVector" + ": " + j);
        else
          usedNodeVector[j] = model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, "usedNodeVector" + ": " + j);
      }

      // Integrate new variables
      model.update();
      // set objecion function
      GRBLinExpr objexpr = new GRBLinExpr();
      for (int i = 0; i < this.nodeSize; i++) {
        objexpr.addTerm(Parameter.addNewVirNodeCost, usedNodeVector[i]);
        objexpr.addTerm(Parameter.addNodeComputaionCost, EnhancedGraphComputationMatrix[i]);
        for (int j = 0; j < this.nodeSize; j++) {
          objexpr.addTerm(1.0, EnhancedGraphBandwithMatrix[i][j]);
        }
      }
      model.setObjective(objexpr, GRB.MINIMIZE);

      // Add constraint 7
      // regulate the TransfromMatrix
      for (int k = 0; k < this.nodeSize; k++) {
        for (int i = 0; i <= this.nodeSize4Embeded; i++) {
          for (int j = 0; j < this.VN.nodeSize; j++) {
            GRBLinExpr conexpr = new GRBLinExpr();
            conexpr.addTerm(1.0, TransfromMatrix[i][j][k]);
            model.addConstr(conexpr, GRB.LESS_EQUAL, usedNodeVector[k], "Con usedNode:" + k + " " + i + " " + j);
          }
        }

      }

      // 2
      for (int i = 0; i <= this.nodeSize4Embeded; i++) {
        for (int j = 0; j < this.VN.nodeSize; j++) {
          GRBLinExpr conexpr = new GRBLinExpr();
          for (int k = 0; k < this.nodeSize; k++) {
            conexpr.addTerm(1.0, TransfromMatrix[i][j][k]);
          }
          model.addConstr(conexpr, GRB.EQUAL, 1.0, "Con Tiv=1: " + i + " " + j);
        }
      }

      for (int i = 0; i <= this.nodeSize4Embeded; i++) {
        for (int k = 0; k < this.nodeSize; k++) {
          GRBLinExpr conexpr = new GRBLinExpr();
          for (int j = 0; j < this.VN.nodeSize; j++) {
            conexpr.addTerm(1.0, TransfromMatrix[i][j][k]);
          }
          model.addConstr(conexpr, GRB.LESS_EQUAL, 1.0, "Con Tuj<=1:" + i + " " + k);
        }
      }

      for (int i = 0; i <= this.nodeSize4Embeded; i++) {
        GRBLinExpr conexpr = new GRBLinExpr();
        for (int j = 0; j < this.VN.nodeSize; j++) {
          for (int k = 0; k < this.nodeSize; k++) {
            conexpr.addTerm(1.0, TransfromMatrix[i][j][k]);
          }
        }
        model.addConstr(conexpr, GRB.EQUAL, this.nodeSize4Embeded, "Con Tuv=n:" + i);
      }

      // 3
      for (int i = 1; i <= this.nodeSize4Embeded; i++) {
        GRBLinExpr conexpr = new GRBLinExpr();
        for (int j = 0; j < this.VN.nodeSize; j++) {
          conexpr.addTerm(1.0, TransfromMatrix[i][j][i - 1]);
        }
        model.addConstr(conexpr, GRB.EQUAL, 0.0, "Con Tuk=0:");
      }

      // 4 MAG<=MBG
      for (int i = 0; i < this.nodeSize4Embeded; i++) {
        for (int j = 0; j < this.nodeSize4Embeded; j++) {
          GRBLinExpr conexpr = new GRBLinExpr();
          conexpr.addTerm(1.0, EnhancedGraphBandwithMatrix[i][j]);
          model.addConstr(conexpr, GRB.GREATER_EQUAL, this.VN.edgeBandwithDemand[i][j], "con MAB<=MBB");
        }
      }

      // 8
      // MAC*T<MBC
      for (int i = 0; i <= this.nodeSize4Embeded; i++) {
        for (int j = 0; j < this.nodeSize; j++) {
          GRBLinExpr conexpr = new GRBLinExpr();
          for (int k = 0; k < this.VN.nodeSize; k++) {
            conexpr.addTerm(this.VN.nodeComputationDemand[k], TransfromMatrix[i][k][j]);
          }
          model.addConstr(conexpr, GRB.LESS_EQUAL, EnhancedGraphComputationMatrix[j], "MAC*T" + "T " + i + "r " + j);
        }
      }

      // 5
      // T*MBS>MAS
      for (int i = 0; i <= this.nodeSize4Embeded; i++) {
        for (int j = 0; j < this.VN.nodeSize; j++) {
          for (int l = 0; l < this.serviceNumber; l++) {
            GRBLinExpr conexpr = new GRBLinExpr();
            for (int k = 0; k < this.nodeSize; k++) {
              if (this.boolServiceTypeSet[k][l])
                conexpr.addTerm(1.0, TransfromMatrix[i][j][k]);
            }
            if (this.VN.nodeServiceType[j] == (l)) {
              model.addConstr(conexpr, GRB.GREATER_EQUAL, 1.0, "T*MBS" + "T " + i + "r " + j + "c: " + l);
            }
            // else {
            // model.addConstr(conexpr, GRB.GREATER_EQUAL, 0.0,
            // "T*ESM" + "T " + i + "r " + j + "c: " + l);
            // }
          }
        }
      }

      // (MAG*T)'*T<MBG
      //
      // for (int i = 0; i <= this.nodeSize4Embeded; i++) {
      // // T'*MAG'
      // GRBQuadExpr TMAG[][] = new GRBQuadExpr[this.nodeSize][this.nodeSize];
      // for (int j = 0; j < this.nodeSize; j++) {
      // for (int t = 0; t < this.nodeSize; t++) {
      // TMAG[j][t] = new GRBQuadExpr();
      // for (int k = 0; k < this.VN.nodeSize; k++) {
      // for (int l = 0; l < this.VN.nodeSize; l++) {
      // // TMAG[j][k].addTerm(this.VNR.edgeBandwithDemand[k][l],
      // // TransfromMatrix[i][l][j]);
      // for (int p = 0; p < this.VN.nodeSize; p++) {
      // Integer inte = this.VN.edgeBandwithDemand[k][p];
      // TMAG[j][t].addTerm(inte.doubleValue(), TransfromMatrix[i][k][j],
      // TransfromMatrix[i][p][t]);
      // }
      // }
      // }
      // model.addQConstr(TMAG[j][t], GRB.LESS_EQUAL,
      // EnhancedGraphBandwithMatrix[j][t],
      // "T " + i + "T'*MAG'*T" + "r " + j + " c " + t);
      // }
      // }
      // }
      model.optimize();
      int optimstatus = model.get(GRB.IntAttr.Status);
      if (optimstatus != GRB.OPTIMAL) {
        return false;
      }
      for (int i = 0; i < this.nodeSize; i++) {
        if (i >= this.nodeSize4Embeded) {
          this.nodeComputationUsed[i] = (int) EnhancedGraphComputationMatrix[i].get(GRB.DoubleAttr.X);
        } else {
          this.nodeComputationUsed[i] = (int) EnhancedGraphComputationMatrix[i].get(GRB.DoubleAttr.X)
              - this.VN.nodeComputationDemand[i];
        }
        for (int j = 0; j < this.nodeSize; j++) {
          if (i < this.nodeSize4Embeded && j < this.nodeSize4Embeded) {
            this.edgeBandwithUsed[i][j] = (int) (EnhancedGraphBandwithMatrix[i][j].get(GRB.DoubleAttr.X)
                - this.VN.edgeBandwithDemand[i][j]);
          } else {
            this.edgeBandwithUsed[i][j] = (int) EnhancedGraphBandwithMatrix[i][j].get(GRB.DoubleAttr.X);
          }
        }
      }
      loggerEnhancedVirtualNetwork.info("ILP method Succed");
      computeUsedResource();
    } catch (GRBException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return true;

  }

  /**
   * @param sequence
   * @param isFD
   * @param isExact
   * @return
   * 
   */
  public boolean startEnhanceVirtualNetwork(boolean isExact, boolean isFD, int sequence) {
    boolean enhanceEmbedAlgorithmResult = false;
    if (isExact) {
      enhanceEmbedAlgorithmResult = OptimalAlgorithmIP4Survivability();
    } else {
      this.obtainItems();
      try {
        enhanceEmbedAlgorithmResult = this.HeursitcAlgorithm4Survivability(isFD, sequence);
      } catch (GRBException e) {
        e.printStackTrace();
      }
    }
    return enhanceEmbedAlgorithmResult;
  }

  /**
   * 
   */
  public void destructerResource() {
    for (int i = 0; i < this.nodeSize; i++) {
      for (int j = 0; j < this.nodeSize; j++) {
        eEdge2sPath.get(i).get(j).clear();
      }
      eEdge2sPath.get(i).clear();
    }
    eEdge2sPath.clear();
    label2Node.clear();
    for (int i = 0; i < Knapsacks.size(); i++) {
      Knapsacks.get(i).neighborEdge.clear();
    }
    Knapsacks.clear();
  }

}
