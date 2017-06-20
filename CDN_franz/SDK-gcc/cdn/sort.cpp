#include "cdn.h"
vector<int> selectionNode;
int middleTempNode[MAX_N];

inline int cmp(const void* a, const void* b) 
{
	return *(int*)b - *(int*)a;
}


void Sort4Consumer()
{
	int i, c;
	int min_index, min_cost = INF, tmp_cost;
	int can_customer[G.Cnum],can_customer_need[G.Cnum];
	for (int i = 0; i < G.Cnum; i++)
	{
		can_customer[i] = G.sv[i];
		can_customer_need[i] = G.C_Demand[i];
	}
	int num;
	if (G.Nnum > 500)
		 num = G.Cnum / 2 + G.Cnum / 5;
	else
		 num = G.Cnum / 2;
	while (num--)
	{
		memset(middleTempNode, 0, sizeof(middleTempNode));
		int mindemand = INF;
		for (i = 0; i < G.Cnum; i++)
		{
			if (mindemand > can_customer_need[i] && can_customer_need[i] != INF)
			{
				mindemand = can_customer_need[i];
				min_index = i;
			}
		}
		can_customer_need[min_index] = INF;
		can_customer[min_index] = -1;
		c = 0;
		for(i = 0;i < G.Cnum; i++)
		{
			if (can_customer[i] != -1)
			{
				lastBestServer[c++] = can_customer[i];
			}
		}
		G.chosNum = c;
		tmp_cost = CostFlow_ZKW();
	
		if(tmp_cost >= INF)
		{
			lastBestServer[c++] = can_customer[min_index];
			G.chosNum++;
			can_customer[min_index] = G.sv[min_index];
		}
		if (tmp_cost < min_cost)
		{
			min_cost = tmp_cost;
			for (int i = 0; i < c; i++)
			{
				middleTempNode[lastBestServer[i]] = 1;
			}
			B.copyGene(middleTempNode, B.initNode);
		}
	}
	
}

