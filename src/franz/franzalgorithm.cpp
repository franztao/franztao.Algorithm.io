/*
 * algorithm.cpp
 *
 *  Created on: Jul 8, 2016
 *      Author: Taoheng
 */
#include "../head.h"
#include"../route.h"
#include "gurobi_c++.h"

#include "../franz/networkflow.h"
//#include "lib/lib_io.h"
//#include <glpk.h>

extern Graph *p_graph;
extern pthread_mutex_t mutex_result;
extern DisjointPath *AlgorithmResult;
extern pthread_mutex_t mutex_thread;

int num_thread = 0;
//sem_t mutex_thread;

pthread_cond_t franz_cond;
pthread_mutex_t franz_mutex;
pthread_t franz_tid;

void *ParallelThread(void *vargp);

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
		pthread_t *tid_ex, *tid_in;
		tid_ex = (pthread_t*) malloc(sizeof(pthread_t));
		tid_in = (pthread_t*) malloc(sizeof(pthread_t));
		InclusionExclusionSet *p_set_in = new InclusionExclusionSet(
				p_graph->edgeNum);
		InclusionExclusionSet *p_set_ex = new InclusionExclusionSet(
				p_graph->edgeNum);

		std::copy(p_iter_set->Inclusion.begin(), p_iter_set->Inclusion.end(),
				p_set_ex->Inclusion.begin());
		std::copy(p_iter_set->Exlusion.begin(), p_iter_set->Exlusion.end(),
				p_set_ex->Exlusion.begin());
		p_set_ex->Exlusion.at(p_request->APInclusionEdges[i]) = false;
		rc = pthread_create(tid_ex, NULL, ParallelThread, p_set_ex);
		if (rc) {
			printf("ERROR; return code from pthread_create() is %d\n", rc);
			exit(-1);
		}
		tidvec.push_back(*tid_ex);

		std::copy(p_iter_set->Inclusion.begin(), p_iter_set->Inclusion.end(),
				p_set_in->Inclusion.begin());
		std::copy(p_iter_set->Exlusion.begin(), p_iter_set->Exlusion.end(),
				p_set_in->Exlusion.begin());
		p_set_in->Inclusion.at(p_request->APInclusionEdges[i]) = false;

		p_iter_set->Inclusion.at(p_request->APInclusionEdges[i]) = false;
		rc = pthread_create(tid_in, NULL, ParallelThread, p_set_in);
		if (rc) {
			printf("ERROR; return code from pthread_create() is %d\n", rc);
			exit(-1);
		}
		tidvec.push_back(*tid_in);

	}

	for (unsigned i = 0; i < p_request->APExlusionEdges.size(); i++) {
		pthread_t *tid_ex, *tid_in;
		tid_ex = (pthread_t*) malloc(sizeof(pthread_t));
		tid_in = (pthread_t*) malloc(sizeof(pthread_t));
		InclusionExclusionSet *p_set_in = new InclusionExclusionSet(
				p_graph->edgeNum);
		InclusionExclusionSet *p_set_ex = new InclusionExclusionSet(
				p_graph->edgeNum);

		std::copy(p_iter_set->Inclusion.begin(), p_iter_set->Inclusion.end(),
				p_set_in->Inclusion.begin());
		std::copy(p_iter_set->Exlusion.begin(), p_iter_set->Exlusion.end(),
				p_set_in->Exlusion.begin());
		p_set_in->Inclusion.at(p_request->APExlusionEdges[i]) = false;
		rc = pthread_create(tid_in, NULL, ParallelThread, p_set_in);
		if (rc) {
			printf("ERROR; return code from pthread_create() is %d\n", rc);
			exit(-1);
		}
		tidvec.push_back(*tid_in);

		if ((i + 1) != p_request->APExlusionEdges.size()) {
			std::copy(p_iter_set->Inclusion.begin(),
					p_iter_set->Inclusion.end(), p_set_ex->Inclusion.begin());
			std::copy(p_iter_set->Exlusion.begin(), p_iter_set->Exlusion.end(),
					p_set_ex->Exlusion.begin());
			p_set_ex->Exlusion.at(p_request->APExlusionEdges[i]) = false;
			p_iter_set->Exlusion.at(p_request->APExlusionEdges[i]) = false;
			rc = pthread_create(tid_ex, NULL, ParallelThread, p_set_ex);
			if (rc) {
				printf("ERROR; return code from pthread_create() is %d\n", rc);
				exit(-1);
			}
			tidvec.push_back(*tid_ex);
		}

	}

	for (unsigned i = 0; i < tidvec.size(); i++) {
		pthread_join(tidvec.at(i), NULL);
	}

