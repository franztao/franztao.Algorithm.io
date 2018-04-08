/**
 * 
 */

package virtualnetwork;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBQuadExpr;
import gurobi.GRBVar;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import multipleknapsack.MulitpleKnapsack;
import sevn.Algorithm;
import sevn.Parameter;

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
public class SurvivalVirtualNetwork
{

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
    public Vector<Vector<Vector<Integer>>> surEdge2SubPath;

    public int serviceNum;
    public boolean[][] boolServiceTypeSet;

    public String[] node2Label;
    public Map<String, Integer> label2Node;

    public int[] virNode2surNode;
    public int[] surNode2virNode;
    public int[] surNode2subNode;

    public VirtualNetwork virNet;

    // public boolean sampleInit;

    class StarEdgeStructure
    {
        int neighborNode4VirNetInd;
        int neighborNode4SurVirNetInd;
        int neighborNodeType;
        int neighborEdgeBandwith;
        // public int neighborUsedEdgeBandwithCapacity;

    }

    /**
     * starStructure.
     * 
     * @author Taoheng
     *
     */
    class StarStructure
    {
        int starNodeServiceType;
        int starNodeComputation;
        int starNodeSurVirNetInd;

        boolean[] nodeServiceTypeSet;
        Vector<StarEdgeStructure> neighborEdge;
        // Vector<Integer> neighborEnhancedVNID;
        // Vector<Integer> neighborEnhancedVNType;
        // Vector<Integer> neighborEdgeBandwith;
    }

    class ConsumeResource
    {
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
     *            vn
     * @param bn
     *            bn
     */
    public SurvivalVirtualNetwork(SubstrateNetwork sn, VirtualNetwork vn, BackupNode bn)
    {
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
        surEdge2SubPath = new Vector<Vector<Vector<Integer>>>();

        for (int i = 0; i < this.nodeSize; i++)
        {
            surEdge2SubPath.addElement(new Vector<Vector<Integer>>());
            for (int j = 0; j < this.nodeSize; j++)
            {
                surEdge2SubPath.get(i).addElement(new Vector<Integer>());
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
        this.surNode2virNode = new int[this.virNet.nodeSize];
        this.surNode2subNode = new int[this.nodeSize];

        // knapsack problem
        this.items = new StarStructure[this.virNet.nodeSize];
        this.consumedResource = new ConsumeResource();

        constrcutSurVirNet(sn, vn, bn);
    }

    /**
     * nodeSize.
     * 
     * @return the nodeSize
     */
    public int getNodeSize()
    {
        return nodeSize;
    }

    /**
     * setNodeSize.
     * 
     * @param nodeSize
     *            the nodeSize to set
     */
    public void setNodeSize(int nodeSize)
    {
        this.nodeSize = nodeSize;
    }

    /**
     * constrcutSurVirNet.
     * 
     * @param vn
     *            vn
     * @param bn
     *            bn
     */
    private void constrcutSurVirNet(SubstrateNetwork sn, VirtualNetwork vn, BackupNode bn)
    {
        // node
        for (int i = 0; i < this.nodeSize; i++)
        {
            if (i < this.nodeSize4Failure)
            {
                this.nodeComputationCapacity[i] = vn.nodeComputationCapacity[i];
                this.nodeComputationConsume[i] = vn.nodeComputationDemand[i];
                this.surNode2subNode[i] = vn.virNode2subNode[i];

            } else
            {
                this.nodeComputationCapacity[i] = bn.nodeComputationCapacity[i - this.nodeSize4Failure];
                this.nodeComputationConsume[i] = 0;
                this.surNode2subNode[i] = bn.backNode2subNode[i - this.nodeSize4Failure];
                if (bn.isHaveSubstrateNodeResource4buNode[i - this.nodeSize4Failure])
                {
                    this.isSubNodeComEmpty[i] = true;
                }
            }
        }
        for (int i = 0; i < this.nodeSize4Failure; i++)
        {
            this.virNode2surNode[i] = i;
            this.surNode2virNode[i] = i;
        }
        // edge
        for (int i = 0; i < this.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if ((i < this.nodeSize4Failure)
                        && (vn.edgeBandwithDemand[this.virNode2surNode[i]][this.virNode2surNode[j]] > 0))
                {
                    int isurNode = this.virNode2surNode[i];
                    int jsurNode = this.virNode2surNode[j];
                    this.topology[isurNode][jsurNode] = true;
                    this.topology[jsurNode][isurNode] = true;
                    this.edgeBandwith4Comsume[isurNode][jsurNode] = vn.edgeBandwithDemand[i][j];
                    this.edgeBandwith4Comsume[jsurNode][isurNode] = vn.edgeBandwithDemand[i][j];
                }
            }
        }

        // service
        for (int i = 0; i < this.nodeSize; i++)
        {
            for (int j = 0; j < this.serviceNum; j++)
            {
                if (i < this.nodeSize4Failure)
                {
                    this.boolServiceTypeSet[i][j] = sn.boolServiceTypeSet[vn.virNode2subNode[i]][j];
                } else
                {
                    this.boolServiceTypeSet[i][j] = sn.boolServiceTypeSet[bn.backNode2subNode[i
                            - this.nodeSize4Failure]][j];
                }
            }

        }

        // label
        String estr = "EN_";
        String bstr = "BN_";
        for (int i = 0; i < this.nodeSize; i++)
        {
            if (i < this.nodeSize4Failure)
            {
                this.node2Label[i] = estr + i;
                this.label2Node.put(estr + i, i);
            } else
            {
                this.node2Label[i] = bstr + (i - this.nodeSize4Failure);
                this.label2Node.put(bstr + (i - this.nodeSize4Failure), i);
            }
        }

        for (int i = 0; i < this.nodeSize; i++)
        {
            if (this.nodeComputationConsume[i] > 0)
            {
                consumedResource.initNodeNumber++;
                consumedResource.consumedNodeNumber++;

                consumedResource.initNodeComputation += this.nodeComputationConsume[i];
                consumedResource.consumedNodeComputation += this.nodeComputationConsume[i];
            }
        }

        for (int i = 0; i < this.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                consumedResource.consumeEdgeBandwith += this.edgeBandwith4Comsume[i][j];
                consumedResource.initEdgeBandwith += this.edgeBandwith4Comsume[i][j];
            }
        }

    }

