/*
 * mustnodepath.cpp
 *
 *  Created on: Jul 26, 2016
 *      Author: franz
 */
#include "../head.h"
//#include"glpk.h"
#include"gurobi_c++.h"

//#include"singlemustnodepath_nlp.hpp"
//using namespace Ipopt;
using namespace std;

void processAuxilityAPInfo(GraphTopo *p_graph, Request *p_request) {
	int now = p_graph->source;
	int next;
	int sum = 0;
	int hop = 0;

	(*p_request).AP_PathNode.push_back(now);
	while (now != (p_graph->destination)) {
		for (int i = 0; i < p_graph->edgeNum; i++) {
			if ((!p_request->BPMustNotPassEdges4AP[i])
					&& (now == p_graph->getithEdge(i).from)) {
				(*p_request).AP_PathEdge.push_back(i);
//				(*p_request).BPMustNotPassEdges4AP[i] = false;
				if (-1 != p_graph->getithEdge(i).ithsrlg)
					(*p_request).APSrlgs.push_back(p_graph->getithEdge(i).ithsrlg);
				next = p_graph->getithEdge(i).to;
				sum += p_graph->getithEdge(i).cost;
				hop++;
			}
		}
		now = next;
		(*p_request).AP_PathNode.push_back(now);
	}

	(*p_request).APCostSum = sum;
	(*p_request).APHopSum = hop;
	for (unsigned int i = 0; i < (*p_request).APSrlgs.size(); i++) {
		int srlg = (*p_request).APSrlgs[i];
		for (unsigned int j = 0;
				j < (*p_graph).srlgGroups[srlg].srlgMember.size(); j++) {
			int srlgmem = (*p_graph).srlgGroups[srlg].srlgMember[j];
			if ((*p_request).BPMustNotPassEdges4AP[srlgmem]
					&& (*p_request).BPMustNotPassEdgesRLAP[srlgmem]) {
				(*p_request).BPMustNotPassEdgesRLAP[srlgmem] = false;
				p_request->RLAP_PathEdge.push_back(srlgmem);
			}
		}
	}
#ifndef ConsolePrint
	cout << "succeed to find the AP cost:(" << (*p_request).APCostSum
	<< ") hop:(" << (*p_request).APHopSum << ") :";

	cout << "edge list " << (*p_request).AP_PathEdge.size() << "  :";
	for (unsigned i = 0; i < (*p_request).AP_PathEdge.size(); i++) {
		cout << (*p_request).AP_PathEdge[i] << " ";
	}
	cout << endl;

	cout << "|AP|:" << p_request->AP_PathEdge.size() << "  |RLAP|:"
	<< p_request->RLAP_PathEdge.size() << endl;
#endif
	return;
}
//
////find AP path by ILP method ,glpk library.
//bool findAP_ILP_glpk(Graph *p_graph, Request *p_request) {
//
//	char s[1000];
//	string strc;
//
//	glp_prob *lp;
//	lp = glp_create_prob();
//	glp_set_prob_name(lp, "sample");
//	glp_set_obj_dir(lp, GLP_MIN);
//
//	glp_add_rows(lp, 2 * p_graph->nodeNum);
//	unsigned int i;
//	strc = "inoutdegrezero";
//	for (i = 1; i <= p_graph->nodeNum; i++) {
//		sprintf(s, "%d", i);
//		glp_set_row_name(lp, i, (strc + s).c_str());
//		if ((i - 1) == p_graph->source)
//			glp_set_row_bnds(lp, i, GLP_FX, 1.0, 1.0);
//		if ((i - 1) == p_graph->destination)
//			glp_set_row_bnds(lp, i, GLP_FX, -1.0, -1.0);
//		if (((i - 1) != p_graph->source) && ((i - 1) != p_graph->destination))
//			glp_set_row_bnds(lp, i, GLP_FX, 0.0, 0.0);
//	}
//	for (i = 1; i <= p_graph->nodeNum; i++) {
//		sprintf(s, "%d", (i + p_graph->nodeNum));
//		glp_set_row_name(lp, (i + p_graph->nodeNum), (strc + s).c_str());
//		glp_set_row_bnds(lp, (i + p_graph->nodeNum), GLP_DB, 0.0, 2.0);
//	}
//
//	strc = "e";
//	for (i = 1; i <= p_graph->edgeNum; i++) {
//		sprintf(s, "%d", i);
//		glp_add_cols(lp, i);
//		glp_set_col_name(lp, i, (strc + s).c_str());
//		if (!p_request->APMustPassEdges.at(i - 1))
//			glp_set_col_bnds(lp, i, GLP_FX, 1.0, 1.0);
//
//		if (!p_request->APMustNotPassEdges.at(i - 1))
//			glp_set_col_bnds(lp, i, GLP_FX, 0.0, 0.0);
//
//		if ((p_request->APMustPassEdges.at(i - 1))
//				&& (p_request->APMustNotPassEdges.at(i - 1))) {
//			glp_set_col_bnds(lp, i, GLP_DB, 0.0, 1.0);
//		}
//
//		glp_set_obj_coef(lp, i, p_graph->getithEdge(i - 1).cost);
//	}
//	if (i <= p_graph->edgeNum) {
//		printf("EORROR:glp_set_obj_coef");
//		exit(exit_ILP_glp_set_obj_coef);
//	}
//	int *ia, *ja;
//	double *ar;
//
//	ia = (int*) malloc(
//			(((2 * p_graph->nodeNum) * (p_graph->edgeNum)) + 10) * sizeof(int));
//	ja = (int*) malloc(
//			(((2 * p_graph->nodeNum) * (p_graph->edgeNum)) + 10) * sizeof(int));
//	ar = (double*) malloc(
//			(((2 * p_graph->nodeNum) * (p_graph->edgeNum)) + 10)
//					* sizeof(double));
//	unsigned index = 1;
//	for (i = 0; i < 2 * p_graph->nodeNum; i++) {
//		for (int j = 0; j < p_graph->edgeNum; j++) {
//			ia[index] = i + 1;
//			ja[index] = j + 1;
//			ar[index] = 0;
//			if (i < p_graph->nodeNum) {
//
//				if (i == p_graph->getithEdge(j).from) {
//					ar[index] = 1;
//				}
//				if (i == p_graph->getithEdge(j).to) {
//					ar[index] = -1;
//				}
//			} else {
//				if ((i == p_graph->getithEdge(j).from)
//						|| (i == p_graph->getithEdge(j).to)) {
//					ar[index] = 1;
//				}
//			}
//			index++;
//		}
//
//	}
//
//	glp_load_matrix(lp, (2 * p_graph->nodeNum) * (p_graph->edgeNum), ia, ja,
//			ar);
//
//	glp_simplex(lp, NULL);
//
//	if (!(GLP_OPT == glp_get_status(lp))) {
//		return false;
//	}
//
//	bool SolutionIsInteger = true;
//
//	for (unsigned int i = 1; i <= p_graph->edgeNum; i++) {
//		if (glp_get_col_prim(lp, i)) {
//			if (glp_get_col_prim(lp, i) != 1.0) {
//				SolutionIsInteger = false;
//			}
//		}
//	}
//	if (SolutionIsInteger == false)
//		return false;
//
//	int now = p_graph->source;
//	int next;
//	int sum = 0;
//	int hop = 0;
//	(*p_request).AP_PathNode.push_back(now);
//	while (now != (p_graph->destination)) {
//		for (int i = 0; i < p_graph->edgeNum; i++) {
//			if ((1 == glp_get_col_prim(lp, i + 1)) //e.at(i).get(GRB_DoubleAttr_X))
//			&& (now == p_graph->getithEdge(i).from)) {
//				(*p_request).AP_PathEdge.push_back(i);
//				(*p_request).BPMustNotPassEdges4AP[i] = false;
//				if (-1 != p_graph->getithEdge(i).ithsrlg)
//					(*p_request).APSrlgs.push_back(p_graph->getithEdge(i).ithsrlg);
//				next = p_graph->getithEdge(i).to;
//				sum += p_graph->getithEdge(i).cost;
//				hop++;
//			}
//		}
//		now = next;
//		(*p_request).AP_PathNode.push_back(now);
//	}
//
//	(*p_request).APCostSum = sum;
//	(*p_request).APHopSum = hop;
//	for (i = 0; i < (*p_request).APSrlgs.size(); i++) {
//		int srlg = (*p_request).APSrlgs[i];
//		for (unsigned int j = 0;
//				j < (*p_graph).srlgGroups[srlg].srlgMember.size(); j++) {
//			int srlgmem = (*p_graph).srlgGroups[srlg].srlgMember[j];
//			if ((*p_request).BPMustNotPassEdges4AP[srlgmem]
//					&& (*p_request).BPMustNotPassEdgesRLAP[srlgmem]) {
//				(*p_request).BPMustNotPassEdgesRLAP[srlgmem] = false;
//				p_request->RLAP_PathEdge.push_back(srlgmem);
//			}
//		}
//	}
//
//	glp_delete_prob(lp);
//	free(ia);
//	free(ja);
//	free(ar);
//	return true;
//}

