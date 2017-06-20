# -*- coding: utf-8 -*-
import os  
import os.path
import sys
import glob
import time;

def fun1(fil,f):
	f1=open(fil,'r')
	lines=f1.readlines()
	f1.close()

	num=lines[0].split(' ')
	num=num+[lines[2]]
	[nodeNum,edgeNum,costNum,severCost]=[int(i) for i in num]
	myMap=[([0] * 1000) for i in range(1000)]
	for j in range(4,4+edgeNum):
		num=lines[j].split(' ')
		[fr,to,cap,cost]=[int(i) for i in num]
		myMap[fr][to]=[cap,cost]
		myMap[to][fr]=[cap,cost]
	costID={}
	for j in range(4+edgeNum+1,4+edgeNum+1+costNum):
		num=lines[j].split(' ')
		[costid,node,cap]=[int(i) for i in num]
		costID[costid]=[node,cap]

	totalCost=0
	
	f1=open(fil.split('.')[0],'r')
	lines=f1.readlines()
	f1.close()

	severSet=set([])
	n=int(lines[0])
	for j in range(2,2+n):
		l=lines[j].split(' ')
		l1=[int(i) for i in l]
		severSet.add(l1[0])
		cap=l1[-1]
		for i in range(0,len(l1)-3):
			#print(l1[i])
			#print(" ")
			#print(l1[i+1])
			myMap[l1[i]][l1[i+1]][0]-=cap
			totalCost+=cap*myMap[l1[i]][l1[i+1]][1]
			if(myMap[l1[i]][l1[i+1]][0]<0):
				f.write("超出容量:"+str(l1[i])+'->'+str(l1[i+1]))
				return
		if(l1[-3]!=costID[l1[-2]][0]):
			f.write("连接错误:"+str(l1[-3])+'->'+str(l1[-2]))
			return
		costID[l1[-2]][1]-=cap
		if(costID[l1[-2]][1]<0):
			f.write("消费节点超出容量:"+str(l1[-2]))
			return
	for key in costID:
		if(costID[key][1]!=0):
			f.write("未发送完成:"+str(key))
			return
	f.write("pathcost:"+str(totalCost))
	print("pathcost:"+str(totalCost))	
	totalCost+=severCost*len(severSet)
	f.write("cost:"+str(totalCost))
	print("cost:"+str(totalCost)+'\n')
	f.write("servernum:"+str(len(severSet)))
	print("servernum:"+str(len(severSet))+'\n')		

def fun(fil,f):
	fil1=fil.split('.')[0]
	cdnPath="./cdn"
	s=cdnPath+" "+fil+" "+fil1
	f.write(''+fil1+':\t')
	print(''+fil1+':\t')
	time_start=time.time();
	os.system(s)
	time_end=time.time();
	t=int((time_end-time_start)*1000)
	f.write('time:'+str(t)+'ms\t')
	print('time:'+str(t)+'ms\t')
	fun1(fil,f)
	f.write('\n')
	

def main():
	fil=glob.glob('*.txt')
	fil.sort()
	f=open('ans','a')
	for f1 in fil:
		fun(f1,f)
	f.write('\n')
	f.close()

 
if __name__ == "__main__":
    main()
