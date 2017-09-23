/**
 * 
 */
package evsnr;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

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
	private Logger algorithmLog = Logger.getLogger(Algorithm.class);

	public String algorithmName;
	private SubstrateNetwork SN;
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

	public Algorithm() {
		PropertyConfigurator.configure("log4j.properties");
	}

	/**
	 * @param isForceRelease
	 * 
	 */
	public void releaseResource(boolean isForceRelease) {
		for (int i = 0; i < this.SN.VNCollection.size(); i++) {
			if (this.SN.VNCollection.get(i).getIsRunning()) {
				this.SN.VNCollection.get(i).setLeaveTime(this.SN.VNCollection.get(i).getLeaveTime() - 1);
				if (isForceRelease || (0 == this.SN.VNCollection.get(i).getLeaveTime())) {
					releaseVNResource(i);
					this.SN.VNCollection.get(i).setIsRunning(false);
					if (this.SN.VNCollection.get(i).EVN.isSucceed) {
						releaseEVNResource(i);
					}
				}
			}
		}

	}

	/**
	 * 
	 */
	private void releaseEVNResource(int index) {
		if (!this.isShared) {
			for (int i = 0; i < this.SN.EVNCollection.get(index).nodeSize; i++) {
				if (this.SN.EVNCollection.get(index).nodeComputationEnhanced[i] > 0) {
					this.SN.nodeComputation4EnhanceNoSharedSum[this.SN.EVNCollection
							.get(index).eNode2sNode[i]] -= this.SN.EVNCollection.get(index).nodeComputationEnhanced[i];
				}
			}
		}
		if (!this.isShared) {
			for (int i = 0; i < this.SN.EVNCollection.get(index).nodeSize; i++) {
				for (int j = 0; j < i; j++) {
					if (this.SN.EVNCollection.get(index).edgeBandwithEnhanced[i][j] > 0) {
						for (int p = 0; p < this.SN.EVNCollection.get(index).eEdge2sPath.get(i).get(j).size()
								- 1; p++) {
							int from = this.SN.EVNCollection.get(index).eEdge2sPath.get(i).get(j).get(p);
							int to = this.SN.EVNCollection.get(index).eEdge2sPath.get(i).get(j).get(p + 1);
							this.SN.edgeBandwith4EnhanceNoSharedSum[from][to] -= this.SN.EVNCollection
									.get(index).edgeBandwithEnhanced[i][j];

							this.SN.edgeBandwith4EnhanceNoSharedSum[to][from] = this.SN.edgeBandwith4EnhanceNoSharedSum[from][to];
						}
					}
				}
			}
		}

		this.SN.EVNCollection.get(index).destructerResource();

	}

	/**
	 * @param index
	 * 
	 */
	private void releaseVNResource(int index) {

		// node
		for (int i = 0; i < this.SN.VNCollection.get(index).nodeSize; i++) {
			this.SN.nodeComputation4Former[this.SN.VNCollection.get(index).vNode2sNode[i]] -= this.SN.VNCollection
					.get(index).nodeComputationDemand[i];
		}

		// edge
		for (int i = 0; i < this.SN.VNCollection.get(index).nodeSize; i++) {
			for (int j = 0; j < i; j++) {
				if (this.SN.VNCollection.get(index).edgeBandwithDemand[i][j] > 0) {
					for (int p = 0; p < this.SN.VNCollection.get(index).vEdge2sPath.get(i).get(j).size() - 1; p++) {
						int from = this.SN.VNCollection.get(index).vEdge2sPath.get(i).get(j).get(p);
						int to = this.SN.VNCollection.get(index).vEdge2sPath.get(i).get(j).get(p + 1);
						this.SN.edgeBandwith4Former[from][to] -= this.SN.VNCollection
								.get(index).edgeBandwithDemand[i][j];
						this.SN.edgeBandwith4Former[to][from] = this.SN.edgeBandwith4Former[from][to];
					}
				}
			}
		}

		this.SN.VNCollection.get(index).destructerResource();
	}

	/**
	 * @param VN
	 * @param sameVN
	 * @param vnp
	 * @return
	 */
	private boolean constructVirtualNetwork(VirtualNetwork VN, VirtualNetwork sameVN) {
		VN.setLeaveTime((int) (EVSNR.VNRequestsContinueTimeMinimum
				+ Math.random() * (EVSNR.VNRequestsContinueTimeMaximum - EVSNR.VNRequestsContinueTimeMinimum)));

		// node
		boolean[] isSamesNode = new boolean[this.SN.nodeSize];
		for (int i = 0; i < VN.nodeSize; i++) {
			// the virtual node map whose physical node
			// every virtual node must map different physical node
			int snodeloc;
			if (EVSNR.isSameVNQ4EveryTime) {
				snodeloc = sameVN.vNode2sNode[i];
				VN.nodeServiceType[i] = sameVN.nodeServiceType[i];
				VN.nodeComputationDemand[i] = sameVN.nodeComputationDemand[i];
			} else {
				do {
					snodeloc = (int) Math.round(Math.random() * (this.SN.nodeSize - 1));
					if (!isSamesNode[snodeloc]) {
						VN.vNode2sNode[i] = snodeloc;
						isSamesNode[snodeloc] = true;
						break;
					}
				} while (true);
				// service
				int nodeservice = this.SN.vectorServiceTypeSet.get(snodeloc)
						.elementAt((int) Math.random() * (this.SN.vectorServiceTypeSet.get(snodeloc).size() - 1));
				VN.nodeServiceType[i] = nodeservice;
				// node demand
				VN.nodeComputationDemand[i] = (int) (this.vnp.nodeComputationMinimum + Math
						.round(Math.random() * (this.vnp.nodeComputationMaximum - this.vnp.nodeComputationMinimum)));
			}
			if (VN.nodeComputationDemand[i] > this.SN.getSubstrateRemainComputaion4VN(snodeloc, this.isShared)) {
				algorithmLog.warn("Fail to embed virtual network (" + i + ")-th node into substrate network");
				return false;
			} else {
				VN.nodeComputationCapacity[i] = this.SN.nodeComputationCapacity[snodeloc]
						- this.SN.getSubstrateRemainComputaion4VN(snodeloc, this.isShared);
				this.SN.nodeComputation4Temp[snodeloc] += VN.nodeComputationDemand[i];
			}

		}

		// edge
		for (int i = 0; i < VN.nodeSize; i++) {
			for (int j = 0; j < i; j++) {
				if ((Math.random() < vnp.node2nodeProbability)
						|| (EVSNR.isSameVNQ4EveryTime && sameVN.topology[i][j])) {
					int distributeIthEdgeBandwith = 0;
					if (VN.vNode2sNode[i] != VN.vNode2sNode[j]) {

						// exist edge's path,bandwith
						// source destination bandwith[][]
						int[][] tempBandwith = new int[this.SN.nodeSize][this.SN.nodeSize];
						int[][] tempTopo = new int[this.SN.nodeSize][this.SN.nodeSize];
						for (int k = 0; k < this.SN.nodeSize; k++) {
							for (int l = 0; l < k; l++) {
								int rbw = this.SN.getSubStrateRemainBandwith4VN(k, l, this.isShared);
								if (rbw > 0) {
									tempBandwith[k][l] = tempBandwith[l][k] = rbw;
									tempTopo[l][k] = tempTopo[k][l] = 1;
								}
							}
						}

						ShortestPath shortestPath = new ShortestPath(this.SN.nodeSize);
						List<Integer> pathList = new LinkedList<Integer>();
						pathList = shortestPath.Dijkstra(VN.vNode2sNode[i], VN.vNode2sNode[j], tempTopo);

						if (pathList == null) {
							algorithmLog.warn("Alg: " + this.algorithmName + " Fail to embed VN (" + i + " to " + j
									+ ")-edge into SN : feasible path is null");
							return false;
						}
						if (pathList.isEmpty()) {
							algorithmLog.warn("Alg: " + this.algorithmName + " Fail to embed VN (" + i + " to " + j
									+ ")-edge into SN : feasible path is is empty");
							return false;
						}
						// set virtual network's edge bandwith
						int pathMaximumBandwith = 0;
						VN.vEdge2sPath.get(i).get(j).addElement(pathList.get(0));
						for (int s = pathList.get(0), k = 1; k < pathList.size(); k++) {
							int e = pathList.get(k);
							pathMaximumBandwith = Math.max(pathMaximumBandwith, tempBandwith[s][e]);
							VN.vEdge2sPath.get(i).get(j).addElement(e);
							s = e;
						}
						for (int k = pathList.size() - 1; k >= 0; k--) {
							VN.vEdge2sPath.get(j).get(i).addElement(pathList.get(k));
						}
						if (EVSNR.isSameVNQ4EveryTime) {
							distributeIthEdgeBandwith = VN.edgeBandwithDemand[i][j];
						} else {
							distributeIthEdgeBandwith = (int) (vnp.edgeBandwithMinimum
									+ Math.round(Math.random() * (vnp.edgeBandwithMaximum - vnp.edgeBandwithMinimum)));
						}
						if (distributeIthEdgeBandwith > pathMaximumBandwith) {
							algorithmLog.warn("Alg: " + this.algorithmName + " Fail to embed VN (" + i + " to " + j
									+ ") edge into SN :SN edge resource not more than PathBandwith Demand");
							return false;
						}

						for (int s = pathList.get(0), k = 1; k < pathList.size(); k++) {
							int e = pathList.get(k);
							this.SN.edgeBandwith4Temp[s][e] += distributeIthEdgeBandwith;
							this.SN.edgeBandwith4Temp[e][s] = this.SN.edgeBandwith4Temp[s][e];
							s = e;
						}
					}
					VN.topology[i][j] = VN.topology[j][i] = true;
					VN.edgeBandwithDemand[i][j] = VN.edgeBandwithDemand[j][i] = distributeIthEdgeBandwith;
				}
			}
		}

		// modify node and edge new value
		for (int i = 0; i < SN.nodeSize; i++) {
			this.SN.nodeComputation4Former[i] += this.SN.nodeComputation4Temp[i];
			this.SN.nodeComputation4Temp[i] = 0;
			for (int j = 0; j < i; j++) {
				if (i != j) {
					this.SN.edgeBandwith4Former[i][j] += this.SN.edgeBandwith4Temp[i][j];
					this.SN.edgeBandwith4Former[j][i] = this.SN.edgeBandwith4Former[i][j];
					this.SN.edgeBandwith4Temp[i][j] = this.SN.edgeBandwith4Temp[j][i] = 0;
				}
			}
		}
		for (int i = 0; i < VN.nodeSize; i++) {
			this.SN.VNQIndexSet4sNode.get(VN.vNode2sNode[i]).addElement(this.SN.VNCollection.size());
			for (int j = 0; j < VN.nodeSize; j++) {
				for (int k = 0; k < VN.vEdge2sPath.get(i).get(j).size() - 1; k++) {
					this.SN.VNQIndexSet4Edge.get(VN.vEdge2sPath.get(i).get(j).get(k))
							.get(VN.vEdge2sPath.get(i).get(j).get(k + 1)).addElement(this.SN.VNCollection.size());
				}
			}
		}
		return true;
	}

	/**
	 * @param sameVN
	 * @param vnp
	 */
	public void generateAndEnhanceVNrequest(VirtualNetwork sameVN) {
		this.SN.vnqNumber++;
		VirtualNetwork VN = new VirtualNetwork(this.vnp, sameVN);
		// construct a virtual network
		if (constructVirtualNetwork(VN, sameVN)) {
			VN.setIndex(this.SN.VNCollection.size());
			VN.setIsRunning(true);
			this.SN.VNSuceedEmbedSum++;
			this.SN.VNCollection.addElement(VN);
			algorithmLog.info("Alg: " + this.algorithmName + " Succeed to generate ("
					+ (this.SN.VNCollection.size() - 1) + ")-th  VN");
		} else {
			this.SN.clearTempResource();
			algorithmLog.warn(
					"Alg: " + this.algorithmName + " Fail to generate (" + this.SN.VNCollection.size() + ")-th VN");
			return;
		}

		// construct backup node
		BackupNode BN = new BackupNode(this.SN, VN, this.isShared);
		// construct EnhancedVirtualNetwork
		EnhancedVirtualNetwork EVN = new EnhancedVirtualNetwork(this.SN, VN, BN);
		VN.EVN = EVN;

		if (constructEnhanceVirtualNetwork(EVN)) {
			this.SN.EVNSuceedEmbedSum++;
			algorithmLog.info("Alg: " + this.algorithmName + " Succeed to construct EVN and embed EVN");
		} else {
			this.SN.clearTempResource();
			algorithmLog.warn("Alg: " + this.algorithmName + " Fail to construct EVN");
		}
		this.SN.EVNCollection.addElement(EVN);
		return;
	}

	/**
	 * @param vn
	 * @return
	 */
	private boolean constructEnhanceVirtualNetwork(EnhancedVirtualNetwork EVN) {

		if (EVN.startEnhanceVirtualNetwork(this.isExact, this.isFD, this.sequence)) {
			if (distributeResource4EVN(EVN)) {
				EVN.isSucceed = true;
				algorithmLog.info("Alg: " + this.algorithmName + " Succeed distribute enough resource 4 EVN");
				return true;
			} else {
				if (EVSNR.IsReleaseVNafterEVNFailure) {
					this.SN.VNCollection.get(this.SN.VNCollection.size() - 1).setLeaveTime(1);
				}
				algorithmLog.info("Alg: " + this.algorithmName + " Fail distribute enough resource 4 EVN");
			}
		} else {
			algorithmLog.info("Alg: " + this.algorithmName + " Algorithm Fail to obtain the solution of EVN");
		}
		return false;
	}

	/**
	 * @param evn
	 * @return
	 */
	private boolean distributeResource4EVN(EnhancedVirtualNetwork evn) {
		for (int i = 0; i < this.SN.nodeSize; i++) {
			int nodeResource = 0;
			for (int j = 0; j < evn.nodeSize; j++) {
				if (i == evn.eNode2sNode[j]) {
					if (j < evn.VN.nodeSize) {
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
				if (nodeResource < this.SN.getSubstrateRemainComputaion4EVN(i, this.isShared)) {
					this.SN.nodeComputation4Temp[i] += nodeResource;
				} else {
					algorithmLog.warn("Fail to distribute enhacned network (" + i + ") node into substrate network");
					return false;
				}
			}
			if (nodeResource < 0) {
				algorithmLog.error("distributeResource4EVN nodeResource less than zero ");
			}

		}

		for (int i = 0; i < evn.nodeSize; i++) {
			for (int j = 0; j < evn.nodeSize; j++) {
				if (i < evn.nodeSize4Embeded && j < evn.nodeSize4Embeded) {

					evn.edgeBandwithEnhanced[i][j] = evn.edgeBandwithUsed[i][j] - evn.VN.edgeBandwithDemand[i][j];
				} else {
					evn.edgeBandwithEnhanced[i][j] = evn.edgeBandwithUsed[i][j];
				}
			}
		}
		// edge
		for (int i = 0; i < evn.nodeSize; i++) {
			for (int j = 0; j < i; j++) {
				if (evn.edgeBandwithEnhanced[i][j] > 0) {
					int tempTopology[][];
					tempTopology = new int[this.SN.nodeSize][this.SN.nodeSize];
					for (int k = 0; k < this.SN.nodeSize; k++) {
						for (int l = 0; l < this.SN.nodeSize; l++) {
							int bandwith = this.SN.getSubStrateRemainBandwith4EVN(k, l, this.isShared);
							if (bandwith > evn.edgeBandwithEnhanced[i][j]) {
								tempTopology[k][l] = tempTopology[l][k] = 1;
							}
						}
					}

					ShortestPath shortestPath = new ShortestPath(this.SN.nodeSize);
					List<Integer> pathList = new LinkedList<Integer>();

					// do not split shortest path
					pathList = shortestPath.Dijkstra(evn.eNode2sNode[i], evn.eNode2sNode[j], tempTopology);
					if (pathList == null) {
						algorithmLog.warn("Fail to embedd enhanced network (" + i + "--" + j
								+ ") edge into substrate network: null value");
						return false;
					}
					if (pathList.isEmpty()) {
						algorithmLog.warn("Fail to embedd enhanced network (" + i + "--" + j
								+ ") edge into substrate network: lack path");
						return false;
					}

					// set virtual network's edge bandwith
					for (int k = 0; k < pathList.size(); k++) {
						evn.eEdge2sPath.get(i).get(j).addElement(pathList.get(k));
					}
					for (int k = pathList.size() - 1; k >= 0; k--) {
						evn.eEdge2sPath.get(j).get(i).addElement(pathList.get(k));
					}

					for (int s = pathList.get(0), k = 1; k < pathList.size(); k++) {
						int e = pathList.get(k);
						this.SN.edgeBandwith4Temp[s][e] += evn.edgeBandwithEnhanced[i][j];
						this.SN.edgeBandwith4Temp[e][s] = this.SN.edgeBandwith4Temp[s][e];
						s = e;
					}
				} else {
					if (evn.edgeBandwithEnhanced[i][j] < 0) {
						algorithmLog.error("distributeResource4EVN edgeResource less than zero ");
						return false;
					}
				}
			}
		}

		for (int i = 0; i < evn.nodeSize; i++) {
			this.SN.EVNIndexSet4sNode.get(evn.eNode2sNode[i]).addElement(this.SN.EVNCollection.size());
			for (int j = 0; j < evn.nodeSize; j++) {
				for (int k = 0; k < evn.eEdge2sPath.get(i).get(j).size() - 1; k++) {
					this.SN.EVNIndexSet4Edge.get(evn.eEdge2sPath.get(i).get(j).get(k))
							.get(evn.eEdge2sPath.get(i).get(j).get(k + 1)).addElement(this.SN.EVNCollection.size());
				}
			}
		}

		// set node and edge new value
		for (int i = 0; i < evn.nodeSize; i++) {
			if (i < evn.VN.nodeSize) {
				evn.nodeComputationEnhanced[i] = evn.nodeComputationUsed[i] - evn.VN.nodeComputationDemand[i];
			} else {
				evn.nodeComputationEnhanced[i] = evn.nodeComputationUsed[i];
			}
		}

		for (int i = 0; i < SN.nodeSize; i++) {
			if (!this.isShared) {
				this.SN.nodeComputation4EnhanceNoSharedSum[i] += this.SN.nodeComputation4Temp[i];
			}
			this.SN.nodeComputation4Temp[i] = 0;
			for (int j = 0; j < i; j++) {
				if (!this.isShared) {
					this.SN.edgeBandwith4EnhanceNoSharedSum[i][j] += this.SN.edgeBandwith4Temp[i][j];
					this.SN.edgeBandwith4EnhanceNoSharedSum[j][i] = this.SN.edgeBandwith4EnhanceNoSharedSum[i][j];
				}
				this.SN.edgeBandwith4Temp[i][j] = this.SN.edgeBandwith4Temp[j][i] = 0;
			}

		}
		return true;

	}

	/**
	 * @return the sn
	 */
	public SubstrateNetwork getSn() {
		return SN;
	}

	/**
	 * @param sn
	 *            the sn to set
	 */
	public void setSn(SubstrateNetwork sn) {
		this.SN = sn;
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
	 * 
	 */
	public void isClearAllResource() {

		for (int i = 0; i < this.SN.nodeSize; i++) {
			if (this.SN.nodeComputation4Former[i] > 0) {
				algorithmLog.error("substrate (" + i + ") node former resource fail release completely");
				break;
			}
		}

		for (int i = 0; i < this.SN.nodeSize; i++) {
			if (this.SN.nodeComputation4EnhanceNoSharedSum[i] > 0) {
				algorithmLog.error("substrate (" + i + ") node enhance resource fail release completely");
				break;
			}
		}

		for (int i = 0; i < this.SN.nodeSize; i++) {
			for (int j = 0; j < i; j++) {
				if (this.SN.edgeBandwith4Former[j][i] > 0) {
					algorithmLog.error("Ishared:(" + this.isShared + ") substrate (" + j + "--" + i
							+ ") edge former resource fail release completely");
					break;
				}

			}
		}

		for (int i = 0; i < this.SN.nodeSize; i++) {
			for (int j = 0; j < i; j++) {
				if (this.SN.edgeBandwith4EnhanceNoSharedSum[j][i] > 0) {
					algorithmLog.error("Ishared:(" + this.isShared + ") substrate (" + j + "--" + i
							+ ") edge enhance resource fail release completely");
					break;
				}

			}
		}

	}

	/**
	 * @param string
	 * @param sn_FI_NoShared
	 * @param isExact
	 * @param failureindependent
	 * @param isShared
	 */
	public void setParameter(String algName, SubstrateNetwork sn, boolean isExact, boolean failureindependent,
			boolean isShared) {
		this.algorithmName = algName;
		this.SN = sn;
		this.isExact = isExact;
		this.isFD = failureindependent;
		this.isShared = isShared;
	}

}
