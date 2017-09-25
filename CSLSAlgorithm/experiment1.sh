#!/bin/bash
#Program:
#	
#History:
#2016/07/16	franz	first release

#PATH=/bin:/sbin:/usr/bin:/usr/sdbin:/usr/local/bin:/usr/local/sbin:~/bin
export LD_LIBRARY_PATH=/home/franz/Downloads/gurobi563/linux64/lib:$LD_LIBRARY_PATH
  
#1.franz algorithm 2.ILP 3.KSP 0.all algorithm

startalgorithm=1
algorithmlen=6

startexamplelen=202
examplelen=218

startsrlglen=0
srlglen=19

startsrlgtype=1
srlgtype=3

date=$(date +%Y%m%d%H%M%S)

for ((i=${startexamplelen};i<=${examplelen};i=i+1))
do
	test=test${i}
	for ((l=${startsrlgtype};l<=${srlgtype};l=l+1))
	do
		rm -f test/${test}/result/srlg${l}/*
		for((k=${startsrlglen};k<=${srlglen};k=k+1))
		do
			
			for((j=${startalgorithm};j<=${algorithmlen};j=j+1))
			do
				result=result${j}
				echo "test/${test}/topo.csv"
				echo "test/${test}/demand.csv"
				echo "test/${test}/srlg${l}/${k}.csv"
./Debug/SRLG_Franz test/${test}/topo.csv test/${test}/demand.csv test/${test}/srlg${l}/${k}.csv test/${test}/${result}.csv ${j} >>test/${test}/result/srlg${l}/${test}_${date}.txt	
				answer=$(echo $?)
				echo ${answer}
				if [ "0" == "${answer}" ]; then
				cat test/${test}/result/srlg${l}/${test}_${date}.txt | grep 'APcost' | cut -d ':' -f2   >> test/${test}/result/srlg${l}/${test}_APcost_${date}.txt	
				cat test/${test}/result/srlg${l}/${test}_${date}.txt | grep 'CostSum' | cut -d ':' -f2   >> test/${test}/result/srlg${l}/${test}_CostSum_${date}.txt	
				cat test/${test}/result/srlg${l}/${test}_${date}.txt | grep 'used' | cut -d ' ' -f5 >> test/${test}/result/srlg${l}/${test}_time_${date}.txt	
					if [ "1" == "${j}" ]; then
					cat test/${test}/result/srlg${l}/${test}_${date}.txt | grep 'isornot_parallar'  | cut -d ':' -f2 >> test/${test}/result/srlg${l}/${test}_isornot_paralle_${date}.txt	
					fi
				else
					echo "-1" >> test/${test}/result/srlg${l}/${test}_APcost_${date}.txt
					echo "-1" >> test/${test}/result/srlg${l}/${test}_CostSum_${date}.txt
					echo "-1" >> test/${test}/result/srlg${l}/${test}_time_${date}.txt
					if [ "1" == "${j}" ]; then
					echo "-1" >> test/${test}/result/srlg${l}/${test}_isornot_paralle_${date}.txt
					fi
				fi
				rm test/${test}/result/srlg${l}/${test}_${date}.txt
				echo -e "${test}  ${srlgfile}${k}  algorithmlen${j}\n"
			done
			echo  >> test/${test}/result/srlg${l}/${test}_APcost_${date}.txt
				echo  >> test/${test}/result/srlg${l}/${test}_CostSum_${date}.txt
				echo  >> test/${test}/result/srlg${l}/${test}_time_${date}.txt
				echo  >> test/${test}/result/srlg${l}/${test}_isornot_paralle_${date}.txt
			
		done
	done
done
	

#/Debug/SRLG_Franz test6/topo.csv test6/randomdemand/demand0.csv test6/topo.csv test6/result.csv >> recode.txt

#./Debug/SRLG_Franz test1/topo.csv test1/demand.csv test1/topo.csv test1/result.csv >> test1/recode.txt

#verify the result is right.


