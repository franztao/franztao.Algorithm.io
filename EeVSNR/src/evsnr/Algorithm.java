/**
 * 
 */
package evsnr;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import standardalgorithm.ShortestPath;
import substratenetwork.BackupNode;
import substratenetwork.SubstrateNetwork;
import virtualnetwork.EnhancedVirtualNetwork;
import virtualnetwork.VirtualNetwork;
import virtualnetwork.VirtualNetworkParameter;

/**
 * @author franz
 *
 */
public class Algorithm {

	public String algorithmName;

	private SubstrateNetwork sn;
	private VirtualNetworkParameter vnp;
	private Vector<VirtualNetwork> VNSet;

	// algorithm
	// NoShared Shared
	// FD FI
	// FD: ILP EVSNR
	// EVSNR: Min Ran
	private boolean isShared;
	private boolean isFD;
	private boolean isExact;
	// 0 Ran 1 Min
	private int sequence;

	/**
	 * 
	 */
	public void releaseResource() {
		for (int i = 0; i < this.VNSet.size(); i++) {
			if (this.VNSet.get(i).getIsRunning()) {
				this.VNSet.get(i).setLeaveTime(this.VNSet.get(i).getLeaveTime() - 1);
				if (0 == this.VNSet.get(i).getLeaveTime()) {
					this.VNSet.get(i).setIsRunning(false);
					// node enhancend node edge enhanced edge
				}
			}
		}
	}

