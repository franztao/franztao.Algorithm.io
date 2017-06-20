#include <iostream>
#include <algorithm>
#include <vector>
#include <queue>
#include <set>
#include <string.h>
#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#include "route.h"
#include "lib_record.h"
#include "sys/timeb.h"
using namespace std;




#define MAX_NodeSize (2015)
#define MAX_EnodeSize (105)
#define prunewidth 1

#ifndef __FranzDebug
bool debug_showinitmatrix=false;
bool debug_runingsearchfunction=false;
bool debug_showresult=false;
bool debug_showscc=false;
bool debug_showdijkstrainfo=false;
#else
bool debug_showscc=false;
bool debug_showinitmatrix=false;
bool debug_runingsearchfunction=true;
bool debug_showresult=true;
bool debug_showdijkstrainfo=false;


#endif // __FranzDebug

int DebugInputGraphMatrix[MAX_NodeSize][MAX_NodeSize];
int DebugInputMaxGraphMatix[MAX_NodeSize][MAX_NodeSize];
int DebugFlagofInputGraphEdgeMatrix[MAX_NodeSize][MAX_NodeSize];
int DebugFlagofInputMaxGraphEdgeMatrix[MAX_NodeSize][MAX_NodeSize];
/*
1=<Weight value<=100
weight value is integer
No loop link
Multiple link with different weight
|vertices|<=2000,|outer degree|<=20,sparse graph
|Essential vertices|<=100
Directed loop graph

some tips modified the algorithm:
0.reverse the whole graph
1.water drop method
2.every branch have the limited time
3. if((-1!=vs_TSPDFS[j])&&(-1!=(dp.vsnode[j])))
4.find 3 path method
5.find the second path,add some value to the edge links the first path's enode
6.add linkpaths between every enodes.
7.optimate some array's long assignment problem
8.first calculate the more enodesize's path
*/

/*
1,0,3,1
2,0,3,2

0,0,1,1
1,1,2,1
2,2,3,1
3,1,4,1
4,4,3,1
5,0,5,1
6,5,2,1
*/

/******************************franz' code************************/



int inputedge_num;
int inputdemand_num;
int demandEnode[2][MAX_EnodeSize];
int demandEnodeSize[2];
int ithdispath;

//调试参数
struct timeb StartTime;
struct timeb OverTime;







//判断是否存在分离路径
bool last_suceedFindDisjointPath;



//××××××××××××××原始图输入相关属性参数××××××××××××××××//
//edge number of initial graph
//原始输入图边的个数
//7
int EdgeSizeofInputGragh;
//node number of initial graph
//原始输入图点的个数
//6
int NodeSizeofInputGragh;
//the initial graph's topological structure(matrix)
//原始输入图的拓扑结构（矩阵形式）
/*
点的标号0 3 1 2 4 5
点的点号0 1 2 3 4 5
       0 0 1 0 0 1
       0 0 0 0 0 0
       0 0 0 1 1 0
       0 1 0 0 0 0
       0 1 0 0 0 0
       0 0 0 1 0 0
*/
int InputGraphMatix[MAX_NodeSize][MAX_NodeSize];
int InputMaxGraphMatix[MAX_NodeSize][MAX_NodeSize];
//原始输入图每条边所对应的标号
int FlagofInputGraphEdgeMatrix[MAX_NodeSize][MAX_NodeSize];
int FlagofInputMaxGraphEdgeMatrix[MAX_NodeSize][MAX_NodeSize];
//判断经过洗点处理后这个点是否有效存在
//1 1 1 1 1 1
bool isValuedNode[MAX_NodeSize];
//表示路径1，或者2的必经点个数
int ENodeSize;
//存储必经点
int ENode[MAX_EnodeSize];
//标号——点号
//0 2 3 1 4 5
int index_node[MAX_NodeSize];
//点号——标号
//0 2 3 1 4 5
int node_index[MAX_NodeSize];
//判断这个点是路径1或者2的必经点
//[0]: 2
//[1]: 3
bool isENode[2][MAX_NodeSize];
//源点
//0
int Source;
//终点
//1
int Destination;
//输入图经过洗点后的拓扑结构（链表形式）
vector<int>GraphList[MAX_NodeSize];
//输入图经过洗点后的反向拓扑结构（链表形式）
vector<int>rGraphList[MAX_NodeSize];



//× SCC算法参数×//
//强连通图联通分量（强联通处理后的图点的个数）
int NodeSizeofSCC;
//从源点能到达多少个必经点
int isdfsAllENSCC;
//判断从源点能到达终点
bool isDfstodest;
//是否是scc的第一次dfs操作
bool scc_isFirstDfs;
//ith点号对应ith连通分量
int ithnode_ithSCC[MAX_NodeSize];
//ith连通分量有的必经点个数
int ENnumofithSCC[MAX_NodeSize];
//判断这个点是否要在topo排序中使用
bool isValuedNode4BuildTopoSort[MAX_NodeSize];
//scc 算法中间变量
bool scc_vs[MAX_NodeSize];
vector<int>scc_vsnode;








//× 拓扑排序算法参数×//
//进行拓扑排序的图的拓扑结构（链表形式）
vector<int>TopoSortGraphEdge[MAX_NodeSize];
//进行拓扑排序的图的反向拓扑结构（链表形式）
vector<int>rTopoSortGraphEdge[MAX_NodeSize];
//ith连通分量对应ith拓扑序
int ithscc_ithtopo[MAX_NodeSize];
//ith拓扑序对应ith连通分量
int ithtopo_ithscc[MAX_NodeSize];
//拓扑排序参数：每个点的入度值
int indegofTopoNode[MAX_NodeSize];
//有必经点的ith连通分量的顺序排序
int ithSCChaveNE_toposeq[MAX_NodeSize];
//顺序排序对应有必经点的ithTopoSort
int seq_ithTopohaveNE[MAX_NodeSize];


