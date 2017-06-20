#include "deploy.h"
#include "cdn.h"
#include <signal.h>
#include <unistd.h>
using namespace std;
int numberM = 0;
GraphTopo G;
TabuSearchClass B;
int lastBestServer[MAX_N];
int k = 0;
//
#define smallNodeNum 200
#define midlleNodeNum 600
#include "another.h"
/*
 1. 两个网络节点之间最多仅存在一条链路，链路上下行方向的网络总带宽相互独立，并且上下行方向的总带宽与网络租用费相同。例如对于网络节点A与B之间的链路，该条链路上的总带宽为10Gbps，单位租用费为1K/Gbps，则表示A->B、B->A两个方向上的网络总带宽分别为10Gbps，并且租用费均为1K/Gbps。如果某条数据流在该链路A->B方向的占用带宽为3Gbps，那么该数据流在该链路的租用费为3K，并且该链路A->B方向的剩余可用带宽为7Gbps。而B->A方向的剩余可用带宽不受该数据流的影响，仍为10Gbps。
 2. 每个网络节点最多仅能连接一个消费节点，每个消费节点仅能连接一个网络节点。消费节点与连接的网络节点之间的链路总带宽无限大，并且网络租用费为零。
 3. 网络节点数量不超过1000个，每个节点的链路数量不超过20条，消费节点的数量不超过500个。
 4. 链路总带宽与网络租用费为[0, 100]的整数，视频内容服务器部署成本与消费节点的视频带宽消耗需求为[0,5000]的整数。
 5. 部署方案中，网络路径上的占用带宽必须为整数。
 6. “满足消费节点的带宽消耗需求”是指输出给消费节点的带宽总和不得小于该消费节点的视频带宽消耗需求。
 7. 每个网络节点上最多仅可部署一台视频内容服务器。
 */

#define MAX_NODE_franz 5005

//double pagerank[MAX_NODE][MAX_NODE];
//const double r = 0.15;
//const unsigned pageRankIteration = 50; //迭代次数
//const double delta4PageRank = 0.000001;

unsigned int augmentnetworkNode;
unsigned int networkNode;
unsigned int networkLink;

unsigned long deployCost;
const unsigned long maximumDeployCost = 10000;

unsigned int consumeNode;
unsigned long consumeNodeDemand[MAX_NODE_franz];
unsigned long consumeNodeDemandSum;
unsigned int networkNode2consumeNode[MAX_NODE_franz];

unsigned int startNode;
unsigned int secendNode;
unsigned int endNode;

bool timeout;
void sig_handler(int num) //超时处理
		{
	timeout = true;
}

struct edge_franz {
	unsigned int fromID;
	unsigned int toID;
	unsigned long cap;
	long cost;
	unsigned long deployCost;
	unsigned int revIndex;
	long flow;

//	unsigned long cap4PageRank;
//	unsigned long cost4Pagerank;

//	edge *next, *pair;

};
int *final_server;
vector<edge_franz> globalGraph[MAX_NODE_franz];

#define MAX_EDGE (2005*40)
#define inf INT_MAX
struct MCMF {
	struct tedge {
		int from, to, cap, cost, next;
//		unsigned id;
	} edgeList[MAX_EDGE];
	int numEdgeZKW, node2edgeIndex[MAX_EDGE];
	bool isVis[MAX_EDGE];
	int d[MAX_EDGE], currentMinimunCost_zkw, pathCostSum;
	//augmentnetworkNode;
	// startNode, endNode

	MCMF& operator =(MCMF& globalzkw)
	//注意：此处一定要返回对象的引用，否则返回后其值立即消失！
			{
//		this->augmentnetworkNode = a.augmentnetworkNode;
		this->numEdgeZKW = globalzkw.numEdgeZKW;
		this->pathCostSum = this->currentMinimunCost_zkw = 0;
		for (unsigned int i = 0; i < augmentnetworkNode; i++) {
			this->node2edgeIndex[i] = globalzkw.node2edgeIndex[i];
		}
		for (int i = 0; i < this->numEdgeZKW; i++) {
			this->edgeList[i].from = globalzkw.edgeList[i].from;
			this->edgeList[i].to = globalzkw.edgeList[i].to;
			this->edgeList[i].cap = globalzkw.edgeList[i].cap;
			this->edgeList[i].cost = globalzkw.edgeList[i].cost;
			this->edgeList[i].next = globalzkw.edgeList[i].next;

		}
		return *this;
	}
	void init(int n) {
//		this->augmentnetworkNode = n;
		numEdgeZKW = 0;
		currentMinimunCost_zkw = -1;
		pathCostSum = 0;
		memset(node2edgeIndex, -1, sizeof(node2edgeIndex));
		return;
	}

