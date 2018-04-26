/**
 * 
 */
package virtualNetwork;

import sevn.Parameter;

/**
 * @author franz
 *
 */
public class VirtualNetworkParameter
{
    // node parameter
    public int nodeSize = -1;//
    public int nodeSizeMinimum = Parameter.VirtualNodeSizeMinimum;
    public int nodeSizeMaximum = Parameter.VirtualNodeSizeMaximum;
    public int nodeComputationMinimum = Parameter.VirtualNodeComputationMinimum;
    public int nodeComputationMaximum = Parameter.VirtualNodeComputationMaximum;

    // edge parameter
    public double node2nodeProbability = Parameter.VirtualNodenodeProbability;
    public int edgeBandwithMinimum = Parameter.VirtualEdgeBandwithMinimum;
    public int edgeBandwithMaximum = Parameter.VirtualEdgeBandwithMaximum;

    // service parameter
    public int serviceNumber = Parameter.ServiceNumber;

    public int topologyType = Parameter.TopologyType;
    public boolean isSampleInit;

    /**
     * @param sampleInit2
     */
    public VirtualNetworkParameter()
    {
        if (this.topologyType == Parameter.TopologyTypeSample)
        {
            this.nodeSize = 4;
            this.serviceNumber = 4;
        }

        if (this.topologyType == Parameter.TopologyTypeDataCenter)
        {
            this.nodeSize = Parameter.DataCenterVNSize;
            this.nodeSizeMinimum = 2;
            this.nodeSizeMaximum = Parameter.DataCenterVNSize + 2;

            // the VM slots on each PM and bandwidth on each link,
            // according to a normal distribution with mean of ↵ and standard
            // deviation of min{↵, 1.0 − ↵}.
            this.nodeComputationMinimum = 1;
            this.nodeComputationMaximum = 1;

            this.serviceNumber = 3;
        }

        if (this.topologyType == Parameter.TopologyTypeRandom || this.topologyType == Parameter.TopologyTypeSNDLib)
        {
            this.nodeSize = (int) (this.nodeSizeMinimum
                    + Math.random() * (this.nodeSizeMaximum - this.nodeSizeMinimum));
            this.serviceNumber = Parameter.ServiceNumber;
        }
    }

    /**
     * @return the nodeSize
     */
    public int getNodeSize()
    {
        return nodeSize;
    }

    /**
     * @param nodeSize
     *            the nodeSize to set
     */
    public void setNodeSize(int nodeSize)
    {
        this.nodeSize = nodeSize;
    }

    /**
     * @return the nodeSizeMinimum
     */
    public int getNodeSizeMinimum()
    {
        return nodeSizeMinimum;
    }

    /**
     * @param nodeSizeMinimum
     *            the nodeSizeMinimum to set
     */
    public void setNodeSizeMinimum(int nodeSizeMinimum)
    {
        this.nodeSizeMinimum = nodeSizeMinimum;
    }

    /**
     * @return the nodeSizeMaximum
     */
    public int getNodeSizeMaximum()
    {
        return nodeSizeMaximum;
    }

    /**
     * @param nodeSizeMaximum
     *            the nodeSizeMaximum to set
     */
    public void setNodeSizeMaximum(int nodeSizeMaximum)
    {
        this.nodeSizeMaximum = nodeSizeMaximum;
    }

    /**
     * @return the nodeComputationMinimum
     */
    public int getNodeComputationMinimum()
    {
        return nodeComputationMinimum;
    }

    /**
     * @param nodeComputationMinimum
     *            the nodeComputationMinimum to set
     */
    public void setNodeComputationMinimum(int nodeComputationMinimum)
    {
        this.nodeComputationMinimum = nodeComputationMinimum;
    }

    /**
     * @return the nodeComputationMaximum
     */
    public int getNodeComputationMaximum()
    {
        return nodeComputationMaximum;
    }

    /**
     * @param nodeComputationMaximum
     *            the nodeComputationMaximum to set
     */
    public void setNodeComputationMaximum(int nodeComputationMaximum)
    {
        this.nodeComputationMaximum = nodeComputationMaximum;
    }

    /**
     * @return the node2nodeProbability
     */
    public double getNode2nodeProbability()
    {
        return node2nodeProbability;
    }

    /**
     * @param node2nodeProbability
     *            the node2nodeProbability to set
     */
    public void setNode2nodeProbability(double node2nodeProbability)
    {
        this.node2nodeProbability = node2nodeProbability;
    }

    /**
     * @return the edgeBandwithMinimum
     */
    public int getEdgeBandwithMinimum()
    {
        return edgeBandwithMinimum;
    }

    /**
     * @param edgeBandwithMinimum
     *            the edgeBandwithMinimum to set
     */
    public void setEdgeBandwithMinimum(int edgeBandwithMinimum)
    {
        this.edgeBandwithMinimum = edgeBandwithMinimum;
    }

    /**
     * @return the edgeBandwithMaximum
     */
    public int getEdgeBandwithMaximum()
    {
        return edgeBandwithMaximum;
    }

    /**
     * @param edgeBandwithMaximum
     *            the edgeBandwithMaximum to set
     */
    public void setEdgeBandwithMaximum(int edgeBandwithMaximum)
    {
        this.edgeBandwithMaximum = edgeBandwithMaximum;
    }

    /**
     * @return the serviceNumber
     */
    public int getServiceNumber()
    {
        return serviceNumber;
    }

    /**
     * @param serviceNumber
     *            the serviceNumber to set
     */
    public void setServiceNumber(int serviceNumber)
    {
        this.serviceNumber = serviceNumber;
    }

    /**
     * @return the sampleInit
     */
    public boolean isSampleInit()
    {
        return isSampleInit;
    }

    /**
     * @param sampleInit
     *            the sampleInit to set
     */
    public void setSampleInit(boolean sampleInit)
    {
        this.isSampleInit = sampleInit;
    }
}
