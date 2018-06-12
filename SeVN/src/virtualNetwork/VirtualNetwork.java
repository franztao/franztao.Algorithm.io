/**
 * 
 */
package virtualNetwork;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import survivabelVirtualNetwork.SurvivalVirtualNetwork;

/**
 * @author franz
 *
 */
public class VirtualNetwork
{
    public boolean isRunning;
    public int index;
    public int leaveTime;
    //node
    public int nodeSize;
    public int[] virNode2phyNode;
    public int[] nodeComputationDemand;
    public int[] nodeComputationCapacity;
    //edge
    public boolean[][] topology;
    public int[][] edgeBandwithDemand;
    public Vector<Vector<Vector<Integer>>> virEdge2PhyPath;
    //function
    public int functionNum;
    public int[] nodeFunctionType;

    public String[] node2Label;
    public Map<String, Integer> label2Node;


    public SurvivalVirtualNetwork surVirNet;

    /**
     * VirtualNetwork.
     * 
     * @param vnp
     *            vnp
     */
    public VirtualNetwork(VirtualNetworkParameter vnp)
    {

        this.nodeSize = vnp.nodeSize;
        this.functionNum = vnp.serviceNumber;
        initializeVariable();

    }

    /**
     * VirtualNetwork.
     * 
     * @param sameVirNet
     *            sameVirNet
     */
    public VirtualNetwork(VirtualNetwork sameVirNet)
    {
        this.nodeSize = sameVirNet.nodeSize;
        this.functionNum = sameVirNet.functionNum;
        initializeVariable();
    }

    /**
     * instantiation.
     */
    private void initializeVariable()
    {
        // node
        this.nodeComputationDemand = new int[nodeSize];
        this.nodeComputationCapacity = new int[nodeSize];
        this.virNode2phyNode = new int[nodeSize];

        //edge
        this.topology = new boolean[nodeSize][nodeSize];
        this.edgeBandwithDemand = new int[nodeSize][nodeSize];
        this.virEdge2PhyPath = new Vector<Vector<Vector<Integer>>>();

        for (int i = 0; i < this.nodeSize; i++)
        {
            virEdge2PhyPath.addElement(new Vector<Vector<Integer>>());
            for (int j = 0; j < this.nodeSize; j++)
            {
                virEdge2PhyPath.get(i).addElement(new Vector<Integer>());
            }
        }

        //FunctionType
        this.nodeFunctionType = new int[nodeSize];

        this.node2Label = new String[nodeSize];
        this.label2Node = new HashMap<String, Integer>();

        String str = "VN_";
        for (int i = 0; i < this.nodeSize; i++)
        {
            this.node2Label[i] = str + (i + 1);
            label2Node.put(str + (i + 1), 0);
        }

    }

    /**
     * 
     */
    public void destructerResource()
    {
        for (int i = 0; i < this.nodeSize; i++)
        {
            for (int j = 0; j < this.nodeSize; j++)
            {
                virEdge2PhyPath.get(i).get(j).clear();

            }
            virEdge2PhyPath.get(i).clear();
        }
        virEdge2PhyPath.clear();
        label2Node.clear();
    }
}
