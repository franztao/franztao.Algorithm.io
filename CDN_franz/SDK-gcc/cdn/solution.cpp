#include "solution.h"
#include <stdio.h>
#include <iostream>
#include <vector>
#include <string.h>
using namespace std;
const int maxdist = 9999;
bool iswujie(int consumer_id,int **consumer_node,short int **graphic,int node_num/*,int result[4]*/)
{
	int sum=0;
//	printf("consumer_id%dconsumer_node[consumer_id]%d\n", consumer_id,consumer_node[consumer_id][0]);
	for (int i = 0; i < node_num; ++i)
	{	
		if(graphic[consumer_node[consumer_id][0]][i]!=0)//if have have a path
		{
			if (graphic[consumer_node[consumer_id][0]][i]<consumer_node[consumer_id][1])//判断链路带宽是否小于节点需求
			{
				sum+=graphic[consumer_node[consumer_id][0]][i];
			}else
			{
				return true;
			}
		}
	}	
	if(sum>=consumer_node[consumer_id][1])
	{
		//result[0]=consumer_id;
		return true;
	}
	else
	{	//printf("sum:%d,need:%d\n",sum,consumer_node[consumer_id][1] );
		return false;
	}
}
//
int getMidServerNode(short int **rent_money,int **consumer_node,int consumer_num,int *result,
	vector<vector<int> > &dist,vector<vector<int> > &pre)
{
	int linknetid=consumer_node[1][0];
	vector<int> path;
	int temp=0;
	int pos=0;
	// getlowpath(pre[linknetid],linknetid,consumer_node[3][0],path);
	// printpath(path);
	for (int i =1; i < consumer_num; ++i)
	{
		getlowpath(pre[linknetid],linknetid,consumer_node[i][0],path);
		temp=getMidNode(linknetid,consumer_node[i][0],rent_money,path,dist[linknetid]);
		if (temp!=linknetid)
		{
			result[pos++]=temp;
		//	cout<<"midnode  "<<result[pos-1]<<endl;
		}
	}
	return pos;
}
//select the server that meet all consumers's need at minum rent money
int getLowRentServer(short int **rent_money,short int**graphic,int **consumer_node,int consumer_node_num,int node_num,int *result,
	vector<vector<int> > dist)
{
	int *net_node_rent=new int[node_num];
	short int *pos=new short int[node_num];	//save the node  rent pos
	memset(net_node_rent,0,sizeof(int)*node_num);
	int temp;
	for (int i = 0; i < node_num; ++i)
	{
		for (int j = 0; j < consumer_node_num; ++j)
		{
			net_node_rent[i]+=dist[i][consumer_node[j][0]];
		}
		pos[i]=i;
	}
	for (int i = 0; i < node_num; ++i)//consider the degree of each node
	{
		for (int j = 0; j < node_num; ++j)
		{
			if (rent_money[i][j]!=0)
			{
				net_node_rent[i]--;
				net_node_rent[i]-=(0.2*graphic[i][j]);
			}
		}
	}
	for (int i = 0; i < node_num; ++i)
	{
		for (int j =1; j< node_num; ++j)
		{
			if (net_node_rent[j-1]>net_node_rent[j])
			{
				temp=net_node_rent[j];
				net_node_rent[j]=net_node_rent[j-1];
				net_node_rent[j-1]=temp;

				temp=pos[j];
				pos[j]=pos[j-1];
				pos[j-1]=temp;
			}
		}
	}
	for (int i = 0; i < consumer_node_num-1; ++i)
	{
		result[i]=pos[i];
//		cout<<net_node_rent[i]<<" "<<pos[i]<<endl;
	}
	return consumer_node_num-1;
}
//divide into 2,for select the middle server
int getNearMidServer(short int **rent_money,int **consumer_node,int consumer_node_num,int *result,
	vector<vector<int> > &dist,vector<vector<int> > &pre)
{
	int linknetid=0;
	int temp;
	int servernum=0;
	vector<int> path;
	int *dist20=new int[consumer_node_num];//consumer node to node 0
	int *pos=new int[consumer_node_num];

	for (int i = 1; i < consumer_node_num; ++i)
	{
		dist20[i]=dist[consumer_node[linknetid][0]][consumer_node[i][0]];
		pos[i]=i;
	}
	for (int i = 1; i < consumer_node_num; ++i)
	{
		for (int j = 1; j< consumer_node_num-1; ++j)
		{
			if (dist20[j]>dist20[j+1])
			{
				temp=dist20[j];
				dist20[j]=dist20[j+1];
				dist20[j+1]=temp;

				temp=pos[j];
				pos[j]=pos[j+1];
				pos[j+1]=temp;
			}
		}
	}
	linknetid=consumer_node[0][0];
	for (int i =1; i < consumer_node_num/2; ++i)
	{
		getlowpath(pre[linknetid],linknetid,consumer_node[pos[i]][0],path);
		temp=getMidNode(linknetid,consumer_node[pos[i]][0],rent_money,path,dist[linknetid]);
		if (temp!=linknetid)
		{
			result[servernum++]=temp;
//			cout<<"servernum  "<<result[servernum-1]<<endl;
		}
	}
	linknetid=consumer_node[consumer_node_num/2+1][0];
	for (int i = linknetid+1; i < consumer_node_num; ++i)
	{
		getlowpath(pre[linknetid],linknetid,consumer_node[pos[i]][0],path);
		temp=getMidNode(linknetid,consumer_node[pos[i]][0],rent_money,path,dist[linknetid]);
		if (temp!=linknetid)
		{
			result[servernum++]=temp;
//			cout<<"servernum  "<<result[servernum-1]<<endl;
		}		
	}
	delete dist20;
	delete pos;
	return servernum;
}
void getallLowPath(int node_num,vector<vector<int> > &dist, vector<vector<int> > &pre, short int **chengben, short int **graphic)
{
	for (int i = 0; i <node_num; ++i)
	{
		Dijkstra(node_num,i,dist[i],pre[i],chengben,graphic);
	}
}
/*n是总的结点数,start_id是出发结点,dist是start_id到各点的距离,pre前一个结点,rent_money是结点间的权值*/
void Dijkstra(int n, int start_id, vector<int> &dist, vector<int> &pre, short int **chengben,short int **graphic)
{
    vector<bool> s(n);	//the min node have found
    for (int i = 0; i < n;i++)
    {
    	if (graphic[start_id][i]!=0)// if have a path
    	{
	        dist[i] = chengben[start_id][i];
	        if (dist[i] < maxdist)
	            pre[i] = start_id;
	        else
	            pre[i] = 0;
    	}
    	else
    	dist[i] = maxdist;
    }
    dist[start_id] = 0;
    s[start_id] = true;
    for (int i = 1; i < n;i++)//总的迭代次数
    {
        int best_node = start_id;
        int temp = maxdist;
        for (int j = 0; j <n;j++)//找到最小的距离
        {
            if (!s[j]&&dist[j]<temp)
            {
                temp = dist[j];
                best_node = j;
            }
        }
        s[best_node] = true;
 //       cout<<"best_node"<<best_node<<endl;
        for (int j = 0; j <n;j++)//更新dist和pre
        {
            if (!s[j] && graphic[best_node][j] != 0)
            {
                int newdist = dist[best_node] + chengben[best_node][j];
                if (newdist<dist[j])
                {
                    dist[j] = newdist;
                    pre[j] = best_node;
                }
            }
        }       
    }
 //   cout<<"endl"<<endl;
}
//select the rent money divide bandwidth as the distance for the dijstra
void getRentBW(short int**graphic,short int**rent_money,short int **rent_BW,int node_num)
{
	for (int i = 0; i < node_num; ++i)
	{
		for (int j= 0; j < node_num; ++j)
		{
			if (graphic[i][j]!=0)
			{
				rent_BW[i][j]=(rent_money[i][j]/graphic[i][j])*100;
				cout<<rent_BW[i][j]<<endl;
			}
		}
	}
}
//获取最短路径-path[0] is the end node
vector<int> getlowpath(vector<int> pre, int init, int fina,vector<int> &path)
{
	int temp=fina;//cout<<path.size()<<"getpath:";
	path.clear();
    while (temp != init)
    {
        path.push_back(temp);
        temp = pre[fina];
        fina = temp;
    }
    path.push_back(init);
  //   cout << init << "->";
    //  for (int i = path.size(); i >0;i--)
    //  {
    //      cout << path[i-1] << "->";
    //  }
    // cout <<endl;
    return path;
}
void printpath(vector<int> path)
{
	cout<<"path[0]:"<<path[0]<<" ";
    for (int i = path.size(); i >0;i--)
    {
        cout << path[i-1] << "->";
    }
    cout <<endl;
  //  path.clear();
}

