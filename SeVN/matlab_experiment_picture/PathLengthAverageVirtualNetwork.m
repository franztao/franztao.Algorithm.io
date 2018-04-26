main;


experimentNode=DataProcess('Data\\PathLength_VirEdge_');
experimentNodeAcc=DataProcess('Data\\PathLength_VirEdge_Accumulate');
experimentAverage=DataProcess('Data\\Request_VirNet_');
experimentAverageACC=DataProcess('Data\\Request_VirNet_Accumulate_');



experimentNode=experimentNode./experimentAverage;
experimentNodeAcc=experimentNodeAcc./experimentAverageACC;

subplot(1,2,1);

hold on;
grid on;
axis tight
xlabel('real-time');
for i=1:1:AlgNum
    plot(plotXaxisValue,PolyTao(plotXaxisValue,experimentNode(i,:),curveFittingPolypower,iscurveFitting),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end

subplot(1,2,2);
hold on;
grid on;
axis tight
xlabel('increment');
for i=1:1:AlgNum
    plot(plotXaxisValue,PolyTao(plotXaxisValue,experimentNodeAcc(i,:),curveFittingPolypower,iscurveFitting),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end

h=legend(LegendString,'Orientation','horizontal','FontSize',LegendSize);
set(h,'Fontsize',pictureLegendFont);

hold off;
if ~exist([FileAbsolutePath,'Data\\Fig'])
    mkdir([FileAbsolutePath,'Data\\Fig'])
end
saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\PathLengthAverageVirtualNetwork','.eps']);
saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\PathLengthAverageVirtualNetwork','.jpg']);