//find the first path(AP),if there is not AP,the algorithm end,otherwise return true and find BP
bool findAP_ILP_gurobi(GraphTopo *p_graph, Request *p_request) {
	try {
		GRBEnv env = GRBEnv();
		unsigned edge_num = p_graph->edgeNum;
		unsigned node_num = p_graph->nodeNum;
		GRBModel model = GRBModel(env);
		model.getEnv().set(GRB_IntParam_OutputFlag, 0);
		// Create variables
		vector<GRBVar> e; // = vector<GRBVar>(edge_num);
		for (unsigned int i = 0; i < edge_num; i++) {
			GRBVar ei;
			e.push_back(ei);
		}
		string str = "vars";
		char s[1000];
		for (unsigned i = 0; i < edge_num; i++) {
			sprintf(s, "%d", i);

			if (!p_request->APMustPassEdges.at(i))
				e.at(i) = model.addVar(1.0, 1.0, 0.0, GRB_BINARY, (str + s));
			if (!p_request->APMustNotPassEdges.at(i))
				e.at(i) = model.addVar(0.0, 0.0, 0.0, GRB_BINARY, (str + s));
			if ((p_request->APMustPassEdges.at(i))
					&& (p_request->APMustNotPassEdges.at(i))) {
				e.at(i) = model.addVar(0.0, 1.0, 0.0, GRB_BINARY, (str + s));
			}

		}

		// Integrate new variables

		model.update();
		GRBLinExpr obj = 0.0;
		for (unsigned int i = 0; i < edge_num; i++) {
			obj += (p_graph->getithEdge(i).cost * e.at(i));
		}
		model.setObjective(obj, GRB_MINIMIZE);

		string strc = "consinout";
		for (unsigned int i = 0; i < node_num; i++) {
			sprintf(s, "%u", i);
			GRBLinExpr con_inout0 = 0.0;
			for (unsigned int j = 0; j < p_graph->edgeNum; j++) {
				if (i == p_graph->getithEdge(j).from) {
					con_inout0 += e.at(j);
				}
				if (i == p_graph->getithEdge(j).to)
					con_inout0 += (-1 * e.at(j));
			}
			if (i == p_graph->source)
				model.addConstr(con_inout0 == 1, (strc + s));
			if (i == p_graph->destination)
				model.addConstr(con_inout0 == (-1), (strc + s));
			if ((i != p_graph->source) && (i != p_graph->destination))
				model.addConstr(con_inout0 == 0, (strc + s));

		}

		string strd = "consdegree";
		for (unsigned int i = 0; i < edge_num; i++) {
			sprintf(s, "%u", i);
			GRBLinExpr con = 0.0;
			for (unsigned int j = 0; j < p_graph->edgeNum; j++) {
				if ((i == p_graph->getithEdge(j).from)
						|| ((i == p_graph->getithEdge(j).to)))
					con += e.at(j);
			}
			model.addConstr(con <= 2, (strd + s));
		}
// Optimize model

//		cout<<model.getVars()<<endl;
		model.optimize();

		 int optimstatus = model.get(GRB_IntAttr_Status);
		if (optimstatus != GRB_OPTIMAL) {
			return false;
		}

		bool SolutionIsInteger = true;


		for (unsigned int i = 0; i < edge_num; i++) {
			if (e.at(i).get(GRB_DoubleAttr_X)) {
				if (e.at(i).get(GRB_DoubleAttr_X) != 1.0) {
					SolutionIsInteger = false;
				}
			}
		}
		if (SolutionIsInteger == false)
			return false;

		int now = p_graph->source;
		int next;
		int sum = 0;
		int hop = 0;

		(*p_request).AP_PathNode.push_back(now);
		while (now != (p_graph->destination)) {
			for (unsigned int i = 0; i < p_graph->edgeNum; i++) {
				if ((e.at(i).get(GRB_DoubleAttr_X))
						&& (now == p_graph->getithEdge(i).from)) {
					(*p_request).AP_PathEdge.push_back(i);
					(*p_request).BPMustNotPassEdges4AP[i] = false;
					if (-1 != p_graph->getithEdge(i).ithsrlg)
						(*p_request).APSrlgs.push_back(
								p_graph->getithEdge(i).ithsrlg);
					next = p_graph->getithEdge(i).to;
					sum += p_graph->getithEdge(i).cost;
					hop++;
				}
			}
			now = next;
			(*p_request).AP_PathNode.push_back(now);
		}

		sort(p_request->APSrlgs.begin(), p_request->APSrlgs.end());
		vector<int>::iterator pos;
		pos = unique(p_request->APSrlgs.begin(), p_request->APSrlgs.end());
		p_request->APSrlgs.erase(pos, p_request->APSrlgs.end());

		(*p_request).APCostSum = sum;
		(*p_request).APHopSum = hop;
		for (unsigned int i = 0; i < (*p_request).APSrlgs.size(); i++) {
			int srlg = (*p_request).APSrlgs[i];
			for (unsigned int j = 0;
					j < (*p_graph).srlgGroups[srlg].srlgMember.size(); j++) {
				int srlgmem = (*p_graph).srlgGroups[srlg].srlgMember[j];
				if ((*p_request).BPMustNotPassEdges4AP[srlgmem]
						&& (*p_request).BPMustNotPassEdgesRLAP[srlgmem]) {
					(*p_request).BPMustNotPassEdgesRLAP[srlgmem] = false;
					p_request->RLAP_PathEdge.push_back(srlgmem);
				}
			}
		}
		return true;

	} catch (GRBException e) {
		cout << "Error code = " << e.getErrorCode() << endl;
		cout << e.getMessage() << endl;
	} catch (...) {
		cout << "Exception during optimization" << endl;
	}
	return false;
}

