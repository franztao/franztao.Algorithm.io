#include "deploy.h"
#include "lib/lib_io.h"
#include "lib/lib_time.h"
#include "stdio.h"
#include <time.h>
#include "cdn.h"

int main(int argc, char *argv[])
{
    print_time("Begin");
    char *topo[MAX_EDGE_NUM];
    int line_num;

    char *topo_file = argv[1];

    line_num = read_file(topo, MAX_EDGE_NUM, topo_file);

    printf("line num is :%d \n", line_num);
    if (line_num == 0)
    {
        printf("Please input valid topo file.\n");
        return -1;
    }
    char *result_file = argv[2];

    deploy_server(topo, line_num, result_file);
    release_buff(topo, line_num);
	printf("%fs\n",(double)clock()/ CLOCKS_PER_SEC);

    print_time("End");

	return 0;
}