	/**
	 * @param vn
	 * @param vnp
	 * @return
	 */
	private boolean constructVirtualNetwork(VirtualNetwork vn) {
		vn.setLeaveTime((int) (EVSNR.VNRequestsContinueTimeMinimum
				+ Math.random() * (EVSNR.VNRequestsContinueTimeMaximum - EVSNR.VNRequestsContinueTimeMinimum)));

		if (!vn.isTestSample) {
			for (int i = 0; i < vn.nodeSize; i++) {
				// the virtual node map whose phisical node
				int snodeloc = (int) Math.round(Math.random() * this.sn.nodeSize);
				vn.vNode2sNode[i] = snodeloc;
				// service
				int nodeservice = this.sn.vectorServiceTypeSet[snodeloc]
						.elementAt((int) Math.round(Math.random() * this.sn.vectorServiceTypeSet[snodeloc].size()));
				vn.nodeServiceType[i] = nodeservice;
				// node demand
				vn.nodeComputationDemand[i] = (int) (this.vnp.nodeComputationMinimum + Math
						.round(Math.random() * (this.vnp.nodeComputationMaximum - this.vnp.nodeComputationMinimum)));
				if (vn.nodeComputationDemand[i] > (this.sn.nodeComputationCapacity[snodeloc]
						- this.sn.getRemainComputaion(snodeloc, this.isShared))) {
					System.out.println("Fail to generate embedded virtual network's node");
					return false;
				} else {
					vn.nodeComputationCapacity[i] = this.sn.nodeComputationCapacity[snodeloc]
							- this.sn.getRemainComputaion(snodeloc, this.isShared);
					this.sn.nodeComputation4Temp[snodeloc] += vn.nodeComputationDemand[i];
				}
			}
		}

		for (int i = 0; i < vn.nodeSize; i++) {
			for (int j = 0; j < i; j++) {
				if ((vn.isTestSample && vn.topology[i][j])
						|| ((!vn.isTestSample) && (Math.random() < vnp.node2nodeProbability))) {
					int distributeBandwith;
					if (vn.vNode2sNode[i] != vn.vNode2sNode[j]) {

						// exist edge's path,bandwith
						// source destination bandwith[][]
						int[][] bandwith = new int[this.sn.nodeSize][this.sn.nodeSize];
						for (int k = 0; k < this.sn.nodeSize; k++) {
							for (int l = 0; l < this.sn.nodeSize; l++) {
								// bandwith[k][l] =
								// this.sn.edgeBandwithCapacity[k][l]-
								// this.sn.edgeBandwithCurrentConsume[k][l];
								bandwith[k][l] = this.sn.getRemainBandwith(k, l, this.isShared);
							}
						}
						ShortestPath shortestpath = new ShortestPath(this.sn.nodeSize);
						List<Integer> pathlist = new LinkedList<Integer>();
						pathlist = shortestpath.Dijkstra(vn.vNode2sNode[i], vn.vNode2sNode[j], bandwith);
						if (pathlist.isEmpty()) {
							System.out.println("Fail to generate embedded virtual network's edge");
							return false;
						}

						// set virtual network's edge bandwith
						int minimumPathBandwith = 0;
						vn.vEdge2sPath.get(i).get(j).addElement(pathlist.get(0));
						for (int s = pathlist.get(0), k = 1; k < pathlist.size(); k++) {
							int e = pathlist.get(k);
							minimumPathBandwith = Math.max(minimumPathBandwith, bandwith[s][e]);
							vn.vEdge2sPath.get(i).get(j).addElement(e);
							s = e;
						}
						for (int k = pathlist.size() - 1; k >= 0; k--) {
							vn.vEdge2sPath.get(j).get(i).addElement(pathlist.get(k));
						}

						if (vn.isTestSample) {
							distributeBandwith = vn.edgeBandwithDemand[i][j];
						} else {
							distributeBandwith = (int) (vnp.edgeBandwithMinimum
									+ Math.round(Math.random() * (vnp.edgeBandwithMaximum - vnp.edgeBandwithMinimum)));
						}

						if (distributeBandwith > minimumPathBandwith) {
							System.out.println("Fail to generate embedded virtual network's edge");
							return false;
						}

						for (int s = pathlist.get(0), k = 1; k < pathlist.size(); k++) {
							int e = pathlist.get(k);
							this.sn.edgeBandwith4Temp[s][e] += distributeBandwith;
							this.sn.edgeBandwith4Temp[e][s] += distributeBandwith;
							s = e;
						}
					} else {

						// two adjacent node of the edge is in the same node,
						// the edge need not any more bandwith.
						distributeBandwith = (int) (vnp.edgeBandwithMinimum
								+ Math.round(Math.random() * (vnp.edgeBandwithMaximum - vnp.edgeBandwithMinimum)));
						if (vn.isTestSample) {
							distributeBandwith = vn.edgeBandwithDemand[i][j];
						}
					}
					vn.topology[i][j] = true;
					vn.topology[j][i] = true;
					vn.edgeBandwithDemand[i][j] = distributeBandwith;
					vn.edgeBandwithDemand[j][i] = distributeBandwith;
				}
			}
		}
		// set node and edge new value
		for (int i = 0; i < sn.nodeSize; i++) {
			// this.nodeComputationCapacity[nodeloc] -
			// this.nodeComputation4Former[nodeloc]
			// - this.nodeComputation4Enhance_Sum[nodeloc] -
			// this.nodeComputation4Temp[nodeloc];
			this.sn.nodeComputation4Former[i] += this.sn.nodeComputation4Temp[i];
			this.sn.nodeComputation4Temp[i] = 0;
			for (int j = 0; j < sn.nodeSize; j++) {
				if (i != j) {
					// this.edgeBandwithCapacity[k][l] -
					// this.edgeBandwith4Former[k][l]
					// - this.edgeBandwith4Enhance_Sum[k][l] -
					// this.edgeBandwith4Temp[k][l];
					this.sn.edgeBandwith4Former[i][j] += this.sn.edgeBandwith4Temp[i][j];
					this.sn.edgeBandwith4Former[j][i] += this.sn.edgeBandwith4Temp[j][i];
					this.sn.edgeBandwith4Temp[i][j] = this.sn.edgeBandwith4Temp[j][i] = 0;
				}
			}
		}
		for (int i = 0; i < vn.nodeSize; i++) {
			this.sn.VNReqSet4Node.get(vn.vNode2sNode[i]).addElement(this.sn.VNQ.size());
			for (int j = 0; j < vn.nodeSize; j++) {
				for (int k = 0; k < vn.vEdge2sPath.get(i).get(j).size() - 1; k++) {
					this.sn.VNReqSet4Edge.get(vn.vEdge2sPath.get(i).get(j).get(k))
							.get(vn.vEdge2sPath.get(i).get(j).get(k + 1)).addElement(this.sn.VNQ.size());
				}
			}
		}
		this.sn.VNQ.add(vn);

		return true;
	}

	/**
	 * @param vnp
	 */
	public void generateAndEnhanceVNrequest() {
		VirtualNetwork vn = new VirtualNetwork(this.vnp);
		// construct a virtual network
		if (constructVirtualNetwork(vn)) {
			vn.setIndex(VNSet.size());
			VNSet.addElement(vn);
		} else {
			this.sn.clearTemp();
			System.out.println("Fail to generate virtual network");
			return;
		}
		if (constructEnhanceVirtualNetwork(vn)) {
			System.out.println("Succeed to enhance virtual network");
		} else {
			this.sn.clearTemp();
			System.out.println("Fail to enhance virtual network");
		}
		return;
	}

