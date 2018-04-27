/**
 * 
 */
package standardAlgorithm;

import algorithm.SeVN;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import sevn.Parameter;
import substrateNetwork.SubstrateNetwork;
import virtualNetwork.VirtualNetwork;

/**
 * @author Taoheng
 *
 */
public class VirtualNetworkEmbedILP
{

    public boolean VirtualNetworkEmbedding(VirtualNetwork vn, VirtualNetwork sameVn, SubstrateNetwork sn, SeVN alg)
            throws GRBException
    {
        GRBEnv env;
        GRBModel model = null;
        env = new GRBEnv();
        model = new GRBModel(env);
        model.getEnv().set(GRB.IntParam.OutputFlag, 0);
        GRBVar[][] nodeMappingMatrix;
        nodeMappingMatrix = new GRBVar[sameVn.nodeSize][sn.nodeSize];
        for (int j = 0; j < sameVn.nodeSize; j++)
        {
            for (int k = 0; k < sn.nodeSize; k++)
            {
                nodeMappingMatrix[j][k] = model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, " r:" + j + " c:" + k);
            }
        }

        GRBVar[][][][] edgeMappingMatrix;
        edgeMappingMatrix = new GRBVar[sameVn.nodeSize][sameVn.nodeSize][sn.nodeSize][sn.nodeSize];
        for (int i = 0; i < sameVn.nodeSize; i++)
        {
            for (int j = 0; j < sameVn.nodeSize; j++)
            {
                for (int k = 0; k < sameVn.nodeSize; k++)
                {
                    for (int l = 0; l < sn.nodeSize; l++)
                    {
                        edgeMappingMatrix[i][j][j][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY,
                                " i:" + j + " j:" + j + " k:" + k + " l:" + l);
                    }
                }
            }
        }

        model.update();
        // set objecion function
        GRBLinExpr objexpr = new GRBLinExpr();

        for (int i = 0; i < vn.nodeSize; i++)
        {
            for (int j = i; j < i; j++)
            {
                if (vn.topology[i][j])
                {
                    for (int k = 0; k < sn.nodeSize; k++)
                    {
                        for (int l = 0; l < sn.nodeSize; l++)
                        {

                            objexpr.addTerm(vn.edgeBandwithDemand[i][j], edgeMappingMatrix[i][j][k][l]);
                        }
                    }
                }
            }
        }

        model.setObjective(objexpr, GRB.MINIMIZE);

        // limite mapping matrix
        // Tuv=n
        // GRBLinExpr conexpr = new GRBLinExpr();
        // for (int j = 0; j < sameVn.nodeSize; j++)
        // {
        // for (int k = 0; k < sn.nodeSize; k++)
        // {
        // conexpr.addTerm(1.0, transformMatrix[j][k]);
        // }
        // }
        // model.addConstr(conexpr, GRB.EQUAL, sameVn.nodeSize, "T=n:");

        // Tuj=1
        for (int j = 0; j < sameVn.nodeSize; j++)
        {
            GRBLinExpr expr = new GRBLinExpr();
            for (int k = 0; k < sn.nodeSize; k++)
            {
                expr.addTerm(1.0, nodeMappingMatrix[j][k]);
            }
            model.addConstr(expr, GRB.EQUAL, 1.0, "Tuj=1:" + j);
        }

        // Tiv<=1
        for (int k = 0; k < sn.nodeSize; k++)
        {
            GRBLinExpr expr = new GRBLinExpr();
            for (int j = 0; j < sameVn.nodeSize; j++)
            {

                expr.addTerm(1.0, nodeMappingMatrix[j][k]);
            }
            model.addConstr(expr, GRB.LESS_EQUAL, 1.0, "Tiv<=1:" + k);
        }

