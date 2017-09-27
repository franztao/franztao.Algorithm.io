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
  public void startExperiment() {
    for (int i = 0; i < Parameter.ExperimentTimes; i++) {
      generateComparableAlgorithm(this.vnp);
      if (Parameter.isSameVNQ4EveryTime) {
        runComparableAlgorithmInSameVirNet(i);
      } else {
        runComparableAlgorithm(i);
      }
      this.result.recordExperimentParameter(i, algorithms);

    }
  }

  /**
   * runComparableAlgorithmInSameVirNet.
   * 
   * @param experimentTimes
   *          running experimentTimes
   */
  private void runComparableAlgorithmInSameVirNet(int experimentTimes) {
    for (int time = 0; time <= Parameter.SubstrateNewtorkRunTimeInterval; time++) {
      for (int alg = 0; alg < algorithms.size(); alg++) {

        if ((time % (Parameter.SubstrateNewtorkRunTimeInterval
            / Parameter.ExperimentPicturePlotNumber)) == 0) {
          this.algorithmResult[alg].recordExperimentData(experimentTimes, algorithms.get(alg),
              time);
        }
        algorithms.get(alg).releaseResource(false);
        if ((0 == (time % Parameter.VNRequestsDuration))) {
          if ((Math.random() < Parameter.requestAppearProbability)) {
            VirtualNetwork sameVirNet = new VirtualNetwork(this.vnp);
            constructSameVirNet(sameVirNet);
            algorithms.get(alg).generateAndEnhanceVNrequest(sameVirNet);
          }
        }
      }
    }

    for (int alg = 0; alg < algorithms.size(); alg++) {
      algorithms.get(alg).releaseResource(true);
      algorithms.get(alg).isClearAllResource();
      this.algorithmResult[alg].writeExperimentData(experimentTimes, algorithms.get(alg));
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
          vn.vNode2sNode[i] = snodeloc;
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
    for (int i = 0; i < vn.nodeSize; i++) {
      for (int j = 0; j < i; j++) {
        if ((Math.random() < vnp.node2nodeProbability)) {
          if (vn.vNode2sNode[i] != vn.vNode2sNode[j]) {
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
        if ((0 == (time % Parameter.VNRequestsDuration))
            && (Math.random() < Parameter.requestAppearProbability)) {
          algorithms.get(alg).generateAndEnhanceVNrequest(null);
        }
      }
      algorithms.get(alg).releaseResource(true);
      algorithms.get(alg).isClearAllResource();
      this.result.writeExperimentData(experimentTimes, algorithms.get(alg));
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
      SubstrateNetwork sn_FD_EVSNR_Ran_Shared = (SubstrateNetwork) this.basicSubstrateNework
          .clone();
      alg = new Algorithm();
      alg.setParameter("sn_FD_EVSNR_Ran_Shared", sn_FD_EVSNR_Ran_Shared, false,
          Parameter.FailureDependent, true);
      alg.setSequence(Parameter.Ran);
      this.algorithms.addElement(alg);

      SubstrateNetwork sn_FD_EVSNR_Ran_NoShared = (SubstrateNetwork) this.basicSubstrateNework
          .clone();
      alg = new Algorithm();
      alg.setParameter("sn_FD_EVSNR_Ran_NoShared", sn_FD_EVSNR_Ran_NoShared, false,
          Parameter.FailureDependent, false);
      alg.setSequence(Parameter.Ran);
      this.algorithms.addElement(alg);

      SubstrateNetwork sn_FD_EVSNR_Min_Shared = (SubstrateNetwork) this.basicSubstrateNework
          .clone();
      alg = new Algorithm();
      alg.setParameter("sn_FD_EVSNR_Min_Shared", sn_FD_EVSNR_Min_Shared, false,
          Parameter.FailureDependent, true);
      alg.setSequence(Parameter.Min);
      this.algorithms.addElement(alg);

      SubstrateNetwork sn_FD_EVSNR_Min_NoShared = (SubstrateNetwork) this.basicSubstrateNework
          .clone();
      alg = new Algorithm();
      alg.setParameter("sn_FD_EVSNR_Min_NoShared", sn_FD_EVSNR_Min_NoShared, false,
          Parameter.FailureDependent, false);
      alg.setSequence(Parameter.Min);
      this.algorithms.addElement(alg);
      SubstrateNetwork sn_FI_Shared = (SubstrateNetwork) this.basicSubstrateNework.clone();
      alg = new Algorithm();
      alg.setParameter("sn_FI_Shared", sn_FI_Shared, false, Parameter.FailureIndependent, true);
      alg.setSequence(Parameter.Ran);
      this.algorithms.addElement(alg);

      SubstrateNetwork sn_FI_NoShared = (SubstrateNetwork) this.basicSubstrateNework.clone();
      alg = new Algorithm();
      alg.setParameter("sn_FI_NoShared", sn_FI_NoShared, false, Parameter.FailureIndependent,
          false);
      alg.setSequence(Parameter.Ran);
      this.algorithms.addElement(alg);

      SubstrateNetwork sn_FI_Shared_Min = (SubstrateNetwork) this.basicSubstrateNework.clone();
      alg = new Algorithm();
      alg.setParameter("sn_FI_Shared_Min", sn_FI_Shared_Min, false, Parameter.FailureIndependent,
          true);
      alg.setSequence(Parameter.Min);
      this.algorithms.addElement(alg);

      SubstrateNetwork sn_FI_NoShared_Min = (SubstrateNetwork) this.basicSubstrateNework.clone();
      alg = new Algorithm();
      alg.setParameter("sn_FI_NoShared_Min", sn_FI_NoShared_Min, false,
          Parameter.FailureIndependent, false);
      alg.setSequence(Parameter.Min);
      this.algorithms.addElement(alg);

      // SubstrateNetwork sn_FD_ILP_Shared = (SubstrateNetwork)
      // this.basicSubstrateNework.clone();
      // alg = new Algorithm();
      // alg.setParameter("sn_FD_ILP_Shared", sn_FD_ILP_Shared, true,
      // EVSNR.FailureDependent, true);
      // this.algorithms.addElement(alg);
      //
      // SubstrateNetwork sn_FD_ILP_NoShared = (SubstrateNetwork)
      // this.basicSubstrateNework.clone();
      // alg = new Algorithm();
      // alg.setParameter("sn_FD_ILP_NoShared", sn_FD_ILP_NoShared, true,
      // EVSNR.FailureDependent, false);
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
