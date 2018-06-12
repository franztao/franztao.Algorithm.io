/**
 * 
 */

package algorithm;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import gurobi.GRB;
import gurobi.GRBException;
import sevn.Parameter;
import standardAlgorithm.VirtualNetworkEmbedILP;
import standardAlgorithm.ShortestPath;
import substrateNetwork.BackupNode;
import substrateNetwork.PhysicalNetworkt;
import survivabelVirtualNetwork.SurvivalVirtualNetwork;
import virtualNetwork.VirtualNetwork;
import virtualNetwork.VirtualNetworkParameter;

/**
 * Algorithm.
 * 
 * @author franz
 *
 */
public class SeVNAlgorithm
{
    private Logger sevnLog = Logger.getLogger(SeVNAlgorithm.class.getName());

    public String algorithmName;
    public PhysicalNetworkt subNet;
    public VirtualNetworkParameter vnp;

    public StarDP starDP;

    // algorithm
    // NoShared Shared
    // FD FI
    // FD: ILP EVSNR
    // EVSNR: Min Ran
    public boolean isShared;
    public int algType;
    public boolean isExact;
    // 0 Ran 1 Min
    public int failSequence;

    public SeVNAlgorithm()
    {
        sevnLog.setLevel(Parameter.logLevel);
        PropertyConfigurator.configure("log4j.properties");
    }

    /**
     * releaseResource.
     * 
     * @param isForceRelease
     * 
     */
    public void releaseResource(boolean isForceRelease)
    {

        for (int i = 0; i < this.subNet.virNetSet.size(); i++)
        {
            if ((this.subNet.virNetSet.get(i) != null) && (this.subNet.virNetSet.get(i).isRunning))
            {
                this.subNet.virNetSet.get(i).leaveTime = this.subNet.virNetSet.get(i).leaveTime - 1;
                if (isForceRelease || (0 == this.subNet.virNetSet.get(i).leaveTime))
                {
                    releaseVirNetResource(i);
                    this.subNet.virNetSet.get(i).isRunning = false;

                    if (this.subNet.virNetSet.get(i).surVirNet.isSucceedProtection)
                    {
                        releaseSurVirNetResource(this.subNet.virNetSet.get(i).surVirNet.index);
                    }
                    this.subNet.virNetSet.set(i, null);
                }

            }
        }

    }

    /**
     * releaseEVNResource.
     */
    private void releaseSurVirNetResource(int index)
    {
        if (!this.isShared)
        {
            // node
            for (int i = 0; i < this.subNet.surNetSet.get(index).nodeSize; i++)
            {
                if (this.subNet.surNetSet.get(index).nodeComputation4Backup[i] > 0)
                {
                    this.subNet.nodeComputation4SurvivalUnsharedBackupSum[this.subNet.surNetSet.get(
                            index).surNode2phyNode[i]] -= this.subNet.surNetSet.get(index).nodeComputation4Backup[i];
                }
            }
            for (int i = 0; i < this.subNet.surNetSet.get(index).nodeSize; i++)
            {
                for (int j = 0; j < i; j++)
                {
                    if (this.subNet.surNetSet.get(index).edgeBandwith4Backup[i][j] > 0)
                    {
                        for (int p = 0; p < this.subNet.surNetSet.get(index).surEdge2PhyPath.get(i).get(j).size()
                                - 1; p++)
                        {
                            int from = this.subNet.surNetSet.get(index).surEdge2PhyPath.get(i).get(j).get(p);
                            int to = this.subNet.surNetSet.get(index).surEdge2PhyPath.get(i).get(j).get(p + 1);
                            this.subNet.edgeBandwith4SurvivalUnsharedBackupSum[from][to] -= this.subNet.surNetSet
                                    .get(index).edgeBandwith4Backup[i][j];

                            this.subNet.edgeBandwith4SurvivalUnsharedBackupSum[to][from] = this.subNet.edgeBandwith4SurvivalUnsharedBackupSum[from][to];
                        }
                    }
                }
            }
        }

        this.subNet.surNetSet.get(index).destructerResource(starDP.knapSacks);

    }

