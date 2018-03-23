#include "route.h"
#include<iostream>
#include "lib/lib_record.h"
#include "lib/lib_time.h"
#include "head.h"

GraphTopo *p_graph;
DisjointPathPair *AlgorithmResult;
//mutex lock for saving reslut
pthread_mutex_t mutex_result;
//pthread_mutex_t mutex_thread;
int Algorithm;
bool reverseDFS_mustedgeoutnode(GraphTopo *p_graph, Request *p_request,
		vector<int> &permute) {

	typedef pair<int, int> P;
	vector<int> dist((*p_graph).nodeNum, (-1));
	vector<int> hop((*p_graph).nodeNum, (-1));
	priority_queue<P, vector<P>, greater<P> > que;
	unsigned int len;
	dist[p_graph->destination] = 0;
	hop[p_graph->destination] = 0;
	que.push(P(0, p_graph->destination));

	while (!que.empty()) {
		P p = que.top();
		que.pop();
		int v = p.second;

		if (dist[v] < p.first)
			continue;
		len = (*p_graph).rtopo_r_Node_c_EdgeList[v].edgeList.size();
		for (unsigned int i = 0; i < len; i++) {

			EdgeClass &e = p_graph->getithEdge(
					(*p_graph).rtopo_r_Node_c_EdgeList[v].edgeList[i]);

			if ((!p_request->APMustNotPassEdges[e.id])) //|| (!p_request->APMustPassEdges[e.id]))
				continue;
			if (-1 == dist[e.from]) {
				dist[e.from] = dist[v] + e.cost;
				hop[e.from] = hop[v] + 1;
				que.push(P(dist[e.from], e.from));
			} else {
				if (dist[e.from] > (dist[v] + e.cost)) {
					dist[e.from] = dist[v] + e.cost;
					hop[e.from] = hop[v] + 1;
					que.push(P(dist[e.from], e.from));
				} else {
					if (dist[e.from] == (dist[v] + e.cost)) {
						if (hop[e.from] > (hop[v] + 1)) {
							hop[e.from] = hop[v] + 1;
							que.push(P(dist[e.from], e.from));
						}
					}
				}
			}
		}
	}
//
//	cout << "*****************" << endl;
//	for (unsigned i = 0; i < p_graph->edgeNum; i++) {
//		if (!p_request->APMustPassEdges[i]) {
//			cout << "must" << " "
//					<< p_graph->nid_nindex[p_graph->getithEdge(i).from] << " "
//					<< p_graph->nid_nindex[p_graph->getithEdge(i).to] << endl;
//		}
//
//	}
//	cout << endl;
//	for (unsigned i = 0; i < p_graph->edgeNum; i++) {
//
//		if (!p_request->APMustNotPassEdges[i]) {
//			cout << "must not " << " "
//					<< p_graph->nid_nindex[p_graph->getithEdge(i).from] << " "
//					<< p_graph->nid_nindex[p_graph->getithEdge(i).to] << endl;
//		}
//	}
//
//	cout << "-------------" << endl;

	for (unsigned i = 0; i < permute.size(); i++) {
		if (-1 == dist[p_graph->getithEdge(permute.at(i)).to]) {
			return false;
		}
	}
	return true;
}

