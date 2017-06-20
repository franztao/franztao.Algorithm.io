package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import bbf.BBF;
import data.HashGenerationMatrice;
import data.InputData;
import dbf.DBF;
import main.parameters;
import sfbf.SFBF;

public class Test {
	String absoluteInDataPathString = "src/testdata/20161209/in/";
	String absoluteOutDataPathString = "src/testdata/20161209/out/";

	String[] dataString = { "test1_virtualdata.txt", "test1_actualdata_IP_1.txt", "test1_actualdata_IP_2.txt",
			"test1_actualdata_webcache_1.txt", "test1_actualdata_webcache_2.txt" };
	String[] dataSearchString = { "test1_virtualdata_search.txt", "test1_actualdata_IP_1_search.txt",
			"test1_actualdata_IP_2_search.txt", "test1_actualdata_webcache_1_search.txt",
			"test1_actualdata_webcache_2_search.txt" };
	
	String[] outDataString={"test1_virtualdata", "test1_actualdata_IP_1", "test1_actualdata_IP_2",
			"test1_actualdata_webcache_1", "test1_actualdata_webcache_2"
			
	};

	public SFBF[] sfbf;
	public DBF[] dbf;
	public BBF bbf;
	public int lamda[][];
	public int[] inDataFileArray;
	public int[] searchDataFileArray;
	public int[] hitperDataFileArray;

	// public boolean[][] falsepositivearray;
	public int cacheSize = Integer.MAX_VALUE;
	// cachesize = Integer.MAX_VALUE;
	//
	public int testUnitNum = 5;
	// public int testunitnum = 6;

	public int lamdaLenght = 100000;

	public boolean QueryTimewithshifting = true;
	public HashGenerationMatrice[] hashRootMatrice;
	public int hashFunctionNum = 6;// k

	// private FileWriter datafilewriter;// = new FileWriter();
	// private FileWriter searchdatafilewriter;
	// private FileWriter hitperdatafilewriter;// = new FileWriter();

	// private FileWriter Extensionroundwriter;
	// private FileWriter Falsepositiveratewriter;
	// private FileWriter QueryingCPUtimewithshiftingwriter;
	// private FileWriter QueryingCPUtimewithoutshiftingwriter;
	// private FileWriter Spacesizeoffilterswriter;

	private FileReader dataFileReader;// = new FileReader[testunitnum];
	private FileReader searchdatafilereader;

	public int test1InsertDataLength = 1000000;
	public int test1SearchDataLength = 500000;
	public int test1InsertedDataLength_Interval = test1InsertDataLength / 25;// test1_indatalength/25
	public int perRangeSearchNum = 20;

	
	//---------------------------------------
	public BufferedReader test3BufferedReader;
	public int test3_indatalength = 1000000;
	public int test3_preindatalength = 2000000;
	public String filetest_sort_indata;
	public int test2_indatalength = 15000;// 1500000
	// 0:LongTimeNotVisit 1:Random 2:LongestVectorLength 3:LongestVectorLength
	public int replaceStrategy;
	//---------------------------------------
	

	public void setLamda() throws IOException {
		lamda = new int[testUnitNum][lamdaLenght];
		// 1,2,3,4...
		for (int i = 0; i < 32; i++) {
			lamda[0][i] = i + 1;
		}
		// 1,3,5,7,9
		lamda[1][0] = 1;
		for (int i = 1; i < 32; i++) {
			lamda[1][i] = lamda[1][i - 1] + 2;
		}
		// 1,1,2,2,3,3,4,4
		lamda[2][0] = 1;
		for (int i = 1; i < 32; i++) {
			if (i % 2 == 0) {
				lamda[2][i] = lamda[2][i - 1] + 1;
			} else {
				lamda[2][i] = lamda[2][i - 1];
			}
		}

		String midstr, tempString;
		BufferedReader midreader;

		midstr = absoluteInDataPathString + "test1_sfbf3.txt";
		dataFileReader = new FileReader(midstr);
		midreader = new BufferedReader(dataFileReader);
		lamda[3][0] = 1;
		for (int i = 1; i < lamdaLenght; i++) {
			tempString = midreader.readLine();
			lamda[3][i] = Integer.valueOf(tempString);
		}

		midstr = absoluteInDataPathString + "test1_sfbf5.txt";
		dataFileReader = new FileReader(midstr);
		midreader = new BufferedReader(dataFileReader);
		lamda[4][0] = 1;
		for (int i = 1; i < lamdaLenght; i++) {
			tempString = midreader.readLine();
			lamda[4][i] = Integer.valueOf(tempString);
		}
	}

