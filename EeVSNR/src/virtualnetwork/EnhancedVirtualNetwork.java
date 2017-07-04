/**
 * 
 */
package virtualnetwork;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import evsnr.EVSNR;
import gurobi.*;
import multipleknapsack.MulitpleKnapsack;
import substratenetwork.BackupNode;

/**
 * @author franz
 *
 */
public class EnhancedVirtualNetwork {

	public final int Method = EVSNR.MethodILP;

	// nodeSize=enhacnedNodeSize+backupNodeSize
	public int nodeSize;
	public int embededNodeSize;
	public int backupNodeSize;
	// nodeComputationCapacity-usedNodeCurrentComputationCapacity
	public int nodeComputationCapacity[];
	public int needNodeComputation[];

	public boolean topology[][];
	// edgeBandwithCapacity-usedEdgeCurrentBandwithCapacity
	public int needEdgeBandwith[][];

	public int serviceNumber;
	public boolean nodeServiceType[][];

	public String nodeLabel[];
	public Map<String, Integer> label2ID;

	public int vNode2eNode[];

	public VirtualNetwork VN;

	public boolean sampleInit;

	class StarEdgeStructure {
		int neighborVNID;
		int neighborEnhancedVNID;
		int neighborNodeType;
		int neighborEdgeBandwithCapacity;
		// public int neighborUsedEdgeBandwithCapacity;

	}

	class starStructure {
		int starNodeType;
		int starNodeComputation;
		int starNodeEnhancedVNID;
		int starNodeVNID;
		Vector<StarEdgeStructure> neighborEdge;
		// Vector<Integer> neighborEnhancedVNID;
		// Vector<Integer> neighborEnhancedVNType;
		// Vector<Integer> neighborEdgeBandwith;
	}

	class UsedResource {
		int initNodeNumber;
		int initNodeComputation;
		int initEdgeBandwith;

		int usedNodeNumber;
		int usedNodeComputation;
		int usedEdgeBandwith;
	}

	UsedResource usedResource;

	starStructure Items[];
	Vector<starStructure> Knapsacks;

	/**
	 * @param vn
	 * @param path
	 * @param bn
	 */
	public EnhancedVirtualNetwork(VirtualNetwork vn, List<Integer>[][] path, BackupNode bn) {
		this.nodeSize = vn.nodeSize + bn.backupNodeSize;
		nodeComputationCapacity = new int[this.nodeSize];
		needNodeComputation = new int[this.nodeSize];

		this.VN = vn;
		topology = new boolean[this.nodeSize][this.nodeSize];
		needEdgeBandwith = new int[this.nodeSize][this.nodeSize];

		nodeServiceType = new boolean[nodeSize][serviceNumber + 1];
		nodeLabel = new String[nodeSize];
		label2ID = new HashMap<String, Integer>();

		vNode2eNode = new int[this.VN.nodeSize];

		Items = new starStructure[this.VN.nodeSize];

		this.serviceNumber = vn.serviceNumber;

		this.usedResource = new UsedResource();
		
		this.sampleInit=vn.sampleInit;
		if(this.sampleInit){
			initSample1();
		}else{
			constrcutEVN(vn,path,bn);
		}
	}

	/**
	 * @param vn2
	 * @param path
	 * @param bn
	 */
	private void constrcutEVN(VirtualNetwork vn2, List<Integer>[][] path, BackupNode bn) {
		// TODO Auto-generated method stub
		
	}