	inline void addedge(int u, int v, int cap, int w) {
		edgeList[numEdgeZKW] = (tedge ) { u, v, cap, w, node2edgeIndex[u] };
		node2edgeIndex[u] = numEdgeZKW++;
		edgeList[numEdgeZKW] = (tedge ) { v, u, 0, -w, node2edgeIndex[v] };
		node2edgeIndex[v] = numEdgeZKW++;
		return;
	}
	inline int aug(int u, int flow) {
		if (u == endNode) {
			if (-1 == currentMinimunCost_zkw) {
				currentMinimunCost_zkw = pathCostSum * flow;
			} else
				currentMinimunCost_zkw += pathCostSum * flow;
			return flow;
		}
		int tmp = flow;
		isVis[u] = true;
		for (int i = node2edgeIndex[u]; i != -1; i = edgeList[i].next) {
			int v = edgeList[i].to;
			if (edgeList[i].cap && !edgeList[i].cost && !isVis[v]) {
				int currentaugmentflow = aug(v,
						tmp < edgeList[i].cap ? tmp : edgeList[i].cap);
				edgeList[i].cap -= currentaugmentflow;
				edgeList[i ^ 1].cap += currentaugmentflow;
				tmp -= currentaugmentflow;
				if (!tmp)
					return flow;
			}
		}
		return flow - tmp;
	}
	inline bool spfa() {
		for (unsigned int i = 0; i < augmentnetworkNode; i++)
			d[i] = inf;
		static deque<int> duilie_Q;
		duilie_Q.push_back(endNode);
		d[endNode] = 0;
		while (!duilie_Q.empty()) {
			int u = duilie_Q.front(), tmp;
			duilie_Q.pop_front();
			for (int i = node2edgeIndex[u]; i != -1; i = edgeList[i].next)
				if (edgeList[i ^ 1].cap
						&& (tmp = d[u] - edgeList[i].cost) < d[edgeList[i].to])
					(d[edgeList[i].to] = tmp)
							<= d[duilie_Q.empty() ? startNode : duilie_Q.front()]
							?duilie_Q.push_front(edgeList[i].to) :
							duilie_Q.push_back(edgeList[i].to);
		}
		for (unsigned int u = 0; u < augmentnetworkNode; u++)
			for (int i = node2edgeIndex[u]; i != -1; i = edgeList[i].next)
				edgeList[i].cost += d[edgeList[i].to] - d[u];
		pathCostSum += d[startNode];
		return d[startNode] < inf;
	}
	int costflow(int S, int T, int flow) {
		//		cout << S << " " << T << " " << flow << endl;
//				this->starteNode = S;
//				this->endNode = T;
		while (spfa()) {
			do {
				memset(isVis, false, sizeof(isVis));
			} while (aug(S, inf));
		}
		return currentMinimunCost_zkw;
	}
} globalZKWGraph;

void print_time_franz(const char *head) {
#ifdef _DEBUG
	struct timeb rawtime;
	struct tm * timeinfo;
	ftime(&rawtime);
	timeinfo = localtime(&rawtime.time);

	static int ms = rawtime.millitm;
	static unsigned long s = rawtime.time;
	int out_ms = rawtime.millitm - ms;
	unsigned long out_s = rawtime.time - s;
	ms = rawtime.millitm;
	s = rawtime.time;

	if (out_ms < 0) {
		out_ms += 1000;
		out_s -= 1;
	}
	// printf("%s date/time is: %s \tused time is %lu s %d ms.\n", head, asctime(timeinfo), out_s, out_ms);
	if (strcmp(head, ""))
	printf("%s used time is %lf ms.\n", head, (1000.0 * out_s + out_ms));
#endif
}
void add_edge(unsigned from, unsigned to, unsigned long cap, unsigned long cost,
		unsigned long deploy) {
	edge_franz ein;
	ein.fromID = from;
	ein.toID = to;
	ein.cap = cap;
//	ein.cap4PageRank = cap;
	ein.cost = cost;
//	ein.cost4Pagerank = cost;
	ein.deployCost = deploy;
	ein.revIndex = globalGraph[to].size();
	ein.flow = 0;
	globalGraph[from].push_back(ein);

	edge_franz eout;
	eout.fromID = to;
	eout.toID = from;
	eout.cap = 0;
//	eout.cap4PageRank = cap;
	eout.cost = -1 * cost;
//	eout.cost4Pagerank = cost;
	eout.deployCost = deploy;
	eout.revIndex = globalGraph[from].size() - 1;
	eout.flow = 0;
	globalGraph[to].push_back(eout);

//	int capvalue;
//	if (ULONG_MAX == cap) {
//		capvalue = INT_MAX;
//	} else {
//		capvalue = cap;
//	}
//
//	localsol.addedge_path(from, to, capvalue, to, localgraph[from].size() - 1,
//			localgraph[to].size() - 1);
}

