#include "head.h"
#include "lib/lib_io.h"

int edge_num;
int demand_num;
int srlg_num;

bool judge_isStarProperty() {
	return true;
}

void eliminate_invalidnodeandedge() {
}

//void StoreEdgeInformationbyMatrix(Graph *p_graph, int EdgeFlag, int inEdgeFlag,
//		int outEdgeflag, int EdgeWeight) {
//	if ((inEdgeFlag != (*p_graph).Destination)
//			&& (outEdgeflag != (*p_graph).Source)
//			&& (inEdgeFlag != outEdgeflag)) {
//		if (0 == (*p_graph).InputGraphMatix[inEdgeFlag][outEdgeflag]) {
//			(*p_graph).InputGraphMatix[inEdgeFlag][outEdgeflag] = EdgeWeight;
//			(*p_graph).FlagofInputGraphEdgeMatrix[inEdgeFlag][outEdgeflag] =
//					EdgeFlag;
//			(*p_graph).EdgeSizeofInputGragh++;
//		} else {
//			if (0 == (*p_graph).InputMaxGraphMatix[inEdgeFlag][outEdgeflag]) {
//				(*p_graph).InputMaxGraphMatix[inEdgeFlag][outEdgeflag] =
//						EdgeWeight;
//				(*p_graph).FlagofInputMaxGraphEdgeMatrix[inEdgeFlag][outEdgeflag] =
//						EdgeFlag;
//				(*p_graph).EdgeSizeofInputGragh++;
//			} else {
//				if ((*p_graph).InputMaxGraphMatix[inEdgeFlag][outEdgeflag]
//						> EdgeWeight) {
//					(*p_graph).InputMaxGraphMatix[inEdgeFlag][outEdgeflag] =
//							EdgeWeight;
//					(*p_graph).FlagofInputMaxGraphEdgeMatrix[inEdgeFlag][outEdgeflag] =
//							EdgeFlag;
//				}
//			}
//			if ((*p_graph).InputGraphMatix[inEdgeFlag][outEdgeflag]
//					> (*p_graph).InputMaxGraphMatix[inEdgeFlag][outEdgeflag]) {
//				int mid;
//				mid = (*p_graph).InputMaxGraphMatix[inEdgeFlag][outEdgeflag];
//				(*p_graph).InputMaxGraphMatix[inEdgeFlag][outEdgeflag] =
//						(*p_graph).InputGraphMatix[inEdgeFlag][outEdgeflag];
//				(*p_graph).InputGraphMatix[inEdgeFlag][outEdgeflag] = mid;
//				mid =
//						(*p_graph).FlagofInputMaxGraphEdgeMatrix[inEdgeFlag][outEdgeflag];
//				(*p_graph).FlagofInputMaxGraphEdgeMatrix[inEdgeFlag][outEdgeflag] =
//						(*p_graph).FlagofInputGraphEdgeMatrix[inEdgeFlag][outEdgeflag];
//				(*p_graph).FlagofInputGraphEdgeMatrix[inEdgeFlag][outEdgeflag] =
//						mid;
//			}
//		}
//	}
//}
//add edge to the graph(List)

