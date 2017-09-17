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
runTimes=2  #20
timelimit=30000
mostrunTimes=5
testSampleStartIndex=1   #1
testSampleEndIndex=16   #16
Sample="Sample"
projectName="gurobi"
date=20170915 #$(date +%Y%m%d%H%M%S)


#storage parameter
mkdir -p ./${Sample}/${date}
touch ./${Sample}/${date}/Parameter.txt
rm -f  ./${Sample}/${date}/Parameter.txt
echo $algorithmNum >> ./${Sample}/${date}/Parameter.txt
echo $showAlgorithmNum >> ./${Sample}/${date}/Parameter.txt
echo $coreNum >> ./${Sample}/${date}/Parameter.txt
echo $runTimes >> ./${Sample}/${date}/Parameter.txt
echo $testSampleEndIndex >> ./${Sample}/${date}/Parameter.txt
echo $timelimit >> ./${Sample}/${date}/Parameter.txt


rm -f ./${Sample}/${date}/NodeEdge.txt
touch ./${Sample}/${date}/NodeEdge.txt

for ((currentCoreNum=${startCoreNum};currentCoreNum<=${coreNum};currentCoreNum=currentCoreNum+1))
do
	for ((ithTestSample=${testSampleStartIndex};ithTestSample<=${testSampleEndIndex};ithTestSample=ithTestSample+1))
	do
		test=test${ithTestSample}
		echo "coreNumber: "$currentCoreNum
		echo "test/${test}/topo.csv"
		echo "test/${test}/demand.csv"
		echo "test/${test}/srlg.csv"
		mkdir  -p ${Sample}/${date}/coreNum${currentCoreNum}
		mkdir  -p ${Sample}/${date}/coreNum${currentCoreNum}/${test}
		
		for((j=${startAlgorithm};j<=${algorithmNum};j=j+1))
		do
			echo "algorithm ${j}"
			mkdir  -p ${Sample}/${date}/coreNum${currentCoreNum}/${test}/algorithm${j}
			touch ${Sample}/${date}/coreNum${currentCoreNum}/${test}/algorithm${j}/mid.txt
			
			rm -f ${Sample}/${date}/coreNum${currentCoreNum}/${test}/algorithm${j}/result.txt
			
			runNum=0
			for ((lthRun=1;lthRun<=${runTimes};))
			do	
taskset -c 0-$((${currentCoreNum} - 1)) ./Debug/${projectName} ${Sample}/${test}/topo.csv ${Sample}/${test}/demand.csv ${Sample}/${test}/srlg.csv ${Sample}/${test}/${result}.csv ${j} >> ${Sample}/${date}/coreNum${currentCoreNum}/${test}/algorithm${j}/mid.txt			
#echo "tao" >> ${Sample}/${date}/coreNum${currentCoreNum}/${test}/algorithm${j}/mid.txt
				#may be fault segment problem
				answer=$(echo $?)
				echo ${answer}
				((runNum=${runNum}+1))
				if [ "0" == "${answer}" ]; then
					
	
					if [ "${currentCoreNum}" == "${startCoreNum}" ] && [ "${j}" == "${startAlgorithm}" ] && [ "${lthRun}" == "1" ]; then
						echo ${ithTestSample} >> ${Sample}/${date}/NodeEdge.txt
						cat ${Sample}/${date}/coreNum${currentCoreNum}/${test}/algorithm${j}/mid.txt | grep 'allnode' | cut -d ':' -f2 >> ${Sample}/${date}/NodeEdge.txt
						cat ${Sample}/${date}/coreNum${currentCoreNum}/${test}/algorithm${j}/mid.txt | grep 'alledge' | cut -d ':' -f2 >> ${Sample}/${date}/NodeEdge.txt
					fi
					
					cat ${Sample}/${date}/coreNum${currentCoreNum}/${test}/algorithm${j}/mid.txt | grep 'APcost' | cut -d ':' -f2   >>${Sample}/${date}/coreNum${currentCoreNum}/${test}/algorithm${j}/result.txt	
					cat ${Sample}/${date}/coreNum${currentCoreNum}/${test}/algorithm${j}/mid.txt | grep 'CostSum' | cut -d ':' -f2   >> ${Sample}/${date}/coreNum${currentCoreNum}/${test}/algorithm${j}/result.txt	
					cat ${Sample}/${date}/coreNum${currentCoreNum}/${test}/algorithm${j}/mid.txt | grep 'APhop' | cut -d ':' -f2   >>${Sample}/${date}/coreNum${currentCoreNum}/${test}/algorithm${j}/result.txt	
			 		cat ${Sample}/${date}/coreNum${currentCoreNum}/${test}/algorithm${j}/mid.txt | grep 'HopSum' | cut -d ':' -f2   >> ${Sample}/${date}/coreNum${currentCoreNum}/${test}/algorithm${j}/result.txt	
			 		cat ${Sample}/${date}/coreNum${currentCoreNum}/${test}/algorithm${j}/mid.txt | grep 'used' | cut -d ' ' -f5 >> ${Sample}/${date}/coreNum${currentCoreNum}/${test}/algorithm${j}/result.txt
					((lthRun=${lthRun}+1))
				fi
				rm ${Sample}/${date}/coreNum${currentCoreNum}/${test}/algorithm${j}/mid.txt
				if [  ${runNum} -ge ${mostrunTimes} ]; then
					break
				fi
			done
		done
	done
done








