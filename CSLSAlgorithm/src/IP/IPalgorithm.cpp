#include "../head.h"
#include"glpk.h"
#include<vector>
//#include "localsolver.h"
#include"gurobi_c++.h"
//using namespace localsolver;
using namespace std;

extern DisjointPathPair *AlgorithmResult;

void RecordResult(GraphTopo *p_graph, vector<bool>&APpath, vector<bool>&BPpath) {
	AlgorithmResult->APcostsum = 0;
	AlgorithmResult->BPcostsum = 0;
	int state = p_graph->source;

	while (state != p_graph->destination) {
		for (unsigned int j = 0;
				j < p_graph->ftopo_r_Node_c_EdgeList.at(state).edgeList.size();
				j++) {
			int iedge = p_graph->ftopo_r_Node_c_EdgeList.at(state).edgeList.at(j);
			if (APpath[iedge]) {
				state = p_graph->getithEdge(iedge).to;
				AlgorithmResult->APedge.push_back(iedge);
				AlgorithmResult->APcostsum += p_graph->getithEdge(iedge).cost;
				break;
			}
		}
	}
	AlgorithmResult->APhop = AlgorithmResult->APedge.size()+1 ;
	state = p_graph->source;

	while (state != p_graph->destination) {
		for (unsigned int j = 0;
				j < p_graph->ftopo_r_Node_c_EdgeList.at(state).edgeList.size();
				j++) {
			int iedge = p_graph->ftopo_r_Node_c_EdgeList.at(state).edgeList.at(j);
			if (BPpath[iedge]) {
				state = p_graph->getithEdge(iedge).to;
				AlgorithmResult->BPedge.push_back(iedge);
				AlgorithmResult->BPcostsum += p_graph->getithEdge(iedge).cost;
				break;
			}
		}
	}

	AlgorithmResult->BPhop = AlgorithmResult->BPedge.size()+1 ;
	AlgorithmResult->SolutionNotFeasible=false;
}

