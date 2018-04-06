/**
 * 
 */

package evsnr;

import org.apache.commons.math3.distribution.PoissonDistribution;

import substratenetwork.SubStrateNetworkParameter;

import substratenetwork.SubstrateNetwork;
import virtualnetwork.VirtualNetworkParameter;

public class Main {

  /**
   * main.
   * 
   * @param args
   *          main parameter
   */

  public static void main(String[] args) {
    boolean isSampleInit = false;
    Result result = new Result();
    result.recordTexParameter();

    SubStrateNetworkParameter snp = new SubStrateNetworkParameter(isSampleInit);

    SubstrateNetwork sn = new SubstrateNetwork(snp);

    VirtualNetworkParameter vnp = new VirtualNetworkParameter(isSampleInit);

    Experiment exp = new Experiment(sn, vnp, result);
    exp.bootExperiment();

    return;
  }

}
