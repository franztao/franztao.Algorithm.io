/**
 * 
 */

package sevn;

import java.util.Random;
import java.util.Vector;

import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.PoissonDistribution;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import algorithm.SeVNAlgorithm;
import substrateNetwork.SubstrateNetworkt;
import virtualNetwork.VirtualNetwork;
import virtualNetwork.VirtualNetworkParameter;

/**
 * Experiment.
 * 
 * @author franz
 *
 */
public class Experiment
{
    // log
    private Logger experimentlogger = Logger.getLogger(Experiment.class.getName());

    private SubstrateNetworkt basicSubstrateNework;
    private VirtualNetworkParameter vnp;
    private Result[] algorithmResult;

    Vector<SeVNAlgorithm> algorithms;

    /**
     * Experiment .
     */
    public Experiment(SubstrateNetworkt sn, VirtualNetworkParameter vnp)
    {
        experimentlogger.setLevel(Parameter.logLevel);
        PropertyConfigurator.configure("log4j.properties");

        this.basicSubstrateNework = sn;
        this.vnp = vnp;
        this.algorithms = new Vector<SeVNAlgorithm>();

    }

    /**
     * startExperiment .
     * 
     * @param vnp
     * 
     */
    public void bootExperiment(int ithExper)
    {
        generateComparableAlgorithm(this.vnp);
        runComparableAlgorithmWithSameVirNet(ithExper);
        

    }

    /**
     * runComparableAlgorithmInSameVirNet.
     * 
     * @param ithExper
     *            running experimentTimes
     */
    private void runComparableAlgorithmWithSameVirNet(int ithExper)
    {
        for (int time = 0; time <= Parameter.SubstrateNewtorkRunTimeInterval; time++)
        {

            VirtualNetwork protoVirNet = generateProtoVirtualNetwork();

            experimentlogger.info("####### Time/Total Time: " + time + "/" + Parameter.SubstrateNewtorkRunTimeInterval);

            for (int alg = 0; alg < algorithms.size(); alg++)
            {

                if ((time % (Parameter.SubstrateNewtorkRunTimeInterval / Parameter.ExperimentPicturePlotNumber)) == 0)
                {
                    this.algorithmResult[alg].recordExperimentData(ithExper, algorithms.get(alg), time);
                }

                algorithms.get(alg).releaseResource(false);
                this.algorithmResult[alg].updateExperimentData(algorithms.get(alg), time);

                if ((0 == (time % Parameter.VirNetDuration)))
                {
                    long AppearNum;
                    double AppearProbability;
                    if (Parameter.RequestType == Parameter.RequestTypePossion)
                    {
                        PoissonDistribution dist = new PoissonDistribution(Parameter.PossionMean);
                        AppearNum = dist.sample();
                        AppearProbability = 1;
                    }

                    if (Parameter.RequestType == Parameter.RequestTypeGeometric)
                    {
                        AppearNum = Parameter.RequestPerTimeAppearNum;
                        AppearProbability = Parameter.RequestAppearProbability;
                    }

                    for (int r = 0; r < AppearNum; r++)
                    {
                        if ((Math.random() <= AppearProbability))
                        {
                            experimentlogger.info("+++++ Time/Total Time: " + time + "/"
                                    + Parameter.SubstrateNewtorkRunTimeInterval + "  Alg: " + alg);
                            algorithms.get(alg).generateAndProtectVirNet(protoVirNet);
                            this.algorithmResult[alg].updateExperimentDataAccumulate(algorithms.get(alg));
                        }
                    }
                }
            }
            experimentlogger.info("-----------------------------------------------------------------\n");
        }

        for (int alg = 0; alg < algorithms.size(); alg++)
        {
            algorithms.get(alg).releaseResource(true);
            algorithms.get(alg).isClearAllResource();
            this.algorithmResult[alg].writeExperimentDatatoFile(ithExper, algorithms.get(alg));
        }

    }

    /**
     * @return
     * 
     */
    private VirtualNetwork generateProtoVirtualNetwork()
    {
        VirtualNetwork protoVirNet = new VirtualNetwork(this.vnp);

        if (Parameter.TopologyType == Parameter.TopologyTypeRandom
                || Parameter.TopologyType == Parameter.TopologyTypeSNDLib)
        {
            constructSameVirNet4RandomTopo(protoVirNet);
        }
        if (Parameter.TopologyType == Parameter.TopologyTypeSample)
        {
            constructSameVirNet4Sample(protoVirNet);
        }

        if (Parameter.TopologyType == Parameter.TopologyTypeDataCenter)
        {
            constructSameVirNet4DataCenter(protoVirNet);
        }
        return protoVirNet;
    }

