package doublebf;

import a2bf.A2BFBloomFilter;
import data.HashGenerationMatrice;
import data.InputData;

public class DoubleBF {
	private HashGenerationMatrice[] hashgenmatrice;
	public int k;
	// key's length
	public int R = 32;
	// key's width
	public int w = 32;
	public int n;
	public int m;
	public int lmax;
	public int active;

	public int[] activesize = new int[2];
	A2BFBloomFilter A2BFBloomFilterGroup;
	
	public int reset_num;
	public int hit_num;
	public int hit_sum;
	
	public DoubleBF(int hashfunctionnum, HashGenerationMatrice[] hashgenmatrice2, int cachesize) {
		this.hashgenmatrice = hashgenmatrice2;
		this.m = cachesize;
		this.k=hashfunctionnum;
		this.lmax = (int) (Math.ceil(Math.log(cachesize / 2.0) / Math.log(2.0)));
		this.active = 0;
		this.activesize[0] = this.activesize[1] = 0;
		this.n = cachesize >> 5;//>>1 两个bloom过滤器  >>4  1024 :  64
		this.A2BFBloomFilterGroup = new A2BFBloomFilter(this.m);
		this.reset_num=0;
		this.hit_num=0;
		this.hit_sum=0;
		this.flush(A2BFBloomFilterGroup.activebloomfiltervalue[0]);
		this.flush(A2BFBloomFilterGroup.activebloomfiltervalue[1]);
	}

	public boolean inserteverykey(InputData a) {
		boolean result = false;
		if (query(a, A2BFBloomFilterGroup.activebloomfiltervalue[active])){
			if((this.activesize[active]<<1)>this.n){
				insert(a, A2BFBloomFilterGroup.activebloomfiltervalue[(active+1)%2]);
				activesize[(active+1)%2]++;
			}
			result=true;
		}
		else{
			result=false;
			insert(a, A2BFBloomFilterGroup.activebloomfiltervalue[active]);
			activesize[active]++;
			if((this.activesize[active]<<1)>this.n){
				insert(a, A2BFBloomFilterGroup.activebloomfiltervalue[(active+1)%2]);
				activesize[(active+1)%2]++;
			}
		}
		if(this.activesize[active]>this.n){
			flush(A2BFBloomFilterGroup.activebloomfiltervalue[(active + 1) % 2]);
			activesize[(active+1)%2]=0;
			active = (active + 1) % 2;
			this.reset_num++;
		}
		
		return result;
		
	}

	private void flush(byte[] bs) {
		// TODO Auto-generated method stub
		for (int i = 0; i < bs.length; i++) {
			bs[i] = 0;
		}
	}

	private void insert(InputData a, byte[] active1bloomfiltervalue) {
		// TODO Auto-generated method stub
		int bytenum;
		int bitnum;
		int valueloc;
		int mid;
		for (int l = 0; l < this.k; l++) {
			valueloc = 0;
			for (int i = 0; i < this.lmax; i++) {
				mid = 0;
				for (int j = 0; j < a.inputdatabitlength; j++) {
					bytenum = j >> 3;
					bitnum = j % 8;
//					int bit = ((a.inputdata_byte[bytenum] & (1 << bitnum))
//							& (hashgenmatrice[l].row[i].colbit[bytenum] & (1 << bitnum)));
//					if (bit != 0)
						mid ^= 1;
				}
				if (mid != 0)
					valueloc = valueloc | (1 << (lmax - i - 1));
			}
			bytenum = valueloc >> 3;
			bitnum = valueloc % 8;
			byte midbyte = (byte) (1 << bitnum);
			active1bloomfiltervalue[bytenum] = (byte) ((active1bloomfiltervalue[bytenum]) | midbyte);//
		}
	}

	private boolean query(InputData a, byte[] active1bloomfiltervalue) {
		// TODO Auto-generated method stub
		int bytenum;
		int bitnum;
		int valueloc;
		int mid = 0;
		int l;
		int[] midaddr = new int[this.k];
		//System.out.println("aaaaaaaaaaapppppppppp"+this.lmax);
		for (l = 0; l < this.k; l++) {
			valueloc = 0;
			for (int ij = 0; ij < this.lmax; ij++) {
				mid = 0;
				for (int j = 0; j < a.inputdatabitlength; j++) {
					bytenum = j >> 3;
					bitnum = j % 8;
//					int bit = ((a.inputdata_byte[bytenum] & (1 << bitnum))
//							& (hashgenmatrice[l].row[ij].colbit[bytenum] & (1 << bitnum)));// bloomfiltervalue[bytenum]
//					if (bit != 0)
						mid ^= 1;
				}
				if (mid != 0)
					valueloc = valueloc | (1 << (this.lmax - ij - 1));
			}
			midaddr[l] = valueloc;
		}
		for (l = 0; l < this.k; l++) {
			mid = midaddr[l];
			bytenum = mid >> 3;
			bitnum = mid % 8;
			if ((active1bloomfiltervalue[bytenum] & (1 << bitnum)) == 0) {
				break;
			}
		}
		if (l == this.k)
			return true;
		else{
			//System.out.println("aaaaaaaaaaapppppppppp"+this.lmax);
			return false;
			
		}
	}
}
