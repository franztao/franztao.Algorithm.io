/*
 * mustnodepath.cpp
 *
 *  Created on: Jul 26, 2016
 *      Author: franz
 */
#include "../head.h"
#include"glpk.h"

#include "IpIpoptApplication.hpp"
#include"singlemustnodepath_nlp.hpp"
using namespace Ipopt;
using namespace std;

int findAP_ILP_ipopt(Graph *p_graph, Request *p_request) {

	// Create a new instance of your nlp
	//  (use a SmartPtr, not raw)
//	SmartPtr<TNLP> mynlp = new franz_NLP(p_graph, p_request);
	SmartPtr<TNLP> mynlp = new franz_NLP(p_graph, p_request);

	// Create a new instance of IpoptApplication
	//  (use a SmartPtr, not raw)
	// We are using the factory, since this allows us to compile this
	// example with an Ipopt Windows DLL
	SmartPtr<IpoptApplication> app = IpoptApplicationFactory();
	app->RethrowNonIpoptException(true);

	// Change some options
	// Note: The following choices are only examples, they might not be
	//       suitable for your optimization problem.
	app->Options()->SetNumericValue("tol", 1e-7);
//	app->Options()->SetNumericValue("max_iter",3);
//	app->Options()->SetIntegerValue("tol");
//	app->Options()->SetStringValue("mu_strategy", "adaptive");
//	app->Options()->SetStringValue("output_file", "ipopt.out");

	// The following overwrites the default name (ipopt.opt) of the
	// options file
	// app->Options()->SetStringValue("option_file_name", "hs071.opt");

	// Initialize the IpoptApplication and process the options
	ApplicationReturnStatus status;
	status = app->Initialize();
	if (status != Solve_Succeeded) {
		std::cout << std::endl << std::endl
				<< "*** Error during initialization!" << std::endl;
		return (int) status;
	}

	// Ask Ipopt to solve the problem
	status = app->OptimizeTNLP(mynlp);;//app->OptimizeTNLP(mynlp);

	if (status == Solve_Succeeded) {
		std::cout << std::endl << std::endl << "*** The problem solved!"
				<< std::endl;
	} else {
		std::cout << std::endl << std::endl << "*** The problem FAILED!"
				<< std::endl;
	}

	// As the SmartPtrs go out of scope, the reference count
	// will be decremented and the objects will automatically
	// be deleted.

	return (int) status;

}

