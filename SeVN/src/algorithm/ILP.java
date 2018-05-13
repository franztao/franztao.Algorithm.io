/**
 * 
 */
package algorithm;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBQuadExpr;
import gurobi.GRBVar;
import sevn.Parameter;
import survivabelVirtualNetwork.SurvivalVirtualNetwork;

/**
 * @author Taoheng
 *
 */
public class ILP
{
    /**
     * exactAlgorithmIP4SeVirNet.
     * 
     * @param alg
     * 
     * @return
     * 
     */
    public boolean exactAlgorithmIntegerProgram4SeVN(SeVNAlgorithm alg, SurvivalVirtualNetwork sn)
    {
        try
        {
            GRBEnv env = new GRBEnv();
            GRBModel model = new GRBModel(env);
            model.getEnv().set(GRB.IntParam.OutputFlag, 0);
            // Create variables
            GRBVar[][][] transformMatrix;
            transformMatrix = new GRBVar[sn.nodeSize4Failure + 1][sn.virNet.nodeSize][sn.nodeSize];
            for (int i = 0; i <= sn.nodeSize4Failure; i++)
            {
                for (int j = 0; j < sn.virNet.nodeSize; j++)
                {
                    for (int k = 0; k < sn.nodeSize; k++)
                    {
                        if (0 == i)
                        {
                            if (k == sn.virNode2surNode[j])
                            {
                                transformMatrix[i][j][k] = model.addVar(1.0, 1.0, 0.0, GRB.CONTINUOUS,
                                        "T" + i + " r:" + j + " c:" + k);
                            } else
                            {
                                transformMatrix[i][j][k] = model.addVar(0.0, 0.0, 0.0, GRB.CONTINUOUS,
                                        "T" + i + " r:" + j + " c:" + k);
                            }
                        } else
                        {
                            // 3
                            if ((i - 1) == k)
                            {
                                transformMatrix[i][j][k] = model.addVar(0.0, 0.0, 0.0, GRB.CONTINUOUS,
                                        "T" + i + " r:" + j + " c:" + k);
                            } else
                            {
                                transformMatrix[i][j][k] = model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS,
                                        "T" + i + " r:" + j + " c:" + k);
                            }
                        }
                    }
                }
            }

            // 1
            GRBVar[][] survivalGraphBandwithMatrix;
            survivalGraphBandwithMatrix = new GRBVar[sn.nodeSize][sn.nodeSize];
            for (int j = 0; j < sn.nodeSize; j++)
            {
                for (int k = 0; k < sn.nodeSize; k++)
                {
                    survivalGraphBandwithMatrix[j][k] = model.addVar(0.0, Parameter.SubStrateEdgeBandwithMaximum, 0.0,
                            GRB.INTEGER, "GB" + " r:" + j + " c:" + k);
                }
            }

            // 2
            GRBVar[] survivalGraphComputationMatrix;
            survivalGraphComputationMatrix = new GRBVar[sn.nodeSize];
            for (int j = 0; j < sn.nodeSize; j++)
            {
                survivalGraphComputationMatrix[j] = model.addVar(0.0, sn.nodeComputationCapacity[j], 0.0, GRB.INTEGER,
                        "GC" + " r:" + j);
            }

            GRBVar[] activeNodeVector;
            activeNodeVector = new GRBVar[sn.nodeSize];
            for (int j = 0; j < sn.nodeSize; j++)
            {
                if (j < sn.nodeSize4Failure)
                {
                    activeNodeVector[j] = model.addVar(1.0, 1.0, 0.0, GRB.CONTINUOUS, "activeNode" + ": " + j);
                } else
                {
                    activeNodeVector[j] = model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, "activeNode" + ": " + j);
                }
            }

            // Integrate new variables
            model.update();
            // set objecion function
            GRBLinExpr objexpr = new GRBLinExpr();
            for (int i = 0; i < sn.nodeSize; i++)
            {
                objexpr.addTerm(Parameter.addNewVirNodeCost, activeNodeVector[i]);
                objexpr.addTerm(Parameter.addNodeComputaionCost, survivalGraphComputationMatrix[i]);
                for (int j = 0; j < sn.nodeSize; j++)
                {
                    objexpr.addTerm(Parameter.addEdgeBandwithCost, survivalGraphBandwithMatrix[i][j]);
                }
            }
            model.setObjective(objexpr, GRB.MINIMIZE);

