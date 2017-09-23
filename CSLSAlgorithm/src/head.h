#ifndef __HEAD_H__
#define __HEAD_H__

#include <iostream>
#include <algorithm>
#include <vector>
#include <queue>
#include <set>
#include<map>
#include <string.h>
#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#include<limits.h>
#include<assert.h>
#include "sys/timeb.h"

#include <fstream>
#include<sstream>

#include <sys/time.h>
#include "sys/timeb.h"

#include<pthread.h>
#include <signal.h>
#include<semaphore.h>
#include<errno.h>

using namespace std;
//#define __FranzDebug true
#ifndef __FranzDebug
#define debug_showinitmatrix (false)
#define debug_showsrlginfo (true)
#else
#define debug_showinitmatrix (false)
#define debug_showdijkstrainfo (false)
#endif // __FranzDebug

const int MAX_NodeSize = (2515); //may be overflow

#define Algorithm_All 0
#define algorithm_franz 1
#define algorithm_COSE 2
#define algorithm_IHKSP 3
#define algorithm_ILP 4
#define algorithm_IQP 5
#define algorithm_ILP_sum 6
#define algorithm_IQP_sum 7
#define algorithm_TA 8

#define algorithm_getSRLGcsv -2
#define algorithm_statisticParallelFranzAlgorithm -3
#define algorithm_setSRLGcsv -4

#define MustNodePathAlgorithmPermuteDijkstra 1
#define MustNodePathAlgorithmGUROBI 2
#define ConsolePrint false

#define exit_ILP_glp_set_obj_coef 50
#define exit_pthread_create 51//ERROR; return code from pthread_create()

#define LimitedTime 30 //algorithm's rumtime's limitaion.
#define ThreadNum 1
#define MAX_ITERATIONS 100000 //KSP's the most larger iteration's number

#define FranzMustNodeAlgorithmType 1 //1:internal dijistra 2:ILP
#define CoSEMustNodeAlgorithmType 1 //1:internal dijistra 2:ILP

const int WeightSort = (0); //0:weight value,1:hop
const bool isUndirectedGraph = true; //the graph is or not directed

//every srlg and what srlg is belonged from a edge.
class SrlgMember {
public:
	//the corresponding id of every SRLG's members
	vector<int> srlgMember;
	//members' number of every SRLG
	unsigned int srlgMembersNum;
};

class EdgeClass {
public:
	int from; //start node
	int to; //end node
	int cost; //weight value
	int index;
	int id; //topo.csv. edge flag begin from 0.//logical sequence
	int capacity; //capacity of network flow graph//could be removed
	int ithsrlg; //SRLG id. -1:represent the edge do not belong any SRLG
//	int ithsrlg4virtualedge; //SRLG's id of the edge which form the dummy vertex//could be removed
	int revedgeid; //reverse edge's id -1:represent the graph is directed//could be removed
	bool fsrlg, rsrlg;
	EdgeClass(int f, int t, int c, int index, int id, int cap, int iths,
			int r) {
		this->from = f;
		this->to = t;
		this->cost = c;
		this->index = index;
		this->id = id;
		this->capacity = cap;
		this->ithsrlg = iths;
//		this->ithsrlg4virtualedge = ithsrl;
		this->revedgeid = r;
		fsrlg = rsrlg = false;
	}
};

class EdgeList {
public:
	vector<int> edgeList; //the list of every edge's id.
};
class GraphTopo {
public:
	//all edges of graph.
	vector<EdgeClass> edges;
	EdgeClass& getithEdge(unsigned int i) {
		return this->edges.at(i);
	}
	unsigned int getEdgeSize() {
		return this->edges.size();
	}
	//demand.csv
	int source, destination;

	//set of all edge. topo.csv
	//forward edge topological structure,row:vector column:vector
	vector<EdgeList> ftopo_r_Node_c_EdgeList;
	//reverse edge topological structure,row:vector column:vector
	vector<EdgeList> rtopo_r_Node_c_EdgeList;

	//edge number of initial graph
	//node number of initial graph
	unsigned int edgeNum, nodeNum;
	//node number of initial graph which is transformed into no-star graph
	unsigned int initNodeNumber_beforeTransToNostar;

	//shared risk link groups,row:vector column: vector
	vector<SrlgMember> srlgGroups;
	unsigned int srlgGroupsNum;

