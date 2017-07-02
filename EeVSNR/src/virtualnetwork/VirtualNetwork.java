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
public class VirtualNetworkRequest {
	public int nodeSize;
	public int edgeSize;
	public boolean topology[][];

	public int nodeComputationDemand[];
	public int edgeBandwithDemand[][];

	public int serviceNumber;
	public int nodeServiceType[];

	public String nodeLabel[];
	public Map<String, Integer> labeltoID;

	public VirtualNetworkRequest(int nodeSize, int edgeSize, int serviceNumber) {
		this.nodeSize = nodeSize;
		this.serviceNumber = serviceNumber;
		topology = new boolean[nodeSize][nodeSize];

		nodeComputationDemand = new int[nodeSize];
		edgeBandwithDemand = new int[nodeSize][nodeSize];

		nodeServiceType = new int[nodeSize + 1];
		nodeLabel = new String[nodeSize];
		labeltoID = new HashMap<String, Integer>();
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

		edgeSize = 4;

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

		nodeLabel[0] = "V1";
		nodeLabel[1] = "V2";
		nodeLabel[2] = "V3";
		nodeLabel[3] = "V4";

		labeltoID.put("V1", 0);
		labeltoID.put("V2", 1);
		labeltoID.put("V3", 2);
		labeltoID.put("V4", 3);
	}

	/**
	 * 
	 */
	public void initSample2() {
		nodeComputationDemand[0] = 3;
		nodeComputationDemand[1] = 4;

		edgeBandwithDemand[0][1] = 3;
		edgeBandwithDemand[1][0] = 3;

		edgeSize = 1;

		for (int i = 0; i < nodeSize; i++) {
			for (int j = 0; j < nodeSize; j++) {
				if (0 != edgeBandwithDemand[i][j]) {
					topology[i][j] = true;
				}
			}
		}

		nodeServiceType[0] = 1;
		nodeServiceType[1] = 2;

		nodeLabel[0] = "V1";
		nodeLabel[1] = "V2";

		labeltoID.put("V1", 0);
		labeltoID.put("V2", 1);
	}

	public void initSample3() {
		nodeComputationDemand[0] = 3;

		edgeSize = 0;


		nodeServiceType[0] = 1;

		nodeLabel[0] = "V1";

		labeltoID.put("V1", 0);
		
	}
}
