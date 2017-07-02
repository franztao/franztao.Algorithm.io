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
	public boolean topology[][];
	public int vnode2snode[];

	public int nodeComputationDemand[];
	public int edgeBandwithDemand[][];

	public int serviceNumber;
	public int nodeServiceType[];

	public String nodeLabel[];
	public Map<String, Integer> labeltoID;

	public boolean sampleInit;
	/**
	 * @param vnp
	 */
	public VirtualNetwork(VirtualNetworkParameter vnp) {
		this.nodeSize = (int) (vnp.nodeSizeMinimum + Math.random() * (vnp.nodeSizeMaximum - vnp.nodeSizeMinimum));
		this.vnode2snode = new int[nodeSize];
		this.nodeComputationDemand = new int[nodeSize];

		this.topology = new boolean[nodeSize][nodeSize];
		this.edgeBandwithDemand = new int[nodeSize][nodeSize];

		this.serviceNumber = vnp.serviceNumber;
		this.nodeServiceType = new int[nodeSize + 1];

		this.nodeLabel = new String[nodeSize];
		this.labeltoID = new HashMap<String, Integer>();
		this.sampleInit=vnp.sampleInit;
		init();
		if (vnp.sampleInit) {
			this.nodeSize = 4;
			initSample1();
		}
	}

	/**
	 * 
	 */
	private void init() {
		String str = "VN_";
		for (int i = 0; i < this.nodeSize; i++) {
			this.nodeLabel[i] = str + (i + 1);

			labeltoID.put(str + (i + 1), 0);
		}
	}

	public void initSample1() {
		nodeComputationDemand[0] = 2;
		nodeComputationDemand[1] = 3;
		nodeComputationDemand[2] = 5;
		nodeComputationDemand[3] = 6;

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

		nodeServiceType[0] = 1;
		nodeServiceType[1] = 2;
		nodeServiceType[2] = 3;
		nodeServiceType[3] = 4;
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

		nodeServiceType[0] = 1;
		nodeServiceType[1] = 2;
	}

	public void initSample3() {
		nodeComputationDemand[0] = 3;

		nodeServiceType[0] = 1;
	}
}
