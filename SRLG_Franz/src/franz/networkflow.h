/*
 * NetworkFlow.h
 *
 *  Created on: Jul 13, 2016
 *      Author: Taoheng
 */

#ifndef NETWORKFLOW_H_
#define NETWORKFLOW_H_

#include <vector>
#include"../head.h"
using namespace std;
/*
 *
 */
class newtworkEdge {
public:
	int to, cap, rev, id;
	newtworkEdge(int t, int c, int r, int i) :
			to(t), cap(c), rev(r), id(i) {
	}
};
class newtworkEdgelist {
public:
	vector<newtworkEdge> newtworkedgelist;
};

class NetworkFlow {
public:
	int source, destination;
	vector<newtworkEdgelist> G;
	vector<int> used;
	int nodeSize;

	vector<int> level;

	NetworkFlow(GraphTopo &graph, Request&request) {
		int from, to, cap, id;
		G = vector<newtworkEdgelist>(graph.nodeNum);
		nodeSize = graph.nodeNum;
		source = graph.source;
		destination = graph.destination;
		for (unsigned int i = 0; i < graph.getEdgeSize(); i++) {
			if(!request.APMustNotPassEdges.at(i))
				continue;
			from = graph.getithEdge(i).from;
			to = graph.getithEdge(i).to;
			id = graph.getithEdge(i).id;
			cap = request.edgeCapacity.at(id); //graph.edges[i].capacity;
#ifndef ConsolePrint
			cout << id << " " << graph.nid_nindex[from] << " - "
					<< graph.nid_nindex[to] << " - " << cap << endl;
#endif
			newtworkEdge e1(to, cap, G[to].newtworkedgelist.size(), id);
			G[from].newtworkedgelist.push_back(e1);
			newtworkEdge e2(from, cap, G[from].newtworkedgelist.size() - 1, id);
			G[to].newtworkedgelist.push_back(e2);
		}

	}
	void clearUsedVector() {
		used = vector<int>(nodeSize, false);
	}

};

extern void MaxFlowAlgorithm_fordfulkerson(GraphTopo &graph, Request & request);

#endif /* NETWORKFLOW_H_ */
