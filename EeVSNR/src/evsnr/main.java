/**
 * 
 */
package evsnr;

import gurobi.GRBException;
import substratenetwork.SubStrateNetworkParameter;
import substratenetwork.SubstrateNetwork;
import virtualnetwork.EnhancedVirtualNetwork;
import virtualnetwork.VirtualNetwork;
import virtualnetwork.VirtualNetworkParameter;

/**
 * @author franz
 *
 */
public class main {

	/**
	 * @param args
	 */
	

	public static void main(String[] args) throws GRBException {
		// TODO Auto-generated method stub
		// read substrate network

		boolean SampleInit =true;
		SubStrateNetworkParameter snp=new SubStrateNetworkParameter(SampleInit);
//		//node parameter
//		public int nodeSize=40;
//		public int nodeComputationMaximum=100;
//		public int nodeComputationMinimum=40;
//		public int edgeBandwithMaximum=100;
//		
//		//edge parameter
//		public double node2nodeProbability=0.4;
//		public int edgeBandwithMinimum=50;
//
//		//service parameter
//		public int serviceNumber=5;
		SubstrateNetwork FDSubstrateNework = new SubstrateNetwork(snp);

		VirtualNetworkParameter vnp=new VirtualNetworkParameter(SampleInit);
//		// node parameter
//		public int nodeSize = 4;
//		public int nodeSizeMinimum = 2;
//		public int nodeSizeMaximum = 10;
//		public int nodeComputationMaximum = 20;
//		public int nodeComputationMinimum = 5;
//
//		// edge parameter
//		public double node2nodeProbability = 0.4;
//		public int edgeBandwithMinimum = 10;
//		public int edgeBandwithMaximum = 30;
//
//		// service parameter
//		public int serviceNumber = 5;
		
		FDSubstrateNework.startExperiment(vnp);
		
		// embedding virtual network into substrate network
		// enhance the virtual network


//		EnhancedVirtualNetwork EVNR1 = new EnhancedVirtualNetwork(7, 4, 4, VNR1);
//		EVNR1.initSample1();
//		EVNR1.computeItems();
//		EVNR1.HeursitcAlgorithm4Survivability(4, EVNR1.FailureDependent);
//		System.out.println("------------------" + "----------------");
//		
//		EnhancedVirtualNetwork EVNR2 = new EnhancedVirtualNetwork(7, 4, 4, VNR1);
//		EVNR2.initSample1();
//		EVNR2.computeItems();
//		EVNR2.HeursitcAlgorithm4Survivability(4, EVNR2.FailureIndependent);
//		System.out.println("------------------" + "----------------");
		
		
		
//		VirtualNetwork VNR2 = new VirtualNetwork(2, 1, 2);
//		VNR2.initSample2();
//		
//		EnhancedVirtualNetwork EVNR3 = new EnhancedVirtualNetwork(4, 2, 2, VNR2);
//		EVNR3.initSample2();
//		EVNR3.computeItems();
//		EVNR3.HeursitcAlgorithm4Survivability(2, EVNR3.FailureDependent);
//		System.out.println("------------------" + "----------------");
		
		
		
		
//		VirtualNetworkRequest VNR3 = new VirtualNetworkRequest(1, 0, 2);
//		VNR3.initSample3();
//		EnhancedVirtualNetwork EVNR5 = new EnhancedVirtualNetwork(2, 0, 2, VNR3);
//		EVNR5.initSample3();
//		EVNR5.computeItems();
//		EVNR5.OptimalAlgorithmIP4Survivability();
//		EVNR5.HeursitcAlgorithm4Survivability(1, EVNR5.FailureDependent);
//		System.out.println("------------------" + "----------------");
		

	}

}
