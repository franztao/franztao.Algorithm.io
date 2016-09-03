/*
 * IMSHalgorithm.cpp
 *
 *  Created on: Aug 24, 2016
 *      Author: franz
 */
#include"../head.h"
extern DisjointPaths *AlgorithmResult;
extern Graph *p_graph;
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
bool GetKthShotestPath(Graph *p_graph, Request *p_request, int iterative,
		vector<int> &h) {
	typedef pair<int, int> P;
	vector<int> dist((*p_graph).nodeNum, (-1));
	vector<int> hop((*p_graph).nodeNum, (-1));
	vector<int> path_node((*p_graph).nodeNum, (-1));
	vector<int> path_edge((*p_graph).nodeNum, (-1));
	priority_queue<P, vector<P>, greater<P> > que;
	unsigned int len;
	int ite;
	int ithpathAstarcost;
	int lessithcost = 0;

	//get the distance of ith shortest path
	ite = iterative;
	dist[(*p_graph).source] = 0;
	hop[(*p_graph).source] = 0;
	que.push(P(0 + h[p_graph->source], (*p_graph).source));
	while (!que.empty()) {
		P p = que.top();
		que.pop();
		int v = p.second;
		if (v == p_graph->destination) {
			lessithcost++;
			ite--;
			if (0 == ite) {
				ithpathAstarcost = p.first;
				break;
			} else {
				continue;
			}
		}
		len = (*p_graph).topo_Node_fEdgeList[v].edgeList.size();
		for (unsigned int i = 0; i < len; i++) {
			Edge e = (*p_graph).edges.at(
					(*p_graph).topo_Node_fEdgeList[v].edgeList[i]);
			if (e.to == p_graph->source) {
				continue;
			}
			if (-1 == dist[e.to]) {
				dist[e.to] = dist[v] + e.cost;
				hop[e.to] = hop[v] + 1;
				que.push(P(dist[e.to] + h[e.to], e.to));
			} else {
				if ((p_graph->destination == e.to)) {
					dist[e.to] = dist[v] + e.cost;
					hop[e.to] = hop[v] + 1;
					que.push(P(dist[e.to] + h[e.to], e.to));
					continue;
				}
				if (dist[e.to] > dist[v] + e.cost) {
					dist[e.to] = dist[v] + e.cost;
					hop[e.to] = hop[v] + 1;
					que.push(P(dist[e.to] + h[e.to], e.to));
				} else {
					if (dist[e.to] == (dist[v] + e.cost)) {
						if (hop[e.to] > (hop[v] + 1)) {
							hop[e.to] = hop[v] + 1;
							que.push(P(dist[e.to] + h[e.to], e.to));
						} else {
							if (dist[e.to] == (dist[v] + e.cost)) {
								if (hop[e.to] > (hop[v] + 1)) {
									hop[e.to] = hop[v] + 1;
									que.push(P(dist[e.to] + h[e.to], e.to));
								}
							}
						}
					}
				}
			}
		}
	}
	if (0 != ite) {
		cout << "failed to find the kth shortest path" << endl;
		return false;
	}
	ite = iterative;
	dist = vector<int>((*p_graph).nodeNum, (-1));
	hop = vector<int>((*p_graph).nodeNum, (-1));
	dist[(*p_graph).source] = 0;
	hop[(*p_graph).source] = 0;
	while (!que.empty()) {
		que.pop();
	}
	que.push(P(0 + h[p_graph->source], (*p_graph).source));
	bool findithpath = false;

	while (!que.empty()) {
		P p = que.top();
		que.pop();
		int v = p.second;
		//if node is destination when poping node from the priortiy
		if (findithpath)
			break;
		if (v == p_graph->destination) {
			continue;
		}
		len = (*p_graph).topo_Node_fEdgeList[v].edgeList.size();
		for (unsigned int i = 0; i < len; i++) {
			Edge e = (*p_graph).edges.at(
					(*p_graph).topo_Node_fEdgeList[v].edgeList[i]);
			if (e.to == p_graph->source) {
				continue;
			}
			if (findithpath)
				break;
			if ((p_graph->destination) == e.to) {
				if ((dist[v] + e.cost) <= ithpathAstarcost) {
					lessithcost--;
					dist[e.to] = dist[v] + e.cost;
					hop[e.to] = hop[v] + 1;
					path_node[e.to] = v;
					path_edge[e.to] = e.id;
					que.push(P(dist[e.to] + h[e.to], e.to));
					if ((ithpathAstarcost == (dist[e.to] + h[e.to]))
							&& (0 == lessithcost)) {
						findithpath = true;
					}
				}
				continue;
			} else {
				if (-1 == dist[e.to]) {
					dist[e.to] = dist[v] + e.cost;
					hop[e.to] = hop[v] + 1;
					path_node[e.to] = v;
					path_edge[e.to] = e.id;
					que.push(P(dist[e.to] + h[e.to], e.to));
				} else {

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
	}
	int now;
	int next;
	now = (*p_graph).destination;
//		(*p_request).AP_PathNode.push_back(now);
//		next = path_node[now];
	vector<int> midnode;
	vector<int> midedge;
	next = path_node[now];
	midnode.push_back(now);
	while (next != -1) {
//			(*p_request).AP_PathNode.push_back(next);
//			(*p_request).AP_PathEdge.push_back(path_edge[now]);
		midnode.push_back(next);
		midedge.push_back(path_edge[now]);
		(*p_request).BPMustNotPassEdges4AP[path_edge[now]] = false;
		if (-1 != p_graph->edges[path_edge[now]].ithsrlg)
			(*p_request).APSrlgs.push_back(
					p_graph->edges[path_edge[now]].ithsrlg);
		now = next;
		next = path_node[now];
	}
	for (int k = (midnode.size() - 1); k >= 0; k--) {
		p_request->AP_PathNode.push_back(midnode[k]);
	}
	for (int k = (midedge.size() - 1); k >= 0; k--) {
		p_request->AP_PathEdge.push_back(midedge[k]);
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
	return true;
}
int fff = 0;
bool nodeedgelistcomp(const int& pfirst, const int& psecond) {
	return p_graph->edges.at(pfirst).cost <= p_graph->edges.at(psecond).cost;
}

//sort every node's neighbor edge
void SortEdges(Graph *p_graph) {
	int nodesize = p_graph->nodeNum;
	//****there is a bug in the  sort function.
	for (int i = 0; i < nodesize; i++) {
		if (p_graph->topo_Node_fEdgeList.at(i).edgeList.size() > 1)
			std::sort(p_graph->topo_Node_fEdgeList.at(i).edgeList.begin(),
					p_graph->topo_Node_fEdgeList.at(i).edgeList.end(),
					nodeedgelistcomp);

		if (p_graph->topo_Node_rEdgeList.at(i).edgeList.size() > 1)
			std::sort(p_graph->topo_Node_rEdgeList.at(i).edgeList.begin(),
					p_graph->topo_Node_rEdgeList.at(i).edgeList.end(),
					nodeedgelistcomp);
	}
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
//		(*p_request).BP_PathNode.push_back(now);
		vector<int> midnode;
		vector<int> midedge;
		next = path_node[now];
		midnode.push_back(now);
		while (-1 != next) {
//			(*p_request).BP_PathNode.push_back(next);
//			(*p_request).BP_PathEdge.push_back(path_edge[now]);
			midnode.push_back(next);
			midedge.push_back(path_edge[now]);
			(*p_request).BPCostSum += p_graph->edges[path_edge[now]].cost;
			now = next;
			next = path_node[now];
		}
		for (int k = (midnode.size() - 1); k >= 0; k--) {
			p_request->BP_PathNode.push_back(midnode[k]);
		}
		for (int k = (midedge.size() - 1); k >= 0; k--) {
			p_request->BP_PathEdge.push_back(midedge[k]);
		}

		DisjointPaths *dispath = new DisjointPaths(
				p_request->AP_PathEdge.size(), p_request->BP_PathEdge.size());
		std::copy(p_request->AP_PathEdge.begin(), p_request->AP_PathEdge.end(),
				dispath->APedge.begin());
		std::copy(p_request->BP_PathEdge.begin(), p_request->BP_PathEdge.end(),
				dispath->BPedge.begin());

		dispath->APcostsum = p_request->APCostSum;
		dispath->BPcostsum = p_request->BPCostSum;
		if ((dispath->APcostsum) < (AlgorithmResult->APcostsum))
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
//	SortEdges(p_graph);
	Request *p_request = new Request(p_graph->source, p_graph->destination,
			p_graph->edgeNum);
	while ((ite <= MAX_ITERATIONS) && (INT_MAX == AlgorithmResult->APcostsum)
			&& (-1 != dest_dis[p_graph->source])) {
		p_request->clear(p_graph->edgeNum);
		if (GetKthShotestPath(p_graph, p_request, ite + 1, dest_dis)) {
			if (ITKSPAlgorithm(p_graph, p_request)) {
				cout << "iterative number: " << ite << " /" << MAX_ITERATIONS;
				return true;
			} else {
				ite++;
			}
		} else {
			break;
		}
//
	}

	delete p_request;
	return false;
}

