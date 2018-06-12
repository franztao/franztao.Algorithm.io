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

    public SurvivalVirtualNetwork surNet;

    /**
     * @param surNet
     */
    public StarDP(SurvivalVirtualNetwork surNet)
    {
        this.surNet = surNet;
    }

    StarStructure[] items;
    Vector<StarStructure> knapSacks;

    public void obtainItems(SurvivalVirtualNetwork surNet)
    {
        this.items = new StarStructure[surNet.virNet.nodeSize];
        for (int i = 0; i < surNet.virNet.nodeSize; i++)
        {
            items[i] = new StarStructure();
            // node
            items[i].starNode2SurNetInd = surNet.virNode2surNode[i];
            items[i].starNodeComputation = surNet.virNet.nodeComputationDemand[i];
            items[i].starNodeFunctionType = surNet.virNet.nodeFunctionType[i];
            items[i].neighborEdge = new Vector<StarEdgeStructure>();
            // edge
            for (int j = 0; j < surNet.virNet.nodeSize; j++)
            {
                if (surNet.virNet.topology[i][j])
                {
                    StarEdgeStructure edge = new StarEdgeStructure();
                    edge.neighborNode4VirNetInd = j;
                    edge.neighborNode4SurNetInd = surNet.virNode2surNode[j];
                    edge.neighborEdgeBandwith = surNet.virNet.edgeBandwithDemand[i][j];
                    edge.neighborNodeFunctionType = surNet.virNet.nodeFunctionType[j];
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
    public boolean heursitcAlgorithm4SeVN(SeVNAlgorithm alg, SurvivalVirtualNetwork svn)
    {
        // 1 node+2 node computaion+12 bandwidth
        // 0 node+1 node computaion+5 bandwidth
        // 0 node+2 node computaion+3 bandwidth
        // 1 node+6 node computaion+3 bandwidth
        // =2 node+11 node computaion+23 bandwidth

        if (alg.failSequence == Parameter.Ran)
        {
            for (int i = 0; i < svn.nodeSize4Failure;)
            {

                if ((i == 0) && (Parameter.TopologyType == Parameter.TopologyTypeDataCenter))
                {
                    i++;
                }

                Vector<Integer> failureNodeSet = new Vector<Integer>();
                int[] ithItem2ithKnapsack = new int[svn.nodeSize4Failure];

                for (int j = 0; j < (Parameter.NodeFailureNumberEnd) && (i < svn.nodeSize4Failure); j++)
                {
                    failureNodeSet.add(i);
                    i++;
                }

                int bipartiteMatchingSum = testFailureNode(failureNodeSet, alg.algType, ithItem2ithKnapsack);
                if (bipartiteMatchingSum == -1)
                {
                    this.surNet.edgeWeightSum = 0;
                    return false;
                }
                this.surNet.edgeWeightSum = bipartiteMatchingSum;

                /// ??ithItem2ithKnapsack

                ///
                svn.augmentNodeEdge(ithItem2ithKnapsack, Parameter.MatchMethod, this.items, this.knapSacks);

            }
        }

        // Parameter.Min do noe support mylitiple node failure emperiment
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
                        Vector<Integer> nodeFailure = new Vector<Integer>();
                        nodeFailure.add(j);
                        solutionCost[j] = testFailureNode(nodeFailure, alg.algType, solution[j]);
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
                svn.augmentNodeEdge(solution[nextOptimalFailNode], Parameter.MatchMethod, this.items, this.knapSacks);
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
    void constructKnapsacks(Vector<Integer> failureSurNodeSet)
    {
        knapSacks = new Vector<StarStructure>();
        for (int i = 0; i < this.surNet.nodeSize; i++)
        {
            boolean isContinue = false;
            for (int t = 0; t < failureSurNodeSet.size(); t++)
            {
                if (this.surNet.surNode2phyNode[i] == this.surNet.surNode2phyNode[failureSurNodeSet.get(t)])
                {
                    isContinue = true;
                }
            }
            if (isContinue)
            {
                continue;
            }

            StarStructure physicalStar = new StarStructure();
            physicalStar.starNodeFunctionType = -1;
            physicalStar.nodeFunctionTypeSet = new boolean[this.surNet.functionNum];
            for (int j = 0; j < this.surNet.functionNum; j++)
            {
                physicalStar.nodeFunctionTypeSet[j] = this.surNet.boolFunctionTypeSet[i][j];
            }

            physicalStar.starNode2SurNetInd = i;
            physicalStar.starNodeComputation = this.surNet.nodeComputationConsume[i];
            // ineffective value could be erased
            physicalStar.neighborEdge = new Vector<StarEdgeStructure>();

            for (int surNode = 0; surNode < this.surNet.nodeSize; surNode++)
            {

                boolean isContinuek = false;
                for (int t = 0; t < failureSurNodeSet.size(); t++)
                {
                    if (surNode == failureSurNodeSet.get(t))
                    {
                        isContinuek = true;
                    }
                }
                if (isContinuek)
                {
                    continue;
                }

                if (!this.surNet.topology[i][surNode])
                {
                    continue;
                }

                StarEdgeStructure edge = new StarEdgeStructure();
                edge.neighborNode4VirNetInd = this.surNet.surNode2virNode[surNode];
                edge.neighborNode4SurNetInd = surNode;
                if (i == surNode)
                {
                    edge.neighborEdgeBandwith = Integer.MAX_VALUE;
                } else
                {
                    edge.neighborEdgeBandwith = this.surNet.edgeBandwith4Comsume[i][surNode];
                }
                edge.neighborNodeFunctionType = -1;
                physicalStar.neighborEdge.addElement(edge);

            }
            knapSacks.addElement(physicalStar);

        }
    }

    /**
     * failIthNode.
     * 
     * @param failSurNode
     *            failureNodeID
     * @param algType
     *            failurtype
     * @param ithItem2ithKnapsack
     *            solution
     * @return int
     * @throws GRBException
     *             GRBException
     */
    public int testFailureNode(Vector<Integer> nodeFailure, int algType, int[] ithItem2ithKnapsack)
    {
        // int failSurNode
        constructKnapsacks(nodeFailure);
        MulitpleKnapsack mkp = new MulitpleKnapsack(this.surNet.virNet.nodeSize, knapSacks.size(),
                this.surNet.nodeSize);
        constructMultipleKnapsackProbem(mkp, algType);
        int optimalResult = -1;
        if (Parameter.MatchMethod == Parameter.MatchMethodDP)
        {
            optimalResult = mkp.optimalSoutionDP(ithItem2ithKnapsack);
            if (optimalResult == -1)
            {
                return -1;
            }
        }

        if (Parameter.MatchMethod == Parameter.MatchMethodIP)
        {
            try
            {
                optimalResult = mkp.optimalSoutionIntegerPrograming(ithItem2ithKnapsack);
            } catch (GRBException e)
            {
                e.printStackTrace();
            }
            if (optimalResult == -1)
            {
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
     * @param algType
     *            isFailDep
     * @return boolean
     */
    boolean constructMultipleKnapsackProbem(MulitpleKnapsack multKnaP, int algType)
    {
        for (int i = 0; i < this.surNet.virNet.nodeSize; i++)
        {
            for (int j = 0; j < knapSacks.size(); j++)
            {
                multKnaP.matchWeightMatrix[i][j] = Integer.MAX_VALUE;
                if ((algType == Parameter.RVNProtection)
                        && (knapSacks.elementAt(j).starNode2SurNetInd < this.surNet.nodeSize4Failure)
                        && (items[i].starNode2SurNetInd != knapSacks.elementAt(j).starNode2SurNetInd))
                {
                    continue;
                }

                if ((algType == Parameter.One2OneProtection)
                        && (items[i].starNode2SurNetInd != knapSacks.elementAt(j).starNode2SurNetInd)
                        && (0 != this.surNet.nodeComputationConsume[knapSacks.elementAt(j).starNode2SurNetInd]))
                {
                    continue;
                }

                if (!knapSacks.elementAt(j).nodeFunctionTypeSet[items[i].starNodeFunctionType])
                {
                    continue;
                }

                if (items[i].starNodeComputation > this.surNet.nodeComputationCapacity[knapSacks
                        .elementAt(j).starNode2SurNetInd])
                {
                    continue;
                }
                multKnaP.matchWeightMatrix[i][j] = 0;

                if ((this.surNet.nodeComputationConsume[knapSacks.elementAt(j).starNode2SurNetInd] == 0)
                        && this.surNet.isSubNodeComEmpty[knapSacks.elementAt(j).starNode2SurNetInd])
                {
                    multKnaP.matchWeightMatrix[i][j] += Parameter.addNewVirNodeNewPhysicalNodeCost;
                }

                if (0 == this.surNet.nodeComputationConsume[knapSacks.elementAt(j).starNode2SurNetInd])
                {
                    multKnaP.matchWeightMatrix[i][j] += Parameter.addNewVirNodeCost;
                }

                if (items[i].starNode2SurNetInd != knapSacks.elementAt(j).starNode2SurNetInd)
                {
                    multKnaP.matchWeightMatrix[i][j] += Parameter.transformNodeCost;
                }

                if (items[i].starNodeComputation > this.surNet.nodeComputationConsume[knapSacks
                        .elementAt(j).starNode2SurNetInd])
                {

                    multKnaP.matchWeightMatrix[i][j] += (Parameter.addNodeComputaionCost * (items[i].starNodeComputation
                            - this.surNet.nodeComputationConsume[knapSacks.elementAt(j).starNode2SurNetInd]));

                }

                for (int k = 0; k < items[i].neighborEdge.size(); k++)
                {
                    boolean isExistedEdge = true;
                    for (int l = 0; l < knapSacks.elementAt(j).neighborEdge.size(); l++)
                    {

                        if (items[i].neighborEdge
                                .elementAt(k).neighborNode4SurNetInd == knapSacks.elementAt(j).neighborEdge
                                        .elementAt(l).neighborNode4SurNetInd)
                        {
                            isExistedEdge = false;
                            if (items[i].neighborEdge
                                    .elementAt(k).neighborEdgeBandwith > knapSacks.elementAt(j).neighborEdge
                                            .elementAt(l).neighborEdgeBandwith)
                            {

                                multKnaP.matchWeightMatrix[i][j] += (Parameter.addEdgeBandwithCost
                                        * (items[i].neighborEdge.elementAt(k).neighborEdgeBandwith
                                                - knapSacks.elementAt(j).neighborEdge
                                                        .elementAt(l).neighborEdgeBandwith));

                            }

                        }
                    }
                    if (isExistedEdge)
                    {
                        multKnaP.matchWeightMatrix[i][j] += items[i].neighborEdge.elementAt(k).neighborEdgeBandwith;
                    }

                }

            }
        }

        for (int i = 0; i < knapSacks.size(); i++)
        {
            multKnaP.ithKapsack2ithUnionKnapsack[i] = knapSacks.elementAt(i).starNode2SurNetInd;
        }
        for (int i = 0; i < this.surNet.nodeSize; i++)
        {
            multKnaP.unionKnapsackCapacity[i] = this.surNet.nodeComputationCapacity[i];
        }
        for (int i = 0; i < this.surNet.virNet.nodeSize; i++)
        {
            multKnaP.itemCapacity[i] = this.surNet.virNet.nodeComputationDemand[i];
        }
        return false;

    }

}
