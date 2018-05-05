package substrateNetwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import algorithm.SeVN;
import sevn.Parameter;
import sndlib.core.io.SNDlibIOFactory;
import sndlib.core.io.SNDlibIOFormat;
import sndlib.core.io.SNDlibParseException;
import sndlib.core.io.SNDlibParser;
import sndlib.core.network.Link;
import sndlib.core.network.Network;
import sndlib.core.network.Node;
import survivabelVirtualNetwork.SurvivalVirtualNetwork;
import virtualNetwork.VirtualNetwork;

public class SubstrateNetwork implements Cloneable
{
    private Logger substrateNetworkLog = Logger.getLogger(SubstrateNetwork.class);

    public SeVN algorihtm;

    // node
    public int nodeSize;
    public int[] nodeComputationCapacity;
    public int[] nodeComputation4Crital;
    public int[] nodeComputation4SurvivalUnsharedBackupSum;
    public int[] nodeComputation4Temp;
    public Vector<Vector<Integer>> virNetIndexSet4sNode;
    public Vector<Vector<Integer>> surVirNetIndexSet4sNode;
    // edge
    public int edgeSize;
    public boolean[][] topology;
    public int[][] edgeBandwithCapacity;
    public int[][] edgeBandwith4Crital;
    public int[][] edgeBandwith4SurvivalUnsharedBackupSum;
    public int[][] edgeBandwith4Temp;
    public Vector<Vector<Vector<Integer>>> virNetIndexSet4sEdge;
    public Vector<Vector<Vector<Integer>>> surVirNetIndexSet4sEdge;

    public int functionNum;
    public boolean[][] boolFunctionTypeSet;
    public Vector<Vector<Integer>> vectorFunctionTypeSet;

    public String[] node2Label;
    public Map<String, Integer> label2Node;

    public Vector<VirtualNetwork> virNetSet;
    public Vector<SurvivalVirtualNetwork> surVirNetSet;

    // acceptionRatio
    public int virNetSuceedEmbedSum;
    public int surNetSuceedEmbedSum;
    // EmbeddingCost
    public int virNetReqSum;