typedef pair<unsigned long, unsigned> P;
long minimumCostFlow_potention(unsigned start, unsigned end,
		unsigned long currentAugmentFlow) {
	unsigned long miniumCost = 0;
	unsigned long potention[MAX_NODE_franz];
	unsigned long distance[MAX_NODE_franz];
	unsigned prevv[MAX_NODE_franz], preve[MAX_NODE_franz];

	fill(potention, potention + MAX_NODE_franz, 0);
//	for(unsigned i=0;i<augmentnetworkNode;i++){
//		for(unsigned j=0;j<globalGraph[i].size();j++){
//			globalGraph[i].at(j).flow=0;
//		}
//	}
	while (0 < currentAugmentFlow) {
		priority_queue<P, vector<P>, greater<P> > que;
		fill(distance, distance + MAX_NODE_franz, ULONG_MAX);
		distance[start] = 0;
		que.push(P(0, start));
		while (!que.empty()) {
			P p = que.top();
			que.pop();
			unsigned v = p.second;
			if (ULONG_MAX == distance[v])
				continue;
			if (distance[v] < p.first)
				continue;
			for (unsigned i = 0; i < globalGraph[v].size(); i++) {
				edge_franz &e = globalGraph[v][i];
				if ((0 < e.cap)
						&& (distance[e.toID]
								> (distance[v] + e.cost + potention[v]
										- potention[e.toID]))) {
					distance[e.toID] = (distance[v] + e.cost + potention[v]
							- potention[e.toID]);
					prevv[e.toID] = v;
					preve[e.toID] = i;
					que.push(P(distance[e.toID], e.toID));

				}
			}
		}
		if (ULONG_MAX == distance[end]) {
			return -1;
		}
		for (unsigned v = 0; v < MAX_NODE_franz; v++) {
			potention[v] += distance[v];
		}
		unsigned long d = currentAugmentFlow;
		for (unsigned v = end; v != start; v = prevv[v]) {
			if (d > globalGraph[prevv[v]][preve[v]].cap)
				d = globalGraph[prevv[v]][preve[v]].cap;

		}
		if (currentAugmentFlow < d) {
			d = currentAugmentFlow;
			currentAugmentFlow = 0;
		} else
			currentAugmentFlow -= d;
		miniumCost += d * potention[end];
		for (unsigned v = end; v != start; v = prevv[v]) {
			edge_franz &e = globalGraph[prevv[v]][preve[v]];
			e.cap -= d;
			e.flow += d;
			globalGraph[v][e.revIndex].cap += d;
			globalGraph[v][e.revIndex].flow -= d;
		}
	}
	return miniumCost;
}

long minimumCostFlow(unsigned start, unsigned end,
		unsigned long currentAugmentFlow) {
	unsigned long miniumCost = 0;
	unsigned long distance[MAX_NODE_franz];
	unsigned prevv[MAX_NODE_franz], preve[MAX_NODE_franz];

	while (0 < currentAugmentFlow) {
		fill(distance, distance + MAX_NODE_franz, ULONG_MAX);
		distance[start] = 0;
		bool isUpdate = true;
		while (isUpdate) {
			isUpdate = false;
			for (unsigned int v = 0; v < augmentnetworkNode; v++) {
				if (ULONG_MAX == distance[v])
					continue;
				for (unsigned int i = 0; i < globalGraph[v].size(); i++) {
					edge_franz &e = globalGraph[v][i];
					unsigned long midcost = distance[v] + e.cost; // + e.deployCost / 3
					if ((0 < e.cap) && (distance[e.toID] > midcost)) {
						distance[e.toID] = midcost;
						prevv[e.toID] = v;
						preve[e.toID] = i;
						isUpdate = true;
					}
				}
			}

		}
		if (ULONG_MAX == distance[end]) {
			return -1;
		}

		unsigned int d = currentAugmentFlow;
		for (unsigned v = end; v != start; v = prevv[v]) {
			if (d > globalGraph[prevv[v]][preve[v]].cap)
				d = globalGraph[prevv[v]][preve[v]].cap;

		}
		if (currentAugmentFlow < d) {
			d = currentAugmentFlow;
			currentAugmentFlow = 0;

		} else
			currentAugmentFlow -= d;
		miniumCost += d * distance[end];
		for (unsigned v = end; v != start; v = prevv[v]) {
			edge_franz &e = globalGraph[prevv[v]][preve[v]];
			e.cap -= d;
			e.flow += d;
			globalGraph[v][e.revIndex].cap += d;
			globalGraph[v][e.revIndex].flow -= d;
		}
	}
	return miniumCost;
}

struct Path {
	vector<unsigned> pathnode;
	unsigned int cap;
};

