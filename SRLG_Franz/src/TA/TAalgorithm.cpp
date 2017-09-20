#include"../head.h"

const int M = 100000;
extern DisjointPathPair *AlgorithmResult;
bool ShortestPath(GraphTopo *p_graph, Request * p_request, vector<bool>&A) {
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

			EdgeClass &e = p_graph->getithEdge(
					(*p_graph).ftopo_r_Node_c_EdgeList[v].edgeList[i]);

			if (!A.at(e.id)) //p_request->APMustNotPassEdges[e.id])
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
							dist[e.to] = dist[v] + e.cost;
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

	if (-1 == dist[(*p_graph).destination]) {
		return false;
	} else {
		int now;
		int next;

		now = (*p_graph).destination;
		vector<int> midnode;
		vector<int> midedge;
		next = path_node[now];
		midnode.push_back(now);

		p_request->APSrlgs.clear(); //
		while (next != -1) {
			//			(*p_request).AP_PathNode.push_back(next);
			//			(*p_request).AP_PathEdge.push_back(path_edge[now]);
			midnode.push_back(next);
			midedge.push_back(path_edge[now]);
			(*p_request).BPMustNotPassEdges4AP[path_edge[now]] = false;
			if (-1 != p_graph->getithEdge(path_edge[now]).ithsrlg)
				(*p_request).APSrlgs.push_back(
						p_graph->getithEdge(path_edge[now]).ithsrlg);
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

		sort(p_request->APSrlgs.begin(), p_request->APSrlgs.end());
		vector<int>::iterator pos;
		pos = unique(p_request->APSrlgs.begin(), p_request->APSrlgs.end());
		p_request->APSrlgs.erase(pos, p_request->APSrlgs.end());

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
}
bool FindCandidateBackup(GraphTopo *p_graph, Request * p_request, vector<int> &T) {
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

			EdgeClass &e = p_graph->getithEdge(
					(*p_graph).ftopo_r_Node_c_EdgeList[v].edgeList[i]);

//			if (!A.at(e.id)) //p_request->APMustNotPassEdges[e.id])
//				continue;
			if (!p_request->BPMustNotPassEdges4AP[e.id])
				continue;
			int addcost;
			if (!p_request->BPMustNotPassEdgesRLAP[e.id]) {
				addcost = M;
			} else
				addcost = e.cost;

			if (-1 == dist[e.to]) {
				dist[e.to] = dist[v] + addcost;			//e.cost;
				hop[e.to] = hop[v] + 1;
				path_node[e.to] = v;
				path_edge[e.to] = e.id;
				que.push(P(dist[e.to], e.to));
			} else {
				if (dist[e.to] > (dist[v] + addcost)) {
					dist[e.to] = dist[v] + addcost;
					hop[e.to] = hop[v] + 1;
					path_node[e.to] = v;
					path_edge[e.to] = e.id;
					que.push(P(dist[e.to], e.to));
				} else {
					if (dist[e.to] == (dist[v] + addcost)) {
						if (hop[e.to] > (hop[v] + 1)) {
							dist[e.to] = dist[v] + addcost;
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

	if (-1 == dist[(*p_graph).destination]) {
		return false;
	} else {
		int now;
		int next;

		now = (*p_graph).destination;
		vector<int> midnode;
		vector<int> midedge;
		next = path_node[now];
		midnode.push_back(now);

		while (next != -1) {
			midnode.push_back(next);
			midedge.push_back(path_edge[now]);
			if (!p_request->BPMustNotPassEdgesRLAP[path_edge[now]])
				T.push_back(p_graph->getithEdge(path_edge[now]).ithsrlg);
			(*p_request).BPCostSum += p_graph->getithEdge(path_edge[now]).cost;
			now = next;
			next = path_node[now];
		}
		for (int k = (midnode.size() - 1); k >= 0; k--) {
			p_request->BP_PathNode.push_back(midnode[k]);
		}
		for (int k = (midedge.size() - 1); k >= 0; k--) {
			p_request->BP_PathEdge.push_back(midedge[k]);
		}

		return true;
	}
	return false;
}

int MostRiskyActiveLink(GraphTopo *p_graph, Request * p_request, vector<int> &T) {
	int target_edge = -1;
	int target_num = 0;
	for (unsigned i = 0; i < p_request->AP_PathEdge.size(); i++) {
		int edge = p_request->AP_PathEdge.at(i);
		int srlg = p_graph->getithEdge(edge).ithsrlg;
		int midnum = 0;
		for (unsigned j = 0; j < T.size(); j++) {
			if (srlg == T.at(j))
				midnum++;
		}
		if (midnum > target_num) {
			target_edge = edge;
			target_num = midnum;
		}
	}
	return target_edge;
}

void RecordAnswer(Request * p_request) {
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
}

bool TAAlgorithm(GraphTopo *p_graph) {
	int e = p_graph->edgeNum;
	vector<bool> A = vector<bool>(p_graph->edgeNum, true);
	Request *p_request = new Request(p_graph->source, p_graph->destination,
			p_graph->edgeNum);

	while (0 != e) {
		p_request->clear(p_graph->edgeNum);
		if (!ShortestPath(p_graph, p_request, A)) {
			return false;
		}
		vector<int> T;
		if (!FindCandidateBackup(p_graph, p_request, T)) {
			return false;
		}
		if (T.empty()) {
			//record answer
			RecordAnswer(p_request);
			return true;
		} else {
			int R = MostRiskyActiveLink(p_graph, p_request, T);
//			cout<<"R:   "<<R<<"  ("<<p_graph->getithEdge(R).from<<")->("<<p_graph->getithEdge(R).to<<")"<<endl;
			A.at(R) = false;
		}
		T.clear();
		e--;
	}
	return true;
}
