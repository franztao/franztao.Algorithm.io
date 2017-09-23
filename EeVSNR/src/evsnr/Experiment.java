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
	 * @param vnp
	 * 
	 */
	public void startExperiment() {
		for (int i = 0; i < EVSNR.ExperimentTimes; i++) {
			generateComparableAlgorithm(this.vnp);
			if (EVSNR.isSameVNQ4EveryTime) {
				runComparableAlgorithmInSameVN(i);
			} else {
				runComparableAlgorithm(i);
			}
			this.result.recordExperimentParameter(i, algorithms);

		}
	}

	/**
	 * @param experimentTimes
	 */
	private void runComparableAlgorithmInSameVN(int experimentTimes) {
		for (int time = 0; time <= EVSNR.SubstrateNewtorkRunTimeInterval; time++) {
			for (int alg = 0; alg < algorithms.size(); alg++) {
				if ((time % (EVSNR.SubstrateNewtorkRunTimeInterval / EVSNR.ExperimentPicturePlotNumber)) == 0) {
					this.algorithmResult[alg].recordExperimentData(experimentTimes, algorithms.get(alg), time);
				}
				algorithms.get(alg).releaseResource(false);
				if ((0 == (time % EVSNR.VNRequestsDuration)) && (Math.random() < EVSNR.requestAppearProbability)) {
					VirtualNetwork sameVN = new VirtualNetwork(this.vnp);
					constructSameVN(sameVN);
					algorithms.get(alg).generateAndEnhanceVNrequest(sameVN);
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
	 * @param sameVN
	 * @param vnp
	 */
	private void constructSameVN(VirtualNetwork VN) {
		boolean[] isSamesNode = new boolean[this.basicSubstrateNework.nodeSize];
		for (int i = 0; i < VN.nodeSize; i++) {
			int snodeloc;
			do {
				snodeloc = (int) Math.round(Math.random() * (this.basicSubstrateNework.nodeSize - 1));
				if (!isSamesNode[snodeloc]) {
					VN.vNode2sNode[i] = snodeloc;
					isSamesNode[snodeloc] = true;
					break;
				}
			} while (true);
			// service
			int nodeservice = this.basicSubstrateNework.vectorServiceTypeSet.get(snodeloc).elementAt(
					(int) Math.random() * (this.basicSubstrateNework.vectorServiceTypeSet.get(snodeloc).size() - 1));
			VN.nodeServiceType[i] = nodeservice;
			// node demand
			VN.nodeComputationDemand[i] = (int) (this.vnp.nodeComputationMinimum
					+ Math.round(Math.random() * (this.vnp.nodeComputationMaximum - this.vnp.nodeComputationMinimum)));

		}
		for (int i = 0; i < VN.nodeSize; i++) {
			for (int j = 0; j < i; j++) {
				if ((Math.random() < vnp.node2nodeProbability)) {
					if (VN.vNode2sNode[i] != VN.vNode2sNode[j]) {
						int distributeIthEdgeBandwith = (int) (vnp.edgeBandwithMinimum
								+ Math.round(Math.random() * (vnp.edgeBandwithMaximum - vnp.edgeBandwithMinimum)));
						VN.topology[i][j] = VN.topology[j][i] = true;
						VN.edgeBandwithDemand[i][j] = VN.edgeBandwithDemand[j][i] = distributeIthEdgeBandwith;
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
					+ " Begin --------------------------------------------------------------------------------------");
			for (int time = 0; time <= EVSNR.SubstrateNewtorkRunTimeInterval; time++) {
				if ((time % (EVSNR.SubstrateNewtorkRunTimeInterval / EVSNR.ExperimentPicturePlotNumber)) == 0) {
					this.result.recordExperimentData(experimentTimes, algorithms.get(alg), time);
				}
				algorithms.get(alg).releaseResource(false);
				if ((0 == (time % EVSNR.VNRequestsDuration)) && (Math.random() < EVSNR.requestAppearProbability)) {
					algorithms.get(alg).generateAndEnhanceVNrequest(null);
				}
			}
			algorithms.get(alg).releaseResource(true);
			algorithms.get(alg).isClearAllResource();
			this.result.writeExperimentData(experimentTimes, algorithms.get(alg));
			logger.info("------------------------" + algorithms.get(alg).algorithmName
					+ " End --------------------------------------------------------------------------------------\n\n");
		}

	}

	/**
	 * @param vnp2
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

			SubstrateNetwork sn_FI_Shared = (SubstrateNetwork) this.basicSubstrateNework.clone();
			alg = new Algorithm();
			alg.setParameter("sn_FI_Shared", sn_FI_Shared, false, EVSNR.FailureIndependent, true);
			alg.setSequence(EVSNR.Ran);
			this.algorithms.addElement(alg);

			SubstrateNetwork sn_FI_NoShared = (SubstrateNetwork) this.basicSubstrateNework.clone();
			alg = new Algorithm();
			alg.setParameter("sn_FI_NoShared", sn_FI_NoShared, false, EVSNR.FailureIndependent, false);
			alg.setSequence(EVSNR.Ran);
			this.algorithms.addElement(alg);

			SubstrateNetwork sn_FI_Shared_Min = (SubstrateNetwork) this.basicSubstrateNework.clone();
			alg = new Algorithm();
			alg.setParameter("sn_FI_Shared_Min", sn_FI_Shared_Min, false, EVSNR.FailureIndependent, true);
			alg.setSequence(EVSNR.Min);
			this.algorithms.addElement(alg);

			SubstrateNetwork sn_FI_NoShared_Min = (SubstrateNetwork) this.basicSubstrateNework.clone();
			alg = new Algorithm();
			alg.setParameter("sn_FI_NoShared_Min", sn_FI_NoShared_Min, false, EVSNR.FailureIndependent, false);
			alg.setSequence(EVSNR.Min);
			this.algorithms.addElement(alg);

			SubstrateNetwork sn_FD_EVSNR_Ran_Shared = (SubstrateNetwork) this.basicSubstrateNework.clone();
			alg = new Algorithm();
			alg.setParameter("sn_FD_EVSNR_Ran_Shared", sn_FD_EVSNR_Ran_Shared, false, EVSNR.FailureDependent, true);
			alg.setSequence(EVSNR.Ran);
			this.algorithms.addElement(alg);

			SubstrateNetwork sn_FD_EVSNR_Ran_NoShared = (SubstrateNetwork) this.basicSubstrateNework.clone();
			alg = new Algorithm();
			alg.setParameter("sn_FD_EVSNR_Ran_NoShared", sn_FD_EVSNR_Ran_NoShared, false, EVSNR.FailureDependent,
					false);
			alg.setSequence(EVSNR.Ran);
			this.algorithms.addElement(alg);

			SubstrateNetwork sn_FD_EVSNR_Min_Shared = (SubstrateNetwork) this.basicSubstrateNework.clone();
			alg = new Algorithm();
			alg.setParameter("sn_FD_EVSNR_Min_Shared", sn_FD_EVSNR_Min_Shared, false, EVSNR.FailureDependent, true);
			alg.setSequence(EVSNR.Min);
			this.algorithms.addElement(alg);

			SubstrateNetwork sn_FD_EVSNR_Min_NoShared = (SubstrateNetwork) this.basicSubstrateNework.clone();
			alg = new Algorithm();
			alg.setParameter("sn_FD_EVSNR_Min_NoShared", sn_FD_EVSNR_Min_NoShared, false, EVSNR.FailureDependent,
					false);
			alg.setSequence(EVSNR.Min);
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
