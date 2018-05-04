/**
 * 
 */

package sevn;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import algorithm.SeVN;
import virtualNetwork.VirtualNetwork;

/**
 * Result.
 * 
 * @author Taoheng
 *
 */
public class Result
{
    private Logger resultLog = Logger.getLogger(Result.class);

    String fileAbsolutePath = Parameter.FileAbsolutePath;//
    String dataFilePathString = "/Data/";
    String prefix = "\\newcommand{\\";

    double[] arrayAcceptanceRatioSurNet;
    double[] arrayAcceptanceRatioVirNet;

    public int[] arrayCostEdgeBwAcc;
    public int[] arrayCostNodeCpAcc;
    public int[] arrayActiveNodeSubNodeAcc;

    private int[] arrayActiveNodeSubNode;
    private int[] arrayCostNodeCp;
    private int[] arrayCostEdgeBw;

    private int[] arrayPathLengthSubEdgeAcc;
    private int[] arrayPathLengthSubEdge;

    public int[] arrayActiveNodeVirNodeAcc;
    public int[] arrayRevenueEdgeBwAcc;
    public int[] arrayRevenueNodeCpAcc;
    private int[] arrayRevenueNodeCp;
    private int[] arrayRevenueEdgeBw;
    private int[] arrayActiveNodeVirNode;

    public int recordDataLength;
    private int[] arrayPathLengthVirEdgeAcc;
    private int[] arrayPathLengthVirEdge;

    private int[] arrayVirNetReqAcc;
    private int[] arraySurNetReqAcc;
    private int[] arrayVirNetReq;
    private int[] arraySurNetReq;

    public int activeNodeVirNode;
    public int pathLengthVirEdge;
    public int revenueNodeCp;
    public int revenueEdgeBw;
    public int virNetReq;
    public int surNetReq;

    public int activeNodeSubNode;
    public int pathLengthSubEdge;
    public int costNodeCp;
    public int costEdgeBw;

    public int activeNodeVirNodeAcc;
    public int pathLengthVirEdgeAcc;
    public int revenueNodeCpAcc;
    public int revenueEdgeBwAcc;
    public int virNetReqAcc;
    public int surNetReqAcc;

    public int activeNodeSubNodeAcc;
    public int pathLengthSubEdgeAcc;
    public int costNodeCpAcc;
    public int costEdgeBwAcc;

    public double migrationFrequenceNode;
    public double migrationFrequenceEdge;
    public double[] arrayMigrationFrequenceNode;
    public double[] arrayMigrationFrequenceEdge;

    private double stressNode;
    private double stressEdge;
    public double[] arrayStressNode;
    public double[] arrayStressEdge;

    public double utilizationNode;
    public double utilizationEdge;
    public double[] arrayUtilizationNode;
    public double[] arrayUtilizationEdge;
    private double acceptanceRatioSurNet;
    private double acceptanceRatioVirNet;

    /**
     * Result.
     */
    public Result()
    {
        PropertyConfigurator.configure("log4j.properties");
        arrayAcceptanceRatioSurNet = new double[(Parameter.ExperimentPicturePlotNumber + 1)
                * Parameter.ExperimentTimes];
        arrayAcceptanceRatioVirNet = new double[(Parameter.ExperimentPicturePlotNumber + 1)
                * Parameter.ExperimentTimes];

        arrayCostEdgeBwAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayCostNodeCpAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayActiveNodeSubNodeAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];

