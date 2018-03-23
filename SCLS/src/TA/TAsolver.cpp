/*
 * TAsolver.cpp
 *
 *  Created on: Nov 8, 2016
 *      Author: franz
 */


#include "../head.h"
#include"../route.h"

bool TAanswer;
extern GraphTopo *p_graph;

pthread_cond_t TA_cond;
pthread_mutex_t TA_mutex;
pthread_t ta_tid;
pthread_t ta_chilld_tid;

void *TAParallelThread(void *vargp) { //Graph *p_graph,Request *p_request

	ta_chilld_tid = pthread_self();
	pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL); //允许退出线程
	pthread_setcanceltype(PTHREAD_CANCEL_ASYNCHRONOUS, NULL); //设置立即取消
	//instead of waiting for another thread to perform PTHREAD_JOIN on it.

	TAanswer = TAAlgorithm(p_graph);

	pthread_mutex_lock(&TA_mutex);
	pthread_cond_signal(&TA_cond);
	pthread_mutex_unlock(&TA_mutex);
	pthread_exit(NULL);
	return NULL;
}

bool TAAlgorithmBasicFlows(GraphTopo *p_graph) {
//	pthread_t tid;

	int rc;
	TAanswer = false;
	pthread_mutex_init(&TA_mutex, NULL);
	pthread_cond_init(&TA_cond, NULL);
	rc = pthread_create(&ta_tid, NULL, TAParallelThread, NULL);
	if (rc) {
		printf("ERROR; return code from pthread_create() is %d\n", rc);
		exit(exit_pthread_create);
	}

	//set running time of the other algorithm is just 30s.

	struct timeval now;
	struct timespec outtime;
	gettimeofday(&now, NULL);
	outtime.tv_sec = now.tv_sec + LimitedTime;
	outtime.tv_nsec = now.tv_usec * 1000;
	pthread_mutex_lock(&TA_mutex);
	pthread_cond_timedwait(&TA_cond, &TA_mutex, &outtime);
	pthread_cancel(ta_chilld_tid);
	pthread_mutex_unlock(&TA_mutex);

	pthread_mutex_destroy(&TA_mutex);
	pthread_cond_destroy(&TA_cond);
	return TAanswer;
}

