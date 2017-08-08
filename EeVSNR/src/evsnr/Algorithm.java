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

	public Algorithm() {
		this.VNSet = new Vector<VirtualNetwork>();
	}

	/**
	 * 
	 */
	public void releaseResource() {
		for (int i = 0; i < this.VNSet.size(); i++) {
			if (this.VNSet.get(i).getIsRunning()) {
				this.VNSet.get(i).setLeaveTime(this.VNSet.get(i).getLeaveTime() - 1);
				if (0 == this.VNSet.get(i).getLeaveTime()) {
					releaseVNResource();
					this.VNSet.get(i).setIsRunning(false);
					if (this.VNSet.get(i).evn.isSucceed) {
						releaseEVNResource();
					}
				}
			}
		}

		// release edge resource
	}

	/**
	 * 
	 */
	private void releaseEVNResource() {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 */
	private void releaseVNResource() {
		// TODO Auto-generated method stub

	}

	/**
	 * @param vn
	 * @param vnp
	 * @return
	 */
	private boolean constructVirtualNetwork(VirtualNetwork vn) {
		vn.setLeaveTime((int) (EVSNR.VNRequestsContinueTimeMinimum
				+ Math.random() * (EVSNR.VNRequestsContinueTimeMaximum - EVSNR.VNRequestsContinueTimeMinimum)));

		// node
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
					System.out.println("Fail to embed virtual network's node into substrate network");
					return false;
				} else {
					vn.nodeComputationCapacity[i] = this.sn.nodeComputationCapacity[snodeloc]
							- this.sn.getRemainComputaion(snodeloc, this.isShared);
					this.sn.nodeComputation4Temp[snodeloc] += vn.nodeComputationDemand[i];
				}

			}
		}

		// edge
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
							for (int l = 0; l < k; l++) {
								bandwith[k][l] = this.sn.getRemainBandwith(k, l, this.isShared);
								bandwith[l][k] = bandwith[k][l];
							}
						}

						ShortestPath shortestPath = new ShortestPath(this.sn.nodeSize);
						List<Integer> pathList = new LinkedList<Integer>();
						pathList = shortestPath.Dijkstra(vn.vNode2sNode[i], vn.vNode2sNode[j], bandwith);
						if (pathList.isEmpty()) {
							System.out
									.println("Fail to embedd virtual network's edge into substrate network: lack path");
							return false;
						}

						// set virtual network's edge bandwith
						int minimumPathBandwith = 0;
						vn.vEdge2sPath.get(i).get(j).addElement(pathList.get(0));
						for (int s = pathList.get(0), k = 1; k < pathList.size(); k++) {
							int e = pathList.get(k);
							minimumPathBandwith = Math.max(minimumPathBandwith, bandwith[s][e]);
							vn.vEdge2sPath.get(i).get(j).addElement(e);
							s = e;
						}
						for (int k = pathList.size() - 1; k >= 0; k--) {
							vn.vEdge2sPath.get(j).get(i).addElement(pathList.get(k));
						}

						if (vn.isTestSample) {
							distributeBandwith = vn.edgeBandwithDemand[i][j];
						} else {
							distributeBandwith = (int) (vnp.edgeBandwithMinimum
									+ Math.round(Math.random() * (vnp.edgeBandwithMaximum - vnp.edgeBandwithMinimum)));
						}

						if (distributeBandwith > minimumPathBandwith) {
							System.out.println(
									"Fail to embedd virtual network's edge into substrate network: substrate resource lack");
							return false;
						}

						for (int s = pathList.get(0), k = 1; k < pathList.size(); k++) {
							int e = pathList.get(k);
							this.sn.edgeBandwith4Temp[s][e] += distributeBandwith;
							this.sn.edgeBandwith4Temp[e][s] = this.sn.edgeBandwith4Temp[s][e];
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
					vn.topology[i][j] = vn.topology[j][i] = true;
					vn.edgeBandwithDemand[i][j] = vn.edgeBandwithDemand[j][i] = distributeBandwith;
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
			for (int j = 0; j < i; j++) {
				if (i != j) {
					// this.edgeBandwithCapacity[k][l] -
					// this.edgeBandwith4Former[k][l]
					// - this.edgeBandwith4Enhance_Sum[k][l] -
					// this.edgeBandwith4Temp[k][l];
					this.sn.edgeBandwith4Former[i][j] += this.sn.edgeBandwith4Temp[i][j];
					this.sn.edgeBandwith4Former[j][i] = this.sn.edgeBandwith4Former[i][j];
					this.sn.edgeBandwith4Temp[i][j] = this.sn.edgeBandwith4Temp[j][i] = 0;
				}
			}
		}
		for (int i = 0; i < vn.nodeSize; i++) {
			this.sn.VNQIndexSet4sNode.get(vn.vNode2sNode[i]).addElement(this.sn.VNQ.size());
			for (int j = 0; j < vn.nodeSize; j++) {
				for (int k = 0; k < vn.vEdge2sPath.get(i).get(j).size() - 1; k++) {
					this.sn.VNQIndexSet4Edge.get(vn.vEdge2sPath.get(i).get(j).get(k))
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
			vn.setIndex(this.VNSet.size());
			this.VNSet.addElement(vn);
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
		
		for (int i = 0; i < this.sn.nodeSize; i++) {
			int nodeResource = 0;
			for (int j = 0; j < evn.nodeSize; j++) {
				if (i == evn.eNode2sNode[j]) {
					if (i < evn.VN.nodeSize) {
						if (evn.nodeComputationUsed[j] - evn.VN.nodeComputationDemand[j] > 0) {
							nodeResource += evn.nodeComputationUsed[j] - evn.VN.nodeComputationDemand[j];
						}
					} else {
						if (evn.nodeComputationUsed[j] > 0) {
							nodeResource += evn.nodeComputationUsed[j];
						}
					}
				}
			}
			if (nodeResource > 0) {
				if (nodeResource > this.sn.getRemainComputaion(i, this.isShared)) {
					this.sn.nodeComputation4Temp[i] += nodeResource;
				} else {
					System.out.println("Fail to distribute enhacned node");
					return false;
				}
			}
		}

		for(int i=0;i<evn.nodeSize;i++){
			for(int j=0;j<evn.nodeSize;j++){
				evn.edgeBandwithEnhanced[i][j]=evn.edgeBandwithUsed[i][j];
				if(i<evn.nodeSize4Embeded&&j<evn.nodeSize4Embeded){
					evn.edgeBandwithEnhanced[i][j]=evn.edgeBandwithUsed[i][j]-evn.VN.edgeBandwithDemand[i][j];
				}
			}
		}
		//edge
		for(int i=0;i<evn.nodeSize;i++){
			for(int j=0;j<i;j++){
				if(evn.edgeBandwithEnhanced[i][j]>0){
					int tempBandwith[][];
					tempBandwith=new int[this.sn.nodeSize][this.sn.nodeSize];
					for(int k=0;k<this.sn.nodeSize;k++){
						for(int l=0;l<this.sn.nodeSize;l++){
							tempBandwith[k][l] = this.sn.getRemainBandwith(k, l, this.isShared);
							tempBandwith[l][k] = tempBandwith[k][l];
						}
					}
					ShortestPath shortestPath = new ShortestPath(this.sn.nodeSize);
					List<Integer> pathList = new LinkedList<Integer>();
					pathList = shortestPath.Dijkstra(evn.eNode2sNode[i], evn.eNode2sNode[j], tempBandwith);
					if (pathList.isEmpty()) {
						System.out
								.println("Fail to embedd virtual network's edge into substrate network: lack path");
						return false;
					}

					// set virtual network's edge bandwith
					int minimumPathBandwith = 0;
					vn.vEdge2sPath.get(i).get(j).addElement(pathList.get(0));
					for (int s = pathList.get(0), k = 1; k < pathList.size(); k++) {
						int e = pathList.get(k);
						minimumPathBandwith = Math.max(minimumPathBandwith, bandwith[s][e]);
						vn.vEdge2sPath.get(i).get(j).addElement(e);
						s = e;
					}
					for (int k = pathList.size() - 1; k >= 0; k--) {
						vn.vEdge2sPath.get(j).get(i).addElement(pathList.get(k));
					}

					if (vn.isTestSample) {
						distributeBandwith = vn.edgeBandwithDemand[i][j];
					} else {
						distributeBandwith = (int) (vnp.edgeBandwithMinimum
								+ Math.round(Math.random() * (vnp.edgeBandwithMaximum - vnp.edgeBandwithMinimum)));
					}

					if (distributeBandwith > minimumPathBandwith) {
						System.out.println(
								"Fail to embedd virtual network's edge into substrate network: substrate resource lack");
						return false;
					}

					for (int s = pathList.get(0), k = 1; k < pathList.size(); k++) {
						int e = pathList.get(k);
						this.sn.edgeBandwith4Temp[s][e] += distributeBandwith;
						this.sn.edgeBandwith4Temp[e][s] = this.sn.edgeBandwith4Temp[s][e];
						s = e;
					}
						}
					}
				}else{
					System.out.println("Error : enhance resource");
				}
			
			}
			
		}
		
		evn.isSucceed = true;
		this.sn.ENV.addElement(evn);
		// set node and edge new value
		for (int i = 0; i < evn.nodeSize; i++) {
			if (i < evn.VN.nodeSize) {
				evn.nodeComputationEnhanced[i] = evn.nodeComputationUsed[i] - evn.VN.nodeComputationDemand[i];
			} else {
				evn.nodeComputationEnhanced[i] = evn.nodeComputationUsed[i];
			}
		}
		
		for (int i = 0; i < sn.nodeSize; i++) {
			if (!this.isShared) {
				this.sn.nodeComputation4Enhance_Sum[i] += this.sn.nodeComputation4Temp[i];
			}
			this.sn.nodeComputation4Temp[i] = 0;
			for (int j = 0; j < i; j++) {
					this.sn.edgeBandwith4Enhance_Sum[i][j] += this.sn.edgeBandwith4Temp[i][j];
					this.sn.edgeBandwith4Enhance_Sum[j][i] =this.sn.edgeBandwith4Former[i][j];
					this.sn.edgeBandwith4Temp[i][j] = this.sn.edgeBandwith4Temp[j][i] = 0;
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

	}

}
