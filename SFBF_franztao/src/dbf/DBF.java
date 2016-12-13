package dbf;

import java.util.Random;
import java.util.Vector;

import data.HashGenerationMatrice;
import data.InputData;

public class DBF {
	
	public int len_bloomfiltervectorlength=1000000;
	
	public HashGenerationMatrice[] hashgenmatrice;
	public int ii;
	public int k;
	// key's length
	public int R = 32;
	// key's width
	public int w = 32;
	public int n;
	public int m;
	// current datanum in this bloomfilter
	public int nl;
	// testinputdata's bitlength
	public int databitlength;

	public int id;
	// public TestResult testresult;

	public Vector<DBFBloomFilter> DBFBloomFilterGroup;

	public Vector<Long> timestamp;// =new Vector<Long>();//
	public Long timerecord;

	public int cachesize;
	public int curent_cachesize;

	public int insert_ii;

	public int reset_num;

	public int bloomfiltersumsize;

	public long timestart, timeend;

	public long eachaccessbloom;

	public int ExtensionRound;
	public int SpaceSizeOfFilter;

	public int bloomfilterlamdarange;

	public int[] pow2num;
//	public Vector<Integer> bloomfiltervectorlength;
	public int[] bloomfiltervectorlength;
	public boolean boolean4test3;

	public int replacestrategy;
	public int maxbloomfilterlength;
	public int loc_maxbloomfilterlength;

	public DBF(int hashfunctionnum, int testunitnum, HashGenerationMatrice[] hashgenmatrice2, int cachesize, int range,
			int replacestrategy) {
		this.cachesize = cachesize;

		this.id = testunitnum;
		this.bloomfilterlamdarange = range;

		// this.testresult = testresult2;
		this.hashgenmatrice = hashgenmatrice2;
		initparameter(hashfunctionnum);
		DBFBloomFilterGroup = new Vector<DBFBloomFilter>();
		DBFBloomFilter bf = new DBFBloomFilter(this.m);
		DBFBloomFilterGroup.add(bf);

		timestamp = new Vector<Long>();
		this.timerecord = Long.MIN_VALUE;
		// timestamp.add(System.currentTimeMillis());
		timestamp.add(this.timerecord);
		this.timerecord++;
		bloomfiltervectorlength =new int [len_bloomfiltervectorlength];// new Vector<Integer>();
		bloomfiltervectorlength[0]=1;

		this.replacestrategy = replacestrategy;
	}

	private void initparameter(int hashfunctionnum) {
		// TODO Auto-generated method stub
		ii = 0;
		this.k = hashfunctionnum;

		this.n = 64;
		this.m = 1024;
		this.nl = 0;
		this.databitlength = 32;

		this.curent_cachesize = this.m;
		this.bloomfiltersumsize = this.curent_cachesize;

		this.insert_ii = 0;
		this.reset_num = 0;

		this.eachaccessbloom = 0;

		pow2num = new int[this.bloomfilterlamdarange + 1];
		pow2num[0] = 0;
		int acc = 1;
		for (int pi = 1; pi < this.bloomfilterlamdarange + 1; pi++) {
			pow2num[pi] = acc;
			acc *= 2;
		}

		this.boolean4test3 = true;

		this.maxbloomfilterlength = 1024;
		this.loc_maxbloomfilterlength = 0;
	}

	public boolean inserteverykey(InputData a) {
		boolean result;

		timestart = System.currentTimeMillis();
		result = query_insert(a);
		timeend = System.currentTimeMillis();

		return result;

	}

