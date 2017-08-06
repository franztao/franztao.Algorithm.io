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
	public int nodeComputation4Enhance_Sum[];
	public int nodeComputation4Temp[];
	public Vector<Vector<Integer>> VNReqSet4Node;

	public boolean topology[][];
	public int edgeBandwithCapacity[][];
	public int edgeBandwith4Former[][];
	public int edgeBandwith4Enhance_Sum[][];
	public int edgeBandwith4Temp[][];
	public Vector<Vector<Vector<Integer>>> VNReqSet4Edge;

	public int serviceNumber;
	public boolean boolServiceTypeSet[][];
	public Vector<Integer>[] vectorServiceTypeSet;

	public String node2Label[];
	public Map<String, Integer> label2Node;

	public Vector<VirtualNetwork> VNQ;
	public Vector<EnhancedVirtualNetwork> ENV;

	/**
	 * @param snp
	 */
	public SubstrateNetwork(SubStrateNetworkParameter snp) {
		// node
		this.nodeSize = snp.getNodeSize();
		this.nodeComputationCapacity = new int[nodeSize];
		this.nodeComputation4Former = new int[nodeSize];
		this.nodeComputation4Enhance_Sum = new int[nodeSize];
		this.nodeComputation4Temp = new int[nodeSize];
		this.VNReqSet4Node = new Vector<Vector<Integer>>();
		for (int i = 0; i < nodeSize; i++) {
			VNReqSet4Node.addElement(new Vector<Integer>());
		}

		// edge
		this.topology = new boolean[nodeSize][nodeSize];
		this.edgeBandwithCapacity = new int[nodeSize][nodeSize];
		edgeBandwith4Former = new int[nodeSize][nodeSize];
		edgeBandwith4Enhance_Sum = new int[nodeSize][nodeSize];
		edgeBandwith4Temp = new int[nodeSize][nodeSize];
		this.VNReqSet4Edge = new Vector<Vector<Vector<Integer>>>();
		for (int i = 0; i < nodeSize; i++) {
			this.VNReqSet4Edge.addElement(new Vector<Vector<Integer>>());
			for (int j = 0; j < nodeSize; j++) {
				VNReqSet4Edge.get(i).add(new Vector<Integer>());
			}
		}

		// service
		this.serviceNumber = snp.getServiceNumber();
		this.boolServiceTypeSet = new boolean[nodeSize][serviceNumber];
		// serviceTypeVector = new Vector<Integer>();

		// label
		this.node2Label = new String[nodeSize];
		this.label2Node = new HashMap<String, Integer>();

		this.VNQ = new Vector<VirtualNetwork>();
		this.ENV = new Vector<EnhancedVirtualNetwork>();
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
	public int getRemainComputaion(int nodeloc, boolean isShared) {
		int remain = 0;
		if (isShared) {
			int maxShare = 0;
			for (int i = 0; i < this.VNReqSet4Node.get(nodeloc).size(); i++) {
				int vnq = this.VNReqSet4Node.get(nodeloc).get(i);
				int temp = 0;
				for (int j = 0; j < this.VNQ.get(vnq).getNodeSize(); j++) {
					if (nodeloc == this.VNQ.get(vnq).vNode2sNode[j]) {
						// one substrate network node map multiple virtual
						// network request and network node
						temp += this.ENV.get(vnq).nodeComputationEnhanced[j];
					}
				}
				maxShare = Math.max(maxShare, temp);

			}
			remain = this.nodeComputationCapacity[nodeloc] - this.nodeComputation4Former[nodeloc]
					- this.nodeComputation4Temp[nodeloc] - maxShare;

		} else {
			remain = this.nodeComputationCapacity[nodeloc] - this.nodeComputation4Former[nodeloc]
					- this.nodeComputation4Temp[nodeloc] - this.nodeComputation4Enhance_Sum[nodeloc];
		}
		return remain;
	}

	/**
	 * @param k
	 * @param l
	 * @param isShared
	 * @return
	 */
	public int getRemainBandwith(int k, int l, boolean isShared) {
		int remain = 0;
		if (isShared) {
			int maxShare = 0;
			for (int i = 0; i < this.VNReqSet4Edge.get(k).get(l).size(); i++) {
				int vnq = this.VNReqSet4Edge.get(k).get(l).get(i);
				int temp = 0;
				for (int p = 0; p < this.VNQ.get(vnq).getNodeSize(); p++) {
					for (int q = 0; q < this.VNQ.get(vnq).getNodeSize(); q++) {

						for (int t = 0; t < this.VNQ.get(vnq).vEdge2sPath.size() - 1; t++) {
							if (k == this.VNQ.get(vnq).vEdge2sPath.get(p).get(q).get(t)
									&& (l == this.VNQ.get(vnq).vEdge2sPath.get(p).get(q).get(t + 1))) {
								temp += this.ENV.get(vnq).edgeBandwithEnhanced[p][q];
							}
						}
					}
				}
				maxShare = Math.max(maxShare, temp);
			}
			remain = this.edgeBandwithCapacity[k][l] - this.edgeBandwith4Former[k][l] - this.edgeBandwith4Temp[k][l]
					- maxShare;
		} else {
			remain = this.edgeBandwithCapacity[k][l] - this.edgeBandwith4Former[k][l]
					- this.edgeBandwith4Enhance_Sum[k][l] - this.edgeBandwith4Temp[k][l];
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
					this.topology[i][j] = true;
					this.topology[j][i] = true;
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
			this.vectorServiceTypeSet[i] = new Vector<Integer>();
			for (int j = 0; j < this.serviceNumber; j++) {
				if (Math.random() < snp.getSerivecProbability()) {
					this.boolServiceTypeSet[i][j] = true;
					vectorServiceTypeSet[i].addElement(j);
				}
			}
		}

		// label
		String str = "SN";
		for (int i = 0; i < this.nodeSize; i++) {
			node2Label[i] = (str + (i + 1));
			label2Node.put((str + (1 + i)), i);
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

	@Override
	public Object clone() throws CloneNotSupportedException {
		return (SubstrateNetwork) super.clone();
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
					- this.nodeComputation4Enhance_Sum[i];
		}
		return remain;
	}

	/**
	 * 
	 */
	public void clearTemp() {
		for (int i = 0; i < this.nodeSize; i++) {
			this.nodeComputation4Temp[i] = 0;
			for (int j = 0; j < this.nodeSize; j++) {
				this.edgeBandwith4Temp[i][j] = 0;
			}
		}
	}

}
