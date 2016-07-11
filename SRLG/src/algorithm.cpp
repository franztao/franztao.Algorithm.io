/*
 * algorithm.cpp
 *
 *  Created on: Jul 8, 2016
 *      Author: Taoheng
 */
#include "head.h"
bool findAP(Graph *p_graph, Request *p_request) {
	cout << "is finding the AP" << endl;

	typedef pair<int, int> P;
	vector<int> dist((*p_graph).NodeNum, (-1));
	vector<int> path_node((*p_graph).NodeNum, (-1));
	vector<int> path_edge((*p_graph).NodeNum, (-1));
	//	for(int i=0;i<NodeSizeofInputGragh;i++){
	//		dist.push_back(-1);
	//		path.push_back(-1);
	//	}
	priority_queue<P, vector<P>, greater<P> > que;
	dist[(*p_graph).Source] = 0;
	que.push(P(0, (*p_graph).Source));
	while (!que.empty()) {
		P p = que.top();
		que.pop();
		int v = p.second;

		if (dist[v] < p.first)
			continue;
		for (unsigned int i = 0; i < (*p_graph).GraphList[v].edgelist.size();
				i++) {

			Edge e = (*p_graph).GraphList[v].edgelist[i];
			if (0 == (p_request->APedgestabu.size())) {
				cout << "p_request->APedgestabu is " << "NULL" << endl;
			}
			if (false == p_request->APedgestabu[e.id])
				continue;
			if (-1 == dist[e.to]) {
				dist[e.to] = dist[v] + e.cost;
				path_node[e.to] = v;
				path_edge[e.to] = e.id;
				que.push(P(dist[e.to], e.to));
			} else {
				if (dist[e.to] > dist[v] + e.cost) {
					dist[e.to] = dist[v] + e.cost;
					path_node[e.to] = v;
					path_edge[e.to] = e.id;
					que.push(P(dist[e.to], e.to));
				}
			}
		}
	}
	for (int i = 0; i < p_graph->NodeNum; i++) {
		cout << p_graph->node_index[i] << " ---:" << dist[i] << endl;
	}

	if (-1 == dist[(*p_graph).Destination]) {
		cout << "failed to find the AP" << endl;
		return false;
	} else {
		int now;
		int next;

		now = (*p_graph).Destination;
		(*p_request).AP_node.push_back(now);
		next = path_node[now];
		while (next != -1) {
			(*p_request).AP_node.push_back(next);
			(*p_request).AP_edge.push_back(path_edge[now]);
			(*p_request).AP_WeightSum += p_graph->edges[path_edge[now]].cost;

			(*p_request).BPedgestabu[path_edge[now]] = false;
			if (-1 != p_graph->edges[path_edge[now]].ithslrg)
				(*p_request).AP_srlgs.push_back(
						p_graph->edges[path_edge[now]].ithslrg);
			now = next;
			next = path_node[now];

		}
		for (unsigned int i = 0; i < (*p_request).AP_srlgs.size(); i++) {
			int srlg = (*p_request).AP_srlgs[i];
			for (unsigned int j = 0;
					j < (*p_graph).SRLGGroups[srlg].srlgMember.size(); j++) {
				int srlgmem = (*p_graph).SRLGGroups[srlg].srlgMember[j];
				(*p_request).BPedgestabu[srlgmem] = false;
			}
		}
		cout << "succeed to find the AP " << (*p_request).AP_WeightSum << ": ";
		vector<int>::iterator it = (*p_request).AP_node.end();
		do {
			it--;
			cout << p_graph->node_index[(*it)] << " ";
		} while (it != (*p_request).AP_node.begin());
		cout << endl;
		return true;
	}
}
bool findBP(Graph *p_graph, Request *p_request) {
	cout << "is finding the BP" << endl;
	typedef pair<int, int> P;
	vector<int> dist((*p_graph).NodeNum, (-1));
	vector<int> path_node((*p_graph).NodeNum, (-1));
	vector<int> path_edge((*p_graph).NodeNum, (-1));
	priority_queue<P, vector<P>, greater<P> > que;
	dist[(*p_graph).Source] = 0;
	que.push(P(0, (*p_graph).Source));
	while (!que.empty()) {
		P p = que.top();
		que.pop();
		int v = p.second;
		if (dist[v] < p.first)
			continue;
		for (unsigned int i = 0; i < (*p_graph).GraphList[v].edgelist.size();
				i++) {
			Edge e = (*p_graph).GraphList[v].edgelist[i];
			if (false == p_request->BPedgestabu[e.id])
				continue;
			if (-1 == dist[e.to]) {
				dist[e.to] = dist[v] + e.cost;
				path_node[e.to] = v;
				path_edge[e.to] = e.id;
				que.push(P(dist[e.to], e.to));
			} else {
				if (dist[e.to] > dist[v] + e.cost) {
					dist[e.to] = dist[v] + e.cost;
					path_node[e.to] = v;
					path_edge[e.to] = e.id;
					que.push(P(dist[e.to], e.to));
				}
			}
		}
	}
	if (-1 == path_node[(*p_graph).Destination]) {
		cout << "failed to find the BP : begin the franz's algorithm" << endl;
		return false;
	} else {
		int now;
		int next;

		now = (*p_graph).Destination;
		(*p_request).BP_node.push_back(now);
		next = path_node[now];
		while (next != -1) {
			(*p_request).BP_node.push_back(next);
			(*p_request).BP_edge.push_back(path_edge[now]);
			(*p_request).BP_WeightSum += p_graph->edges[path_edge[now]].cost;

			now = next;
			next = path_node[now];

		}

		cout << "succeed to find the BP " << (*p_request).BP_WeightSum << "  :";
		vector<int>::iterator it = (*p_request).BP_node.end();
		do {
			it--;
			cout << p_graph->node_index[(*it)] << " ";
		} while (it != (*p_request).BP_node.begin());
		cout << endl;
		return true;
	}
}
void parallel() {

}
bool mainprocedure4algorithm(Graph *p_graph, Request *p_request) {
	//if(false==findAP(graph,request))
	if (false == findAP(p_graph, p_request))
		return false;
	if (false == findBP(p_graph, p_request)) {
		parallel();
	} else {
		return true;
	}
	return true;
}