	public void initSample1() {
		nodeComputationCapacity[0] = 5;
		nodeComputationCapacity[1] = 7;
		nodeComputationCapacity[2] = 7;
		nodeComputationCapacity[3] = 10;
		nodeComputationCapacity[4] = 6;
		nodeComputationCapacity[5] = 9;
		nodeComputationCapacity[6] = 8;

		needEdgeBandwith[0][1] = 4;
		needEdgeBandwith[0][2] = 5;
		needEdgeBandwith[0][3] = 3;
		needEdgeBandwith[1][2] = 6;
		needEdgeBandwith[1][0] = 4;
		needEdgeBandwith[2][0] = 5;
		needEdgeBandwith[3][0] = 3;
		needEdgeBandwith[2][1] = 6;

		for (int i = 0; i < nodeSize; i++) {
			for (int j = 0; j < nodeSize; j++) {
				if (0 != needEdgeBandwith[i][j]) {
					topology[i][j] = true;
				}
			}
		}

		needNodeComputation[0] = 2;
		needNodeComputation[1] = 3;
		needNodeComputation[2] = 5;
		needNodeComputation[3] = 6;

		nodeServiceType[0][1] = true;
		nodeServiceType[1][2] = true;
		nodeServiceType[1][3] = true;
		nodeServiceType[2][3] = true;
		nodeServiceType[3][4] = true;
		nodeServiceType[4][1] = true;
		nodeServiceType[4][2] = true;
		nodeServiceType[5][1] = true;
		nodeServiceType[5][4] = true;
		nodeServiceType[6][2] = true;
		nodeServiceType[6][3] = true;

		nodeLabel[0] = "E1";
		nodeLabel[1] = "E2";
		nodeLabel[2] = "E3";
		nodeLabel[3] = "E4";
		nodeLabel[4] = "B1";
		nodeLabel[5] = "B2";
		nodeLabel[6] = "B3";

		label2ID.put("E1", 0);
		label2ID.put("E2", 1);
		label2ID.put("E3", 2);
		label2ID.put("E4", 3);
		label2ID.put("B1", 4);
		label2ID.put("B2", 5);
		label2ID.put("B3", 6);

		// there may be some virtual node located into the same enhanced node
		vNode2eNode[0] = 0;
		vNode2eNode[1] = 1;
		vNode2eNode[2] = 2;
		vNode2eNode[3] = 3;

		backupNodeSize = 3;
		embededNodeSize = 4;

		usedResource.initEdgeBandwith = 18;
		usedResource.initNodeComputation = 16;
		usedResource.initNodeNumber = 4;

		usedResource.usedEdgeBandwith = 18;
		usedResource.usedNodeComputation = 16;
		usedResource.usedNodeNumber = 4;

	}

	/**
	 * 
	 */
	public void initSample2() {
		nodeComputationCapacity[0] = 7;
		nodeComputationCapacity[1] = 5;
		nodeComputationCapacity[2] = 3;
		nodeComputationCapacity[3] = 7;

		needEdgeBandwith[0][1] = 3;
		needEdgeBandwith[1][0] = 3;

		for (int i = 0; i < nodeSize; i++) {
			for (int j = 0; j < nodeSize; j++) {
				if (0 != needEdgeBandwith[i][j]) {
					topology[i][j] = true;
				}
			}
		}

		needNodeComputation[0] = 3;
		needNodeComputation[1] = 4;

		nodeServiceType[0][1] = true;
		nodeServiceType[0][2] = true;
		nodeServiceType[1][2] = true;
		nodeServiceType[2][1] = true;
		nodeServiceType[3][1] = true;
		nodeServiceType[3][2] = true;

		nodeLabel[0] = "E1";
		nodeLabel[1] = "E2";
		nodeLabel[2] = "B1";
		nodeLabel[3] = "B2";

		label2ID.put("E1", 0);
		label2ID.put("E2", 1);
		label2ID.put("B1", 2);
		label2ID.put("B2", 3);

		// there may be some virtual node located into the same enhanced node
		vNode2eNode[0] = 0;
		vNode2eNode[1] = 1;

		backupNodeSize = 2;
		embededNodeSize = 2;

		usedResource.initEdgeBandwith = 3;
		usedResource.initNodeComputation = 7;
		usedResource.initNodeNumber = 2;

		usedResource.usedEdgeBandwith = 3;
		usedResource.usedNodeComputation = 7;
		usedResource.usedNodeNumber = 2;
	}

	/**
	 * 
	 */
	public void initSample3() {
		// TODO Auto-generated method stub
		nodeComputationCapacity[0] = 5;
		nodeComputationCapacity[1] = 5;

		needNodeComputation[0] = 3;

		nodeServiceType[0][1] = true;
		nodeServiceType[1][1] = true;
		nodeServiceType[1][2] = true;

		nodeLabel[0] = "E1";
		nodeLabel[1] = "E2";

		label2ID.put("E1", 0);
		label2ID.put("B1", 1);

		// there may be some virtual node located into the same enhanced node
		vNode2eNode[0] = 0;

		backupNodeSize = 1;
		embededNodeSize = 1;

		usedResource.initEdgeBandwith = 0;
		usedResource.initNodeComputation = 3;
		usedResource.initNodeNumber = 1;

		usedResource.usedEdgeBandwith = 0;
		usedResource.usedNodeComputation = 3;
		usedResource.usedNodeNumber = 1;
	}