    /**
     * releaseVNResource.
     * 
     * @param index
     *            index
     * 
     */
    private void releaseVirNetResource(int index)
    {

        // node
        for (int i = 0; i < this.subNet.virNetSet.get(index).nodeSize; i++)
        {
            this.subNet.nodeComputation4Crital[this.subNet.virNetSet
                    .get(index).virNode2phyNode[i]] -= this.subNet.virNetSet.get(index).nodeComputationDemand[i];
        }

        // edge
        for (int i = 0; i < this.subNet.virNetSet.get(index).nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if (this.subNet.virNetSet.get(index).edgeBandwithDemand[i][j] > 0)
                {
                    for (int p = 0; p < this.subNet.virNetSet.get(index).virEdge2PhyPath.get(i).get(j).size() - 1; p++)
                    {
                        int from = this.subNet.virNetSet.get(index).virEdge2PhyPath.get(i).get(j).get(p);
                        int to = this.subNet.virNetSet.get(index).virEdge2PhyPath.get(i).get(j).get(p + 1);
                        if (this.subNet.edgeBandwith4Crital[from][to] < this.subNet.virNetSet
                                .get(index).edgeBandwithDemand[i][j])
                        {
                            sevnLog.error("release VirNetResource");
                        }
                        this.subNet.edgeBandwith4Crital[from][to] -= this.subNet.virNetSet
                                .get(index).edgeBandwithDemand[i][j];
                        this.subNet.edgeBandwith4Crital[to][from] = this.subNet.edgeBandwith4Crital[from][to];
                    }
                }
            }
        }

