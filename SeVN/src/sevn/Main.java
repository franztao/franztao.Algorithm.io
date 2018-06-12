/**
 * 
 */

package sevn;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import substrateNetwork.PhysicalNetworkParameter;
import substrateNetwork.PhysicalNetworkt;
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
        Logger mainlogger = Logger.getLogger(Main.class.getName());
        mainlogger.setLevel(Parameter.logLevel);
        PropertyConfigurator.configure("log4j.properties");
        
        Result result = new Result();
        result.recordTexParameter();

        if (Parameter.BasicExperiment == Parameter.ExperimentType || Parameter.AllExperiment)
        {
            Parameter.ExperimentFileString = "";
            BootExperiment();

            mainlogger.error("BasicExperiment");
        }

        if (Parameter.PossionMeanExperiment == Parameter.ExperimentType || Parameter.AllExperiment)
        {
            for (int i = Parameter.PossionMeanStart; i <= Parameter.PossionMeanEnd; i = i + Parameter.PossionMeanAdd)
            {
                Parameter.PossionMean = i;
                Parameter.ExperimentFileString = "PossionMean_" + i + "_";

                BootExperiment();

                mainlogger.error("PossionMean_" + i + "_");
            }
            Parameter.PossionMean = 5;
        }

        if (Parameter.ExponentialMeanExperiment == Parameter.ExperimentType || Parameter.AllExperiment)
        {

            for (int i = Parameter.ExponentialMeanStart; i <= Parameter.ExponentialMeanEnd; i = i
                    + Parameter.ExponentialMeanAdd)
            {
                Parameter.VNRequestsContinueTimeExponentialMean = i;
                Parameter.ExperimentFileString = "ExponentialMean_" + i + "_";

                BootExperiment();

                mainlogger.error("ExponentialMean_" + i + "_");
            }
            Parameter.VNRequestsContinueTimeExponentialMean = Parameter.PhysicalNewtorkRunTimeInterval / 10;
        }

        if (Parameter.NodeFailureExperiment == Parameter.ExperimentType || Parameter.AllExperiment)
        {

            for (int i = Parameter.NodeFailureNumberStart; i <= Parameter.NodeFailureNumberEnd; i = i + 1)
            {
                Parameter.NodeFailureNumber = i;
                Parameter.ExperimentFileString = "NodeFailure_" + i + "_";
                BootExperiment();

                mainlogger.error("NodeFailure_" + i + "_");
            }
            Parameter.NodeFailureNumber = 1;
        }

        return;
    }

    public static void BootExperiment()
    {
        Result result = new Result();
        for (int ithExp = 0; ithExp < Parameter.ExperimentTimes; ithExp++)
        {
            PhysicalNetworkParameter snp = new PhysicalNetworkParameter();
            PhysicalNetworkt sn = new PhysicalNetworkt(snp, ithExp);
            VirtualNetworkParameter vnp = new VirtualNetworkParameter();
            Experiment exp = new Experiment(sn, vnp);
            result.recordExperimentParameter(exp.algorithms);
            exp.bootExperiment(ithExp);
        }
    }
}