	public void setMatrix() {
		hashRootMatrice = new HashGenerationMatrice[hashFunctionNum];
		for (int i = 0; i < hashFunctionNum; i++) {
			hashRootMatrice[i] = new HashGenerationMatrice(32, 32);
		}
	}

	private void doExperimentOne() throws IOException {
		// TODO Auto-generated method stub
		String test_sort_indata;
		String test_sort_searchdata;
		String out_abusolutepath;
		this.replaceStrategy = 0;

		for (int i = 0; i < 1; i++) {//dataString.length
			test_sort_indata = absoluteInDataPathString + dataString[i];
			test_sort_searchdata = absoluteInDataPathString + dataSearchString[i];
			out_abusolutepath = absoluteOutDataPathString + outDataString[i];
			test1ReadInitData(test_sort_indata, test_sort_searchdata);
			test1InsertData(out_abusolutepath);
		}

	}

	public void test1() throws IOException {
		setMatrix();
		setLamda();
		doExperimentOne();
	}

	private void test1ReadInitData(String test_sort_indata, String test_sort_searchdata) throws IOException {
		// TODO Auto-generated method stub
		String midstr;
		BufferedReader br_dataFileReader;
		String tempString = null;

		inDataFileArray = new int[test1InsertDataLength];
		searchDataFileArray = new int[test1SearchDataLength];

		// read inserted data.
		midstr = test_sort_indata;
		dataFileReader = new FileReader(midstr);
		br_dataFileReader = new BufferedReader(dataFileReader);
		for (int j = 0; j < test1InsertDataLength; j++) {
			tempString = br_dataFileReader.readLine();
			inDataFileArray[j] = Integer.valueOf(tempString);

		}
		br_dataFileReader.close();
		dataFileReader.close();

		// read search data.
		midstr = test_sort_searchdata;
		searchdatafilereader = new FileReader(midstr);
		br_dataFileReader = new BufferedReader(searchdatafilereader);
		for (int j = 0; j < test1SearchDataLength; j++) {
			tempString = br_dataFileReader.readLine();
			searchDataFileArray[j] = Integer.valueOf(tempString);
		}
		br_dataFileReader.close();
		searchdatafilereader.close();

		dbf = new DBF[1];
		dbf[0] = new DBF(hashFunctionNum, testUnitNum + 1, hashRootMatrice, cacheSize, 1, this.replaceStrategy);
		// dbf[1] = new DBF(hashfunctionnum, testunitnum + 2, hashgenmatrice,
		// cachesize, 5, replacestrategy);
		// dbf[2] = new DBF(hashfunctionnum, testunitnum + 3, hashgenmatrice,
		// cachesize, 1, replacestrategy);

		bbf = new BBF(hashFunctionNum, testUnitNum + 4, hashRootMatrice);

		sfbf = new SFBF[testUnitNum];
		for (int test_i = 0; test_i < testUnitNum; test_i++) {
			sfbf[test_i] = new SFBF(hashFunctionNum, test_i, QueryTimewithshifting, hashRootMatrice, lamda[test_i],
					cacheSize, this.replaceStrategy);
		}
	}