//判断此强联通分量能否到达终点在去除拥有必经点的分量的强联通分量图中
bool isFindPathtoDestinTopoGraphremovedEN[MAX_NodeSize];
//判断此点能否到达终点在去除必经点图中
bool isFindPathtoDestinInitGraphremovedEN[MAX_NodeSize];





//× TSP图找路的相关参数×//
//TSP图的点的个数
int TSPNodesize;
//TSP图的点的编号
int TSPNode[MAX_EnodeSize];
//TSP图的点的顺序编号
int TSPNode_seq[MAX_NodeSize];
//必经点的出度值
int Enodeoutdegree[MAX_NodeSize];
//必经点的入度值
int Enodeindegree[MAX_NodeSize];
//在tsp上是否找到路径
bool succeedFindDisjointPath;
//路径的最小权值
int ResultValueMinCost;
//自己设置的权值最大上限
int setMAXValue=(1<<30);
//中间变量
int tsp_d[MAX_NodeSize];
int tsp_vs[MAX_NodeSize];
typedef pair<int,int> P;
struct state
{
    //此时路径段的跳数
    int hop;
    //此时路径段所处点的编号
    int id;
    //此时路径段的权值
    int costsum;
    //记录此时路径段经过的点
    int vsnode[MAX_NodeSize];
    //should deal with
    //以权值位优先级
    friend bool operator<(const struct state a,const struct state b)
    {
        if(a.costsum<b.costsum)
        {
            return true;
        }
        if(a.costsum>b.costsum)
        {
            return false;
        }
        if(a.hop<b.hop)
        {
            return true;
        }
        if(a.hop>b.hop)
        {
            return false;
        }

        return false;
    }
} fq,dp,sp;
//priority_queue<state>TSPprique;
//TSP图的拓扑结构（链表形式）
vector<state>TSPGraph[MAX_EnodeSize];
//路径1或者2
vector<int>ResultPath;
//控制是否结束dfs搜索
bool isendTSPDFS;//=true;
//参数，设计成全局变量，介绍内存消耗
int vs_TSPDFS[MAX_NodeSize];


void DebugPrint()
{
    for(int i=0; i<inputdemand_num; i++)
    {
        cout<<"disjointpath  "<<i<<endl;
        //cout<<"FFF"<<destination<<endl;
        cout<<"source node:"<<node_index[Source];
        cout<<"  destination node:"<<node_index[Destination]<<endl;
        cout<<"essential node ("<<demandEnodeSize[i]<<"):";
        for(int j=0; j<demandEnodeSize[i]; j++)
        {
            cout<<node_index[demandEnode[i][j]]<<" ";
        }
        cout<<endl;
    }
    cout<<"node size:"<<NodeSizeofInputGragh<<endl;
    cout<<"edge size:"<<EdgeSizeofInputGragh<<endl;

    /*
        cout<<"node_index :";
        for(int i=0; i<NodeSizeofInputGragh; i++)
        {
            cout<<node_index[i]<<" ";
        }
        cout<<endl;
        cout<<"index_node :";
        for(int i=0; i<NodeSizeofInputGragh; i++)
        {
            cout<<index_node[i]<<" ";
        }
        cout<<endl;
        */
    if(debug_showinitmatrix)
    {
        cout<<"Initmatrix:"<<endl;
        for(int i=0; i<NodeSizeofInputGragh; i++)
        {
            cout<<node_index[i]<<" ";
        }
        cout<<endl;
        cout<<endl;

        for(int i=0; i<NodeSizeofInputGragh; i++)
        {
            for(int j=0; j<NodeSizeofInputGragh; j++)
            {
                cout<<InputGraphMatix[i][j]<<" ";
            }
            cout<<endl;
        }
    }
}
//add edge to the graph(List)
//构造链表形式的原始图
void add_edge(int from,int to)
{
    GraphList[from].push_back(to);
    rGraphList[to].push_back(from);
}
//wrap some node except src and dest node whose indgree lower than 1 or outdgree than 1.
//去除入度或者出处为1的不符合要求的点（洗点操作）
void DealwithInputGraph()
{
    bool boolindegree;
    bool booloutdegree;
    booloutdegree=false;
    boolindegree=false;
    for(int k=2; k<NodeSizeofInputGragh; k++)
    {
        for(int i=0; i<NodeSizeofInputGragh; i++)
        {
            if(0<InputGraphMatix[k][i])
                booloutdegree=true;
        }
        for(int j=0; j<NodeSizeofInputGragh; j++)
        {
            if(0<InputGraphMatix[j][k])
                boolindegree=true;
        }
        if((false==boolindegree)||(false==booloutdegree))
        {
            isValuedNode[k]=false;
        }
    }

    return ;
}
//judge the graph whether succeed to find path or not.
//初始判断这个图是否存在解
bool InitJudgeGraphisCorrect()
{
    bool boolindegree;
    bool booloutdegree;
    booloutdegree=false;
    boolindegree=false;
    for(int i=0; i<inputdemand_num; i++)
    {
        for(int k=0; k<demandEnodeSize[i]; k++)
        {
            if(false==isValuedNode[demandEnode[i][k]])
            {
                return false;
            }
        }
    }
    for(int i=0; i<inputdemand_num; i++)
    {
        for(int j=0; j<NodeSizeofInputGragh; j++)
        {
            if(0<InputGraphMatix[Source][j])
                booloutdegree=true;
        }
        for(int j=0; j<NodeSizeofInputGragh; j++)
        {
            if(0<InputGraphMatix[j][Destination])
                boolindegree=true;
        }
        if(false==booloutdegree)
            return false;
        if(false==boolindegree)
            return false;
    }
    //
    //fromdestdfswithoutEN(disjointdest[0]);
    return true;
}
int EdgeofFirstPath[MAX_NodeSize][MAX_NodeSize];
//change the physical structure of the graph(matrix->list)
//将图从矩阵表示形式得到链表表示形式
void GraphArraytoList()
{
    for(int i=0; i<NodeSizeofInputGragh; i++)
    {
        if(true==isValuedNode[i])
        {
            for(int j=0; j<NodeSizeofInputGragh; j++)
            {
                if(j!=i)
                    if(true==isValuedNode[j])
                    {
                        if(0<InputGraphMatix[i][j])
                        {
                            add_edge(i,j);
                        }
                    }
            }
        }
    }
}