    /**
     * SubstrateNetwork.
     * 
     * @param snp
     *            snp
     */
    public SubstrateNetwork(SubStrateNetworkParameter snp, int ithexperiment)
    {

        PropertyConfigurator.configure("log4j.properties");

        Network network = null;
        if (Parameter.TopologyType == Parameter.TopologyTypeSNDLib)
        {
            Reader networkReader = null;

            File file = new File(Parameter.SNDLibFile);
            String[] filelist = file.list();
            File readfile = null;
            if (filelist[ithexperiment % filelist.length] != null)
            {
                readfile = new File(Parameter.SNDLibFile + "\\" + filelist[ithexperiment % filelist.length]);
            }
            try
            {
                networkReader = new FileReader(readfile.getAbsolutePath());
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            SNDlibParser parser = SNDlibIOFactory.newParser(SNDlibIOFormat.NATIVE);
            try
            {
                network = parser.parseNetwork(networkReader);
            } catch (SNDlibParseException | IOException e)
            {
                e.printStackTrace();
            }
            if (network != null)
            {
                this.nodeSize = network.nodeCount();
            } else
            {
                this.nodeSize = -1;
            }

        } else
        {
            // node
            this.nodeSize = snp.nodeSize;
        }

        // node
        this.nodeComputationCapacity = new int[nodeSize];
        this.nodeComputation4Crital = new int[nodeSize];
        this.nodeComputation4SurvivalUnsharedBackupSum = new int[nodeSize];
        this.nodeComputation4Temp = new int[nodeSize];
        this.virNetIndexSet4sNode = new Vector<Vector<Integer>>();
        this.surVirNetIndexSet4sNode = new Vector<Vector<Integer>>();
        for (int i = 0; i < nodeSize; i++)
        {
            virNetIndexSet4sNode.addElement(new Vector<Integer>());
            surVirNetIndexSet4sNode.addElement(new Vector<Integer>());
        }

        // edge
        this.edgeSize = 0;
        this.topology = new boolean[nodeSize][nodeSize];
        this.edgeBandwithCapacity = new int[nodeSize][nodeSize];
        edgeBandwith4Crital = new int[nodeSize][nodeSize];
        edgeBandwith4SurvivalUnsharedBackupSum = new int[nodeSize][nodeSize];
        edgeBandwith4Temp = new int[nodeSize][nodeSize];
        this.virNetIndexSet4sEdge = new Vector<Vector<Vector<Integer>>>();
        this.surVirNetIndexSet4sEdge = new Vector<Vector<Vector<Integer>>>();
        for (int i = 0; i < nodeSize; i++)
        {
            this.virNetIndexSet4sEdge.addElement(new Vector<Vector<Integer>>());
            this.surVirNetIndexSet4sEdge.addElement(new Vector<Vector<Integer>>());
            for (int j = 0; j < nodeSize; j++)
            {
                this.virNetIndexSet4sEdge.get(i).add(new Vector<Integer>());
                this.surVirNetIndexSet4sEdge.get(i).add(new Vector<Integer>());
            }
        }

        // service
        this.functionNum = snp.functionTypeNumber;
        this.boolFunctionTypeSet = new boolean[nodeSize][functionNum];
        this.vectorFunctionTypeSet = new Vector<Vector<Integer>>();
        for (int i = 0; i < this.nodeSize; i++)
        {
            this.vectorFunctionTypeSet.addElement(new Vector<Integer>());
        }

        // label
        this.node2Label = new String[nodeSize];
        this.label2Node = new HashMap<String, Integer>();

        this.virNetSet = new Vector<VirtualNetwork>();
        this.surVirNetSet = new Vector<SurvivalVirtualNetwork>();
        if (Parameter.TopologyType == Parameter.TopologyTypeSample)
        {
            setResourceDistribution4Sample();
        }
        if (Parameter.TopologyType == Parameter.TopologyTypeRandom)
        {
            setResourceDistribution4RandomTopo(snp);
        }

        if (Parameter.TopologyType == Parameter.TopologyTypeDataCenter)
        {
            setResourceDistribution4DataCenter(snp);
        }

        if (Parameter.TopologyType == Parameter.TopologyTypeSNDLib)
        {
            setResourceDistribution4SNDLib(snp, network);
        }

        // label
        String str = "SN_";
        for (int i = 0; i < this.nodeSize; i++)
        {
            node2Label[i] = (str + (i + 1));
            label2Node.put((str + (1 + i)), i);
        }

        this.virNetSuceedEmbedSum = 0;
        this.surNetSuceedEmbedSum = 0;
        this.virNetReqSum = 0;

    }

    public void setResourceDistribution4Sample()
    {
        nodeComputationCapacity[0] = 5;
        nodeComputationCapacity[1] = 7;
        nodeComputationCapacity[2] = 7;
        nodeComputationCapacity[3] = 10;
        nodeComputationCapacity[4] = 6;
        nodeComputationCapacity[5] = 9;
        nodeComputationCapacity[6] = 10;
        nodeComputationCapacity[7] = 8;

        topology[0][1] = true;
        topology[0][2] = true;
        topology[0][3] = true;
        topology[0][4] = true;

        topology[1][2] = true;
        topology[1][4] = true;

        topology[2][4] = true;
        topology[2][5] = true;
        topology[2][6] = true;

        topology[3][6] = true;
        topology[4][7] = true;

        for (int i = 0; i < nodeSize; i++)
        {
            for (int j = 0; j < nodeSize; j++)
            {
                if (topology[i][j])
                {
                    topology[j][i] = true;
                    edgeBandwithCapacity[i][j] = edgeBandwithCapacity[j][i] = 10;
                }
            }
        }

        boolFunctionTypeSet[0][0] = true;

        boolFunctionTypeSet[1][1] = true;
        boolFunctionTypeSet[1][2] = true;

        boolFunctionTypeSet[2][2] = true;

        boolFunctionTypeSet[3][3] = true;

        boolFunctionTypeSet[4][0] = true;
        boolFunctionTypeSet[4][1] = true;

        boolFunctionTypeSet[5][0] = true;
        boolFunctionTypeSet[5][3] = true;

        boolFunctionTypeSet[6][1] = true;
        boolFunctionTypeSet[6][2] = true;

        boolFunctionTypeSet[7][1] = true;

    }

    /**
     * @param snp
     * @param network
     */
    private void setResourceDistribution4SNDLib(SubStrateNetworkParameter snp, Network network)
    {
        // node computation
        for (int i = 0; i < this.nodeSize; i++)
        {
            this.nodeComputationCapacity[i] = (int) (snp.nodeComputationMinimum
                    + Math.random() * (snp.nodeComputationMaximum - snp.nodeComputationMinimum));
        }

        // edge bandwith
        Iterator<Node> fromNodeSetIte = network.nodes().iterator();

        for (int i = 0; i < this.nodeSize; i++)
        {
            Node fromNode = fromNodeSetIte.next();
            for (int j = 0; j < i; j++)
            {

                Iterator<Node> toNodeSetIte = network.nodes().iterator();
                Node toNode = toNodeSetIte.next();

                for (Iterator<Link> edgeIte = network.links().iterator(); edgeIte.hasNext();)
                {
                    Link link = edgeIte.next();

                    if (link.getFirstNode().getId().equals(fromNode.getId())
                            && link.getSecondNode().getId().equals(toNode.getId()))
                    {
                        this.topology[i][j] = this.topology[j][i] = true;
                    }
                    if (link.getFirstNode().getId().equals(toNode.getId())
                            && link.getSecondNode().getId().equals(fromNode.getId()))
                    {
                        this.topology[i][j] = this.topology[j][i] = true;
                    }
                }
            }
        }
        for (int i = 0; i < this.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if (this.topology[i][j])
                {
                    this.edgeSize++;
                    this.edgeBandwithCapacity[i][j] = (int) (snp.edgeBandwithMinimum
                            + Math.random() * (snp.edgeBandwithMaximum - snp.edgeBandwithMinimum));
                    this.edgeBandwithCapacity[j][i] = this.edgeBandwithCapacity[i][j];
                }
            }
        }

        // service
        for (int i = 0; i < this.nodeSize; i++)
        {
            for (int j = 0; j < this.functionNum; j++)
            {
                if (Math.random() < snp.functionTypeProbability)
                {
                    this.boolFunctionTypeSet[i][j] = true;
                    vectorFunctionTypeSet.get(i).addElement(j);
                }
            }
            if (vectorFunctionTypeSet.get(i).size() == 0)
            {
                int index = (int) ((this.functionNum - 1) * Math.random());
                this.boolFunctionTypeSet[i][index] = true;
                vectorFunctionTypeSet.get(i).addElement(index);
            }
        }

    }

