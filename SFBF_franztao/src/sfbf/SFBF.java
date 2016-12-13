package sfbf;

import java.util.Random;
import java.util.Vector;

import data.HashGenerationMatrice;
import data.InputData;
import data.TestResult;

public class SFBF {
	
	public int lamdalenght = 100000;
	// control query rightshift
	public boolean QueryTimewithshifting;

	public boolean code = false;

	public int id;
	// k is the hash function's number
	public int k;
	// hash function' generation key
	public HashGenerationMatrice[] hashgenmatrice;
	// key's length
	public int R = 32;
	// key's width
	public int w = 32;
	// current datanum in this bloomfilter
	public int nl;

	public int[] n;
	public int[] m;

	
	public int[] lamda = new int[lamdalenght];

	public int ii;

	public int databitlength;

	public int cachesize;
	public int curent_cachesize;

	public int currentinsertingbloomfiter_i;
	public int maxm;
	public int maxmloc;

	public Vector<SFBloomFilter> SFbloomfilterGroup;

	public Vector<Long> timestamp;
	public Long timerecord;

	// public TestResult testresult;

	public int reset_num;

	public int bloomfiltersumsize;

	public long timestart, timeend;

	public long eachaccessbloom;
	public int currentmaxbloomfilterbitlen;
	public int ithbloomfilterbitlen[];
	public int currentinsertingbloomfiterlen;
	public int SpaceSizeOfFilter;
	public int ExtensionRound;
	public boolean boolean4test3;

	public int replacestrategy;
	public int[][] vectorsfbf;// = new int[this.k][6];
	public boolean[][] flagsfbf;// = new boolean[this.k][6];

	public SFBF(int hashfunctionnum, int id, boolean querytimewithshifting, HashGenerationMatrice[] hashgenmatrice2,
			int[] lamda, int cachesize, int replacestrategy) {
		// this.lamda=lamda;
		this.cachesize = cachesize;

		for (int i = 0; i < lamdalenght; i++) {
			this.lamda[i] = lamda[i];
		}
		this.QueryTimewithshifting = querytimewithshifting;
		// this.testresult = result;
		this.id = id;
		this.hashgenmatrice = hashgenmatrice2;
		initparameter(hashfunctionnum);

		SFbloomfilterGroup = new Vector<SFBloomFilter>();
		SFBloomFilter bf = new SFBloomFilter(this.m[ii]);
		SFbloomfilterGroup.add(bf);

		timestamp = new Vector<Long>();
		this.timerecord = Long.MIN_VALUE;
		// timestamp.add(System.currentTimeMillis());
		timestamp.add(this.timerecord);
		this.timerecord++;

		this.replacestrategy = replacestrategy;

	}

	private void initparameter(int hashfunctionnum) {
		// TODO Auto-generated method stub
		ii = 0;
		this.k = hashfunctionnum;
		this.vectorsfbf = new int[this.k][6];
		this.flagsfbf = new boolean[this.k][6];
		this.n = new int[lamdalenght];
		this.m = new int[lamdalenght];
		this.ithbloomfilterbitlen = new int[lamdalenght];

		this.n[ii] = 64;
		this.m[ii] = 1024;
		this.ithbloomfilterbitlen[0] = 10;

		this.nl = 0;
		this.databitlength = 32;

		this.currentinsertingbloomfiter_i = 0;
		this.maxm = this.m[ii];
		this.maxmloc = 0;
		this.currentmaxbloomfilterbitlen = 10;

		this.curent_cachesize = this.m[ii];
		this.bloomfiltersumsize = this.curent_cachesize;

		this.reset_num = 0;

		this.eachaccessbloom = 0;

		this.SpaceSizeOfFilter = 0;
		this.ExtensionRound = 0;

		this.boolean4test3 = true;
	}

	private void hashkey(InputData a, byte[] bloomfiltervalue) {
		// TODO Auto-generated method stub
		// int lmax_insert_ii = (int) (Math.log(this.m[this.insert_ii]) /
		// Math.log(2.0));
		int bytenum;
		int bitnum;
		int valueloc;
		int mid;
		for (int l = 0; l < this.k; l++) {
			valueloc = 0;
			for (int i = 0; i < this.currentinsertingbloomfiterlen; i++) {
				mid = 0;
				int bit = a.inputdata_int & (hashgenmatrice[l].row[i].colbit_int);
				mid = countOne(bit);
				if ((mid & 1) != 0)
					if (mid != 0)
						valueloc = valueloc | (1 << (this.currentinsertingbloomfiterlen - i - 1));
			}

			bytenum = valueloc >>> 3;
			bitnum = valueloc % 8;

			byte midbyte = (byte) (1 << bitnum);
			bloomfiltervalue[bytenum] = (byte) ((bloomfiltervalue[bytenum]) | midbyte);
		}
	}