void augmentPath(unsigned start, unsigned end, unsigned long currentAugmentFlow,
		string & reslutstring) {
	unsigned int numPath = 0;
	vector<Path> pathset;

	long distance[MAX_NODE_franz];
	unsigned prevv[MAX_NODE_franz], preve[MAX_NODE_franz];
	while (currentAugmentFlow > 0) {
		fill(distance, distance + MAX_NODE_franz, LONG_MAX);
		distance[start] = 0;
		bool isUpdate = true;
		while (isUpdate) {
			isUpdate = false;
			for (unsigned int v = 0; v < augmentnetworkNode; v++) {
				if (LONG_MAX == distance[v])
					continue;
				for (unsigned int i = 0; i < globalGraph[v].size(); i++) {
					edge_franz &e = globalGraph[v][i];
					long midcost = distance[v] + e.cost; // + e.deployCost / 3
					if ((0 < e.flow) && (distance[e.toID] > midcost)) {
						distance[e.toID] = midcost;
						prevv[e.toID] = v;
						preve[e.toID] = i;
						isUpdate = true;
					}
				}
			}
		}
		if (LONG_MAX == distance[end]) {
			cout << "wrong:augmentPath" << endl;
			return;
		}

		unsigned long d = currentAugmentFlow;
		for (unsigned v = end; v != start; v = prevv[v]) {
			unsigned long absvalue = labs(globalGraph[prevv[v]][preve[v]].flow);
			if (d > absvalue)
				d = absvalue;
		}
		currentAugmentFlow -= d;

		Path p;
		numPath++;
		p.cap = d;

		int mid = 0;
		for (unsigned v = end; v != start; v = prevv[v]) {
			edge_franz &e = globalGraph[prevv[v]][preve[v]];
			e.flow -= d;
			if (mid >= 1) {
				p.pathnode.push_back(v);
			}
			mid++;
		}
		p.pathnode.at(p.pathnode.size() - 1) = p.pathnode.at(
				p.pathnode.size() - 1) - networkNode;
		pathset.push_back(p);
	}
	unsigned stri = 0;

//	cout << "augmentPathend" << endl;
	char midstr[100];
	sprintf(midstr, "%u", numPath);
	for (unsigned i = 0; i < strlen(midstr); i++) {
		reslutstring.push_back(midstr[i]);
//		reslutfile[stri] = midstr[i];
		stri++;
	}
	reslutstring.push_back('\n');
//	reslutfile[stri] = '\n';
	stri++;

	reslutstring.push_back('\n');
//	reslutfile[stri] = '\n';
	stri++;

	for (unsigned i = 0; i < pathset.size(); i++) {
		Path p = pathset.at(i);
		for (unsigned j = 0; j < p.pathnode.size(); j++) {
			char str[100];
			sprintf(str, "%u", (p.pathnode.at(j)));
			for (unsigned k = 0; k < strlen(str); k++) {

				reslutstring.push_back(str[k]);
//				reslutfile[stri] = str[k];
				stri++;
			}
			reslutstring.push_back(' ');
//			reslutfile[stri] = ' ';
			stri++;
		}

		char capstr[100];
		sprintf(capstr, "%u", p.cap);
//		cout<<"capstr"<<capstr<<endl;
		for (unsigned j = 0; j < strlen(capstr); j++) {
			reslutstring.push_back(capstr[j]);
//			reslutfile[stri] = capstr[j];
			stri++;
		}
		reslutstring.push_back('\n');
//		reslutfile[stri] = '\n';
		stri++;

	}
//	for (unsigned i = 0; i < stri; i++) {
//		printf("%c", reslutfile[i]);
//	}
//	19 // 注：共输出19条网络路径
//
//	0 9 11 1 13 // 注：起始网络节点ID为0，经由ID为9、11等网络节点到达消费节点1，占用带宽为13
//	0 7 10 10
//	0 8 0 36
//	0 6 5 8 13
	return;
}

