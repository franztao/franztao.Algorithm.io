/**
 * 
 */
package virtualnetwork;

import evsnr.EVSNR;

/**
 * @author franz
 *
 */
public class VirtualNetworkParameter {
	// node parameter
	public int nodeSize;// = EVSNR.VirtualNodeSize;
	public int nodeSizeMinimum = EVSNR.VirtualNodeSizeMinimum;
	public int nodeSizeMaximum = EVSNR.VirtualNodeSizeMaximum;
	public int nodeComputationMinimum = EVSNR.VirtualNodeComputationMinimum;
	public int nodeComputationMaximum = EVSNR.VirtualNodeComputationMaximum;

	// edge parameter
	public double node2nodeProbability = EVSNR.VirtualNodenodeProbability;
	public int edgeBandwithMinimum = EVSNR.VirtualEdgeBandwithMinimum;
	public int edgeBandwithMaximum = EVSNR.VirtualEdgeBandwithMaximum;

	// service parameter
	public int serviceNumber = EVSNR.ServiceNumber;

	public boolean sampleInit;

	/**
	 * @param sampleInit2
	 */
	public VirtualNetworkParameter(boolean sampleInit2) {
		this.sampleInit = sampleInit2;
		if (sampleInit2) {
			this.nodeSize = 4;
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
	 * @param nodeSize
	 *            the nodeSize to set
	 */
	public void setNodeSize(int nodeSize) {
		this.nodeSize = nodeSize;
	}

	/**
	 * @return the nodeSizeMinimum
	 */
	public int getNodeSizeMinimum() {
		return nodeSizeMinimum;
	}

	/**
	 * @param nodeSizeMinimum
	 *            the nodeSizeMinimum to set
	 */
	public void setNodeSizeMinimum(int nodeSizeMinimum) {
		this.nodeSizeMinimum = nodeSizeMinimum;
	}

	/**
	 * @return the nodeSizeMaximum
	 */
	public int getNodeSizeMaximum() {
		return nodeSizeMaximum;
	}

	/**
	 * @param nodeSizeMaximum
	 *            the nodeSizeMaximum to set
	 */
	public void setNodeSizeMaximum(int nodeSizeMaximum) {
		this.nodeSizeMaximum = nodeSizeMaximum;
	}

	/**
	 * @return the nodeComputationMinimum
	 */
	public int getNodeComputationMinimum() {
		return nodeComputationMinimum;
	}

	/**
	 * @param nodeComputationMinimum
	 *            the nodeComputationMinimum to set
	 */
	public void setNodeComputationMinimum(int nodeComputationMinimum) {
		this.nodeComputationMinimum = nodeComputationMinimum;
	}

	/**
	 * @return the nodeComputationMaximum
	 */
	public int getNodeComputationMaximum() {
		return nodeComputationMaximum;
	}

	/**
	 * @param nodeComputationMaximum
	 *            the nodeComputationMaximum to set
	 */
	public void setNodeComputationMaximum(int nodeComputationMaximum) {
		this.nodeComputationMaximum = nodeComputationMaximum;
	}

	/**
	 * @return the node2nodeProbability
	 */
	public double getNode2nodeProbability() {
		return node2nodeProbability;
	}

	/**
	 * @param node2nodeProbability
	 *            the node2nodeProbability to set
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
	 * @param edgeBandwithMinimum
	 *            the edgeBandwithMinimum to set
	 */
	public void setEdgeBandwithMinimum(int edgeBandwithMinimum) {
		this.edgeBandwithMinimum = edgeBandwithMinimum;
	}

	/**
	 * @return the edgeBandwithMaximum
	 */
	public int getEdgeBandwithMaximum() {
		return edgeBandwithMaximum;
	}

	/**
	 * @param edgeBandwithMaximum
	 *            the edgeBandwithMaximum to set
	 */
	public void setEdgeBandwithMaximum(int edgeBandwithMaximum) {
		this.edgeBandwithMaximum = edgeBandwithMaximum;
	}

	/**
	 * @return the serviceNumber
	 */
	public int getServiceNumber() {
		return serviceNumber;
	}

	/**
	 * @param serviceNumber
	 *            the serviceNumber to set
	 */
	public void setServiceNumber(int serviceNumber) {
		this.serviceNumber = serviceNumber;
	}

	/**
	 * @return the sampleInit
	 */
	public boolean isSampleInit() {
		return sampleInit;
	}

	/**
	 * @param sampleInit
	 *            the sampleInit to set
	 */
	public void setSampleInit(boolean sampleInit) {
		this.sampleInit = sampleInit;
	}
}
