subplot(3,1,1);

filemat=csvread('F:\\workspace\\SFBF_franztao\\src\\file\\hitratio.txt');
%a2sfhitratiomat=filemat(1:3:30);
sfbfhitratiomat=filemat(1:2:22);
dbfhitratiomat=filemat(2:2:22);

%x=[1,2,4,8,16,32,64,128,256,512,1024];

size(x)
size(sfbfhitratiomat)
%dousfhitratiomat=filemat(3:3:30);
%a2sfhitratiomat=(a2sfhitratiomat)';
sfbfhitratiomat=(sfbfhitratiomat)';
dbfhitratiomat=(dbfhitratiomat)';
%dousfhitratiomat=(dousfhitratiomat)';

linewidth=2;
xlabel('Memory Size(KB)','Fontsize',12);
ylabel('Average hit ratio','Fontsize',12);
grid on;
%axis([0,300,0,0.01]);
%set(gca,'XDir','reverse','YDir','normal');
set(gca, 'GridLineStyle' ,'-');
set(gca,'xticklabel',{'1','2','4','8','16','32','64','128','256','512','1024'});
hold on;
%plot(a2sfhitratiomat,'r-','LineWidth',linewidth);
plot(sfbfhitratiomat,'b-','LineWidth',linewidth);
plot(dbfhitratiomat,'r-','LineWidth',linewidth);
legend('SFBF','DBF',2);
hold off;

subplot(3,1,2);

filemat=csvread('F:\\workspace\\SFBF_franztao\\src\\file\\reset.txt');
%a2sfhitratiomat=filemat(1:3:30);
sfbfhitratiomat=filemat(1:2:22);
dbfhitratiomat=filemat(2:2:22);

%x=[1,2,4,8,16,32,64,128,256,512,1024];

%dousfhitratiomat=filemat(3:3:30);
%a2sfhitratiomat=(a2sfhitratiomat)';
sfbfhitratiomat=(sfbfhitratiomat)';
dbfhitratiomat=(dbfhitratiomat)';
%dousfhitratiomat=(dousfhitratiomat)';
linewidth=2;
xlabel('Memory Size(KB)','Fontsize',12);
ylabel('No. of resets','Fontsize',12);
grid on;
%axis([0,300,0,0.01]);
%set(gca,'XDir','reverse','YDir','normal');
set(gca, 'GridLineStyle' ,'-');
set(gca,'xticklabel',{'1','2','4','8','16','32','64','128','256','512','1024'});
hold on;
%plot(a2sfhitratiomat,'r-','LineWidth',linewidth);
plot(sfbfhitratiomat,'b-','LineWidth',linewidth);
plot(dbfhitratiomat,'r-','LineWidth',linewidth);
legend('SFBF','DBF',2);
hold off;




subplot(3,1,3);

filemat=csvread('F:\\workspace\\SFBF_franztao\\src\\file\\searchtime.txt');
%a2sfhitratiomat=filemat(1:3:30);
sfbfhitratiomat=filemat(1:2:22);
dbfhitratiomat=filemat(2:2:22);
%x=[1,2,4,8,16,32,64,128,256,512,1024];
%dousfhitratiomat=filemat(3:3:30);
%a2sfhitratiomat=(a2sfhitratiomat)';
sfbfhitratiomat=(sfbfhitratiomat)';
dbfhitratiomat=(dbfhitratiomat)';
%dousfhitratiomat=(dousfhitratiomat)';
linewidth=2;
xlabel('Memory Size(KB)','Fontsize',12);
ylabel('Sum Time of insert key','Fontsize',12);
grid on;
set(gca,'xticklabel',{'1','2','4','8','16','32','64','128','256','512','1024'});
%axis([2,4,8,16,32,64,128,256,512,1024]);
%set(gca,'XDir','reverse','YDir','normal');
%set(gca,'xtick',[1,2,4,8,16,32,64,128,256,512,1024])

set(gca, 'GridLineStyle' ,'-');
hold on;
%plot(a2sfhitratiomat,'r-','LineWidth',linewidth);
plot(sfbfhitratiomat,'b-','LineWidth',linewidth);
plot(dbfhitratiomat,'r-','LineWidth',linewidth);
legend('SFBF','DBF',2);
hold off;


