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

//load the demand,topo and srlg data.
bool LoadGraphData(GraphTopo *p_graph, char *topo[MAX_EDGE_NUM], int edgenum,
		char *demand[MAX_DEMAND_NUM], int demandnum, char *srlg[MAX_SRLG_NUM],
		int srlgnum) {
	edge_num = edgenum;
	demand_num = demandnum;
	srlg_num = srlgnum;

	int int_stringofint;

	string str;
	char temp[64];
//	(*p_request).source = (*p_graph).source;
//	(*p_request).destination = (*p_graph).destination;

//load the topo structure of the graph from topo.csv
	for (int i = 0; i < edge_num; i++) {
		stringstream ss;
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
//		ss.clear();
		sprintf(temp, "%d", int_stringofint);
		str = string(temp);

		if (p_graph->nindex_nid.end() == p_graph->nindex_nid.find(str)) {

			p_graph->nindex_nid.insert(
					pair<string, int>(str, p_graph->nodeNum));
			p_graph->nid_nindex.insert(
					pair<int, string>(p_graph->nodeNum, str));
//			(*p_graph).nindex_nid[int_stringofint] = (*p_graph).nodeNum;
//			(*p_graph).nid_nindex[(*p_graph).nodeNum] = int_stringofint;

			(*p_graph).isValidNode[(*p_graph).nodeNum] = true;
			(*p_graph).nodeNum++;
		}
		inEdgeFlag = (*p_graph).nindex_nid[str];

		int_stringofint = 0;
		for (; j < ithtopoSize; j++) {
			if (!(('0' <= topo[i][j]) && ('9' >= topo[i][j]))) {
				j++;
				break;
			}
			int_stringofint = int_stringofint * 10 + (topo[i][j] - '0');
		}

		sprintf(temp, "%d", int_stringofint);
		str = string(temp);
//		if (-1 == (*p_graph).nindex_nid[int_stringofint]) {
//			(*p_graph).nindex_nid[int_stringofint] = (*p_graph).nodeNum;
//			(*p_graph).nid_nindex[(*p_graph).nodeNum] = int_stringofint;
		if (p_graph->nindex_nid.end() == p_graph->nindex_nid.find(str)) {
			p_graph->nindex_nid.insert(
					pair<string, int>(str, p_graph->nodeNum));
			p_graph->nid_nindex.insert(
					pair<int, string>(p_graph->nodeNum, str));
			(*p_graph).isValidNode[(*p_graph).nodeNum] = true;
			(*p_graph).nodeNum++;

		}
		outEdgeflag = (*p_graph).nindex_nid[str];
		int_stringofint = 0;

		//for solving the bug where forgetting set edge's cost in topo.csv
		if (j == ithtopoSize) {
			int_stringofint = 1;
		}

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
		p_graph->AddEdges(EdgeFlag, inEdgeFlag, outEdgeflag, EdgeWeight);
	}

	//load the demanding disjoint path from demand.csv
	for (int i = 0; i < demand_num; i++) {
		stringstream ss;
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

		int_stringofint = int_stringofint % (p_graph->nodeNum);

		(*p_graph).source = int_stringofint;

		for (int_stringofint = 0; j < demandStrLen; j++) {
			if (!(('0' <= demand[i][j]) && ('9' >= demand[i][j]))) {
				j++;
				break;
			}
			int_stringofint = int_stringofint * 10 + (demand[i][j] - '0');
		}

		//resolve the bug that there are big number' source and destination in randomdemand's file
		int_stringofint = int_stringofint % (p_graph->nodeNum);

		(*p_graph).destination = int_stringofint;

		sprintf(temp, "%d", (*p_graph).source);
		str = string(temp);

		if (p_graph->nindex_nid.end() == p_graph->nindex_nid.find(str)) {
			return false; //if source no exist in the edges.return false;
		} else {
			(*p_graph).source = (*p_graph).nindex_nid[str];
		}
		sprintf(temp, "%d", (*p_graph).destination);
		str = string(temp);

		if (p_graph->nindex_nid.end() == p_graph->nindex_nid.find(str)) {
			return false; //if destination no exist in the edges.return false;
		} else {
			(*p_graph).destination = (*p_graph).nindex_nid[str];
		}
	}

	if (isUndirectedGraph) {
		unsigned int edge = p_graph->edgeNum;
		for (unsigned i = 0; i < edge; i++) {
			EdgeClass fe = p_graph->getithEdge(i);
			p_graph->getithEdge(i).revedgeid = p_graph->edgeNum;
			EdgeClass re(fe.to, fe.from, fe.cost, fe.id, p_graph->edgeNum, 1, -1, i);
			p_graph->AddEdges(re);
			p_graph->edgeNum++;
		}

	}
	//load the srlg structure from srlg.csv
	for (int i = 0; i < srlg_num; i++) {
		//int group;
		int member;
		int res;
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
				if (isUndirectedGraph) {
					res = p_graph->getithEdge(member).revedgeid;
					srlgmem.srlgMember.push_back(res);
				}
				int_stringofint = 0;
				havanum = false;
			} else {
				if (('0' <= srlg[i][j]) && ('9' >= srlg[i][j])) {
					havanum = true;
					int_stringofint = int_stringofint * 10 + (srlg[i][j] - '0');
				}
			}
		}
		srlgmem.srlgMembersNum = srlgmem.srlgMember.size();
		(*p_graph).srlgGroups.push_back(srlgmem);
	}
	(*p_graph).srlgGroupsNum = srlg_num;

	p_graph->TransformedToEdgeBelongingOnlySRLG();
	p_graph->TransformToNostarGraph(); //
	p_graph->ConstructGraphbyLink();
	p_graph->InsertSRLGInfoToEdgeInfo();
	//eliminate_invalidnodeandedge();
	return true;
}

