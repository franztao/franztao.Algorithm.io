/**
 * 
 */

package standardalgorithm;

import java.util.List;
import org.jgraph.graph.DefaultEdge;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 * ShortestPath.
 * 
 * @author franz
 *
 */
public class ShortestPath {
  public int nodeSize;
  public int source;
  public int destination;
  public int[][] edgeConstraint;

  /**
   * ShortestPath.
   * 
   * @param nodeSize2
   *          nodeSize2
   */
  public ShortestPath(int nodeSize2) {
    // TODO Auto-generated constructor stub
    this.nodeSize = nodeSize2;
  }

  public List<Integer> dijkstra(int source, int end, int[][] topo) {
    SimpleWeightedGraph<Integer, DefaultEdge> graph = new SimpleWeightedGraph<>(DefaultEdge.class);
    for (int i = 0; i < this.nodeSize; i++) {
      graph.addVertex(i);
    }
    for (int i = 0; i < this.nodeSize; i++) {
      for (int j = 0; j < this.nodeSize; j++) {
        if ((topo[i][j] != 0) && (i != j)) {
          graph.addEdge(i, j);
        }
      }
    }

    DijkstraShortestPath<Integer, DefaultEdge> dijk = new DijkstraShortestPath<>(graph);
    GraphPath<Integer, DefaultEdge> path = dijk.getPath(source, end);
    if (path != null) {
      return path.getVertexList();
    } else {
      return null;
    }
  }

}
