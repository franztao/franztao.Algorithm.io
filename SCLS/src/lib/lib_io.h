#ifndef __LIB_IO_H__
#define __LIB_IO_H__

//may be more larger edge
#define MAX_EDGE_NUM    (3000 * 20)
#define MAX_DEMAND_NUM  2
//may be more srlg group
#define MAX_SRLG_NUM 1000

extern int read_file(char ** const buff, const unsigned int spec, const char * const filename);

extern void write_result(const char * const filename);

extern void release_buff(char ** const buff, const int valid_item_num);

#endif

