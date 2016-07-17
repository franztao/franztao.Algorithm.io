/*
 * algorithm.cpp
 *
 *  Created on: Jul 8, 2016
 *      Author: Taoheng
 */
#include "head.h"
#include "networkflow.h"

extern Graph *p_graph;
extern sem_t mutex_result;
extern vector<DisjointPath> result;

int num_thread = 0;
sem_t mutex_thread;

void *ParallelThread(void *vargp);
//find the first path(AP),if there is not AP,the algorithm end,otherwise return true and find BP
bool findAP(Graph *p_graph, Request *p_request) {
	cout << endl << "is finding the AP" << endl;

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
		for (unsigned int i = 0; i < (*p_graph).graphList[v].edgeList.size();
				i++) {

			Edge e = (*p_graph).edges.at((*p_graph).graphList[v].edgeList[i]);
			if (0 == (p_request->APMustNotPassEdges.size())) {
				cout << "p_request->APedgestabu is " << "NULL" << endl;
			}
			if (!p_request->APMustNotPassEdges[e.id])
				continue;
			if (-1 == dist[e.to]) {
				dist[e.to] = dist[v] + e.cost;
				hop[e.to] = hop[v] + 1;
				path_node[e.to] = v;
				path_edge[e.to] = e.id;
				que.push(P(dist[e.to], e.to));
			} else {
				if (dist[e.to] > dist[v] + e.cost) {
					dist[e.to] = dist[v] + e.cost;
					hop[e.to] = hop[v] + 1;
					path_node[e.to] = v;
					path_edge[e.to] = e.id;
					que.push(P(dist[e.to], e.to));
				}
			}
		}
	}

	for (int i = 0; i < p_graph->nodeNum; i++) {
		cout << p_graph->node_index[i] << " ---:" << dist[i] << endl;
	}

	if (-1 == dist[(*p_graph).destination]) {
		cout << "failed to find the AP" << endl;
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
			//(*p_request).AP_CostSum += p_graph->edges[path_edge[now]].cost;

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

		cout << "succeed to find the AP cost:(" << (*p_request).APCostSum
				<< ") hop:(" << (*p_request).APHopSum << ") :";
		vector<int>::iterator it = (*p_request).AP_PathNode.end();
		do {
			it--;
			cout << p_graph->node_index[(*it)] << " ";
		} while (it != (*p_request).AP_PathNode.begin());
		cout << endl;

		cout << "edge list " << (*p_request).AP_PathEdge.size() << "  :";
		for (unsigned i = 0; i < (*p_request).AP_PathEdge.size(); i++) {
			cout << (*p_request).AP_PathEdge[i] << " ";
		}
		cout << endl;

		cout << "|AP|:" << p_request->AP_PathEdge.size() << "  |RLAP|:"
				<< p_request->RLAP_PathEdge.size() << endl;

		return true;
	}
}

