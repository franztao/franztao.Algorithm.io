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
public class SurvivalVirtualNetwork {

  private Logger loggerEnhancedVirtualNetwork = Logger.getLogger(SurvivalVirtualNetwork.class);

  public final int matchMethod = Parameter.MatchMethod;

  // nodeSize=enhacnedNodeSize+backupNodeSize
  public int nodeSize;
  public int nodeSize4Failure;
  public int nodeSize4Backup;
  // nodeComputationCapacity-usedNodeCurrentComputationCapacity
  public int[] nodeComputationCapacity;
  public int[] nodeComputationConsume;
  public int[] nodeComputation4Backup;
  public boolean[] isSubNodeComEmpty;

  public boolean[][] topology;
  // edgeBandwithCapacity-usedEdgeCurrentBandwithCapacity
  public int[][] edgeBandwith4Comsume;
  public int[][] edgeBandwith4Backup;
  public Vector<Vector<Vector<Integer>>> eEdge2sPath;

  public int serviceNum;
  public boolean[][] boolServiceTypeSet;

  public String[] node2Label;
  public Map<String, Integer> label2Node;

  public int[] virNode2surNode;
  public int[] surNode2subNode;

  public VirtualNetwork virNet;

  // public boolean sampleInit;

  class StarEdgeStructure {
    int neighborNode4VirNetInd;
    int neighborNode4SurVirNetInd;
    int neighborNodeType;
    int neighborEdgeBandwithCapacity;
    // public int neighborUsedEdgeBandwithCapacity;

  }

  /**
   * starStructure.
   * 
   * @author Taoheng
   *
   */
  class StarStructure {
    int starNodeServiceType;
    int starNodeComputation;
    int starNodeSurVirNetInd;
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

  StarStructure[] items;
  Vector<StarStructure> knapsacks;

  public boolean isSucceedEmbed;

