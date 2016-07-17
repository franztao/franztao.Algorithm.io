#include "head.h"
extern int demand_num;
extern int srlg_num;

void DebugPrint(Graph *p_graph) {
	for (int i = 0; i < demand_num; i++) {
		cout << "disjointpath  " << i << endl;
		cout << "source node:" << (*p_graph).node_index[(*p_graph).source];
		cout << "  destination node:"
				<< (*p_graph).node_index[(*p_graph).destination] << endl;

		//        cout<<"essential node ("<<demandEnodeSize[i]<<"):";
		//        for(int j=0; j<demandEnodeSize[i]; j++)
		//        {
		//            cout<<node_index[demandEnode[i][j]]<<" ";
		//        }
	}
	cout << "node size:" << (*p_graph).nodeNum << endl;
	cout << "edge size:" << (*p_graph).edgeNum << endl;

	cout << "node_index :";
	for (int i = 0; i < (*p_graph).nodeNum; i++) {
		cout << (*p_graph).node_index[i] << " ";
	}
	cout << endl;
	cout << "index_node :";
	for (int i = 0; i < (*p_graph).nodeNum; i++) {
		cout << (*p_graph).index_node[i] << " ";
	}
	cout << endl;

	if (debug_showinitmatrix) {
		cout << "Initmatrix:" << endl;
		for (int i = 0; i < (*p_graph).nodeNum; i++) {
			cout << (*p_graph).node_index[i] << " ";
		}
		cout << endl;
		cout << endl;

		for (int i = 0; i < (*p_graph).nodeNum; i++) {
			for (int j = 0; j < (*p_graph).nodeNum; j++) {
				//cout << (*p_graph).InputGraphMatix[i][j] << " ";
			}
			cout << endl;
		}
	}
	for (unsigned int i = 0; i < p_graph->edges.size(); i++) {
		cout << p_graph->edges[i].id << " :"
				<< p_graph->node_index[p_graph->edges[i].from] << " "
				<< p_graph->node_index[p_graph->edges[i].to] << " ";
		cout << p_graph->edges[i].ithsrlg << " " << p_graph->edges[i].cost
				<< endl;
	}

	SrlgMember srlgmem;
	if (debug_showsrlginfo) {
		for (int i = 0; i < srlg_num; i++) {
			srlgmem = (*p_graph).srlgGroups.at(i);
			cout << "SRLG" << i << ":";
			for (int j = 0; j < srlgmem.srlgmemberNum; j++) {
				cout << srlgmem.srlgMember.at(j) << "  ";
			}
			cout << endl;
		}
	}
}
