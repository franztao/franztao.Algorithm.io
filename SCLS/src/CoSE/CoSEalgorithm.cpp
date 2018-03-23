#include "../head.h"
#include"../route.h"

extern GraphTopo *p_graph;
extern DisjointPathPair *AlgorithmResult;

//sem_t mutex_thread;

pthread_cond_t cose_cond;
pthread_mutex_t cose_mutex;
pthread_t cose_tid;

//bool finishparallel_getresult;

void *CoSEParallelThread(void *vargp);
void CoSEDivideAndConquer(Request *p_request) {

	int rc;
	vector<pthread_t> tidvec;
	InclusionExclusionSet *raw_set = new InclusionExclusionSet(
			p_graph->srlgGroupsNum);

	std::copy(p_request->APMustPassSRLGs.begin(),
			p_request->APMustPassSRLGs.end(), raw_set->Inclusion.begin());
	std::copy(p_request->APMustNotPassSRLGs.begin(),
			p_request->APMustNotPassSRLGs.end(), raw_set->Exlusion.begin());

	for (unsigned i = 0; i < p_request->APInclusionEdges.size(); i++) {
		pthread_t *tid_ex; //, *tid_in;
		tid_ex = (pthread_t*) malloc(sizeof(pthread_t));
//		tid_in = (pthread_t*) malloc(sizeof(pthread_t));
//		InclusionExclusionSet *p_set_in = new InclusionExclusionSet(
//				p_graph->srlgGroupsNum);
		InclusionExclusionSet *p_set_ex = new InclusionExclusionSet(
				p_graph->srlgGroupsNum);

		std::copy(raw_set->Inclusion.begin(), raw_set->Inclusion.end(),
				p_set_ex->Inclusion.begin());
		std::copy(raw_set->Exlusion.begin(), raw_set->Exlusion.end(),
				p_set_ex->Exlusion.begin());
		p_set_ex->Exlusion.at(p_request->APInclusionEdges[i]) = false; //

		rc = pthread_create(tid_ex, NULL, CoSEParallelThread, p_set_ex);
		if (rc) {
			printf("ERROR; return code from pthread_create() is %d\n", rc);
			exit(exit_pthread_create);
		}
		tidvec.push_back(*tid_ex);

		raw_set->Inclusion.at(p_request->APInclusionEdges[i]) = false; //

	}

	for (unsigned i = 0; i < tidvec.size(); i++) {
		pthread_join(tidvec.at(i), NULL);
	}

	delete raw_set;

}
class PATHsrlg {
public:
	vector<int> srlg;
};
bool addnewPaths(GraphTopo *p_graph, Request *p_request, PATHsrlg &psrlg) {
	typedef pair<int, int> P;
	vector<int> dist((*p_graph).nodeNum, (-1));
	vector<int> hop((*p_graph).nodeNum, (-1));
	vector<int> path_node((*p_graph).nodeNum, (-1));
	vector<int> path_edge((*p_graph).nodeNum, (-1));
	priority_queue<P, vector<P>, greater<P> > que;
	unsigned int len;
	dist[(*p_graph).source] = 0;
	hop[(*p_graph).source] = 0;
	que.push(P(0, (*p_graph).source));
	while (!que.empty()) {
		P p = que.top();
		que.pop();
		int v = p.second;
		if (dist[v] < p.first)
			continue;
		len = (*p_graph).ftopo_r_Node_c_EdgeList[v].edgeList.size();
		for (unsigned int i = 0; i < len; i++) {
			EdgeClass &e = p_graph->getithEdge(
					(*p_graph).ftopo_r_Node_c_EdgeList[v].edgeList[i]);
			if (0 == (p_request->APMustNotPassEdges.size())) {
				cout << "p_request->APedgestabu is " << "NULL" << endl;
			}
			if (!p_request->APMustNotPassEdges[e.id])
				continue;
			if (-1 == dist[e.to]) {
				dist[e.to] = dist[v] + e.cost;
				hop[e.to] = hop[v] + 1;
				path_node[e.to] = v;
				path_edge[e.to] = e.id;
				que.push(P(dist[e.to], e.to));
			} else {
				if (dist[e.to] > (dist[v] + e.cost)) {
					dist[e.to] = dist[v] + e.cost;
					hop[e.to] = hop[v] + 1;
					path_node[e.to] = v;
					path_edge[e.to] = e.id;
					que.push(P(dist[e.to], e.to));
				} else {
					if (dist[e.to] == (dist[v] + e.cost)) {
						if (hop[e.to] > (hop[v] + 1)) {
							hop[e.to] = hop[v] + 1;
							path_node[e.to] = v;
							path_edge[e.to] = e.id;
							que.push(P(dist[e.to], e.to));
						}
					}
				}
			}
		}
	}
	if (-1 == dist[(*p_graph).destination]) {
		return false;
	}

	int now;
	int next;
	int srlgi;

	now = p_graph->destination;
	next = path_node[now];
	while (next != -1) {
		srlgi = p_graph->getithEdge(path_edge[now]).ithsrlg;
		if (-1 != srlgi)
			psrlg.srlg.push_back(srlgi);
		now = next;
		next = path_node[now];
	}
	sort(psrlg.srlg.begin(), psrlg.srlg.end());
	vector<int>::iterator pos;
	pos = unique(psrlg.srlg.begin(), psrlg.srlg.end());
	psrlg.srlg.erase(pos, psrlg.srlg.end());
//	paths_L.push_back(psrlg);
	return true;
}
void GetConflictingSRLGSet(GraphTopo *p_graph, Request *p_request) {
	vector<int> conflictingSRLGset_T;
	//p_request->APInclusionEdges.size()
//	vector<int>path;
	vector<PATHsrlg> paths_L;
	PATHsrlg pathsrlg;
	for (unsigned i = 0; i < p_request->APSrlgs.size(); i++) {
		pathsrlg.srlg.push_back(p_request->APSrlgs.at(i));
	}
	paths_L.push_back(pathsrlg);
	unsigned int firsti = 0;
	while (true) {
		int srlg;

		bool belongingall;
		if (firsti >= paths_L.at(0).srlg.size()) {
			break;
		}
		while (true) {
			if (firsti >= paths_L.at(0).srlg.size()) {
				belongingall = false;
				break;
			}
			belongingall = true;
			srlg = paths_L.at(0).srlg.at(firsti);
			firsti++;
			if (paths_L.size() >= 1) {
				for (unsigned i = 1; i < paths_L.size(); i++) {
					unsigned j;
					for (j = 0; j < paths_L.at(i).srlg.size(); j++) {
						if (srlg == paths_L.at(i).srlg.at(j)) {
							break;
						}
					}
					if (j == paths_L.at(i).srlg.size()) {
						belongingall = false;
						break;
					}
				}
			}
			if (belongingall)
				break;
		}
		if (belongingall) {
			conflictingSRLGset_T.push_back(srlg);
			for (unsigned i = 0;
					i < p_graph->srlgGroups.at(srlg).srlgMember.size(); i++) {
				p_request->APMustNotPassEdges.at(
						p_graph->srlgGroups.at(srlg).srlgMember.at(i)) = false;
			}
			PATHsrlg psrlg;
			if (!addnewPaths(p_graph, p_request, psrlg)) {
				break;
			}
			paths_L.push_back(psrlg);
		}
	}

//	cout << "ssssssss------" << endl;
//	for (unsigned i = 0; i < paths_L.size(); i++) {
//		for (unsigned j = 0; j < paths_L.at(i).srlg.size(); j++) {
//			cout << " " << paths_L.at(i).srlg.at(j);
//		}
//		cout << endl;
//	}
//	cout << "dddddddddddd------" << endl;

	p_request->APInclusionEdges.clear();
	for (unsigned i = 0; i < conflictingSRLGset_T.size(); i++) {
//		cout << "conflictingSRLGset_T" << " " << conflictingSRLGset_T.at(i)
//				<< endl;
//		for(unsigned j=0;j<p_graph->srlgGroups.at(conflictingSRLGset_T.at(i)).srlgMember.size();j++){
//			int edgede=p_graph->srlgGroups.at(conflictingSRLGset_T.at(i)).srlgMember.at(j);
//			cout<<"  ("<<edgede<<"):"<<p_graph->nid_nindex[p_graph->getithEdge(edgede).from]<<"-->"<<p_graph->nid_nindex[p_graph->getithEdge(edgede).to];
//		}
//		cout<<endl;
//		cout<<endl;
		int srlg = conflictingSRLGset_T.at(i);
		if ((!p_request->APMustPassSRLGs.at(srlg))
				|| (!p_request->APMustNotPassSRLGs.at(srlg)))
			continue;
		p_request->APInclusionEdges.push_back(conflictingSRLGset_T.at(i));
	}
	return;
}
void recursivePermute(vector<int> &SRLGset, unsigned int index,
		vector<int> permute, GraphTopo *p_graph, Request *p_request) {
	if (index == SRLGset.size()) {
		if (reverseDFS_mustedgeoutnode(p_graph, p_request, permute)) {
			findAP_interval_dijastra(p_graph, p_request, permute);
		}
		return;
	} else {
		for (unsigned int i = 0;
				i < p_graph->srlgGroups.at(SRLGset.at(index)).srlgMembersNum;
				i++) {
			int edge = p_graph->srlgGroups.at(SRLGset.at(index)).srlgMember.at(
					i);
			vector<int> newPermute;
			for (unsigned int i = 0; i < permute.size(); i++){
				newPermute.push_back(i);
			}
//			std::copy(permute.begin(), permute.end(), newPermute.begin());
			newPermute.push_back(edge);
			recursivePermute(SRLGset, index + 1, newPermute, p_graph,
					p_request);
		}
		permute.clear();
	}
	return;
}
void *CoSEParallelThread(void *vargp) { //Graph *p_graph,Request *p_request
	pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL); //允许退出线程
	pthread_setcanceltype(PTHREAD_CANCEL_ASYNCHRONOUS, NULL); //设置立即取消
	InclusionExclusionSet *p_set = (InclusionExclusionSet *) vargp;
	pthread_t tid = pthread_self();
	//instead of waiting for another thread to perform PTHREAD_JOIN on it.

	//pthread_detach(tid);
	Request *p_request = new Request(p_graph->source, p_graph->destination,
			p_graph->edgeNum);

	p_request->initSRLG(p_graph->srlgGroupsNum);

	std::copy(p_set->Inclusion.begin(), p_set->Inclusion.end(),
			p_request->APMustPassSRLGs.begin());
	std::copy(p_set->Exlusion.begin(), p_set->Exlusion.end(),
			p_request->APMustNotPassSRLGs.begin());

	bool existmustedge = false;
	for (unsigned int i = 0; i < p_set->Inclusion.size(); i++) {
		if (!p_request->APMustPassSRLGs.at(i)) {
			existmustedge = true;
		}
	}

	for (unsigned i = 0; i < p_graph->edgeNum; i++) {
		int srlg = p_graph->getithEdge(i).ithsrlg;
		if (!(p_request->APMustPassSRLGs.at(srlg))) {
			p_request->APMustPassEdges.at(i) = false;
		}
		if (!(p_request->APMustNotPassSRLGs.at(srlg))) {
			p_request->APMustNotPassEdges.at(i) = false;
		}
	}

	if (existmustedge) {
		bool succeed_findAP = false;
		if (CoSEMustNodeAlgorithmType == MustNodePathAlgorithmPermuteDijkstra) {
			vector<int> SRLGset;
			for (unsigned int i = 0; i < p_graph->srlgGroups.size(); i++) {
				if (!p_request->APMustPassSRLGs.at(i)) {
					SRLGset.push_back(i);
				}
			}
			vector<int> initPermute;
			recursivePermute(SRLGset, 0, initPermute, p_graph, p_request);
//			cout << "SRLGset taotao" << SRLGset.size() << endl;

			if (0 != p_request->APCostSum) {
				succeed_findAP = true;
				if (!findBP(p_graph, p_request)) {
					GetConflictingSRLGSet(p_graph, p_request);
					CoSEDivideAndConquer(p_request);
				}
			}
		}

		if (CoSEMustNodeAlgorithmType == MustNodePathAlgorithmGUROBI) {
			succeed_findAP = CoSE_findAP_ILP_gurobi(p_graph, p_request);
			if (succeed_findAP) {
				if (!findBP(p_graph, p_request)) {
					GetConflictingSRLGSet(p_graph, p_request);
					CoSEDivideAndConquer(p_request);
				}
			}
		}

	} else {
		if (CoSE_findAP_dijastra(p_graph, p_request)) {
			if (!findBP(p_graph, p_request)) {
				GetConflictingSRLGSet(p_graph, p_request);
				CoSEDivideAndConquer(p_request);
			}
		}
	}
	delete p_set;
	delete p_request;
	//once find the result,in time arise the main pthread,let it continue to halt.
	if (cose_tid == tid) {
		pthread_mutex_lock(&cose_mutex);
//		finishparallel_getresult = true;
		AlgorithmResult->SolutionisOptimalFeasible = true;
		pthread_cond_signal(&cose_cond);

		pthread_mutex_unlock(&cose_mutex);
	}
	pthread_exit(NULL);
	return NULL;
}

