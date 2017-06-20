package kmeans;

import main.Data;

public class KMeans {
	Data[] datacentroid;
	int sum_centroid;

	public Data[] datapoint;
	int sum_datapoint;

	int sum_attribute;
	int iteration;

	public KMeans() {
		// TODO Auto-generated constructor stub

	}

	public KMeans(Data[] datapoint2, int num_data, Data[] datacentroid2, int num_cent, int num_attribute, int it) {
		// TODO Auto-generated constructor stub
		this.sum_centroid=num_cent;
		this.sum_datapoint=num_data;
		datapoint = new Data[num_data];
		iteration = it;
		for (int i1 = 0; i1 < num_data; i1++) {
			datapoint[i1] = new Data(num_attribute);
			for (int j = 0; j < num_attribute; j++) {
				datapoint[i1].attribute[j] = datapoint2[i1].attribute[j];
			}
			datapoint[i1].num_attribute = num_attribute;
			datapoint[i1].clusterclass = datapoint2[i1].clusterclass;
		}

		datacentroid = new Data[num_cent];
		for (int i1 = 0; i1 < num_cent; i1++) {
			datacentroid[i1] = new Data(num_attribute);
			for (int j = 0; j < num_attribute; j++) {
				datacentroid[i1].attribute[j] = datacentroid2[i1].attribute[j];
			}
			datacentroid[i1].num_attribute = num_attribute;
			datacentroid[i1].clusterclass = datacentroid2[i1].clusterclass;
		}

	}
	public void run() {
		int[] c = new int[this.sum_datapoint];
		while ((this.iteration--) > 0) {
			for (int i = 0; i < this.sum_datapoint; i++) {
				c[i] = argmin_j(i);
				this.datapoint[i].clusterclass=c[i];
			}
			for (int j = 0; j < this.sum_centroid; j++) {
				datacentroid[j].avepoint4sum_centroid = 0;
				for (int k = 0; k < this.sum_attribute; k++) {
					datacentroid[j].attribute[k] = 0;
				}
			}
			for (int i = 0; i < this.sum_datapoint; i++) {
				int index = c[i];
				for (int k = 0; k < this.sum_attribute; k++) {
					datacentroid[index].attribute[k] += datapoint[i].attribute[k];
					datacentroid[index].avepoint4sum_centroid++;
				}
			}
			for (int i = 0; i < this.sum_centroid; i++) {
				for (int k = 0; k < this.sum_attribute; k++) {
					datacentroid[i].attribute[k] /= datacentroid[i].avepoint4sum_centroid;
				}
			}

		}
	}
	private int argmin_j(int i) {
		// TODO Auto-generated method stub
		double min = -1;
		int id = 0;
		for (int j = 0; j < this.sum_centroid; j++) {
			if (min < 0) {
				min = distance(this.datapoint[i], this.datacentroid[j]);
				id = j;
			} else {
				double val = distance(this.datapoint[i], this.datacentroid[j]);
				if (min < val) {
					min = val;
					id = j;
				}
			}
		}
		return id;
	}

	private double distance(Data data, Data data2) {
		// TODO Auto-generated method stub
		// euclide distance
		double sum_distance = 0;
		for (int i = 0; i < data.num_attribute; i++) {
			sum_distance += (Math.pow(Math.abs(data.attribute[i] - data2.attribute[i]), 2));
		}
		sum_distance = Math.sqrt(sum_distance);
		
		// cosine similarity
		double xdotsy,normx,normy;
		xdotsy=normx=normy=0;
		for (int i = 0; i < data.num_attribute; i++) {
			xdotsy += (data.attribute[i] * data2.attribute[i]);
			normx+=(data.attribute[i]*data.attribute[i]);
			normy+=(data2.attribute[i]*data2.attribute[i]);
		}
		//sum_distance=xdotsy/(normx*normy);
		return sum_distance;
	}

}
