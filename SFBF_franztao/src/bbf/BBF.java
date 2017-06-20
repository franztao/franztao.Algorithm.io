package bbf;

import java.util.Vector;

import data.HashGenerationMatrice;
import data.InputData;
import data.TestResult;
import dbf.DBFBloomFilter;

public class BBF {
	public HashGenerationMatrice[] hashgenmatrice;
	public int k;
	// key's length
	public int R = 32;
	// key's width
	public int w = 32;
	public int n;
	public int m;

	// testinputdata's bitlength
	public int databitlength;

	// public Vector<DBFBloomFilter>DBFBloomFilterGroup;
	DBFBloomFilter DBFBloomFilterGroup;
	public int id;
	
	
	public BBF(int hashfunctionnum, int testunitnum, HashGenerationMatrice[] hashgenmatrice2) {
		this.id=testunitnum;
		this.hashgenmatrice=hashgenmatrice2;
		initparameter(hashfunctionnum);
		// BBFBloomFilterGroup=new Vector<DBFBloomFilter>();
		this.DBFBloomFilterGroup = new DBFBloomFilter(this.m);
		// DBFBloomFilterGroup.add(bf);
	}

	private void initparameter(int hashfunctionnum) {
		// TODO Auto-generated method stub
		this.k = hashfunctionnum;

		this.n = 64;
		this.m = 1024;

		this.databitlength = 32;
	}

	public void InsertionandExtensionOperation(InputData a) {

			hashkey(a, DBFBloomFilterGroup.bloomfiltervalue);
	}
	public boolean query(InputData a) {
		int lmax = 10;
		int bytenum;
		int bitnum;
		int valueloc;
		int mid = 0;

		int midl = 10;
		int l;
		for (l = 0; l < this.k; l++) {
			valueloc = 0;
			for (int ij = 0; ij < lmax; ij++) {
				mid = 0;
				int bit = a.inputdata_int&(hashgenmatrice[l].row[ij].colbit_int);
				mid=CountOne(bit);
				if ((mid&1) != 0)
					valueloc = valueloc | (1 << (midl - ij - 1));
			}
			// bytenum=valueloc/8;
			// bitnum=valueloc%8;
			// midaddr[l] = valueloc;
			mid = valueloc;
			bytenum = mid >>> 3;
			bitnum = mid % 8;
			if ((DBFBloomFilterGroup.bloomfiltervalue[bytenum] & (1 << bitnum)) == 0) {
				break;
			}
		}
		
		if (l == this.k)
			return true;
		else
			return false;

	}
	private void hashkey(InputData a, byte[] bloomfiltervalue) {
		// TODO Auto-generated method stub
		int lmax = 10;
		int bytenum;
		int bitnum;
		int valueloc;
		int mid;
		for (int l = 0; l < this.k; l++) {
			valueloc = 0;
			for (int i = 0; i < lmax; i++) {
				mid = 0;
				int bit = a.inputdata_int&(hashgenmatrice[l].row[i].colbit_int);
				mid=CountOne(bit);
				if ((mid&1) != 0)
					valueloc = valueloc | (1 << (lmax - i - 1));
			}
			bytenum = valueloc >>> 3;
			bitnum = valueloc % 8;
			byte midbyte = (byte) (1 << bitnum);
			bloomfiltervalue[bytenum] = (byte) ((bloomfiltervalue[bytenum]) | midbyte);//
		}
	}

	
	private int CountOne(int n)
	{
	    //0xAAAAAAAA，0x55555555分别是以“1位”为单位提取奇偶位
	    n = ((n & 0xAAAAAAAA) >>> 1) + (n & 0x55555555);
	    //0xCCCCCCCC，0x33333333分别是以“2位”为单位提取奇偶位
	    n = ((n & 0xCCCCCCCC) >> 2) + (n & 0x33333333);
	    //0xF0F0F0F0，0x0F0F0F0F分别是以“4位”为单位提取奇偶位
	    n = ((n & 0xF0F0F0F0) >>> 4) + (n & 0x0F0F0F0F);
	    //0xFF00FF00，0x00FF00FF分别是以“8位”为单位提取奇偶位
	    n = ((n & 0xFF00FF00) >>> 8) + (n & 0x00FF00FF);
	    //0xFFFF0000，0x0000FFFF分别是以“16位”为单位提取奇偶位
	    n = ((n & 0xFFFF0000) >>> 16) + (n & 0x0000FFFF);

	    return n;
	}
}