void heuristicAddGraphEdgeValue(int heursiticvalue)
{

    if(1==ithdispath)
    {
        //deal with
        for(int j=0; j<NodeSizeofInputGragh; j++)
        {
            for(int k=0; k<NodeSizeofInputGragh; k++)
            {
                InputGraphMatix[j][k]=DebugInputGraphMatrix[j][k];
                FlagofInputGraphEdgeMatrix[j][k]=DebugFlagofInputGraphEdgeMatrix[j][k];
            }
        }
        for(int i=ResultPath.size()-1; i>=1; i--)
        {
            EdgeofFirstPath[ResultPath[i]][ResultPath[i-1]]=-1;
            if(0==DebugInputMaxGraphMatix[ResultPath[i]][ResultPath[i-1]])
                InputGraphMatix[ResultPath[i]][ResultPath[i-1]]+=heursiticvalue;
        }
    }
    else
    {
        for(int i=ResultPath.size()-1; i>=1; i--)
        {
            if(0!=InputMaxGraphMatix[ResultPath[i]][ResultPath[i-1]])
            {
                InputGraphMatix[ResultPath[i]][ResultPath[i-1]]=InputMaxGraphMatix[ResultPath[i]][ResultPath[i-1]];
                FlagofInputGraphEdgeMatrix[ResultPath[i]][ResultPath[i-1]]=FlagofInputMaxGraphEdgeMatrix[ResultPath[i]][ResultPath[i-1]];
            }
            else
            {
                InputGraphMatix[ResultPath[i]][ResultPath[i-1]]+=heursiticvalue;
            }
        }


    }
}

//强联通算法部分
//forward dfs function for SCC
void SCCFowardDfs(int v)
{
    if(true==scc_isFirstDfs)
    {
        if(v==Destination)
        {
            isDfstodest=true;
        }
        isValuedNode4BuildTopoSort[v]=true;
        if(true==isENode[ithdispath][v])
        {
            isdfsAllENSCC++;
        }
    }
    scc_vs[v]=true;
    for(unsigned int i=0; i<GraphList[v].size(); i++)
    {
        if(!scc_vs[GraphList[v][i]])
            SCCFowardDfs(GraphList[v][i]);
    }
    scc_vsnode.push_back(v);
}
//reverse dfs function for SCC
void SCCreverseDfs(int v,int k)
{
    ithnode_ithSCC[v]=k;
    scc_vs[v]=true;
    for(unsigned int i=0; i<rGraphList[v].size(); i++)
    {
        if(!scc_vs[rGraphList[v][i]]) SCCreverseDfs(rGraphList[v][i],k);
    }
}
//main function for SCC
int SCCMainFunction()
{
    isDfstodest=false;
    isdfsAllENSCC=0;
    scc_isFirstDfs=true;
    scc_vsnode.clear();
    memset(scc_vs,0,sizeof(scc_vs));
    memset(ENnumofithSCC,0,sizeof(ENnumofithSCC));
    memset(isValuedNode4BuildTopoSort,0,sizeof(isValuedNode4BuildTopoSort));
    for(int v=0; v<NodeSizeofInputGragh; v++)
    {
        if(!scc_vs[v])SCCFowardDfs(v);
        scc_isFirstDfs=false;
    }
    memset(scc_vs,0,sizeof(scc_vs));
    int countnum=0;
    for(int i=scc_vsnode.size()-1; i>=0; i--)
    {
        if(!scc_vs[scc_vsnode[i]])SCCreverseDfs(scc_vsnode[i],countnum++);
    }
    for(int i=0; i<ENodeSize; i++)
    {
        ENnumofithSCC[ithnode_ithSCC[ENode[i]]]++;
    }
    return countnum;
}

