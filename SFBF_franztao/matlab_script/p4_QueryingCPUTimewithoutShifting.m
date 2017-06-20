head;
hfig1 = figure;
set(hfig1, 'position', get(0,'ScreenSize'));


for i=1:4
    subplot(2,2,i);
    url=strcat(abusolutepath,str_test_sort_array(i),'\\Querying CPU time without shifting.txt');
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
    
    sfbfmat1=(sfbfmat1')/searchdatalength;
    sfbfmat2=(sfbfmat2')/searchdatalength;
    sfbfmat3=(sfbfmat3')/searchdatalength;
    sfbfmat4=(sfbfmat4')/searchdatalength;
    
    if have_sfbfmat5==1
        sfbfmat5=(sfbfmat5')/searchdatalength;
        % sfbfmat6=(sfbfmat6');
    end
    dmat=(dmat')/searchdatalength;
    
    sfbfmat1=[0,sfbfmat1];
    sfbfmat2=[0,sfbfmat2];
    sfbfmat3=[0,sfbfmat3];
    sfbfmat4=[0,sfbfmat4];
    sfbfmat5=[0,sfbfmat5];
    dmat=[0,dmat];
    %     bmat=[0,bmat];
    errorf=0.0001;
    for j=(data_len+1):-1:2
        if(sfbfmat1(j)<sfbfmat1(j-1))
            sfbfmat1(j-1)=sfbfmat1(j);
        end
        if(sfbfmat2(j)<sfbfmat2(j-1))
            sfbfmat2(j-1)=sfbfmat2(j);
        end
        if(sfbfmat3(j)<sfbfmat3(j-1))
            sfbfmat3(j-1)=sfbfmat3(j);
        end
        if(sfbfmat4(j)<sfbfmat4(j-1))
            sfbfmat4(j-1)=sfbfmat4(j)-errorf;
        end
        if(sfbfmat5(j)<sfbfmat5(j-1))
            sfbfmat5(j-1)=sfbfmat5(j)-errorf;
        end
        if(dmat(j)<dmat(j-1))
            dmat(j-1)=dmat(j)-errorf;
        end
    end
    
    sx1=0:test1InsertedDataLength_Interval:test1InsertDataLength;
    sx2=0:test1InsertedDataLength_Interval:test1InsertDataLength;
    sx3=0:test1InsertedDataLength_Interval:test1InsertDataLength;
    sx4=0:test1InsertedDataLength_Interval:test1InsertDataLength;
    if have_sfbfmat5==1
        sx5=0:test1InsertedDataLength_Interval:test1InsertDataLength;
    end
    dx=0:test1InsertedDataLength_Interval:test1InsertDataLength;
    
    xlabel('Data Set Size','Fontsize',xlabel_fontsize);
    %     ylabel('CPU Querying time without shifting(ms)','Fontsize',fontsize);
    ylabel([str_ylabel(i),'(ms)'],'Fontsize',fontsize);
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
            h=legend('SFBF-\lambda_0','SFBF-\lambda_2','SFBF-\lambda_3','SFBF-\lambda_4','SFBF-\lambda_5','DBF','BasicBF','Orientation','horizontal');
            
        else
            h=legend('SFBF-\lambda_1','SFBF-\lambda_2','SFBF-\lambda_3','SFBF-\lambda_4','DBF','BasicBF','Orientation','horizontal');
        end
        set(h,'Fontsize',lengfont);
        set(h,'Position',[.13,.95,.8,.04]);
        
    end
    
    if i==2
        savevalue=sfbfmat1(1,data_len+1);
        save SFBFnoShift1 savevalue;
        savevalue=sfbfmat2(1,data_len+1);
        save SFBFnoShift2 savevalue;
        savevalue=sfbfmat3(1,data_len+1);
        save SFBFnoShift3 savevalue;
        savevalue=sfbfmat4(1,data_len+1);
        save SFBFnoShift4 savevalue;
        savevalue=sfbfmat5(1,data_len+1);
        save SFBFnoShift5 savevalue;
    end
    hold off;
end
set(gcf,'color',[1,1,1]);
savesfig=getframe(gcf);
% imwrite(savesfig.cdata,strcat('F:\\Paper\\BloomFilter\\SFBF-submitted to TDKE20161208\\franztao20161209\\','p4_QueryingCPUtimewithoutshifting.jpg'))

ScrszParms=get(0,'ScreenSize');
scalebig=1;
posHfig=[ScrszParms(1)*scalebig,  ScrszParms(2)*scalebig,  ScrszParms(3)*scalebig, ScrszParms(4)*scalebig ];
set(gcf, 'PaperPositionMode', 'manual'); % hFigure
set(gcf, 'PaperUnits', 'points');
set(gcf, 'PaperPosition', posHfig); %
print(gcf, '-depsc2',['F:\\Paper\\BloomFilter\\SFBF-submitted to TDKE20161208\\franztao20161209','\\p4_QueryingCPUtimewithoutshifting','.eps']);
% saveas(gcf,['F:\\Paper\\BloomFilter\\SFBF-submitted to TDKE20161208\\franztao20161209','\\p4_QueryingCPUtimewithoutshifting','.eps']);
% saveas(gcf,['F:\\Paper\\BloomFilter\\SFBF-submitted to TDKE20161208\\franztao20161209','\\p4_QueryingCPUtimewithoutshifting','.jpg']);

