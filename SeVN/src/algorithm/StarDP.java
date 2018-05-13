/**
 * 
 */
package algorithm;

import java.util.Vector;

import gurobi.GRBException;
import sevn.Parameter;
import standardAlgorithm.MulitpleKnapsack;
import survivabelVirtualNetwork.SurvivalVirtualNetwork;

/**
 * @author Taoheng
 *
 */
public class StarDP
{

    public SurvivalVirtualNetwork svn;

    /**
     * @param svn
     */
    public StarDP(SurvivalVirtualNetwork svn)
    {
        this.svn = svn;
    }

    StarStructure[] items;
    Vector<StarStructure> knapsacks;

    public void obtainItems(SurvivalVirtualNetwork svn)
    {
        this.items = new StarStructure[svn.virNet.nodeSize];
        for (int i = 0; i < svn.virNet.nodeSize; i++)
        {
            items[i] = new StarStructure();
            items[i].starNodeSurVirNetInd = svn.virNode2surNode[i];
            items[i].starNodeComputation = svn.virNet.nodeComputationDemand[i];
            items[i].starNodeFunctionType = svn.virNet.nodeFunctionType[i];
            items[i].neighborEdge = new Vector<StarEdgeStructure>();
            for (int j = 0; j < svn.virNet.nodeSize; j++)
            {
                if (svn.virNet.topology[i][j])
                {
                    StarEdgeStructure edge = new StarEdgeStructure();
                    edge.neighborNode4VirNetInd = j;
                    edge.neighborNode4SurVirNetInd = svn.virNode2surNode[j];
                    edge.neighborEdgeBandwith = svn.virNet.edgeBandwithDemand[i][j];
                    edge.neighborNodeType = svn.virNet.nodeFunctionType[j];
                    items[i].neighborEdge.addElement(edge);
                }
            }
        }
    }