void readData(char * topo[MAX_EDGE_NUM], int line_num) {

	unsigned i = 0;

	//网络节点数量 网络链路数量 消费节点数量
	networkNode = 0;
	for (; i < strlen(topo[0]); i++) {
		if (('9' >= topo[0][i]) && ('0' <= topo[0][i])) {
			networkNode *= 10;
			networkNode += (topo[0][i] - '0');
		} else
			break;
	}
	i++;
	networkLink = 0;
	for (; i < strlen(topo[0]); i++) {
		if (('9' >= topo[0][i]) && ('0' <= topo[0][i])) {
			networkLink *= 10;
			networkLink += (topo[0][i] - '0');
		} else
			break;
	}

	i++;
	consumeNode = 0;
	for (; i < strlen(topo[0]); i++) {
		if (('9' >= topo[0][i]) && ('0' <= topo[0][i])) {
			consumeNode *= 10;
			consumeNode += (topo[0][i] - '0');
		} else
			break;
	}

	globalZKWGraph.init((networkNode + consumeNode + 3));
	//视频内容服务器部署成本
	i = 0;
	deployCost = 0;
	for (; i < strlen(topo[2]); i++) {
		if (('9' >= topo[2][i]) && ('0' <= topo[2][i])) {
			deployCost *= 10;
			deployCost += (topo[2][i] - '0');
		} else
			break;
	}

	//链路起始节点ID 链路终止节点ID 总带宽大小 单位网络租用费
	i = 4;
	for (; i < (networkLink + 4); i++) {
		unsigned j = 0;
		unsigned s = 0;
		for (; j < strlen(topo[i]); j++) {
			if (('9' >= topo[i][j]) && ('0' <= topo[i][j])) {
				s *= 10;
				s += (topo[i][j] - '0');
			} else
				break;
		}
		j++;
		unsigned t = 0;
		for (; j < strlen(topo[i]); j++) {
			if (('9' >= topo[i][j]) && ('0' <= topo[i][j])) {
				t *= 10;
				t += (topo[i][j] - '0');
			} else
				break;
		}
		j++;
		unsigned cap = 0;
		for (; j < strlen(topo[i]); j++) {
			if (('9' >= topo[i][j]) && ('0' <= topo[i][j])) {
				cap *= 10;
				cap += (topo[i][j] - '0');
			} else
				break;
		}
		j++;
		unsigned unitcost = 0;
		for (; j < strlen(topo[i]); j++) {
			if (('9' >= topo[i][j]) && ('0' <= topo[i][j])) {
				unitcost *= 10;
				unitcost += (topo[i][j] - '0');
			} else
				break;
		}

//void add_edge(unsigned from, unsigned to, unsigned cap, unsigned cost,unsigned deploy) {
		add_edge(s, t, cap, unitcost, 0);
		add_edge(t, s, cap, unitcost, 0);

		globalZKWGraph.addedge(s, t, cap, unitcost);
		globalZKWGraph.addedge(t, s, cap, unitcost);
	}

	//消费节点ID 相连网络节点ID 视频带宽消耗需求
	i = networkLink + 5;
	consumeNodeDemandSum = 0;
	for (unsigned j = 0; j < networkNode; j++)
		networkNode2consumeNode[j] = networkNode;
	for (; i < (networkLink + consumeNode + 5); i++) {
		unsigned j = 0;
		unsigned s = 0;
		for (; j < strlen(topo[i]); j++) {
			if (('9' >= topo[i][j]) && ('0' <= topo[i][j])) {
				s *= 10;
				s += (topo[i][j] - '0');
			} else
				break;
		}
		j++;
		unsigned t = 0;
		for (; j < strlen(topo[i]); j++) {
			if (('9' >= topo[i][j]) && ('0' <= topo[i][j])) {
				t *= 10;
				t += (topo[i][j] - '0');
			} else
				break;
		}
		j++;
		unsigned demand = 0;
		for (; j < strlen(topo[i]); j++) {
			if (('9' >= topo[i][j]) && ('0' <= topo[i][j])) {
				demand *= 10;
				demand += (topo[i][j] - '0');
			} else
				break;
		}
//		cout<<i<<" "<<s<<" "<<t<<" "<<demand<<endl;
//		add_edge(s, t, cap, unitcost, 0);
		consumeNodeDemandSum += demand;
		consumeNodeDemand[s] = demand;
		networkNode2consumeNode[t] = s;
		add_edge(s + networkNode, t, demand, 0, 0);
//		add_edge(t, s + networkNode, demand, 0, 0);

		globalZKWGraph.addedge(s + networkNode, t, demand, 0);

	}
	i = 0;
	startNode = networkNode + consumeNode;
	secendNode = networkNode + consumeNode + 1;
	for (; i < consumeNode; i++) {
		add_edge(startNode, i + networkNode, consumeNodeDemand[i], 0, 0);

		globalZKWGraph.addedge(startNode, i + networkNode, consumeNodeDemand[i],
				0);
	}

	//7 14 16 18 19 29 35 37 38 40 41 42 44 49 51 54 55 56 58 70 75 79 81 83 85 88 94 103 110 111 124 126 128 141 143 146 147 153 157 161 162 165 166 169 170 185 187 190 192 195 197 203 206 210 213 221 224 225 228 229 237 239 241 245 246 247 249 251 253 259 262 263 266 267 271 281 284 289 298 301 302 307 311 313 314 316 318 323 324 325 334 340 344 363 369 372 375 376 380 389 390 391 398 404 411 413 417 419 420 421 426 433 440 442 443 444 448 449 464 467 470 478 484 487 491 495 501 506 509 510 512 518 520 523 524 536 538 554 557 560 561 562 566 567 570 576 578 585 589 591 597 599 600 607 610 612 624 626 628 630 633 637 644 646 649 651 655 668 669 672 678 679 684 687 689 692 698 707 710 712 714 730 735 736 746 750 751 754 760 774 779 782 787 790 793 797 798
//	int networkconcrete[197] = { 7, 14, 16, 18, 19, 29, 35, 37, 38, 40, 41, 42,
//			44, 49, 51, 54, 55, 56, 58, 70, 75, 79, 81, 83, 85, 88, 94, 103,
//			110, 111, 124, 126, 128, 141, 143, 146, 147, 153, 157, 161, 162,
//			165, 166, 169, 170, 185, 187, 190, 192, 195, 197, 203, 206, 210,
//			213, 221, 224, 225, 228, 229, 237, 239, 241, 245, 246, 247, 249,
//			251, 253, 259, 262, 263, 266, 267, 271, 281, 284, 289, 298, 301,
//			302, 307, 311, 313, 314, 316, 318, 323, 324, 325, 334, 340, 344,
//			363, 369, 372, 375, 376, 380, 389, 390, 391, 398, 404, 411, 413,
//			417, 419, 420, 421, 426, 433, 440, 442, 443, 444, 448, 449, 464,
//			467, 470, 478, 484, 487, 491, 495, 501, 506, 509, 510, 512, 518,
//			520, 523, 524, 536, 538, 554, 557, 560, 561, 562, 566, 567, 570,
//			576, 578, 585, 589, 591, 597, 599, 600, 607, 610, 612, 624, 626,
//			628, 630, 633, 637, 644, 646, 649, 651, 655, 668, 669, 672, 678,
//			679, 684, 687, 689, 692, 698, 707, 710, 712, 714, 730, 735, 736,
//			746, 750, 751, 754, 760, 774, 779, 782, 787, 790, 793, 797, 798 };

//	int networkconcrete[38] = { 4, 11, 17, 20, 22, 25, 30, 36, 37, 46, 49, 54,
//			56, 57, 60, 61, 66, 69, 72, 81, 83, 85, 89, 97, 103, 106, 108, 114,
//			123, 126, 128, 131, 135, 137, 139, 147, 155, 159 };
//	for (i = 0; i < 197; i++) {
//		add_edge(networkconcrete[i], secendNode, ULONG_MAX, 0, 0);
//		add_edge_zkw(networkconcrete[i], secendNode, LONG_MAX, 0);
//		sol.addedge(networkconcrete[i], secendNode, inf, 0);
//		addedge_zwk3(networkconcrete[i], secendNode, INT_MAX, 0);
//	}

//	endNode = networkNode + consumeNode + 2;
//	add_edge(secendNode, endNode, ULONG_MAX, 0, 0);
//
//	globalZKWGraph.addedge(secendNode, endNode, inf, 0);

//	augmentnetworkNode = networkNode + consumeNode + 3;
	endNode = networkNode + consumeNode + 1;
	augmentnetworkNode = networkNode + consumeNode + 2;
}

