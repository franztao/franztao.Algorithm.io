/**
 * 
 */
package evsnr;

import java.util.Random;
import java.util.Timer;
import java.util.Vector;

import substratenetwork.SubstrateNetwork;
import virtualnetwork.VirtualNetworkParameter;

/**
 * @author franz
 *
 */
public class Experiment {
	private SubstrateNetwork basicSubstrateNework;
	private VirtualNetworkParameter vnp;

	Vector<Algorithm> algorithms;

	public Experiment(SubstrateNetwork FDSubstrateNework, VirtualNetworkParameter vnp) {
		this.basicSubstrateNework = FDSubstrateNework;
		this.vnp = vnp;
		this.algorithms = new Vector<Algorithm>();
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
		for (int time = 0; time < EVSNR.SubstrateNewtorkRunTimeInterval; time++) {
			for (int alg = 0; alg < algorithms.size(); alg++) {
				algorithms.get(alg).releaseResource();
			}
			if ((0 == (time % EVSNR.VNRequestsDuration))
					&& (new Random().nextDouble() < EVSNR.requestAppearProbability)) {
				for (int alg = 0; alg < algorithms.size(); alg++) {
					 algorithms.get(alg).generateVNrequest();
				}
			}
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
		try {
			SubstrateNetwork sn_FI_Shared = (SubstrateNetwork) this.basicSubstrateNework.clone();
			Algorithm alg = new Algorithm();
			alg.setFD(EVSNR.FailureIndependent);
			alg.setShared(true);
			alg.setExact(false);
			alg.setSn(sn_FI_Shared);
			this.algorithms.addElement(alg);

			SubstrateNetwork sn_FI_NoShared = (SubstrateNetwork) this.basicSubstrateNework.clone();
			alg = new Algorithm();
			alg.setFD(EVSNR.FailureIndependent);
			alg.setShared(false);
			alg.setExact(false);
			alg.setSn(sn_FI_NoShared);
			this.algorithms.addElement(alg);

			SubstrateNetwork sn_FD_ILP_Shared = (SubstrateNetwork) this.basicSubstrateNework.clone();
			alg = new Algorithm();
			alg.setFD(EVSNR.FailureDependent);
			alg.setShared(true);
			alg.setExact(true);
			alg.setSn(sn_FD_ILP_Shared);
			this.algorithms.addElement(alg);

			SubstrateNetwork sn_FD_ILP_NoShared = (SubstrateNetwork) this.basicSubstrateNework.clone();
			alg = new Algorithm();
			alg.setFD(EVSNR.FailureDependent);
			alg.setShared(false);
			alg.setExact(true);
			alg.setSn(sn_FD_ILP_NoShared);
			this.algorithms.addElement(alg);

			SubstrateNetwork sn_FD_EVSNR_Ran_Shared = (SubstrateNetwork) this.basicSubstrateNework.clone();
			alg = new Algorithm();
			alg.setFD(EVSNR.FailureDependent);
			alg.setShared(true);
			alg.setExact(false);
			alg.setSequence(EVSNR.Ran);
			alg.setSn(sn_FD_EVSNR_Ran_Shared);
			this.algorithms.addElement(alg);

			SubstrateNetwork sn_FD_EVSNR_Ran_NoShared = (SubstrateNetwork) this.basicSubstrateNework.clone();
			alg = new Algorithm();
			alg.setFD(EVSNR.FailureDependent);
			alg.setShared(false);
			alg.setExact(false);
			alg.setSequence(EVSNR.Ran);
			alg.setSn(sn_FD_EVSNR_Ran_NoShared);
			this.algorithms.addElement(alg);

			SubstrateNetwork sn_FD_EVSNR_Min_Shared = (SubstrateNetwork) this.basicSubstrateNework.clone();
			alg = new Algorithm();
			alg.setFD(EVSNR.FailureDependent);
			alg.setShared(true);
			alg.setExact(false);
			alg.setSequence(EVSNR.Min);
			alg.setSn(sn_FD_EVSNR_Min_Shared);
			this.algorithms.addElement(alg);

			SubstrateNetwork sn_FD_EVSNR_Min_NoShared = (SubstrateNetwork) this.basicSubstrateNework.clone();
			alg = new Algorithm();
			alg.setFD(EVSNR.FailureDependent);
			alg.setShared(false);
			alg.setExact(false);
			alg.setSequence(EVSNR.Min);
			alg.setSn(sn_FD_EVSNR_Min_NoShared);
			this.algorithms.addElement(alg);
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (int alg = 0; alg < algorithms.size(); alg++) {
			algorithms.get(alg).setVnp(vnp2);
		}
		
	}

}
