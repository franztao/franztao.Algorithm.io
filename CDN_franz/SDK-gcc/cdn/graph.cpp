#include "cdn.h"


void GraphTopo::initial(char *topo[MAX_EDGE_NUM], int line_num)
{
	int src, des_t, ci, b, c, de;
	char *pos_line;
	pos_line = topo[0];
	sscanf(pos_line, "%d%d%d", &Nnum, &Line_num, &Cnum);
	pos_line = topo[2];
	sscanf(pos_line, "%d", &Server_Cost);
	
	node = Nnum + Cnum + 2;
	int n = 0;
	for (int i = 4; i< Line_num + 4; i++)
	{
		pos_line = topo[i];
		src = des_t = b = c = 0;
		sscanf(pos_line, "%d%d%d%d", &src, &des_t, &b, &c);

		from_Index[n] = src;
		v[n] = des_t;
		bt[n] = b;
		cs[n++] = c;
		Netnode_degree[src] += b;
		Netnode_degree[des_t] += b;
		
	}
	for (int i = Line_num + 5; i < line_num; i++)
	{
		pos_line = topo[i];
		ci = des_t = de = 0;
		sscanf(pos_line, "%d%d%d", &ci, &des_t, &de);

		s[ci] = ci + Nnum;
		sv[ci] = des_t;
		C_Demand[ci] = de;
		demand[de] = des_t;
		
	}
}
