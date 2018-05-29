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

        if (Parameter.BasicExperiment == Parameter.ExperimentType || Parameter.AllExperiment)
        {
            Parameter.ExperimentFileString = "";
            for (int ithExp = 0; ithExp < Parameter.ExperimentTimes; ithExp++)
            {

                SubStrateNetworkParameter snp = new SubStrateNetworkParameter();
                SubstrateNetworkt sn = new SubstrateNetworkt(snp, ithExp);
                VirtualNetworkParameter vnp = new VirtualNetworkParameter();
                Experiment exp = new Experiment(sn, vnp);
                result.recordExperimentParameter(exp.algorithms);
                exp.bootExperiment(ithExp);
            }
        }

        if (Parameter.PossionMeanExperiment == Parameter.ExperimentType || Parameter.AllExperiment)
        {
            for (int i = Parameter.PossionMeanStart; i <= Parameter.PossionMeanEnd; i = i + Parameter.PossionMeanAdd)
            {
                Parameter.ExperimentFileString = "PossionMean_" + i+"_";

                for (int ithExp = 0; ithExp < Parameter.ExperimentTimes; ithExp++)
                {

                    SubStrateNetworkParameter snp = new SubStrateNetworkParameter();
                    SubstrateNetworkt sn = new SubstrateNetworkt(snp, ithExp);
                    VirtualNetworkParameter vnp = new VirtualNetworkParameter();
                    Experiment exp = new Experiment(sn, vnp);
                    result.recordExperimentParameter(exp.algorithms);
                    exp.bootExperiment(ithExp);
                }
            }

        }
        if (Parameter.ExponentialMeanExperiment == Parameter.ExperimentType || Parameter.AllExperiment)
        {
            for (int i = Parameter.ExponentialMeanStart; i <= Parameter.ExponentialMeanEnd; i = i
                    + Parameter.ExponentialMeanAdd)
            {
                Parameter.ExperimentFileString = "ExponentialMean_" + i+"_";

                for (int ithExp = 0; ithExp < Parameter.ExperimentTimes; ithExp++)
                {
                    SubStrateNetworkParameter snp = new SubStrateNetworkParameter();
                    SubstrateNetworkt sn = new SubstrateNetworkt(snp, ithExp);
                    VirtualNetworkParameter vnp = new VirtualNetworkParameter();
                    Experiment exp = new Experiment(sn, vnp);
                    result.recordExperimentParameter(exp.algorithms);
                    exp.bootExperiment(ithExp);
                }
            }
        }

        return;
    }

}
