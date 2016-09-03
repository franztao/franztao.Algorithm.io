#include "head.h"
extern int demand_num;

void DebugPrint(Graph *p_graph) {
	for (int i = 0; i < demand_num; i++) {
		cout << "PATH(" << i <<") ";
		cout << "source node:" << (*p_graph).nid_nindex[(*p_graph).source];
		cout << "  destination node:"
				<< (*p_graph).nid_nindex[(*p_graph).destination] << endl;
	}
	cout << "node size:" << (*p_graph).nodeNum <<"   ";
	cout << "edge size:" << (*p_graph).edgeNum << endl;

//	cout << "node_index :";
//	for (int i = 0; i < (*p_graph).nodeNum; i++) {
//		cout << (*p_graph).nid_nindex[i] << " ";
//	}
//	cout << endl;
//	cout << "index_node :";
//	for (int i = 0; i < (*p_graph).nodeNum; i++) {
//		cout << (*p_graph).nindex_nid[i] << " ";
//	}
//	cout << endl;

//
//	for (unsigned int i = 0; i < p_graph->edges.size(); i++) {
//		cout << p_graph->edges[i].id << " :"
//				<< p_graph->node_index[p_graph->edges[i].from] << " "
//				<< p_graph->node_index[p_graph->edges[i].to] << " ";
//		cout << p_graph->edges[i].ithsrlg << " " << p_graph->edges[i].cost
//				<< endl;
//	}
//
//	SrlgMember srlgmem;
//	if (debug_showsrlginfo) {
//		for (int i = 0; i < srlg_num; i++) {
//			srlgmem = (*p_graph).srlgGroups.at(i);
//			cout << "SRLG" << i << ":";
//			for (int j = 0; j < srlgmem.srlgMembersNum; j++) {
//				cout << srlgmem.srlgMember.at(j) << "  ";
//			}
//			cout << endl;
//		}
//	}
}
