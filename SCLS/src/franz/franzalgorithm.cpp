/*
 * algorithm.cpp
 *
 *  Created on: Jul 8, 2016
 *      Author: Taoheng
 */
#include "../head.h"
#include"../route.h"
//#include "gurobi_c++.h"

#include "../franz/networkflow.h"
//#include "lib/lib_io.h"
//#include <glpk.h>

extern GraphTopo *p_graph;
extern pthread_mutex_t mutex_result;
extern DisjointPathPair *AlgorithmResult;
extern pthread_mutex_t mutex_thread;
extern int Algorithm;

//int num_thread = 0;
//sem_t mutex_thread;
pthread_cond_t franz_cond;
pthread_mutex_t franz_mutex;
pthread_t franz_tid;
void *
franzParallelThread(void *vargp);

void DivideAndConquer(Request *p_request)
{

	int rc;
	vector < pthread_t > tidvec;
	InclusionExclusionSet *raw_set = new InclusionExclusionSet(
			p_graph->edgeNum);

	cout << "APInclusionEdges" << (p_request->APInclusionEdges.size()) << endl;
	cout << "APExlusionEdges" << p_request->APExlusionEdges.size() << endl;
	std::copy(p_request->APMustPassEdges.begin(),
			p_request->APMustPassEdges.end(), raw_set->Inclusion.begin());
	std::copy(p_request->APMustNotPassEdges.begin(),
			p_request->APMustNotPassEdges.end(), raw_set->Exlusion.begin());
//	cout<<"APInclusionEdges"<<p_request->APInclusionEdges.size()<<endl;
//	cout<<"APExlusionEdges"<<p_request->APExlusionEdges.size()<<endl;
	for (unsigned i = 0; i < p_request->APInclusionEdges.size(); i++)
	{
		pthread_t *tid_ex; //, *tid_in;
		tid_ex = (pthread_t*) malloc(sizeof(pthread_t));
//		tid_in = (pthread_t*) malloc(sizeof(pthread_t));
//		InclusionExclusionSet *p_set_in = new InclusionExclusionSet(
//				p_graph->edgeNum);
		InclusionExclusionSet *p_set_ex = new InclusionExclusionSet(
				p_graph->edgeNum);

		std::copy(raw_set->Inclusion.begin(), raw_set->Inclusion.end(),
				p_set_ex->Inclusion.begin());
		std::copy(raw_set->Exlusion.begin(), raw_set->Exlusion.end(),
				p_set_ex->Exlusion.begin());
		p_set_ex->Exlusion.at(p_request->APInclusionEdges[i]) = false;
		rc = pthread_create(tid_ex, NULL, franzParallelThread, p_set_ex);
		if (rc)
		{
			printf("ERROR; return code from pthread_create() is %d\n", rc);
			exit(exit_pthread_create);
		}
		tidvec.push_back(*tid_ex);

//		std::copy(raw_set->Inclusion.begin(), raw_set->Inclusion.end(),
//				p_set_in->Inclusion.begin());
//		p_set_in->Inclusion.at(p_request->APInclusionEdges[i]) = false;
//
//		rc = pthread_create(tid_in, NULL, franzParallelThread, p_set_in);
//		if (rc) {
//			printf("ERROR; return code from pthread_create() is %d\n", rc);
//			exit(exit_pthread_create);
//		}
		raw_set->Inclusion.at(p_request->APInclusionEdges[i]) = false;
//		tidvec.push_back(*tid_in);

	}

	for (unsigned i = 0; i < p_request->APExlusionEdges.size(); i++)
	{
		pthread_t *tid_in; //*tid_ex
//		tid_ex = (pthread_t*) malloc(sizeof(pthread_t));
		tid_in = (pthread_t*) malloc(sizeof(pthread_t));
		InclusionExclusionSet *p_set_in = new InclusionExclusionSet(
				p_graph->edgeNum);
//		InclusionExclusionSet *p_set_ex = new InclusionExclusionSet(
//				p_graph->edgeNum);

		std::copy(raw_set->Inclusion.begin(), raw_set->Inclusion.end(),
				p_set_in->Inclusion.begin());
		std::copy(raw_set->Exlusion.begin(), raw_set->Exlusion.end(),
				p_set_in->Exlusion.begin());
		p_set_in->Inclusion.at(p_request->APExlusionEdges[i]) = false;
		rc = pthread_create(tid_in, NULL, franzParallelThread, p_set_in);
		if (rc)
		{
			printf("ERROR; return code from pthread_create() is %d\n", rc);
			exit(-1);
		}
		tidvec.push_back(*tid_in);

		raw_set->Exlusion.at(p_request->APExlusionEdges[i]) = false;
//		if ((i + 1) != p_request->APExlusionEdges.size()) {
//			std::copy(raw_set->Inclusion.begin(), raw_set->Inclusion.end(),
//					p_set_ex->Inclusion.begin());
//			p_set_ex->Exlusion.at(p_request->APExlusionEdges[i]) = false;
//
//			raw_set->Exlusion.at(p_request->APExlusionEdges[i]) = false;
//			rc = pthread_create(tid_ex, NULL, franzParallelThread, p_set_ex);
//			if (rc) {
//				printf("ERROR; return code from pthread_create() is %d\n", rc);
//				exit(-1);
//			}
//			tidvec.push_back(*tid_ex);
//		}

	}

	for (unsigned i = 0; i < tidvec.size(); i++)
	{
		pthread_join(tidvec.at(i), NULL);
	}

//	for (unsigned i = 0; i < tidvec.size(); i++){
//		free(&tidvec.at(i));
//	}
	delete raw_set;

}