//对强连通图进行拓扑排序算法部分
//topo sort function
bool TopoOrder(int topon)
{
    int top=-1;
    int i;
    int sorti=0;
    for(i=(topon-1); i>=0; i--)
    {
        if(0==indegofTopoNode[i])
        {
            indegofTopoNode[i]=top;
            top=i;
        }
    }
    for(i=0; i<topon; ++i)
    {
        if(top==-1)
        {
            return false;
        }
        int j=top;
        ithtopo_ithscc[sorti]=j;
        sorti++;
        top=indegofTopoNode[top];
        int topofirstenode=false;
        int exmid=0;
        for(unsigned int k=0; k<TopoSortGraphEdge[j].size(); ++k)
        {
            if((0==(--indegofTopoNode[TopoSortGraphEdge[j][k]])))
            {
                if(false==topofirstenode)
                {
                    indegofTopoNode[TopoSortGraphEdge[j][k]]=top;
                    top=TopoSortGraphEdge[j][k];
                    topofirstenode=true;
                }
                else
                {
                    if(ENnumofithSCC[top]>0)
                    {

                        exmid=indegofTopoNode[top];
                        indegofTopoNode[TopoSortGraphEdge[j][k]]=exmid;
                        indegofTopoNode[top]=TopoSortGraphEdge[j][k];
                    }
                    else
                    {
                        indegofTopoNode[TopoSortGraphEdge[j][k]]=top;
                        top=TopoSortGraphEdge[j][k];
                    }
                }
            }
        }
    }
    return true;
}
//after SCC the function that get the topo sequence.
void getTopoSort4SCC()
{
    int toponodesize=NodeSizeofSCC;
    bool vs_matrixtopograph[MAX_NodeSize][MAX_NodeSize];
    for(int i=0; i<NodeSizeofInputGragh; i++)
    {
        fill(vs_matrixtopograph[i],vs_matrixtopograph[i]+NodeSizeofInputGragh,0);
    }
    for(int i=0; i<toponodesize; i++)
    {
        TopoSortGraphEdge[i].clear();
        rTopoSortGraphEdge[i].clear();
    }
    memset(indegofTopoNode,0,sizeof(indegofTopoNode));

    for(int i=0; i<NodeSizeofInputGragh; i++)
    {
        if(true==isValuedNode4BuildTopoSort[i])
        {
            for(int j=0; j<NodeSizeofInputGragh; j++)
            {
                if(j!=i)
                    if(true==isValuedNode4BuildTopoSort[j])
                    {
                        if(InputGraphMatix[i][j]>0)
                        {

                            if((ithnode_ithSCC[i]!=ithnode_ithSCC[j]))//&&(topoedge[ithnode_ithSCC[i]][ithnode_ithSCC[j]]==false))
                            {

                                if((false==vs_matrixtopograph[ithnode_ithSCC[i]][ithnode_ithSCC[j]]))
                                {

                                    TopoSortGraphEdge[ithnode_ithSCC[i]].push_back(ithnode_ithSCC[j]);
                                    rTopoSortGraphEdge[ithnode_ithSCC[j]].push_back(ithnode_ithSCC[i]);

                                    vs_matrixtopograph[ithnode_ithSCC[i]][ithnode_ithSCC[j]]=true;
                                    indegofTopoNode[ithnode_ithSCC[j]]++;

                                }

                            }
                        }
                    }
            }
        }
    }
    TopoOrder(toponodesize);
    for(int i=0; i<toponodesize; i++)
    {
        ithscc_ithtopo[ithtopo_ithscc[i]]=i;
    }
    seq_ithTopohaveNE[0]=ithscc_ithtopo[ithnode_ithSCC[Source]];
    int sequence=1;
    for(int i=0; i<toponodesize; i++)
    {
        if(0<ENnumofithSCC[ithtopo_ithscc[i]])
        {
            ithSCChaveNE_toposeq[ithtopo_ithscc[i]]=sequence;
            seq_ithTopohaveNE[sequence]=i;
            sequence++;
        }
    }
    ithSCChaveNE_toposeq[ithnode_ithSCC[Destination]]=sequence;
    seq_ithTopohaveNE[sequence]=ithscc_ithtopo[ithnode_ithSCC[Destination]];
    sequence++;
}



