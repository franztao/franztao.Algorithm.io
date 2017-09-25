/**
 * 
 */
package substratenetwork;

import java.util.Vector;

import virtualnetwork.VirtualNetwork;
import virtualnetwork.VirtualNetworkParameter;

/**
 * @author franz should simplify the backup node's number notice testsample
 */
public class BackupNode {

  public int backupNodeSize;
  public int[] bNode2sNode;
  public boolean[][] boolServiceTypeSet;
  public int[] nodeComputationCapacity;

  public boolean[] isHaveSubstrateNodeResource4buNode;

  /**
   * @param sn
   *          sn
   * @param vn
   *          vn
   * @param isShared
   *          isShared
   */
  public BackupNode(SubstrateNetwork sn, VirtualNetwork vn, boolean isShared) {
    boolean[] isUsedNode = new boolean[sn.nodeSize];
    for (int i = 0; i < vn.nodeSize; i++) {
      isUsedNode[vn.vNode2sNode[i]] = true;
    }
    // backup node number;
    this.backupNodeSize = 0;
    for (int i = 0; i < sn.nodeSize; i++) {
      if (!isUsedNode[i]) {
        this.backupNodeSize++;
      }
    }

    this.bNode2sNode = new int[this.backupNodeSize];
    this.boolServiceTypeSet = new boolean[this.backupNodeSize][sn.serviceNumber];
    this.nodeComputationCapacity = new int[this.backupNodeSize];
    this.isHaveSubstrateNodeResource4buNode = new boolean[this.backupNodeSize];
    for (int i = 0, j = 0; i < sn.nodeSize; i++) {
      if (!isUsedNode[i]) {
        this.bNode2sNode[j] = i;
        if (sn.nodeComputationCapacity[i] == sn.getSubstrateRemainComputaion4VirNet(i, isShared)) {
          isHaveSubstrateNodeResource4buNode[j] = true;
        }

        for (int l = 0; l < sn.serviceNumber; l++) {
          boolServiceTypeSet[j][l] = sn.boolServiceTypeSet[i][l];
        }
        this.nodeComputationCapacity[j] = sn.getSubstrateRemainComputaion4EVN(i, isShared);
        j++;
      }
    }
  }

}
