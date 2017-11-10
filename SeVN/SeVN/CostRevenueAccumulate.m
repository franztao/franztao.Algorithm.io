%MappingCost_edge_sn_FD_EVSNR_Ran_NoShared
%MappingCost_node_sn_FD_EVSNR_Ran_NoShared
main;

dataCostVectorEdge=[];
dataCostVectorNode=[];
dataCostVectorNodeUsed=[];
dataCostVectorEdgeUsed=[];
for i=1:1:AlgNum
    dataCostNodeUsed=load([FileAbsolutePath,'Data\\Utilization_SubNode_Accumulate',strtrim(AlgName(i,:)),'.txt']);
    dataCostEdgeUsed=load([FileAbsolutePath,'Data\\Utilization_SubEdge_Accumulate',strtrim(AlgName(i,:)),'.txt']);
    
    dataCostNode=load([FileAbsolutePath,'Data\\Cost_NodeCp_Accumulate',strtrim(AlgName(i,:)),'.txt']);
    dataCostEdge=load([FileAbsolutePath,'Data\\Cost_EdgeBw_Accumulate',strtrim(AlgName(i,:)),'.txt']);
    dataCostVectorEdge=[dataCostVectorEdge;dataCostEdge'];
    dataCostVectorNode=[dataCostVectorNode;dataCostNode'];
    dataCostVectorNodeUsed=[dataCostVectorNodeUsed;dataCostNodeUsed'];
    
    dataCostVectorEdgeUsed=[dataCostVectorEdgeUsed;dataCostEdgeUsed'];
    %dataCostVector=load([FileAbsolutePath,'Data\\AcceptionRatio_Edge_sn_FD_EVSNR_Ran_NoShared.txt']);
end
[r,c]=size(dataCostVectorEdge);

experimentCostEdge=zeros(AlgNum,ExperimentPicturePlotNumber);
experimentCostNode=zeros(AlgNum,ExperimentPicturePlotNumber);
experimentCostNodeUsed=zeros(AlgNum,ExperimentPicturePlotNumber);
experimentCostEdgeUsed=zeros(AlgNum,ExperimentPicturePlotNumber);
for i=1:1:AlgNum
    for j=1:1:c
        experimentCostEdge(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentCostEdge(i,mod(j-1,ExperimentPicturePlotNumber)+1)+dataCostVectorEdge(i,j);
        experimentCostNode(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentCostNode(i,mod(j-1,ExperimentPicturePlotNumber)+1)+dataCostVectorNode(i,j);
        experimentCostNodeUsed(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentCostNodeUsed(i,mod(j-1,ExperimentPicturePlotNumber)+1)+dataCostVectorNodeUsed(i,j);
        
        experimentCostEdgeUsed(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentCostEdgeUsed(i,mod(j-1,ExperimentPicturePlotNumber)+1)+dataCostVectorNodeUsed(i,j);
    end
end
for i=1:1:AlgNum
    for j=1:1:ExperimentPicturePlotNumber
        experimentCostEdge(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentCostEdge(i,mod(j-1,ExperimentPicturePlotNumber)+1)*1.0/ExperimentTimes;
        experimentCostNode(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentCostNode(i,mod(j-1,ExperimentPicturePlotNumber)+1)*1.0/ExperimentTimes;
        experimentCostNodeUsed(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentCostNodeUsed(i,mod(j-1,ExperimentPicturePlotNumber)+1)*1.0/ExperimentTimes;
        experimentCostEdgeUsed(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentCostEdgeUsed(i,mod(j-1,ExperimentPicturePlotNumber)+1)*1.0/ExperimentTimes;
    end
end


dataRevenueVectorEdge=[];
dataRevenueVectorNode=[];
dataRevenueVectorNodeUsed=[];

dataRevenueVectorEdgeUsed=[];
for i=1:1:AlgNum
    dataRevenueNodeUsed=load([FileAbsolutePath,'Data\\Utilization_VirNode_Accumulate',strtrim(AlgName(i,:)),'.txt']);
    dataRevenueEdgeUsed=load([FileAbsolutePath,'Data\\Utilization_VirEdge_Accumulate',strtrim(AlgName(i,:)),'.txt']);
    
    dataRevenueNode=load([FileAbsolutePath,'Data\\Revenue_NodeCp_Accumulate',strtrim(AlgName(i,:)),'.txt']);
    dataRevenueEdge=load([FileAbsolutePath,'Data\\Revenue_EdgeBw_Accumulate',strtrim(AlgName(i,:)),'.txt']);
    dataRevenueVectorEdge=[dataRevenueVectorEdge;dataRevenueEdge'];
    dataRevenueVectorNode=[dataRevenueVectorNode;dataRevenueNode'];
    dataRevenueVectorNodeUsed=[dataRevenueVectorNodeUsed;dataRevenueNodeUsed'];
    
    dataRevenueVectorEdgeUsed=[dataRevenueVectorEdgeUsed;dataRevenueEdgeUsed'];
    %dataRevenueVector=load([FileAbsolutePath,'Data\\AcceptionRatio_Edge_sn_FD_EVSNR_Ran_NoShared.txt']);
end
[r,c]=size(dataRevenueVectorEdge);

experimentRevenueEdge=zeros(AlgNum,ExperimentPicturePlotNumber);
experimentRevenueNode=zeros(AlgNum,ExperimentPicturePlotNumber);
experimentRevenueNodeUsed=zeros(AlgNum,ExperimentPicturePlotNumber);
experimentRevenueEdgeUsed=zeros(AlgNum,ExperimentPicturePlotNumber);
for i=1:1:AlgNum
    for j=1:1:c
        experimentRevenueEdge(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentRevenueEdge(i,mod(j-1,ExperimentPicturePlotNumber)+1)+dataRevenueVectorEdge(i,j);
        experimentRevenueNode(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentRevenueNode(i,mod(j-1,ExperimentPicturePlotNumber)+1)+dataRevenueVectorNode(i,j);
        experimentRevenueNodeUsed(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentRevenueNodeUsed(i,mod(j-1,ExperimentPicturePlotNumber)+1)+dataRevenueVectorNodeUsed(i,j);
        
        experimentRevenueEdgeUsed(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentRevenueEdgeUsed(i,mod(j-1,ExperimentPicturePlotNumber)+1)+dataRevenueVectorNodeUsed(i,j);
    end
end
for i=1:1:AlgNum
    for j=1:1:ExperimentPicturePlotNumber
        experimentRevenueEdge(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentRevenueEdge(i,mod(j-1,ExperimentPicturePlotNumber)+1)*1.0/ExperimentTimes;
        experimentRevenueNode(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentRevenueNode(i,mod(j-1,ExperimentPicturePlotNumber)+1)*1.0/ExperimentTimes;
        experimentRevenueNodeUsed(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentRevenueNodeUsed(i,mod(j-1,ExperimentPicturePlotNumber)+1)*1.0/ExperimentTimes;
        experimentRevenueEdgeUsed(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentRevenueEdgeUsed(i,mod(j-1,ExperimentPicturePlotNumber)+1)*1.0/ExperimentTimes;
    end
end


experimentCostEdge=experimentCostEdge./experimentRevenueEdge;
experimentCostNode=experimentCostNode./experimentRevenueNode;
experimentCostNodeUsed=experimentCostNodeUsed./experimentRevenueNodeUsed;
experimentCostEdgeUsed=experimentCostEdgeUsed./experimentRevenueEdgeUsed;


plotXaxisValue=0:(SubstrateNewtorkRunTimeInterval/(ExperimentPicturePlotNumber-1)):SubstrateNewtorkRunTimeInterval;


subplot(1,2,1);
% plot(plotXaxisValue,experimentCostNode);
hold on;
grid on;
for i=1:1:AlgNum
    plot(plotXaxisValue,experimentCostNode(i,:),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end

subplot(1,2,2);
% plot(plotXaxisValue,experimentCostEdge);
hold on;
grid on;
for i=1:1:AlgNum
    plot(plotXaxisValue,experimentCostEdge(i,:),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end

% subplot(1,4,4);
% experimentCostEdgebNode=experimentCostNodeUsed.*addNewNodeCost+experimentCostEdge+(experimentCostNode*RelativeCostbetweenComputingBandwidth);
% % plot(plotXaxisValue,experimentCostEdgebNode);
% hold on;
% grid on;
% for i=1:1:AlgNum
%     plot(plotXaxisValue,experimentCostEdgebNode(i,:),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
% end
h=legend(LegendString,'Orientation','horizontal','FontSize',LegendSize);
set(h,'Fontsize',pictureLegendFont);
hold off;
if ~exist([FileAbsolutePath,'Data\\Fig'])
    mkdir([FileAbsolutePath,'Data\\Fig'])
end
saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\CostRevenueAccumulate','.eps']);
saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\CostRevenueAccumulate','.jpg']);

