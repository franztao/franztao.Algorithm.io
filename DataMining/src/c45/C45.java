package c45;

import java.util.Vector;

import main.Data;

public class C45 {

	Vector<TreeNode> treenode;
	int rootindex;

	public Data[] datapoint;
	int sum_datapoint;
	int sum_attribute;
	int num_sub = 2;

	public C45(Data[] datapoint2, int num_data, int num_attribute) {
		// TODO Auto-generated constructor stub
		this.rootindex = 0;
		this.sum_datapoint = num_data;
		this.treenode = new Vector<TreeNode>();
		this.sum_attribute = num_attribute;
		datapoint = new Data[num_data];
		for (int i1 = 0; i1 < num_data; i1++) {
			datapoint[i1] = new Data(num_attribute);
			for (int j = 0; j < num_attribute; j++) {
				datapoint[i1].attribute[j] = datapoint2[i1].attribute[j];
			}
			datapoint[i1].num_attribute = num_attribute;
			datapoint[i1].clusterclass = datapoint2[i1].clusterclass;
		}
	}

	public int C45_BuildTree(int fa, boolean[] examples, int seattribute, double target_attrbutes_left,
			double target_attrbutes_right, boolean[] attributes, int remain_attributes) {

		int root;
		int labelyes = 0, lableno = 0;

		TreeNode tn = new TreeNode(fa, this.rootindex);
		tn.target_attrbutes = seattribute;
		tn.target_attrbutes_left = target_attrbutes_left;
		tn.target_attrbutes_right = target_attrbutes_right;
		treenode.add(tn);
		this.rootindex++;

		root = tn.root;
		boolean positive = false, negative = false;
		int selectattribute;

		for (int i = 0; i < this.sum_datapoint; i++) {
			if (examples[i] == false) {
				if (this.datapoint[i].clusterclass == 0) {
					positive = true;
				} else {
					negative = true;
				}
				if (this.datapoint[i].attribute[seattribute] > target_attrbutes_left) {
					if (this.datapoint[i].attribute[seattribute] < target_attrbutes_right) {
						if (this.datapoint[i].clusterclass == 0) {
							lableno++;
						} else {
							labelyes++;
						}

					}
				}
			}
		}
		if (!(positive && negative)) {
			if (positive)
				tn.label = false;
			else
				tn.label = true;
			treenode.setElementAt(tn, tn.root);
			return root;
		}
		if (0 == remain_attributes) {
			if (labelyes > lableno)
				tn.label = true;
			else
				tn.label = false;
			treenode.setElementAt(tn, tn.root);
			return root;
		}
		if (positive && negative) {
			selectattribute = InformationGainRatio(examples, attributes, remain_attributes);
			double left, right;
			for (int j = 0; j < this.num_sub; j++) {
				if (selectattribute < 54) {
					left = j * (100.0 / this.num_sub);
					right = (j + 1) * (100.0 / this.num_sub);
				} else {
					left = j * 100;
					right = (j + 1) * 100;
					if ((j + 1) == this.num_sub)
						right = Double.MAX_VALUE;
				}
				boolean[] midexamples = new boolean[this.sum_datapoint];
				boolean isemptyexample = true;
				labelyes = 0;
				lableno = 0;

				for (int i = 0; i < this.sum_datapoint; i++) {
					midexamples[i] = examples[i];
					if (examples[i] == false) {
						if ((left < this.datapoint[i].attribute[selectattribute])
								&& (right > this.datapoint[i].attribute[selectattribute])) {
							isemptyexample = false;
							midexamples[i] = true;
						}
					}
				}
				if (isemptyexample) {
					TreeNode emptytn = new TreeNode(root, this.rootindex);
					if (labelyes > lableno)
						emptytn.label = true;
					else
						emptytn.label = false;
					treenode.add(emptytn);
					this.rootindex++;
					tn.son.add(emptytn.root);
					tn.num_son++;

				} else {
					boolean[] midattributes = new boolean[this.sum_datapoint];
					for (int i = 0; i < this.sum_attribute; i++) {
						midattributes[i] = attributes[i];
					}
					midattributes[selectattribute] = true;
					int midroot = C45_BuildTree(root, midexamples, selectattribute, left, right, midattributes,
							(remain_attributes - 1));
					tn.son.add(midroot);
					tn.num_son++;
				}
			}

		}
		treenode.setElementAt(tn, tn.root);
		return root;
	}

