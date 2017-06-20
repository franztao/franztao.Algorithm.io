

fontsize=15;
abusolutepath='F:\\workspace\\SFBF_franztao\\src\testdata\\out\\test2\\replace0\\';
str_test_sort='test2_virtualdata';
str_test_sort='test2_actualdata_IP_1';
% str_test_sort='test2_actualdata_IP_2';
% str_test_sort='test2_actualdata_webcache_1';
% str_test_sort='test2_actualdata_webcache_2';

%kk=[abusolutepath,str_test_sort]
subplot(3,1,1);

filemat=csvread([abusolutepath,str_test_sort,'\\hitratio.txt']);

sfbfhitratiomat1=filemat(1:7:63);
sfbfhitratiomat2=filemat(2:7:63);
sfbfhitratiomat3=filemat(3:7:63);
sfbfhitratiomat4=filemat(4:7:63);


dbfhitratiomat1=filemat(5:7:63);
dbfhitratiomat2=filemat(6:7:63);

dbfhitratiomat3=filemat(7:7:63);
% sx=[1,2,4,8,16,32,64,128,256,512,1024,2048,4096,8192];

sfbfhitratiomat1=sfbfhitratiomat1';
sfbfhitratiomat2=sfbfhitratiomat2';
sfbfhitratiomat3=sfbfhitratiomat3';
sfbfhitratiomat4=sfbfhitratiomat4';
dbfhitratiomat1=dbfhitratiomat1';
dbfhitratiomat2=dbfhitratiomat2';
dbfhitratiomat3=dbfhitratiomat3';

size(sfbfhitratiomat1)

linewidth=2;
xlabel('Memory Size','Fontsize',fontsize);
ylabel('Average hit ratio','Fontsize',fontsize);
grid on;

% set(gca,'xtick',[1 2 3 4 5 6 7 8 9 10 11 12 13 7 15 16],'XTickLabel',{'1Kb','2Kb','4Kb','8Kb','16Kb','32Kb','64Kb','128Kb','256Kb','512Kb','1Mb','2Mb','4Mb','8Mb','16Mb','32Mb'});
set(gca,'xtick',[1 2 3 4 5 6 7 8],'XTickLabel',{'1Kb','4Kb','16Kb','64Kb','256Kb','1Mb','4Mb','16Mb'});
set(gca, 'GridLineStyle' ,'-');
set(gca, 'Fontsize',10);

% set(gca,'xtick',[2005 2006 2007 2008 2009 2010]);

hold on;

plot(sfbfhitratiomat1,'r-o','LineWidth',linewidth,'MarkerFaceColor','r');
plot(sfbfhitratiomat2,'c-^','LineWidth',linewidth,'MarkerFaceColor','c');
plot(sfbfhitratiomat3,'m-<','LineWidth',linewidth,'MarkerFaceColor','m');
plot(sfbfhitratiomat4,'b->','LineWidth',linewidth,'MarkerFaceColor','b'); 

plot(dbfhitratiomat1,'g->','LineWidth',linewidth,'MarkerFaceColor','g'); 
plot(dbfhitratiomat2,'y->','LineWidth',linewidth,'MarkerFaceColor','y'); 

plot(dbfhitratiomat3,'k-','LineWidth',linewidth);
h=legend('SFBF-\lambda_1-shift','SFBF-\lambda_2-shift','SFBF-\lambda_3-shift','SFBF-\lambda_4-shift','SFBF-\lambda_5-shift','SFBF-\lambda_6-shift','DBF',2);
set(h,'Fontsize',12);
hold off;

subplot(3,1,2);
filemat=csvread([abusolutepath,str_test_sort,'\\reset.txt']);
%filemat=csvread('F:\\workspace\\SFBF_franztao\\src\testdata\\out\\test2_virtualdata\\reset.txt');
sfbfhitratiomat1=filemat(1:7:63);
sfbfhitratiomat2=filemat(2:7:63);
sfbfhitratiomat3=filemat(3:7:63);
sfbfhitratiomat4=filemat(4:7:63);


dbfhitratiomat1=filemat(5:7:63);
dbfhitratiomat2=filemat(6:7:63);

dbfhitratiomat3=filemat(7:7:63);

sfbfhitratiomat1=sfbfhitratiomat1';
sfbfhitratiomat2=sfbfhitratiomat2';
sfbfhitratiomat3=sfbfhitratiomat3';
sfbfhitratiomat4=sfbfhitratiomat4';
dbfhitratiomat1=dbfhitratiomat1';
dbfhitratiomat2=dbfhitratiomat2';
dbfhitratiomat3=dbfhitratiomat3';

size(sfbfhitratiomat1)

linewidth=2;
xlabel('Memory Size','Fontsize',fontsize);
ylabel('No. of resets','Fontsize',fontsize);
grid on;
set(gca, 'GridLineStyle' ,'-');
set(gca, 'Fontsize',10);