        arrayCostEdgeBw = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayCostNodeCp = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayActiveNodeSubNode = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];

        arrayPathLengthSubEdgeAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayPathLengthSubEdge = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];

        arrayActiveNodeVirNodeAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayRevenueEdgeBwAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayRevenueNodeCpAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];

        arrayRevenueNodeCp = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayRevenueEdgeBw = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayActiveNodeVirNode = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];

        arrayVirNetReqAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arraySurNetReqAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayVirNetReq = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arraySurNetReq = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayPathLengthVirEdgeAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayPathLengthVirEdge = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];

        arrayMigrationFrequenceNode = new double[(Parameter.ExperimentPicturePlotNumber + 1)
                * Parameter.ExperimentTimes];
        arrayMigrationFrequenceEdge = new double[(Parameter.ExperimentPicturePlotNumber + 1)
                * Parameter.ExperimentTimes];

        arrayStressNode = new double[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayStressEdge = new double[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];

        arrayUtilizationNode = new double[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayUtilizationEdge = new double[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];

        recordDataLength = 0;
    }

    /*
     * note experimental section about parameter into .tex file
     */
    void recordTexParameter()
    {
        try
        {
            File flTexFileWriter = new File(fileAbsolutePath + dataFilePathString + "Fig/number.tex");

            if (!flTexFileWriter.exists())
            {
                try
                {
                    flTexFileWriter.createNewFile();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            FileWriter fwTexFileWriter = new FileWriter(flTexFileWriter);
            fwTexFileWriter.write(prefix + "addNewNodeCost" + "}{" + Parameter.addNewVirNodeCost + "}\n");
            fwTexFileWriter
                    .write(prefix + "transformExistedNodeCost" + "}{" + Parameter.transformExistedNodeCost + "}\n");
            fwTexFileWriter.write(prefix + "addNodeComputaionCost" + "}{" + Parameter.addNodeComputaionCost + "}\n");
            fwTexFileWriter.write(prefix + "addEdgeBandwithCost" + "}{" + Parameter.addEdgeBandwithCost + "}\n");
            fwTexFileWriter.write(prefix + "RelativeCostbetweenComputingBandwidth" + "}{"
                    + Parameter.RelativeCostbetweenComputingBandwidth + "}\n");

            fwTexFileWriter.write(prefix + "SubstrateNewtorkRunTimeInterval" + "}{"
                    + Parameter.SubstrateNewtorkRunTimeInterval + "}\n");
            fwTexFileWriter.write(prefix + "unitTimeInterval" + "}{" + Parameter.unitTimeInterval + "}\n");
            fwTexFileWriter
                    .write(prefix + "requestAppearProbability" + "}{" + Parameter.RequestAppearProbability + "}\n");
            fwTexFileWriter
                    .write(prefix + "RequestPerTimeAppearNum" + "}{" + Parameter.RequestPerTimeAppearNum + "}\n");
            fwTexFileWriter.write(prefix + "VNRequestsDuration" + "}{" + Parameter.VirNetDuration + "}\n");
            fwTexFileWriter.write(prefix + "PossionMean" + "}{" + Parameter.PossionMean + "}\n");

            fwTexFileWriter.write(
                    prefix + "VNRequestsContinueTimeMinimum" + "}{" + Parameter.VNRequestsContinueTimeMinimum + "}\n");
            fwTexFileWriter.write(
                    prefix + "VNRequestsContinueTimeMaximum" + "}{" + Parameter.VNRequestsContinueTimeMaximum + "}\n");
            fwTexFileWriter.write(
                    prefix + "VNRequestsContinueTimeAverage" + "}{" + Parameter.VNRequestsContinueTimeAverage + "}\n");
            fwTexFileWriter.write(prefix + "VNRequestsContinueTimeExponentialMean" + "}{"
                    + Parameter.VNRequestsContinueTimeExponentialMean + "}\n");

            fwTexFileWriter.write(prefix + "SubStrateNodeSize" + "}{" + Parameter.SubStrateNodeSize + "}\n");
            fwTexFileWriter.write(prefix + "SubStrateNodeComputationMinimum" + "}{"
                    + Parameter.SubStrateNodeComputationMinimum + "}\n");
            fwTexFileWriter.write(prefix + "SubStrateNodeComputationMaximum" + "}{"
                    + Parameter.SubStrateNodeComputationMaximum + "}\n");
            fwTexFileWriter.write(
                    prefix + "SubStrateNodenodeProbability" + "}{" + Parameter.SubStrateNodenodeProbability + "}\n");
            fwTexFileWriter.write(
                    prefix + "SubStrateEdgeBandwithMinimum" + "}{" + Parameter.SubStrateEdgeBandwithMinimum + "}\n");
            fwTexFileWriter.write(
                    prefix + "SubStrateEdgeBandwithMaximum" + "}{" + Parameter.SubStrateEdgeBandwithMaximum + "}\n");

            fwTexFileWriter.write(prefix + "VirtualNodeSizeMinimum" + "}{" + Parameter.VirtualNodeSizeMinimum + "}\n");
            fwTexFileWriter.write(prefix + "VirtualNodeSizeMaximum" + "}{" + Parameter.VirtualNodeSizeMaximum + "}\n");
            fwTexFileWriter.write(
                    prefix + "VirtualNodeComputationMinimum" + "}{" + Parameter.VirtualNodeComputationMinimum + "}\n");
            fwTexFileWriter.write(
                    prefix + "VirtualNodeComputationMaximum" + "}{" + Parameter.VirtualNodeComputationMaximum + "}\n");
            fwTexFileWriter
                    .write(prefix + "VirtualNodenodeProbability" + "}{" + Parameter.VirtualNodenodeProbability + "}\n");
            fwTexFileWriter
                    .write(prefix + "VirtualEdgeBandwithMinimum" + "}{" + Parameter.VirtualEdgeBandwithMinimum + "}\n");
            fwTexFileWriter
                    .write(prefix + "VirtualEdgeBandwithMaximum" + "}{" + Parameter.VirtualEdgeBandwithMaximum + "}\n");
            fwTexFileWriter.write(prefix + "SubStrateFacilityNodeFailDuration" + "}{"
                    + Parameter.SubStrateFacilityNodeFailDuration + "}\n");
            fwTexFileWriter.write(prefix + "ExperimentTimes" + "}{" + Parameter.ExperimentTimes + "}\n");

            fwTexFileWriter.flush();
            fwTexFileWriter.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * recordExperimentData.
     * 
     * @param experimentTimes
     *            experimentTimes
     * @param algorithm
     *            algorithm
     * @param time
     * 
     */
    public void recordExperimentData(int experimentTimes, SeVN algorithm, int time)
    {
        recordExperimentData4AllPerformenceMetric(experimentTimes, algorithm, time);
    }

    /**
     * recordExperimentData4EveryAlgorithmMappingCost.
     * 
     * @param experimentTimes
     *            experimentTimes
     * @param algorithm
     *            algorithm
     * @param time
     *            time
     */
    private void recordExperimentData4AllPerformenceMetric(int experimentTimes, SeVN algorithm, int time)
    {

        arrayActiveNodeVirNodeAcc[recordDataLength] = this.activeNodeVirNodeAcc;
        arrayPathLengthVirEdgeAcc[recordDataLength] = this.pathLengthVirEdgeAcc;
        arrayRevenueNodeCpAcc[recordDataLength] = this.revenueNodeCpAcc;
        arrayRevenueEdgeBwAcc[recordDataLength] = this.revenueEdgeBwAcc;

        arrayActiveNodeVirNode[recordDataLength] = this.activeNodeVirNode;
        arrayPathLengthVirEdge[recordDataLength] = this.pathLengthVirEdge;
        arrayRevenueNodeCp[recordDataLength] = this.revenueNodeCp;
        arrayRevenueEdgeBw[recordDataLength] = this.revenueEdgeBw;

        arrayVirNetReqAcc[recordDataLength] = this.virNetReqAcc;
        arraySurNetReqAcc[recordDataLength] = this.surNetReqAcc;

        arrayActiveNodeSubNodeAcc[recordDataLength] = this.activeNodeSubNodeAcc;
        arrayPathLengthSubEdgeAcc[recordDataLength] = this.pathLengthSubEdgeAcc;
        arrayCostNodeCpAcc[recordDataLength] = this.costNodeCpAcc;
        arrayCostEdgeBwAcc[recordDataLength] = this.costEdgeBwAcc;

        arrayActiveNodeSubNode[recordDataLength] = this.activeNodeSubNode;
        arrayPathLengthSubEdge[recordDataLength] = this.pathLengthSubEdge;
        arrayCostNodeCp[recordDataLength] = this.costNodeCp;
        arrayCostEdgeBw[recordDataLength] = this.costEdgeBw;

        arrayVirNetReq[recordDataLength] = this.virNetReq;
        arraySurNetReq[recordDataLength] = this.surNetReq;

        arrayMigrationFrequenceNode[recordDataLength] = this.migrationFrequenceNode;
        arrayMigrationFrequenceEdge[recordDataLength] = this.migrationFrequenceEdge;

        arrayStressNode[recordDataLength] = this.stressNode;
        arrayStressEdge[recordDataLength] = this.stressEdge;

        arrayUtilizationNode[recordDataLength] = this.utilizationNode;
        arrayUtilizationEdge[recordDataLength] = this.utilizationEdge;

        arrayAcceptanceRatioSurNet[recordDataLength] = this.acceptanceRatioSurNet;
        arrayAcceptanceRatioVirNet[recordDataLength] = this.acceptanceRatioVirNet;

        recordDataLength++;

    }

    /**
     * writeExperimentData4AcceptionRatio.
     * 
     * @param experimentTimes
     *            experimentTimes
     * @param algorithm
     *            algorithm
     */

    /**
     * writeExperimentData.
     * 
     * @param experimentTimes
     *            experimentTimes
     * @param algorithm
     *            algorithm
     * @param filename
     *            filename
     * @param recordData
     *            recordData
     * @param dataLength
     *            dataLength
     */
    private void writeExperimentData(int experimentTimes, SeVN algorithm, String filename, double[] recordData,
            int dataLength)
    {
        File fl = new File(fileAbsolutePath + dataFilePathString + filename + algorithm.algorithmName + ".txt");
        FileWriter fw;
        if (!fl.exists())
        {
            try
            {
                fl.createNewFile();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            if (experimentTimes == 0)
            {
                fw = new FileWriter(fl);
            } else
            {
                fw = new FileWriter(fl, true);
            }
            for (int i = 0; i < dataLength; i++)
            {
                fw.write(recordData[i] + "\n");
            }
            fw.flush();

            fw.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * recordExperimentParameter.
     * 
     * @param experimentTimes
     *            experimentTimes
     * @param algorithms
     *            algorithms
     * 
     */
    public void recordExperimentParameter(Vector<SeVN> algorithms)
    {
        File flParameter = new File(fileAbsolutePath + dataFilePathString + "Parameter.txt");
        FileWriter fwParameter;

        if (!flParameter.exists())
        {
            try
            {
                flParameter.createNewFile();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            fwParameter = new FileWriter(flParameter);
            fwParameter.write(Parameter.ExperimentTimes + "\n");
            fwParameter.write((Parameter.ExperimentPicturePlotNumber + 1) + "\n");
            fwParameter.write(Parameter.SubstrateNewtorkRunTimeInterval + "\n");
            fwParameter.write(algorithms.size() + "\n");
            fwParameter.write(Parameter.RelativeCostbetweenComputingBandwidth + "\n");
            fwParameter.write(Parameter.addNewVirNodeCost + "\n");
            fwParameter.flush();
            fwParameter.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * 
     * @param experimentTimes
     *            .
     * @param algorithm
     *            .
     * @param filename
     *            .
     * @param recordData
     *            .
     * @param datalength
     *            .
     */
    void writeExperimentData(int experimentTimes, SeVN algorithm, String filename, int[] recordData, int datalength)
    {
        File fl = new File(fileAbsolutePath + dataFilePathString + filename + algorithm.algorithmName + ".txt");
        FileWriter fw;
        if (!fl.exists())
        {
            try
            {
                fl.createNewFile();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            if (experimentTimes == 0)
            {
                fw = new FileWriter(fl);
            } else
            {
                fw = new FileWriter(fl, true);
            }
            for (int i = 0; i < datalength; i++)
            {
                fw.write(recordData[i] + "\n");
            }
            fw.flush();

            fw.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * updateExperimentData.
     * 
     * @param algorithm
     *            algorithm
     */
    public void updateExperimentData(SeVN algorithm)
    {
        // node
        int usedNode = 0;
        int usedEdge = 0;
        int nodeComputation = 0;
        int edgeBandwith = 0;
        for (int i = 0; i < algorithm.subNet.nodeSize; i++)
        {
            int nc = algorithm.subNet.nodeComputationCapacity[i]
                    - algorithm.subNet.getSubstrateRemainComputaion4VirNet(i, algorithm.isShared);

            if (nc < 0)
            {
                resultLog.error("nodeComputation less zero");
            }
            if (nc != 0)
            {
                usedNode++;
            }
            if (nc > 0)
            {
                nodeComputation += nc;
            }

            for (int j = 0; j < i; j++)
            {
                int bc = algorithm.subNet.edgeBandwithCapacity[i][j]
                        - algorithm.subNet.getSubStrateRemainBandwith4VirNet(i, j, algorithm.isShared);
                if (bc < 0)
                {
                    resultLog.error("edgeBandwith less zero");
                }
                if (bc != 0)
                {
                    usedEdge++;
                }
                if (bc > 0)
                {
                    edgeBandwith += bc;
                }

            }
        }

        this.activeNodeSubNode = usedNode;
        this.pathLengthSubEdge = usedEdge;
        this.costNodeCp = nodeComputation;
        this.costEdgeBw = edgeBandwith;

        usedNode = 0;
        usedEdge = 0;
        nodeComputation = 0;
        edgeBandwith = 0;
        int runningVirNetNum = 0;
        int runningSurNetNum = 0;
        for (int i = 0; i < algorithm.subNet.virNetSet.size(); i++)
        {
            VirtualNetwork vn = algorithm.subNet.virNetSet.get(i);
            if (vn != null && vn.isRunning)
            {
                runningVirNetNum++;
                for (int j = 0; j < vn.nodeSize; j++)
                {
                    if (vn.nodeComputationDemand[j] > 0)
                    {
                        usedNode++;
                        nodeComputation += vn.nodeComputationDemand[j];
                    }
                    for (int k = 0; k < j; k++)
                    {
                        if (vn.edgeBandwithDemand[j][k] > 0)
                        {
                            usedEdge++;
                            edgeBandwith += vn.edgeBandwithDemand[j][k];
                        }
                    }
                }
            }
            if (vn != null && vn.isRunning && vn.surVirNet.isSucceedEmbed)
            {
                runningSurNetNum++;
                for (int j = vn.nodeSize; j < vn.surVirNet.nodeSize; j++)
                {
                    if (vn.surVirNet.nodeComputation4Backup[j] > 0)
                    {
                        usedNode++;
                        nodeComputation += vn.surVirNet.nodeComputation4Backup[j];
                    }
                    for (int k = 0; k < j; k++)
                    {
                        if (vn.surVirNet.edgeBandwith4Backup[j][k] > 0)
                        {
                            usedEdge++;
                            edgeBandwith += vn.surVirNet.edgeBandwith4Backup[j][k];
                        }
                    }
                }
            }
        }

        this.activeNodeVirNode = usedNode;
        this.pathLengthVirEdge = usedEdge;
        this.revenueNodeCp = nodeComputation;
        this.revenueEdgeBw = edgeBandwith;
        this.surNetReq = runningSurNetNum;
        this.virNetReq = runningVirNetNum;

        // migration ratio
        int embeddedVN4SubstrateNode = 0;
        int embeddedVN4SubstrateEdge = 0;
        int vnRequestInSubstrateEdge = 0;
        int vnRequestInSubstrateNode = 0;
        for (int i = 0; i < algorithm.subNet.nodeSize; i++)
        {
            if (algorithm.subNet.nodeComputationCapacity[i] != algorithm.subNet.getSubstrateRemainComputaion4VirNet(i,
                    algorithm.isShared))
            {
                embeddedVN4SubstrateNode++;
            }

            for (int j = 0; j < algorithm.subNet.nodeSize; j++)
            {
                if (algorithm.subNet.edgeBandwithCapacity[i][j] != algorithm.subNet.getSubStrateRemainBandwith4VirNet(i,
                        j, algorithm.isShared))
                {
                    embeddedVN4SubstrateEdge++;
                }
            }
        }

        for (int i = 0; i < algorithm.subNet.nodeSize; i++)
        {
            for (int j = 0; j < algorithm.subNet.virNetIndexSet4sNode.get(i).size(); j++)
            {
                int vnIndex = algorithm.subNet.virNetIndexSet4sNode.get(i).get(j);
                if (algorithm.subNet.virNetSet.get(vnIndex) != null
                        && algorithm.subNet.virNetSet.get(vnIndex).isRunning)
                {
                    vnRequestInSubstrateNode++;
                }
            }
            for (int j = 0; j < algorithm.subNet.nodeSize; j++)
            {
                for (int k = 0; k < algorithm.subNet.virNetIndexSet4sEdge.get(i).get(j).size(); k++)
                {
                    int vnIndex = algorithm.subNet.virNetIndexSet4sEdge.get(i).get(j).get(k);
                    if (algorithm.subNet.virNetSet.get(vnIndex) != null
                            && algorithm.subNet.virNetSet.get(vnIndex).isRunning)
                    {
                        vnRequestInSubstrateEdge++;
                    }
                }
            }
        }
        if ((embeddedVN4SubstrateEdge == 0) || (embeddedVN4SubstrateNode == 0))
        {
            this.migrationFrequenceNode = 0;
            this.migrationFrequenceEdge = 0;
        } else
        {
            this.migrationFrequenceNode = ((1.0 * vnRequestInSubstrateNode / embeddedVN4SubstrateNode));
            this.migrationFrequenceEdge = ((1.0 * vnRequestInSubstrateEdge / embeddedVN4SubstrateEdge));
        }

        // stress
        this.stressNode = (1.0 * vnRequestInSubstrateNode / algorithm.subNet.nodeSize);
        this.stressEdge = (1.0 * vnRequestInSubstrateNode / algorithm.subNet.edgeSize);

        // utilization
        this.utilizationNode = 0;
        this.utilizationEdge = 0;
        for (int i = 0; i < algorithm.subNet.nodeSize; i++)
        {
            int all = algorithm.subNet.nodeComputationCapacity[i];
            int remain = algorithm.subNet.getSubstrateRemainComputaion4VirNet(i, algorithm.isShared);
            if (all != remain)
            {
                utilizationNode += ((all - remain) * 1.0 / algorithm.subNet.nodeComputationCapacity[i]);
            }

            for (int j = 0; j < algorithm.subNet.nodeSize; j++)
            {
                all = algorithm.subNet.edgeBandwithCapacity[i][j];
                remain = algorithm.subNet.getSubStrateRemainBandwith4VirNet(i, j, algorithm.isShared);
                if (all != remain)
                {
                    utilizationEdge += ((all - remain) * 1.0 / algorithm.subNet.edgeBandwithCapacity[i][j]);

                }
            }
        }
        this.utilizationNode = this.utilizationNode / algorithm.subNet.nodeSize;
        this.utilizationEdge = this.utilizationEdge / algorithm.subNet.edgeSize;

        // acception ratio
        if (0 == algorithm.subNet.virNetSet.size())
        {
            this.acceptanceRatioSurNet = 1.0;
            this.acceptanceRatioVirNet = 1.0;
        } else
        {
            this.acceptanceRatioSurNet = (1.0 * algorithm.subNet.surNetSuceedEmbedSum / algorithm.subNet.virNetReqSum);
            this.acceptanceRatioVirNet = (1.0 * algorithm.subNet.virNetSuceedEmbedSum / algorithm.subNet.virNetReqSum);

        }
    }

    /**
     * writeExperimentData.
     * 
     * @param experimentTimes
     *            experimentTimes
     * @param algorithm
     *            algorithm
     */
    public void writeExperimentDatatoFile(int experimentTimes, SeVN algorithm)
    {
        writeExperimentData(experimentTimes, algorithm, "ActiveNode_SubNode_", this.arrayActiveNodeSubNode,
                recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "PathLength_SubEdge_", this.arrayPathLengthSubEdge,
                recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "Cost_NodeCp_", this.arrayCostNodeCp, recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "Cost_NdgeBw_", this.arrayCostEdgeBw, recordDataLength);

        writeExperimentData(experimentTimes, algorithm, "ActiveNode_SubNode_Accumulate", this.arrayActiveNodeSubNodeAcc,
                recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "PathLength_SubEdge_Accumulate", this.arrayPathLengthSubEdgeAcc,
                recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "Cost_NodeCp_Accumulate", this.arrayCostNodeCpAcc,
                recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "Cost_EdgeBw_Accumulate", this.arrayCostEdgeBwAcc,
                recordDataLength);

        writeExperimentData(experimentTimes, algorithm, "ActiveNode_VirNode_", this.arrayActiveNodeVirNode,
                recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "PathLength_VirEdge_", this.arrayPathLengthVirEdge,
                recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "Revenue_NodeCp_", this.arrayRevenueNodeCp, recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "Revenue_EdgeBw_", this.arrayRevenueEdgeBw, recordDataLength);
        // Request_VirNet_Accumulate_ MapRevenue_virnet_
        writeExperimentData(experimentTimes, algorithm, "Request_VirNet_", this.arrayVirNetReq, recordDataLength);
        // MapRevenue_subnet_
        writeExperimentData(experimentTimes, algorithm, "Request_SurNet_", this.arraySurNetReq, recordDataLength);

        writeExperimentData(experimentTimes, algorithm, "ActiveNode_VirNode_Accumulate", this.arrayActiveNodeVirNodeAcc,
                recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "PathLength_VirEdge_Accumulate", this.arrayPathLengthVirEdgeAcc,
                recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "Revenue_NodeCp_Accumulate", this.arrayRevenueNodeCpAcc,
                recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "Revenue_EdgeBw_Accumulate", this.arrayRevenueEdgeBwAcc,
                recordDataLength);
        // MapRevenue_virnet_Sum_
        writeExperimentData(experimentTimes, algorithm, "Request_VirNet_Accumulate_", this.arrayVirNetReqAcc,
                recordDataLength);
        // MapRevenue_subnet_Sum_
        writeExperimentData(experimentTimes, algorithm, "Request_SurNet_Accumulate_", this.arraySurNetReqAcc,
                recordDataLength);

        // Migration
        // MigrationFrequence_node
        writeExperimentData(experimentTimes, algorithm, "MigrationFrequence_node", this.arrayMigrationFrequenceNode,
                recordDataLength);
        // MigrationFrequence_edge
        writeExperimentData(experimentTimes, algorithm, "MigrationFrequence_edge", this.arrayMigrationFrequenceEdge,
                recordDataLength);

        // Stress
        // Stress_node
        writeExperimentData(experimentTimes, algorithm, "Stress_Node", this.arrayStressNode, recordDataLength);
        // Stress_edge
        writeExperimentData(experimentTimes, algorithm, "Stress_Edge", this.arrayStressEdge, recordDataLength);

        // utilization
        // utilization_node
        writeExperimentData(experimentTimes, algorithm, "Utilization_Node", this.arrayUtilizationNode,
                recordDataLength);
        // utilization_edge
        writeExperimentData(experimentTimes, algorithm, "Utilization_Edge", this.arrayUtilizationEdge,
                recordDataLength);

        // Acception ratio
        writeExperimentData(experimentTimes, algorithm, "AcceptionRatio_VN_", this.arrayAcceptanceRatioVirNet,
                recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "AcceptionRatio_SVN_", this.arrayAcceptanceRatioSurNet,
                recordDataLength);

        recordDataLength = 0;
    }

    /**
     * 
     * @param algorithm
     *            .
     */
    public void updateExperimentDataAccumulate(SeVN algorithm)
    {
        // node
        int usedNode = 0;
        int usedEdge = 0;
        int nodeComputation = 0;
        int edgeBandwith = 0;
        for (int i = 0; i < algorithm.subNet.nodeSize; i++)
        {
            int nc = algorithm.subNet.nodeComputationCapacity[i]
                    - algorithm.subNet.getSubstrateRemainComputaion4VirNet(i, algorithm.isShared);

            if (nc < 0)
            {
                resultLog.error("nodeComputation less zero");
            }
            if (nc != 0)
            {
                usedNode++;
            }
            if (nc > 0)
            {
                nodeComputation += nc;
            }

            for (int j = 0; j < i; j++)
            {
                int bc = algorithm.subNet.edgeBandwithCapacity[i][j]
                        - algorithm.subNet.getSubStrateRemainBandwith4VirNet(i, j, algorithm.isShared);
                if (bc < 0)
                {
                    resultLog.error("edgeBandwith less zero");
                }
                if (bc != 0)
                {
                    usedEdge++;
                }
                if (bc > 0)
                {
                    edgeBandwith += bc;
                }

            }
        }

        this.costEdgeBwAcc += (edgeBandwith - this.costEdgeBw);
        this.pathLengthSubEdgeAcc += (usedEdge - this.pathLengthSubEdge);
        this.costNodeCpAcc += (nodeComputation - this.costNodeCp);
        this.activeNodeSubNodeAcc += (usedNode - this.activeNodeSubNode);

        this.costEdgeBw = edgeBandwith;
        this.pathLengthSubEdge = usedEdge;
        this.costNodeCp = nodeComputation;
        this.activeNodeSubNode = usedNode;

        usedNode = 0;
        usedEdge = 0;
        nodeComputation = 0;
        edgeBandwith = 0;
        int vnNum = 0;
        int svnNum = 0;
        for (int i = 0; i < algorithm.subNet.virNetSet.size(); i++)
        {
            VirtualNetwork vn = algorithm.subNet.virNetSet.get(i);
            if (vn != null && vn.isRunning)
            {
                vnNum++;
                for (int j = 0; j < vn.nodeSize; j++)
                {
                    if (vn.nodeComputationDemand[j] > 0)
                    {
                        usedNode++;
                        nodeComputation += vn.nodeComputationDemand[j];
                    }
                    for (int k = 0; k < j; k++)
                    {
                        if (vn.edgeBandwithDemand[j][k] > 0)
                        {
                            usedEdge++;
                            edgeBandwith += vn.edgeBandwithDemand[j][k];
                        }
                    }
                }
            }
            if (vn != null && vn.isRunning && vn.surVirNet.isSucceedEmbed)
            {
                svnNum++;
                for (int j = vn.nodeSize; j < vn.surVirNet.nodeSize; j++)
                {
                    if (vn.surVirNet.nodeComputation4Backup[j] > 0)
                    {
                        usedNode++;
                        nodeComputation += vn.surVirNet.nodeComputation4Backup[j];
                    }
                    for (int k = 0; k < j; k++)
                    {
                        if (vn.surVirNet.edgeBandwith4Backup[j][k] > 0)
                        {
                            usedEdge++;
                            edgeBandwith += vn.surVirNet.edgeBandwith4Backup[j][k];
                        }
                    }
                }
            }
        }

        this.activeNodeVirNodeAcc += (usedNode - this.activeNodeVirNode);
        this.pathLengthVirEdgeAcc += (usedEdge - this.pathLengthVirEdge);
        this.revenueEdgeBwAcc += (edgeBandwith - this.revenueEdgeBw);
        this.revenueNodeCpAcc += (nodeComputation - this.revenueNodeCp);

        this.virNetReqAcc = algorithm.subNet.virNetSuceedEmbedSum;
        if (algorithm.algorithmName.equals("VirNet"))
        {
            this.surNetReqAcc = this.virNetReqAcc;
        } else
        {
            this.surNetReqAcc = algorithm.subNet.surNetSuceedEmbedSum;
        }

        this.activeNodeVirNode = usedNode;
        this.pathLengthVirEdge = usedEdge;
        this.revenueNodeCp = nodeComputation;
        this.revenueEdgeBw = edgeBandwith;

        this.virNetReq = vnNum;
        // when algorithm is VN, set its sur num is equal vn.
        if (algorithm.algorithmName.equals("VirNet"))
        {
            this.surNetReq = vnNum;
        } else
        {
            this.surNetReq = svnNum;
        }

    }

}