//judge the ithscc whether accessible to the destination without other scc having ENode
//求强连通图中哪些连通分量不经过有必经点的分量可以到达终点
void FindPathtoDestinTopoGraphremovedEN(int rdv)
{
    isFindPathtoDestinTopoGraphremovedEN[rdv]=true;
    for(unsigned int i=0; i<rTopoSortGraphEdge[rdv].size(); i++)
    {
        if(0<ENnumofithSCC[rTopoSortGraphEdge[rdv][i]])
        {
            isFindPathtoDestinTopoGraphremovedEN[rTopoSortGraphEdge[rdv][i]]=true;
        }
        if(false==isFindPathtoDestinTopoGraphremovedEN[rTopoSortGraphEdge[rdv][i]])
            if((rTopoSortGraphEdge[rdv][i]!=ithnode_ithSCC[Source])&&(0==ENnumofithSCC[rTopoSortGraphEdge[rdv][i]]))
                FindPathtoDestinTopoGraphremovedEN(rTopoSortGraphEdge[rdv][i]);
    }
}
//judge the ithnode whether accessible to the destination without other ENode
//求原图中哪些点不经过必经点可以到达终点
void FindPathtoDestinInitGraphremovedEN(int rdv)
{

    isFindPathtoDestinInitGraphremovedEN[rdv]=true;
    for(unsigned int i=0; i<rGraphList[rdv].size(); i++)
    {
        if((true==isENode[ithdispath][rGraphList[rdv][i]])&&(false==isFindPathtoDestinInitGraphremovedEN[rGraphList[rdv][i]]))
        {
            isFindPathtoDestinInitGraphremovedEN[rGraphList[rdv][i]]=true;

        }
        if(rGraphList[rdv][i]!=Source)
        {
            if((false==isFindPathtoDestinInitGraphremovedEN[rGraphList[rdv][i]])&&(false==isENode[ithdispath][rGraphList[rdv][i]]))
            {
                FindPathtoDestinInitGraphremovedEN(rGraphList[rdv][i]);
            }
        }

    }
}
//压缩点，将不必经点去掉，将原问题转变成TSP问题
void BuildTSPGraph()
{
    TSPNodesize=0;
    TSPNode[TSPNodesize]=Source;
    TSPNode_seq[Source]=TSPNodesize;
    TSPNodesize++;
    for(int i=0; i<ENodeSize; i++)
    {
        TSPNode[TSPNodesize]=ENode[i];
        TSPNode_seq[ENode[i]]=TSPNodesize;
        TSPNodesize++;
    }
    for(int i=0; i<TSPNodesize; i++)
    {
        TSPGraph[i].clear();
    }




    for(int i=0; i<TSPNodesize; i++)
    {
        //fill(tsp_d,tsp_d+NodeSizeofInputGragh,setMAXValue);
        fill(tsp_d,tsp_d+NodeSizeofInputGragh,-1);
        fill(tsp_vs,tsp_vs+NodeSizeofInputGragh,-1);
        priority_queue<P,vector<P>,greater<P> >midque;
        tsp_d[TSPNode[i]]=0;
        midque.push(P(0,TSPNode[i]));
        while(!midque.empty())
        {
            P p=midque.top();
            midque.pop();
            int v=p.second;
            if((v==Destination)||((v!=TSPNode[i])&&(true==isENode[ithdispath][v]))||(tsp_d[v]<p.first))
                continue;
            int u;
            for(unsigned int j=0; j<GraphList[v].size(); j++)
            {
                u=GraphList[v][j];
                if(-1==tsp_d[u])
                {
                    tsp_d[u]=tsp_d[v]+InputGraphMatix[v][u];
                    //tsp_d[u]=tsp_d[v]+1;
                    tsp_vs[u]=v;
                    midque.push(P(tsp_d[u],u));
                }
                else
                {
                    //if(tsp_d[u]>(tsp_d[v]+1))
                    if(tsp_d[u]>(tsp_d[v]+InputGraphMatix[v][u]))
                    {
                        tsp_d[u]=tsp_d[v]+InputGraphMatix[v][u];
                        //tsp_d[u]=tsp_d[v]+1;
                        tsp_vs[u]=v;
                        midque.push(P(tsp_d[u],u));
                    }
                }
            }
        }
        int pre,next,costsum,hop;
        int icci=ithnode_ithSCC[TSPNode[i]];
        int iccj;
        int j;
        for(int l=0; l<TSPNodesize; l++)
        {
            if(i==0&&l==0)
            {
                continue;
            }
            if(l==0)
            {
                j=Destination;
            }
            else
                j=TSPNode[l];

            if((tsp_vs[j]!=-1))//&&((true==isENode[j])||j==destination))
            {
                iccj=ithnode_ithSCC[j];
                if((icci==iccj)||((ithSCChaveNE_toposeq[icci]+1)==ithSCChaveNE_toposeq[iccj]))
                {
                    //cout<<node_index[PreNode[i]]<<"   "<<node_index[j]<<"fff"<<endl;
                    //    Enodeoutdegree[PreNode[i]]++;
                    //    Enodeindegree[j]++;
                    pre=j;
                    hop=0;
                    costsum=0;
                    memset(fq.vsnode,-1,sizeof(fq.vsnode));
                    //memset(fq.bit,0,sizeof(fq.bit));
                    next=tsp_vs[pre];
                    fq.vsnode[pre]=next;
                    //b1=pre/8;
                    //  b2=pre%8;
                    //fq.bit[b1]|=(1<<b2);
                    while(-1!=next)
                    {
                        costsum+=InputGraphMatix[next][pre];
                        pre=next;
                        hop++;
                        next=tsp_vs[pre];
                        fq.vsnode[pre]=next;
                        /*
                         if(next!=-1){
                             b1=pre/8;
                             b2=pre%8;
                             fq.bit[b1]|=(1<<b2);
                         }
                         */
                    }
                    fq.id=j;
                    fq.hop=hop;
                    fq.costsum=costsum;
                    TSPGraph[i].push_back(fq);
                }
            }
        }
    }

    if(debug_showdijkstrainfo)
    {
        cout<<"debug_______________________"<<endl;
        for(int i=0; i<TSPNodesize; i++)
        {
            cout<<"index ("<<node_index[TSPNode[i]]<<")  :";
            for(unsigned int j=0; j<TSPGraph[i].size(); j++)
            {
                cout<<node_index[TSPGraph[i][j].id]<<"("<<TSPGraph[i][j].costsum<<")   ";

            }
            cout<<endl;
        }

    }


}


void dfs4EnodeTSP(int start,int enodenum,int costsum)//,unsigned int bit[19])
{
    bool isbreak;
    int seq_preque;
    int mid_seqid;
    int mid_cost;
    int back_vs[MAX_NodeSize];
    int outdegreeofTSPid;
    int mid_top;
    //cout<<start<<"         "<<enodenum<<endl;
    if(false==isendTSPDFS)
    {
        return ;
    }
    /*
    ftime(&OverTime);
    if(10<=(OverTime.time-StartTime.time))
    {
        isTSPendDFS=false;
        return ;
    }
    */
    memcpy(back_vs,vs_TSPDFS,NodeSizeofInputGragh*4);

    seq_preque=TSPNode_seq[start];
    outdegreeofTSPid=TSPGraph[seq_preque].size();
    //剪枝，去掉一些点的出边
    // outdegreeofTSPid=(outdegreeofTSPid/10)+1;
    outdegreeofTSPid=ceil(outdegreeofTSPid*prunewidth*1.0);

    for(int i=0; i<outdegreeofTSPid; i++)
    {
        if(true==isendTSPDFS)
        {
            isbreak=true;
            dp=TSPGraph[seq_preque][i];
            if(dp.id!=Destination)
            {
                for(int j=0; j<NodeSizeofInputGragh; j++)
                {
                    if((-1!=vs_TSPDFS[j])&&(-1!=(dp.vsnode[j])))
                    {
                        isbreak=false;
                        break;
                    }
                    //ansvs[j]=dp.vsnode[j];
                }
                if(isbreak)
                {
                    if((enodenum+1)==ENodeSize)
                    {
                        if(isFindPathtoDestinInitGraphremovedEN[dp.id]&&isFindPathtoDestinTopoGraphremovedEN[ithnode_ithSCC[dp.id]])
                        {
                            mid_seqid=TSPNode_seq[dp.id];
                            mid_cost=costsum+dp.costsum;
                            for(unsigned int j=0; j<TSPGraph[mid_seqid].size(); j++)
                            {
                                sp=TSPGraph[mid_seqid][j];
                                if(sp.id==Destination)
                                {
                                    break;
                                }

                            }
                            if(sp.vsnode[Destination]!=-1)
                            {
                                for(int j=0; j<NodeSizeofInputGragh; j++)
                                {
                                    if((-1!=vs_TSPDFS[j])&&(-1!=(sp.vsnode[j])))
                                    {
                                        isbreak=false;
                                        break;
                                    }
                                    //ansvs[j]=dp.vsnode[j];
                                }
                                if(isbreak)
                                {
                                    mid_top=dp.hop+sp.hop;
                                    for(int j=0; j<NodeSizeofInputGragh; j++)
                                    {
                                        if(-1!=(dp.vsnode[j]))
                                        {
                                            vs_TSPDFS[j]=dp.vsnode[j];
                                            mid_top--;
                                            if(0==mid_top)
                                                break;
                                        }
                                        if(-1!=(sp.vsnode[j]))
                                        {
                                            vs_TSPDFS[j]=sp.vsnode[j];
                                            mid_top--;
                                            if(0==mid_top)
                                                break;
                                        }
                                    }
                                    isendTSPDFS=false;
                                    mid_cost+=sp.costsum;
                                    if(ResultValueMinCost>mid_cost)
                                    {
                                        ResultValueMinCost=mid_cost;
                                        if(!ResultPath.empty())
                                            ResultPath.clear();
                                        ResultPath.push_back(Destination);
                                        for(int pathi=vs_TSPDFS[Destination]; -1!=pathi; pathi=vs_TSPDFS[pathi])
                                        {
                                            ResultPath.push_back(pathi);

                                        }
                                        succeedFindDisjointPath=true;
                                    }
                                    memcpy(vs_TSPDFS,back_vs,NodeSizeofInputGragh*4);
                                }

                            }
                        }
                    }
                    else
                    {
                        mid_top=dp.hop;
                        for(int j=0; j<NodeSizeofInputGragh; j++)
                        {
                            if(-1!=(dp.vsnode[j]))
                            {
                                vs_TSPDFS[j]=dp.vsnode[j];
                                mid_top--;
                                if(0==mid_top)
                                    break;
                            }
                        }
                        dfs4EnodeTSP(dp.id,enodenum+1,costsum+dp.costsum);
                        memcpy(vs_TSPDFS,back_vs,NodeSizeofInputGragh*4);
                    }
                }
            }
        }
    }
    return ;
}

