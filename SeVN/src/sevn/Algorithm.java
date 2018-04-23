/**
 * 
 */

package sevn;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import standardalgorithm.ShortestPath;
import substratenetwork.BackupNode;
import substratenetwork.SubstrateNetwork;
import virtualnetwork.SurvivalVirtualNetwork;
import virtualnetwork.VirtualNetwork;
import virtualnetwork.VirtualNetworkParameter;

/**
 * Algorithm.
 * 
 * @author franz
 *
 */
public class Algorithm
{
    private Logger algorithmLog = Logger.getLogger(Algorithm.class);

    public String algorithmName;
    private SubstrateNetwork sn;
    private VirtualNetworkParameter vnp;

    // algorithm
    // NoShared Shared
    // FD FI
    // FD: ILP EVSNR
    // EVSNR: Min Ran
    private boolean isShared;
    public int isFailDep;
    public boolean isExact;
    // 0 Ran 1 Min
    public int sequence;

    public Algorithm()
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
        for (int i = 0; i < this.sn.virNetCollection.size(); i++)
        {
            if (this.sn.virNetCollection.get(i).getIsRunning())
            {
                this.sn.virNetCollection.get(i).setLeaveTime(this.sn.virNetCollection.get(i).getLeaveTime() - 1);
                if (isForceRelease || (0 == this.sn.virNetCollection.get(i).getLeaveTime()))
                {
                    releaseVirNetResource(i);
                    this.sn.virNetCollection.get(i).setIsRunning(false);
                    if (this.sn.virNetCollection.get(i).surVirNet.isSucceedEmbed)
                    {
                        releaseEnVirNetResource(i);
                    }
                }
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
            for (int i = 0; i < this.sn.surVirNetSet.get(index).nodeSize; i++)
            {
                if (this.sn.surVirNetSet.get(index).nodeComputation4Backup[i] > 0)
                {
                    this.sn.nodeComputation4SurvivalUnsharedBackupSum[this.sn.surVirNetSet.get(
                            index).surNode2subNode[i]] -= this.sn.surVirNetSet.get(index).nodeComputation4Backup[i];
                }
            }
        }
        if (!this.isShared)
        {
            for (int i = 0; i < this.sn.surVirNetSet.get(index).nodeSize; i++)
            {
                for (int j = 0; j < i; j++)
                {
                    if (this.sn.surVirNetSet.get(index).edgeBandwith4Backup[i][j] > 0)
                    {
                        for (int p = 0; p < this.sn.surVirNetSet.get(index).surEdge2SubPath.get(i).get(j).size()
                                - 1; p++)
                        {
                            int from = this.sn.surVirNetSet.get(index).surEdge2SubPath.get(i).get(j).get(p);
                            int to = this.sn.surVirNetSet.get(index).surEdge2SubPath.get(i).get(j).get(p + 1);
                            this.sn.edgeBandwith4SurvivalUnsharedBackupSum[from][to] -= this.sn.surVirNetSet
                                    .get(index).edgeBandwith4Backup[i][j];

                            this.sn.edgeBandwith4SurvivalUnsharedBackupSum[to][from] = this.sn.edgeBandwith4SurvivalUnsharedBackupSum[from][to];
                        }
                    }
                }
            }
        }

        this.sn.surVirNetSet.get(index).destructerResource();

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
        for (int i = 0; i < this.sn.virNetCollection.get(index).nodeSize; i++)
        {
            this.sn.nodeComputation4Former[this.sn.virNetCollection
                    .get(index).virNode2subNode[i]] -= this.sn.virNetCollection.get(index).nodeComputationDemand[i];
        }

        // edge
        for (int i = 0; i < this.sn.virNetCollection.get(index).nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if (this.sn.virNetCollection.get(index).edgeBandwithDemand[i][j] > 0)
                {
                    for (int p = 0; p < this.sn.virNetCollection.get(index).virEdge2subPath.get(i).get(j).size()
                            - 1; p++)
                    {
                        int from = this.sn.virNetCollection.get(index).virEdge2subPath.get(i).get(j).get(p);
                        int to = this.sn.virNetCollection.get(index).virEdge2subPath.get(i).get(j).get(p + 1);
                        this.sn.edgeBandwith4Former[from][to] -= this.sn.virNetCollection
                                .get(index).edgeBandwithDemand[i][j];
                        this.sn.edgeBandwith4Former[to][from] = this.sn.edgeBandwith4Former[from][to];
                    }
                }
            }
        }

