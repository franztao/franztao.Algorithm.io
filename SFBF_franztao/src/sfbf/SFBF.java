package sfbf;

import java.util.Random;
import java.util.Vector;

import data.HashGenerationMatrice;
import data.InputData;
import data.TestResult;

public class SFBF {

	// public int lamdalenght = 100000;
	// control query rightshift
	public boolean QueryTimewithshifting;

	public boolean code = false;

	public int id;
	// k is the hash function's number
	public int k;
	// hash function' generation key
	public HashGenerationMatrice[] hashRootMatrice;
	// key's length
	public int R = 100000;
	// key's width
	public int w = 32;
	// current datanum in this bloomfilter
	public int nl;

	public int[] n;
	public int[] m;

	public int[] lamda = new int[this.R];

	public int ii;

	public int dataBitSize;

	public int cacheSize;
	public int curentCacheSize;

	public int currentInsertingBF_ii;
	public int max_m;
	public int ii4max_m;

	public Vector<SFBFVector> SFBFVectorGroup;

	public Vector<Long> SFBFVectorTimeStamp;
	public Long timeSum;

	// public TestResult testresult;

	public int reset_num;

	public int BFCacheSumSize;

	public long timestart, timeend;

	public long eachaccessbloom;
	public int currentMaxBFVectorBitLength;
	public int ithBFvectorBitLength[];
	public int currentInsertingBFSize;
	public int SpaceSizeOfFilter;
	public int ExtensionRound;
	public boolean boolean4test3;

	public int replacestrategy;
	public final int Random4Replace = 0;
	public final int Time4Replace = 1;
	public final int Max4Replace = 2;

	public int[][] vectorsfbf;// = new int[this.k][6];
	public boolean[][] flagsfbf;// = new boolean[this.k][6];

	public SFBF(int hashfunctionnum, int id, boolean querytimewithshifting, HashGenerationMatrice[] hashgenmatrice2,
			int[] lamda, int cachesize, int replacestrategy) {
		// this.lamda=lamda;
		this.cacheSize = cachesize;

		for (int i = 0; i < this.R; i++) {
			this.lamda[i] = lamda[i];
		}
		this.QueryTimewithshifting = querytimewithshifting;
		this.id = id;
		this.hashRootMatrice = hashgenmatrice2;
		initParameters(hashfunctionnum);

		SFBFVectorGroup = new Vector<SFBFVector>();
		SFBFVector bf = new SFBFVector(this.m[ii]);
		SFBFVectorGroup.add(bf);

		SFBFVectorTimeStamp = new Vector<Long>();
		this.timeSum = Long.MIN_VALUE;
		// timestamp.add(System.currentTimeMillis());
		SFBFVectorTimeStamp.add(this.timeSum);
		this.timeSum++;

		this.replacestrategy = replacestrategy;

	}

	private void initParameters(int hashfunctionnum) {
		// TODO Auto-generated method stub
		ii = 0;
		this.k = hashfunctionnum;
		this.vectorsfbf = new int[this.k][6];
		this.flagsfbf = new boolean[this.k][6];
		this.n = new int[this.R];
		this.m = new int[this.R];
		this.ithBFvectorBitLength = new int[this.R];

		this.n[ii] = 64;
		this.m[ii] = 1024;
		this.ithBFvectorBitLength[0] = 10;

		this.nl = 0;
		this.dataBitSize = 32;

		this.currentInsertingBF_ii = 0;
		this.max_m = this.m[ii];
		this.ii4max_m = 0;
		this.currentMaxBFVectorBitLength = 10;

		this.curentCacheSize = this.m[ii];
		this.BFCacheSumSize = this.curentCacheSize;

		this.reset_num = 0;

		this.eachaccessbloom = 0;

		this.SpaceSizeOfFilter = 0;
		this.ExtensionRound = 0;

		this.boolean4test3 = true;
	}