	private boolean query_insert(InputData a) {
		// TODO Auto-generated method stub
		int bytenum;
		int bitnum;
		int valueloc;
		int mid = 0;
		int dbfbloomfilterbitlen = 9 + this.bloomfilterlamdarange;
		int hashfun_i;
		int patch;
		int[][] currentdatahashvalue = new int[this.k][bloomfilterlamdarange];
		for (hashfun_i = 0; hashfun_i < this.k; hashfun_i++) {
			valueloc = 0;
			patch = 0;
			for (int ij = 0; ij < dbfbloomfilterbitlen; ij++) {
				mid = 0;
				int bit = a.inputdata_int & (hashgenmatrice[hashfun_i].row[ij].colbit_int);
				mid = CountOne(bit);
				if ((mid & 1) != 0)
					valueloc = valueloc | (1 << (9 - ij + patch));
				if (ij >= 9) {
					patch++;
					currentdatahashvalue[hashfun_i][ij - 9] = valueloc;
					valueloc <<= 1;
				}
			}
		}
		for (int i = this.ii; i >= 0; i--) {
			for (hashfun_i = 0; hashfun_i < this.k; hashfun_i++) {
				mid = currentdatahashvalue[hashfun_i][this.bloomfiltervectorlength[i] - 1];//[this.bloomfiltervectorlength.get(i) - 1];
				bytenum = mid >>> 3;
				bitnum = mid % 8;
				if ((DBFBloomFilterGroup.get(i).bloomfiltervalue[bytenum] & (1 << bitnum)) == 0) {
					break;
				}
			}
			if (hashfun_i == this.k) {
				this.eachaccessbloom += (this.ii - i + 1);
				// timestamp.set(i, System.currentTimeMillis());
				timestamp.set(i, this.timerecord);
				this.timerecord++;
				return true;
			}
		}
		this.eachaccessbloom += this.ii;

		// hashkey(a, DBFBloomFilterGroup.get(this.insert_ii).bloomfiltervalue);
		for (hashfun_i = 0; hashfun_i < this.k; hashfun_i++) {
			inserthashintvalue2bloomfilter(
					currentdatahashvalue[hashfun_i][this.bloomfiltervectorlength[this.insert_ii] - 1],
					DBFBloomFilterGroup.get(this.insert_ii).bloomfiltervalue);
		}
		// timestamp.set(this.insert_ii, System.currentTimeMillis());
		timestamp.set(this.insert_ii, this.timerecord);
		this.timerecord++;
		this.nl++;

		if (this.nl == (this.n * this.pow2num[this.bloomfiltervectorlength[this.insert_ii]])) {
			if (this.curent_cachesize < this.cachesize) {
				if ((this.m + this.curent_cachesize) <= this.cachesize) {

					Random r = new Random();
					int midindex = r.nextInt();
					midindex = midindex > 0 ? midindex : -midindex;
					midindex = (midindex % this.bloomfilterlamdarange) + 1;

					int midsize = this.curent_cachesize;

					this.curent_cachesize = this.m * this.pow2num[midindex] + midsize;
					while (this.curent_cachesize > this.cachesize) {
						midindex--;
						this.curent_cachesize = this.m * this.pow2num[midindex] + midsize;
					}

//					bloomfiltervectorlength.add(midindex);
					this.nl = 0;
					ii++;
					bloomfiltervectorlength[ii]=midindex;
					this.insert_ii = ii;
					DBFBloomFilter bf = new DBFBloomFilter(this.m * this.pow2num[midindex]);
					DBFBloomFilterGroup.add(bf);
					if (this.maxbloomfilterlength < this.m * this.pow2num[midindex]) {
						this.maxbloomfilterlength = this.m * this.pow2num[midindex];
						this.loc_maxbloomfilterlength = this.insert_ii;
					}

					// this.timestamp.add(System.currentTimeMillis());
					timestamp.add(this.timerecord);
					this.timerecord++;
					this.bloomfiltersumsize = this.curent_cachesize;
				} else {
					this.bloomfiltersumsize = this.curent_cachesize;
					this.curent_cachesize = this.cachesize;
				}

			} else {
				dbf_replacestrategy();
			}
		}

		return false;
	}