void processAuxilityAPInfo(Graph *p_graph, Request *p_request) {
	int now = p_graph->source;
	int next;
	int sum = 0;
	int hop = 0;

	(*p_request).AP_PathNode.push_back(now);
	while (now != (p_graph->destination)) {
		for (int i = 0; i < p_graph->edgeNum; i++) {
			if ((!p_request->BPMustNotPassEdges4AP[i])
					&& (now == p_graph->edges.at(i).from)) {
				(*p_request).AP_PathEdge.push_back(i);
//				(*p_request).BPMustNotPassEdges4AP[i] = false;
				if (-1 != p_graph->edges[i].ithsrlg)
					(*p_request).APSrlgs.push_back(p_graph->edges[i].ithsrlg);
				next = p_graph->edges.at(i).to;
				sum += p_graph->edges.at(i).cost;
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
	vector<int>::iterator it = (*p_request).AP_PathNode.begin();
	do {
		cout << p_graph->node_index[(*it)] << " ";
		it++;
	}while (it != (*p_request).AP_PathNode.end());
	cout << endl;

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


bool findAP_ILP_glpk(Graph *p_graph, Request *p_request) {

	char s[1000];
	string strc;

	glp_prob *lp;
	lp = glp_create_prob();
	glp_set_prob_name(lp, "sample");
	glp_set_obj_dir(lp, GLP_MIN);

	glp_add_rows(lp, 2 * p_graph->nodeNum);
	int i;
	strc = "inoutdegrezero";
	for (i = 1; i <= p_graph->nodeNum; i++) {
		sprintf(s, "%d", i);
		glp_set_row_name(lp, i, (strc + s).c_str());
		if ((i - 1) == p_graph->source)
			glp_set_row_bnds(lp, i, GLP_FX, 1.0, 1.0);
		if ((i - 1) == p_graph->destination)
			glp_set_row_bnds(lp, i, GLP_FX, -1.0, -1.0);
		if (((i - 1) != p_graph->source) && ((i - 1) != p_graph->destination))
			glp_set_row_bnds(lp, i, GLP_FX, 0.0, 0.0);
	}
	for (i = 1; i <= p_graph->nodeNum; i++) {
		sprintf(s, "%d", (i + p_graph->nodeNum));
		glp_set_row_name(lp, (i + p_graph->nodeNum), (strc + s).c_str());
		glp_set_row_bnds(lp, (i + p_graph->nodeNum), GLP_DB, 0.0, 2.0);
	}

	strc = "e";
	for (i = 1; i <= p_graph->edgeNum; i++) {
		cout<<p_graph->edgeNum<<"   "<<i<<"----------"<<s<<endl;
		sprintf(s, "%d", i);
		glp_add_cols(lp, i);
		glp_set_col_name(lp, i, (strc + s).c_str());
		if (!p_request->APMustPassEdges.at(i - 1))
			glp_set_col_bnds(lp, i, GLP_FX, 1.0, 1.0);

		if (!p_request->APMustNotPassEdges.at(i - 1))
			glp_set_col_bnds(lp, i, GLP_FX, 0.0, 0.0);

		if ((p_request->APMustPassEdges.at(i - 1))
				&& (p_request->APMustNotPassEdges.at(i - 1))) {
			glp_set_col_bnds(lp, i, GLP_DB, 0.0, 1.0);
		}

		glp_set_obj_coef(lp, i, p_graph->edges.at(i - 1).cost);
	}
	if(i<=p_graph->edgeNum){
		printf("EORROR:glp_set_obj_coef");
		exit(exit_ILP_glp_set_obj_coef);
	}
	int *ia, *ja;
	double *ar;

	ia = (int*) malloc(
			(((2 * p_graph->nodeNum) * (p_graph->edgeNum)) + 10) * sizeof(int));
	ja = (int*) malloc(
			(((2 * p_graph->nodeNum) * (p_graph->edgeNum)) + 10) * sizeof(int));
	ar = (double*) malloc(
			(((2 * p_graph->nodeNum) * (p_graph->edgeNum)) + 10)
					* sizeof(double));
	unsigned index = 1;
	for (i = 0; i < 2 * p_graph->nodeNum; i++) {
		for (int j = 0; j < p_graph->edgeNum; j++) {
			ia[index] = i + 1;
			ja[index] = j + 1;
			ar[index] = 0;
			if (i < p_graph->nodeNum) {

				if (i == p_graph->edges.at(j).from) {
					ar[index] = 1;
				}
				if (i == p_graph->edges.at(j).to) {
					ar[index] = -1;
				}
			} else {
				if ((i == p_graph->edges.at(j).from)
						|| (i == p_graph->edges.at(j).to)) {
					ar[index] = 1;
				}
			}
			index++;
		}

	}

	glp_load_matrix(lp, (2 * p_graph->nodeNum) * (p_graph->edgeNum), ia, ja,
			ar);

	glp_simplex(lp, NULL);

	if (!(GLP_OPT == glp_get_status(lp))) {
		return false;
	}
	cout << "ILP value:" << glp_get_obj_val(lp) << endl;
//	for (int i = 1; i <= p_graph->edgeNum; i++) {
//		cout<<glp_get_col_prim(lp, i)<<endl;
//	}

	int now = p_graph->source;
	int next;
	int sum = 0;
	int hop = 0;
	(*p_request).AP_PathNode.push_back(now);
	while (now != (p_graph->destination)) {
		for (int i = 0; i < p_graph->edgeNum; i++) {
			if ((1 == glp_get_col_prim(lp, i + 1)) //e.at(i).get(GRB_DoubleAttr_X))
			&& (now == p_graph->edges.at(i).from)) {
				(*p_request).AP_PathEdge.push_back(i);
				(*p_request).BPMustNotPassEdges4AP[i] = false;
				if (-1 != p_graph->edges[i].ithsrlg)
					(*p_request).APSrlgs.push_back(p_graph->edges[i].ithsrlg);
				next = p_graph->edges.at(i).to;
				sum += p_graph->edges.at(i).cost;
				hop++;
			}
		}
		now = next;
		(*p_request).AP_PathNode.push_back(now);
	}

	(*p_request).APCostSum = sum;
	(*p_request).APHopSum = hop;
	for (i = 0; i < (*p_request).APSrlgs.size(); i++) {
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
	vector<int>::iterator it = (*p_request).AP_PathNode.begin();
	do {
		cout << p_graph->node_index[(*it)] << " ";
		it++;
	}while (it != (*p_request).AP_PathNode.end());
	cout << endl;

	cout << "edge list " << (*p_request).AP_PathEdge.size() << "  :";
	for (unsigned i = 0; i < (*p_request).AP_PathEdge.size(); i++) {
		cout << (*p_request).AP_PathEdge[i] << " ";
	}
	cout << endl;

	cout << "|AP|:" << p_request->AP_PathEdge.size() << "  |RLAP|:"
	<< p_request->RLAP_PathEdge.size() << endl;
#endif

	glp_delete_prob(lp);
	free(ia);
	free(ja);
	free(ar);
	return true;
}

bool findMustNodePath(Graph *p_graph, Request *p_request) {
//	int r = findAP_ILP_ipopt(p_graph, p_request);
//	if (r == 0) {
//		processAuxilityAPInfo(p_graph, p_request);
//		return true;
//	}
	if(findAP_ILP_glpk(p_graph,p_request)){
		return true;
	}

	return false;
}

