package main;

public class parameters {

	public int cachesize = -1;

	public int testunitnum;

	public int lamdalenght;

	public boolean QueryTimewithshifting = true;
	public int hashfunctionnum;// k
	public int test1_indatalength;
	public int test1_searchdatalength;// 00;// 100000;
	public int test1_indatalength_step;

	public parameters(int cs, int tun, int ll, boolean qts, int hdn, int t1i, int t1s, int t1is) {
		// TODO Auto-generated constructor stub
		this.cachesize = cs;
		this.testunitnum = tun;
		this.lamdalenght = ll;
		this.QueryTimewithshifting = qts;
		this.hashfunctionnum = hdn;
		this.test1_indatalength = t1i;
		this.test1_searchdatalength = t1s;
		this.test1_indatalength_step = t1is;
	}

}
