clc;
clear;
clf;
global FileAbsolutePath
FileAbsolutePath='C:\\Users\\Taoheng\\Desktop\\SeVN\\';



Parameter=load( [FileAbsolutePath,'Data\\Parameter.txt']);
global ExperimentTimes
ExperimentTimes=Parameter(1,1);
global ExperimentPicturePlotNumber
ExperimentPicturePlotNumber=Parameter(2,1);
SubstrateNewtorkRunTimeInterval=Parameter(3,1);
global AlgNum
AlgNum=Parameter(4,1);
RelativeCostbetweenComputingBandwidth=Parameter(5,1);
addNewNodeCost=Parameter(6,1);
% AlgNum=9;
% FileString={'FD_Min_Shared_Heuristic','FD_Min_NoShared_Heuristic','FI_Min_Shared_Heuristic','FI_Min_NoShared_Heuristic','FD_Ran_Shared_Heuristic','FD_Ran_NoShared_Heuristic','FI_Ran_Shared_Heuristic','FI_Ran_NoShared_Heuristic','VirNet'};
% LegendString={'FD Min Shared','FD Min NoShared','FI Min Shared','FI Min NoShared','FD Ran Shared','FD Ran NoShared','FI Ran Shared','FI Ran NoShared','VN'};

% ALgLineStyle=str2mat('r-p','b-d','m-o','g-+','r:p','b:d','m:o','g:+','k-<');
% ALgLineBlockColor=str2mat('r','b','m','g','r','b','m','g','k');

% AlgNum=5;
% FileString={'FD_Min_Shared_Heuristic','FD_Min_NoShared_Heuristic','FI_Min_Shared_Heuristic','FI_Min_NoShared_Heuristic','VirNet'};
% LegendString={'FD Min Shared','FD Min NoShared','FI Min Shared','FI Min NoShared','VN'};


MyAlgorithmMethodAbrreviation='STAR';
SecondAlgorithmMethodAbrreviation='RVN';
ThirdAlgorithmMethodAbrreviation='bVN';
ForthAlgorithmMethodAbrreviation='oVN';
AlgNum=7;
% FileString={'FD_Ran_Shared_Heuristic','FD_Ran_NoShared_Heuristic','FI_Ran_Shared_Heuristic','FI_Ran_NoShared_Heuristic','VirNet'};

FileString={'FD_Ran_Shared_Heuristic','FD_Ran_NoShared_Heuristic','FI_Ran_Shared_Heuristic','FI_Ran_NoShared_Heuristic','One2OneProtection_Ran_Shared','One2OneProtection_Ran_NoShared','VirNet'};

LegendString={[MyAlgorithmMethodAbrreviation,' Shared'],[MyAlgorithmMethodAbrreviation,' NoShared'],[SecondAlgorithmMethodAbrreviation,' Shared'],[SecondAlgorithmMethodAbrreviation,' NoShared'],[ForthAlgorithmMethodAbrreviation,' Shared'],[ForthAlgorithmMethodAbrreviation,' NoShared'],ThirdAlgorithmMethodAbrreviation};

ALgLineStyle=str2mat('r-p','b-d','m-o','g->','y-h','y-s','k-<');
ALgLineBlockColor=str2mat('r','b','m','g','y','y','k');


global AlgName
AlgName=str2mat(FileString);


%Picture Parameter
pictureLineWidth=2;
pictureLegendFont=7;
LegendSize=10;


plotXaxisValue=0:(SubstrateNewtorkRunTimeInterval/(ExperimentPicturePlotNumber-1)):SubstrateNewtorkRunTimeInterval;

iscurveFitting=false;
curveFittingPolypower=10;