    /**
     * getSubstrateRemainComputaion4EVN.
     * 
     * @param nodeloc
     *            nodeloc
     * @param isShared
     *            isShared
     * @return int
     */
    public int getSubstrateRemainComputaion4SurVirNet(int nodeloc, boolean isShared)
    {
        int remainComputation;
        if (isShared)
        {
            remainComputation = this.nodeComputationCapacity[nodeloc] - this.nodeComputation4Crital[nodeloc]
                    - this.nodeComputation4Temp[nodeloc];
        } else
        {
            remainComputation = this.nodeComputationCapacity[nodeloc] - this.nodeComputation4Crital[nodeloc]
                    - this.nodeComputation4Temp[nodeloc] - this.nodeComputation4SurvivalUnsharedBackupSum[nodeloc];
        }
        if (remainComputation < 0)
        {
            substrateNetworkLog.error(this.algorihtm.algorithmName + " getSubstrateRemainComputaion4SurVirNet");
        }
        return remainComputation;
    }

    /**
     * getSubstrateRemainComputaion4VirNet.
     * 
     * @param nodeloc
     *            nodeloc
     * @param isShared
     *            isShared
     * @return RemainComputaion
     */
    public int getSubstrateRemainComputaion4VirNet(int nodeloc, boolean isShared)
    {
        int remainComputation = 0;
        if (isShared)
        {
            int maxShare = 0;
            for (int i = 0; i < this.surVirNetIndexSet4sNode.get(nodeloc).size(); i++)
            {
                int ithSurVirNet = this.surVirNetIndexSet4sNode.get(nodeloc).get(i);
                int temp = 0;
                if ((this.virNetSet.get(this.surVirNetSet.get(ithSurVirNet).virNet.index) != null)
                        && this.virNetSet.get(this.surVirNetSet.get(ithSurVirNet).virNet.index).isRunning
                        && this.surVirNetSet.get(ithSurVirNet).isSucceedEmbed)
                {
                    for (int j = 0; j < this.surVirNetSet.get(ithSurVirNet).nodeSize; j++)
                    {
                        if (nodeloc == this.surVirNetSet.get(ithSurVirNet).surNode2subNode[j])
                        {
                            // one substrate network node map multiple virtual
                            // network request and network node
                            temp += this.surVirNetSet.get(ithSurVirNet).nodeComputation4Backup[j];
                        }
                    }
                }

                maxShare = Math.max(maxShare, temp);
            }
            remainComputation = this.nodeComputationCapacity[nodeloc] - this.nodeComputation4Crital[nodeloc]
                    - this.nodeComputation4Temp[nodeloc] - maxShare;

        } else
        {
            remainComputation = this.nodeComputationCapacity[nodeloc] - this.nodeComputation4Crital[nodeloc]
                    - this.nodeComputation4Temp[nodeloc] - this.nodeComputation4SurvivalUnsharedBackupSum[nodeloc];
        }

        if (remainComputation < 0)
        {
            substrateNetworkLog.error(this.algorihtm.algorithmName + " getSubstrateRemainComputaion4VirNet");
        }
        return remainComputation;
    }

