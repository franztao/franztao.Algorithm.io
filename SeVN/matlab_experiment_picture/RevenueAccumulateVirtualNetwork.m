main;

experimentEdge=DataProcess('Data\\Revenue_EdgeBw_Accumulate');
experimentNode=DataProcess('Data\\Revenue_NodeCp_Accumulate');

subplot(1,2,1);
hold on;
grid on;
axis tight
xlabel('Node Computation')
for i=1:1:AlgNum
    plot(plotXaxisValue,PolyTao(plotXaxisValue,experimentNode(i,:),curveFittingPolypower,iscurveFitting),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end

subplot(1,2,2);
hold on;
grid on;
axis tight
xlabel('Edge Bandwidth')
for i=1:1:AlgNum
    plot(plotXaxisValue,PolyTao(plotXaxisValue,experimentEdge(i,:),curveFittingPolypower,iscurveFitting),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end

h=legend(LegendString,'Orientation','horizontal','FontSize',LegendSize);
set(h,'Fontsize',pictureLegendFont);

hold off;
if ~exist([FileAbsolutePath,'Data\\Fig'])
    mkdir([FileAbsolutePath,'Data\\Fig'])
end
saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\RevenueAccumulateVirtualNetwork','.eps']);
saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\RevenueAccumulateVirtualNetwork','.jpg']);


