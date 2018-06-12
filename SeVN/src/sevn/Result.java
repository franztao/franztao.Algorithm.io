/**
 * 
 */

package sevn;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import algorithm.SeVNAlgorithm;
import virtualNetwork.VirtualNetwork;

/**
 * Result.
 * 
 * @author Taoheng
 *
 */
public class Result
{
    private Logger resultLog = Logger.getLogger(Result.class.getName());

    String fileAbsolutePath = Parameter.FileAbsolutePath;//
    String dataFilePathString = "/Data/";
    String prefix = "\\newcommand{\\";

    public int virNetReq;
    public int surNetReq;
    public int virNetReqAcc;
    public int surNetReqAcc;
    public int[] arrayVirNetReq;
    public int[] arraySurNetReq;
    public int[] arrayVirNetReqAcc;
    public int[] arraySurNetReqAcc;

    private double acceptanceRatioSurNet;
    private double acceptanceRatioVirNet;
    public double[] arrayAcceptanceRatioSurNet;
    public double[] arrayAcceptanceRatioVirNet;

    public int activeNodeVirNode;
    public double activeNodeVirNodeAcc;
    public int activeNodeSubNode;
    public double activeNodeSubNodeAcc;
    public int[] arrayActiveNodeVirNode;
    public double[] arrayActiveNodeVirNodeAcc;
    public int[] arrayActiveNodeSubNode;
    public double[] arrayActiveNodeSubNodeAcc;

    public int pathLengthVirEdge;
    public int pathLengthSubEdge;
    public double pathLengthSubEdgeAcc;
    public double pathLengthVirEdgeAcc;
    public int[] arrayPathLengthVirEdge;
    public double[] arrayPathLengthVirEdgeAcc;
    public double[] arrayPathLengthSubEdge;
    public double[] arrayPathLengthSubEdgeAcc;

    public int costNodeCp;
    public int costEdgeBw;
    public double costNodeCpAcc;
    public double costEdgeBwAcc;
    public int[] arrayCostNodeCp;
    public int[] arrayCostEdgeBw;
    public double[] arrayCostEdgeBwAcc;
    public double[] arrayCostNodeCpAcc;

    public int revenueNodeCp;
    public int revenueEdgeBw;
    public double revenueNodeCpAcc;
    public double revenueEdgeBwAcc;
    public int[] arrayRevenueNodeCp;
    public int[] arrayRevenueEdgeBw;
    public double[] arrayRevenueEdgeBwAcc;
    public double[] arrayRevenueNodeCpAcc;

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

    public double edgeWeight;
    public double edgeWeightAcc;
    public double[] arrayEdgeWeight;
    public double[] arrayEdgeWeightAcc;

    public int accumulateSum;
    public int recordDataLength;