int getMidNode(int start_id,int end_id,short int **rent_money,vector<int> path,vector<int> dist)
{
	int curdist=0;
	int lastnode=start_id;
	for (int i = path.size()-1; i >=0;i--)
	{
		curdist+=rent_money[lastnode][path[i]];
		if(curdist>=dist[end_id]/2)
		{
			return lastnode;
		}
		lastnode=path[i];
	}
	return lastnode;
}

//找到起始点到各服务器节点的最低路径成本服务器
int getminServernode(vector<int> dist,int *resultServerNode,int server_num)
{
	int mindist=maxdist;
	int pos=0;
	for (int j = 0; j < server_num; ++j)
	{	//cout<<j<<"start_id"<<resultServerNode[j]<<endl;
		if (mindist>dist[resultServerNode[j]])
		{	
		//	cout<<"selcet server"<<resultServerNode[j]<<"dist"<<dist[resultServerNode[j]]<<endl;
			mindist=dist[resultServerNode[j]];
			pos=j;
		}
	}//cout<<pos<<"dist"<<mindist<<endl;
	if (mindist==maxdist)
	{
		pos=-1;
	}
	return pos;
}

//起始点到服务器点最短路径，输出解决方案，
void getsolutionpath(int path_id,vector<int> lowpath,vector<vector<int> > &solutionpath,int minbw)
{
	int pos=1;
	for (int i = lowpath.size()-1; i >=0; --i)
	{
		solutionpath[path_id][pos++]=lowpath[i];
	}
	solutionpath[path_id][pos++]=minbw;
}
void getInfluencePath(int **server_bw,int *influenceNode,int consumer_node_num,vector<vector<int> > &solutionpath,int pathid,
	vector<vector<int> > dist,vector<vector<int> > pre,int node_num,short int **rent_money,short int **graphic,
	int** consumer_node,int chengben)
{
	int pos=0;
	std::vector<int> path;
	int server_num=0;
	int *resultServerNode=new int[node_num];
	for (int i = 0; i < node_num; ++i)
	{
		if (server_bw[i][0]!=-1)
		{
			resultServerNode[server_num]=i;// for save by hash
			server_num++;
//			cout<<"server"<<i;
		}
	}
	for (int i = 0; i < consumer_node_num; ++i)
	{	
		if (influenceNode[i]<=0)
		{
			continue;
		}
		solutionpath[pathid][pos++]=i;
		int need=influenceNode[i];
//		cout<<"consumer_node------------"<<i<<"need"<<need<<endl;
		while(need>0)
		{
			int temp=0;//zwc20170316
			int minbw=0;
			initdist(dist);
			Dijkstra(node_num,consumer_node[i][0],dist[consumer_node[i][0]],pre[consumer_node[i][0]],rent_money,graphic);
			// for (int a= 0; a < dist[consumer_node[i][0]].size(); ++a)
			// {
			// 	cout<<"dist"<<dist[consumer_node[i][0]][a]<<endl;
			// }
			temp=getminServernode(dist[consumer_node[i][0]],resultServerNode,server_num);
			if (temp==-1)
			{
				solutionpath[pathid][pos++]=consumer_node[i][0];
				solutionpath[pathid][pos++]=0;	
				resultServerNode[server_num]=consumer_node[i][0];
				server_num=server_num+1;
				rollBackGraphic(solutionpath,graphic,i,pathid);
//					cout<<"break no path to server"<<endl;
				break;					
			}
			else
			{
				consumer_node[i][2]=resultServerNode[temp];	
			}
//				cout<<dist[consumer_node[i][0]][resultServerNode[temp]]<<"ser:"<<resultServerNode[temp]<<endl;			
			if (dist[consumer_node[i][0]][resultServerNode[temp]]>chengben/need)
			{
				consumer_node[i][2]=consumer_node[i][0];
				solutionpath[pathid][pos++]=consumer_node[i][0];
				solutionpath[pathid][pos++]=0;	
				resultServerNode[server_num]=consumer_node[i][0];
				server_num=server_num+1;
			//	if (is_rollback)
				{
				rollBackGraphic(solutionpath,graphic,i,pathid);
				}
//					cout<<"break path money lager"<<endl;
				break;						
			}		
			path.clear();				
			getlowpath(pre[consumer_node[i][0]],consumer_node[i][0],consumer_node[i][2],path);
		//		printpath(path);
			minbw=getlowBwpath(path,graphic);
//				cout<<"minbw"<<minbw<<"need:"<<need<<endl;
			if (minbw>need)
			{
				minbw=need;
			}					
			getsolutionpath(pathid,path,solutionpath,minbw);
			need-=minbw;
			for (int i = path.size()-1; i >0; --i)
			{
				graphic[path[i]][path[i-1]]-=minbw;
			}
			if (need>0)
			{
				pathid++;
				// if (pathid>node_num+consumer_node_num-1)
				// {
				// 	break;
				// }
				solutionpath[pathid][0]=i;
			}
//			cout<<"pathid"<<pathid<<"need"<<need<<endl;
		}//end of while	
		// if (pathid>node_num+consumer_node_num-1)
		// {
		// 	break;
		// }
		pathid++;
		path.clear();
		pos=0;
	}
	delete resultServerNode;
}
bool getAnothernode(short int **rent_money,short int **graphic,int start_id,vector<int> path,
	int except_num,int node_num,int *anothernode)
{
	int mindist=0xffff;
	bool node_id=false;
	int bw_pay=0;
	cout<<"start_another_id:"<<start_id<<endl;
	for (int i = 0; i < node_num; ++i)//if have no node ??
	{
		if (rent_money[start_id][i]!=0)
		{
			if (!checkpath(i,path)&&i!=except_num&&graphic[start_id][i]>0)
			{
				//bw_pay=rent_money[start_id][i]/graphic[start_id][i];
				bw_pay=rent_money[start_id][i];
				if (mindist>bw_pay)
				{
					mindist=rent_money[start_id][i];
					anothernode[0]=i;
					node_id=true;
				}
			}
		}
	}
	anothernode[1]=graphic[start_id][anothernode[0]];
	cout<<"anothernode:"<<anothernode[0]<<endl;
	return node_id;
}
bool checkpath(int node_id,vector<int> path)
{
	for (int i = 0; i < path.size(); ++i)
	{
		if (node_id==path[i])
		{
			return true;
		}
	}
	return false;
}
int getinitServerNode(short int **graphic,int *resultServerNode,int node_num,int consumer_node_num)
{
	char *num=new char[node_num];
	int max=0,temp=0;
	int server_num=0;
	for (int i = 0; i < node_num; ++i)
	{
		for (int j = 0; j < node_num; ++j)
		{
			if (graphic[i][j]!=0)
			{
				num[i]++;
			}
		}
	}
	for (int i = 0; i < consumer_node_num; ++i)
	{
		for (int j = 0; j < node_num; ++j)
		{
			if (num[j]>max)
			{
				max=num[j];
				temp=j;
			}
		}
		resultServerNode[server_num++]=temp;
	//	cout<<"server_node:"<<temp<<endl;
		num[temp]=0;
		max=0;
	}
	return server_num;
}

