#include "route.h"
#include<iostream>
#include "lib/lib_record.h"

#include "head.h"

Graph graph;
Request request;

void search_route(char *topo[MAX_EDGE_NUM], int edge_num,
		char *demand[MAX_DEMAND_NUM], int demand_num, char *srlg[MAX_SRLG_NUM],
		int srlg_num) {
	load_data(&graph, &request, topo, edge_num, demand, demand_num, srlg,
			srlg_num);

	debugprint(&graph);
	if (false == judge_isStarProperty) {
		cout << "the slrg is not star structure" << endl;
		return;
	}

	request.APedgestabu = vector<bool>(graph.EdgeNum, true);
//	request.AP_srlgs = vector<bool>(graph.SRLGGroupsNum, true);
	request.BPedgestabu = vector<bool>(graph.SRLGGroupsNum, true);
	if (false == mainprocedure4algorithm(&graph, &request)) {
		cout << "it is impossible to have a SRLG-disjoint path pair" << endl;
		return;
	}
}

