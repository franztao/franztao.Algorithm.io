#!/bin/bash
#Program:
#	all algortihm run test202-test218 for 20 times in four core's environment,and record the result.
#History:
#2015/09/13	franz	first release
 
#PATH=/bin:/sbin:/usr/bin:/usr/sdbin:/usr/local/bin:/usr/local/sbin:~/bin
export LD_LIBRARY_PATH=/home/franz/Downloads/gurobi563/linux64/lib:$LD_LIBRARY_PATH
  
#1.franz algorithm 2.cose 3.ksp 4.ILP 5.IQP 6.ILP_sum 7.IQP_sum
startalgorithm=1
algorithmlen=7

example_i=202
example_num=218


date=1 #$(date +%Y%m%d%H%M%S)

#four cores
core=3

#numbers times run
number=20
num_i=1
for ((corei=0;corei<=${core};corei=corei+1))
do
	for ((l=1;l<=${number};l=l+1))
	do
			for ((i=${example_i};i<=${example_num};i=i+1))
			do
			test=test${i}
			echo "test/${test}/topo.csv"
			echo "test/${test}/demand.csv"
			echo "test/${test}/srlg.csv"
			mkdir  -p test/core${corei}
			mkdir  -p test/core${corei}/${test}
			for((j=${startalgorithm};j<=${algorithmlen};j=j+1))
			do
				echo "algorithm ${j}"
taskset -c 0-${corei} ./Debug/SRLG_Franz test/${test}/topo.csv test/${test}/demand.csv test/${test}/srlg.csv test/${test}/${result}.csv ${j} >>test/core${corei}/${test}/mid.txt	
				answer=$(echo $?)
				echo ${answer}
				if [ "0" == "${answer}" ]; then
				cat test/core${corei}/${test}/mid.txt| grep 'APcost' | cut -d ':' -f2   >>test/core${corei}/${test}/result.txt	
				cat test/core${corei}/${test}/mid.txt | grep 'CostSum' | cut -d ':' -f2   >> test/core${corei}/${test}/result.txt	
				cat test/core${corei}/${test}/mid.txt| grep 'APhop' | cut -d ':' -f2   >>test/core${corei}/${test}/result.txt	
			 	cat test/core${corei}/${test}/mid.txt | grep 'HopSum' | cut -d ':' -f2   >> test/core${corei}/${test}/result.txt	
			 	cat test/core${corei}/${test}/mid.txt | grep 'used' | cut -d ' ' -f5 >> test/core${corei}/${test}/result.txt
		 
				if [ "1" == "${j}" ]; then
				cat test/core${corei}/${test}/mid.txt | grep 'allnode' | cut -d ':' -f2 >> test/core${corei}/nodeedge.txt
				cat test/core${corei}/${test}/mid.txt | grep 'alledge' | cut -d ':' -f2 >> test/core${corei}/nodeedge.txt
				fi
			else
				echo "-1" >> test/core${corei}/${test}/result.txt
				echo "-1" >> test/core${corei}/${test}/result.txt
				echo "-1" >> test/core${corei}/${test}/result.txt
				echo "-1" >> test/core${corei}/${test}/result.txt
				echo "-1" >> test/core${corei}/${test}/result.txt
			fi
			rm test/core${corei}/${test}/mid.txt
			done
		done
	done
done