//你要完成的功能总入口
int getcost(vector<bool> popula) {

	MCMF globalsol = globalZKWGraph;

	unsigned numCurrentServer = 0;
	for (unsigned i = 0; i < networkNode; i++) {
		if (popula[i]) {
			globalsol.addedge(i, secendNode, INT_MAX, 0);
			numCurrentServer++;
		}
//		else {
//			globalsol.addedge(i, secendNode, INT_MAX, deployCost * 1000);
//		}

	}

	//	cout << "potention:"
	//			<< (minimumCostFlow(startNode, endNode, consumeNodeDemandSum))
	//			<< endl;
#ifdef _DEBUG
	print_time_franz("");
#endif
	int answercost = globalsol.costflow(startNode, endNode,
			consumeNodeDemandSum);
#ifdef _DEBUG
	print_time_franz("-");
#endif
	if (-1 != answercost)
		answercost += (numCurrentServer * deployCost);
	return answercost;
}

void geneticAlgorithm(int popula_size, vector<bool> &best_solution) {

	int gene_size = networkNode;
	//   int global_best=0xffffff;
	bool isfirst = true;
	bool is_small_graphic = false;
	int same_num = 0;
	int last_best;
	if (networkNode < smallNodeNum) {
		is_small_graphic = true;
	}
	vector<vector<bool> > popula;	//定义种群
	// bool popula=new bool*[popula_size];
	// for (int i = 0; i < gene_size; ++i)
	// {
	// 	popula[i]=new bool[gene_size];
	// }
	struct timeval tpstart;
	gettimeofday(&tpstart, NULL);
	srand(tpstart.tv_usec);
	//生//成初始的第0代种群

	for (int i = 0; i < popula_size - 1; i++) {
		vector<bool> geti;
		for (int j = 0; j < gene_size; j++) {
			double rd = (rand() % 1000) / 1000.0;
			if (rd < (consumeNode * 1.0 / networkNode)) {
				geti.push_back(true);
				// 	popula[i][j]=true;
			} else {
				//popula[i][j]=false;
				geti.push_back(false);
			}
		}
		popula.push_back(geti);    //0-1序列
	}
	// cout<<"gengsize"<<gene_size<<endl;
	vector<bool> geti;
	if (networkNode < 600) {
		for (int i = 0; i < gene_size; ++i) {
			if (networkNode2consumeNode[i] != networkNode) {
				geti.push_back(true);
			} else {
				geti.push_back(false);
			}
		}
	} else {
		for (int i = 0; i < gene_size; ++i) {
			if (final_server[i]) {
				geti.push_back(true);
			} else {
				geti.push_back(false);
			}
		}
	}

	popula.push_back(geti);
	vector<int> fitness_po(popula_size, 0);    //初始化
	////////////////////
	for (int turns = 0; timeout == false; turns++)
	//    for(int turns=0;turns<10;turns++)
			{
		//更新nextpop fitness
		for (int i = 0; i < popula_size; i++) {
			int fitness;
////////////////////////////////
			fitness = getcost(popula[i]);
			if (fitness == -1) {
				fitness = 0xffffff;
			}
			//       cout<<fitness<<endl;
			fitness = -fitness;
			fitness_po[i] = fitness;
		}
		int temp;
		vector<bool> tempp;
		//按照fit排序,qian优秀者
		for (int i = 0; i < popula_size; i++) {
			for (int j = 0; j < popula_size - 1; j++) {
				if (fitness_po[j] < fitness_po[j + 1]) {
					temp = fitness_po[j];
					fitness_po[j] = fitness_po[j + 1];
					fitness_po[j + 1] = temp;
					tempp = popula[j];
					popula[j] = popula[j + 1];
					popula[j + 1] = tempp;
				}
			}
		}
		if (last_best != fitness_po[0]) {
			last_best = fitness_po[0];
			same_num = 0;
		} else {
			same_num++;
			if (same_num > 200)	//if the best cost is the same all the time
					{	//cout<<"break"<<endl;
				break;
			}
		}
		//            if (turns>20)
		//            {
		//        	 cout<<turns<<"best"<<fitness_po[0]<<endl;
		//            	global_best=fitness_po[0];
		//            }
		if (networkNode > 600 && isfirst) {
			isfirst = false;
			popula_size -= 4;
		}
		//        	gettimeofday(&tpstart,NULL);
		// srand(tpstart.tv_usec);
		//cross over，
		gettimeofday(&tpstart, NULL);
		srand(tpstart.tv_usec);
		for (int child = popula_size / 2; child < popula_size; child++) {
			int baba = rand() % (popula_size / 2);        //给cha个体一定的概率活
			int mama = rand() % (popula_size / 2);
			int pos = rand() % gene_size + 1;
			; //单点杂交 在n个分量中，随机确定第pos个分量 ,前pos个进行cross
			for (int i = 0; i < pos; i++) {
				popula[child][i] = popula[baba][i];
			}
			for (int i = pos; i < gene_size; i++) {
				popula[child][i] = popula[mama][i];
			}
		}
#ifdef _DEBUG
		cout << "best" << fitness_po[0] << endl;
#endif

		//        	gettimeofday(&tpstart,NULL);
		// srand(tpstart.tv_usec);
		//mutate算子 //save最优秀的两个不发生mututate
		float pm = 1.0 / gene_size + 1 / (turns + 60);   //can not be too high!!
		//    float pm=1.0/160;
		for (int i = 2; i < popula_size; i++) {
			for (int j = 0; j < gene_size; j++) {
				float pro = (rand() % 100000) / 100000.0;
				if (pro < pm) {
					popula[i][j] = !popula[i][j];  //取反
				}
			}
		}  //cout<<"hello----------------------------------------"<<endl;
		   //       break;
	} //end of for timeout
//   cout<<"global_best"<<global_best<<endl;
	best_solution.assign(popula[0].begin(), popula[0].end());
}

