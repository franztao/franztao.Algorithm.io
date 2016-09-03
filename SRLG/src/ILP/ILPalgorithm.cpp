#include "../head.h"
#include "gurobi_c++.h"
#include"glpk.h"
#include<vector>
#include "localsolver.h"
using namespace localsolver;
using namespace std;

extern DisjointPaths *AlgorithmResult;

bool ILPAlgorithm_glpk(Graph *p_graph) {
	char s[1000];
	string strc;

	glp_prob *lp;
	lp = glp_create_prob();
	glp_set_prob_name(lp, "sample");
	glp_set_obj_dir(lp, GLP_MIN);
	int srlgmulti = 0;
	for (unsigned i = 0; i < p_graph->srlgGroups.size(); i++) {
		srlgmulti += (p_graph->srlgGroups.at(i).srlgMember.size()
				* p_graph->srlgGroups.at(i).srlgMember.size());
	}
	int rownum = (2 * p_graph->nodeNum) + p_graph->edgeNum + srlgmulti;
	int colnum = (2 * p_graph->edgeNum);

	int index = 0;
	//AX1=u

	glp_add_cols(lp, colnum);
	//APedge
	strc = "APedge";
	for (int i = 1; i <= p_graph->edgeNum; i++) {
		sprintf(s, "%d", i);
		glp_set_col_name(lp, i + index, (strc + s).c_str());
//		glp_set_col_kind(lp, i + index, GLP_BV);
//		glp_set_col_kind(lp, i + index, GLP_IV);
		glp_set_col_bnds(lp, i + index, GLP_DB, 0.0, 1.0);
//		glp_set_col_stat(lp, i+ index,GLP_NF);
		glp_set_obj_coef(lp, i, p_graph->edges.at(i - 1).cost);

	}
	//BPedge
	index += p_graph->edgeNum;
	strc = "BPedge";
	for (int i = 1; i <= p_graph->edgeNum; i++) {
		sprintf(s, "%d", i);
		glp_set_col_name(lp, i + index, (strc + s).c_str());
//		glp_set_col_kind(lp, i + index, GLP_BV);
//		glp_set_col_kind(lp, i + index, GLP_IV);
		glp_set_col_bnds(lp, i + index, GLP_DB, 0.0, 1.0);
		glp_set_obj_coef(lp, i + index, 0.0);
//		glp_set_col_stat(lp, i+ index,GLP_NF);

	}

	index = 0;
	glp_add_rows(lp, rownum);
	strc = "APinoutdegrezero";
	for (int i = 1; i <= p_graph->nodeNum; i++) {
		sprintf(s, "%d", i);
		glp_set_row_name(lp, i, (strc + s).c_str());
		if ((i - 1) == p_graph->source)
			glp_set_row_bnds(lp, i, GLP_FX, 1.0, 1.0);
		if ((i - 1) == p_graph->destination)
			glp_set_row_bnds(lp, i, GLP_FX, -1.0, -1.0);
		if (((i - 1) != p_graph->source) && ((i - 1) != p_graph->destination))
			glp_set_row_bnds(lp, i, GLP_FX, 0.0, 0.0);
	}

	//AX2=u
	index += p_graph->nodeNum;
	strc = "BPinoutdegrezero";
	for (int i = 1; i <= p_graph->nodeNum; i++) {
		sprintf(s, "%d", i);
		glp_set_row_name(lp, i + index, (strc + s).c_str());
		if ((i - 1) == p_graph->source)
			glp_set_row_bnds(lp, i + index, GLP_FX, 1.0, 1.0);
		if ((i - 1) == p_graph->destination)
			glp_set_row_bnds(lp, i + index, GLP_FX, -1.0, -1.0);
		if (((i - 1) != p_graph->source) && ((i - 1) != p_graph->destination))
			glp_set_row_bnds(lp, i + index, GLP_FX, 0.0, 0.0);
	}

	//0<=X1+X2<=1
	index += p_graph->nodeNum;
	strc = "APnotcrossBP";
	for (int i = 1; i <= p_graph->edgeNum; i++) {
		sprintf(s, "%d", i);
		glp_set_row_name(lp, i + index, (strc + s).c_str());
		glp_set_row_bnds(lp, i + index, GLP_DB, 0.0, 1.0);
	}

	index += p_graph->edgeNum;

	strc = "APsrlgnotcrossBPsrlg";
	for (int i = 1; i <= srlgmulti; i++) {
		sprintf(s, "%d", i);
		glp_set_row_name(lp, i + index, (strc + s).c_str());
		glp_set_row_bnds(lp, (i + index), GLP_DB, 0.0, 1.0);
	}

	int *ia, *ja;
	double *ar;

	ia = (int*) malloc(((rownum * colnum) + 2) * sizeof(int));
	ja = (int*) malloc(((rownum * colnum) + 2) * sizeof(int));
	ar = (double*) malloc(((rownum * colnum) + 2) * sizeof(double));
	if (NULL == ia) {
		printf("ERROR:there is enough storage for ia array\n");
		exit(-1);
	}
	if (NULL == ja) {
		printf("ERROR:there is enough storage for ja array\n");
		exit(-1);
	}
	if (NULL == ar) {
		printf("ERROR:there is enough storage for ar array\n");
		exit(-1);
	}

	int jlimit1 = p_graph->edgeNum;
	int jlimit2 = 2 * p_graph->edgeNum;

	index = 1;
	int indexrow = 0;

	//AX1=u
	for (int i = 0; i < p_graph->nodeNum; i++) {
		for (int j = 0; j < colnum; j++) {
			ia[index] = i + 1;
			ja[index] = j + 1;
			ar[index] = 0;
			if (j < jlimit1) {
				if (i == p_graph->edges.at(j).from) {
					ar[index] = 1;
				}
				if (i == p_graph->edges.at(j).to) {
					ar[index] = -1;
				}
			}
			index++;
		}
	}
	//AX2=u
	indexrow += p_graph->nodeNum;
	for (int i = 0; i < p_graph->nodeNum; i++) {
		for (int j = 0; j < colnum; j++) {
			ia[index] = i + 1 + indexrow;
			ja[index] = j + 1;
			ar[index] = 0;
			if ((j < jlimit2) && (j >= jlimit1)) {
				if (i == p_graph->edges.at(j - jlimit1).from) {
					ar[index] = 1;
				}
				if (i == p_graph->edges.at(j - jlimit1).to) {
					ar[index] = -1;
				}
			}
			index++;
		}
	}
	//0<=X1+X2<=1
	indexrow += p_graph->nodeNum;
	for (int i = 0; i < p_graph->edgeNum; i++) {
		for (int j = 0; j < colnum; j++) {
			ia[index] = i + 1 + indexrow;
			ja[index] = j + 1;
			ar[index] = 0;
			if (j < jlimit1) {
				if (i == j) {
					ar[index] = 1;
				}
			}
			if ((j < jlimit2) && (j >= jlimit1)) {
				if (i == (j - jlimit1)) {
					ar[index] = 1;
				}
			}
			index++;
		}
	}

	indexrow += p_graph->edgeNum;

	//0<=APsrlge1+BPsrlge2<=1

	for (int i = 0; i < p_graph->srlgGroupsNum; i++) {
		unsigned srlglen = p_graph->srlgGroups.at(i).srlgMember.size();
		for (unsigned j = 0; j < srlglen; j++) {
			for (unsigned k = 0; k < srlglen; k++) {
				indexrow++;
				for (int l = 0; l < colnum; l++) {
					ia[index] = indexrow;
					ja[index] = l + 1;
					ar[index] = 0;

					if ((l < p_graph->edgeNum)
							&& (l == p_graph->srlgGroups.at(i).srlgMember.at(j)))
						ar[index] = 1;
					if ((l >= p_graph->edgeNum)
							&& ((l - p_graph->edgeNum)
									== p_graph->srlgGroups.at(i).srlgMember.at(
											k)))
						ar[index] = 1;
					index++;
				}
			}
		}
	}

	glp_load_matrix(lp, (rownum * colnum), ia, ja, ar);
	glp_simplex(lp, NULL);
//	glp_interior(lp,NULL);

	if (!(GLP_OPT == glp_get_status(lp))) {
		return false;
	}

//	cout << "ILP value:" << glp_get_obj_val(lp) << endl;
//	cout << "glp get num rows" << glp_get_num_rows(lp) << endl;
//	cout << "glp_get_num_cols" << glp_get_num_cols(lp) << endl;
//	cout << " glp_get_num_nz" << glp_get_num_nz(lp) << endl;

	bool SolutionIsInteger = true;

	for (int i = 1; i <= colnum; i++) {
		if (glp_get_col_prim(lp, i)) {
			if (glp_get_col_prim(lp, i) != 1.0) {
				SolutionIsInteger = false;
			}
		}
	}
	if (SolutionIsInteger == false)
		return false;

	AlgorithmResult->APcostsum = 0;
	int state = p_graph->source;
	vector<bool> apvis = vector<bool>(p_graph->edgeNum, false);
	for (int i = 1; i <= jlimit1; i++) {
		if (glp_get_col_prim(lp, i)) {
			int id = i - 1;
			apvis[id] = true;
		}
	}

	while (state != p_graph->destination) {
		for (int j = 0; j < p_graph->topo_Node_fEdgeList.at(state).edgeList.size(); j++) {
			int iedge = p_graph->topo_Node_fEdgeList.at(state).edgeList.at(j);
			if (apvis[iedge]) {
				state = p_graph->edges.at(iedge).to;
				AlgorithmResult->APnode.push_back(iedge);
				AlgorithmResult->APcostsum += p_graph->edges.at(iedge).cost;
				break;
			}
		}
	}

	state = p_graph->source;
	apvis = vector<bool>(p_graph->edgeNum, false);
	for (int i = jlimit1 + 1; i <= jlimit2; i++) {
		if (glp_get_col_prim(lp, i)) {
			int id = i - 1 - jlimit1;
			apvis[id] = true;
		}
	}

	while (state != p_graph->destination) {
		for (int j = 0; j < p_graph->topo_Node_fEdgeList.at(state).edgeList.size(); j++) {
			int iedge = p_graph->topo_Node_fEdgeList.at(state).edgeList.at(j);
			if (apvis[iedge]) {
				state = p_graph->edges.at(iedge).to;
				AlgorithmResult->BPnode.push_back(iedge);
				break;
			}
		}
	}

	glp_delete_prob(lp);
	free(ia);
	free(ja);
	free(ar);
	if (INT_MAX == AlgorithmResult->APcostsum)
		return false;
	else
		return true;
}

