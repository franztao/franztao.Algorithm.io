package a2bf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import bbf.BBF;
import data.HashGenerationMatrice;
import data.InputData;
import data.TestResult;
import dbf.DBF;
import doublebf.DoubleBF;
import sfbf.SFBF;

public class cycle {

}
//
//static boolean debug = true;
//static int datalength = 1500000;
//static int searchdatalength = 2000000;
//static int testunitnum = 5;
//static int step = 100;
//static String fileabsolutepath = "F:\\workspace\\SFBF_franztao\\src\\file";
//
//static TestResult testresult;
//static int hashfunctionnum = 6;
//static boolean QueryTimewithshifting = true;
//public static HashGenerationMatrice[] hashgenmatrice;
//
//static int cachesize = Integer.MAX_VALUE;
//
//static SFBF[] sfbf;
//static DBF dbf;
//static BBF bbf;
//static A2BF a2bf;
//static DoubleBF doublebf;
//
//// set;
//static int[] datafilearray;
//
//static int[] searchdatafilearray;
//
//static int[] hitperdatafilearray;
//
//static boolean[][] falsepositivearray;
//
//private static FileWriter datafilewriter;// = new FileWriter();
//
//private static FileWriter searchdatafilewriter;
//
//private static FileWriter hitperdatafilewriter;// = new FileWriter();
//
//private static FileReader datafilereader;// = new FileReader[testunitnum];
//
//private static FileReader searchdatafilereader;
//
//private static FileReader hitperdatafilereader;// = new
//
///**
// * @param args
// * @throws IOException
// */
////   new WebCache("F:\\workspace\\SFBF_franztao\\src\\file\\actualdata_webcache_100000000","F:\\workspace\\SFBF_franztao\\src\\file\\actualdata_webcache_100000000.txt").dealwebcachefile();;
//// new  IPCache("F:\\workspace\\SFBF_franztao\\src\\file\\actualdata_ipcache_10000000","F:\\workspace\\SFBF_franztao\\src\\file\\actualdata_ipcache_10000000.txt").dealipcachefile();;
//
// make_initdate(true);
//
//if (false) {
//	read_initconcernddate();
//	test1_init();
//	insert_data();
//	writeresulttodatfile();
//}
//if (false) {
//
//	test2_read_initconcernddate((fileabsolutepath + "\\actualdata_ipcache_1500000.txt"));
//	test2_init();
//	test2_insertdata();
//}
//private static void test2_read_initconcernddate(String str) throws IOException {
//	// TODO Auto-generated method stub
//	String midstr;
//	datafilearray = new int[datalength];
//	midstr = str;// ;
//	datafilereader = new FileReader(midstr);
//
//	BufferedReader midreader;// = new BufferedReader[testunitnum];
//	String tempString = null;
//	midreader = new BufferedReader(datafilereader);
//	for (int j = 0; j < datalength; j++) {
//		tempString = midreader.readLine();
//		datafilearray[j] = Integer.valueOf(tempString);
//
//	}
//
//}
//
//private static void test2_init() {
//	// TODO Auto-generated method stub
//	hashgenmatrice = new HashGenerationMatrice[hashfunctionnum];
//	for (int i = 0; i < hashfunctionnum; i++) {
//		hashgenmatrice[i] = new HashGenerationMatrice(32, 32);
//	}
//	testresult = new TestResult(testunitnum, datalength);
//
//}
//
//private static void test2_insertdata() throws IOException {
//	// TODO Auto-generated method stub
//	int lamda[];
//	sfbf = new SFBF[1];
//
//	lamda = new int[32];
//	InputData a;
//
//	for (int i = 0; i < 32; i++) {
//
//		lamda[i] = i + 1;
//	}
//	boolean exist;
//	double hit_sum, sfbfhit_num, dbfhit_num;
//	Set<Integer> set = new HashSet<Integer>();
//	String midstr;
//	midstr = fileabsolutepath + "\\hitratio.txt";
//	FileWriter hitratiofilewriter = new FileWriter(midstr);
//	midstr = fileabsolutepath + "\\reset.txt";
//	FileWriter resetfilewriter = new FileWriter(midstr);
//
//	midstr = fileabsolutepath + "\\searchtime.txt";
//	FileWriter searchtimefilewriter = new FileWriter(midstr);
//	long sfbftimes, sfbftimee, dbftimes, dbftimee, sfbftimesum, dbftimesum;
//	for (int i = 10; i < 30; i++) {
//
//		cachesize = 1 << i;
//		sfbf[0] = new SFBF(hashfunctionnum, testresult, 0, QueryTimewithshifting, hashgenmatrice, lamda, cachesize);
//		dbf = new DBF(hashfunctionnum, testresult, 0, hashgenmatrice, cachesize);
//		// System.out.println(a2bf.hit_num+" ----1----"+a2bf.hit_sum+"
//		// "+(a2bf.hit_num/(a2bf.hit_sum*1.0)));
//		// System.out.println(sfbfhit_num+" ----2----"+sbfbhit_sum+"
//		// "+(sfbfhit_num/sbfbhit_sum));
//		// System.out.println();
//
//		hit_sum = 0;
//		sfbfhit_num = 0;
//		dbfhit_num = 0;
//		Random ran = new Random();
//		int[] mid = new int[datalength];
//		for (int j = 0; j < datalength; j++) {
//			mid[j] = ran.nextInt();
//		}
//
//		// int mid = 0;
//		sfbftimesum = dbftimesum = 0;
//		sfbftimes = System.currentTimeMillis();
//		for (int j = 0; j < datalength; j++) {
//			mid[j] = datafilearray[j];
//			 //mid[j]=j%100;
//			a = new InputData(mid[j], 32, j + 1);
//			if (set.contains(mid[j])) {
//				exist = true;
//			} else {
//				set.add(mid[j]);
//				exist = false;
//
//			}
//			boolean judge;
//
//			judge = sfbf[0].inserteverykey(a);
//
//			sfbftimesum += (sfbf[0].timeend - sfbf[0].timestart);
//
//			if (exist) {
//				hit_sum++;
//				if (judge)
//					sfbfhit_num++;
//			}
//		}
//		sfbftimee = System.currentTimeMillis();
//		// sfbftimesum += (sfbftimee- sfbftimes);
//
//		set.clear();
//
//		dbftimes = System.currentTimeMillis();
//		for (int j = 0; j < datalength; j++) {
//			mid[j] = datafilearray[j];
//			 //mid[j]=12345678;
//			a = new InputData(mid[j], 32, j + 1);
//			if (set.contains(mid[j])) {
//				exist = true;
//			} else {
//				set.add(mid[j]);
//				exist = false;
//
//			}
//			boolean judge;
//
//			judge = dbf.inserteverykey(a);
//
//			dbftimesum += (dbf.timeend - dbf.timestart);
//
//			if (exist) {
//				if (judge)
//					dbfhit_num++;
//			}
//		}
//		dbftimee = System.currentTimeMillis();
//		// dbftimesum += (dbftimee - dbftimes);
//		set.clear();
//		System.out.printf(i + "\n\n");
//		System.out.println(sfbf[0].ii + "--ii--" + dbf.ii);
//		System.out.println(sfbf[0].eachaccessbloom + "--eachaccessbloom--" + dbf.eachaccessbloom);
//		System.out.println(sfbftimesum + "--sum time--" + dbftimesum);
//
//		System.out.println(sfbfhit_num + "  ----2----" + hit_sum + "   hit_ratio:" + (sfbfhit_num / hit_sum)
//				+ "  resetnu:" + sfbf[0].reset_num);
//		System.out.println(dbfhit_num + "  ----2----" + hit_sum + "   hit_ratio:" + (dbfhit_num / hit_sum)
//				+ "  resetnu:" + dbf.reset_num);
//
//		System.out.println(((sfbf[0].bloomfiltersumsize) >> 4) + "     " + (1 << (i - 4)) + "    "
//				+ ((1 << (i - 4)) - ((sfbf[0].bloomfiltersumsize) >> 4)));
//
//		System.out.println(((dbf.bloomfiltersumsize) >> 4) + "     " + (1 << (i - 4)) + "    "
//				+ ((1 << (i - 4)) - ((dbf.bloomfiltersumsize) >> 4)));
//
//		System.out.printf(i + "\n\n");
//
//		hitratiofilewriter.write((sfbfhit_num / hit_sum) + "\r\n");
//		hitratiofilewriter.write((dbfhit_num / hit_sum) + "\r\n");
//
//		resetfilewriter.write(sfbf[0].reset_num + "\r\n");
//		resetfilewriter.write(dbf.reset_num + "\r\n");
//
//		searchtimefilewriter.write(sfbftimesum + "\r\n");
//		searchtimefilewriter.write(dbftimesum + "\r\n");
//
//	}
//	hitratiofilewriter.close();
//	resetfilewriter.close();
//	searchtimefilewriter.close();
//}
//
//private static void writeresulttodatfile() throws IOException {
//	// TODO Auto-generated method stub
//	testresult.writeresultdatatodatfile(fileabsolutepath);
//}
//
//private static void insert_data() {
//	// TODO Auto-generated method stub
//	InputData input_data;
//	boolean judge;
//	double timestart;
//	double timeend;
//	int sum;
//	double querytimestart;
//	double querytimeend;
//	for (int i = 0; i < testunitnum; i++) {
//		testresult.QueryCPUTimeWithoutShifting[i][0] = 0;
//		testresult.QueryCPUTimeWithShifting[i][0] = 0;
//		testresult.ExtensionRound[i][0] = 0;
//		testresult.SpaceSizeOfFilter[i][0] = 1024;
//		testresult.FalsePositiveRate[i][0] = 0;
//
//		timestart = System.currentTimeMillis();
//		sfbf[i].QueryTimewithshifting = true;
//		for (int j = 0; j < datalength; j++) {
//			input_data = new InputData(datafilearray[j], 32, j + 1);
//			sfbf[i].InsertionandExtensionOperation(input_data);
//			if ((j + 1) % 100 == 0) {
//				/////
//				sum = 0;
//				sfbf[i].QueryTimewithshifting = true;
//
//				querytimestart = System.currentTimeMillis();
//				for (int l = 0; l < searchdatalength; l++) {
//					input_data = new InputData(searchdatafilearray[l], 32, l + 1);
//					judge = sfbf[i].Lightweightquery(input_data);
//					if (judge) {
//						sum += 1;
//					}
//				}
//				querytimeend = System.currentTimeMillis();
//				testresult.QueryCPUTimeWithShifting[i][(j + 1) / step] = querytimeend - querytimestart;
//
//				sum -= hitperdatafilearray[(j) / step];
//				testresult.FalsePositiveRate[i][(j + 1) / step] = sum;
//
//				// System.out.println("aa"+j);
//
//				sfbf[i].QueryTimewithshifting = false;
//
//				querytimestart = System.currentTimeMillis();
//				for (int l = 0; l < searchdatalength; l++) {
//					input_data = new InputData(searchdatafilearray[l], 32, l + 1);
//					judge = sfbf[i].Lightweightquery(input_data);
//				}
//				querytimeend = System.currentTimeMillis();
//				testresult.QueryCPUTimeWithoutShifting[i][(j + 1) / step] = querytimeend - querytimestart;
//
//			}
//
//		}
//		timeend = System.currentTimeMillis();
//		testresult.insertdatasumtime = timeend - timestart;
//		System.out.println(testresult.insertdatasumtime + "");
//
//	}
//
//	testresult.ExtensionRound[testunitnum][0] = 0;
//	testresult.SpaceSizeOfFilter[testunitnum][0] = 1024;
//	testresult.FalsePositiveRate[testunitnum][0] = 0;
//	testresult.QueryCPUTimeWithShifting[testunitnum][0] = 0;
//	testresult.QueryCPUTimeWithoutShifting[testunitnum][0] = 0;
//
//	timestart = System.currentTimeMillis();
//	for (int j = 0; j < datalength; j++) {
//		InputData input_d = new InputData(datafilearray[j], 32, j + 1);
//		dbf.InsertionExtensionOperation(input_d);
//		if ((j + 1) % 100 == 0) {
//			sum = 0;
//			querytimestart = System.currentTimeMillis();
//			for (int l = 0; l < searchdatalength; l++) {
//				input_data = new InputData(searchdatafilearray[l], 32, l + 1);
//
//				judge = dbf.query(input_data);
//				if (judge == true) {
//					sum += 1;
//				}
//			}
//			querytimeend = System.currentTimeMillis();
//			sum -= hitperdatafilearray[(j) / step];
//			testresult.FalsePositiveRate[testunitnum][(j + 1) / step] = sum;
//			testresult.QueryCPUTimeWithShifting[testunitnum][(j + 1) / step] = querytimeend - querytimestart;
//			testresult.QueryCPUTimeWithoutShifting[testunitnum][(j + 1) / step] = querytimeend - querytimestart;
//		}
//
//	}
//	timeend = System.currentTimeMillis();
//	testresult.insertdatasumtime = timeend - timestart;
//	System.out.println(testresult.insertdatasumtime + "");
//
//	timestart = System.currentTimeMillis();
//	for (int j = 0; j < datalength; j++) {
//		input_data = new InputData(datafilearray[j], 32, j + 1);
//		bbf.InsertionandExtensionOperation(input_data);
//		if ((j + 1) % 100 == 0) {
//			/////
//			sum = 0;
//			for (int l = 0; l < searchdatalength; l++) {
//				input_data = new InputData(searchdatafilearray[l], 32, l + 1);
//				judge = bbf.query(input_data);
//				if (judge == true) {
//					sum += 1;
//				}
//			}
//			sum -= hitperdatafilearray[(j + 1) / 100];
//			testresult.FalsePositiveRate[testunitnum + 1][(j + 1) / 100] = sum;
//		}
//	}
//	timeend = System.currentTimeMillis();
//	testresult.insertdatasumtime = timeend - timestart;
//	System.out.println(testresult.insertdatasumtime + "");
//
//}
//
//private static void read_initconcernddate() throws IOException {
//	// TODO Auto-generated method stub
//	String midstr;
//	datafilearray = new int[datalength];
//	hitperdatafilearray = new int[(datalength / step) + 1];
//	searchdatafilearray = new int[searchdatalength];
//	midstr = fileabsolutepath + "\\filedata" + 0;
//	datafilereader = new FileReader(midstr);
//	midstr = fileabsolutepath + "\\hitperdatafile" + 0;
//	hitperdatafilereader = new FileReader(midstr);
//
//	BufferedReader midreader;// = new BufferedReader[testunitnum];
//	String tempString = null;
//	midreader = new BufferedReader(datafilereader);
//	for (int j = 0; j < datalength; j++) {
//		tempString = midreader.readLine();
//		datafilearray[j] = Integer.valueOf(tempString);
//
//	}
//	midreader = new BufferedReader(hitperdatafilereader);
//
//	for (int j = 0; j < datalength / step; j++) {
//		tempString = midreader.readLine();
//		hitperdatafilearray[j] = Integer.valueOf(tempString);
//
//	}
//
//	midstr = fileabsolutepath + "\\searchdatafile";
//	searchdatafilereader = new FileReader(midstr);
//	midreader = new BufferedReader(searchdatafilereader);
//	for (int j = 0; j < searchdatalength; j++) {
//		tempString = midreader.readLine();
//		searchdatafilearray[j] = Integer.valueOf(tempString);
//	}
//}
//
//private static void make_initdate(boolean actual) throws IOException {
//	actual=false;
//	String midstr;
//	int data;
//	
//	int[] dataarray = new int[datalength];
//	int[] searcharray = new int[searchdatalength];
//	BufferedReader midreader1 = null;
//	BufferedReader midreader2 = null;
//
//	midstr = fileabsolutepath + "\\filedata" + 0;
//
//	if (actual == false) {
//		datafilewriter = new FileWriter(midstr);
//		searchdatafilewriter = new FileWriter("F:\\workspace\\SFBF_franztao\\src\\file\\searchdatafile");
//	} else {
//		datafilereader = new FileReader(midstr);
//		midreader1 = new BufferedReader(datafilereader);
//		
//		searchdatafilereader = new FileReader("F:\\workspace\\SFBF_franztao\\src\\file\\searchdatafile");
//		midreader2 = new BufferedReader(searchdatafilereader);
//	}
//
//	midstr = fileabsolutepath + "\\hitperdatafile" + 0;
//	hitperdatafilewriter = new FileWriter(midstr);
//
//	Random random = new Random();
//	for (int i = 0; i < searchdatalength; i++) {
//
//		if (actual == false) {
//			data = random.nextInt();
//			searcharray[i] = data;
//			searchdatafilewriter.write("" + data + "\r\n");
//		} else {
//			String tempString = null;
//
//			tempString = midreader2.readLine();
//			searcharray[i] = Integer.valueOf(tempString);
//		}
//	}
//
//	int hit = 0;
//	int sum = 0;
//	for (int i = 0; i < datalength; i++) {
//		if (actual == false) {
//			data = random.nextInt();
//			dataarray[i] = data;
//
//			datafilewriter.write("" + data + "\r\n");
//		} else {
//			String tempString = null;
//
//			tempString = midreader1.readLine();
//			dataarray[i] = Integer.valueOf(tempString);
//			data = dataarray[i];
//		}
//
////		if ((i + 1) % step == 0) {
////			Arrays.sort(dataarray, 0, i + 1);
////
////			sum = 0;
////			for (int j = 0; j < searchdatalength; j++) {
////
////				hit = Arrays.binarySearch(dataarray, 0, i + 1, searcharray[j]);
////				if (hit >= 0) {
////					sum++;
////				}
////			}
////			hitperdatafilewriter.write(sum + "\r\n");
////		}
//	}
//	if (actual == false) {
//		datafilewriter.close();
//		searchdatafilewriter.close();
//
//	} else {
//		datafilereader.close();
//		searchdatafilereader.close();
//	}
//	hitperdatafilewriter.close();
//
//}
//
//private static void test1_init() {
//	// TODO Auto-generated method stub
//	hashgenmatrice = new HashGenerationMatrice[hashfunctionnum];
//	for (int i = 0; i < hashfunctionnum; i++) {
//		hashgenmatrice[i] = new HashGenerationMatrice(32, 32);
//	}
//	testresult = new TestResult(testunitnum, datalength);
//
//	dbf = new DBF(hashfunctionnum, testresult, testunitnum, hashgenmatrice, cachesize);
//	bbf = new BBF(hashfunctionnum, testresult, testunitnum + 1, hashgenmatrice);
//	int lamda[];
//	sfbf = new SFBF[testunitnum];
//	lamda = new int[32];
//	int mid;
//	Random random = new Random();
//	for (int i = 0; i < 32; i++) {
//		lamda[i] = i + 1;
//	}
//	sfbf[0] = new SFBF(hashfunctionnum, testresult, 0, QueryTimewithshifting, hashgenmatrice, lamda, cachesize);
//	lamda[0] = 1;
//	lamda[1] = 1;
//	for (int i = 2; i < 32; i++) {
//		lamda[i] = lamda[i - 1] + lamda[i - 2];
//	}
//	sfbf[1] = new SFBF(hashfunctionnum, testresult, 1, QueryTimewithshifting, hashgenmatrice, lamda, cachesize);
//	lamda[0] = 1;
//	for (int i = 1; i < 32; i++) {
//
//		mid = random.nextInt();
//		if (mid < 0)
//			mid = -1 * mid;
//		mid = mid % 2;
//		mid += 1;
//		lamda[i] = lamda[i - 1] + mid;
//	}
//	sfbf[2] = new SFBF(hashfunctionnum, testresult, 2, QueryTimewithshifting, hashgenmatrice, lamda, cachesize);
//
//	lamda[0] = 1;
//	for (int i = 1; i < 32; i++) {
//		mid = random.nextInt();
//		if (mid < 0)
//			mid = -1 * mid;
//		mid = mid % 3;
//		mid += 1;
//		lamda[i] = lamda[i - 1] + mid;
//	}
//	sfbf[3] = new SFBF(hashfunctionnum, testresult, 3, QueryTimewithshifting, hashgenmatrice, lamda, cachesize);
//	lamda[0] = 1;
//	for (int i = 1; i < 32; i++) {
//		if (i % 2 == 0)
//			mid = 1;
//		else
//			mid = 0;
//		lamda[i] = lamda[i - 1] + mid;
//	}
//	sfbf[4] = new SFBF(hashfunctionnum, testresult, 4, QueryTimewithshifting, hashgenmatrice, lamda, cachesize);
//
//}

