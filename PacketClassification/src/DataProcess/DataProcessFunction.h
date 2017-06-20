/*
 * DataProcessFunction.h
 *
 *  Created on: Mar 21, 2017
 *      Author: franz
 */

#ifndef DATAPROCESS_DATAPROCESSFUNCTION_H_
#define DATAPROCESS_DATAPROCESSFUNCTION_H_

#include"../Head/head.h"
#include"../Head/packet.h"
#include"../Head/rulesets.h"
void ReadData(const char * rulesets,const char * packet,RuleSet *ruleset,PacketSet *packetset);
void Experiment(RuleSet *ruleset,PacketSet *packetset);
#endif /* DATAPROCESS_DATAPROCESSFUNCTION_H_ */
