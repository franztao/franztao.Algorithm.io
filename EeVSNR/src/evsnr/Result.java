/**
 * 
 */
package evsnr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Taoheng
 *
 */
public class Result {

	String FileAbsolutePath = "C:\\Users\\Taoheng\\Desktop\\NFT4VNR\\";//
	String prefix = "\\newcommand{\\";

	/**
	 * 
	 */
	public Result() {
	}

	void recordParameter() {
		try {
			FileWriter TexFileWriter = new FileWriter(FileAbsolutePath + "number.tex");
			TexFileWriter.write(prefix + "addNewNodeCost" + "}{" + EVSNR.addNewNodeCost + "}\n");
			TexFileWriter.write(prefix + "transformExistedNodeCost" + "}{" + EVSNR.transformExistedNodeCost + "}\n");
			TexFileWriter.write(prefix + "addNodeComputaionCost" + "}{" + EVSNR.addNodeComputaionCost + "}\n");
			TexFileWriter.write(prefix + "addEdgeBandwithCost" + "}{" + EVSNR.addEdgeBandwithCost + "}\n");
			TexFileWriter.write(prefix + "RelativeCostbetweenComputingBandwidth" + "}{"
					+ EVSNR.RelativeCostbetweenComputingBandwidth + "}\n");

			TexFileWriter.write(
					prefix + "SubstrateNewtorkRunTimeInterval" + "}{" + EVSNR.SubstrateNewtorkRunTimeInterval + "}\n");
			TexFileWriter.write(prefix + "unitTimeInterval" + "}{" + EVSNR.unitTimeInterval + "}\n");
			TexFileWriter.write(prefix + "requestAppearProbability" + "}{" + EVSNR.requestAppearProbability + "}\n");
			TexFileWriter.write(prefix + "VNRequestsDuration" + "}{" + EVSNR.VNRequestsDuration + "}\n");
			TexFileWriter.write(
					prefix + "VNRequestsContinueTimeMinimum" + "}{" + EVSNR.VNRequestsContinueTimeMinimum + "}\n");
			TexFileWriter.write(
					prefix + "VNRequestsContinueTimeMaximum" + "}{" + EVSNR.VNRequestsContinueTimeMaximum + "}\n");

			TexFileWriter.write(prefix + "SubStrateNodeSize" + "}{" + EVSNR.SubStrateNodeSize + "}\n");
			TexFileWriter.write(
					prefix + "SubStrateNodeComputationMinimum" + "}{" + EVSNR.SubStrateNodeComputationMinimum + "}\n");
			TexFileWriter.write(
					prefix + "SubStrateNodeComputationMaximum" + "}{" + EVSNR.SubStrateNodeComputationMaximum + "}\n");
			TexFileWriter
					.write(prefix + "SubStrateNodenodeProbability" + "}{" + EVSNR.SubStrateNodenodeProbability + "}\n");
			TexFileWriter
					.write(prefix + "SubStrateEdgeBandwithMinimum" + "}{" + EVSNR.SubStrateEdgeBandwithMinimum + "}\n");
			TexFileWriter
					.write(prefix + "SubStrateEdgeBandwithMaximum" + "}{" + EVSNR.SubStrateEdgeBandwithMaximum + "}\n");

			TexFileWriter.write(prefix + "VirtualNodeSizeMinimum" + "}{" + EVSNR.VirtualNodeSizeMinimum + "}\n");
			TexFileWriter.write(prefix + "VirtualNodeSizeMaximum" + "}{" + EVSNR.VirtualNodeSizeMaximum + "}\n");
			TexFileWriter.write(
					prefix + "VirtualNodeComputationMinimum" + "}{" + EVSNR.VirtualNodeComputationMinimum + "}\n");
			TexFileWriter.write(
					prefix + "VirtualNodeComputationMaximum" + "}{" + EVSNR.VirtualNodeComputationMaximum + "}\n");
			TexFileWriter
					.write(prefix + "VirtualNodenodeProbability" + "}{" + EVSNR.VirtualNodenodeProbability + "}\n");
			TexFileWriter
					.write(prefix + "VirtualEdgeBandwithMinimum" + "}{" + EVSNR.VirtualEdgeBandwithMinimum + "}\n");
			TexFileWriter
					.write(prefix + "VirtualEdgeBandwithMaximum" + "}{" + EVSNR.VirtualEdgeBandwithMaximum + "}\n");
			TexFileWriter.write(prefix + "SubStrateFacilityNodeFailDuration" + "}{"
					+ EVSNR.SubStrateFacilityNodeFailDuration + "}\n");

			TexFileWriter.flush();
			TexFileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param algorithm
	 * @param time
	 * 
	 */
	public void recordExperimentData(Algorithm algorithm, int time) {
		recordExperimentData4AcceptionRatio(algorithm, time);
		recordExperimentData4MappingCost(algorithm, time);
		recordExperimentData4MigrationFrequence(algorithm, time);
	}

	/**
	 * @param algorithm
	 * @param time
	 */
	private void recordExperimentData4MigrationFrequence(Algorithm algorithm, int time) {
		File f_MappingCost_node = new File(
				FileAbsolutePath + "\\Data\\" + "MigrationFrequence_" + algorithm.algorithmName + ".txt");
		FileWriter fw_MigrationFrequence_node;
		if (!f_MappingCost_node.exists()) {
			try {
				f_MappingCost_node.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			if (time == 0) {
				fw_MigrationFrequence_node = new FileWriter(f_MappingCost_node);
			} else {
				fw_MigrationFrequence_node = new FileWriter(f_MappingCost_node, true);
			}
			if (time == 0) {
				fw_MigrationFrequence_node.write("0\n");
			} else {
				int failnode = (int) (Math.random() * (algorithm.getSn().nodeSize - 1));
				int migrate = 0;
				for (int i = 0; i < algorithm.getSn().VNCollection.size(); i++) {
					if (algorithm.getSn().VNCollection.get(i).getIsRunning()) {
						for (int j = 0; j < algorithm.getSn().VNCollection.get(i).nodeSize; j++) {
							if (algorithm.getSn().VNCollection.get(i).vNode2sNode[j] == failnode) {
								migrate++;
							}
						}
					}
				}
				fw_MigrationFrequence_node.write(migrate + "\n");
			}
			fw_MigrationFrequence_node.flush();
			fw_MigrationFrequence_node.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param algorithm
	 * @param time
	 */
	private void recordExperimentData4MappingCost(Algorithm algorithm, int time) {
		File f_MappingCost_node = new File(
				FileAbsolutePath + "\\Data\\" + "MappingCost_node_" + algorithm.algorithmName + ".txt");
		File f_MappingCost_edge = new File(
				FileAbsolutePath + "\\Data\\" + "MappingCost_edge_" + algorithm.algorithmName + ".txt");
		FileWriter fw_MappingCost_node;
		FileWriter fw_MappingCost_edge;

		if (!f_MappingCost_node.exists()) {
			try {
				f_MappingCost_node.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!f_MappingCost_edge.exists()) {
			try {
				f_MappingCost_edge.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			if (time == 0) {
				fw_MappingCost_node = new FileWriter(f_MappingCost_node);
				fw_MappingCost_edge = new FileWriter(f_MappingCost_edge);
			} else {
				fw_MappingCost_node = new FileWriter(f_MappingCost_node, true);
				fw_MappingCost_edge = new FileWriter(f_MappingCost_edge, true);
			}
			int nodeCompution = 0;
			for (int i = 0; i < algorithm.getSn().nodeSize; i++) {
				nodeCompution += (algorithm.getSn().nodeComputationCapacity[i]
						- algorithm.getSn().getSubstrateRemainComputaion4VN(i, algorithm.isShared()));
//				nodeCompution+=(algorithm.getSn().nodeComputation4Former[i]
//						+ algorithm.getSn().nodeComputation4Temp[i] + algorithm.getSn().nodeComputation4EnhanceNoSharedSum[i]);
			}
			int edgeBandwith = 0;
			for (int i = 0; i < algorithm.getSn().nodeSize; i++) {
				for (int j = 0; j < i; j++) {
					edgeBandwith += (algorithm.getSn().edgeBandwithCapacity[i][j]
							- algorithm.getSn().getSubStrateRemainBandwith4VN(i, j, algorithm.isShared()));
//					
//					edgeBandwith +=(algorithm.getSn().edgeBandwith4Former[i][j] + algorithm.getSn().edgeBandwith4Temp[i][j]
//							+ algorithm.getSn().edgeBandwith4EnhanceNoSharedSum[i][j]);
				}
			}
			fw_MappingCost_edge.write(""+edgeBandwith + "\n");
			fw_MappingCost_node.write(""+nodeCompution + "\n");

			fw_MappingCost_edge.flush();
			fw_MappingCost_node.flush();

			fw_MappingCost_edge.close();
			fw_MappingCost_node.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param time
	 * @param algorithm
	 * 
	 */
	private void recordExperimentData4AcceptionRatio(Algorithm algorithm, int time) {
		File f_AcceptionRatio_evn = new File(
				FileAbsolutePath + "\\Data\\" + "AcceptionRatio_EVN_" + algorithm.algorithmName + ".txt");
		File f_AcceptionRatio_vn = new File(
				FileAbsolutePath + "\\Data\\" + "AcceptionRatio_VN_" + algorithm.algorithmName + ".txt");
		FileWriter fw_AcceptionRatio_evn;
		FileWriter fw_AcceptionRatio_vn;

		if (!f_AcceptionRatio_evn.exists()) {
			try {
				f_AcceptionRatio_evn.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!f_AcceptionRatio_vn.exists()) {
			try {
				f_AcceptionRatio_vn.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			if (time == 0) {
				fw_AcceptionRatio_evn = new FileWriter(f_AcceptionRatio_evn);
				fw_AcceptionRatio_vn = new FileWriter(f_AcceptionRatio_vn);
			} else {
				fw_AcceptionRatio_evn = new FileWriter(f_AcceptionRatio_evn, true);
				fw_AcceptionRatio_vn = new FileWriter(f_AcceptionRatio_vn, true);
			}
			if (0 == algorithm.getSn().VNCollection.size()) {
				fw_AcceptionRatio_vn.write("1\n");
				fw_AcceptionRatio_evn.write("1\n");

			} else {
				fw_AcceptionRatio_vn.write((1.0 * algorithm.getSn().vnSuceedMap / algorithm.getSn().vnqNumber) + "\n");
				fw_AcceptionRatio_evn
						.write((1.0 * algorithm.getSn().evnSuceedMap / algorithm.getSn().vnqNumber) + "\n");
			}
			fw_AcceptionRatio_vn.flush();
			fw_AcceptionRatio_evn.flush();

			fw_AcceptionRatio_vn.close();
			fw_AcceptionRatio_evn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
