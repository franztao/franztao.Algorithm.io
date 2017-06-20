/*
 * head.h
 *
 *  Created on: Jan 11, 2017
 *      Author: franz
 */

#ifndef HEAD_HEAD_H_
#define HEAD_HEAD_H_

#include<stdint.h>
#include<vector>
#include<iostream>
#include <iostream>
#include <fstream>
#include <cstdlib>
#include<cstring>
#include<limits.h>
#include<math.h>
#include<algorithm>

using namespace std;



#define OFP_ETH_ALEN 4
#define LimitRules 10
#define scale 10
#define LimitPackets (LimitRules*scale)
//#define RuleBitLength 32
const unsigned HashFuncNum=3;

const unsigned PacketFileLinestrLen=1000;
const unsigned RuleFileLinestrLen=1000;


const int FieldBitSize=32;

#endif /* HEAD_HEAD_H_ */
