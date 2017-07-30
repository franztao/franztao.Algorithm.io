/**
 * 
 */
package virtualnetwork;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import evsnr.EVSNR;
import gurobi.*;
import multipleknapsack.MulitpleKnapsack;
import substratenetwork.BackupNode;
import substratenetwork.SubstrateNetwork;

/**
 * @author franz
 *
 */
public class EnhancedVirtualNetwork {

	public final int Method = EVSNR.MatchMethodILP;

	// nodeSize=enhacnedNodeSize+backupNodeSize
	public int nodeSize;
	public int nodeSize4Embeded;
	public int nodeSize4Backup;
	// nodeComputationCapacity-usedNodeCurrentComputationCapacity
	public int nodeComputationCapacity[];
	public int consumeNodeComputation[];
	public int enhancedNodeComputation[];

	public boolean topology[][];
	// edgeBandwithCapacity-usedEdgeCurrentBandwithCapacity
	public int consumeEdgeBandwith[][];
	public int enhancedEdgeBandwith[][];

	public int serviceNumber;
	public boolean boolServiceTypeSet[][];

	public String node2Label[];
	public Map<String, Integer> label2Node;

	public int vNode2eNode[];

	public VirtualNetwork VN;

	// public boolean sampleInit;

	class StarEdgeStructure {
		int neighborVNID;
		int neighborEnhancedVNID;
		int neighborNodeType;
		int neighborEdgeBandwithCapacity;
		// public int neighborUsedEdgeBandwithCapacity;

	}

	class starStructure {
		int starNodeServiceType;
		int starNodeComputation;
		int starNodeEnhancedVNID;
		int starNodeVNID;
		Vector<StarEdgeStructure> neighborEdge;
		// Vector<Integer> neighborEnhancedVNID;
		// Vector<Integer> neighborEnhancedVNType;
		// Vector<Integer> neighborEdgeBandwith;
	}

	class ConsumeResource {
		int initNodeNumber;
		int initNodeComputation;
		int initEdgeBandwith;

		int consumedNodeNumber;
		int consumedNodeComputation;
		int consumeEdgeBandwith;
	}

	ConsumeResource consumedResource;

	starStructure Items[];
	Vector<starStructure> Knapsacks;

	/**
	 * @param vn
	 * @param path
	 * @param bn
	 */
	public EnhancedVirtualNetwork(SubstrateNetwork fDSubstrateNework, VirtualNetwork vn, BackupNode bn,
			Vector<Vector<LinkedList<Integer>>> path) {
		this.VN = vn;
		// node
		this.nodeSize4Embeded = vn.nodeSize;
		this.nodeSize4Backup = bn.backupNodeSize;
		this.nodeSize = this.nodeSize4Embeded + this.nodeSize4Backup;
		this.nodeComputationCapacity = new int[this.nodeSize];
		this.consumeNodeComputation = new int[this.nodeSize];
		this.enhancedNodeComputation = new int[this.nodeSize];
		// edge
		this.topology = new boolean[this.nodeSize][this.nodeSize];
		this.consumeEdgeBandwith = new int[this.nodeSize][this.nodeSize];
		this.enhancedEdgeBandwith = new int[this.nodeSize][this.nodeSize];
		// service
		this.serviceNumber = vn.serviceNumber;
		this.boolServiceTypeSet = new boolean[nodeSize][serviceNumber];
		// label
		this.node2Label = new String[nodeSize];
		this.label2Node = new HashMap<String, Integer>();
		// embed function
		this.vNode2eNode = new int[this.VN.nodeSize];
		// knapsack problem
		this.Items = new starStructure[this.VN.nodeSize];

		this.consumedResource = new ConsumeResource();

		// this.sampleInit = vn.sampleInit;
		// if (this.sampleInit) {
		// initSample1();
		// } else {
		constrcutEVN(fDSubstrateNework, vn, bn, path);
		// }
	}

