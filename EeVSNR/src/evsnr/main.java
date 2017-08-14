/**
 * 
 */

package evsnr;


import substratenetwork.SubStrateNetworkParameter;

import substratenetwork.SubstrateNetwork;
import virtualnetwork.VirtualNetworkParameter;

/**
 * @author franz
 *
 */
public class main {

	/**
	 * @param args
	 */

	public static void main(String[] args) {
		boolean SampleInit = false;
		Result result=new Result();
		result.recordParameter();
		
		SubStrateNetworkParameter snp = new SubStrateNetworkParameter(SampleInit);
		
		SubstrateNetwork sn = new SubstrateNetwork(snp);
		
		VirtualNetworkParameter vnp = new VirtualNetworkParameter(SampleInit);
		
		Experiment exp = new Experiment(sn, vnp,result);
		exp.startExperiment();
		
		return ;
	}

}
