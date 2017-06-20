/*
 * BFrange.h
 *
 *  Created on: Jan 11, 2017
 *      Author: franz
 */

#ifndef CBF_BFRANGE_H_
#define CBF_BFRANGE_H_
#include"../Head/head.h"
class ChainElement {
public:
	unsigned ruleid;
	uint64_t key;
};
class BFbit {
public:
	unsigned count = 0;
	vector<ChainElement> chain;
	unsigned NumberChainElement = 0;
};

bool judge(const pair<unsigned, unsigned> a, const pair<unsigned, unsigned> b);

class Arg {
public:
	vector<pair<unsigned, unsigned> > argelement;

	unsigned argmin() {
		sort(this->argelement.begin(),this->argelement.end(),judge);
		return this->argelement.at(0).second;
	}
};

class Prefix {
public:
	uint32_t prefix;
	unsigned prefixLength;
	unsigned sufixWildLength;

};

void RangePrefixCalculation(unsigned l, unsigned r, vector<Prefix>&Rr);
uint64_t converingPrefix2UniqueBinary(Prefix p);

class BFrange {
public:

	vector<BFbit> BloomFilter;
	unsigned BloomFilterBitLength; //=RuleBitLength*LimitRules*HashFuncNum*2;
	BFrange(unsigned fieldbitsize) {
		BloomFilterBitLength = fieldbitsize * LimitRules * HashFuncNum * 2;
		BloomFilter = vector<BFbit>(BloomFilterBitLength);
	}
	void insertRangeRule2BFrang(unsigned id, uint32_t l,
			uint32_t r, bool isOFF);
	unsigned queryAllRanges4Element(unsigned search, bool isOFF);
	bool deleteRangeFromBFrange(unsigned src_l,unsigned src_r, bool isOFF);
	void insertChain2BFrange(vector<ChainElement> S);
};

#endif /* CBF_BFRANGE_H_ */