	private void test1InsertData(String out_abusolutepath) throws IOException {
		InputData input_data;
		boolean isInBF;
		double timestart;
		double timeend;

		int numerator_FalsePositive = 0;
		int denominator_FalsePositive = 0;
		double querytimestart;
		double querytimeend;
		double num_QueryCPUTimeWithoutShifting = 0;
		double num_QueryCPUTimeWithShifting = 0;
		double num_QueryTime = 0;
		int num_ExtensionRound = 0;
		int num_SpaceSizeOfFilter = 0;

		String midstr;

		Set<Integer> set = new HashSet<Integer>();

		midstr = out_abusolutepath + "/Extension round.txt";
		FileWriter fw_ExtensionRound = new FileWriter(midstr);
		midstr = out_abusolutepath + "/False positive rate.txt";
		FileWriter fw_FalsePositiveRate = new FileWriter(midstr);
		midstr = out_abusolutepath + "/Querying CPU time with shifting.txt";
		FileWriter fw_QueryingCPUTimewithShifting = new FileWriter(midstr);
		midstr = out_abusolutepath + "/Querying CPU time without shifting.txt";
		FileWriter fw_QueryingCPUTimewithoutshifting = new FileWriter(midstr);
		midstr = out_abusolutepath + "/Space size of filters.txt";
		FileWriter fw_SpaceSizeofFilters = new FileWriter(midstr);

		//get sfbf experiment result
		for (int i = 0; i < testUnitNum; i++) {
			timestart = System.currentTimeMillis();
			sfbf[i].QueryTimewithshifting = true;
			for (int j = 0; j < test1InsertDataLength; j++) {
				input_data = new InputData(inDataFileArray[j], 32, j + 1);
				isInBF = sfbf[i].Lightweightquery(input_data);
				if (isInBF == false) {
					sfbf[i].InsertionandExtensionOperation(input_data);
					set.add(inDataFileArray[j]);
				}
				if ((j + 1) % test1InsertedDataLength_Interval == 0) {

					num_ExtensionRound = sfbf[i].ExtensionRound;
					num_SpaceSizeOfFilter = sfbf[i].SpaceSizeOfFilter;

					fw_ExtensionRound.write(num_ExtensionRound + "\r\n");
					fw_SpaceSizeofFilters.write(num_SpaceSizeOfFilter + "\r\n");

					numerator_FalsePositive = 0;
					denominator_FalsePositive = 0;
					sfbf[i].QueryTimewithshifting = true;
					num_QueryCPUTimeWithShifting = 0;
					for (int av = 0; av < perRangeSearchNum; av++) {
						querytimestart = System.currentTimeMillis();
						for (int l = 0; l < test1SearchDataLength; l++) {
							input_data = new InputData(searchDataFileArray[l], 32, l + 1);
							isInBF = sfbf[i].Lightweightquery(input_data);
							if (0 == av) {
								if (!set.contains(searchDataFileArray[l])) {
									if (isInBF) {
										numerator_FalsePositive += 1;
									}
									denominator_FalsePositive++;
								}
							}
						}
						// System.out.println("falsepositive_sum"+falsepositive_sum);
						querytimeend = System.currentTimeMillis();
						num_QueryCPUTimeWithShifting = +(querytimeend - querytimestart);
					}
					num_QueryCPUTimeWithShifting /= perRangeSearchNum;
					fw_QueryingCPUTimewithShifting.write(num_QueryCPUTimeWithShifting + "\r\n");

					fw_FalsePositiveRate.write((numerator_FalsePositive * 1.0) / denominator_FalsePositive + "\r\n");

					sfbf[i].QueryTimewithshifting = false;
					num_QueryCPUTimeWithoutShifting = 0;
					for (int av = 0; av < perRangeSearchNum; av++) {
						querytimestart = System.currentTimeMillis();
						for (int l = 0; l < test1SearchDataLength; l++) {
							input_data = new InputData(searchDataFileArray[l], 32, l + 1);
							isInBF = sfbf[i].Lightweightquery(input_data);
						}
						querytimeend = System.currentTimeMillis();
						num_QueryCPUTimeWithoutShifting += (querytimeend - querytimestart);
					}
					num_QueryCPUTimeWithoutShifting /= perRangeSearchNum;
					fw_QueryingCPUTimewithoutshifting.write(num_QueryCPUTimeWithoutShifting + "\r\n");
				}

			}
			timeend = System.currentTimeMillis();
			System.out.println("sfbf:" + i + ":" + (timeend - timestart));
			set.clear();
		}

		//get dbf experiment result
		for (int dbf_i = 0; dbf_i < 1; dbf_i++) {
			timestart = System.currentTimeMillis();
			for (int j = 0; j < test1InsertDataLength; j++) {
				input_data = new InputData(inDataFileArray[j], 32, j + 1);
				isInBF = dbf[dbf_i].query(input_data);
				if (isInBF == false) {
					set.add(inDataFileArray[j]);
					dbf[dbf_i].InsertionExtensionOperation(input_data);
				}
				if ((j + 1) % test1InsertedDataLength_Interval == 0) {
					num_ExtensionRound = dbf[dbf_i].ExtensionRound;
					num_SpaceSizeOfFilter = dbf[dbf_i].SpaceSizeOfFilter;
					fw_ExtensionRound.write(num_ExtensionRound + "\r\n");
					fw_SpaceSizeofFilters.write(num_SpaceSizeOfFilter + "\r\n");

					num_QueryTime = 0;
					numerator_FalsePositive = 0;
					denominator_FalsePositive = 0;

					for (int av = 0; av < perRangeSearchNum; av++) {
						querytimestart = System.currentTimeMillis();
						for (int l = 0; l < test1SearchDataLength; l++) {
							input_data = new InputData(searchDataFileArray[l], 32, l + 1);
							isInBF = dbf[dbf_i].query(input_data);
							if (0 == av)
								if (!set.contains(searchDataFileArray[l])) {

									if (isInBF == true) {
										numerator_FalsePositive += 1;
									}
									denominator_FalsePositive++;
								}
						}
						querytimeend = System.currentTimeMillis();
						num_QueryTime += (querytimeend - querytimestart);
					}

					num_QueryTime /= perRangeSearchNum;
					fw_QueryingCPUTimewithoutshifting.write(num_QueryTime + "\r\n");
					fw_QueryingCPUTimewithShifting.write(num_QueryTime + "\r\n");
					fw_FalsePositiveRate.write((numerator_FalsePositive * 1.0) / denominator_FalsePositive + "\r\n");
					// System.out.println("dbf:denominator:"+falsepositive_denominator+"
					// numerator:"+falsepositive_numerator);

				}
			}
			timeend = System.currentTimeMillis();
			System.out.println("dbf" + dbf_i + ":" + (timeend - timestart));
			set.clear();
		}

		numerator_FalsePositive = 0;
		timestart = System.currentTimeMillis();
		for (int j = 0; j < test1InsertDataLength; j++) {
			input_data = new InputData(inDataFileArray[j], 32, j + 1);
			set.add(inDataFileArray[j]);
			bbf.InsertionandExtensionOperation(input_data);
			if ((j + 1) % test1InsertedDataLength_Interval == 0) {
				numerator_FalsePositive = 0;
				denominator_FalsePositive = 0;
				for (int l = 0; l < test1SearchDataLength; l++) {
					input_data = new InputData(searchDataFileArray[l], 32, l + 1);
					isInBF = bbf.query(input_data);
					if (!set.contains(searchDataFileArray[l])) {
						denominator_FalsePositive++;
						if (isInBF == true) {
							numerator_FalsePositive += 1;
						}
					}
				}
				fw_FalsePositiveRate.write((numerator_FalsePositive * 1.0) / denominator_FalsePositive + "\r\n");

			}
		}
		timeend = System.currentTimeMillis();
		System.out.println("bbf:" + (timeend - timestart));
		set.clear();

		fw_ExtensionRound.close();
		fw_FalsePositiveRate.close();
		fw_QueryingCPUTimewithShifting.close();
		fw_QueryingCPUTimewithoutshifting.close();
		fw_SpaceSizeofFilters.close();
	}

