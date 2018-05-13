/**
 * 
 */

package substrateNetwork;

import sevn.Parameter;

/**
 * 。
 * 
 * @author franz
 *
 */
public class SubStrateNetworkParameter
{

    // node parameter
    public int nodeSize = -1;
    public int nodeComputationMinimum = Parameter.SubStrateNodeComputationMinimum;
    public int nodeComputationMaximum = Parameter.SubStrateNodeComputationMaximum;

    // edge parameter
    public double node2nodeProbability = Parameter.SubStrateNodenodeProbability;
    public int edgeBandwithMinimum = Parameter.SubStrateEdgeBandwithMinimum;
    public int edgeBandwithMaximum = Parameter.SubStrateEdgeBandwithMaximum;

    // service parameter
    public int functionTypeNumber;
    public double functionTypeProbability = Parameter.FunctionTypeProbability;

    /**
     * SubStrateNetworkParameter.
     * 
     */
    public SubStrateNetworkParameter()
    {
        if (Parameter.TopologyType == Parameter.TopologyTypeSample)
        {
            this.nodeSize = 8;
            this.functionTypeNumber = 4;
        }

        if (Parameter.TopologyType == Parameter.TopologyTypeSNDLib)
        {
            this.functionTypeNumber = Parameter.FunctionNumber;
        }

        if (Parameter.TopologyType == Parameter.TopologyTypeRandom)
        {
            this.nodeSize = Parameter.SubStrateNodeSize;
            this.functionTypeNumber = Parameter.FunctionNumber;
        }

        if (Parameter.TopologyType == Parameter.TopologyTypeDataCenter)
        {
            this.nodeSize = 1 + Parameter.DataCenterAry + Parameter.DataCenterAry * Parameter.DataCenterAry
                    + Parameter.DataCenterAry * Parameter.DataCenterAry * Parameter.DataCenterAry + 1;
            this.functionTypeNumber = 3;
        }

    }

}
