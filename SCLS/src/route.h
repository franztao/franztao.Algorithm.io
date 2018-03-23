#ifndef __ROUTE_H__
#define __ROUTE_H__

#include "lib/lib_io.h"

#include "head.h"

//extern void StoreEdgeInformationbyLink(int EdgeFlag, int inEdgeFlag,int outEdgeflag, int EdgeWeight);

//extern void StoreEdgeInformationbyMatrix(Graph *graph, int EdgeFlag,int inEdgeFlag, int outEdgeflag, int EdgeWeight);
extern bool reverseDFS_mustedgeoutnode(GraphTopo *p_graph, Request *p_request,vector<int> &permute);


extern bool LoadGraphData(GraphTopo *graph, char *topo[MAX_EDGE_NUM], int edgenum,
		char *demand[MAX_DEMAND_NUM], int demandnum, char *srlg[MAX_SRLG_NUM],
		int srlgnum);
extern void DebugPrint(GraphTopo *graph);
extern void search_route(char *graph[MAX_EDGE_NUM], int edge_num,
		char *condition[MAX_DEMAND_NUM], int demand_num,
		char *srlg[MAX_SRLG_NUM], int srlg_num, int algorithm, char *str,char *srlg_file_name);
extern bool judge_isStarProperty();
extern void eliminate_invalidnodeandedge();
extern void getSRLGcsv(GraphTopo* p_graph, char * str);
extern void BuildNetworkFlowGraph(GraphTopo *p_graph, Request *p_request);
extern void GetConflictingSRLGLinkSet(GraphTopo *p_graph, Request *p_request);

//extern void setSRLGcsv(GraphTopo *p_graph, char *str);


//franz
extern bool findAP_ILP_gurobi(GraphTopo *p_graph, Request *p_request);
extern bool findAP_dijastra(GraphTopo *p_graph, Request *p_request);
extern bool findMustNodePath(GraphTopo *p_graph, Request *p_request);
extern bool FranzAlgorithmBasicFlows(GraphTopo *graph);

extern bool findAP_interval_dijastra(GraphTopo *p_graph, Request *p_request,vector<int> &permute);
extern bool findBP(GraphTopo *p_graph, Request *p_request);


//LP
extern bool ILPAlgorithm_gurobi(GraphTopo *graph,int type);
extern bool ILPAlgorithm_glpk(GraphTopo *graph);
extern bool ILPAlgorithmBasicFlows_LocalSolver(GraphTopo *graph);
extern bool IPAlgorithmBasicFlows(GraphTopo *p_graph,int type);
extern bool IQPAlgorithm_gurobi(GraphTopo *p_graph,int type);


//KSP
extern bool IHKSPAlgorithm(GraphTopo const *p_graph);
extern bool KSPAlgorithmBasicFlows(GraphTopo *p_graph);

//TA
extern bool TAAlgorithm(GraphTopo *p_graph);
extern bool TAAlgorithmBasicFlows(GraphTopo *p_graph);

//CoSE
extern bool CoSE_findAP_ILP_gurobi(GraphTopo *p_graph, Request *p_request);
extern bool CoSE_findAP_dijastra(GraphTopo *p_graph, Request *p_request);
extern bool CoSEAlgorithmBasicFlows(GraphTopo *p_graph);
extern bool CoSEfindBP(GraphTopo *p_graph, Request *p_request);
#endif
