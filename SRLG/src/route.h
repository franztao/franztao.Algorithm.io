#ifndef __ROUTE_H__
#define __ROUTE_H__

#include "lib/lib_io.h"
#include "head.h"
//extern void StoreEdgeInformationbyLink(int EdgeFlag, int inEdgeFlag,int outEdgeflag, int EdgeWeight);

//extern void StoreEdgeInformationbyMatrix(Graph *graph, int EdgeFlag,int inEdgeFlag, int outEdgeflag, int EdgeWeight);

extern bool LoadData(Graph *graph, char *topo[MAX_EDGE_NUM], int edgenum,
		char *demand[MAX_DEMAND_NUM], int demandnum, char *srlg[MAX_SRLG_NUM],
		int srlgnum);

extern void DebugPrint(Graph *graph);
extern void search_route(char *graph[MAX_EDGE_NUM], int edge_num,
		char *condition[MAX_DEMAND_NUM], int demand_num,
		char *srlg[MAX_SRLG_NUM], int srlg_num, int algorithm, char *str);
extern bool judge_isStarProperty();

extern bool FranzAlgorithmBasicFlows(Graph *graph);
extern bool ILPAlgorithmBasicFlows_gurobi(Graph *graph);
extern bool ILPAlgorithm_glpk(Graph *graph);
extern bool ILPAlgorithmBasicFlows_LocalSolver(Graph *graph);

extern void eliminate_invalidnodeandedge();

extern void BuildNetworkFlowGraph(Graph *p_graph, Request *p_request);
extern void GetConflictingSRLGLinkSet(Graph *p_graph, Request *p_request);

extern bool findMustNodePath(Graph *p_graph, Request *p_request);

extern void getSRLGcsv(Graph* p_graph, char * str);

extern bool ILPAlgorithmBasicFlows(Graph *p_graph);

extern bool IHKSPAlgorithm(Graph *p_graph);
extern bool KSPAlgorithmBasicFlows(Graph *p_graph);

#endif
