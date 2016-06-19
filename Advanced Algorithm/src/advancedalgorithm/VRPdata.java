package advancedalgorithm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class VRPdata {
	class point {
		int axis_x, axis_y;// 0-1000000;

		// depot
		Vector<Integer> haveclient = new Vector<Integer>();

		// client
		int demand4customer;
		int whichdepot;
	}

	Vector<point> depot;
	Vector<point> customer;
	int num_depot;
	int num_truck;
	int capacity4truck;
	int num_customer;
	int demand4customer;
	int range = 1000000;

	public VRPdata() throws IOException {
		// TODO Auto-generated constructor stub
		num_depot = 200;
		num_customer = 1000;
		num_truck = 10000;
		capacity4truck = 100000000;
		depot = new Vector<point>();
		customer = new Vector<point>();
		getdata();
		preprocessdata();
	}

	private void preprocessdata() {
		// TODO Auto-generated method stub
		int dis = 0, di;
		int loc = 0;
		for (int i = 0; i < this.num_customer; i++) {
			for (int j = 0; j < this.num_depot; j++) {
				if (j == 0) {
					loc = j;
					dis = distance(depot.get(j), customer.get(i));
				} else {
					di = distance(depot.get(j), customer.get(i));
					if (di < dis) {
						loc = j;
						dis = di;
					}
				}
			}
			point p = customer.get(i);
			p.whichdepot = loc;
			customer.set(i, p);

			p = depot.get(loc);
			p.haveclient.addElement(i + 1);// 1,2,3,4
			depot.set(loc, p);
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

	private void getdata() throws IOException {
		// TODO Auto-generated method stub
		String abusolutepath = "C:\\Users\\Taoheng\\Desktop\\VRP\\";
		String str = "realdata.txt";
		FileReader fin = new FileReader(abusolutepath + str);
		BufferedReader bf = new BufferedReader(fin);

		String line;
		int x, y, mid, d;

		line = bf.readLine();
		for (int i = 0; i < this.num_depot; i++) {
			point p = new point();
			mid = Integer.parseInt(line);
			x = deal(mid);
			line = bf.readLine();

			mid = Integer.parseInt(line);
			y = deal(mid);
			line = bf.readLine();

			p.axis_x = x;
			p.axis_y = y;
			depot.add(p);
		}

		for (int i = 0; i < this.num_customer; i++) {
			point p = new point();
			mid = Integer.parseInt(line);
			x = deal(mid);
			line = bf.readLine();

			mid = Integer.parseInt(line);
			y = deal(mid);
			line = bf.readLine();

			mid = Integer.parseInt(line);
			d = deal(mid);
			line = bf.readLine();

			p.axis_x = x;
			p.axis_y = y;

			p.demand4customer = d;
			customer.add(p);
		}
	}

	private int deal(int mid) {
		// TODO Auto-generated method stub
		if (mid < 0)
			mid = mid * -1;
		mid %= this.range;
		return mid;
	}

}