	private void mapKeytoValue(InputData a, byte[] BFVectorValue) {
		// TODO Auto-generated method stub
		int byteIndex;
		int bitIndex;
		int multiplicationValue;
		int temp;
		for (int l = 0; l < this.k; l++) {
			multiplicationValue = 0;
			for (int i = 0; i < this.currentInsertingBFSize; i++) {
				temp = 0;
				int bit = a.inputData_INT & (hashRootMatrice[l].row[i].colbit_int);
				temp = countOne(bit);
				if (0 != (temp & 1))
					if (0 != temp)
						multiplicationValue = multiplicationValue | (1 << (this.currentInsertingBFSize - i - 1));
			}

			byteIndex = multiplicationValue >>> 3;
			bitIndex = multiplicationValue % 8;

			byte tempbyte = (byte) (1 << bitIndex);
			BFVectorValue[byteIndex] = (byte) ((BFVectorValue[byteIndex]) | tempbyte);
		}
	}

	public boolean Lightweightquery(InputData a) {
		int hashvalue[] = new int[this.k];
		int bytenum;
		int bitnum;
		int valueloc;
		int temp = 0;
		// with shift operation
		if (this.QueryTimewithshifting) {
			for (int l = 0; l < this.k; l++) {
				valueloc = 0;
				for (int i = 0; i < this.currentMaxBFVectorBitLength; i++) {
					temp = 0;
					int bit = a.inputData_INT & (hashRootMatrice[l].row[i].colbit_int);
					temp = countOne(bit);
					if (0 != (temp & 1))
						valueloc = valueloc | (1 << (this.currentMaxBFVectorBitLength - i - 1));
				}
				hashvalue[l] = valueloc;
			}
		}

		for (int j = 0; j < this.k; j++) {
			for (int i = 0; i < 6; i++) {
				flagsfbf[j][i] = false;
			}
		}
		for (int i = 0; i <= this.ii; i++) {
			int i_hashfunnum;
			for (i_hashfunnum = 0; i_hashfunnum < this.k; i_hashfunnum++) {
				if (this.QueryTimewithshifting) {
					temp = hashvalue[i_hashfunnum] >>> (this.currentMaxBFVectorBitLength
							- this.ithBFvectorBitLength[i]);

				} else {
					//// ????? what meaning of if (this.id >= 3) {
					if (this.id >= 3) {
						if (flagsfbf[i_hashfunnum][this.ithBFvectorBitLength[i] - 10] == false) {
							valueloc = 0;
							for (int ij = 0; ij < this.ithBFvectorBitLength[i]; ij++) {
								temp = 0;
								int bit = a.inputData_INT & (hashRootMatrice[i_hashfunnum].row[ij].colbit_int);
								temp = countOne(bit);
								if ((temp & 1) != 0)
									valueloc = valueloc | (1 << (this.ithBFvectorBitLength[i] - ij - 1));
							}
							temp = valueloc;
							vectorsfbf[i_hashfunnum][this.ithBFvectorBitLength[i] - 10] = valueloc;
							flagsfbf[i_hashfunnum][this.ithBFvectorBitLength[i] - 10] = true;
						} else {
							temp = vectorsfbf[i_hashfunnum][this.ithBFvectorBitLength[i] - 10];
						}
					} else {
						valueloc = 0;
						for (int ij = 0; ij < this.ithBFvectorBitLength[i]; ij++) {
							temp = 0;
							int bit = a.inputData_INT & (hashRootMatrice[i_hashfunnum].row[ij].colbit_int);
							temp = countOne(bit);
							if ((temp & 1) != 0)
								valueloc = valueloc | (1 << (this.ithBFvectorBitLength[i] - ij - 1));
						}
						temp = valueloc;
					}
				}
				bytenum = temp >>> 3;
				bitnum = temp % 8;
				if (0 == (SFBFVectorGroup.get(i).bitVectorBYTEs[bytenum] & (1 << bitnum))) {
					break;
				}
			}
			if (i_hashfunnum == this.k) {
				this.eachaccessbloom += (this.ii - i + 1);
				// timestamp.set(i, System.currentTimeMillis());/////////
				SFBFVectorTimeStamp.set(i, this.timeSum);
				this.timeSum++;
				return true;
			}
		}
		this.eachaccessbloom += this.ii;
		return false;
	}

