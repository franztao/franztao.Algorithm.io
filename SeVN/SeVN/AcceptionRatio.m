
%AcceptionRatio_EVN_sn_FD_EVSNR_Ran_NoShared
main;
dataVectorEVN=[];
dataVectorVN=[];
for i=1:1:AlgNum
    dataEVN=load([FileAbsolutePath,'Data\\AcceptionRatio_SVN_',strtrim(AlgName(i,:)),'.txt']);
    dataVN=load([FileAbsolutePath,'Data\\AcceptionRatio_VN_',strtrim(AlgName(i,:)),'.txt']);
    dataVectorEVN=[dataVectorEVN;dataEVN'];
    dataVectorVN=[dataVectorVN;dataVN'];
    %dataVector=load([FileAbsolutePath,'Data\\AcceptionRatio_EVN_sn_FD_EVSNR_Ran_NoShared.txt']);
end
[r,c]=size(dataVectorEVN);

experimentEVN=zeros(AlgNum,ExperimentPicturePlotNumber);
experimentVN=zeros(AlgNum,ExperimentPicturePlotNumber);
for i=1:1:AlgNum
    for j=1:1:c
        experimentEVN(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentEVN(i,mod(j-1,ExperimentPicturePlotNumber)+1)+dataVectorEVN(i,j);
        experimentVN(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentVN(i,mod(j-1,ExperimentPicturePlotNumber)+1)+dataVectorVN(i,j);
    end
end
for i=1:1:AlgNum
    for j=1:1:ExperimentPicturePlotNumber
        experimentEVN(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentEVN(i,mod(j-1,ExperimentPicturePlotNumber)+1)*1.0/ExperimentTimes;
        experimentVN(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentVN(i,mod(j-1,ExperimentPicturePlotNumber)+1)*1.0/ExperimentTimes;
    end
end


plotXaxisValue=0:(SubstrateNewtorkRunTimeInterval/(ExperimentPicturePlotNumber-1)):SubstrateNewtorkRunTimeInterval;

subplot(1,3,1);
title('Accepted Ratio');
% plot(plotXaxisValue,experimentVN);
hold on;
grid on;
for i=1:1:AlgNum
    plot(plotXaxisValue,experimentVN(i,:),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end

subplot(1,3,2);
% plot(plotXaxisValue,experimentEVN);
hold on;
grid on;
for i=1:1:AlgNum
    plot(plotXaxisValue,experimentEVN(i,:),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end

subplot(1,3,3);
experimentEVNbVN=experimentEVN./experimentVN;
% plot(plotXaxisValue,experimentEVNbVN);
hold on;
grid on;
for i=1:1:AlgNum
    plot(plotXaxisValue,experimentEVNbVN(i,:),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end
%h=legend('FI\_Shared','FI\_NoShared','FD\_EVSNR\_Ran\_Shared','FD\_EVSNR\_Ran\_NoShared','Orientation','horizontal');
%h=legend('FD\_Min\_Shared\_Heuristic','FD\_Min\_NoShared\_Heuristic','FD\_Ran\_Shared\_Heuristic','FD\_Ran\_NoShared\_Heuristic','FI\_Min\_Shared\_Heuristic','FI\_Min\_NoShared\_Heuristic','FI\_Ran\_Shared\_Heuristic','FI\_Ran\_NoShared\_Heuristic','Orientation','horizontal','FontSize',LegendSize);
h=legend(LegendString,'Orientation','horizontal','FontSize',LegendSize);
set(h,'Fontsize',pictureLegendFont);
hold off;
if ~exist([FileAbsolutePath,'Data\\Fig'])
    mkdir([FileAbsolutePath,'Data\\Fig'])
end
saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\AcceptedRatio','.eps']);
saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\AcceptedRatio','.jpg']);