//
void printAnswer(DisjointPathPair *FranzAlgorithmResult, bool getanswer) {

//	cout << endl
//			<< "**************Successful to find disjoint path**************"
//			<< endl;
	if (!getanswer) {
		cout << "APcost:" << "-1" << endl;
		cout << "BPcost:" << "-1" << endl;
		cout << "CostSum:" << "-1" << endl;
		cout << "APhop:" << "-1" << endl;
		cout << "BPhop:" << "-1" << endl;
		cout << "HopSum:" << "-1" << endl;
		return;
	}
	cout << "APcost:" << FranzAlgorithmResult->APcostsum << "  " << endl;
	cout << "BPcost:" << FranzAlgorithmResult->BPcostsum << "  " << endl;
	cout << "CostSum:"
			<< (FranzAlgorithmResult->APcostsum
					+ FranzAlgorithmResult->BPcostsum) << "  " << endl;
	cout << "APhop:" << FranzAlgorithmResult->APhop << "  " << endl;
	cout << "BPhop:" << FranzAlgorithmResult->BPhop << "  " << endl;
	cout << "HopSum:"
			<< (FranzAlgorithmResult->APhop + FranzAlgorithmResult->BPhop)
			<< "  " << endl;
	return;
	cout << "AP edge(" << (FranzAlgorithmResult->APhop - 1) << "): ";
	unsigned int i = 0;
	for (i = 0; i < FranzAlgorithmResult->APedge.size(); i++) {
		record_result(WORK_PATH, FranzAlgorithmResult->APedge.at(i));
		cout << FranzAlgorithmResult->APedge.at(i) << "   ";
	}
	cout << endl;

	cout << "AP node(" << (FranzAlgorithmResult->APhop) << "): ";
	for (i = 0; i < FranzAlgorithmResult->APedge.size(); i++) {

		cout
				<< p_graph->nid_nindex[p_graph->getithEdge(
						FranzAlgorithmResult->APedge.at(i)).from] << "   ";

	}
	cout
			<< p_graph->nid_nindex[p_graph->getithEdge(
					FranzAlgorithmResult->APedge.at(i - 1)).to] << "   ";
	cout << endl;

	cout << "BP edge(" << (FranzAlgorithmResult->BPhop - 1) << "): ";
	for (i = 0; i <= (FranzAlgorithmResult->BPedge.size() - 1); i++) {
		record_result(BACK_PATH, FranzAlgorithmResult->BPedge.at(i));
		cout << FranzAlgorithmResult->BPedge.at(i) << "   ";
	}
	cout << endl;
	cout << "BP node(" << (FranzAlgorithmResult->BPhop) << "): ";
	for (i = 0; i <= (FranzAlgorithmResult->BPedge.size() - 1); i++) {
		cout
				<< p_graph->nid_nindex[p_graph->getithEdge(
						FranzAlgorithmResult->BPedge.at(i)).from] << "   ";
	}

	cout
			<< p_graph->nid_nindex[p_graph->getithEdge(
					FranzAlgorithmResult->BPedge.at(i - 1)).to] << "   ";
	cout << endl << endl;

}
//verify whether the answer is really right.
void verify_result(DisjointPathPair *FranzAlgorithmResult) {

}

