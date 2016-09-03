#include "route.h"
#include "lib_record.h"
#include"sys/timeb.h"


#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include<algorithm>
#include<queue>
#include <iostream>
#include<vector>

using namespace std;



/*
1=<Weight value<=20
weight value is integer
No loop link
Multiple link with different weight
|vertices|<=600,|outer degree|<=8,sparse graph
|Essential vertices|<=50
Directed loop graph

some tips advance the algorithm:
0.reverse the whole graph
1.prepush some node
4.water drop method

*/

//Maximum number of edges.
#define MAX_EV (55)
//50

//Maximum number of nodes.
#define MAX_V (666)
//600
//parameters for debuging
bool debug_showinputstr=true;
bool debug_showbfsqueue=false;
bool debug_showinitmatrix=false;
bool debug_showresultpath=true;
bool debug_showtopoinfo=true;
bool debug_write=true;
bool debug_showbfsfor14or15=false;

const int BigNode=500;

//node number of initial graph
int Node4InitInputGragh;
//edge number of initial graph
int Edg4InitInputGragh;

//the initial graph's topological structure(matrix)
int InitGraphMatix[MAX_V][MAX_V];
//the initial graph's topological structure's index(matrix)
int Index4InitGraphEdgeMatix[MAX_V][MAX_V];
//judge the node whether useless after cleaning useless node.
int ExistNodeafterscaledown[MAX_V];

int index_node[MAX_V];
int node_index[MAX_V];

int ENode[MAX_EV];
bool isENode[MAX_V];
int ENodeSize;

int source,destination;
bool beabletofindpath;
bool initsetparavalue=false;


//properties of every nodes
struct degee4node
{
    int outdegreeofNE;
    int outdegreeofE;
    //int indegreeofNE;
    // int indegreeofE;
} degree4node[MAX_V];

//the parameters about SCC
vector<int>SCC_G[MAX_V];
vector<int>SCC_rG[MAX_V];
vector<int>SCC_vs;
bool SCC_used[MAX_V];
int ithnode_ithSCC[MAX_V];
int SCCSize;
int ENodesnumofithSCC[MAX_V];
int SCCDFSAllENode;
bool vs_firstsccdfs;
bool isDFStodestination;
bool used4toposort[MAX_V];
int ithscc_ithtopo[MAX_V];

//the parameters about topological sort of DAG
int indegreeofTopoNode[MAX_V];
vector<int>TopoEdge[MAX_V];
vector<int>rTopoEdge[MAX_V];
int ithtopo_ithscc[MAX_V];
int ithscchaveNE_seq[MAX_V];
int seqithscchaveNE_ithtopo[MAX_V];

bool scc_istoanodeFromDestwithouthaveEN[MAX_V];
bool istoanodeFromDestwithouthaveEN[MAX_V];
bool istoanodeFromDest[MAX_V];

bool debug_isruningsearch_route=true;
//debug function
struct timeb starttime;
struct timeb endtime;