            // 1
            for (int i = 0; i <= sn.nodeSize4Failure; i++)
            {
                for (int j = 0; j < sn.nodeSize; j++)
                {
                    GRBLinExpr conexpr = new GRBLinExpr();
                    for (int k = 0; k < sn.virNet.nodeSize; k++)
                    {
                        conexpr.addTerm(sn.virNet.nodeComputationDemand[k], transformMatrix[i][k][j]);
                    }
                    model.addConstr(conexpr, GRB.LESS_EQUAL, survivalGraphComputationMatrix[j],
                            "MAC*T" + "T " + i + "r " + j);
                }
            }

            // (MAG*T)'*T<MBG
            //
            for (int l = 0; l <= sn.nodeSize4Failure; l++)
            {
                for (int i = 0; i < sn.nodeSize4Failure; i++)
                {
                    for (int j = 0; j < sn.nodeSize; j++)
                    {
                        GRBQuadExpr conexprL = new GRBQuadExpr();
                        for (int k = 0; k < sn.nodeSize; k++)
                        {
                            if (k < j)
                            {
                                conexprL.addTerm(1.0, transformMatrix[l][i][k], survivalGraphBandwithMatrix[j][k]);
                            } else
                            {
                                conexprL.addTerm(1.0, transformMatrix[l][i][k], survivalGraphBandwithMatrix[k][j]);

                            }
                        }
                        GRBQuadExpr conexprR = new GRBQuadExpr();
                        for (int t = 0; t < sn.virNet.nodeSize; t++)
                        {
                            conexprR.addTerm(sn.virNet.edgeBandwithDemand[t][i], transformMatrix[l][t][j]);
                        }
                        model.addQConstr(conexprR, GRB.LESS_EQUAL, conexprL,
                                "T " + l + "T'*MAG'*T" + "r " + i + " c " + j);
                    }
                }
            }

            for (int k = 0; k < sn.nodeSize; k++)
            {
                for (int j = k + 1; j < sn.nodeSize; j++)
                {
                    model.addConstr(survivalGraphBandwithMatrix[j][k], GRB.EQUAL, survivalGraphBandwithMatrix[k][j],
                            "Equl B r:" + k + " j:" + j);
                }
            }

            // for (int i = 0; i <= this.nodeSize4Failure; i++) {
            // // T'*MAG'
            // GRBQuadExpr[][] tmag = new GRBQuadExpr[this.nodeSize][this.nodeSize];
            // for (int j = 0; j < this.nodeSize; j++) {
            // for (int t = 0; t < this.nodeSize; t++) {
            // tmag[j][t] = new GRBQuadExpr();
            // for (int k = 0; k < this.virNet.nodeSize; k++) {
            // for (int l = 0; l < this.virNet.nodeSize; l++) {
            // // TMAG[j][k].addTerm(this.VNR.edgeBandwithDemand[k][l],
            // // TransfromMatrix[i][l][j]);
            // for (int p = 0; p < this.virNet.nodeSize; p++) {
            // Integer inte = this.virNet.edgeBandwithDemand[k][p];
            // tmag[j][t].addTerm(inte.doubleValue(), transformMatrix[i][k][j],
            // transformMatrix[i][p][t]);
            // }
            // }
            // }
            // model.addQConstr(tmag[j][t], GRB.LESS_EQUAL,
            // survivalGraphBandwithMatrix[j][t],
            // "T " + i + "T'*MAG'*T" + "r " + j + " c " + t);
            // }
            // }
            // }

            // 3
            for (int i = 0; i < sn.nodeSize4Failure; i++)
            {
                for (int j = 0; j < sn.nodeSize4Failure; j++)
                {
                    GRBLinExpr conexpr = new GRBLinExpr();
                    conexpr.addTerm(1.0, survivalGraphBandwithMatrix[i][j]);
                    model.addConstr(conexpr, GRB.GREATER_EQUAL, sn.virNet.edgeBandwithDemand[i][j], "con MAB<=MBB");
                }
            }

            // 4
            for (int i = 0; i <= sn.nodeSize4Failure; i++)
            {
                for (int j = 0; j < sn.virNet.nodeSize; j++)
                {
                    GRBLinExpr conexpr = new GRBLinExpr();
                    for (int k = 0; k < sn.nodeSize; k++)
                    {
                        conexpr.addTerm(1.0, transformMatrix[i][j][k]);
                    }
                    model.addConstr(conexpr, GRB.EQUAL, 1.0, "Con Tiv=1: " + i + " " + j);
                }
            }

