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
		// //node parameter
		// public int nodeSize=40;
		// public int nodeComputationMaximum=100;
		// public int nodeComputationMinimum=40;
		// public int edgeBandwithMaximum=100;
		//
		// //edge parameter
		// public double node2nodeProbability=0.4;
		// public int edgeBandwithMinimum=50;
		//
		// //service parameter
		// public int serviceNumber=5;
		SubstrateNetwork FDSubstrateNework = new SubstrateNetwork(snp);

		VirtualNetworkParameter vnp = new VirtualNetworkParameter(SampleInit);
		// // node parameter
		// public int nodeSize = 4;
		// public int nodeSizeMinimum = 2;
		// public int nodeSizeMaximum = 10;
		// public int nodeComputationMaximum = 20;
		// public int nodeComputationMinimum = 5;
		//
		// // edge parameter
		// public double node2nodeProbability = 0.4;
		// public int edgeBandwithMinimum = 10;
		// public int edgeBandwithMaximum = 30;
		//
		// // service parameter
		// public int serviceNumber = 5;
		Experiment exp = new Experiment(FDSubstrateNework, vnp);
		exp.startExperiment();


	}

}