    /**
     * @param protoVirNet
     */
    private void constructSameVirNet4Sample(VirtualNetwork vn)
    {
        vn.leaveTime = (int) Parameter.SubstrateNewtorkRunTimeInterval;
        vn.nodeComputationDemand[0] = 2;
        vn.nodeComputationDemand[1] = 3;
        vn.nodeComputationDemand[2] = 5;
        vn.nodeComputationDemand[3] = 6;
        vn.virNode2subNode[0] = 0;
        vn.virNode2subNode[1] = 1;
        vn.virNode2subNode[2] = 2;
        vn.virNode2subNode[3] = 3;

        vn.virNode2subNode[0] = 0;
        vn.virNode2subNode[1] = 1;
        vn.virNode2subNode[2] = 2;
        vn.virNode2subNode[3] = 3;

        vn.edgeBandwithDemand[0][1] = 4;
        vn.edgeBandwithDemand[0][2] = 5;
        vn.edgeBandwithDemand[0][3] = 3;
        vn.edgeBandwithDemand[1][2] = 6;
        vn.edgeBandwithDemand[1][0] = 4;
        vn.edgeBandwithDemand[2][0] = 5;
        vn.edgeBandwithDemand[3][0] = 3;
        vn.edgeBandwithDemand[2][1] = 6;

        for (int i = 0; i < vn.nodeSize; i++)
        {
            for (int j = 0; j < vn.nodeSize; j++)
            {
                if (0 != vn.edgeBandwithDemand[i][j])
                {
                    vn.topology[i][j] = true;
                }
            }
        }

        vn.nodeFunctionType[0] = 0;
        vn.nodeFunctionType[1] = 1;
        vn.nodeFunctionType[2] = 2;
        vn.nodeFunctionType[3] = 3;

    }

    /**
     * constructSameVirNet4DataCenter.
     * 
     */
    private void constructSameVirNet4DataCenter(VirtualNetwork vn)
    {

        if (Parameter.VNRequestsLeaseType == Parameter.VNRequestsLeaseTypeExponential)
        {

            ExponentialDistribution dist = new ExponentialDistribution(Parameter.VNRequestsContinueTimeExponentialMean);
            vn.leaveTime = (((int) dist.sample()));
        }

        if (Parameter.VNRequestsLeaseType == Parameter.VNRequestsLeaseTypeUniform)
        {
            vn.leaveTime = ((int) (Parameter.VNRequestsContinueTimeMinimum + Math.random()
                    * (Parameter.VNRequestsContinueTimeMaximum - Parameter.VNRequestsContinueTimeMinimum)));
        }

        boolean[] isSamesNode = new boolean[this.basicSubstrateNework.nodeSize];

        // core
        vn.virNode2subNode[0] = 0;
        isSamesNode[0] = true;
        vn.nodeFunctionType[0] = 0;
        vn.nodeComputationDemand[0] = 0;

        for (int i = 1; i < vn.nodeSize; i++)
        {
            int snodeloc;
            do
            {
                snodeloc = (int) Math.ceil(1 + Parameter.DataCenterAry
                        + Parameter.DataCenterAry * Parameter.DataCenterAry + 1 + Math.random()
                                * (Parameter.DataCenterAry * Parameter.DataCenterAry * Parameter.DataCenterAry - 1));
                if (!isSamesNode[snodeloc])
                {
                    vn.virNode2subNode[i] = snodeloc;
                    isSamesNode[snodeloc] = true;
                    break;
                }
            } while (true);

            // service
            Random ran = new Random();

            int index = Math.abs(ran.nextInt())
                    % (this.basicSubstrateNework.vectorFunctionTypeSet.get(snodeloc).size());
            int nodeservice = this.basicSubstrateNework.vectorFunctionTypeSet.get(snodeloc).elementAt(index);
            vn.nodeFunctionType[i] = nodeservice;

            // node demand
            vn.nodeComputationDemand[i] = 1;

        }

        // edge demand
        for (int i = 1; i < vn.nodeSize; i++)
        {

            if (vn.virNode2subNode[i] != vn.virNode2subNode[0])
            {
                int distributeIthEdgeBandwith = (int) (2
                        + Math.ceil(Math.random() * (Parameter.DataCenterVNBandWidth)));
                vn.topology[i][0] = vn.topology[0][i] = true;
                vn.edgeBandwithDemand[i][0] = vn.edgeBandwithDemand[0][i] = distributeIthEdgeBandwith;
            }
        }

    }

