package geneticalgorithm;

import java.util.Random;

/**
 * 
 */

/**
 * @author Taoheng
 *
 */
public class GA {

	public int[] chromosome;
	// chromosome_size=16bit
	public int pop_size;
	public double[] Dfitness_individual;
	public double[] Dfitnesspercent_individual;

	Random ran;
	public int interative;
	
	public GA(int num,int iter) {
		this.pop_size = num;
		this.interative=iter;
	}

	public void run() {
		this.ran = new Random();
		IndividualCoding();
		printGene();
		for (; interative >= 0; interative--) {
			DuplicationCalculation();
			printGene();
			Crosscalculation();
			printGene();
			MutaionCalculation();
			printGene();
		}

	}

	private void MutaionCalculation() {
		// TODO Auto-generated method stub
		// step 4: mutation calculation
		//random flip one bit of the chromosome
		for (int i = 0; i < this.pop_size; i++) {
			int bit = 1 << (ran.nextInt(this.pop_size - 1));
			this.chromosome[i] = this.chromosome[i] ^ bit;
		}

	}

	private void Crosscalculation() {
		// TODO Auto-generated method stub
		// step 3: cross calculation
		//Partially Matching Crossover
		for (int i = 0; i < this.pop_size; i += 2) {
			int bit = (1 << ran.nextInt(this.pop_size - 1)) - 1;
			int first = this.chromosome[i] & bit;
			int second = this.chromosome[i + 1] & bit;
			this.chromosome[i] = (this.chromosome[i] & (~bit)) | second;
			this.chromosome[i + 1] = (this.chromosome[i + 1] & (~bit)) | first;
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
		int[] mcode = new int[this.pop_size];
		for (int i = 0; i < this.pop_size; i++) {
			this.Dfitnesspercent_individual[i] = this.Dfitness_individual[i] / Dsum_fitness;
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

	private void IndividualCoding() {
		// TODO Auto-generated method stub
		// step 1:produce initial group
		// individual coding:The generation of initial population
		//a 16 bits of interger represent a chromosome
		chromosome = new int[pop_size];
		Dfitnesspercent_individual = new double[this.pop_size];

		for (int i = 0; i < this.pop_size; i++) {
			this.chromosome[i] = ran.nextInt(16);
		}

	}

	private void printGene() {
		// TODO Auto-generated method stub
		for (int i = 0; i < this.pop_size; i++) {
			System.out.print(Integer.toBinaryString(this.chromosome[i]) + " ");
		}
		System.out.println();
	}

	// calculate fitness
	private double fitnessfunction(int i) {
		// TODO Auto-generated method stub
		double sum;
		sum = (i * 1.0) * Math.sin(i * Math.PI * 10) + 2.0;
		return sum;
	}

}
