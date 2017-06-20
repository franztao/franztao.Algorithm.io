#ifndef  _CDN_H
#define  _CDN_H

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <ctype.h>
#include <algorithm>
#include <vector>
#include <queue>
#include <stack>
#include <math.h>
#include <deque>
#include <map>
#include <set>
#include <iostream>

#include "lib/lib_io.h"
using namespace std;

#define  INF              (200000000)
#define  CLENGTH          (800)
#define  Tabu_PC               (0.8)
#define  Tabu_PM               (0.01)
#define  MAX_GENERATION   (300)
#define  TabuTableSize  (20)
#define  MAX_N            (1000)
#define  MAX_C            (500)
#define  MAX_NODE_franz        (MAX_N+MAX_C + 2)
#define  MAX_Dr           (20)
#define  MAX_Edge_Num     (MAX_N*MAX_Dr/2)
#define  MAX_BANDWT       (100)
#define  MAX_COST         (100)
#define  MAX_SERVER_COST  (5000)
#define  MAX_C_DEMAND     (5000)
#define  UNREACH          (20000000)

class EdgeClass;
class AugmentEdgeClass;
class GraphTopo;
class Unique;
class TabuSearchClass;
extern GraphTopo G;
extern TabuSearchClass B;
extern vector<vector<int> > vectorarray;
typedef long long Int64;

void add_edge(int u, int v, int cap, int cost);
void initGraphTopoStructure();

extern string tempstring, bestpathstring;
extern void DFS(int u);
extern void ParameterReset();
extern int CostFlow_ZKW();
extern void InplementMCMF();

extern int numberM;
extern int num4K;
extern int populationSize;
extern int lastBestServer[MAX_N];
extern char starray[MAX_Edge_Num];
extern int allRoadNumber;

extern int currentServer[MAX_N];
extern void Sort4Consumer();
extern void Sort4NetworkNode();
extern int generationSize;
extern int bestIndex;
extern int worstIndex;
extern vector<int> selectionNode;
extern int middleTempNode[MAX_N];

extern void init();

extern int Flow[MAX_Edge_Num * 4];
extern int Road[MAX_NODE_franz];
extern int k;

extern int node[MAX_C];
extern int head[MAX_NODE_franz];
extern int defineFlow;

extern void InitEdge();
extern vector<int> pathVector;
extern int isflag[MAX_NODE_franz];

extern void constructHeap(int *heap, int num);
extern void adjustHeap(int *heap, int k, int num);
extern int deleteHeap(int *heap, int n);

class GraphTopo {
public:
	GraphTopo() {
	}
	;
	void initial(char *topo[MAX_EDGE_NUM], int line_num);
	int from_Index[MAX_Edge_Num];
	int v[MAX_Edge_Num];
	int bt[MAX_Edge_Num];
	int cs[MAX_Edge_Num];
	int s[MAX_C];
	int sv[MAX_C];
	int C_Demand[MAX_C];
	int demand[MAX_C];
	int Netnode_degree[MAX_N];
	int Nnum;
	int Cnum;
	int node;
	int EN;
	int candEdgeNum;
	int roadmincost, minflow, bestcost;
	int chosNum;
	int S;
	int T;
	int Line_num;
	int Server_Cost;
	int lagn, lagc;

};

class EdgeClass {
public:
	int u, v, cost, flow, candflow, next, re;
};
extern EdgeClass edge[MAX_Edge_Num * 4];

class TabuSearchClass {
public:
	TabuSearchClass() {
	}
	;
	int initNode[MAX_N];
	int bestNode[MAX_N];
	int bestEvauValue;
	int currenttNode[MAX_N];

	int neighbourNum;
	int tabuTableLength;
	int Conlength;
	int tabuTable[TabuTableSize][MAX_N];
	int bestTabuSearchValue;
	int curtEvaluationValue;
	int tempNode[MAX_N];
	int tempEvaluationValue;
	int number4Gene;
	int bestNodeNumber;

	int Evaluat(int *p);
	void getNeighbour(int *p, int *q);
	int isTabuTable(int *p);
	void TabuTableFlush(int *p);
	void BeginSolve();
	void Initset(int n, int c, int m);
	void SetTabuTableLength();
	void initGene();
	void copyGene(int *p, int *q);

};

class AugmentEdgeClass {
public:
	int from, v, flow, next;
};
extern AugmentEdgeClass initialEdge[MAX_Edge_Num];

#endif
