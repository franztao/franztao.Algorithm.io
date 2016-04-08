package geneticalgorithm;

import java.util.HashSet;
import java.util.Random;
import java.util.Stack;

public class TSP {

	// Prerequisite:
	// 1.the topology of the graph is symmetrical.
	// 2.The weighted value of the edge is range from 1 to 100.

	// note:1.the storage mode of the graph is adjacent matrix.
	// 2.the node number is from 0 to node.
	// 3.the value of edge is between 0 and 100.
	int[][] Graph;
	int destination;
	int node;
	Random ran;

	public int pop_size;
	public int[][] chromosome;
	// chromosome_size= node's int
	public double[] Dfitness_individual;
	public double[] Dfitnesspercent_individual;

	int Imidvalue;
	public int interative;

	public TSP(int i, int size, int iter) {
		// TODO Auto-generated constructor stub
		this.node = i;
		this.Graph = new int[this.node][this.node];

		ran = new Random();

		this.pop_size = size;
		this.interative = iter;
	}

	public void run() {
		// TODO Auto-generated method stub
		// init the edge value of the graph;
		Imidvalue = 0;
		for (int i = 0; i < this.node; i++) {
			this.Graph[i][i] = 0;
			for (int j = i + 1; j < this.node; j++) {
				this.Graph[i][j] = ran.nextInt(100);
				this.Graph[j][i] = this.Graph[i][j];
				this.Imidvalue += this.Graph[i][j];
			}
		}
		IndividualCoding();
		printGene();
		for (; interative >= 0; interative--) {
			System.out.println("Duplication");
			DuplicationCalculation();
			printGene();

			System.out.println("Cross");
			Crosscalculation();
			printGene();

			System.out.println("Mutation");
			MutaionCalculation();
			printGene();
		}

	}

	private void IndividualCoding() {
		// TODO Auto-generated method stub
		// step 1:produce initial group
		// individual coding:The generation of initial population
		// a node's int of array represent a chromosome
		chromosome = new int[pop_size][this.node];
		Dfitnesspercent_individual = new double[this.pop_size];

		for (int i = 0; i < this.pop_size; i++) {
			// this.individual_coding[i]=ran.nextInt(16);
			this.chromosome[i] = permutation(this.node);

		}

	}

	private void MutaionCalculation() {
		// TODO Auto-generated method stub
		// step 4: mutation calculation
		// random exchange two int's of the chromosome,the two int is not in the
		// same location of array.
		int r1, r2;
		do {
			r1 = ran.nextInt(this.node);
			r2 = ran.nextInt(this.node);
		} while (r1 == r2);
		for (int i = 0; i < this.pop_size; i++) {
			this.chromosome[i][r1] = this.chromosome[i][r1] ^ this.chromosome[i][r2];
			this.chromosome[i][r2] = this.chromosome[i][r1] ^ this.chromosome[i][r2];
			this.chromosome[i][r1] = this.chromosome[i][r1] ^ this.chromosome[i][r2];
		}

	}

	private void Crosscalculation() {
		// TODO Auto-generated method stub
		// step 3: cross calculation
		//Partially Matching Crossover
		for (int i = 0; i < this.pop_size; i += 2) {
			int loc = ran.nextInt(this.node);
			for (int j = loc + 1; j < this.node; j++) {

				this.chromosome[i][j] = this.chromosome[i][j] ^ this.chromosome[i + 1][j];

				this.chromosome[i + 1][j] = this.chromosome[i][j] ^ this.chromosome[i + 1][j];

				this.chromosome[i][j] = this.chromosome[i][j] ^ this.chromosome[i + 1][j];

			}
			boolean flag[] = new boolean[this.node];
			Stack<Integer> st = new Stack<Integer>();

			for (int k = 0; k < this.node; k++) {
				flag[k] = false;
			}
			for (int k = 0; k < this.node; k++) {
				flag[this.chromosome[i][k]] = true;
			}
			for (int k = 0; k < this.node; k++) {
				if (flag[k] == false) {
					st.push(k);
				}
			}
			for (int k = 0; k < this.node; k++) {
				flag[k] = false;
			}
			for (int k = 0; k < this.node; k++) {
				if (flag[this.chromosome[i][k]] == false) {

					flag[this.chromosome[i][k]] = true;
				} else {
					this.chromosome[i][k] = st.pop();
				}
			}

			for (int k = 0; k < this.node; k++) {
				flag[k] = false;
			}
			for (int k = 0; k < this.node; k++) {
				flag[this.chromosome[i + 1][k]] = true;
			}
			for (int k = 0; k < this.node; k++) {
				if (flag[k] == false) {
					st.push(k);
				}
			}
			for (int k = 0; k < this.node; k++) {
				flag[k] = false;
			}
			for (int k = 0; k < this.node; k++) {
				if (flag[this.chromosome[i][k]] == false) {
					flag[this.chromosome[i][k]] = true;
				} else {
					this.chromosome[i + 1][k] = st.pop();
				}
			}

		}
	}

	private void DuplicationCalculation() {
		// TODO Auto-generated method stub
		// step 2: duplication calculation
		//Russian roulette method
		this.Dfitness_individual = new double[this.pop_size];
		double Dsum_fitness = 0;
		for (int i = 0; i < this.pop_size; i++) {
			this.Dfitness_individual[i] = fitnessfunction(this.chromosome[i]);
			Dsum_fitness += this.Dfitness_individual[i];

		}
		int[][] mcode = new int[this.pop_size][this.node];
		for (int i = 0; i < this.pop_size; i++) {
			this.Dfitnesspercent_individual[i] = this.Dfitness_individual[i] / (1.0 * Dsum_fitness);
			mcode[i] = this.chromosome[i];
		}

		for (int i = 0; i < this.pop_size; i++) {
			double select = ran.nextInt(100) / 100.0;
			int j = -1;
			while (select > 0) {
				j++;
				select -= this.Dfitnesspercent_individual[j];
			}
			if (j == -1)
				j = 0;
			this.chromosome[i] = mcode[j];

		}
	}

	private void printGene() {
		// TODO Auto-generated method stub
		for (int i = 0; i < this.pop_size; i++) {
			for (int j = 0; j < this.node; j++)
				System.out.print(this.chromosome[i][j] + "-");
			System.out.println();
		}
		System.out.println();
	}

	private int[] permutation(int node2) {
		// TODO Auto-generated method stub
		Random r = new Random();
		int ri;
		boolean[] flag = new boolean[node2];
		int[] ans = new int[node2];
		for (int i = 0; i < node2; i++) {
			ri = r.nextInt(node2);
			while (flag[ri] == true) {
				ri = (ri + 1) % node2;
			}
			flag[ri] = true;
			ans[ri] = i;
		}

		return ans;
	}

	private double fitnessfunction(int[] individual_coding2) {
		// TODO Auto-generated method stub
		double sum = 0;
		for (int i = 0; i < this.node; i++) {
			sum += this.Graph[individual_coding2[i]][individual_coding2[(i + 1) % this.node]];
		}
		sum = this.Imidvalue - sum;
		return sum;
	}

}
