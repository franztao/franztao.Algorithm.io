package c45;

import java.util.Vector;

public class TreeNode {
	int root;
	int father;
	Vector<Integer> son;
	int num_son;
	int target_attrbutes;
	double target_attrbutes_left;
	double target_attrbutes_right;
	boolean label;
	public TreeNode(int fa, int rootindex) {
		// TODO Auto-generated constructor stub
		this.father=fa;
		this.root=rootindex;
		this.num_son=0;
		this.son=new Vector<Integer>();
	}

}
