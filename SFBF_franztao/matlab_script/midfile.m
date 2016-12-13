abusolutepath='F:\\workspace\\SFBF_franztao\\src\testdata\\out\\';
str_test_sort='test1_virtualdata';
str_test_sort='test1_actualdata_IP_1';
str_test_sort='test1_actualdata_IP_2';
% str_test_sort='test1_actualdata_webcache_1';
% str_test_sort='test1_actualdata_webcache_2';
judge=1;
lengfont=12;

linewidth=2;
fontsize=16;

searchdatalength=30000;


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

% sfbfmat1=log(sfbfmat1);
% sfbfmat2=log(sfbfmat2);
% sfbfmat3=log(sfbfmat3);
% sfbfmat4=log(sfbfmat4);
% sfbfmat5=log(sfbfmat5);
% dmat1=log(dmat);
% 
% 
% 
% sfbfmat1=(sfbfmat1)/log(2);
% sfbfmat2=(sfbfmat2)/log(2);
% sfbfmat3=(sfbfmat3)/log(2);
% sfbfmat4=(sfbfmat4)/log(2);
% sfbfmat5=(sfbfmat5)/log(2);
% dmat1=(dmat)/log(1.5);



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