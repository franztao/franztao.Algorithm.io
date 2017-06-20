/*
 * BFrang.cpp
 *
 *  Created on: Mar 20, 2017
 *      Author: franz
 */

#include"../Head/head.h"
#include "BFrange.h"

/*
 功能              |           示例            |    位运算
 ———————-+—————————+——————–
 去掉最后一位          | (101101->10110)           | x shr 1
 在最后加一个0         | (101101->1011010)         | x shl 1
 在最后加一个1         | (101101->1011011)         | x shl 1+1
 把最后一位变成1       | (101100->101101)          | x or 1
 把最后一位变成0       | (101101->101100)          | x or 1-1
 最后一位取反          | (101101->101100)          | x xor 1
 把右数第k位变成1      | (101001->101101,k=3)      | x or (1 shl (k-1))
 把右数第k位变成0      | (101101->101001,k=3)      | x and not (1 shl (k-1))
 右数第k位取反         | (101001->101101,k=3)      | x xor (1 shl (k-1))
 取末三位             | (1101101->101)            | x and 7
 取末k位              | (1101101->1101,k=5)       | x and (1 shl k-1)
 取右数第k位          | (1101101->1,k=4)          | x shr (k-1) and 1
 把末k位变成1         | (101001->101111,k=4)      | x or (1 shl k-1)
 末k位取反            | (101001->100110,k=4)      | x xor (1 shl k-1)
 把右边连续的1变成0    | (100101111->100100000)    | x and (x+1)
 把右起第一个0变成1    | (100101111->100111111)    | x or (x+1)
 把右边连续的0变成1    | (11011000->11011111)      | x or (x-1)
 取右边连续的1         | (100101111->1111)         | (x xor (x+1)) shr 1
 去掉右起第一个1的左边 | (100101000->1000)         | x and (x xor (x-1))

 将最右边的1变成0 ： x&(x-1)
 将最右边的0变成1 ： x|(x+1)
 */

unsigned int murMurHash(const void *key, int len) {
	const unsigned int m = 0x5bd1e995;
	const int r = 24;
	const int seed = 97;
	unsigned int h = seed ^ len;
	// Mix 4 bytes at a time into the hash
	const unsigned char *data = (const unsigned char *) key;
	while (len >= 4) {
		unsigned int k = *(unsigned int *) data;
		k *= m;
		k ^= k >> r;
		k *= m;
		h *= m;
		h ^= k;
		data += 4;
		len -= 4;
	}
	// Handle the last few bytes of the input array
	switch (len) {
	case 3:
		h ^= data[2] << 16;
	case 2:
		h ^= data[1] << 8;
	case 1:
		h ^= data[0];
		h *= m;
	};
	// Do a few final mixes of the hash to ensure the last few
	// bytes are well-incorporated.
	h ^= h >> 13;
	h *= m;
	h ^= h >> 15;
	return h;
}

void RangePrefixCalculation(unsigned A, unsigned B, vector<Prefix>&Rr) {
	//remove-right-zero-string A

	unsigned n = 32;
	for (unsigned i = 0; i < 32; i++) {
		if (0 == (A & (1 << (31 - i)))) {
			n--;
		} else {
			break;
		}
	}

	unsigned T = A;
	for (unsigned i = 0; i < 32; i++) {
		if (0 == (T & 1)) {
			T = T >> 1;
		} else {
			break;
		}
	}

	unsigned ii = 32;
	for (unsigned i = 0; i < 32; i++) {
		if (0 == (T & (1 << (31 - i)))) {
			ii--;
		} else {
			break;
		}
	}

	for (; ii < n; ii++) {
		unsigned temp = (T << (n - ii)) | ((1 << (n - ii)) - 1);
		if (temp <= B) {
			//insert T(+)(****...) into P
			Prefix p;
			p.prefix = T;
			p.sufixWildLength = (n - ii);
			p.prefixLength = ii;
			Rr.push_back(p);

			T = temp + 1;
			RangePrefixCalculation(T, B, Rr);
		} else {
			ii++;
			for (unsigned i = 0; i < 32; i++) {
				if (T & (1 << (31 - i))) {
					T = T & ((1 << (31 - i - ii)) - 1);
					break;
				}
			}
		}
	}
}

uint64_t converingPrefix2UniqueBinary(Prefix p) {
	uint64_t temp = p.prefix;
	temp = temp << (p.sufixWildLength);
	unsigned l;
	if (0 == p.prefixLength)
		l = (unsigned) (floor(log(1.0 * p.prefixLength))) + 1;
	else
		l = 0;

	temp = (temp << (l)) | p.prefixLength;
	return temp;
}

void ElementPrefixCalculation(unsigned q, vector<Prefix> &Pq) {
	unsigned n = 32;
	for (unsigned i = 0; i < 32; i++) {
		if (0 == (q & (1 << (31 - i)))) {
			n--;
		} else {
			break;
		}
	}

	for (unsigned i = 0; i <= n; i++) {
		Prefix p;
		p.prefix = q;
		p.prefixLength = n - i;
		p.sufixWildLength = i;
		Pq.push_back(p);
		q = q >> 1;
	}
}

void BFrange::insertChain2BFrange(vector<ChainElement> S) {
	vector<ChainElement>::iterator it = S.begin();
	for (; it != S.end(); ++it) {
		Arg arg;
		for (unsigned i = 1; i <= HashFuncNum; i++) {
			unsigned key = (it->key >> 32) ^ (it->key & ((1 << 32) - 1)) ^ i;
			unsigned value = murMurHash(&key, 32) % this->BloomFilterBitLength;
			arg.argelement.push_back(
					make_pair(this->BloomFilter.at(value).count, value));
		}
		unsigned l = arg.argmin();
		vector<ChainElement> chain = this->BloomFilter.at(l).chain;
		chain.push_back(*it);
		this->BloomFilter.at(l).NumberChainElement++;
	}
}