    /**
     * getSubStrateRemainBandwith4SurVirNet.
     * 
     * @param from
     *            from
     * @param to
     *            to
     * @param isShared
     *            isShared
     * @return int
     */
    public int getSubStrateRemainBandwith4SurVirNet(int from, int to, boolean isShared)
    {
        int remain = 0;
        if (isShared)
        {
            remain = this.edgeBandwithCapacity[from][to] - this.edgeBandwith4Crital[from][to]
                    - this.edgeBandwith4Temp[from][to];
        } else
        {
            remain = this.edgeBandwithCapacity[from][to] - this.edgeBandwith4Crital[from][to]
                    - this.edgeBandwith4Temp[from][to] - this.edgeBandwith4SurvivalUnsharedBackupSum[from][to];
        }
        return remain;
    }

    /**
     * @param from
     * @param to
     * @param isShared
     * @return
     */
    public int getSubStrateRemainBandwith4VirNet(int from, int to, boolean isShared)
    {
        int remain = 0;
        if (isShared)
        {
            int maxShare = 0;
            for (int i = 0; i < this.surVirNetIndexSet4sEdge.get(from).get(to).size(); i++)
            {
                int ithSeVN = this.surVirNetIndexSet4sEdge.get(from).get(to).get(i);

                int tempBandwith = 0;
                if ((this.virNetSet.get(this.surVirNetSet.get(ithSeVN).virNet.index) != null)
                        && this.virNetSet.get(this.surVirNetSet.get(ithSeVN).virNet.index).isRunning
                        && this.surVirNetSet.get(ithSeVN).isSucceedEmbed)
                {
                    for (int p = 0; p < this.surVirNetSet.get(ithSeVN).nodeSize; p++)
                    {
                        for (int q = 0; q < this.surVirNetSet.get(ithSeVN).nodeSize; q++)
                        {
                            for (int t = 0; t < (this.surVirNetSet.get(ithSeVN).surEdge2SubPath.get(p).get(q).size()
                                    - 1); t++)
                            {
                                if ((from == this.surVirNetSet.get(ithSeVN).surEdge2SubPath.get(p).get(q).get(t))
                                        && (to == this.surVirNetSet.get(ithSeVN).surEdge2SubPath.get(p).get(q)
                                                .get(t + 1)))
                                {

                                    tempBandwith += this.surVirNetSet.get(ithSeVN).edgeBandwith4Backup[p][q];
                                }
                            }
                        }
                    }
                }
                maxShare = Math.max(maxShare, tempBandwith);
            }
            remain = this.edgeBandwithCapacity[from][to] - this.edgeBandwith4Crital[from][to]
                    - this.edgeBandwith4Temp[from][to] - maxShare;
        } else
        {
            remain = this.edgeBandwithCapacity[from][to] - this.edgeBandwith4Crital[from][to]
                    - this.edgeBandwith4Temp[from][to] - this.edgeBandwith4SurvivalUnsharedBackupSum[from][to];
        }
        if (remain < 0)
        {
            substrateNetworkLog.error(this.algorihtm.algorithmName + " getSubStrateRemainBandwith4VirNet");
        }
        return remain;
    }

