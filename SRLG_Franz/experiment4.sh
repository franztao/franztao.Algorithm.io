#!/bin/bash
#Program:
#	all algortihm run test202-test218 for 20 times in four coreNum's environment,and record the result.
#History:
#2015/09/13	franz	first release
 
#PATH=/bin:/sbin:/usr/bin:/usr/sdbin:/usr/local/bin:/usr/local/sbin:~/bin
export LD_LIBRARY_PATH=/home/franz/Downloads/gurobi563/linux64/lib:$LD_LIBRARY_PATH
  
#1.franz algorithm 2.cose 3.ksp 4.ILP 5.IQP 6.ILP_sum 7.IQP_sum 8.TA
startAlgorithm=1
algorithmNum=5

testSampleStartIndex=401   #202
testSampleEndIndex=404   #218


date=1 #$(date +%Y%m%d%H%M%S)

#four cores
startCoreNum=1
coreNum=4

#numbers times run
runTimes=20
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
taskset -c 0-$((${currentCoreNum} - 1)) ./Debug/SRLG_Franz test/${test}/topo.csv test/${test}/demand.csv test/${test}/srlg.csv test/${test}/${result}.csv ${j} >>test/coreNum${currentCoreNum}/${test}/mid.txt	
				answer=$(echo $?)
				echo ${answer}
				if [ "0" == "${answer}" ]; then
					cat test/coreNum${currentCoreNum}/${test}/mid.txt| grep 'APcost' | cut -d ':' -f2   >>test/coreNum${currentCoreNum}/${test}/result.txt	
					cat test/coreNum${currentCoreNum}/${test}/mid.txt | grep 'CostSum' | cut -d ':' -f2   >> test/coreNum${currentCoreNum}/${test}/result.txt	
					cat test/coreNum${currentCoreNum}/${test}/mid.txt| grep 'APhop' | cut -d ':' -f2   >>test/coreNum${currentCoreNum}/${test}/result.txt	
			 	 	cat test/coreNum${currentCoreNum}/${test}/mid.txt | grep 'HopSum' | cut -d ':' -f2   >> test/coreNum${currentCoreNum}/${test}/result.txt	
			 		cat test/coreNum${currentCoreNum}/${test}/mid.txt | grep 'used' | cut -d ' ' -f5 >> test/coreNum${currentCoreNum}/${test}/result.txt
					if [ "1" == "${j}" ]; then
						cat test/coreNum${currentCoreNum}/${test}/mid.txt | grep 'allnode' | cut -d ':' -f2 >> test/coreNum${currentCoreNum}/nodeedge.txt
						cat test/coreNum${currentCoreNum}/${test}/mid.txt | grep 'alledge' | cut -d ':' -f2 >> test/coreNum${currentCoreNum}/nodeedge.txt
					fi
					((j=${j}+1))
				fi
				rm test/coreNum${currentCoreNum}/${test}/mid.txt
			done
		done
	done
done

#else
					#					echo "-1" >> test/coreNum${currentCoreNum}/${test}/result.txt
					#					echo "-1" >> test/coreNum${currentCoreNum}/${test}/result.txt
					#					echo "-1" >> test/coreNum${currentCoreNum}/${test}/result.txt
					#				echo "-1" >> test/coreNum${currentCoreNum}/${test}/result.txt
					#				echo "-1" >> test/coreNum${currentCoreNum}/${test}/result.txt