	public void InsertionandExtensionOperation(InputData a) {
		// System.out.println(this.nl+"aa"+this.n[this.insert_ii]+"aa"+a.seq+"aa"+this.cachesize+"aa"+this.curent_cachesize);
		if (this.nl == this.n[this.currentInsertingBF_ii]) {
			// System.out.println(this.curent_cachesize+" -

			if (this.curentCacheSize < this.cacheSize) {
				this.m[ii + 1] = (int) (Math.pow(2, this.lamda[ii + 1] - 1) * this.m[0]);
				if ((this.m[ii + 1] + this.curentCacheSize) <= this.cacheSize) {
					this.curentCacheSize = this.m[ii + 1] + this.curentCacheSize;
					this.n[ii + 1] = (int) (Math.pow(2, this.lamda[ii + 1] - 1) * this.n[0]);
					this.nl = 0;
					if (this.m[ii + 1] > this.max_m) {
						this.max_m = this.m[ii + 1];
						this.ii4max_m = ii + 1;
						this.currentMaxBFVectorBitLength = (int) (Math.log(this.m[this.ii4max_m]) / Math.log(2.0));
					}
					ii++;
					this.currentInsertingBF_ii = ii;
					this.currentInsertingBFSize = (int) (Math.log(this.m[this.currentInsertingBF_ii]) / Math.log(2.0));
					this.ithBFvectorBitLength[ii] = (int) (Math.log(this.m[ii]) / Math.log(2.0));

					SFBFVector bf = new SFBFVector(this.m[ii]);
					SFBFVectorGroup.add(bf);
					// timestamp.add(System.currentTimeMillis());
					SFBFVectorTimeStamp.add(this.timeSum);
					this.timeSum++;
					this.BFCacheSumSize = this.curentCacheSize;

					ExtensionRound = ExtensionRound + 1;
					SpaceSizeOfFilter = SpaceSizeOfFilter + this.m[ii];

				} else {
					int remainCacheSize = (this.cacheSize - this.curentCacheSize) / this.m[0];
					if (remainCacheSize >= 1) {
						int increment_m = (int) (Math.ceil(Math.log(remainCacheSize * 1.0) / Math.log(2.0)));
						this.m[ii + 1] = (int) (Math.pow(2, increment_m) * this.m[0]);
						if (this.m[ii + 1] > this.max_m) {
							this.max_m = this.m[ii + 1];
							this.ii4max_m = ii + 1;
							this.currentMaxBFVectorBitLength = (int) (Math.log(this.m[this.ii4max_m]) / Math.log(2.0));
						}

						this.n[ii + 1] = (int) (Math.pow(2, increment_m) * this.n[0]);
						this.nl = 0;
						ii++;

						this.ithBFvectorBitLength[ii] = (int) (Math.log(this.m[ii]) / Math.log(2.0));
						this.currentInsertingBF_ii = ii;
						this.currentInsertingBFSize = (int) (Math.log(this.m[this.currentInsertingBF_ii])
								/ Math.log(2.0));
						SFBFVector bf = new SFBFVector(this.m[ii]);
						SFBFVectorGroup.add(bf);
						// timestamp.add(System.currentTimeMillis());
						SFBFVectorTimeStamp.add(this.timeSum);
						this.timeSum++;
						this.curentCacheSize += this.m[ii];

					}
					this.BFCacheSumSize = this.curentCacheSize;
					this.curentCacheSize = this.cacheSize;

				}

			} else {
				SFBFReplaceOpearation();
			}

		}
		mapKeytoValue(a, SFBFVectorGroup.get(this.currentInsertingBF_ii).bitVectorBYTEs);
		this.nl++;
	}

