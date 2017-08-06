/**
 * 
 */
package evsnr;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import gurobi.GRBException;
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
public class DynamicVritualNetworkRequest implements Runnable {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#run()
	 */
	private SubstrateNetwork FDSubstrateNework;
	private VirtualNetworkParameter vnp;
	private final ReentrantLock synchronizeLock;
	private boolean isbackupResourceShared;
	private int algorithmType;

	public DynamicVritualNetworkRequest(SubstrateNetwork fDSubstrateNework2, VirtualNetworkParameter vnp2,
			boolean isbackupResourceShared, int algorithmType) {
		this.FDSubstrateNework = fDSubstrateNework2;
		this.vnp = vnp2;
		this.synchronizeLock = new ReentrantLock();
		this.isbackupResourceShared = isbackupResourceShared;
		this.algorithmType = algorithmType;
	}

	@Override
	public void run() {
		generateEnhancedVritualNetwork();
		try {
			Thread.sleep(EVSNR.VNRequestsDuration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		releaseResource();

	}

	/**
	 * 
	 */
	private void releaseResource() {
		// TODO Auto-generated method stub
		// former node consumption
		// augment node consumption
		// former edge bandwith
		// augment edge bandwith
	}

	/**
	 * 
	 */
	private void generateEnhancedVritualNetwork() {
		VirtualNetwork vn = new VirtualNetwork(this.vnp);
		Vector<Vector<LinkedList<Integer>>> path;
		// construct a virtual network
		this.synchronizeLock.lock();
		try {
			path = constructVirtualNetwork(vn);
		} finally {
			this.synchronizeLock.unlock();
		}

		if (path == null) {
			System.out.println("Warning: ＣonstructVirtualNetwork failure");
			return;
		} else {
			this.synchronizeLock.lock();
			try {
				// construct backup node
//				BackupNode bn = new BackupNode(this.FDSubstrateNework, vn);
				// construct EnhancedVirtualNetwork
				EnhancedVirtualNetwork evn = null ;//= new EnhancedVirtualNetwork(FDSubstrateNework, vn, bn, path);

				if (this.algorithmType == EVSNR.IPAlgorithm) {
					evn.OptimalAlgorithmIP4Survivability();
				}
				if (this.algorithmType == EVSNR.FailureDependentHeuriticAlgorithm) {
					evn.computeItems();
					boolean result;
//						result = evn.HeursitcAlgorithm4Survivability(evn.nodeSize4Embeded, EVSNR.FailureDependent);
				}
				if (this.algorithmType == EVSNR.FailureIndependentHeuriticAlgorithm) {
					evn.computeItems();
//						evn.HeursitcAlgorithm4Survivability(evn.nodeSize4Embeded, EVSNR.FailureIndependent);
				}
				// distribute enhanced network
			} finally {
				this.synchronizeLock.unlock();
			}
		}
		System.out.println("------------------" + "----------------");
//		this.FDSubstrateNework.vnquest.addElement(vn);

	}

	/**
	 * @param vn
	 * @param vnp
	 * @return
	 */
	private Vector<Vector<LinkedList<Integer>>> constructVirtualNetwork(VirtualNetwork vn) {

		Vector<Vector<LinkedList<Integer>>> path = new Vector<Vector<LinkedList<Integer>>>();
		for (int i = 0; i < vn.nodeSize; i++) {
			Vector<LinkedList<Integer>> e = new Vector<LinkedList<Integer>>();
			path.add(e);
		}
		for (int i = 0; i < vn.nodeSize; i++) {
			for (int j = 0; j < vn.nodeSize; j++) {
				LinkedList<Integer> e = new LinkedList<Integer>();
				path.get(i).add(e);
			}
		}
		if (!vn.isTestSample) {
			for (int i = 0; i < vn.nodeSize; i++) {
				// the virtual node map whose phisical node
				int nodeloc = (int) Math.round(Math.random() * this.FDSubstrateNework.nodeSize);
				vn.vNode2sNode[i] = nodeloc;
				// service
				int nodeservice = this.FDSubstrateNework.vectorServiceTypeSet[nodeloc].elementAt(
						(int) Math.round(Math.random() * this.FDSubstrateNework.vectorServiceTypeSet[nodeloc].size()));
				vn.nodeServiceType[i] = nodeservice;
				// node demand
				vn.nodeComputationDemand[i] = (int) (vnp.nodeComputationMinimum
						+ Math.round(Math.random() * (vnp.nodeComputationMaximum - vnp.nodeComputationMinimum)));
//				if (vn.nodeComputationDemand[i] > (this.FDSubstrateNework.nodeComputationCapacity[nodeloc]
//						- this.FDSubstrateNework.nodeComputationCurrentConsume[nodeloc])) {
//					System.out.println("be not able to generate embedded virtual network's node");
//					return null;
//				} else {
//					vn.nodeComputationCurrentConsume[i] = this.FDSubstrateNework.nodeComputationCapacity[i]
//							- this.FDSubstrateNework.nodeComputationCurrentConsume[i];
//					this.FDSubstrateNework.nodeComputationCurrentConsume[i] += vn.nodeComputationDemand[i];
//				}
			}
		}

		for (int i = 0; i < vn.nodeSize; i++) {
			for (int j = 0; j < i; j++) {
				if ((vn.isTestSample && vn.topology[i][j])
						|| ((!vn.isTestSample) && (Math.random() < vnp.node2nodeProbability))) {
					int bw;
					if (vn.vNode2sNode[i] != vn.vNode2sNode[j]) {

						// exist edge's path,bandwith
						// source destination bandwith[][]
						// return path
						int[][] bandwith = new int[this.FDSubstrateNework.nodeSize][this.FDSubstrateNework.nodeSize];
						for (int k = 0; k < this.FDSubstrateNework.nodeSize; k++) {
							for (int l = 0; l < this.FDSubstrateNework.nodeSize; l++) {
//								bandwith[k][l] = this.FDSubstrateNework.edgeBandwithCapacity[k][l]
//										- this.FDSubstrateNework.edgeBandwithCurrentConsume[k][l];
							}
						}
						ShortestPath sp = new ShortestPath(this.FDSubstrateNework.nodeSize);
						List<Integer> pp = new LinkedList<Integer>();
						pp = sp.Dijkstra(vn.vNode2sNode[i], vn.vNode2sNode[j], bandwith);
						if (pp.isEmpty()) {
							System.out.println("be not able to generate embedded virtual network's edge");
							return null;
						}
						path.get(i).get(j).clear();
						// for (int k = 0; k < pp.size(); k++) {
						// path.get(i).get(j).add(pp.get(k));
						// }
						for (int x : pp) {
							path.get(i).get(j).add(x);
						}

						// set virtual network's edge bandwith
						// notice
						bw = Integer.MAX_VALUE;
						for (int s = path.get(i).get(j).get(0), k = 1; k < path.get(i).get(j).size(); k++) {
							int e = path.get(i).get(j).get(k);
							bw = Math.min(bw, bandwith[s][e]);
							s = e;
						}
						if (vn.isTestSample) {
							bw = vn.edgeBandwithDemand[i][j];
						}
						for (int s = path.get(i).get(j).get(0), k = 1; k < path.get(i).get(j).size(); k++) {
							int e = path.get(i).get(j).get(k);
//							this.FDSubstrateNework.edgeBandwithCurrentConsume[s][e] += bw;
//							this.FDSubstrateNework.edgeBandwithCurrentConsume[e][s] += bw;
							s = e;
						}
					} else {

						// two adjacent node of the edge is in the same node,
						// the edge need not any more bandwith.
						bw = (int) (vnp.edgeBandwithMinimum
								+ Math.round(Math.random() * (vnp.edgeBandwithMaximum - vnp.edgeBandwithMinimum)));
						if (vn.isTestSample) {
							bw = vn.edgeBandwithDemand[i][j];
						}
					}
					vn.topology[i][j] = true;
					vn.topology[j][i] = true;
					vn.edgeBandwithDemand[i][j] = bw;
					vn.edgeBandwithDemand[j][i] = bw;
				}
			}
		}

		return path;
	}

}
