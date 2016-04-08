package advancedalgorithm;
/**
 * 
 */

/**
 * @author Taoheng
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
//		new GA(4,10).run();
//		new TSP(30,6,10).run();
		ACO aco=new ACO(30,10,100,1.f,5.f,0.5f);
		aco.init();
		aco.solve();
	}

}
