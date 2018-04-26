main;

experimentNodeUsed=DataProcess('Data\\Cost_NodeCp_Accumulate');
experimentEdgeUsed=DataProcess('Data\\Cost_EdgeBw_Accumulate');
experimentAverage=DataProcess('Data\\Request_VirNet_Accumulate_');

experimentNodeUsed=experimentNodeUsed./experimentAverage;
experimentEdgeUsed=experimentEdgeUsed./experimentAverage;

subplot(1,2,1);
% plot(plotXaxisValue,experimentNode);
hold on;
grid on;
axis tight
xlabel('Node Computation')
for i=1:1:AlgNum
    plot(plotXaxisValue,PolyTao(plotXaxisValue,experimentNodeUsed(i,:),curveFittingPolypower,iscurveFitting),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end

subplot(1,2,2);
hold on;
grid on;
axis tight
xlabel('Edge Bandwidth')
for i=1:1:AlgNum
    plot(plotXaxisValue,PolyTao(plotXaxisValue,experimentEdgeUsed(i,:),curveFittingPolypower,iscurveFitting),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end

h=legend(LegendString,'Orientation','horizontal','FontSize',LegendSize);
set(h,'Fontsize',pictureLegendFont);
hold off;
if ~exist([FileAbsolutePath,'Data\\Fig'])
    mkdir([FileAbsolutePath,'Data\\Fig'])
end

saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\CostAccumulateAverageSubstrateNetwork','.eps']);
saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\CostAccumulateAverageSubstrateNetwork','.jpg']);

