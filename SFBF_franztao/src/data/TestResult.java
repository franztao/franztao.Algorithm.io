package data;

import java.io.FileWriter;
import java.io.IOException;

public class TestResult {
	public int testunitnum;
	public int datalength;
	public int[][] ExtensionRound;
	public int[][] FalsePositiveRate;
	public int[][] SpaceSizeOfFilter;
	public double[][] QueryCPUTimeWithoutShifting;
	public double[][] QueryCPUTimeWithShifting;
	public double insertdatasumtime;
	public int samplinsite = 100;

	public TestResult(int test, int length) {
		this.testunitnum = test;
		this.datalength = length;
		this.ExtensionRound = new int[this.testunitnum + 1][this.datalength + 1];
		this.FalsePositiveRate = new int[this.testunitnum + 2][(this.datalength/samplinsite)+1];
		this.SpaceSizeOfFilter = new int[this.testunitnum + 1][this.datalength + 1];
		this.QueryCPUTimeWithoutShifting = new double[this.testunitnum + 2][(this.datalength/samplinsite)+1];
		this.QueryCPUTimeWithShifting = new double[this.testunitnum + 2][(this.datalength/samplinsite)+1];
	}

	public void writeresultdatatodatfile(String path) throws IOException {
		String mid;
		mid = path + "\\Querying CPU time with shifting.txt";
		FileWriter filewriter = new FileWriter(mid);
		for (int i = 0; i < this.testunitnum + 1; i++) {
			for (int j = 0; j <= (this.datalength / samplinsite); j++) {
				filewriter.write(QueryCPUTimeWithShifting[i][j] + "\r\n");
			}
			filewriter.write("\r\n");
		}
		filewriter.close();

		mid = path + "\\Querying CPU time without shifting.txt";
		filewriter = new FileWriter(mid);
		for (int i = 0; i < this.testunitnum+1; i++) {
			for (int j = 0; j <= (this.datalength / samplinsite); j++) {
				filewriter.write(QueryCPUTimeWithoutShifting[i][j] + "\r\n");
			}
			filewriter.write("\r\n");
		}
		filewriter.close();

		mid = path + "\\False positive rate.txt";
		filewriter = new FileWriter(mid);
		for (int i = 0; i < this.testunitnum + 2; i++) {
			for (int j = 0; j <= (this.datalength / samplinsite); j++) {
				filewriter.write(FalsePositiveRate[i][j] + "\r\n");
			}
			filewriter.write("\r\n");
		}
		filewriter.close();
		
		mid = path + "\\Space size of filters.txt";
		filewriter = new FileWriter(mid);
		for (int i = 0; i < this.testunitnum + 1; i++) {
			for (int j = 0; j <= this.datalength; j++) {
				if (j % 100 == 0)
					filewriter.write(SpaceSizeOfFilter[i][j] + "\r\n");
			}
			filewriter.write("\r\n");
		}
		filewriter.close();

		mid = path + "\\Extension round.txt";
		filewriter = new FileWriter(mid);
		for (int i = 0; i < this.testunitnum + 1; i++) {
			for (int j = 0; j <= this.datalength; j++) {
				if (j % 100 == 0)
					filewriter.write(ExtensionRound[i][j] + "\r\n");
			}
			filewriter.write("\r\n");
		}
		filewriter.close();

		
	}

}