	/**
	 * @param vn
	 * @return
	 */
	private boolean constructEnhanceVirtualNetwork(VirtualNetwork vn) {
		// construct backup node
		BackupNode bn = new BackupNode(this.sn, vn, isShared);
		// construct EnhancedVirtualNetwork
		EnhancedVirtualNetwork evn = new EnhancedVirtualNetwork(this.sn, vn, bn);
		// algorithm
		// NoShared Shared
		// FD FI
		// FD: ILP EVSNR
		// EVSNR: Min Ran
		// private boolean isShared;
		// private boolean isFD;
		// private boolean isExact;
		// // 0 Ran 1 Min
		// private int sequence;
		if (evn.startEnhanceVirtualNetwork(this.isExact, this.isFD, this.isShared, this.sequence)) {
			if (distributeResource4EVN(evn)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param evn
	 * @return
	 */
	private boolean distributeResource4EVN(EnhancedVirtualNetwork evn) {
		for (int i = 0; i < evn.nodeSize; i++) {
			int nodeResource = 0;
			if (i < evn.VN.nodeSize) {
				if (evn.nodeComputationUsed[i] - evn.VN.nodeComputationDemand[i] > 0) {
					nodeResource = evn.nodeComputationUsed[i] - evn.VN.nodeComputationDemand[i];
				}
			} else {
				if (evn.nodeComputationUsed[i] > 0) {
					nodeResource = evn.nodeComputationUsed[i];
				}
			}

			if (nodeResource > 0) {
				int sNodeLoc = evn.VN.vNode2sNode[i];
				if (this.isShared) {

				} else {
					if (nodeResource > this.sn.getRemainComputaion(sNodeLoc, this.isShared)) {
						this.sn.nodeComputation4Temp[sNodeLoc] += nodeResource;
					}
				}
			}
		}

		// set node and edge new value
		for (int i = 0; i < sn.nodeSize; i++) {
			// this.nodeComputationCapacity[nodeloc] -
			// this.nodeComputation4Former[nodeloc]
			// - this.nodeComputation4Enhance_Sum[nodeloc] -
			// this.nodeComputation4Temp[nodeloc];
			this.sn.nodeComputation4Former[i] += this.sn.nodeComputation4Temp[i];
			this.sn.nodeComputation4Temp[i] = 0;
			for (int j = 0; j < vn.nodeSize; j++) {
				if (i != j) {
					// this.edgeBandwithCapacity[k][l] -
					// this.edgeBandwith4Former[k][l]
					// - this.edgeBandwith4Enhance_Sum[k][l] -
					// this.edgeBandwith4Temp[k][l];
					this.sn.edgeBandwith4Former[i][j] += this.sn.edgeBandwith4Temp[i][j];
					this.sn.edgeBandwith4Former[j][i] += this.sn.edgeBandwith4Temp[j][i];
					this.sn.edgeBandwith4Temp[i][j] = this.sn.edgeBandwith4Temp[j][i] = 0;
				}
			}
		}
		return false;

	}

	/**
	 * @return the sn
	 */
	public SubstrateNetwork getSn() {
		return sn;
	}

	/**
	 * @param sn
	 *            the sn to set
	 */
	public void setSn(SubstrateNetwork sn) {
		this.sn = sn;
	}

	/**
	 * @return the isShared
	 */
	public boolean isShared() {
		return isShared;
	}

	/**
	 * @param isShared
	 *            the isShared to set
	 */
	public void setShared(boolean isShared) {
		this.isShared = isShared;
	}

	/**
	 * @return the isFD
	 */
	public boolean isFD() {
		return isFD;
	}

	/**
	 * @param isFD
	 *            the isFD to set
	 */
	public void setFD(boolean isFD) {
		this.isFD = isFD;
	}

	/**
	 * @return the isExact
	 */
	public boolean isExact() {
		return isExact;
	}

	/**
	 * @param isExact
	 *            the isExact to set
	 */
	public void setExact(boolean isExact) {
		this.isExact = isExact;
	}

	/**
	 * @return the sequence
	 */
	public int getSequence() {
		return sequence;
	}

	/**
	 * @param sequence
	 *            the sequence to set
	 */
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the vnp
	 */
	public VirtualNetworkParameter getVnp() {
		return vnp;
	}

	/**
	 * @param vnp
	 *            the vnp to set
	 */
	public void setVnp(VirtualNetworkParameter vnp) {
		this.vnp = vnp;
		this.VNSet = new Vector<VirtualNetwork>();
	}

}
