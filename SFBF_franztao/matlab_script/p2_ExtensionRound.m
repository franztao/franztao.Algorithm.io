head;
hfig1 = figure;
set(hfig1, 'position', get(0,'ScreenSize'));

for i=1:4
    subplot(2,2,i);
    url=strcat(abusolutepath,str_test_sort_array(i),'\\Extension round.txt');
    filemat=csvread(url{1});
    sfbfmat1=filemat(1:data_len);
    sfbfmat2=filemat((data_len+1):(2*data_len));
    sfbfmat3=filemat((2*data_len+1):(3*data_len));
    sfbfmat4=filemat((3*data_len+1):(4*data_len));
    
    if have_sfbfmat5==1
        sfbfmat5=filemat((4*data_len+1):(5*data_len));
        dmat=filemat((5*data_len+1):(6*data_len));
    else
        dmat=filemat((5*data_len+1):(6*data_len));
    end
    
    sfbfmat1=(sfbfmat1');
    sfbfmat2=(sfbfmat2');
    sfbfmat3=(sfbfmat3');
    sfbfmat4=(sfbfmat4');
    
    if have_sfbfmat5==1
        sfbfmat5=(sfbfmat5');
    end
    dmat=dmat';
    
    
    sfbfmat1=[0,sfbfmat1];
    sfbfmat2=[0,sfbfmat2];
    sfbfmat3=[0,sfbfmat3];
    sfbfmat4=[0,sfbfmat4];
    sfbfmat5=[0,sfbfmat5];
    dmat=[0,dmat];
    %     bmat=[0,bmat];
    
    sx1=0:test1InsertedDataLength_Interval:test1InsertDataLength;
    sx2=0:test1InsertedDataLength_Interval:test1InsertDataLength;
    sx3=0:test1InsertedDataLength_Interval:test1InsertDataLength;
    sx4=0:test1InsertedDataLength_Interval:test1InsertDataLength;
    sx5=0:test1InsertedDataLength_Interval:test1InsertDataLength;
    
    dx=0:test1InsertedDataLength_Interval:test1InsertDataLength;
    %     bx=0:test1InsertedDataLength_Interval:test1InsertDataLength;
    
    xlabel('Data Set Size','Fontsize',xlabel_fontsize);
    %     ylabel('Extension round','Fontsize',fontsize);
    ylabel(str_ylabel(i),'Fontsize',fontsize);
    grid on;
    
    set(gca, 'GridLineStyle' ,'-');
    set(gca, 'Fontsize',grid_fontsize);
    
    hold on;
    plot(sx1,sfbfmat1,'r-o','LineWidth',linewidth,'MarkerFaceColor','r');
    plot(sx2,sfbfmat2,'c-^','LineWidth',linewidth,'MarkerFaceColor','c');
    plot(sx3,sfbfmat3,'m-<','LineWidth',linewidth,'MarkerFaceColor','m');
    plot(sx4,sfbfmat4,'b->','LineWidth',linewidth,'MarkerFaceColor','b');
    
    if have_sfbfmat5==1
        plot(sx5,sfbfmat5,'g-p','LineWidth',linewidth,'MarkerFaceColor','g');
        %  plot(sx6,sfbfmat6,'y->','LineWidth',linewidth,'MarkerFaceColor','y');
    end
    
    plot(dx,dmat,'k-s','LineWidth',linewidth,'MarkerFaceColor','k');
    
    if i==1
        if have_sfbfmat5==1
            h=legend('SFBF-\lambda_1','SFBF-\lambda_2','SFBF-\lambda_3','SFBF-\lambda_4','SFBF-\lambda_5','DBF','BasicBF','Orientation','horizontal');
            
        else
            h=legend('SFBF-\lambda_1','SFBF-\lambda_2','SFBF-\lambda_3','SFBF-\lambda_4','DBF','BasicBF','Orientation','horizontal');
        end
        set(h,'Fontsize',lengfont);
        set(h,'Position',[.13,.95,.8,.04]);
        
        savevalue=dmat(1,data_len);
        save SecIXone savevalue;
    end
    hold off;
end
set(gcf,'color',[1,1,1]);
savesfig=getframe(gcf);
% imwrite(savesfig.cdata,strcat('F:\\Paper\\BloomFilter\\SFBF-submitted to TDKE20161208\\franztao20161209\\','p2_ExtensionRound.jpg'))

ScrszParms=get(0,'ScreenSize');
scalebig=1;
posHfig=[ScrszParms(1)*scalebig,  ScrszParms(2)*scalebig,  ScrszParms(3)*scalebig, ScrszParms(4)*scalebig ];
set(gcf, 'PaperPositionMode', 'manual'); % hFigure
set(gcf, 'PaperUnits', 'points');
set(gcf, 'PaperPosition', posHfig); %
print(gcf, '-depsc2',['F:\\Paper\\BloomFilter\\SFBF-submitted to TDKE20161208\\franztao20161209','\\p2_ExtensionRound','.eps']);
% saveas(gcf,['F:\\Paper\\BloomFilter\\SFBF-submitted to TDKE20161208\\franztao20161209','\\p2_ExtensionRound','.eps']);
% saveas(gcf,['F:\\Paper\\BloomFilter\\SFBF-submitted to TDKE20161208\\franztao20161209','\\p2_ExtensionRound','.jpg']);