    /**
     * constructSameVirNet.
     * 
     */
    public void constructSameVirNet4RandomTopo(VirtualNetwork vn)
    {

        if (Parameter.VNRequestsLeaseType == Parameter.VNRequestsLeaseTypeExponential)
        {
            ExponentialDistribution dist = new ExponentialDistribution(Parameter.VNRequestsContinueTimeExponentialMean);
            vn.leaveTime = (((int) dist.sample()));
        }
        if (Parameter.VNRequestsLeaseType == Parameter.VNRequestsLeaseTypeUniform)
        {
            vn.leaveTime = ((int) (Parameter.VNRequestsContinueTimeMinimum + Math.random()
                    * (Parameter.VNRequestsContinueTimeMaximum - Parameter.VNRequestsContinueTimeMinimum)));
        }
        boolean[] isSamesNode = new boolean[this.basicSubstrateNework.nodeSize];
        for (int i = 0; i < vn.nodeSize; i++)
        {
            int snodeloc;
            do
            {
                snodeloc = (int) Math.round(Math.random() * (this.basicSubstrateNework.nodeSize - 1));
                // every phisical mapped only one virtual node
                if (!isSamesNode[snodeloc])
                {
                    vn.virNode2subNode[i] = snodeloc;
                    isSamesNode[snodeloc] = true;
                    break;
                }
            } while (true);
            // service
            Random ran = new Random();
            int index = Math.abs(ran.nextInt())
                    % (this.basicSubstrateNework.vectorFunctionTypeSet.get(snodeloc).size());
            int nodeservice = this.basicSubstrateNework.vectorFunctionTypeSet.get(snodeloc).elementAt(index);
            vn.nodeFunctionType[i] = nodeservice;
            // node demand
            vn.nodeComputationDemand[i] = (int) (this.vnp.nodeComputationMinimum
                    + Math.ceil(Math.random() * (this.vnp.nodeComputationMaximum - this.vnp.nodeComputationMinimum)));
        }

        // edge demand
        for (int i = 0; i < vn.nodeSize; i++)
        {
            boolean haseEdge = false;
            for (int j = 0; j < i; j++)
            {
                if ((Math.random() < vnp.node2nodeProbability))
                {
                    haseEdge = true;
                    if (vn.virNode2subNode[i] != vn.virNode2subNode[j])
                    {
                        int distributeIthEdgeBandwith = (int) (vnp.edgeBandwithMinimum
                                + Math.ceil(Math.random() * (vnp.edgeBandwithMaximum - vnp.edgeBandwithMinimum)));
                        vn.topology[i][j] = vn.topology[j][i] = true;
                        vn.edgeBandwithDemand[i][j] = vn.edgeBandwithDemand[j][i] = distributeIthEdgeBandwith;
                    }
                }
            }
            if (!haseEdge && i != 0)
            {
                int toNode = (int) (Math.ceil(Math.random() * i) % i);
                int distributeIthEdgeBandwith = (int) (vnp.edgeBandwithMinimum
                        + Math.ceil(Math.random() * (vnp.edgeBandwithMaximum - vnp.edgeBandwithMinimum)));
                vn.topology[i][toNode] = vn.topology[toNode][i] = true;
                vn.edgeBandwithDemand[i][toNode] = vn.edgeBandwithDemand[toNode][i] = distributeIthEdgeBandwith;
            }
        }

    }