//	for (unsigned i = 0; i < tidvec.size(); i++){
//		free(&tidvec.at(i));
//	}
	delete p_iter_set;

}

//find the first path(AP),if there is not AP,the algorithm end,otherwise return true and find BP

bool findAP_ILP_gurobi(Graph *p_graph, Request *p_request) {
	try {

		GRBEnv env = GRBEnv();

		GRBModel model = GRBModel(env);

		// Create variables
		vector<GRBVar> e = vector<GRBVar>(p_graph->edgeNum);
		string str = "e";
		char s[10000];
		for (unsigned i = 0; i < p_graph->edgeNum; i++) {
			sprintf(s, "%d", i);

			if (!p_request->APMustPassEdges.at(i))
				e.at(i) = model.addVar(1.0, 1.0, 0.0, GRB_BINARY, (str + s));
			if (!p_request->APMustNotPassEdges.at(i))
				e.at(i) = model.addVar(0.0, 0.0, 0.0, GRB_BINARY, (str + s));
			if ((p_request->APMustPassEdges.at(i))
					&& (p_request->APMustNotPassEdges.at(i))) {
				e.at(i) = model.addVar(0.0, 1.0, 0.0, GRB_BINARY, (str + s));
			}

		}

		// Integrate new variables

		model.update();

		GRBLinExpr obj = 0.0;
		for (int i = 0; i < p_graph->edgeNum; i++) {
			obj += (p_graph->edges.at(i).cost * e.at(i));
		}
		model.setObjective(obj, GRB_MINIMIZE);
		string strc = "c";
		for (int i = 0; i < p_graph->nodeNum; i++) {
			sprintf(s, "%d", i);
			GRBLinExpr con = 0.0;
			for (int j = 0; j < p_graph->edgeNum; j++) {
				if (i == p_graph->edges.at(j).from)
					con += e.at(j);
				if (i == p_graph->edges.at(j).to)
					con += (-1 * e.at(j));
			}
			if (i == p_graph->source)
				model.addConstr(con == 1, (strc + s));
			if (i == p_graph->destination)
				model.addConstr(con == (-1), (strc + s));
			if ((i != p_graph->source) && (i != p_graph->destination))
				model.addConstr(con == 0, (strc + s));

		}
		for (int i = 0; i < p_graph->nodeNum; i++) {
			sprintf(s, "%d", (i + p_graph->nodeNum));
			GRBLinExpr con = 0.0;
			for (int j = 0; j < p_graph->edgeNum; j++) {
				if ((i == p_graph->edges.at(j).from)
						|| ((i == p_graph->edges.at(j).to)))
					con += e.at(j);
			}
			model.addConstr(con <= 2, (strc + s));
		}

// Optimize model

		model.optimize();
//#ifndef ConsolePrint
		for (unsigned i = 0; i < e.size(); i++) {
			cout << e.at(i).get(GRB_StringAttr_VarName) << " "
					<< e.at(i).get(GRB_DoubleAttr_X) << endl;
		}

		cout << "Obj: " << model.get(GRB_DoubleAttr_ObjVal) << endl;
//#endif
		int now = p_graph->source;
		int next;
		int sum = 0;
		int hop = 0;

		(*p_request).AP_PathNode.push_back(now);
		while (now != (p_graph->destination)) {
			for (int i = 0; i < p_graph->edgeNum; i++) {
				if ((1 == e.at(i).get(GRB_DoubleAttr_X))
						&& (now == p_graph->edges.at(i).from)) {
					(*p_request).AP_PathEdge.push_back(i);
					(*p_request).BPMustNotPassEdges4AP[i] = false;
					if (-1 != p_graph->edges[i].ithsrlg)
						(*p_request).APSrlgs.push_back(
								p_graph->edges[i].ithsrlg);
					next = p_graph->edges.at(i).to;
					sum += p_graph->edges.at(i).cost;
					hop++;
				}
			}
			now = next;
			(*p_request).AP_PathNode.push_back(now);
		}

		(*p_request).APCostSum = sum;
		(*p_request).APHopSum = hop;
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
#ifndef ConsolePrint
		cout << "succeed to find the AP cost:(" << (*p_request).APCostSum
		<< ") hop:(" << (*p_request).APHopSum << ") :";
		vector<int>::iterator it = (*p_request).AP_PathNode.begin();
		do {
			cout << p_graph->node_index[(*it)] << " ";
			it++;
		}while (it != (*p_request).AP_PathNode.end());
		cout << endl;

		cout << "edge list " << (*p_request).AP_PathEdge.size() << "  :";
		for (unsigned i = 0; i < (*p_request).AP_PathEdge.size(); i++) {
			cout << (*p_request).AP_PathEdge[i] << " ";
		}
		cout << endl;

		cout << "|AP|:" << p_request->AP_PathEdge.size() << "  |RLAP|:"
		<< p_request->RLAP_PathEdge.size() << endl;
#endif
		return true;

	} catch (GRBException e) {
		cout << "Error code = " << e.getErrorCode() << endl;
		cout << e.getMessage() << endl;
	} catch (...) {
		cout << "Exception during optimization" << endl;
	}
	return false;
}