string optimalString;

#ifdef _DEBUG
const unsigned int timeMaxLimit = 80;
#else
const unsigned int timeMaxLimit = 88;
#endif

//char optimalString[10000000];
//
//你要完成的功能总入口

void smallgraph(char * topo[MAX_EDGE_NUM], int line_num, char * filename) {
	readData(topo, line_num);
	//cout << "readData_over" << endl;
	final_server = new int[networkNode];
	// if (networkNode > 600) {
	// 	alarm(87);
	// 	memset(final_server, 0, sizeof(int) * networkNode);
	// 	getmidsolution(topo, line_num, filename, final_server);
	// }
	//genetic part
	timeout = false;
	vector<bool> best_solution; //(networkNode,false);
	signal(SIGALRM, sig_handler);
	if (networkNode < 200) {
		alarm(86);
		geneticAlgorithm(60, best_solution);
	} else if (networkNode >= 200 && networkNode < 600) {
		alarm(timeMaxLimit);
		geneticAlgorithm(20, best_solution);
	} else {
		alarm(87);
		memset(final_server, 0, sizeof(int) * networkNode);
		getmidsolution(topo, line_num, filename, final_server);
		geneticAlgorithm(6, best_solution);
	}	///----------------------------------

	//augment path part
	unsigned numOptimalServer = 0;
	//	int networkconcrete[197] = { 7, 14, 16, 18, 19, 29, 35, 37, 38, 40, 41, 42,
	//			44, 49, 51, 54, 55, 56, 58, 70, 75, 79, 81, 83, 85, 88, 94, 103,
	//			110, 111, 124, 126, 128, 141, 143, 146, 147, 153, 157, 161, 162,
	//			165, 166, 169, 170, 185, 187, 190, 192, 195, 197, 203, 206, 210,
	//			213, 221, 224, 225, 228, 229, 237, 239, 241, 245, 246, 247, 249,
	//			251, 253, 259, 262, 263, 266, 267, 271, 281, 284, 289, 298, 301,
	//			302, 307, 311, 313, 314, 316, 318, 323, 324, 325, 334, 340, 344,
	//			363, 369, 372, 375, 376, 380, 389, 390, 391, 398, 404, 411, 413,
	//			417, 419, 420, 421, 426, 433, 440, 442, 443, 444, 448, 449, 464,
	//			467, 470, 478, 484, 487, 491, 495, 501, 506, 509, 510, 512, 518,
	//			520, 523, 524, 536, 538, 554, 557, 560, 561, 562, 566, 567, 570,
	//			576, 578, 585, 589, 591, 597, 599, 600, 607, 610, 612, 624, 626,
	//			628, 630, 633, 637, 644, 646, 649, 651, 655, 668, 669, 672, 678,
	//			679, 684, 687, 689, 692, 698, 707, 710, 712, 714, 730, 735, 736,
	//			746, 750, 751, 754, 760, 774, 779, 782, 787, 790, 793, 797, 798 };
	//	for (unsigned i = 0; i < 197; i++) {
	//
	//		numOptimalServer++;
	//		add_edge(networkconcrete[i], secendNode, ULONG_MAX, 0, 0);
	//	}
	for (unsigned i = 0; i < networkNode; i++) {
		if (best_solution[i]) {
			numOptimalServer++;
			add_edge(i, secendNode, ULONG_MAX, 0, 0);
		} else {
			add_edge(i, secendNode, ULONG_MAX, 100 * deployCost, 0);
		}
	}

	cout << "numOptimalServer * deployCost: " << numOptimalServer * deployCost
			<< " Path Cost: "
			<< (minimumCostFlow(startNode, endNode, consumeNodeDemandSum))
			<< endl;
	augmentPath(startNode, endNode, consumeNodeDemandSum, optimalString);
	///----------------------------------

	//print the path
	//	topo_file=optimalstring.c_str();
#ifdef _DEBUG
	cout << optimalString.c_str() << endl;
#endif
	// 需要输出的内容
	// 直接调用输出文件的方法输出到指定文件中(ps请注意格式的正确性，如果有解，第一行只有一个数据；第二行为空；第三行开始才是具体的数据，数据之间用一个空格分隔开)
	write_result(optimalString.c_str(), filename);
}

