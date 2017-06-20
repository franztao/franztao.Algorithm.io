/*
 * ILPSolver.cpp
 *
 *  Created on: Aug 23, 2016
 *      Author: franz
 */

#include "../head.h"
#include"../route.h"

bool KSPanswer;
extern Graph *p_graph;

pthread_cond_t KSP_cond;
pthread_mutex_t KSP_mutex;

void *KSPParallelThread(void *vargp) { //Graph *p_graph,Request *p_request

	pthread_t tid = pthread_self();
	cout << endl << "start----------thread: " << tid
			<< "------------------------" << endl;
	//instead of waiting for another thread to perform PTHREAD_JOIN on it.

	//pthread_detach(tid);
	//ILPAlgorithmBasicFlows_glpk ILPAlgorithmBasicFlows_LocalSolver
	KSPanswer = IHKSPAlgorithm(p_graph);	//ILPAlgorithmBasicFlows_glpk



	pthread_mutex_lock(&KSP_mutex);
	pthread_cond_signal(&KSP_cond);
	pthread_mutex_unlock(&KSP_mutex);
	cout << endl << "end----------thread: " << tid << "------------------------"
			<< endl;
	pthread_exit(NULL);
	return NULL;
}

bool KSPAlgorithmBasicFlows(Graph *p_graph) {
	pthread_t tid;

	pthread_mutex_init(&KSP_mutex, NULL);
	pthread_cond_init(&KSP_cond, NULL);
	int rc;
	KSPanswer = false;
//	ILPanswer=ILPAlgorithm_glpk(p_graph);
	rc = pthread_create(&tid, NULL, KSPParallelThread, NULL);
	if (rc) {
		printf("ERROR; return code from pthread_create() is %d\n", rc);
		exit(exit_franz_pthread_create);
	}

	//set running time of the other algorithm is just 30s.
	struct timeval now;
	struct timespec outtime;
	gettimeofday(&now, NULL);
	outtime.tv_sec = now.tv_sec + LimitedTime;
	outtime.tv_nsec = now.tv_usec * 1000;
	//when running time ksp algorithm pass over the  LimitedTime,terminate the ksp grogram
	pthread_mutex_lock(&KSP_mutex);
	pthread_cond_timedwait(&KSP_cond, &KSP_mutex, &outtime);
	pthread_mutex_unlock(&KSP_mutex);

//	sleep(LimitedTime);

//	rc = pthread_join(tid, NULL);
//	if (rc) {
//		printf("ERROR; return code from pthread_join() is %d\n", rc);
//		exit(-1);
//	}

	return KSPanswer;
}