	//node's index map node's id
	map<string, int> nindex_nid;
	//node's  id map node's　index
	map<int, string> nid_nindex;
	//judge this node is valued to calculation,some nodes could be removed such as independent node.
	//true :represent this node is valid
	vector<bool> isValidNode;

	vector<int> virtualNode;

	GraphTopo(int edgenum) {
		source = destination = -1;
		edgeNum = nodeNum = initNodeNumber_beforeTransToNostar = srlgGroupsNum =
				0;
		this->isValidNode = vector<bool>(edgenum * 3, false);
	}

	//transform the graph of star property into no-star property graph
	void TransformToNostarGraph(void) {
		int pre, next;
		this->initNodeNumber_beforeTransToNostar = this->nodeNum;
		//transform star edges belonging the same slrg into no star edge.
		//A-->B A-->C ====> A->D->B A->D->C
		for (unsigned int i = 0; i < this->srlgGroupsNum; i++) {
			for (unsigned int j = 0; j < this->srlgGroups[i].srlgMember.size();
					j++) {
				pre = this->srlgGroups[i].srlgMember[j];
				if (this->edges[pre].fsrlg)
					continue;
				int virtualnode = -1;
				for (unsigned int k = j + 1;
						k < this->srlgGroups[i].srlgMember.size(); k++) {
					next = this->srlgGroups[i].srlgMember[k];
					if (this->edges[pre].from == this->edges[next].from) {
						if (-1 == virtualnode) {
							virtualnode = this->nodeNum;
							stringstream ss;
							ss << virtualnode;
							string str = "v" + ss.str();
							this->nindex_nid.insert(
									pair<string, int>(str, this->nodeNum));
							this->nid_nindex.insert(
									pair<int, string>(this->nodeNum, str));

							this->isValidNode[this->nodeNum] = true;
							this->nodeNum++;
							this->virtualNode.push_back(this->edges[pre].from);

						}
						this->edges[next].from = virtualnode;
						this->edges[next].fsrlg = true;
					}
				}
				if (-1 != virtualnode) {
					EdgeClass e((this->edges[pre].from), virtualnode, 0,
							(this->edges.size()), this->edgeNum, 1, i, -1);
					this->edges[pre].from = virtualnode;
					this->edges[pre].fsrlg = true;
					//GraphList[inEdgeFlag].push_back(e);
					this->edgeNum++;
					this->edges.push_back(e);
				}
			}
		}
		for (unsigned int i = 0; i < this->srlgGroupsNum; i++) {
			for (unsigned int j = 0; j < this->srlgGroups[i].srlgMember.size();
					j++) {
				pre = this->srlgGroups[i].srlgMember[j];
				int virtualnode = -1;
				if (this->edges[pre].rsrlg)
					continue;
				for (unsigned int k = j + 1;
						k < this->srlgGroups[i].srlgMember.size(); k++) {
					next = this->srlgGroups[i].srlgMember[k];
					if (this->edges[pre].to == this->edges[next].to) {
						if (-1 == virtualnode) {
							virtualnode = this->nodeNum;
//							this->nindex_nid[virtualnode] = this->nodeNum;
//							this->nid_nindex[this->nodeNum] = virtualnode;
							stringstream ss;
							ss << virtualnode;
							string str = "v" + ss.str();
							this->nindex_nid.insert(
									pair<string, int>(str, this->nodeNum));
							this->nid_nindex.insert(
									pair<int, string>(this->nodeNum, str));

							this->isValidNode[this->nodeNum] = true;
							this->nodeNum++;
						}
						this->edges[next].to = virtualnode;
						this->edges[next].rsrlg = true;
					}
				}
				if (-1 != virtualnode) {
					//(inEdgeFlag,outEdgeflag,EdgeWeight,EdgeFlag,1,-1,-1,-1);
					EdgeClass e(virtualnode, (this->edges[pre].to), 0,
							this->edges.size(), this->edgeNum, 1, i, -1);
					this->edges[pre].to = virtualnode;
					this->edges[pre].rsrlg = true;
					this->edgeNum++;
					this->edges.push_back(e);
				}
			}
		}
	}

	//add edge into the topo structure.
	void AddEdges(int EdgeFlag, int inEdgeFlag, int outEdgeflag,
			int EdgeWeight) {
		EdgeClass fe(inEdgeFlag, outEdgeflag, EdgeWeight, EdgeFlag,
				this->edgeNum, 1, -1, -1);

		this->edgeNum++;

		this->edges.push_back(fe);

	}
	void AddEdges(EdgeClass e) {
		this->edges.push_back(e);
	}

