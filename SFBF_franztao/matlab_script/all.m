p1_falsepositiverate;
p2_ExtensionRound;
p3_SpaceSizeFilters;
p4_QueryingCPUTimewithoutShifting;
p5_QueryingCPUtimewithshifting;

clc;
clear;
SecIXone=load('SecIXone.mat')
SecIXtwo=load('SecIXtwo.mat')


SFBFfault1=load('SFBFfault1.mat');
SFBFfault1=SFBFfault1.savevalue;
SFBFfault2=load('SFBFfault2.mat');
SFBFfault2=SFBFfault2.savevalue;
SFBFfault3=load('SFBFfault3.mat');
SFBFfault3=SFBFfault3.savevalue;
SFBFfault4=load('SFBFfault4.mat');
SFBFfault4=SFBFfault4.savevalue;
SFBFfault5=load('SFBFfault5.mat');
SFBFfault5=SFBFfault5.savevalue;
DBFfault=load('DBFfault');
DBFfault=DBFfault.savevalue;

SFBFShift1=load('SFBFShift1.mat');
SFBFShift1=SFBFShift1.savevalue;
SFBFShift2=load('SFBFShift2.mat');
SFBFShift2=SFBFShift2.savevalue;
SFBFShift3=load('SFBFShift3.mat');
SFBFShift3=SFBFShift3.savevalue;
SFBFShift4=load('SFBFShift4.mat');
SFBFShift4=SFBFShift4.savevalue;
SFBFShift5=load('SFBFShift5.mat');
SFBFShift5=SFBFShift5.savevalue;
DBFShift=load('DBFShift');
DBFShift=DBFShift.savevalue;

SFBFnoShift1=load('SFBFnoShift1.mat');
SFBFnoShift1=SFBFnoShift1.savevalue;
SFBFnoShift2=load('SFBFnoShift2.mat');
SFBFnoShift2=SFBFnoShift2.savevalue;
SFBFnoShift3=load('SFBFnoShift3.mat');
SFBFnoShift3=SFBFnoShift3.savevalue;
SFBFnoShift4=load('SFBFnoShift4.mat');
SFBFnoShift4=SFBFnoShift4.savevalue;
SFBFnoShift5=load('SFBFnoShift5.mat');
SFBFnoShift5=SFBFnoShift5.savevalue;


SFBFrate1=(SFBFShift1./SFBFnoShift1)*100
SFBFrate2=(SFBFShift2./SFBFnoShift2)*100
SFBFrate3=(SFBFShift3./SFBFnoShift3)*100
SFBFrate4=(SFBFShift4./SFBFnoShift4)*100
SFBFrate5=(SFBFShift5./SFBFnoShift5)*100

DBFShift
SFBFShift1
SFBFShift2
SFBFShift3
SFBFShift4
SFBFShift5

positive_SFBFtoDBF1=SFBFfault1/DBFfault*100
positive_SFBFtoDBF2=SFBFfault2/DBFfault*100
positive_SFBFtoDBF3=SFBFfault3/DBFfault*100
positive_SFBFtoDBF4=SFBFfault4/DBFfault*100
positive_SFBFtoDBF5=SFBFfault5/DBFfault*100

SFBFtoDBF1=SFBFShift1/DBFShift*100
SFBFtoDBF2=SFBFShift2/DBFShift*100
SFBFtoDBF3=SFBFShift3/DBFShift*100
SFBFtoDBF4=SFBFShift4/DBFShift*100
SFBFtoDBF5=SFBFShift5/DBFShift*100

