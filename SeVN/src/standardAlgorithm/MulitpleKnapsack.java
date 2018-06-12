/**
 * 
 */
package standardAlgorithm;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import gurobi.*;
import sevn.Parameter;

/**
 * @author franz
 *
 */
public class MulitpleKnapsack
{
    public int itemNum;
    public int knapsackNum;
    public int matchMatrix[][];

    public int unionKnapsackSize;
    public int ithKapsack2ithUnionKnapsack[];
    public int unionKnapsackCapacity[];
    public int capacityItem[];

    private Logger loggerMulitpleKnapsack = Logger.getLogger(MulitpleKnapsack.class);

    /**
     * MulitpleKnapsack.
     * 
     * @param row
     *            row
     * @param col
     *            col
     * @param unionKnapsackSize
     *            unionKnapsackSize
     */
    public MulitpleKnapsack(int row, int col, int unionKnapsackSize)
    {
        PropertyConfigurator.configure("log4j.properties");

        this.itemNum = row;
        this.knapsackNum = col;
        this.unionKnapsackSize = unionKnapsackSize;
        matchMatrix = new int[row][col];
        ithKapsack2ithUnionKnapsack = new int[col];
        capacityItem = new int[row];
        unionKnapsackCapacity = new int[unionKnapsackSize];
    }

    /**
     * optimalSoutionDP.
     * 
     * @param solution
     *            solution
     * @return int
     */
    public int optimalSoutionDP(int solution[])
    {

        // int[] dims=new int[this.unionKnapsackSize+1];
        // dims[0]=this.itemNumber+1;
        // for(int i=0;i<this.unionKnapsackSize;i++){
        // dims[i+1]=this.unionKnapsackCapacity[i]+1;
        // }
        // Object dp=Array.newInstance(Integer.TYPE, dims);
        // System.out.println(dp.getClass().getName());
        // System.out.println(Array.getLength(dp));

        int dp[][];
        int select[][];
        int dimensionMultiple = 1;

        int radix[] = new int[this.unionKnapsackSize];
        for (int i = this.unionKnapsackSize - 1, j = 0; i >= 0; i--)
        {
            dimensionMultiple *= (this.unionKnapsackCapacity[i] + 1);
            radix[this.unionKnapsackSize - 1 - j] = dimensionMultiple;
            j++;
        }
        for (int i = 0; i < this.unionKnapsackSize - 1; i++)
        {
            radix[i] = radix[i + 1];
        }
        radix[this.unionKnapsackSize - 1] = 1;

        dp = new int[this.itemNum + 1][dimensionMultiple];
        select = new int[this.itemNum + 1][dimensionMultiple];
        // initial variable
        for (int i = 1; i <= this.itemNum; i++)
        {
            for (int j = 0; j < dimensionMultiple; j++)
            {
                dp[i][j] = -1;
            }
        }
        for (int i = 1; i <= this.itemNum; i++)
        {
            int dims[] = new int[this.unionKnapsackSize];
            int d;
            for (int j = 0; j < dimensionMultiple; j++)
            {
                for (int k = 0; k < this.knapsackNum; k++)
                {
                    if (this.matchMatrix[i - 1][k] != Integer.MAX_VALUE)
                    {
                        int ithunionbag = this.ithKapsack2ithUnionKnapsack[k];
                        if ((dims[ithunionbag] - this.capacityItem[i - 1]) > 0)
                        {
                            int newinteger = 0;
                            for (int l = 0; l < this.unionKnapsackSize; l++)
                            {
                                if (l == ithunionbag)
                                {
                                    newinteger += (radix[l] * (dims[l] - this.capacityItem[i - 1]));
                                } else
                                    newinteger += (radix[l] * dims[l]);
                            }
                            if (dp[i - 1][newinteger] != -1)
                            {
                                if (dp[i][j] == -1)
                                {
                                    dp[i][j] = dp[i - 1][newinteger] + this.matchMatrix[i - 1][k];
                                    select[i][j] = ithunionbag;
                                } else
                                {
                                    // dp[i][j] = Math.min(dp[i][j],
                                    // dp[i - 1][newinteger] +
                                    // this.matchingMatrix[i - 1][k]);
                                    if (dp[i][j] > (dp[i - 1][newinteger] + this.matchMatrix[i - 1][k]))
                                    {
                                        dp[i][j] = dp[i - 1][newinteger] + this.matchMatrix[i - 1][k];
                                        select[i][j] = ithunionbag;
                                    }
                                }
                            }

                        }
                    }
                }
                d = this.unionKnapsackSize - 1;
                dims[d]++;
                //
                // for (int t = 0; t < this.unionKnapsackSize; t++) {
                // System.out.print(dims[t]+ " ");
                // }
                // System.out.println();

                while (dims[d] == (this.unionKnapsackCapacity[d] + 1))
                {
                    dims[d] -= (this.unionKnapsackCapacity[d] + 1);
                    if (d != 0)
                        dims[d - 1]++;
                    else
                        break;
                    d--;
                }
            }
        }

        if (dp[this.itemNum][dimensionMultiple - 1] != -1)
        {
            int index = this.itemNum;
            int recurse = dimensionMultiple - 1;
            while (index > 0)
            {
                solution[index - 1] = select[index][recurse];
                recurse -= (radix[select[index][recurse]] * this.capacityItem[index - 1]);
                index--;
            }
            // for(int i=0;i< this.itemNumber;i++){
            // System.out.println("+++"+solution[i]);
            // }
            loggerMulitpleKnapsack.info("----DP optimal solution: (" + dp[this.itemNum][dimensionMultiple - 1] + ") ");
            return dp[this.itemNum][dimensionMultiple - 1];
        } else
        {
            loggerMulitpleKnapsack.warn("----DP exist no optimal solution");
            return -1;
        }
    }

