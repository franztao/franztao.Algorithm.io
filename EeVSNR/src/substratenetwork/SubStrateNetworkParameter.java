/**
 * 
 */
package substratenetwork;

import evsnr.EVSNR;

/**
 * @author franz
 *
 */
public class SubStrateNetworkParameter {

	// node parameter
	private int nodeSize = EVSNR.SubStrateNodeSize;
	private int nodeComputationMinimum = EVSNR.SubStrateNodeComputationMinimum;
	private int nodeComputationMaximum = EVSNR.SubStrateNodeComputationMaximum;
	

	// edge parameter
	private double node2nodeProbability = EVSNR.SubStrateNodenodeProbability;
	private int edgeBandwithMinimum = EVSNR.SubStrateEdgeBandwithMinimum;
	private int edgeBandwithMaximum = EVSNR.SubStrateEdgeBandwithMaximum;

	// service parameter
	private int serviceNumber = EVSNR.ServiceNumber;
	private double serivecProbability = EVSNR.SerivecProbability;


		
	private boolean sampleInit;
	
	/**
	 * @param sampleInit
	 */
	public SubStrateNetworkParameter(boolean sampleInit) {
		this.sampleInit = sampleInit;
		if (sampleInit) {
			this.nodeSize = 9;
			this.serviceNumber = 4;
		}
	}

	/**
	 * @return the nodeSize
	 */
	public int getNodeSize() {
		return nodeSize;
	}

	/**
	 * @param nodeSize the nodeSize to set
	 */
	public void setNodeSize(int nodeSize) {
		this.nodeSize = nodeSize;
	}

	/**
	 * @return the nodeComputationMaximum
	 */
	public int getNodeComputationMaximum() {
		return nodeComputationMaximum;
	}

	/**
	 * @param nodeComputationMaximum the nodeComputationMaximum to set
	 */
	public void setNodeComputationMaximum(int nodeComputationMaximum) {
		this.nodeComputationMaximum = nodeComputationMaximum;
	}

	/**
	 * @return the nodeComputationMinimum
	 */
	public int getNodeComputationMinimum() {
		return nodeComputationMinimum;
	}

	/**
	 * @param nodeComputationMinimum the nodeComputationMinimum to set
	 */
	public void setNodeComputationMinimum(int nodeComputationMinimum) {
		this.nodeComputationMinimum = nodeComputationMinimum;
	}

	/**
	 * @return the edgeBandwithMaximum
	 */
	public int getEdgeBandwithMaximum() {
		return edgeBandwithMaximum;
	}

	/**
	 * @param edgeBandwithMaximum the edgeBandwithMaximum to set
	 */
	public void setEdgeBandwithMaximum(int edgeBandwithMaximum) {
		this.edgeBandwithMaximum = edgeBandwithMaximum;
	}

	/**
	 * @return the node2nodeProbability
	 */
	public double getNode2nodeProbability() {
		return node2nodeProbability;
	}

	/**
	 * @param node2nodeProbability the node2nodeProbability to set
	 */
	public void setNode2nodeProbability(double node2nodeProbability) {
		this.node2nodeProbability = node2nodeProbability;
	}

	/**
	 * @return the edgeBandwithMinimum
	 */
	public int getEdgeBandwithMinimum() {
		return edgeBandwithMinimum;
	}

	/**
	 * @param edgeBandwithMinimum the edgeBandwithMinimum to set
	 */
	public void setEdgeBandwithMinimum(int edgeBandwithMinimum) {
		this.edgeBandwithMinimum = edgeBandwithMinimum;
	}

	/**
	 * @return the serviceNumber
	 */
	public int getServiceNumber() {
		return serviceNumber;
	}

	/**
	 * @param serviceNumber the serviceNumber to set
	 */
	public void setServiceNumber(int serviceNumber) {
		this.serviceNumber = serviceNumber;
	}

	/**
	 * @return the serivecProbability
	 */
	public double getSerivecProbability() {
		return serivecProbability;
	}

	/**
	 * @param serivecProbability the serivecProbability to set
	 */
	public void setSerivecProbability(double serivecProbability) {
		this.serivecProbability = serivecProbability;
	}

	/**
	 * @return the sampleInit
	 */
	public boolean isSampleInit() {
		return sampleInit;
	}

	/**
	 * @param sampleInit the sampleInit to set
	 */
	public void setSampleInit(boolean sampleInit) {
		this.sampleInit = sampleInit;
	}


}
