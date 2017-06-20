#include "deploy.h"
#include <stdio.h>
#include "solution.h"
#include <iostream>
#include "memory.h"
//你要完成的功能总入口
//topo二维字符数组，存储每行字符串
void getNumFromLine(char *line_str,int *result);
void getmidsolution(char * topo[MAX_EDGE_NUM], int line_num,char * filename,int *final_server)
{
	using namespace std;
	int line_pos=0;
	 int node_num,link_num,consumer_node_num;//网络节点数，链路，消费节点
	 int chengben;//服务器成本
	int temp[4]={0};
//	bool isnull=true;
	int low_server_bw=65;
	bool is_rollback=true;
	bool is_mid_deg=false;//yuan true
	int avg_pay=0;
	//int special_node;
	getNumFromLine(topo[line_pos],temp);
	node_num=temp[0];
	link_num=temp[1];
	consumer_node_num=temp[2];
//	printf("%d,%d,%d\n",temp[0],temp[1],temp[2] );
	short int **graphic=new short int* [node_num];//
	short int **rent_money=new short int*[node_num];
	int **consumer_node=new int* [consumer_node_num];
	for(int i=0;i<node_num;i++)
	{
		graphic[i]=new short int[node_num];
		memset(graphic[i],0,sizeof(short int)*node_num);//初始化
		rent_money[i]=new short int[node_num];
		memset(rent_money[i],0,sizeof(short int)*node_num);
	}
	for (int i = 0; i < consumer_node_num; ++i)
	{
		consumer_node[i]=new int[3];//0:linkenode,1,needbandwidth2,server3,havepath
		memset(consumer_node[i],0,sizeof(int)*3);
	}
//	printf("%d,%d\n",consumer_node[0][0],consumer_node[1][1]);
	line_pos+=2;//
	getNumFromLine(topo[line_pos],temp);
	chengben=temp[0];
//	std::cout<<"cb:"<<chengben<<endl;
	line_pos+=2;
	for (int i = 0; i < link_num; ++i)//用邻接矩阵保存图的带宽和租赁费用
	{
		getNumFromLine(topo[line_pos],temp);
		graphic[temp[0]][temp[1]]=temp[2];
		graphic[temp[1]][temp[0]]=temp[2];	//全链接图
		rent_money[temp[0]][temp[1]]=temp[3];
		rent_money[temp[1]][temp[0]]=temp[3];
		++line_pos;
//		avg_pay+=temp[3];
//		printf("%d,%d,%d,%d\n",temp[0],temp[1],temp[2],temp[3]);
	}
//	avg_pay=avg_pay/link_num;
//	yuzhi=chengben/avg_pay;
//	cout<<"yuzhi"<<yuzhi<<endl;
	line_pos+=1;
	for (int i = 0; i < consumer_node_num; ++i)//保存消费节点和需要bandwidth
	{
		getNumFromLine(topo[line_pos],temp);
		consumer_node[temp[0]][0]=temp[1];//link node
		consumer_node[temp[0]][1]=temp[2];//bandwidth
		++line_pos;
//		printf("%d,%d,%d\n",temp[0],temp[1],temp[2]);
	}
	int pathsizenum=node_num*4;	

		low_server_bw=70;
		is_rollback=true;
		is_mid_deg=false;//yuan true
		pathsizenum=node_num*4;		
	
	int *resultServerNode=new int[node_num];//zwc warn
	int server_num=0;

//	int *servernum=&server_num;	
	memset(resultServerNode,0,sizeof(int)*node_num);
	vector<int> dist_temp(node_num,9999),pre_temp(node_num);
	vector<vector<int> > dist(node_num,dist_temp);
	vector<vector<int> > pre(node_num,pre_temp);


	 getallLowPath(node_num,dist,pre,rent_money,graphic);//zwc0321 set the ren_bw as the distance
	if (is_mid_deg)
	{//max
	server_num=getMidServerNode(rent_money,consumer_node,consumer_node_num,resultServerNode,
		dist,pre);
	}
	else
	{//min
	server_num=getinitServerNode(graphic,resultServerNode, node_num, consumer_node_num);
	}
//	// server_num=getNearMidServer(rent_money,consumer_node, consumer_node_num,resultServerNode,dist,pre);
////	server_num=getLowRentServer(rent_money,graphic,consumer_node, consumer_node_num, node_num, resultServerNode,dist);
//	cout<<"servernum"<<server_num<<endl;

	for (int i = 0; i < consumer_node_num; ++i)
	{
		int temp=0;
		temp=getminServernode(dist[consumer_node[i][0]],resultServerNode,server_num);
		consumer_node[i][2]=resultServerNode[temp];
//		cout<<consumer_node[i][2]<<endl;
	}
	int pathid=0;
	int pos=0;

	 vector<int> path;
	vector<int> path_temp(node_num,-1);
	vector<vector<int> > solutionpath(pathsizenum,path_temp);

	int *consumer_sort=new int[consumer_node_num];
	sortConsumer(consumer_node, consumer_node_num,consumer_sort);
	int sort_id=0;
	//for (int i = 0; i < consumer_node_num; ++i)
	 for (int sort_id = 0;sort_id < consumer_node_num; ++sort_id)
	 {	
	 	int i=consumer_sort[sort_id];
		solutionpath[pathid][pos++]=i;
//		cout<<"consumer_node------------------"<<i<<endl;
		// if (consumer_node[i][1]>yuzhi)
		// {	
		// 	solutionpath[pathid][pos++]=consumer_node[i][0];
		// 	solutionpath[pathid][pos++]=consumer_node[i][1];
		// 	//cout<<"lager;"<<endl;
		// }
		// else
		// {
			int need=consumer_node[i][1];
			while(need>0)
			{
				int temp=0;//zwc20170316
				int minbw=0;
				initdist(dist);
//				getRentBW(graphic,rent_money,rent_BW,node_num);
				Dijkstra(node_num,consumer_node[i][0],dist[consumer_node[i][0]],pre[consumer_node[i][0]],rent_money,graphic);
				temp=getminServernode(dist[consumer_node[i][0]],resultServerNode,server_num);
				if (temp==-1)
				{
					consumer_node[i][2]=consumer_node[i][0];
					solutionpath[pathid][pos++]=consumer_node[i][0];
					solutionpath[pathid][pos++]=0;	
					resultServerNode[server_num]=consumer_node[i][0];
					server_num=server_num+1;
					if (is_rollback)
					{
					rollBackGraphic(solutionpath,graphic,i,pathid);
					}
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
					if (is_rollback)
					{
					rollBackGraphic(solutionpath,graphic,i,pathid);
					}
//					cout<<"break path money lager"<<endl;
					break;						
				}			
					path.clear();				
				getlowpath(pre[consumer_node[i][0]],consumer_node[i][0],consumer_node[i][2],path);
				//	printpath(path);
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
					solutionpath[pathid][0]=i;
				}
			}//end of while
//		}	
		pathid++;
		path.clear();
		pos=0;
	}
	int pathsize=0;
	correctPath(solutionpath,consumer_node);

	//pathid=betterPath(solutionpath,consumer_node, node_num, consumer_node_num);
	int **server_bw=new int*[node_num];
	int *influence_node=new int[consumer_node_num];
	memset(influence_node,0,sizeof(int)*consumer_node_num);
