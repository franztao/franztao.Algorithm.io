/**
 * 
 */

package survivabelVirtualNetwork;

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

import sevn.Parameter;
import standardAlgorithm.MulitpleKnapsack;
import substrateNetwork.BackupNode;
import substrateNetwork.SubstrateNetwork;
import virtualNetwork.VirtualNetwork;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import algorithm.SeVN;
import algorithm.StarStructure;

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

    /**
     * starStructure.
     * 
     * @author Taoheng
     *
     */

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
        // this.items = new StarStructure[this.virNet.nodeSize];
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
     * augmentNodeEdge.
     * 
     * @param solution
     *            solution
     * @param failNode
     *            failurenodeID
     * @param matchMehtod
     *            Method
     */
    public void augmentNodeEdge(int[] solution, int failNode, int matchMehtod, StarStructure[] items,
            Vector<StarStructure> knapsacks)
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
     * computeUsedResource.
     * 
     * @param alg
     *            alg
     */
    public void computeConsumedResource(SeVN alg)
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
     * destructerResource.
     */
    public void destructerResource(Vector<StarStructure> knapsacks)
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
