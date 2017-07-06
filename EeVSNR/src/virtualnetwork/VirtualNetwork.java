/**
 * 
 */
package virtualnetwork;

import java.util.HashMap;
import java.util.Map;

/**
 * @author franz
 *
 */
public class VirtualNetwork {
	public int nodeSize;
	public int vNode2sNode[];
	public int[] nodeComputationDemand;
	public int[] nodeComputationCapacity4embeded;

	public boolean topology[][];
	public int edgeBandwithDemand[][];

	public int serviceNumber;
	public int nodeServiceType[];

	public String node2Label[];
	public Map<String, Integer> label2Node;

	public boolean sampleInit;

	/**
	 * @param vnp
	 */
	public VirtualNetwork(VirtualNetworkParameter vnp) {
		if (vnp.sampleInit) {
			this.nodeSize = 4;
		} else
			this.nodeSize = (int) (vnp.nodeSizeMinimum + Math.random() * (vnp.nodeSizeMaximum - vnp.nodeSizeMinimum));
		this.vNode2sNode = new int[nodeSize];
		this.nodeComputationDemand = new int[nodeSize];
		this.nodeComputationCapacity4embeded = new int[nodeSize];

		this.topology = new boolean[nodeSize][nodeSize];
		this.edgeBandwithDemand = new int[nodeSize][nodeSize];

		this.serviceNumber = vnp.serviceNumber;
		this.nodeServiceType = new int[nodeSize];

		this.node2Label = new String[nodeSize];
		this.label2Node = new HashMap<String, Integer>();
		this.sampleInit = vnp.sampleInit;
		init();
		if (vnp.sampleInit) {
			// this.nodeSize = 4;
			initSample1();
		}
	}

	/**
	 * 
	 */
	private void init() {
		String str = "VN_";
		for (int i = 0; i < this.nodeSize; i++) {
			this.node2Label[i] = str + (i + 1);

			label2Node.put(str + (i + 1), 0);
		}
	}

	public void initSample1() {
		nodeComputationDemand[0] = 2;
		nodeComputationDemand[1] = 3;
		nodeComputationDemand[2] = 5;
		nodeComputationDemand[3] = 6;
		nodeComputationCapacity4embeded[0]=5;
		nodeComputationCapacity4embeded[1]=7;
		nodeComputationCapacity4embeded[2]=7;
		nodeComputationCapacity4embeded[3]=10;
		vNode2sNode[0] = 0;
		vNode2sNode[1] = 1;
		vNode2sNode[2] = 2;
		vNode2sNode[3] = 3;

		edgeBandwithDemand[0][1] = 4;
		edgeBandwithDemand[0][2] = 5;
		edgeBandwithDemand[0][3] = 3;
		edgeBandwithDemand[1][2] = 6;
		edgeBandwithDemand[1][0] = 4;
		edgeBandwithDemand[2][0] = 5;
		edgeBandwithDemand[3][0] = 3;
		edgeBandwithDemand[2][1] = 6;

		for (int i = 0; i < nodeSize; i++) {
			for (int j = 0; j < nodeSize; j++) {
				if (0 != edgeBandwithDemand[i][j]) {
					topology[i][j] = true;
				}
			}
		}

		nodeServiceType[0] = 0;
		nodeServiceType[1] = 1;
		nodeServiceType[2] = 2;
		nodeServiceType[3] = 3;
	}

	public void initSample2() {
		nodeComputationDemand[0] = 3;
		nodeComputationDemand[1] = 4;

		edgeBandwithDemand[0][1] = 3;
		edgeBandwithDemand[1][0] = 3;

		for (int i = 0; i < nodeSize; i++) {
			for (int j = 0; j < nodeSize; j++) {
				if (0 != edgeBandwithDemand[i][j]) {
					topology[i][j] = true;
				}
			}
		}

		nodeServiceType[0] = 0;
		nodeServiceType[1] = 1;
	}

	public void initSample3() {
		nodeComputationDemand[0] = 3;

		nodeServiceType[0] = 0;
	}
}
