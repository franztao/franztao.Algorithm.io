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

	Vector<Algorithm> algorithms;

	public Experiment(SubstrateNetwork sn, VirtualNetworkParameter vnp) {
		this.basicSubstrateNework = sn;
		this.vnp = vnp;
		this.algorithms = new Vector<Algorithm>();

		PropertyConfigurator.configure("log4j.properties");
		// BasicConfigurator.configure();

	}

	/**
	 * @param vnp
	 * 
	 */
	public void startExperiment() {
		generateComparableAlgorithm(this.vnp);
		runComparableAlgorithm();

	}

	/**
	 * 
	 */
	private void runComparableAlgorithm() {
		for (int alg = 0; alg < algorithms.size(); alg++) {
			logger.info("------------------------" + algorithms.get(alg).algorithmName
					+ " Begin --------------------------------------------------------------------------------------");
			for (int time = 0; time < EVSNR.SubstrateNewtorkRunTimeInterval; time++) {
				algorithms.get(alg).releaseResource(false);
				if ((0 == (time % EVSNR.VNRequestsDuration)) && (Math.random() < EVSNR.requestAppearProbability)) {
					algorithms.get(alg).generateAndEnhanceVNrequest();
				}
			}
			algorithms.get(alg).releaseResource(true);
			algorithms.get(alg).isClearAllResource();
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
		try {

			// SubstrateNetwork sn_FD_ILP_Shared = (SubstrateNetwork)
			// this.basicSubstrateNework.clone();
			// alg = new Algorithm();
			// alg.setFD(EVSNR.FailureDependent);
			// alg.setShared(true);
			// alg.setExact(true);
			// alg.setSn(sn_FD_ILP_Shared);
			// alg.algorithmName="sn_FD_ILP_Shared";
			// this.algorithms.addElement(alg);
			//
			// SubstrateNetwork sn_FD_ILP_NoShared = (SubstrateNetwork)
			// this.basicSubstrateNework.clone();
			// alg = new Algorithm();
			// alg.setFD(EVSNR.FailureDependent);
			// alg.setShared(false);
			// alg.setExact(true);
			// alg.setSn(sn_FD_ILP_NoShared);
			// alg.algorithmName="sn_FD_ILP_NoShared";
			// this.algorithms.addElement(alg);

			 SubstrateNetwork sn_FI_Shared = (SubstrateNetwork)
			 this.basicSubstrateNework.clone();
			 alg = new Algorithm();
			 alg.setParameter("sn_FI_Shared", sn_FI_Shared, false, EVSNR.FailureIndependent, true);
			 this.algorithms.addElement(alg);

			SubstrateNetwork sn_FI_NoShared = (SubstrateNetwork) this.basicSubstrateNework.clone();
			alg = new Algorithm();
			alg.setParameter("sn_FI_NoShared", sn_FI_NoShared, false, EVSNR.FailureIndependent, false);
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

			// SubstrateNetwork sn_FD_EVSNR_Min_Shared = (SubstrateNetwork)
			// this.basicSubstrateNework.clone();
			// alg = new Algorithm();
			// alg.setFD(EVSNR.FailureDependent);
			// alg.setShared(true);
			// alg.setExact(false);
			// alg.setSequence(EVSNR.Min);
			// alg.setSn(sn_FD_EVSNR_Min_Shared);
			// this.algorithms.addElement(alg);
			//
			// SubstrateNetwork sn_FD_EVSNR_Min_NoShared = (SubstrateNetwork)
			// this.basicSubstrateNework.clone();
			// alg = new Algorithm();
			// alg.setFD(EVSNR.FailureDependent);
			// alg.setShared(false);
			// alg.setExact(false);
			// alg.setSequence(EVSNR.Min);
			// alg.setSn(sn_FD_EVSNR_Min_NoShared);
			// this.algorithms.addElement(alg);
		} catch (CloneNotSupportedException e) {
			logger.error("Fail to construct various algorithms");
			e.printStackTrace();
		}
		logger.info("Succeed to construct various algorithms\n");
		for (int i = 0; i < algorithms.size(); i++) {
			algorithms.get(i).setVnp(vnp2);
		}

	}

}
