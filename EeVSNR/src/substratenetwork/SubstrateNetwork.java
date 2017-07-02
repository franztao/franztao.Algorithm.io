package substratenetwork;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import virtualnetwork.VirtualNetworkParameter;
import virtualnetwork.EnhancedVirtualNetwork;
import virtualnetwork.VirtualNetwork;

public class SubstrateNetwork {
	public int nodeSize;
	public int edgeSize;
	public boolean topology[][];

	public int nodeComputationCapacity[];
	public int edgeBandwithCapacity[][];
	public int usedNodeCurrentComputationCapacity[];
	public int usedEdgeCurrentBandwithCapacity[][];

	public int serviceNumber;
	public boolean serviceType[][];
	public Vector<Integer> serviceTypeVector;

	public String nodeLabel[];
	public Map<String, Integer> labeltoID;

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
		usedNodeCurrentComputationCapacity = new int[nodeSize];

		// edge
		topology = new boolean[nodeSize][nodeSize];
		edgeBandwithCapacity = new int[nodeSize][nodeSize];
		usedEdgeCurrentBandwithCapacity = new int[nodeSize][nodeSize];

		// service
		this.serviceNumber = snp.serviceNumber;
		serviceType = new boolean[nodeSize][serviceNumber + 1];
		serviceTypeVector = new Vector<Integer>();

		// label
		nodeLabel = new String[nodeSize];
		labeltoID = new HashMap<String, Integer>();

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
					this.edgeBandwithCapacity[j][i] =this.edgeBandwithCapacity[i][j] ;
				}
			}
		}

		// service
		for (int i = 0; i < this.nodeSize; i++) {
			for (int j = 0; j < this.serviceNumber; j++) {
				if (Math.random() > snp.serivecProbability) {
					this.serviceType[i][j + 1] = true;
					serviceTypeVector.addElement(j + 1);
				}
			}
		}

		// label
		String str = "SN";
		for (int i = 0; i < this.nodeSize; i++) {
			nodeLabel[i] = (str + (i + 1));
			labeltoID.put((str + (1 + i)), i);
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

		serviceType[0][1] = true;
		serviceType[1][2] = true;
		serviceType[1][3] = true;
		serviceType[2][3] = true;
		serviceType[3][4] = true;
		serviceType[4][2] = true;
		serviceType[4][3] = true;
		serviceType[5][1] = true;
		serviceType[5][2] = true;
		serviceType[6][2] = true;
		serviceType[6][3] = true;
		serviceType[7][1] = true;
		serviceType[7][4] = true;
		serviceType[8][4] = true;

		nodeLabel[0] = "S1";
		nodeLabel[1] = "S2";
		nodeLabel[2] = "S3";
		nodeLabel[3] = "S4";
		nodeLabel[4] = "S5";
		nodeLabel[5] = "S6";
		nodeLabel[6] = "S7";
		nodeLabel[7] = "S8";
		nodeLabel[8] = "S9";

		labeltoID.put("S1", 0);
		labeltoID.put("S2", 1);
		labeltoID.put("S3", 2);
		labeltoID.put("S4", 3);
		labeltoID.put("S5", 4);
		labeltoID.put("S6", 5);
		labeltoID.put("S7", 6);
		labeltoID.put("S8", 7);
		labeltoID.put("S9", 8);
	}

	/**
	 * @param vnp
	 * 
	 */
	public void startExperiment(VirtualNetworkParameter vnp) {
		// read virtual network
		// for(int i=0;i<this.timeSpan;i++)
		// time slots
		VirtualNetwork vn = new VirtualNetwork(vnp);
		distributeVirtualNetwork(vn, vnp);

		vnquest.addElement(vn);
		// VNR1.initSample1();
	}

	/**
	 * @param vn
	 * @param vnp
	 */
	private boolean distributeVirtualNetwork(VirtualNetwork vn, VirtualNetworkParameter vnp) {
		Vector<Integer>[][] path = null;
		for (int i = 0; i < vn.nodeSize; i++) {
			int nodeloc = (int) Math.round(Math.random() * this.nodeSize);
			int nodeservice = this.serviceTypeVector
					.elementAt((int) Math.round(Math.random() * this.serviceTypeVector.size()));
			vn.nodeServiceType[i] = nodeservice;
			vn.vnode2snode[i] = nodeloc;
			vn.nodeComputationDemand[i] = (int) (vnp.nodeComputationMinimum
					+ Math.random() * (vnp.nodeComputationMaximum - vnp.nodeComputationMinimum));
			if (vn.nodeComputationDemand[i] == 0) {
				vn.nodeComputationDemand[i]++;
			}
			if (vn.nodeComputationDemand[i] > (this.nodeComputationCapacity[i]
					- this.usedNodeCurrentComputationCapacity[i])) {
				System.out.println("be not able to embed virtual network");
				return false;
			} else {
				this.usedNodeCurrentComputationCapacity[i] += vn.nodeComputationDemand[i];
			}
		}

		
		for (int i = 0; i < vn.nodeSize; i++) {
			for (int j = 0; j < i; j++) {
				double ran = Math.random();
				if (ran < vnp.node2nodeProbability) {
					// exist edge's path,bandwith
					path[i][j]=new Vector<Integer>();
					int bandwith = 1;//
					vn.topology[i][j] = true;
					vn.topology[j][i] = true;
					vn.edgeBandwithDemand[i][j] = bandwith;
					vn.edgeBandwithDemand[j][i] = bandwith;
				}
			}
		}
		
		//other backnode
		BackupNode bn=new BackupNode();
		
		EnhancedVirtualNetwork evn = new EnhancedVirtualNetwork(vn,path,bn);
		// EVNR1.initSample1();
		// EVNR1.computeItems();
		// EVNR1.HeursitcAlgorithm4Survivability(4, EVNR1.FailureDependent);
		// System.out.println("------------------" + "----------------");
		return true;

	}

}
