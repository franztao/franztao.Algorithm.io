#include   "cdn.h"

int isflag[MAX_NODE_franz];
int instance[MAX_NODE_franz], last[MAX_NODE_franz], head[MAX_NODE_franz],
		agmenhead[MAX_NODE_franz];
int num4K = 0;
EdgeClass edge2edge[MAX_Edge_Num * 4];
int allFlow, defineFlow, bestCostValue;

char starray[MAX_Edge_Num];
string tempstring, bestpathstring;
char linenum[10];
int cost, ans;
int Road[MAX_NODE_franz];
int value4K, value4R = 0;
int node[MAX_C];

int minimulFlowValue = INF;
int allRoadNumber;
int value4Nk;

int minValue;
int m = 0;
int prenode[MAX_NODE_franz];
int preedge[MAX_NODE_franz];
int graphMatrix[MAX_N][MAX_N];
vector<int> pathVector;
AugmentEdgeClass initialEdge[MAX_Edge_Num];
int minumulFlow[MAX_Edge_Num];
void init() {
	G.EN = 0;
	ans = cost = 0;
}

void add_edge(int u, int v, int cap, int cost) {
	edge2edge[G.EN].u = u;
	edge2edge[G.EN].v = v;
	edge2edge[G.EN].candflow = cap;
	edge2edge[G.EN].cost = cost;
	edge2edge[G.EN].flow = cap;

	edge2edge[G.EN].re = G.EN + 1;
	edge2edge[G.EN].next = head[u];
	head[u] = G.EN++;

	edge2edge[G.EN].u = v;
	edge2edge[G.EN].v = u;
	edge2edge[G.EN].candflow = 0;
	edge2edge[G.EN].cost = -cost;
	edge2edge[G.EN].flow = 0;

	edge2edge[G.EN].re = G.EN - 1;
	edge2edge[G.EN].next = head[v];
	head[v] = G.EN++;
}

void ParameterReset() {
	for (int i = 0; i < G.candEdgeNum; i++)
		edge2edge[i].flow = edge2edge[i].candflow;
	for (int u = 0; u < G.Nnum; u++)
		head[u] = agmenhead[u];
	head[G.T] = agmenhead[G.Nnum];
}

void gethead() {
	for (int u = 0; u < G.Nnum; u++)
		agmenhead[u] = head[u];
	agmenhead[G.Nnum] = head[G.T];
}
void initGraphTopoStructure() {
	for (int i = 0; i < G.Line_num; i++) {
		add_edge(G.from_Index[i], G.v[i], G.bt[i], G.cs[i]);
		add_edge(G.v[i], G.from_Index[i], G.bt[i], G.cs[i]);
	}
	for (int i = 0; i < G.Cnum; i++) {
		add_edge(G.s[i], G.sv[i], G.C_Demand[i], 0);
		add_edge(G.sv[i], G.s[i], G.C_Demand[i], 0);
	}
	for (int i = G.Nnum; i < G.Nnum + G.Cnum; i++) {
		add_edge(i, G.T, INF, 0);
		node[i] = i - G.Nnum;
	}

	G.candEdgeNum = G.Line_num * 4 + G.Cnum * 4 + G.Cnum * 2;
	gethead();
}

int dfs(int u, int a) {
	if (u == G.T) {
		ans += cost * a;
		return a;
	}
	isflag[u] = 1;
	int flow = 0, f;
	for (int i = head[u]; i != -1; i = edge2edge[i].next) {
		if (instance[edge2edge[i].v] == instance[u] - edge2edge[i].cost
				&& edge2edge[i].flow && !isflag[edge2edge[i].v]) {
			f = dfs(edge2edge[i].v, min(a, edge2edge[i].flow));
			ans += f * edge2edge[i].cost;
			edge2edge[i].flow -= f;
			edge2edge[edge2edge[i].re].flow += f;
			a -= f;
			flow += f;
			if (a == 0)
				break;
		}
	}
	return flow;
}

bool spfa() {
	deque<int> duilie_Q;
	for (int i = 0; i < G.node; i++)
		instance[i] = INF;
	instance[G.T] = 0;
	isflag[G.T] = 1;
	duilie_Q.push_back(G.T);
	while (!duilie_Q.empty()) {
		int u = duilie_Q.front(), temp;
		duilie_Q.pop_front();
		for (int i = head[u]; i != -1; i = edge2edge[i].next)
			if (edge2edge[edge2edge[i].re].flow
					&& (temp = instance[u] - edge2edge[i].cost)
							< instance[edge2edge[i].v])
							{
				instance[edge2edge[i].v] = temp;
				duilie_Q.push_back(edge2edge[i].v);
			}
		isflag[u] = 0;
	}
	return instance[G.S] < INF;
}

