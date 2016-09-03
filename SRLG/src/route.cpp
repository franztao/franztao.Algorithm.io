#include "route.h"
#include<iostream>
#include "lib/lib_record.h"
#include "lib/lib_time.h"
#include "head.h"

Graph *p_graph;
DisjointPaths *AlgorithmResult;
pthread_mutex_t mutex_result;
pthread_mutex_t mutex_thread;
void print_answer(DisjointPaths *FranzAlgorithmResult) {

	cout << "optimal APcost:" << FranzAlgorithmResult->APcostsum << "  " << endl;

	cout << "AP edge: ";
	unsigned int i = 0;
	for (i = 0; i < FranzAlgorithmResult->APnode.size(); i++) {

		record_result(WORK_PATH, FranzAlgorithmResult->APnode.at(i));
		cout << FranzAlgorithmResult->APnode.at(i) << "   ";
	}
	cout << endl;

	cout << "AP node: ";
	for (i = 0; i < FranzAlgorithmResult->APnode.size(); i++) {

		cout
				<< p_graph->nid_nindex[p_graph->edges.at(
						FranzAlgorithmResult->APnode.at(i)).from] << "   ";

	}
	cout
			<< p_graph->nid_nindex[p_graph->edges.at(
					FranzAlgorithmResult->APnode.at(i - 1)).to] << "   ";
	cout << endl;

	cout << "BP edge: ";
	for (i = 0; i < FranzAlgorithmResult->BPnode.size(); i++) {
		record_result(BACK_PATH, FranzAlgorithmResult->BPnode.at(i));
		cout << FranzAlgorithmResult->BPnode.at(i) << "   ";
	}
	cout << endl;
	cout << "BP node: ";
	for (i = 0; i < FranzAlgorithmResult->BPnode.size(); i++) {
		cout
				<< p_graph->nid_nindex[p_graph->edges.at(
						FranzAlgorithmResult->BPnode.at(i)).from] << "   ";
	}
	cout
			<< p_graph->nid_nindex[p_graph->edges.at(
					FranzAlgorithmResult->BPnode.at(i - 1)).to] << "   ";
	cout << endl;

	cout << "|||||||Be Successful to find disjoint path||||||||||" << endl;

}
//verify whether the answer is really right.
void verify_result(DisjointPaths *FranzAlgorithmResult) {

}
void search_route(char *topo[MAX_EDGE_NUM], int edge_num,
		char *demand[MAX_DEMAND_NUM], int demand_num, char *srlg[MAX_SRLG_NUM],
		int srlg_num, int algorithm, char *str) {


	AlgorithmResult = new DisjointPaths();
	p_graph = new Graph(edge_num);


	if(!LoadData(p_graph, topo, edge_num, demand, demand_num, srlg, srlg_num)){
		printf("Error:LoadData\n");
		return ;
	}

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