//find the second path(BP),if there is not BP,the algorithm begin conquer and divide ,otherwise return true and the algorithm get srlg-min-min disjoint path
bool findBP(Graph *p_graph, Request *p_request) {
	cout << endl << "is finding the BP" << endl;

	typedef pair<int, int> P;
	vector<int> dist((*p_graph).nodeNum, (-1));
	vector<int> path_node((*p_graph).nodeNum, (-1));
	vector<int> path_edge((*p_graph).nodeNum, (-1));
	priority_queue<P, vector<P>, greater<P> > que;
	dist[(*p_graph).source] = 0;
	que.push(P(0, (*p_graph).source));

	while (!que.empty()) {
		P p = que.top();
		que.pop();
		int v = p.second;
		if (dist[v] < p.first)
			continue;
		for (unsigned int i = 0; i < (*p_graph).graphList[v].edgeList.size();
				i++) {
			Edge e = (*p_graph).edges.at((*p_graph).graphList[v].edgeList[i]);
			if (!p_request->BPMustNotPassEdges4AP[e.id]
					|| !p_request->BPMustNotPassEdgesRLAP[e.id])
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
	if (-1 == path_node[(*p_graph).destination]) {
		cout << "failed to find the BP : begin the franz's algorithm" << endl;
		return false;
	} else {
		int now;
		int next;

		now = (*p_graph).destination;
		(*p_request).BP_PathNode.push_back(now);
		next = path_node[now];
		while (next != -1) {
			(*p_request).BP_PathNode.push_back(next);
			(*p_request).BP_PathEdge.push_back(path_edge[now]);
			(*p_request).BPCostSum += p_graph->edges[path_edge[now]].cost;

			now = next;
			next = path_node[now];

		}

		cout << "succeed to find the BP " << (*p_request).BPCostSum << "  :";
		vector<int>::iterator it = (*p_request).BP_PathNode.end();
		do {
			it--;
			cout << p_graph->node_index[(*it)] << " ";
		} while (it != (*p_request).BP_PathNode.begin());
		cout << endl;
		DisjointPath *dispath = new DisjointPath(p_request->AP_PathEdge.size(),
				p_request->BP_PathEdge.size());
		std::copy(p_request->AP_PathEdge.begin(), p_request->AP_PathEdge.end(),
				dispath->AP.begin());
		std::copy(p_request->BP_PathEdge.begin(), p_request->BP_PathEdge.end(),
				dispath->BP.begin());
		sem_wait(&mutex_result);
		result.push_back(*dispath);
		sem_post(&mutex_result);
		return true;
	}
}
void BuildNetworkFlowGraph(Graph *p_graph, Request *p_request) {
	int AP = p_request->AP_PathEdge.size();
	int RLAP = p_request->RLAP_PathEdge.size();
	for (unsigned int i = 0; i < p_graph->edges.size(); i++) {
		if (!p_request->BPMustNotPassEdgesRLAP[i]) {
			p_request->edgeCapacity.at(i) = AP + 1;
		}
		if (p_request->BPMustNotPassEdgesRLAP[i]
				&& p_request->BPMustNotPassEdges4AP[i]) {
			p_request->edgeCapacity.at(i) = (RLAP + 1) * (AP + 1);
		}
	}
	return;
}

void GetCutEdge(Graph& graph, Request &request) {
	for (int i = 0; i < graph.edgeNum; i++) {
		Edge e = graph.edges.at(i);
		if ((1 == request.edgeCapacity[e.id])
				&& (false == request.STNodeCut[e.from])
				&& (true == request.STNodeCut[e.to])) {
			if (true == request.APMustPassEdges[e.id]) {
				request.APInclusionEdges.push_back(e.id);
				request.APMustPassEdges[e.id] = false;

			}
		}
		if (((request.APHopSum + 1) == request.edgeCapacity[e.id])
				&& (true == request.STNodeCut[e.from])
				&& (false == request.STNodeCut[e.to])) {
			if (true == request.APMustNotPassEdges[e.id]) {

				request.APExlusionEdges.push_back(e.id);
				request.APMustNotPassEdges[e.id] = false;

			}
			int ithsrlg = e.ithsrlg;
			for (unsigned int j = 0; j < request.AP_PathEdge.size(); j++) {
				int id = request.AP_PathEdge[j];
				if (ithsrlg == graph.edges[id].ithsrlg) {
					if (true == request.APMustPassEdges[id]) {
						request.APInclusionEdges.push_back(id);
						request.APMustPassEdges[id] = false;
					}

				}
			}
		}
	}

	cout << "Exlusion: " << endl;
	for (unsigned i = 0; i < request.APExlusionEdges.size(); i++) {
		int id = request.APExlusionEdges[i];
		cout << graph.node_index[graph.edges[id].from] << "  "
				<< graph.node_index[graph.edges[id].to] << endl;
	}
	cout << endl << "Inclusion: " << endl;
	for (unsigned i = 0; i < request.APInclusionEdges.size(); i++) {
		int id = request.APInclusionEdges[i];
		cout << graph.node_index[graph.edges[id].from] << "  "
				<< graph.node_index[graph.edges[id].to] << endl;
	}
}
void GetConflictingSRLGLinkSet(Graph *p_graph, Request *p_request) {
	BuildNetworkFlowGraph(p_graph, p_request);
	MaxFlowAlgorithm_fordfulkerson((*p_graph), (*p_request));
	GetCutEdge((*p_graph), (*p_request));
}
void DivideAndConquer(Request *p_request) {

	int rc;
	vector<pthread_t> tidvec;
	InclusionExclusionSet *p_iter_set = new InclusionExclusionSet(
			p_graph->edgeNum);

	std::copy(p_request->APMustPassEdges.begin(),
			p_request->APMustPassEdges.end(), p_iter_set->Inclusion.begin());
	std::copy(p_request->APMustNotPassEdges.begin(),
			p_request->APMustNotPassEdges.end(), p_iter_set->Exlusion.begin());

	for (unsigned i = 0; i < p_request->APInclusionEdges.size(); i++) {
		pthread_t tid_ex, tid_in;
		InclusionExclusionSet *p_set_in = new InclusionExclusionSet(
				p_graph->edgeNum);
		InclusionExclusionSet *p_set_ex = new InclusionExclusionSet(
				p_graph->edgeNum);

		std::copy(p_iter_set->Inclusion.begin(), p_iter_set->Inclusion.end(),
				p_set_ex->Inclusion.begin());
		std::copy(p_iter_set->Exlusion.begin(), p_iter_set->Exlusion.end(),
				p_set_ex->Exlusion.begin());
		p_set_ex->Exlusion.at(p_request->APInclusionEdges[i]) = false;
		rc = pthread_create(&tid_ex, NULL, ParallelThread, p_set_ex);
		if (rc) {
			printf("ERROR; return code from pthread_create() is %d\n", rc);
			exit(-1);
		}
		tidvec.push_back(tid_ex);

		std::copy(p_iter_set->Inclusion.begin(), p_iter_set->Inclusion.end(),
				p_set_in->Inclusion.begin());
		std::copy(p_iter_set->Exlusion.begin(), p_iter_set->Exlusion.end(),
				p_set_in->Exlusion.begin());
		p_set_in->Inclusion.at(p_request->APInclusionEdges[i]) = false;
		p_iter_set->Inclusion.at(p_request->APInclusionEdges[i]) = false;
		rc = pthread_create(&tid_in, NULL, ParallelThread, p_set_in);
		if (rc) {
			printf("ERROR; return code from pthread_create() is %d\n", rc);
			exit(-1);
		}
		tidvec.push_back(tid_in);

	}

	for (unsigned i = 0; i < p_request->APExlusionEdges.size(); i++) {
		pthread_t tid_ex, tid_in;
		InclusionExclusionSet *p_set_in = new InclusionExclusionSet(
				p_graph->edgeNum);
		InclusionExclusionSet *p_set_ex = new InclusionExclusionSet(
				p_graph->edgeNum);

		std::copy(p_iter_set->Inclusion.begin(), p_iter_set->Inclusion.end(),
				p_set_in->Inclusion.begin());
		std::copy(p_iter_set->Exlusion.begin(), p_iter_set->Exlusion.end(),
				p_set_in->Exlusion.begin());
		p_set_in->Inclusion.at(p_request->APExlusionEdges[i]) = false;
		rc = pthread_create(&tid_in, NULL, ParallelThread, p_set_in);
		if (rc) {
			printf("ERROR; return code from pthread_create() is %d\n", rc);
			exit(-1);
		}
		tidvec.push_back(tid_in);

		if ((i + 1) != p_request->APExlusionEdges.size()) {
			std::copy(p_iter_set->Inclusion.begin(),
					p_iter_set->Inclusion.end(), p_set_ex->Inclusion.begin());
			std::copy(p_iter_set->Exlusion.begin(), p_iter_set->Exlusion.end(),
					p_set_ex->Exlusion.begin());
			p_set_ex->Exlusion.at(p_request->APExlusionEdges[i]) = false;
			p_iter_set->Exlusion.at(p_request->APExlusionEdges[i]) = false;
			rc = pthread_create(&tid_ex, NULL, ParallelThread, p_set_ex);
			if (rc) {
				printf("ERROR; return code from pthread_create() is %d\n", rc);
				exit(-1);
			}
			tidvec.push_back(tid_ex);
		}

	}
	for (unsigned i = 0; i < tidvec.size(); i++) {
		pthread_join(tidvec.at(i), NULL);
	}
	delete p_iter_set;

}

void *ParallelThread(void *vargp) { //Graph *p_graph,Request *p_request
	sem_wait(&mutex_thread);
	num_thread++;
	sem_post(&mutex_thread);
	InclusionExclusionSet *p_set = (InclusionExclusionSet *) vargp;

	pthread_t tid = pthread_self();
	cout << endl << "------------------------" << tid
			<< "------------------------" << endl;
	pthread_detach(tid);
	Request *p_request = new Request(p_graph->source, p_graph->destination,
			p_graph->edgeNum);

	//is value copy or reference copy?
	std::copy(p_set->Inclusion.begin(), p_set->Inclusion.end(),
			p_request->APMustPassEdges.begin());
	std::copy(p_set->Exlusion.begin(), p_set->Exlusion.end(),
			p_request->APMustNotPassEdges.begin());

	if (!findAP(p_graph, p_request)) {
		printf("%ld thread do not find AP", tid);
	}
	if (!findBP(p_graph, p_request)) {
		GetConflictingSRLGLinkSet(p_graph, p_request);
		DivideAndConquer(p_request);
	} else {
		printf("%ld thread find BP ,so find disjoint path", tid);
	}

	delete p_set;
	delete p_request;
	//cout<<endl<<"FFFFFFFFFFF-------------------------------num_thread:"<<num_thread<<endl;
	printf("\nFFFFFFFFFFF-------------------------------num_thread: %d\n",
			num_thread);
	pthread_exit(NULL);
	return NULL;
}

//the main structure of our algorithm
bool AlgorithmBasicFlows(Graph *p_graph) {
	pthread_t tid;
	sem_init(&mutex_result, 0, 1);
	sem_init(&mutex_thread, 0, 1);
	InclusionExclusionSet *innclusionexclusionset = new InclusionExclusionSet(
			p_graph->edgeNum);
	int rc;
	rc = pthread_create(&tid, NULL, ParallelThread, innclusionexclusionset);
	if (rc) {
		printf("ERROR; return code from pthread_create() is %d\n", rc);
		exit(-1);
	}
	//cout<<"|||||||||||||||||Be Successful to find disjoint path|||||||||||||||||||"<<endl;
	pthread_join(tid, NULL);
	//if(result.size()>0)

	return true;
}

