package advancedalgorithm;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import advancedalgorithm.VRPdata.point;

public class VRP_GA {

	VRPdata vrpdata;
	int iteration = 100;
	int popsize = 20;
	int genesize;

	class gene {
		Vector<Integer> representation = new Vector<Integer>();
		Vector<Integer> star = new Vector<Integer>();
		double fitness;
	}

	Vector<gene> population;

	public VRP_GA() throws IOException {
		// TODO Auto-generated constructor stub
		vrpdata = new VRPdata();
		population = new Vector<gene>();
	}

	public void begin() {
		for (int i = 0; i < this.vrpdata.num_depot; i++) {
			genesize = this.vrpdata.depot.get(i).haveclient.size();
			population.clear();

			if (genesize > 1) {
				initpopulation(i);
				for (int iter = iteration; iter >= 0; iter--) {
					fitness(i);
					reproduction();
					cross();
					mutation();
				}

			}
			if (genesize > 0) {
				System.out.println("µÚ" + i + "¸ö³µÕ¾£º ");
				if (genesize > 1) {
					for (int j = 1; j < population.get(0).representation.size(); j++) {
						if (population.get(0).representation.get(j) == 0) {
							System.out.print("|||");
						} else {
							System.out.print(population.get(0).representation.get(j) + " ");
						}
					}
				}
				if (genesize == 1) {
					System.out.print(this.vrpdata.depot.get(i).haveclient.get(0));
				}
				System.out.println();
			}

		}
	}

	private void mutation() {
		// TODO Auto-generated method stub
		Vector<Integer> vec = new Vector<Integer>();
		for (int popsizei = 0; popsizei < popsize; popsizei++) {
			gene g = population.get(popsizei);
			Random ran = new Random();
			double prob = ran.nextDouble();
			if (prob > g.fitness)
				continue;
			for (int j = 0; j < g.representation.size(); j++) {
				if (g.representation.get(j) != 0) {
					vec.addElement(g.representation.get(j));
				}
			}
			int i = ran.nextInt(vec.size());
			int j = ran.nextInt(vec.size());
			int mid = vec.get(i);
			vec.set(i, vec.get(j));
			vec.set(j, mid);

			gene newg = new gene();
			int r_capacity = this.vrpdata.capacity4truck;
			newg.representation.add(0);
			newg.star.add(0);

			for (int gi = 0; gi < genesize; gi++) {
				int clienti = vec.get(gi);

				int cap = this.vrpdata.customer.get(clienti - 1).demand4customer;
				if (r_capacity >= cap) {
					newg.representation.addElement(clienti);
					r_capacity -= cap;
				} else {
					newg.representation.add(0);
					newg.star.add(g.representation.size() - 1);
					r_capacity = this.vrpdata.capacity4truck;
					newg.representation.addElement(clienti);
					r_capacity -= cap;
				}

			}
			newg.representation.add(0);
			newg.star.add(g.representation.size() - 1);
			population.set(popsizei, newg);
		}

	}

	private void cross() {
		// TODO Auto-generated method stub
		Vector<Integer> father = new Vector<Integer>();
		Vector<Integer> mother = new Vector<Integer>();
		HashSet<Integer> fs = new HashSet<Integer>();
		HashSet<Integer> fm = new HashSet<Integer>();
		for (int popsizei = 0; popsizei < popsize; popsizei += 2) {
			gene gf = population.get(popsizei);
			gene gm = population.get(popsizei + 1);
			Random ran = new Random();
			double prob = ran.nextDouble();
			if (prob > gf.fitness)
				continue;
			for (int j = 0; j < gf.representation.size(); j++) {
				if (gf.representation.get(j) != 0) {
					father.addElement(gf.representation.get(j));
					fs.add(gf.representation.get(j));
					fm.add(gf.representation.get(j));

				}

				if (gm.representation.get(j) != 0) {
					mother.addElement(gm.representation.get(j));
				}
			}

			int i = ran.nextInt(father.size());
			int j = ran.nextInt(mother.size());
			Vector<Integer> midfather = new Vector<Integer>();
			Vector<Integer> midmother = new Vector<Integer>();
			int s;
			for (s = 0; s < i; s++) {
				midfather.addElement(father.get(s));
			}
			for (s = j; s < mother.size(); s++) {
				midfather.addElement(mother.get(s));
			}
			for (s = 0; s < j; s++) {
				midmother.addElement(mother.get(s));
			}
			for (s = i; s < father.size(); s++) {
				midmother.addElement(father.get(s));
			}

			father.clear();
			mother.clear();

			for (s = 0; s < midfather.size(); s++) {
				if (fs.contains(midfather.get(s))) {
					father.add(midfather.get(s));
					fs.remove(midfather.get(s));
				}
			}
			Iterator<Integer> f = fs.iterator();
			while (f.hasNext()) {
				father.add(f.next());
			}

			for (s = 0; s < midmother.size(); s++) {
				if (fs.contains(midmother.get(s))) {
					mother.add(midmother.get(s));
					fm.remove(midmother.get(s));
				}
			}
			Iterator<Integer> m = fm.iterator();
			while (m.hasNext()) {
				father.add(m.next());
			}

			gene newg = new gene();
			int r_capacity = this.vrpdata.capacity4truck;
			newg.representation.add(0);
			newg.star.add(0);

			for (int gi = 0; gi < genesize; gi++) {
				int clienti = father.get(gi);

				int cap = this.vrpdata.customer.get(clienti - 1).demand4customer;
				if (r_capacity >= cap) {
					newg.representation.addElement(clienti);
					r_capacity -= cap;
				} else {
					newg.representation.add(0);
					newg.star.add(gf.representation.size() - 1);
					r_capacity = this.vrpdata.capacity4truck;
					newg.representation.addElement(clienti);
					r_capacity -= cap;
				}

			}
			newg.representation.add(0);
			newg.star.add(gf.representation.size() - 1);
			population.set(popsizei, newg);
		}
	}

