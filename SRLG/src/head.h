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
#include "sys/timeb.h"

using namespace std;

#define MAX_NodeSize (2015)//may be overflow
#define WeightSort (0) //0:weight value,1:hop
//every srlg and what srlg is belonged from a edge.
class SrlgMember {
public:
	vector<int> srlgMember;
	int num_srlgmember;
};

class Edge {
public:
	int from;
	int to;
	int cost;
	int id; //topo.csv.edge flag begin from 0.
	int capacity;
	int ithslrg;
	int virtualedge;
};
class EdgeList {
public:
	vector<Edge> edgelist;
};

class Graph {
public:
	//demand.csv
	int Source, Destination;
	//set of all edge. topo.csv
	vector<Edge> edges;

	//edge number of initial graph
	//原始输入图边的个数 19
	//node number of initial graph
	//原始输入图点的个数 11
	int EdgeNum, NodeNum;

	//SRLG
	vector<SrlgMember> SRLGGroups;
	int SRLGGroupsNum;

	//标号--点号
	int index_node[MAX_NodeSize];
	//点号--标号
	int node_index[MAX_NodeSize];

	//judge this node is valued to calculation
	int isUsableNode[MAX_NodeSize];

	//输入图拓扑结构（链表形式）
	vector<EdgeList> GraphList;
	//vector<edge>GraphList[MAX_NodeSize];
	Graph() {
		Source = -1;
		Destination = -1;
		EdgeNum = 0;
		NodeNum = 0;
		SRLGGroupsNum = 0;
		memset(index_node, -1, sizeof(index_node));
		memset(node_index, -1, sizeof(node_index));
	}
	void transformgraph() {
		int pre, next;
		for (unsigned int i = 0; i < this->SRLGGroups.size(); i++) {
			for (unsigned int j = 0; j < this->SRLGGroups[i].srlgMember.size();
					j++) {
				pre = this->SRLGGroups[i].srlgMember[j];
				int virtualnode = -1;
				for (unsigned int k = j + 1;
						k < this->SRLGGroups[i].srlgMember.size(); k++) {
					next = this->SRLGGroups[i].srlgMember[k];
					if (this->edges[pre].from == this->edges[next].from) {
						if (-1 == virtualnode) {
							virtualnode = this->NodeNum;
							this->index_node[virtualnode] =
									this->NodeNum;
							this->node_index[this->NodeNum] =
									virtualnode;
							this->isUsableNode[this->NodeNum] = 1;
							this->NodeNum++;
							this->edges[next].from = virtualnode;
						}
					}
				}
				if (-1 != virtualnode) {
					Edge e;
					e.from = this->edges[pre].from;
					this->edges[pre].from = virtualnode;
					e.to = virtualnode;
					e.cost = 0;
					e.ithslrg = i;
					e.id = this->edges.size();
					e.virtualedge = i;
					e.capacity = 1;
					//GraphList[inEdgeFlag].push_back(e);
					this->EdgeNum++;
					this->edges.push_back(e);
				}
			}
		}
		for (unsigned int i = 0; i < this->SRLGGroups.size(); i++) {
			for (unsigned int j = 0; j < this->SRLGGroups[i].srlgMember.size();
					j++) {
				pre = this->SRLGGroups[i].srlgMember[j];
				int virtualnode = -1;
				for (unsigned int k = j + 1;
						k < this->SRLGGroups[i].srlgMember.size(); k++) {
					next = this->SRLGGroups[i].srlgMember[k];
					if (this->edges[pre].to == this->edges[next].to) {
						if (-1 == virtualnode) {
							virtualnode = this->NodeNum;
							this->index_node[virtualnode] =
									this->NodeNum;
							this->node_index[this->NodeNum] =
									virtualnode;
							this->isUsableNode[this->NodeNum] = 1;
							this->NodeNum++;
							this->edges[next].from = virtualnode;
						}
					}
				}
				if (-1 != virtualnode) {
					Edge e;
					e.from = virtualnode;
					e.to = this->edges[pre].to;
					this->edges[pre].to = virtualnode;
					e.cost = 0;
					e.ithslrg = i;
					e.id = this->edges.size();
					e.virtualedge = i;
					e.capacity = 1;
					//GraphList[inEdgeFlag].push_back(e);
					this->EdgeNum++;
					this->edges.push_back(e);
				}
			}
		}
	}
	void addedges(int EdgeFlag, int inEdgeFlag, int outEdgeflag,
			int EdgeWeight) {
		Edge e;
		e.id = EdgeFlag;
		e.from = inEdgeFlag;
		e.to = outEdgeflag;
		e.cost = EdgeWeight;
		e.ithslrg = -1;
//		e.id = this->edges.size();
		e.virtualedge = -1;
		e.capacity = 1;

		//GraphList[inEdgeFlag].push_back(e);
		this->EdgeNum++;
		this->edges.push_back(e);
	}
	//构造链表形式的原始图
	void StoreEdgeInformationbyLink() {
		this->GraphList = vector<EdgeList>(this->NodeNum);
		for (unsigned int i = 0; i < this->edges.size(); i++) {
			Edge e;
			e.id = this->edges.at(i).id;
			e.from = this->edges.at(i).from;
			e.to = this->edges.at(i).to;
			e.cost = this->edges.at(i).cost;
			e.ithslrg = -1;
			e.virtualedge = -1;
			e.capacity = 1;
			this->GraphList[this->edges.at(i).from].edgelist.push_back(e);
		}
		return;
	}

	//	//the initial graph's topological structure(matrix)
	//	//原始输入图的拓扑结构（矩阵形式）
	//	int InputGraphMatix[MAX_NodeSize][MAX_NodeSize];
	//	int InputMaxGraphMatix[MAX_NodeSize][MAX_NodeSize];
	//
	//	//原始输入图每条边所对应的标号
	//	int FlagofInputGraphEdgeMatrix[MAX_NodeSize][MAX_NodeSize];
	//	int FlagofInputMaxGraphEdgeMatrix[MAX_NodeSize][MAX_NodeSize];
};
class Request {
public:
	int Source, Destination;
	vector<bool> APedgestabu;
	vector<int> APexlusionedges;
	////
	vector<bool> APedgesallow;
	vector<int> APinclusionedges;

	//vector<bool>AP_srlgsallow;
	vector<int> AP_srlgs;
	vector<bool> BPedgestabu;

	vector<int> AP_node;
	vector<int> AP_edge;
	int AP_WeightSum;

	vector<int> BP_node;
	vector<int> BP_edge;
	int BP_WeightSum;

	Request() {
		Source = -1;
		Destination = -1;
		AP_WeightSum = 0;
		BP_WeightSum = 0;
	}
};

#ifndef __FranzDebug
#define debug_showinitmatrix (false)
#define debug_showsrlginfo (true)
#else
#define debug_showinitmatrix (false)
#define debug_showdijkstrainfo (false)
#endif // __FranzDebug

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

