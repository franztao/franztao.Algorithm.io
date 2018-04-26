main;

experimentNode=DataProcess('Data\\Stress_Node');
experimentEdge=DataProcess('Data\\Stress_Edge');

experimentEdge(:,1)=NaN;
experimentNode(:,1)=NaN;
plotXaxisValue=0:(SubstrateNewtorkRunTimeInterval/(ExperimentPicturePlotNumber-1)):SubstrateNewtorkRunTimeInterval;

subplot(1,2,1);
hold on;
grid on;
axis tight
xlabel('Node')
for i=1:1:AlgNum
    plot(plotXaxisValue,PolyTao(plotXaxisValue,experimentNode(i,:),curveFittingPolypower,iscurveFitting),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end

subplot(1,2,2);
hold on;
grid on;
axis tight
xlabel('Edge')
for i=1:1:AlgNum
    plot(plotXaxisValue,PolyTao(plotXaxisValue,experimentEdge(i,:),curveFittingPolypower,iscurveFitting),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end

h=legend(LegendString,'Orientation','horizontal','FontSize',LegendSize);
set(h,'Fontsize',pictureLegendFont);
hold off;
if ~exist([FileAbsolutePath,'Data\\Fig'])
    mkdir([FileAbsolutePath,'Data\\Fig'])
end
saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\Stress','.eps']);
saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\Stress','.jpg']);
