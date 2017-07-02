/**
 * 
 */
package substratenetwork;

/**
 * @author franz
 *
 */
public class SubStrateNetworkParameter {

	// node parameter
	public int nodeSize = 40;
	public int nodeComputationMaximum = 100;
	public int nodeComputationMinimum = 40;
	public int edgeBandwithMaximum = 100;

	// edge parameter
	public double node2nodeProbability = 0.4;
	public int edgeBandwithMinimum = 50;

	// service parameter
	public int serviceNumber = 5;
	public double serivecProbability = 0.5;

	public boolean sampleInit;
	
	public int time=1000;

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

}
