/*
 * IMSHalgorithm.cpp
 *
 *  Created on: Aug 24, 2016
 *      Author: franz
 */
#include"../head.h"
extern DisjointPath *AlgorithmResult;

//reverse edge and get the shortest path's array from destination node to every other node
void ReverseEdgeGetSSP(Graph *p_graph, vector<int> &dis) {
	typedef pair<int, int> P;
	vector<int> hop((*p_graph).nodeNum, (-1));
	priority_queue<P, vector<P>, greater<P> > que;
	unsigned int len;
	dis[p_graph->destination] = 0;
	hop[p_graph->destination] = 0;
	que.push(P(0, p_graph->destination));
	while (!que.empty()) {
		P p = que.top();
		que.pop();
		int v = p.second;
		if (dis[v] < p.first)
			continue;
		len = (*p_graph).topo_Node_rEdgeList[v].edgeList.size();
		for (unsigned int i = 0; i < len; i++) {
			Edge e = (*p_graph).edges.at(
					(*p_graph).topo_Node_rEdgeList[v].edgeList[i]);
			if (-1 == dis[e.from]) {
				dis[e.from] = dis[v] + e.cost;
				hop[e.from] = hop[v] + 1;
				que.push(P(dis[e.from], e.from));
			} else {
				if (dis[e.from] > (dis[v] + e.cost)) {
					dis[e.from] = dis[v] + e.cost;
					hop[e.from] = hop[v] + 1;
					que.push(P(dis[e.from], e.from));
				} else {
					if (dis[e.from] == (dis[v] + e.cost)) {
						if (hop[e.from] > (hop[v] + 1)) {
							hop[e.from] = hop[v] + 1;
							que.push(P(dis[e.from], e.from));
						}
					}
				}
			}
		}
	}
	return;
}
//Compute the (i+1)th shortest path pi using dijstra and A* salgorithm.
bool GetKthShotestPath(Graph *p_graph, Request *p_request, int k,
		vector<int> &h) {
	typedef pair<int, int> P;
	vector<int> dist((*p_graph).nodeNum, (-1));
	vector<int> hop((*p_graph).nodeNum, (-1));
	vector<int> path_node((*p_graph).nodeNum, (-1));
	vector<int> path_edge((*p_graph).nodeNum, (-1));
	priority_queue<P, vector<P>, greater<P> > que;
	unsigned int len;
	dist[(*p_graph).source] = 0;
	hop[(*p_graph).source] = 0;
	que.push(P(0 + h[p_graph->source], (*p_graph).source));

	while (!que.empty()) {
		P p = que.top();
		que.pop();
		int v = p.second;
//		if (dist[v] < p.first)
//			continue;
		if (v == p_graph->destination) {
			if (0 == k)
				break;
			else
				continue;
		}

		len = (*p_graph).topo_Node_fEdgeList[v].edgeList.size();
		for (unsigned int i = 0; i < len; i++) {

			Edge e = (*p_graph).edges.at(
					(*p_graph).topo_Node_fEdgeList[v].edgeList[i]);

			if (-1 == dist[e.to]) {
				dist[e.to] = dist[v] + e.cost;
				hop[e.to] = hop[v] + 1;
				path_node[e.to] = v;
				path_edge[e.to] = e.id;
				que.push(P(dist[e.to] + h[e.to], e.to));
			} else {
				if ((p_graph->destination == e.to) && k) {
					dist[e.to] = dist[v] + e.cost;
					hop[e.to] = hop[v] + 1;
					path_node[e.to] = v;
					path_edge[e.to] = e.id;
					que.push(P(dist[e.to] + h[e.to], e.to));
					k--;
					continue;
				}
				if (0 == k) {
					break;
				}

				if (dist[e.to] > dist[v] + e.cost) {
					dist[e.to] = dist[v] + e.cost;
					hop[e.to] = hop[v] + 1;
					path_node[e.to] = v;
					path_edge[e.to] = e.id;
					que.push(P(dist[e.to] + h[e.to], e.to));
				} else {
					if (dist[e.to] == (dist[v] + e.cost)) {
						if (hop[e.to] > (hop[v] + 1)) {
							hop[e.to] = hop[v] + 1;
							path_node[e.to] = v;
							path_edge[e.to] = e.id;
							que.push(P(dist[e.to] + h[e.to], e.to));
						} else {
							if (dist[e.to] == (dist[v] + e.cost)) {
								if (hop[e.to] > (hop[v] + 1)) {
									hop[e.to] = hop[v] + 1;
									path_node[e.to] = v;
									path_edge[e.to] = e.id;
									que.push(P(dist[e.to] + h[e.to], e.to));
								}
							}
						}
					}
				}
			}
		}
	}
	if (0 == k) {
		cout << "failed to find the kth shortest path" << endl;
		return false;
	} else {
		int now;
		int next;
		now = (*p_graph).destination;
		(*p_request).AP_PathNode.push_back(now);
		next = path_node[now];
		while (next != -1) {
			(*p_request).AP_PathNode.push_back(next);
			(*p_request).AP_PathEdge.push_back(path_edge[now]);
			(*p_request).BPMustNotPassEdges4AP[path_edge[now]] = false;
			if (-1 != p_graph->edges[path_edge[now]].ithsrlg)
				(*p_request).APSrlgs.push_back(
						p_graph->edges[path_edge[now]].ithsrlg);
			now = next;
			next = path_node[now];
		}
		(*p_request).APCostSum = dist[(*p_graph).destination];
		(*p_request).APHopSum = hop[(*p_graph).destination];
		for (unsigned int i = 0; i < (*p_request).APSrlgs.size(); i++) {
			int srlg = (*p_request).APSrlgs[i];
			for (unsigned int j = 0;
					j < (*p_graph).srlgGroups[srlg].srlgMember.size(); j++) {
				int srlgmem = (*p_graph).srlgGroups[srlg].srlgMember[j];
				if ((*p_request).BPMustNotPassEdges4AP[srlgmem]
						&& (*p_request).BPMustNotPassEdgesRLAP[srlgmem]) {
					(*p_request).BPMustNotPassEdgesRLAP[srlgmem] = false;
					p_request->RLAP_PathEdge.push_back(srlgmem);
				}
			}
		}
		vector<int>::iterator it = (*p_request).AP_PathNode.end();
		do {
			it--;
		} while (it != (*p_request).AP_PathNode.begin());
		return true;
	}
	return false;
}
void SortEdges(Graph *p_graph) {

}
bool ITKSPAlgorithm(Graph *p_graph, Request *p_request) {
	typedef pair<int, int> P;
	vector<int> dist((*p_graph).nodeNum, (-1));
	vector<int> hop((*p_graph).nodeNum, (-1));
	vector<int> path_node((*p_graph).nodeNum, (-1));
	vector<int> path_edge((*p_graph).nodeNum, (-1));
	priority_queue<P, vector<P>, greater<P> > que;
	unsigned int len;
	dist[(*p_graph).source] = 0;
	hop[(*p_graph).source] = 0;
	que.push(P(0, (*p_graph).source));

	while (!que.empty()) {
		P p = que.top();
		que.pop();
		int v = p.second;
		if (dist[v] < p.first)
			continue;
		len = (*p_graph).topo_Node_fEdgeList[v].edgeList.size();
		for (unsigned int i = 0; i < len; i++) {
			Edge e = (*p_graph).edges.at(
					(*p_graph).topo_Node_fEdgeList[v].edgeList[i]);
			if (!p_request->BPMustNotPassEdges4AP[e.id]
					|| !p_request->BPMustNotPassEdgesRLAP[e.id])
				continue;
			if (-1 == dist[e.to]) {
				dist[e.to] = dist[v] + e.cost;
				hop[e.to] = hop[v] + 1;
				path_node[e.to] = v;
				path_edge[e.to] = e.id;
				que.push(P(dist[e.to], e.to));
			} else {
				if (dist[e.to] > (dist[v] + e.cost)) {
					dist[e.to] = dist[v] + e.cost;
					hop[e.to] = hop[v] + 1;
					path_node[e.to] = v;
					path_edge[e.to] = e.id;
					que.push(P(dist[e.to], e.to));
				} else {
					if (dist[e.to] == (dist[v] + e.cost)) {
						if (hop[e.to] > (hop[v] + 1)) {
							hop[e.to] = hop[v] + 1;
							path_node[e.to] = v;
							path_edge[e.to] = e.id;
							que.push(P(dist[e.to], e.to));
						}
					}
				}
			}
		}
	}
	if (-1 == path_node[(*p_graph).destination]) {
		return false;
	} else {
		int now;
		int next;

		now = (*p_graph).destination;
		(*p_request).BP_PathNode.push_back(now);
		next = path_node[now];
		while (-1 != next) {
			(*p_request).BP_PathNode.push_back(next);
			(*p_request).BP_PathEdge.push_back(path_edge[now]);
			(*p_request).BPCostSum += p_graph->edges[path_edge[now]].cost;

			now = next;
			next = path_node[now];

		}

		DisjointPath *dispath = new DisjointPath(p_request->AP_PathEdge.size(),
				p_request->BP_PathEdge.size());
		std::copy(p_request->AP_PathEdge.begin(), p_request->AP_PathEdge.end(),
				dispath->AP.begin());
		std::copy(p_request->BP_PathEdge.begin(), p_request->BP_PathEdge.end(),
				dispath->BP.begin());
		dispath->APsum = p_request->APCostSum;
		if (dispath->APsum < AlgorithmResult->APsum)
			AlgorithmResult->getResult(dispath);
		delete dispath;
		return true;
	}
	return false;
}
bool IHKSPAlgorithm(Graph *p_graph) {
	int ite = 0;

	vector<int> dest_dis = vector<int>(p_graph->nodeNum, -1); //-1 represent inf
	ReverseEdgeGetSSP(p_graph, dest_dis);
	SortEdges(p_graph);
	while ((ite <= MAX_ITERATIONS) && (INT_MAX == AlgorithmResult->APsum)
			&& (-1 != dest_dis[p_graph->source])) {
		Request *p_request = new Request(p_graph->source, p_graph->destination,
				p_graph->edgeNum);
		if (GetKthShotestPath(p_graph, p_request, ite + 1, dest_dis)) {
			if (ITKSPAlgorithm(p_graph, p_request))
				return true;
			else {
				ite++;
			}
		} else {
			break;
		}
	}
	return false;
}

