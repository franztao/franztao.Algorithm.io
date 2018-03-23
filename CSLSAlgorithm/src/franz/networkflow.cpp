/*
 * NetworkFlow.cpp
 *
 *  Created on: Jul 13, 2016
 *      Author: Taoheng
 */

#include "../franz/networkflow.h"

//find the augmentation path.
int dfsNetworkFlow_fordfulkerson(int v, int t, int f, NetworkFlow & netflow) {
	if (v == t)
		return f;
	netflow.used[v] = true;
	for (unsigned int i = 0; i < netflow.G[v].newtworkedgelist.size(); i++) {
		newtworkEdge&e = netflow.G[v].newtworkedgelist[i];
		if (!netflow.used[e.to] && e.cap > 0) {
			int min = f > e.cap ? e.cap : f;
			int d = dfsNetworkFlow_fordfulkerson(e.to, t, min, netflow);
			if (0 < d) {
				e.cap -= d;
				netflow.G[e.to].newtworkedgelist[e.rev].cap += d;
				return d;
			}
		}
	}
	return 0;
}

void dfsGetSTCut(NetworkFlow & netflow, int s, Request & request) {
	request.STNodeCut[s] = true;
	netflow.used[s] = true;
	for (unsigned int i = 0; i < netflow.G[s].newtworkedgelist.size(); i++) {
		newtworkEdge &e = netflow.G[s].newtworkedgelist[i];
		if ((!netflow.used[e.to]) && (0 < e.cap)) {
			netflow.used[e.to] = true;
			request.STNodeCut[e.to] = true;
			dfsGetSTCut(netflow, e.to, request);
		}
	}
}
//time complexity=O(EF) find maxflow in graph G*,
void MaxFlowAlgorithm_fordfulkerson(GraphTopo &graph, Request & request) {
	int maxflow = 0;
	NetworkFlow netflow(graph, request);
	for (;;) {
		netflow.clearUsedVector();
		int augmentationflow = dfsNetworkFlow_fordfulkerson(graph.source,
				graph.destination,
				INT_MAX, netflow);
		if (0 == augmentationflow)
			break;
		maxflow += augmentationflow;
	}

#ifndef ConsolePrint
	cout << "maxflow:" << maxflow << endl;
#endif

	request.STNodeCut = vector<bool>(graph.nodeNum, false);
	netflow.clearUsedVector();
	dfsGetSTCut(netflow, graph.source, request);
#ifndef ConsolePrint
	for (int i = 0; i < graph.nodeNum; i++) {
		if (true == request.STNodeCut[i]) {
			cout << "S'node: " << graph.nid_nindex[i] << endl;
		}
	}
#endif
	//delete netflow;
	return;
}
void bfsNetworkFlow_dinic(NetworkFlow & netflow) {
	netflow.level = vector<int>(netflow.nodeSize, -1);
	queue<int> que;
	netflow.level[netflow.source] = 0;
	que.push(netflow.source);
	while (!que.empty()) {
		int v = que.front();
		que.pop();
		for (unsigned i = 0; i < netflow.G.at(v).newtworkedgelist.size(); i++) {
			newtworkEdge &e = netflow.G.at(v).newtworkedgelist.at(i);
			if ((0 < e.cap) && (0 > netflow.level[e.to])) {
				netflow.level[e.to] = netflow.level[v] + 1;
				que.push(e.to);
			}
		}
	}
}
int dfsNetworkFlow_dinic(NetworkFlow & netflow, int v, int t, int f) {
	if (v == t)
		return f;
	for (int &i = netflow.used[v]; i < netflow.G.at(v).newtworkedgelist.size();
			i++) {
		newtworkEdge &e = netflow.G.at(v).newtworkedgelist.at(i);
		if ((0 < e.cap) && (netflow.level[v] < netflow.level[e.to])) {
			int min = f > e.cap ? e.cap : f;
			int d = dfsNetworkFlow_dinic(netflow, e.to, t, min);
			if (d > 0) {
				e.cap -= d;
				netflow.G.at(e.to).newtworkedgelist.at(e.rev).cap += d;
				return d;
			}
		}
	}
	return 0;
}
//time complexity=O(EV2)
void MaxFlowAlgorithm_dinic(GraphTopo &graph, Request & request) {
	int maxflow = 0;
	NetworkFlow netflow(graph, request);

	for (;;) {
		bfsNetworkFlow_dinic(netflow);
		if (netflow.level[graph.destination] < 0)
			break;
		netflow.clearUsedVector();
		int augmentationflow;
		while ((augmentationflow = dfsNetworkFlow_dinic(netflow, graph.source,
				graph.destination, INT_MAX)) > 0) {
			maxflow += augmentationflow;
		}
	}
}

