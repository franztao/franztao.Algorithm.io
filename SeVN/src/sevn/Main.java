/**
 * 
 */

package sevn;

import substrateNetwork.SubStrateNetworkParameter;
import substrateNetwork.SubstrateNetworkt;
import virtualNetwork.VirtualNetworkParameter;

public class Main
{

    /**
     * main.
     * 
     * @param args
     *            main parameter
     */

    public static void main(String[] args)
    {
        Result result = new Result();
        result.recordTexParameter();

        for (int ithExp = 0; ithExp < Parameter.ExperimentTimes; ithExp++)
        {
            SubStrateNetworkParameter snp = new SubStrateNetworkParameter();

            SubstrateNetworkt sn = new SubstrateNetworkt(snp, ithExp);

            VirtualNetworkParameter vnp = new VirtualNetworkParameter();

            Experiment exp = new Experiment(sn, vnp);

            result.recordExperimentParameter(exp.algorithms);

            exp.bootExperiment(ithExp);
        }

        return;
    }

}
