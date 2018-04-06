main;

experimentEdge=DataProcess('Data\\Cost_NodeCp_Accumulate');
experimentNode=DataProcess('Data\\Cost_EdgeBw_Accumulate');

experimentRevenueEdge=DataProcess('Data\\Revenue_NodeCp_Accumulate');
experimentRevenueNode=DataProcess('Data\\Revenue_EdgeBw_Accumulate');

experimentCostEdge=experimentEdge./experimentRevenueEdge;
experimentCostNode=experimentNode./experimentRevenueNode;

subplot(1,2,1);
hold on;
grid on;
axis tight
xlabel('Node Computation')
for i=1:1:AlgNum
    plot(plotXaxisValue,experimentCostNode(i,:),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end

subplot(1,2,2);
hold on;
grid on;
axis tight
xlabel('Edge Bandwidth')
for i=1:1:AlgNum
    plot(plotXaxisValue,experimentCostEdge(i,:),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end

h=legend(LegendString,'Orientation','horizontal','FontSize',LegendSize);
set(h,'Fontsize',pictureLegendFont);
hold off;
if ~exist([FileAbsolutePath,'Data\\Fig'])
    mkdir([FileAbsolutePath,'Data\\Fig'])
end
saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\CostRevenueAccumulate','.eps']);
saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\CostRevenueAccumulate','.jpg']);