//bool ILPAlgorithm_glpk(Graph *p_graph) {
//	char s[1000];
//	string strc;
//
//	glp_prob *lp;
//	lp = glp_create_prob();
//	glp_set_prob_name(lp, "sample");
//	glp_set_obj_dir(lp, GLP_MIN);
//	unsigned int srlgmulti = 0;
//	for (unsigned i = 0; i < p_graph->srlgGroups.size(); i++) {
//		srlgmulti += (p_graph->srlgGroups.at(i).srlgMember.size()
//				* p_graph->srlgGroups.at(i).srlgMember.size());
//	}
//	int rownum = (2 * p_graph->nodeNum) + p_graph->edgeNum + srlgmulti;
//	int colnum = (2 * p_graph->edgeNum);
//
//	int index = 0;
//	//AX1=u
//
//	glp_add_cols(lp, colnum);
//	//APedge
//	strc = "APedge";
//	for (unsigned int i = 1; i <= p_graph->edgeNum; i++) {
//		sprintf(s, "%d", i);
//		glp_set_col_name(lp, i + index, (strc + s).c_str());
////		glp_set_col_kind(lp, i + index, GLP_BV);
////		glp_set_col_kind(lp, i + index, GLP_IV);
//		glp_set_col_bnds(lp, i + index, GLP_DB, 0.0, 1.0);
////		glp_set_col_stat(lp, i+ index,GLP_NF);
//		glp_set_obj_coef(lp, i, p_graph->getithEdge(i - 1).cost);
//
//	}
//	//BPedge
//	index += p_graph->edgeNum;
//	strc = "BPedge";
//	for (unsigned int i = 1; i <= p_graph->edgeNum; i++) {
//		sprintf(s, "%d", i);
//		glp_set_col_name(lp, i + index, (strc + s).c_str());
////		glp_set_col_kind(lp, i + index, GLP_BV);
////		glp_set_col_kind(lp, i + index, GLP_IV);
//		glp_set_col_bnds(lp, i + index, GLP_DB, 0.0, 1.0);
//		glp_set_obj_coef(lp, i + index, 0.0);
////		glp_set_col_stat(lp, i+ index,GLP_NF);
//
//	}
//
//	index = 0;
//	glp_add_rows(lp, rownum);
//	strc = "APinoutdegrezero";
//	for (int i = 1; i <= p_graph->nodeNum; i++) {
//		sprintf(s, "%d", i);
//		glp_set_row_name(lp, i, (strc + s).c_str());
//		if ((i - 1) == p_graph->source)
//			glp_set_row_bnds(lp, i, GLP_FX, 1.0, 1.0);
//		if ((i - 1) == p_graph->destination)
//			glp_set_row_bnds(lp, i, GLP_FX, -1.0, -1.0);
//		if (((i - 1) != p_graph->source) && ((i - 1) != p_graph->destination))
//			glp_set_row_bnds(lp, i, GLP_FX, 0.0, 0.0);
//	}
//
//	//AX2=u
//	index += p_graph->nodeNum;
//	strc = "BPinoutdegrezero";
//	for (unsigned int i = 1; i <= p_graph->nodeNum; i++) {
//		sprintf(s, "%d", i);
//		glp_set_row_name(lp, i + index, (strc + s).c_str());
//		if ((i - 1) == p_graph->source)
//			glp_set_row_bnds(lp, i + index, GLP_FX, 1.0, 1.0);
//		if ((i - 1) == p_graph->destination)
//			glp_set_row_bnds(lp, i + index, GLP_FX, -1.0, -1.0);
//		if (((i - 1) != p_graph->source) && ((i - 1) != p_graph->destination))
//			glp_set_row_bnds(lp, i + index, GLP_FX, 0.0, 0.0);
//	}
//
//	//0<=X1+X2<=1
//	index += p_graph->nodeNum;
//	strc = "APnotcrossBP";
//	for (unsigned int i = 1; i <= p_graph->edgeNum; i++) {
//		sprintf(s, "%d", i);
//		glp_set_row_name(lp, i + index, (strc + s).c_str());
//		glp_set_row_bnds(lp, i + index, GLP_DB, 0.0, 1.0);
//	}
//
//	index += p_graph->edgeNum;
//
//	strc = "APsrlgnotcrossBPsrlg";
//	for (unsigned int i = 1; i <= srlgmulti; i++) {
//		sprintf(s, "%d", i);
//		glp_set_row_name(lp, i + index, (strc + s).c_str());
//		glp_set_row_bnds(lp, (i + index), GLP_DB, 0.0, 1.0);
//	}
//
//	int *ia, *ja;
//	double *ar;
//
//	ia = (int*) malloc(((rownum * colnum) + 2) * sizeof(int));
//	ja = (int*) malloc(((rownum * colnum) + 2) * sizeof(int));
//	ar = (double*) malloc(((rownum * colnum) + 2) * sizeof(double));
//	if (NULL == ia) {
//		printf("ERROR:there is enough storage for ia array\n");
//		exit(-1);
//	}
//	if (NULL == ja) {
//		printf("ERROR:there is enough storage for ja array\n");
//		exit(-1);
//	}
//	if (NULL == ar) {
//		printf("ERROR:there is enough storage for ar array\n");
//		exit(-1);
//	}
//
//	int jlimit1 = p_graph->edgeNum;
//	int jlimit2 = 2 * p_graph->edgeNum;
//
//	index = 1;
//	int indexrow = 0;
//
//	//AX1=u
//	for (int i = 0; i < p_graph->nodeNum; i++) {
//		for (int j = 0; j < colnum; j++) {
//			ia[index] = i + 1;
//			ja[index] = j + 1;
//			ar[index] = 0;
//			if (j < jlimit1) {
//				if (i == p_graph->getithEdge(j).from) {
//					ar[index] = 1;
//				}
//				if (i == p_graph->getithEdge(j).to) {
//					ar[index] = -1;
//				}
//			}
//			index++;
//		}
//	}
//	//AX2=u
//	indexrow += p_graph->nodeNum;
//	for (int i = 0; i < p_graph->nodeNum; i++) {
//		for (int j = 0; j < colnum; j++) {
//			ia[index] = i + 1 + indexrow;
//			ja[index] = j + 1;
//			ar[index] = 0;
//			if ((j < jlimit2) && (j >= jlimit1)) {
//				if (i == p_graph->getithEdge(j - jlimit1).from) {
//					ar[index] = 1;
//				}
//				if (i == p_graph->getithEdge(j - jlimit1).to) {
//					ar[index] = -1;
//				}
//			}
//			index++;
//		}
//	}
//	//0<=X1+X2<=1
//	indexrow += p_graph->nodeNum;
//	for (int i = 0; i < p_graph->edgeNum; i++) {
//		for (int j = 0; j < colnum; j++) {
//			ia[index] = i + 1 + indexrow;
//			ja[index] = j + 1;
//			ar[index] = 0;
//			if (j < jlimit1) {
//				if (i == j) {
//					ar[index] = 1;
//				}
//			}
//			if ((j < jlimit2) && (j >= jlimit1)) {
//				if (i == (j - jlimit1)) {
//					ar[index] = 1;
//				}
//			}
//			index++;
//		}
//	}
//
//	indexrow += p_graph->edgeNum;
//
//	//0<=APsrlge1+BPsrlge2<=1
//
//	for (unsigned int i = 0; i < p_graph->srlgGroupsNum; i++) {
//		unsigned srlglen = p_graph->srlgGroups.at(i).srlgMember.size();
//		for (unsigned j = 0; j < srlglen; j++) {
//			for (unsigned k = 0; k < srlglen; k++) {
//				indexrow++;
//				for (int l = 0; l < colnum; l++) {
//					ia[index] = indexrow;
//					ja[index] = l + 1;
//					ar[index] = 0;
//
//					if ((l < p_graph->edgeNum)
//							&& (l == p_graph->srlgGroups.at(i).srlgMember.at(j)))
//						ar[index] = 1;
//					if ((l >= p_graph->edgeNum)
//							&& ((l - p_graph->edgeNum)
//									== p_graph->srlgGroups.at(i).srlgMember.at(
//											k)))
//						ar[index] = 1;
//					index++;
//				}
//			}
//		}
//	}
//
//	glp_load_matrix(lp, (rownum * colnum), ia, ja, ar);
//	glp_simplex(lp, NULL);
////	glp_interior(lp,NULL);
//
//	if (!(GLP_OPT == glp_get_status(lp))) {
//		return false;
//	}
//
////	cout << "ILP value:" << glp_get_obj_val(lp) << endl;
////	cout << "glp get num rows" << glp_get_num_rows(lp) << endl;
////	cout << "glp_get_num_cols" << glp_get_num_cols(lp) << endl;
////	cout << " glp_get_num_nz" << glp_get_num_nz(lp) << endl;
//
//	bool SolutionIsInteger = true;
//
//	for (int i = 1; i <= colnum; i++) {
//		if (glp_get_col_prim(lp, i)) {
//			if (glp_get_col_prim(lp, i) != 1.0) {
//				SolutionIsInteger = false;
//			}
//		}
//	}
//	if (SolutionIsInteger == false)
//		return false;
//
//	vector<bool> apvis = vector<bool>(p_graph->edgeNum, false);
//	for (int i = 1; i <= jlimit1; i++) {
//		if (glp_get_col_prim(lp, i)) {
//			int id = i - 1;
//			apvis[id] = true;
//		}
//	}
//	vector<bool> bpvis = vector<bool>(p_graph->edgeNum, false);
//	for (int i = jlimit1 + 1; i <= jlimit2; i++) {
//		if (glp_get_col_prim(lp, i)) {
//			int id = i - 1 - jlimit1;
//			bpvis[id] = true;
//		}
//	}
//	RecordResult(p_graph, apvis, bpvis);
//
//	glp_delete_prob(lp);
//	free(ia);
//	free(ja);
//	free(ar);
//	if (INT_MAX == AlgorithmResult->APcostsum)
//		return false;
//	else
//		return true;
//}

