main;

experimentNode=DataProcess('Data\\ActiveNode_VirNode_');
experimentNodeAcc=DataProcess('Data\\ActiveNode_VirNode_Accumulate');
experimentSubNode=DataProcess('Data\\ActiveNode_SubNode_');
experimentSubNodeAcc=DataProcess('Data\\ActiveNode_SubNode_Accumulate');

experimentNode=experimentSubNode./experimentNode;
experimentNodeAcc=experimentSubNodeAcc./experimentNodeAcc;

subplot(1,2,1);
hold on;
grid on;
axis tight
for i=1:1:AlgNum
    plot(plotXaxisValue,experimentNode(i,:),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end

subplot(1,2,2);
hold on;
grid on;
axis tight
for i=1:1:AlgNum
    plot(plotXaxisValue,experimentNodeAcc(i,:),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end

h=legend(LegendString,'Orientation','horizontal','FontSize',LegendSize);
set(h,'Fontsize',pictureLegendFont);
hold off;
if ~exist([FileAbsolutePath,'Data\\Fig'])
    mkdir([FileAbsolutePath,'Data\\Fig'])
end
saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\ActiveNodeSubVir2VirNet','.eps']);
saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\ActiveNodeSubVir2VirNet','.jpg']);