	private void test2_read_initdata(String test_sort2) throws IOException {
		// TODO Auto-generated method stub
		String midstr;
		inDataFileArray = new int[test2_indatalength];
		midstr = test_sort2;// ;
		dataFileReader = new FileReader(midstr);

		BufferedReader midreader;// = new BufferedReader[testunitnum];
		String tempString = null;
		midreader = new BufferedReader(dataFileReader);
		for (int j = 0; j < test2_indatalength; j++) {
			tempString = midreader.readLine();
			inDataFileArray[j] = Integer.valueOf(tempString);
		}

		hashRootMatrice = new HashGenerationMatrice[hashFunctionNum];
		for (int i = 0; i < hashFunctionNum; i++) {
			hashRootMatrice[i] = new HashGenerationMatrice(32, 32);
		}
	}

	private void test2_insert_data(String out_abusolutepath) throws IOException {
		// TODO Auto-generated method stub
		int lamda[][];
		sfbf = new SFBF[testUnitNum];
		dbf = new DBF[3];
		lamda = new int[testUnitNum][lamdaLenght];

		int m_mid;
		for (int i = 0; i < 32; i++) {
			lamda[0][i] = i + 1;
		}
		Random random = new Random();
		for (int i = 0; i < 32; i++) {
			lamda[0][i] = i + 1;
		}

		lamda[1][0] = 1;
		for (int i = 1; i < 32; i++) {

			m_mid = random.nextInt();
			if (m_mid < 0)
				m_mid = -1 * m_mid;
			m_mid = m_mid % 2;
			m_mid += 1;
			lamda[1][i] = lamda[1][i - 1] + m_mid;
		}

		lamda[2][0] = 1;
		for (int i = 1; i < 32; i++) {
			m_mid = random.nextInt();
			if (m_mid < 0)
				m_mid = -1 * m_mid;
			m_mid = m_mid % 3;
			m_mid += 1;
			lamda[2][i] = lamda[2][i - 1] + m_mid;
		}

		lamda[3][0] = 1;
		for (int i = 1; i < 32; i++) {
			if (i % 2 == 0)
				m_mid = 1;
			else
				m_mid = 0;
			lamda[3][i] = lamda[3][i - 1] + m_mid;
			System.out.println(lamda[3][i] + "\n");
		}

		if (testUnitNum == 6) {
			lamda[4][0] = 1;
			for (int i = 1; i < lamdaLenght; i++) {
				m_mid = random.nextInt();
				m_mid = m_mid > 0 ? m_mid : -m_mid;
				m_mid = (m_mid % 3) + 1;
				lamda[4][i] = m_mid;
				// System.out.println("lamda[4][i] " + lamda[4][i]);
			}
			lamda[5][0] = 1;
			for (int i = 1; i < lamdaLenght; i++) {
				m_mid = random.nextInt();
				m_mid = m_mid > 0 ? m_mid : -m_mid;
				m_mid = (m_mid % 5) + 1;
				lamda[5][i] = m_mid;
				// System.out.println("lamda[5][i] " + lamda[5][i]);
			}
		}

		boolean exist;
		boolean judge;
		double hit_sum, sfbfhit_num, dbfhit_num;

		Set<Integer> set = new HashSet<Integer>();

		InputData a;

		String midstr;
		midstr = out_abusolutepath + "/hitratio.txt";
		FileWriter hitratiofilewriter = new FileWriter(midstr);
		midstr = out_abusolutepath + "/reset.txt";
		FileWriter resetfilewriter = new FileWriter(midstr);
		midstr = out_abusolutepath + "/searchtime.txt";
		FileWriter searchtimefilewriter = new FileWriter(midstr);

		long sfbftimesum, dbftimesum;
		for (int i = 10; i < 20; i++) {
			cacheSize = 1 << i;
			for (int test_i = 0; test_i < testUnitNum; test_i++) {
				sfbf[test_i] = new SFBF(hashFunctionNum, test_i, QueryTimewithshifting, hashRootMatrice, lamda[test_i],
						cacheSize, this.replaceStrategy);
			}

			// dbf = new DBF(hashfunctionnum, testunitnum, hashgenmatrice,
			// cachesize,1);
			this.replaceStrategy = 0;
			dbf[0] = new DBF(hashFunctionNum, testUnitNum, hashRootMatrice, cacheSize, 1, replaceStrategy);
			dbf[1] = new DBF(hashFunctionNum, testUnitNum, hashRootMatrice, cacheSize, 1, replaceStrategy);
			dbf[2] = new DBF(hashFunctionNum, testUnitNum, hashRootMatrice, cacheSize, 1, replaceStrategy);

			int[] mid = new int[test2_indatalength];
			sfbftimesum = dbftimesum = 0;
			sfbfhit_num = 0;
			dbfhit_num = 0;
			hit_sum = 0;
			for (int test_i = 0; test_i < testUnitNum; test_i++) {
				hit_sum = 0;
				sfbfhit_num = 0;

				sfbftimesum = dbftimesum = 0;
				for (int j = 0; j < test2_indatalength; j++) {
					mid[j] = inDataFileArray[j];

					a = new InputData(mid[j], 32, j + 1);
					if (set.contains(mid[j])) {
						exist = true;
					} else {
						set.add(mid[j]);
						exist = false;

					}

					judge = sfbf[test_i].inserteverykey(a);
					sfbftimesum += (sfbf[test_i].timeend - sfbf[test_i].timestart);

					if (exist) {
						hit_sum++;
						if (judge)
							sfbfhit_num++;
					}
				}
				searchtimefilewriter.write(sfbftimesum + "\r\n");
				resetfilewriter.write(sfbf[test_i].reset_num + "\r\n");
				hitratiofilewriter.write((sfbfhit_num / hit_sum) + "\r\n");
				set.clear();
			}
			hit_sum = 0;
			for (int dbf_i = 0; dbf_i < 3; dbf_i++) {
				for (int j = 0; j < test2_indatalength; j++) {
					mid[j] = inDataFileArray[j];

					a = new InputData(mid[j], 32, j + 1);
					if (set.contains(mid[j])) {
						exist = true;
					} else {
						set.add(mid[j]);
						exist = false;

					}
					judge = dbf[dbf_i].inserteverykey(a);
					dbftimesum += (dbf[dbf_i].timeend - dbf[dbf_i].timestart);

					if (exist) {
						hit_sum++;
						if (judge)
							dbfhit_num++;
					}
				}

				set.clear();
				resetfilewriter.write(dbf[dbf_i].reset_num + "\r\n");
				searchtimefilewriter.write(dbftimesum + "\r\n");
				hitratiofilewriter.write((dbfhit_num / hit_sum) + "\r\n");
			}
		}
		hitratiofilewriter.close();
		resetfilewriter.close();
		searchtimefilewriter.close();
	}

