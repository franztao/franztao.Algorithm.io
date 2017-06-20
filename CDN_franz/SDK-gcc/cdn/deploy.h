#ifndef __ROUTE_H__
#define __ROUTE_H__

#include "lib/lib_io.h"

#include <iostream>
#include <algorithm>
#include <vector>
#include <string.h>
#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#include<limits.h>
#include<assert.h>

#include <fstream>
#include<sstream>
#include "sys/timeb.h"
#include <queue>
#include<pthread.h>
#include <signal.h>
#include<semaphore.h>
#include<errno.h>
#include<iterator>
#include <sys/time.h>
using namespace std;
void deploy_server(char * graph[MAX_EDGE_NUM], int edge_num, char * filename);
inline int getservernum();

int getcost(vector<bool> Next_Population);
void yichuan(int popula_size,vector<bool> &best_solution);


	

#endif
