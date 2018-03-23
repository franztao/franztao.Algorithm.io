#include "lib/lib_io.h"
#include "lib/lib_time.h"
#include "route.h"

#include <stdio.h>

int main(int argc, char *argv[])
{

	//print_time("Experiment Beginn");
	char *topo[MAX_EDGE_NUM];
	int edgeNum;

	char *demand[MAX_DEMAND_NUM];
	int demandNum;
	char *srlg[MAX_SRLG_NUM];
	int srlgNum;

	//char[] of absolute path of file
	char *topo_file = argv[1];
	char *demand_file = argv[2];
	char *srlg_file = argv[3];
	char *result_file = argv[4];
	//algorthm types.
	int alg = atoi(argv[5]);

	//
	if (1 == argc)
	{
		string si = "0";
		string suffix =
				"/home/franz/franz/franzDocuments/eclipse4cworkspace/SCLS/Sample/test";
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

		alg = 0;
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
	edgeNum = read_file(topo, MAX_EDGE_NUM, topo_file);
	if (edgeNum == 0)
	{
		printf("Please input valid topo file.\n");
		return -1;
	}

	//read demand.csv file
	demandNum = read_file(demand, MAX_DEMAND_NUM, demand_file);
	if (demandNum != MAX_DEMAND_NUM)
	{
		printf("Please input valid demand file.\n");
		return -1;
	}

	//read srlg.csv file
	srlgNum = read_file(srlg, MAX_SRLG_NUM, srlg_file);
	if (srlgNum == 0)
	{
		printf("Please input valid srlg file.\n");
		return -1;
	}

	//begin to find disjoint paths.
	search_route(topo, edgeNum, demand, demandNum, srlg, srlgNum, alg,
			topo_file, srlg_file);

	//write disjoint paths to file.
	//write_result(result_file);
	//release buffer data.

	release_buff(topo, edgeNum);
	release_buff(demand, demandNum);
	release_buff(srlg, srlgNum);

	return 0;
}

