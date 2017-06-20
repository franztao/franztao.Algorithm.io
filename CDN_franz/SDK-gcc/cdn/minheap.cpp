#include "cdn.h"

void adjustHeap(int *p, int k, int n)
{
	int i, tmp;

	tmp = p[k];
	i = 2 * k + 1;
	while (i < n)
	{
		if (i + 1 < n && p[i + 1] < p[i]) 
			i++;

		if (p[i] >= tmp)
			break;

		p[k] = p[i];   
		k = i;
		i = 2 * k + 1;
	}
	p[k] = tmp;
}

void constructHeap(int *p,int n)
{
	for (int i = n / 2 - 1; i >= 0; i--)
	{
		adjustHeap(p, i, n);
	}
}

int deleteHeap(int *p, int n)
{
	int tmp = p[0];
	p[0] = p[n - 1];
	p[n - 1] = tmp;
	adjustHeap(p, 0, n - 1);
	return tmp;
}