int CostFlow_ZKW() {
	int n;
	init();
	ParameterReset();
	G.EN = G.candEdgeNum;
	head[G.S] = -1;
	num4K++;
	G.roadmincost = 0;
	allFlow = 0;
	ans = 0;
	for (int m = 0; m < G.chosNum; m++)
		add_edge(G.S, lastBestServer[m], INF, 0);
	while (spfa()) {
		isflag[G.T] = 1;
		while (isflag[G.T]) {
			memset(isflag, 0, sizeof(isflag));
			n = dfs(G.S, INF);
			allFlow += n;
		}
	}
	if (allFlow != defineFlow)
		ans = INF;
	return G.Server_Cost * G.chosNum + ans;
}

bool SPFA() {
	int v, c, f;
	queue<int> duilie_Q;
	for (int i = 0; i < G.node; i++) {
		instance[i] = INF;
	}
	memset(last, -1, sizeof(last));
	memset(isflag, 0, sizeof(isflag));

	isflag[G.S] = 1;
	instance[G.S] = 0;
	duilie_Q.push(G.S);
	while (!duilie_Q.empty()) {
		int u = duilie_Q.front();
		duilie_Q.pop();
		isflag[u] = 0;
		for (int i = head[u]; i != -1; i = edge2edge[i].next) {
			v = edge2edge[i].v;
			c = edge2edge[i].cost;
			f = edge2edge[i].flow;
			if (f && instance[v] > instance[u] + c) {
				instance[v] = instance[u] + c;
				last[v] = i;
				if (!isflag[v]) {
					duilie_Q.push(v);
					isflag[v] = 1;
				}
			}
		}
	}
	return instance[G.T] == INF ? 0 : 1;
}

bool mincostmaxflow() {
	int i;
	num4K++;

	allFlow = 0;
	G.roadmincost = 0;
	while (SPFA()) {
		int l = INF + 1;
		for (i = last[G.T]; i != -1; i = last[edge2edge[i].u]) {
			if (edge2edge[i].flow < l)
				l = edge2edge[i].flow;
		}
		for (i = last[G.T]; i != -1; i = last[edge2edge[i].u]) {
			edge2edge[i].flow -= l;
			edge2edge[i ^ 1].flow += l;
		}
		allFlow += l;
		G.roadmincost += l * instance[G.T];

	}
	if (allFlow == defineFlow) {
		bestCostValue = G.Server_Cost * k + G.roadmincost;
		return 1;
	}
	return 0;
}

void add_Edge(int u, int v, int f) {
	initialEdge[value4Nk].v = v;
	initialEdge[value4Nk].flow = f;
	initialEdge[value4Nk].next = head[u];
	head[u] = value4Nk++;
}

void InitEdge() {
	memset(head, -1, sizeof(head));
	for (int i = 0; i < G.EN; i += 2) {
		add_Edge(edge2edge[i].u, edge2edge[i].v,
				edge2edge[edge2edge[i].re].flow);
	}
}

void getroad() {
	int i;
	tempstring += "\n";
	for (i = value4K - 1; i >= 0; i--) {
		sprintf(starray, "%d ", Road[i]);
		tempstring += starray;
	}
}

void DFS(int u) {
	isflag[u] = 1;
	if (u == G.T) {
		Road[value4K++] = minimulFlowValue;
		for (int i = prenode[G.T]; i != G.S; i = prenode[i]) {
			i >= G.Nnum ? Road[value4K++] = i - G.Nnum : Road[value4K++] = i;
		}
		for (int i = G.T; i != G.S; i = prenode[i]) {
			initialEdge[preedge[i]].flow -= minimulFlowValue;
		}
		memset(prenode, 0, sizeof(prenode));
		memset(preedge, 0, sizeof(preedge));
		getroad();

		allRoadNumber++;
		value4K = 0;
		minimulFlowValue = INF;
		u = G.S;
	}
	for (int i = head[u]; i != -1; i = initialEdge[i].next) {
		int to = initialEdge[i].v;
		if (initialEdge[i].flow > 0) {
			minimulFlowValue = min(minimulFlowValue, initialEdge[i].flow);
			isflag[to] = 1;
			preedge[to] = i;
			prenode[to] = u;

			DFS(to);
			isflag[to] = 0;
		}
	}
}
void InplementMCMF() {
	ParameterReset();
	G.EN = G.candEdgeNum;
	head[G.S] = -1;
	for (int m = 0; m < k; m++) {
		add_edge(G.S, lastBestServer[m], INF, 0);
	}
	mincostmaxflow();
}