//find the second path(BP),if there is not BP,the algorithm begin conquer and divide ,otherwise return true and the algorithm get srlg-min-min disjoint path
bool findBP(Graph *p_graph, Request *p_request) {
#ifndef ConsolePrint
	cout << endl << "is finding the BP" << endl;
#endif
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
				i < (*p_graph).topo_Node_fEdgeList[v].edgeList.size(); i++) {
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
#ifndef ConsolePrint
		cout << "failed to find the BP : begin the franz's algorithm" << endl;
#endif
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
#ifndef ConsolePrint
		cout << "succeed to find the BP " << (*p_request).BPCostSum << "  :";
		vector<int>::iterator it = (*p_request).BP_PathNode.end();
		do {
			it--;
			cout << p_graph->node_index[(*it)] << " ";
		}while (it != (*p_request).BP_PathNode.begin());
		cout << endl;
#endif

		DisjointPath *dispath = new DisjointPath(p_request->AP_PathEdge.size(),
				p_request->BP_PathEdge.size());
		std::copy(p_request->AP_PathEdge.begin(), p_request->AP_PathEdge.end(),
				dispath->AP.begin());
		std::copy(p_request->BP_PathEdge.begin(), p_request->BP_PathEdge.end(),
				dispath->BP.begin());
		dispath->APsum = p_request->APCostSum;
		pthread_mutex_lock(&mutex_result);
		if (dispath->APsum < AlgorithmResult->APsum)
			AlgorithmResult->getResult(dispath);
		pthread_mutex_unlock(&mutex_result);
//		sem_wait(&mutex_result);
//		if (dispath->APsum < AlgorithmResult->APsum)
//			AlgorithmResult->getResult(dispath);
//		sem_post(&mutex_result);
		delete dispath;
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
			if ((true == request.APMustPassEdges[e.id])
					&& (true == request.APMustNotPassEdges[e.id])) {
				request.APInclusionEdges.push_back(e.id);
				request.APMustPassEdges[e.id] = false;

			}
		}
		if (((request.APHopSum + 1) == request.edgeCapacity[e.id])
				&& (true == request.STNodeCut[e.from])
				&& (false == request.STNodeCut[e.to])) {
			if ((true == request.APMustPassEdges[e.id])
					&& (true == request.APMustNotPassEdges[e.id])) {
				request.APExlusionEdges.push_back(e.id);
				request.APMustNotPassEdges[e.id] = false;
			}
			int ithsrlg = e.ithsrlg;
			for (unsigned int j = 0; j < request.AP_PathEdge.size(); j++) {
				int id = request.AP_PathEdge[j];
				if (ithsrlg == graph.edges[id].ithsrlg) {
					if ((true == request.APMustPassEdges[e.id])
							&& (true == request.APMustNotPassEdges[e.id])) {
						request.APInclusionEdges.push_back(id);
						request.APMustPassEdges[id] = false;
					}

				}
			}
		}
	}