//TSP问题进行DFS求路径
void TSPdfs()
{
    ResultValueMinCost=setMAXValue;
    BuildTSPGraph();
    //why i==1
    for(int i=1; i<TSPNodesize; i++)
        sort(TSPGraph[i].begin(),TSPGraph[i].end());
    // unsigned int bit[19];
    fill(vs_TSPDFS,vs_TSPDFS+NodeSizeofInputGragh,-1);
    //fill(bit,bit+19,0);
    isendTSPDFS=true;
    //unsigned int bit1,bit2;
    //bit1=source/8;
    //bit2=source%8;
    //bit[bit1]|=(1<<bit2);
    dfs4EnodeTSP(Source,0,0);//,bit);
    //cout<<"minimumcost:    "<<minimumcost<<endl;

}

void mainalgorithm()
{
    //输出的两条路径
    unsigned short result[2][MAX_NodeSize];//P'路径
    int resultlength[2];
    // unsigned short result2[];//P''路径
    DealwithInputGraph();
    if(debug_showresult)
    {
        DebugPrint();
    }
    if(last_suceedFindDisjointPath==true)
        last_suceedFindDisjointPath=InitJudgeGraphisCorrect();
    GraphArraytoList();
    for(int l=0; (l<(inputdemand_num+1))&&(true==last_suceedFindDisjointPath); l++)
    {


        ithdispath=l%2;
        ENodeSize=demandEnodeSize[ithdispath];
        for(int i=0; i<ENodeSize; i++)
        {
            ENode[i]=demandEnode[ithdispath][i];
        }

        NodeSizeofSCC=SCCMainFunction();
        if((isDfstodest==false)||(isdfsAllENSCC!=ENodeSize))
            last_suceedFindDisjointPath=false;

        if(true==last_suceedFindDisjointPath)
        {
            if(debug_showscc)
            {
                cout<<"ordnode_ordSCC:";
                for(int i=0; i<NodeSizeofInputGragh; i++)
                {
                    cout<<ithnode_ithSCC[i]<<" ";
                }
                cout<<endl;
            }

            getTopoSort4SCC();
            memset(isFindPathtoDestinInitGraphremovedEN,0,sizeof(isFindPathtoDestinInitGraphremovedEN));
            FindPathtoDestinInitGraphremovedEN(Destination);
            memset(isFindPathtoDestinTopoGraphremovedEN,0,sizeof(isFindPathtoDestinTopoGraphremovedEN));
            FindPathtoDestinTopoGraphremovedEN(ithnode_ithSCC[Destination]);
            if(!ResultPath.empty())
                ResultPath.clear();
            TSPdfs();
            last_suceedFindDisjointPath=succeedFindDisjointPath;

        }

        if(true==last_suceedFindDisjointPath)
        {
            int j=0;
            int mid_heusum=0;
            int intersectionsum=0;
            int costsum=0;
            for(int i=ResultPath.size()-1; i>=1; i--,j++)
            {
                result[ithdispath][j]=FlagofInputGraphEdgeMatrix[ResultPath[i]][ResultPath[i-1]];
                //mid_heusum+=InputGraphMatix[ResultPath[i]][ResultPath[i-1]];
                if(2==l)
                {
                    if((-1==EdgeofFirstPath[ResultPath[i]][ResultPath[i-1]])&&(0==InputMaxGraphMatix[ResultPath[i]][ResultPath[i-1]]))
                    {
                        intersectionsum++;

                    }
                    costsum+=DebugInputGraphMatrix[ResultPath[i]][ResultPath[i-1]];
                }
                if(0==l)
                    costsum+=DebugInputGraphMatrix[ResultPath[i]][ResultPath[i-1]];
                if(1==l)
                {
                    if(InputGraphMatix[ResultPath[i]][ResultPath[i-1]]>DebugInputMaxGraphMatix[ResultPath[i]][ResultPath[i-1]])
                    {
                        costsum+=DebugInputGraphMatrix[ResultPath[i]][ResultPath[i-1]];
                    }
                    else
                    {
                        costsum+=InputGraphMatix[ResultPath[i]][ResultPath[i-1]];
                    }
                }

            }
            mid_heusum=costsum;
            resultlength[ithdispath]=ResultPath.size()-1;

            if(debug_showresult&&(!ResultPath.empty()))
            {
                cout<<"path_node("<<l<<") :";
                for(unsigned int i=0; i<(ResultPath.size()); i++)
                {
                    if(i!=0)
                        cout<<" | ";
                    cout<<node_index[ResultPath[i]];
                }
                cout<<endl;
                cout<<"path_edge("<<l<<") :";
                for(unsigned int i=0; i<(ResultPath.size()-1); i++)
                {
                    if(i!=0)
                        cout<<"|";
                    cout<<result[ithdispath][i];
                }
                cout<<endl;
                cout<<"costsum:  "<<costsum<<endl<<endl;;
                if(l==2)
                {
                    cout<<"coincidentEdgeNum: "<<intersectionsum<<endl;
                }
            }
            if(l<2)
                heuristicAddGraphEdgeValue((mid_heusum>100?mid_heusum:100));//mid_heusum+
        }

    }

    /*****************************************************************************************/
    if(true==last_suceedFindDisjointPath)
    {
        for(int i=0; i<resultlength[0]; i++)
        {
            record_result(WORK_PATH, result[0][resultlength[0]-i-1]);
        }
        for(int i=0; i<resultlength[1]; i++)
        {
            record_result(BACK_PATH, result[1][resultlength[1]-i-1]);
        }
    }
}