    /**
     * Result.
     */
    public Result()
    {
        resultLog.setLevel(Parameter.logLevel);
        PropertyConfigurator.configure("log4j.properties");
        arrayAcceptanceRatioSurNet = new double[(Parameter.ExperimentPicturePlotNumber + 1)
                * Parameter.ExperimentTimes];
        arrayAcceptanceRatioVirNet = new double[(Parameter.ExperimentPicturePlotNumber + 1)
                * Parameter.ExperimentTimes];

        arrayCostEdgeBwAcc = new double[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayCostNodeCpAcc = new double[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayActiveNodeSubNodeAcc = new double[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];

        arrayCostEdgeBw = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayCostNodeCp = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayActiveNodeSubNode = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];

        arrayPathLengthSubEdgeAcc = new double[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayPathLengthSubEdge = new double[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];

        arrayActiveNodeVirNodeAcc = new double[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayRevenueEdgeBwAcc = new double[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayRevenueNodeCpAcc = new double[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];

        arrayRevenueNodeCp = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayRevenueEdgeBw = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayActiveNodeVirNode = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];

        arrayVirNetReqAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arraySurNetReqAcc = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayVirNetReq = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arraySurNetReq = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayPathLengthVirEdgeAcc = new double[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayPathLengthVirEdge = new int[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];

        arrayMigrationFrequenceNode = new double[(Parameter.ExperimentPicturePlotNumber + 1)
                * Parameter.ExperimentTimes];
        arrayMigrationFrequenceEdge = new double[(Parameter.ExperimentPicturePlotNumber + 1)
                * Parameter.ExperimentTimes];

        arrayStressNode = new double[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayStressEdge = new double[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];

        arrayUtilizationNode = new double[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayUtilizationEdge = new double[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];

        arrayEdgeWeight = new double[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];
        arrayEdgeWeightAcc = new double[(Parameter.ExperimentPicturePlotNumber + 1) * Parameter.ExperimentTimes];

        this.recordDataLength = 0;
        this.accumulateSum = 0;
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

            fwTexFileWriter.write(prefix + "NodeFailureNumberStart" + "}{" + Parameter.NodeFailureNumberStart + "}\n");
            fwTexFileWriter.write(prefix + "NodeFailureNumberEnd" + "}{" + Parameter.NodeFailureNumberEnd + "}\n");

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
    public void recordExperimentData(int experimentTimes, SeVNAlgorithm algorithm, int time)
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
    private void recordExperimentData4AllPerformenceMetric(int experimentTimes, SeVNAlgorithm algorithm, int time)
    {

        arrayVirNetReq[recordDataLength] = this.virNetReq;
        arraySurNetReq[recordDataLength] = this.surNetReq;
        arrayVirNetReqAcc[recordDataLength] = this.virNetReqAcc;
        arraySurNetReqAcc[recordDataLength] = this.surNetReqAcc;

        arrayAcceptanceRatioSurNet[recordDataLength] = this.acceptanceRatioSurNet;
        arrayAcceptanceRatioVirNet[recordDataLength] = this.acceptanceRatioVirNet;

        arrayActiveNodeVirNode[recordDataLength] = this.activeNodeVirNode;
        arrayActiveNodeVirNodeAcc[recordDataLength] = this.activeNodeVirNodeAcc;
        arrayActiveNodeSubNode[recordDataLength] = this.activeNodeSubNode;
        arrayActiveNodeSubNodeAcc[recordDataLength] = this.activeNodeSubNodeAcc;

        arrayPathLengthVirEdge[recordDataLength] = this.pathLengthVirEdge;
        arrayPathLengthVirEdgeAcc[recordDataLength] = this.pathLengthVirEdgeAcc;
        arrayPathLengthSubEdge[recordDataLength] = this.pathLengthSubEdge;
        arrayPathLengthSubEdgeAcc[recordDataLength] = this.pathLengthSubEdgeAcc;

        arrayRevenueNodeCp[recordDataLength] = this.revenueNodeCp;
        arrayRevenueNodeCpAcc[recordDataLength] = this.revenueNodeCpAcc;
        arrayRevenueEdgeBw[recordDataLength] = this.revenueEdgeBw;
        arrayRevenueEdgeBwAcc[recordDataLength] = this.revenueEdgeBwAcc;

        arrayCostNodeCp[recordDataLength] = this.costNodeCp;
        arrayCostEdgeBw[recordDataLength] = this.costEdgeBw;
        arrayCostNodeCpAcc[recordDataLength] = this.costNodeCpAcc;
        arrayCostEdgeBwAcc[recordDataLength] = this.costEdgeBwAcc;

        arrayMigrationFrequenceNode[recordDataLength] = this.migrationFrequenceNode;
        arrayMigrationFrequenceEdge[recordDataLength] = this.migrationFrequenceEdge;

        arrayStressNode[recordDataLength] = this.stressNode;
        arrayStressEdge[recordDataLength] = this.stressEdge;

        arrayUtilizationNode[recordDataLength] = this.utilizationNode;
        arrayUtilizationEdge[recordDataLength] = this.utilizationEdge;

        arrayEdgeWeight[recordDataLength] = this.edgeWeight;
        arrayEdgeWeightAcc[recordDataLength] = this.edgeWeightAcc;

        recordDataLength++;

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
    public void recordExperimentParameter(Vector<SeVNAlgorithm> algorithms)
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

            fwParameter.write(Parameter.PossionMeanStart + "\n");
            fwParameter.write(Parameter.PossionMeanEnd + "\n");
            fwParameter.write(Parameter.PossionMeanAdd + "\n");

            fwParameter.write(Parameter.ExponentialMeanStart + "\n");
            fwParameter.write(Parameter.ExponentialMeanEnd + "\n");
            fwParameter.write(Parameter.ExponentialMeanAdd + "\n");

            fwParameter.write(Parameter.NodeFailureNumberStart + "\n");
            fwParameter.write(Parameter.NodeFailureNumberEnd + "\n");

            fwParameter.flush();
            fwParameter.close();
        } catch (IOException e)
        {
            e.printStackTrace();
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
    public void writeExperimentDatatoFile(int experimentTimes, SeVNAlgorithm algorithm)
    {
        // Request_VirNet_Accumulate_ MapRevenue_virnet_
        writeExperimentData(experimentTimes, algorithm, "Request_VirNet_" + Parameter.ExperimentFileString,
                this.arrayVirNetReq, recordDataLength);
        // MapRevenue_subnet_
        writeExperimentData(experimentTimes, algorithm, "Request_SurNet_" + Parameter.ExperimentFileString,
                this.arraySurNetReq, recordDataLength);
        // MapRevenue_virnet_Sum_
        writeExperimentData(experimentTimes, algorithm, "Request_VirNet_Accumulate_" + Parameter.ExperimentFileString,
                this.arrayVirNetReqAcc, recordDataLength);
        // MapRevenue_subnet_Sum_
        writeExperimentData(experimentTimes, algorithm, "Request_SurNet_Accumulate_" + Parameter.ExperimentFileString,
                this.arraySurNetReqAcc, recordDataLength);

        // ActiveNode
        writeExperimentData(experimentTimes, algorithm, "ActiveNode_VirNode_" + Parameter.ExperimentFileString,
                this.arrayActiveNodeVirNode, recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "ActiveNode_SubNode_" + Parameter.ExperimentFileString,
                this.arrayActiveNodeSubNode, recordDataLength);
        writeExperimentData(experimentTimes, algorithm,
                "ActiveNode_SubNode_Accumulate_" + Parameter.ExperimentFileString, this.arrayActiveNodeSubNodeAcc,
                recordDataLength);
        writeExperimentData(experimentTimes, algorithm,
                "ActiveNode_VirNode_Accumulate_" + Parameter.ExperimentFileString, this.arrayActiveNodeVirNodeAcc,
                recordDataLength);

        writeExperimentData(experimentTimes, algorithm, "PathLength_SubEdge_" + Parameter.ExperimentFileString,
                this.arrayPathLengthSubEdge, recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "PathLength_VirEdge_" + Parameter.ExperimentFileString,
                this.arrayPathLengthVirEdge, recordDataLength);
        writeExperimentData(experimentTimes, algorithm,
                "PathLength_SubEdge_Accumulate_" + Parameter.ExperimentFileString, this.arrayPathLengthSubEdgeAcc,
                recordDataLength);
        writeExperimentData(experimentTimes, algorithm,
                "PathLength_VirEdge_Accumulate_" + Parameter.ExperimentFileString, this.arrayPathLengthVirEdgeAcc,
                recordDataLength);

        writeExperimentData(experimentTimes, algorithm, "Cost_NodeCp_" + Parameter.ExperimentFileString,
                this.arrayCostNodeCp, recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "Cost_NodeCp_Accumulate_" + Parameter.ExperimentFileString,
                this.arrayCostNodeCpAcc, recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "Cost_EdgeBw_" + Parameter.ExperimentFileString,
                this.arrayCostEdgeBw, recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "Cost_EdgeBw_Accumulate_" + Parameter.ExperimentFileString,
                this.arrayCostEdgeBwAcc, recordDataLength);

        writeExperimentData(experimentTimes, algorithm, "Revenue_NodeCp_" + Parameter.ExperimentFileString,
                this.arrayRevenueNodeCp, recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "Revenue_EdgeBw_" + Parameter.ExperimentFileString,
                this.arrayRevenueEdgeBw, recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "Revenue_NodeCp_Accumulate_" + Parameter.ExperimentFileString,
                this.arrayRevenueNodeCpAcc, recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "Revenue_EdgeBw_Accumulate_" + Parameter.ExperimentFileString,
                this.arrayRevenueEdgeBwAcc, recordDataLength);

        // Migration
        // MigrationFrequence_node
        writeExperimentData(experimentTimes, algorithm, "MigrationFrequence_node_" + Parameter.ExperimentFileString,
                this.arrayMigrationFrequenceNode, recordDataLength);
        // MigrationFrequence_edge
        writeExperimentData(experimentTimes, algorithm, "MigrationFrequence_edge_" + Parameter.ExperimentFileString,
                this.arrayMigrationFrequenceEdge, recordDataLength);

        // Stress
        // Stress_node
        writeExperimentData(experimentTimes, algorithm, "Stress_Node_" + Parameter.ExperimentFileString,
                this.arrayStressNode, recordDataLength);
        // Stress_edge
        writeExperimentData(experimentTimes, algorithm, "Stress_Edge_" + Parameter.ExperimentFileString,
                this.arrayStressEdge, recordDataLength);

        // utilization
        // utilization_node
        writeExperimentData(experimentTimes, algorithm, "Utilization_Node_" + Parameter.ExperimentFileString,
                this.arrayUtilizationNode, recordDataLength);
        // utilization_edge
        writeExperimentData(experimentTimes, algorithm, "Utilization_Edge_" + Parameter.ExperimentFileString,
                this.arrayUtilizationEdge, recordDataLength);

        // Acception ratio
        writeExperimentData(experimentTimes, algorithm, "AcceptionRatio_VN_" + Parameter.ExperimentFileString,
                this.arrayAcceptanceRatioVirNet, recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "AcceptionRatio_SVN_" + Parameter.ExperimentFileString,
                this.arrayAcceptanceRatioSurNet, recordDataLength);

        // EdgeWeight
        writeExperimentData(experimentTimes, algorithm, "EdgeWeight_" + Parameter.ExperimentFileString,
                this.arrayEdgeWeight, recordDataLength);
        writeExperimentData(experimentTimes, algorithm, "EdgeWeight_Accumulate_" + Parameter.ExperimentFileString,
                this.arrayEdgeWeightAcc, recordDataLength);

        recordDataLength = 0;
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
    void writeExperimentData(int experimentTimes, SeVNAlgorithm algorithm, String filename, int[] recordData,
            int datalength)
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
     * @param time
     */
    public void updateExperimentData(SeVNAlgorithm algorithm, int time)
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
                resultLog.error("Substrate nodeComputation less zero");
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
                    resultLog.error("Substrate edgeBandwith less zero");
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

        int edgeWeight = 0;

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
            if (vn != null && vn.isRunning && (vn.surVirNet.isSucceedProtection))
            {
                edgeWeight += vn.surVirNet.edgeWeightSum;
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

        this.edgeWeight = edgeWeight;
        this.edgeWeightAcc += edgeWeight;

        this.activeNodeVirNode = usedNode;
        this.pathLengthVirEdge = usedEdge;
        this.revenueNodeCp = nodeComputation;
        this.revenueEdgeBw = edgeBandwith;
        if (algorithm.algorithmName.equals("VirNet"))
        {
            this.surNetReq = runningVirNetNum;
        } else
        {
            this.surNetReq = runningSurNetNum;
        }

        this.virNetReq = runningVirNetNum;

        // migration ratio
        int embeddedVN4SubstrateNode = 0;
        int embeddedVN4SubstrateEdge = 0;
        int vnRequestInSubstrateNode = 0;
        int vnRequestInSubstrateEdge = 0;
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
            for (int j = 0; j < i; j++)
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
            this.migrationFrequenceNode = ((time - 1) * this.migrationFrequenceNode
                    + (1.0 * vnRequestInSubstrateNode / embeddedVN4SubstrateNode)) / time;
            this.migrationFrequenceEdge = ((time - 1) * this.migrationFrequenceEdge
                    + (1.0 * vnRequestInSubstrateEdge / embeddedVN4SubstrateEdge)) / time;
        }

        // stress
        if (time > 1)
        {
            this.stressNode = ((time - 1) * this.stressNode
                    + (1.0 * vnRequestInSubstrateNode / algorithm.subNet.nodeSize)) / time;
            this.stressEdge = ((time - 1) * this.stressEdge
                    + (1.0 * vnRequestInSubstrateEdge / algorithm.subNet.edgeSize)) / time;
        }

        // utilization
        this.utilizationNode = 0;
        this.utilizationEdge = 0;

        double utilizationN = 0;
        double utilizationE = 0;
        for (int i = 0; i < algorithm.subNet.nodeSize; i++)
        {
            int all = algorithm.subNet.nodeComputationCapacity[i];
            int remain = algorithm.subNet.getSubstrateRemainComputaion4VirNet(i, algorithm.isShared);
            if (all != remain)
            {
                utilizationN += ((all - remain) * 1.0 / all);
            }

            for (int j = 0; j < algorithm.subNet.nodeSize; j++)
            {
                all = algorithm.subNet.edgeBandwithCapacity[i][j];
                remain = algorithm.subNet.getSubStrateRemainBandwith4VirNet(i, j, algorithm.isShared);
                if (all != remain)
                {
                    utilizationE += ((all - remain) * 1.0 / all);

                }
            }
        }
        if (time > 1)
        {
            this.utilizationNode = ((time - 1) * this.utilizationNode + (utilizationN / algorithm.subNet.nodeSize))
                    / time;
            this.utilizationEdge = ((time - 1) * this.utilizationEdge + (utilizationE / algorithm.subNet.edgeSize))
                    / time;
        }
        // acception ratio
        if (0 == algorithm.subNet.virNetSet.size())
        {
            this.acceptanceRatioSurNet = 1.0;
            this.acceptanceRatioVirNet = 1.0;
        } else
        {
            this.acceptanceRatioSurNet = (1.0 * algorithm.subNet.surNetSuceedEmbedSum / algorithm.subNet.reqSum);
            this.acceptanceRatioVirNet = (1.0 * algorithm.subNet.virNetSuceedEmbedSum / algorithm.subNet.reqSum);

        }
    }

    /**
     * 
     * @param algorithm
     *            .
     */
    public void updateExperimentDataAccumulate(SeVNAlgorithm algorithm)
    {
        // node
        int activeNode = 0;
        int activeEdge = 0;
        int nodeComputation = 0;
        int edgeBandwith = 0;
        for (int i = 0; i < algorithm.subNet.nodeSize; i++)
        {
            int nc = algorithm.subNet.nodeComputationCapacity[i]
                    - algorithm.subNet.getSubstrateRemainComputaion4VirNet(i, algorithm.isShared);

            if (nc < 0)
            {
                resultLog.error("SubstrateNetworkt nodeComputation less zero");
            }
            if (nc != 0)
            {
                activeNode++;
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
                    resultLog.error("SubstrateNetworkt edgeBandwith less zero");
                }
                if (bc != 0)
                {
                    activeEdge++;
                }
                if (bc > 0)
                {
                    edgeBandwith += bc;
                }

            }
        }

        if (Parameter.IsIncrementSum)
        {
            if ((edgeBandwith - this.costEdgeBw) > 0)
            {
                this.costEdgeBwAcc += (edgeBandwith - this.costEdgeBw);
            }

            if ((activeEdge - this.pathLengthSubEdge) > 0)
            {
                this.pathLengthSubEdgeAcc += (activeEdge - this.pathLengthSubEdge);
            }

            if ((nodeComputation - this.costNodeCp) > 0)
            {
                this.costNodeCpAcc += (nodeComputation - this.costNodeCp);
            }
            if ((activeNode - this.activeNodeSubNode) > 0)
            {
                this.activeNodeSubNodeAcc += (activeNode - this.activeNodeSubNode);
            }
        } else
        {
            this.costEdgeBwAcc += edgeBandwith;
            // this.costEdgeBwAcc += 1.0 * this.costEdgeBwAcc / (this.accumulateSum + 1);

            this.pathLengthSubEdgeAcc += activeEdge;
            // this.pathLengthSubEdgeAcc = 1.0 * this.pathLengthSubEdgeAcc /
            // (this.accumulateSum + 1);

            this.costNodeCpAcc += nodeComputation;
            // this.costNodeCpAcc = 1.0 * this.costNodeCpAcc / (this.accumulateSum + 1);

            this.activeNodeSubNodeAcc += activeNode;
            // this.activeNodeSubNodeAcc = 1.0 * this.activeNodeSubNodeAcc /
            // (this.accumulateSum + 1);

        }

        this.costEdgeBw = edgeBandwith;
        this.pathLengthSubEdge = activeEdge;
        this.costNodeCp = nodeComputation;
        this.activeNodeSubNode = activeNode;

        activeNode = 0;
        activeEdge = 0;
        nodeComputation = 0;
        edgeBandwith = 0;
        int vnNum = 0;
        int svnNum = 0;

        int edgeWeight = 0;
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
                        activeNode++;
                        nodeComputation += vn.nodeComputationDemand[j];
                    }
                    for (int k = 0; k < j; k++)
                    {
                        if (vn.edgeBandwithDemand[j][k] > 0)
                        {
                            activeEdge++;
                            edgeBandwith += vn.edgeBandwithDemand[j][k];
                        }
                    }
                }
            }
            // algorithm.algorithmName.equals("VirNet")||
            if (vn != null && vn.isRunning && (vn.surVirNet.isSucceedProtection))
            {
                edgeWeight += vn.surVirNet.edgeWeightSum;
                svnNum++;
                for (int j = vn.nodeSize; j < vn.surVirNet.nodeSize; j++)
                {
                    if (vn.surVirNet.nodeComputation4Backup[j] > 0)
                    {
                        activeNode++;
                        nodeComputation += vn.surVirNet.nodeComputation4Backup[j];
                    }
                    for (int k = 0; k < j; k++)
                    {
                        if (vn.surVirNet.edgeBandwith4Backup[j][k] > 0)
                        {
                            activeEdge++;
                            edgeBandwith += vn.surVirNet.edgeBandwith4Backup[j][k];
                        }
                    }
                }
            }
        }

        if (Parameter.IsIncrementSum)
        {
            if ((activeNode - this.activeNodeVirNode) > 0)
            {
                this.activeNodeVirNodeAcc += (activeNode - this.activeNodeVirNode);
            }

            if ((activeEdge - this.pathLengthVirEdge) > 0)
            {
                this.pathLengthVirEdgeAcc += (activeEdge - this.pathLengthVirEdge);
            }

            if ((edgeBandwith - this.revenueEdgeBw) > 0)
            {
                this.revenueEdgeBwAcc += (edgeBandwith - this.revenueEdgeBw);
            }

            if ((nodeComputation - this.revenueNodeCp) > 0)
            {
                this.revenueNodeCpAcc += (nodeComputation - this.revenueNodeCp);
            }

            if ((edgeWeight - this.edgeWeight) > 0)
            {
                this.edgeWeightAcc += (edgeWeight - this.edgeWeight);
            }
        } else
        {
            this.activeNodeVirNodeAcc += activeNode;
            // this.activeNodeVirNodeAcc = 1.0 * this.activeNodeVirNodeAcc /
            // (this.accumulateSum + 1);

            this.pathLengthVirEdgeAcc += activeEdge;
            // this.pathLengthVirEdgeAcc = 1.0 * this.pathLengthVirEdgeAcc /
            // (this.accumulateSum + 1);

            this.revenueEdgeBwAcc += edgeBandwith;
            // this.revenueEdgeBwAcc = 1.0 * this.revenueEdgeBwAcc / (this.accumulateSum +
            // 1);

            this.revenueNodeCpAcc += nodeComputation;
            // this.revenueNodeCpAcc = 1.0 * this.revenueNodeCpAcc / (this.accumulateSum +
            // 1);

            this.edgeWeightAcc += edgeWeight;
            // this.edgeWeightAcc = 1.0 * this.edgeWeightAcc / (this.accumulateSum + 1);
        }

        this.virNetReq = vnNum;
        if (algorithm.algorithmName.equals("VirNet"))
        {
            this.surNetReq = vnNum;
        } else
        {
            this.surNetReq = svnNum;
        }

        // this.virNetReqAcc += algorithm.subNet.virNetSuceedEmbedSum;
        this.virNetReqAcc += vnNum;
        if (algorithm.algorithmName.equals("VirNet"))
        {
            this.surNetReqAcc += this.virNetReq;
        } else
        {
            this.surNetReqAcc += this.surNetReq;
        }

        this.activeNodeVirNode = activeNode;
        this.pathLengthVirEdge = activeEdge;
        this.revenueNodeCp = nodeComputation;
        this.revenueEdgeBw = edgeBandwith;
        this.edgeWeight = edgeWeight;

        this.accumulateSum = this.accumulateSum + 1;

    }

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
    private void writeExperimentData(int experimentTimes, SeVNAlgorithm algorithm, String filename, double[] recordData,
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

}