    /**
     * @param vnp
     *            vnp2
     * 
     */
    private void generateComparableAlgorithm(VirtualNetworkParameter vnp)
    {
        // NoShared Shared
        // FD FI
        // FD ILP EVSNR Min Ran
        // VNE can not able to compare with VNE algorithm
        SeVNAlgorithm alg;
        this.algorithms.clear();
        try
        {
            // SubstrateNetwork FD_Min_Shared = (SubstrateNetwork)
            // this.basicSubstrateNework.clone();
            // alg = new SeVN();
            // alg.setParameter("FD_Min_Shared_Heuristic", FD_Min_Shared, false,
            // Parameter.FailureDependent, true,
            // Parameter.Min);
            // this.algorithms.addElement(alg);
            //
            // SubstrateNetwork FD_Min_NoShared = (SubstrateNetwork)
            // this.basicSubstrateNework.clone();
            // alg = new SeVN();
            // alg.setParameter("FD_Min_NoShared_Heuristic", FD_Min_NoShared, false,
            // Parameter.FailureDependent, false,
            // Parameter.Min);
            // this.algorithms.addElement(alg);
            // SubstrateNetwork FI_Min_Shared = (SubstrateNetwork)
            // this.basicSubstrateNework.clone();
            // alg = new SeVN();
            // alg.setParameter("FI_Min_Shared_Heuristic", FI_Min_Shared, false,
            // Parameter.FailureIndependent, true,
            // Parameter.Min);
            // this.algorithms.addElement(alg);
            //
            // SubstrateNetwork FI_Min_NoShared = (SubstrateNetwork)
            // this.basicSubstrateNework.clone();
            // alg = new SeVN();
            // alg.setParameter("FI_Min_NoShared_Heuristic", FI_Min_NoShared, false,
            // Parameter.FailureIndependent, false,
            // Parameter.Min);
            // this.algorithms.addElement(alg);
            // SubstrateNetwork One2OneProtection_Min_NoShared = (SubstrateNetwork)
            // this.basicSubstrateNework.clone();
            // alg = new SeVN();
            // alg.setParameter("One2OneProtection_Ran_NoShared",
            // One2OneProtection_Min_NoShared, false,
            // Parameter.One2OneProtection, false, Parameter.Min);
            // this.algorithms.addElement(alg);
            //
            // SubstrateNetwork One2OneProtection_Min_Shared = (SubstrateNetwork)
            // this.basicSubstrateNework.clone();
            // alg = new SeVN();
            // alg.setParameter("One2OneProtection_Min_Shared",
            // One2OneProtection_Min_Shared, false,
            // Parameter.One2OneProtection, true, Parameter.Min);
            // this.algorithms.addElement(alg);

            SubstrateNetworkt FD_Ran_Shared = (SubstrateNetworkt) this.basicSubstrateNework.clone();
            alg = new SeVNAlgorithm();
            alg.setParameter("FD_Ran_Shared_Heuristic", FD_Ran_Shared, false, Parameter.FailureDependent, true,
                    Parameter.Ran);
            this.algorithms.addElement(alg);

            SubstrateNetworkt FD_Ran_NoShared = (SubstrateNetworkt) this.basicSubstrateNework.clone();
            alg = new SeVNAlgorithm();
            alg.setParameter("FD_Ran_NoShared_Heuristic", FD_Ran_NoShared, false, Parameter.FailureDependent, false,
                    Parameter.Ran);
            this.algorithms.addElement(alg);

            SubstrateNetworkt FI_Ran_Shared = (SubstrateNetworkt) this.basicSubstrateNework.clone();
            alg = new SeVNAlgorithm();
            alg.setParameter("FI_Ran_Shared_Heuristic", FI_Ran_Shared, false, Parameter.FailureIndependent, true,
                    Parameter.Ran);
            this.algorithms.addElement(alg);

            SubstrateNetworkt FI_Ran_NoShared = (SubstrateNetworkt) this.basicSubstrateNework.clone();
            alg = new SeVNAlgorithm();
            alg.setParameter("FI_Ran_NoShared_Heuristic", FI_Ran_NoShared, false, Parameter.FailureIndependent, false,
                    Parameter.Ran);
            this.algorithms.addElement(alg);

            SubstrateNetworkt One2OneProtection_Ran_NoShared = (SubstrateNetworkt) this.basicSubstrateNework.clone();
            alg = new SeVNAlgorithm();
            alg.setParameter("One2OneProtection_Ran_NoShared", One2OneProtection_Ran_NoShared, false,
                    Parameter.One2OneProtection, false, Parameter.Ran);
            this.algorithms.addElement(alg);

            SubstrateNetworkt One2OneProtection_Ran_Shared = (SubstrateNetworkt) this.basicSubstrateNework.clone();
            alg = new SeVNAlgorithm();
            alg.setParameter("One2OneProtection_Ran_Shared", One2OneProtection_Ran_Shared, false,
                    Parameter.One2OneProtection, true, Parameter.Ran);
            this.algorithms.addElement(alg);

            SubstrateNetworkt virNet = (SubstrateNetworkt) this.basicSubstrateNework.clone();
            alg = new SeVNAlgorithm();
            alg.setParameter("VirNet", virNet, false, Parameter.FailureIndependent, false, Parameter.Ran);
            this.algorithms.addElement(alg);

            // SubstrateNetwork FD_ILP_Shared_Exact = (SubstrateNetwork)
            // this.basicSubstrateNework.clone();
            // alg = new Algorithm();
            // alg.setParameter("sn_FD_ILP_Shared", FD_ILP_Shared_Exact, true,
            // Parameter.FailureDependent,
            // true, -1);
            // this.algorithms.addElement(alg);
            //
            // SubstrateNetwork FD_ILP_NoShared_Exact = (SubstrateNetwork)
            // this.basicSubstrateNework.clone();
            // alg = new Algorithm();
            // alg.setParameter("sn_FD_ILP_NoShared", FD_ILP_NoShared_Exact, true,
            // Parameter.FailureDependent, false, -1);
            // this.algorithms.addElement(alg);
        } catch (CloneNotSupportedException e)
        {
            experimentlogger.error("Fail to construct various algorithms");
            e.printStackTrace();
        }
        experimentlogger.info("Succeed to initialize various type of algorithms\n");

        this.algorithmResult = new Result[algorithms.size()];
        for (int i = 0; i < algorithms.size(); i++)
        {
            algorithmResult[i] = new Result();
            algorithms.get(i).vnp = (vnp);
        }

    }

}
