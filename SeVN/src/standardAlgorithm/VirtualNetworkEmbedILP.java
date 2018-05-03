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
    public GRBEnv env;
    public GRBModel model;
    public GRBVar[][] nodeMappingMatrix;
    public GRBVar[][][][] edgeMappingMatrix;
    
    public boolean VirtualNetworkEmbedding( VirtualNetwork protoVN, SubstrateNetwork subNet, SeVN alg)
            throws GRBException
    {
        env = new GRBEnv();
        model = new GRBModel(env);
        model.getEnv().set(GRB.IntParam.OutputFlag, 0);
        
        
     // Create variables
        nodeMappingMatrix = new GRBVar[protoVN.nodeSize][subNet.nodeSize];
        for (int i = 0; i < protoVN.nodeSize; i++)
        {
            for (int j = 0; j < subNet.nodeSize; j++)
            {
                nodeMappingMatrix[i][j] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, " r:" + i + " c:" + j);
            }
        }

        edgeMappingMatrix = new GRBVar[protoVN.nodeSize][protoVN.nodeSize][subNet.nodeSize][subNet.nodeSize];
        for (int i = 0; i < protoVN.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                for (int k = 0; k < subNet.nodeSize; k++)
                {
                    for (int l = 0; l < subNet.nodeSize; l++)
                    {
                        edgeMappingMatrix[i][j][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY,
                                " i:" + i + " j:" + j + " k:" + k + " l:" + l);
                    }
                }
            }
        }

        model.update();
        // set objecion function
        GRBLinExpr objexpr = new GRBLinExpr();
        for (int i = 0; i < protoVN.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if (protoVN.topology[i][j])
                {
                    for (int k = 0; k < subNet.nodeSize; k++)
                    {
                        for (int l = 0; l < k; l++)
                        {

                            objexpr.addTerm(protoVN.edgeBandwithDemand[i][j], edgeMappingMatrix[i][j][k][l]);
                        }
                    }
                }
            }
        }

        model.setObjective(objexpr, GRB.MINIMIZE);

        // limite mapping matrix

        // Tuj=1
        for (int i = 0; i < protoVN.nodeSize; i++)
        {
            GRBLinExpr expr = new GRBLinExpr();
            for (int j = 0; j < subNet.nodeSize; j++)
            {
                expr.addTerm(1.0, nodeMappingMatrix[i][j]);
            }
            model.addConstr(expr, GRB.EQUAL, 1.0, "Tuj=1:" + i);
        }

        // Tiv<=1
        for (int j = 0; j < subNet.nodeSize; j++)
        {
            GRBLinExpr expr = new GRBLinExpr();
            for (int i = 0; i < protoVN.nodeSize; i++)
            {
                expr.addTerm(1.0, nodeMappingMatrix[i][j]);
            }
            model.addConstr(expr, GRB.LESS_EQUAL, 1.0, "Tiv<=1:" + j);
        }

        // node mapping
        // T'D<=C
        // computaion
        for (int j = 0; j < subNet.nodeSize; j++)
        {
            GRBLinExpr expr = new GRBLinExpr();
            for (int i = 0; i < protoVN.nodeSize; i++)
            {
                expr.addTerm(protoVN.nodeComputationDemand[i], nodeMappingMatrix[i][j]);
            }
            model.addConstr(expr, GRB.LESS_EQUAL, subNet.getSubstrateRemainComputaion4VirNet(j, alg.isShared()),
                    "T'*D<=C" + "r " + j);
        }
        // virtual function
        // T'*S<=So
        for (int j = 0; j < subNet.nodeSize; j++)
        {
            for (int l = 0; l < subNet.serviceNum; l++)
            {
                GRBLinExpr expr = new GRBLinExpr();
                for (int i = 0; i < protoVN.nodeSize; i++)
                {
                    if (protoVN.nodeFunctionType[i] == l)
                    {
                        expr.addTerm(1.0, nodeMappingMatrix[i][j]);
                    }
                }
                if (subNet.boolServiceTypeSet[j][l])
                {
                    model.addConstr(expr, GRB.LESS_EQUAL, 1.0, "T'*S" + "r " + j + "c: " + l);
                } else
                {
                    model.addConstr(expr, GRB.LESS_EQUAL, 0.0, "T'*S" + "r " + j + "c: " + l);
                }

            }
        }

        // edge limition
//        for (int i = 0; i < vn.nodeSize; i++)
//        {
//            for (int j = 0; j < i; j++)
//            {
//                if (protoVn.topology[i][j])
//                {
//                    for (int k = 0; k < sn.nodeSize; k++)
//                    {
//                        for (int l = 0; l < k; l++)
//                        {
//                            model.addConstr(edgeMappingMatrix[i][j][k][l], GRB.EQUAL, edgeMappingMatrix[i][j][l][k],
//                                    "vPathEqual" + "i " + i + "j " + j + "r " + k + "c: " + l);
//                        }
//                    }
//
//                }
//            }
//        }
//        
        // edge mapping
        for (int i = 0; i < protoVN.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if (protoVN.topology[i][j])
                {
                    for (int k = 0; k < subNet.nodeSize; k++)
                    {
                        GRBLinExpr indegree = new GRBLinExpr();
                        for (int l = 0; l < subNet.nodeSize; l++)
                        {

                            indegree.addTerm(1.0, edgeMappingMatrix[i][j][k][l]);
                        }
                        GRBLinExpr outdegree = new GRBLinExpr();
                        for (int l = 0; l < subNet.nodeSize; l++)
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
        for (int k = 0; k < subNet.nodeSize; k++)
        {
            for (int l = 0; l < subNet.nodeSize; l++)
            {
                GRBLinExpr bandwidth = new GRBLinExpr();
                for (int i = 0; i < protoVN.nodeSize; i++)
                {
                    for (int j = 0; j < i; j++)
                    {
                        if (protoVN.topology[i][j])
                        {

                            bandwidth.addTerm(protoVN.edgeBandwithDemand[i][j], edgeMappingMatrix[i][j][k][l]);
                            bandwidth.addTerm(protoVN.edgeBandwithDemand[i][j], edgeMappingMatrix[i][j][l][k]);

                        }
                    }
                }
                model.addConstr(bandwidth, GRB.LESS_EQUAL, subNet.getSubStrateRemainBandwith4VirNet(k, l, alg.isShared()),
                        "vPathBandwidth" + "r " + k + "c: " + l);
            }
        }

        model.optimize();
        int optimstatus = model.get(GRB.IntAttr.Status);
        if (optimstatus != GRB.OPTIMAL)
        {
            return false;
        }

        return true;
    }

}
