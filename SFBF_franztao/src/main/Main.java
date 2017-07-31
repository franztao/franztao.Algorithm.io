package main;


import java.io.IOException;

import data.IPCache;
import data.VirtualData;
import data.WebCache;
import test.Test;

public class Main {

	public static void main(String args[]) throws IOException {
		String rootstr="C:\\Users\\Taoheng\\Desktop\\New folder\\";
		int datasize=1000000;
		int testtao;
//		int testunitnum = 5;
//		// public int testunitnum = 6;
//		int lamdalenght = 1000;
//		boolean QueryTimewithshifting = true;
//		int hashfunctionnum = 6;// k
		
//		int test1_indatalength = 30000;
//		int test1_searchdatalength = 150000;// 00;// 100000;
//		int test1_indatalength_step = 1200;
//		parameters para=new parameters(-1,testunitnum,lamdalenght,QueryTimewithshifting,hashfunctionnum,test1_indatalength,test1_searchdatalength,test1_indatalength_step);
		
//		new VirtualData(rootstr+"test1_sfbf5.txt",datasize).getVirtualDataFile();
		
		
//		new IPCache("F:\\workspace\\SFBF_franztao\\src\\IP1.txt","F:\\workspace\\SFBF_franztao\\src\\test2_actualdata_IP_1.txt").dealipcachefile();	
//		new WebCache("F:\\workspace\\SFBF_franztao\\src\\testdata\\out\\test2\\replace0\\test2_actualdata_webcache_2\\web21.txt","F:\\workspace\\SFBF_franztao\\src\\testdata\\out\\test2\\replace0\\test2_actualdata_webcache_2\\test2_actualdata_webcache_21.txt").dealwebcachefile();
//		new WebCache("F:\\workspace\\SFBF_franztao\\src\\testdata\\out\\test2_actualdata_webcache_2\\web21.txt","F:\\workspace\\SFBF_franztao\\src\\testdata\\out\\test2_actualdata_webcache_2\\test2_actualdata_webcache_21.txt").dealwebcachefile();	

		new Test().test1();
//		new Test().test3();
//		for(int i=0;i>=0;i++){
//			if(i%10000==0)
//			System.out.println(System.currentTimeMillis());
//		}
	}



}
