#include "route.h"
#include<iostream>
#include "lib/lib_record.h"
#include "lib/lib_time.h"
#include "head.h"

Graph *p_graph;
DisjointPath *AlgorithmResult;
pthread_mutex_t mutex_result;
pthread_mutex_t mutex_thread;
void print_answer(DisjointPath *FranzAlgorithmResult) {

	cout << "optimal APcost:" << FranzAlgorithmResult->APsum << "  " << endl;

	cout << "AP edge: ";
	unsigned int i = 0;
	for (i = 0; i < FranzAlgorithmResult->AP.size(); i++) {

		record_result(WORK_PATH, FranzAlgorithmResult->AP.at(i));
		cout << FranzAlgorithmResult->AP.at(i) << "   ";
	}
	cout << endl;

	cout << "AP node: ";
	for (i = 0; i < FranzAlgorithmResult->AP.size(); i++) {

		cout
				<< p_graph->node_index[p_graph->edges.at(
						FranzAlgorithmResult->AP.at(i)).from] << "   ";

	}
	cout
			<< p_graph->node_index[p_graph->edges.at(
					FranzAlgorithmResult->AP.at(i - 1)).to] << "   ";
	cout << endl;

	cout << "BP edge: ";
	for (i = 0; i < FranzAlgorithmResult->BP.size(); i++) {
		record_result(BACK_PATH, FranzAlgorithmResult->BP.at(i));
		cout << FranzAlgorithmResult->BP.at(i) << "   ";
	}
	cout << endl;
	cout << "BP node: ";
	for (i = 0; i < FranzAlgorithmResult->BP.size(); i++) {
		cout
				<< p_graph->node_index[p_graph->edges.at(
						FranzAlgorithmResult->BP.at(i)).from] << "   ";
	}
	cout
			<< p_graph->node_index[p_graph->edges.at(
					FranzAlgorithmResult->BP.at(i - 1)).to] << "   ";
	cout << endl;

	cout << "|||||||Be Successful to find disjoint path||||||||||" << endl;

}
void verify_result(DisjointPath *FranzAlgorithmResult) {

}
void search_route(char *topo[MAX_EDGE_NUM], int edge_num,
		char *demand[MAX_DEMAND_NUM], int demand_num, char *srlg[MAX_SRLG_NUM],
		int srlg_num, int algorithm, char *str) {

	AlgorithmResult = new DisjointPath();
	p_graph = new Graph();
	LoadData(p_graph, topo, edge_num, demand, demand_num, srlg, srlg_num);

	DebugPrint(p_graph);

	if ((algorithm_franz == algorithm) || (algorithm_all == algorithm)) {
		print_time("FranzAlgorithmBegin\n");
		if (FranzAlgorithmBasicFlows(p_graph)) {
			print_answer(AlgorithmResult);

		} else {
			cout << "it is impossible to have a SRLG-disjoint path pair"
					<< endl;

		}

		print_time("FranzAlgorithmEnd");
		cout << endl
				<< "-------------------------------------------------------------"
				<< endl;
	}

	if ((algorithm_ILP == algorithm) || (algorithm_all == algorithm)) {
		print_time("ILPBegin\n");
		if (ILPAlgorithmBasicFlows(p_graph)) { //ILPAlgorithmBasicFlows ILPAlgorithm_glpk
			print_answer(AlgorithmResult);
		} else {
			cout << "it is impossible to have a SRLG-disjoint path pair"
					<< endl;

		}
		print_time("ILPEnd");
		cout << endl
				<< "-------------------------------------------------------------"
				<< endl;

	}

	if ((algorithm_IHKSP == algorithm) || (algorithm_all == algorithm)) {
		print_time("IHKSPbegin\n");
		if (KSPAlgorithmBasicFlows(p_graph)) { //ILPAlgorithmBasicFlows ILPAlgorithm_glpk
			print_answer(AlgorithmResult);
		} else {
			cout << "it is impossible to have a SRLG-disjoint path pair"
					<< endl;

		}
		print_time("IHKSPEnd");
		cout << endl
				<< "-------------------------------------------------------------"
				<< endl;

	}

	if (algorithm_getSRLGcsv == algorithm) {
		char abusolutepath[100];
		int i;
		int len = strlen(str);
		for (i = 0; i < (len - 8); i++) {
			abusolutepath[i] = str[i];
		}
		abusolutepath[i] = '\0';
		getSRLGcsv(p_graph, abusolutepath);
	}

	verify_result(AlgorithmResult);
	free(p_graph);
	return;
}