        this.subNet.virNetSet.get(index).destructerResource();
    }

    private boolean nodeMappingVNE(VirtualNetwork vn, VirtualNetwork protoVN, VirtualNetworkEmbedILP ilp)
    {

        // node
        if (Parameter.VNEAlgorithm == Parameter.VNERandom)
        {
            for (int i = 0; i < protoVN.nodeSize; i++)
            {
                int snodeloc;
                snodeloc = protoVN.virNode2phyNode[i];
                vn.virNode2phyNode[i] = snodeloc;
                vn.nodeFunctionType[i] = protoVN.nodeFunctionType[i];
                vn.nodeComputationDemand[i] = protoVN.nodeComputationDemand[i];
            }
        }

        if (Parameter.VNEAlgorithm == Parameter.VNEILP)
        {

            try
            {
                if (!ilp.VirtualNetworkEmbedding(protoVN, subNet, this))
                {
                    sevnLog.warn("Fail ILP VNE");
                    return false;
                } else
                {
                    sevnLog.info("Succeed ILP VNE");
                }
            } catch (GRBException e)
            {
                e.printStackTrace();
            }
            for (int i = 0; i < protoVN.nodeSize; i++)
            {
                int j;
                for (j = 0; j < subNet.nodeSize; j++)
                {
                    try
                    {
                        if (ilp.nodeMappingMatrix[i][j].get(GRB.DoubleAttr.X) == 1)
                        {
                            int snodeloc = j;
                            vn.virNode2phyNode[i] = snodeloc;
                            vn.nodeFunctionType[i] = protoVN.nodeFunctionType[i];
                            vn.nodeComputationDemand[i] = protoVN.nodeComputationDemand[i];
                            break;
                        }
                    } catch (GRBException e)
                    {
                        e.printStackTrace();
                    }

                }
                if (j == subNet.nodeSize)
                {
                    sevnLog.error("ILP node error");
                    return false;
                }
            }
        }

        for (int i = 0; i < vn.nodeSize; i++)
        {
            if (vn.nodeComputationDemand[i] < 0)
            {
                sevnLog.error("Fail to embed VN (" + i + ")-th node into SN: demand less than  zero");
                return false;
            }
            if (vn.nodeComputationDemand[i] > this.subNet.getSubstrateRemainComputaion4VirNet(vn.virNode2phyNode[i],
                    this.isShared))
            {
                sevnLog.warn("Fail to embed VN (" + i + ")-th node into SN: demand larger than resource");
                return false;
            }
            vn.nodeComputationCapacity[i] = this.subNet.getSubstrateRemainComputaion4VirNet(vn.virNode2phyNode[i],
                    this.isShared);
            this.subNet.nodeComputation4Temp[vn.virNode2phyNode[i]] += vn.nodeComputationDemand[i];
        }
        return true;
    }

    /**
     * copyVirtualNetwork.
     * 
     * @param vn
     *            vn
     * @param protoVN
     *            sameVn
     * @param vnp
     *            vnp
     * @return boolean
     */
    private boolean copyVirtualNetwork(VirtualNetwork vn, VirtualNetwork protoVN)
    {
        vn.leaveTime = protoVN.leaveTime;
        VirtualNetworkEmbedILP ilp = new VirtualNetworkEmbedILP();
        if (!nodeMappingVNE(vn, protoVN, ilp))
        {
            this.subNet.clearTempResource();
            sevnLog.warn("Node Mapping Failure");
            return false;
        }
        if (!edgeMappingVNE(vn, protoVN, ilp))
        {
            this.subNet.clearTempResource();
            sevnLog.warn("Edge Mapping Failure");
            return false;
        }
        distributeNodeEdgeDemand(vn);
        return true;

    }

    void distributeNodeEdgeDemand(VirtualNetwork vn)
    {
        // modify node and edge new value
        for (int i = 0; i < subNet.nodeSize; i++)
        {
            if (this.subNet.nodeComputation4Temp[i] > 0)
            {
                this.subNet.nodeComputation4Crital[i] += this.subNet.nodeComputation4Temp[i];
                this.subNet.nodeComputation4Temp[i] = 0;
            }
            for (int j = 0; j < i; j++)
            {
                if (i != j)
                {
                    this.subNet.edgeBandwith4Crital[i][j] += this.subNet.edgeBandwith4Temp[i][j];
                    this.subNet.edgeBandwith4Crital[j][i] = this.subNet.edgeBandwith4Crital[i][j];
                    this.subNet.edgeBandwith4Temp[i][j] = this.subNet.edgeBandwith4Temp[j][i] = 0;
                }
            }
        }
        for (int i = 0; i < vn.nodeSize; i++)
        {
            this.subNet.virNetIndexSet4sNode.get(vn.virNode2phyNode[i]).addElement(this.subNet.virNetSet.size());
            for (int j = 0; j < vn.nodeSize; j++)
            {
                for (int k = 0; k < vn.virEdge2PhyPath.get(i).get(j).size() - 1; k++)
                {
                    this.subNet.virNetIndexSet4sEdge.get(vn.virEdge2PhyPath.get(i).get(j).get(k))
                            .get(vn.virEdge2PhyPath.get(i).get(j).get(k + 1)).addElement(this.subNet.virNetSet.size());
                }
            }
        }
    }

    /**
     * @param vn
     * @param protoVn
     * @param ilp
     */
    private boolean edgeMappingVNE(VirtualNetwork vn, VirtualNetwork protoVn, VirtualNetworkEmbedILP ilp)
    {
        if (Parameter.VNEAlgorithm == Parameter.VNERandom)
        {
            // edge
            for (int i = 0; i < protoVn.nodeSize; i++)
            {
                for (int j = 0; j < i; j++)
                {
                    if (!protoVn.topology[i][j])
                    {
                        continue;
                    }
                    int distributeBandwith = 0;
                    if (vn.virNode2phyNode[i] != vn.virNode2phyNode[j])
                    {
                        // exist edge's path,bandwith
                        int[][] tempBandwith = new int[this.subNet.nodeSize][this.subNet.nodeSize];
                        int[][] tempTopo = new int[this.subNet.nodeSize][this.subNet.nodeSize];
                        for (int k = 0; k < this.subNet.nodeSize; k++)
                        {
                            for (int l = 0; l < k; l++)
                            {
                                int remainBandWidth = this.subNet.getSubStrateRemainBandwith4VirNet(k, l,
                                        this.isShared);
                                if (remainBandWidth < 0)
                                {
                                    return false;
                                }
                                if (remainBandWidth > 0)
                                {
                                    tempBandwith[k][l] = tempBandwith[l][k] = remainBandWidth;
                                    tempTopo[l][k] = tempTopo[k][l] = 1;
                                }
                            }
                        }

                        ShortestPath shortestPath = new ShortestPath(this.subNet.nodeSize);
                        List<Integer> pathList = new LinkedList<Integer>();
                        pathList = shortestPath.dijkstra(vn.virNode2phyNode[i], vn.virNode2phyNode[j], tempTopo);
                        if (pathList == null)
                        {
                            sevnLog.warn("Alg: " + this.algorithmName + " Fail to embed VN (" + i + " to " + j
                                    + ")-edge into SN : feasible path is null");
                            return false;
                        }
                        if (pathList.isEmpty())
                        {
                            sevnLog.warn("Alg: " + this.algorithmName + " Fail to embed VN (" + i + " to " + j
                                    + ")-edge into SN : feasible path is is empty");
                            return false;
                        }
                        // set virtual network's edge bandwith
                        int pathMaximumBandwith = Integer.MAX_VALUE;
                        vn.virEdge2PhyPath.get(i).get(j).addElement(pathList.get(0));
                        for (int s = pathList.get(0), k = 1; k < pathList.size(); k++)
                        {
                            int e = pathList.get(k);
                            pathMaximumBandwith = Math.min(pathMaximumBandwith, tempBandwith[s][e]);
                            vn.virEdge2PhyPath.get(i).get(j).addElement(e);
                            s = e;
                        }
                        for (int k = pathList.size() - 1; k >= 0; k--)
                        {
                            vn.virEdge2PhyPath.get(j).get(i).addElement(pathList.get(k));
                        }

                        distributeBandwith = protoVn.edgeBandwithDemand[i][j];
                        // distributeBandwith=0;
                        if (distributeBandwith > pathMaximumBandwith)
                        {
                            sevnLog.warn("Alg: " + this.algorithmName + " Fail to embed VN (" + i + " to " + j
                                    + ") edge into SN :SN edge resource not more than PathBandwith Demand");
                            return false;
                        }

                        for (int s = pathList.get(0), k = 1; k < pathList.size(); k++)
                        {
                            int e = pathList.get(k);
                            this.subNet.edgeBandwith4Temp[s][e] += distributeBandwith;
                            this.subNet.edgeBandwith4Temp[e][s] = this.subNet.edgeBandwith4Temp[s][e];
                            s = e;
                        }
                        vn.topology[i][j] = vn.topology[j][i] = true;
                        vn.edgeBandwithDemand[i][j] = vn.edgeBandwithDemand[j][i] = distributeBandwith;
                    }

                }
            }
        }

        if (Parameter.VNEAlgorithm == Parameter.VNEILP)
        {
            for (int i = 0; i < protoVn.nodeSize; i++)
            {
                for (int j = 0; j < i; j++)
                {
                    if (!protoVn.topology[i][j])
                    {
                        continue;
                    }
                    if (vn.virNode2phyNode[i] != vn.virNode2phyNode[j])
                    {
                        int startNode = vn.virNode2phyNode[i];
                        int endNode = vn.virNode2phyNode[j];
                        vn.virEdge2PhyPath.get(i).get(j).addElement(startNode);
                        vn.virEdge2PhyPath.get(j).get(i).insertElementAt(startNode, 0);
                        try
                        {
                            while (startNode != endNode)
                            {
                                int k;
                                for (k = 0; k < subNet.nodeSize; k++)
                                {
                                    if ((ilp.edgeMappingMatrix[i][j][startNode][k].get(GRB.DoubleAttr.X) == 1.0))
                                    {
                                        vn.virEdge2PhyPath.get(i).get(j).addElement(k);
                                        vn.virEdge2PhyPath.get(j).get(i).insertElementAt(k, 0);
                                        this.subNet.edgeBandwith4Temp[startNode][k] += protoVn.edgeBandwithDemand[i][j];
                                        this.subNet.edgeBandwith4Temp[k][startNode] = this.subNet.edgeBandwith4Temp[startNode][k];
                                        startNode = k;
                                        break;
                                    }

                                }
                                if (k == subNet.nodeSize)
                                {
                                    sevnLog.error("VNE ILP is internal error: VN edge to SN path is wrong");
                                    return false;
                                }

                            }
                        } catch (GRBException e)
                        {
                            e.printStackTrace();
                        }
                        vn.topology[i][j] = vn.topology[j][i] = true;
                        vn.edgeBandwithDemand[i][j] = vn.edgeBandwithDemand[j][i] = protoVn.edgeBandwithDemand[i][j];
                    }
                }
            }
        }

        return true;

    }

    /**
     * generateAndProtectVirNet.
     * 
     * @param protoVirNet
     *            sameVirNet
     */
    public boolean generateAndProtectVirNet(VirtualNetwork protoVirNet)
    {

        this.subNet.reqSum++;
        VirtualNetwork virNet = new VirtualNetwork(protoVirNet);

        // construct a virtual network
        if (copyVirtualNetwork(virNet, protoVirNet))
        {
            virNet.index = this.subNet.virNetSet.size();
            virNet.isRunning = true;
            this.subNet.virNetSuceedEmbedSum++;
            this.subNet.virNetSet.addElement(virNet);
            sevnLog.info("Alg: " + this.algorithmName + " Succeed to generate (" + this.subNet.virNetSet.size()
                    + ")-th  VN");
        } else
        {
            this.subNet.clearTempResource();
            sevnLog.warn("Alg: " + this.algorithmName + "Fail to generate (" + this.subNet.virNetSet.size()
                    + ")-th VN: embed VN");
            return false;
        }

        // construct backup node
        BackupNode bn = new BackupNode(this.subNet, virNet, this.isShared);

        // construct SurvivalVirtualNetwork
        SurvivalVirtualNetwork surNet = new SurvivalVirtualNetwork(this.subNet, virNet, bn);

        // VirNet directly return, not process survivable procedure.
        if (this.algorithmName.equals("VirNet") || generateSurvivalVirtualNetwork(surNet))
        {
            // svn.isSucceedEmbed=true;
            this.subNet.surNetSuceedEmbedSum++;
            sevnLog.info("Alg: " + this.algorithmName + " Succeed to construct SeVN and embed SeVN");
            surNet.index = this.subNet.surNetSet.size();
            this.subNet.surNetSet.addElement(surNet);
            return true;
        } else
        {
            this.subNet.clearTempResource();
            sevnLog.warn("Alg: " + this.algorithmName + " Fail to construct SeVN");
            surNet.index = this.subNet.surNetSet.size();
            this.subNet.surNetSet.addElement(surNet);
            return false;
        }

    }

    public boolean startSeVNAlgorithm(SurvivalVirtualNetwork surNet)
    {
        boolean isSeVNAlgorithmSuccess = false;
        if (this.isExact)
        {
            sevnLog.error("ILP algorithm do not exist");
            // isSvnAlgorithmSuccess = new ILP().exactAlgorithmIntegerProgram4SeVN(this,
            // svn);
        } else
        {

            this.starDP = new StarDP(surNet);
            this.starDP.obtainItems(surNet);
            isSeVNAlgorithmSuccess = starDP.heursitcAlgorithm4SeVN(this, surNet);
        }
        return isSeVNAlgorithmSuccess;
    }

    /**
     * generateSurvivalVirtualNetwork.
     * 
     * @param vn
     *            vn
     * @return boolean
     */
    private boolean generateSurvivalVirtualNetwork(SurvivalVirtualNetwork surNet)
    {

        if (startSeVNAlgorithm(surNet))
        {
            if (distributeResource4SurNet(surNet))
            {
                surNet.isSucceedProtection = true;
                sevnLog.info("Alg: " + this.algorithmName + " Succeed distribute enough resource for SeVN");
                return true;
            } else
            {
                if (Parameter.IsReleaseVNafterEVNFailure)
                {
                    this.subNet.virNetSet.get(surNet.virNet.index).leaveTime = 1;
                }
                sevnLog.info("Alg: " + this.algorithmName + " Fail distribute enough resource for SeVN");
            }
        } else
        {
            sevnLog.info("Alg: " + this.algorithmName + " Algorithm fail generate Survival Virtual Network");
        }
        return false;
    }

    /**
     * distributeResource4EVN.
     * 
     * @param surNet
     *            evn
     * @return boolean
     */
    private boolean distributeResource4SurNet(SurvivalVirtualNetwork surNet)
    {
        for (int i = 0; i < this.subNet.nodeSize; i++)
        {
            int nodeResource = 0;
            for (int j = 0; j < surNet.nodeSize; j++)
            {
                if (i == surNet.surNode2phyNode[j])
                {
                    if (j < surNet.virNet.nodeSize)
                    {
                        if (surNet.nodeComputationConsume[j]
                                - surNet.virNet.nodeComputationDemand[surNet.surNode2virNode[j]] > 0)
                        {
                            nodeResource += surNet.nodeComputationConsume[j]
                                    - surNet.virNet.nodeComputationDemand[surNet.surNode2virNode[j]];
                        }
                    } else
                    {
                        if (surNet.nodeComputationConsume[j] > 0)
                        {
                            nodeResource += surNet.nodeComputationConsume[j];
                        }
                    }
                }
            }

            if (nodeResource > 0)
            {
                if (nodeResource < this.subNet.getSubstrateRemainComputaion4SurVirNet(i, this.isShared))
                {
                    this.subNet.nodeComputation4Temp[i] += nodeResource;
                } else
                {
                    sevnLog.warn("Fail to distribute (" + i + ") node  RemainComputaion SeVN network ");
                    return false;
                }
            }
            if (nodeResource < 0)
            {
                sevnLog.error("distributeResource 4 SeVN nodeResource less than zero ");
            }

        }

        for (int i = 0; i < surNet.nodeSize; i++)
        {
            for (int j = 0; j < surNet.nodeSize; j++)
            {
                if (i < surNet.nodeSize4Failure && j < surNet.nodeSize4Failure)
                {

                    surNet.edgeBandwith4Backup[i][j] = surNet.edgeBandwith4Comsume[i][j]
                            - surNet.virNet.edgeBandwithDemand[surNet.surNode2virNode[i]][surNet.surNode2virNode[j]];
                } else
                {
                    surNet.edgeBandwith4Backup[i][j] = surNet.edgeBandwith4Comsume[i][j];
                }
            }
        }

        // edge
        for (int i = 0; i < surNet.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if (surNet.edgeBandwith4Backup[i][j] > 0)
                {
                    int[][] tempTopology;
                    tempTopology = new int[this.subNet.nodeSize][this.subNet.nodeSize];
                    for (int k = 0; k < this.subNet.nodeSize; k++)
                    {
                        for (int l = 0; l < this.subNet.nodeSize; l++)
                        {
                            int bd = this.subNet.getSubStrateRemainBandwith4SurVirNet(k, l, this.isShared);
                            if (bd > surNet.edgeBandwith4Backup[i][j])
                            {
                                tempTopology[k][l] = tempTopology[l][k] = 1;
                            }
                        }
                    }

                    ShortestPath shortestPath = new ShortestPath(this.subNet.nodeSize);
                    List<Integer> pathList = new LinkedList<Integer>();

                    // do not split shortest path
                    pathList = shortestPath.dijkstra(surNet.surNode2phyNode[i], surNet.surNode2phyNode[j],
                            tempTopology);
                    if (pathList == null)
                    {
                        sevnLog.warn("Fail to embedd SeVN network (" + i + "--" + j + ") edge: path null");
                        return false;
                    }
                    if (pathList.isEmpty())
                    {
                        sevnLog.warn("Fail to embedd SeVn network (" + i + "--" + j + ") edge: path is Empty");
                        return false;
                    }

                    // set virtual network's edge bandwith
                    for (int k = 0; k < pathList.size(); k++)
                    {
                        surNet.surEdge2PhyPath.get(i).get(j).addElement(pathList.get(k));
                    }
                    for (int k = pathList.size() - 1; k >= 0; k--)
                    {
                        surNet.surEdge2PhyPath.get(j).get(i).addElement(pathList.get(k));
                    }

                    for (int s = pathList.get(0), k = 1; k < pathList.size(); k++)
                    {
                        int e = pathList.get(k);
                        this.subNet.edgeBandwith4Temp[s][e] += surNet.edgeBandwith4Backup[i][j];
                        this.subNet.edgeBandwith4Temp[e][s] = this.subNet.edgeBandwith4Temp[s][e];
                        s = e;
                    }
                } else
                {
                    if (surNet.edgeBandwith4Backup[i][j] < 0)
                    {
                        sevnLog.error("distributeResource SeVN edgeResource less than zero ");
                        return false;
                    }
                }
            }
        }

        for (int i = 0; i < surNet.nodeSize; i++)
        {
            this.subNet.surNetIndexSet4subNode.get(surNet.surNode2phyNode[i]).addElement(this.subNet.surNetSet.size());
            for (int j = 0; j < surNet.nodeSize; j++)
            {
                for (int k = 0; k < surNet.surEdge2PhyPath.get(i).get(j).size() - 1; k++)
                {
                    this.subNet.surVirNetIndexSet4sEdge.get(surNet.surEdge2PhyPath.get(i).get(j).get(k))
                            .get(surNet.surEdge2PhyPath.get(i).get(j).get(k + 1))
                            .addElement(this.subNet.surNetSet.size());
                }
            }
        }

        // set node and edge new value
        for (int i = 0; i < surNet.nodeSize; i++)
        {
            if (i < surNet.virNet.nodeSize)
            {
                surNet.nodeComputation4Backup[i] = surNet.nodeComputationConsume[i]
                        - surNet.virNet.nodeComputationDemand[i];
            } else
            {
                surNet.nodeComputation4Backup[i] = surNet.nodeComputationConsume[i];
            }

        }

        for (int i = 0; i < subNet.nodeSize; i++)
        {
            if (!this.isShared)
            {
                this.subNet.nodeComputation4SurvivalUnsharedBackupSum[i] += this.subNet.nodeComputation4Temp[i];
            }
            this.subNet.nodeComputation4Temp[i] = 0;
            for (int j = 0; j < i; j++)
            {
                if (!this.isShared)
                {
                    this.subNet.edgeBandwith4SurvivalUnsharedBackupSum[i][j] += this.subNet.edgeBandwith4Temp[i][j];
                    this.subNet.edgeBandwith4SurvivalUnsharedBackupSum[j][i] = this.subNet.edgeBandwith4SurvivalUnsharedBackupSum[i][j];
                }
                this.subNet.edgeBandwith4Temp[i][j] = this.subNet.edgeBandwith4Temp[j][i] = 0;
            }

        }
        return true;

    }

    public void isClearAllResource()
    {

        for (int i = 0; i < this.subNet.nodeSize; i++)
        {
            if (this.subNet.nodeComputation4Crital[i] > 0)
            {
                sevnLog.error("substrate (" + i + ") node cirtal resource fail release completely");
                break;
            }
        }

        for (int i = 0; i < this.subNet.nodeSize; i++)
        {
            if (this.subNet.nodeComputation4SurvivalUnsharedBackupSum[i] > 0)
            {
                sevnLog.error("substrate (" + i + ") node survivabal resource fail release completely");
                break;
            }
        }

        for (int i = 0; i < this.subNet.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if (this.subNet.edgeBandwith4Crital[j][i] > 0)
                {
                    sevnLog.error("Ishared:(" + this.isShared + ") substrate (" + j + "--" + i
                            + ") edge cirtal resource fail release completely");
                    break;
                }

            }
        }

        for (int i = 0; i < this.subNet.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if (this.subNet.edgeBandwith4SurvivalUnsharedBackupSum[j][i] > 0)
                {
                    sevnLog.error("Ishared:(" + this.isShared + ") substrate (" + j + "--" + i
                            + ") edge survivabal resource fail release completely");
                    break;
                }

            }
        }

    }

    /**
     * setParameter.
     * 
     * @param algName
     *            algName
     * @param sn
     *            sn
     * @param isExact
     *            isExact
     * @param failureindependent
     *            failureindependent
     * @param isShared
     *            isShared
     */
    public void setParameter(String algName, PhysicalNetworkt sn, boolean isExact, int failureindependent,
            boolean isShared, int sequence)
    {
        this.algorithmName = algName;
        this.subNet = sn;
        this.subNet.algorihtm = this;
        this.isExact = isExact;
        this.algType = failureindependent;
        this.isShared = isShared;
        this.failSequence = sequence;
    }

}