bool IQPAlgorithm_gurobi(GraphTopo *p_graph, int type) {
	try {
		GRBEnv env = GRBEnv();
		GRBModel model = GRBModel(env);
		model.getEnv().set(GRB_IntParam_OutputFlag, 0);
		model.getEnv().set(GRB_DoubleParam_TimeLimit, LimitedTime);
		model.getEnv().set(GRB_IntParam_Threads,ThreadNum);
		// Create variables
		vector<GRBVar> APe = vector<GRBVar>((p_graph->edgeNum));
		vector<GRBVar> BPe = vector<GRBVar>((p_graph->edgeNum));
		char s[10000];
		string str;

		str = "AP";
		for (unsigned int i = 0; i < p_graph->edgeNum; i++) {
			sprintf(s, "%d", i);
			APe.at(i) = model.addVar(0.0, 1.0, 0.0, GRB_BINARY, (str + s));

		}
		str = "BP";
		for (unsigned int i = 0; i < p_graph->edgeNum; i++) {
			sprintf(s, "%d", i);
			BPe.at(i) = model.addVar(0.0, 1.0, 0.0, GRB_BINARY, (str + s));

		}

		// Integrate new variables

		model.update();

		GRBLinExpr obj = 0.0;
		if (type == algorithm_IQP) {
			for (unsigned int i = 0; i < p_graph->edgeNum; i++) {
				obj += (p_graph->getithEdge(i).cost * APe.at(i));

			}
		}

		if (type == algorithm_IQP_sum) {
			for (unsigned int i = 0; i < p_graph->edgeNum; i++) {
				obj += (p_graph->getithEdge(i).cost * APe.at(i));
				obj += (p_graph->getithEdge(i).cost * BPe.at(i));

			}

		}
		model.setObjective(obj, GRB_MINIMIZE);

		string strc;
		strc = "APconnect";
		for (unsigned int i = 0; i < p_graph->nodeNum; i++) {
			sprintf(s, "%d", i);
			GRBLinExpr con = 0.0;
			for (unsigned int j = 0; j < p_graph->edgeNum; j++) {
				if (i == p_graph->getithEdge(j).from)
					con += APe.at(j);
				if (i == p_graph->getithEdge(j).to)
					con += (-1 * APe.at(j));
			}
			if (i == p_graph->source)
				model.addConstr(con == 1, (strc + s));
			if (i == p_graph->destination)
				model.addConstr(con == (-1), (strc + s));
			if ((i != p_graph->source) && (i != p_graph->destination))
				model.addConstr(con == 0, (strc + s));

		}

		strc = "BPconnect";
		for (unsigned int i = 0; i < p_graph->nodeNum; i++) {
			sprintf(s, "%d", i);
			GRBLinExpr con = 0.0;
			for (unsigned int j = 0; j < p_graph->edgeNum; j++) {
				if (i == p_graph->getithEdge(j).from)
					con += BPe.at(j);
				if (i == p_graph->getithEdge(j).to)
					con += (-1 * BPe.at(j));
			}
			if (i == p_graph->source)
				model.addConstr(con == 1, (strc + s));
			if (i == p_graph->destination)
				model.addConstr(con == (-1), (strc + s));
			if ((i != p_graph->source) && (i != p_graph->destination))
				model.addConstr(con == 0, (strc + s));

		}

		strc = "EdgeDisjoint";
		for (unsigned int i = 0; i < p_graph->edgeNum; i++) {
			sprintf(s, "%d", i);
			GRBLinExpr con = 0.0;
			con += (BPe.at(i) + APe.at(i));
			model.addConstr(con <= 1, (strc + s));
		}

		strc = "SrlgDisjoint";
		for (int i = 0; i < p_graph->srlgGroupsNum; i++) {
			sprintf(s, "%d", i);
			GRBLinExpr APcon = 0.0;
			for (unsigned j = 0; j < APe.size(); j++) {
				if (i == p_graph->getithEdge(j).ithsrlg)
					APcon += APe.at(j);
			}
			GRBLinExpr BPcon = 0.0;
			for (unsigned j = 0; j < BPe.size(); j++) {
				if (i == p_graph->getithEdge(j).ithsrlg)
					BPcon += BPe.at(j);
			}

			model.addQConstr(BPcon * APcon == 0, (strc + s));
		}

		// Optimize model
		model.optimize();

		int optimstatus = model.get(GRB_IntAttr_Status);

		if (optimstatus != GRB_OPTIMAL) {
			return false;
		}
		bool SolutionIsInteger = true;

		for (unsigned i = 0; i < APe.size(); i++) {
			if (APe.at(i).get(GRB_DoubleAttr_X)) {
				if (APe.at(i).get(GRB_DoubleAttr_X) != 1.0) {
					SolutionIsInteger = false;
				}
			}
			if (BPe.at(i).get(GRB_DoubleAttr_X)) {
				if (BPe.at(i).get(GRB_DoubleAttr_X) != 1.0) {
					SolutionIsInteger = false;
				}
			}
		}
		if (SolutionIsInteger == false)
			return false;

		vector<bool> apvis = vector<bool>(p_graph->edgeNum, false);
		for (unsigned i = 0; i < APe.size(); i++) {
			if (APe.at(i).get(GRB_DoubleAttr_X)) {
				apvis[i] = true;
			}
		}
		vector<bool> bpvis = vector<bool>(p_graph->edgeNum, false);
		for (unsigned i = 0; i < BPe.size(); i++) {
			if (BPe.at(i).get(GRB_DoubleAttr_X)) {
				bpvis[i] = true;
			}
		}
		RecordResult(p_graph, apvis, bpvis);

		if (AlgorithmResult->SolutionNotFeasible)
			return false;
		else
			return true;

	} catch (GRBException e) {
		cout << "Error code = " << e.getErrorCode() << endl;
		cout << e.getMessage() << endl;
	} catch (...) {
		cout << "Exception during optimization" << endl;
	}
	return false;
}