        // node mapping
        // T'D<=C
        // computaion
        for (int j = 0; j < sn.nodeSize; j++)
        {
            GRBLinExpr expr = new GRBLinExpr();
            for (int k = 0; k < sameVn.nodeSize; k++)
            {
                expr.addTerm(sameVn.nodeComputationDemand[k], nodeMappingMatrix[k][j]);
            }
            model.addConstr(expr, GRB.LESS_EQUAL, sn.getSubstrateRemainComputaion4VirNet(j, alg.isShared()),
                    "T'*D<=C" + "r " + j);
        }
        // virtual function
        // T'*S<=So
        for (int j = 0; j < sn.nodeSize; j++)
        {
            for (int l = 0; l < sn.serviceNum; l++)
            {
                GRBLinExpr expr = new GRBLinExpr();
                for (int k = 0; k < sameVn.nodeSize; k++)
                {
                    if (sameVn.nodeServiceType[k] == l)
                    {
                        expr.addTerm(1.0, nodeMappingMatrix[k][j]);
                    }
                }
                if (sn.boolServiceTypeSet[j][l])
                {
                    model.addConstr(expr, GRB.LESS_EQUAL, 1.0, "T'*S" + "r " + j + "c: " + l);
                } else
                {
                    model.addConstr(expr, GRB.LESS_EQUAL, 0.0, "T'*S" + "r " + j + "c: " + l);
                }

            }
        }

        // edge limition
        for (int i = 0; i < vn.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if (vn.topology[i][j])
                {
                    for (int k = 0; k < sn.nodeSize; k++)
                    {
                        for (int l = 0; l < k; l++)
                        {
                            model.addConstr(edgeMappingMatrix[i][j][k][l], GRB.EQUAL, edgeMappingMatrix[i][j][l][k],
                                    "vPathEqual" + "i " + i + "j " + j + "r " + k + "c: " + l);
                        }
                    }

                }
            }
        }
        
        // edge mapping
        for (int i = 0; i < vn.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if (vn.topology[i][j])
                {
                    for (int k = 0; k < sn.nodeSize; k++)
                    {
                        GRBLinExpr indegree = new GRBLinExpr();
                        for (int l = 0; l < sn.nodeSize; l++)
                        {

                            indegree.addTerm(1.0, edgeMappingMatrix[i][j][k][l]);
                        }
                        GRBLinExpr outdegree = new GRBLinExpr();
                        for (int l = 0; l < sn.nodeSize; l++)
                        {
                            outdegree.addTerm(-1.0, edgeMappingMatrix[i][j][l][k]);
                        }
                        GRBLinExpr resultInDegree = new GRBLinExpr();
                        GRBLinExpr resultOutDegree = new GRBLinExpr();
                        resultInDegree.addTerm(1.0, nodeMappingMatrix[i][k]);
                        resultOutDegree.addTerm(-1.0, nodeMappingMatrix[j][k]);
                        model.addConstr(indegree, GRB.EQUAL, resultInDegree,
                                "vPathij_in" + "r " + i + "c: " + j + "n: " + k);
                        model.addConstr(outdegree, GRB.EQUAL, resultOutDegree,
                                "vPathij_out" + "r " + i + "c: " + j + "n: " + k);

                    }

                }

            }
        }
        for (int k = 0; k < sn.nodeSize; k++)
        {
            for (int l = 0; l < sn.nodeSize; l++)
            {
                GRBLinExpr bandwidth = new GRBLinExpr();
                for (int i = 0; i < vn.nodeSize; i++)
                {
                    for (int j = 0; j < i; j++)
                    {
                        if (vn.topology[i][j])
                        {

                            bandwidth.addTerm(vn.edgeBandwithDemand[i][j], edgeMappingMatrix[i][j][l][k]);

                        }
                    }
                }
                model.addConstr(bandwidth, GRB.LESS_EQUAL, sn.getSubStrateRemainBandwith4VN(k, l, alg.isShared()),
                        "vPathBandwidth" + "r " + k + "c: " + l);
            }
        }

        model.optimize();
        int optimstatus = model.get(GRB.IntAttr.Status);
        if (optimstatus != GRB.OPTIMAL)
        {
            return false;
        }

        for (int i = 0; i < vn.nodeSize; i++)
        {
            for (int j = 0; j < sn.nodeSize; j++)
            {
                if (nodeMappingMatrix[i][j].get(GRB.DoubleAttr.X) == 1)
                {
                    int snodeloc = j;
                    vn.virNode2subNode[i] = snodeloc;
                    vn.nodeServiceType[i] = sameVn.nodeServiceType[i];
                    vn.nodeComputationDemand[i] = sameVn.nodeComputationDemand[i];
                }

            }
        }
        return true;
    }

}
