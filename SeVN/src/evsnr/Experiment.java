/**
 * 
 */

package evsnr;

import java.util.Vector;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import substratenetwork.SubstrateNetwork;
import virtualnetwork.VirtualNetwork;
import virtualnetwork.VirtualNetworkParameter;

/**
 * Experiment.
 * 
 * @author franz
 *
 */
public class Experiment {
  private Logger logger = Logger.getLogger(Experiment.class);

  private SubstrateNetwork basicSubstrateNework;
  private VirtualNetworkParameter vnp;
  private Result result;
  private Result[] algorithmResult;

  Vector<Algorithm> algorithms;

  /**
   * Experiment .
   */
  public Experiment(SubstrateNetwork sn, VirtualNetworkParameter vnp, Result result) {
    this.basicSubstrateNework = sn;
    this.vnp = vnp;
    this.algorithms = new Vector<Algorithm>();
    this.result = result;
    PropertyConfigurator.configure("log4j.properties");
    // BasicConfigurator.configure();

  }

  /**
   * startExperiment .
   * 
   * @param vnp
   * 
   */
  public void bootExperiment() {
    for (int i = 0; i < Parameter.ExperimentTimes; i++) {
      generateComparableAlgorithm(this.vnp);
      if (Parameter.IsSameVirNet4EveryTime) {
        runComparableAlgorithmInSameVirNet(i);
      } else {
        runComparableAlgorithm(i);
      }

    }
    this.result.recordExperimentParameter(0, algorithms);
  }