	public boolean Lightweightquery(InputData a) {

		// this.lmax_i= (int) (Math.log(this.m[this.maxmloc]) / Math.log(2.0));
		// int lmax = (int) (Math.log(this.m[this.maxmloc]) / Math.log(2.0));

		int midaddr[] = new int[this.k];
		int bytenum;
		int bitnum;
		int valueloc;
		int mid = 0;
		if (this.QueryTimewithshifting) {
			for (int l = 0; l < this.k; l++) {
				valueloc = 0;
				for (int i = 0; i < this.currentmaxbloomfilterbitlen; i++) {
					mid = 0;
					int bit = a.inputdata_int & (hashgenmatrice[l].row[i].colbit_int);
					mid = countOne(bit);
					if ((mid & 1) != 0)
						valueloc = valueloc | (1 << (this.currentmaxbloomfilterbitlen - i - 1));
				}
				midaddr[l] = valueloc;
			}
		}

		for (int j = 0; j < this.k; j++) {
			for (int i = 0; i < 6; i++) {
				flagsfbf[j][i] = false;
			}
		}
		for (int i = 0; i <= this.ii; i++) {
			int hashfun_i;
			for (hashfun_i = 0; hashfun_i < this.k; hashfun_i++) {
				if (this.QueryTimewithshifting) {
					mid = midaddr[hashfun_i] >>> (this.currentmaxbloomfilterbitlen - this.ithbloomfilterbitlen[i]);

				} else {
					// System.out.println(hashfun_i);

					if (this.id >= 3) {
						if (flagsfbf[hashfun_i][this.ithbloomfilterbitlen[i] - 10] == false) {
							valueloc = 0;
							for (int ij = 0; ij < this.ithbloomfilterbitlen[i]; ij++) {
								mid = 0;
								int bit = a.inputdata_int & (hashgenmatrice[hashfun_i].row[ij].colbit_int);
								mid = countOne(bit);
								if ((mid & 1) != 0)
									valueloc = valueloc | (1 << (this.ithbloomfilterbitlen[i] - ij - 1));
							}
							mid = valueloc;
							vectorsfbf[hashfun_i][this.ithbloomfilterbitlen[i] - 10] = valueloc;
							flagsfbf[hashfun_i][this.ithbloomfilterbitlen[i] - 10] = true;
						} else {
							mid = vectorsfbf[hashfun_i][this.ithbloomfilterbitlen[i] - 10];
						}
					} else {
						valueloc = 0;
						for (int ij = 0; ij < this.ithbloomfilterbitlen[i]; ij++) {
							mid = 0;
							int bit = a.inputdata_int & (hashgenmatrice[hashfun_i].row[ij].colbit_int);
							mid = countOne(bit);
							if ((mid & 1) != 0)
								valueloc = valueloc | (1 << (this.ithbloomfilterbitlen[i] - ij - 1));
						}
						mid = valueloc;
					}
				}
				bytenum = mid >>> 3;
				bitnum = mid % 8;
				if ((SFbloomfilterGroup.get(i).bloomfiltervalue[bytenum] & (1 << bitnum)) == 0) {
					break;
				}
			}
			if (hashfun_i == this.k) {
				this.eachaccessbloom += (this.ii - i + 1);
				// timestamp.set(i, System.currentTimeMillis());/////////
				timestamp.set(i, this.timerecord);
				this.timerecord++;
				return true;
			}
		}
		this.eachaccessbloom += this.ii;
		return false;
	}

