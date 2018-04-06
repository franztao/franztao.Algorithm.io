main;


experimentEdgeUsed=DataProcess('Data\\Revenue_EdgeBw_Accumulate');
experimentNodeUsed=DataProcess('Data\\Revenue_NodeCp_Accumulate');
experimentAverage=DataProcess('Data\\Request_VirNet_Accumulate_');

experimentNodeUsed=experimentNodeUsed./experimentAverage;
experimentEdgeUsed=experimentEdgeUsed./experimentAverage;


plotXaxisValue=0:(SubstrateNewtorkRunTimeInterval/(ExperimentPicturePlotNumber-1)):SubstrateNewtorkRunTimeInterval;

subplot(1,2,1);

hold on;
grid on;
axis tight
xlabel('Node Computation')
for i=1:1:AlgNum
    plot(plotXaxisValue,experimentNodeUsed(i,:),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end

subplot(1,2,2);
hold on;
grid on;
axis tight
xlabel('Edge Bandwidth')
for i=1:1:AlgNum
    plot(plotXaxisValue,experimentEdgeUsed(i,:),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end

h=legend(LegendString,'Orientation','horizontal','FontSize',LegendSize);
set(h,'Fontsize',pictureLegendFont);

hold off;
if ~exist([FileAbsolutePath,'Data\\Fig'])
    mkdir([FileAbsolutePath,'Data\\Fig'])
end
saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\RevenueAccumulateAverageVirtualNetwork','.eps']);
saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\RevenueAccumulateAverageVirtualNetwork','.jpg']);


