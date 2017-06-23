/**
 * 
 */
package multipleknapsack;

import gurobi.*;

/**
 * @author franz
 *
 */
public class MulitpleKnapsack {
	public int itemNumber;
	public int knapsackNumber;
	public int matchingMatrix[][];

	public int unionKnapsackSize;
	public int ithKapsack2ithUnionKnapsack[];
	public int unionKnapsackCapacity[];
	public int capacityItem[];

	public MulitpleKnapsack(int row, int col, int unionKnapsackSize) {
		this.itemNumber = row;
		this.knapsackNumber = col;
		this.unionKnapsackSize = unionKnapsackSize;
		matchingMatrix = new int[row][col];
		ithKapsack2ithUnionKnapsack = new int[col];
		capacityItem = new int[row];
		unionKnapsackCapacity = new int[unionKnapsackSize];
	}

	public boolean optimalSoutionILP(int solution[]) throws GRBException {
		GRBEnv env = new GRBEnv("franztao");
		GRBModel model = new GRBModel(env);
		GRBVar varx[][];
		varx = new GRBVar[this.itemNumber][this.knapsackNumber];

		// Create variables
		for (int i = 0; i < this.itemNumber; i++) {
			for (int j = 0; j < this.knapsackNumber; j++) {
				if (Integer.MAX_VALUE != this.matchingMatrix[i][j])
					varx[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "r:" + i + " c:" + j);
				else

					varx[i][j] = model.addVar(0.0, 0.0, 0.0, GRB.BINARY, "r:" + i + " c:" + j);
			}
		}
		// Integrate new variables

		model.update();
		GRBLinExpr objexpr = new GRBLinExpr();
		// Set objective: minimize

		for (int i = 0; i < this.itemNumber; i++) {
			for (int j = 0; j < this.knapsackNumber; j++) {
				if (Integer.MAX_VALUE != this.matchingMatrix[i][j])
					objexpr.addTerm(this.matchingMatrix[i][j], varx[i][j]);
			}
		}
		// String path = System.getgetProperty("java.library.path");
		// System.out.println("Java path: " + path);
		model.setObjective(objexpr, GRB.MINIMIZE);
		// Add constraint
		for (int i = 0; i < this.itemNumber; i++) {
			GRBLinExpr conexpr1 = new GRBLinExpr();
			for (int j = 0; j < this.knapsackNumber; j++) {
				if (Integer.MAX_VALUE != this.matchingMatrix[i][j])
					conexpr1.addTerm(1.0, varx[i][j]);
			}
			model.addConstr(conexpr1, GRB.EQUAL, 1.0, "constraint_row =" + i);
		}
		GRBLinExpr conexpr1[] = new GRBLinExpr[this.unionKnapsackSize];

		for (int i = 0; i < this.unionKnapsackSize; i++) {
			conexpr1[i] = new GRBLinExpr();
		}
		for (int i = 0; i < this.knapsackNumber; i++) {
			for (int j = 0; j < this.itemNumber; j++) {
				if (Integer.MAX_VALUE != this.matchingMatrix[j][i])
					conexpr1[this.ithKapsack2ithUnionKnapsack[i]].addTerm(this.capacityItem[j], varx[j][i]);
			}
		}
		for (int i = 0; i < this.unionKnapsackSize; i++) {
			model.addConstr(conexpr1[i], GRB.LESS_EQUAL, this.unionKnapsackCapacity[i], "constraint_col <" + i);
		}

		model.optimize();
		int optimstatus = model.get(GRB.IntAttr.Status);
		if (optimstatus != GRB.OPTIMAL) {
			return false;
		}
		for (int i = 0; i < this.itemNumber; i++) {
			for (int j = 0; j < this.knapsackNumber; j++) {
				if (1.0 == varx[i][j].get(GRB.DoubleAttr.X)) {
					solution[i] = j;
					System.out.println(j);
				}
			}
		}
		model.dispose();
		env.dispose();
		return true;
	}

}
