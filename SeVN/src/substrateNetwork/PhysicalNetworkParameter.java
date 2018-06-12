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
public class PhysicalNetworkParameter
{

    // node parameter
    public int nodeSize = -1;
    public int nodeComputationMinimum = Parameter.PhysicalNodeComputationMinimum;
    public int nodeComputationMaximum = Parameter.PhysicalNodeComputationMaximum;

    // edge parameter
    public double node2nodeProbability = Parameter.PhysicalNodenodeProbability;
    public int edgeBandwithMinimum = Parameter.PhysicalEdgeBandwithMinimum;
    public int edgeBandwithMaximum = Parameter.PhysicalEdgeBandwithMaximum;

    // service parameter
    public int functionTypeNumber;
    public double functionTypeProbability = Parameter.FunctionTypeProbability;

    /**
     * SubStrateNetworkParameter.
     * 
     */
    public PhysicalNetworkParameter()
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
            this.nodeSize = Parameter.PhysicalNodeSize;
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