	//construte the topo's form(ith node's edgelist)
	void ConstructGraphbyLink(void) {
		this->ftopo_r_Node_c_EdgeList = vector<EdgeList>(this->nodeNum);
		this->rtopo_r_Node_c_EdgeList = vector<EdgeList>(this->nodeNum);
		for (unsigned int i = 0; i < this->edges.size(); i++) {
			this->ftopo_r_Node_c_EdgeList[this->edges.at(i).from].edgeList.push_back(
					this->edges.at(i).id);
			this->rtopo_r_Node_c_EdgeList[this->edges.at(i).to].edgeList.push_back(
					this->edges.at(i).id);
		}
		return;
	}

	void InsertSRLGInfoToEdgeInfo() {
		for (unsigned int i = 0; i < this->srlgGroupsNum; i++) {
			SrlgMember srlgmem = this->srlgGroups.at(i);
			for (unsigned int j = 0; j < srlgmem.srlgMembersNum; j++) {
				int ithgroupithmem = srlgmem.srlgMember.at(j);
				this->edges.at(ithgroupithmem).ithsrlg = i;
			}
		}
	}

	//some edge's srlg's id is -1, I let this edge have itself srlg'id.
	void DesignAllEdgtoHaveSRLG() {
		for (unsigned i = 0; i < this->edgeNum; i++) {
			if (-1 == this->edges.at(i).ithsrlg) {
				this->edges.at(i).ithsrlg = this->srlgGroupsNum;

				SrlgMember srlgmem;
				srlgmem.srlgMember.push_back(i);
				if (isUndirectedGraph) {

					int res = this->edges.at(i).revedgeid;
					this->edges.at(res).ithsrlg = this->srlgGroupsNum;
					srlgmem.srlgMember.push_back(res);
				}
				srlgmem.srlgMembersNum = srlgmem.srlgMember.size();
				this->srlgGroups.push_back(srlgmem);
				this->srlgGroupsNum++;

			}
		}
	}

	//resolve one edge is belonging mang srlgs.
	void TransformedToEdgeBelongingOnlySRLG() {

	}
	//	//the initial graph's topological structure(matrix)
	//	int InputGraphMatix[MAX_NodeSize][MAX_NodeSize];
	//	int InputMaxGraphMatix[MAX_NodeSize][MAX_NodeSize];
	//
	//	int FlagofInputGraphEdgeMatrix[MAX_NodeSize][MAX_NodeSize];
	//	int FlagofInputMaxGraphEdgeMatrix[MAX_NodeSize][MAX_NodeSize];
};

class InclusionExclusionSet {
public:
	int veclen;
	//AP path must pass or not pass edges or srlgs.
	vector<bool> Inclusion;
	vector<bool> Exlusion;

	InclusionExclusionSet(int len) {
		this->Inclusion = vector<bool>(len, true);
		this->Exlusion = vector<bool>(len, true);
		this->veclen = len;
	}
	void clear() {
		this->Inclusion.clear();
		this->Exlusion.clear();
		this->Inclusion = vector<bool>(veclen, true);
		this->Exlusion = vector<bool>(veclen, true);
	}
};
class Request {
public:
//	int source, destination;
	vector<bool> APMustPassEdges;
	vector<bool> APMustNotPassEdges;
	vector<bool> BPMustNotPassEdges4AP;
	vector<bool> BPMustNotPassEdgesRLAP;

	vector<bool> APMustPassSRLGs;
	vector<bool> APMustNotPassSRLGs;

	vector<int> APExlusionEdges;
	vector<int> APInclusionEdges;
	vector<int> APSrlgs;	//may exist duplicate value

	vector<int> RLAP_PathEdge;

	//AP path information
	vector<int> AP_PathNode;
	vector<int> AP_PathEdge;
	int APCostSum;
	int APHopSum;

	//BP path information
	vector<int> BP_PathNode;
	vector<int> BP_PathEdge;

	int BPCostSum;

	vector<int> edgeCapacity;// setting capacity of every edges of network flow graph

