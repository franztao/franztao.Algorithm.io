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
	private Logger logger = Logger.getLogger(Algorithm.class);

	public String algorithmName;

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

	public Algorithm() {

		PropertyConfigurator.configure("log4j.properties");
	}

	/**
	 * @param isForceRelease
	 * 
	 */
	public void releaseResource(boolean isForceRelease) {
		for (int i = 0; i < this.sn.VNCollection.size(); i++) {
			if (this.sn.VNCollection.get(i).getIsRunning()) {
				this.sn.VNCollection.get(i).setLeaveTime(this.sn.VNCollection.get(i).getLeaveTime() - 1);
				if (isForceRelease || (0 == this.sn.VNCollection.get(i).getLeaveTime())) {
					releaseVNResource(i);
					this.sn.VNCollection.get(i).setIsRunning(false);
					if (this.sn.VNCollection.get(i).EVN.isSucceed) {
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
			for (int i = 0; i < this.sn.EVNCollection.get(index).nodeSize; i++) {
				if (this.sn.EVNCollection.get(index).nodeComputationEnhanced[i] > 0) {
					this.sn.nodeComputation4EnhanceNoSharedSum[this.sn.EVNCollection
							.get(index).eNode2sNode[i]] -= this.sn.EVNCollection.get(index).nodeComputationEnhanced[i];
				}
			}
		}
		if (!this.isShared) {
			for (int i = 0; i < this.sn.EVNCollection.get(index).nodeSize; i++) {
				for (int j = 0; j < i; j++) {
					if (this.sn.EVNCollection.get(index).edgeBandwithEnhanced[i][j] > 0) {
						for (int p = 0; p < this.sn.EVNCollection.get(index).eEdge2sPath.get(i).get(j).size()
								- 1; p++) {
							int from = this.sn.EVNCollection.get(index).eEdge2sPath.get(i).get(j).get(p);
							int to = this.sn.EVNCollection.get(index).eEdge2sPath.get(i).get(j).get(p + 1);
							this.sn.edgeBandwith4EnhanceNoSharedSum[from][to] -= this.sn.EVNCollection
									.get(index).edgeBandwithEnhanced[i][j];

							this.sn.edgeBandwith4EnhanceNoSharedSum[to][from] = this.sn.edgeBandwith4EnhanceNoSharedSum[from][to];
						}
					}
				}
			}
		}

		this.sn.EVNCollection.get(index).destructerResource();

	}

	/**
	 * @param index
	 * 
	 */
	private void releaseVNResource(int index) {

		// node
		for (int i = 0; i < this.sn.VNCollection.get(index).nodeSize; i++) {
			this.sn.nodeComputation4Former[this.sn.VNCollection.get(index).vNode2sNode[i]] -= this.sn.VNCollection
					.get(index).nodeComputationDemand[i];
		}

		// edge
		for (int i = 0; i < this.sn.VNCollection.get(index).nodeSize; i++) {
			for (int j = 0; j < i; j++) {
				if (this.sn.VNCollection.get(index).edgeBandwithDemand[i][j] > 0) {
					for (int p = 0; p < this.sn.VNCollection.get(index).vEdge2sPath.get(i).get(j).size() - 1; p++) {
						int from = this.sn.VNCollection.get(index).vEdge2sPath.get(i).get(j).get(p);
						int to = this.sn.VNCollection.get(index).vEdge2sPath.get(i).get(j).get(p + 1);
						this.sn.edgeBandwith4Former[from][to] -= this.sn.VNCollection
								.get(index).edgeBandwithDemand[i][j];
						this.sn.edgeBandwith4Former[to][from] = this.sn.edgeBandwith4Former[from][to];
					}
				}
			}
		}

		this.sn.VNCollection.get(index).destructerResource();
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
				int snodeloc = (int) Math.round(Math.random() * (this.sn.nodeSize - 1));
				vn.vNode2sNode[i] = snodeloc;
				// service
				int nodeservice = this.sn.vectorServiceTypeSet.get(snodeloc)
						.elementAt((int) Math.random() * (this.sn.vectorServiceTypeSet.get(snodeloc).size() - 1));
				vn.nodeServiceType[i] = nodeservice;
				// node demand
				vn.nodeComputationDemand[i] = (int) (this.vnp.nodeComputationMinimum + Math
						.round(Math.random() * (this.vnp.nodeComputationMaximum - this.vnp.nodeComputationMinimum)));

				if (vn.nodeComputationDemand[i] > this.sn.getSubstrateRemainComputaion4VN(snodeloc, this.isShared)) {
					logger.warn("Fail to embed virtual network (" + i + ")-th node into substrate network");
					return false;
				} else {
					vn.nodeComputationCapacity[i] = this.sn.nodeComputationCapacity[snodeloc]
							- this.sn.getSubstrateRemainComputaion4VN(snodeloc, this.isShared);
					this.sn.nodeComputation4Temp[snodeloc] += vn.nodeComputationDemand[i];
				}

			}
		}

		// edge
		for (int i = 0; i < vn.nodeSize; i++) {
			for (int j = 0; j < i; j++) {
				if ((vn.isTestSample && vn.topology[i][j])
						|| ((!vn.isTestSample) && (Math.random() < vnp.node2nodeProbability))) {
					int distributeBandwith = 0;
					if (vn.vNode2sNode[i] != vn.vNode2sNode[j]) {

						// exist edge's path,bandwith
						// source destination bandwith[][]
						int[][] bandwith = new int[this.sn.nodeSize][this.sn.nodeSize];
						int[][] topo = new int[this.sn.nodeSize][this.sn.nodeSize];
						for (int k = 0; k < this.sn.nodeSize; k++) {
							for (int l = 0; l < k; l++) {
								int bw = this.sn.getSubStrateRemainBandwith4VN(k, l, this.isShared);
								if (bw > 0) {
									bandwith[k][l] = bandwith[l][k] = bw;
									topo[l][k] = topo[k][l] = 1;
								}
							}
						}

						ShortestPath shortestPath = new ShortestPath(this.sn.nodeSize);
						List<Integer> pathList = new LinkedList<Integer>();
						pathList = shortestPath.Dijkstra(vn.vNode2sNode[i], vn.vNode2sNode[j], topo);
						if (pathList.isEmpty()) {
							logger.warn("Fail to embed virtual network (" + i + " to " + j
									+ ")-edge into substrate network :  substrate network lack feasible path");
							return false;
						}

						// set virtual network's edge bandwith
						int maxPathBandwith = 0;
						vn.vEdge2sPath.get(i).get(j).addElement(pathList.get(0));
						for (int s = pathList.get(0), k = 1; k < pathList.size(); k++) {
							int e = pathList.get(k);
							maxPathBandwith = Math.max(maxPathBandwith, bandwith[s][e]);
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

						if (distributeBandwith > maxPathBandwith) {
							logger.warn("Fail to embed virtual network (" + i + " to " + j
									+ ") edge into substrate network :  substrate network edge resource not more than PathBandwith Demand");

							return false;
						}

						for (int s = pathList.get(0), k = 1; k < pathList.size(); k++) {
							int e = pathList.get(k);
							this.sn.edgeBandwith4Temp[s][e] += distributeBandwith;
							this.sn.edgeBandwith4Temp[e][s] = this.sn.edgeBandwith4Temp[s][e];
							s = e;
						}
					}
					// else {
					//
					// // two adjacent node of the edge is in the same node,
					// // the edge need not any more bandwith.
					// distributeBandwith = (int) (vnp.edgeBandwithMinimum
					// + Math.round(Math.random() * (vnp.edgeBandwithMaximum -
					// vnp.edgeBandwithMinimum)));
					// if (vn.isTestSample) {
					// distributeBandwith = vn.edgeBandwithDemand[i][j];
					// }
					// }
					vn.topology[i][j] = vn.topology[j][i] = true;
					vn.edgeBandwithDemand[i][j] = vn.edgeBandwithDemand[j][i] = distributeBandwith;
				}
			}
		}
		// modify node and edge new value
		for (int i = 0; i < sn.nodeSize; i++) {
			this.sn.nodeComputation4Former[i] += this.sn.nodeComputation4Temp[i];
			this.sn.nodeComputation4Temp[i] = 0;
			for (int j = 0; j < i; j++) {
				if (i != j) {
					this.sn.edgeBandwith4Former[i][j] += this.sn.edgeBandwith4Temp[i][j];
					this.sn.edgeBandwith4Former[j][i] = this.sn.edgeBandwith4Former[i][j];
					this.sn.edgeBandwith4Temp[i][j] = this.sn.edgeBandwith4Temp[j][i] = 0;
				}
			}
		}
		for (int i = 0; i < vn.nodeSize; i++) {
			this.sn.VNQIndexSet4sNode.get(vn.vNode2sNode[i]).addElement(this.sn.VNCollection.size());
			for (int j = 0; j < vn.nodeSize; j++) {
				for (int k = 0; k < vn.vEdge2sPath.get(i).get(j).size() - 1; k++) {
					this.sn.VNQIndexSet4Edge.get(vn.vEdge2sPath.get(i).get(j).get(k))
							.get(vn.vEdge2sPath.get(i).get(j).get(k + 1)).addElement(this.sn.VNCollection.size());
				}
			}
		}

		return true;
	}

	/**
	 * @param vnp
	 */
	public void generateAndEnhanceVNrequest() {
		this.sn.vnqNumber++;
		VirtualNetwork vn = new VirtualNetwork(this.vnp);
		// construct a virtual network
		if (constructVirtualNetwork(vn)) {
			vn.setIndex(this.sn.VNCollection.size());
			vn.setIsRunning(true);
			this.sn.vnSuceedMap++;
			this.sn.VNCollection.addElement(vn);
			logger.info("Succeed to generate (" + (this.sn.VNCollection.size() - 1) + ")-th  virtual network");
		} else {
			this.sn.clearTemp();
			logger.warn("Fail to generate (" + this.sn.VNCollection.size() + ")-th virtual network");
			return;
		}

		// construct backup node
		BackupNode bn = new BackupNode(this.sn, vn, isShared);
		// construct EnhancedVirtualNetwork
		EnhancedVirtualNetwork evn = new EnhancedVirtualNetwork(this.sn, vn, bn);
		vn.EVN = evn;

		if (constructEnhanceVirtualNetwork(evn)) {
			this.sn.evnSuceedMap++;
			logger.info("Succeed to construct enhance network and Algorithm Succeed");
		} else {
			this.sn.clearTemp();
			logger.warn("Fail to construct enhanced network");
		}
		this.sn.EVNCollection.addElement(evn);
		return;
	}

	/**
	 * @param vn
	 * @return
	 */
	private boolean constructEnhanceVirtualNetwork(EnhancedVirtualNetwork evn) {

		if (evn.startEnhanceVirtualNetwork(this.isExact, this.isFD, this.isShared, this.sequence)) {
			if (distributeResource4EVN(evn)) {
				evn.isSucceed = true;
				return true;
			}
		} else {
			logger.info("Algorithm fail to get the solution");
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
				if (nodeResource < this.sn.getSubstrateRemainComputaion4EVN(i, this.isShared)) {
					this.sn.nodeComputation4Temp[i] += nodeResource;
				} else {
					logger.warn("Fail to distribute enhacned network (" + i + ") node into substrate network");

					return false;
				}
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
					tempTopology = new int[this.sn.nodeSize][this.sn.nodeSize];
					for (int k = 0; k < this.sn.nodeSize; k++) {
						for (int l = 0; l < this.sn.nodeSize; l++) {
							int bandwith = this.sn.getSubStrateRemainBandwith4EVN(k, l, this.isShared);
							if (bandwith > evn.edgeBandwithEnhanced[i][j]) {
								tempTopology[k][l] = tempTopology[l][k] = 1;
							}
						}
					}
					ShortestPath shortestPath = new ShortestPath(this.sn.nodeSize);
					List<Integer> pathList = new LinkedList<Integer>();

					// do not split shortest path
					pathList = shortestPath.Dijkstra(evn.eNode2sNode[i], evn.eNode2sNode[j], tempTopology);
					if (pathList.isEmpty()) {
						logger.warn("Fail to embedd enhanced network ("+i+"--"+j+") edge into substrate network: lack path");
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
						this.sn.edgeBandwith4Temp[s][e] += evn.edgeBandwithEnhanced[i][j];
						this.sn.edgeBandwith4Temp[e][s] = this.sn.edgeBandwith4Temp[s][e];
						s = e;
					}
				}
			}
		}

		for (int i = 0; i < evn.nodeSize; i++) {
			this.sn.EVNIndexSet4sNode.get(evn.eNode2sNode[i]).addElement(this.sn.EVNCollection.size());
			for (int j = 0; j < evn.nodeSize; j++) {
				for (int k = 0; k < evn.eEdge2sPath.get(i).get(j).size() - 1; k++) {
					this.sn.EVNIndexSet4Edge.get(evn.eEdge2sPath.get(i).get(j).get(k))
							.get(evn.eEdge2sPath.get(i).get(j).get(k + 1)).addElement(this.sn.EVNCollection.size());
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

		for (int i = 0; i < sn.nodeSize; i++) {
			this.sn.nodeComputation4Temp[i] = 0;
			if (!this.isShared) {
				this.sn.nodeComputation4EnhanceNoSharedSum[i] += this.sn.nodeComputation4Temp[i];
			}
			for (int j = 0; j < i; j++) {
				if (!this.isShared) {
					this.sn.edgeBandwith4EnhanceNoSharedSum[i][j] += this.sn.edgeBandwith4Temp[i][j];
					this.sn.edgeBandwith4EnhanceNoSharedSum[j][i] = this.sn.edgeBandwith4EnhanceNoSharedSum[i][j];
				}
				this.sn.edgeBandwith4Temp[i][j] = this.sn.edgeBandwith4Temp[j][i] = 0;
			}

		}
		return true;

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
	 * 
	 */
	public void isClearAllResource() {

		for (int i = 0; i < this.sn.nodeSize; i++) {
			if (this.sn.nodeComputation4Former[i] > 0) {
				logger.error("substrate (" + i + ") node former resource fail release completely");
				break;
			}
		}

		for (int i = 0; i < this.sn.nodeSize; i++) {
			if (this.sn.nodeComputation4EnhanceNoSharedSum[i] > 0) {
				logger.error("substrate (" + i + ") node enhance resource fail release completely");
				break;
			}
		}

		for (int i = 0; i < this.sn.nodeSize; i++) {
			for (int j = 0; j < i; j++) {
				if (this.sn.edgeBandwith4Former[j][i] > 0) {
					logger.error("Ishared:(" + this.isShared + ") substrate (" + j + "--" + i
							+ ") edge former resource fail release completely");
					break;
				}

			}
		}

		for (int i = 0; i < this.sn.nodeSize; i++) {
			for (int j = 0; j < i; j++) {
				if (this.sn.edgeBandwith4EnhanceNoSharedSum[j][i] > 0) {
					logger.error("Ishared:(" + this.isShared + ") substrate (" + j + "--" + i
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
		this.sn = sn;
		this.isExact = isExact;
		this.isFD = failureindependent;
		this.isShared = isShared;
	}

}