	private void SFBFReplaceOpearation() {
		// TODO Auto-generated method stub
		int replaceithVector = 0;
		if (this.replacestrategy == this.Time4Replace) {
			long mintimestamp = SFBFVectorTimeStamp.get(0);
			for (int i = 1; i <= this.ii; i++) {
				if (this.SFBFVectorTimeStamp.get(i) < mintimestamp) {
					mintimestamp = this.SFBFVectorTimeStamp.get(i);
					replaceithVector = i;
				}
			}
		}
		if (this.replacestrategy == this.Random4Replace) {
			Random r = new Random();
			int data = r.nextInt();
			data = data > 0 ? data : -data;
			data = data % (this.ii + 1);
			replaceithVector = data;
		}
		if (this.replacestrategy == this.Max4Replace) {
			replaceithVector = this.ii4max_m;
		}
		this.currentInsertingBF_ii = replaceithVector;
		this.currentInsertingBFSize = (int) (Math.log(this.m[this.currentInsertingBF_ii]) / Math.log(2.0));
		this.nl = 0;
		SFBFVectorGroup.get(this.currentInsertingBF_ii).flush();
		SFBFVectorTimeStamp.set(this.currentInsertingBF_ii, this.timeSum);
		this.timeSum++;
		if (this.boolean4test3 == true) {
			this.reset_num++;
		}
	}

	public boolean inserteverykey(InputData a) {
		boolean result;
		timestart = System.currentTimeMillis();
		result = lightweightquery_insert(a);
		timeend = System.currentTimeMillis();

		return result;

	}