    /**
     * heursitcAlgorithm4Survivability.
     * 
     * @param svn
     */
    public boolean heursitcAlgorithm4SeVN(SeVNAlgorithm alg, SurvivalVirtualNetwork svn) throws GRBException
    {
        // 1 node+2 node computaion+12 bandwidth
        // 0 node+1 node computaion+5 bandwidth
        // 0 node+2 node computaion+3 bandwidth
        // 1 node+6 node computaion+3 bandwidth
        // =2 node+11 node computaion+23 bandwidth

        if (alg.failSequence == Parameter.Ran)
        {
            for (int i = 0; i < svn.nodeSize4Failure; i++)
            {

                if ((i == 0) && (Parameter.TopologyType == Parameter.TopologyTypeDataCenter))
                {
                    i++;
                }

                int[] ithItem2ithKnapsack = new int[svn.nodeSize4Failure];
                if (failIthNode(i, alg.isFailDep, ithItem2ithKnapsack) == -1)
                {
                    return false;
                }
                svn.augmentNodeEdge(ithItem2ithKnapsack, i, Parameter.MatchMethod, this.items, this.knapsacks);

            }
        }
        if (alg.failSequence == Parameter.Min)
        {
            boolean[] isFailed = new boolean[svn.nodeSize4Failure];
            for (int i = 0; i < svn.nodeSize4Failure; i++)
            {
                if ((i == 0) && (Parameter.TopologyType == Parameter.TopologyTypeDataCenter))
                {
                    i++;
                }

                int[][] solution = new int[svn.nodeSize4Failure][svn.nodeSize4Failure];
                int[] solutionCost = new int[svn.nodeSize4Failure];

                for (int j = 0; j < svn.nodeSize4Failure; j++)
                {
                    if ((j == 0) && (Parameter.TopologyType == Parameter.TopologyTypeDataCenter))
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
                for (int j = 0; j < svn.nodeSize4Failure; j++)
                {
                    if ((j == 0) && (Parameter.TopologyType == Parameter.TopologyTypeDataCenter))
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
                svn.augmentNodeEdge(solution[nextOptimalFailNode], nextOptimalFailNode, Parameter.MatchMethod,
                        this.items, this.knapsacks);
                isFailed[nextOptimalFailNode] = true;
            }
        }
        svn.computeConsumedResource(alg);
        return true;

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
        for (int i = 0; i < this.svn.nodeSize; i++)
        {
            if (this.svn.surNode2subNode[i] != this.svn.surNode2subNode[failSurNode])
            {

                StarStructure star = new StarStructure();
                star.starNodeFunctionType = -1;
                star.nodeServiceTypeSet = new boolean[this.svn.functionNum];
                for (int j = 0; j < this.svn.functionNum; j++)
                {
                    star.nodeServiceTypeSet[j] = this.svn.boolFunctionTypeSet[i][j];
                }
                star.starNodeSurVirNetInd = i;
                star.starNodeComputation = this.svn.nodeComputationConsume[i];
                // ineffective value could be erased
                star.neighborEdge = new Vector<StarEdgeStructure>();

                for (int k = 0; k < this.svn.virNet.nodeSize; k++)
                {
                    int surNode = this.svn.virNode2surNode[k];

                    if (failSurNode != surNode)
                    {
                        StarEdgeStructure edge = new StarEdgeStructure();
                        edge.neighborNode4VirNetInd = k;
                        edge.neighborNode4SurVirNetInd = surNode;
                        if (i == this.svn.virNode2surNode[k])
                        {
                            edge.neighborEdgeBandwith = Integer.MAX_VALUE;
                        } else
                        {
                            edge.neighborEdgeBandwith = this.svn.edgeBandwith4Comsume[i][surNode];
                        }
                        edge.neighborNodeType = this.svn.virNet.nodeFunctionType[k];
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
        MulitpleKnapsack mkp = new MulitpleKnapsack(this.svn.virNet.nodeSize, knapsacks.size(), this.svn.nodeSize);
        constructMultipleKnapsackProbem(mkp, isFailDep);
        int optimalResult = -1;
        if (Parameter.MatchMethod == Parameter.MatchMethodDP)
        {
            optimalResult = mkp.optimalSoutionDP(ithItem2ithKnapsack);
            if (optimalResult == -1)
            {
                // loggerEnhancedVirtualNetwork.warn("Failure node: " + failSurNode + ", there
                // is not solution");
                return -1;
            }
        }

        if (Parameter.MatchMethod == Parameter.MatchMethodIP)
        {
            optimalResult = mkp.optimalSoutionIntegerPrograming(ithItem2ithKnapsack);
            if (optimalResult == -1)
            {
                // loggerEnhancedVirtualNetwork.warn("Failure node: " + failSurNode + ", there
                // is not solution");
                return -1;
            }
        }

        return optimalResult;

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
        for (int i = 0; i < this.svn.virNet.nodeSize; i++)
        {
            for (int j = 0; j < knapsacks.size(); j++)
            {
                multKnaP.matchMatrix[i][j] = Integer.MAX_VALUE;
                if ((isFailDep == Parameter.FailureIndependent)
                        && (knapsacks.elementAt(j).starNodeSurVirNetInd < this.svn.nodeSize4Failure)
                        && (items[i].starNodeSurVirNetInd != knapsacks.elementAt(j).starNodeSurVirNetInd))
                {
                    continue;
                }

                if ((isFailDep == Parameter.One2OneProtection)
                        && (items[i].starNodeSurVirNetInd != knapsacks.elementAt(j).starNodeSurVirNetInd)
                        && (0 != this.svn.nodeComputationConsume[knapsacks.elementAt(j).starNodeSurVirNetInd]))
                {
                    continue;
                }

                // if (knapsacks.elementAt(j).starNodeSurVirNetInd ==
                // items[i].starNodeSurVirNetInd) {
                // System.out.println("fff");
                // }
                if (knapsacks.elementAt(j).nodeServiceTypeSet[items[i].starNodeFunctionType])
                {
                    if (items[i].starNodeComputation <= this.svn.nodeComputationCapacity[knapsacks
                            .elementAt(j).starNodeSurVirNetInd])
                    {
                        multKnaP.matchMatrix[i][j] = 0;

                        if (this.svn.isSubNodeComEmpty[knapsacks.elementAt(j).starNodeSurVirNetInd])
                        {
                            multKnaP.matchMatrix[i][j] += Parameter.addNewVirNodeNewSubNodeCost;
                        }

                        if (0 == this.svn.nodeComputationConsume[knapsacks.elementAt(j).starNodeSurVirNetInd])
                        {
                            multKnaP.matchMatrix[i][j] += Parameter.addNewVirNodeCost;
                        }

                        if (items[i].starNodeSurVirNetInd != knapsacks.elementAt(j).starNodeSurVirNetInd)
                        {
                            multKnaP.matchMatrix[i][j] += Parameter.transformExistedNodeCost;
                        }

                        if (items[i].starNodeComputation > this.svn.nodeComputationConsume[knapsacks
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
                                            * (items[i].starNodeComputation - this.svn.nodeComputationConsume[knapsacks
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
        for (int i = 0; i < this.svn.nodeSize; i++)
        {
            multKnaP.unionKnapsackCapacity[i] = this.svn.nodeComputationCapacity[i];
        }
        for (int i = 0; i < this.svn.virNet.nodeSize; i++)
        {
            multKnaP.capacityItem[i] = this.svn.virNet.nodeComputationDemand[i];
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

}