	/**
	 * @param vn2
	 * @param path
	 * @param bn
	 */
	private void constrcutEVN(SubstrateNetwork fDSubstrateNework, VirtualNetwork vn2, BackupNode bn,
			Vector<Vector<LinkedList<Integer>>> path) {
		// node
		for (int i = 0; i < this.nodeSize; i++) {
			if (i < this.nodeSize4Embeded) {
				this.nodeComputationCapacity[i] = vn2.nodeComputationCurrentConsume[i];
				this.consumeNodeComputation[i] = vn2.nodeComputationDemand[i];
			} else {
				this.nodeComputationCapacity[i] = bn.nodeComputationCapacity[i - this.nodeSize4Embeded];
				this.consumeNodeComputation[i] = 0;
			}
		}

		// edge
		for (int i = 0; i < this.nodeSize4Embeded; i++) {
			for (int j = 0; j < this.nodeSize4Embeded; j++) {
				this.topology[i][j] = vn2.topology[i][j];
				this.consumeEdgeBandwith[i][j] = vn2.edgeBandwithDemand[i][j];
			}
		}

		// service
		for (int i = 0; i < this.nodeSize; i++) {
			for (int j = 0; j < this.serviceNumber; j++) {
				if (i < this.nodeSize4Embeded) {
					this.boolServiceTypeSet[i][j] = fDSubstrateNework.boolServiceTypeSet[vn2.vNode2sNode[i]][j];
				} else {
					this.boolServiceTypeSet[i][j] = fDSubstrateNework.boolServiceTypeSet[bn.bNode2sNode[i
							- this.nodeSize4Embeded]][j];
				}
			}
		}

		// label
		String estr = "EN_";
		String bstr = "BN_";
		for (int i = 0; i < this.nodeSize; i++) {
			if (i < this.nodeSize4Embeded) {
				this.node2Label[i] = estr + i;
				this.label2Node.put(estr + i, i);
			} else {
				this.node2Label[i] = bstr + (i - this.nodeSize4Embeded);
				this.label2Node.put(bstr + (i - this.nodeSize4Embeded), i);
			}
		}

		for (int i = 0; i < this.nodeSize4Embeded; i++) {
			this.vNode2eNode[i] = i;
		}

		for (int i = 0; i < this.nodeSize; i++) {
			if (this.consumeNodeComputation[i] > 0) {
				consumedResource.initNodeNumber++;
				consumedResource.consumedNodeNumber++;

				consumedResource.initNodeComputation += this.consumeNodeComputation[i];
				consumedResource.consumedNodeComputation += this.consumeNodeComputation[i];
			}
		}

		for (int i = 0; i < this.nodeSize; i++) {
			for (int j = 0; j < this.nodeSize; j++) {
				consumedResource.consumeEdgeBandwith += this.consumeEdgeBandwith[i][j];
				consumedResource.initEdgeBandwith += this.consumeEdgeBandwith[i][j];
			}
		}
		consumedResource.consumeEdgeBandwith /= 2;
		consumedResource.initEdgeBandwith /= 2;

	}

