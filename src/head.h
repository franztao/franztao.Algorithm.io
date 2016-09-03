#ifndef __HEAD_H__
#define __HEAD_H__

#include <iostream>
#include <algorithm>
#include <vector>
#include <queue>
#include <set>
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
#include<semaphore.h>

using namespace std;

#ifndef __FranzDebug
#define debug_showinitmatrix (false)
#define debug_showsrlginfo (true)
#else
#define debug_showinitmatrix (false)
#define debug_showdijkstrainfo (false)
#endif // __FranzDebug

const int MAX_NodeSize = (2015); //may be overflow
#define algorithm_franz 1
#define algorithm_IQP 5
#define algorithm_IMSH 4
#define algorithm_IHKSP 3
#define algorithm_ILP 2
#define algorithm_getSRLGcsv -2
#define algorithm_all 0

#define ConsolePrint false

#define exit_ILP_glp_set_obj_coef 50
#define exit_franz_pthread_create 51//ERROR; return code from pthread_create()

#define LimitedTime 30
#define MAX_ITERATIONS 1000
const int WeightSort = (0); //0:weight value,1:hop
const bool isUndirectedGraph = false; //

//every srlg and what srlg is belonged from a edge.
class SrlgMember {
public:
	//the corresponding index of every SRLG's members
	vector<int> srlgMember;
	//members' number of every SRLG
	unsigned int srlgMembersNum;
};

class Edge {
public:
	int from; //starting node
	int to; //end node
	int cost; //weight value
	int index;
	int id; //topo.csv.edge flag begin from 0.
	int capacity; //capacity of network flow graph//could be removed
	int ithsrlg; //-1:represent the edge do not belong any SRLG
	int ithsrlg4virtualedge; //the edge which form the dummy vertex//could be removed
	int revid; //reverse edge's id -1:represent the graph is directed//could be removed
	Edge(int f, int t, int c, int index, int id, int cap, int iths, int ithsrl,
			int r) {
		this->from = f;
		this->to = t;
		this->cost = c;
		this->index = index;
		this->id = id;
		this->capacity = cap;
		this->ithsrlg = iths;
		this->ithsrlg4virtualedge = ithsrl;
		this->revid = r;
	}
};

class EdgeList {
public:
	vector<int> edgeList; //the list of every edge's id.
};
class Graph {
public:
	//demand.csv
	int source, destination;

	//set of all edge. topo.csv
	vector<Edge> edges;
	vector<EdgeList> topo_Node_fEdgeList;
	vector<EdgeList> topo_Node_rEdgeList;

	//edge number of initial graph
	//node number of initial graph
	unsigned int edgeNum, nodeNum;
	//**
	unsigned int nodeNumbeforeTransToNostar;

	//shared risk link groups
	vector<SrlgMember> srlgGroups;
	unsigned int srlgGroupsNum;

	//int index_node[MAX_NodeSize];
	vector<int> nindex_nid;
	//int node_index[MAX_NodeSize];
	vector<int> nid_nindex;

//	//index_id
//	vector<int> eindex_eid;
//	vector<int> eid_eindex;

	//judge this node is valued to calculation
	vector<bool> isValidNode;

	vector<int> virtualnode;

	Graph(int edgenum) {
		source = -1;
		destination = -1;
		edgeNum = 0;
		nodeNum = nodeNumbeforeTransToNostar = 0;
		srlgGroupsNum = 0;

		//-1 represent it is not used.
		//maybe have bug.
		this->nid_nindex = vector<int>(edgenum * 2, -1);
		this->nindex_nid = vector<int>(edgenum * 2, -1);
		//false represent the ith index's node dosen't exist
		this->isValidNode = vector<bool>(edgenum * 2, false);

//		p_graph->eindex_eid = vector<int>(edgenum , -1);
//		p_graph->eid_eindex = vector<int>(edgenum , -1);
	}

