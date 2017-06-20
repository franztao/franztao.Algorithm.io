/*
 * DataProcessFunction.cpp
 *
 *  Created on: Jan 10, 2017
 *      Author: franz
 */
#include"../Head/packet.h"
#include"../Head/rulesets.h"
#include"../CBF/BFrange.h"

using namespace std;
//@20.64.180.124/32	190.31.238.11/32	0 : 65535	1526 : 1526	0x06/0xFF	0x0000/0x0200

bool judge(const pair<unsigned, unsigned> a, const pair<unsigned, unsigned> b) {
	if (a.first < b.first) {
		return true;
	} else {
		if (a.first == b.first) {
			if (a.second < b.second) {
				return true;
			}
		} else {
			return false;
		}
	}
	return false;
}

void ProcessRuleSet(RuleSet * ruleset, char *rulesetlinestring) {
	unsigned stringlen = strlen(rulesetlinestring);
	unsigned i, mask, mid = 0, src = 0;

	for (i = 0; i < stringlen; i++) {
		if (rulesetlinestring[i] == '/') {
			src = (src << 8) + mid;
			mid = 0;
			i++;
			mid = mid * 10 + rulesetlinestring[i] - '0';
			i++;
			mid = mid * 10 + rulesetlinestring[i] - '0';
			mask = UINT_MAX << (32 - mid);
			mid = 0;
			i++;
			break;
		}
		if (rulesetlinestring[i] == '.') {
			src = (src << 8) + mid;
			mid = 0;
		}
		if ((rulesetlinestring[i] <= '9') && (rulesetlinestring[i] >= '0')) {
			mid = mid * 10 + (rulesetlinestring[i] - '0');
		}
	}
	ruleset->ruleSets[ruleset->ruleSize].nw_src = src;
	ruleset->ruleSets[ruleset->ruleSize].src_mask = mask;

	unsigned dst = 0;
	mask = 0;
	for (; i < stringlen; i++) {
		if (rulesetlinestring[i] == '/') {
			dst = (dst << 8) + mid;
			mid = 0;
			i++;
			mid = mid * 10 + rulesetlinestring[i] - '0';
			i++;
			mid = mid * 10 + rulesetlinestring[i] - '0';
			mask = UINT_MAX << (32 - mid);
			;
			mid = 0;
			i++;
			break;
		}
		if (rulesetlinestring[i] == '.') {
			dst = (dst << 8) + mid;
			mid = 0;
		}
		if ((rulesetlinestring[i] <= '9') && (rulesetlinestring[i] >= '0')) {
			mid = mid * 10 + (rulesetlinestring[i] - '0');
		}
	}
	ruleset->ruleSets[ruleset->ruleSize].nw_dst = dst;
	ruleset->ruleSets[ruleset->ruleSize].dst_mask = mask;

	bool havenumber = false;
	mid = 0;
	for (; i < stringlen; i++) {
		if ((rulesetlinestring[i] <= '9') && (rulesetlinestring[i] >= '0')) {
			havenumber = true;
			mid = mid * 10 + (rulesetlinestring[i] - '0');
		} else {
			if (havenumber) {
				havenumber = false;
				break;
			}
		}
	}
	ruleset->ruleSets[ruleset->ruleSize].in_port_l = mid;

	mid = 0;
	for (; i < stringlen; i++) {
		if ((rulesetlinestring[i] <= '9') && (rulesetlinestring[i] >= '0')) {
			havenumber = true;
			mid = mid * 10 + (rulesetlinestring[i] - '0');
		} else {
			if (havenumber) {
				havenumber = false;
				break;
			}
		}
	}
	ruleset->ruleSets[ruleset->ruleSize].in_port_r = mid;

	mid = 0;
	for (; i < stringlen; i++) {
		if ((rulesetlinestring[i] <= '9') && (rulesetlinestring[i] >= '0')) {
			havenumber = true;
			mid = mid * 10 + (rulesetlinestring[i] - '0');
		} else {
			if (havenumber) {
				havenumber = false;
				break;
			}
		}
	}
	ruleset->ruleSets[ruleset->ruleSize].out_port_l = mid;

	mid = 0;
	for (; i < stringlen; i++) {
		if ((rulesetlinestring[i] <= '9') && (rulesetlinestring[i] >= '0')) {
			havenumber = true;
			mid = mid * 10 + (rulesetlinestring[i] - '0');
		} else {
			if (havenumber) {
				havenumber = false;
				break;
			}
		}
	}
	ruleset->ruleSets[ruleset->ruleSize].out_port_r = mid;

	ruleset->ruleSize++;
}

