/**
 * 
 */
package advancedalgorithm;

/**
 * @author from http://blog.csdn.net/com_stu_zhang/article/details/7534651
 *matlab code: linprog(f,A,b,Aeq,beq,LB,UB)
 */
public class Simplex {
	private static final double EPSILON = 1.0E-1;
	private double[][] a;// tableaux
	private int M;// number of constraints
	private int N; // number of original variables
	private int[] basis;//

	/**
	 * 
	 */
	public Simplex(double[][] A, double[] b, double[] c) {
		// TODO Auto-generated constructor stub
		M = b.length;
		N = c.length;
		a = new double[M + 1][N + M + 1];
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				a[i][j] = A[i][j];
			}
		}
		for (int i = 0; i < M; i++)
			a[i][N + i] = 1.0;
		for (int j = 0; j < N; j++)
			a[M][j] = c[j];
		for (int i = 0; i < M; i++)
			a[i][M + N] = b[i];
		basis = new int[M];
		for (int i = 0; i < M; i++)
			basis[i] = N + i;

		solve();

		assert check(A, b, c);

	}

	private boolean check(double[][] A, double[] b, double[] c) {
		// TODO Auto-generated method stub
		return isPrimalFeaible(A, b) && isDualFeasible(A, c) && isOptimal(b, c);
	}

	private boolean isOptimal(double[] b, double[] c) {
		// TODO Auto-generated method stub
		double[] x = primal();
		double[] y = dual();
		double value = value();
		double v1 = 0.0;
		for (int j = 0; j < x.length; j++)
			v1 += c[j] * x[j];
		double v2 = 0.0;
		for (int i = 0; i < y.length; i++)
			v2 += y[i] * b[i];
		if (Math.abs(value - v1) > this.EPSILON || Math.abs(value - v2) > this.EPSILON) {
			System.out.println("value = " + value + ", cx = " + v1 + ", yb = " + v2);
			return false;
		}
		return true;
	}

	private double value() {
		// TODO Auto-generated method stub
		return -a[M][M + N];
	}

	// ?
	private double[] primal() {
		// TODO Auto-generated method stub
		double x[] = new double[N];
		for (int i = 0; i < M; i++)
			if (basis[i] < N)
				x[basis[i]] = a[i][M + N];
		return x;
	}

	private boolean isPrimalFeaible(double[][] A, double[] b) {
		// TODO Auto-generated method stub
		double[] x = primal();
		// check that x>=0
		for (int j = 0; j < x.length; j++) {
			if (x[j] < 0.0) {
				System.out.println("x[" + j + "]=" + x[j] + "is negative");
				return false;
			}
		}
		// check taht Ax<=b
		for (int i = 0; i < M; i++) {
			double sum = 0.0;
			for (int j = 0; j < N; j++) {
				sum += A[i][j] * x[j];
			}
			if (sum > b[i] + EPSILON) {
				System.out.println("not primal feasible:");
				System.out.println("b[" + i + "] = " + b[i] + ", sum = " + sum);
				return false;
			}
		}
		return true;

	}

	private boolean isDualFeasible(double[][] A, double[] c) {
		// TODO Auto-generated method stub
		double[] y = dual();
		for (int i = 0; i < y.length; i++) {
			if (y[i] < 0.0) {
				System.out.println("y[" + i + "]=" + y[i] + "is negative");
				return false;
			}
		}
		for (int j = 0; j < N; j++) {
			double sum = 0.0;
			for (int i = 0; i < M; i++) {
				sum += A[i][j] * y[i];
			}
			if (sum < c[j] - this.EPSILON) {
				System.out.println("not dual feasible:");
				System.out.println("c[" + j + "] = " + c[j] + ", sum = " + sum);
				return false;
			}
		}
		return true;
	}

	private double[] dual() {
		// TODO Auto-generated method stub
		double[] y = new double[M];
		for (int i = 0; i < M; i++)
			y[i] = -a[M][N + i];
		return y;
	}

	private void solve() {
		// TODO Auto-generated method stub
		while (true) {
			// find entering column q
			int q = bland();
			if (-1 == q)
				break;
			// find leaving row p
			int p = minRatioRule(q);
			if (-1 == p)
				throw new RuntimeException("Linear program is nbounded");
			// pivot
			pivot(p, q);
			basis[p] = q;
		}
	}
	//pivote on entry(p,q)using Gauss-Jordan elimination
	private void pivot(int p, int q) {
		// TODO Auto-generated method stub
		//everything but row p and colum q
		for (int i = 0; i <= M; i++)
			for (int j = 0; j <= M + N; j++)
				if (i != p && j != q)
					a[i][j] -= (a[p][j] * a[i][q] / a[p][q]);
		//zero out column q
		for(int i=0;i<=M;i++){
			if(i!=p)a[i][q]=0.0;
		}
		//scale row p
		for(int j=0;j<=M+N;j++)
			if(j!=q)a[p][j]/=a[p][q];
		a[p][q]=1.0;

	}

	// find row p using min ration rule(-1 if no such row)
	private int minRatioRule(int q) {
		// TODO Auto-generated method stub
		int p = -1;
		for (int i = 0; i < M; i++) {
			if (a[i][q] <= 0)
				continue;
			else if (-1 == p)
				p = i;
			else if ((a[i][M + N] / a[i][q]) < (a[p][M + N] / a[p][q]))
				p = i;
		}
		return p;
	}

	private int bland() {
		// TODO Auto-generated method stub
		for (int j = 0; j < M + N; j++)
			if (a[M][j] > 0)
				return j;
		return -1;
	}

}