	public void initSample1() {
		nodeComputationCapacity[0] = 5;
		nodeComputationCapacity[1] = 7;
		nodeComputationCapacity[2] = 7;
		nodeComputationCapacity[3] = 10;
		nodeComputationCapacity[4] = 6;
		nodeComputationCapacity[5] = 9;
		nodeComputationCapacity[6] = 8;

		consumeNodeComputation[0] = 2;
		consumeNodeComputation[1] = 3;
		consumeNodeComputation[2] = 5;
		consumeNodeComputation[3] = 6;

		consumeEdgeBandwith[0][1] = 4;
		consumeEdgeBandwith[0][2] = 5;
		consumeEdgeBandwith[0][3] = 3;
		consumeEdgeBandwith[1][2] = 6;
		consumeEdgeBandwith[1][0] = 4;
		consumeEdgeBandwith[2][0] = 5;
		consumeEdgeBandwith[3][0] = 3;
		consumeEdgeBandwith[2][1] = 6;

		for (int i = 0; i < nodeSize; i++) {
			for (int j = 0; j < nodeSize; j++) {
				if (0 != consumeEdgeBandwith[i][j]) {
					topology[i][j] = true;
				}
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

		node2Label[0] = "E1";
		node2Label[1] = "E2";
		node2Label[2] = "E3";
		node2Label[3] = "E4";
		node2Label[4] = "B1";
		node2Label[5] = "B2";
		node2Label[6] = "B3";

		label2Node.put("E1", 0);
		label2Node.put("E2", 1);
		label2Node.put("E3", 2);
		label2Node.put("E4", 3);
		label2Node.put("B1", 4);
		label2Node.put("B2", 5);
		label2Node.put("B3", 6);

		// there may be some virtual node located into the same enhanced node
		vNode2eNode[0] = 0;
		vNode2eNode[1] = 1;
		vNode2eNode[2] = 2;
		vNode2eNode[3] = 3;

		nodeSize4Backup = 3;
		nodeSize4Embeded = 4;

		consumedResource.initEdgeBandwith = 18;
		consumedResource.initNodeComputation = 16;
		consumedResource.initNodeNumber = 4;

		consumedResource.consumeEdgeBandwith = 18;
		consumedResource.consumedNodeComputation = 16;
		consumedResource.consumedNodeNumber = 4;

	}

	/**
	 * 
	 */
	public void initSample2() {
		nodeComputationCapacity[0] = 7;
		nodeComputationCapacity[1] = 5;
		nodeComputationCapacity[2] = 3;
		nodeComputationCapacity[3] = 7;

		consumeEdgeBandwith[0][1] = 3;
		consumeEdgeBandwith[1][0] = 3;

		for (int i = 0; i < nodeSize; i++) {
			for (int j = 0; j < nodeSize; j++) {
				if (0 != consumeEdgeBandwith[i][j]) {
					topology[i][j] = true;
				}
			}
		}

		consumeNodeComputation[0] = 3;
		consumeNodeComputation[1] = 4;

		boolServiceTypeSet[0][0] = true;
		boolServiceTypeSet[0][1] = true;
		boolServiceTypeSet[1][1] = true;
		boolServiceTypeSet[2][0] = true;
		boolServiceTypeSet[3][0] = true;
		boolServiceTypeSet[3][1] = true;

		node2Label[0] = "E1";
		node2Label[1] = "E2";
		node2Label[2] = "B1";
		node2Label[3] = "B2";

		label2Node.put("E1", 0);
		label2Node.put("E2", 1);
		label2Node.put("B1", 2);
		label2Node.put("B2", 3);

		// there may be some virtual node located into the same enhanced node
		vNode2eNode[0] = 0;
		vNode2eNode[1] = 1;

		nodeSize4Backup = 2;
		nodeSize4Embeded = 2;

		consumedResource.initEdgeBandwith = 3;
		consumedResource.initNodeComputation = 7;
		consumedResource.initNodeNumber = 2;

		consumedResource.consumeEdgeBandwith = 3;
		consumedResource.consumedNodeComputation = 7;
		consumedResource.consumedNodeNumber = 2;
	}

	/**
	 * 
	 */
	public void initSample3() {
		// TODO Auto-generated method stub
		nodeComputationCapacity[0] = 5;
		nodeComputationCapacity[1] = 5;

		consumeNodeComputation[0] = 3;

		boolServiceTypeSet[0][0] = true;
		boolServiceTypeSet[1][0] = true;
		boolServiceTypeSet[1][1] = true;

		node2Label[0] = "E1";
		node2Label[1] = "E2";

		label2Node.put("E1", 0);
		label2Node.put("B1", 1);

		// there may be some virtual node located into the same enhanced node
		vNode2eNode[0] = 0;

		nodeSize4Backup = 1;
		nodeSize4Embeded = 1;

		consumedResource.initEdgeBandwith = 0;
		consumedResource.initNodeComputation = 3;
		consumedResource.initNodeNumber = 1;

		consumedResource.consumeEdgeBandwith = 0;
		consumedResource.consumedNodeComputation = 3;
		consumedResource.consumedNodeNumber = 1;
	}

	public void computeItems() {
		for (int i = 0; i < this.VN.nodeSize; i++) {
			Items[i] = new starStructure();
			Items[i].starNodeVNID = i;
			Items[i].starNodeEnhancedVNID = this.vNode2eNode[i];
			Items[i].starNodeComputation = this.VN.nodeComputationDemand[i];
			Items[i].starNodeServiceType = this.VN.nodeServiceType[i];
			Items[i].neighborEdge = new Vector<StarEdgeStructure>();
			for (int j = 0; j < this.VN.nodeSize; j++) {
				if (this.VN.topology[i][j]) {
					StarEdgeStructure edge = new StarEdgeStructure();
					edge.neighborVNID = j;
					edge.neighborEnhancedVNID = this.vNode2eNode[j];
					edge.neighborEdgeBandwithCapacity = this.VN.edgeBandwithDemand[i][j];
					edge.neighborNodeType = this.VN.nodeServiceType[j];
					Items[i].neighborEdge.addElement(edge);
				}
			}
		}
	}

	void constructKnapsacks(int failurenodeID) {
		Knapsacks = new Vector<starStructure>();
		for (int i = 0; i < this.nodeSize; i++) {
			if (i != failurenodeID) {
				for (int j = 0; j < this.serviceNumber; j++) {
					if (this.boolServiceTypeSet[i][j]) {
						starStructure bag = new starStructure();
						bag.starNodeServiceType = j;
						bag.starNodeEnhancedVNID = i;
						bag.starNodeComputation = this.consumeNodeComputation[i];
						// ineffective value
						bag.starNodeVNID = -1;
						bag.neighborEdge = new Vector<StarEdgeStructure>();
						for (int k = 0; k < this.VN.nodeSize; k++) {
							if (failurenodeID != this.vNode2eNode[k]) {
								StarEdgeStructure edge = new StarEdgeStructure();
								edge.neighborVNID = k;
								edge.neighborEnhancedVNID = this.vNode2eNode[k];
								if (i == this.vNode2eNode[k])
									edge.neighborEdgeBandwithCapacity = Integer.MAX_VALUE;
								else
									edge.neighborEdgeBandwithCapacity = this.consumeEdgeBandwith[i][this.vNode2eNode[k]];
								// if (i ==
								// this.virtualNode2EnhancedVirtualNode[k])
								// edge.neighborUsedEdgeBandwithCapacity =
								// Integer.MAX_VALUE;
								// else
								// edge.neighborUsedEdgeBandwithCapacity =
								// this.usedEdgeBandwith[i][this.virtualNode2EnhancedVirtualNode[k]];
								edge.neighborNodeType = this.VN.nodeServiceType[k];
								bag.neighborEdge.addElement(edge);

							}
						}
						Knapsacks.addElement(bag);
					}
				}
			}

		}
	}

	public boolean failIthNode(int failurenodeID, boolean failurtype) throws GRBException {
		constructKnapsacks(failurenodeID);
		MulitpleKnapsack MKP = new MulitpleKnapsack(this.VN.nodeSize, Knapsacks.size(), this.nodeSize);
		constructMultipleKnapsackProbem(MKP, failurtype);
		if (failurenodeID == 3) {
			MKP.matchingMatrix[1][5] = Integer.MAX_VALUE;
		}
		int solution[] = new int[this.VN.nodeSize];

		if (this.Method == EVSNR.MatchMethodDP) {
			if (!MKP.optimalSoutionDP(solution)) {
				System.out.println("Failure node: " + failurenodeID + ", there is not solution");
				return false;
			}
		}

		if (this.Method == EVSNR.MatchMethodILP) {
			if (!MKP.optimalSoutionILP(solution)) {
				System.out.println("Failure node: " + failurenodeID + ", there is not solution");
				return false;
			}
		}
		augmentNodeEdge(solution, failurenodeID, Method);
		return true;

	}

	/**
	 * @param solution
	 */
	private void augmentNodeEdge(int[] solution, int failurenodeID, int Method) {
		// TODO Auto-generated method stub

		int virutialNode2NewVirtualNode[] = new int[this.VN.nodeSize];
		if (Method == EVSNR.MatchMethodILP) {
			for (int i = 0; i < this.VN.nodeSize; i++) {
				virutialNode2NewVirtualNode[i] = Knapsacks.elementAt(solution[i]).starNodeEnhancedVNID;
				// System.out.println("00000 "+virutialNode2NewVirtualNode[i] +
				// 1);
			}
		}
		if (Method == EVSNR.MatchMethodDP) {
			for (int i = 0; i < this.VN.nodeSize; i++) {
				virutialNode2NewVirtualNode[i] = solution[i];
				// System.out.println(virutialNode2NewVirtualNode[i] + 1);
			}
		}

		for (int i = 0; i < this.VN.nodeSize; i++) {
			// add node computation
			if (this.consumeNodeComputation[virutialNode2NewVirtualNode[i]] < Items[i].starNodeComputation)
				this.consumeNodeComputation[virutialNode2NewVirtualNode[i]] = Items[i].starNodeComputation;
			for (int j = 0; j < Items[i].neighborEdge.size(); j++) {
				int neighborVNNode = Items[i].neighborEdge.elementAt(j).neighborVNID;
				// if (failurenodeID !=
				// virutialNode2NewVirtualNode[neighborVNNode]) {
				if (virutialNode2NewVirtualNode[i] != virutialNode2NewVirtualNode[neighborVNNode])
					if (this.consumeEdgeBandwith[virutialNode2NewVirtualNode[i]][virutialNode2NewVirtualNode[neighborVNNode]] < Items[i].neighborEdge
							.elementAt(j).neighborEdgeBandwithCapacity) {
						this.consumeEdgeBandwith[virutialNode2NewVirtualNode[i]][virutialNode2NewVirtualNode[neighborVNNode]] = Items[i].neighborEdge
								.elementAt(j).neighborEdgeBandwithCapacity;
						this.consumeEdgeBandwith[virutialNode2NewVirtualNode[neighborVNNode]][virutialNode2NewVirtualNode[i]] = Items[i].neighborEdge
								.elementAt(j).neighborEdgeBandwithCapacity;
						this.topology[virutialNode2NewVirtualNode[i]][virutialNode2NewVirtualNode[neighborVNNode]] = this.topology[virutialNode2NewVirtualNode[neighborVNNode]][virutialNode2NewVirtualNode[i]] = true;
						// }
					}
			}
		}

	}

	boolean constructMultipleKnapsackProbem(MulitpleKnapsack mKP, boolean failurtype) {
		for (int i = 0; i < this.VN.nodeSize; i++) {
			for (int j = 0; j < Knapsacks.size(); j++) {
				mKP.matchingMatrix[i][j] = Integer.MAX_VALUE;
				if ((failurtype == EVSNR.FailureIndependent)
						&& (Knapsacks.elementAt(j).starNodeEnhancedVNID < this.nodeSize4Embeded)
						&& (Knapsacks.elementAt(j).starNodeEnhancedVNID != Items[i].starNodeEnhancedVNID)) {
					continue;
				}
				if (Items[i].starNodeServiceType == Knapsacks.elementAt(j).starNodeServiceType) {
					if (Items[i].starNodeComputation <= this.nodeComputationCapacity[Knapsacks
							.elementAt(j).starNodeEnhancedVNID]) {
						mKP.matchingMatrix[i][j] = 0;
						if (0 == this.consumeNodeComputation[Knapsacks.elementAt(j).starNodeEnhancedVNID]) {
							mKP.matchingMatrix[i][j] += EVSNR.addNewNodeCost;
						}
						if (Items[i].starNodeEnhancedVNID != Knapsacks.elementAt(j).starNodeEnhancedVNID) {
							mKP.matchingMatrix[i][j] += EVSNR.transformExistedNodeCost;
						}
						if (Items[i].starNodeComputation > this.consumeNodeComputation[Knapsacks
								.elementAt(j).starNodeEnhancedVNID]) {
							mKP.matchingMatrix[i][j] += (EVSNR.addNodeComputaionCost * (Items[i].starNodeComputation
									- this.consumeNodeComputation[Knapsacks.elementAt(j).starNodeEnhancedVNID]));
						}
						for (int k = 0; k < Items[i].neighborEdge.size(); k++) {
							boolean existedEdge = true;
							for (int l = 0; l < Knapsacks.elementAt(j).neighborEdge.size(); l++) {
								if (Items[i].neighborEdge
										.elementAt(k).neighborVNID == Knapsacks.elementAt(j).neighborEdge
												.elementAt(l).neighborVNID) {
									existedEdge = false;
									if (Items[i].neighborEdge.elementAt(
											k).neighborEdgeBandwithCapacity > Knapsacks.elementAt(j).neighborEdge
													.elementAt(l).neighborEdgeBandwithCapacity) {
										mKP.matchingMatrix[i][j] += (Items[i].neighborEdge
												.elementAt(k).neighborEdgeBandwithCapacity
												- Knapsacks.elementAt(j).neighborEdge
														.elementAt(l).neighborEdgeBandwithCapacity);
									}
								}
							}
							if (existedEdge) {
								mKP.matchingMatrix[i][j] += Items[i].neighborEdge
										.elementAt(k).neighborEdgeBandwithCapacity;
							}
						}

					}
				}

			}
		}
		for (int i = 0; i < Knapsacks.size(); i++) {
			mKP.ithKapsack2ithUnionKnapsack[i] = Knapsacks.elementAt(i).starNodeEnhancedVNID;
			System.out.print((mKP.ithKapsack2ithUnionKnapsack[i] + 1) + "\t ");
		}
		System.out.println();
		for (int i = 0; i < this.nodeSize; i++) {
			mKP.unionKnapsackCapacity[i] = this.nodeComputationCapacity[i];
			System.out.print((i + 1) + ":" + mKP.unionKnapsackCapacity[i] + "\t ");
		}
		System.out.println();
		for (int i = 0; i < this.VN.nodeSize; i++) {
			mKP.capacityItem[i] = this.VN.nodeComputationDemand[i];
			System.out.print(mKP.capacityItem[i] + "\t ");
		}
		System.out.println();

		for (int i = 0; i < this.VN.nodeSize; i++) {
			for (int j = 0; j < Knapsacks.size(); j++) {
				// System.out.print("i: "+i+"j: "+j+" --"+matchingMatrix[i][j]);
				if (mKP.matchingMatrix[i][j] == Integer.MAX_VALUE)
					System.out.print("00\t  ");
				else {
					// final int addNewNodeCost = 10000000;
					// final int transformExistedNodeCost = 100000;
					// final int addNodeComputaionCost = 1000;
					// final int addEdgeBandwithCost = 1;
					int printint = mKP.matchingMatrix[i][j];
					if ((mKP.matchingMatrix[i][j] / EVSNR.addNewNodeCost) > 0)
						System.out.print("N+");
					printint = printint % EVSNR.addNewNodeCost;
					if ((printint / EVSNR.transformExistedNodeCost) > 0)
						System.out.print("M+");
					printint = printint % EVSNR.transformExistedNodeCost;
					if ((printint / EVSNR.addNodeComputaionCost) > 0)
						System.out.print("(" + (printint / EVSNR.addNodeComputaionCost) + ")+");
					printint = printint % EVSNR.addNodeComputaionCost;
					System.out.print(printint + "\t  ");
				}
			}
			System.out.println("");
		}
		return false;

	}

	public void computeUsedResource() {
		int number = 0;
		int resource = 0;
		for (int i = 0; i < this.nodeSize; i++) {
			if (this.consumeNodeComputation[i] > 0) {
				resource += this.consumeNodeComputation[i];
				number++;
			}
		}
		System.out.print((number - this.consumedResource.consumedNodeNumber) + " Node + ");
		System.out.print((resource - this.consumedResource.consumedNodeComputation) + " Computation + ");
		this.consumedResource.consumedNodeNumber = number;
		this.consumedResource.consumedNodeComputation = resource;

		number = 0;
		for (int i = 0; i < this.nodeSize; i++) {
			for (int j = 0; j < this.nodeSize; j++) {
				number += this.consumeEdgeBandwith[i][j];
			}
		}
		number = number / 2;
		System.out.println((number - this.consumedResource.consumeEdgeBandwith) + " Bandwith \n");
		this.consumedResource.consumeEdgeBandwith = number;
	}

	public boolean HeursitcAlgorithm4Survivability(int failurenodenumber, boolean failurtype) throws GRBException {

		for (int i = 0; i < failurenodenumber; i++) {
			System.out.println("Begin " + i + " node failure");
			if (!failIthNode(i, failurtype)) {
				return false;
			}
			computeUsedResource();
		}
		return true;

	}

	/**
	 * @return
	 * 
	 */
	public boolean OptimalAlgorithmIP4Survivability() {
		// TODO Auto-generated method stub
		try {
			GRBEnv env = new GRBEnv("EeVSNAlgorithmIP");
			GRBModel model = new GRBModel(env);

			// Create variables
			GRBVar TransfromMatrix[][][];
			TransfromMatrix = new GRBVar[this.nodeSize4Embeded + 1][this.VN.nodeSize][this.nodeSize];
			for (int i = 0; i <= this.nodeSize4Embeded; i++) {
				for (int j = 0; j < this.VN.nodeSize; j++) {
					for (int k = 0; k < this.nodeSize; k++) {
						if (0 == i) {
							if (k == this.vNode2eNode[j]) {
								TransfromMatrix[i][j][k] = model.addVar(1.0, 1.0, 0.0, GRB.CONTINUOUS,
										"T" + i + " r:" + j + " c:" + k);
							} else {
								TransfromMatrix[i][j][k] = model.addVar(0.0, 0.0, 0.0, GRB.CONTINUOUS,

										"T" + i + " r:" + j + " c:" + k);
							}
						} else {
							TransfromMatrix[i][j][k] = model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS,
									"T" + i + " r:" + j + " c:" + k);
						}
					}
				}
			}

			GRBVar EnhancedGraphBandwithMatrix[][];
			EnhancedGraphBandwithMatrix = new GRBVar[this.nodeSize][this.nodeSize];
			for (int j = 0; j < this.nodeSize; j++) {
				for (int k = 0; k < this.nodeSize; k++) {
					EnhancedGraphBandwithMatrix[j][k] = model.addVar(0.0, Integer.MAX_VALUE, 0.0, GRB.CONTINUOUS,
							"EGBM" + " r:" + j + " c:" + k);
				} // Integer.MAX_VALUE
			}

			GRBVar UsedNodeVector[];
			UsedNodeVector = new GRBVar[this.nodeSize];
			for (int j = 0; j < this.nodeSize; j++) {
				if (j < this.nodeSize4Embeded)
					UsedNodeVector[j] = model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, "UNV" + ": " + j);
				else
					UsedNodeVector[j] = model.addVar(1.0, 1.0, 0.0, GRB.CONTINUOUS, "UNV" + ": " + j);
			}

			// Integrate new variables
			model.update();
			GRBLinExpr objexpr = new GRBLinExpr();
			for (int i = 0; i < this.nodeSize; i++) {
				objexpr.addTerm(EVSNR.addNewNodeCost, UsedNodeVector[i]);
				for (int j = 0; j < this.nodeSize; j++) {
					objexpr.addTerm(1.0, EnhancedGraphBandwithMatrix[i][j]);
				}
			}
			model.setObjective(objexpr, GRB.MINIMIZE);

			// Add constraint
			// regulate the TransfromMatrix
			for (int k = 0; k < this.nodeSize; k++) {
				for (int i = 0; i <= this.nodeSize4Embeded; i++) {
					for (int j = 0; j < this.VN.nodeSize; j++) {
						GRBLinExpr conexpr = new GRBLinExpr();
						conexpr.addTerm(1.0, TransfromMatrix[i][j][k]);
						model.addConstr(conexpr, GRB.LESS_EQUAL, UsedNodeVector[k],
								"Con usedNode:" + k + " " + i + " " + j);
					}
				}

			}

			for (int i = 0; i <= this.nodeSize4Embeded; i++) {
				for (int j = 0; j < this.VN.nodeSize; j++) {
					GRBLinExpr conexpr = new GRBLinExpr();
					for (int k = 0; k < this.nodeSize; k++) {
						conexpr.addTerm(1.0, TransfromMatrix[i][j][k]);
					}
					model.addConstr(conexpr, GRB.EQUAL, 1.0, "Con all VN Node have a embedding:" + i + " " + j);
				}
			}

			for (int i = 1; i <= this.nodeSize4Embeded; i++) {
				GRBLinExpr conexpr = new GRBLinExpr();
				for (int j = 0; j < this.VN.nodeSize; j++) {
					conexpr.addTerm(1.0, TransfromMatrix[i - 1][j][i - 1]);
				}
				model.addConstr(conexpr, GRB.EQUAL, 0.0, "Con Tiji=false :");
			}

			// T*ESM>VSM
			for (int i = 0; i <= this.nodeSize4Embeded; i++) {
				for (int j = 0; j < this.VN.nodeSize; j++) {
					for (int l = 0; l < this.serviceNumber; l++) {
						GRBLinExpr conexpr = new GRBLinExpr();
						for (int k = 0; k < this.nodeSize; k++) {
							if (this.boolServiceTypeSet[k][l])
								conexpr.addTerm(1.0, TransfromMatrix[i][j][k]);
						}
						if (this.VN.nodeServiceType[j] == (l)) {
							model.addConstr(conexpr, GRB.GREATER_EQUAL, 1.0, "T*ESM" + "T " + i + "r " + j + "c: " + l);
						} else {
							model.addConstr(conexpr, GRB.GREATER_EQUAL, 0.0, "T*ESM" + "T " + i + "r " + j + "c: " + l);
						}
					}
				}
			}

			// MACN*T<MBC
			for (int i = 0; i <= this.nodeSize4Embeded; i++) {
				for (int j = 0; j < this.nodeSize; j++) {
					GRBLinExpr conexpr = new GRBLinExpr();
					for (int k = 0; k < this.VN.nodeSize; k++) {
						conexpr.addTerm(this.VN.nodeComputationDemand[k], TransfromMatrix[i][k][j]);
					}
					model.addConstr(conexpr, GRB.LESS_EQUAL, this.nodeComputationCapacity[j],
							"MACN*T" + "T " + i + "r " + j);
				}
			}

			// (MAG*T)'*T<MBG

			for (int i = 0; i <= this.nodeSize4Embeded; i++) {
				// T'*MAG'
				GRBQuadExpr TMAG[][] = new GRBQuadExpr[this.nodeSize][this.nodeSize];
				for (int j = 0; j < this.nodeSize; j++) {
					for (int t = 0; t < this.nodeSize; t++) {
						TMAG[j][t] = new GRBQuadExpr();
						for (int k = 0; k < this.VN.nodeSize; k++) {
							for (int l = 0; l < this.VN.nodeSize; l++) {
								// TMAG[j][k].addTerm(this.VNR.edgeBandwithDemand[k][l],
								// TransfromMatrix[i][l][j]);
								for (int p = 0; p < this.VN.nodeSize; p++) {
									Integer inte = this.VN.edgeBandwithDemand[k][p];
									TMAG[j][t].addTerm(inte.doubleValue(), TransfromMatrix[i][k][j],
											TransfromMatrix[i][p][t]);
								}
							}
						}
						model.addQConstr(TMAG[j][t], GRB.LESS_EQUAL, EnhancedGraphBandwithMatrix[j][t],
								"T " + i + "T'*MAG'*T" + "r " + j + " c " + t);
					}
				}
			}
			model.optimize();
			int optimstatus = model.get(GRB.IntAttr.Status);
			if (optimstatus != GRB.OPTIMAL) {
				return false;
			}
			// for (int i = 0; i < this.nodeSize; i++) {
			// System.out.print(i + ": " +
			// UsedNodeVector[i].get(GRB.DoubleAttr.X) + " ");
			// }
			// System.out.println();

		} catch (GRBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;

	}

}
