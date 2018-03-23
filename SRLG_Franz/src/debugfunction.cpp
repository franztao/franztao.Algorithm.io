#include "head.h"
extern int demand_num;

//print the related information of the graph
void DebugPrint(GraphTopo *p_graph) {
	for (int i = 0; i < demand_num; i++) {
		cout << "PATH(" << i <<") ";
		cout << "source node:" << (*p_graph).nid_nindex[(*p_graph).source];
		cout << "  destination node:"
				<< (*p_graph).nid_nindex[(*p_graph).destination] << endl;
	}
	cout << "allnode:" << (*p_graph).nodeNum <<endl;
	cout << "alledge:" << (*p_graph).edgeNum << endl;

//	cout << "node_index :";
//	for (int i = 0; i < (*p_graph).nodeNum; i++) {
//		cout << (*p_graph).nid_nindex[i] << " ";
//	}
//	for (unsigned int i = 0; i < p_graph->getEdgeSize(); i++) {
//		cout << p_graph->getithEdge(i).id << " :"
//
//				<< p_graph->nid_nindex[p_graph->getithEdge(i).to] << " ";
//		cout << p_graph->getithEdge(i).ithsrlg << " " << p_graph->getithEdge(i).cost
//				<< endl;
//	}
//	SrlgMember srlgmem;
//	if (debug_showsrlginfo) {
//		for (int i = 0; i < p_graph->srlgGroupsNum; i++) {
//			srlgmem = (*p_graph).srlgGroups.at(i);
//			cout << "SRLG" << i << ":";
//			for (int j = 0; j < srlgmem.srlgMembersNum; j++) {
//				cout << srlgmem.srlgMember.at(j) << "  ";
//			}
//			cout << endl;
//		}
//	}
}
