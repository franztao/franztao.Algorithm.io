/*
 * rulesets.h
 *
 *  Created on: Jan 10, 2017
 *      Author: franz
 */

#ifndef HEAD_RULESETS_H_
#define HEAD_RULESETS_H_
#include"../Head/head.h"
/*
 @245.54.131.162/2	13.179.253.75/1	21032 : 60587	1224 : 57037	0x08/0xFF	0x149e/0x2f20
 @180.24.244.53/26	42.28.175.166/20	4241 : 40672	47891 : 53644	0xcf/0xFF	0x853f/0x4370
 @95.189.236.105/9	57.146.241.123/27	35678 : 54838	27453 : 28183	0xbe/0xFF	0xb635/0x8303
 @252.154.153.143/21	18.7.133.71/2	36391 : 49000	28552 : 29340	0x8a/0xFF	0x8371/0x7bf6
 */
class Rule {
public:
	uint32_t nw_src; /* IP source address. */
	uint32_t src_mask;

	uint32_t nw_dst; /* IP destination address. */
	uint32_t dst_mask;

	uint16_t in_port_l; /* Input switch port. */
	uint16_t in_port_r;

	uint16_t out_port_l; /* Input switch port. */
	uint16_t out_port_r;

	uint8_t nw_proto; /* IP protocol or lower 8 bits of
	 * ARP opcode. */
};
class RuleSet {
public :
	Rule ruleSets[LimitRules];
	unsigned  ruleSize=0;
	unsigned  fileds=0;
};

#endif /* HEAD_RULESETS_H_ */