//construct G* graph.set the capacity of every edges.
void BuildNetworkFlowGraph(GraphTopo *p_graph, Request *p_request)
{
	int AP = p_request->AP_PathEdge.size();
	int RLAP = p_request->RLAP_PathEdge.size();
	for (unsigned int i = 0; i < p_graph->getEdgeSize(); i++)
	{
		if (!p_request->BPMustNotPassEdgesRLAP[i])
		{
			p_request->edgeCapacity.at(i) = AP + 1;
		}
		if (p_request->BPMustNotPassEdgesRLAP[i]
				&& p_request->BPMustNotPassEdges4AP[i])
		{
			p_request->edgeCapacity.at(i) = (RLAP + 1) * (AP + 1);
		}
	}
	return;
}

//get the cut set.
void GetCutEdge(GraphTopo& graph, Request &request)
{
	set<int> SRLGset;
	for (unsigned int i = 0; i < graph.edgeNum; i++)
	{
		EdgeClass &e = graph.getithEdge(i);
		if ((1 == request.edgeCapacity[e.id])
				&& (true == request.STNodeCut[e.from])
				&& (false == request.STNodeCut[e.to]))
		{ //
			if ((true == request.APMustPassEdges[e.id])
					&& (true == request.APMustNotPassEdges[e.id]))
			{
				if (-1 == e.ithsrlg)
				{
					request.APInclusionEdges.push_back(e.id);
				}
				else
				{
					if (SRLGset.find(e.ithsrlg) == SRLGset.end())
					{
						request.APInclusionEdges.push_back(e.id);
						SRLGset.insert(e.ithsrlg);
					}
				}
//				request.APMustPassEdges[e.id] = false;
			}
		}
		if (((request.APHopSum + 1) == request.edgeCapacity[e.id])
				&& (true == request.STNodeCut[e.from])
				&& (false == request.STNodeCut[e.to]))
		{
//			if ((true == request.APMustPassEdges[e.id])
//					&& (true == request.APMustNotPassEdges[e.id])) {
//				request.APExlusionEdges.push_back(e.id);
//				request.APMustNotPassEdges[e.id] = false;
//			}
			int ithsrlg = e.ithsrlg;
			if (SRLGset.find(ithsrlg) != SRLGset.end())
			{
				continue;
			}
			for (unsigned int j = 0; j < request.AP_PathEdge.size(); j++)
			{
				int id = request.AP_PathEdge[j];
				if (ithsrlg == graph.getithEdge(id).ithsrlg)
				{
					if ((true == request.APMustPassEdges[e.id])
							&& (true == request.APMustNotPassEdges[e.id]))
					{
						request.APInclusionEdges.push_back(id);
						SRLGset.insert(ithsrlg);
//						request.APMustPassEdges[id] = false;
					}

				}
			}
			//positive cut.
//			request.APExlusionEdges.clear();

//			sort(request.APInclusionEdges.begin(),
//					request.APInclusionEdges.end());
//			vector<int>::iterator pos;
//			pos = unique(request.APInclusionEdges.begin(),
//					request.APInclusionEdges.end());
//			request.APInclusionEdges.erase(pos, request.APInclusionEdges.end());
		}
	}
#ifndef ConsolePrint
	cout << "Exlusion: " << endl;
	for (unsigned i = 0; i < request.APExlusionEdges.size(); i++)
	{
		int id = request.APExlusionEdges[i];
		cout << graph.nid_nindex[graph.getEdge(id).from] << "  "
		<< graph.nid_nindex[graph.getEdge(id).to] << endl;
	}
	cout << endl << "Inclusion: " << endl;
	for (unsigned i = 0; i < request.APInclusionEdges.size(); i++)
	{
		int id = request.APInclusionEdges[i];
		cout << graph.nid_nindex[graph.getEdge(id).from] << "  "
		<< graph.nid_nindex[graph.getEdge(id).to] << endl;
	}
#endif
}

//get confiliting srlg link edge set.
void GetConflictingSRLGLinkSet(GraphTopo *p_graph, Request *p_request)
{

	BuildNetworkFlowGraph(p_graph, p_request);
	MaxFlowAlgorithm_fordfulkerson((*p_graph), (*p_request));
	GetCutEdge((*p_graph), (*p_request));
}