	private int InformationGainRatio(boolean[] examples, boolean[] attributes, int remain_attributes) {
		if (1 == remain_attributes) {
			for (int i = 0; i < this.sum_attribute; i++) {
				if (false == attributes[i])
					return i;
			}
		}
		double max_GrianRate = -1;
		int select = -1;
		double Info;
		int D, CiD;

		double InfoA, Gain, SplitInfoA;
		int Dj;
		double left, right;
		for (int i = 0; i < this.sum_attribute; i++) {
			if (false == attributes[i]) {
				Info = 0;
				D = 0;
				CiD = 0;
				for (int j = 0; j < this.sum_datapoint; j++) {
					if (false == examples[j]) {
						D++;
						if (this.datapoint[j].clusterclass == 0) {
							CiD++;
						}
					}
				}
				Info += (-1.0 * (1.0 * CiD / D) * Math.log(1.0 * CiD / D) / Math.log(2));
				CiD = (D - CiD);
				Info += (-1.0 * (1.0 * CiD / D) * Math.log(1.0 * CiD / D) / Math.log(2));

				InfoA = 0;
				SplitInfoA = 0;
				for (int j = 0; j < this.num_sub; j++) {
					if (i < 54) {
						left = j * (100.0 / this.num_sub);
						right = (j + 1) * (100.0 / this.num_sub);
					} else {
						left = j * 100;
						right = (j + 1) * 100;
						if ((j + 1) == this.num_sub)
							right = Double.MAX_VALUE;
					}
					Dj = 0;
					int midCiD = 0;
					for (int k = 0; k < this.sum_datapoint; k++) {
						if (false == examples[k]) {
							if ((this.datapoint[k].attribute[i] > left) && (this.datapoint[k].attribute[i] < right)) {
								Dj++;
								if (this.datapoint[k].clusterclass == 0)
									midCiD++;
							}
						}
					}
					if (Dj > 0) {
						InfoA += (1.0 * (1.0 * Dj / D)
								* ((-1.0 * (1.0 * midCiD / Dj) * Math.log(1.0 * midCiD / Dj) / Math.log(2))
										+ (-1.0 * (1.0 * (Dj - midCiD) / Dj) * Math.log(1.0 * (Dj - midCiD) / Dj)
												/ Math.log(2))));
						SplitInfoA += (-1.0 * (1.0 * Dj / D) * Math.log(1.0 * Dj / D) / Math.log(2));
					}
				}
				double GrianRate = (Info - InfoA) / SplitInfoA;
				if (max_GrianRate < 0) {
					max_GrianRate = GrianRate;
					select = i;
				} else {
					if (GrianRate > max_GrianRate) {
						max_GrianRate = GrianRate;
						select = i;
					}

				}
			}

		}
		return select;
		// TODO Auto-generated method stub

	}

	public boolean serach(Data data) {
		// TODO Auto-generated method stub
		return serach_C45tree(data, treenode.get(0));
	}

	private boolean serach_C45tree(Data da, TreeNode trno) {
		if (trno.num_son == 0) {
			return trno.label;
		}
		for (int i = 0; i < trno.num_son; i++) {
			TreeNode midtreenode = treenode.get(trno.son.get(i));
			int selectatr = midtreenode.target_attrbutes;
			if ((da.attribute[selectatr] >= midtreenode.target_attrbutes_left)
					&& (da.attribute[selectatr] <= midtreenode.target_attrbutes_right)) {
				return serach_C45tree(da, midtreenode);
			}
		}
		return false;

	}

	public void run() {
		// TODO Auto-generated method stub
		boolean[] ex = new boolean[this.sum_datapoint];
		boolean[] att = new boolean[this.sum_attribute];
		// int fa, boolean[] examples, int seattribute, double
		// target_attrbutes_left,
		// double target_attrbutes_right, boolean[] attributes, int
		// remain_attributes
		C45_BuildTree(-1, ex, 27, 0, 100, att, this.sum_attribute);

	}
}