void mediumgraph(GraphTopo& G, char * filename) {
	int Nerberhood, Tsl;
	srand((unsigned) time(NULL));
	G.chosNum = 0;
	G.bestcost = INF;
	defineFlow = 0;
	if (G.Nnum > 500)
		Nerberhood = 20, Tsl = 0;
	else if (G.Nnum < 200)
		Nerberhood = 40, Tsl = 5;
	else
		Nerberhood = 60, Tsl = 10;
	for (int i = 0; i < G.Cnum; i++)
		defineFlow += G.C_Demand[i];
	memset(head, -1, sizeof(head));

	G.T = G.Nnum + G.Cnum + 1;
	G.S = G.Nnum + G.Cnum;
	initGraphTopoStructure();
	B.Initset(G.Nnum, Nerberhood, Tsl);
	B.BeginSolve();
	for (int i = 0; i < G.Nnum; i++)
		if (B.bestNode[i] == 1) {
			lastBestServer[k++] = i;
		}
	InplementMCMF();
	InitEdge();
	memset(isflag, 0, sizeof(isflag));
	DFS(G.S);
	sprintf(starray, "%d\n", allRoadNumber);
	bestpathstring = starray;
	bestpathstring += tempstring;
	char * topo_file = (char *) bestpathstring.c_str();
	write_result(topo_file, filename);

}
// #define smallNodeNum 200
// #define midlleNodeNum 600
void deploy_server(char * topo[MAX_EDGE_NUM], int line_num, char * filename) {
	G.initial(topo, line_num);
	cout << G.Nnum << " " << smallNodeNum << endl;
//	return ;
	if (G.Nnum <= smallNodeNum) {
		smallgraph(topo, line_num, filename);
	} else {
		if (G.Nnum <= midlleNodeNum)
			mediumgraph(G, filename);
		else {
			mediumgraph(G, filename);
		}
	}
}