void *
franzParallelThread(void *vargp)
{ //Graph *p_graph,Request *p_request
//	sem_wait(&mutex_thread);
//	pthread_mutex_lock(&mutex_thread);
//	num_thread++;
//	pthread_mutex_unlock(&mutex_thread);
//	sem_post(&mutex_thread);
	InclusionExclusionSet *p_set = (InclusionExclusionSet *) vargp;

	pthread_t tid = pthread_self();
#ifndef ConsolePrint
	cout << endl << "------------thread: " << tid << "------------------------"
	<< endl;
#endif
	//instead of waiting for another thread to perform PTHREAD_JOIN on it.

	//pthread_detach(tid);
	Request *p_request = new Request(p_graph->source, p_graph->destination,
			p_graph->edgeNum);

	//is value copy or reference copy?
	std::copy(p_set->Inclusion.begin(), p_set->Inclusion.end(),
			p_request->APMustPassEdges.begin());
	std::copy(p_set->Exlusion.begin(), p_set->Exlusion.end(),
			p_request->APMustNotPassEdges.begin());

	bool bool_existmustedge = false;
	for (unsigned int i = 0; i < p_set->Inclusion.size(); i++)
	{
		if (!p_request->APMustPassEdges.at(i))
		{
			bool_existmustedge = true;
		}
	}
	if (bool_existmustedge)
	{
		AlgorithmResult->IsParalle = true;
		if (algorithm_statisticParallelFranzAlgorithm == Algorithm)
		{
			return NULL;
		}
		bool succeed_findAP = false;
		if (MustNodePathAlgorithmPermuteDijkstra == FranzMustNodeAlgorithmType)
		{
			vector<int> permute;
			for (unsigned int i = 0; i < p_graph->edgeNum; i++)
			{
				if (!p_request->APMustPassEdges.at(i))
				{
					permute.push_back(i);
				}
			}
			if (reverseDFS_mustedgeoutnode(p_graph, p_request, permute))
			{
				if (findAP_interval_dijastra(p_graph, p_request, permute))
				{
//					cout<<"findAP_interval_dijastra　Suceed"<<endl;
					succeed_findAP = true;
				}
//				else {
//					cout<<"findAP_interval_dijastra　failure then use ILP"<<endl;
//					//succeed_findAP = findAP_ILP_gurobi(p_graph, p_request);
//				}
				if (succeed_findAP)
				{
					if (!findBP(p_graph, p_request))
					{
						GetConflictingSRLGLinkSet(p_graph, p_request);
						DivideAndConquer(p_request);
					}
				}
			}
		}
		if (MustNodePathAlgorithmGUROBI == FranzMustNodeAlgorithmType)
		{
			if (findAP_ILP_gurobi(p_graph, p_request))
			{
				if (!findBP(p_graph, p_request))
				{
					GetConflictingSRLGLinkSet(p_graph, p_request);
					DivideAndConquer(p_request);
				}
			}
		}

	}
	else
	{
		if (findAP_dijastra(p_graph, p_request))
		{
			if (!findBP(p_graph, p_request))
			{
				GetConflictingSRLGLinkSet(p_graph, p_request);
				DivideAndConquer(p_request);
			}
		}
	}

	delete p_set;
	delete p_request;
	//if find the result,in time arise the main pthread,let it continue to halt.
	if (franz_tid == tid)
	{
		pthread_mutex_lock(&franz_mutex);
		pthread_cond_signal(&franz_cond);
		pthread_mutex_unlock(&franz_mutex);
	}
	pthread_exit (NULL);
	return NULL;
}

//the main structure of our algorithm
bool FranzAlgorithmBasicFlows(GraphTopo *p_graph)
{

//	sem_init(&mutex_result, 0, 1);
	pthread_mutex_init(&mutex_result, NULL);
//	pthread_mutex_init(&mutex_thread, NULL);
//	sem_init(&mutex_thread, 0, 1);
	InclusionExclusionSet *innclusionexclusionset = new InclusionExclusionSet(
			p_graph->edgeNum);
	int rc;

	pthread_mutex_init(&franz_mutex, NULL);
	pthread_cond_init(&franz_cond, NULL);
	rc = pthread_create(&franz_tid, NULL, franzParallelThread,
			innclusionexclusionset);
	if (rc)
	{
		printf("ERROR; return code from pthread_create() is %d\n", rc);
		exit(exit_pthread_create);
	}

	struct timeval now;
	struct timespec outtime;
	gettimeofday(&now, NULL);
	outtime.tv_sec = now.tv_sec + LimitedTime;
	outtime.tv_nsec = now.tv_usec * 1000;
	//set that the main pthread just run for LimitedTime seconds ,beyond the time,terminate other pthreads.
	pthread_mutex_lock(&franz_mutex);
	pthread_cond_timedwait(&franz_cond, &franz_mutex, &outtime);
	pthread_mutex_unlock(&franz_mutex);

	pthread_mutex_destroy(&franz_mutex);
	pthread_cond_destroy(&franz_cond);

	if (AlgorithmResult->SolutionNotFeasible)
	{
		return false;
	}
	else
	{
		return true;
	}
}