	public void InsertionandExtensionOperation(InputData a) {
		// System.out.println(this.nl+"aa"+this.n[this.insert_ii]+"aa"+a.seq+"aa"+this.cachesize+"aa"+this.curent_cachesize);
		if (this.nl == this.n[this.currentinsertingbloomfiter_i]) {
			// System.out.println(this.curent_cachesize+" -

			if (this.curent_cachesize < this.cachesize) {
				this.m[ii + 1] = (int) (Math.pow(2, this.lamda[ii + 1] - 1) * this.m[0]);
				if ((this.m[ii + 1] + this.curent_cachesize) <= this.cachesize) {
					// System.out.println(" this.lamda[ii + 1] "+ this.lamda[ii
					// + 1] +"\n");
					// System.out.println("this.m[ii + 1]"+this.m[ii +
					// 1]+"this.curent_cachesize"+this.curent_cachesize+"\n");
					this.curent_cachesize = this.m[ii + 1] + this.curent_cachesize;
					this.n[ii + 1] = (int) (Math.pow(2, this.lamda[ii + 1] - 1) * this.n[0]);
					this.nl = 0;
					if (this.m[ii + 1] > this.maxm) {
						this.maxm = this.m[ii + 1];
						this.maxmloc = ii + 1;
						this.currentmaxbloomfilterbitlen = (int) (Math.log(this.m[this.maxmloc]) / Math.log(2.0));
					}
					ii++;
					this.currentinsertingbloomfiter_i = ii;
					this.currentinsertingbloomfiterlen = (int) (Math.log(this.m[this.currentinsertingbloomfiter_i])
							/ Math.log(2.0));
					this.ithbloomfilterbitlen[ii] = (int) (Math.log(this.m[ii]) / Math.log(2.0));

					SFBloomFilter bf = new SFBloomFilter(this.m[ii]);
					SFbloomfilterGroup.add(bf);
					// timestamp.add(System.currentTimeMillis());
					timestamp.add(this.timerecord);
					this.timerecord++;
					this.bloomfiltersumsize = this.curent_cachesize;

					ExtensionRound = ExtensionRound + 1;
					SpaceSizeOfFilter = SpaceSizeOfFilter + this.m[ii];

				} else {
					int remain = (this.cachesize - this.curent_cachesize) / this.m[0];
					// System.out.println("aaaaaaaaaaa:" + remain);
					if (remain >= 1) {
						int increment = (int) (Math.ceil(Math.log(remain * 1.0) / Math.log(2.0)));
						this.m[ii + 1] = (int) (Math.pow(2, increment) * this.m[0]);
						if (this.m[ii + 1] > this.maxm) {
							this.maxm = this.m[ii + 1];
							this.maxmloc = ii + 1;
							this.currentmaxbloomfilterbitlen = (int) (Math.log(this.m[this.maxmloc]) / Math.log(2.0));
						}

						this.n[ii + 1] = (int) (Math.pow(2, increment) * this.n[0]);
						this.nl = 0;
						ii++;

						this.ithbloomfilterbitlen[ii] = (int) (Math.log(this.m[ii]) / Math.log(2.0));
						this.currentinsertingbloomfiter_i = ii;
						this.currentinsertingbloomfiterlen = (int) (Math.log(this.m[this.currentinsertingbloomfiter_i])
								/ Math.log(2.0));
						SFBloomFilter bf = new SFBloomFilter(this.m[ii]);
						SFbloomfilterGroup.add(bf);
						// timestamp.add(System.currentTimeMillis());
						timestamp.add(this.timerecord);
						this.timerecord++;
						this.curent_cachesize += this.m[ii];

					}
					this.bloomfiltersumsize = this.curent_cachesize;
					this.curent_cachesize = this.cachesize;

				}

			} else {

				sfbf_replacestrategy();

			}

		}
		hashkey(a, SFbloomfilterGroup.get(this.currentinsertingbloomfiter_i).bloomfiltervalue);
		this.nl++;
	}

