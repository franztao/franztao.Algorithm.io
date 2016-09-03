package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import c45.C45;
import kmeans.KMeans;

public class Algorithm {

	Data[] datapoint;
	Data[] datacentroid;
	int num_data;
	int num_attribute;
	KMeans kmeans;
	C45 c45;

	int sum_realspam;
	int sum_testspam;
	int spam_nospam;
	int nospam_spam;

	public Algorithm() {
		// TODO Auto-generated constructor stub
		this.num_attribute = 57;
		this.num_data = 0;
		datapoint = new Data[5000];
		datacentroid = new Data[2];
	}

	void run() throws IOException {
		getData();
		kmeans = new KMeans(datapoint, num_data, datacentroid, 2, num_attribute, 1000);
		kmeans.run();

		c45 = new C45(datapoint, num_data, num_attribute);
		c45.run();

		evaluation();

	}

	private void evaluation() {
		sum_realspam = spam_nospam = nospam_spam = sum_testspam = 0;
		// TODO Auto-generated method stub
		System.out.println("------------------K-means -----------------------");
		for (int i = 0; i < this.num_data; i++) {
			if (this.datapoint[i].clusterclass == 1)
				sum_realspam++;
			if (this.kmeans.datapoint[i].clusterclass == 1)
				sum_testspam++;
			if ((this.datapoint[i].clusterclass == 0) && (this.kmeans.datapoint[i].clusterclass == 1)) {
				nospam_spam++;
			}
			if ((this.datapoint[i].clusterclass == 1) && (this.kmeans.datapoint[i].clusterclass == 0)) {
				spam_nospam++;
			}
		}
		System.out.println("sum of round-truth spam :" + sum_realspam + " sum of test spam :" + sum_testspam);
		System.out.println("false positive :" + nospam_spam);
		System.out.println("false negative :" + spam_nospam);

		System.out.println("------------------K-means -----------------------\n");

		System.out.println("------------------C4.5 -----------------------");
		sum_realspam = spam_nospam = nospam_spam = sum_testspam = 0;
		for (int i = 0; i < this.num_data; i++) {
			if (this.datapoint[i].clusterclass == 1)
				sum_realspam++;
			if ((c45.serach(this.datapoint[i])==true))
				sum_testspam++;
			if ((this.datapoint[i].clusterclass == 0) && (c45.serach(this.datapoint[i])==true)) {
				nospam_spam++;
			}
			if ((this.datapoint[i].clusterclass == 1) && (c45.serach(this.datapoint[i])==false)) {
				spam_nospam++;
			}
		}
		System.out.println("sum of round-truth spam :" + sum_realspam + " sum of test spam :" + sum_testspam);
		System.out.println("false positive :" + nospam_spam);
		System.out.println("false negative :" + spam_nospam);

		System.out.println("------------------C4.5 -----------------------");
	}

	void getData() throws IOException {

		String abusolutepath = "C:\\Users\\Taoheng\\Desktop\\data mining work\\spambase\\";
		String str = "spambase.data";
		FileReader fin = new FileReader(abusolutepath + str);
		BufferedReader bf = new BufferedReader(fin);

		String line;
		String attributestring;
		// int num_attribute;

		line = bf.readLine();
		int beginIndex, endIndex;
		int index;
		while (line != null) {
			beginIndex = 0;
			endIndex = 0;
			index = 0;
			datapoint[this.num_data] = new Data(this.num_attribute);
			datapoint[this.num_data].num_attribute = this.num_attribute;
			for (; endIndex <= line.length();) {
				if (endIndex == line.length() || line.charAt(endIndex) == ',') {
					attributestring = line.substring(beginIndex, endIndex);
					if (attributestring != null) {
						if (endIndex != line.length()) {
							datapoint[this.num_data].attribute[index] = Double.parseDouble(attributestring);
						} else {
							datapoint[this.num_data].clusterclass = Integer.parseInt(attributestring);
						}
					}
					beginIndex = endIndex + 1;
					endIndex = beginIndex;
					index++;
				} else {
					endIndex++;
				}

			}
			this.num_data++;
			line = bf.readLine();
		}
		datacentroid[0] = new Data(this.num_attribute);
		datacentroid[1] = new Data(this.num_attribute);

		datacentroid[0].num_attribute = this.num_attribute;
		datacentroid[1].num_attribute = this.num_attribute;

		datacentroid[0].clusterclass = 0;
		datacentroid[1].clusterclass = 1;
		boolean isinitial = true;
		if (isinitial) {
			int dir;
			for (int i = 0; i < this.num_data; i++) {
				if (datapoint[i].clusterclass == 0) {
					dir = 0;
				} else
					dir = 1;
				for (int j = 0; j < this.num_attribute; j++) {
					if (j == 0)
						datacentroid[dir].attribute[j] = 0;
					datacentroid[dir].attribute[j] += datapoint[i].attribute[j];
				}
			}
			for (int j = 0; j < this.num_attribute; j++) {
				datacentroid[0].attribute[j] /= this.num_data;
				datacentroid[1].attribute[j] /= this.num_data;
			}
		}
	}
}
