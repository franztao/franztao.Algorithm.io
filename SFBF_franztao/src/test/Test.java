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
import sfbf.SFBF;

public class Test {
	public SFBF[] sfbf;
	public DBF[] dbf;
	public BBF bbf;

	public int[] indatafilearray;// 30000
	public int[] searchdatafilearray;// 1000000
	public int[] hitperdatafilearray;// 100000

	// public boolean[][] falsepositivearray;
	public int cachesize;

	public int testunitnum = 5;
	// public int testunitnum = 6;

	public int lamdalenght = 1000;

	public boolean QueryTimewithshifting = true;
	public HashGenerationMatrice[] hashgenmatrice;
	public int hashfunctionnum = 6;// k

	// private FileWriter datafilewriter;// = new FileWriter();
	// private FileWriter searchdatafilewriter;
	// private FileWriter hitperdatafilewriter;// = new FileWriter();

	// private FileWriter Extensionroundwriter;
	// private FileWriter Falsepositiveratewriter;
	// private FileWriter QueryingCPUtimewithshiftingwriter;
	// private FileWriter QueryingCPUtimewithoutshiftingwriter;
	// private FileWriter Spacesizeoffilterswriter;

	private FileReader datafilereader;// = new FileReader[testunitnum];
	private FileReader searchdatafilereader;

	public int test1_indatalength = 30000;
	public int test1_searchdatalength = 150000;// 00;// 100000;
	public int test1_indatalength_step = 1200;

	public int test2_indatalength = 15000;// 1500000

	public int get_averagedata = 1;

	public BufferedReader test3BufferedReader;
	public int test3_indatalength = 1000000;
	public int test3_preindatalength = 2000000;
	public String filetest_sort_indata;

	// 0:LongTimeNotVisit 1:Random 2:LongestVectorLength 3:LongestVectorLength
	public int replacestrategy;

	public int lamda[][];