bool BFrange::deleteRangeFromBFrange(unsigned src_l, unsigned src_r,
		bool isOFF) {
	vector<Prefix> Rr;
	RangePrefixCalculation(src_l, src_r, Rr);
	for (vector<Prefix>::iterator it = Rr.begin(); it != Rr.end(); ++it) {
		uint64_t str = converingPrefix2UniqueBinary(*it);
		vector<ChainElement> S;

		for (unsigned i = 1; i <= HashFuncNum; i++) {
			unsigned key = (str >> 32) ^ (str & ((1 << 32) - 1)) ^ i;
			unsigned value = murMurHash(&key, 32) % this->BloomFilterBitLength;
			this->BloomFilter.at(value).count--;
			if (isOFF) {
				//no balance.
				vector<ChainElement> chn = this->BloomFilter.at(value).chain;
				this->BloomFilter.at(value).NumberChainElement--;
				vector<ChainElement>::iterator it = chn.begin();	//声明迭代器
				for (; it != this->BloomFilter.at(value).chain.end(); ++it) {
					if (str == it->key)
						chn.erase(it);
				}
			} else {
				vector<ChainElement> chn = this->BloomFilter.at(value).chain;
				vector<ChainElement>::iterator it = chn.begin();	//声明迭代器
				for (; it != this->BloomFilter.at(value).chain.end(); ++it) {
					if (str != it->key)
						S.push_back(*it);
				}
				chn.clear();
				this->BloomFilter.at(value).NumberChainElement = 0;
			}
		}
		if (!isOFF) {
			this->insertChain2BFrange(S);
		}

	}
	return false;
}

void BFrange::insertRangeRule2BFrang(unsigned id, uint32_t src_l,
		uint32_t src_r, bool isOFF) {

	vector<Prefix> Rr;
	RangePrefixCalculation(src_l, src_r, Rr);

	for (vector<Prefix>::iterator it = Rr.begin(); it != Rr.end(); ++it) {
		uint64_t str = converingPrefix2UniqueBinary(*it);

		ChainElement elements;
		elements.ruleid = id;
		elements.key = str;

		vector<ChainElement> S;
		S.push_back(elements);

		for (unsigned i = 1; i <= HashFuncNum; i++) {
			unsigned key = (elements.key >> 32)
					^ (elements.key & ((1 << 32) - 1)) ^ i;
			unsigned value = murMurHash(&key, 32) % this->BloomFilterBitLength;
			this->BloomFilter.at(value).count++;
			//balance??

			if (isOFF) {
				//no balance.
				this->BloomFilter.at(value).chain.push_back(elements);
				this->BloomFilter.at(value).NumberChainElement++;
			} else {
				vector<ChainElement>::iterator it = this->BloomFilter.at(value).chain.begin();//声明迭代器
				for (;
						it
								!= this->BloomFilter.at(value).chain.end();
						++it) {	//遍历v2,赋值给v1
					S.push_back(*it);
				}
				this->BloomFilter.at(value).chain.clear();
				this->BloomFilter.at(value).NumberChainElement = 0;

			}

		}

		if (!isOFF) {
			this->insertChain2BFrange(S);
		}
	}
}

unsigned BFrange::queryAllRanges4Element(unsigned search, bool isOFF) {
	vector<Prefix> Pq;
	ElementPrefixCalculation(search, Pq);
	for (vector<Prefix>::iterator it = Pq.begin(); it != Pq.end(); ++it) {
		uint64_t str = converingPrefix2UniqueBinary(*it);
		unsigned i;
		for (i = 1; i <= HashFuncNum; i++) {
			unsigned key = (str >> 32) ^ (str & ((1 << 32) - 1)) ^ i;
			unsigned value = murMurHash(&key, 32) % this->BloomFilterBitLength;
			if (0 == (this->BloomFilter.at(value).count)) {
				break;
			}
		}

		if (i == (HashFuncNum + 1)) {
			if (isOFF) {
				for (i = 1; i <= HashFuncNum; i++) {
					unsigned key = (str >> 32) ^ (str & ((1 << 32) - 1)) ^ i;
					unsigned value = murMurHash(&key, 32)
							% this->BloomFilterBitLength;
					Arg arg;
					arg.argelement.push_back(
							make_pair(this->BloomFilter.at(value).count,
									value));
					unsigned l = arg.argmin();

					vector<ChainElement> chain = this->BloomFilter.at(l).chain;
					for (vector<ChainElement>::iterator it = chain.begin();
							it != chain.end(); ++it) {
						if (str == (*it).key) {
							return (*it).ruleid;
						}
					}
				}
			} else {
				for (i = 1; i <= 1; i++) {
					unsigned key = (str >> 32) ^ (str & ((1 << 32) - 1)) ^ i;
					unsigned value = murMurHash(&key, 32)
							% this->BloomFilterBitLength;
					vector<ChainElement> chain =
							this->BloomFilter.at(value).chain;
					for (vector<ChainElement>::iterator it = chain.begin();
							it != chain.end(); ++it) {
						if (str == (*it).key) {
							return (*it).ruleid;
						}
					}
				}
			}
		}
	}
	return -1;

}