//the main structure of our algorithm
bool CoSEAlgorithmBasicFlows(GraphTopo  *p_graph) {
	p_graph->DesignAllEdgtoHaveSRLG();
//	finishparallel_getresult = false;
	InclusionExclusionSet *innclusionexclusionset = new InclusionExclusionSet(
			p_graph->srlgGroupsNum);
	int rc;

	//parallel run the COSE algorithm.
	pthread_mutex_init(&cose_mutex, NULL);
	pthread_cond_init(&cose_cond, NULL);
	rc = pthread_create(&cose_tid, NULL, CoSEParallelThread,
			innclusionexclusionset);
	if (rc) {
		printf(
				"ERROR:CoSEAlgorithmBasicFlows return code from pthread_create() is %d\n",
				rc);
		exit(exit_pthread_create);
	}

	//limit time
	struct timeval now;
	struct timespec outtime;
	gettimeofday(&now, NULL);
	outtime.tv_sec = now.tv_sec + LimitedTime;
	outtime.tv_nsec = now.tv_usec * 1000;

	//set that the main pthread just run for LimitedTime seconds ,beyond the time,terminate other pthreads.
	pthread_mutex_lock(&cose_mutex);
	pthread_cond_timedwait(&cose_cond, &cose_mutex, &outtime);
	pthread_mutex_unlock(&cose_mutex);

	pthread_mutex_destroy(&cose_mutex);
	pthread_cond_destroy(&cose_cond);
	//get the result then return true.
	if (AlgorithmResult->SolutionNotFeasible) { //!finishparallel_getresult) {
		return false;
	} else {
		return true;
	}
}
