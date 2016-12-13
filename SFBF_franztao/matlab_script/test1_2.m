
abusolutepath='F:\\workspace\\SFBF_franztao\\src\testdata\\out\\';
str_test_sort='test1_virtualdata';
str_test_sort='test1_actualdata_IP_1';
% str_test_sort='test1_actualdata_IP_2';
% str_test_sort='test1_actualdata_webcache_1';
% str_test_sort='test1_actualdata_webcache_2';

judge=1;
lengfont=14;
fontsize=17;
searchdatalength=100000;

subplot(1,3,1);
filemat=csvread([abusolutepath,str_test_sort,'\\Extension round.txt']);
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

sfbfmat1=(sfbfmat1');
sfbfmat2=(sfbfmat2');
sfbfmat3=(sfbfmat3');
sfbfmat4=(sfbfmat4');

if judge==1
    sfbfmat5=(sfbfmat5');
    % sfbfmat6=(sfbfmat6');
end
dmat=(dmat');


sx1=1:1200:30000;
sx2=1:1200:30000;
sx3=1:1200:30000;
sx4=1:1200:30000;
sx5=1:1200:30000;
% sx6=1:1200:30000;
dx=1:1200:30000;

linewidth=2;

xlabel('Data Set Size','Fontsize',fontsize);
ylabel('Extension round','Fontsize',fontsize);
grid on;
%axis([0,300,0,0.01]);
%set(gca,'XDir','reverse','YDir','normal');
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
    h=legend('SFBF-\lambda_1','SFBF-\lambda_2','SFBF-\lambda_3','SFBF-\lambda_4','SFBF-\lambda_5','DBF',2);
    set(h,'Fontsize',lengfont);
else
    legend('SFBF-\lambda_1','SFBF-\lambda_2','SFBF-\lambda_3','SFBF-\lambda_4','DBF',2);
end
%legend('SFBF-1,2,3,4','SFBF-1,1,2,3','SFBF-1,1+[1,2]','SFBF-1,1+[1,2,3]','SFBF-1,1,2,2,3,3',2);
hold off;

subplot(1,3,2);

filemat=csvread([abusolutepath,str_test_sort,'\\Space size of filters.txt']);
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

sfbfmat1=(sfbfmat1');
sfbfmat2=(sfbfmat2');
sfbfmat3=(sfbfmat3');
sfbfmat4=(sfbfmat4');
if judge==1
    sfbfmat5=(sfbfmat5');
    % sfbfmat6=(sfbfmat6');
end
dmat=(dmat');

sx1=1:1200:30000;
sx2=1:1200:30000;
sx3=1:1200:30000;
sx4=1:1200:30000;
if judge==1
    sx5=1:1200:30000;
    % sx6=1:1200:30000;
end
dx=1:1200:30000;


xlabel('Data Set Size','Fontsize',fontsize);
ylabel('Space size of filters','Fontsize',fontsize);
grid on;
%axis([0,300,0,0.01]);
%set(gca,'XDir','reverse','YDir','normal');

set(gca, 'GridLineStyle' ,'-');
set(gca, 'Fontsize',fontsize);

hold on;
plot(sx1,sfbfmat1,'r-o','LineWidth',linewidth,'MarkerFaceColor','r');
plot(sx2,sfbfmat2,'c-^','LineWidth',linewidth,'MarkerFaceColor','c');
plot(sx3,sfbfmat3,'m-<','LineWidth',linewidth,'MarkerFaceColor','m');
plot(sx4,sfbfmat4,'b->','LineWidth',linewidth,'MarkerFaceColor','b');

if judge==1
    plot(sx5,sfbfmat5,'g-p','LineWidth',linewidth,'MarkerFaceColor','g');
end

plot(dx,dmat,'k-s','LineWidth',linewidth,'MarkerFaceColor','k');
% plot(dx,dmat,'k','LineWidth',linewidth);

if judge==1
    h=legend('SFBF-\lambda_1','SFBF-\lambda_2','SFBF-\lambda_3','SFBF-\lambda_4','SFBF-\lambda_5','DBF',2);
    set(h,'Fontsize',lengfont);
else
    legend('SFBF-\lambda_1','SFBF-\lambda_2','SFBF-\lambda_3','SFBF-\lambda_4','DBF',2);
end
hold off;

subplot(1,3,3);

filemat=csvread([abusolutepath,str_test_sort,'\\False positive rate.txt']);
sfbfmat1=filemat(1:25);
sfbfmat2=filemat(26:50);
sfbfmat3=filemat(51:75);
sfbfmat4=filemat(76:100);

if judge==1
    sfbfmat5=filemat(101:125);
    % sfbfmat6=filemat(126:150);
    dmat=filemat(126:150);
    bmat=filemat(151:175);
else
    bmat=filemat(126:150);
    dmat=filemat(101:125);
end
sfbfmat1=(sfbfmat1');
sfbfmat2=(sfbfmat2');
sfbfmat3=(sfbfmat3');
sfbfmat4=(sfbfmat4');


if judge==1
    sfbfmat5=(sfbfmat5');
    % sfbfmat6=(sfbfmat6');
end
dmat=dmat';
bmat=(bmat');

sx1=1:1200:30000;
sx2=1:1200:30000;
sx3=1:1200:30000;
sx4=1:1200:30000;

if judge==1
    sx5=1:1200:30000;
    % sx6=1:1200:30000;
end
dx=1:1200:30000;
bx=1:1200:30000;


xlabel('Data Set Size','Fontsize',fontsize);
ylabel('False positive rate','Fontsize',fontsize);
grid on;
%axis([0,300,0,0.01]);
%set(gca,'XDir','reverse','YDir','normal');

set(gca, 'GridLineStyle' ,'-');
set(gca, 'Fontsize',fontsize);

hold on;
plot(sx1,sfbfmat1,'r-o','LineWidth',linewidth,'MarkerFaceColor','r');
plot(sx2,sfbfmat2,'c-^','LineWidth',linewidth,'MarkerFaceColor','c');
plot(sx3,sfbfmat3,'m-<','LineWidth',linewidth,'MarkerFaceColor','m');
plot(sx4,sfbfmat4,'b->','LineWidth',linewidth,'MarkerFaceColor','b');

if judge==1
    plot(sx5,sfbfmat5,'g-p','LineWidth',linewidth,'MarkerFaceColor','g');
    %  plot(sx6,sfbfmat6,'y->','LineWidth',linewidth,'MarkerFaceColor','y');
end

plot(dx,dmat,'k-s','LineWidth',linewidth,'MarkerFaceColor','k');
% plot(bx,bmat,'y-*','LineWidth',1);
if judge==1
    h=legend('SFBF-\lambda_1','SFBF-\lambda_2','SFBF-\lambda_3','SFBF-\lambda_4','SFBF-\lambda_5','DBF','BasicBF',2);
    set(h,'Fontsize',lengfont);
else
    legend('SFBF-\lambda_1','SFBF-\lambda_2','SFBF-\lambda_3','SFBF-\lambda_4','DBF','BasicBF',2);
end
hold off;