void StoreEdgeInformation(int EdgeFlag,int inEdgeFlag,int outEdgeflag,int EdgeWeight)
{
    if((inEdgeFlag!=Destination)&&(outEdgeflag!=Source)&&(inEdgeFlag!=outEdgeflag))
    {
        if(0==InputGraphMatix[inEdgeFlag][outEdgeflag])
        {
            InputGraphMatix[inEdgeFlag][outEdgeflag]=EdgeWeight;
            FlagofInputGraphEdgeMatrix[inEdgeFlag][outEdgeflag]=EdgeFlag;
            EdgeSizeofInputGragh++;

            DebugInputGraphMatrix[inEdgeFlag][outEdgeflag]=InputGraphMatix[inEdgeFlag][outEdgeflag];
            DebugFlagofInputGraphEdgeMatrix[inEdgeFlag][outEdgeflag]= FlagofInputGraphEdgeMatrix[inEdgeFlag][outEdgeflag];
        }
        else
        {
            if(0==InputMaxGraphMatix[inEdgeFlag][outEdgeflag])
            {
                InputMaxGraphMatix[inEdgeFlag][outEdgeflag]=EdgeWeight;
                FlagofInputMaxGraphEdgeMatrix[inEdgeFlag][outEdgeflag]=EdgeFlag;
                EdgeSizeofInputGragh++;

                DebugInputMaxGraphMatix[inEdgeFlag][outEdgeflag]= InputMaxGraphMatix[inEdgeFlag][outEdgeflag];
                DebugFlagofInputMaxGraphEdgeMatrix[inEdgeFlag][outEdgeflag]= FlagofInputMaxGraphEdgeMatrix[inEdgeFlag][outEdgeflag];
            }
            else
            {
                if(InputMaxGraphMatix[inEdgeFlag][outEdgeflag]>EdgeWeight)
                {
                    InputMaxGraphMatix[inEdgeFlag][outEdgeflag]=EdgeWeight;
                    FlagofInputMaxGraphEdgeMatrix[inEdgeFlag][outEdgeflag]=EdgeFlag;

                    DebugInputMaxGraphMatix[inEdgeFlag][outEdgeflag]= InputMaxGraphMatix[inEdgeFlag][outEdgeflag];
                    DebugFlagofInputMaxGraphEdgeMatrix[inEdgeFlag][outEdgeflag]= FlagofInputMaxGraphEdgeMatrix[inEdgeFlag][outEdgeflag];
                }
            }
            if(InputGraphMatix[inEdgeFlag][outEdgeflag]>InputMaxGraphMatix[inEdgeFlag][outEdgeflag])
            {
                int midInput;
                midInput=InputMaxGraphMatix[inEdgeFlag][outEdgeflag];
                InputMaxGraphMatix[inEdgeFlag][outEdgeflag]=InputGraphMatix[inEdgeFlag][outEdgeflag];
                InputGraphMatix[inEdgeFlag][outEdgeflag]=midInput;
                midInput= FlagofInputMaxGraphEdgeMatrix[inEdgeFlag][outEdgeflag];
                FlagofInputMaxGraphEdgeMatrix[inEdgeFlag][outEdgeflag]=FlagofInputGraphEdgeMatrix[inEdgeFlag][outEdgeflag];
                FlagofInputGraphEdgeMatrix[inEdgeFlag][outEdgeflag]=midInput;

                DebugInputGraphMatrix[inEdgeFlag][outEdgeflag]=InputGraphMatix[inEdgeFlag][outEdgeflag];
                DebugInputMaxGraphMatix[inEdgeFlag][outEdgeflag]= InputMaxGraphMatix[inEdgeFlag][outEdgeflag];
                DebugFlagofInputGraphEdgeMatrix[inEdgeFlag][outEdgeflag]= FlagofInputGraphEdgeMatrix[inEdgeFlag][outEdgeflag];
                DebugFlagofInputMaxGraphEdgeMatrix[inEdgeFlag][outEdgeflag]= FlagofInputMaxGraphEdgeMatrix[inEdgeFlag][outEdgeflag];
            }
        }
    }


}