	private void dbf_replacestrategy() {
		// TODO Auto-generated method stub

		int loc = 0;
		if (this.replacestrategy == 0) {
			long mintimestamp = timestamp.get(0);

			for (int i = 1; i <= this.ii; i++) {
				if (this.timestamp.get(i) < mintimestamp) {
					mintimestamp = this.timestamp.get(i);
					loc = i;
				}
			}
		}

		if (this.replacestrategy == 1) {
			Random r = new Random();
			int data = r.nextInt();
			data = data > 0 ? data : -data;
			data = data % (this.ii + 1);
			loc = data;
		}

		if (this.replacestrategy == 2) {
			loc = this.loc_maxbloomfilterlength;
			long mintimestamp = timestamp.get(loc);
			for (int i = 0; i <= this.ii; i++) {
				if (this.bloomfiltervectorlength[i] == this.bloomfiltervectorlength[loc]) {
					if (this.timestamp.get(i) < mintimestamp) {
						mintimestamp = this.timestamp.get(i);
						loc = i;
					}
				}

			}
		}
		if (this.replacestrategy == 3) {
			loc = 0;
			long mintimestamp = timestamp.get(loc);
			for (int i = 0; i <= this.ii; i++) {
				if (this.bloomfiltervectorlength[i] == this.bloomfiltervectorlength[loc]) {
					if (this.timestamp.get(i) < mintimestamp) {
						mintimestamp = this.timestamp.get(i);
						loc = i;
					}
				}

			}
		}

		this.insert_ii = loc;
		this.nl = 0;
		DBFBloomFilterGroup.get(loc).flush();
		this.timestamp.set(loc, this.timerecord);
		this.timerecord++;

		if (this.boolean4test3 == true)
			this.reset_num++;
	}

	public boolean query(InputData a) {
		int bytenum;
		int bitnum;
		int valueloc;
		int mid = 0;
		int midl = 9 + this.bloomfilterlamdarange;
		int l;
		int[][] midaddr = new int[this.k][bloomfilterlamdarange];
		int patch;
		for (l = 0; l < this.k; l++) {
			patch = 0;
			valueloc = 0;
			for (int ij = 0; ij < midl; ij++) {
				mid = 0;
				int bit = (a.inputdata_int) & (hashgenmatrice[l].row[ij].colbit_int);
				mid = CountOne(bit);
				if ((mid & 1) != 0)
					valueloc = valueloc | (1 << (9 - ij + patch));
				if (ij >= 9) {
					patch++;
					midaddr[l][ij - 9] = valueloc;
					valueloc <<= 1;
				}

			}
		}
		for (int i = this.ii; i >= 0; i--) {
			for (l = 0; l < this.k; l++) {
				mid = midaddr[l][this.bloomfiltervectorlength[i] - 1];
				bytenum = mid >>> 3;
				bitnum = mid % 8;//
				if ((DBFBloomFilterGroup.get(i).bloomfiltervalue[bytenum] & (1 << bitnum)) == 0) {
					break;
				}
			}
			if (l == this.k) {
				this.eachaccessbloom += (this.ii - i + 1);
				// timestamp.set(i, System.currentTimeMillis());
				timestamp.set(i, this.timerecord);
				this.timerecord++;
				return true;
			}
		}
		this.eachaccessbloom += this.ii;
		return false;

	}

	public int CountOne(int n) {
		// 0xAAAAAAAA，0x55555555分别是以“1位”为单位提取奇偶位
		n = ((n & 0xAAAAAAAA) >>> 1) + (n & 0x55555555);
		// 0xCCCCCCCC，0x33333333分别是以“2位”为单位提取奇偶位
		n = ((n & 0xCCCCCCCC) >>> 2) + (n & 0x33333333);
		// 0xF0F0F0F0，0x0F0F0F0F分别是以“4位”为单位提取奇偶位
		n = ((n & 0xF0F0F0F0) >>> 4) + (n & 0x0F0F0F0F);
		// 0xFF00FF00，0x00FF00FF分别是以“8位”为单位提取奇偶位
		n = ((n & 0xFF00FF00) >>> 8) + (n & 0x00FF00FF);
		// 0xFFFF0000，0x0000FFFF分别是以“16位”为单位提取奇偶位
		n = ((n & 0xFFFF0000) >>> 16) + (n & 0x0000FFFF);

		return n;
	}