% set(gca,'xtick',[1 2 3 4 5 6 7 8 9 10 11 12 13 7 15 16],'XTickLabel',{'1Kb','2Kb','4Kb','8Kb','16Kb','32Kb','64Kb','128Kb','256Kb','512Kb','1Mb','2Mb','4Mb','8Mb','16Mb','32Mb'});
% set(gca,'xticklabel',{'1','2','4','8','16','32','64','128','256','512','1024'});
set(gca,'xtick',[1 2 3 4 5 6 7 8],'XTickLabel',{'1Kb','4Kb','16Kb','64Kb','256Kb','1Mb','4Mb','16Mb'});
hold on;

plot(sfbfhitratiomat1,'r-o','LineWidth',linewidth,'MarkerFaceColor','r');
plot(sfbfhitratiomat2,'c-^','LineWidth',linewidth,'MarkerFaceColor','c');
plot(sfbfhitratiomat3,'m-<','LineWidth',linewidth,'MarkerFaceColor','m');
plot(sfbfhitratiomat4,'b->','LineWidth',linewidth,'MarkerFaceColor','b'); 

plot(dbfhitratiomat1,'g->','LineWidth',linewidth,'MarkerFaceColor','g'); 
plot(dbfhitratiomat2,'y->','LineWidth',linewidth,'MarkerFaceColor','y'); 

plot(dbfhitratiomat3,'k-','LineWidth',linewidth);
h=legend('SFBF-\lambda_1-shift','SFBF-\lambda_2-shift','SFBF-\lambda_3-shift','SFBF-\lambda_4-shift','SFBF-\lambda_5-shift','SFBF-\lambda_6-shift','DBF',2);
set(h,'Fontsize',12);
hold off;




subplot(3,1,3);
filemat=csvread([abusolutepath,str_test_sort,'\\searchtime.txt']);
%filemat=csvread('F:\\workspace\\SFBF_franztao\\src\testdata\\out\\test2_virtualdata\\searchtime.txt');
sfbfhitratiomat1=filemat(1:7:63);
sfbfhitratiomat2=filemat(2:7:63);
sfbfhitratiomat3=filemat(3:7:63);
sfbfhitratiomat4=filemat(4:7:63);


dbfhitratiomat1=filemat(5:7:63);
dbfhitratiomat2=filemat(6:7:63);

dbfhitratiomat3=filemat(7:7:63);

sfbfhitratiomat1=sfbfhitratiomat1';
sfbfhitratiomat2=sfbfhitratiomat2';
sfbfhitratiomat3=sfbfhitratiomat3';
sfbfhitratiomat4=sfbfhitratiomat4';
dbfhitratiomat1=dbfhitratiomat1';
dbfhitratiomat2=dbfhitratiomat2';
dbfhitratiomat3=dbfhitratiomat3';

size(sfbfhitratiomat1)

linewidth=2;
xlabel('Memory Size','Fontsize',12);
ylabel('Average Time of insert a key','Fontsize',12);
grid on;
set(gca, 'GridLineStyle' ,'-');
set(gca, 'Fontsize',10);

% set(gca,'xtick',[1 2 3 4 5 6 7 8 9 10 11 12 13 7 15 16],'XTickLabel',{'1Kb','2Kb','4Kb','8Kb','16Kb','32Kb','64Kb','128Kb','256Kb','512Kb','1Mb','2Mb','4Mb','8Mb','16Mb','32Mb'});
% set(gca,'xticklabel',{'1','2','4','8','16','32','64','128','256','512','1024'});
set(gca,'xtick',[1 2 3 4 5 6 7 8],'XTickLabel',{'1Kb','4Kb','16Kb','64Kb','256Kb','1Mb','4Mb','16Mb'});
hold on;

plot(sfbfhitratiomat1,'r-o','LineWidth',linewidth,'MarkerFaceColor','r');
plot(sfbfhitratiomat2,'c-^','LineWidth',linewidth,'MarkerFaceColor','c');
plot(sfbfhitratiomat3,'m-<','LineWidth',linewidth,'MarkerFaceColor','m');
plot(sfbfhitratiomat4,'b->','LineWidth',linewidth,'MarkerFaceColor','b'); 

plot(dbfhitratiomat1,'g->','LineWidth',linewidth,'MarkerFaceColor','g'); 
plot(dbfhitratiomat2,'y->','LineWidth',linewidth,'MarkerFaceColor','y'); 

plot(dbfhitratiomat3,'k-','LineWidth',linewidth);
h=legend('SFBF-\lambda_1-shift','SFBF-\lambda_2-shift','SFBF-\lambda_3-shift','SFBF-\lambda_4-shift','SFBF-\lambda_5-shift','SFBF-\lambda_6-shift','DBF',2);
set(h,'Fontsize',12);
hold off;