//int timelimite;
void DebugPrint()
{
    cout<<"source node:"<<node_index[source]<<"  destination node:"<<node_index[destination]<<endl;
    cout<<"essential node"<<ENodeSize<<":";
    for(int i=0; i<ENodeSize; i++)
    {
        cout<<node_index[ENode[i]]<<" ";
    }
    cout<<endl;
    cout<<"node size:"<<Node4InitInputGragh<<endl;
    cout<<"edge size:"<<Edg4InitInputGragh<<endl;

    if(debug_showinitmatrix)
    {
        cout<<"Initmatrix:"<<endl;
        for(int i=0; i<Node4InitInputGragh; i++)
        {
            cout<<node_index[i]<<" ";
        }
        cout<<endl;
        cout<<endl;

        for(int i=0; i<Node4InitInputGragh; i++)
        {
            for(int j=0; j<Node4InitInputGragh; j++)
            {
                cout<<InitGraphMatix[i][j]<<" ";
            }
            cout<<endl;
        }
    }

    cout<<"SCC     "<<SCCSize<<"   :";
    for(int i=0; i<Node4InitInputGragh; i++)
    {
        cout<<ithnode_ithSCC[i]<<" ";
    }
    cout<<endl;
    cout<<"ithSCChaveNE"<<":";
    for(int i=0; i<Node4InitInputGragh; i++)
    {
        cout<<ENodesnumofithSCC[ithnode_ithSCC[i]]<<" ";
    }
    cout<<endl;
}
//wrap some node whose indgree lower than 1 or outdgree than 1.
void scaledowngragh()
{
    bool judgeindegree,judgeoutdegree;
    judgeindegree=judgeoutdegree=false;
    for(int k=2; k<Node4InitInputGragh; k++)
    {
        for(int i=0; i<Node4InitInputGragh; i++)
        {
            //attention
            if(0<InitGraphMatix[k][i])
                judgeoutdegree=true;
        }
        for(int j=0; j<Node4InitInputGragh; j++)
        {
            if(0<InitGraphMatix[j][k])
                judgeindegree=true;
        }
        if((false==judgeindegree)||(false==judgeoutdegree))
        {
            ExistNodeafterscaledown[k]=0;
        }
    }
}
//judge the graph whether succeed to find path or not.
bool judgegraph()
{
    bool judgeindegree,judgeoutdegree;
    judgeindegree=judgeoutdegree=false;
    for(int j=0; j<Node4InitInputGragh; j++)
    {
        if(0<InitGraphMatix[source][j])
            judgeoutdegree=true;
    }
    if(false==judgeoutdegree)
        return false;
    for(int i=0; i<Node4InitInputGragh; i++)
    {
        if(0<InitGraphMatix[i][destination])
            judgeindegree=true;
    }
    if(false==judgeindegree)
        return false;
    for(int k=0; k<ENodeSize; k++)
    {
        if(0==ExistNodeafterscaledown[ENode[k]])
        {
            return false;
        }
    }
    return true;
}
//before finding path clear and set the parameter's value
void initcleardata()
{
    Node4InitInputGragh=0;
    Edg4InitInputGragh=0;
    memset(index_node,-1,sizeof(index_node));
    memset(node_index,-1,sizeof(node_index));
    if(initsetparavalue)
    {
        memset(InitGraphMatix,0,sizeof(InitGraphMatix));
        memset(Index4InitGraphEdgeMatix,0,sizeof(Index4InitGraphEdgeMatix));
        memset(ExistNodeafterscaledown,0,sizeof(ExistNodeafterscaledown));
        memset(isENode,0,sizeof(isENode));
    }
    return ;

}
//add edge to the graph(vector)
void add_edge(int from,int to)
{
    SCC_G[from].push_back(to);
    SCC_rG[to].push_back(from);
    /*
    if(true==isENode[from])
    {
        degree4node[to].indegreeofNE++;
    }
    else
    {
        degree4node[to].indegreeofE++;
    }
    */
    if(true==isENode[to])
    {
        degree4node[from].outdegreeofNE++;
    }
    else
    {
        degree4node[from].outdegreeofE++;
    }
}
//compare function for ksort function(quick sort)
bool cmp(int st,int ed )
{
    if((true==isENode[st])&&(true==isENode[ed]))
    {
        if(degree4node[st].outdegreeofNE>degree4node[ed].outdegreeofNE)
        {
            return false;
        }
        else
        {
            if(degree4node[st].outdegreeofNE<degree4node[ed].outdegreeofNE)
            {
                return true;
            }
            else
            {
                if(degree4node[st].outdegreeofE<degree4node[ed].outdegreeofE)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
    }
    if((true==isENode[st]))
    {
        return false;
    }
    if(true==isENode[ed])
    {
        return true;
    }
    if(degree4node[st].outdegreeofNE>degree4node[ed].outdegreeofNE)
    {
        return false;
    }
    else
    {
        if(degree4node[st].outdegreeofNE<degree4node[ed].outdegreeofNE)
        {
            return true;
        }
        else
        {
            if(degree4node[st].outdegreeofE<degree4node[ed].outdegreeofE)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
}
//my own implementation of quick sort
void ksort(int l,int h,vector<int>a)
{
    if(h<(l+2))
        return ;
    int e=h,p=l;
    while(l<h)
    {
        while(++l<e&&cmp(a[l],a[p]));
        while(--h<p&&(cmp(a[p],a[h])));
        if(l<h)
        {
            swap(a[l],a[h]);
        }


    }
    swap(a[h],a[p]);
    ksort(p,h,a);
    ksort(l,e,a);
}
//change the physical structure of the graph(matrix->list)
void convertMatrixtoList()
{
    if(initsetparavalue)
    {
        for(int i=0; i<Node4InitInputGragh; i++)
        {
            if(ExistNodeafterscaledown[i]>0)
            {
                SCC_G[i].clear();
            }
        }
    }
    for(int i=0; i<Node4InitInputGragh; i++)
    {
        if(ExistNodeafterscaledown[i]>0)
        {
            for(int j=0; j<Node4InitInputGragh; j++)
            {
                if(i!=j)
                    if(ExistNodeafterscaledown[j]>0)
                    {
                        if(InitGraphMatix[i][j]>0)
                        {
                            add_edge(i,j);
                        }
                    }
            }
        }
    }
//   for(int i=0; i<Node4InitInputGragh; i++)
    //  {
    //      if(ExistNodeafterscaledown[i]>0)
    //      {
    //          ksort(0,SCC_G[i].size(),SCC_G[i]);
    //     }
    //  }
}
//forward dfs function for SCC
void ccdfs(int v)
{
    if(true==vs_firstsccdfs)
    {
        used4toposort[v]=true;
        if(true==isENode[v])
            SCCDFSAllENode++;
        if(v==destination)
        {
            isDFStodestination=true;
        }
    }
    SCC_used[v]=true;
    for(int i=0; i<SCC_G[v].size(); i++)
    {
        if(!SCC_used[SCC_G[v][i]])
            ccdfs(SCC_G[v][i]);
    }
    SCC_vs.push_back(v);
}
//reverse dfs function for SCC
void ccrdfs(int v,int k)
{
    SCC_used[v]=true;
    ithnode_ithSCC[v]=k;
    for(int i=0; i<SCC_rG[v].size(); i++)
    {
        if(!SCC_used[SCC_rG[v][i]]) ccrdfs(SCC_rG[v][i],k);
    }
}
//main function for SCC
int scc()
{
    vs_firstsccdfs=true;
    isDFStodestination=false;
    SCCDFSAllENode=0;
    if(true==initsetparavalue)
    {
        memset(SCC_used,0,sizeof(SCC_used));
        memset(used4toposort,0,sizeof(used4toposort));
        SCC_vs.clear();
    }
    for(int v=0; v<Node4InitInputGragh; v++)
    {
        //if(existNode[v]>0)
        if(!SCC_used[v])ccdfs(v);
        vs_firstsccdfs=false;
    }
    memset(SCC_used,0,sizeof(SCC_used));
    int k=0;
    for(int i=SCC_vs.size()-1; i>=0; i--)
    {
        // if(existNode[vs[i]]>0)
        if(!SCC_used[SCC_vs[i]])ccrdfs(SCC_vs[i],k++);
    }
    if(true==initsetparavalue)
        memset(ENodesnumofithSCC,0,sizeof(ENodesnumofithSCC));
    for(int i=0; i<ENodeSize; i++)
    {
        ENodesnumofithSCC[ithnode_ithSCC[ENode[i]]]++;
    }
    return k;
}
//topo sort function
bool TopoOrder(int topon)
{
    int i,top=-1;
    int sorti=0;
    for(i=(topon-1); i>=0; i--)
    {
        if(0==indegreeofTopoNode[i])
        {
            indegreeofTopoNode[i]=top;
            top=i;
        }
    }
    if(debug_showtopoinfo)
        cout<<"TopoSort "<<topon<<"  :";
    for(i=0; i<topon; ++i)
    {
        if(top==-1)
        {
            if(debug_write)
                cout<<"The graph is not DAG"<<endl;
            return false;
        }
        int j=top;
        ithtopo_ithscc[sorti]=j;
        if(debug_showtopoinfo)
            cout<<j<<" ";
        sorti++;
        top=indegreeofTopoNode[top];
        int topofirstenode=false;
        int exmid=0;
        for(int k=0; k<TopoEdge[j].size(); ++k)
        {
            if((0==(--indegreeofTopoNode[TopoEdge[j][k]])))
            {
                if(false==topofirstenode)
                {
                    indegreeofTopoNode[TopoEdge[j][k]]=top;
                    top=TopoEdge[j][k];
                    topofirstenode=true;
                }
                else
                {
                    if(ENodesnumofithSCC[top]>0)
                    {
                        exmid=indegreeofTopoNode[top];
                        indegreeofTopoNode[TopoEdge[j][k]]=exmid;
                        indegreeofTopoNode[top]=TopoEdge[j][k];
                    }
                    else
                    {
                        indegreeofTopoNode[TopoEdge[j][k]]=top;
                        top=TopoEdge[j][k];
                    }
                }
            }
        }
    }
    if(debug_showtopoinfo)
        cout<<endl;
}
//after SCC the function that get the topo sequence.
void getTopoSort4SCC()
{
    bool vs_matrixtopograph[MAX_V][MAX_V];
    int toponodesize=SCCSize;
    if(true==initsetparavalue)
    {
        memset(TopoEdge,0,sizeof(TopoEdge));
        memset(indegreeofTopoNode,0,sizeof(indegreeofTopoNode));
    }
    for(int i=0; i<Node4InitInputGragh; i++)
    {
        if(true==used4toposort[i])
        {
            for(int j=0; j<Node4InitInputGragh; j++)
            {
                if(i!=j)
                    if(true==used4toposort[j])
                    {
                        if(InitGraphMatix[i][j]>0)
                        {
                            if((ithnode_ithSCC[i]!=ithnode_ithSCC[j])&&(false==vs_matrixtopograph[ithnode_ithSCC[i]][ithnode_ithSCC[j]]))//&&(topoedge[ithnode_ithSCC[i]][ithnode_ithSCC[j]]==false))
                            {
                                TopoEdge[ithnode_ithSCC[i]].push_back(ithnode_ithSCC[j]);//=true;
                                rTopoEdge[ithnode_ithSCC[j]].push_back(ithnode_ithSCC[i]);
                                vs_matrixtopograph[ithnode_ithSCC[i]][ithnode_ithSCC[j]]=true;
                                indegreeofTopoNode[ithnode_ithSCC[j]]++;
                            }
                        }
                    }
            }
        }
    }
    if(debug_showtopoinfo)
    {
        cout<<"topoindgree:";
        for(int i=0; i<toponodesize; i++)
        {
            cout<<indegreeofTopoNode[i]<<" ";
        }
        cout<<endl;
        for(int i=0; i<toponodesize; i++)
        {
            for(int j=0; j<TopoEdge[i].size(); j++)
            {
                cout<<TopoEdge[i][j]<<" ";
            }
            cout<<endl;
        }
    }
    TopoOrder(toponodesize);
    for(int i=0; i<toponodesize; i++)
    {
        ithscc_ithtopo[ithtopo_ithscc[i]]=i;
    }
    seqithscchaveNE_ithtopo[0]=ithscc_ithtopo[source];
    //seqnumofitscchaveNE[ithnode_ithSCC[source]]=0;
    //seqnumofitscchaveNE[ithnode_ithSCC[destination]]=seq;
    int seq=1;
    for(int i=0; i<toponodesize; i++)
    {
        if(ENodesnumofithSCC[ithtopo_ithscc[i]]>0)
        {
            ithscchaveNE_seq[ithtopo_ithscc[i]]=seq;
            seqithscchaveNE_ithtopo[seq]=i;//ithtopo_ithscc[ithnode_ithSCC[i]];
            seq++;
        }
    }
    ithscchaveNE_seq[ithnode_ithSCC[destination]]=seq;
    seqithscchaveNE_ithtopo[seq]=ithscc_ithtopo[ithnode_ithSCC[destination]];
    seq++;
    if(debug_showtopoinfo)
    {
        cout<<"toposort:";
        for(int i=0; i<toponodesize; i++)
        {
            cout<<ithtopo_ithscc[i]<<" ";
        }
        cout<<endl;
        cout<<"ithSCCtoithTopo:";
        for(int i=0; i<toponodesize; i++)
        {
            cout<<ithscc_ithtopo[i]<<" ";
        }
        cout<<endl;
        cout<<"seqnumofitscchaveNE:";
        for(int i=0; i<toponodesize; i++)
        {
            cout<<ithscchaveNE_seq[i]<<" ";
        }
        cout<<endl;
        cout<<"seqscctoithtop:"<<seq<<"   ";
        for(int i=0; i<seq; i++)
        {
            cout<<seqithscchaveNE_ithtopo[i]<<" ";
        }
        cout<<endl;
    }
}
//judge the ithscc whether accessible to the destination without other scc having ENode
void fromdestdfswithoutscchaveEN(int rdv)
{
    scc_istoanodeFromDestwithouthaveEN[rdv]=true;
    for(int i=0; i<rTopoEdge[rdv].size(); i++)
    {
        if(0<ENodesnumofithSCC[rTopoEdge[rdv][i]])
        {
            scc_istoanodeFromDestwithouthaveEN[rTopoEdge[rdv][i]]=true;
        }
        if((0==ENodesnumofithSCC[rTopoEdge[rdv][i]])&&(rTopoEdge[rdv][i]!=ithnode_ithSCC[source]))
            if(false==scc_istoanodeFromDestwithouthaveEN[rTopoEdge[rdv][i]])
                fromdestdfswithoutscchaveEN(rTopoEdge[rdv][i]);
    }
}
//judge the ithnode whether accessible to the destination without other ENode
void fromdestdfswithoutEN(int rdv)
{
    istoanodeFromDestwithouthaveEN[rdv]=true;
    for(int i=0; i<SCC_rG[rdv].size(); i++)
    {
        if(true==isENode[SCC_rG[rdv][i]])
        {
            istoanodeFromDestwithouthaveEN[SCC_rG[rdv][i]]=true;
        }
        if((false==isENode[SCC_rG[rdv][i]])&&(false==istoanodeFromDestwithouthaveEN[SCC_rG[rdv][i]]))
            if(SCC_rG[rdv][i]!=source)
                fromdestdfswithoutEN(SCC_rG[rdv][i]);
    }
}
//judge the ithnode whether accessible to the destination
void fromdestdfs(int rdv)
{
    istoanodeFromDest[rdv]=true;
    for(int i=0; i<SCC_rG[rdv].size(); i++)
    {
        if((false==istoanodeFromDest[SCC_rG[rdv][i]]))
            if(SCC_rG[rdv][i]!=source)
                fromdestdfs(SCC_rG[rdv][i]);
    }
}
void rerversdfsfromdest()
{
    if(true==initsetparavalue)
    {
        memset(scc_istoanodeFromDestwithouthaveEN,0,sizeof(scc_istoanodeFromDestwithouthaveEN));
        memset(istoanodeFromDestwithouthaveEN,0,sizeof(istoanodeFromDestwithouthaveEN));
        memset(istoanodeFromDest,0,sizeof(istoanodeFromDest));
    }
    fromdestdfswithoutscchaveEN(ithnode_ithSCC[destination]);
    // if(ithnode_ithSCC[destination]!=ithnode_ithSCC[source]){
    //     istoanodeFromDestwithoutscchaveEN[ithnode_ithSCC[source]]=false;
    //  }
    fromdestdfswithoutEN(destination);
    fromdestdfs(destination);
}

//bfs parameters

vector<int>path;
//bool usednode[MAX_V];
bool bfsissucceedtofindpath;

struct status
{
    int id;
    int hop;
    int costsum;

    int numbofhavedENode;
    int OutNodeisNE;
    int OutNodeisE;

    int ithSCC;
    int Enodeofithscc;
    int vsnode[MAX_V];
    int seqlocithscchavedNE;

    //int bit[19];

    friend bool operator<(const struct status a,const struct status b)
    {
        if((ENodeSize==12)&&(Node4InitInputGragh<150))
        {
            if((a.id==destination)&&(b.id!=destination))
            {
                return false;
            }
            else
            {
                if((a.id!=destination)&&(b.id==destination))
                {
                    return true;
                }
            }

            if(a.numbofhavedENode<b.numbofhavedENode)
            {
                return true;
            }
            else
            {
                if(a.numbofhavedENode>b.numbofhavedENode)
                    return false;
                else
                {
                    if(a.OutNodeisNE<b.OutNodeisNE)
                    {
                        return true;
                    }
                    else
                    {
                        if(a.OutNodeisNE>b.OutNodeisNE)
                            return false;
                        else
                        {
                            if(a.OutNodeisE<b.OutNodeisE)
                            {
                                return true;
                            }
                            else
                            {
                                if(a.OutNodeisE>b.OutNodeisE)
                                    return false;
                                else
                                {

                                    if(a.costsum>b.costsum)//a.OutNodeisE<b.OutNodeisE)
                                    {
                                        return true;
                                    }
                                    else
                                    {
                                        if(a.costsum<b.costsum)//a.OutNodeisE>b.OutNodeisE)
                                        {
                                            return false;
                                        }
                                        else
                                        {
                                            if(a.hop<b.hop)//a.OutNodeisNE<b.OutNodeisNE)
                                            {
                                                return true;
                                            }
                                            else
                                            {
                                                if(a.hop>b.hop)//a.OutNodeisNE>b.OutNodeisNE)
                                                    return false;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if((ENodeSize==12)&&(Node4InitInputGragh>=150))
        {
            if((a.id==destination)&&(b.id!=destination))
            {
                return false;
            }
            else
            {
                if((a.id!=destination)&&(b.id==destination))
                {
                    return true;
                }
            }

            if(a.numbofhavedENode<b.numbofhavedENode)
            {
                return true;
            }
            else
            {
                if(a.numbofhavedENode>b.numbofhavedENode)
                    return false;
                else
                {
                    if(a.costsum>b.costsum)
                    {
                        return true;
                    }
                    else
                    {
                        if(a.costsum<b.costsum)
                            return false;
                        else
                        {

                            if(a.hop>b.hop)
                            {
                                return true;
                            }
                            else
                            {
                                if(a.hop<b.hop)
                                {


                                    if(a.OutNodeisE<b.OutNodeisE)
                                    {
                                        return true;
                                    }
                                    else
                                    {
                                        if(a.OutNodeisE>b.OutNodeisE)
                                            return false;
                                        else
                                        {
                                            if(a.OutNodeisNE<b.OutNodeisNE)
                                            {
                                                return true;
                                            }
                                            else
                                            {
                                                if(a.OutNodeisNE>b.OutNodeisNE)
                                                    return false;

                                            }

                                        }
                                    }

                                }
                            }
                        }
                    }
                }

            }
        }
        if((12!=ENodeSize)&&(Node4InitInputGragh<=BigNode))
        {
            if((a.id==destination)&&(b.id!=destination))
            {
                return false;
            }
            else
            {
                if((a.id!=destination)&&(b.id==destination))
                {
                    return true;
                }
            }
            if(a.numbofhavedENode<b.numbofhavedENode)
            {
                return true;
            }
            else
            {
                if(a.numbofhavedENode>b.numbofhavedENode)
                    return false;
                else
                {
                    if(a.costsum>b.costsum)
                    {
                        return true;
                    }
                    else
                    {
                        if(a.costsum<b.costsum)
                            return false;
                        else
                        {

                            if(a.hop>b.hop)
                            {
                                return true;
                            }
                            else
                            {
                                if(a.hop<b.hop)
                                {
                                    if(a.OutNodeisNE<b.OutNodeisNE)
                                    {
                                        return true;
                                    }
                                    else
                                    {
                                        if(a.OutNodeisNE>b.OutNodeisNE)
                                            return false;
                                        else
                                        {
                                            if(a.OutNodeisE<b.OutNodeisE)
                                            {
                                                return true;
                                            }
                                            else
                                            {
                                                if(a.OutNodeisE>b.OutNodeisE)
                                                    return false;

                                            }

                                        }
                                    }

                                }
                            }
                        }
                    }
                }

            }
        }
        if((12!=ENodeSize)&&(Node4InitInputGragh>BigNode))
        {
            if(a.costsum<b.costsum)
            {
                return true;
            }
            else
            {
                if(a.costsum>b.costsum)
                {
                    return false;
                }
                else
                {
                    if(a.hop<b.hop)
                    {
                        return true;
                    }
                    else
                    {
                        if(a.hop>b.hop)
                            return false;

                    }
                }
            }
        }
        return false;
    }
} fq,dp,sp;
priority_queue<status>que;

int minimumcost;
int INF=11111111;
int d[MAX_V];
bool used[MAX_V];
int firstvs[MAX_V];
void bfs()
{
    int cu,cv;
    while(!que.empty())
    {
        que.pop();
    }
    fq.id=source;
    fq.hop=0;
    fq.costsum=0;
    fq.numbofhavedENode=0;
    fq.ithSCC=ithnode_ithSCC[source];
    fq.Enodeofithscc=0;
    memset(fq.vsnode,-1,sizeof(fq.vsnode));
    fq.vsnode[source]=-1;
    fq.seqlocithscchavedNE=0;
    que.push(fq);

    minimumcost=INF;
    //int itercontrol=1000;
    while(!que.empty())
    {
        dp=que.top();
        if((dp.id==destination)&&(dp.numbofhavedENode==ENodeSize))
        {
            if(dp.costsum<minimumcost)
            {
                path.clear();
                path.push_back(destination);
                for(int i=dp.vsnode[destination]; i!=-1; i=dp.vsnode[i])
                {
                    path.push_back(i);
                }
                minimumcost=dp.costsum;
                bfsissucceedtofindpath=true;
            }
            //////
            //return ;
        }
        que.pop();
        if(ENodeSize>10)
        {
            ftime(&endtime);
            if(16<=(endtime.time-starttime.time))
            {
                return ;
            }
        }
        cu=dp.id;
        for(int i=0; i<SCC_G[cu].size(); i++)
        {
            cv=SCC_G[cu][i];
            if((-1)==dp.vsnode[cv])
            {
                if(isENode[cv])
                {
                    if((ithnode_ithSCC[cu]==ithnode_ithSCC[cv]))
                    {
                        //struct status sp;
                        sp.id=cv;
                        sp.hop=dp.hop+1;
                        sp.costsum=dp.costsum+InitGraphMatix[cu][cv];

                        sp.numbofhavedENode=dp.numbofhavedENode+1;
                        sp.OutNodeisNE=degree4node[cv].outdegreeofNE;
                        sp.OutNodeisE=degree4node[cv].outdegreeofE;

                        sp.ithSCC=dp.ithSCC;
                        sp.Enodeofithscc=dp.Enodeofithscc+1;

                        memcpy(sp.vsnode,dp.vsnode,Node4InitInputGragh*4);
                        sp.vsnode[cv]=cu;
                        sp.seqlocithscchavedNE=dp.seqlocithscchavedNE;

                        if(istoanodeFromDest[sp.id])
                            if(sp.numbofhavedENode==ENodeSize)
                            {
                                if(scc_istoanodeFromDestwithouthaveEN[sp.ithSCC]&&istoanodeFromDestwithouthaveEN[sp.id])
                                {

                                    que.push(sp);
                                }
                            }
                            else
                            {
                                que.push(sp);
                            }

                        // que.push(sp);

                    }
                    else
                    {
                        if(ENodesnumofithSCC[ithnode_ithSCC[cu]]<=dp.Enodeofithscc)
                        {
                            if(ithscc_ithtopo[ithnode_ithSCC[cv]]<=seqithscchaveNE_ithtopo[dp.seqlocithscchavedNE+1])
                            {
                                //struct status sp;
                                sp.id=cv;
                                sp.hop=dp.hop+1;
                                sp.costsum=dp.costsum+InitGraphMatix[cu][cv];

                                sp.numbofhavedENode=dp.numbofhavedENode+1;
                                sp.OutNodeisNE=degree4node[cv].outdegreeofNE;
                                sp.OutNodeisE=degree4node[cv].outdegreeofE;

                                sp.ithSCC=ithnode_ithSCC[cv];
                                sp.Enodeofithscc=1;
                                // sp.vsnode=dp.vsnode;
                                memcpy(sp.vsnode,dp.vsnode,Node4InitInputGragh*4);
                                sp.vsnode[cv]=cu;
                                sp.seqlocithscchavedNE=dp.seqlocithscchavedNE+1;

                                if(istoanodeFromDest[sp.id])
                                    if(sp.numbofhavedENode==ENodeSize)
                                    {
                                        if(scc_istoanodeFromDestwithouthaveEN[sp.ithSCC]&&istoanodeFromDestwithouthaveEN[sp.id])
                                        {

                                            que.push(sp);
                                        }
                                    }
                                    else
                                    {
                                        que.push(sp);
                                    }

                                //que.push(sp);

                            }
                        }
                    }
                }
                else
                {
                    if((ithnode_ithSCC[cu]==ithnode_ithSCC[cv]))
                    {
                        //struct status sp;
                        sp.id=cv;
                        sp.hop=dp.hop+1;
                        sp.costsum=dp.costsum+InitGraphMatix[cu][cv];

                        sp.numbofhavedENode=dp.numbofhavedENode;
                        sp.OutNodeisNE=degree4node[cv].outdegreeofNE;
                        sp.OutNodeisE=degree4node[cv].outdegreeofE;

                        sp.ithSCC=dp.ithSCC;
                        sp.Enodeofithscc=dp.Enodeofithscc;

                        memcpy(sp.vsnode,dp.vsnode,Node4InitInputGragh*4);
                        sp.vsnode[cv]=cu;
                        sp.seqlocithscchavedNE=dp.seqlocithscchavedNE;

                        if(istoanodeFromDest[sp.id])
                            if(sp.numbofhavedENode==ENodeSize)
                            {
                                if(scc_istoanodeFromDestwithouthaveEN[sp.ithSCC]&&istoanodeFromDestwithouthaveEN[sp.id])
                                {
                                    que.push(sp);
                                }
                            }
                            else
                            {
                                que.push(sp);
                            }

                        //que.push(sp);
                    }
                    else
                    {

                        if(ENodesnumofithSCC[ithnode_ithSCC[cu]]<=dp.Enodeofithscc)
                        {
                            if(ithscc_ithtopo[ithnode_ithSCC[cv]]<=seqithscchaveNE_ithtopo[dp.seqlocithscchavedNE+1])
                            {
                                sp.id=cv;
                                sp.hop=dp.hop+1;
                                sp.costsum=dp.costsum+InitGraphMatix[cu][cv];

                                sp.numbofhavedENode=dp.numbofhavedENode;
                                sp.OutNodeisNE=degree4node[cv].outdegreeofNE;
                                sp.OutNodeisE=degree4node[cv].outdegreeofE;

                                sp.ithSCC=ithnode_ithSCC[cv];
                                sp.Enodeofithscc=0;
                                // sp.vsnode=dp.vsnode;
                                memcpy(sp.vsnode,dp.vsnode,Node4InitInputGragh*4);
                                sp.vsnode[cv]=cu;
                                if(ENodesnumofithSCC[ithnode_ithSCC[cv]]>0)
                                {
                                    sp.seqlocithscchavedNE=dp.seqlocithscchavedNE+1;
                                }
                                else
                                {
                                    sp.seqlocithscchavedNE=dp.seqlocithscchavedNE;
                                }

                                if(istoanodeFromDest[sp.id])
                                    if(sp.numbofhavedENode==ENodeSize)
                                    {
                                        if(scc_istoanodeFromDestwithouthaveEN[sp.ithSCC]&&istoanodeFromDestwithouthaveEN[sp.id])
                                        {

                                            que.push(sp);

                                        }
                                    }
                                    else
                                    {
                                        que.push(sp);
                                    }

                                //  que.push(sp);
                            }
                        }
                    }
                }
            }
        }
    }
}


int getlastpath(int start,int lastvs[MAX_V],int incost)
{
    fill(d,d+Node4InitInputGragh,INF);
    fill(used,used+Node4InitInputGragh,false);
    fill(firstvs,firstvs+Node4InitInputGragh,-1);
    d[start]=0;
    int mid=lastvs[start];
    lastvs[start]=-1;
    while(true)
    {
        int v=-1;
        for(int u=0; u<Node4InitInputGragh; u++)
        {
            if((-1==(lastvs[u]))&&(u!=source)&&(u!=destination))
            {
                if((false==used[u])&&((v==-1)||(d[u]<d[v])))
                    v=u;
            }
        }
        if(v==-1)
            break;
        used[v]=true;
        for(int u=0; u<Node4InitInputGragh; u++)
        {
            if((-1==(lastvs[u]))&&(u!=source))
            {
                if(InitGraphMatix[v][u]!=0)
                    if(d[u]>(d[v]+InitGraphMatix[v][u]))
                    {
                        d[u]=d[v]+InitGraphMatix[v][u];
                        firstvs[u]=v;
                    }
            }
        }
    }
    lastvs[start]=mid;
    // mid=start;
    if(-1!=(firstvs[destination]))
    {
        int pre=destination;
        int next;//=firstvs[pre];
        do
        {
            next=firstvs[pre];
            lastvs[pre]=next;
            incost+=InitGraphMatix[next][pre];
            pre=next;
            //slpsp->hop++;
        }
        while(next!=start);
        return incost;
    }
    else
    {
        return 0;
    }
}

vector<status>preque[MAX_EV];
int PreNode[MAX_EV];
int preNode_seq[MAX_V];
int preNodesize;
//ENode
//ENode[i]
//Node4InitInputGragh
//destination
//InitGraphMatix
typedef pair<int,int> P;

void prepushEnode()
{
    preNodesize=0;
    PreNode[preNodesize]=source;
    preNode_seq[source]=preNodesize;
    preNodesize++;
    for(int i=0; i<ENodeSize; i++)
    {
        PreNode[preNodesize]=ENode[i];
        preNode_seq[ENode[i]]=preNodesize;
        preNodesize++;
    }
    /*
    for(int ii=0;ii<SCCSize;ii++){
            cout<<" ff "<<ithscchaveNE_seq[ii];
        }
        cout<<endl;
        */
    for(int i=0; i<preNodesize; i++)
    {
        fill(d,d+Node4InitInputGragh,INF);
        //fill(used,used+Node4InitInputGragh,false);
        fill(firstvs,firstvs+Node4InitInputGragh,-1);
        priority_queue<P,vector<P>,greater<P> >midque;
        d[PreNode[i]]=0;
        midque.push(P(0,PreNode[i]));
        //cout<<"size   :"<<midque.size()<<endl;
        while(!midque.empty())
        {
            P p=midque.top();
            midque.pop();
            int v=p.second;
            if((v==destination)||((v!=PreNode[i])&&(true==isENode[v]))||(d[v]<p.first))
                continue;
            //used[v]=true;
            int u;
            for(int j=0; j<SCC_G[v].size(); j++)
            {
                u=SCC_G[v][j];
                //if(InitGraphMatix[v][u]!=0)
                if(d[u]>(d[v]+InitGraphMatix[v][u]))
                {
                    d[u]=d[v]+InitGraphMatix[v][u];
                    //d[u]=d[v]+1;
                    firstvs[u]=v;
                    midque.push(P(d[u],u));
                }
            }
        }
        int pre,next,costsum,hop;
        //int b1,b2;
        int icci=ithnode_ithSCC[PreNode[i]];
        int iccj;
        //for(int j=0; j<Node4InitInputGragh; j++)
        int j;


        for(int l=0; l<preNodesize; l++)
        {
            if(l==0)
            {
                j=destination;
            }
            else
                j=PreNode[l];
            if(i==0&&l==0)
            {
                continue;
            }
            if((firstvs[j]!=-1)&&((true==isENode[j])||j==destination))
            {
                iccj=ithnode_ithSCC[j];

                if((icci==iccj)||((ithscchaveNE_seq[icci]+1)==ithscchaveNE_seq[iccj]))
                {
                    //cout<<"i "<<icci<<"  j "<<iccj<<endl;
                    pre=j;
                    hop=0;
                    costsum=0;
                    memset(fq.vsnode,-1,sizeof(fq.vsnode));
                    //memset(fq.bit,0,sizeof(fq.bit));
                    next=firstvs[pre];
                    fq.vsnode[pre]=next;
//                b1=pre/8;
                    //  b2=pre%8;
                    //fq.bit[b1]|=(1<<b2);
                    while(-1!=next)
                    {
                        costsum+=InitGraphMatix[next][pre];
                        pre=next;

                        hop++;
                        next=firstvs[pre];
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
                    preque[i].push_back(fq);
                }

            }
        }

    }
}
void showbfsunit(struct status *show)
{
    cout<<"----------------------------------"<<endl;
    cout<<"Enloc: "<<node_index[show->id]<<" weight: "<<show->costsum;
    cout<<"  hop: "<<show->hop<<endl;
    int loc=show->id;
    int pre=show->vsnode[show->id];
    while(-1!=pre)
    {
        cout<<"("<<pre<<","<<loc<<") "<<endl;
        cout<<"("<<node_index[pre]<<","<<node_index[loc]<<") "<<Index4InitGraphEdgeMatix[pre][loc]<<"  "<<InitGraphMatix[pre][loc]<<endl;
        loc=pre;
        pre=show->vsnode[loc];

    }


}

bool controltime;//=true;
int ansvs[MAX_V];
void dfsfor14or15(int start,int enodenum,int costsum)//,unsigned int bit[19])
{
    if(false==controltime)
    {
        return ;
    }
    int back_vs[MAX_V];
    //unsigned int bt[19];
    memcpy(back_vs,ansvs,Node4InitInputGragh*4);
    //memcpy(bt,bit,19*4);
    /*
    if(enodenum==ENodeSize)
    {

            int cost=getlastpath(start,vs,costsum);
            if(cost>0)
            {
                if(minimumcost>cost)
                {
                    minimumcost=cost;
                    path.clear();
                    path.push_back(destination);
                    for(int i=vs[destination]; i!=-1; i=vs[i])
                    {
                        path.push_back(i);
                    }
                    bfsissucceedtofindpath=true;
                }
                controltime=false;
            }
        return ;
    }
    */
    /*
        ftime(&endtime);
        if(17<=(endtime.time-starttime.time))
        {
            controltime=false;
            return ;
        }
    */
    //memcpy(vs,back_vs,Node4InitInputGragh*4);
    int seq=preNode_seq[start];
    bool isover;
    //int b1,b2;
    for(int i=0; i<preque[seq].size(); i++)
    {
        if(true==controltime)
        {
            isover=true;
            dp=preque[seq][i];
            if(dp.id!=destination)
            {
                for(int j=0; j<Node4InitInputGragh; j++)
                {
                    if((-1!=ansvs[j])&&(-1!=(dp.vsnode[j])))
                    {
                        isover=false;
                    }
                }

                /*

                for(int j=0; j<19; j++)
                {
                    if(0!=(bit[j]&dp.bit[j]))
                    {
                        //cout<<bit[j]<<"     "<<dp.bit[j]<<"jj"<<j<<endl;
                        isover=false;
                    }
                }
                */
                if(isover)
                {
                    if((enodenum+1)==ENodeSize)
                    {
                        if(istoanodeFromDestwithouthaveEN[dp.id]&&scc_istoanodeFromDestwithouthaveEN[ithnode_ithSCC[dp.id]])
                        {
                            int start=dp.id;
                            int seqstart=preNode_seq[start];
                            int cost=costsum+dp.costsum;
                            for(int j=0; j<preque[seqstart].size(); j++)
                            {
                                sp=preque[seqstart][j];
                                if(sp.id==destination)
                                {
                                    break;
                                }

                            }

                            if(sp.vsnode[destination]!=-1)
                            {
                                for(int j=0; j<Node4InitInputGragh; j++)
                                {
                                    if(-1!=(dp.vsnode[j]))
                                    {
                                        ansvs[j]=dp.vsnode[j];
                                        //b1=j/8;
                                        // b2=j%8;
                                        //bit[b1]|=(1<<b2);
                                    }

                                }
                                controltime=false;
                                if(-1!=(sp.vsnode[destination]))
                                {
                                    int pre=destination;
                                    int next;//=firstvs[pre];
                                    do
                                    {
                                        next=sp.vsnode[pre];
                                        ansvs[pre]=next;
                                        costsum+=InitGraphMatix[next][pre];
                                        pre=next;
                                        //slpsp->hop++;
                                    }
                                    while(next!=start);
                                    if(minimumcost>cost)
                                    {
                                        minimumcost=cost;
                                        path.clear();
                                        path.push_back(destination);
                                        for(int i=ansvs[destination]; i!=-1; i=ansvs[i])
                                        {
                                            path.push_back(i);
                                        }
                                        bfsissucceedtofindpath=true;
                                    }
                                }
                            }
                        }
                    }
                    else
                    {

                            for(int j=0; j<Node4InitInputGragh; j++)
                            {
                                if(-1!=(dp.vsnode[j]))
                                {
                                    ansvs[j]=dp.vsnode[j];
                                    //b1=j/8;
                                    // b2=j%8;
                                    //bit[b1]|=(1<<b2);
                                }
                            }
                            dfsfor14or15(dp.id,enodenum+1,costsum+dp.costsum);//,bit);
                            memcpy(ansvs,back_vs,Node4InitInputGragh*4);
                        //memcpy(bit,bt,19*4);

                    }

                }

            }
        }
    }
    return ;
}
void bfsfor14or15()
{
    minimumcost=INF;
    prepushEnode();

    for(int i=0; i<preNodesize; i++)
        sort(preque[i].begin(),preque[i].end());
    if(debug_showbfsfor14or15)
        for(int i=0; i<preNodesize; i++)
        {

            cout<<"+++++++++++"<<node_index[PreNode[i]]<<"+++++++++++++"<<endl;
            for(int j=0; j<preque[i].size(); j++)
            {
                sp=preque[i][j];
                showbfsunit(&sp);
            }

        }

    // unsigned int bit[19];
    fill(ansvs,ansvs+Node4InitInputGragh,-1);
    //fill(bit,bit+19,0);
    controltime=true;
    //unsigned int bit1,bit2;
    //bit1=source/8;
    //bit2=source%8;
    //bit[bit1]|=(1<<bit2);
    dfsfor14or15(source,0,0);//,bit);
    //cout<<"minimumcost:    "<<minimumcost<<endl;

}
void search_route(char *topo[5000], int edge_num, char *demand)
{
    unsigned short result[MAX_V];
    int demandStrLength = strlen(demand);
    int mInput;
    int ithtopolebgth;
    int mFlag,inEdge,outEdge,mWeight;
    ftime(&starttime);
    ENodeSize=0;
    if(debug_isruningsearch_route)
    {
        debug_showinputstr=false;
        debug_write=false;
        debug_showbfsqueue=false;
        debug_showinitmatrix=false;
        debug_showresultpath=false;
        debug_showtopoinfo=false;
        debug_showbfsfor14or15=false;
        initcleardata();

    }
    beabletofindpath=true;

    if(debug_showinputstr)
        cout<<"demand:"<<demand<<endl;
    int demandi=0;
    for(mInput=0; demandi<demandStrLength; demandi++)
    {
        if(!((demand[demandi]<='9')&&(demand[demandi]>='0')))
        {
            demandi++;
            break;
        }
        mInput=mInput*10+(demand[demandi]-'0');
    }
    source = mInput;

    for(mInput=0; demandi<demandStrLength; demandi++)
    {
        if(!((demand[demandi]<='9')&&(demand[demandi]>='0')))
        {
            demandi++;
            break;
        }
        mInput=mInput*10+(demand[demandi]-'0');
    }
    destination = mInput;

    index_node[source]=Node4InitInputGragh;
    node_index[Node4InitInputGragh]=source;
    source=Node4InitInputGragh;

    ExistNodeafterscaledown[Node4InitInputGragh]=1;
    Node4InitInputGragh++;

    index_node[destination]=Node4InitInputGragh;
    node_index[Node4InitInputGragh]=destination;
    destination=Node4InitInputGragh;

    ExistNodeafterscaledown[Node4InitInputGragh]=1;
    Node4InitInputGragh++;

    for(mInput=0; demandi<=demandStrLength; demandi++)
    {
        if(demand[demandi]!='\n')
        {
            if((demand[demandi]==',')||(demand[demandi]=='|')||(demandi==(demandStrLength)))
            {
                index_node[mInput]=Node4InitInputGragh;
                node_index[Node4InitInputGragh]=mInput;
                ENode[ENodeSize]=Node4InitInputGragh;
                isENode[Node4InitInputGragh]=true;
                ExistNodeafterscaledown[Node4InitInputGragh]=1;
                Node4InitInputGragh++;
                ENodeSize++;
                mInput=0;
                if(demandi==demandStrLength)
                {
                    break;
                }
            }
            else
            {
                mInput=mInput*10+(demand[demandi]-'0');
            }
        }
        if((demand[demandi]!='\n')&&(demandi==demandStrLength))
            break;
    }

    for(int i=0; i<edge_num; i++)
    {
        ithtopolebgth=strlen(topo[i]);
        int j=0;
        mInput=0;
        for(; j<ithtopolebgth; j++)
        {
            if(!((topo[i][j]<='9')&&(topo[i][j]>='0')))
            {
                j++;
                break;
            }
            mInput=mInput*10+(topo[i][j]-'0');
        }
        mFlag=mInput;
        mInput=0;
        for(; j<ithtopolebgth; j++)
        {
            if(!((topo[i][j]<='9')&&(topo[i][j]>='0')))
            {
                j++;
                break;
            }
            mInput=mInput*10+(topo[i][j]-'0');
        }
        if(index_node[mInput]==-1)
        {
            index_node[mInput]=Node4InitInputGragh;
            node_index[Node4InitInputGragh]=mInput;
            ExistNodeafterscaledown[Node4InitInputGragh]=1;
            Node4InitInputGragh++;
        }
        inEdge=index_node[mInput];

        mInput=0;
        for(; j<ithtopolebgth; j++)
        {
            if(!((topo[i][j]<='9')&&(topo[i][j]>='0')))
            {
                j++;
                break;
            }
            mInput=mInput*10+(topo[i][j]-'0');
        }

        if(index_node[mInput]==-1)
        {
            index_node[mInput]=Node4InitInputGragh;
            node_index[Node4InitInputGragh]=mInput;
            ExistNodeafterscaledown[Node4InitInputGragh]=1;
            Node4InitInputGragh++;

        }
        outEdge=index_node[mInput];

        mInput=0;
        for(; j<ithtopolebgth; j++)
        {
            if(!((topo[i][j]<='9')&&(topo[i][j]>='0')))
            {
                j++;
                break;
            }
            mInput=mInput*10+(topo[i][j]-'0');
        }
        mWeight=mInput;
        if((inEdge!=destination)&&(outEdge!=source)&&(inEdge!=outEdge))
        {

            if(InitGraphMatix[inEdge][outEdge]==0)
            {
                InitGraphMatix[inEdge][outEdge]=mWeight;
                Index4InitGraphEdgeMatix[inEdge][outEdge]=mFlag;
                Edg4InitInputGragh++;

            }
            else
            {
                if(InitGraphMatix[inEdge][outEdge]>mWeight)
                {
                    InitGraphMatix[inEdge][outEdge]=mWeight;
                    Index4InitGraphEdgeMatix[inEdge][outEdge]=mFlag;
                }
            }

        }
    }

    scaledowngragh();
    if(beabletofindpath==true)
        beabletofindpath=judgegraph();

    convertMatrixtoList();

    SCCSize=scc();

    if(debug_showinputstr)
        DebugPrint();
    if((isDFStodestination==false)||(SCCDFSAllENode!=ENodeSize))
        beabletofindpath=false;

    //after SCC,the graph's node accimulate to some parts,some parts compose
    //the DAG,get the toposort.
    getTopoSort4SCC();

    rerversdfsfromdest();

    if(beabletofindpath==true)
    {

        bfsissucceedtofindpath=false;
        // path.clear();
        //memset(usednode,0,sizeof(usednode));
        //dfs(source,0,0,ithSCCtoithTopo[ithnode_ithSCC[source]]);
        if(Node4InitInputGragh<=BigNode)
        {
            bfs();
        }
        else
        {
            bfsfor14or15();
        }

        beabletofindpath=bfsissucceedtofindpath;
    }

    int pathcostsum=0;
    if(debug_showresultpath)
    {
        for(int i=path.size()-1; i>=1; i--)
        {
            if(i==(path.size()-1))
            {
                cout<<"edge index:"<<Index4InitGraphEdgeMatix[path[i]][path[i-1]];
                pathcostsum+=InitGraphMatix[path[i]][path[i-1]];
            }
            else
            {
                cout<<"|"<<Index4InitGraphEdgeMatix[path[i]][path[i-1]];
                pathcostsum+=InitGraphMatix[path[i]][path[i-1]];
            }
        }
        cout<<endl<<"pathcostsum:"<<pathcostsum<<endl;
    }
    if(debug_showresultpath)
    {
        cout<<endl;
        for(int i=path.size()-1; i>=0; i--)
        {

            if(i==(path.size()-1))
            {
                cout<<"node index:"<<node_index[path[i]];
            }
            else
            {
                cout<<"|"<<node_index[path[i]];
            }
        }


    }


    if(true==beabletofindpath)
    {
        int j=0;
        for(int i=path.size()-1; i>=1; i--,j++)
        {
            if((1==i)&&debug_showresultpath)
                cout<<"succeed"<<endl;
            result[j]=Index4InitGraphEdgeMatix[path[i]][path[i-1]];
        }
        for (int i = 0; i < j; i++)
             record_result(result[i]);
    }
}

/*
int main()
{
    if(debug_isruningsearch_route)
        initcleardata();

    debug_isruningsearch_route=false;
    //search_route(char *topo[5000], int edge_num, char *demand)
    char demand[3000];
    char topo[5000][20];

    int edge_num=0;
    int judgefile;
    FILE *fp;
    fp=fopen("demand.csv","r");
    fscanf(fp,"%s",demand);

    fclose(fp);
    fp=fopen("topo.csv","r");
    judgefile=fscanf(fp,"%s",topo[edge_num]);
    while(judgefile!=-1)
    {

        edge_num++;
        judgefile=fscanf(fp,"%s",topo[edge_num]);

    }
    fclose(fp);
    char *intopo[5000];
    for(int i=0; i<edge_num; i++)
    {
        intopo[i]=topo[i];
    }
    search_route(intopo,edge_num,demand);

}
*/
/*
void dfs(int s,int n,int enodenumb,int seqnumithscc)
{
    if(s==destination&&(n==ENodeSize))
    {
        succeedfindpath=true;
    }
    usednode[s]=true;
    for(int i=0; i<SCC_G[s].size(); i++)
    {
        if((false==succeedfindpath)&&(!usednode[SCC_G[s][i]]))
        {
            if((true==isENode[SCC_G[s][i]]))
            {
                if((ithnode_ithSCC[s]==ithnode_ithSCC[SCC_G[s][i]]))
                {
                    dfs(SCC_G[s][i],n+1,enodenumb+1,seqnumithscc);
                }
                else
                {
                    if(ENodesnumofithSCC[ithnode_ithSCC[s]]<=enodenumb)
                    {
                        if((seqnumithscc+1)==seqnumofitscchaveNE[ithnode_ithSCC[SCC_G[s][i]]])
                            dfs(SCC_G[s][i],n+1,1,(seqnumithscc+1));
                    }
                }
            }

        }
    }
    for(int i=0; i<SCC_G[s].size(); i++)
    {
        if((false==succeedfindpath)&&(!usednode[SCC_G[s][i]]))
        {
            if((false==isENode[SCC_G[s][i]]))
            {
                if((ithnode_ithSCC[s]==ithnode_ithSCC[SCC_G[s][i]]))
                {
                    dfs(SCC_G[s][i],n,enodenumb,seqnumithscc);
                }
                else
                {
                    if(ENodesnumofithSCC[ithnode_ithSCC[s]]<=enodenumb)
                    {
                        if(ENodesnumofithSCC[ithnode_ithSCC[SCC_G[s][i]]]>0)
                        {
                            if((seqnumithscc+1)==seqnumofitscchaveNE[ithnode_ithSCC[SCC_G[s][i]]])
                                dfs(SCC_G[s][i],n,0,(seqnumithscc+1));
                        }
                        else
                        {
                            dfs(SCC_G[s][i],n,0,seqnumithscc);
                        }

                    }
                }
            }

        }
    }

    usednode[s]=false;
    if(succeedfindpath==true)
    {
        path.push_back(s);
    }
    return ;
}
*/