void LoadData(Graph *p_graph, char *topo[MAX_EDGE_NUM],
		int edgenum, char *demand[MAX_DEMAND_NUM], int demandnum,
		char *srlg[MAX_SRLG_NUM], int srlgnum) {
	edge_num = edgenum;
	demand_num = demandnum;
	srlg_num = srlgnum;
	p_graph->node_index = vector<int>(edgenum * 2, -1);
	p_graph->index_node = vector<int>(edgenum * 2, -1);
	p_graph->isValidNode = vector<bool>(edgenum * 2, -1);
	int int_stringofint;

	//load the demanding disjoint path from demand.csv
	for (int i = 0; i < demand_num; i++) {
		int j = 0;
		int demandStrLen = strlen(demand[i]);
		for (int_stringofint = 0; j < demandStrLen; j++) {
			if (!(('0' <= demand[i][j]) && ('9' >= demand[i][j]))) {
				j++;
				break;
			}
			int_stringofint = int_stringofint * 10 + (demand[i][j] - '0');
		}

		for (int_stringofint = 0; j < demandStrLen; j++) {
			if (!(('0' <= demand[i][j]) && ('9' >= demand[i][j]))) {
				j++;
				break;
			}
			int_stringofint = int_stringofint * 10 + (demand[i][j] - '0');
		}
		(*p_graph).source = int_stringofint;
		//Source = int_stringofint;
		//        Destination = int_stringofint;
		for (int_stringofint = 0; j < demandStrLen; j++) {
			if (!(('0' <= demand[i][j]) && ('9' >= demand[i][j]))) {
				j++;
				break;
			}
			int_stringofint = int_stringofint * 10 + (demand[i][j] - '0');
		}
		(*p_graph).destination = int_stringofint;
		//Destination = int_stringofint;
		//        Source = int_stringofint;

		if (-1 == (*p_graph).index_node[(*p_graph).source]) {
			(*p_graph).index_node[(*p_graph).source] = (*p_graph).nodeNum;
			(*p_graph).node_index[(*p_graph).nodeNum] = (*p_graph).source;
			(*p_graph).source = (*p_graph).nodeNum;
			(*p_graph).isValidNode[(*p_graph).nodeNum] = true;
			(*p_graph).nodeNum++;

		} else {
			(*p_graph).source = (*p_graph).index_node[(*p_graph).source];
		}

		if (-1 == (*p_graph).index_node[(*p_graph).destination]) {
			(*p_graph).index_node[(*p_graph).destination] =
					(*p_graph).nodeNum;
			(*p_graph).node_index[(*p_graph).nodeNum] =
					(*p_graph).destination;
			(*p_graph).destination = (*p_graph).nodeNum;
			(*p_graph).isValidNode[(*p_graph).nodeNum] = true;
			(*p_graph).nodeNum++;

		} else {
			(*p_graph).destination =
					(*p_graph).index_node[(*p_graph).destination];
		}
	}
//	(*p_request).source = (*p_graph).source;
//	(*p_request).destination = (*p_graph).destination;

	//load the topo structure of the graph from topo.csv
	for (int i = 0; i < edge_num; i++) {
		int EdgeFlag, inEdgeFlag, outEdgeflag, EdgeWeight;
		int j = 0;
		int_stringofint = 0;
		int ithtopoSize = strlen(topo[i]);
		for (; j < ithtopoSize; j++) {
			if (!(('0' <= topo[i][j]) && ('9' >= topo[i][j]))) {
				j++;
				break;
			}
			int_stringofint = int_stringofint * 10 + (topo[i][j] - '0');
		}
		EdgeFlag = int_stringofint;

		int_stringofint = 0;
		for (; j < ithtopoSize; j++) {
			if (!(('0' <= topo[i][j]) && ('9' >= topo[i][j]))) {
				j++;
				break;
			}
			int_stringofint = int_stringofint * 10 + (topo[i][j] - '0');
		}
		if (-1 == (*p_graph).index_node[int_stringofint]) {
			(*p_graph).index_node[int_stringofint] = (*p_graph).nodeNum;
			(*p_graph).node_index[(*p_graph).nodeNum] = int_stringofint;
			(*p_graph).isValidNode[(*p_graph).nodeNum] = true;
			(*p_graph).nodeNum++;
		}
		inEdgeFlag = (*p_graph).index_node[int_stringofint];

		int_stringofint = 0;
		for (; j < ithtopoSize; j++) {
			if (!(('0' <= topo[i][j]) && ('9' >= topo[i][j]))) {
				j++;
				break;
			}
			int_stringofint = int_stringofint * 10 + (topo[i][j] - '0');
		}

		if (-1 == (*p_graph).index_node[int_stringofint]) {
			(*p_graph).index_node[int_stringofint] = (*p_graph).nodeNum;
			(*p_graph).node_index[(*p_graph).nodeNum] = int_stringofint;
			(*p_graph).isValidNode[(*p_graph).nodeNum] = 1;
			(*p_graph).nodeNum++;

		}
		outEdgeflag = (*p_graph).index_node[int_stringofint];
		int_stringofint = 0;

		for (; j < ithtopoSize; j++) {
			if (!(('0' <= topo[i][j]) && ('9' >= topo[i][j]))) {
				j++;
				break;
			}
			int_stringofint = int_stringofint * 10 + (topo[i][j] - '0');
		}
		if (0 == WeightSort) {
			EdgeWeight = int_stringofint;
		}
		if (1 == WeightSort) {
			EdgeWeight = 1;
		}
		//StoreEdgeInformationbyMatrix(p_graph, EdgeFlag, inEdgeFlag, outEdgeflag,
		//	EdgeWeight);
		//addedges(p_graph, EdgeFlag, inEdgeFlag, outEdgeflag, EdgeWeight);
		p_graph->AddEdges(EdgeFlag, inEdgeFlag, outEdgeflag, EdgeWeight);
		//StoreEdgeInformationbyLink(graph,EdgeFlag, inEdgeFlag, outEdgeflag,EdgeWeight);
	}

	//load the srlg structure from srlg.csv
	for (int i = 0; i < srlg_num; i++) {
		//int group;
		int member;
		int j = 0;
		int_stringofint = 0;
		int ithsrlgSize = strlen(srlg[i]);

		for (; j < ithsrlgSize; j++) {
			if (!(('0' <= srlg[i][j]) && ('9' >= srlg[i][j]))) {
				j++;
				break;
			}
			int_stringofint = int_stringofint * 10 + (srlg[i][j] - '0');
		}
		//group = int_stringofint;

		bool havanum = false;
		SrlgMember srlgmem; //= new srlgMembers();
		for (int_stringofint = 0; j <= ithsrlgSize; j++) {
			if ((!(('0' <= srlg[i][j]) && ('9' >= srlg[i][j])) && havanum)
					|| ((j == (ithsrlgSize)) && havanum)) {
				member = int_stringofint;
				srlgmem.srlgMember.push_back(member);
				//srlgmem.srlgMember.push_back(index_node[int_stringofint]);
				int_stringofint = 0;
				havanum = false;

			} else {
				if (('0' <= srlg[i][j]) && ('9' >= srlg[i][j])) {
					havanum = true;
					int_stringofint = int_stringofint * 10 + (srlg[i][j] - '0');
				}
			}
		}
		srlgmem.srlgmemberNum = srlgmem.srlgMember.size();
		(*p_graph).srlgGroups.push_back(srlgmem);
	}
	(*p_graph).srlgGroupsNum = srlg_num;

	p_graph->TransformToNostarGraph();
	p_graph->ConstructGraphbyLink();
	p_graph->InsertSRLGInfoToEdgeInfo();

	//eliminate_invalidnodeandedge();
	return;
}