void getcharpath(char *reschar,vector<vector<int> > solutionpath)
{
	char *temp=new char[8];
	char linend='\n';
	int j=0;
	int bw=0;
	bool isfirst=false;
	for (int i = 0; i < solutionpath.size(); ++i)
	{
		for (j = solutionpath[i].size()-1; j >=0; --j)
		{
			if (solutionpath[i][j]!=-1)
			{
				if (!isfirst)
				{
					isfirst=true;
					bw=solutionpath[i][j];
					sprintf(temp,"%c",linend);
					strcat(reschar,temp);
			//		cout<<"bw"<<bw<<endl;
				}
				else
				{
				sprintf(temp,"%d ",solutionpath[i][j]);
				strcat(reschar,temp);
			//	cout<<reschar<<endl;
				}
			}
		}
		if (solutionpath[i][0]!=-1)
		{
			sprintf(temp,"%d",bw);
			strcat(reschar,temp);
		}
		isfirst=false;
	}
}
int correctPath(vector<vector<int> >&solutionpath,int **consumer_node)
{
	int pathsize=solutionpath.size();
	int *errorPath=new int[pathsize];
	memset(errorPath,-2,sizeof(int)*pathsize);
	int pos=0;
	int lastnode=-3;
	int returnpathsize=0;
	for (int i = 0; i < pathsize; ++i)
	{
		for (int j=solutionpath[i].size()-1;j>=0 ; --j)
		{
			if (solutionpath[i][j]!=-1)
			{
				if (solutionpath[i][j]==0&&j!=0)
				{	
					if (solutionpath[i][0]!=lastnode)
					{
						errorPath[pos++]=solutionpath[i][0];
						lastnode=solutionpath[i][0];
					}
					break;//zwc20170317
				}else{break;}
			}
		}
	}
	for (int i = 0; i < pathsize; ++i)
	{
		for (int j = 0; j < pos; ++j)
		{
			if (solutionpath[i][0]==errorPath[j])
			{
				for (int k = 0; k < solutionpath[i].size(); ++k)
				{
					solutionpath[i][k]=-1;
				}
			}
		}
	}
	int nn=0;
	for (int i = 0; i < pathsize; ++i)
	{
		if (nn>=pos)
		{
			break;
		}		
		if (solutionpath[i][0]==-1)
		{
			solutionpath[i][0]=errorPath[nn];
			solutionpath[i][1]=consumer_node[errorPath[nn]][0];//set the server in the node near to consumer
			solutionpath[i][2]=consumer_node[errorPath[nn]][1];
			nn++;
		}
	}
	for (int i = 0; i < pathsize; ++i)
	{
		if (solutionpath[i][0]!=-1)
		{
			returnpathsize++;
		}
	}
	return returnpathsize;
}
int betterPath(vector<vector<int> >&solutionpath,int **consumer_node,int node_num,int consumer_node_num)
{
	int *server=new int [node_num];
	int *consumer=new int [consumer_node_num];
	memset(server,0,sizeof(int)*node_num);
	memset(consumer,0,sizeof(int)*consumer_node_num);
	bool is_need=false;
	for (int i = 0; i < solutionpath.size(); ++i)
	{
		for (int j = solutionpath[i].size()-1; j>0; --j)
		{
			if (solutionpath[i][j]!=-1&&j>0)
			{
				server[solutionpath[i][j-1]]++;
				if (j>=3)//if path length lager than 3
				{
					consumer[solutionpath[i][0]]++;
					if (consumer[solutionpath[i][0]]>1)
					{
						is_need=true;
					}
				}
				break;
			}
		}
	}
	int servernum=0;
	for (int i = 0; i < node_num; ++i)
	{
		if (server[i]>0)
		{
			servernum++;
		}
	}
	int pos=0;
	//cout<<"servernum"<<servernum<<endl;
	if ((servernum>=consumer_node_num)&&is_need)
	{
		for (int i = 0; i <consumer_node_num; ++i)
		{
			if (consumer[i])
			{
				for (int k = 0; k < solutionpath.size(); ++k)
				{
					if (solutionpath[k][0]==i)//if equal to consumer node
					{
						pos=k;
						for (int j =0;j< solutionpath[k].size(); ++j)
						{
							solutionpath[k][j]=-1;
						}
					}

				}
				solutionpath[pos][0]=i;
				solutionpath[pos][1]=consumer_node[i][0];
				solutionpath[pos][2]=consumer_node[i][1];
			}
		}
	}
	int returnpathsize=0;
	for (int i = 0; i < solutionpath.size(); ++i)
	{
		if (solutionpath[i][0]!=-1)
		{
			returnpathsize++;
		}
	}
	//cout<<returnpathsize;
	delete server;
	delete consumer;
	return returnpathsize;
}
//find the min bw int the path
int getlowBwpath(vector<int> path,short int **graphic)
{
	int minBW=0xffff;
	for (int i = path.size()-1; i >0; --i)
	{
		if (minBW>graphic[path[i]][path[i-1]])
		{
			minBW=graphic[path[i]][path[i-1]];
			//cout<<path[i]<<"-"<<path[i-1]<<"-"<<minBW<<endl;
		}
	}
	return minBW;
}
void initdist(vector<vector<int> >&dist)
{
	for (int i = 0; i < dist.size(); ++i)
	{
		for (int j = 0; j < dist[i].size(); ++j)
		{
			dist[i][j]=maxdist;
		}
	}
}
void sortConsumer(int **consumer_node,int consumer_node_num,int *sortConsumer)
{
	int temp;
	for (int i = 0; i < consumer_node_num; ++i)
	{
		sortConsumer[i]=i;
	}
	for (int i = 0; i < consumer_node_num; ++i)
	{
		for (int j = 1; j < consumer_node_num; ++j)
		{
			if(consumer_node[sortConsumer[j-1]][1]<consumer_node[sortConsumer[j]][1])
			{
				temp=sortConsumer[j-1];
				sortConsumer[j-1]=sortConsumer[j];
				sortConsumer[j]=temp;
			}
		}
	}
	// for (int i = 0; i < consumer_node_num; ++i)
	// {
	// 	cout<<sortConsumer[i]<<" ";
	// }
}
void getServerProvideBW(vector<vector<int> > solutionpath,int **server_bw)
{
	int pos=0;
	for (int i = 0; i < solutionpath.size(); ++i)
	{
		if (solutionpath[i][0]!=-1)
		{
			for (int j = 1; j < solutionpath[i].size(); ++j)
			{
				if (solutionpath[i][j]==-1)
				{
					server_bw[solutionpath[i][j-2]][0]=solutionpath[i][0];//consumer node id
					server_bw[solutionpath[i][j-2]][1]+=solutionpath[i][j-1];//node bw
					break;
				}
			}
		}
	}
}
//roll back graphic bandwith for the error path
void rollBackGraphic(vector<vector<int> >solutionpath,short int **graphic,int consumer_id,int pathid)
{
	int bw=0;
	for (int i = 0; i < pathid; ++i)
	{
		if (consumer_id==solutionpath[i][0])
		{
			for (int k =solutionpath[i].size()-1; k>0; --k)
			{
				if (solutionpath[i][k]!=-1)
				{
					bw=solutionpath[i][k];
					break;
				}
			}
			if (bw==0)
			{
				break;
			}
			for (int j =1; j < solutionpath[i].size()-2; ++j)
			{
				if (solutionpath[i][j+2]!=-1)
				{
					graphic[solutionpath[i][j]][solutionpath[i][j+1]]+=bw;
				//	cout<<solutionpath[i][j]<<"->"<<solutionpath[i][j+1]<<endl;
				}
				else
				{
					break;
				}
			}
		}
	}
}