	private void inserthashintvalue2bloomfilter(int hashintvalue, byte[] bloomfiltervalue) {
		// TODO Auto-generated method stub
		int bytenum = hashintvalue >>> 3;
		int bitnum = hashintvalue % 8;
		byte midbyte = (byte) (1 << bitnum);
		bloomfiltervalue[bytenum] = (byte) ((bloomfiltervalue[bytenum]) | midbyte);
	}

	public void InsertionExtensionOperation(InputData a) {
		// System.out.println("input_data:"+a.inputdata_int+"
		// "+a.inputdatabitlength+" "+a.seq);
		if (this.nl == (this.n * this.pow2num[this.bloomfiltervectorlength[this.insert_ii]])) {
			if (this.curent_cachesize < this.cachesize) {
				if (((this.m * this.pow2num[this.bloomfiltervectorlength[this.insert_ii]])
						+ this.curent_cachesize) <= this.cachesize) {
					Random r = new Random();
					int midindex = r.nextInt();
					midindex = midindex > 0 ? midindex : -midindex;
					midindex = (midindex % this.bloomfilterlamdarange) + 1;
					// System.out.println(midindex);
					
					this.curent_cachesize = this.m * this.pow2num[midindex] + this.curent_cachesize;
					this.nl = 0;
					ii++;
					bloomfiltervectorlength[ii]=(midindex);
					this.insert_ii = ii;
					DBFBloomFilter bf = new DBFBloomFilter(this.m * this.pow2num[midindex]);
					// System.out.println("ii"+ii+"this.m*this.pow2num[midindex]"+((this.m*this.pow2num[midindex])>>>3));
					DBFBloomFilterGroup.add(bf);
					
					this.ExtensionRound = this.ExtensionRound + 1;
					this.SpaceSizeOfFilter = this.SpaceSizeOfFilter + this.m * this.pow2num[midindex];
					
					// this.timestamp.add(System.currentTimeMillis());
					timestamp.add(this.timerecord);
					this.timerecord++;
					this.bloomfiltersumsize = this.curent_cachesize;
				} else {
					this.bloomfiltersumsize = this.curent_cachesize;
					this.curent_cachesize = this.cachesize;
				}

			} else {
				long mintimestamp = timestamp.get(0);
				int loc = 0;
				for (int i = 1; i <= this.ii; i++) {
					if (this.timestamp.get(i) <= mintimestamp) {
						mintimestamp = this.timestamp.get(i);
						loc = i;
					}
				}
				this.insert_ii = loc;
				this.nl = 0;
				DBFBloomFilterGroup.get(loc).flush();
				// timestamp.set(loc, System.currentTimeMillis());
				timestamp.set(loc, this.timerecord);
				this.timerecord++;
				// if(this.boolean4test3==true)
				// this.reset_num++;
			}
		}
		// System.out.println(DBFBloomFilterGroup.get(this.insert_ii).bloomfiltervalue.length);
		hashkey(a, DBFBloomFilterGroup.get(this.insert_ii).bloomfiltervalue);
		this.nl++;
	}

	private void hashkey(InputData a, byte[] bloomfiltervalue) {
		// TODO Auto-generated method stub
		int insertingithbloomfiltervectorlength = 9 + this.bloomfiltervectorlength[this.insert_ii];
		int bytenum;
		int bitnum;
		int valueloc;
		int mid;
		for (int l = 0; l < this.k; l++) {
			valueloc = 0;
			for (int i = 0; i < insertingithbloomfiltervectorlength; i++) {
				mid = 0;
				int bit = a.inputdata_int & (hashgenmatrice[l].row[i].colbit_int);
				mid = CountOne(bit);
				if ((mid & 1) != 0)
					valueloc |= (1 << (insertingithbloomfiltervectorlength - i - 1));
			}
			bytenum = valueloc >>> 3;
			bitnum = valueloc % 8;//
			byte midbyte = (byte) (1 << bitnum);
			bloomfiltervalue[bytenum] = (byte) ((bloomfiltervalue[bytenum]) | midbyte);//
		}
	}

}
