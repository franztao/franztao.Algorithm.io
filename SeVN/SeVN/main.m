clc;
clear;
clf;
FileAbsolutePath='C:\\Users\\Taoheng\\Desktop\\SeVN\\';

Parameter=load( [FileAbsolutePath,'Data\\Parameter.txt']);
ExperimentTimes=Parameter(1,1);
ExperimentPicturePlotNumber=Parameter(2,1);
SubstrateNewtorkRunTimeInterval=Parameter(3,1);
AlgNum=Parameter(4,1);
RelativeCostbetweenComputingBandwidth=Parameter(5,1);
addNewNodeCost=Parameter(6,1);
% AlgNum=8;
% FileString={'FD_Min_Shared_Heuristic','FD_Min_NoShared_Heuristic','FD_Ran_Shared_Heuristic','FD_Ran_NoShared_Heuristic','FI_Min_Shared_Heuristic','FI_Min_NoShared_Heuristic','FI_Ran_Shared_Heuristic','FI_Ran_NoShared_Heuristic','VirNet'};
% LegendString={'FD Min Shared','FD Min NoShared','FD Ran Shared','FD Ran NoShared','FI Min Shared','FI Min NoShared','FI Ran Shared','FI Ran NoShared','VN'};

% AlgNum=5;
% FileString={'FD_Min_Shared_Heuristic','FD_Min_NoShared_Heuristic','FI_Min_Shared_Heuristic','FI_Min_NoShared_Heuristic','VirNet'};
% LegendString={'FD Min Shared','FD Min NoShared','FI Min Shared','FI Min NoShared','VN'};

AlgNum=5;

FileString={'FD_Ran_Shared_Heuristic','FD_Ran_NoShared_Heuristic','FI_Ran_Shared_Heuristic','FI_Ran_NoShared_Heuristic','VirNet'};
LegendString={'FD Shared','FD NoShared','FI Shared','FI NoShared','VN'};


AlgName=str2mat(FileString);
%LengendString=str2mat(LegendString);
ALgLineStyle=str2mat('r-p','b:p','m-o','g:o','k-<','y:<','b->','g:>');
ALgLineBlockColor=str2mat('r','b','m','g','k','y','b','g');


%Picture Parameter
pictureLineWidth=2;
pictureLegendFont=10;
LegendSize=10;




