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
		//0 Ran 1 Min 
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
	 * @param sn the sn to set
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
	 * @param isShared the isShared to set
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
	 * @param isFD the isFD to set
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
	 * @param isExact the isExact to set
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
	 * @param sequence the sequence to set
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
	 * @param vnp the vnp to set
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
				int nodeservice = this.sn.vectorServiceTypeSet[nodeloc].elementAt(
						(int) Math.round(Math.random() * this.sn.vectorServiceTypeSet[nodeloc].size()));
				vn.nodeServiceType[i] = nodeservice;
				// node demand
				vn.nodeComputationDemand[i] = (int) (this.vnp.nodeComputationMinimum
						+ Math.round(Math.random() * (this.vnp.nodeComputationMaximum - this.vnp.nodeComputationMinimum)));
				if (vn.nodeComputationDemand[i] > (this.sn.nodeComputationCapacity[nodeloc]
						- this.sn.getRemainComputaion(nodeloc,this.isShared))) {
					System.out.println("be not able to generate embedded virtual network's node");
					return false;
				} else {
					vn.nodeComputationCurrentConsume[i] = this.sn.nodeComputationCapacity[nodeloc]
							- this.sn.getRemainComputaion(nodeloc,this.isShared);
					this.sn.nodeComputation4Temp[nodeloc] += vn.nodeComputationDemand[i];
				}
			}
		}

		for (int i = 0; i < vn.nodeSize; i++) {
			for (int j = 0; j < i; j++) {
				if ((vn.isTestSample && vn.topology[i][j])
						|| ((!vn.isTestSample) && (Math.random() < vnp.node2nodeProbability))) {
					int leavebandwith;
					if (vn.vNode2sNode[i] != vn.vNode2sNode[j]) {

						// exist edge's path,bandwith
						// source destination bandwith[][]
						int[][] bandwith = new int[this.sn.nodeSize][this.sn.nodeSize];
						for (int k = 0; k < this.sn.nodeSize; k++) {
							for (int l = 0; l < this.sn.nodeSize; l++) {
//								bandwith[k][l] = this.sn.edgeBandwithCapacity[k][l]- this.sn.edgeBandwithCurrentConsume[k][l];
								bandwith[k][l]=this.sn.getRemainBandwith(k,l,this.isShared);
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
						leavebandwith = Integer.MAX_VALUE;
						for (int s = pathlist.get(0), k = 1; k <pathlist.size(); k++) {
							int e = pathlist.get(k);
							leavebandwith = Math.min(leavebandwith, bandwith[s][e]);
							vn.vEdge2sEdgeSetFrom[i][j].addElement(s);
							vn.vEdge2sEdgeSetTo[i][j].addElement(s);
							s = e;
						}
						if (vn.isTestSample) {
							leavebandwith = vn.edgeBandwithDemand[i][j];
						}
						for (int s = pathlist.get(0), k = 1; k <pathlist.size(); k++) {
							int e = pathlist.get(k);
							this.sn.edgeBandwith4Temp[s][e] += leavebandwith;
							this.sn.edgeBandwith4Temp[e][s] += leavebandwith;
							s = e;
						}
					} else {

						// two adjacent node of the edge is in the same node,
						// the edge need not any more bandwith.
						leavebandwith = (int) (vnp.edgeBandwithMinimum
								+ Math.round(Math.random() * (vnp.edgeBandwithMaximum - vnp.edgeBandwithMinimum)));
						if (vn.isTestSample) {
							leavebandwith = vn.edgeBandwithDemand[i][j];
						}
					}
					vn.topology[i][j] = true;
					vn.topology[j][i] = true;
					vn.edgeBandwithDemand[i][j] = leavebandwith;
					vn.edgeBandwithDemand[j][i] = leavebandwith;
				}
			}
		}
		//set node and edge new value
		
		return false;
	}
	
	/**
	 * @param vnp
	 */
	public void generateVNrequest() {
		VirtualNetwork vn = new VirtualNetwork(this.vnp);
		// construct a virtual network
		constructVirtualNetwork(vn);
		
	}

}
