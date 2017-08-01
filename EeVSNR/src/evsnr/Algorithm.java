/**
 * 
 */
package evsnr;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import standardalgorithm.ShortestPath;
import substratenetwork.SubstrateNetwork;
import virtualnetwork.VirtualNetwork;
import virtualnetwork.VirtualNetworkParameter;

/**
 * @author franz
 *
 */
public class Algorithm {

	private SubstrateNetwork sn;
	private VirtualNetworkParameter vnp;

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
		// TODO Auto-generated method stub

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
	}

	/**
	 * @param vn
	 * @param vnp
	 * @return
	 */
	private boolean constructVirtualNetwork(VirtualNetwork vn) {

		if (!vn.isTestSample) {
			for (int i = 0; i < vn.nodeSize; i++) {
				// the virtual node map whose phisical node
				int nodeloc = (int) Math.round(Math.random() * this.sn.nodeSize);
				vn.vNode2sNode[i] = nodeloc;
				// service
				int nodeservice = this.sn.vectorServiceTypeSet[nodeloc]
						.elementAt((int) Math.round(Math.random() * this.sn.vectorServiceTypeSet[nodeloc].size()));
				vn.nodeServiceType[i] = nodeservice;
				// node demand
				vn.nodeComputationDemand[i] = (int) (this.vnp.nodeComputationMinimum + Math
						.round(Math.random() * (this.vnp.nodeComputationMaximum - this.vnp.nodeComputationMinimum)));
				if (vn.nodeComputationDemand[i] > (this.sn.nodeComputationCapacity[nodeloc]
						- this.sn.getRemainComputaion(nodeloc, this.isShared))) {
					System.out.println("be not able to generate embedded virtual network's node");
					return false;
				} else {
					vn.nodeComputationCurrentConsume[i] = this.sn.nodeComputationCapacity[nodeloc]
							- this.sn.getRemainComputaion(nodeloc, this.isShared);
					this.sn.nodeComputation4Temp[nodeloc] += vn.nodeComputationDemand[i];
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
						ShortestPath sp = new ShortestPath(this.sn.nodeSize);
						List<Integer> pathlist = new LinkedList<Integer>();
						pathlist = sp.Dijkstra(vn.vNode2sNode[i], vn.vNode2sNode[j], bandwith);
						if (pathlist.isEmpty()) {
							System.out.println("be not able to generate embedded virtual network's edge");
							return false;
						}

						// set virtual network's edge bandwith
						// notice
						int minimumPathBandwith = 0;
						for (int s = pathlist.get(0), k = 1; k < pathlist.size(); k++) {
							int e = pathlist.get(k);
							minimumPathBandwith = Math.max(minimumPathBandwith, bandwith[s][e]);
							vn.vEdge2sEdgeSetFrom[i][j].addElement(s);
							vn.vEdge2sEdgeSetTo[i][j].addElement(s);
							s = e;
						}

						if (vn.isTestSample) {
							distributeBandwith = vn.edgeBandwithDemand[i][j];
						} else {
							distributeBandwith = (int) (vnp.edgeBandwithMinimum
									+ Math.round(Math.random() * (vnp.edgeBandwithMaximum - vnp.edgeBandwithMinimum)));
						}

						if (distributeBandwith > minimumPathBandwith) {
							System.out.println("be not able to generate embedded virtual network's edge");
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
		for (int i = 0; i < vn.nodeSize; i++) {
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
					this.sn.edgeBandwith4Temp[i][j] = 0;
					this.sn.edgeBandwith4Temp[j][i] = 0;
				}
			}
		}
		return true;
	}

	/**
	 * @param vnp
	 */
	public void generateVNrequest() {
		VirtualNetwork vn = new VirtualNetwork(this.vnp);
		// construct a virtual network
		if(constructVirtualNetwork(vn)){
			System.out.println("Fail to generate virtual network");
			return ;
		}

	}

}