        this.sn.virNetCollection.get(index).destructerResource();
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
        boolean[] isSamesNode = new boolean[this.sn.nodeSize];
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
                    snodeloc = (int) Math.round(Math.random() * (this.sn.nodeSize - 1));
                    if (!isSamesNode[snodeloc])
                    {
                        vn.virNode2subNode[i] = snodeloc;
                        isSamesNode[snodeloc] = true;
                        break;
                    }
                } while (true);
                // service
                int nodeservice = this.sn.vectorServiceTypeSet.get(snodeloc)
                        .elementAt((int) Math.random() * (this.sn.vectorServiceTypeSet.get(snodeloc).size() - 1));
                vn.nodeServiceType[i] = nodeservice;
                // node demand
                vn.nodeComputationDemand[i] = (int) (this.vnp.nodeComputationMinimum + Math
                        .round(Math.random() * (this.vnp.nodeComputationMaximum - this.vnp.nodeComputationMinimum)));
            }
            if (vn.nodeComputationDemand[i] > this.sn.getSubstrateRemainComputaion4VirNet(snodeloc, this.isShared))
            {
                algorithmLog.warn("Fail to embed virtual network (" + i + ")-th node into substrate network");
                return false;
            } else
            {
                vn.nodeComputationCapacity[i] = this.sn.getSubstrateRemainComputaion4VirNet(snodeloc, this.isShared);
                this.sn.nodeComputation4Temp[snodeloc] += vn.nodeComputationDemand[i];
            }

        }

        // edge
        for (int i = 0; i < vn.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if ((Parameter.IsSameVirNet4EveryTime && sameVn.topology[i][j])
                        || ((Parameter.IsSameVirNet4EveryTime == false)))
                {

                    if (Math.random() > vnp.node2nodeProbability)
                    {
                        break;

                    }

                    int distributeIthEdgeBandwith = 0;
                    // System.out.println("distributeIthEdgeBandwith
                    // "+distributeIthEdgeBandwith);
                    if (vn.virNode2subNode[i] != vn.virNode2subNode[j])
                    {
                        // exist edge's path,bandwith
                        // source destination bandwith[][]
                        int[][] tempBandwith = new int[this.sn.nodeSize][this.sn.nodeSize];
                        int[][] tempTopo = new int[this.sn.nodeSize][this.sn.nodeSize];
                        for (int k = 0; k < this.sn.nodeSize; k++)
                        {
                            for (int l = 0; l < k; l++)
                            {
                                int rbw = this.sn.getSubStrateRemainBandwith4VN(k, l, this.isShared);
                                if (rbw > 0)
                                {
                                    tempBandwith[k][l] = tempBandwith[l][k] = rbw;
                                    tempTopo[l][k] = tempTopo[k][l] = 1;
                                }
                            }
                        }

                        ShortestPath shortestPath = new ShortestPath(this.sn.nodeSize);
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
                            distributeIthEdgeBandwith = sameVn.edgeBandwithDemand[i][j];
                        } else
                        {
                            distributeIthEdgeBandwith = (int) (vnp.edgeBandwithMinimum
                                    + Math.round(Math.random() * (vnp.edgeBandwithMaximum - vnp.edgeBandwithMinimum)));
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
                            this.sn.edgeBandwith4Temp[s][e] += distributeIthEdgeBandwith;
                            this.sn.edgeBandwith4Temp[e][s] = this.sn.edgeBandwith4Temp[s][e];
                            s = e;
                        }
                    }
                    vn.topology[i][j] = vn.topology[j][i] = true;
                    vn.edgeBandwithDemand[i][j] = vn.edgeBandwithDemand[j][i] = distributeIthEdgeBandwith;
                }
            }
        }

        // modify node and edge new value
        for (int i = 0; i < sn.nodeSize; i++)
        {
            if (this.sn.nodeComputation4Temp[i] > 0)
            {
                this.sn.nodeComputation4Former[i] += this.sn.nodeComputation4Temp[i];

                this.sn.nodeComputation4Temp[i] = 0;
            }
            for (int j = 0; j < i; j++)
            {
                if (i != j)
                {
                    this.sn.edgeBandwith4Former[i][j] += this.sn.edgeBandwith4Temp[i][j];
                    this.sn.edgeBandwith4Former[j][i] = this.sn.edgeBandwith4Former[i][j];

                    this.sn.edgeBandwith4Temp[i][j] = this.sn.edgeBandwith4Temp[j][i] = 0;
                }
            }
        }
        for (int i = 0; i < vn.nodeSize; i++)
        {
            this.sn.virNetIndexSet4sNode.get(vn.virNode2subNode[i]).addElement(this.sn.virNetCollection.size());
            for (int j = 0; j < vn.nodeSize; j++)
            {
                for (int k = 0; k < vn.virEdge2subPath.get(i).get(j).size() - 1; k++)
                {
                    this.sn.virNetIndexSet4Edge.get(vn.virEdge2subPath.get(i).get(j).get(k))
                            .get(vn.virEdge2subPath.get(i).get(j).get(k + 1))
                            .addElement(this.sn.virNetCollection.size());
                }
            }
        }
        return true;

    }

    /**
     * generateAndProtectVirNet.
     * 
     * @param sameVirNet
     *            sameVirNet
     */
    public void generateAndProtectVirNet(VirtualNetwork sameVirNet)
    {

        this.sn.virNetSum++;
        VirtualNetwork vn = new VirtualNetwork(sameVirNet);

        // construct a virtual network
        if (copyVirtualNetwork(vn, sameVirNet))
        {
            vn.setIndex(this.sn.virNetCollection.size());
            vn.setIsRunning(true);
            this.sn.virNetSuceedEmbedSum++;
            this.sn.virNetCollection.addElement(vn);
            algorithmLog.info("Alg: " + this.algorithmName + " Succeed to generate ("
                    + (this.sn.virNetCollection.size() - 1) + ")-th  VN");
        } else
        {
            this.sn.clearTempResource();
            algorithmLog.warn(
                    "Alg: " + this.algorithmName + " Fail to generate (" + this.sn.virNetCollection.size() + ")-th VN");
            return;
        }

        // construct backup node
        BackupNode bn = new BackupNode(this.sn, vn, this.isShared);

        // construct SurvivalVirtualNetwork
        SurvivalVirtualNetwork svn = new SurvivalVirtualNetwork(this.sn, vn, bn);
        vn.surVirNet = svn;

        // VirNet directly return, not process survival procedure.
        if (this.algorithmName.equals("VirNet") || generateSurvivalVirtualNetwork(svn))
        {
            this.sn.surNetSuceedEmbedSum++;
            algorithmLog.info("Alg: " + this.algorithmName + " Succeed to construct EVN and embed EVN");
        } else
        {
            this.sn.clearTempResource();
            algorithmLog.warn("Alg: " + this.algorithmName + " Fail to construct EVN");
        }

        this.sn.surVirNetSet.addElement(svn);
        return;
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

        if (svn.startSeVNAlgorithm(this))
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
                    this.sn.virNetCollection.get(this.sn.virNetCollection.size() - 1).setLeaveTime(1);
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
        for (int i = 0; i < this.sn.nodeSize; i++)
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
                if (nodeResource < this.sn.getSubstrateRemainComputaion4SurVirNet(i, this.isShared))
                {
                    this.sn.nodeComputation4Temp[i] += nodeResource;
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
                    tempTopology = new int[this.sn.nodeSize][this.sn.nodeSize];
                    for (int k = 0; k < this.sn.nodeSize; k++)
                    {
                        for (int l = 0; l < this.sn.nodeSize; l++)
                        {
                            int bd = this.sn.getSubStrateRemainBandwith4SurVirNet(k, l, this.isShared);
                            if (bd > svn.edgeBandwith4Backup[i][j])
                            {
                                tempTopology[k][l] = tempTopology[l][k] = 1;
                            }
                        }
                    }

                    ShortestPath shortestPath = new ShortestPath(this.sn.nodeSize);
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
                        this.sn.edgeBandwith4Temp[s][e] += svn.edgeBandwith4Backup[i][j];
                        this.sn.edgeBandwith4Temp[e][s] = this.sn.edgeBandwith4Temp[s][e];
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
            this.sn.surVirNetIndexSet4sNode.get(svn.surNode2subNode[i]).addElement(this.sn.surVirNetSet.size());
            for (int j = 0; j < svn.nodeSize; j++)
            {
                for (int k = 0; k < svn.surEdge2SubPath.get(i).get(j).size() - 1; k++)
                {
                    this.sn.surVirNetIndexSet4Edge.get(svn.surEdge2SubPath.get(i).get(j).get(k))
                            .get(svn.surEdge2SubPath.get(i).get(j).get(k + 1)).addElement(this.sn.surVirNetSet.size());
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

        for (int i = 0; i < sn.nodeSize; i++)
        {
            if (!this.isShared)
            {
                this.sn.nodeComputation4SurvivalUnsharedBackupSum[i] += this.sn.nodeComputation4Temp[i];
            }
            this.sn.nodeComputation4Temp[i] = 0;
            for (int j = 0; j < i; j++)
            {
                if (!this.isShared)
                {
                    this.sn.edgeBandwith4SurvivalUnsharedBackupSum[i][j] += this.sn.edgeBandwith4Temp[i][j];
                    this.sn.edgeBandwith4SurvivalUnsharedBackupSum[j][i] = this.sn.edgeBandwith4SurvivalUnsharedBackupSum[i][j];
                }
                this.sn.edgeBandwith4Temp[i][j] = this.sn.edgeBandwith4Temp[j][i] = 0;
            }

        }
        return true;

    }

    /**
     * @return the sn
     */
    public SubstrateNetwork getSn()
    {
        return sn;
    }

    /**
     * @param sn
     *            the sn to set
     */
    public void setSn(SubstrateNetwork sn)
    {
        this.sn = sn;
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
        return sequence;
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

        for (int i = 0; i < this.sn.nodeSize; i++)
        {
            if (this.sn.nodeComputation4Former[i] > 0)
            {
                algorithmLog.error("substrate (" + i + ") node former resource fail release completely");
                break;
            }
        }

        for (int i = 0; i < this.sn.nodeSize; i++)
        {
            if (this.sn.nodeComputation4SurvivalUnsharedBackupSum[i] > 0)
            {
                algorithmLog.error("substrate (" + i + ") node enhance resource fail release completely");
                break;
            }
        }

        for (int i = 0; i < this.sn.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if (this.sn.edgeBandwith4Former[j][i] > 0)
                {
                    algorithmLog.error("Ishared:(" + this.isShared + ") substrate (" + j + "--" + i
                            + ") edge former resource fail release completely");
                    break;
                }

            }
        }

        for (int i = 0; i < this.sn.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if (this.sn.edgeBandwith4SurvivalUnsharedBackupSum[j][i] > 0)
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
        this.sn = sn;
        this.isExact = isExact;
        this.isFailDep = failureindependent;
        this.isShared = isShared;
        this.sequence = sequence;
    }

}
