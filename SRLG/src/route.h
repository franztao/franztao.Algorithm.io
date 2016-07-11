#ifndef __ROUTE_H__
#define __ROUTE_H__

#include "lib/lib_io.h"
#include "head.h"
//extern void StoreEdgeInformationbyLink(int EdgeFlag, int inEdgeFlag,int outEdgeflag, int EdgeWeight);

//extern void StoreEdgeInformationbyMatrix(Graph *graph, int EdgeFlag,int inEdgeFlag, int outEdgeflag, int EdgeWeight);

extern void load_data(Graph *graph, Request *request, char *topo[MAX_EDGE_NUM],
		int edgenum, char *demand[MAX_DEMAND_NUM], int demandnum,
		char *srlg[MAX_SRLG_NUM], int srlgnum);
extern void debugprint(Graph *graph);
extern void search_route(char *graph[MAX_EDGE_NUM], int edge_num,
		char *condition[MAX_DEMAND_NUM], int demand_num,
		char *srlg[MAX_SRLG_NUM], int srlg_num);
extern bool judge_isStarProperty();
extern bool mainprocedure4algorithm(Graph *graph, Request *request);
extern void findAP(Graph *graph, Request *request);
extern void findBP(Graph *graph, Request *request);
extern void eliminate_invalidnodeandedge();
extern void insert_srlginfotoedgeinfo(Graph *graph);
#endif