	public void computeItems() {
		for (int i = 0; i < this.VN.nodeSize; i++) {
			Items[i] = new starStructure();
			Items[i].starNodeVNID = i;
			Items[i].starNodeEnhancedVNID = this.vNode2eNode[i];
			Items[i].starNodeComputation = this.VN.nodeComputationDemand[i];
			Items[i].starNodeType = this.VN.nodeServiceType[i];
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
				for (int j = 1; j <= this.serviceNumber; j++) {
					if (this.nodeServiceType[i][j]) {
						starStructure bag = new starStructure();
						bag.starNodeType = j;
						bag.starNodeEnhancedVNID = i;
						bag.starNodeComputation = this.needNodeComputation[i];
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
									edge.neighborEdgeBandwithCapacity = this.needEdgeBandwith[i][this.vNode2eNode[k]];
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

	public boolean ithNode(int failurenodeID, boolean failurtype) throws GRBException {
		constructKnapsacks(failurenodeID);
		MulitpleKnapsack MKP = new MulitpleKnapsack(this.VN.nodeSize, Knapsacks.size(), this.nodeSize);
		constructMultipleKnapsackProbem(MKP, failurtype);
		if (failurenodeID == 3) {
			MKP.matchingMatrix[1][5] = Integer.MAX_VALUE;
		}
		int solution[] = new int[this.VN.nodeSize];

		if (this.Method == EVSNR.MethodDP) {
			if (!MKP.optimalSoutionDP(solution)) {
				System.out.println("Failure node: " + failurenodeID + ", there is not solution");
				return false;
			}
		}

		if (this.Method == EVSNR.MethodILP) {
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
		if (Method == EVSNR.MethodILP) {
			for (int i = 0; i < this.VN.nodeSize; i++) {
				virutialNode2NewVirtualNode[i] = Knapsacks.elementAt(solution[i]).starNodeEnhancedVNID;
				// System.out.println("00000 "+virutialNode2NewVirtualNode[i] +
				// 1);
			}
		}
		if (Method == EVSNR.MethodDP) {
			for (int i = 0; i < this.VN.nodeSize; i++) {
				virutialNode2NewVirtualNode[i] = solution[i];
				// System.out.println(virutialNode2NewVirtualNode[i] + 1);
			}
		}

		for (int i = 0; i < this.VN.nodeSize; i++) {
			// add node computation
			if (this.needNodeComputation[virutialNode2NewVirtualNode[i]] < Items[i].starNodeComputation)
				this.needNodeComputation[virutialNode2NewVirtualNode[i]] = Items[i].starNodeComputation;
			for (int j = 0; j < Items[i].neighborEdge.size(); j++) {
				int neighborVNNode = Items[i].neighborEdge.elementAt(j).neighborVNID;
				// if (failurenodeID !=
				// virutialNode2NewVirtualNode[neighborVNNode]) {
				if (virutialNode2NewVirtualNode[i] != virutialNode2NewVirtualNode[neighborVNNode])
					if (this.needEdgeBandwith[virutialNode2NewVirtualNode[i]][virutialNode2NewVirtualNode[neighborVNNode]] < Items[i].neighborEdge
							.elementAt(j).neighborEdgeBandwithCapacity) {
						this.needEdgeBandwith[virutialNode2NewVirtualNode[i]][virutialNode2NewVirtualNode[neighborVNNode]] = Items[i].neighborEdge
								.elementAt(j).neighborEdgeBandwithCapacity;
						this.needEdgeBandwith[virutialNode2NewVirtualNode[neighborVNNode]][virutialNode2NewVirtualNode[i]] = Items[i].neighborEdge
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
						&& (Knapsacks.elementAt(j).starNodeEnhancedVNID < this.embededNodeSize)
						&& (Knapsacks.elementAt(j).starNodeEnhancedVNID != Items[i].starNodeEnhancedVNID)) {
					continue;
				}
				if (Items[i].starNodeType == Knapsacks.elementAt(j).starNodeType) {
					if (Items[i].starNodeComputation <= this.nodeComputationCapacity[Knapsacks
							.elementAt(j).starNodeEnhancedVNID]) {
						mKP.matchingMatrix[i][j] = 0;
						if (0 == this.needNodeComputation[Knapsacks.elementAt(j).starNodeEnhancedVNID]) {
							mKP.matchingMatrix[i][j] += EVSNR.addNewNodeCost;
						}
						if (Items[i].starNodeEnhancedVNID != Knapsacks.elementAt(j).starNodeEnhancedVNID) {
							mKP.matchingMatrix[i][j] += EVSNR.transformExistedNodeCost;
						}
						if (Items[i].starNodeComputation > this.needNodeComputation[Knapsacks
								.elementAt(j).starNodeEnhancedVNID]) {
							mKP.matchingMatrix[i][j] += (EVSNR.addNodeComputaionCost * (Items[i].starNodeComputation
									- this.needNodeComputation[Knapsacks.elementAt(j).starNodeEnhancedVNID]));
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
			if (this.needNodeComputation[i] > 0) {
				resource += this.needNodeComputation[i];
				number++;
			}
		}
		System.out.print((number - this.usedResource.usedNodeNumber) + " Node + ");
		System.out.print((resource - this.usedResource.usedNodeComputation) + " Computation + ");
		this.usedResource.usedNodeNumber = number;
		this.usedResource.usedNodeComputation = resource;

		number = 0;
		for (int i = 0; i < this.nodeSize; i++) {
			for (int j = 0; j < this.nodeSize; j++) {
				number += this.needEdgeBandwith[i][j];
			}
		}
		number = number / 2;
		System.out.println((number - this.usedResource.usedEdgeBandwith) + " Bandwith \n");
		this.usedResource.usedEdgeBandwith = number;
	}

	public boolean HeursitcAlgorithm4Survivability(int failurenodenumber, boolean failurtype) throws GRBException {
		System.out.println("------------------" + failurtype + "----------------");
		for (int i = 0; i < failurenodenumber; i++) {
			if (!ithNode(i, failurtype)) {
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
			TransfromMatrix = new GRBVar[this.embededNodeSize + 1][this.VN.nodeSize][this.nodeSize];
			for (int i = 0; i <= this.embededNodeSize; i++) {
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
				if (j < this.embededNodeSize)
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
				for (int i = 0; i <= this.embededNodeSize; i++) {
					for (int j = 0; j < this.VN.nodeSize; j++) {
						GRBLinExpr conexpr = new GRBLinExpr();
						conexpr.addTerm(1.0, TransfromMatrix[i][j][k]);
						model.addConstr(conexpr, GRB.LESS_EQUAL, UsedNodeVector[k],
								"Con usedNode:" + k + " " + i + " " + j);
					}
				}

			}

			for (int i = 0; i <= this.embededNodeSize; i++) {
				for (int j = 0; j < this.VN.nodeSize; j++) {
					GRBLinExpr conexpr = new GRBLinExpr();
					for (int k = 0; k < this.nodeSize; k++) {
						conexpr.addTerm(1.0, TransfromMatrix[i][j][k]);
					}
					model.addConstr(conexpr, GRB.EQUAL, 1.0, "Con all VN Node have a embedding:" + i + " " + j);
				}
			}

			for (int i = 1; i <= this.embededNodeSize; i++) {
				GRBLinExpr conexpr = new GRBLinExpr();
				for (int j = 0; j < this.VN.nodeSize; j++) {
					conexpr.addTerm(1.0, TransfromMatrix[i - 1][j][i - 1]);
				}
				model.addConstr(conexpr, GRB.EQUAL, 0.0, "Con Tiji=false :");
			}

			// T*ESM>VSM
			for (int i = 0; i <= this.embededNodeSize; i++) {
				for (int j = 0; j < this.VN.nodeSize; j++) {
					for (int l = 0; l < this.serviceNumber; l++) {
						GRBLinExpr conexpr = new GRBLinExpr();
						for (int k = 0; k < this.nodeSize; k++) {
							if (this.nodeServiceType[k][l + 1])
								conexpr.addTerm(1.0, TransfromMatrix[i][j][k]);
						}
						if (this.VN.nodeServiceType[j] == (l + 1)) {
							model.addConstr(conexpr, GRB.GREATER_EQUAL, 1.0, "T*ESM" + "T " + i + "r " + j + "c: " + l);
						} else {
							model.addConstr(conexpr, GRB.GREATER_EQUAL, 0.0, "T*ESM" + "T " + i + "r " + j + "c: " + l);
						}
					}
				}
			}

			// MACN*T<MBC
			for (int i = 0; i <= this.embededNodeSize; i++) {
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

			for (int i = 0; i <= this.embededNodeSize; i++) {
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
