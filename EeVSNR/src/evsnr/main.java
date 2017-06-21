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
	public static int  substrateNodeSize=9;
	public static int  substrateEdgeSize=12;
	public static int  serviceNumber=4;
	
	public static void main(String[] args) throws GRBException {
		// TODO Auto-generated method stub
		//read substrate network
		//read virtual network
		//embedding virtual network into substrate network
		//enhance the virtual network
		
		VirtualNetworkRequest VNR1=new VirtualNetworkRequest(4,4,4);
		VNR1.faultInit();
		
		SubstrateNetwork FDSubstrateNework=new SubstrateNetwork(substrateNodeSize,substrateEdgeSize,serviceNumber); 
		FDSubstrateNework.faultInit();
		
		EnhancedVirtualNetwork EVNR1=new EnhancedVirtualNetwork(8,4,4,VNR1);
		EVNR1.faultInit();
		EVNR1.computeItems();
		EVNR1.AugmentNodeEdge4Survivability(4,EVNR1.FailureDependent);
		System.out.println("------------------"+"----------------");
		EnhancedVirtualNetwork EVNR2=new EnhancedVirtualNetwork(8,4,4,VNR1);
		EVNR2.faultInit();
		EVNR2.computeItems();
		EVNR2.AugmentNodeEdge4Survivability(4,EVNR1.FailureIndependent);
		
		
	}

}