  /**
   * runComparableAlgorithmInSameVirNet.
   * 
   * @param experimentTimes
   *          running experimentTimes
   */
  private void runComparableAlgorithmInSameVirNet(int experimentTimes) {
    for (int time = 0; time <= Parameter.SubstrateNewtorkRunTimeInterval; time++) {
      VirtualNetwork sameVirNet = new VirtualNetwork(this.vnp);
      constructSameVirNet(sameVirNet);

      logger.info("###################### Time/Total Time: " + time + "/"
          + Parameter.SubstrateNewtorkRunTimeInterval);

      for (int alg = 0; alg < algorithms.size(); alg++) {

        if ((time % (Parameter.SubstrateNewtorkRunTimeInterval
            / Parameter.ExperimentPicturePlotNumber)) == 0) {
          this.algorithmResult[alg].recordExperimentData(experimentTimes, algorithms.get(alg),
              time);
        }

        algorithms.get(alg).releaseResource(false);
        this.algorithmResult[alg].updateExperimentData(algorithms.get(alg));
        if ((0 == (time % Parameter.VirNetDuration))) {
          for (int r = 0; r < Parameter.RequestPerTimeAppearNum; r++) {
            if ((Math.random() < Parameter.RequestAppearProbability)) {
              logger.info("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
              algorithms.get(alg).generateAndProtectVirNet(sameVirNet);
              this.algorithmResult[alg].updateExperimentDataAccumulate(algorithms.get(alg));
            }
          }
        }
      }
      logger.info("-----------------------------------------------------------------\n");
    }

    for (int alg = 0; alg < algorithms.size(); alg++) {
      algorithms.get(alg).releaseResource(true);
      algorithms.get(alg).isClearAllResource();
      this.algorithmResult[alg].writeExperimentDatatoFile(experimentTimes, algorithms.get(alg));
    }

  }

  /**
   * constructSameVirNet.
   * 
   * @param sameVN
   *          sameVN
   * @param vnp
   *          vnp
   */
  private void constructSameVirNet(VirtualNetwork vn) {
    vn.setLeaveTime((int) (Parameter.VNRequestsContinueTimeMinimum + Math.random()
        * (Parameter.VNRequestsContinueTimeMaximum - Parameter.VNRequestsContinueTimeMinimum)));
    boolean[] isSamesNode = new boolean[this.basicSubstrateNework.nodeSize];
    for (int i = 0; i < vn.nodeSize; i++) {
      int snodeloc;
      do {
        snodeloc = (int) Math.round(Math.random() * (this.basicSubstrateNework.nodeSize - 1));
        if (!isSamesNode[snodeloc]) {
          vn.virNode2subNode[i] = snodeloc;
          isSamesNode[snodeloc] = true;
          break;
        }
      } while (true);
      // service
      int index = (int) Math.random()
          * (this.basicSubstrateNework.vectorServiceTypeSet.get(snodeloc).size() - 1);
      int nodeservice = this.basicSubstrateNework.vectorServiceTypeSet.get(snodeloc)
          .elementAt(index);
      vn.nodeServiceType[i] = nodeservice;
      // node demand
      vn.nodeComputationDemand[i] = (int) (this.vnp.nodeComputationMinimum + Math.round(
          Math.random() * (this.vnp.nodeComputationMaximum - this.vnp.nodeComputationMinimum)));

    }

    // edge demand
    for (int i = 0; i < vn.nodeSize; i++) {
      for (int j = 0; j < i; j++) {
        if ((Math.random() < vnp.node2nodeProbability)) {
          if (vn.virNode2subNode[i] != vn.virNode2subNode[j]) {
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
   * @param experimentTimes
   * 
   */
  private void runComparableAlgorithm(int experimentTimes) {

    for (int alg = 0; alg < algorithms.size(); alg++) {
      logger.info("------------------------" + algorithms.get(alg).algorithmName
          + " Begin --------------------------------------");
      for (int time = 0; time <= Parameter.SubstrateNewtorkRunTimeInterval; time++) {
        if ((time % (Parameter.SubstrateNewtorkRunTimeInterval
            / Parameter.ExperimentPicturePlotNumber)) == 0) {
          this.result.recordExperimentData(experimentTimes, algorithms.get(alg), time);
        }
        algorithms.get(alg).releaseResource(false);
        if ((0 == (time % Parameter.VirNetDuration))
            && (Math.random() < Parameter.RequestAppearProbability)) {
          algorithms.get(alg).generateAndProtectVirNet(null);
        }
      }
      algorithms.get(alg).releaseResource(true);
      algorithms.get(alg).isClearAllResource();
      this.result.writeExperimentDatatoFile(experimentTimes, algorithms.get(alg));
      logger.info("----------" + algorithms.get(alg).algorithmName + " End -----------\n\n");
    }

  }

  /**
   * @param vnp2
   *          vnp2
   * 
   */
  private void generateComparableAlgorithm(VirtualNetworkParameter vnp2) {
    // NoShared Shared
    // FD FI
    // FD ILP EVSNR Min Ran
    // VNE can not able to compare with VNE algorithm
    Algorithm alg;
    this.algorithms.clear();
    try {
      SubstrateNetwork FD_Min_Shared = (SubstrateNetwork) this.basicSubstrateNework.clone();
      alg = new Algorithm();
      alg.setParameter("FD_Min_Shared_Heuristic", FD_Min_Shared, false, Parameter.FailureDependent,
          true, Parameter.Min);
      this.algorithms.addElement(alg);

      SubstrateNetwork FD_Min_NoShared = (SubstrateNetwork) this.basicSubstrateNework.clone();
      alg = new Algorithm();
      alg.setParameter("FD_Min_NoShared_Heuristic", FD_Min_NoShared, false,
          Parameter.FailureDependent, false, Parameter.Min);
      this.algorithms.addElement(alg);

      SubstrateNetwork FD_Ran_Shared = (SubstrateNetwork) this.basicSubstrateNework.clone();
      alg = new Algorithm();
      alg.setParameter("FD_Ran_Shared_Heuristic", FD_Ran_Shared, false, Parameter.FailureDependent,
          true, Parameter.Ran);
      this.algorithms.addElement(alg);

      SubstrateNetwork FD_Ran_NoShared = (SubstrateNetwork) this.basicSubstrateNework.clone();
      alg = new Algorithm();
      alg.setParameter("FD_Ran_NoShared_Heuristic", FD_Ran_NoShared, false,
          Parameter.FailureDependent, false, Parameter.Ran);
      this.algorithms.addElement(alg);

      SubstrateNetwork FI_Min_Shared = (SubstrateNetwork) this.basicSubstrateNework.clone();
      alg = new Algorithm();
      alg.setParameter("FI_Min_Shared_Heuristic", FI_Min_Shared, false,
          Parameter.FailureIndependent, true, Parameter.Min);
      this.algorithms.addElement(alg);

      SubstrateNetwork FI_Min_NoShared = (SubstrateNetwork) this.basicSubstrateNework.clone();
      alg = new Algorithm();
      alg.setParameter("FI_Min_NoShared_Heuristic", FI_Min_NoShared, false,
          Parameter.FailureIndependent, false, Parameter.Min);
      this.algorithms.addElement(alg);

      SubstrateNetwork FI_Ran_Shared = (SubstrateNetwork) this.basicSubstrateNework.clone();
      alg = new Algorithm();
      alg.setParameter("FI_Ran_Shared_Heuristic", FI_Ran_Shared, false,
          Parameter.FailureIndependent, true, Parameter.Ran);
      this.algorithms.addElement(alg);

      SubstrateNetwork FI_Ran_NoShared = (SubstrateNetwork) this.basicSubstrateNework.clone();
      alg = new Algorithm();
      alg.setParameter("FI_Ran_NoShared_Heuristic", FI_Ran_NoShared, false,
          Parameter.FailureIndependent, false, Parameter.Ran);
      this.algorithms.addElement(alg);

      SubstrateNetwork virNet = (SubstrateNetwork) this.basicSubstrateNework.clone();
      alg = new Algorithm();
      alg.setParameter("VirNet", virNet, false, Parameter.FailureIndependent, false, Parameter.Ran);
      this.algorithms.addElement(alg);

      SubstrateNetwork One2OneProtection_Ran_NoShared = (SubstrateNetwork) this.basicSubstrateNework
          .clone();
      alg = new Algorithm();
      alg.setParameter("One2OneProtection_Ran_NoShared", One2OneProtection_Ran_NoShared, false,
          Parameter.One2OneProtection, false, Parameter.Ran);
      this.algorithms.addElement(alg);

      SubstrateNetwork One2OneProtection_Ran_Shared = (SubstrateNetwork) this.basicSubstrateNework
          .clone();
      alg = new Algorithm();
      alg.setParameter("One2OneProtection_Ran_Shared", One2OneProtection_Ran_Shared, false,
          Parameter.One2OneProtection, true, Parameter.Ran);
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
    } catch (CloneNotSupportedException e) {
      logger.error("Fail to construct various algorithms");
      e.printStackTrace();
    }
    logger.info("Succeed to initialize various type of algorithms\n");

    this.algorithmResult = new Result[algorithms.size()];
    for (int i = 0; i < algorithms.size(); i++) {
      algorithmResult[i] = new Result();
      algorithms.get(i).setVnp(vnp2);
    }

  }

}
