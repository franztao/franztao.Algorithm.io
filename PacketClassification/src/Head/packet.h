/*
 * packet.h
 *
 *  Created on: Jan 10, 2017
 *      Author: franz
 */

#ifndef PACKET_H_
#define PACKET_H_

#include"../Head/head.h"

struct ofp_match {
	uint32_t wildcards; /* Wildcard fields. */

	uint8_t dl_src[OFP_ETH_ALEN]; /* Ethernet source address. */
	uint8_t dl_dst[OFP_ETH_ALEN]; /* Ethernet destination address. */
	uint16_t dl_vlan; /* Input VLAN id. */
	uint8_t dl_vlan_pcp; /* Input VLAN priority. */
	uint8_t pad1[1]; /* Align to 64-bits */
	uint16_t dl_type; /* Ethernet frame type. */
	uint8_t nw_tos; /* IP ToS (actually DSCP field, 6 bits). */

	uint8_t pad2[2]; /* Align to 64-bits */

	uint16_t tp_src; /* TCP/UDP source port. */
	uint16_t tp_dst; /* TCP/UDP destination port. */
};

/*
2200580984	3231205376	65535	30804	6	0	67778
567920121	3194090308	65535	1521	6	268439552	676
2419258907	282425922	65535	61509	6	0	50562
 */
class Packet{
public:
	uint32_t nw_src; /* IP source address. */
	uint32_t nw_dst; /* IP destination address. */
	uint16_t in_port; /* Input switch port. */

	uint16_t out_port; /* Input switch port. */

	uint8_t nw_proto; /* IP protocol or lower 8 bits of
		 * ARP opcode. */

};

class PacketSet{
public :
	Packet packetSets[LimitPackets];
	unsigned int packetSize=0;
	unsigned int fileds=0;

};
#endif /* PACKET_H_ */