void ReadRuleSet(const char * rulestring, RuleSet *ruleset) {
	ifstream ifs_rulefile(rulestring);
	if (ifs_rulefile.is_open()) {
		unsigned readRules = 0;
		char rulesetlinestring[RuleFileLinestrLen];
		while ((!ifs_rulefile.eof()) && (readRules < LimitRules)) {
			ifs_rulefile.getline(rulesetlinestring, RuleFileLinestrLen);

			ProcessRuleSet(ruleset, rulesetlinestring);
			readRules++;
		}
	} else {
		cout << "Error opening ruleset file" << endl;
		exit(1);
	}
	ifs_rulefile.close();
}
void ProcessPacketSet(PacketSet *packetset, char * packetlinestring) {
	unsigned stringlen = strlen(packetlinestring);
	bool havenumber = false;
	unsigned mid = 0;
	unsigned i = 0;
	for (; i < stringlen; i++) {
		if ((packetlinestring[i] <= '9') && (packetlinestring[i] >= '0')) {
			havenumber = true;
			mid = mid * 10 + (packetlinestring[i] - '0');
		} else {
			if (havenumber) {
				havenumber = false;
				break;
			}
		}
	}
	packetset->packetSets[packetset->packetSize].nw_src = mid;
	packetset->fileds++;

	mid = 0;
	for (; i < stringlen; i++) {
		if ((packetlinestring[i] <= '9') && (packetlinestring[i] >= '0')) {
			havenumber = true;
			mid = mid * 10 + (packetlinestring[i] - '0');
		} else {
			if (havenumber) {
				havenumber = false;
				break;
			}
		}
	}
	packetset->packetSets[packetset->packetSize].nw_dst = mid;
	packetset->fileds++;

	mid = 0;
	for (; i < stringlen; i++) {
		if ((packetlinestring[i] <= '9') && (packetlinestring[i] >= '0')) {
			havenumber = true;
			mid = mid * 10 + (packetlinestring[i] - '0');
		} else {
			if (havenumber) {
				havenumber = false;
				break;
			}
		}
	}
	packetset->packetSets[packetset->packetSize].in_port = mid;
	packetset->fileds++;

	mid = 0;
	for (; i < stringlen; i++) {
		if ((packetlinestring[i] <= '9') && (packetlinestring[i] >= '0')) {
			havenumber = true;
			mid = mid * 10 + (packetlinestring[i] - '0');
		} else {
			if (havenumber) {
				havenumber = false;
				break;
			}
		}
	}
	packetset->packetSets[packetset->packetSize].out_port = mid;

	packetset->packetSize++;

}
void ReadPacketSet(const char * packetstring, PacketSet *packetset) {
	ifstream ifs_packetfile(packetstring);
	if (ifs_packetfile.is_open()) {
		unsigned readPackets = 0;
		char packetsetlinestring[PacketFileLinestrLen];
		while ((!ifs_packetfile.eof()) && (readPackets < LimitPackets)) {
			ifs_packetfile.getline(packetsetlinestring, PacketFileLinestrLen);

			ProcessPacketSet(packetset, packetsetlinestring);
			readPackets++;
		}
	} else {
		cout << "Error opening Packetset file" << endl;
		exit(1);
	}
	ifs_packetfile.close();
}

void ReadData(const char *rule, const char * packet, RuleSet *ruleset,
		PacketSet *packetset) {
	ReadRuleSet(rule, ruleset);
	ReadPacketSet(packet, packetset);
	cout << "!!!open World!!!" << endl;
}

void Experiment(RuleSet *ruleset, PacketSet *packetset) {
	//prefix match,filed:src
	BFrange* bfrange_src = new BFrange(FieldBitSize);

	bool isOFF = true;

	for (unsigned i = 0; i < ruleset->ruleSize; i++) {
		unsigned src_l = ruleset->ruleSets[i].nw_src
				& ruleset->ruleSets[i].src_mask;
		unsigned src_r = ruleset->ruleSets[i].nw_src
				| (~ruleset->ruleSets[i].src_mask);
		bfrange_src->insertRangeRule2BFrang(i, src_l, src_r, isOFF);
	}

	for (unsigned i = 0; i < packetset->packetSize; i++) {

		bfrange_src->queryAllRanges4Element(packetset->packetSets[i].nw_src,
				isOFF);
	}
}