	public static boolean isValidInt(String value) {
		try {
			Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public void test3() throws IOException {
		String test_sort;
		String out_abusolutepath;

		String[] midpath;
		midpath = new String[4];
		midpath[0] = "/test2/replace0";
		midpath[1] = "/test2/replace1";
		midpath[2] = "/test2/replace2";
		midpath[3] = "/test2/replace3";
		for (int ri = 2; ri <= 3; ri++) {
			this.replaceStrategy = ri;
			// test_sort = "src/testdata/in/test2_virtualdata.txt";// windows
			// test3_read_initdata(test_sort);
			// out_abusolutepath =
			// "src/testdata/out/"+midpath[ri]+"/test2_virtualdata";
			// test3_insert_data(out_abusolutepath);
			//
			// test_sort = "src/testdata/in/test2_actualdata_IP_1.txt";//
			// windows
			// test3_read_initdata(test_sort);
			// out_abusolutepath =
			// "src/testdata/out/"+midpath[ri]+"/test2_actualdata_IP_1";
			// test3_insert_data(out_abusolutepath);

			test_sort = "src/testdata/in/test2_actualdata_IP_2.txt";// windows
			test3_read_initdata(test_sort);
			out_abusolutepath = "src/testdata/out/" + midpath[ri] + "/test2_actualdata_IP_2";
			test3_insert_data(out_abusolutepath);

			test_sort = "src/testdata/in/test2_actualdata_webcache_1.txt";//
			test3_read_initdata(test_sort);
			out_abusolutepath = "src/testdata/out/" + midpath[ri] + "/test2_actualdata_webcache_1";
			test3_insert_data(out_abusolutepath);

			test_sort = "src/testdata/in/test2_actualdata_webcache_2.txt";//
			test3_read_initdata(test_sort);
			out_abusolutepath = "src/testdata/out/" + midpath[ri] + "/test2_actualdata_webcache_2";
			test3_insert_data(out_abusolutepath);
		}
	}

	private void test3_insert_data(String out_abusolutepath) throws IOException {
		// TODO Auto-generated method stub
		int lamda[][];
		sfbf = new SFBF[testUnitNum];
		dbf = new DBF[3];
		lamda = new int[testUnitNum][lamdaLenght];

		int m_mid;
		Random random = new Random();
		for (int i = 0; i < 32; i++) {
			lamda[0][i] = i + 1;
		}

		lamda[1][0] = 1;
		for (int i = 1; i < 32; i++) {
			m_mid = random.nextInt();
			if (m_mid < 0)
				m_mid = -1 * m_mid;
			m_mid = m_mid % 2;
			m_mid += 1;
			lamda[1][i] = lamda[1][i - 1] + m_mid;
		}

		lamda[2][0] = 1;
		for (int i = 1; i < 32; i++) {
			m_mid = random.nextInt();
			if (m_mid < 0)
				m_mid = -1 * m_mid;
			m_mid = m_mid % 3;
			m_mid += 1;
			lamda[2][i] = lamda[2][i - 1] + m_mid;
		}

		lamda[3][0] = 1;
		for (int i = 1; i < 32; i++) {
			if (i % 2 == 0)
				m_mid = 1;
			else
				m_mid = 0;
			lamda[3][i] = lamda[3][i - 1] + m_mid;
		}

		boolean exist;
		boolean judge;
		double hit_sum, sfbfhit_num, dbfhit_num;

		Set<Integer> set = new HashSet<Integer>();

		InputData a;
		String midstr;
		midstr = out_abusolutepath + "/hitratio.txt";
		FileWriter hitratiofilewriter = new FileWriter(midstr);
		midstr = out_abusolutepath + "/reset.txt";
		FileWriter resetfilewriter = new FileWriter(midstr);
		midstr = out_abusolutepath + "/searchtime.txt";
		FileWriter searchtimefilewriter = new FileWriter(midstr);

		long sfbftimesum, dbftimesum;
		///
		for (int i = 10; i <= 26; i += 2) {
			cacheSize = 1 << i;
			for (int test_i = 0; test_i < testUnitNum; test_i++) {
				sfbf[test_i] = new SFBF(hashFunctionNum, test_i, QueryTimewithshifting, hashRootMatrice, lamda[test_i],
						cacheSize, replaceStrategy);
			}

			dbf[0] = new DBF(hashFunctionNum, testUnitNum, hashRootMatrice, cacheSize, 3, replaceStrategy);
			dbf[1] = new DBF(hashFunctionNum, testUnitNum, hashRootMatrice, cacheSize, 5, replaceStrategy);
			dbf[2] = new DBF(hashFunctionNum, testUnitNum, hashRootMatrice, cacheSize, 1, replaceStrategy);

			String tempString;
			int mid;
			sfbftimesum = 0;
			sfbfhit_num = 0;
			dbftimesum = 0;
			dbfhit_num = 0;
			hit_sum = 0;

			// test3BufferedReader.markSupported();
			// test3BufferedReader.mark((int)test3file.length());
			for (int test_i = 0; test_i < testUnitNum; test_i++) {
				System.out.println("size:" + i + " sfbf" + test_i);
				test3BufferedReader = new BufferedReader(new FileReader(filetest_sort_indata));
				int seq = 0;
				sfbf[test_i].boolean4test3 = false;
				while (true) {
					do {
						tempString = test3BufferedReader.readLine();
						if (isValidInt(tempString))
							break;
					} while (true);
					mid = Integer.valueOf(tempString);
					seq++;
					if (set.contains(mid)) {
						exist = true;
					} else {
						set.add(mid);
						a = new InputData(mid, 32, seq + 1);
						judge = sfbf[test_i].inserteverykey(a);
						exist = false;
					}
					if (seq == test3_preindatalength) {
						break;
					}
				}

				hit_sum = 0;
				sfbfhit_num = 0;
				sfbftimesum = 0;
				sfbf[test_i].boolean4test3 = true;
				for (int j = 0; j < test3_indatalength; j++) {
					do {
						tempString = test3BufferedReader.readLine();
						if (isValidInt(tempString))
							break;
					} while (true);
					mid = Integer.valueOf(tempString);
					a = new InputData(mid, 32, seq + 1);
					seq++;
					judge = sfbf[test_i].inserteverykey(a);
					sfbftimesum += (sfbf[test_i].timeend - sfbf[test_i].timestart);

					if (set.contains(mid)) {
						exist = true;
					} else {
						set.add(mid);
						exist = false;

					}
					hit_sum++;
					if (exist) {
						if (judge)
							sfbfhit_num++;
					}
				}

				set.clear();
				searchtimefilewriter.write((sfbftimesum * 1.0) / test3_indatalength + "\r\n");
				resetfilewriter.write(sfbf[test_i].reset_num + "\r\n");
				hitratiofilewriter.write((sfbfhit_num / hit_sum) + "\r\n");
				test3BufferedReader.close();
			}
			for (int dbf_i = 0; dbf_i < 3; dbf_i++) {
				System.out.println("size:" + i + " dbf" + dbf_i);
				test3BufferedReader = new BufferedReader(new FileReader(filetest_sort_indata));
				int seq = 0;

				dbf[dbf_i].boolean4test3 = false;
				while (true) {
					do {
						tempString = test3BufferedReader.readLine();
						if (isValidInt(tempString))
							break;
					} while (true);
					seq++;
					mid = Integer.valueOf(tempString);
					if (!set.contains(mid)) {
						set.add(mid);
						a = new InputData(mid, 32, seq + 1);

						judge = dbf[dbf_i].inserteverykey(a);
					}
					if (seq == test3_preindatalength) {
						break;
					}
				}

				hit_sum = 0;
				dbfhit_num = 0;
				dbftimesum = 0;
				dbf[dbf_i].boolean4test3 = true;
				for (int j = 0; j < test3_indatalength; j++) {
					do {
						tempString = test3BufferedReader.readLine();
						if (isValidInt(tempString))
							break;
					} while (true);
					mid = Integer.valueOf(tempString);
					a = new InputData(mid, 32, seq + 1);
					seq++;
					judge = dbf[dbf_i].inserteverykey(a);
					dbftimesum += (dbf[dbf_i].timeend - dbf[dbf_i].timestart);
					if (set.contains(mid)) {
						exist = true;
					} else {
						set.add(mid);
						exist = false;
					}
					hit_sum++;
					if (exist) {
						if (judge)
							dbfhit_num++;
					}
				}

				set.clear();
				searchtimefilewriter.write((dbftimesum * 1.0) / test3_indatalength + "\r\n");
				resetfilewriter.write(dbf[dbf_i].reset_num + "\r\n");
				hitratiofilewriter.write((dbfhit_num * 1.0 / hit_sum) + "\r\n");
			}

		}
		hitratiofilewriter.close();
		resetfilewriter.close();
		searchtimefilewriter.close();
	}

	private void test3_read_initdata(String test_sort) throws FileNotFoundException {
		// TODO Auto-generated method stub
		// datafilereader = new FileReader(test3file);
		filetest_sort_indata = test_sort;
		// for (int j = 0; j < test2_indatalength; j++) {
		// tempString = midreader.readLine();
		// indatafilearray[j] = Integer.valueOf(tempString);
		// }

		hashRootMatrice = new HashGenerationMatrice[hashFunctionNum];
		for (int i = 0; i < hashFunctionNum; i++) {
			hashRootMatrice[i] = new HashGenerationMatrice(32, 32);
		}
	}

	public void test2() throws IOException {
		String test_sort;
		String out_abusolutepath;

		test_sort = "src/testdata/in/test2_virtualdata.txt";// windows
		test2_read_initdata(test_sort);
		out_abusolutepath = "src/testdata/out/test2_virtualdata";
		test2_insert_data(out_abusolutepath);

		test_sort = "src/testdata/in/test2_actualdata_IP_1.txt";// windows
		test2_read_initdata(test_sort);
		out_abusolutepath = "src/testdata/out/test2_actualdata_IP_1";
		test2_insert_data(out_abusolutepath);
		//
		// test_sort = "src/testdata/in/test2_actualdata_IP_2.txt";// windows
		// test2_read_initdata(test_sort);
		// out_abusolutepath = "src/testdata/out/test2_actualdata_IP_2";
		// test2_insert_data(out_abusolutepath);
		//
		// test_sort = "src/testdata/in/test2_actualdata_webcache_1.txt";//
		// windows
		// test2_read_initdata(test_sort);
		// out_abusolutepath = "src/testdata/out/test2_actualdata_webcache_1";
		// test2_insert_data(out_abusolutepath);
		//
		// test_sort = "src/testdata/in/test2_actualdata_webcache_2.txt";//
		// windows
		// test2_read_initdata(test_sort);
		// out_abusolutepath = "src/testdata/out/test2_actualdata_webcache_2";
		// test2_insert_data(out_abusolutepath);

	}
}
