#include "lib/lib_io.h"
#include "lib/lib_time.h"
#include "route.h"

#include <stdio.h>

int main(int argc, char *argv[]) {

	//print_time("Experiment Beginn");
	char *topo[MAX_EDGE_NUM];
	int edge_num;

	char *demand[MAX_DEMAND_NUM];
	int demand_num;
	char *srlg[MAX_SRLG_NUM];
	int srlg_num;

	//char[] of absolute path of file
	char *topo_file = argv[1];
	char *demand_file = argv[2];
	char *srlg_file = argv[3];
	char *result_file = argv[4];
	//algorthm types.
	int alg = atoi(argv[5]);

	//
	if (argc == 1) {
		string si = "0";
		string suffix =
				"/home/franz/franzDocuments/eclipse4cworkspace/CSLSAlgorithm/Sample/test";
		string strtopo = "/topo.csv";
		string strdemand = "/demand.csv";
		string strsrlg = "/srlg.csv";
		string strresult = "/result.csv";

//		char argv1[100] ="/home/franz/eclipse4cworkspace/SRLG_Franz/test6/topo.csv";
//		char argv2[100] ="/home/franz/eclipse4cworkspace/SRLG_Franz/test6/demand.csv";
//		char argv3[100] ="/home/franz/eclipse4cworkspace/SRLG_Franz/test6/srlg.csv";
//		char argv4[100] =	"/home/franz/eclipse4cworkspace/SRLG_Franz/test6/result.csv";
//		topo_file=argv1;
//		demand_file=argv2;
//		srlg_file=argv3;
//		result_file=argv4;

		alg =0;
		string s1 = (suffix + si + strtopo);
		string s2 = (suffix + si + strdemand);
		string s3 = (suffix + si + strsrlg);
		string s4 = (suffix + si + strresult);

		topo_file = (char *) malloc(sizeof(char) * (s1.length() + 1));

		demand_file = (char *) malloc(sizeof(char) * (s2.length() + 1));
		srlg_file = (char *) malloc(sizeof(char) * (s3.length() + 1));
		result_file = (char *) malloc(sizeof(char) * (s4.length() + 1));
		strcpy(topo_file, s1.c_str());
		strcpy(demand_file, s2.c_str());
		strcpy(srlg_file, s3.c_str());
		strcpy(result_file, s4.c_str());

	}
	//read topo.csv file
	edge_num = read_file(topo, MAX_EDGE_NUM, topo_file);
	if (edge_num == 0) {
		printf("Please input valid topo file.\n");
		return -1;
	}

	//read demand.csv file
	demand_num = read_file(demand, MAX_DEMAND_NUM, demand_file);
	if (demand_num != MAX_DEMAND_NUM) {
		printf("Please input valid demand file.\n");
		return -1;
	}

	//read srlg.csv file
	srlg_num = read_file(srlg, MAX_SRLG_NUM, srlg_file);
	if (srlg_num == 0) {
		printf("Please input valid srlg file.\n");
		return -1;
	}
	//begin to find disjoint paths.
//	alg=-4;
	cout<<"allsrlg:"<<srlg_num<<endl;
	search_route(topo, edge_num, demand, demand_num, srlg, srlg_num, alg,
			topo_file,srlg_file);

	//write disjoint paths to file.
//	write_result(result_file);
	//release buffer data.
	release_buff(topo, edge_num);
	release_buff(demand, demand_num);
	release_buff(srlg, srlg_num);

	return 0;
}

