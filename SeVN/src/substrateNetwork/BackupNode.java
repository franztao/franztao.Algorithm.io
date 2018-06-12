/**
 * 
 */
package substrateNetwork;

import virtualNetwork.VirtualNetwork;

/**
 * @author franz should simplify the backup node's number notice testsample
 */
public class BackupNode
{

    public int backupNodeSize;
    public int[] backNode2phyNode;
    public boolean[][] boolFunctionTypeSet;
    public int[] nodeComputationCapacity;

    public boolean[] isPhysicalNodeBoot;

    /**
     * BackupNode.
     * 
     * @param subNet
     *            sn
     * @param virNet
     *            vn
     * @param isShared
     *            isShared
     */
    public BackupNode(PhysicalNetworkt subNet, VirtualNetwork virNet, boolean isShared)
    {
        // backup node number;
        boolean[] isUsedNode = new boolean[subNet.nodeSize];
        for (int i = 0; i < virNet.nodeSize; i++)
        {
            isUsedNode[virNet.virNode2phyNode[i]] = true;
        }

        this.backupNodeSize = 0;

        for (int i = 0; i < subNet.nodeSize; i++)
        {
            if (!isUsedNode[i])
            {
                this.backupNodeSize++;
            }
        }

        this.backNode2phyNode = new int[this.backupNodeSize];
        this.boolFunctionTypeSet = new boolean[this.backupNodeSize][subNet.functionNum];
        this.nodeComputationCapacity = new int[this.backupNodeSize];
        this.isPhysicalNodeBoot = new boolean[this.backupNodeSize];

        for (int i = 0, j = 0; i < subNet.nodeSize; i++)
        {
            if (!isUsedNode[i])
            {
                this.backNode2phyNode[j] = i;
                if (subNet.nodeComputationCapacity[i] == subNet.getSubstrateRemainComputaion4VirNet(i, isShared))
                {
                    isPhysicalNodeBoot[j] = true;
                }

                for (int l = 0; l < subNet.functionNum; l++)
                {
                    boolFunctionTypeSet[j][l] = subNet.boolFunctionTypeSet[i][l];
                }

                this.nodeComputationCapacity[j] = subNet.getSubstrateRemainComputaion4SurVirNet(i, isShared);
                j++;
            }
        }
    }

}
