/*
 * ILPSolver.cpp
 *
 *  Created on: Aug 23, 2016
 *      Author: franz
 */

#include "../head.h"
#include"../route.h"

bool ILPanswer;
extern Graph *p_graph;

pthread_cond_t ILP_cond;
pthread_mutex_t ILP_mutex;

void *IPParallelThread(void *vargp) { //Graph *p_graph,Request *p_request

//	pthread_t tid = pthread_self();
//	cout << endl << "start----------thread: " << tid
//			<< "------------------------" << endl;
	//instead of waiting for another thread to perform PTHREAD_JOIN on it.

	//pthread_detach(tid);
	//ILPAlgorithmBasicFlows_glpk ILPAlgorithmBasicFlows_LocalSolver
	ILPanswer = ILPAlgorithm_glpk(p_graph);	//ILPAlgorithmBasicFlows_glpk

	pthread_mutex_lock(&ILP_mutex);
	pthread_cond_signal(&ILP_cond);
	pthread_mutex_unlock(&ILP_mutex);
//	cout << endl << "end----------thread: " << tid << "------------------------"
//			<< endl;
	pthread_exit(NULL);
	return NULL;
}

bool IPAlgorithmBasicFlows(Graph *p_graph) {
	pthread_t tid;

	pthread_mutex_init(&ILP_mutex, NULL);
	pthread_cond_init(&ILP_cond, NULL);
	int rc;
	ILPanswer = false;
//	ILPanswer=ILPAlgorithm_glpk(p_graph);
	rc = pthread_create(&tid, NULL, IPParallelThread, NULL);
	if (rc) {
		printf("ERROR; return code from pthread_create() is %d\n", rc);
		exit(-1);
	}

	//set running time of the other algorithm is just 30s.
	struct timeval now;
	struct timespec outtime;
	gettimeofday(&now, NULL);
	outtime.tv_sec = now.tv_sec + LimitedTime;
	outtime.tv_nsec = now.tv_usec * 1000;
	pthread_mutex_lock(&ILP_mutex);
	pthread_cond_timedwait(&ILP_cond, &ILP_mutex, &outtime);
	pthread_mutex_unlock(&ILP_mutex);

//	sleep(LimitedTime);

//	rc = pthread_join(tid, NULL);
//	if (rc) {
//		printf("ERROR; return code from pthread_join() is %d\n", rc);
//		exit(-1);
//	}

	return ILPanswer;
}