    /**
     * setResourceDistribution.
     * 
     * @param snp
     *            snp
     */
    private void setResourceDistribution4RandomTopo(SubStrateNetworkParameter snp)
    {
        // node computation
        for (int i = 0; i < this.nodeSize; i++)
        {
            this.nodeComputationCapacity[i] = (int) (snp.nodeComputationMinimum
                    + Math.random() * (snp.nodeComputationMaximum - snp.nodeComputationMinimum));
        }

        // edge bandwith
        for (int i = 0; i < this.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if (Math.random() < snp.node2nodeProbability)
                {
                    this.topology[i][j] = this.topology[j][i] = true;
                }
            }
        }
        for (int i = 0; i < this.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if (this.topology[i][j])
                {
                    this.edgeSize++;
                    this.edgeBandwithCapacity[i][j] = (int) (snp.edgeBandwithMinimum
                            + Math.random() * (snp.edgeBandwithMaximum - snp.edgeBandwithMinimum));
                    this.edgeBandwithCapacity[j][i] = this.edgeBandwithCapacity[i][j];
                }
            }
        }

        // service
        for (int i = 0; i < this.nodeSize; i++)
        {
            for (int j = 0; j < this.functionNum; j++)
            {
                if (Math.random() < snp.functionTypeProbability)
                {
                    this.boolFunctionTypeSet[i][j] = true;
                    vectorFunctionTypeSet.get(i).addElement(j);
                }
            }
            if (vectorFunctionTypeSet.get(i).size() == 0)
            {
                int index = (int) ((this.functionNum - 1) * Math.random());
                this.boolFunctionTypeSet[i][index] = true;
                vectorFunctionTypeSet.get(i).addElement(index);
            }
        }

    }

    /**
     * setResourceDistribution.
     * 
     * @param snp
     *            snp
     */
    private void setResourceDistribution4DataCenter(SubStrateNetworkParameter snp)
    {
        // node computation
        for (int i = 0; i < this.nodeSize; i++)
        {
            // core switch
            if (i == 0)
            {
                this.nodeComputationCapacity[i] = Integer.MAX_VALUE;
            } else
            {
                this.nodeComputationCapacity[i] = Parameter.DataCenterPMSlots;
            }

        }

        // edge bandwith
        for (int i = 1; i <= Parameter.DataCenterAry; i++)
        {
            this.topology[i][0] = this.topology[0][i] = true;
        }

        int kdouble = Parameter.DataCenterAry * Parameter.DataCenterAry;
        for (int i = (1 + Parameter.DataCenterAry); i <= (Parameter.DataCenterAry + kdouble); i++)
        {
            if ((i % (Parameter.DataCenterAry)) == 0)
            {
                this.topology[i][(i / Parameter.DataCenterAry)
                        - 1] = this.topology[(i / Parameter.DataCenterAry) - 1][i] = true;
            } else
            {
                this.topology[i][i / Parameter.DataCenterAry] = this.topology[i / Parameter.DataCenterAry][i] = true;
            }
        }

        int ktriple = Parameter.DataCenterAry * Parameter.DataCenterAry * Parameter.DataCenterAry;
        for (int i = (Parameter.DataCenterAry + kdouble + 1); i <= (Parameter.DataCenterAry + kdouble + ktriple); i++)
        {
            if ((i % (kdouble)) == 0)
            {
                this.topology[i][(i / kdouble) - 1] = this.topology[(i / kdouble) - 1][i] = true;
            } else
            {
                this.topology[i][i / kdouble] = this.topology[i / kdouble][i] = true;
            }
        }

        for (int i = 0; i < this.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if (this.topology[i][j])
                {
                    this.edgeSize++;
                    if (i >= (1 + Parameter.DataCenterAry + kdouble))
                    {
                        this.edgeBandwithCapacity[i][j] = Parameter.DataCenterToR2PM;
                    } else
                    {
                        this.edgeBandwithCapacity[i][j] = Parameter.DataCenterCore2Aggregation;
                    }

                    this.edgeBandwithCapacity[j][i] = this.edgeBandwithCapacity[i][j];
                }
            }
        }

        // service
        for (int i = 0; i < this.nodeSize; i++)
        {
            // core
            if (i == 0)
            {
                this.boolFunctionTypeSet[i][0] = true;
                vectorFunctionTypeSet.get(i).addElement(0);
            }

            // aggreation,ToR
            if ((i >= 1) && (i <= (Parameter.DataCenterAry + kdouble)))
            {
                this.boolFunctionTypeSet[i][1] = true;
                vectorFunctionTypeSet.get(i).addElement(1);
            }

            // PM
            if (i >= (1 + Parameter.DataCenterAry + kdouble))
            {
                this.boolFunctionTypeSet[i][2] = true;
                vectorFunctionTypeSet.get(i).addElement(2);
            }
        }

    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        SubstrateNetwork sn = (SubstrateNetwork) super.clone();
        sn.nodeComputationCapacity = new int[sn.nodeSize];
        sn.nodeComputationCapacity = Arrays.copyOf(this.nodeComputationCapacity, this.nodeSize);
        sn.nodeComputation4Crital = new int[sn.nodeSize];
        sn.nodeComputation4SurvivalUnsharedBackupSum = new int[sn.nodeSize];
        sn.nodeComputation4Temp = new int[sn.nodeSize];

        // sn.VNQIndexSet4sNode = (Vector<Vector<Integer>>) ((SubstrateNetwork)
        // super.clone()).VNQIndexSet4sNode.clone();
        // sn.EVNIndexSet4sNode = (Vector<Vector<Integer>>) ((SubstrateNetwork)
        // super.clone()).VNQIndexSet4sNode.clone();
        sn.virNetIndexSet4sNode = new Vector<Vector<Integer>>();
        sn.surVirNetIndexSet4sNode = new Vector<Vector<Integer>>();
        for (int i = 0; i < sn.nodeSize; i++)
        {
            sn.virNetIndexSet4sNode.addElement(new Vector<Integer>());
            sn.surVirNetIndexSet4sNode.addElement(new Vector<Integer>());
        }

        sn.topology = new boolean[sn.nodeSize][sn.nodeSize];
        sn.edgeBandwithCapacity = new int[sn.nodeSize][sn.nodeSize];
        for (int i = 0; i < this.nodeSize; i++)
        {
            sn.topology[i] = Arrays.copyOf(this.topology[i], this.nodeSize);
            sn.edgeBandwithCapacity[i] = Arrays.copyOf(this.edgeBandwithCapacity[i], this.nodeSize);
        }
        sn.edgeBandwith4Crital = new int[sn.nodeSize][sn.nodeSize];
        sn.edgeBandwith4SurvivalUnsharedBackupSum = new int[sn.nodeSize][sn.nodeSize];
        sn.edgeBandwith4Temp = new int[sn.nodeSize][sn.nodeSize];

        // sn.VNCollection = (Vector<VirtualNetwork>) ((SubstrateNetwork)
        // super.clone()).VNCollection.clone();
        // sn.EVNCollection = (Vector<EnhancedVirtualNetwork>)
        // ((SubstrateNetwork) super.clone()).EVNCollection.clone();

        sn.virNetSet = new Vector<VirtualNetwork>();
        sn.surVirNetSet = new Vector<SurvivalVirtualNetwork>();

        // sn.VNQIndexSet4Edge = (Vector<Vector<Vector<Integer>>>)
        // ((SubstrateNetwork) super.clone()).VNQIndexSet4Edge
        // .clone();
        // sn.EVNIndexSet4Edge = (Vector<Vector<Vector<Integer>>>)
        // ((SubstrateNetwork) super.clone()).EVNIndexSet4Edge
        // .clone();
        sn.virNetIndexSet4sEdge = new Vector<Vector<Vector<Integer>>>();
        sn.surVirNetIndexSet4sEdge = new Vector<Vector<Vector<Integer>>>();

        for (int i = 0; i < nodeSize; i++)
        {
            sn.virNetIndexSet4sEdge.addElement(new Vector<Vector<Integer>>());
            sn.surVirNetIndexSet4sEdge.addElement(new Vector<Vector<Integer>>());
            for (int j = 0; j < nodeSize; j++)
            {
                sn.virNetIndexSet4sEdge.get(i).add(new Vector<Integer>());
                sn.surVirNetIndexSet4sEdge.get(i).add(new Vector<Integer>());
            }
        }
        return sn;
    }

    /**
     * clearTempResource.
     */
    public void clearTempResource()
    {
        for (int i = 0; i < this.nodeSize; i++)
        {
            this.nodeComputation4Temp[i] = 0;
            for (int j = 0; j < i; j++)
            {
                this.edgeBandwith4Temp[i][j] = this.edgeBandwith4Temp[j][i] = 0;
            }
        }
    }

}