  /**
   * SurvivalVirtualNetwork.
   * 
   * @param vn
   *          vn
   * @param bn
   *          bn
   */
  public SurvivalVirtualNetwork(SubstrateNetwork sn, VirtualNetwork vn, BackupNode bn) {
    PropertyConfigurator.configure("log4j.properties");
    this.virNet = vn;
    this.isSucceedEmbed = false;
    // node
    this.nodeSize4Failure = vn.nodeSize;
    this.nodeSize4Backup = bn.backupNodeSize;
    this.nodeSize = this.nodeSize4Failure + this.nodeSize4Backup;
    this.nodeComputationCapacity = new int[this.nodeSize];
    this.nodeComputationConsume = new int[this.nodeSize];
    this.nodeComputation4Backup = new int[this.nodeSize];
    this.isSubNodeComEmpty = new boolean[this.nodeSize];
    // edge
    this.topology = new boolean[this.nodeSize][this.nodeSize];
    this.edgeBandwith4Comsume = new int[this.nodeSize][this.nodeSize];
    this.edgeBandwith4Backup = new int[this.nodeSize][this.nodeSize];
    eEdge2sPath = new Vector<Vector<Vector<Integer>>>();
    for (int i = 0; i < this.nodeSize; i++) {
      eEdge2sPath.addElement(new Vector<Vector<Integer>>());
      for (int j = 0; j < this.nodeSize; j++) {
        eEdge2sPath.get(i).addElement(new Vector<Integer>());
      }
    }
    // service
    this.serviceNum = vn.serviceNum;
    this.boolServiceTypeSet = new boolean[nodeSize][serviceNum];
    // label
    this.node2Label = new String[nodeSize];
    this.label2Node = new HashMap<String, Integer>();
    // embed function
    this.virNode2surNode = new int[this.virNet.nodeSize];
    this.surNode2subNode = new int[this.nodeSize];

    // knapsack problem
    this.items = new StarStructure[this.virNet.nodeSize];
    this.consumedResource = new ConsumeResource();

    constrcutSurVirNet(sn, vn, bn);
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
   * constrcutSurVirNet.
   * 
   * @param vn
   *          vn
   * @param bn
   *          bn
   */
  private void constrcutSurVirNet(SubstrateNetwork sn, VirtualNetwork vn, BackupNode bn) {
    // node
    for (int i = 0; i < this.nodeSize; i++) {
      if (i < this.nodeSize4Failure) {
        this.nodeComputationCapacity[i] = vn.nodeComputationCapacity[i];
        this.nodeComputationConsume[i] = vn.nodeComputationDemand[i];
        this.surNode2subNode[i] = vn.vNode2sNode[i];

      } else {
        this.nodeComputationCapacity[i] = bn.nodeComputationCapacity[i - this.nodeSize4Failure];
        this.nodeComputationConsume[i] = 0;
        this.surNode2subNode[i] = bn.bNode2sNode[i - this.nodeSize4Failure];
        if (bn.isHaveSubstrateNodeResource4buNode[i - this.nodeSize4Failure]) {
          this.isSubNodeComEmpty[i] = true;
        }
      }
    }
    for (int i = 0; i < this.nodeSize4Failure; i++) {
      this.virNode2surNode[i] = i;
    }
    // edge
    for (int i = 0; i < this.nodeSize; i++) {
      for (int j = 0; j < i; j++) {
        if ((i < this.nodeSize4Failure)
            && (vn.edgeBandwithDemand[this.virNode2surNode[i]][this.virNode2surNode[j]] > 0)) {
          this.topology[this.virNode2surNode[i]][this.virNode2surNode[j]] = this.topology[this.virNode2surNode[j]][this.virNode2surNode[i]] = true;
          this.edgeBandwith4Comsume[this.virNode2surNode[i]][this.virNode2surNode[j]] = this.edgeBandwith4Comsume[this.virNode2surNode[j]][this.virNode2surNode[i]] = vn.edgeBandwithDemand[this.virNode2surNode[i]][this.virNode2surNode[j]];
        }
      }
    }

    // service
    for (int i = 0; i < this.nodeSize; i++) {
      for (int j = 0; j < this.serviceNum; j++) {
        if (i < this.nodeSize4Failure) {
          this.boolServiceTypeSet[i][j] = sn.boolServiceTypeSet[vn.vNode2sNode[i]][j];
        } else {
          this.boolServiceTypeSet[i][j] = sn.boolServiceTypeSet[bn.bNode2sNode[i
              - this.nodeSize4Failure]][j];
        }
      }
    }

    // label
    String estr = "EN_";
    String bstr = "BN_";
    for (int i = 0; i < this.nodeSize; i++) {
      if (i < this.nodeSize4Failure) {
        this.node2Label[i] = estr + i;
        this.label2Node.put(estr + i, i);
      } else {
        this.node2Label[i] = bstr + (i - this.nodeSize4Failure);
        this.label2Node.put(bstr + (i - this.nodeSize4Failure), i);
      }
    }

    for (int i = 0; i < this.nodeSize; i++) {
      if (this.nodeComputationConsume[i] > 0) {
        consumedResource.initNodeNumber++;
        consumedResource.consumedNodeNumber++;

        consumedResource.initNodeComputation += this.nodeComputationConsume[i];
        consumedResource.consumedNodeComputation += this.nodeComputationConsume[i];
      }
    }

    for (int i = 0; i < this.nodeSize; i++) {
      for (int j = 0; j < this.nodeSize; j++) {
        consumedResource.consumeEdgeBandwith += this.edgeBandwith4Comsume[i][j];
        consumedResource.initEdgeBandwith += this.edgeBandwith4Comsume[i][j];
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

    nodeComputationConsume[0] = 2;
    nodeComputationConsume[1] = 3;
    nodeComputationConsume[2] = 5;
    nodeComputationConsume[3] = 6;

    edgeBandwith4Comsume[0][1] = 4;
    edgeBandwith4Comsume[0][2] = 5;
    edgeBandwith4Comsume[0][3] = 3;
    edgeBandwith4Comsume[1][2] = 6;
    edgeBandwith4Comsume[1][0] = 4;
    edgeBandwith4Comsume[2][0] = 5;
    edgeBandwith4Comsume[3][0] = 3;
    edgeBandwith4Comsume[2][1] = 6;

    for (int i = 0; i < nodeSize; i++) {
      for (int j = 0; j < nodeSize; j++) {
        if (0 != edgeBandwith4Comsume[i][j]) {
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
    virNode2surNode[0] = 0;
    virNode2surNode[1] = 1;
    virNode2surNode[2] = 2;
    virNode2surNode[3] = 3;

    nodeSize4Backup = 3;
    nodeSize4Failure = 4;

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

    edgeBandwith4Comsume[0][1] = 3;
    edgeBandwith4Comsume[1][0] = 3;

    for (int i = 0; i < nodeSize; i++) {
      for (int j = 0; j < nodeSize; j++) {
        if (0 != edgeBandwith4Comsume[i][j]) {
          topology[i][j] = true;
        }
      }
    }

    nodeComputationConsume[0] = 3;
    nodeComputationConsume[1] = 4;

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
    virNode2surNode[0] = 0;
    virNode2surNode[1] = 1;

    nodeSize4Backup = 2;
    nodeSize4Failure = 2;

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

    nodeComputationConsume[0] = 3;

    boolServiceTypeSet[0][0] = true;
    boolServiceTypeSet[1][0] = true;
    boolServiceTypeSet[1][1] = true;

    node2Label[0] = "E1";
    node2Label[1] = "E2";

    label2Node.put("E1", 0);
    label2Node.put("B1", 1);

    // there may be some virtual node located into the same enhanced node
    virNode2surNode[0] = 0;

    nodeSize4Backup = 1;
    nodeSize4Failure = 1;

    consumedResource.initEdgeBandwith = 0;
    consumedResource.initNodeComputation = 3;
    consumedResource.initNodeNumber = 1;

    consumedResource.consumeEdgeBandwith = 0;
    consumedResource.consumedNodeComputation = 3;
    consumedResource.consumedNodeNumber = 1;
  }

  public void obtainItems() {
    for (int i = 0; i < this.virNet.nodeSize; i++) {
      items[i] = new StarStructure();
      // Items[i].starNodeVNID = i;
      items[i].starNodeSurVirNetInd = this.virNode2surNode[i];
      items[i].starNodeComputation = this.virNet.nodeComputationDemand[i];
      items[i].starNodeServiceType = this.virNet.nodeServiceType[i];
      items[i].neighborEdge = new Vector<StarEdgeStructure>();
      for (int j = 0; j < this.virNet.nodeSize; j++) {
        if (this.virNet.topology[i][j]) {
          StarEdgeStructure edge = new StarEdgeStructure();
          edge.neighborNode4VirNetInd = j;
          edge.neighborNode4SurVirNetInd = this.virNode2surNode[j];
          edge.neighborEdgeBandwithCapacity = this.virNet.edgeBandwithDemand[i][j];
          edge.neighborNodeType = this.virNet.nodeServiceType[j];
          items[i].neighborEdge.addElement(edge);
        }
      }
    }
  }

  /**
   * constructKnapsacks.
   * 
   * @param failSurNode
   *          failureNodeID
   */
  void constructKnapsacks(int failSurNode) {
    knapsacks = new Vector<StarStructure>();
    for (int i = 0; i < this.nodeSize; i++) {
      if (i != failSurNode) {
        if ((i < this.nodeSize4Failure)
            && (this.surNode2subNode[this.virNode2surNode[i]] 
                != this.surNode2subNode[this.virNode2surNode[failSurNode]])) {
          continue;
        }
        for (int j = 0; j < this.serviceNum; j++) {
          if (this.boolServiceTypeSet[i][j]) {
            
            StarStructure star = new StarStructure();
            star.starNodeServiceType = j;
            star.starNodeSurVirNetInd = i;
            star.starNodeComputation = this.nodeComputationConsume[i];
            // ineffective value could be erased
            star.neighborEdge = new Vector<StarEdgeStructure>();
            
            for (int k = 0; k < this.virNet.nodeSize; k++) {
              int surNode = this.virNode2surNode[k];
              
              if (failSurNode != surNode) {
                StarEdgeStructure edge = new StarEdgeStructure();
                edge.neighborNode4VirNetInd = k;
                edge.neighborNode4SurVirNetInd = surNode;
                if (i == this.virNode2surNode[k]) {
                  edge.neighborEdgeBandwithCapacity = Integer.MAX_VALUE;
                } else {
                  edge.neighborEdgeBandwithCapacity = this.edgeBandwith4Comsume[i][surNode];
                }
                edge.neighborNodeType = this.virNet.nodeServiceType[k];
                star.neighborEdge.addElement(edge);
              }
              
            }
            knapsacks.addElement(star);
          }
        }
      }
    }
  }

  /**
   * failIthNode.
   * 
   * @param failSurNode
   *          failureNodeID
   * @param isFailDep
   *          failurtype
   * @param solution
   *          solution
   * @return int
   * @throws GRBException
   *           GRBException
   */
  public int failIthNode(int failSurNode, boolean isFailDep, int[] solution) throws GRBException {
    constructKnapsacks(failSurNode);
    MulitpleKnapsack mkp = new MulitpleKnapsack(this.virNet.nodeSize, knapsacks.size(),
        this.nodeSize);
    constructMultipleKnapsackProbem(mkp, isFailDep);
    int optimalResult = -1;
    if (this.matchMethod == Parameter.MatchMethodDP) {
      optimalResult = mkp.optimalSoutionDP(solution);
      if (optimalResult == -1) {
        loggerEnhancedVirtualNetwork
            .warn("Failure node: " + failSurNode + ", there is not solution");
        return -1;
      }
    }

    if (this.matchMethod == Parameter.MatchMethodILP) {
      optimalResult = mkp.optimalSoutionILP(solution);
      if (optimalResult == -1) {
        loggerEnhancedVirtualNetwork
            .warn("Failure node: " + failSurNode + ", there is not solution");
        return -1;
      }
    }

    return optimalResult;

  }

  /**
   * @param solution
   */
  private void augmentNodeEdge(int[] solution, int failurenodeID, int Method) {

    int virutialNode2NewEnhancedVirtualNode[] = new int[this.virNet.nodeSize];
    if (Method == Parameter.MatchMethodILP) {
      for (int i = 0; i < this.virNet.nodeSize; i++) {
        virutialNode2NewEnhancedVirtualNode[i] = knapsacks
            .elementAt(solution[i]).starNodeSurVirNetInd;
      }
    }
    if (Method == Parameter.MatchMethodDP) {
      for (int i = 0; i < this.virNet.nodeSize; i++) {
        virutialNode2NewEnhancedVirtualNode[i] = solution[i];
        // System.out.println(virutialNode2NewVirtualNode[i] + 1);
      }
    }

    for (int i = 0; i < this.virNet.nodeSize; i++) {
      // add node computation
      if (this.nodeComputationConsume[virutialNode2NewEnhancedVirtualNode[i]] < items[i].starNodeComputation)
        this.nodeComputationConsume[virutialNode2NewEnhancedVirtualNode[i]] = items[i].starNodeComputation;
      for (int j = 0; j < items[i].neighborEdge.size(); j++) {
        int neighborVNNode = items[i].neighborEdge.elementAt(j).neighborNode4VirNetInd;
        // if (failurenodeID !=
        // virutialNode2NewVirtualNode[neighborVNNode]) {
        if (virutialNode2NewEnhancedVirtualNode[i] != virutialNode2NewEnhancedVirtualNode[neighborVNNode])
          if (this.edgeBandwith4Comsume[virutialNode2NewEnhancedVirtualNode[i]][virutialNode2NewEnhancedVirtualNode[neighborVNNode]] < items[i].neighborEdge
              .elementAt(j).neighborEdgeBandwithCapacity) {
            this.edgeBandwith4Comsume[virutialNode2NewEnhancedVirtualNode[i]][virutialNode2NewEnhancedVirtualNode[neighborVNNode]] = items[i].neighborEdge
                .elementAt(j).neighborEdgeBandwithCapacity;
            this.edgeBandwith4Comsume[virutialNode2NewEnhancedVirtualNode[neighborVNNode]][virutialNode2NewEnhancedVirtualNode[i]] = items[i].neighborEdge
                .elementAt(j).neighborEdgeBandwithCapacity;
            this.topology[virutialNode2NewEnhancedVirtualNode[i]][virutialNode2NewEnhancedVirtualNode[neighborVNNode]] = this.topology[virutialNode2NewEnhancedVirtualNode[neighborVNNode]][virutialNode2NewEnhancedVirtualNode[i]] = true;
            // }
          }
      }
    }
  }

  boolean constructMultipleKnapsackProbem(MulitpleKnapsack multKnaP, boolean isFailDep) {
    for (int i = 0; i < this.virNet.nodeSize; i++) {
      for (int j = 0; j < knapsacks.size(); j++) {
        multKnaP.matchMatrix[i][j] = Integer.MAX_VALUE;
        if ((isFailDep == Parameter.FailureIndependent)
            && (knapsacks.elementAt(j).starNodeSurVirNetInd < this.nodeSize4Failure)
            && (knapsacks.elementAt(j).starNodeSurVirNetInd != items[i].starNodeSurVirNetInd)) {
          continue;
        }
        if (items[i].starNodeServiceType == knapsacks.elementAt(j).starNodeServiceType) {
          if (items[i].starNodeComputation <= this.nodeComputationCapacity[knapsacks
              .elementAt(j).starNodeSurVirNetInd]) {
            multKnaP.matchMatrix[i][j] = 0;
            if (this.isSubNodeComEmpty[knapsacks.elementAt(j).starNodeSurVirNetInd]) {
              multKnaP.matchMatrix[i][j] += Parameter.addNewVirNodeNewSubNodeCost;
            }

            if (0 == this.nodeComputationConsume[knapsacks.elementAt(j).starNodeSurVirNetInd]) {
              multKnaP.matchMatrix[i][j] += Parameter.addNewVirNodeCost;
            }
            if (items[i].starNodeSurVirNetInd != knapsacks.elementAt(j).starNodeSurVirNetInd) {
              multKnaP.matchMatrix[i][j] += Parameter.transformExistedNodeCost;
            }
            if (items[i].starNodeComputation > this.nodeComputationConsume[knapsacks
                .elementAt(j).starNodeSurVirNetInd]) {
              multKnaP.matchMatrix[i][j] += (Parameter.addNodeComputaionCost
                  * (items[i].starNodeComputation
                      - this.nodeComputationConsume[knapsacks.elementAt(j).starNodeSurVirNetInd]));
            }
            for (int k = 0; k < items[i].neighborEdge.size(); k++) {
              boolean isExistedEdge = true;
              for (int l = 0; l < knapsacks.elementAt(j).neighborEdge.size(); l++) {
                if (items[i].neighborEdge
                    .elementAt(k).neighborNode4VirNetInd == knapsacks.elementAt(j).neighborEdge
                        .elementAt(l).neighborNode4VirNetInd) {
                  isExistedEdge = false;
                  if (items[i].neighborEdge.elementAt(
                      k).neighborEdgeBandwithCapacity > knapsacks.elementAt(j).neighborEdge
                          .elementAt(l).neighborEdgeBandwithCapacity) {
                    multKnaP.matchMatrix[i][j] += (items[i].neighborEdge
                        .elementAt(k).neighborEdgeBandwithCapacity
                        - knapsacks.elementAt(j).neighborEdge
                            .elementAt(l).neighborEdgeBandwithCapacity);
                  }
                }
              }
              if (isExistedEdge) {
                multKnaP.matchMatrix[i][j] += items[i].neighborEdge
                    .elementAt(k).neighborEdgeBandwithCapacity;
              }
            }

          }
        }

      }
    }
    for (int i = 0; i < knapsacks.size(); i++) {
      multKnaP.ithKapsack2ithUnionKnapsack[i] = knapsacks.elementAt(i).starNodeSurVirNetInd;
      // System.out.print((mKP.ithKapsack2ithUnionKnapsack[i] + 1) + "\t
      // ");
    }
    for (int i = 0; i < this.nodeSize; i++) {
      multKnaP.unionKnapsackCapacity[i] = this.nodeComputationCapacity[i];
      // System.out.print((i + 1) + ":" + mKP.unionKnapsackCapacity[i] +
      // "\t ");
    }
    // System.out.println();
    for (int i = 0; i < this.virNet.nodeSize; i++) {
      multKnaP.capacityItem[i] = this.virNet.nodeComputationDemand[i];
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

  /**
   * computeUsedResource.
   */
  public void computeConsumedResource() {
    int nodenumber = 0;
    int nodecomputaiton = 0;
    for (int i = 0; i < this.nodeSize; i++) {
      if (this.nodeComputationConsume[i] > 0) {
        nodecomputaiton += this.nodeComputationConsume[i];
        nodenumber++;
      }
    }

    int edgebandwith = 0;
    for (int i = 0; i < this.nodeSize; i++) {
      for (int j = 0; j < i; j++) {
        edgebandwith += this.edgeBandwith4Comsume[i][j];
      }
    }
    loggerEnhancedVirtualNetwork
        .info((nodenumber - this.consumedResource.consumedNodeNumber) + " Node + "
            + (nodecomputaiton - this.consumedResource.consumedNodeComputation) + " Computation + "
            + (edgebandwith - this.consumedResource.consumeEdgeBandwith) + " Bandwith ");
    this.consumedResource.consumedNodeNumber = nodenumber;
    this.consumedResource.consumedNodeComputation = nodecomputaiton;
    this.consumedResource.consumeEdgeBandwith = edgebandwith;
  }

  /**
   * heursitcAlgorithm4Survivability.
   */
  public boolean heursitcAlgorithm4Survivability(boolean isFailDep, int sequence)
      throws GRBException {
    // 1 node+2 node computaion+12 bandwidth
    // 0 node+1 node computaion+5 bandwidth
    // 0 node+2 node computaion+3 bandwidth
    // 1 node+6 node computaion+3 bandwidth
    // =2 node+11 node computaion+23 bandwidth

    if (sequence == Parameter.Ran) {
      for (int i = 0; i < this.nodeSize4Failure; i++) {
        int[] solution = new int[this.nodeSize4Failure];
        if (failIthNode(i, isFailDep, solution) == -1) {
          return false;
        } else {
          augmentNodeEdge(solution, i, matchMethod);
        }
      }
    }
    if (sequence == Parameter.Min) {
      boolean[] isFailed = new boolean[this.nodeSize4Failure];
      for (int i = 0; i < this.nodeSize4Failure; i++) {
        int[][] solution = new int[this.nodeSize4Failure][this.nodeSize4Failure];
        int[] solutionCost = new int[this.nodeSize4Failure];
        for (int j = 0; j < this.nodeSize4Failure; j++) {
          if (!isFailed[j]) {
            solutionCost[j] = failIthNode(j, isFailDep, solution[j]);
            if (solutionCost[j] == -1) {
              return false;
            }
          }
        }
        int optimalSolutionNode = -1;
        int optimalSolutionVal = Integer.MAX_VALUE;
        for (int j = 0; j < this.nodeSize4Failure; j++) {
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
    computeConsumedResource();
    return true;

  }

  /**
   * exactAlgorithmIP4SeVirNet.
   * 
   * @return
   * 
   */
  public boolean exactAlgorithmIP4SeVirNet() {
    try {
      GRBEnv env = new GRBEnv();
      GRBModel model = new GRBModel(env);
      model.getEnv().set(GRB.IntParam.OutputFlag, 0);
      // Create variables
      GRBVar[][][] transformMatrix;
      transformMatrix = new GRBVar[this.nodeSize4Failure + 1][this.virNet.nodeSize][this.nodeSize];
      for (int i = 0; i <= this.nodeSize4Failure; i++) {
        for (int j = 0; j < this.virNet.nodeSize; j++) {
          for (int k = 0; k < this.nodeSize; k++) {
            if (0 == i) {
              if (k == this.virNode2surNode[j]) {
                transformMatrix[i][j][k] = model.addVar(1.0, 1.0, 0.0, GRB.CONTINUOUS,
                    "T" + i + " r:" + j + " c:" + k);
              } else {
                transformMatrix[i][j][k] = model.addVar(0.0, 0.0, 0.0, GRB.CONTINUOUS,
                    "T" + i + " r:" + j + " c:" + k);
              }
            } else {
              // 3
              if ((i - 1) == k) {
                transformMatrix[i][j][k] = model.addVar(0.0, 0.0, 0.0, GRB.CONTINUOUS,
                    "T" + i + " r:" + j + " c:" + k);
              } else {
                transformMatrix[i][j][k] = model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS,
                    "T" + i + " r:" + j + " c:" + k);
              }
            }
          }
        }
      }

      // 1
      GRBVar[][] survivalGraphBandwithMatrix;
      survivalGraphBandwithMatrix = new GRBVar[this.nodeSize][this.nodeSize];
      for (int j = 0; j < this.nodeSize; j++) {
        for (int k = 0; k < this.nodeSize; k++) {
          survivalGraphBandwithMatrix[j][k] = model.addVar(0.0, Integer.MAX_VALUE, 0.0,
              GRB.CONTINUOUS, "MBG" + " r:" + j + " c:" + k);
        }
      }

      GRBVar[] survivalGraphComputationMatrix;
      survivalGraphComputationMatrix = new GRBVar[this.nodeSize];
      for (int j = 0; j < this.nodeSize; j++) {
        survivalGraphComputationMatrix[j] = model.addVar(0.0, this.nodeComputationCapacity[j], 0.0,
            GRB.CONTINUOUS, "MBC" + " r:" + j);
      }

      GRBVar[] usedNodeVector;
      usedNodeVector = new GRBVar[this.nodeSize];
      for (int j = 0; j < this.nodeSize; j++) {
        if (j < this.nodeSize4Failure) {
          usedNodeVector[j] = model.addVar(1.0, 1.0, 0.0, GRB.CONTINUOUS,
              "usedNodeVector" + ": " + j);
        } else {
          usedNodeVector[j] = model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS,
              "usedNodeVector" + ": " + j);
        }
      }

      // Integrate new variables
      model.update();
      // set objecion function
      GRBLinExpr objexpr = new GRBLinExpr();
      for (int i = 0; i < this.nodeSize; i++) {
        objexpr.addTerm(Parameter.addNewVirNodeCost, usedNodeVector[i]);
        objexpr.addTerm(Parameter.addNodeComputaionCost, survivalGraphComputationMatrix[i]);
        for (int j = 0; j < this.nodeSize; j++) {
          objexpr.addTerm(1.0, survivalGraphBandwithMatrix[i][j]);
        }
      }
      model.setObjective(objexpr, GRB.MINIMIZE);

      // Add constraint 7
      // regulate the TransfromMatrix
      for (int k = 0; k < this.nodeSize; k++) {
        for (int i = 0; i <= this.nodeSize4Failure; i++) {
          for (int j = 0; j < this.virNet.nodeSize; j++) {
            GRBLinExpr conexpr = new GRBLinExpr();
            conexpr.addTerm(1.0, transformMatrix[i][j][k]);
            model.addConstr(conexpr, GRB.LESS_EQUAL, usedNodeVector[k],
                "Con usedNode:" + k + " " + i + " " + j);
          }
        }

      }

      // 2
      for (int i = 0; i <= this.nodeSize4Failure; i++) {
        for (int j = 0; j < this.virNet.nodeSize; j++) {
          GRBLinExpr conexpr = new GRBLinExpr();
          for (int k = 0; k < this.nodeSize; k++) {
            conexpr.addTerm(1.0, transformMatrix[i][j][k]);
          }
          model.addConstr(conexpr, GRB.EQUAL, 1.0, "Con Tiv=1: " + i + " " + j);
        }
      }

      for (int i = 0; i <= this.nodeSize4Failure; i++) {
        for (int k = 0; k < this.nodeSize; k++) {
          GRBLinExpr conexpr = new GRBLinExpr();
          for (int j = 0; j < this.virNet.nodeSize; j++) {
            conexpr.addTerm(1.0, transformMatrix[i][j][k]);
          }
          model.addConstr(conexpr, GRB.LESS_EQUAL, 1.0, "Con Tuj<=1:" + i + " " + k);
        }
      }

      for (int i = 0; i <= this.nodeSize4Failure; i++) {
        GRBLinExpr conexpr = new GRBLinExpr();
        for (int j = 0; j < this.virNet.nodeSize; j++) {
          for (int k = 0; k < this.nodeSize; k++) {
            conexpr.addTerm(1.0, transformMatrix[i][j][k]);
          }
        }
        model.addConstr(conexpr, GRB.EQUAL, this.nodeSize4Failure, "Con Tuv=n:" + i);
      }

      // 3
      for (int i = 1; i <= this.nodeSize4Failure; i++) {
        GRBLinExpr conexpr = new GRBLinExpr();
        for (int j = 0; j < this.virNet.nodeSize; j++) {
          conexpr.addTerm(1.0, transformMatrix[i][j][i - 1]);
        }
        model.addConstr(conexpr, GRB.EQUAL, 0.0, "Con Tuk=0:");
      }

      // 4 MAG<=MBG
      for (int i = 0; i < this.nodeSize4Failure; i++) {
        for (int j = 0; j < this.nodeSize4Failure; j++) {
          GRBLinExpr conexpr = new GRBLinExpr();
          conexpr.addTerm(1.0, survivalGraphBandwithMatrix[i][j]);
          model.addConstr(conexpr, GRB.GREATER_EQUAL, this.virNet.edgeBandwithDemand[i][j],
              "con MAB<=MBB");
        }
      }

      // 8
      // MAC*T<MBC
      for (int i = 0; i <= this.nodeSize4Failure; i++) {
        for (int j = 0; j < this.nodeSize; j++) {
          GRBLinExpr conexpr = new GRBLinExpr();
          for (int k = 0; k < this.virNet.nodeSize; k++) {
            conexpr.addTerm(this.virNet.nodeComputationDemand[k], transformMatrix[i][k][j]);
          }
          model.addConstr(conexpr, GRB.LESS_EQUAL, survivalGraphComputationMatrix[j],
              "MAC*T" + "T " + i + "r " + j);
        }
      }

      // 5
      // T*MBS>MAS
      for (int i = 0; i <= this.nodeSize4Failure; i++) {
        for (int j = 0; j < this.virNet.nodeSize; j++) {
          for (int l = 0; l < this.serviceNum; l++) {
            GRBLinExpr conexpr = new GRBLinExpr();
            for (int k = 0; k < this.nodeSize; k++) {
              if (this.boolServiceTypeSet[k][l])
                conexpr.addTerm(1.0, transformMatrix[i][j][k]);
            }
            if (this.virNet.nodeServiceType[j] == (l)) {
              model.addConstr(conexpr, GRB.GREATER_EQUAL, 1.0,
                  "T*MBS" + "T " + i + "r " + j + "c: " + l);
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
        if (i >= this.nodeSize4Failure) {
          this.nodeComputationConsume[i] = (int) survivalGraphComputationMatrix[i]
              .get(GRB.DoubleAttr.X);
        } else {
          this.nodeComputationConsume[i] = (int) survivalGraphComputationMatrix[i]
              .get(GRB.DoubleAttr.X) - this.virNet.nodeComputationDemand[i];
        }
        for (int j = 0; j < this.nodeSize; j++) {
          if (i < this.nodeSize4Failure && j < this.nodeSize4Failure) {
            this.edgeBandwith4Comsume[i][j] = (int) (survivalGraphBandwithMatrix[i][j]
                .get(GRB.DoubleAttr.X) - this.virNet.edgeBandwithDemand[i][j]);
          } else {
            this.edgeBandwith4Comsume[i][j] = (int) survivalGraphBandwithMatrix[i][j]
                .get(GRB.DoubleAttr.X);
          }
        }
      }
      loggerEnhancedVirtualNetwork.info("ILP method Succed");
      computeConsumedResource();
    } catch (GRBException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return true;

  }

  /**
   * startSurvivalVirtualNetwork.
   * 
   * @param sequence
   *          sequence
   * @param isFailDep
   *          isFD
   * @param isExact
   *          isExact
   * @return
   * 
   */
  public boolean startSurvivalVirtualNetwork(boolean isExact, boolean isFailDep, int sequence) {
    boolean issvnAlgorithmSuccess = false;
    if (isExact) {
      issvnAlgorithmSuccess = exactAlgorithmIP4SeVirNet();
    } else {
      this.obtainItems();
      try {
        issvnAlgorithmSuccess = this.heursitcAlgorithm4Survivability(isFailDep, sequence);
      } catch (GRBException e) {
        e.printStackTrace();
      }
    }
    return issvnAlgorithmSuccess;
  }

  /**
   * destructerResource.
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
    for (int i = 0; i < knapsacks.size(); i++) {
      knapsacks.get(i).neighborEdge.clear();
    }
    knapsacks.clear();
  }

}
