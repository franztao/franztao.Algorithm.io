/**
 * 
 */

package sevn;

import substratenetwork.SubStrateNetworkParameter;

import substratenetwork.SubstrateNetwork;
import virtualnetwork.VirtualNetworkParameter;

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

        for (int i = 0; i < Parameter.ExperimentTimes; i++)
        {
            SubStrateNetworkParameter snp = new SubStrateNetworkParameter();

            SubstrateNetwork sn = new SubstrateNetwork(snp, i);

            VirtualNetworkParameter vnp = new VirtualNetworkParameter();

            Experiment exp = new Experiment(sn, vnp, result);

            exp.bootExperiment(i);
        }

        return;
    }

}
