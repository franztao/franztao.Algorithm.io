package main;


import java.io.IOException;

import data.IPCache;
import data.VirtualData;
import data.WebCache;
import test.Test;

public class Main {


	public static void main(String args[]) throws IOException {

//		new VirtualData("F:\\workspace\\SFBF_franztao\\src\\testdata\\in\\test1_sfbf5.txt").dealwebcachefile();
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
