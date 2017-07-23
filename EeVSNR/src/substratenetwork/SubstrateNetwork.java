package substratenetwork;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import standardalgorithm.ShortestPath;
import virtualnetwork.VirtualNetworkParameter;
import virtualnetwork.EnhancedVirtualNetwork;
import virtualnetwork.VirtualNetwork;

public class SubstrateNetwork {
	public int nodeSize;
	public int nodeComputationCapacity[];
	public int nodeComputationCurrentConsume[];

	public boolean topology[][];
	public int edgeBandwithCapacity[][];
	public int edgeBandwithCurrentConsume[][];

	public int serviceNumber;
	public boolean boolServiceTypeSet[][];
	public Vector<Integer>[] vectorServiceTypeSet;

	public String node2Label[];
	public Map<String, Integer> label2Node;

	public Vector<VirtualNetwork> vnquest;
	public Vector<EnhancedVirtualNetwork> evn;
	public int timeSpan;

	/**
	 * @param snp
	 */
	public SubstrateNetwork(SubStrateNetworkParameter snp) {
		// node
		this.nodeSize = snp.nodeSize;
		this.nodeComputationCapacity = new int[nodeSize];
		this.nodeComputationCurrentConsume = new int[nodeSize];

		// edge
		this.topology = new boolean[nodeSize][nodeSize];
		this.edgeBandwithCapacity = new int[nodeSize][nodeSize];
		this.edgeBandwithCurrentConsume = new int[nodeSize][nodeSize];

		// service
		this.serviceNumber = snp.serviceNumber;
		this.boolServiceTypeSet = new boolean[nodeSize][serviceNumber];
		// serviceTypeVector = new Vector<Integer>();

		// label
		this.node2Label = new String[nodeSize];
		this.label2Node = new HashMap<String, Integer>();

		this.vnquest = new Vector<VirtualNetwork>();
		this.evn = new Vector<EnhancedVirtualNetwork>();
		if (snp.sampleInit) {
			faultSetResourceDistribution();
		} else {
			setResourceDistribution(snp);
		}
	}

	/**
	 * @param snp
	 */
	private void setResourceDistribution(SubStrateNetworkParameter snp) {
		// node computation
		for (int i = 0; i < this.nodeSize; i++) {
			this.nodeComputationCapacity[i] = (int) (snp.nodeComputationMinimum
					+ Math.random() * (snp.nodeComputationMaximum - snp.nodeComputationMinimum));

		}

		// edge bandwith
		for (int i = 0; i < this.nodeSize; i++) {
			for (int j = 0; j < i; j++) {
				if (Math.random() < snp.node2nodeProbability) {
					this.topology[i][j] = true;
					this.topology[j][i] = true;
				}

			}
		}
		for (int i = 0; i < this.nodeSize; i++) {
			for (int j = 0; j < this.nodeSize; j++) {
				if (this.topology[i][j]) {
					this.edgeBandwithCapacity[i][j] = (int) (snp.edgeBandwithMinimum
							+ Math.random() * (snp.edgeBandwithMaximum - snp.edgeBandwithMinimum));
					this.edgeBandwithCapacity[j][i] = this.edgeBandwithCapacity[i][j];
				}
			}
		}

		// service
		for (int i = 0; i < this.nodeSize; i++) {
			this.vectorServiceTypeSet[i] = new Vector<Integer>();
			for (int j = 0; j < this.serviceNumber; j++) {
				if (Math.random() < snp.serivecProbability) {
					this.boolServiceTypeSet[i][j] = true;
					vectorServiceTypeSet[i].addElement(j + 1);
				}
			}
		}

		// label
		String str = "SN";
		for (int i = 0; i < this.nodeSize; i++) {
			node2Label[i] = (str + (i + 1));
			label2Node.put((str + (1 + i)), i);
		}
		// timeSpan
		this.timeSpan = snp.runningTime;
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

}
