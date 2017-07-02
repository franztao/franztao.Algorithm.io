/**
 * 
 */
package virtualnetwork;

/**
 * @author franz
 *
 */
public class VirtualNetworkParameter {
	// node parameter
	public int nodeSize = 4;
	public int nodeSizeMinimum = 2;
	public int nodeSizeMaximum = 10;
	public int nodeComputationMaximum = 20;
	public int nodeComputationMinimum = 5;

	// edge parameter
	public double node2nodeProbability = 0.4;
	public int edgeBandwithMinimum = 10;
	public int edgeBandwithMaximum = 30;

	// service parameter
	public int serviceNumber = 5;

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
}