	private boolean lightweightquery_insert(InputData a) {
		// TODO Auto-generated method stub
		int currentdatahashvalue[] = new int[this.k];
		int bytenum;
		int bitnum;
		int valueloc;
		int mid = 0;
		for (int hashfun_i = 0; hashfun_i < this.k; hashfun_i++) {
			valueloc = 0;
			for (int i = 0; i < this.currentMaxBFVectorBitLength; i++) {
				mid = 0;
				int bit = a.inputData_INT & (hashRootMatrice[hashfun_i].row[i].colbit_int);
				mid = countOne(bit);
				if ((mid & 1) != 0)
					valueloc = valueloc | (1 << (this.currentMaxBFVectorBitLength - i - 1));
			}
			currentdatahashvalue[hashfun_i] = valueloc;
		}
		for (int i = 0; i <= this.ii; i++) {
			int l;
			for (l = 0; l < this.k; l++) {
				mid = currentdatahashvalue[l] >>> (this.currentMaxBFVectorBitLength - this.ithBFvectorBitLength[i]);
				bytenum = mid >>> 3;
				bitnum = mid % 8;
				if ((SFBFVectorGroup.get(i).bitVectorBYTEs[bytenum] & (1 << bitnum)) == 0) {
					break;
				}
			}
			if (l == this.k) {
				this.eachaccessbloom += (this.ii - i + 1);
				// timestamp.set(i, System.currentTimeMillis());
				SFBFVectorTimeStamp.set(i, this.timeSum);
				this.timeSum++;
				return true;
			}
		}
		this.eachaccessbloom += this.ii;

		for (int hashfun_i = 0; hashfun_i < this.k; hashfun_i++) {
			inserthashintvalue2bloomfilter(currentdatahashvalue[hashfun_i],
					SFBFVectorGroup.get(this.currentInsertingBF_ii).bitVectorBYTEs);
		}
		// timestamp.set(this.currentinsertingbloomfiter_i,
		// System.currentTimeMillis());
		SFBFVectorTimeStamp.set(this.currentInsertingBF_ii, this.timeSum);
		this.timeSum++;
		this.nl++;

		if (this.nl == this.n[this.currentInsertingBF_ii]) {
			if (this.curentCacheSize < this.cacheSize) {
				this.m[ii + 1] = (int) (Math.pow(2, this.lamda[ii + 1] - 1) * this.m[0]);
				if ((this.m[ii + 1] + this.curentCacheSize) <= this.cacheSize) {
					this.curentCacheSize = this.m[ii + 1] + this.curentCacheSize;
					this.n[ii + 1] = (int) (Math.pow(2, this.lamda[ii + 1] - 1) * this.n[0]);
					this.nl = 0;
					this.max_m = this.m[ii + 1];
					this.ii4max_m = ii + 1;
					this.currentMaxBFVectorBitLength = (int) (Math.log(this.m[this.ii4max_m]) / Math.log(2.0));
					ii++;
					this.currentInsertingBF_ii = ii;
					this.currentInsertingBFSize = (int) (Math.log(this.m[this.currentInsertingBF_ii]) / Math.log(2.0));
					this.ithBFvectorBitLength[ii] = (int) (Math.log(this.m[ii]) / Math.log(2.0));

					SFBFVector bf = new SFBFVector(this.m[ii]);
					SFBFVectorGroup.add(bf);
					// timestamp.add(System.currentTimeMillis());
					SFBFVectorTimeStamp.add(this.timeSum);
					this.timeSum++;
					this.BFCacheSumSize = this.curentCacheSize;
				} else {
					int remain = (this.cacheSize - this.curentCacheSize) / this.m[0];
					if (remain >= 1) {
						int increment = (int) (Math.ceil(Math.log(remain * 1.0) / Math.log(2.0)));
						this.m[ii + 1] = (int) (Math.pow(2, increment) * this.m[0]);
						if (this.m[ii + 1] > this.max_m) {
							this.max_m = this.m[ii + 1];
							this.ii4max_m = ii + 1;
							this.currentMaxBFVectorBitLength = (int) (Math.log(this.m[this.ii4max_m]) / Math.log(2.0));
						}

						this.n[ii + 1] = (int) (Math.pow(2, increment) * this.n[0]);
						this.nl = 0;
						ii++;

						this.ithBFvectorBitLength[ii] = (int) (Math.log(this.m[ii]) / Math.log(2.0));
						this.currentInsertingBF_ii = ii;
						this.currentInsertingBFSize = (int) (Math.log(this.m[this.currentInsertingBF_ii])
								/ Math.log(2.0));
						SFBFVector bf = new SFBFVector(this.m[ii]);
						SFBFVectorGroup.add(bf);
						// timestamp.add(System.currentTimeMillis());
						SFBFVectorTimeStamp.add(this.timeSum);
						this.timeSum++;
						this.curentCacheSize += this.m[ii];
					}
					this.BFCacheSumSize = this.curentCacheSize;
					this.curentCacheSize = this.cacheSize;

				}
			} else {
				long mintimestamp = SFBFVectorTimeStamp.get(0);
				int loc = 0;
				for (int i = 1; i <= this.ii; i++) {
					if (this.SFBFVectorTimeStamp.get(i) <= mintimestamp) {
						mintimestamp = this.SFBFVectorTimeStamp.get(i);
						loc = i;
					}
				}
				this.currentInsertingBF_ii = loc;
				this.currentInsertingBFSize = (int) (Math.log(this.m[this.currentInsertingBF_ii]) / Math.log(2.0));
				this.nl = 0;
				SFBFVectorGroup.get(this.currentInsertingBF_ii).flush();
				// timestamp.set(loc, System.currentTimeMillis());
				SFBFVectorTimeStamp.set(loc, this.timeSum);
				this.timeSum++;

				if (this.boolean4test3 == true) {
					this.reset_num++;
				}
			}
		}

		return false;
	}

	private int countOne(int n) {
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
		hashintvalue = hashintvalue >>> (this.currentMaxBFVectorBitLength
				- this.ithBFvectorBitLength[currentInsertingBF_ii]);
		int bytenum = hashintvalue >>> 3;
		int bitnum = hashintvalue % 8;
		byte midbyte = (byte) (1 << bitnum);
		bloomfiltervalue[bytenum] = (byte) ((bloomfiltervalue[bytenum]) | midbyte);
	}

}
