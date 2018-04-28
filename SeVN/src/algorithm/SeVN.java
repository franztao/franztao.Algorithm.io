/**
 * 
 */

package algorithm;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import gurobi.GRB;
import gurobi.GRBException;
import sevn.Parameter;
import standardAlgorithm.VirtualNetworkEmbedILP;
import standardAlgorithm.ShortestPath;
import substrateNetwork.BackupNode;
import substrateNetwork.SubstrateNetwork;
import survivabelVirtualNetwork.SurvivalVirtualNetwork;
import virtualNetwork.VirtualNetwork;
import virtualNetwork.VirtualNetworkParameter;

/**
 * Algorithm.
 * 
 * @author franz
 *
 */
public class SeVN
{
    private Logger algorithmLog = Logger.getLogger(SeVN.class);

    public String algorithmName;
    private SubstrateNetwork subNet;
    private VirtualNetworkParameter vnp;

    public StarDP alg;

    // algorithm
    // NoShared Shared
    // FD FI
    // FD: ILP EVSNR
    // EVSNR: Min Ran
    private boolean isShared;
    public int isFailDep;
    public boolean isExact;
    // 0 Ran 1 Min
    public int failSequence;

    public SeVN()
    {
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
        for (int i = 0; i < this.subNet.virNetCollection.size(); i++)
        {
            if ((this.subNet.virNetCollection.get(i) != null) && (this.subNet.virNetCollection.get(i).getIsRunning()))
            {
                this.subNet.virNetCollection.get(i)
                        .setLeaveTime(this.subNet.virNetCollection.get(i).getLeaveTime() - 1);
                if (isForceRelease || (0 == this.subNet.virNetCollection.get(i).getLeaveTime()))
                {
                    releaseVirNetResource(i);
                    this.subNet.virNetCollection.get(i).setIsRunning(false);
                    if (this.subNet.virNetCollection.get(i).surVirNet.isSucceedEmbed)
                    {
                        releaseEnVirNetResource(i);
                    }
                }
                this.subNet.virNetCollection.set(i, null);
            }
        }

    }

    /**
     * releaseEVNResource.
     */
    private void releaseEnVirNetResource(int index)
    {
        if (!this.isShared)
        {
            for (int i = 0; i < this.subNet.surVirNetSet.get(index).nodeSize; i++)
            {
                if (this.subNet.surVirNetSet.get(index).nodeComputation4Backup[i] > 0)
                {
                    this.subNet.nodeComputation4SurvivalUnsharedBackupSum[this.subNet.surVirNetSet.get(
                            index).surNode2subNode[i]] -= this.subNet.surVirNetSet.get(index).nodeComputation4Backup[i];
                }
            }
        }
        if (!this.isShared)
        {
            for (int i = 0; i < this.subNet.surVirNetSet.get(index).nodeSize; i++)
            {
                for (int j = 0; j < i; j++)
                {
                    if (this.subNet.surVirNetSet.get(index).edgeBandwith4Backup[i][j] > 0)
                    {
                        for (int p = 0; p < this.subNet.surVirNetSet.get(index).surEdge2SubPath.get(i).get(j).size()
                                - 1; p++)
                        {
                            int from = this.subNet.surVirNetSet.get(index).surEdge2SubPath.get(i).get(j).get(p);
                            int to = this.subNet.surVirNetSet.get(index).surEdge2SubPath.get(i).get(j).get(p + 1);
                            this.subNet.edgeBandwith4SurvivalUnsharedBackupSum[from][to] -= this.subNet.surVirNetSet
                                    .get(index).edgeBandwith4Backup[i][j];

                            this.subNet.edgeBandwith4SurvivalUnsharedBackupSum[to][from] = this.subNet.edgeBandwith4SurvivalUnsharedBackupSum[from][to];
                        }
                    }
                }
            }
        }

        this.subNet.surVirNetSet.get(index).destructerResource(alg.knapsacks);

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
        for (int i = 0; i < this.subNet.virNetCollection.get(index).nodeSize; i++)
        {
            this.subNet.nodeComputation4Former[this.subNet.virNetCollection
                    .get(index).virNode2subNode[i]] -= this.subNet.virNetCollection.get(index).nodeComputationDemand[i];
        }

        // edge
        for (int i = 0; i < this.subNet.virNetCollection.get(index).nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if (this.subNet.virNetCollection.get(index).edgeBandwithDemand[i][j] > 0)
                {
                    for (int p = 0; p < this.subNet.virNetCollection.get(index).virEdge2subPath.get(i).get(j).size()
                            - 1; p++)
                    {
                        int from = this.subNet.virNetCollection.get(index).virEdge2subPath.get(i).get(j).get(p);
                        int to = this.subNet.virNetCollection.get(index).virEdge2subPath.get(i).get(j).get(p + 1);
                        this.subNet.edgeBandwith4Former[from][to] -= this.subNet.virNetCollection
                                .get(index).edgeBandwithDemand[i][j];
                        this.subNet.edgeBandwith4Former[to][from] = this.subNet.edgeBandwith4Former[from][to];
                    }
                }
            }
        }

