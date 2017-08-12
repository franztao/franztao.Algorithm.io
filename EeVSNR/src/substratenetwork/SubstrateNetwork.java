package substratenetwork;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import virtualnetwork.EnhancedVirtualNetwork;
import virtualnetwork.VirtualNetwork;

public class SubstrateNetwork implements Cloneable {
	public int nodeSize;
	public int nodeComputationCapacity[];
	public int nodeComputation4Former[];
	public int nodeComputation4EnhanceNoSharedSum[];
	public int nodeComputation4Temp[];
	public Vector<Vector<Integer>> VNQIndexSet4sNode;
	public Vector<Vector<Integer>> EVNIndexSet4sNode;

	public boolean topology[][];
	public int edgeBandwithCapacity[][];
	public int edgeBandwith4Former[][];
	public int edgeBandwith4EnhanceNoSharedSum[][];
	public int edgeBandwith4Temp[][];
	public Vector<Vector<Vector<Integer>>> VNQIndexSet4Edge;
	public Vector<Vector<Vector<Integer>>> EVNIndexSet4Edge;

	public int serviceNumber;
	public boolean boolServiceTypeSet[][];
	public Vector<Vector<Integer>> vectorServiceTypeSet;

	public String node2Label[];
	public Map<String, Integer> label2Node;

	public Vector<VirtualNetwork> VNCollection;
	public Vector<EnhancedVirtualNetwork> EVNCollection;

	/**
	 * @param snp
	 */
	public SubstrateNetwork(SubStrateNetworkParameter snp) {
		// node
		this.nodeSize = snp.getNodeSize();
		this.nodeComputationCapacity = new int[nodeSize];
		this.nodeComputation4Former = new int[nodeSize];
		this.nodeComputation4EnhanceNoSharedSum = new int[nodeSize];
		this.nodeComputation4Temp = new int[nodeSize];
		this.VNQIndexSet4sNode = new Vector<Vector<Integer>>();
		this.EVNIndexSet4sNode = new Vector<Vector<Integer>>();
		for (int i = 0; i < nodeSize; i++) {
			VNQIndexSet4sNode.addElement(new Vector<Integer>());
			EVNIndexSet4sNode.addElement(new Vector<Integer>());
		}

		// edge
		this.topology = new boolean[nodeSize][nodeSize];
		this.edgeBandwithCapacity = new int[nodeSize][nodeSize];
		edgeBandwith4Former = new int[nodeSize][nodeSize];
		edgeBandwith4EnhanceNoSharedSum = new int[nodeSize][nodeSize];
		edgeBandwith4Temp = new int[nodeSize][nodeSize];
		this.VNQIndexSet4Edge = new Vector<Vector<Vector<Integer>>>();
		this.EVNIndexSet4Edge = new Vector<Vector<Vector<Integer>>>();
		for (int i = 0; i < nodeSize; i++) {
			this.VNQIndexSet4Edge.addElement(new Vector<Vector<Integer>>());
			this.EVNIndexSet4Edge.addElement(new Vector<Vector<Integer>>());
			for (int j = 0; j < nodeSize; j++) {
				this.VNQIndexSet4Edge.get(i).add(new Vector<Integer>());
				this.EVNIndexSet4Edge.get(i).add(new Vector<Integer>());
			}
		}

		// service
		this.serviceNumber = snp.getServiceNumber();
		this.boolServiceTypeSet = new boolean[nodeSize][serviceNumber];
		this.vectorServiceTypeSet = new Vector<Vector<Integer>>();
		for (int i = 0; i < this.nodeSize; i++) {
			this.vectorServiceTypeSet.addElement(new Vector<Integer>());
		}

		// label
		this.node2Label = new String[nodeSize];
		this.label2Node = new HashMap<String, Integer>();

		this.VNCollection = new Vector<VirtualNetwork>();
		this.EVNCollection = new Vector<EnhancedVirtualNetwork>();
		if (snp.isSampleInit()) {
			faultSetResourceDistribution();
		} else {
			setResourceDistribution(snp);
		}
	}