#ifndef ConsolePrint
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
#endif
}
void GetConflictingSRLGLinkSet(Graph *p_graph, Request *p_request) {
	BuildNetworkFlowGraph(p_graph, p_request);
	MaxFlowAlgorithm_fordfulkerson((*p_graph), (*p_request));
	GetCutEdge((*p_graph), (*p_request));
}

bool findAP_dijastra(Graph *p_graph, Request *p_request) {
#ifndef	ConsolePrint
	cout << endl << "is finding the AP" << endl;
#endif
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
				i < (*p_graph).topo_Node_fEdgeList[v].edgeList.size(); i++) {

			Edge e = (*p_graph).edges.at(
					(*p_graph).topo_Node_fEdgeList[v].edgeList[i]);

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
void *ParallelThread(void *vargp) { //Graph *p_graph,Request *p_request
//	sem_wait(&mutex_thread);

	pthread_mutex_lock(&mutex_thread);
	num_thread++;
	pthread_mutex_unlock(&mutex_thread);
//	sem_post(&mutex_thread);
	InclusionExclusionSet *p_set = (InclusionExclusionSet *) vargp;

	pthread_t tid = pthread_self();
	cout << endl << "------------thread: " << tid << "------------------------"
			<< endl;
	//instead of waiting for another thread to perform PTHREAD_JOIN on it.

	//pthread_detach(tid);
	Request *p_request = new Request(p_graph->source, p_graph->destination,
			p_graph->edgeNum);

	//is value copy or reference copy?
	std::copy(p_set->Inclusion.begin(), p_set->Inclusion.end(),
			p_request->APMustPassEdges.begin());
	std::copy(p_set->Exlusion.begin(), p_set->Exlusion.end(),
			p_request->APMustNotPassEdges.begin());

	if (!findMustNodePath(p_graph, p_request)) { //findAP_dijastra findAP_ILP findAP_ILP_glpk  findAP_ILP_gurobi   findMustNodePath
		printf("%ld thread do not find AP", tid);
	} else {
		if (!findBP(p_graph, p_request)) {
			GetConflictingSRLGLinkSet(p_graph, p_request);
			DivideAndConquer(p_request);
		} else {
			printf("%ld thread find BP ,so find disjoint path", tid);
		}
	}

	delete p_set;
	delete p_request;
	printf("\n-------------------------------num_thread: %d\n", num_thread);
	cout << endl << "------------thread: " << tid << "------------------------"
			<< endl;
//	while(true){
//				printf("fff\n");
//			}
	if (franz_tid == tid) {
		pthread_mutex_lock(&franz_mutex);
		pthread_cond_signal(&franz_cond);
		pthread_mutex_unlock(&franz_mutex);

	}
	pthread_exit(NULL);
	return NULL;
}

//the main structure of our algorithm
bool FranzAlgorithmBasicFlows(Graph *p_graph) {

//	sem_init(&mutex_result, 0, 1);
	pthread_mutex_init(&mutex_result, NULL);
	pthread_mutex_init(&mutex_thread, NULL);
//	sem_init(&mutex_thread, 0, 1);
	InclusionExclusionSet *innclusionexclusionset = new InclusionExclusionSet(
			p_graph->edgeNum);
	int rc;
	rc = pthread_create(&franz_tid, NULL, ParallelThread,
			innclusionexclusionset);
	if (rc) {
		printf("ERROR; return code from pthread_create() is %d\n", rc);
		exit(-1);
	}

	pthread_mutex_init(&franz_mutex, NULL);
	pthread_cond_init(&franz_cond, NULL);
	struct timeval now;
	struct timespec outtime;
	gettimeofday(&now, NULL);
	outtime.tv_sec = now.tv_sec + LimitedTime;
	outtime.tv_nsec = now.tv_usec * 1000;
	pthread_mutex_lock(&franz_mutex);
	pthread_cond_timedwait(&franz_cond, &franz_mutex, &outtime);
	pthread_mutex_unlock(&franz_mutex);

//	rc = pthread_join(tid, NULL);
//	if (rc) {
//		printf("ERROR; return code from pthread_join() is %d\n", rc);
//		exit(-1);
//	}

	if (INT_MAX == AlgorithmResult->APsum) {
		return false;
	} else {
		return true;
	}
}