        this.subNet.virNetCollection.get(index).destructerResource();
    }

    private boolean nodeMappingVNE(VirtualNetwork vn, VirtualNetwork sameVn, VirtualNetworkEmbedILP ilp)
    {
        // LeaveTime
        if (Parameter.IsSameVirNet4EveryTime)
        {
            vn.setLeaveTime(sameVn.getLeaveTime());
        } else
        {
            if (Parameter.VNRequestsLeaseType == Parameter.VNRequestsLeaseTypeExponential)
            {

                ExponentialDistribution dist = new ExponentialDistribution(
                        Parameter.VNRequestsContinueTimeExponentialMean);
                vn.setLeaveTime(((int) dist.sample()));
            }

            if (Parameter.VNRequestsLeaseType == Parameter.VNRequestsLeaseTypeUniform)
            {
                vn.setLeaveTime((int) (Parameter.VNRequestsContinueTimeMinimum + Math.random()
                        * (Parameter.VNRequestsContinueTimeMaximum - Parameter.VNRequestsContinueTimeMinimum)));
            }
        }

        // node
        if (Parameter.VNEAlgorithm == Parameter.VNERandom)
        {
            boolean[] isSamesNode = new boolean[this.subNet.nodeSize];
            for (int i = 0; i < vn.nodeSize; i++)
            {
                int snodeloc;
                if (Parameter.IsSameVirNet4EveryTime)
                {

                    snodeloc = sameVn.virNode2subNode[i];
                    vn.virNode2subNode[i] = snodeloc;
                    vn.nodeServiceType[i] = sameVn.nodeServiceType[i];
                    vn.nodeComputationDemand[i] = sameVn.nodeComputationDemand[i];

                } else
                {
                    do
                    {
                        snodeloc = (int) Math.round(Math.random() * (this.subNet.nodeSize - 1));
                        if (!isSamesNode[snodeloc])
                        {
                            vn.virNode2subNode[i] = snodeloc;
                            isSamesNode[snodeloc] = true;
                            break;
                        }
                    } while (true);
                    // service
                    int nodeservice = this.subNet.vectorServiceTypeSet.get(snodeloc).elementAt(
                            (int) Math.random() * (this.subNet.vectorServiceTypeSet.get(snodeloc).size() - 1));
                    vn.nodeServiceType[i] = nodeservice;
                    // node demand
                    vn.nodeComputationDemand[i] = (int) (this.vnp.nodeComputationMinimum + Math.round(
                            Math.random() * (this.vnp.nodeComputationMaximum - this.vnp.nodeComputationMinimum)));
                }
            }
        }

        if (Parameter.VNEAlgorithm == Parameter.VNEILP)
        {
            ilp = new VirtualNetworkEmbedILP();
            try
            {
                if (!ilp.VirtualNetworkEmbedding(vn, sameVn, subNet, this))
                {
                    algorithmLog.warn("Fail ILP VNE");
                    return false;
                } else
                {
                    algorithmLog.warn("Succeed ILP VNE");
                }
            } catch (GRBException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        for (int i = 0; i < vn.nodeSize; i++)
        {
            if (vn.nodeComputationDemand[i] > this.subNet.getSubstrateRemainComputaion4VirNet(vn.virNode2subNode[i],
                    this.isShared))
            {
                algorithmLog.warn("Fail to embed VN (" + i + ")-th node into SN: demand larger than resource");
                return false;
            } else
            {
                vn.nodeComputationCapacity[i] = this.subNet.getSubstrateRemainComputaion4VirNet(vn.virNode2subNode[i],
                        this.isShared);
                this.subNet.nodeComputation4Temp[vn.virNode2subNode[i]] += vn.nodeComputationDemand[i];
            }
        }
        return true;
    }

    /**
     * copyVirtualNetwork.
     * 
     * @param vn
     *            vn
     * @param sameVn
     *            sameVn
     * @param vnp
     *            vnp
     * @return boolean
     */
    private boolean copyVirtualNetwork(VirtualNetwork vn, VirtualNetwork sameVn)
    {
        VirtualNetworkEmbedILP ilp = null;
        if (!nodeMappingVNE(vn, sameVn, ilp))
        {
            algorithmLog.warn("Node Mapping Failure");
        }
        if (!edgeMappingVNE(vn, sameVn, ilp))
        {
            algorithmLog.warn("Edge Mapping Failure");
        }
        return true;

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
            for (int i = 0; i < vn.nodeSize; i++)
            {
                for (int j = 0; j < i; j++)
                {
                    if ((Parameter.IsSameVirNet4EveryTime && protoVn.topology[i][j])
                            || ((Parameter.IsSameVirNet4EveryTime == false)))
                    {
                        int distributeIthEdgeBandwith = 0;
                        if (vn.virNode2subNode[i] != vn.virNode2subNode[j])
                        {
                            // exist edge's path,bandwith
                            // source destination bandwith[][]
                            int[][] tempBandwith = new int[this.subNet.nodeSize][this.subNet.nodeSize];
                            int[][] tempTopo = new int[this.subNet.nodeSize][this.subNet.nodeSize];
                            for (int k = 0; k < this.subNet.nodeSize; k++)
                            {
                                for (int l = 0; l < k; l++)
                                {
                                    int rbw = this.subNet.getSubStrateRemainBandwith4VN(k, l, this.isShared);
                                    if (rbw > 0)
                                    {
                                        tempBandwith[k][l] = tempBandwith[l][k] = rbw;
                                        tempTopo[l][k] = tempTopo[k][l] = 1;
                                    }
                                }
                            }

                            ShortestPath shortestPath = new ShortestPath(this.subNet.nodeSize);
                            List<Integer> pathList = new LinkedList<Integer>();
                            pathList = shortestPath.dijkstra(vn.virNode2subNode[i], vn.virNode2subNode[j], tempTopo);

                            if (pathList == null)
                            {
                                algorithmLog.warn("Alg: " + this.algorithmName + " Fail to embed VN (" + i + " to " + j
                                        + ")-edge into SN : feasible path is null");
                                return false;
                            }
                            if (pathList.isEmpty())
                            {
                                algorithmLog.warn("Alg: " + this.algorithmName + " Fail to embed VN (" + i + " to " + j
                                        + ")-edge into SN : feasible path is is empty");
                                return false;
                            }
                            // set virtual network's edge bandwith
                            int pathMaximumBandwith = 0;
                            vn.virEdge2subPath.get(i).get(j).addElement(pathList.get(0));
                            for (int s = pathList.get(0), k = 1; k < pathList.size(); k++)
                            {
                                int e = pathList.get(k);
                                pathMaximumBandwith = Math.max(pathMaximumBandwith, tempBandwith[s][e]);
                                vn.virEdge2subPath.get(i).get(j).addElement(e);
                                s = e;
                            }
                            for (int k = pathList.size() - 1; k >= 0; k--)
                            {
                                vn.virEdge2subPath.get(j).get(i).addElement(pathList.get(k));
                            }
                            if (Parameter.IsSameVirNet4EveryTime)
                            {
                                distributeIthEdgeBandwith = protoVn.edgeBandwithDemand[i][j];
                            } else
                            {
                                distributeIthEdgeBandwith = (int) (vnp.edgeBandwithMinimum + Math
                                        .round(Math.random() * (vnp.edgeBandwithMaximum - vnp.edgeBandwithMinimum)));
                            }
                            if (distributeIthEdgeBandwith > pathMaximumBandwith)
                            {
                                algorithmLog.warn("Alg: " + this.algorithmName + " Fail to embed VN (" + i + " to " + j
                                        + ") edge into SN :SN edge resource not more than PathBandwith Demand");
                                return false;
                            }

                            for (int s = pathList.get(0), k = 1; k < pathList.size(); k++)
                            {
                                int e = pathList.get(k);
                                this.subNet.edgeBandwith4Temp[s][e] += distributeIthEdgeBandwith;
                                this.subNet.edgeBandwith4Temp[e][s] = this.subNet.edgeBandwith4Temp[s][e];
                                s = e;
                            }
                        }
                        vn.topology[i][j] = vn.topology[j][i] = true;
                        vn.edgeBandwithDemand[i][j] = vn.edgeBandwithDemand[j][i] = distributeIthEdgeBandwith;
                    }
                }
            }
        }

        if (Parameter.VNEAlgorithm == Parameter.VNEILP)
        {
            for (int i = 0; i < vn.nodeSize; i++)
            {
                for (int j = 0; j < i; j++)
                {
                    if (!protoVn.topology[i][j])
                    {
                        continue;
                    }
                    if (vn.virNode2subNode[i] != vn.virNode2subNode[j])
                    {
                        int startNode = vn.virNode2subNode[i];
                        int endNode = vn.virNode2subNode[i];
                        vn.virEdge2subPath.get(i).get(j).addElement(startNode);
                        while (startNode != endNode)
                        {
                            int k;
                            for (k = 0; k < subNet.nodeSize; k++)
                            {
                                try
                                {
                                    if ((ilp.edgeMappingMatrix[i][j][startNode][k].get(GRB.DoubleAttr.X) == 1.0)
                                            || (ilp.edgeMappingMatrix[i][j][k][startNode].get(GRB.DoubleAttr.X) == 1.0))
                                    {
                                        vn.virEdge2subPath.get(i).get(j).addElement(k);
                                        this.subNet.edgeBandwith4Temp[startNode][k] += protoVn.edgeBandwithDemand[i][j];
                                        startNode = k;
                                    }
                                } catch (GRBException e)
                                {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                            if (k == subNet.nodeSize)
                            {
                                algorithmLog.warn("VNE ILP is internal error: VN edge to SN path is wrong");
                            }

                        }
                        vn.topology[i][j] = vn.topology[j][i] = true;
                        vn.edgeBandwithDemand[i][j] = vn.edgeBandwithDemand[j][i] = protoVn.edgeBandwithDemand[i][j];
                    }
                }
            }
        }

        // modify node and edge new value
        for (int i = 0; i < subNet.nodeSize; i++)
        {
            if (this.subNet.nodeComputation4Temp[i] > 0)
            {
                this.subNet.nodeComputation4Former[i] += this.subNet.nodeComputation4Temp[i];
                this.subNet.nodeComputation4Temp[i] = 0;
            }
            for (int j = 0; j < i; j++)
            {
                if (i != j)
                {
                    this.subNet.edgeBandwith4Former[i][j] += this.subNet.edgeBandwith4Temp[i][j];
                    this.subNet.edgeBandwith4Former[j][i] = this.subNet.edgeBandwith4Former[i][j];
                    this.subNet.edgeBandwith4Temp[i][j] = this.subNet.edgeBandwith4Temp[j][i] = 0;
                }
            }
        }
        for (int i = 0; i < vn.nodeSize; i++)
        {
            this.subNet.virNetIndexSet4sNode.get(vn.virNode2subNode[i]).addElement(this.subNet.virNetCollection.size());
            for (int j = 0; j < vn.nodeSize; j++)
            {
                for (int k = 0; k < vn.virEdge2subPath.get(i).get(j).size() - 1; k++)
                {
                    this.subNet.virNetIndexSet4sEdge.get(vn.virEdge2subPath.get(i).get(j).get(k))
                            .get(vn.virEdge2subPath.get(i).get(j).get(k + 1))
                            .addElement(this.subNet.virNetCollection.size());
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
    public void generateAndProtectVirNet(VirtualNetwork protoVirNet)
    {

        this.subNet.virNetReqSum++;
        VirtualNetwork virNet = new VirtualNetwork(protoVirNet);

        // construct a virtual network
        if (copyVirtualNetwork(virNet, protoVirNet))
        {
            virNet.setIndex(this.subNet.virNetCollection.size());
            virNet.setIsRunning(true);
            this.subNet.virNetSuceedEmbedSum++;
            this.subNet.virNetCollection.addElement(virNet);
            algorithmLog.info("Alg: " + this.algorithmName + " Succeed to generate ("
                    + (this.subNet.virNetCollection.size() - 1) + ")-th  VN");
        } else
        {
            this.subNet.clearTempResource();
            algorithmLog.warn("Alg: " + this.algorithmName + "Fail to generate (" + this.subNet.virNetCollection.size()
                    + ")-th VN: embed VN error");
            return;
        }

        // construct backup node
        BackupNode bn = new BackupNode(this.subNet, virNet, this.isShared);

        // construct SurvivalVirtualNetwork
        SurvivalVirtualNetwork svn = new SurvivalVirtualNetwork(this.subNet, virNet, bn);
        virNet.surVirNet = svn;

        // VirNet directly return, not process survivable procedure.
        if (this.algorithmName.equals("VirNet") || generateSurvivalVirtualNetwork(svn))
        {

            this.subNet.surNetSuceedEmbedSum++;
            algorithmLog.info("Alg: " + this.algorithmName + " Succeed to construct SeVN and embed SeVN");
        } else
        {
            this.subNet.clearTempResource();
            algorithmLog.warn("Alg: " + this.algorithmName + " Fail to construct SeVN");
        }

        this.subNet.surVirNetSet.addElement(svn);
        return;
    }

    public boolean startSeVNAlgorithm(SeVN seVN, SurvivalVirtualNetwork svn)
    {
        boolean isSvnAlgorithmSuccess = false;
        if (this.isExact)
        {
            isSvnAlgorithmSuccess = new ILP().exactAlgorithmIntegerProgram4SeVN(this, svn);
        } else
        {

            this.alg = new StarDP(svn);
            alg.obtainItems(svn);
            try
            {

                isSvnAlgorithmSuccess = alg.heursitcAlgorithm4SeVN(this, svn);
            } catch (GRBException e)
            {
                e.printStackTrace();
            }
        }
        return isSvnAlgorithmSuccess;
    }

    /**
     * generateSurvivalVirtualNetwork.
     * 
     * @param vn
     *            vn
     * @return boolean
     */
    private boolean generateSurvivalVirtualNetwork(SurvivalVirtualNetwork svn)
    {

        if (startSeVNAlgorithm(this, svn))
        {

            if (distributeResource4SurVirNet(svn))
            {
                svn.isSucceedEmbed = true;
                algorithmLog.info("Alg: " + this.algorithmName + " Succeed distribute enough resource for SeVN");
                return true;
            } else
            {
                if (Parameter.IsReleaseVNafterEVNFailure)
                {
                    this.subNet.virNetCollection.get(this.subNet.virNetCollection.size() - 1).setLeaveTime(1);
                }
                algorithmLog.info("Alg: " + this.algorithmName + " Fail distribute enough resource for SeVN");
            }
        } else
        {
            algorithmLog.info("Alg: " + this.algorithmName + " Algorithm fail to ask the SeVN solution");
        }
        return false;
    }

    /**
     * distributeResource4EVN.
     * 
     * @param svn
     *            evn
     * @return boolean
     */
    private boolean distributeResource4SurVirNet(SurvivalVirtualNetwork svn)
    {
        for (int i = 0; i < this.subNet.nodeSize; i++)
        {
            int nodeResource = 0;
            for (int j = 0; j < svn.nodeSize; j++)
            {
                if (i == svn.surNode2subNode[j])
                {
                    if (j < svn.virNet.nodeSize)
                    {
                        if (svn.nodeComputationConsume[j]
                                - svn.virNet.nodeComputationDemand[svn.surNode2virNode[j]] > 0)
                        {
                            nodeResource += svn.nodeComputationConsume[j]
                                    - svn.virNet.nodeComputationDemand[svn.surNode2virNode[j]];
                        }
                    } else
                    {
                        if (svn.nodeComputationConsume[j] > 0)
                        {
                            nodeResource += svn.nodeComputationConsume[j];
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
                    algorithmLog.warn("Fail to distribute enhacned network (" + i + ") node into substrate network");
                    return false;
                }
            }
            if (nodeResource < 0)
            {
                algorithmLog.error("distributeResource4EVN nodeResource less than zero ");
            }

        }

        for (int i = 0; i < svn.nodeSize; i++)
        {
            for (int j = 0; j < svn.nodeSize; j++)
            {
                if (i < svn.nodeSize4Failure && j < svn.nodeSize4Failure)
                {

                    svn.edgeBandwith4Backup[i][j] = svn.edgeBandwith4Comsume[i][j]
                            - svn.virNet.edgeBandwithDemand[svn.surNode2virNode[i]][svn.surNode2virNode[j]];
                } else
                {
                    svn.edgeBandwith4Backup[i][j] = svn.edgeBandwith4Comsume[i][j];
                }
            }
        }
        // edge
        for (int i = 0; i < svn.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if (svn.edgeBandwith4Backup[i][j] > 0)
                {
                    int[][] tempTopology;
                    tempTopology = new int[this.subNet.nodeSize][this.subNet.nodeSize];
                    for (int k = 0; k < this.subNet.nodeSize; k++)
                    {
                        for (int l = 0; l < this.subNet.nodeSize; l++)
                        {
                            int bd = this.subNet.getSubStrateRemainBandwith4SurVirNet(k, l, this.isShared);
                            if (bd > svn.edgeBandwith4Backup[i][j])
                            {
                                tempTopology[k][l] = tempTopology[l][k] = 1;
                            }
                        }
                    }

                    ShortestPath shortestPath = new ShortestPath(this.subNet.nodeSize);
                    List<Integer> pathList = new LinkedList<Integer>();

                    // do not split shortest path
                    pathList = shortestPath.dijkstra(svn.surNode2subNode[i], svn.surNode2subNode[j], tempTopology);
                    if (pathList == null)
                    {
                        algorithmLog.warn("Fail to embedd enhanced network (" + i + "--" + j
                                + ") edge into substrate network: null value");
                        return false;
                    }
                    if (pathList.isEmpty())
                    {
                        algorithmLog.warn("Fail to embedd enhanced network (" + i + "--" + j
                                + ") edge into substrate network: lack path");
                        return false;
                    }

                    // set virtual network's edge bandwith
                    for (int k = 0; k < pathList.size(); k++)
                    {
                        svn.surEdge2SubPath.get(i).get(j).addElement(pathList.get(k));
                    }
                    for (int k = pathList.size() - 1; k >= 0; k--)
                    {
                        svn.surEdge2SubPath.get(j).get(i).addElement(pathList.get(k));
                    }

                    for (int s = pathList.get(0), k = 1; k < pathList.size(); k++)
                    {
                        int e = pathList.get(k);
                        this.subNet.edgeBandwith4Temp[s][e] += svn.edgeBandwith4Backup[i][j];
                        this.subNet.edgeBandwith4Temp[e][s] = this.subNet.edgeBandwith4Temp[s][e];
                        s = e;
                    }
                } else
                {
                    if (svn.edgeBandwith4Backup[i][j] < 0)
                    {
                        algorithmLog.error("distributeResource4EVN edgeResource less than zero ");
                        return false;
                    }
                }
            }
        }

        for (int i = 0; i < svn.nodeSize; i++)
        {
            this.subNet.surVirNetIndexSet4sNode.get(svn.surNode2subNode[i]).addElement(this.subNet.surVirNetSet.size());
            for (int j = 0; j < svn.nodeSize; j++)
            {
                for (int k = 0; k < svn.surEdge2SubPath.get(i).get(j).size() - 1; k++)
                {
                    this.subNet.surVirNetIndexSet4sEdge.get(svn.surEdge2SubPath.get(i).get(j).get(k))
                            .get(svn.surEdge2SubPath.get(i).get(j).get(k + 1))
                            .addElement(this.subNet.surVirNetSet.size());
                }
            }
        }

        // set node and edge new value
        for (int i = 0; i < svn.nodeSize; i++)
        {
            if (i < svn.virNet.nodeSize)
            {
                svn.nodeComputation4Backup[i] = svn.nodeComputationConsume[i] - svn.virNet.nodeComputationDemand[i];
            } else
            {
                svn.nodeComputation4Backup[i] = svn.nodeComputationConsume[i];
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

    /**
     * @return the sn
     */
    public SubstrateNetwork getSn()
    {
        return subNet;
    }

    /**
     * @param sn
     *            the sn to set
     */
    public void setSn(SubstrateNetwork sn)
    {
        this.subNet = sn;
    }

    /**
     * @return the isShared
     */
    public boolean isShared()
    {
        return isShared;
    }

    /**
     * @param isShared
     *            the isShared to set
     */
    public void setShared(boolean isShared)
    {
        this.isShared = isShared;
    }

    /**
     * @return the isFD
     */
    public int isFD()
    {
        return isFailDep;
    }

    /**
     * @param isFD
     *            the isFD to set
     */
    public void setFD(int isFD)
    {
        this.isFailDep = isFD;
    }

    /**
     * @return the isExact
     */
    public boolean isExact()
    {
        return isExact;
    }

    /**
     * @param isExact
     *            the isExact to set
     */
    public void setExact(boolean isExact)
    {
        this.isExact = isExact;
    }

    /**
     * @return the sequence
     */
    public int getSequence()
    {
        return failSequence;
    }

    /**
     * @return the vnp
     */
    public VirtualNetworkParameter getVnp()
    {
        return vnp;
    }

    /**
     * @param vnp
     *            the vnp to set
     */
    public void setVnp(VirtualNetworkParameter vnp)
    {
        this.vnp = vnp;

    }

    /**
     * 
     */
    public void isClearAllResource()
    {

        for (int i = 0; i < this.subNet.nodeSize; i++)
        {
            if (this.subNet.nodeComputation4Former[i] > 0)
            {
                algorithmLog.error("substrate (" + i + ") node former resource fail release completely");
                break;
            }
        }

        for (int i = 0; i < this.subNet.nodeSize; i++)
        {
            if (this.subNet.nodeComputation4SurvivalUnsharedBackupSum[i] > 0)
            {
                algorithmLog.error("substrate (" + i + ") node enhance resource fail release completely");
                break;
            }
        }

        for (int i = 0; i < this.subNet.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if (this.subNet.edgeBandwith4Former[j][i] > 0)
                {
                    algorithmLog.error("Ishared:(" + this.isShared + ") substrate (" + j + "--" + i
                            + ") edge former resource fail release completely");
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
                    algorithmLog.error("Ishared:(" + this.isShared + ") substrate (" + j + "--" + i
                            + ") edge enhance resource fail release completely");
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
    public void setParameter(String algName, SubstrateNetwork sn, boolean isExact, int failureindependent,
            boolean isShared, int sequence)
    {
        this.algorithmName = algName;
        this.subNet = sn;
        this.isExact = isExact;
        this.isFailDep = failureindependent;
        this.isShared = isShared;
        this.failSequence = sequence;
    }

}
