//============================================================================
// Name        : PacketClassification.cpp
// Author      : franz
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================
#include"../Head/packet.h"
#include"../Head/rulesets.h"

#include"../DataProcess/DataProcessFunction.h"
#include<stdio.h>
//#include<>
#include <iostream>

using namespace std;

int main() {
	const char * PacketFileString="/home/franz/franzDocuments/eclipse4cworkspace/PacketClassification/data/acl1_filters_100k_trace";
	const char * RuleSetFileString="/home/franz/franzDocuments/eclipse4cworkspace/PacketClassification/data/acl1_filters_100k";
	PacketSet *packetset;
	packetset=new PacketSet();
	RuleSet *ruleset;
	ruleset=new RuleSet();
	ReadData(RuleSetFileString,PacketFileString,ruleset,packetset);
	Experiment(ruleset,packetset);

	delete packetset;
	delete ruleset;
	cout << "!!!Hello World!!!" << endl; // prints !!!Hello World!!!
	return 0;
}