	vector<bool> STNodeCut;	//true: belong s,false: belong t;
	void clear(int edgenum) {
		APHopSum = APCostSum = BPCostSum = 0;
		this->AP_PathNode.clear();
		this->AP_PathEdge.clear();
		this->BP_PathNode.clear();
		this->BP_PathEdge.clear();
		this->RLAP_PathEdge.clear();
		for (int i = 0; i < edgenum; i++) {
			this->APMustNotPassEdges[i] = true;
			this->APMustPassEdges[i] = true;
			this->BPMustNotPassEdges4AP[i] = true;
			this->BPMustNotPassEdgesRLAP[i] = true;
			this->edgeCapacity[i] = 1;
		}
	}
	//initialize Request class.
	Request(int s, int d, int edgenum) {
		APHopSum = APCostSum = BPCostSum = 0;
		this->APMustNotPassEdges = vector<bool>(edgenum, true);
		this->APMustPassEdges = vector<bool>(edgenum, true);
		this->BPMustNotPassEdges4AP = vector<bool>(edgenum, true);
		this->BPMustNotPassEdgesRLAP = vector<bool>(edgenum, true);
		this->edgeCapacity = vector<int>(edgenum, 1);
	}

	void initSRLG(int srlgnum) {
		this->APMustPassSRLGs = vector<bool>(srlgnum, true);
		this->APMustNotPassSRLGs = vector<bool>(srlgnum, true);
	}
};

class Internal_Path {
public:
	Request *p_request;
	vector<int> permute;
};

class DisjointPathPair {
public:
	//index of edge of AP path
	vector<int> APedge;
	//index of edge of BP path
	vector<int> BPedge;
	int APcostsum;
	int BPcostsum;
	unsigned int APhop;
	unsigned int BPhop;
	//true: can get the result.
	bool SolutionNotFeasible;
	//true: can get the optimal result.
	bool SolutionisOptimalFeasible;

	//true: the franz algorithm parralle
	bool IsParalle;
	//init class disjointpaths.
	DisjointPathPair() {
		this->APcostsum = INT_MAX;
		this->BPcostsum = INT_MAX;
		this->APhop = 0;
		this->BPhop = 0;
		this->SolutionNotFeasible = true;
		this->IsParalle = false;
		this->SolutionisOptimalFeasible = false;
	}
	//init class disjointpathpair,and define the length of APedge and BPedge.
	DisjointPathPair(int aplen, int bplen) {
		this->APcostsum = INT_MAX;
		this->BPcostsum = INT_MAX;
		this->APhop = 0;
		this->BPhop = 0;
		this->SolutionNotFeasible = true;
		this->IsParalle = false;
		this->SolutionisOptimalFeasible = false;
		this->APedge = vector<int>(aplen);
		this->BPedge = vector<int>(bplen);

	}
	//copy pathpair from dispath.
	void copyResult(DisjointPathPair *dispath) {
		this->APedge = vector<int>(dispath->APedge.size());
		this->BPedge = vector<int>(dispath->BPedge.size());
		std::copy(dispath->APedge.begin(), dispath->APedge.end(),
				this->APedge.begin());
		std::copy(dispath->BPedge.begin(), dispath->BPedge.end(),
				this->BPedge.begin());
		this->APcostsum = dispath->APcostsum;
		this->BPcostsum = dispath->BPcostsum;
//		this->APhop = 1 + this->APedge.size();
//		this->BPhop = 1 + this->BPedge.size();
		this->APhop = dispath->APhop;
		this->BPhop = dispath->BPhop;
		this->SolutionNotFeasible = dispath->SolutionNotFeasible;
//		this->Isornot_Paralle = dispath->Isornot_Paralle;
	}
	//reinit the class disjointpathpair
	void clearResult() {
		this->APcostsum = this->BPcostsum = INT_MAX;
		this->APhop = this->BPhop = 0;
		this->SolutionNotFeasible = true;
		this->IsParalle = false;
		this->SolutionisOptimalFeasible = false;
		this->APedge.clear();
		this->BPedge.clear();
	}

};
#endif

/*
 1=<Weight value<=100
 weight value is integer
 No loop link
 Multiple link with different weight
 |vertices|<=2000,|outer degree|<=20,sparse graph
 |Essential vertices|<=100
 Directed loop graph
 some tips modified the algorithm:
 0.reverse the whole graph
 1.water drop method
 2.every branch have the limited time
 3. if((-1!=vs_TSPDFS[j])&&(-1!=(dp.vsnode[j])))
 4.find 3 path method
 5.find the second path,add some value to the edge links the first path's enode
 6.add linkpaths between every enodes.
 7.optimate some array's long assignment problem
 8.first calculate the more enodesize's path
 */