	public void test1() throws IOException {

		hashgenmatrice = new HashGenerationMatrice[hashfunctionnum];
		for (int i = 0; i < hashfunctionnum; i++) {
			hashgenmatrice[i] = new HashGenerationMatrice(32, 32);
		}

		lamda = new int[testunitnum][lamdalenght];

		for (int i = 0; i < 32; i++) {
			lamda[0][i] = i + 1;
		}

		lamda[1][0] = 1;
		for (int i = 1; i < 32; i++) {
			lamda[1][i] = lamda[1][i - 1] + 2;
		}
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

		midstr = "src/testdata/in/" + "test1_sfbf3.txt";
		datafilereader = new FileReader(midstr);
		midreader = new BufferedReader(datafilereader);
		lamda[3][0] = 1;
		for (int i = 1; i < lamdalenght; i++) {
			tempString = midreader.readLine();
			lamda[3][i] = Integer.valueOf(tempString);
		}

		midstr = "src/testdata/in/" + "test1_sfbf5.txt";
		datafilereader = new FileReader(midstr);
		midreader = new BufferedReader(datafilereader);
		lamda[4][0] = 1;
		for (int i = 1; i < lamdalenght; i++) {
			tempString = midreader.readLine();
			lamda[4][i] = Integer.valueOf(tempString);
		}

		String test_sort_indata;
		String test_sort_searchdata;
		String out_abusolutepath;
		this.replacestrategy = 0;
//		test_sort_indata = "src/testdata/in/test1_virtualdata.txt";
//		test_sort_searchdata = "src/testdata/in/test1_virtualdata_search.txt";
//		out_abusolutepath = "src/testdata/out/test1_virtualdata";
//		test1_read_initdata(test_sort_indata, test_sort_searchdata);
//		test1_insert_data(out_abusolutepath);

//		test_sort_indata = "src/testdata/in/test1_actualdata_IP_1.txt";
//		test_sort_searchdata = "src/testdata/in/test1_actualdata_IP_1_search.txt";
//		out_abusolutepath = "src/testdata/out/test1_actualdata_IP_1";
//		test1_read_initdata(test_sort_indata, test_sort_searchdata);
//		test1_insert_data(out_abusolutepath);

		test_sort_indata = "src/testdata/in/test1_actualdata_IP_2.txt";
		test_sort_searchdata = "src/testdata/in/test1_actualdata_IP_2_search.txt";
		out_abusolutepath = "src/testdata/out/test1_actualdata_IP_2";
		test1_read_initdata(test_sort_indata, test_sort_searchdata);
		test1_insert_data(out_abusolutepath);

//		test_sort_indata = "src/testdata/in/test1_actualdata_webcache_1.txt";
//		test_sort_searchdata = "src/testdata/in/test1_actualdata_webcache_1_search.txt";
//		out_abusolutepath = "src/testdata/out/test1_actualdata_webcache_1";
//		test1_read_initdata(test_sort_indata, test_sort_searchdata);
//		test1_insert_data(out_abusolutepath);
//
//		test_sort_indata = "src/testdata/in/test1_actualdata_webcache_2.txt";
//		test_sort_searchdata = "src/testdata/in/test1_actualdata_webcache_2_search.txt";
//		out_abusolutepath = "src/testdata/out/test1_actualdata_webcache_2";
//		test1_read_initdata(test_sort_indata, test_sort_searchdata);
//		test1_insert_data(out_abusolutepath);

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
			this.replacestrategy = ri;
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
		sfbf = new SFBF[testunitnum];
		dbf = new DBF[3];
		lamda = new int[testunitnum][lamdalenght];

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
			cachesize = 1 << i;
			for (int test_i = 0; test_i < testunitnum; test_i++) {
				sfbf[test_i] = new SFBF(hashfunctionnum, test_i, QueryTimewithshifting, hashgenmatrice, lamda[test_i],
						cachesize, replacestrategy);
			}

			dbf[0] = new DBF(hashfunctionnum, testunitnum, hashgenmatrice, cachesize, 3, replacestrategy);
			dbf[1] = new DBF(hashfunctionnum, testunitnum, hashgenmatrice, cachesize, 5, replacestrategy);
			dbf[2] = new DBF(hashfunctionnum, testunitnum, hashgenmatrice, cachesize, 1, replacestrategy);

			String tempString;
			int mid;
			sfbftimesum = 0;
			sfbfhit_num = 0;
			dbftimesum = 0;
			dbfhit_num = 0;
			hit_sum = 0;

			// test3BufferedReader.markSupported();
			// test3BufferedReader.mark((int)test3file.length());
			for (int test_i = 0; test_i < testunitnum; test_i++) {
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

		hashgenmatrice = new HashGenerationMatrice[hashfunctionnum];
		for (int i = 0; i < hashfunctionnum; i++) {
			hashgenmatrice[i] = new HashGenerationMatrice(32, 32);
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

	private void test1_read_initdata(String test_sort_indata, String test_sort_searchdata) throws IOException {
		// TODO Auto-generated method stub
		String midstr;
		BufferedReader midreader;
		String tempString = null;

		indatafilearray = new int[test1_indatalength];
		searchdatafilearray = new int[test1_searchdatalength];

		midstr = test_sort_indata;
		datafilereader = new FileReader(midstr);
		midreader = new BufferedReader(datafilereader);
		for (int j = 0; j < test1_indatalength; j++) {
			tempString = midreader.readLine();
			indatafilearray[j] = Integer.valueOf(tempString);

		}

		midstr = test_sort_searchdata;
		searchdatafilereader = new FileReader(midstr);
		midreader = new BufferedReader(searchdatafilereader);
		for (int j = 0; j < test1_searchdatalength; j++) {
			tempString = midreader.readLine();
			searchdatafilearray[j] = Integer.valueOf(tempString);
		}

		cachesize = Integer.MAX_VALUE;

		dbf = new DBF[1];
		dbf[0] = new DBF(hashfunctionnum, testunitnum + 1, hashgenmatrice, cachesize, 1, replacestrategy);
		// dbf[1] = new DBF(hashfunctionnum, testunitnum + 2, hashgenmatrice,
		// cachesize, 5, replacestrategy);
		// dbf[2] = new DBF(hashfunctionnum, testunitnum + 3, hashgenmatrice,
		// cachesize, 1, replacestrategy);

		bbf = new BBF(hashfunctionnum, testunitnum + 4, hashgenmatrice);

		sfbf = new SFBF[testunitnum];

		for (int test_i = 0; test_i < testunitnum; test_i++) {
			sfbf[test_i] = new SFBF(hashfunctionnum, test_i, QueryTimewithshifting, hashgenmatrice, lamda[test_i],
					cachesize, this.replacestrategy);
		}
	}

	private void test1_insert_data(String out_abusolutepath) throws IOException {
		InputData input_data;
		boolean judge;
		double timestart;
		double timeend;

		int falsepositive_numerator = 0;
		int falsepositive_denominator = 0;
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
		FileWriter Extensionroundwriter = new FileWriter(midstr);
		midstr = out_abusolutepath + "/False positive rate.txt";
		FileWriter Falsepositiveratewriter = new FileWriter(midstr);
		midstr = out_abusolutepath + "/Querying CPU time with shifting.txt";
		FileWriter QueryingCPUtimewithshiftingwriter = new FileWriter(midstr);
		midstr = out_abusolutepath + "/Querying CPU time without shifting.txt";
		FileWriter QueryingCPUtimewithoutshiftingwriter = new FileWriter(midstr);
		midstr = out_abusolutepath + "/Space size of filters.txt";
		FileWriter Spacesizeoffilterswriter = new FileWriter(midstr);

		for (int i = 0; i < testunitnum; i++) {
			timestart = System.currentTimeMillis();
			sfbf[i].QueryTimewithshifting = true;
			for (int j = 0; j < test1_indatalength; j++) {
				input_data = new InputData(indatafilearray[j], 32, j + 1);

				judge = sfbf[i].Lightweightquery(input_data);
				if (judge == false) {
					sfbf[i].InsertionandExtensionOperation(input_data);
					set.add(indatafilearray[j]);
				}
				if ((j + 1) % test1_indatalength_step == 0) {

					num_ExtensionRound = sfbf[i].ExtensionRound;
					num_SpaceSizeOfFilter = sfbf[i].SpaceSizeOfFilter;

					Extensionroundwriter.write(num_ExtensionRound + "\r\n");
					Spacesizeoffilterswriter.write(num_SpaceSizeOfFilter + "\r\n");

					falsepositive_numerator = 0;
					falsepositive_denominator = 0;
					sfbf[i].QueryTimewithshifting = true;
					num_QueryCPUTimeWithShifting = 0;
					for (int av = 0; av < get_averagedata; av++) {
						querytimestart = System.currentTimeMillis();
						for (int l = 0; l < test1_searchdatalength; l++) {
							input_data = new InputData(searchdatafilearray[l], 32, l + 1);
							judge = sfbf[i].Lightweightquery(input_data);
							if (0 == av) {
								if (!set.contains(searchdatafilearray[l])) {
									if (judge) {
										falsepositive_numerator += 1;
									}
									falsepositive_denominator++;
								}
							}
						}
						// System.out.println("falsepositive_sum"+falsepositive_sum);
						querytimeend = System.currentTimeMillis();
						num_QueryCPUTimeWithShifting = +(querytimeend - querytimestart);
					}
					num_QueryCPUTimeWithShifting /= get_averagedata;
					QueryingCPUtimewithshiftingwriter.write(num_QueryCPUTimeWithShifting + "\r\n");

					Falsepositiveratewriter.write((falsepositive_numerator * 1.0) / falsepositive_denominator + "\r\n");

					sfbf[i].QueryTimewithshifting = false;
					num_QueryCPUTimeWithoutShifting = 0;
					for (int av = 0; av < get_averagedata; av++) {
						querytimestart = System.currentTimeMillis();
						for (int l = 0; l < test1_searchdatalength; l++) {
							input_data = new InputData(searchdatafilearray[l], 32, l + 1);
							judge = sfbf[i].Lightweightquery(input_data);
						}
						querytimeend = System.currentTimeMillis();
						num_QueryCPUTimeWithoutShifting += (querytimeend - querytimestart);
					}
					num_QueryCPUTimeWithoutShifting /= get_averagedata;
					QueryingCPUtimewithoutshiftingwriter.write(num_QueryCPUTimeWithoutShifting + "\r\n");
				}

			}
			timeend = System.currentTimeMillis();
			System.out.println("sfbf:" + i + ":" + (timeend - timestart));
			set.clear();
		}

		for (int dbf_i = 0; dbf_i < 1; dbf_i++) {
			timestart = System.currentTimeMillis();
			for (int j = 0; j < test1_indatalength; j++) {
				input_data = new InputData(indatafilearray[j], 32, j + 1);
				judge = dbf[dbf_i].query(input_data);
				if (judge == false) {
					set.add(indatafilearray[j]);
					dbf[dbf_i].InsertionExtensionOperation(input_data);
				}
				if ((j + 1) % test1_indatalength_step == 0) {
					num_ExtensionRound = dbf[dbf_i].ExtensionRound;
					num_SpaceSizeOfFilter = dbf[dbf_i].SpaceSizeOfFilter;
					Extensionroundwriter.write(num_ExtensionRound + "\r\n");
					Spacesizeoffilterswriter.write(num_SpaceSizeOfFilter + "\r\n");

					num_QueryTime = 0;
					falsepositive_numerator = 0;
					falsepositive_denominator = 0;

					for (int av = 0; av < get_averagedata; av++) {
						querytimestart = System.currentTimeMillis();
						for (int l = 0; l < test1_searchdatalength; l++) {
							input_data = new InputData(searchdatafilearray[l], 32, l + 1);
							judge = dbf[dbf_i].query(input_data);
							if (0 == av)
								if (!set.contains(searchdatafilearray[l])) {

									if (judge == true) {
										falsepositive_numerator += 1;
									}
									falsepositive_denominator++;
								}
						}
						querytimeend = System.currentTimeMillis();
						num_QueryTime += (querytimeend - querytimestart);
					}

					num_QueryTime /= get_averagedata;
					QueryingCPUtimewithoutshiftingwriter.write(num_QueryTime + "\r\n");
					QueryingCPUtimewithshiftingwriter.write(num_QueryTime + "\r\n");
					Falsepositiveratewriter.write((falsepositive_numerator * 1.0) / falsepositive_denominator + "\r\n");
					// System.out.println("dbf:denominator:"+falsepositive_denominator+"
					// numerator:"+falsepositive_numerator);

				}
			}
			timeend = System.currentTimeMillis();
			System.out.println("dbf" + dbf_i + ":" + (timeend - timestart));
			set.clear();
		}

		falsepositive_numerator = 0;
		timestart = System.currentTimeMillis();
		for (int j = 0; j < test1_indatalength; j++) {
			input_data = new InputData(indatafilearray[j], 32, j + 1);
			set.add(indatafilearray[j]);
			bbf.InsertionandExtensionOperation(input_data);
			if ((j + 1) % test1_indatalength_step == 0) {
				falsepositive_numerator = 0;
				falsepositive_denominator = 0;
				for (int l = 0; l < test1_searchdatalength; l++) {
					input_data = new InputData(searchdatafilearray[l], 32, l + 1);
					judge = bbf.query(input_data);
					if (!set.contains(searchdatafilearray[l])) {
						falsepositive_denominator++;
						if (judge == true) {
							falsepositive_numerator += 1;
						}
					}
				}
				Falsepositiveratewriter.write((falsepositive_numerator * 1.0) / falsepositive_denominator + "\r\n");

			}
		}
		timeend = System.currentTimeMillis();
		System.out.println("bbf:" + (timeend - timestart));
		set.clear();

		Extensionroundwriter.close();
		Falsepositiveratewriter.close();
		QueryingCPUtimewithshiftingwriter.close();
		QueryingCPUtimewithoutshiftingwriter.close();
		Spacesizeoffilterswriter.close();
	}

	private void test2_read_initdata(String test_sort2) throws IOException {
		// TODO Auto-generated method stub
		String midstr;
		indatafilearray = new int[test2_indatalength];
		midstr = test_sort2;// ;
		datafilereader = new FileReader(midstr);

		BufferedReader midreader;// = new BufferedReader[testunitnum];
		String tempString = null;
		midreader = new BufferedReader(datafilereader);
		for (int j = 0; j < test2_indatalength; j++) {
			tempString = midreader.readLine();
			indatafilearray[j] = Integer.valueOf(tempString);
		}

		hashgenmatrice = new HashGenerationMatrice[hashfunctionnum];
		for (int i = 0; i < hashfunctionnum; i++) {
			hashgenmatrice[i] = new HashGenerationMatrice(32, 32);
		}
	}

	private void test2_insert_data(String out_abusolutepath) throws IOException {
		// TODO Auto-generated method stub
		int lamda[][];
		sfbf = new SFBF[testunitnum];
		dbf = new DBF[3];
		lamda = new int[testunitnum][lamdalenght];

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

		if (testunitnum == 6) {
			lamda[4][0] = 1;
			for (int i = 1; i < lamdalenght; i++) {
				m_mid = random.nextInt();
				m_mid = m_mid > 0 ? m_mid : -m_mid;
				m_mid = (m_mid % 3) + 1;
				lamda[4][i] = m_mid;
				// System.out.println("lamda[4][i] " + lamda[4][i]);
			}
			lamda[5][0] = 1;
			for (int i = 1; i < lamdalenght; i++) {
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
			cachesize = 1 << i;
			for (int test_i = 0; test_i < testunitnum; test_i++) {
				sfbf[test_i] = new SFBF(hashfunctionnum, test_i, QueryTimewithshifting, hashgenmatrice, lamda[test_i],
						cachesize, this.replacestrategy);
			}

			// dbf = new DBF(hashfunctionnum, testunitnum, hashgenmatrice,
			// cachesize,1);
			this.replacestrategy = 0;
			dbf[0] = new DBF(hashfunctionnum, testunitnum, hashgenmatrice, cachesize, 1, replacestrategy);
			dbf[1] = new DBF(hashfunctionnum, testunitnum, hashgenmatrice, cachesize, 1, replacestrategy);
			dbf[2] = new DBF(hashfunctionnum, testunitnum, hashgenmatrice, cachesize, 1, replacestrategy);

			int[] mid = new int[test2_indatalength];
			sfbftimesum = dbftimesum = 0;
			sfbfhit_num = 0;
			dbfhit_num = 0;
			hit_sum = 0;
			for (int test_i = 0; test_i < testunitnum; test_i++) {
				hit_sum = 0;
				sfbfhit_num = 0;

				sfbftimesum = dbftimesum = 0;
				for (int j = 0; j < test2_indatalength; j++) {
					mid[j] = indatafilearray[j];

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
					mid[j] = indatafilearray[j];

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

}