//你要完成的功能总入口
void search_route(char *topo[MAX_EDGE_NUM], int edge_num, char *demand[MAX_DEMAND_NUM], int demand_num)
{
    int midInput;
    inputedge_num=edge_num;
    inputdemand_num=demand_num;
    ftime(&StartTime);
    cout<<"franz algorithm begin"<<endl;
    //初始化一些变量
    NodeSizeofInputGragh=0;
    EdgeSizeofInputGragh=0;
    ENodeSize=0;
    last_suceedFindDisjointPath=true;
    memset(index_node,-1,sizeof(index_node));
    memset(node_index,-1,sizeof(node_index));

    for(int i=0; i<demand_num; i++)
    {
        int demi=0;
        int demandStrLen=strlen(demand[i]);
        for(midInput=0; demi<demandStrLen; demi++)
        {
            if(!(('0'<=demand[i][demi])&&('9'>=demand[i][demi])))
            {
                demi++;
                break;
            }
            midInput=midInput*10+(demand[i][demi]-'0');
        }


        for(midInput=0; demi<demandStrLen; demi++)
        {
            if(!(('0'<=demand[i][demi])&&('9'>=demand[i][demi])))
            {
                demi++;
                break;
            }
            midInput=midInput*10+(demand[i][demi]-'0');
        }

        //Source = midInput;
        Destination = midInput;
        for(midInput=0; demi<demandStrLen; demi++)
        {
            if(!(('0'<=demand[i][demi])&&('9'>=demand[i][demi])))
            {
                demi++;
                break;
            }
            midInput=midInput*10+(demand[i][demi]-'0');
        }
        //Destination = midInput;
        Source = midInput;

        if(-1==index_node[Source])
        {

            index_node[Source]=NodeSizeofInputGragh;
            node_index[NodeSizeofInputGragh]=Source;
            Source=NodeSizeofInputGragh;
            isValuedNode[NodeSizeofInputGragh]=true;
            NodeSizeofInputGragh++;

        }
        else
        {
            Source=index_node[Source];
        }

        if(-1==index_node[Destination])
        {

            index_node[Destination]=NodeSizeofInputGragh;
            node_index[NodeSizeofInputGragh]=Destination;
            Destination=NodeSizeofInputGragh;
            isValuedNode[NodeSizeofInputGragh]=true;
            NodeSizeofInputGragh++;

        }
        else
        {
            Destination=index_node[Destination];
        }

        bool havanum=false;
        for(midInput=0; demi<=demandStrLen; demi++)
        {
            if(((!(('0'<=demand[i][demi])&&('9'>=demand[i][demi])))&&havanum)||((demi==(demandStrLen))&&havanum))//(demand[i][demi]==',')||(demand[i][demi]=='|')||(demi==(demandStrLen-1)))
            {
                index_node[midInput]=NodeSizeofInputGragh;
                node_index[NodeSizeofInputGragh]=midInput;
                demandEnode[i][demandEnodeSize[i]]=NodeSizeofInputGragh;
                isENode[i][NodeSizeofInputGragh]=true;
                isValuedNode[NodeSizeofInputGragh]=1;
                midInput=0;
                NodeSizeofInputGragh++;
                demandEnodeSize[i]++;
                havanum=false;

            }
            else
            {
                if(('0'<=demand[i][demi])&&('9'>=demand[i][demi]))
                {
                    havanum=true;
                    midInput=midInput*10+(demand[i][demi]-'0');
                }
            }

        }
    }
    for(int i=0; i<edge_num; i++)
    {
        int EdgeFlag,inEdgeFlag,outEdgeflag,EdgeWeight;
        int j=0;
        midInput=0;
        int ithtopoSize=strlen(topo[i]);
        for(; j<ithtopoSize; j++)
        {
            if(!(('0'<=topo[i][j])&&('9'>=topo[i][j])))
            {
                j++;
                break;
            }
            midInput=midInput*10+(topo[i][j]-'0');
        }
        EdgeFlag=midInput;

        midInput=0;
        for(; j<ithtopoSize; j++)
        {
            if(!(('0'<=topo[i][j])&&('9'>=topo[i][j])))
            {
                j++;
                break;
            }
            midInput=midInput*10+(topo[i][j]-'0');
        }

        if(-1==index_node[midInput])
        {
            index_node[midInput]=NodeSizeofInputGragh;
            node_index[NodeSizeofInputGragh]=midInput;
            isValuedNode[NodeSizeofInputGragh]=1;
            NodeSizeofInputGragh++;
        }
        inEdgeFlag=index_node[midInput];

        midInput=0;
        for(; j<ithtopoSize; j++)
        {
            if(!(('0'<=topo[i][j])&&('9'>=topo[i][j])))
            {
                j++;
                break;
            }
            midInput=midInput*10+(topo[i][j]-'0');
        }

        if(-1==index_node[midInput])
        {
            index_node[midInput]=NodeSizeofInputGragh;
            node_index[NodeSizeofInputGragh]=midInput;
            isValuedNode[NodeSizeofInputGragh]=1;
            NodeSizeofInputGragh++;

        }
        outEdgeflag=index_node[midInput];
        midInput=0;
        for(; j<ithtopoSize; j++)
        {
            if(!(('0'<=topo[i][j])&&('9'>=topo[i][j])))
            {
                j++;
                break;
            }
            midInput=midInput*10+(topo[i][j]-'0');
        }

        EdgeWeight=midInput;

        int exchange=inEdgeFlag;
        inEdgeFlag=outEdgeflag;
        outEdgeflag=exchange;
        StoreEdgeInformation(EdgeFlag,inEdgeFlag,outEdgeflag,EdgeWeight);
    }

    mainalgorithm();
}

