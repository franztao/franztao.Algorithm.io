/**
 * 
 */
package virtualnetwork;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import gurobi.*;
import multipleknapsack.MulitpleKnapsack;

/**
 * @author franz
 *
 */
public class EnhancedVirtualNetwork {

	public final boolean FailureDependent = true;
	public final boolean FailureIndependent = false;
	final int addNewNodeCost = 10000000;
	final int transformExistedNodeCost = 100000;
	final int addNodeComputaionCost = 1000;
	final int addEdgeBandwithCost = 1;

	public int nodeSize;
	public int embededNodeSize;
	public int backupNodeSize;
	// nodeSize=enhacnedNodeSize+backupNodeSize
	public int edgeSize;
	public boolean topology[][];

	// nodeComputationCapacity-usedNodeCurrentComputationCapacity
	public int nodeComputationCapacity[];
	// edgeBandwithCapacity-usedEdgeCurrentBandwithCapacity
	public int usedEdgeBandwith[][];

	public int usedNodeComputation[];
	// public int usedEdgeBandwith[][];

	public int serviceNumber;
	public boolean nodeServiceType[][];

	public String nodeLabel[];
	public Map<String, Integer> labeltoID;

	public int virtualNode2EnhancedVirtualNode[];

	public VirtualNetworkRequest VNR;

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

	public EnhancedVirtualNetwork(int nodeSize, int edgeSize, int serviceNumber, VirtualNetworkRequest VNR) {
		this.nodeSize = nodeSize;
		this.VNR = VNR;
		topology = new boolean[nodeSize][nodeSize];

		nodeComputationCapacity = new int[nodeSize];
		usedEdgeBandwith = new int[nodeSize][nodeSize];

		usedNodeComputation = new int[nodeSize];
		// usedEdgeBandwith = new int[nodeSize][nodeSize];

		nodeServiceType = new boolean[nodeSize][serviceNumber + 1];
		nodeLabel = new String[nodeSize];
		labeltoID = new HashMap<String, Integer>();

		virtualNode2EnhancedVirtualNode = new int[this.VNR.nodeSize];

		Items = new starStructure[this.VNR.nodeSize];

		this.serviceNumber = serviceNumber;

		this.usedResource = new UsedResource();

	}

	public void computeItems() {
		for (int i = 0; i < this.VNR.nodeSize; i++) {
			Items[i] = new starStructure();
			Items[i].starNodeVNID = i;
			Items[i].starNodeEnhancedVNID = this.virtualNode2EnhancedVirtualNode[i];
			Items[i].starNodeComputation = this.VNR.nodeComputationDemand[i];
			Items[i].starNodeType = this.VNR.nodeServiceType[i];
			Items[i].neighborEdge = new Vector<StarEdgeStructure>();
			for (int j = 0; j < this.VNR.nodeSize; j++) {
				if (this.VNR.topology[i][j]) {
					StarEdgeStructure edge = new StarEdgeStructure();
					edge.neighborVNID = j;
					edge.neighborEnhancedVNID = this.virtualNode2EnhancedVirtualNode[j];
					edge.neighborEdgeBandwithCapacity = this.VNR.edgeBandwithDemand[i][j];
					edge.neighborNodeType = this.VNR.nodeServiceType[j];
					Items[i].neighborEdge.addElement(edge);
				}
			}
		}
	}