	private void reproduction() {
		// TODO Auto-generated method stub
		Random ran = new Random();
		double probability;// =ran.nextDouble();
		Vector<gene> Reproducepop = new Vector<gene>();
		for (int popsizei = 0; popsizei < popsize; popsizei++) {
			probability = ran.nextDouble();
			int i = 0;
			while ((i < population.size()) && (probability > population.get(i).fitness)) {
				// System.out.println(probability);
				probability -= population.get(i).fitness;
				i++;
			}

			if (i == population.size()) {
				i--;
			}
			Reproducepop.addElement(population.get(i));
		}
		population = Reproducepop;

	}

	private void fitness(int depoti) {
		// TODO Auto-generated method stub
		double sum = 0;
		for (int popsizei = 0; popsizei < popsize; popsizei++) {
			gene g = population.get(popsizei);
			int dis = 0;
			int last = 0;
			point first = null;
			point second = null;
			first = this.vrpdata.depot.get(depoti);
			for (int i = 1; i < g.representation.size(); i++) {

				if (g.representation.get(i) == 0) {
					second = this.vrpdata.depot.get(depoti);
				} else {
					second = this.vrpdata.customer.get(g.representation.get(i) - 1);
				}
				dis += distance(first, second);

				if (i == (g.representation.size() - 1)) {

					last += distance(first, second);
				}

				first = second;
			}
			if (last != 0)
				g.fitness = last + dis % last;
			else {
				g.fitness = last + dis;
			}
			sum += g.fitness;
			population.set(popsizei, g);
		}
		for (int popsizei = 0; popsizei < popsize; popsizei++) {
			gene g = population.get(popsizei);
			g.fitness = g.fitness / sum;
			population.set(popsizei, g);
		}

	}

	private int distance(point point, point point2) {
		// TODO Auto-generated method stub
		double res;
		// System.out.println(point.axis_x+" "+point.axis_y+" "+point2.axis_x+"
		// "+point2.axis_y);
		res = (point.axis_x - point2.axis_x) * (point.axis_x - point2.axis_x)
				+ ((point.axis_y - point2.axis_y) * (point.axis_y - point2.axis_y));
		res = Math.sqrt(res);

		int i = (int) res;
		return i;
	}

	private void initpopulation(int depoti) {
		// TODO Auto-generated method stub

		for (int popsizei = 0; popsizei < popsize; popsizei++) {

			gene g = new gene();
			int r_capacity = this.vrpdata.capacity4truck;
			g.representation.add(0);
			g.star.add(0);

			for (int i = 0; i < genesize; i++) {
				int clienti = this.vrpdata.depot.get(depoti).haveclient.get(i);

				int cap = this.vrpdata.customer.get(clienti - 1).demand4customer;
				if (r_capacity >= cap) {
					g.representation.addElement(clienti);
					r_capacity -= cap;
				} else {
					g.representation.add(0);
					g.star.add(g.representation.size() - 1);
					r_capacity = this.vrpdata.capacity4truck;
					g.representation.addElement(clienti);
					r_capacity -= cap;
				}

			}
			g.representation.add(0);
			g.star.add(g.representation.size() - 1);
			population.add(g);
			int inte = this.vrpdata.depot.get(depoti).haveclient.get(0);
			this.vrpdata.depot.get(depoti).haveclient.remove(0);
			this.vrpdata.depot.get(depoti).haveclient.addElement(inte);
		}

	}

}
