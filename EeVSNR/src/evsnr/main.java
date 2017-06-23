/**
 * 
 */
package evsnr;

import gurobi.GRBException;
import substrate.network.SubstrateNetwork;
import virtualnetwork.EnhancedVirtualNetwork;
import virtualnetwork.VirtualNetworkRequest;

/**
 * @author franz
 *
 */
public class main {

	/**
	 * @param args
	 */
	public static int substrateNodeSize = 9;
	public static int substrateEdgeSize = 12;
	public static int serviceNumber = 4;

	public static void main(String[] args) throws GRBException {
		// TODO Auto-generated method stub
		// read substrate network
		// read virtual network
		// embedding virtual network into substrate network
		// enhance the virtual network

		VirtualNetworkRequest VNR1 = new VirtualNetworkRequest(4, 4, 4);
		VNR1.initSample1();

		SubstrateNetwork FDSubstrateNework = new SubstrateNetwork(substrateNodeSize, substrateEdgeSize, serviceNumber);
		FDSubstrateNework.faultInit();

		/*EnhancedVirtualNetwork EVNR1 = new EnhancedVirtualNetwork(7, 4, 4, VNR1);
		EVNR1.initSample1();
		EVNR1.computeItems();
		EVNR1.HeursitcAlgorithm4Survivability(4, EVNR1.FailureDependent);
		System.out.println("------------------" + "----------------");
		
		EnhancedVirtualNetwork EVNR2 = new EnhancedVirtualNetwork(7, 4, 4, VNR1);
		EVNR2.initSample1();
		EVNR2.computeItems();
		EVNR2.HeursitcAlgorithm4Survivability(4, EVNR2.FailureIndependent);
		System.out.println("------------------" + "----------------");
		
		*/
		
		VirtualNetworkRequest VNR2 = new VirtualNetworkRequest(2, 1, 2);
		VNR2.initSample2();
		
		EnhancedVirtualNetwork EVNR3 = new EnhancedVirtualNetwork(4, 2, 2, VNR2);
		EVNR3.initSample2();
		EVNR3.computeItems();
		EVNR3.HeursitcAlgorithm4Survivability(2, EVNR3.FailureDependent);
		System.out.println("------------------" + "----------------");
		
		EnhancedVirtualNetwork EVNR4 = new EnhancedVirtualNetwork(4, 2, 2, VNR2);
		EVNR4.initSample2();
		EVNR4.computeItems();
		EVNR4.OptimalAlgorithmIP4Survivability();
		System.out.println("------------------" + "----------------");
		
		
		
//		VirtualNetworkRequest VNR3 = new VirtualNetworkRequest(1, 0, 2);
//		VNR3.initSample3();
//		EnhancedVirtualNetwork EVNR5 = new EnhancedVirtualNetwork(2, 0, 2, VNR3);
//		EVNR5.initSample3();
//		EVNR5.computeItems();
//		EVNR5.OptimalAlgorithmIP4Survivability();
//		EVNR5.HeursitcAlgorithm4Survivability(1, EVNR5.FailureDependent);
		System.out.println("------------------" + "----------------");
//		EnhancedVirtualNetwork EVNR3 = new EnhancedVirtualNetwork(7, 4, 4, VNR1);
//		EVNR3.faultInit();
//		EVNR3.computeItems();
//		EVNR3.OptimalAlgorithmIP4Survivability();
		

	}

}
