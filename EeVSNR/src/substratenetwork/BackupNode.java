/**
 * 
 */
package substratenetwork;

import java.util.Vector;

import virtualnetwork.VirtualNetwork;
import virtualnetwork.VirtualNetworkParameter;

/**
 * @author franz
 * should simplify the backup node's number
 * notice testsample 
 */
public class BackupNode {
	/**
	 * @param fdSubstrateNework
	 * @param vn
	 */
	public BackupNode(SubstrateNetwork fdSubstrateNework, VirtualNetwork vn) {
		boolean usedVnode[] = new boolean[fdSubstrateNework.nodeSize];
		for (int i = 0; i < vn.nodeSize; i++) {
			usedVnode[vn.vNode2sNode[i]] = true;
		}
		// backup node number;
		this.backupNodeSize = 0;
		for (int i = 0; i < fdSubstrateNework.nodeSize; i++) {
			if (!usedVnode[i]) {
				this.backupNodeSize++;
			}
		}

		this.bNode2sNode = new int[this.backupNodeSize];
		this.boolServiceTypeSet = new boolean[this.backupNodeSize][fdSubstrateNework.serviceNumber];
		this.nodeComputationCapacity = new int[this.backupNodeSize];
		for (int i = 0, j = 0; i < fdSubstrateNework.nodeSize; i++) {
			if (!usedVnode[i]) {
				this.bNode2sNode[j] = i;
				for (int l = 0; l < fdSubstrateNework.serviceNumber; l++) {
					boolServiceTypeSet[j][l] = fdSubstrateNework.boolServiceTypeSet[i][l];
				}
//				this.nodeComputationCapacity[j] = fdSubstrateNework.nodeComputationCapacity[i]
//						- fdSubstrateNework.nodeComputationCurrentConsume[i];
				j++;
			}
		}
	}

	public int backupNodeSize;
	public int[] bNode2sNode;
	public boolean[][] boolServiceTypeSet;
	public int[] nodeComputationCapacity;
}
