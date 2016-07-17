#include "route.h"
#include<iostream>
#include "lib/lib_record.h"

#include "head.h"

Graph *p_graph;
vector<DisjointPath>result;
sem_t mutex_result;

void search_route(char *topo[MAX_EDGE_NUM], int edge_num,
		char *demand[MAX_DEMAND_NUM], int demand_num, char *srlg[MAX_SRLG_NUM],
		int srlg_num) {

	p_graph=new Graph();
	LoadData(p_graph, topo, edge_num, demand, demand_num, srlg,
			srlg_num);

	DebugPrint(p_graph);

	if (false == judge_isStarProperty()) {
		cout << "the slrg is not star structure" << endl;
		return;
	}

	if (false == AlgorithmBasicFlows(p_graph)) {
		cout << "it is impossible to have a SRLG-disjoint path pair" << endl;
		return;
	}

	free(p_graph);
}