	/**
	 * @param nodeloc
	 * @param isShared
	 * @return
	 */
	public int getSubstrateIsSharedRemainComputaion(int nodeloc, boolean isShared) {
		int remain;
		if (isShared) {
			remain = this.nodeComputationCapacity[nodeloc] - this.nodeComputation4Former[nodeloc]
					- this.nodeComputation4Temp[nodeloc];
		} else {
			remain = this.nodeComputationCapacity[nodeloc] - this.nodeComputation4Former[nodeloc]
					- this.nodeComputation4Temp[nodeloc] - this.nodeComputation4EnhanceNoSharedSum[nodeloc];
		}
		return remain;
	}

	/**
	 * @param nodeloc
	 * @param isShared
	 * @return
	 */
	public int getSubstrateRemainComputaion(int nodeloc, boolean isShared) {
		int remain = 0;
		if (isShared) {
			int maxShare = 0;
			for (int i = 0; i < this.EVNIndexSet4sNode.get(nodeloc).size(); i++) {
				int ithEVN = this.EVNIndexSet4sNode.get(nodeloc).get(i);
				int temp = 0;
				if (this.EVNCollection.get(ithEVN).isSucceed) {
					for (int j = 0; j < this.EVNCollection.get(ithEVN).getNodeSize(); j++) {
						if (nodeloc == this.EVNCollection.get(ithEVN).eNode2sNode[j]) {
							// one substrate network node map multiple virtual
							// network request and network node
							temp += this.EVNCollection.get(ithEVN).nodeComputationEnhanced[j];
						}
					}
				}
				maxShare = Math.max(maxShare, temp);
			}
			remain = this.nodeComputationCapacity[nodeloc] - this.nodeComputation4Former[nodeloc]
					- this.nodeComputation4Temp[nodeloc] - maxShare;

		} else {
			remain = this.nodeComputationCapacity[nodeloc] - this.nodeComputation4Former[nodeloc]
					- this.nodeComputation4Temp[nodeloc] - this.nodeComputation4EnhanceNoSharedSum[nodeloc];
		}
		return remain;
	}

	/**
	 * @param k
	 * @param l
	 * @param isShared
	 * @return
	 */
	public int getSubStrateIsSharedRemainBandwith(int k, int l, boolean isShared) {
		int remain = 0;
		if (isShared) {
			remain = this.edgeBandwithCapacity[k][l] - this.edgeBandwith4Former[k][l] - this.edgeBandwith4Temp[k][l];
		} else {
			remain = this.edgeBandwithCapacity[k][l] - this.edgeBandwith4Former[k][l] - this.edgeBandwith4Temp[k][l]
					- this.edgeBandwith4EnhanceNoSharedSum[k][l];
		}
		return remain;
	}

	/**
	 * @param k
	 * @param l
	 * @param isShared
	 * @return
	 */
	public int getSubStrateRemainBandwith(int k, int l, boolean isShared) {
		int remain = 0;
		if (isShared) {
			int maxShare = 0;
			for (int i = 0; i < this.EVNIndexSet4Edge.get(k).get(l).size(); i++) {
				int ithEVN = this.EVNIndexSet4Edge.get(k).get(l).get(i);
				int tempBandwith = 0;
				if (this.EVNCollection.get(ithEVN).isSucceed) {
					for (int p = 0; p < this.EVNCollection.get(ithEVN).getNodeSize(); p++) {
						for (int q = 0; q < this.EVNCollection.get(ithEVN).getNodeSize(); q++) {

							for (int t = 0; t < this.EVNCollection.get(ithEVN).eEdge2sPath.get(p).get(q).size()
									- 2; t++) {
								if (k == this.EVNCollection.get(ithEVN).eEdge2sPath.get(p).get(q).get(t)
										&& (l == this.EVNCollection.get(ithEVN).eEdge2sPath.get(p).get(q).get(t + 1))) {

									tempBandwith += this.EVNCollection.get(ithEVN).edgeBandwithEnhanced[p][q];
								}
							}
						}
					}
				}
				maxShare = Math.max(maxShare, tempBandwith);
			}
			remain = this.edgeBandwithCapacity[k][l] - this.edgeBandwith4Former[k][l] - this.edgeBandwith4Temp[k][l]
					- maxShare;
		} else {
			remain = this.edgeBandwithCapacity[k][l] - this.edgeBandwith4Former[k][l] - this.edgeBandwith4Temp[k][l]
					- this.edgeBandwith4EnhanceNoSharedSum[k][l];
		}
		return remain;
	}

