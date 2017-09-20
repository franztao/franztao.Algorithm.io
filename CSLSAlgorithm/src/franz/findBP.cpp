#include "../head.h"
#include"../route.h"
extern pthread_mutex_t mutex_result;
extern DisjointPathPair *AlgorithmResult;
//find the second path(BP),if there is not BP,the algorithm begin conquer and divide ,otherwise return true and the algorithm get srlg-min-min disjoint path
bool findBP(GraphTopo *p_graph, Request *p_request) {
#ifndef ConsolePrint
	cout << endl << "is finding the BP" << endl;
#endif
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
		len = (*p_graph).ftopo_r_Node_c_EdgeList[v].edgeList.size();
		for (unsigned int i = 0; i < len; i++) {
			EdgeClass e = p_graph->getithEdge(
					(*p_graph).ftopo_r_Node_c_EdgeList[v].edgeList[i]);
			//BP must not pass the edges of AP or the related slrgs's edges about AP
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
#ifndef ConsolePrint
		cout << "failed to find the BP : begin the franz's algorithm" << endl;
#endif
		return false;
	} else {
		int now;
		int next;

		now = (*p_graph).destination;
//		(*p_request).BP_PathNode.push_back(now);
//		next = path_node[now];
		vector<int> midnode;
		vector<int> midedge;
		next = path_node[now];
		midnode.push_back(now);
		while (-1 != next) {
//			(*p_request).BP_PathNode.push_back(next);
//			(*p_request).BP_PathEdge.push_back(path_edge[now]);
			midnode.push_back(next);
			midedge.push_back(path_edge[now]);
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
#ifndef ConsolePrint
		cout << "succeed to find the BP " << (*p_request).BPCostSum << "  :";
		vector<int>::iterator it = (*p_request).BP_PathNode.end();
		do {
			it--;
			cout << p_graph->nid_nindex[(*it)] << " ";
		}while (it != (*p_request).BP_PathNode.begin());
		cout << endl;
#endif

		DisjointPathPair *dispath = new DisjointPathPair(
				p_request->AP_PathEdge.size(), p_request->BP_PathEdge.size());
		std::copy(p_request->AP_PathEdge.begin(), p_request->AP_PathEdge.end(),
				dispath->APedge.begin());
		std::copy(p_request->BP_PathEdge.begin(), p_request->BP_PathEdge.end(),
				dispath->BPedge.begin());
		dispath->APcostsum = p_request->APCostSum;
		dispath->BPcostsum = p_request->BPCostSum;
		dispath->APhop=p_request->AP_PathEdge.size()+1;
				dispath->BPhop=p_request->BP_PathEdge.size()+1;
				dispath->SolutionNotFeasible=false;

		pthread_mutex_lock(&mutex_result);
		if ((dispath->APcostsum) < (AlgorithmResult->APcostsum))
			AlgorithmResult->copyResult(dispath);
		pthread_mutex_unlock(&mutex_result);

//		sem_wait(&mutex_result);
//		if (dispath->APsum < AlgorithmResult->APsum)
//			AlgorithmResult->getResult(dispath);
//		sem_post(&mutex_result);
		delete dispath;
		return true;
	}
}