//	int influence_node_num=0;
	for (int i = 0; i < node_num; ++i)
	{
		server_bw[i]=new int[2];
		server_bw[i][0]=-1;	//consumer id
		server_bw[i][1]=0;	//all bw
	}
	getServerProvideBW(solutionpath,server_bw);//cout<<"hello"<<endl;
	for (int i = 0; i < node_num; ++i)
	{	
//		cout<<i<<"-consumer:"<<server_bw[i][0]<<"-bw:"<<server_bw[i][1]<<endl;
		if (server_bw[i][0]!=-1&&server_bw[i][1]<low_server_bw)//then delete
		{
			for (int j = 0; j < solutionpath.size(); ++j)
			{
				if (solutionpath[j][0]!=-1)
				{
				//	for (int k = 1; k < solutionpath[j].size(); ++k)
					for (int k = solutionpath[j].size()-1; k>0;--k)
					{
						if (solutionpath[j][k]!=-1&&solutionpath[j][k-1]==i)//find the delete server node
						{
	//					cout<<"delet server"<<i<<"consumer_node"<<solutionpath[j][0]<<"bw"<<solutionpath[j][k]<<endl;
						influence_node[solutionpath[j][0]]+=solutionpath[j][k];//consumer bandwidth id
							for (int m = 0; m < node_num; ++m)
							{
								if (m<node_num-3)
								{
								if (solutionpath[j][m+3]!=-1)
								{
									graphic[solutionpath[j][m+1]][solutionpath[j][m+2]]+=solutionpath[j][k];
								//	cout<<solutionpath[j][m+1]<<"--"<<solutionpath[j][m+2]<<endl;
								}
								}
								//cout<<solutionpath[j][m]<<" ";
								solutionpath[j][m]=-1;//delete the path
							}
							break;
						}
						if (solutionpath[j][k]!=-1)
						{
							break;
						}
					}
				}
			}
			server_bw[i][0]=-1;		//delete the server node
		}
	}