bool findAP_dijastra(GraphTopo *p_graph, Request *p_request) {

	typedef pair<int, int> P;
	vector<int> dist((*p_graph).nodeNum, (-1));
	vector<int> hop((*p_graph).nodeNum, (-1));
	vector<int> path_node((*p_graph).nodeNum, (-1));
	vector<int> path_edge((*p_graph).nodeNum, (-1));
	priority_queue<P, vector<P>, greater<P> > que;
	unsigned int len;
	dist[(*p_graph).source] = 0;
	hop[(*p_graph).source] = 0;
	que.push(P(0, (*p_graph).source));

	while (!que.empty()) {
		P p = que.top();
		que.pop();
		int v = p.second;

		if (dist[v] < p.first)
			continue;
		len = (*p_graph).ftopo_r_Node_c_EdgeList[v].edgeList.size();
		for (unsigned int i = 0; i < len; i++) {

			EdgeClass &e = p_graph->getithEdge(
					(*p_graph).ftopo_r_Node_c_EdgeList[v].edgeList[i]);

			if (0 == (p_request->APMustNotPassEdges.size())) {
				cout << "p_request->APedgestabu is " << "NULL" << endl;
			}
			if (!p_request->APMustNotPassEdges[e.id])
				continue;
			if (-1 == dist[e.to]) {
				dist[e.to] = dist[v] + e.cost;
				hop[e.to] = hop[v] + 1;
				path_node[e.to] = v;
				path_edge[e.to] = e.id;
				que.push(P(dist[e.to], e.to));
			} else {
				if (dist[e.to] > (dist[v] + e.cost)) {
					dist[e.to] = dist[v] + e.cost;
					hop[e.to] = hop[v] + 1;
					path_node[e.to] = v;
					path_edge[e.to] = e.id;
					que.push(P(dist[e.to], e.to));
				} else {
					if (dist[e.to] == (dist[v] + e.cost)) {
						if (hop[e.to] > (hop[v] + 1)) {
							hop[e.to] = hop[v] + 1;
							path_node[e.to] = v;
							path_edge[e.to] = e.id;
							que.push(P(dist[e.to], e.to));
						}
					}
				}
			}
		}
	}

	if (-1 == dist[(*p_graph).destination]) {
		return false;
	} else {
		int now;
		int next;

		now = (*p_graph).destination;
		vector<int> midnode;
		vector<int> midedge;
		next = path_node[now];
		midnode.push_back(now);

		p_request->APSrlgs.clear(); //
		while (next != -1) {
//			(*p_request).AP_PathNode.push_back(next);
//			(*p_request).AP_PathEdge.push_back(path_edge[now]);
			midnode.push_back(next);
			midedge.push_back(path_edge[now]);
			(*p_request).BPMustNotPassEdges4AP[path_edge[now]] = false;
			if (-1 != p_graph->getithEdge(path_edge[now]).ithsrlg)
				(*p_request).APSrlgs.push_back(
						p_graph->getithEdge(path_edge[now]).ithsrlg);
			now = next;
			next = path_node[now];
		}
		for (int k = (midnode.size() - 1); k >= 0; k--) {
			p_request->AP_PathNode.push_back(midnode[k]);
		}
		for (int k = (midedge.size() - 1); k >= 0; k--) {
			p_request->AP_PathEdge.push_back(midedge[k]);
		}

		(*p_request).APCostSum = dist[(*p_graph).destination];
		(*p_request).APHopSum = hop[(*p_graph).destination];

		sort(p_request->APSrlgs.begin(), p_request->APSrlgs.end());
		vector<int>::iterator pos;
		pos = unique(p_request->APSrlgs.begin(), p_request->APSrlgs.end());
		p_request->APSrlgs.erase(pos, p_request->APSrlgs.end());

		for (unsigned int i = 0; i < (*p_request).APSrlgs.size(); i++) {
			int srlg = (*p_request).APSrlgs[i];
			for (unsigned int j = 0;
					j < (*p_graph).srlgGroups[srlg].srlgMember.size(); j++) {
				int srlgmem = (*p_graph).srlgGroups[srlg].srlgMember[j];
				if ((*p_request).BPMustNotPassEdges4AP[srlgmem]
						&& (*p_request).BPMustNotPassEdgesRLAP[srlgmem]) {
					(*p_request).BPMustNotPassEdgesRLAP[srlgmem] = false;
					p_request->RLAP_PathEdge.push_back(srlgmem);
				}
			}
		}

		return true;
	}
}

//find AP path must pass some essential node.
bool findMustNodePath(GraphTopo *p_graph, Request *p_request) {
//	int r = findAP_ILP_ipopt(p_graph, p_request);
//	if (r == 0) {
//		processAuxilityAPInfo(p_graph, p_request);
//		return true;
//	}
//	if (findAP_ILP_glpk(p_graph, p_request)) {
//		return true;
//	}


	return false;
}

