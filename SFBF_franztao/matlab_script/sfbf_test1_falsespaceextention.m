
subplot(1,3,1);
filemat=csvread('F:\\workspace\\SFBF_franztao\\src\\file\\Extension round.txt');
msfbfmat1=filemat(1:301);
msfbfmat2=filemat(302:602);
msfbfmat3=filemat(603:903);
msfbfmat4=filemat(904:1204);
msfbfmat5=filemat(1205:1505);
mdmat=filemat(1506:1806);
sfbfmat1=msfbfmat1(1:3:300);
sfbfmat2=msfbfmat2(1:3:300);
sfbfmat3=msfbfmat3(1:3:300);
sfbfmat4=msfbfmat4(1:3:300);
sfbfmat5=msfbfmat5(1:3:300);
dmat=mdmat(1:3:300);

sfbfmat1=(sfbfmat1');
sfbfmat2=(sfbfmat2');
sfbfmat3=(sfbfmat3');
sfbfmat4=(sfbfmat4');
sfbfmat5=(sfbfmat5');
dmat=(dmat');

sx1=1:300:30000;
sx2=1:300:30000;
sx3=1:300:30000;
sx4=1:300:30000;
sx5=1:300:30000;
dx=1:300:30000;

linewidth=2;
fontsize=15;
xlabel('Data Set Size','Fontsize',fontsize);
ylabel('Extension round','Fontsize',fontsize);
grid on;
%axis([0,300,0,0.01]);
%set(gca,'XDir','reverse','YDir','normal');
set(gca, 'GridLineStyle' ,'-');
set(gca, 'Fontsize',fontsize);

hold on;
plot(sx1,sfbfmat1,'r-o','LineWidth',linewidth,'MarkerFaceColor','r');
%plot(sfbfmat2,'g-','LineWidth',linewidth);
plot(sx2,sfbfmat3,'c-^','LineWidth',linewidth,'MarkerFaceColor','c');
plot(sx4,sfbfmat4,'m-<','LineWidth',linewidth,'MarkerFaceColor','m');
plot(sx5,sfbfmat5,'b->','LineWidth',linewidth,'MarkerFaceColor','b'); 
plot(dx,dmat,'k-s','LineWidth',linewidth,'MarkerFaceColor','k');
legend('SFBF-1,2,3,4','SFBF-1,1+[1,2]','SFBF-1,1+[1,2,3]','SFBF-1,1,2,2,3,3','DBF',2);
%legend('SFBF-1,2,3,4','SFBF-1,1,2,3','SFBF-1,1+[1,2]','SFBF-1,1+[1,2,3]','SFBF-1,1,2,2,3,3',2);
hold off;

subplot(1,3,2);

filemat=csvread('F:\\workspace\\SFBF_franztao\\src\\file\\Space size of filters.txt');
msfbfmat1=filemat(1:301);
msfbfmat2=filemat(302:602);
msfbfmat3=filemat(603:903);
msfbfmat4=filemat(904:1204);
msfbfmat5=filemat(1205:1505);
mdmat=filemat(1506:1806);


sfbfmat1=msfbfmat1(1:3:300);
sfbfmat2=msfbfmat2(1:3:300);
sfbfmat3=msfbfmat3(1:3:300);
sfbfmat4=msfbfmat4(1:3:300);
sfbfmat5=msfbfmat5(1:3:300);
dmat=mdmat(1:3:300);

sfbfmat1=(sfbfmat1');
sfbfmat2=(sfbfmat2');
sfbfmat3=(sfbfmat3');
sfbfmat4=(sfbfmat4');
sfbfmat5=(sfbfmat5');
dmat=(dmat');

sx1=1:300:30000;
sx2=1:300:30000;
sx3=1:300:30000;
sx4=1:300:30000;
sx5=1:300:30000;
dx=1:300:30000;


linewidth=1;
fontsize=15;
xlabel('Data Set Size','Fontsize',fontsize);
ylabel('Space size of filters','Fontsize',fontsize);
grid on;
%axis([0,300,0,0.01]);
%set(gca,'XDir','reverse','YDir','normal');

set(gca, 'GridLineStyle' ,'-');
set(gca, 'Fontsize',fontsize);

hold on;
plot(sx1,sfbfmat1,'r-o','LineWidth',linewidth,'MarkerFaceColor','r');
%plot(sfbfmat2,'g-','LineWidth',linewidth);
plot(sx3,sfbfmat3,'c-^','LineWidth',linewidth,'MarkerFaceColor','c');
plot(sx4,sfbfmat4,'m-<','LineWidth',linewidth,'MarkerFaceColor','m');
plot(sx5,sfbfmat5,'b->','LineWidth',linewidth,'MarkerFaceColor','b'); 
plot(dx,dmat,'k-s','LineWidth',linewidth,'MarkerFaceColor','k');
legend('SFBF-1,2,3,4','SFBF-1,1+[1,2]','SFBF-1,1+[1,2,3]','SFBF-1,1,2,2,3,3','DBF',2);
hold off;

subplot(1,3,3);

filemat=csvread('F:\\workspace\\SFBF_franztao\\src\\file\\False positive rate.txt');
msfbfmat1=filemat(1:301);
msfbfmat2=filemat(302:602);
msfbfmat3=filemat(603:903);
msfbfmat4=filemat(904:1204);
msfbfmat5=filemat(1205:1505);
mdmat=filemat(1506:1806);
mbmat=filemat(1807:2107);

sfbfmat1=msfbfmat1(1:3:300);
sfbfmat2=msfbfmat2(1:3:300);
sfbfmat3=msfbfmat3(1:3:300);
sfbfmat4=msfbfmat4(1:3:300);
sfbfmat5=msfbfmat5(1:3:300);
dmat=mdmat(1:3:300);
bmat=mbmat(1:3:300);

sx1=1:300:30000;
sx2=1:300:30000;
sx3=1:300:30000;
sx4=1:300:30000;
sx5=1:300:30000;
dx=1:300:30000;
bx=1:300:30000;

searchdatalength=10000;
sfbfmat1=(sfbfmat1')./searchdatalength;
sfbfmat2=(sfbfmat2')./searchdatalength;
sfbfmat3=(sfbfmat3')./searchdatalength;
sfbfmat4=(sfbfmat4')./searchdatalength;
sfbfmat5=(sfbfmat5')./searchdatalength;
dmat=(dmat')./searchdatalength;
bmat=(bmat')./searchdatalength;

linewidth=1;
fontsize=15;
xlabel('Data Set Size','Fontsize',fontsize);
ylabel('False positive rate','Fontsize',fontsize);
grid on;
%axis([0,300,0,0.01]);
%set(gca,'XDir','reverse','YDir','normal');

set(gca, 'GridLineStyle' ,'-');
set(gca, 'Fontsize',fontsize);

hold on;
plot(sx1,sfbfmat1,'r-o','LineWidth',linewidth,'MarkerFaceColor','r');
%plot(sfbfmat2,'g-','LineWidth',linewidth);
plot(sx3,sfbfmat3,'c-^','LineWidth',linewidth,'MarkerFaceColor','c');
plot(sx4,sfbfmat4,'m-<','LineWidth',linewidth,'MarkerFaceColor','m');
plot(sx5,sfbfmat5,'b->','LineWidth',linewidth,'MarkerFaceColor','b'); 
plot(dx,dmat,'k-s','LineWidth',linewidth,'MarkerFaceColor','k');
plot(bx,bmat,'y-*','LineWidth',1);
%legend('SFBF-1,2,3,4','SFBF-1,1,2,3','SFBF-1,1+[1,2]','SFBF-1,1+[1,2,3]','SFBF-1,1,2,2,3,3',2);
legend('SFBF-1,2,3,4','SFBF-1,1+[1,2]','SFBF-1,1+[1,2,3]','SFBF-1,1,2,2,3,3','DBF','BBF',2);
hold off;






