main;

experimentNode=DataProcess('Data\\ActiveNode_SubNode_');
experimentNodeAcc=DataProcess('Data\\ActiveNode_SubNode_Accumulate');

experimentNode(:,1)=NaN;
experimentNodeAcc(:,1)=NaN;

subplot(1,2,1);

title('ActiveNodeSubstrateNetwork');
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
saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\ActiveNodeSubstrateNetwork','.eps']);
saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\ActiveNodeSubstrateNetwork','.jpg']);