	/**
	 * @param snp
	 */
	private void setResourceDistribution(SubStrateNetworkParameter snp) {
		// node computation
		for (int i = 0; i < this.nodeSize; i++) {
			this.nodeComputationCapacity[i] = (int) (snp.getNodeComputationMinimum()
					+ Math.random() * (snp.getNodeComputationMaximum() - snp.getNodeComputationMinimum()));

		}

		// edge bandwith
		for (int i = 0; i < this.nodeSize; i++) {
			for (int j = 0; j < i; j++) {
				if (Math.random() < snp.getNode2nodeProbability()) {
					this.topology[i][j] = this.topology[j][i] = true;
				}

			}
		}
		for (int i = 0; i < this.nodeSize; i++) {
			for (int j = 0; j < i; j++) {
				if (this.topology[i][j]) {
					this.edgeBandwithCapacity[i][j] = (int) (snp.getEdgeBandwithMinimum()
							+ Math.random() * (snp.getEdgeBandwithMaximum() - snp.getEdgeBandwithMinimum()));
					this.edgeBandwithCapacity[j][i] = this.edgeBandwithCapacity[i][j];
				}
			}
		}

		// service
		for (int i = 0; i < this.nodeSize; i++) {
			for (int j = 0; j < this.serviceNumber; j++) {
				if (Math.random() < snp.getSerivecProbability()) {
					this.boolServiceTypeSet[i][j] = true;
					vectorServiceTypeSet.get(i).addElement(j);
				}
			}
			if(vectorServiceTypeSet.get(i).size()==0){
				vectorServiceTypeSet.get(i).addElement((int) ((this.serviceNumber-1)*Math.random()));
			}
		}

		// label
		String str = "SN";
		for (int i = 0; i < this.nodeSize; i++) {
			node2Label[i] = (str + (i + 1));
			label2Node.put((str + (1 + i)), i);
		}
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		SubstrateNetwork sn = (SubstrateNetwork) super.clone();
		sn.nodeComputationCapacity = ((SubstrateNetwork) super.clone()).nodeComputationCapacity.clone();
		sn.nodeComputation4Former = ((SubstrateNetwork) super.clone()).nodeComputation4Former.clone();
		sn.nodeComputation4EnhanceNoSharedSum = ((SubstrateNetwork) super.clone()).nodeComputation4EnhanceNoSharedSum
				.clone();
		sn.nodeComputation4Temp = ((SubstrateNetwork) super.clone()).nodeComputation4Temp.clone();

		// sn.VNQIndexSet4sNode = (Vector<Vector<Integer>>) ((SubstrateNetwork)
		// super.clone()).VNQIndexSet4sNode.clone();
		// sn.EVNIndexSet4sNode = (Vector<Vector<Integer>>) ((SubstrateNetwork)
		// super.clone()).VNQIndexSet4sNode.clone();
		sn.VNQIndexSet4sNode = new Vector<Vector<Integer>>();
		sn.EVNIndexSet4sNode = new Vector<Vector<Integer>>();
		for (int i = 0; i < sn.nodeSize; i++) {
			sn.VNQIndexSet4sNode.addElement( new Vector<Integer>());
			sn.EVNIndexSet4sNode.addElement( new Vector<Integer>());
		}

		sn.topology = ((SubstrateNetwork) super.clone()).topology.clone();
		sn.edgeBandwithCapacity = ((SubstrateNetwork) super.clone()).edgeBandwithCapacity.clone();
		sn.edgeBandwith4Former = ((SubstrateNetwork) super.clone()).edgeBandwith4Former.clone();
		sn.edgeBandwith4EnhanceNoSharedSum = ((SubstrateNetwork) super.clone()).edgeBandwith4EnhanceNoSharedSum.clone();
		sn.edgeBandwith4Temp = ((SubstrateNetwork) super.clone()).edgeBandwith4Temp.clone();

		// sn.VNCollection = (Vector<VirtualNetwork>) ((SubstrateNetwork)
		// super.clone()).VNCollection.clone();
		// sn.EVNCollection = (Vector<EnhancedVirtualNetwork>)
		// ((SubstrateNetwork) super.clone()).EVNCollection.clone();

		sn.VNCollection = new Vector<VirtualNetwork>();
		sn.EVNCollection = new Vector<EnhancedVirtualNetwork>();

		// sn.VNQIndexSet4Edge = (Vector<Vector<Vector<Integer>>>)
		// ((SubstrateNetwork) super.clone()).VNQIndexSet4Edge
		// .clone();
		// sn.EVNIndexSet4Edge = (Vector<Vector<Vector<Integer>>>)
		// ((SubstrateNetwork) super.clone()).EVNIndexSet4Edge
		// .clone();
		sn.VNQIndexSet4Edge = new Vector<Vector<Vector<Integer>>>();
		sn.EVNIndexSet4Edge = new Vector<Vector<Vector<Integer>>>();

		for (int i = 0; i < nodeSize; i++) {
			sn.VNQIndexSet4Edge.addElement(new Vector<Vector<Integer>>());
			sn.EVNIndexSet4Edge.addElement(new Vector<Vector<Integer>>());
			for (int j = 0; j < nodeSize; j++) {
				sn.VNQIndexSet4Edge.get(i).add(new Vector<Integer>());
				sn.EVNIndexSet4Edge.get(i).add(new Vector<Integer>());
			}
		}
		return sn;
	}

