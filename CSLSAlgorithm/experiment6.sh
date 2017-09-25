#!/bin/bash
#Program:
#	all algortihm run test202-test218 for 20 times in four coreNum's environment,and record the result.
#History:
#2015/09/13	franz	first release
 
#PATH=/bin:/sbin:/usr/bin:/usr/sdbin:/usr/local/bin:/usr/local/sbin:~/bin
export LD_LIBRARY_PATH=/home/franz/Downloads/gurobi563/linux64/lib:$LD_LIBRARY_PATH
  
#1.franz algorithm 2.cose 3.ksp 4.ILP 5.IQP 6.ILP_sum 7.IQP_sum 8.TA
startAlgorithm=1
algorithmNum=8  #8
showAlgorithmNum=8
#four cores
startCoreNum=1
coreNum=4
#numbers times run
runTimes=5  #20
timelimit=30000
mostrunTimes=10
testSampleStartIndex=1   #1
testSampleEndIndex=20   #16
Sample="Sample"
projectName="CSLSAlgorithm"
date=20170916 #$(date +%Y%m%d%H%M%S)


for ((ithTestSample=${testSampleStartIndex};ithTestSample<=${testSampleEndIndex};ithTestSample=ithTestSample+1))
do
	test=test${ithTestSample}
	echo "test/${test}/topo.csv"
	./Debug/${projectName} ${Sample}/${test}/topo.csv ${Sample}/${test}/demand.csv ${Sample}/${test}/srlg.csv ${Sample}/${test}/${result}.csv -4 
	echo "test/${test}/topo.csv"
done








