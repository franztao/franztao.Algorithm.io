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

import algorithm.SeVN;
import substrateNetwork.SubstrateNetwork;
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
    private Logger experimentlogger = Logger.getLogger(Experiment.class);

    private SubstrateNetwork basicSubstrateNework;
    private VirtualNetworkParameter vnp;
    private Result[] algorithmResult;

    Vector<SeVN> algorithms;

    /**
     * Experiment .
     */
    public Experiment(SubstrateNetwork sn, VirtualNetworkParameter vnp)
    {
        this.basicSubstrateNework = sn;
        this.vnp = vnp;
        this.algorithms = new Vector<SeVN>();
        // this.result = result;
        PropertyConfigurator.configure("log4j.properties");
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
        // this.result.recordExperimentParameter(0, algorithms);

    }

    /**
     * runComparableAlgorithmInSameVirNet.
     * 
     * @param experimentTimes
     *            running experimentTimes
     */
    private void runComparableAlgorithmWithSameVirNet(int experimentTimes)
    {
        for (int time = 0; time <= Parameter.SubstrateNewtorkRunTimeInterval; time++)
        {
            VirtualNetwork protoVirNet = new VirtualNetwork(this.vnp);

            if (this.vnp.topologyType == Parameter.TopologyTypeRandom
                    || this.vnp.topologyType == Parameter.TopologyTypeSNDLib)
            {
                constructSameVirNet4RandomTopo(protoVirNet);
            }

            if (this.vnp.topologyType == Parameter.TopologyTypeDataCenter)
            {
                constructSameVirNet4DataCenter(protoVirNet);
            }

            experimentlogger.info("####### Time/Total Time: " + time + "/" + Parameter.SubstrateNewtorkRunTimeInterval);

            for (int alg = 0; alg < algorithms.size(); alg++)
            {

                if ((time % (Parameter.SubstrateNewtorkRunTimeInterval / Parameter.ExperimentPicturePlotNumber)) == 0)
                {
                    this.algorithmResult[alg].recordExperimentData(experimentTimes, algorithms.get(alg), time);
                }

                algorithms.get(alg).releaseResource(false);
                this.algorithmResult[alg].updateExperimentData(algorithms.get(alg));

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
                        if ((Math.random() < AppearProbability))
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
            this.algorithmResult[alg].writeExperimentDatatoFile(experimentTimes, algorithms.get(alg));
        }

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
            vn.setLeaveTime(((int) dist.sample()));
        }

        if (Parameter.VNRequestsLeaseType == Parameter.VNRequestsLeaseTypeUniform)
        {
            vn.setLeaveTime((int) (Parameter.VNRequestsContinueTimeMinimum + Math.random()
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
                snodeloc = (int) Math.round(1 + Parameter.DataCenterAry
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

            int index = Math.abs(ran.nextInt()) % (this.basicSubstrateNework.vectorServiceTypeSet.get(snodeloc).size());
            int nodeservice = this.basicSubstrateNework.vectorServiceTypeSet.get(snodeloc).elementAt(index);
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
                        + Math.round(Math.random() * (Parameter.DataCenterVNBandWidth)));
                vn.topology[i][0] = vn.topology[0][i] = true;
                vn.edgeBandwithDemand[i][0] = vn.edgeBandwithDemand[0][i] = distributeIthEdgeBandwith;
            }
        }

    }

    /**
     * constructSameVirNet.
     * 
     */
    private void constructSameVirNet4RandomTopo(VirtualNetwork vn)
    {

        if (Parameter.VNRequestsLeaseType == Parameter.VNRequestsLeaseTypeExponential)
        {
            ExponentialDistribution dist = new ExponentialDistribution(Parameter.VNRequestsContinueTimeExponentialMean);
            vn.setLeaveTime(((int) dist.sample()));
        }
        if (Parameter.VNRequestsLeaseType == Parameter.VNRequestsLeaseTypeUniform)
        {
            vn.setLeaveTime((int) (Parameter.VNRequestsContinueTimeMinimum + Math.random()
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
            int index = Math.abs(ran.nextInt()) % (this.basicSubstrateNework.vectorServiceTypeSet.get(snodeloc).size());
            int nodeservice = this.basicSubstrateNework.vectorServiceTypeSet.get(snodeloc).elementAt(index);
            vn.nodeFunctionType[i] = nodeservice;
            // node demand
            vn.nodeComputationDemand[i] = (int) (this.vnp.nodeComputationMinimum
                    + Math.round(Math.random() * (this.vnp.nodeComputationMaximum - this.vnp.nodeComputationMinimum)));

        }

        // edge demand
        for (int i = 0; i < vn.nodeSize; i++)
        {
            for (int j = 0; j < i; j++)
            {
                if ((Math.random() < vnp.node2nodeProbability))
                {
                    if (vn.virNode2subNode[i] != vn.virNode2subNode[j])
                    {
                        int distributeIthEdgeBandwith = (int) (vnp.edgeBandwithMinimum
                                + Math.round(Math.random() * (vnp.edgeBandwithMaximum - vnp.edgeBandwithMinimum)));
                        vn.topology[i][j] = vn.topology[j][i] = true;
                        vn.edgeBandwithDemand[i][j] = vn.edgeBandwithDemand[j][i] = distributeIthEdgeBandwith;
                    }
                }
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
        SeVN alg;
        this.algorithms.clear();
        try
        {
            SubstrateNetwork FD_Min_Shared = (SubstrateNetwork) this.basicSubstrateNework.clone();
            alg = new SeVN();
            alg.setParameter("FD_Min_Shared_Heuristic", FD_Min_Shared, false, Parameter.FailureDependent, true,
                    Parameter.Min);
            this.algorithms.addElement(alg);

            SubstrateNetwork FD_Min_NoShared = (SubstrateNetwork) this.basicSubstrateNework.clone();
            alg = new SeVN();
            alg.setParameter("FD_Min_NoShared_Heuristic", FD_Min_NoShared, false, Parameter.FailureDependent, false,
                    Parameter.Min);
            this.algorithms.addElement(alg);

            // SubstrateNetwork FD_Ran_Shared = (SubstrateNetwork)
            // this.basicSubstrateNework.clone();
            // alg = new SeVN();
            // alg.setParameter("FD_Ran_Shared_Heuristic", FD_Ran_Shared, false,
            // Parameter.FailureDependent, true,
            // Parameter.Ran);
            // this.algorithms.addElement(alg);
            //
            // SubstrateNetwork FD_Ran_NoShared = (SubstrateNetwork)
            // this.basicSubstrateNework.clone();
            // alg = new SeVN();
            // alg.setParameter("FD_Ran_NoShared_Heuristic", FD_Ran_NoShared, false,
            // Parameter.FailureDependent, false,
            // Parameter.Ran);
            // this.algorithms.addElement(alg);

            SubstrateNetwork FI_Min_Shared = (SubstrateNetwork) this.basicSubstrateNework.clone();
            alg = new SeVN();
            alg.setParameter("FI_Min_Shared_Heuristic", FI_Min_Shared, false, Parameter.FailureIndependent, true,
                    Parameter.Min);
            this.algorithms.addElement(alg);

            SubstrateNetwork FI_Min_NoShared = (SubstrateNetwork) this.basicSubstrateNework.clone();
            alg = new SeVN();
            alg.setParameter("FI_Min_NoShared_Heuristic", FI_Min_NoShared, false, Parameter.FailureIndependent, false,
                    Parameter.Min);
            this.algorithms.addElement(alg);

            // SubstrateNetwork FI_Ran_Shared = (SubstrateNetwork)
            // this.basicSubstrateNework.clone();
            // alg = new SeVN();
            // alg.setParameter("FI_Ran_Shared_Heuristic", FI_Ran_Shared, false,
            // Parameter.FailureIndependent, true,
            // Parameter.Ran);
            // this.algorithms.addElement(alg);
            //
            // SubstrateNetwork FI_Ran_NoShared = (SubstrateNetwork)
            // this.basicSubstrateNework.clone();
            // alg = new SeVN();
            // alg.setParameter("FI_Ran_NoShared_Heuristic", FI_Ran_NoShared, false,
            // Parameter.FailureIndependent, false,
            // Parameter.Ran);
            // this.algorithms.addElement(alg);

            SubstrateNetwork virNet = (SubstrateNetwork) this.basicSubstrateNework.clone();
            alg = new SeVN();
            alg.setParameter("VirNet", virNet, false, Parameter.FailureIndependent, false, Parameter.Ran);
            this.algorithms.addElement(alg);

            // SubstrateNetwork One2OneProtection_Ran_NoShared = (SubstrateNetwork)
            // this.basicSubstrateNework.clone();
            // alg = new SeVN();
            // alg.setParameter("One2OneProtection_Ran_NoShared",
            // One2OneProtection_Ran_NoShared, false,
            // Parameter.One2OneProtection, false, Parameter.Ran);
            // this.algorithms.addElement(alg);
            //
            // SubstrateNetwork One2OneProtection_Ran_Shared = (SubstrateNetwork)
            // this.basicSubstrateNework.clone();
            // alg = new SeVN();
            // alg.setParameter("One2OneProtection_Ran_Shared",
            // One2OneProtection_Ran_Shared, false,
            // Parameter.One2OneProtection, true, Parameter.Ran);
            // this.algorithms.addElement(alg);

            SubstrateNetwork One2OneProtection_Min_NoShared = (SubstrateNetwork) this.basicSubstrateNework.clone();
            alg = new SeVN();
            alg.setParameter("One2OneProtection_Ran_NoShared", One2OneProtection_Min_NoShared, false,
                    Parameter.One2OneProtection, false, Parameter.Min);
            this.algorithms.addElement(alg);

            SubstrateNetwork One2OneProtection_Min_Shared = (SubstrateNetwork) this.basicSubstrateNework.clone();
            alg = new SeVN();
            alg.setParameter("One2OneProtection_Min_Shared", One2OneProtection_Min_Shared, false,
                    Parameter.One2OneProtection, true, Parameter.Min);
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
            algorithms.get(i).setVnp(vnp);
        }

    }

}
