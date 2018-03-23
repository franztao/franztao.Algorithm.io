#include "../head.h"

pthread_mutex_t findAP_permute_mutex;
GraphTopo *findAP_interval_p_graph;
bool findAP_orderedegde_dijastra(GraphTopo *p_graph, Request *p_request, int source,
		int destination, vector<int>& node_vis, vector<int>& edge_vis) {
	typedef pair<int, int> P;
	vector<int> dist((*p_graph).nodeNum, (-1));
	vector<int> hop((*p_graph).nodeNum, (-1));
	vector<int> path_node((*p_graph).nodeNum, (-1));
	vector<int> path_edge((*p_graph).nodeNum, (-1));
	priority_queue<P, vector<P>, greater<P> > que;
	unsigned int len;
	dist[source] = 0;
	hop[source] = 0;
	que.push(P(0, source));
	while (!que.empty()) {
		P p = que.top();
		que.pop();
		int v = p.second;
		if (dist[v] < p.first)
			continue;
		len = (*p_graph).ftopo_r_Node_c_EdgeList[v].edgeList.size();
		for (unsigned int i = 0; i < len; i++) {
			EdgeClass &e = p_graph->getithEdge(
					(*p_graph).ftopo_r_Node_c_EdgeList[v].edgeList[i]);
			if (!p_request->APMustNotPassEdges[e.id])
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
	if (-1 == dist[destination]) {
		return false;
	} else {
		int next = destination;
		while (source != next) {
			if ((-1 != node_vis[next])) { //erase all edge whose out-edge is initial source node;
				return false;
			} else {
				node_vis[next] = path_node[next];
				edge_vis[next] = path_edge[next];
			}
			next = path_node[next];
		}
	}
	return true;
}

void record_APinfo(GraphTopo *p_graph, Request *p_request, vector<int>& path_node,
		vector<int>& path_edge) {
	int now;
	int next;

	int APCostSum = 0;
	now = (*p_graph).destination;
	vector<int> midnode;
	vector<int> midedge;
	next = path_node[now];
	midnode.push_back(now);

	while (next != -1) {
		APCostSum += p_graph->getithEdge(path_edge[now]).cost;
		now = next;
		next = path_node[now];
	}
	//more than the former answer,directly return
	if ((0 != p_request->APCostSum) && (APCostSum >= p_request->APCostSum)) {
		return;
	}

	int APHopSum = 1;
	APCostSum = 0;
	now = (*p_graph).destination;
	next = path_node[now];
	midnode.push_back(now);

	p_request->APSrlgs.clear();
	p_request->AP_PathNode.clear();
	p_request->AP_PathEdge.clear();
	p_request->BPMustNotPassEdges4AP.clear();
	p_request->BPMustNotPassEdgesRLAP.clear();
	p_request->RLAP_PathEdge.clear();

	while (next != -1) {
		APHopSum++;
		midnode.push_back(next);
		midedge.push_back(path_edge[now]);
		APCostSum += p_graph->getithEdge(path_edge[now]).cost;

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

	(*p_request).APCostSum = APCostSum;
	(*p_request).APHopSum = APHopSum;

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
	return;
}

bool findAP_permute_dijastra(Request *p_request, vector<int> *p_permute) {
	int source = findAP_interval_p_graph->source;
	int destination; //=p_graph->destination;
	vector<int> node_vis = vector<int>(findAP_interval_p_graph->nodeNum, -1);
	vector<int> edge_vis = vector<int>(findAP_interval_p_graph->edgeNum);
	//findAP_interval_p_request
	for (unsigned int i = 0; i <= p_permute->size(); i++) {
		if (i < p_permute->size())
			destination =
					findAP_interval_p_graph->getithEdge(p_permute->at(i)).from;
		else
			destination = findAP_interval_p_graph->destination;
		if (source != destination) {
			if (!findAP_orderedegde_dijastra(findAP_interval_p_graph, p_request,
					source, destination, node_vis, edge_vis)) {
				return false;
			}
		}
		if (i < p_permute->size()) {
			node_vis[findAP_interval_p_graph->getithEdge(p_permute->at(i)).to] =
					findAP_interval_p_graph->getithEdge(p_permute->at(i)).from;
			edge_vis[findAP_interval_p_graph->getithEdge(p_permute->at(i)).to] =
					p_permute->at(i);
			source = findAP_interval_p_graph->getithEdge(p_permute->at(i)).to;
		}
	}

	pthread_mutex_lock(&findAP_permute_mutex);
	record_APinfo(findAP_interval_p_graph, p_request, node_vis, edge_vis);
	pthread_mutex_unlock(&findAP_permute_mutex);

	return true;
}
void *findAP_permute_dijastraParallelThread(void *vargp) {
	Internal_Path *p_Internal_Path = (Internal_Path *) vargp;
	findAP_permute_dijastra(p_Internal_Path->p_request,
			&(p_Internal_Path->permute));

	delete p_Internal_Path;
	pthread_exit(NULL);
	return NULL;
}
bool findAP_interval_dijastra(GraphTopo *p_graph, Request *p_request,
		vector<int> &permute) {
	pthread_mutex_init(&findAP_permute_mutex, NULL);
	sort(permute.begin(), permute.end());
//	p_request->APCostSum=0;

	int rc;
//	findAP_interval_p_request = p_request;
	findAP_interval_p_graph = p_graph;

	vector<pthread_t> tidvec;
	do {
		pthread_t *tid;
		tid = (pthread_t*) malloc(sizeof(pthread_t));

		Internal_Path *p_Internal_Path = new Internal_Path();
		p_Internal_Path->p_request = p_request;
		p_Internal_Path->permute = vector<int>(permute.size());

		p_Internal_Path->permute.assign(permute.begin(), permute.end());
		rc = pthread_create(tid, NULL, findAP_permute_dijastraParallelThread,
				p_Internal_Path);

		if (rc) {
			printf("ERROR; return code from pthread_create() is %d\n", rc);
			exit(exit_pthread_create);
		}
		tidvec.push_back(*tid);
	} while (next_permutation(permute.begin(), permute.end()));
	for (unsigned i = 0; i < tidvec.size(); i++) {
		pthread_join(tidvec.at(i), NULL);
	}

//bool get_AP;
//	do {
//
//		findAP_interval_p_request = p_request;
//		findAP_interval_p_graph = p_graph;
//		get_AP=findAP_permute_dijastra(&permute);
//		//this problem should be solved.important~~!!!!!!
//		if (get_AP) {
//			return true;
//		}
//	} while (next_permutation(permute.begin(), permute.end()));

//	for (unsigned int i = 0; i < permute.size(); i++) {
//		cout << p_graph->getithEdge(permute.at(i)).from << "  ----  "
//				<< p_graph->getithEdge(permute.at(i)).to << endl;
//	}
//	cout << endl;
	if (0 != p_request->APCostSum)
//	if (get_AP)
		return true;
	else
		return false;
}