bool ILPAlgorithmBasicFlows_gurobi(Graph *p_graph) {
	try {

		GRBEnv env = GRBEnv();

		GRBModel model = GRBModel(env);

		// Create variables
		vector<GRBVar> APe = vector<GRBVar>((p_graph->edgeNum));
		vector<GRBVar> BPe = vector<GRBVar>((p_graph->edgeNum));
		char s[10000];
		string str;

		str = "AP";
		for (int i = 0; i < p_graph->edgeNum; i++) {
			sprintf(s, "%d", i);
			APe.at(i) = model.addVar(0.0, 1.0, 0.0, GRB_BINARY, (str + s));

		}
		str = "BP";
		for (int i = 0; i < p_graph->edgeNum; i++) {
			sprintf(s, "%d", i);
			BPe.at(i) = model.addVar(0.0, 1.0, 0.0, GRB_BINARY, (str + s));

		}

		// Integrate new variables

		model.update();

		GRBLinExpr obj = 0.0;
		for (int i = 0; i < p_graph->edgeNum; i++) {
			obj += (p_graph->edges.at(i).cost * APe.at(i));
		}
		model.setObjective(obj, GRB_MINIMIZE);

		string strc;
		strc = "APconnect";
		for (int i = 0; i < p_graph->nodeNum; i++) {
			sprintf(s, "%d", i);
			GRBLinExpr con = 0.0;
			for (int j = 0; j < p_graph->edgeNum; j++) {
				if (i == p_graph->edges.at(j).from)
					con += APe.at(j);
				if (i == p_graph->edges.at(j).to)
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
		for (int i = 0; i < p_graph->nodeNum; i++) {
			sprintf(s, "%d", i);
			GRBLinExpr con = 0.0;
			for (int j = 0; j < p_graph->edgeNum; j++) {
				if (i == p_graph->edges.at(j).from)
					con += BPe.at(j);
				if (i == p_graph->edges.at(j).to)
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
		for (int i = 0; i < p_graph->edgeNum; i++) {
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
				if (i == p_graph->edges.at(j).ithsrlg)
					APcon += APe.at(j);
			}
			GRBLinExpr BPcon = 0.0;
			for (unsigned j = 0; j < BPe.size(); j++) {
				if (i == p_graph->edges.at(j).ithsrlg)
					BPcon += BPe.at(j);
			}

			model.addQConstr(BPcon * APcon == 0, (strc + s));
		}

		// Optimize model

		model.optimize();
//		for (unsigned i = 0; i< APe.size(); i++) {
//			cout << APe.at(i).get(GRB_StringAttr_VarName) << " "
//					<< APe.at(i).get(GRB_DoubleAttr_X) << endl;
//		}
//		for (unsigned i = 0; i < APe.size(); i++) {
//			cout << APe.at(i).get(GRB_StringAttr_VarName) << " "
//					<< BPe.at(i).get(GRB_DoubleAttr_X) << endl;
//		}

		cout << "Obj: " << model.get(GRB_DoubleAttr_ObjVal) << endl;
		return true;
	} catch (GRBException e) {
		cout << "Error code = " << e.getErrorCode() << endl;
		cout << e.getMessage() << endl;
	} catch (...) {
		cout << "Exception during optimization" << endl;
	}
	return false;
}

bool ILPAlgorithmBasicFlows_LocalSolver(Graph *p_graph) {

	try {

		/* Declares the optimization model. */
		LocalSolver localsolver;
		LSModel model = localsolver.getModel();

		// 0-1 decisions
		int twocolnum = 2 * p_graph->edgeNum;
		int onecolnum = p_graph->edgeNum;
//		LSExpression *x = new LSExpression(2 * p_graph->edgeNum);
		vector<LSExpression> x; //=new vector<LSExpression>(2 * p_graph->edgeNum);
		x.resize(2 * p_graph->edgeNum);
//        x=(LSExpression *)malloc(2*p_graph->edgeNum*(sizeof(LSExpression)));
		for (int i = 0; i < twocolnum; i++)
			x[i] = model.boolVar();

		// knapsackWeight <- 10*x0 + 60*x1 + 30*x2 + 40*x3 + 30*x4 + 20*x5 + 20*x6 + 2*x7;

		for (int i = 0; i < p_graph->nodeNum; i++) {
			LSExpression knapsackWeight = model.sum();
			lsint knapsackBound;
			knapsackBound = 0;
			if (i == p_graph->source)
				knapsackBound = 1;
			if (i == p_graph->destination)
				knapsackBound = -1;
			for (int j = 0; j < onecolnum; j++) {
				if (i == p_graph->edges.at(j).from)
					knapsackWeight += x[j];
				if (i == p_graph->edges.at(j).to)
					knapsackWeight += (-1 * x[j]);
			}
			model.constraint(knapsackWeight <= knapsackBound);
		}

		for (int i = 0; i < p_graph->nodeNum; i++) {
			LSExpression knapsackWeight = model.sum();
			lsint knapsackBound;
			knapsackBound = 0;
			if (i == p_graph->source)
				knapsackBound = 1;
			if (i == p_graph->destination)
				knapsackBound = -1;
			for (int j = onecolnum; j < twocolnum; j++) {
				if (i == p_graph->edges.at(j - onecolnum).from)
					knapsackWeight += x[j];
				if (i == p_graph->edges.at(j - onecolnum).to)
					knapsackWeight += (-1 * x[j]);
			}
			model.constraint(knapsackWeight <= knapsackBound);
		}

		for (int i = 0; i < onecolnum; i++) {
			LSExpression knapsackWeight = model.sum();
			lsint knapsackBound;
			knapsackBound = 1;

			knapsackWeight += x[i];
			knapsackWeight += x[i + onecolnum];

			model.constraint(knapsackWeight <= knapsackBound);
		}

		for (int i = 0; i < p_graph->srlgGroupsNum; i++) {
			unsigned srlglen = p_graph->srlgGroups.at(i).srlgMember.size();
			for (unsigned j = 0; j < srlglen; j++) {
				for (unsigned k = 0; k < srlglen; k++) {
					LSExpression knapsackWeight = model.sum();
					lsint knapsackBound;
					knapsackBound = 1;
					knapsackWeight += x[p_graph->srlgGroups.at(i).srlgMember.at(
							j)];
					knapsackWeight += x[p_graph->srlgGroups.at(i).srlgMember.at(
							k)];
					model.constraint(knapsackWeight <= knapsackBound);
				}
			}
		}

		LSExpression obj = model.sum();
		for (int i = 0; i < onecolnum; i++) {
//			if (i < p_graph->edgeNum)
			obj += p_graph->edges.at(i).cost * x[i];
//			else
//				obj += p_graph->edges.at(i - p_graph->edgeNum).cost * x[i];
		}

		// minimize obj;
		model.minimize(obj);

		// close model, then solve
		model.close();

		/* Parameterizes the solver. */
		LSPhase phase = localsolver.createPhase();
		LSSolution solution = localsolver.getSolution();
		phase.setTimeLimit(100);
		phase.setEnabled(true);
//		localsolver.LSSolution();
//		phase.setOptimizedObjective(SS_Optimal);

		localsolver.solve();
//		localsolver.
		for (int i = 0; i < onecolnum; i++) {
			if (1 == x[i].getValue())
				cout << i << "  " << p_graph->edges.at(i).cost << endl;
		}
		for (int i = 0; i < onecolnum; i++) {
			if (1 == x[i].getValue()) {
				cout << p_graph->nid_nindex[p_graph->edges.at(i).from] << endl;
			}
		}

		cout << "___________________________" << endl;
		for (int i = onecolnum; i < twocolnum; i++) {
			if (1 == x[i].getValue()) {
				cout
						<< p_graph->nid_nindex[p_graph->edges.at(i - onecolnum).from]
						<< endl;
			}
		}
	} catch (const LSException& e) {
		cout << "LSException occurred:" << e.getMessage() << endl;
	}

	return false;
}
