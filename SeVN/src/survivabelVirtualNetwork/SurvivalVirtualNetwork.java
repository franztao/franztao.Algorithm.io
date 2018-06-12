/**
 * 
 */

package survivabelVirtualNetwork;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import sevn.Parameter;
import substrateNetwork.BackupNode;
import substrateNetwork.PhysicalNetworkt;
import virtualNetwork.VirtualNetwork;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import algorithm.SeVNAlgorithm;
import algorithm.StarStructure;

/**
 * EVN.
 * 
 * @author franz
 *
 */
public class SurvivalVirtualNetwork
{

    private Logger loggerSurvivalVirtualNetwork = Logger.getLogger(SurvivalVirtualNetwork.class.getName());

    // public final int matchMethod = Parameter.MatchMethod;

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
    public Vector<Vector<Vector<Integer>>> surEdge2PhyPath;

    public int functionNum;
    public boolean[][] boolFunctionTypeSet;

    public String[] node2Label;
    public Map<String, Integer> label2Node;

    public int[] virNode2surNode;
    public int[] surNode2virNode;
    public int[] surNode2phyNode;

    public VirtualNetwork virNet;
    public int index;

    public int edgeWeightSum;
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

    public boolean isSucceedProtection;

    /**
     * SurvivalVirtualNetwork.
     * 
     * @param vn
     *            vn
     * @param bn
     *            bn
     */
    public SurvivalVirtualNetwork(PhysicalNetworkt sn, VirtualNetwork vn, BackupNode bn)
    {
        loggerSurvivalVirtualNetwork.setLevel(Parameter.logLevel);
        PropertyConfigurator.configure("log4j.properties");
        this.virNet = vn;
        this.virNet.surVirNet = this;
        this.isSucceedProtection = false;

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
        surEdge2PhyPath = new Vector<Vector<Vector<Integer>>>();

        for (int i = 0; i < this.nodeSize; i++)
        {
            surEdge2PhyPath.addElement(new Vector<Vector<Integer>>());
            for (int j = 0; j < this.nodeSize; j++)
            {
                surEdge2PhyPath.get(i).addElement(new Vector<Integer>());
            }
        }

        // service
        this.functionNum = vn.functionNum;
        this.boolFunctionTypeSet = new boolean[nodeSize][functionNum];

        // label
        this.node2Label = new String[nodeSize];
        this.label2Node = new HashMap<String, Integer>();

        // embed function
        this.virNode2surNode = new int[this.virNet.nodeSize];
        this.surNode2virNode = new int[this.nodeSize];
        this.surNode2phyNode = new int[this.nodeSize];

        // knapsack problem
        // this.items = new StarStructure[this.virNet.nodeSize];
        this.consumedResource = new ConsumeResource();

        constrcutSurVirNet(sn, vn, bn);
    }

    /**
     * constrcutSurVirNet.
     * 
     * @param vn
     *            vn
     * @param bn
     *            bn
     */
    private void constrcutSurVirNet(PhysicalNetworkt sn, VirtualNetwork vn, BackupNode bn)
    {
        // node
        for (int i = 0; i < this.nodeSize; i++)
        {
            if (i < this.nodeSize4Failure)
            {
                this.nodeComputationCapacity[i] = vn.nodeComputationCapacity[i];
                this.nodeComputationConsume[i] = vn.nodeComputationDemand[i];
                this.surNode2phyNode[i] = vn.virNode2phyNode[i];
                this.virNode2surNode[i] = i;
                this.surNode2virNode[i] = i;

            } else
            {
                this.nodeComputationCapacity[i] = bn.nodeComputationCapacity[i - this.nodeSize4Failure];
                this.nodeComputationConsume[i] = 0;
                this.surNode2phyNode[i] = bn.backNode2phyNode[i - this.nodeSize4Failure];
                this.surNode2virNode[i] = -1;
                if (bn.isPhysicalNodeBoot[i - this.nodeSize4Failure])
                {
                    this.isSubNodeComEmpty[i] = true;
                }
            }
        }
        // edge
        for (int i = 0; i < this.virNet.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if ((vn.edgeBandwithDemand[i][j] > 0))
                {
                    int isurNode = this.virNode2surNode[i];
                    int jsurNode = this.virNode2surNode[j];
                    this.topology[isurNode][jsurNode] = this.topology[jsurNode][isurNode] = true;
                    this.edgeBandwith4Comsume[isurNode][jsurNode] = this.edgeBandwith4Comsume[jsurNode][isurNode] = vn.edgeBandwithDemand[i][j];
                }
            }
        }