    /**
     * obtainItems.
     */
    public void obtainItems()
    {
        for (int i = 0; i < this.virNet.nodeSize; i++)
        {
            items[i] = new StarStructure();
            items[i].starNodeSurVirNetInd = this.virNode2surNode[i];
            items[i].starNodeComputation = this.virNet.nodeComputationDemand[i];
            items[i].starNodeServiceType = this.virNet.nodeServiceType[i];
            items[i].neighborEdge = new Vector<StarEdgeStructure>();
            for (int j = 0; j < this.virNet.nodeSize; j++)
            {
                if (this.virNet.topology[i][j])
                {
                    StarEdgeStructure edge = new StarEdgeStructure();
                    edge.neighborNode4VirNetInd = j;
                    edge.neighborNode4SurVirNetInd = this.virNode2surNode[j];
                    edge.neighborEdgeBandwith = this.virNet.edgeBandwithDemand[i][j];
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
     *            failureNodeID
     */
    void constructKnapsacks(int failSurNode)
    {
        knapsacks = new Vector<StarStructure>();
        for (int i = 0; i < this.nodeSize; i++)
        {
            if (this.surNode2subNode[i] != this.surNode2subNode[failSurNode])
            {

                StarStructure star = new StarStructure();
                star.starNodeServiceType = -1;
                star.nodeServiceTypeSet = new boolean[this.serviceNum];
                for (int j = 0; j < this.serviceNum; j++)
                {
                    star.nodeServiceTypeSet[j] = this.boolServiceTypeSet[i][j];
                }
                star.starNodeSurVirNetInd = i;
                star.starNodeComputation = this.nodeComputationConsume[i];
                // ineffective value could be erased
                star.neighborEdge = new Vector<StarEdgeStructure>();

                for (int k = 0; k < this.virNet.nodeSize; k++)
                {
                    int surNode = this.virNode2surNode[k];

                    if (failSurNode != surNode)
                    {
                        StarEdgeStructure edge = new StarEdgeStructure();
                        edge.neighborNode4VirNetInd = k;
                        edge.neighborNode4SurVirNetInd = surNode;
                        if (i == this.virNode2surNode[k])
                        {
                            edge.neighborEdgeBandwith = Integer.MAX_VALUE;
                        } else
                        {
                            edge.neighborEdgeBandwith = this.edgeBandwith4Comsume[i][surNode];
                        }
                        edge.neighborNodeType = this.virNet.nodeServiceType[k];
                        star.neighborEdge.addElement(edge);
                    }

                }
                knapsacks.addElement(star);

            }
        }
    }

    /**
     * failIthNode.
     * 
     * @param failSurNode
     *            failureNodeID
     * @param isFailDep
     *            failurtype
     * @param ithItem2ithKnapsack
     *            solution
     * @return int
     * @throws GRBException
     *             GRBException
     */
    public int failIthNode(int failSurNode, int isFailDep, int[] ithItem2ithKnapsack) throws GRBException
    {
        constructKnapsacks(failSurNode);
        MulitpleKnapsack mkp = new MulitpleKnapsack(this.virNet.nodeSize, knapsacks.size(), this.nodeSize);
        constructMultipleKnapsackProbem(mkp, isFailDep);
        int optimalResult = -1;
        if (this.matchMethod == Parameter.MatchMethodDP)
        {
            optimalResult = mkp.optimalSoutionDP(ithItem2ithKnapsack);
            if (optimalResult == -1)
            {
                loggerEnhancedVirtualNetwork.warn("Failure node: " + failSurNode + ", there is not solution");
                return -1;
            }
        }

        if (this.matchMethod == Parameter.MatchMethodIP)
        {
            optimalResult = mkp.optimalSoutionIntegerPrograming(ithItem2ithKnapsack);
            if (optimalResult == -1)
            {
                loggerEnhancedVirtualNetwork.warn("Failure node: " + failSurNode + ", there is not solution");
                return -1;
            }
        }

        return optimalResult;

    }

    /**
     * augmentNodeEdge.
     * 
     * @param solution
     *            solution
     * @param failNode
     *            failurenodeID
     * @param matchMehtod
     *            Method
     */
    private void augmentNodeEdge(int[] solution, int failNode, int matchMehtod)
    {

        int[] virutialNode2NewSurvivalVirtualNode = new int[this.virNet.nodeSize];
        if (matchMehtod == Parameter.MatchMethodIP)
        {
            for (int i = 0; i < this.virNet.nodeSize; i++)
            {
                virutialNode2NewSurvivalVirtualNode[i] = knapsacks.elementAt(solution[i]).starNodeSurVirNetInd;
            }
        }
        if (matchMehtod == Parameter.MatchMethodDP)
        {
            for (int i = 0; i < this.virNet.nodeSize; i++)
            {
                virutialNode2NewSurvivalVirtualNode[i] = solution[i];
                // System.out.println(virutialNode2NewVirtualNode[i] + 1);
            }
        }

        for (int i = 0; i < this.virNet.nodeSize; i++)
        {
            int ithVirNode2ithSurNode = virutialNode2NewSurvivalVirtualNode[i];
            // add node computation
            if (this.nodeComputationConsume[ithVirNode2ithSurNode] < items[i].starNodeComputation)
            {
                this.nodeComputationConsume[ithVirNode2ithSurNode] = items[i].starNodeComputation;
            }
            for (int j = 0; j < items[i].neighborEdge.size(); j++)
            {
                int neighborVirNetNode = items[i].neighborEdge.elementAt(j).neighborNode4VirNetInd;
                int neighborVirNetNode2ithSurNode = virutialNode2NewSurvivalVirtualNode[neighborVirNetNode];
                if (ithVirNode2ithSurNode != neighborVirNetNode2ithSurNode)
                {
                    int cap = items[i].neighborEdge.elementAt(j).neighborEdgeBandwith;
                    if (this.edgeBandwith4Comsume[ithVirNode2ithSurNode][neighborVirNetNode2ithSurNode] < cap)
                    {
                        this.edgeBandwith4Comsume[ithVirNode2ithSurNode][neighborVirNetNode2ithSurNode] = cap;
                        this.edgeBandwith4Comsume[neighborVirNetNode2ithSurNode][ithVirNode2ithSurNode] = cap;
                        this.topology[ithVirNode2ithSurNode][neighborVirNetNode2ithSurNode] = true;
                        this.topology[neighborVirNetNode2ithSurNode][ithVirNode2ithSurNode] = true;
                    }
                }
            }
        }
    }

    /**
     * constructMultipleKnapsackProbem.
     * 
     * @param multKnaP
     *            multKnaP
     * @param isFailDep
     *            isFailDep
     * @return boolean
     */
    boolean constructMultipleKnapsackProbem(MulitpleKnapsack multKnaP, int isFailDep)
    {
        for (int i = 0; i < this.virNet.nodeSize; i++)
        {
            for (int j = 0; j < knapsacks.size(); j++)
            {
                multKnaP.matchMatrix[i][j] = Integer.MAX_VALUE;
                if ((isFailDep == Parameter.FailureIndependent)
                        && (knapsacks.elementAt(j).starNodeSurVirNetInd < this.nodeSize4Failure)
                        && (items[i].starNodeSurVirNetInd != knapsacks.elementAt(j).starNodeSurVirNetInd))
                {
                    continue;
                }

                if ((isFailDep == Parameter.One2OneProtection)
                        && (items[i].starNodeSurVirNetInd != knapsacks.elementAt(j).starNodeSurVirNetInd)
                        && (0 != this.nodeComputationConsume[knapsacks.elementAt(j).starNodeSurVirNetInd]))
                {
                    continue;
                }

                // if (knapsacks.elementAt(j).starNodeSurVirNetInd ==
                // items[i].starNodeSurVirNetInd) {
                // System.out.println("fff");
                // }
                if (knapsacks.elementAt(j).nodeServiceTypeSet[items[i].starNodeServiceType])
                {
                    if (items[i].starNodeComputation <= this.nodeComputationCapacity[knapsacks
                            .elementAt(j).starNodeSurVirNetInd])
                    {
                        multKnaP.matchMatrix[i][j] = 0;

                        if (this.isSubNodeComEmpty[knapsacks.elementAt(j).starNodeSurVirNetInd])
                        {
                            multKnaP.matchMatrix[i][j] += Parameter.addNewVirNodeNewSubNodeCost;
                        }

                        if (0 == this.nodeComputationConsume[knapsacks.elementAt(j).starNodeSurVirNetInd])
                        {
                            multKnaP.matchMatrix[i][j] += Parameter.addNewVirNodeCost;
                        }

                        if (items[i].starNodeSurVirNetInd != knapsacks.elementAt(j).starNodeSurVirNetInd)
                        {
                            multKnaP.matchMatrix[i][j] += Parameter.transformExistedNodeCost;
                        }

                        if (items[i].starNodeComputation > this.nodeComputationConsume[knapsacks
                                .elementAt(j).starNodeSurVirNetInd])
                        {

                            if (items[i].starNodeSurVirNetInd != knapsacks.elementAt(j).starNodeSurVirNetInd)
                            {
                                if (Parameter.isNewCriter)
                                {
                                    multKnaP.matchMatrix[i][j] += (Parameter.addNodeComputaionCost
                                            * (items[i].starNodeComputation));
                                } else
                                {
                                    multKnaP.matchMatrix[i][j] += (Parameter.addNodeComputaionCost
                                            * (items[i].starNodeComputation - this.nodeComputationConsume[knapsacks
                                                    .elementAt(j).starNodeSurVirNetInd]));
                                }
                            }

                        }

                        for (int k = 0; k < items[i].neighborEdge.size(); k++)
                        {
                            boolean isExistedEdge = true;
                            for (int l = 0; l < knapsacks.elementAt(j).neighborEdge.size(); l++)
                            {

                                if (items[i].neighborEdge
                                        .elementAt(k).neighborNode4VirNetInd == knapsacks.elementAt(j).neighborEdge
                                                .elementAt(l).neighborNode4VirNetInd)
                                {
                                    isExistedEdge = false;
                                    if (items[i].neighborEdge
                                            .elementAt(k).neighborEdgeBandwith > knapsacks.elementAt(j).neighborEdge
                                                    .elementAt(l).neighborEdgeBandwith)
                                    {

                                        if (Parameter.isNewCriter)
                                        {
                                            if (items[i].starNodeSurVirNetInd != knapsacks
                                                    .elementAt(j).starNodeSurVirNetInd)
                                            {
                                                multKnaP.matchMatrix[i][j] += (Parameter.addEdgeBandwithCost
                                                        * (items[i].neighborEdge.elementAt(k).neighborEdgeBandwith));
                                            } else
                                            {
                                                multKnaP.matchMatrix[i][j] += (Parameter.addEdgeBandwithCost
                                                        * (items[i].neighborEdge.elementAt(k).neighborEdgeBandwith
                                                                - knapsacks.elementAt(j).neighborEdge
                                                                        .elementAt(l).neighborEdgeBandwith));
                                            }

                                        } else
                                        {
                                            multKnaP.matchMatrix[i][j] += (Parameter.addEdgeBandwithCost
                                                    * (items[i].neighborEdge.elementAt(k).neighborEdgeBandwith
                                                            - knapsacks.elementAt(j).neighborEdge
                                                                    .elementAt(l).neighborEdgeBandwith));
                                        }

                                    }

                                }
                            }
                            if (isExistedEdge)
                            {
                                multKnaP.matchMatrix[i][j] += items[i].neighborEdge.elementAt(k).neighborEdgeBandwith;
                            }

                        }

                    }
                }

            }
        }

        // System.out.println();
        // System.out.println();
        // for (int i = 0; i < this.virNet.nodeSize; i++) {
        // for (int j = 0; j < knapsacks.size(); j++) {
        // // multKnaP.matchMatrix[i][j] = Integer.MAX_VALUE;
        // if (multKnaP.matchMatrix[i][j] != Integer.MAX_VALUE) {
        // System.out.print(multKnaP.matchMatrix[i][j] + " ");
        // } else
        // System.out.print("oo ");
        // }
        // System.out.println();
        // }
        // System.out.println();
        // System.out.println();
        for (int i = 0; i < knapsacks.size(); i++)
        {
            multKnaP.ithKapsack2ithUnionKnapsack[i] = knapsacks.elementAt(i).starNodeSurVirNetInd;
        }
        for (int i = 0; i < this.nodeSize; i++)
        {
            multKnaP.unionKnapsackCapacity[i] = this.nodeComputationCapacity[i];
        }
        for (int i = 0; i < this.virNet.nodeSize; i++)
        {
            multKnaP.capacityItem[i] = this.virNet.nodeComputationDemand[i];
        }
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
     * 
     * @param alg
     *            alg
     */
    public void computeConsumedResource(Algorithm alg)
    {
        int nodeNum = 0;
        int nodeComputaiton = 0;
        for (int i = 0; i < this.nodeSize; i++)
        {
            if (this.nodeComputationConsume[i] > 0)
            {
                nodeComputaiton += this.nodeComputationConsume[i];
                nodeNum++;
            }
        }

        int edgeBandwith = 0;
        for (int i = 0; i < this.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                edgeBandwith += this.edgeBandwith4Comsume[i][j];
            }
        }
        loggerEnhancedVirtualNetwork.info((nodeNum - this.consumedResource.consumedNodeNumber) + " Node + "
                + (nodeComputaiton - this.consumedResource.consumedNodeComputation) + " Computation + "
                + (edgeBandwith - this.consumedResource.consumeEdgeBandwith) + " Bandwith ");

        this.consumedResource.consumedNodeNumber = nodeNum;
        this.consumedResource.consumedNodeComputation = nodeComputaiton;
        this.consumedResource.consumeEdgeBandwith = edgeBandwith;
    }

    /**
     * heursitcAlgorithm4Survivability.
     */
    public boolean heursitcAlgorithm4SeVN(Algorithm alg) throws GRBException
    {
        // 1 node+2 node computaion+12 bandwidth
        // 0 node+1 node computaion+5 bandwidth
        // 0 node+2 node computaion+3 bandwidth
        // 1 node+6 node computaion+3 bandwidth
        // =2 node+11 node computaion+23 bandwidth

        if (alg.sequence == Parameter.Ran)
        {
            for (int i = 0; i < this.nodeSize4Failure; i++)
            {

                if ((i == 0) && (alg.getVnp().topologyType == Parameter.TopologyTypeDataCenter))
                {
                    i++;
                }

                int[] ithItem2ithKnapsack = new int[this.nodeSize4Failure];
                if (failIthNode(i, alg.isFailDep, ithItem2ithKnapsack) == -1)
                {
                    return false;
                }
                augmentNodeEdge(ithItem2ithKnapsack, i, matchMethod);

            }
        }
        if (alg.sequence == Parameter.Min)
        {
            boolean[] isFailed = new boolean[this.nodeSize4Failure];
            for (int i = 0; i < this.nodeSize4Failure; i++)
            {
                if ((i == 0) && (alg.getVnp().topologyType == Parameter.TopologyTypeDataCenter))
                {
                    i++;
                }
                
                int[][] solution = new int[this.nodeSize4Failure][this.nodeSize4Failure];
                int[] solutionCost = new int[this.nodeSize4Failure];
                
                for (int j = 0; j < this.nodeSize4Failure; j++)
                {
                    if ((j == 0) && (alg.getVnp().topologyType == Parameter.TopologyTypeDataCenter))
                    {
                        j++;
                    }
                    if (!isFailed[j])
                    {
                        solutionCost[j] = failIthNode(j, alg.isFailDep, solution[j]);
                        if (solutionCost[j] == -1)
                        {
                            return false;
                        }
                    }
                }

                int nextOptimalFailNode = -1;
                int optimalSolutionVal = Integer.MAX_VALUE;
                for (int j = 0; j < this.nodeSize4Failure; j++)
                {
                    if ((j == 0) && (alg.getVnp().topologyType == Parameter.TopologyTypeDataCenter))
                    {
                        j++;
                    }
                    if (!isFailed[j])
                    {
                        if (solutionCost[j] < optimalSolutionVal)
                        {
                            // if (nextOptimalFailNode == -1)
                            nextOptimalFailNode = j;
                            optimalSolutionVal = solutionCost[j];
                        }
                    }
                }
                augmentNodeEdge(solution[nextOptimalFailNode], nextOptimalFailNode, matchMethod);
                isFailed[nextOptimalFailNode] = true;
            }
        }
        computeConsumedResource(alg);
        return true;

    }

    /**
     * exactAlgorithmIP4SeVirNet.
     * 
     * @param alg
     * 
     * @return
     * 
     */
    public boolean exactAlgorithmIntegerProgram4SeVN(Algorithm alg)
    {
        try
        {
            GRBEnv env = new GRBEnv();
            GRBModel model = new GRBModel(env);
            model.getEnv().set(GRB.IntParam.OutputFlag, 0);
            // Create variables
            GRBVar[][][] transformMatrix;
            transformMatrix = new GRBVar[this.nodeSize4Failure + 1][this.virNet.nodeSize][this.nodeSize];
            for (int i = 0; i <= this.nodeSize4Failure; i++)
            {
                for (int j = 0; j < this.virNet.nodeSize; j++)
                {
                    for (int k = 0; k < this.nodeSize; k++)
                    {
                        if (0 == i)
                        {
                            if (k == this.virNode2surNode[j])
                            {
                                transformMatrix[i][j][k] = model.addVar(1.0, 1.0, 0.0, GRB.CONTINUOUS,
                                        "T" + i + " r:" + j + " c:" + k);
                            } else
                            {
                                transformMatrix[i][j][k] = model.addVar(0.0, 0.0, 0.0, GRB.CONTINUOUS,
                                        "T" + i + " r:" + j + " c:" + k);
                            }
                        } else
                        {
                            // 3
                            if ((i - 1) == k)
                            {
                                transformMatrix[i][j][k] = model.addVar(0.0, 0.0, 0.0, GRB.CONTINUOUS,
                                        "T" + i + " r:" + j + " c:" + k);
                            } else
                            {
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
            for (int j = 0; j < this.nodeSize; j++)
            {
                for (int k = 0; k < this.nodeSize; k++)
                {
                    survivalGraphBandwithMatrix[j][k] = model.addVar(0.0, Parameter.SubStrateEdgeBandwithMaximum, 0.0,
                            GRB.INTEGER, "GB" + " r:" + j + " c:" + k);
                }
            }

            // 2
            GRBVar[] survivalGraphComputationMatrix;
            survivalGraphComputationMatrix = new GRBVar[this.nodeSize];
            for (int j = 0; j < this.nodeSize; j++)
            {
                survivalGraphComputationMatrix[j] = model.addVar(0.0, this.nodeComputationCapacity[j], 0.0, GRB.INTEGER,
                        "GC" + " r:" + j);
            }

            GRBVar[] activeNodeVector;
            activeNodeVector = new GRBVar[this.nodeSize];
            for (int j = 0; j < this.nodeSize; j++)
            {
                if (j < this.nodeSize4Failure)
                {
                    activeNodeVector[j] = model.addVar(1.0, 1.0, 0.0, GRB.CONTINUOUS, "activeNode" + ": " + j);
                } else
                {
                    activeNodeVector[j] = model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, "activeNode" + ": " + j);
                }
            }

            // Integrate new variables
            model.update();
            // set objecion function
            GRBLinExpr objexpr = new GRBLinExpr();
            for (int i = 0; i < this.nodeSize; i++)
            {
                objexpr.addTerm(Parameter.addNewVirNodeCost, activeNodeVector[i]);
                objexpr.addTerm(Parameter.addNodeComputaionCost, survivalGraphComputationMatrix[i]);
                for (int j = 0; j < this.nodeSize; j++)
                {
                    objexpr.addTerm(Parameter.addEdgeBandwithCost, survivalGraphBandwithMatrix[i][j]);
                }
            }
            model.setObjective(objexpr, GRB.MINIMIZE);

            // 1
            for (int i = 0; i <= this.nodeSize4Failure; i++)
            {
                for (int j = 0; j < this.nodeSize; j++)
                {
                    GRBLinExpr conexpr = new GRBLinExpr();
                    for (int k = 0; k < this.virNet.nodeSize; k++)
                    {
                        conexpr.addTerm(this.virNet.nodeComputationDemand[k], transformMatrix[i][k][j]);
                    }
                    model.addConstr(conexpr, GRB.LESS_EQUAL, survivalGraphComputationMatrix[j],
                            "MAC*T" + "T " + i + "r " + j);
                }
            }

            // (MAG*T)'*T<MBG
            //
            for (int l = 0; l <= this.nodeSize4Failure; l++)
            {
                for (int i = 0; i < this.nodeSize4Failure; i++)
                {
                    for (int j = 0; j < this.nodeSize; j++)
                    {
                        GRBQuadExpr conexprL = new GRBQuadExpr();
                        for (int k = 0; k < this.nodeSize; k++)
                        {
                            if (k < j)
                            {
                                conexprL.addTerm(1.0, transformMatrix[l][i][k], survivalGraphBandwithMatrix[j][k]);
                            } else
                            {
                                conexprL.addTerm(1.0, transformMatrix[l][i][k], survivalGraphBandwithMatrix[k][j]);

                            }
                        }
                        GRBQuadExpr conexprR = new GRBQuadExpr();
                        for (int t = 0; t < this.virNet.nodeSize; t++)
                        {
                            conexprR.addTerm(this.virNet.edgeBandwithDemand[t][i], transformMatrix[l][t][j]);
                        }
                        model.addQConstr(conexprR, GRB.LESS_EQUAL, conexprL,
                                "T " + l + "T'*MAG'*T" + "r " + i + " c " + j);
                    }
                }
            }

            for (int k = 0; k < this.nodeSize; k++)
            {
                for (int j = k + 1; j < this.nodeSize; j++)
                {
                    model.addConstr(survivalGraphBandwithMatrix[j][k], GRB.EQUAL, survivalGraphBandwithMatrix[k][j],
                            "Equl B r:" + k + " j:" + j);
                }
            }

            // for (int i = 0; i <= this.nodeSize4Failure; i++) {
            // // T'*MAG'
            // GRBQuadExpr[][] tmag = new GRBQuadExpr[this.nodeSize][this.nodeSize];
            // for (int j = 0; j < this.nodeSize; j++) {
            // for (int t = 0; t < this.nodeSize; t++) {
            // tmag[j][t] = new GRBQuadExpr();
            // for (int k = 0; k < this.virNet.nodeSize; k++) {
            // for (int l = 0; l < this.virNet.nodeSize; l++) {
            // // TMAG[j][k].addTerm(this.VNR.edgeBandwithDemand[k][l],
            // // TransfromMatrix[i][l][j]);
            // for (int p = 0; p < this.virNet.nodeSize; p++) {
            // Integer inte = this.virNet.edgeBandwithDemand[k][p];
            // tmag[j][t].addTerm(inte.doubleValue(), transformMatrix[i][k][j],
            // transformMatrix[i][p][t]);
            // }
            // }
            // }
            // model.addQConstr(tmag[j][t], GRB.LESS_EQUAL,
            // survivalGraphBandwithMatrix[j][t],
            // "T " + i + "T'*MAG'*T" + "r " + j + " c " + t);
            // }
            // }
            // }

            // 3
            for (int i = 0; i < this.nodeSize4Failure; i++)
            {
                for (int j = 0; j < this.nodeSize4Failure; j++)
                {
                    GRBLinExpr conexpr = new GRBLinExpr();
                    conexpr.addTerm(1.0, survivalGraphBandwithMatrix[i][j]);
                    model.addConstr(conexpr, GRB.GREATER_EQUAL, this.virNet.edgeBandwithDemand[i][j], "con MAB<=MBB");
                }
            }

            // 4
            for (int i = 0; i <= this.nodeSize4Failure; i++)
            {
                for (int j = 0; j < this.virNet.nodeSize; j++)
                {
                    GRBLinExpr conexpr = new GRBLinExpr();
                    for (int k = 0; k < this.nodeSize; k++)
                    {
                        conexpr.addTerm(1.0, transformMatrix[i][j][k]);
                    }
                    model.addConstr(conexpr, GRB.EQUAL, 1.0, "Con Tiv=1: " + i + " " + j);
                }
            }

            // for (int i = 0; i <= this.nodeSize4Failure; i++) {
            // for (int k = 0; k < this.nodeSize; k++) {
            // GRBLinExpr conexpr = new GRBLinExpr();
            // for (int j = 0; j < this.virNet.nodeSize; j++) {
            // conexpr.addTerm(1.0, transformMatrix[i][j][k]);
            // }
            // model.addConstr(conexpr, GRB.LESS_EQUAL, 1.0, "Con Tuj<=1:" + i + " " +
            // k);
            // }
            // }

            // 5
            for (int i = 0; i <= this.nodeSize4Failure; i++)
            {
                GRBLinExpr conexpr = new GRBLinExpr();
                for (int j = 0; j < this.virNet.nodeSize; j++)
                {
                    for (int k = 0; k < this.nodeSize; k++)
                    {
                        conexpr.addTerm(1.0, transformMatrix[i][j][k]);
                    }
                }
                model.addConstr(conexpr, GRB.EQUAL, this.nodeSize4Failure, "Con Tuv=n:" + i);
            }

            // 6
            for (int i = 1; i <= this.nodeSize4Failure; i++)
            {
                GRBLinExpr conexpr = new GRBLinExpr();
                for (int j = 0; j < this.virNet.nodeSize; j++)
                {
                    conexpr.addTerm(1.0, transformMatrix[i][j][i - 1]);
                }
                model.addConstr(conexpr, GRB.EQUAL, 0.0, "Con Tuk=0:");
            }
            // 7
            for (int i = 0; i <= this.nodeSize4Failure; i++)
            {
                for (int j = 0; j < this.virNet.nodeSize; j++)
                {
                    for (int l = 0; l < this.serviceNum; l++)
                    {
                        GRBLinExpr conexpr = new GRBLinExpr();
                        for (int k = 0; k < this.nodeSize; k++)
                        {
                            if (this.boolServiceTypeSet[k][l])
                            {
                                conexpr.addTerm(1.0, transformMatrix[i][j][k]);
                            }
                        }
                        if (this.virNet.nodeServiceType[j] == (l))
                        {
                            model.addConstr(conexpr, GRB.GREATER_EQUAL, 1.0, "T*MBS" + "T " + i + "r " + j + "c: " + l);
                        }
                        // else {
                        // model.addConstr(conexpr, GRB.GREATER_EQUAL, 0.0,
                        // "T*ESM" + "T " + i + "r " + j + "c: " + l);
                        // }
                    }
                }
            }

            // Add constraint 8
            // regulate the TransfromMatrix
            for (int k = 0; k < this.nodeSize; k++)
            {
                for (int i = 0; i <= this.nodeSize4Failure; i++)
                {
                    for (int j = 0; j < this.virNet.nodeSize; j++)
                    {
                        GRBLinExpr conexpr = new GRBLinExpr();
                        conexpr.addTerm(1.0, transformMatrix[i][j][k]);
                        model.addConstr(conexpr, GRB.LESS_EQUAL, activeNodeVector[k],
                                "Con activeNode:" + k + " " + i + " " + j);
                    }
                }

            }

            model.optimize();
            int optimstatus = model.get(GRB.IntAttr.Status);
            if (optimstatus != GRB.OPTIMAL)
            {
                return false;
            }
            for (int i = 0; i < this.nodeSize; i++)
            {
                if (i >= this.nodeSize4Failure)
                {
                    this.nodeComputationConsume[i] += ((int) survivalGraphComputationMatrix[i].get(GRB.DoubleAttr.X));
                } else
                {
                    this.nodeComputationConsume[i] += ((int) survivalGraphComputationMatrix[i].get(GRB.DoubleAttr.X)
                            - this.virNet.nodeComputationDemand[i]);
                }
                for (int j = 0; j < this.nodeSize; j++)
                {
                    if (i < this.nodeSize4Failure && j < this.nodeSize4Failure)
                    {
                        this.edgeBandwith4Comsume[i][j] += ((int) (survivalGraphBandwithMatrix[i][j]
                                .get(GRB.DoubleAttr.X) - this.virNet.edgeBandwithDemand[i][j]));
                    } else
                    {
                        this.edgeBandwith4Comsume[i][j] += ((int) survivalGraphBandwithMatrix[i][j]
                                .get(GRB.DoubleAttr.X));
                    }
                }
            }
            loggerEnhancedVirtualNetwork.info("ILP method Succed");
            computeConsumedResource(alg);
        } catch (GRBException e)
        {
            e.printStackTrace();
        }
        return true;

    }

    /**
     * startSurvivalVirtualNetwork.
     * 
     */
    public boolean startSeVNAlgorithm(Algorithm alg)
    {
        boolean isSvnAlgorithmSuccess = false;
        if (alg.isExact)
        {
            isSvnAlgorithmSuccess = this.exactAlgorithmIntegerProgram4SeVN(alg);
        } else
        {
            this.obtainItems();
            try
            {
                isSvnAlgorithmSuccess = this.heursitcAlgorithm4SeVN(alg);
            } catch (GRBException e)
            {
                e.printStackTrace();
            }
        }
        return isSvnAlgorithmSuccess;
    }

    /**
     * destructerResource.
     */
    public void destructerResource()
    {
        for (int i = 0; i < this.nodeSize; i++)
        {
            for (int j = 0; j < this.nodeSize; j++)
            {
                surEdge2SubPath.get(i).get(j).clear();
            }
            surEdge2SubPath.get(i).clear();
        }
        surEdge2SubPath.clear();
        label2Node.clear();
        for (int i = 0; i < knapsacks.size(); i++)
        {
            knapsacks.get(i).neighborEdge.clear();
        }
        knapsacks.clear();
    }

}
