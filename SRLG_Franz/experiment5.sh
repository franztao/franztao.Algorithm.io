#!/bin/bash
#Program:
#	 compare the performance of my #algprithm and ILP.
#History:
#2015/07/16	franz	first release

#PATH=/bin:/sbin:/usr/bin:/usr/sdbin:/usr/local/bin:/usr/local/sbin:~/bin
export LD_LIBRARY_PATH=/home/franz/Downloads/gurobi563/linux64/lib:$LD_LIBRARY_PATH
  
#1.franz algorithm 2.ILP 3.KSP 0.all algorithm

startalgorithm=1
algorithmlen=6

startexamplelen=300
examplelen=315

startsrlglen=0
srlglen=19

startsrlgtype=1
srlgtype=3

date=$(date +%Y%m%d%H%M%S)

		
/home/franz/franzDocuments/eclipse4cworkspace/SRLG_Franz/Debug/SRLG_Franz /home/franz/franzDocuments/eclipse4cworkspace/SRLG_Franz/test/test202/topo.csv /home/franz/franzDocuments/eclipse4cworkspace/SRLG_Franz/test/test202/demand.csv /home/franz/franzDocuments/eclipse4cworkspace/SRLG_Franz/test/test202/srlg.csv /home/franz/franzDocuments/eclipse4cworkspace/SRLG_Franz/test/test202/${result}.csv 1	

	

#/Debug/SRLG_Franz test6/topo.csv test6/randomdemand/demand0.csv test6/topo.csv test6/result.csv >> recode.txt

#./Debug/SRLG_Franz test1/topo.csv test1/demand.csv test1/topo.csv test1/result.csv >> test1/recode.txt

#verify the result is right.


