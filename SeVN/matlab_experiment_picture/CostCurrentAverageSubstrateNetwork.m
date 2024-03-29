main;

experimentNodeUsed=DataProcess('Data\\Cost_NodeCp_');
experimentEdgeUsed=DataProcess('Data\\Cost_NdgeBw_');
experimentAverage=DataProcess('Data\\Request_VirNet_');

experimentNodeUsed=experimentNodeUsed./experimentAverage;
experimentEdgeUsed=experimentEdgeUsed./experimentAverage;

subplot(1,2,1);
% plot(plotXaxisValue,experimentNode);
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

saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\CostCurrentAverageSubstrateNetwork','.eps']);
saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\CostCurrentAverageSubstrateNetwork','.jpg']);