bool ILPAlgorithm_gurobi(GraphTopo *p_graph, int type) {
	try {

		GRBEnv env = GRBEnv();
		GRBModel model = GRBModel(env);
		model.getEnv().set(GRB_IntParam_OutputFlag, 0);
//		model.getEnv().set(GRB_DoubleParam_TimeLimit, LimitedTime);
//		model.getEnv().set(GRB_IntParam_Threads,ThreadNum);
		// Create variables
		vector<GRBVar> APe = vector<GRBVar>((p_graph->edgeNum));
		vector<GRBVar> BPe = vector<GRBVar>((p_graph->edgeNum));
		char s1[10000];
		char s2[10000];
		string str;

		str = "AP";
		for (unsigned int i = 0; i < p_graph->edgeNum; i++) {
			sprintf(s1, "%d", i);
			APe.at(i) = model.addVar(0.0, 1.0, 0.0, GRB_BINARY, (str + s1));

		}
		str = "BP";
		for (unsigned int i = 0; i < p_graph->edgeNum; i++) {
			sprintf(s1, "%d", i);
			BPe.at(i) = model.addVar(0.0, 1.0, 0.0, GRB_BINARY, (str + s1));

		}

		// Integrate new variables

		model.update();

		GRBLinExpr obj = 0.0;

		if (type == algorithm_ILP) {
			for (unsigned int i = 0; i < p_graph->edgeNum; i++) {
				obj += (p_graph->getithEdge(i).cost * APe.at(i));

			}
		}

		if (type == algorithm_ILP_sum) {
			for (unsigned int i = 0; i < p_graph->edgeNum; i++) {
				obj += (p_graph->getithEdge(i).cost * APe.at(i));
				obj += (p_graph->getithEdge(i).cost * BPe.at(i));

			}

		}
		model.setObjective(obj, GRB_MINIMIZE);

		string strc;
		strc = "APconnect";
		for (unsigned int i = 0; i < p_graph->nodeNum; i++) {
			sprintf(s1, "%d", i);
			GRBLinExpr con = 0.0;
			for (unsigned int j = 0; j < p_graph->edgeNum; j++) {
				if (i == p_graph->getithEdge(j).from)
					con += APe.at(j);
				if (i == p_graph->getithEdge(j).to)
					con += (-1 * APe.at(j));
			}
			if (i == p_graph->source)
				model.addConstr(con == 1, (strc + s1));
			if (i == p_graph->destination)
				model.addConstr(con == (-1), (strc + s1));
			if ((i != p_graph->source) && (i != p_graph->destination))
				model.addConstr(con == 0, (strc + s1));

		}

		strc = "BPconnect";
		for (unsigned int i = 0; i < p_graph->nodeNum; i++) {
			sprintf(s1, "%d", i);
			GRBLinExpr con = 0.0;
			for (unsigned int j = 0; j < p_graph->edgeNum; j++) {
				if (i == p_graph->getithEdge(j).from)
					con += BPe.at(j);
				if (i == p_graph->getithEdge(j).to)
					con += (-1 * BPe.at(j));
			}
			if (i == p_graph->source)
				model.addConstr(con == 1, (strc + s1));
			if (i == p_graph->destination)
				model.addConstr(con == (-1), (strc + s1));
			if ((i != p_graph->source) && (i != p_graph->destination))
				model.addConstr(con == 0, (strc + s1));

		}

		strc = "EdgeDisjoint";
		for (unsigned int i = 0; i < p_graph->edgeNum; i++) {
			sprintf(s1, "%d", i);
			GRBLinExpr con = 0.0;
			con += (BPe.at(i) + APe.at(i));
			model.addConstr(con <= 1, (strc + s1));
		}

		strc = "SrlgDisjoint";
		for (int i = 0; i < p_graph->srlgGroupsNum; i++) {
			for (unsigned j = 0;
					j < p_graph->srlgGroups.at(i).srlgMember.size(); j++) {
				for (unsigned k = 0;
						k < p_graph->srlgGroups.at(i).srlgMember.size(); k++) {
					if(j==k)
						continue;
					sprintf(s1, "%d", j);
					sprintf(s2, "%d", k);
					GRBLinExpr con = 0.0;
					con = APe.at(p_graph->srlgGroups.at(i).srlgMember.at(j))
							+ BPe.at(
									p_graph->srlgGroups.at(i).srlgMember.at(k));
					model.addQConstr(con <= 1, (strc + s1 + "_" + s2));
				}
			}

		}

		// Optimize model
		model.optimize();

		int optimstatus = model.get(GRB_IntAttr_Status);

		if (optimstatus != GRB_OPTIMAL) {
			return false;
		}

		bool SolutionIsInteger = true;

		for (unsigned i = 0; i < APe.size(); i++) {
			if (APe.at(i).get(GRB_DoubleAttr_X)) {
				if (APe.at(i).get(GRB_DoubleAttr_X) != 1.0) {
					SolutionIsInteger = false;
				}
			}
			if (BPe.at(i).get(GRB_DoubleAttr_X)) {
				if (BPe.at(i).get(GRB_DoubleAttr_X) != 1.0) {
					SolutionIsInteger = false;
				}
			}
		}
		if (SolutionIsInteger == false)
			return false;

		vector<bool> apvis = vector<bool>(p_graph->edgeNum, false);
		for (unsigned i = 0; i < APe.size(); i++) {
			if (APe.at(i).get(GRB_DoubleAttr_X)) {
				apvis[i] = true;
			}
		}
		vector<bool> bpvis = vector<bool>(p_graph->edgeNum, false);
		for (unsigned i = 0; i < BPe.size(); i++) {
			if (BPe.at(i).get(GRB_DoubleAttr_X)) {
				bpvis[i] = true;
			}
		}
		RecordResult(p_graph, apvis, bpvis);

		if (AlgorithmResult->SolutionNotFeasible)
			return false;
		else
			return true;

	} catch (GRBException e) {
		cout << "Error code = " << e.getErrorCode() << endl;
		cout << e.getMessage() << endl;
	} catch (...) {
		cout << "Exception during optimization" << endl;
	}
	return false;
}
