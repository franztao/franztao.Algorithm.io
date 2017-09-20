/*
 * IMSHalgorithm.cpp
 *
 *  Created on: Aug 24, 2016
 *      Author: franz
 */
#include"../head.h"
#include<bitset>
extern DisjointPathPair *AlgorithmResult;
extern GraphTopo *p_graph;
//reverse edge and get the shortest path's array from destination node to every other node
void ReverseEdgeGetSSP(GraphTopo const *p_graph, vector<int> &dis) {
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
		len = (*p_graph).rtopo_r_Node_c_EdgeList[v].edgeList.size();
		for (unsigned int i = 0; i < len; i++) {
			EdgeClass e = p_graph->edges.at(p_graph->rtopo_r_Node_c_EdgeList[v].edgeList[i]);
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

//typedef struct {
//	int hc;
//	int loc;
//	int cost;
//	int hop;
//	vector<int> pathnode;
//	vector<int> pathedge;
//} state;
//struct cmp {
//	bool operator()(const state& t1, const state& t2) {
//		return t1.hc > t2.hc;
//	}
//};
class state {
public:
	int hc;
	int loc;
	int cost;
	int hop;
	vector<int> pathnode;
	vector<int> pathedge;
	bool operator<(const state& other) const {
		return this->hc > other.hc;
	}
};
//Compute the (i+1)th shortest path pi using dijstra and A* salgorithm.
bool GetKthShotestPath(GraphTopo const* graph, Request * request, int iterative,
		vector<int> &h) {

	vector<int> path_node = vector<int>(graph->nodeNum, (-1));
	vector<int> path_edge = vector<int>(graph->nodeNum, (-1));
//	while(!que.empty()){
//		que.pop();
//	}

	priority_queue<state, vector<state>, less<state> > que;
	int ite;

	//get the distance of ith shortest path

	ite = iterative;
	int nodelen = graph->nodeNum;
	state start;
	start.cost = 0;
	start.hc = start.cost + h[graph->source];
	start.loc = graph->source;
	start.hop = 0;
	start.pathnode = vector<int>(nodelen, -1);
	start.pathedge = vector<int>(nodelen, -1);
	que.push(start);
	while (!que.empty()) {
		state p = que.top();
		que.pop();
		int v = p.loc;
		//if node is destination when poping node from the priortiy
		if (v == graph->destination) {
			ite--;
			if (ite == 0) {
				std::copy(p.pathnode.begin(), p.pathnode.end(),
						path_node.begin());
				std::copy(p.pathedge.begin(), p.pathedge.end(),
						path_edge.begin());
				request->APCostSum = p.cost;
				request->APHopSum = p.hop;
				break;
			}

		}
		for (unsigned int i = 0;
				i < graph->ftopo_r_Node_c_EdgeList[v].edgeList.size(); i++) {
			EdgeClass e = graph->edges.at(
					graph->ftopo_r_Node_c_EdgeList[v].edgeList[i]);
			if (e.to == graph->source) {
				continue;
			}
			if (-1 != p.pathedge.at(e.to)) {
				continue;
			}

			state next;
			next.cost = p.cost + e.cost;
			next.hc = next.cost + h[e.to];
			next.loc = e.to;
			next.hop = p.hop + 1;
			next.pathnode = vector<int>(nodelen);

			next.pathedge = vector<int>(nodelen);

			std::copy(p.pathnode.begin(), p.pathnode.end(),
					next.pathnode.begin());
			std::copy(p.pathedge.begin(), p.pathedge.end(),
					next.pathedge.begin());
			next.pathnode[e.to] = p.loc;
			next.pathedge[e.to] = e.id;
			que.push(next);

		}
	}

	if (0 != ite) {
		cout << "failed to find the kth shortest path" << endl;
		return false;
	}

//	return true;
	int now;
	int next;
	now = graph->destination;
	vector<int> midnode;
	vector<int> midedge;
	next = path_node[now];
	midnode.push_back(now);
	while (next != -1) {
		midnode.push_back(next);
		midedge.push_back(path_edge[now]);
		request->BPMustNotPassEdges4AP[path_edge[now]] = false;

		//modified by cose bug from code (DesignAllEdgtoHaveSRLG)
		if (-1 != graph->edges.at(path_edge[now]).ithsrlg
				&& (graph->srlgGroups.at(
						graph->edges.at(path_edge[now]).ithsrlg).srlgMembersNum
						> 1))
			request->APSrlgs.push_back(graph->edges.at(path_edge[now]).ithsrlg);
		now = next;
		next = path_node[now];
	}
	for (int k = (midnode.size() - 1); k >= 0; k--) {
		request->AP_PathNode.push_back(midnode[k]);
	}
	for (int k = (midedge.size() - 1); k >= 0; k--) {
		request->AP_PathEdge.push_back(midedge[k]);

	}

	sort(request->APSrlgs.begin(), request->APSrlgs.end());
	vector<int>::iterator pos;
	pos = unique(request->APSrlgs.begin(), request->APSrlgs.end());
	request->APSrlgs.erase(pos, request->APSrlgs.end());

	for (unsigned int i = 0; i < request->APSrlgs.size(); i++) {
		int srlg = request->APSrlgs[i];
		for (unsigned int j = 0; j < graph->srlgGroups[srlg].srlgMember.size();
				j++) {
			int srlgmem = graph->srlgGroups[srlg].srlgMember[j];
			if (request->BPMustNotPassEdges4AP[srlgmem]
					&& request->BPMustNotPassEdgesRLAP[srlgmem]) {
				request->BPMustNotPassEdgesRLAP[srlgmem] = false;
				request->RLAP_PathEdge.push_back(srlgmem);
			}
		}
	}
	return true;
}
bool nodeedgelistcomp(const int& pfirst, const int& psecond) {
	return p_graph->edges.at(pfirst).cost <= p_graph->edges.at(psecond).cost;
}

//sort every node's neighbor edge
void SortEdges(GraphTopo *p_graph) {
	int nodesize = p_graph->nodeNum;
	//****there is a bug in the  sort function.
	for (int i = 0; i < nodesize; i++) {
		if (p_graph->ftopo_r_Node_c_EdgeList.at(i).edgeList.size() > 1)
			std::sort(p_graph->ftopo_r_Node_c_EdgeList.at(i).edgeList.begin(),
					p_graph->ftopo_r_Node_c_EdgeList.at(i).edgeList.end(),
					nodeedgelistcomp);

		if (p_graph->rtopo_r_Node_c_EdgeList.at(i).edgeList.size() > 1)
			std::sort(p_graph->rtopo_r_Node_c_EdgeList.at(i).edgeList.begin(),
					p_graph->rtopo_r_Node_c_EdgeList.at(i).edgeList.end(),
					nodeedgelistcomp);
	}
}
bool ITKSPAlgorithm(GraphTopo const *p_graph, Request *p_request) {

	typedef pair<int, int> P;
	vector<int> dist((*p_graph).nodeNum, (-1));
	vector<int> hop((*p_graph).nodeNum, (-1));
	vector<int> path_node((*p_graph).nodeNum, (-1));
	vector<int> path_edge((*p_graph).nodeNum, (-1));
	priority_queue<P, vector<P>, greater<P> > que;
	dist[(*p_graph).source] = 0;
	hop[(*p_graph).source] = 0;
	que.push(P(0, (*p_graph).source));

	while (!que.empty()) {
		P p = que.top();
		que.pop();
		int v = p.second;
		if (dist[v] < p.first)
			continue;
		for (unsigned int i = 0;
				i < (*p_graph).ftopo_r_Node_c_EdgeList[v].edgeList.size();
				i++) {
			EdgeClass e = p_graph->edges.at(p_graph->ftopo_r_Node_c_EdgeList[v].edgeList[i]);
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
			(*p_request).BPCostSum += p_graph->edges.at(path_edge[now]).cost;
			now = next;
			next = path_node[now];
		}
		for (int k = (midnode.size() - 1); k >= 0; k--) {
			p_request->BP_PathNode.push_back(midnode[k]);
		}
		for (int k = (midedge.size() - 1); k >= 0; k--) {
			p_request->BP_PathEdge.push_back(midedge[k]);
		}

		DisjointPathPair *dispath = new DisjointPathPair(
				p_request->AP_PathEdge.size(), p_request->BP_PathEdge.size());
		std::copy(p_request->AP_PathEdge.begin(), p_request->AP_PathEdge.end(),
				dispath->APedge.begin());
		std::copy(p_request->BP_PathEdge.begin(), p_request->BP_PathEdge.end(),
				dispath->BPedge.begin());

		dispath->APcostsum = p_request->APCostSum;
		dispath->BPcostsum = p_request->BPCostSum;
		dispath->APhop = p_request->AP_PathEdge.size() + 1;
		dispath->BPhop = p_request->BP_PathEdge.size() + 1;
		dispath->SolutionNotFeasible = false;

		if ((dispath->APcostsum) < (AlgorithmResult->APcostsum))
			AlgorithmResult->copyResult(dispath);
		delete dispath;

//		AlgorithmResult->APcostsum=INT_MAX;
		return true;
	}
	return false;
}
bool IHKSPAlgorithm(GraphTopo const *p_graph) {
	int ite = 0;

	vector<int> dest_dis = vector<int>(p_graph->nodeNum, -1); //-1 represent inf
//	for(int i=0;i<p_graph->edges.size();i++){
//		Edge e = (*p_graph).edges.at(i);
//		cout<<e.id<<" "<<e.from<<" "<<e.to<<" "<<e.index<<" "<<e.revid<<endl;
//	}
	ReverseEdgeGetSSP(p_graph, dest_dis);
//	SortEdges(p_graph);
	Request *p_request = new Request(p_graph->source, p_graph->destination,
			p_graph->edgeNum);
	while ((-1 != dest_dis[p_graph->source])
			&& (INT_MAX == AlgorithmResult->APcostsum)
			&& (ite <= MAX_ITERATIONS)) {
//		cout << "iterative number: " << ite << endl;
		p_request->clear(p_graph->edgeNum);
		if (GetKthShotestPath(p_graph, p_request, ite + 1, dest_dis)) {
			if (ITKSPAlgorithm(p_graph, p_request)) {
				cout << "iterative number: " << ite << " /" << MAX_ITERATIONS
						<< endl;
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

