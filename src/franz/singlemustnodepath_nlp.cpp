/*
 * franz_nlp.cpp
 *
 *  Created on: Jul 26, 2016
 *      Author: franz
 */

#include "../franz/singlemustnodepath_nlp.hpp"

#include <cassert>
#include <iostream>

using namespace Ipopt;

// constructor
franz_NLP::franz_NLP(Graph *p_graph, Request *p_request) {
	this->p_graph = p_graph;
	this->p_request = p_request;
}

//destructor
franz_NLP::~franz_NLP() {
}

// returns the size of the problem
bool franz_NLP::get_nlp_info(Index& n, Index& m, Index& nnz_jac_g,
		Index& nnz_h_lag, IndexStyleEnum& index_style) {
	// The problem described in franz_NLP.hpp has edgenum variables, x[0] through x[edgenum]
	n = this->p_graph->edgeNum;

	// one equality constraint and one inequality constraint
	m = 2 * this->p_graph->nodeNum;
	int sum = 0;
	// in this example the jacobian is dense and contains sum nonzeros
	for (Index i = 0; i < (m / 2); i++) {
		for (Index j = 0; j < n; j++) {
			if ((i == p_graph->edges.at(j).from)
					|| (i == p_graph->edges.at(j).to))
				sum += 2;
		}
	}
	nnz_jac_g = sum;

	// the hessian is also dense and has 0 total nonzeros, but we
	// only need the lower left corner (since it is symmetric)
	nnz_h_lag = 0;

	// use the C style indexing (0-based)
	index_style = TNLP::C_STYLE;

	return true;
}

// returns the variable bounds
bool franz_NLP::get_bounds_info(Index n, Number* x_l, Number* x_u, Index m,
		Number* g_l, Number* g_u) {
	// here, the n and m we gave IPOPT in get_nlp_info are passed back to us.
	// If desired, we could assert to make sure they are what we think they are.
	assert(n == this->p_graph->edgeNum);
	assert(m == (2 * this->p_graph->nodeNum));

	// the variables have lower bounds of 0
	// the variables have upper bounds of 1
	for (Index i = 0; i < n; i++) {
		x_l[i] = 0.0;
		x_u[i] = 1.0;

		if (!p_request->APMustPassEdges.at(i)) {
			x_l[i] = 1.0;
			x_u[i] = 1.0;

		}
		if (!p_request->APMustNotPassEdges.at(i)) {
			x_l[i] = 0.0;
			x_u[i] = 0.0;

		}
	}

	// Ipopt interprets any number greater than nlp_upper_bound_inf as
	// infinity. The default value of nlp_upper_bound_inf and nlp_lower_bound_inf
	// is 1e19 and can be changed through ipopt options.
	for (Index i = 0; i < m; i++) {
		if (i < (m / 2)) {
			g_l[i] = g_u[i] = 0.0;
			if (i == p_graph->source){

				g_l[i] = g_u[i] = 1.0;
			}
			if (i == p_graph->destination){
				g_l[i] = g_u[i] = -1.0;
			}
		} else {
			g_l[i] = 0.0;
			g_u[i] = 2.0;
		}

	}
	return true;
}

// returns the initial point for the problem
bool franz_NLP::get_starting_point(Index n, bool init_x, Number* x, bool init_z,
		Number* z_L, Number* z_U, Index m, bool init_lambda, Number* lambda) {
	// Here, we assume we only have starting values for x, if you code
	// your own NLP, you can provide starting values for the dual variables
	// if you wish
	assert(init_x == true);  //true
	assert(init_z == false);
	assert(init_lambda == false);

	// initialize to the given starting point
	for (int i = 0; i < p_graph->edgeNum; i++) {
		x[i] = 0.0;
	}
	x[0] = x[3] = x[5] = x[9] = x[10] = x[11] = x[14] = x[16] = x[19] = x[20] =
			x[21] = 1.0;
//  x[0] = 1.0;
//  x[1] = 5.0;
//  x[2] = 5.0;
//  x[3] = 1.0;

	return true;
}

// returns the value of the objective function
bool franz_NLP::eval_f(Index n, const Number* x, bool new_x,
		Number& obj_value) {
	assert(n == this->p_graph->edgeNum);
	obj_value=0;
	for (Index i = 0; i < n; i++) {
//		std::cout<<this->p_graph->edges.at(i).cost<<"FFFFFFFFF\nFFFFFFF\n"<<endl;
		if(this->p_graph->edges.at(i).cost!=0)
		obj_value += (this->p_graph->edges.at(i).cost * x[i]);
	}

	return true;
}

// return the gradient of the objective function grad_{x} f(x)
bool franz_NLP::eval_grad_f(Index n, const Number* x, bool new_x,
		Number* grad_f) {
	assert(n == this->p_graph->edgeNum);
	for (Index i = 0; i < n; i++) {
		grad_f[i] = this->p_graph->edges.at(i).cost;
	}
	return true;
}

