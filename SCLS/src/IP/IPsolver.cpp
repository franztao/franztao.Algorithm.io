/*
 * ILPSolver.cpp
 *
 *  Created on: Aug 23, 2016
 *      Author: franz
 */

#include "../head.h"
#include"../route.h"

bool bool_IPgetAnswer;
extern GraphTopo *p_graph;

pthread_cond_t ILP_cond;
pthread_mutex_t ILP_mutex;

pthread_t ip_child_tid;
void *IPParallelThread(void *vargp) { //Graph *p_graph,Request *p_request

//	pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL); //允许退出线程
//	pthread_setcanceltype(PTHREAD_CANCEL_ASYNCHRONOUS, NULL); //设置立即取消
//	ip_child_tid = pthread_self();

	//instead of waiting for another thread to perform PTHREAD_JOIN on it.
	//pthread_detach(tid);
	int *p_set = (int *) vargp;

	if (((*p_set) == algorithm_IQP) || (algorithm_IQP_sum == (*p_set)))
		bool_IPgetAnswer = IQPAlgorithm_gurobi(p_graph, (*p_set));
	if (((*p_set) == algorithm_ILP) || (algorithm_ILP_sum == (*p_set)))
		bool_IPgetAnswer = ILPAlgorithm_gurobi(p_graph, (*p_set));

//	ILPanswer = ILPAlgorithm_glpk(p_graph);	//ILPAlgorithmBasicFlows_glpk

	pthread_mutex_lock(&ILP_mutex);
	pthread_cond_signal(&ILP_cond);
	pthread_mutex_unlock(&ILP_mutex);
	pthread_exit(NULL);
	return NULL;
}

bool IPAlgorithmBasicFlows(GraphTopo *p_graph, int type) {
	pthread_t tid;
	int rc;
	bool_IPgetAnswer = false;
//	ILPanswer=ILPAlgorithm_glpk(p_graph);

	int *p_int = (int*) malloc(sizeof(int));
	*p_int = type;
	pthread_mutex_init(&ILP_mutex, NULL);
	pthread_cond_init(&ILP_cond, NULL);
	rc = pthread_create(&tid, NULL, IPParallelThread, p_int);
	if (rc) {
		printf("ERROR; return code from pthread_create() is %d\n", rc);
		exit(-1);
	}

	struct timeval now;
	struct timespec outtime;
	gettimeofday(&now, NULL);
	outtime.tv_sec = now.tv_sec + LimitedTime;
	outtime.tv_nsec = now.tv_usec * 1000;
	pthread_mutex_lock(&ILP_mutex);
	pthread_cond_timedwait(&ILP_cond, &ILP_mutex, &outtime);
	pthread_mutex_unlock(&ILP_mutex);
	pthread_mutex_destroy(&ILP_mutex);
	pthread_cond_destroy(&ILP_cond);
	delete p_int;

	return bool_IPgetAnswer;
}

