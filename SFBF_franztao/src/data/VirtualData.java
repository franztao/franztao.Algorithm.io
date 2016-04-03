package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class VirtualData {
	public String dealedWebCachefilepath;
	private FileWriter cachefilewriter;
	public int notelength = 1030000 ;

	public VirtualData(String path2){
		this.dealedWebCachefilepath = path2;
	}
	
	public void dealwebcachefile() throws IOException{
		String midstr;
		midstr = this.dealedWebCachefilepath;
		cachefilewriter = new FileWriter(midstr);
		Random r=new Random();
		int data;
		for(int i=0;i<this.notelength;i++){
			data=r.nextInt();
			data=data>0?data:-data;
			data=(data%5)+1;
			cachefilewriter.write(data+"\r\n");
//			System.out.println(i);
		}
	}
}