	/**
	 * 
	 */
	public void releaseResource() {
		// TODO Auto-generated method stub

	}

	/**
	 * @param i
	 * @param isShared
	 * @return
	 */
	public int getIsSharedRemainComputaion(int i, boolean isShared) {
		int remain = 0;
		if (isShared) {
			remain = this.nodeComputationCapacity[i] - this.nodeComputation4Former[i] - this.nodeComputation4Temp[i];
		} else {
			remain = this.nodeComputationCapacity[i] - this.nodeComputation4Former[i] - this.nodeComputation4Temp[i]
					- this.nodeComputation4EnhanceNoSharedSum[i];
		}
		return remain;
	}

	/**
	 * 
	 */
	public void clearTemp() {
		for (int i = 0; i < this.nodeSize; i++) {
			this.nodeComputation4Temp[i] = 0;
			for (int j = 0; j < i; j++) {
				this.edgeBandwith4Temp[i][j] = this.edgeBandwith4Temp[j][i] = 0;
			}
		}
	}

	public void faultSetResourceDistribution() {
		nodeComputationCapacity[0] = 5;
		nodeComputationCapacity[1] = 7;
		nodeComputationCapacity[2] = 7;
		nodeComputationCapacity[3] = 10;
		nodeComputationCapacity[4] = 6;
		nodeComputationCapacity[5] = 9;
		nodeComputationCapacity[6] = 8;
		nodeComputationCapacity[7] = 9;
		nodeComputationCapacity[8] = 8;

		for (int i = 0; i < nodeSize; i++) {
			for (int j = 0; j < nodeSize; j++) {
				topology[i][j] = true;
				edgeBandwithCapacity[i][j] = 30;
			}
		}
		boolServiceTypeSet[0][0] = true;
		boolServiceTypeSet[1][1] = true;
		boolServiceTypeSet[1][2] = true;
		boolServiceTypeSet[2][2] = true;
		boolServiceTypeSet[3][3] = true;

		boolServiceTypeSet[4][0] = true;
		boolServiceTypeSet[4][1] = true;
		boolServiceTypeSet[5][0] = true;
		boolServiceTypeSet[5][3] = true;
		boolServiceTypeSet[6][1] = true;
		boolServiceTypeSet[6][2] = true;
		boolServiceTypeSet[7][0] = true;
		boolServiceTypeSet[7][3] = true;
		boolServiceTypeSet[8][3] = true;

		node2Label[0] = "S1";
		node2Label[1] = "S2";
		node2Label[2] = "S3";
		node2Label[3] = "S4";
		node2Label[4] = "S5";
		node2Label[5] = "S6";
		node2Label[6] = "S7";
		node2Label[7] = "S8";
		node2Label[8] = "S9";

		label2Node.put("S1", 0);
		label2Node.put("S2", 1);
		label2Node.put("S3", 2);
		label2Node.put("S4", 3);
		label2Node.put("S5", 4);
		label2Node.put("S6", 5);
		label2Node.put("S7", 6);
		label2Node.put("S8", 7);
		label2Node.put("S9", 8);
	}

	/**
	 * @return the nodeSize
	 */
	public int getNodeSize() {
		return nodeSize;
	}

	/**
	 * @param nodeSize
	 *            the nodeSize to set
	 */
	public void setNodeSize(int nodeSize) {
		this.nodeSize = nodeSize;
	}

}
