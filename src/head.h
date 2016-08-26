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
const int WeightSort = (0); //0:weight value,1:hop
#define algorithm_franz 1
#define algorithm_IMSH 3
#define algorithm_IHKSP 4
#define algorithm_ILP 2
#define algorithm_getSRLGcsv -2
#define algorithm_all -1

#define exit_ILP_glp_set_obj_coef 50

#define LimitedTime 3
#define MAX_ITERATIONS 1000

//every srlg and what srlg is belonged from a edge.
class SrlgMember {
public:
	vector<int> srlgMember; //the corresponding id of every SRLG's members
//	int srlgGroupsNum; //member number of every SRLG
	int srlgMembersNum;
};

class Edge {
public:
	int from; //starting node
	int to; //end node
	int cost; //weight value
	int id; //topo.csv.edge flag begin from 0.
	int capacity; //capacity of network flow graph//could be removed
	int ithsrlg; //-1:represent the edge do not belong any SRLG
	int ithsrlg4virtualedge; //the edge which form the dummy vertex//could be removed
	int revid; //reverse edge's id //could be removed
	Edge(int f, int t, int c, int i, int cap, int iths, int ithsrl, int r) {
		this->from = f;
		this->to = t;
		this->cost = c;
		this->id = i;
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
	//vector<edge>GraphList[MAX_NodeSize];

	//edge number of initial graph
	//node number of initial graph
	int edgeNum, nodeNum;
	int prenodeNum;
	//SRLG
	vector<SrlgMember> srlgGroups;
	int srlgGroupsNum;

	//int index_node[MAX_NodeSize];
	vector<int> index_node;
	//int node_index[MAX_NodeSize];
	vector<int> node_index;
	//judge this node is valued to calculation
	//int isUsableNode[MAX_NodeSize];
	vector<bool> isValidNode;

	vector<int> virtualnode;

	Graph() {
		source = -1;
		destination = -1;
		edgeNum = 0;
		nodeNum = prenodeNum = 0;
		srlgGroupsNum = 0;
		//memset(index_node, -1, sizeof(index_node));
		//memset(node_index, -1, sizeof(node_index));
	}

	//transform the graph of star property into no-star property graph
	void TransformToNostarGraph(void) {

		int pre, next;
		this->prenodeNum = this->nodeNum;
		for (unsigned int i = 0; i < this->srlgGroups.size(); i++) {
			for (unsigned int j = 0; j < this->srlgGroups[i].srlgMember.size();
					j++) {
				pre = this->srlgGroups[i].srlgMember[j];
				int virtualnode = -1;
				for (unsigned int k = j + 1;
						k < this->srlgGroups[i].srlgMember.size(); k++) {
					next = this->srlgGroups[i].srlgMember[k];
					if (this->edges[pre].from == this->edges[next].from) {
						if (-1 == virtualnode) {
							virtualnode = this->nodeNum;
							this->index_node[virtualnode] = this->nodeNum;
							this->node_index[this->nodeNum] = virtualnode;
							this->isValidNode[this->nodeNum] = true;
							this->nodeNum++;
							this->virtualnode.push_back(this->edges[pre].from);

						}
						this->edges[next].from = virtualnode;
					}
				}
				if (-1 != virtualnode) {
					Edge e((this->edges[pre].from), virtualnode, 0,
							(this->edges.size()), 1, i, i, -1);
					this->edges[pre].from = virtualnode;
					//GraphList[inEdgeFlag].push_back(e);
					this->edgeNum++;
					this->edges.push_back(e);
				}
			}
		}
		for (unsigned int i = 0; i < this->srlgGroups.size(); i++) {
			for (unsigned int j = 0; j < this->srlgGroups[i].srlgMember.size();
					j++) {
				pre = this->srlgGroups[i].srlgMember[j];
				int virtualnode = -1;
				for (unsigned int k = j + 1;
						k < this->srlgGroups[i].srlgMember.size(); k++) {
					next = this->srlgGroups[i].srlgMember[k];
					if (this->edges[pre].to == this->edges[next].to) {
						if (-1 == virtualnode) {
							virtualnode = this->nodeNum;
							this->index_node[virtualnode] = this->nodeNum;
							this->node_index[this->nodeNum] = virtualnode;
							this->isValidNode[this->nodeNum] = true;
							this->nodeNum++;
						}
						this->edges[next].to = virtualnode;
					}
				}
				if (-1 != virtualnode) {
					//(inEdgeFlag,outEdgeflag,EdgeWeight,EdgeFlag,1,-1,-1,-1);
					Edge e(virtualnode, (this->edges[pre].to), 0,
							this->edges.size(), 1, i, i, -1);
					this->edges[pre].to = virtualnode;
					this->edgeNum++;
					this->edges.push_back(e);
				}
			}
		}
	}

	void AddEdges(int EdgeFlag, int inEdgeFlag, int outEdgeflag,
			int EdgeWeight) {
		Edge e(inEdgeFlag, outEdgeflag, EdgeWeight, EdgeFlag, 1, -1, -1, -1);
		this->edgeNum++;
		this->edges.push_back(e);
	}

	void ConstructGraphbyLink(void) {
		this->topo_Node_fEdgeList = vector<EdgeList>(this->nodeNum);
		for (unsigned int i = 0; i < this->edges.size(); i++) {
			this->topo_Node_fEdgeList[this->edges.at(i).from].edgeList.push_back(
					this->edges.at(i).id);
			this->topo_Node_rEdgeList[this->edges.at(i).to].edgeList.push_back(
					this->edges.at(i).id);
		}
		return;
	}

	void InsertSRLGInfoToEdgeInfo() {
		for (unsigned int i = 0; i < this->srlgGroups.size(); i++) {
			SrlgMember srlgmem = this->srlgGroups.at(i);
			for (unsigned int j = 0; j < srlgmem.srlgMember.size(); j++) {
				int ithgroupithmem = srlgmem.srlgMember.at(j);
				this->edges.at(ithgroupithmem).ithsrlg = i;
			}
		}
	}
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

	vector<int> AP_PathNode;
	vector<int> AP_PathEdge;
	int APCostSum;
	int APHopSum;

	vector<int> BP_PathNode;
	vector<int> BP_PathEdge;
	int BPCostSum;

	vector<int> edgeCapacity;// setting capacity of every edges of network flow graph

	vector<bool> STNodeCut;	//true: belong s,false: belong t;

	Request(int s, int d, int edgenum) {
		APHopSum = 0;
		APCostSum = 0;
		BPCostSum = 0;
//		this->source=s;
//		this->destination=d;

		this->APMustNotPassEdges = vector<bool>(edgenum, true);
		this->APMustPassEdges = vector<bool>(edgenum, true);

		this->BPMustNotPassEdges4AP = vector<bool>(edgenum, true);
		this->BPMustNotPassEdgesRLAP = vector<bool>(edgenum, true);

		this->edgeCapacity = vector<int>(edgenum, 1);
	}
};

class DisjointPath {
public:
	vector<int> AP;
	vector<int> BP;
	int APsum;
	int BPsum;
	DisjointPath() {
		APsum = 0;
		BPsum = 0;
	}
	DisjointPath(int aplen, int bplen) {
		this->APsum = INT_MAX;
		this->BPsum = INT_MAX;
		this->AP = vector<int>(aplen);
		this->BP = vector<int>(bplen);
	}
	void getResult(DisjointPath *dispath) {
		std::copy(dispath->AP.begin(), dispath->AP.end(), this->AP.begin());
		std::copy(dispath->BP.begin(), dispath->BP.end(), this->BP.begin());
		this->AP = dispath->AP;
		this->BP = dispath->BP;
		this->APsum = dispath->APsum;
		this->BPsum = dispath->BPsum;
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
