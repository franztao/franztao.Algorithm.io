main;

experimentEVN=DataProcess('Data\\AcceptionRatio_SVN_');
experimentVN=DataProcess('Data\\AcceptionRatio_VN_');

experimentVN(:,1)=NaN;
experimentEVN(:,1)=NaN;

subplot(1,3,1);
hold on;
grid on;
axis tight
for i=1:1:AlgNum
    plot(plotXaxisValue,PolyTao(plotXaxisValue,experimentVN(i,:),curveFittingPolypower,iscurveFitting),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end
xlabel('eVN Ratio');


subplot(1,3,2);
hold on;
grid on;
axis tight
for i=1:1:AlgNum
    plot(plotXaxisValue,PolyTao(plotXaxisValue,experimentEVN(i,:),curveFittingPolypower,iscurveFitting),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end
xlabel('SeVN Ratio');

subplot(1,3,3);
experimentEVNbVN=experimentEVN./experimentVN;
hold on;
grid on;
axis tight
for i=1:1:AlgNum
    plot(plotXaxisValue,PolyTao(plotXaxisValue,experimentEVNbVN(i,:),curveFittingPolypower,iscurveFitting),strtrim(ALgLineStyle(i,:)),'LineWidth',pictureLineWidth,'MarkerFaceColor',strtrim(ALgLineBlockColor(i,:)));
end
xlabel('SeVN Ratio/eVN Ratio');

%h=legend('FI\_Shared','FI\_NoShared','FD\_EVSNR\_Ran\_Shared','FD\_EVSNR\_Ran\_NoShared','Orientation','horizontal');
%h=legend('FD\_Min\_Shared\_Heuristic','FD\_Min\_NoShared\_Heuristic','FD\_Ran\_Shared\_Heuristic','FD\_Ran\_NoShared\_Heuristic','FI\_Min\_Shared\_Heuristic','FI\_Min\_NoShared\_Heuristic','FI\_Ran\_Shared\_Heuristic','FI\_Ran\_NoShared\_Heuristic','Orientation','horizontal','FontSize',LegendSize);
hold off;
h=legend(LegendString,'Location','North','Orientation','horizontal','FontSize',LegendSize);
set(h,'Fontsize',pictureLegendFont);


if ~exist([FileAbsolutePath,'Data\\Fig'])
    mkdir([FileAbsolutePath,'Data\\Fig'])
end


saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\AcceptionRatio','.eps']);
saveas(gcf,[FileAbsolutePath,'Data\\Fig','\\AcceptionRatio','.jpg']);