        // function
        for (int i = 0; i < this.nodeSize; i++)
        {
            for (int j = 0; j < this.functionNum; j++)
            {
                if (i < this.nodeSize4Failure)
                {
                    this.boolFunctionTypeSet[i][j] = sn.boolFunctionTypeSet[vn.virNode2phyNode[i]][j];
                } else
                {
                    this.boolFunctionTypeSet[i][j] = sn.boolFunctionTypeSet[bn.backNode2phyNode[i
                            - this.nodeSize4Failure]][j];
                }
            }

        }

        // label
        String estr = "Crital_";
        String bstr = "Backup_";
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
                consumedResource.consumedNodeNumber = consumedResource.initNodeNumber;

                consumedResource.initNodeComputation += this.nodeComputationConsume[i];
                consumedResource.consumedNodeComputation = consumedResource.initNodeComputation;
            }
            for (int j = 0; j < i; j++)
            {
                consumedResource.initEdgeBandwith += this.edgeBandwith4Comsume[i][j];
                consumedResource.consumeEdgeBandwith = consumedResource.initEdgeBandwith;

            }
        }

    }

    /**
     * augmentNodeEdge.
     * 
     * @param ithItem2ithKnapsack
     *            solution
     * @param matchMehtod
     *            Method
     */
    public void augmentNodeEdge(int[] ithItem2ithKnapsack, int matchMehtod, StarStructure[] items, Vector<StarStructure> knapsacks)
    {

        int[] virtualNode2NewSurvivalNode = new int[this.virNet.nodeSize];
        if (matchMehtod == Parameter.MatchMethodIP)
        {
            for (int i = 0; i < this.virNet.nodeSize; i++)
            {
                virtualNode2NewSurvivalNode[i] = knapsacks.elementAt(ithItem2ithKnapsack[i]).starNode2SurNetInd;
            }
        }
        if (matchMehtod == Parameter.MatchMethodDP)
        {
            for (int i = 0; i < this.virNet.nodeSize; i++)
            {
                virtualNode2NewSurvivalNode[i] = ithItem2ithKnapsack[i];
            }
        }

        for (int i = 0; i < this.virNet.nodeSize; i++)
        {
            int ithVirNode2ithSurNode = virtualNode2NewSurvivalNode[i];
            // add node computation
            if (this.nodeComputationConsume[ithVirNode2ithSurNode] < items[i].starNodeComputation)
            {
                this.nodeComputationConsume[ithVirNode2ithSurNode] = items[i].starNodeComputation;
            }
            for (int j = 0; j < items[i].neighborEdge.size(); j++)
            {
                int neighborVirNetNode = items[i].neighborEdge.elementAt(j).neighborNode4VirNetInd;
                int neighborVirNetNode2ithSurNode = virtualNode2NewSurvivalNode[neighborVirNetNode];
                //two virtual node do not map one same physical node
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
    public void computeConsumedResource(SeVNAlgorithm alg)
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
        loggerSurvivalVirtualNetwork.info((nodeNum - this.consumedResource.consumedNodeNumber) + " Node + "
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
                surEdge2PhyPath.get(i).get(j).clear();
            }
            surEdge2PhyPath.get(i).clear();
        }
        surEdge2PhyPath.clear();
        label2Node.clear();
        for (int i = 0; i < knapsacks.size(); i++)
        {
            knapsacks.get(i).neighborEdge.clear();
        }
        knapsacks.clear();
    }

}