	public void initSample1() {
		nodeComputationCapacity[0] = 5;
		nodeComputationCapacity[1] = 7;
		nodeComputationCapacity[2] = 7;
		nodeComputationCapacity[3] = 10;
		nodeComputationCapacity[4] = 6;
		nodeComputationCapacity[5] = 9;
		nodeComputationCapacity[6] = 8;

		usedEdgeBandwith[0][1] = 4;
		usedEdgeBandwith[0][2] = 5;
		usedEdgeBandwith[0][3] = 3;
		usedEdgeBandwith[1][2] = 6;
		usedEdgeBandwith[1][0] = 4;
		usedEdgeBandwith[2][0] = 5;
		usedEdgeBandwith[3][0] = 3;
		usedEdgeBandwith[2][1] = 6;

		for (int i = 0; i < nodeSize; i++) {
			for (int j = 0; j < nodeSize; j++) {
				if (0 != usedEdgeBandwith[i][j]) {
					topology[i][j] = true;
				}
			}
		}

		usedNodeComputation[0] = 2;
		usedNodeComputation[1] = 3;
		usedNodeComputation[2] = 5;
		usedNodeComputation[3] = 6;

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

		labeltoID.put("E1", 0);
		labeltoID.put("E2", 1);
		labeltoID.put("E3", 2);
		labeltoID.put("E4", 3);
		labeltoID.put("B1", 4);
		labeltoID.put("B2", 5);
		labeltoID.put("B3", 6);

		// there may be some virtual node located into the same enhanced node
		virtualNode2EnhancedVirtualNode[0] = 0;
		virtualNode2EnhancedVirtualNode[1] = 1;
		virtualNode2EnhancedVirtualNode[2] = 2;
		virtualNode2EnhancedVirtualNode[3] = 3;

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

		usedEdgeBandwith[0][1] = 3;
		usedEdgeBandwith[1][0] = 3;

		for (int i = 0; i < nodeSize; i++) {
			for (int j = 0; j < nodeSize; j++) {
				if (0 != usedEdgeBandwith[i][j]) {
					topology[i][j] = true;
				}
			}
		}

		usedNodeComputation[0] = 3;
		usedNodeComputation[1] = 4;

		nodeServiceType[0][1] = true;
		nodeServiceType[0][2] = true;
		nodeServiceType[1][2] = true;
		nodeServiceType[3][1] = true;
		nodeServiceType[3][2] = true;

		nodeLabel[0] = "E1";
		nodeLabel[1] = "E2";
		nodeLabel[2] = "B1";
		nodeLabel[3] = "B2";

		labeltoID.put("E1", 0);
		labeltoID.put("E2", 1);
		labeltoID.put("B1", 2);
		labeltoID.put("B2", 3);

		// there may be some virtual node located into the same enhanced node
		virtualNode2EnhancedVirtualNode[0] = 0;
		virtualNode2EnhancedVirtualNode[1] = 1;

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

		usedNodeComputation[0] = 3;

		nodeServiceType[0][1] = true;
		nodeServiceType[1][1] = true;
		nodeServiceType[1][2] = true;

		nodeLabel[0] = "E1";
		nodeLabel[1] = "E2";

		labeltoID.put("E1", 0);
		labeltoID.put("B1", 1);

		// there may be some virtual node located into the same enhanced node
		virtualNode2EnhancedVirtualNode[0] = 0;

		backupNodeSize = 1;
		embededNodeSize = 1;

		usedResource.initEdgeBandwith = 0;
		usedResource.initNodeComputation = 3;
		usedResource.initNodeNumber = 1;

		usedResource.usedEdgeBandwith = 0;
		usedResource.usedNodeComputation = 3;
		usedResource.usedNodeNumber = 1;
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
						bag.starNodeComputation = this.usedNodeComputation[i];
						// ineffective value
						bag.starNodeVNID = -1;
						bag.neighborEdge = new Vector<StarEdgeStructure>();
						for (int k = 0; k < this.VNR.nodeSize; k++) {
							if (failurenodeID != this.virtualNode2EnhancedVirtualNode[k]) {
								StarEdgeStructure edge = new StarEdgeStructure();
								edge.neighborVNID = k;
								edge.neighborEnhancedVNID = this.virtualNode2EnhancedVirtualNode[k];
								if (i == this.virtualNode2EnhancedVirtualNode[k])
									edge.neighborEdgeBandwithCapacity = Integer.MAX_VALUE;
								else
									edge.neighborEdgeBandwithCapacity = this.usedEdgeBandwith[i][this.virtualNode2EnhancedVirtualNode[k]];
								// if (i ==
								// this.virtualNode2EnhancedVirtualNode[k])
								// edge.neighborUsedEdgeBandwithCapacity =
								// Integer.MAX_VALUE;
								// else
								// edge.neighborUsedEdgeBandwithCapacity =
								// this.usedEdgeBandwith[i][this.virtualNode2EnhancedVirtualNode[k]];
								edge.neighborNodeType = this.VNR.nodeServiceType[k];
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
		MulitpleKnapsack MKP = new MulitpleKnapsack(this.VNR.nodeSize, Knapsacks.size(), this.nodeSize);
		constructMultipleKnapsackProbem(MKP, failurtype);
		if (failurenodeID == 3) {
			MKP.matchingMatrix[1][5] = Integer.MAX_VALUE;
		}
		int solution[] = new int[this.VNR.nodeSize];
		if (MKP.optimalSoutionILP(solution)) {
			augmentNodeEdge(solution, failurenodeID);
			return true;
		} else {
			System.out.println("Failure node: " + failurenodeID + ", there is not solution");
			return false;
		}
	}

	/**
	 * @param solution
	 */
	private void augmentNodeEdge(int[] solution, int failurenodeID) {
		// TODO Auto-generated method stub
		int virutialNode2NewVirtualNode[] = new int[this.VNR.nodeSize];
		for (int i = 0; i < this.VNR.nodeSize; i++) {
			virutialNode2NewVirtualNode[i] = Knapsacks.elementAt(solution[i]).starNodeEnhancedVNID;
			System.out.println(virutialNode2NewVirtualNode[i] + 1);
		}

		for (int i = 0; i < this.VNR.nodeSize; i++) {
			// add node computation
			if (this.usedNodeComputation[virutialNode2NewVirtualNode[i]] < Items[i].starNodeComputation)
				this.usedNodeComputation[virutialNode2NewVirtualNode[i]] = Items[i].starNodeComputation;
			for (int j = 0; j < Items[i].neighborEdge.size(); j++) {
				int neighborVNNode = Items[i].neighborEdge.elementAt(j).neighborVNID;
				// if (failurenodeID !=
				// virutialNode2NewVirtualNode[neighborVNNode]) {
				if (virutialNode2NewVirtualNode[i] != virutialNode2NewVirtualNode[neighborVNNode])
					if (this.usedEdgeBandwith[virutialNode2NewVirtualNode[i]][virutialNode2NewVirtualNode[neighborVNNode]] < Items[i].neighborEdge
							.elementAt(j).neighborEdgeBandwithCapacity) {
						this.usedEdgeBandwith[virutialNode2NewVirtualNode[i]][virutialNode2NewVirtualNode[neighborVNNode]] = Items[i].neighborEdge
								.elementAt(j).neighborEdgeBandwithCapacity;
						this.usedEdgeBandwith[virutialNode2NewVirtualNode[neighborVNNode]][virutialNode2NewVirtualNode[i]] = Items[i].neighborEdge
								.elementAt(j).neighborEdgeBandwithCapacity;
						this.topology[virutialNode2NewVirtualNode[i]][virutialNode2NewVirtualNode[neighborVNNode]] = this.topology[virutialNode2NewVirtualNode[neighborVNNode]][virutialNode2NewVirtualNode[i]] = true;
						// }
					}
			}
		}

	}

	boolean constructMultipleKnapsackProbem(MulitpleKnapsack mKP, boolean failurtype) {
		for (int i = 0; i < this.VNR.nodeSize; i++) {
			for (int j = 0; j < Knapsacks.size(); j++) {
				mKP.matchingMatrix[i][j] = Integer.MAX_VALUE;
				if ((failurtype == this.FailureIndependent)
						&& (Knapsacks.elementAt(j).starNodeEnhancedVNID < this.embededNodeSize)
						&& (Knapsacks.elementAt(j).starNodeEnhancedVNID != Items[i].starNodeEnhancedVNID)) {
					continue;
				}
				if (Items[i].starNodeType == Knapsacks.elementAt(j).starNodeType) {
					if (Items[i].starNodeComputation <= this.nodeComputationCapacity[Knapsacks
							.elementAt(j).starNodeEnhancedVNID]) {
						mKP.matchingMatrix[i][j] = 0;
						if (0 == this.usedNodeComputation[Knapsacks.elementAt(j).starNodeEnhancedVNID]) {
							mKP.matchingMatrix[i][j] += addNewNodeCost;
						}
						if (Items[i].starNodeEnhancedVNID != Knapsacks.elementAt(j).starNodeEnhancedVNID) {
							mKP.matchingMatrix[i][j] += transformExistedNodeCost;
						}
						if (Items[i].starNodeComputation > this.usedNodeComputation[Knapsacks
								.elementAt(j).starNodeEnhancedVNID]) {
							mKP.matchingMatrix[i][j] += (addNodeComputaionCost * (Items[i].starNodeComputation
									- this.usedNodeComputation[Knapsacks.elementAt(j).starNodeEnhancedVNID]));
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
		for (int i = 0; i < this.VNR.nodeSize; i++) {
			mKP.capacityItem[i] = this.VNR.nodeComputationDemand[i];
			System.out.print(mKP.capacityItem[i] + "\t ");
		}
		System.out.println();

		for (int i = 0; i < this.VNR.nodeSize; i++) {
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
					if ((mKP.matchingMatrix[i][j] / addNewNodeCost) > 0)
						System.out.print("N+");
					printint = printint % addNewNodeCost;
					if ((printint / transformExistedNodeCost) > 0)
						System.out.print("M+");
					printint = printint % transformExistedNodeCost;
					if ((printint / addNodeComputaionCost) > 0)
						System.out.print("(" + (printint / addNodeComputaionCost) + ")+");
					printint = printint % addNodeComputaionCost;
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
			if (this.usedNodeComputation[i] > 0) {
				resource += this.usedNodeComputation[i];
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
				number += this.usedEdgeBandwith[i][j];
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
			TransfromMatrix = new GRBVar[this.embededNodeSize + 1][this.VNR.nodeSize][this.nodeSize];
			for (int i = 0; i <= this.embededNodeSize; i++) {
				for (int j = 0; j < this.VNR.nodeSize; j++) {
					for (int k = 0; k < this.nodeSize; k++) {
						if (0 == i) {
							if (k == this.virtualNode2EnhancedVirtualNode[j]) {
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
				objexpr.addTerm(this.addNewNodeCost, UsedNodeVector[i]);
				for (int j = 0; j < this.nodeSize; j++) {
					objexpr.addTerm(1.0, EnhancedGraphBandwithMatrix[i][j]);
				}
			}
			model.setObjective(objexpr, GRB.MINIMIZE);

			// Add constraint
			// regulate the TransfromMatrix
			for (int k = 0; k < this.nodeSize; k++) {
				for (int i = 0; i <= this.embededNodeSize; i++) {
					for (int j = 0; j < this.VNR.nodeSize; j++) {
						GRBLinExpr conexpr = new GRBLinExpr();
						conexpr.addTerm(1.0, TransfromMatrix[i][j][k]);
						model.addConstr(conexpr, GRB.LESS_EQUAL, UsedNodeVector[k],
								"Con usedNode:" + k + " " + i + " " + j);
					}
				}

			}

			for (int i = 0; i <= this.embededNodeSize; i++) {
				for (int j = 0; j < this.VNR.nodeSize; j++) {
					GRBLinExpr conexpr = new GRBLinExpr();
					for (int k = 0; k < this.nodeSize; k++) {
						conexpr.addTerm(1.0, TransfromMatrix[i][j][k]);
					}
					model.addConstr(conexpr, GRB.EQUAL, 1.0, "Con all VN Node have a embedding:" + i+" "+j);
				}
			}

			for (int i = 1; i <= this.embededNodeSize; i++) {
				GRBLinExpr conexpr = new GRBLinExpr();
				for (int j = 0; j < this.VNR.nodeSize; j++) {
					conexpr.addTerm(1.0, TransfromMatrix[i - 1][j][i - 1]);
				}
				model.addConstr(conexpr, GRB.EQUAL, 0.0, "Con Tiji=false :" );
			}

			// T*ESM>VSM
			for (int i = 0; i <= this.embededNodeSize; i++) {
				for (int j = 0; j < this.VNR.nodeSize; j++) {
					for (int l = 0; l < this.serviceNumber; l++) {
						GRBLinExpr conexpr = new GRBLinExpr();
						for (int k = 0; k < this.nodeSize; k++) {
							if (this.nodeServiceType[k][l + 1])
								conexpr.addTerm(1.0, TransfromMatrix[i][j][k]);
						}
						if (this.VNR.nodeServiceType[j] == (l + 1)) {
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
					for (int k = 0; k < this.VNR.nodeSize; k++) {
						conexpr.addTerm(this.VNR.nodeComputationDemand[k], TransfromMatrix[i][k][j]);
					}
					model.addConstr(conexpr, GRB.LESS_EQUAL, this.nodeComputationCapacity[j],
							 "MACN*T" +"T " + i + "r " + j);
				}
			}

			// (MAG*T)'*T<MBG

			for (int i = 0; i <= this.embededNodeSize; i++) {
				// T'*MAG'
				GRBQuadExpr TMAG[][] = new GRBQuadExpr[this.nodeSize][this.nodeSize];
				for (int j = 0; j < this.nodeSize; j++) {
					for (int t = 0; t < this.nodeSize; t++) {
						TMAG[j][t] = new GRBQuadExpr();
						for (int k = 0; k < this.VNR.nodeSize; k++) {
							for (int l = 0; l < this.VNR.nodeSize; l++) {
								// TMAG[j][k].addTerm(this.VNR.edgeBandwithDemand[k][l],
								// TransfromMatrix[i][l][j]);
								for (int p = 0; p < this.VNR.nodeSize; p++) {
									Integer inte = this.VNR.edgeBandwithDemand[k][p];
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
			for (int i = 0; i < this.nodeSize; i++) {
				System.out.print(i + ": " + UsedNodeVector[i].get(GRB.DoubleAttr.X)+"  ");
			}
			System.out.println();

		} catch (GRBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;

	}

	

}