	private void sfbf_replacestrategy() {
		// TODO Auto-generated method stub
		int loc = 0;
		if (this.replacestrategy == 0) {
			long mintimestamp = timestamp.get(0);

			for (int i = 1; i <= this.ii; i++) {
				if (this.timestamp.get(i) <= mintimestamp) {
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
			loc = this.maxmloc;
		}
		this.currentinsertingbloomfiter_i = loc;
		this.currentinsertingbloomfiterlen = (int) (Math.log(this.m[this.currentinsertingbloomfiter_i])
				/ Math.log(2.0));
		this.nl = 0;
		SFbloomfilterGroup.get(this.currentinsertingbloomfiter_i).flush();
		// timestamp.set(this.currentinsertingbloomfiter_i,
		// System.currentTimeMillis());
		timestamp.set(this.currentinsertingbloomfiter_i, this.timerecord);
		this.timerecord++;
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
			for (int i = 0; i < this.currentmaxbloomfilterbitlen; i++) {
				mid = 0;
				int bit = a.inputdata_int & (hashgenmatrice[hashfun_i].row[i].colbit_int);
				mid = countOne(bit);
				if ((mid & 1) != 0)
					valueloc = valueloc | (1 << (this.currentmaxbloomfilterbitlen - i - 1));
			}
			currentdatahashvalue[hashfun_i] = valueloc;
		}
		for (int i = 0; i <= this.ii; i++) {
			int l;
			for (l = 0; l < this.k; l++) {
				mid = currentdatahashvalue[l] >>> (this.currentmaxbloomfilterbitlen - this.ithbloomfilterbitlen[i]);
				bytenum = mid >>> 3;
				bitnum = mid % 8;
				if ((SFbloomfilterGroup.get(i).bloomfiltervalue[bytenum] & (1 << bitnum)) == 0) {
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

		for (int hashfun_i = 0; hashfun_i < this.k; hashfun_i++) {
			inserthashintvalue2bloomfilter(currentdatahashvalue[hashfun_i],
					SFbloomfilterGroup.get(this.currentinsertingbloomfiter_i).bloomfiltervalue);
		}
		// timestamp.set(this.currentinsertingbloomfiter_i,
		// System.currentTimeMillis());
		timestamp.set(this.currentinsertingbloomfiter_i, this.timerecord);
		this.timerecord++;
		this.nl++;

		if (this.nl == this.n[this.currentinsertingbloomfiter_i]) {
			if (this.curent_cachesize < this.cachesize) {
				this.m[ii + 1] = (int) (Math.pow(2, this.lamda[ii + 1] - 1) * this.m[0]);
				if ((this.m[ii + 1] + this.curent_cachesize) <= this.cachesize) {
					this.curent_cachesize = this.m[ii + 1] + this.curent_cachesize;
					this.n[ii + 1] = (int) (Math.pow(2, this.lamda[ii + 1] - 1) * this.n[0]);
					this.nl = 0;
					this.maxm = this.m[ii + 1];
					this.maxmloc = ii + 1;
					this.currentmaxbloomfilterbitlen = (int) (Math.log(this.m[this.maxmloc]) / Math.log(2.0));
					ii++;
					this.currentinsertingbloomfiter_i = ii;
					this.currentinsertingbloomfiterlen = (int) (Math.log(this.m[this.currentinsertingbloomfiter_i])
							/ Math.log(2.0));
					this.ithbloomfilterbitlen[ii] = (int) (Math.log(this.m[ii]) / Math.log(2.0));

					SFBloomFilter bf = new SFBloomFilter(this.m[ii]);
					SFbloomfilterGroup.add(bf);
					// timestamp.add(System.currentTimeMillis());
					timestamp.add(this.timerecord);
					this.timerecord++;
					this.bloomfiltersumsize = this.curent_cachesize;
				} else {
					int remain = (this.cachesize - this.curent_cachesize) / this.m[0];
					if (remain >= 1) {
						int increment = (int) (Math.ceil(Math.log(remain * 1.0) / Math.log(2.0)));
						this.m[ii + 1] = (int) (Math.pow(2, increment) * this.m[0]);
						if (this.m[ii + 1] > this.maxm) {
							this.maxm = this.m[ii + 1];
							this.maxmloc = ii + 1;
							this.currentmaxbloomfilterbitlen = (int) (Math.log(this.m[this.maxmloc]) / Math.log(2.0));
						}

						this.n[ii + 1] = (int) (Math.pow(2, increment) * this.n[0]);
						this.nl = 0;
						ii++;

						this.ithbloomfilterbitlen[ii] = (int) (Math.log(this.m[ii]) / Math.log(2.0));
						this.currentinsertingbloomfiter_i = ii;
						this.currentinsertingbloomfiterlen = (int) (Math.log(this.m[this.currentinsertingbloomfiter_i])
								/ Math.log(2.0));
						SFBloomFilter bf = new SFBloomFilter(this.m[ii]);
						SFbloomfilterGroup.add(bf);
						// timestamp.add(System.currentTimeMillis());
						timestamp.add(this.timerecord);
						this.timerecord++;
						this.curent_cachesize += this.m[ii];
					}
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
				this.currentinsertingbloomfiter_i = loc;
				this.currentinsertingbloomfiterlen = (int) (Math.log(this.m[this.currentinsertingbloomfiter_i])
						/ Math.log(2.0));
				this.nl = 0;
				SFbloomfilterGroup.get(this.currentinsertingbloomfiter_i).flush();
				// timestamp.set(loc, System.currentTimeMillis());
				timestamp.set(loc, this.timerecord);
				this.timerecord++;

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
		hashintvalue = hashintvalue >>> (this.currentmaxbloomfilterbitlen
				- this.ithbloomfilterbitlen[currentinsertingbloomfiter_i]);
		int bytenum = hashintvalue >>> 3;
		int bitnum = hashintvalue % 8;
		byte midbyte = (byte) (1 << bitnum);
		bloomfiltervalue[bytenum] = (byte) ((bloomfiltervalue[bytenum]) | midbyte);
	}

}
