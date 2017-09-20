/*
 * ILPSolver.cpp
 *
 *  Created on: Aug 23, 2016
 *      Author: franz
 */

#include "../head.h"
#include"../route.h"

bool KSPanswer;
extern GraphTopo *p_graph;

pthread_cond_t KSP_cond;
pthread_mutex_t KSP_mutex;
pthread_t ksp_tid;
pthread_t ksp_chilld_tid;

void sigroutine(int sig) {
	pthread_exit(NULL);
}
void *KSPParallelThread(void *vargp) { //Graph *p_graph,Request *p_request

	ksp_chilld_tid = pthread_self();
//	pthread_detach(ksp_chilld_tid);
	pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL); //允许退出线程
	pthread_setcanceltype(PTHREAD_CANCEL_ASYNCHRONOUS, NULL); //设置立即取消
	//instead of waiting for another thread to perform PTHREAD_JOIN on it.

	//pthread_detach(tid);
	//ILPAlgorithmBasicFlows_glpk ILPAlgorithmBasicFlows_LocalSolver
	KSPanswer = IHKSPAlgorithm(p_graph);	//ILPAlgorithmBasicFlows_glpk

	pthread_mutex_lock(&KSP_mutex);
	pthread_cond_signal(&KSP_cond);
	pthread_mutex_unlock(&KSP_mutex);
	pthread_exit(NULL);
	return NULL;
}

bool KSPAlgorithmBasicFlows(GraphTopo *p_graph) {
//	pthread_t tid;

	int rc;
	KSPanswer = false;
//	ILPanswer=ILPAlgorithm_glpk(p_graph);
	pthread_mutex_init(&KSP_mutex, NULL);
	pthread_cond_init(&KSP_cond, NULL);
//	signal(SIGKILL,sigroutine);
	rc = pthread_create(&ksp_tid, NULL, KSPParallelThread, NULL);
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
	//when running time ksp algorithm pass over the  LimitedTime,terminate the ksp grogram
	pthread_mutex_lock(&KSP_mutex);
	pthread_cond_timedwait(&KSP_cond, &KSP_mutex, &outtime);

	pthread_cancel(ksp_chilld_tid);
//	cout<<pthread_self()<<"  "<<ksp_chilld_tid<<endl;
	pthread_mutex_unlock(&KSP_mutex);
//	int kill_rc = pthread_kill(ksp_chilld_tid, SIGTERM);//SIGKILL);
//	if (kill_rc == ESRCH)
//		printf("the specified thread did not exists or already quit\n");
//	else if (kill_rc == EINVAL)
//		printf("signal is invalid\n");
//	else
//		printf("the specified thread is alive\n");

	pthread_mutex_destroy(&KSP_mutex);
	pthread_cond_destroy(&KSP_cond);
	return KSPanswer;
}

