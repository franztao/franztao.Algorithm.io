/**
 * 
 */
package standardalgorithm;

import java.util.List;
import java.util.Vector;

import org.jgraph.graph.DefaultEdge;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 * @author franz
 *
 */
public class ShortestPath {
	public int nodeSize;
	public int source;
	public int destination;
	public int edgeConstraint[][];

	/**
	 * @param nodeSize2
	 */
	public ShortestPath(int nodeSize2) {
		// TODO Auto-generated constructor stub
		this.nodeSize = nodeSize2;
	}

	public List<Integer> Dijkstra(int source, int end, int[][] bandwith) {
		SimpleWeightedGraph<Integer, DefaultEdge> graph = new SimpleWeightedGraph<>(DefaultEdge.class);
		for (int i = 0; i < this.nodeSize; i++) {
			graph.addVertex(i);
		}
		for (int i = 0; i < this.nodeSize; i++) {
			for (int j = 0; j < this.nodeSize; j++) {
				if ((bandwith[i][j] != 0) && (i != j)) {
					graph.addEdge(i, j);
				}
			}
		}
		
		DijkstraShortestPath<Integer, DefaultEdge> dijk = new DijkstraShortestPath<>(graph);
		GraphPath<Integer, DefaultEdge> path = dijk.getPath(source, end);
		return path.getVertexList();

	}

}
