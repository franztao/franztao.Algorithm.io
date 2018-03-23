#!/bin/bash
#Program:
#	enum many random request in my algorithm to know the ratio of the request must divide and conquer .
#History:
#2015/09/13	franz	first release
 
#PATH=/bin:/sbin:/usr/bin:/usr/sdbin:/usr/local/bin:/usr/local/sbin:~/bin
export LD_LIBRARY_PATH=/home/franz/Downloads/gurobi563/linux64/lib:$LD_LIBRARY_PATH
  
#1.franz algorithm:-3


example_i=1
example_num=17 #315

srlgdensity_i=0


srlgdensity_num=19 #19

srlgtype_i=1
srlgtype_num=3 #3

demandnumber=1 #99 #9999

date=$(date +%Y%m%d%H%M%S)

for ((i=${example_i};i<=${example_num};i=i+1))
do
	test=test${i}
	for ((l=${srlgtype_i};l<=${srlgtype_num};l=l+1))
	do
		#rm -f test/${test}/result/srlg${l}/*
		for((k=${srlgdensity_i};k<=${srlgdensity_num};k=k+1))
		do
			rm -rf  test/${test}/result/*
			mkdir  -p -m 777 test/${test}/result/${test}_${date}/srlg${l}
			#touch   test/${test}/result/${test}_${date}/srlg${l}/all.txt 		
			for((j=1;j<=${demandnumber};j++))
			do
				echo "test/${test}/topo.csv"
				echo "test/${test}/srlg${l}/${k}.csv"
				echo "test/randomdemand/demand${j}.csv"
#./Debug/SRLG_Franz test/${test}/topo.csv test/${test}/demand.csv test/${test}/srlg${l}/${k}.csv test/${test}/${result}.csv -3 >>test/${test}/result/${test}_${date}/srlg${l}/all.txt 	
./Debug/SRLG_Franz test/${test}/topo.csv test/${test}/demand.csv test/${test}/srlg${l}/${k}.csv test/${test}/${result}.csv -3 >>test/${test}/result/${test}_${date}/srlg${l}/all.txt 	
																		
#./Debug/SRLG_Franz test/${test}/topo.csv test/randomdemand/demand${j}.csv test/${test}/srlg${l}/${k}.csv test/${test}/${result}.csv -3 >>test/${test}/result/${test}_${date}/srlg${l}/all.txt 	
				echo -e "${test}  srlgtype${l}  srlgdensity${k}  "
			done
			mkdir -p -m 777  test/result/${date}
			echo -e "${test}  srlgtype${l}  srlgdensity${k}" >> test/result/${date}/${test}_${date}.txt
			cat test/${test}/result/${test}_${date}/srlg${l}/all.txt | grep 'parallel' | wc -l   >> test/result/${date}/${test}_${date}.txt
			rm -f test/${test}/result/${test}_${date}/srlg${l}/all.txt 		
		done
	done
done
	

#/Debug/SRLG_Franz test6/topo.csv test6/randomdemand/demand0.csv test6/topo.csv test6/result.csv >> recode.txt

#./Debug/SRLG_Franz test1/topo.csv test1/demand.csv test1/topo.csv test1/result.csv >> test1/recode.txt

#verify the result is right.