void search_route(char *topo[MAX_EDGE_NUM], int edge_num,
		char *demand[MAX_DEMAND_NUM], int demand_num, char *srlg[MAX_SRLG_NUM],
		int srlg_num, int algorithm, char *topo_file_name,
		char *srlg_file_name) {

	Algorithm = algorithm;
	//init result's class .
	AlgorithmResult = new DisjointPathPair();
	p_graph = new GraphTopo(edge_num);
	//load the graph's topological detailed information
	if (!LoadGraphData(p_graph, topo, edge_num, demand, demand_num, srlg,
			srlg_num)) {
		printf("Error:LoadGraphData\n");
		return;
	}
	DebugPrint(p_graph);

//	p_graph->getithEdge(0).from

	//franz
	if ((algorithm_franz == algorithm) || (Algorithm_All == algorithm)) {
		AlgorithmResult->clearResult();
		cout << endl << "--------------------------------------------" << endl;
		cout << "SCLS" << endl;
		print_time("");
		if (FranzAlgorithmBasicFlows(p_graph)) {
			printAnswer(AlgorithmResult, true);
		} else {
			printAnswer(AlgorithmResult, false);
		}
		if (AlgorithmResult->IsParalle)
			cout << "isParallar:" << 1 << endl;
		else
			cout << "isParallar:" << 0 << endl;
		print_time("FranzAlgorithmEnd");
		cout << "--------------------------------------------" << endl << endl;
	}

	if ((algorithm_TA == algorithm) || (Algorithm_All == algorithm)) {

		AlgorithmResult->clearResult();
		cout << endl << "--------------------------------------------" << endl;
		cout << "TA" << endl;
		print_time("");
		if (TAAlgorithmBasicFlows(p_graph)) {
			printAnswer(AlgorithmResult, true);
		} else {
			printAnswer(AlgorithmResult, false);
		}
		print_time("TAEnd");
		cout << "--------------------------------------------" << endl << endl;
	}

	//KSP
	if (((algorithm_IHKSP == algorithm) || (Algorithm_All == algorithm))) {

		AlgorithmResult->clearResult();
		cout << endl << "--------------------------------------------" << endl;
		cout << "KSP" << endl;
		print_time("");
		if (KSPAlgorithmBasicFlows(p_graph)) { //ILPAlgorithmBasicFlows ILPAlgorithm_glpk
			printAnswer(AlgorithmResult, true);
		} else {
			printAnswer(AlgorithmResult, false);
		}
		print_time("IHKSPEnd");
		cout << "--------------------------------------------" << endl << endl;
	}

	//ILP
	if ((algorithm_ILP == algorithm) || (Algorithm_All == algorithm)) {

		AlgorithmResult->clearResult();
		cout << endl << "--------------------------------------------" << endl;
		cout << "ILP" << endl;
		print_time("");
		if (IPAlgorithmBasicFlows(p_graph, algorithm_ILP)) { // ILPAlgorithmBasicFlows   ILPAlgorithmBasicFlows_LocalSolver
			printAnswer(AlgorithmResult, true);
		} else {
			printAnswer(AlgorithmResult, false);
		}
		print_time("ILPminEnd");
		cout << "--------------------------------------------" << endl << endl;
	}

	//IQP
	if ((algorithm_IQP == algorithm) || (Algorithm_All == algorithm)) {

		AlgorithmResult->clearResult();
		cout << endl << "--------------------------------------------" << endl;
		cout << "IQP" << endl;
		print_time("");
		if (IPAlgorithmBasicFlows(p_graph, algorithm_IQP)) { // ILPAlgorithmBasicFlows   ILPAlgorithmBasicFlows_LocalSolver
			printAnswer(AlgorithmResult, true);
		} else {
			printAnswer(AlgorithmResult, false);
		}
		print_time("IQPminEnd");
		cout << "--------------------------------------------" << endl << endl;
	}

	if ((algorithm_ILP_sum == algorithm) || (Algorithm_All == algorithm)) {
		AlgorithmResult->clearResult();
		cout << endl << "--------------------------------------------" << endl;
		cout << "ILPsum" << endl;
		print_time("");
		if (IPAlgorithmBasicFlows(p_graph, algorithm_ILP_sum)) { // ILPAlgorithmBasicFlows   ILPAlgorithmBasicFlows_LocalSolver
			printAnswer(AlgorithmResult, true);
		} else {
			printAnswer(AlgorithmResult, false);
		}
		print_time("ILPsumEnd");
		cout << "--------------------------------------------" << endl << endl;
	}
	//IQPsum
	if ((algorithm_IQP_sum == algorithm) || (Algorithm_All == algorithm)) {
		AlgorithmResult->clearResult();
		cout << endl << "--------------------------------------------" << endl;
		cout << "IQPsum" << endl;
		print_time("");
		if (IPAlgorithmBasicFlows(p_graph, algorithm_IQP_sum)) { // ILPAlgorithmBasicFlows   ILPAlgorithmBasicFlows_LocalSolver
			printAnswer(AlgorithmResult, true);
		} else {
			printAnswer(AlgorithmResult, false);
		}
		print_time("IQPsumEnd");
		cout << "--------------------------------------------" << endl << endl;
	}

	//COSE
	//p_graph->DesignAllEdgtoHaveSRLG(); must put the CoSE to the last place.
	if ((algorithm_COSE == algorithm) || (Algorithm_All == algorithm)) {

		AlgorithmResult->clearResult();
		cout << endl << "--------------------------------------------" << endl;
		cout << "COSE" << endl;
		print_time("");
		if (CoSEAlgorithmBasicFlows(p_graph)) {
			printAnswer(AlgorithmResult, true);
		} else {
			printAnswer(AlgorithmResult, false);
		}
		print_time("CoSEEnd");
		cout << "--------------------------------------------" << endl << endl;
	}

	//get srlg.csv file of three types from topo.csv file
	if (algorithm_getSRLGcsv == algorithm) {

		char abusolutepath[100];
		int i;
		int len = strlen(topo_file_name);
		for (i = 0; i < (len - 8); i++) {
			abusolutepath[i] = topo_file_name[i];
		}
		abusolutepath[i] = '\0';
		getSRLGcsv(p_graph, abusolutepath);
	}

	if (algorithm_setSRLGcsv == algorithm) {
		setSRLGcsv(p_graph, srlg_file_name);
	}

	if (algorithm_statisticParallelFranzAlgorithm == algorithm) {
		//firstly,i would know that the case have solution,then judge the algorithm run the split-part.
		AlgorithmResult->clearResult();
		FranzAlgorithmBasicFlows(p_graph);
		if (AlgorithmResult->IsParalle) {
			cout << "parallel:1" << endl;
		}
	}

	//verify_result(AlgorithmResult);
	free(p_graph);
	return;
}