// return the value of the constraints: g(x)
bool franz_NLP::eval_g(Index n, const Number* x, bool new_x, Index m,
		Number* g) {
	assert(n == this->p_graph->edgeNum);
	assert(m == (2 * this->p_graph->nodeNum));

	int bi = m / 2;
	for (Index i = 0; i < bi; i++) {
		//g[i] = 0;
		for (Index j = 0; j < n; j++) {
			if (i == p_graph->edges.at(j).from)
				g[i] += x[j];
			if (i == p_graph->edges.at(j).to)
				g[i] += (-1 * x[j]);

		}
	}
	for (Index i = 0; i < bi; i++) {
		//g[i + bi] = 0;
		for (Index j = 0; j < n; j++) {
			if ((i == p_graph->edges.at(j).from)
					|| (i == p_graph->edges.at(j).to))
				g[i + bi] += x[j];
		}
	}
	return true;
}

// return the structure or values of the jacobian
bool franz_NLP::eval_jac_g(Index n, const Number* x, bool new_x, Index m,
		Index nele_jac, Index* iRow, Index *jCol, Number* values) {
	if (values == NULL) {
		// return the structure of the jacobian

		// this particular jacobian is dense
		Index s = 0;
		int bi = m / 2;
		for (Index i = 0; i < m; i++) {
			for (Index j = 0; j < n; j++) {
				if (i < bi) {
					if ((i == p_graph->edges.at(j).from)
							|| (i == p_graph->edges.at(j).to)) {
						iRow[s] = i+1;
						iRow[s] = j+1;
						s++;
					}
				} else {
					if (((i - bi) == p_graph->edges.at(j).from)
							|| ((i - bi) == p_graph->edges.at(j).to)) {
						iRow[s] = i+1;
						iRow[s] = j+1;
						s++;
					}
				}
			}
		}
	} else {
		// return the values of the jacobian of the constraints
		Index s = 0;
		for (Index i = 0; i < (m / 2); i++) {
			for (Index j = 0; j < n; j++) {
				if ((i == p_graph->edges.at(j).from)) {
					values[s] = 1.0;
					s++;
				}
				if (i == p_graph->edges.at(j).to) {
					values[s] = -1.0;
					s++;
				}
			}
		}
		for (Index i = 0; i < (m / 2); i++) {
			for (Index j = 0; j < n; j++) {
				if ((i == p_graph->edges.at(j).from)
						|| (i == p_graph->edges.at(j).to)) {
					values[s] = 1.0;
					s++;
				}
			}
		}
	}

	return true;
}

//return the structure or values of the hessian
bool franz_NLP::eval_h(Index n, const Number* x, bool new_x, Number obj_factor,
		Index m, const Number* lambda, bool new_lambda, Index nele_hess,
		Index* iRow, Index* jCol, Number* values) {
	if (values == NULL) {
		// return the structure. This is a symmetric matrix, fill the lower left
		// triangle only.

		// the hessian for this problem is actually dense
		Index idx = 0;
//		for (Index row = 0; row < n; row++) {
//			for (Index col = 0; col <= row; col++) {
//				iRow[idx] = row;
//				jCol[idx] = col;
//				idx++;
//			}
//		}
		assert(idx == nele_hess);
	} else {
		// return the values. This is a symmetric matrix, fill the lower left
		// triangle only

		// fill the objective portion
//		Index idx = 0;
//		for (Index row = 0; row < n; row++) {
//			for (Index col = 0; col <= row; col++) {
//				values[idx] = 0.;
//			}
//		}

	}

	return true;
}

void franz_NLP::finalize_solution(SolverReturn status, Index n, const Number* x,
		const Number* z_L, const Number* z_U, Index m, const Number* g,
		const Number* lambda, Number obj_value, const IpoptData* ip_data,
		IpoptCalculatedQuantities* ip_cq) {
// here is where we would store the solution to variables, or write to a file, etc
// so we could use the solution.

// For this example, we write the solution to the console
	std::cout << std::endl << std::endl << "Solution of the primal variables, x"
			<< std::endl;
	for (Index i = 0; i < n; i++) {
		std::cout << "x[" << i << "] = " << x[i] << std::endl;
		p_request->BPMustNotPassEdges4AP[i] = false;
	}

	std::cout << std::endl << std::endl
			<< "Solution of the bound multipliers, z_L and z_U" << std::endl;
	for (Index i = 0; i < n; i++) {
		std::cout << "z_L[" << i << "] = " << z_L[i] << std::endl;
	}
	for (Index i = 0; i < n; i++) {
		std::cout << "z_U[" << i << "] = " << z_U[i] << std::endl;
	}

	std::cout << std::endl << std::endl << "Objective value" << std::endl;
	std::cout << "f(x*) = " << obj_value << std::endl;

	std::cout << std::endl << "Final value of the constraints:" << std::endl;
	for (Index i = 0; i < m; i++) {
		std::cout << "g(" << i << ") = " << g[i] << std::endl;
	}
}

