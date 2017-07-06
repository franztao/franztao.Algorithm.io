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
	public boolean serviceTypeSet[][];
	public Vector<Integer>[] serviceTypeSetVector;

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
		nodeComputationCapacity = new int[nodeSize];
		nodeComputationCurrentConsume = new int[nodeSize];

		// edge
		topology = new boolean[nodeSize][nodeSize];
		edgeBandwithCapacity = new int[nodeSize][nodeSize];
		edgeBandwithCurrentConsume = new int[nodeSize][nodeSize];

		// service
		this.serviceNumber = snp.serviceNumber;
		serviceTypeSet = new boolean[nodeSize][serviceNumber ];
		// serviceTypeVector = new Vector<Integer>();

		// label
		node2Label = new String[nodeSize];
		label2Node = new HashMap<String, Integer>();

		vnquest = new Vector<VirtualNetwork>();
		evn = new Vector<EnhancedVirtualNetwork>();
		if (snp.sampleInit) {
			faultInit();
		} else {
			Init(snp);
		}
	}

	/**
	 * @param snp
	 */
	private void Init(SubStrateNetworkParameter snp) {
		// node computation
		for (int i = 0; i < this.nodeSize; i++) {
			this.nodeComputationCapacity[i] = (int) (snp.nodeComputationMinimum
					+ Math.random() * (snp.nodeComputationMaximum - snp.nodeComputationMinimum));

		}

		// edge bandwith
		for (int i = 0; i < this.nodeSize; i++) {
			for (int j = 0; j < i; j++) {
				double ran = Math.random();
				if (ran < snp.node2nodeProbability) {
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
			this.serviceTypeSetVector[i] = new Vector<Integer>();
			for (int j = 0; j < this.serviceNumber; j++) {
				if (Math.random() > snp.serivecProbability) {
					this.serviceTypeSet[i][j ] = true;
					serviceTypeSetVector[i].addElement(j + 1);
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
		this.timeSpan = snp.time;
	}

	public void faultInit() {
		nodeComputationCapacity[0] = 5;
		nodeComputationCapacity[1] = 7;
		nodeComputationCapacity[2] = 7;
		nodeComputationCapacity[3] = 10;
		nodeComputationCapacity[4] = 8;
		nodeComputationCapacity[5] = 6;
		nodeComputationCapacity[6] = 6;
		nodeComputationCapacity[7] = 9;
		nodeComputationCapacity[8] = 8;

		for (int i = 0; i < nodeSize; i++) {
			for (int j = 0; j < nodeSize; j++) {
				topology[i][j] = true;
				edgeBandwithCapacity[i][j] = 30;
			}
		}

		serviceTypeSet[0][0] = true;
		serviceTypeSet[1][1] = true;
		serviceTypeSet[1][2] = true;
		serviceTypeSet[2][2] = true;
		serviceTypeSet[3][3] = true;
		serviceTypeSet[4][1] = true;
		serviceTypeSet[4][2] = true;
		serviceTypeSet[5][0] = true;
		serviceTypeSet[5][1] = true;
		serviceTypeSet[6][1] = true;
		serviceTypeSet[6][2] = true;
		serviceTypeSet[7][0] = true;
		serviceTypeSet[7][3] = true;
		serviceTypeSet[8][3] = true;

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
