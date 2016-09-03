package geneticalgorithm;

import java.util.Random;
import java.util.Vector;

public class PSO {

	// Prerequisite:
	// 1.the topology of the graph is symmetrical.
	// 2.The weighted value of the edge is range from 1 to 100.

	// note:1.the storage mode of the graph is adjacent matrix.
	// 2.the node number is from 0 to node.
	// 3.the value of edge is between 0 and 100.
	// 4.source is 0 ,destination is 99
	int[][] Graph;
	int node;
	int[] value;
	int[] mostgreatithvalue;
	int mostgreatvalue;
	Random ran;

	int U, T;
	public int pop_size;
	public int interative;

	public class pair {
		int first;
		int second;

	};

	Vector<pair>[] v;// =new [this.node]Vector<pair>();
	Vector<pair>[] P;// =new [this.node]Vector<pair>();
	Vector<pair>[] X;// =new [this.node]Vector<pair>();
	Vector<pair> G;

	// public Vector<>
	public PSO(int i, int initpopsize, int iter) {
		// TODO Auto-generated constructor stub
		this.node = i;
		this.pop_size = initpopsize;
		this.Graph = new int[this.node][this.node];
		ran = new Random();
		this.interative = iter;
		value = new int[this.node];
		mostgreatithvalue = new int[this.node];
		this.mostgreatvalue=0;
		this.U = 7;
		this.T = 5;
		v = new Vector[this.node];
		for (int ii = 0; ii < this.node; ii++) {
			v[ii] = new Vector<pair>();
		}
		P = new Vector[this.node];
		for (int ii = 0; ii < this.node; ii++) {
			P[ii] = new Vector<pair>();
		}
		X = new Vector[this.node];
		for (int ii = 0; ii < this.node; ii++) {
			X[ii] = new Vector<pair>();
		}
		G = new Vector<pair>();

	}

	public void run() {
		// TODO Auto-generated method stub
		initpop();
	}

	private void initpop() {
		// TODO Auto-generated method stub
		for (int i = 0; i < this.node; i++) {
			this.Graph[i][i] = 0;
			for (int j = i + 1; j < this.node; j++) {
				this.Graph[i][j] = ran.nextInt(100);
				this.Graph[j][i] = this.Graph[i][j];
			}
		}

		int f, s;
		for (int i = 0; i < this.pop_size; i++) {
			do {
				f = ran.nextInt(this.node);
			} while ((f != 0) && (f != this.node));
			do {
				s = ran.nextInt(this.node);
			} while ((f != s) && (s != 0) && (s != this.node));
			pair p = new pair();
			p.first = f;
			p.second = s;
			X[i].addElement(p);
			v[i].addElement(p);
		}
		calculatvalue();
		while (this.interative >= 0) {
			this.interative--;
			getithVelocity();
			getithPath();
			calculatvalue();
			printPath();
		}
	}

	private void printPath() {
		// TODO Auto-generated method stub
		int[] array = new int[this.node];
		for (int i = 0; i < this.pop_size; i++) {
			for (int j = 0; j < this.node; j++) {
				array[j] = j;
			}
			for (int j = 0; j < G.size(); j++) {
				int e1, e2;
				e1 = G.elementAt(j).first;
				e2 = G.elementAt(j).second;
				int mid = array[e1];
				array[e1] = array[e2];
				array[e2] = mid;
			}
		}
		System.out.print("The ith iterative best path:");
		for(int i=0;i<this.node;i++){
			System.out.print(array[i]+" ");
		}
		System.out.println("");
	}

	private void getithPath() {
		// TODO Auto-generated method stub
		for(int i=0;i<this.pop_size;i++){
			for(int j=0;j<P[i].size();j++){
				X[i].addElement(v[i].get(j));
			}
		}
	}

	private void getithVelocity() {
		// TODO Auto-generated method stub
		int u,t;
		u=ran.nextInt(10);
		t=ran.nextInt(10);
		if(u<=this.U){
			for(int i=0;i<this.pop_size;i++){
				for(int j=0;j<P[i].size();j++){
					v[i].addElement(P[i].get(j));
				}
				for(int j=X[i].size()-1;j>=0;j--){
					v[i].addElement(X[i].elementAt(j));
				}
			}
			
				
		}
		if(t<=this.T){
			for(int i=0;i<this.pop_size;i++){
				for(int j=0;j<P[i].size();j++){
					v[i].addElement(P[i].elementAt(j));
				}
				for(int j=G.size()-1;j>=0;j--){
					v[i].addElement(G.elementAt(j));
				}
			}
					
		}
	}

	public void calculatvalue() {
		int[] array = new int[this.node];
		for (int i = 0; i < this.pop_size; i++) {
			for (int j = 0; j < this.node; j++) {
				array[j] = j;
			}
			for (int j = 0; j < X[i].size(); j++) {
				int e1, e2;
				e1 = v[i].elementAt(j).first;
				e2 = v[i].elementAt(j).second;
				int mid = array[e1];
				array[e1] = array[e2];
				array[e2] = mid;
			}
			int sum = 0;
			for (int j = 0; j < (this.node - 1); j++) {
				sum += this.Graph[array[j]][array[j + 1]];
			}
			this.value[i] = sum;
			if (this.value[i] > this.mostgreatithvalue[i]) {
				this.mostgreatithvalue[i] = sum;
				P[i].clear();
				for (int j = 0; j < X[i].size(); j++) {
					P[i].addElement(X[i].elementAt(j));
				}
			}
			if (this.value[i] > this.mostgreatvalue) {
				this.mostgreatvalue = this.value[i];
				G.clear();
				for (int j = 0; j < X[i].size(); j++) {
					G.addElement(X[i].elementAt(j));
				}

			}
		}
	}

}