	//transform the graph of star property into no-star property graph
	void TransformToNostarGraph(void) {
		int pre, next;
		unsigned int len;
		this->nodeNumbeforeTransToNostar = this->nodeNum;
		//transform star edges belonging the same slrg into no star edge.
		//A-->B A-->C ====> A->D->B A->C->B
		for (unsigned int i = 0; i < this->srlgGroupsNum; i++) {
			len = this->srlgGroups[i].srlgMember.size();
			for (unsigned int j = 0; j < len; j++) {
				pre = this->srlgGroups[i].srlgMember[j];
				int virtualnode = -1;
				unsigned int len1 = this->srlgGroups[i].srlgMember.size();
				for (unsigned int k = j + 1; k < len1; k++) {
					next = this->srlgGroups[i].srlgMember[k];
					if (this->edges[pre].from == this->edges[next].from) {
						if (-1 == virtualnode) {
							virtualnode = this->nodeNum;
							this->nindex_nid[virtualnode] = this->nodeNum;
							this->nid_nindex[this->nodeNum] = virtualnode;
							this->isValidNode[this->nodeNum] = true;
							this->nodeNum++;
							this->virtualnode.push_back(this->edges[pre].from);

						}
						this->edges[next].from = virtualnode;
					}
				}
				if (-1 != virtualnode) {
					Edge e((this->edges[pre].from), virtualnode, 0,
							(this->edges.size()), this->edgeNum, 1, i, i, -1);
					this->edges[pre].from = virtualnode;
					//GraphList[inEdgeFlag].push_back(e);
					this->edgeNum++;
					this->edges.push_back(e);
				}
			}
		}
		for (unsigned int i = 0; i < this->srlgGroupsNum; i++) {
			len = this->srlgGroups[i].srlgMember.size();
			for (unsigned int j = 0; j < len; j++) {
				pre = this->srlgGroups[i].srlgMember[j];
				int virtualnode = -1;
				unsigned int len1 = this->srlgGroups[i].srlgMember.size();
				for (unsigned int k = j + 1; k < len1; k++) {
					next = this->srlgGroups[i].srlgMember[k];
					if (this->edges[pre].to == this->edges[next].to) {
						if (-1 == virtualnode) {
							virtualnode = this->nodeNum;
							this->nindex_nid[virtualnode] = this->nodeNum;
							this->nid_nindex[this->nodeNum] = virtualnode;
							this->isValidNode[this->nodeNum] = true;
							this->nodeNum++;
						}
						this->edges[next].to = virtualnode;
					}
				}
				if (-1 != virtualnode) {
					//(inEdgeFlag,outEdgeflag,EdgeWeight,EdgeFlag,1,-1,-1,-1);
					Edge e(virtualnode, (this->edges[pre].to), 0,
							this->edges.size(), this->edgeNum, 1, i, i, -1);
					this->edges[pre].to = virtualnode;
					this->edgeNum++;
					this->edges.push_back(e);
				}
			}
		}
	}

	//add edge into the topo structure.
	void AddEdges(int EdgeFlag, int inEdgeFlag, int outEdgeflag,
			int EdgeWeight) {
		Edge fe(inEdgeFlag, outEdgeflag, EdgeWeight, EdgeFlag, this->edgeNum, 1,
				-1, -1, -1);

		this->edgeNum++;
		if (isUndirectedGraph) {
			fe.revid = this->edgeNum;
		}
		this->edges.push_back(fe);
		if (isUndirectedGraph) {
			Edge re(outEdgeflag, inEdgeFlag, EdgeWeight, EdgeFlag,
					this->edgeNum, 1, -1, -1, -1);
			re.revid = this->edgeNum - 1;
			this->edgeNum++;
			this->edges.push_back(re);
		}
	}

	//construte the topo's form(ith node's edgelist)
	void ConstructGraphbyLink(void) {
		this->topo_Node_fEdgeList = vector<EdgeList>(this->nodeNum);
		this->topo_Node_rEdgeList = vector<EdgeList>(this->nodeNum);
		for (unsigned int i = 0; i < this->edges.size(); i++) {
			this->topo_Node_fEdgeList[this->edges.at(i).from].edgeList.push_back(
					this->edges.at(i).id);
			this->topo_Node_rEdgeList[this->edges.at(i).to].edgeList.push_back(
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
	//AP path must pass or not pass edge.
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
		APHopSum = 0;
		APCostSum = 0;
		BPCostSum = 0;
		this->AP_PathNode.clear();
		this->AP_PathEdge.clear();
		for (int i = 0; i < edgenum; i++) {
			this->APMustNotPassEdges[i] = true;
			this->APMustPassEdges[i] = true;
			this->BPMustNotPassEdges4AP[i] = true;
			this->BPMustNotPassEdgesRLAP[i] = true;
			this->edgeCapacity[i] = 1;
		}

	}
	Request(int s, int d, int edgenum) {
		APHopSum = 0;
		APCostSum = 0;
		BPCostSum = 0;

		this->APMustNotPassEdges = vector<bool>(edgenum, true);
		this->APMustPassEdges = vector<bool>(edgenum, true);

		this->BPMustNotPassEdges4AP = vector<bool>(edgenum, true);
		this->BPMustNotPassEdgesRLAP = vector<bool>(edgenum, true);

		this->edgeCapacity = vector<int>(edgenum, 1);
	}

};

class DisjointPaths {
public:
	vector<int> APedge;
	vector<int> BPedge;
	int APcostsum;
	int BPcostsum;
	unsigned int APhop;
	unsigned int BPhop;
	bool solutionNotfeasible;
	DisjointPaths() {
		this->APcostsum = INT_MAX;
		this->BPcostsum = INT_MAX;
		this->solutionNotfeasible = true;
	}
	DisjointPaths(int aplen, int bplen) {
		this->APcostsum = INT_MAX;
		this->BPcostsum = INT_MAX;
		this->APedge = vector<int>(aplen);
		this->BPedge = vector<int>(bplen);
		this->solutionNotfeasible = true;
	}
	void getResult(DisjointPaths *dispath) {
		this->APedge = vector<int>(dispath->APedge.size());
		this->BPedge = vector<int>(dispath->BPedge.size());
		std::copy(dispath->APedge.begin(), dispath->APedge.end(),
				this->APedge.begin());
		std::copy(dispath->BPedge.begin(), dispath->BPedge.end(),
				this->BPedge.begin());
		this->APcostsum = dispath->APcostsum;
		this->BPcostsum = dispath->BPcostsum;
		this->APhop = 1 + this->APedge.size();
		this->BPhop = 1 + this->BPedge.size();
	}
	void clear() {
		this->APcostsum = INT_MAX;
		this->BPcostsum = INT_MAX;
		this->solutionNotfeasible = true;
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