//	getRentBW(graphic,rent_money,rent_BW,node_num);
	getInfluencePath(server_bw,influence_node,consumer_node_num,solutionpath,pathid,dist,pre,node_num,
		rent_money,graphic,consumer_node,chengben);
	pathid=correctPath(solutionpath,consumer_node);

	for (int i = 0; i < solutionpath.size(); ++i)
	{
		for (int j =30;j >0;--j)
		{
			if (solutionpath[i][j]!=-1)
			{
				final_server[solutionpath[i][j-1]]=1;
			//	cout<<solutionpath[i][j-1]<<" f";
				break;
			}
		}
	}

/*	for (int i = 0; i < pathid; ++i)
	{
		for (int j = 0; j < solutionpath[i].size()-5; ++j)
		{
//			cout<<solutionpath[i][j]<<" ";
			pathsize+=2*solutionpath.size();
		}
//		cout<<endl;
	}
	//printf("%x\n",'\n' );
	char *resChar=new char[pathsize];
	sprintf(resChar,"%d\n",pathid);
	getcharpath(resChar,solutionpath);
	// 直接调用输出文件的方法输出到指定文件中(ps如果有解，第一行只有一个数据；第二行为空；第三行开始才是具体的数据，数据之间用一个空格分隔开)
	write_result(resChar, filename);*/
	 delete graphic;
	 delete rent_money;
//	 delete rent_BW; 
}
void getNumFromLine(char *line_str,int *result)
{
	int count=0,n=0;
	int startpos=0,endpos=0;
	int temp=1;
	result[n]=0;
	using namespace std;
	while(line_str[count]!='\n')
	{
		if(line_str[count++]!=' ')
		{
			endpos++;
		}else
		{
//			cout<<endpos<<endl;
			for (int i = endpos-1; i >=startpos; --i)
			{
				result[n]+=((int)(line_str[i]-'0'))*temp;
				temp*=10;
			}
			startpos=++endpos;
			n++;
			result[n]=0;
			temp=1;
		}
	}
//	cout<<"startpos"<<startpos<<"endpos"<<endpos<<endl;
	for (int i = endpos-2; i >=startpos; --i)
	{
		result[n]+=((int)(line_str[i]-'0'))*temp;
		temp*=10;
	}
	startpos=++endpos;
}
