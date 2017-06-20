abusolutepath='F:\\workspace\\SFBF_franztao\\src\testdata\\out\\';
str_test_sort='test1_virtualdata';
str_test_sort='test1_actualdata_IP_1';
% str_test_sort='test1_actualdata_IP_2';
% str_test_sort='test1_actualdata_webcache_1';
% str_test_sort='test1_actualdata_webcache_2';
judge=1;
lengfont=12;

linewidth=2;
fontsize=16;

searchdatalength=30000;

subplot(1,2,1);
[abusolutepath,str_test_sort,'\\Querying CPU time without shifting.txt']
filemat=csvread([abusolutepath,str_test_sort,'\\Querying CPU time without shifting.txt']);
sfbfmat1=filemat(1:25);
sfbfmat2=filemat(26:50);
sfbfmat3=filemat(51:75);
sfbfmat4=filemat(76:100);

if judge==1
    sfbfmat5=filemat(101:125);
    % sfbfmat6=filemat(126:150);
    dmat=filemat(126:150);
else
    
    dmat=filemat(101:125);
end

sfbfmat1=(sfbfmat1')/searchdatalength;
sfbfmat2=(sfbfmat2')/searchdatalength;
sfbfmat3=(sfbfmat3')/searchdatalength;
sfbfmat4=(sfbfmat4')/searchdatalength;

if judge==1
    sfbfmat5=(sfbfmat5')/searchdatalength;
    % sfbfmat6=(sfbfmat6');
end
dmat=(dmat')/searchdatalength;

sx1=1:1200:30000;
sx2=1:1200:30000;
sx3=1:1200:30000;
sx4=1:1200:30000;
if judge==1
    sx5=1:1200:30000;
    % sx6=1:1200:30000;
end
dx=1:1200:30000;


%title('Query Time without shifting','Fontsize',12);
xlabel('Data Set Size','Fontsize',fontsize);
ylabel('CPU Querying time without shifting(ms)','Fontsize',fontsize);
grid on;
set(gca, 'GridLineStyle' ,'-');
set(gca, 'Fontsize',fontsize);
hold on;
plot(sx1,sfbfmat1,'r-o','LineWidth',linewidth,'MarkerFaceColor','r');
plot(sx2,sfbfmat2,'c-^','LineWidth',linewidth,'MarkerFaceColor','c');
plot(sx3,sfbfmat3,'m-<','LineWidth',linewidth,'MarkerFaceColor','m');
plot(sx4,sfbfmat4,'b->','LineWidth',linewidth,'MarkerFaceColor','b');


if judge==1
    plot(sx5,sfbfmat5,'g-p','LineWidth',linewidth,'MarkerFaceColor','g');
    % plot(sx6,sfbfmat6,'y->','LineWidth',linewidth,'MarkerFaceColor','y');
end

plot(dx,dmat,'k-s','LineWidth',linewidth,'MarkerFaceColor','k');
if judge==1
    h=legend('SFBF-\lambda_1-direct','SFBF-\lambda_2-direct','SFBF-\lambda_3-direct','SFBF-\lambda_4-direct','SFBF-\lambda_5-direct','DBF',2);
    set(h,'Fontsize',lengfont);
else
    legend('SFBF-\lambda_1-direct','SFBF-\lambda_2-direct','SFBF-\lambda_3-direct','SFBF-\lambda_4-direct','DBF',2);
end
hold off;


subplot(1,2,2);

filemat=csvread([abusolutepath,str_test_sort,'\\Querying CPU time with shifting.txt']);
sfbfmat1=filemat(1:25);
sfbfmat2=filemat(26:50);
sfbfmat3=filemat(51:75);
sfbfmat4=filemat(76:100);
if judge==1
    sfbfmat5=filemat(101:125);
    % sfbfmat6=filemat(126:150);
    dmat=filemat(126:150);
else
    dmat=filemat(101:125);
end


sfbfmat1=(sfbfmat1')/searchdatalength;
sfbfmat2=(sfbfmat2')/searchdatalength;
sfbfmat3=(sfbfmat3')/searchdatalength;
sfbfmat4=(sfbfmat4')/searchdatalength;

if judge==1
    sfbfmat5=(sfbfmat5')/searchdatalength;
    % sfbfmat6=(sfbfmat6');
end
dmat=(dmat')/searchdatalength;


sx1=1:1200:30000;
sx2=1:1200:30000;
sx3=1:1200:30000;
sx4=1:1200:30000;
if judge==1
    sx5=1:1200:30000;
    % sx6=1:1200:30000;
end
dx=1:1200:30000;

% sfbfmat1=spline(sx1,sfbfmat1,sx1);
% sfbfmat2=spline(sx2,sfbfmat2,sx2);
% sfbfmat3=spline(sx3,sfbfmat3,sx3);
% sfbfmat4=spline(sx4,sfbfmat4,sx4);
% if judge==1
% sfbfmat5=spline(sx5,sfbfmat5,sx5);
% sfbfmat6=spline(sx6,sfbfmat6,sx6);
% end
% dmat=spline(dx,dmat,dx);

%title('Query Time without shifting','Fontsize',12);
xlabel('Data Set Size','Fontsize',fontsize);
ylabel('CPU Querying time with shifting(ms)','Fontsize',fontsize);
grid on;
set(gca, 'GridLineStyle' ,'-');
set(gca, 'Fontsize',fontsize);
hold on;
plot(sx1,sfbfmat1,'r-o','LineWidth',linewidth,'MarkerFaceColor','r');
plot(sx2,sfbfmat2,'c-^','LineWidth',linewidth,'MarkerFaceColor','c');
plot(sx3,sfbfmat3,'m-<','LineWidth',linewidth,'MarkerFaceColor','m');
plot(sx4,sfbfmat4,'b->','LineWidth',linewidth,'MarkerFaceColor','b');
if judge==1
    plot(sx5,sfbfmat5,'g-p','LineWidth',linewidth,'MarkerFaceColor','g');
%     plot(sx6,sfbfmat6,'y->','LineWidth',linewidth,'MarkerFaceColor','y');
end

plot(dx,dmat,'k-s','LineWidth',linewidth,'MarkerFaceColor','k');
if judge==1
    h=legend('SFBF-\lambda_1-shift','SFBF-\lambda_2-shift','SFBF-\lambda_3-shift','SFBF-\lambda_4-shift','SFBF-\lambda_5-shift','DBF',2);
    set(h,'Fontsize',lengfont);
else
    legend('SFBF-\lambda_1-shift','SFBF-\lambda_2-shift','SFBF-\lambda_3-shift','SFBF-\lambda_4-shift','DBF',2);
end
hold off;


%     [legh,objh,outh,outm]=legend(p1,'SFBF-\lambda_1-direct');
%     set(legh,'Box','off');
%     set(legh,'position',[0.2,0.8,0.1,0.1]);
%     set(legh,'Fontsize',lengfont);
%     legh2=copyobj(legh,gcf);
%     [legh2,objh2]=legend(p2,'SFBF-\lambda_2-direct');
%     
%     set(legh2,'Box','off');
%     set(legh2,'position',[0.4,0.8,0.1,0.1]);
%     set(legh2,'Fontsize',lengfont);
%     legh3=copyobj(legh2,gcf);
%     [legh3,objh3]=legend(p3,'SFBF-\lambda_3-direct');
%     
%     set(legh3,'Box','off');
%     set(legh3,'position',[0.4,0.75,0.1,0.1]);
%     set(legh3,'Fontsize',lengfont);
%     legh4=copyobj(legh3,gcf);
%     [legh4,objh4]=legend(p4,'SFBF-\lambda_4-direct');
%     
%     set(legh4,'Box','off');
%     set(legh4,'position',[0.4,0.75,0.1,0.1]);
%     set(legh4,'Fontsize',lengfont);
%     legh5=copyobj(legh4,gcf);
%     [legh5,objh5]=legend(p5,'SFBF-\lambda_5-direct');
%     
%     set(legh5,'Box','off');
%     set(legh5,'position',[0.2,0.7,0.1,0.1]);
%     set(legh5,'Fontsize',lengfont);
%     legh6=copyobj(legh5,gcf);
%     [legh6,objh6]=legend(p6,'SFBF-\lambda_6-direct');
%     
%     set(legh6,'Box','off');
%     set(legh6,'position',[0.4,0.7,0.1,0.1]);
%     set(legh6,'Fontsize',lengfont);