    /**
     * optimalSoutionIntegerPrograming.
     * 
     * @param ithItem2ithKnapsack
     *            ithItem2ithKnapsack
     * @return int
     * @throws GRBException
     *             GRBException
     */
    public int optimalSoutionIntegerPrograming(int[] ithItem2ithKnapsack) throws GRBException
    {
        GRBEnv env = new GRBEnv();
        GRBModel model = null;
        model = new GRBModel(env);

        // close gurobi default system console log
        model.getEnv().set(GRB.IntParam.OutputFlag, 0);

        GRBVar[][] varX;
        varX = new GRBVar[this.itemNum][this.knapsackNum];

        // Create variables
        for (int i = 0; i < this.itemNum; i++)
        {
            for (int j = 0; j < this.knapsackNum; j++)
            {
                if (Integer.MAX_VALUE != this.matchMatrix[i][j])
                {
                    varX[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "r:" + i + " c:" + j);
                } else
                {
                    varX[i][j] = model.addVar(0.0, 0.0, 0.0, GRB.BINARY, "r:" + i + " c:" + j);
                }
            }
        }
        // Integrate new variables
        model.update();

        // Set objective: minimize
        GRBLinExpr objexpr = new GRBLinExpr();
        for (int i = 0; i < this.itemNum; i++)
        {
            for (int j = 0; j < this.knapsackNum; j++)
            {
                if (Integer.MAX_VALUE != this.matchMatrix[i][j])
                {
                    objexpr.addTerm(this.matchMatrix[i][j], varX[i][j]);
                }
            }
        }
        model.setObjective(objexpr, GRB.MINIMIZE);

        // Add constraint
        // row=1
        for (int i = 0; i < this.itemNum; i++)
        {
            GRBLinExpr conexpr1 = new GRBLinExpr();
            for (int j = 0; j < this.knapsackNum; j++)
            {
                if (Integer.MAX_VALUE != this.matchMatrix[i][j])
                {
                    conexpr1.addTerm(1.0, varX[i][j]);
                }
            }
            model.addConstr(conexpr1, GRB.EQUAL, 1.0, "constraint_row =" + i);
        }

        if (!Parameter.IsMultipleVirutalNodeMapOnePhysicalNode)
        {
            for (int j = 0; j < this.knapsackNum; j++)
            {
                GRBLinExpr conexpr1 = new GRBLinExpr();
                for (int i = 0; i < this.itemNum; i++)
                {
                    if (Integer.MAX_VALUE != this.matchMatrix[i][j])
                    {
                        conexpr1.addTerm(1.0, varX[i][j]);
                    }
                }
                model.addConstr(conexpr1, GRB.LESS_EQUAL, 1.0, "constraint_col =" + j);
            }
        }

        GRBLinExpr[] conexpr1 = new GRBLinExpr[this.unionKnapsackSize];
        for (int i = 0; i < this.unionKnapsackSize; i++)
        {
            conexpr1[i] = new GRBLinExpr();
        }
        for (int j = 0; j < this.knapsackNum; j++)
        {
            for (int i = 0; i < this.itemNum; i++)
            {
                if (Integer.MAX_VALUE != this.matchMatrix[i][j])
                {
                    conexpr1[this.ithKapsack2ithUnionKnapsack[j]].addTerm(this.capacityItem[i], varX[i][j]);
                }
            }
        }

        for (int i = 0; i < this.unionKnapsackSize; i++)
        {
            model.addConstr(conexpr1[i], GRB.LESS_EQUAL, this.unionKnapsackCapacity[i], "constraint_col <" + i);
        }

        model.optimize();
        if (model.get(GRB.IntAttr.Status) != GRB.OPTIMAL)
        {
            return -1;
        }
        // loggerMulitpleKnapsack.info("ILP optimal solution: (" +
        // model.get(GRB.DoubleAttr.ObjVal) + " )");
        for (int i = 0; i < this.itemNum; i++)
        {
            for (int j = 0; j < this.knapsackNum; j++)
            {
                if (1.0 == varX[i][j].get(GRB.DoubleAttr.X))
                {
                    ithItem2ithKnapsack[i] = j;
                }
            }
        }
        int optimalValue = new Double(objexpr.getValue()).intValue();

        model.dispose();
        env.dispose();

        return optimalValue;
    }

}
