#!/bin/bash
#Program:
#	all algortihm run test202-test218 for 20 times in four coreNum's environment,and record the result.
#History:
#2015/09/13	franz	first release
 
#PATH=/bin:/sbin:/usr/bin:/usr/sdbin:/usr/local/bin:/usr/local/sbin:~/bin
export LD_LIBRARY_PATH=/home/franz/Downloads/gurobi563/linux64/lib:$LD_LIBRARY_PATH
  
#1.franz algorithm 2.cose 3.ksp 4.ILP 5.IQP 6.ILP_sum 7.IQP_sum 8.TA
startAlgorithm=1
algorithmNum=8
showAlgorithmNum=5
#four cores
startCoreNum=1
coreNum=4
#numbers times run
runTimes=20

testSampleStartIndex=1   #202
testSampleEndIndex=17   #218


date=1 #$(date +%Y%m%d%H%M%S)


for ((currentCoreNum=${startCoreNum};currentCoreNum<=${coreNum};currentCoreNum=currentCoreNum+1))
do
	for ((lthRun=1;lthRun<=${runTimes};lthRun=lthRun+1))
	do
			for ((ithTestSample=${testSampleStartIndex};ithTestSample<=${testSampleEndIndex};ithTestSample=ithTestSample+1))
			do
			test=test${ithTestSample}
			echo "coreNumber: "$currentCoreNum
			echo "test/${test}/topo.csv"
			echo "test/${test}/demand.csv"
			echo "test/${test}/srlg.csv"
			mkdir  -p test/coreNum${currentCoreNum}
			mkdir  -p test/coreNum${currentCoreNum}/${test}
			for((j=${startAlgorithm};j<=${algorithmNum};))
			do
				echo "algorithm ${j}"
taskset -c 0-$((${currentCoreNum} - 1)) ./Debug/SRLG_Franz Sample/${test}/topo.csv Sample/${test}/demand.csv Sample/${test}/srlg.csv Sample/${test}/${result}.csv ${j} >>Sample/coreNum${currentCoreNum}/${test}/mid.txt	
				answer=$(echo $?)
				echo ${answer}
				if [ "0" == "${answer}" ]; then
					cat Sample/coreNum${currentCoreNum}/${test}/mid.txt| grep 'APcost' | cut -d ':' -f2   >>Sample/coreNum${currentCoreNum}/${test}/result.txt	
					cat Sample/coreNum${currentCoreNum}/${test}/mid.txt | grep 'CostSum' | cut -d ':' -f2   >> Sample/coreNum${currentCoreNum}/${test}/result.txt	
					cat Sample/coreNum${currentCoreNum}/${test}/mid.txt| grep 'APhop' | cut -d ':' -f2   >>Sample/coreNum${currentCoreNum}/${test}/result.txt	
			 	 	cat Sample/coreNum${currentCoreNum}/${test}/mid.txt | grep 'HopSum' | cut -d ':' -f2   >> Sample/coreNum${currentCoreNum}/${test}/result.txt	
			 		cat Sample/coreNum${currentCoreNum}/${test}/mid.txt | grep 'used' | cut -d ' ' -f5 >> Sample/coreNum${currentCoreNum}/${test}/result.txt
					if [ "1" == "${j}" ]; then
						cat Sample/coreNum${currentCoreNum}/${test}/mid.txt | grep 'allnode' | cut -d ':' -f2 >> Sample/coreNum${currentCoreNum}/nodeedge.txt
						cat Sample/coreNum${currentCoreNum}/${test}/mid.txt | grep 'alledge' | cut -d ':' -f2 >> Sample/coreNum${currentCoreNum}/nodeedge.txt
					fi
					((j=${j}+1))
				fi
				rm test/coreNum${currentCoreNum}/${test}/mid.txt
			done
		done
	done
done






