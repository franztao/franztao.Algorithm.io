/**
 * 
 */
package evsnr;

import java.util.Iterator;

import org.jgrapht.Graph;
import org.jgrapht.VertexFactory;
import org.jgrapht.demo.CompleteGraphDemo;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import gurobi.GRBException;
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

	public static void main(String[] args) throws GRBException {
		// TODO Auto-generated method stub
		// read substrate network

		boolean SampleInit = true;
		SubStrateNetworkParameter snp = new SubStrateNetworkParameter(SampleInit);
		SubstrateNetwork sn = new SubstrateNetwork(snp);

		VirtualNetworkParameter vnp = new VirtualNetworkParameter(SampleInit);
		
		Experiment exp = new Experiment(sn, vnp);
		exp.startExperiment();
	}

}
