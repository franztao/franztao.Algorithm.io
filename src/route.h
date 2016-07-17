#ifndef __ROUTE_H__
#define __ROUTE_H__

#include "lib/lib_io.h"
#include "head.h"
//extern void StoreEdgeInformationbyLink(int EdgeFlag, int inEdgeFlag,int outEdgeflag, int EdgeWeight);

//extern void StoreEdgeInformationbyMatrix(Graph *graph, int EdgeFlag,int inEdgeFlag, int outEdgeflag, int EdgeWeight);

extern void LoadData(Graph *graph, char *topo[MAX_EDGE_NUM],
		int edgenum, char *demand[MAX_DEMAND_NUM], int demandnum,
		char *srlg[MAX_SRLG_NUM], int srlgnum);


extern void DebugPrint(Graph *graph);
extern void search_route(char *graph[MAX_EDGE_NUM], int edge_num,
		char *condition[MAX_DEMAND_NUM], int demand_num,
		char *srlg[MAX_SRLG_NUM], int srlg_num);
extern bool judge_isStarProperty();
extern bool AlgorithmBasicFlows(Graph *graph);
extern void findAP(Graph *graph, Request *request);
extern void findBP(Graph *graph, Request *request);
extern void eliminate_invalidnodeandedge();

extern void BuildNetworkFlowGraph(Graph *p_graph, Request *p_request);
extern void GetConflictingSRLGLinkSet(Graph *p_graph, Request *p_request);

#endif
