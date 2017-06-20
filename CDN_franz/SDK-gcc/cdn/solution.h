#include <vector>
using namespace std;
bool iswujie(int consumer_id,int **consumer_node,short int **graphic,int node_num/*,int result[4]*/);

int getMidServerNode(short int **rent_money,int **consumer_node,int consumer_num,int *result,vector<vector<int> > &dist,vector<vector<int> > &pre);
//void getLowPath();
int getMidNode(int start_id,int end_id,short int **rent_money,vector<int> path,vector<int> dist);

void Dijkstra(int n, int start_id, vector<int> &dist, vector<int> &pre, short int **chengben,short int **graphic);
void printpath(vector<int> path);
vector<int> getlowpath(vector<int> pre, int init, int fina,vector<int> &path);
void getcharpath(char *reschar,vector<vector<int> > solutionpath);
void getallLowPath(int node_num,vector<vector<int> > &dist, vector<vector<int> > &pre, short int **chengben, short int **graphic);

int getminServernode(vector<int> dist,int *resultServerNode,int server_num);
bool getAnothernode(short int **rent_money,short int **graphic,int start_id,vector<int> path,
	int except_num,int node_num,int *anothernode);
void getsolutionpath(int path_id,vector<int> lowpath,vector<vector<int> > &solutionpath,int minbw);
int getinitServerNode(short int **graphic,int *resultServerNode,int node_num,int consumer_node_num);

bool checkpath(int node_id,vector<int> path);
int correctPath(vector<vector<int> >&solutionpath,int **consumer_node);
int betterPath(vector<vector<int> >&solutionpath,int **consumer_node,int node_num,int consumer_node_num);
int getlowBwpath(vector<int> path,short int **graphic);
void initdist(vector<vector<int> >&dist);
void getServerProvideBW(vector<vector<int> > dist,int **server_bw);
void sortConsumer(int **consumer_node,int consumer_node_num,int *sortConsumer);
void getInfluencePath(int **server_bw,int *influenceNode,int consumer_node_num,vector<vector<int> > &solutionpath,int pathid,
	vector<vector<int> > dist,vector<vector<int> > pre,int node_num,short int **rent_money,short int **graphic,
	int** consumer_node,int chengben);
void rollBackGraphic(vector<vector<int> >solutionpath,short int **graphic,int consumer_id,int pathid);
int getNearMidServer(short int **rent_money,int **consumer_node,int consumer_node_num,int *result,
	vector<vector<int> > &dist,vector<vector<int> > &pre);
void getRentBW(short int**graphic,short int**rent_money,short int **rent_BW,int node_num);
int getLowRentServer(short int **rent_money,short int**graphic,int **consumer_node,int consumer_node_num,int node_num,int *result,
	vector<vector<int> > dist);
