/**
 * 
 */
package evsnr;

import java.util.Vector;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import substratenetwork.SubstrateNetwork;
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
			runComparableAlgorithm(i);
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
					algorithms.get(alg).generateAndEnhanceVNrequest();
				}
			}
			algorithms.get(alg).releaseResource(true);
			algorithms.get(alg).isClearAllResource();
			this.result.writeExperimentData(experimentTimes, algorithms.get(alg));
			logger.info("------------------------" + algorithms.get(alg).algorithmName
					+ " End --------------------------------------------------------------------------------------\n\n");
		}

		this.result.recordExperimentParameter(experimentTimes, algorithms);
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

//			SubstrateNetwork sn_FD_ILP_Shared = (SubstrateNetwork) this.basicSubstrateNework.clone();
//			alg = new Algorithm();
//			alg.setParameter("sn_FD_ILP_Shared", sn_FD_ILP_Shared, true, EVSNR.FailureDependent, true);
//			this.algorithms.addElement(alg);
//
//			SubstrateNetwork sn_FD_ILP_NoShared = (SubstrateNetwork) this.basicSubstrateNework.clone();
//			alg = new Algorithm();
//			alg.setParameter("sn_FD_ILP_NoShared", sn_FD_ILP_NoShared, true, EVSNR.FailureDependent, false);
//			this.algorithms.addElement(alg);
		} catch (CloneNotSupportedException e) {
			logger.error("Fail to construct various algorithms");
			e.printStackTrace();
		}
		logger.info("Succeed to initialize various type of algorithms\n");
		for (int i = 0; i < algorithms.size(); i++) {
			algorithms.get(i).setVnp(vnp2);
		}

	}

}