            // for (int i = 0; i <= this.nodeSize4Failure; i++) {
            // for (int k = 0; k < this.nodeSize; k++) {
            // GRBLinExpr conexpr = new GRBLinExpr();
            // for (int j = 0; j < this.virNet.nodeSize; j++) {
            // conexpr.addTerm(1.0, transformMatrix[i][j][k]);
            // }
            // model.addConstr(conexpr, GRB.LESS_EQUAL, 1.0, "Con Tuj<=1:" + i + " " +
            // k);
            // }
            // }

            // 5
            for (int i = 0; i <= sn.nodeSize4Failure; i++)
            {
                GRBLinExpr conexpr = new GRBLinExpr();
                for (int j = 0; j < sn.virNet.nodeSize; j++)
                {
                    for (int k = 0; k < sn.nodeSize; k++)
                    {
                        conexpr.addTerm(1.0, transformMatrix[i][j][k]);
                    }
                }
                model.addConstr(conexpr, GRB.EQUAL, sn.nodeSize4Failure, "Con Tuv=n:" + i);
            }

            // 6
            for (int i = 1; i <= sn.nodeSize4Failure; i++)
            {
                GRBLinExpr conexpr = new GRBLinExpr();
                for (int j = 0; j < sn.virNet.nodeSize; j++)
                {
                    conexpr.addTerm(1.0, transformMatrix[i][j][i - 1]);
                }
                model.addConstr(conexpr, GRB.EQUAL, 0.0, "Con Tuk=0:");
            }
            // 7
            for (int i = 0; i <= sn.nodeSize4Failure; i++)
            {
                for (int j = 0; j < sn.virNet.nodeSize; j++)
                {
                    for (int l = 0; l < sn.functionNum; l++)
                    {
                        GRBLinExpr conexpr = new GRBLinExpr();
                        for (int k = 0; k < sn.nodeSize; k++)
                        {
                            if (sn.boolFunctionTypeSet[k][l])
                            {
                                conexpr.addTerm(1.0, transformMatrix[i][j][k]);
                            }
                        }
                        if (sn.virNet.nodeFunctionType[j] == (l))
                        {
                            model.addConstr(conexpr, GRB.GREATER_EQUAL, 1.0, "T*MBS" + "T " + i + "r " + j + "c: " + l);
                        }
                        // else {
                        // model.addConstr(conexpr, GRB.GREATER_EQUAL, 0.0,
                        // "T*ESM" + "T " + i + "r " + j + "c: " + l);
                        // }
                    }
                }
            }

            // Add constraint 8
            // regulate the TransfromMatrix
            for (int k = 0; k < sn.nodeSize; k++)
            {
                for (int i = 0; i <= sn.nodeSize4Failure; i++)
                {
                    for (int j = 0; j < sn.virNet.nodeSize; j++)
                    {
                        GRBLinExpr conexpr = new GRBLinExpr();
                        conexpr.addTerm(1.0, transformMatrix[i][j][k]);
                        model.addConstr(conexpr, GRB.LESS_EQUAL, activeNodeVector[k],
                                "Con activeNode:" + k + " " + i + " " + j);
                    }
                }

            }

            model.optimize();
            int optimstatus = model.get(GRB.IntAttr.Status);
            if (optimstatus != GRB.OPTIMAL)
            {
                return false;
            }
            for (int i = 0; i < sn.nodeSize; i++)
            {
                if (i >= sn.nodeSize4Failure)
                {
                    sn.nodeComputationConsume[i] += ((int) survivalGraphComputationMatrix[i].get(GRB.DoubleAttr.X));
                } else
                {
                    sn.nodeComputationConsume[i] += ((int) survivalGraphComputationMatrix[i].get(GRB.DoubleAttr.X)
                            - sn.virNet.nodeComputationDemand[i]);
                }
                for (int j = 0; j < sn.nodeSize; j++)
                {
                    if (i < sn.nodeSize4Failure && j < sn.nodeSize4Failure)
                    {
                        sn.edgeBandwith4Comsume[i][j] += ((int) (survivalGraphBandwithMatrix[i][j].get(GRB.DoubleAttr.X)
                                - sn.virNet.edgeBandwithDemand[i][j]));
                    } else
                    {
                        sn.edgeBandwith4Comsume[i][j] += ((int) survivalGraphBandwithMatrix[i][j]
                                .get(GRB.DoubleAttr.X));
                    }
                }
            }
            sn.computeConsumedResource(alg);
        } catch (GRBException e)
        {
            e.printStackTrace();
        }
        return true;

    }

}
