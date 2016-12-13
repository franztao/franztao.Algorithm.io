
subplot(1,2,1);

querycputimefilemat=csvread('F:\\workspace\\SFBF_franztao\\src\\file\\Querying CPU time without shifting.txt');
msfbfmat1=querycputimefilemat(1:301);
msfbfmat2=querycputimefilemat(302:602);
msfbfmat3=querycputimefilemat(603:903);
msfbfmat4=querycputimefilemat(904:1204);
msfbfmat5=querycputimefilemat(1205:1505);
mdmat=querycputimefilemat(1506:1806);
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

sfbfmat1=spline(sx1,sfbfmat1,sx1);
sfbfmat2=spline(sx2,sfbfmat2,sx2);
sfbfmat3=spline(sx3,sfbfmat3,sx3);
sfbfmat4=spline(sx4,sfbfmat4,sx4);
sfbfmat5=spline(sx5,sfbfmat5,sx5);
dmat=spline(dx,dmat,dx);

linewidth=1;
fontsize=15;
%title('Query Time without shifting','Fontsize',12);
xlabel('Data Set Size','Fontsize',fontsize);
ylabel('CPU Querying time','Fontsize',fontsize);
grid on;
set(gca, 'GridLineStyle' ,'-');
set(gca, 'Fontsize',fontsize);
hold on;
plot(sx1,sfbfmat1,'r-o','LineWidth',linewidth,'MarkerFaceColor','r');
%plot(sfbfmat2,'g-','LineWidth',linewidth);
plot(sx3,sfbfmat3,'c-^','LineWidth',linewidth,'MarkerFaceColor','c');
plot(sx4,sfbfmat4,'m-<','LineWidth',linewidth,'MarkerFaceColor','m');
plot(sx5,sfbfmat5,'b->','LineWidth',linewidth,'MarkerFaceColor','b'); 
%plot(dx,dmat,'k-s','LineWidth',linewidth,'MarkerFaceColor','k');  
legend('SFBF-1,2,3,4-direct','SFBF-1,1+[1,2]-direct','SFBF-1,1+[1,2,3]-direct','SFBF-1,1,2,2,3,3-direct','DBF',2);
hold off;


subplot(1,2,2);

querycputimefilemat=csvread('F:\\workspace\\SFBF_franztao\\src\\file\\Querying CPU time with shifting.txt');
msfbfmat1=querycputimefilemat(1:301);
msfbfmat2=querycputimefilemat(302:602);
msfbfmat3=querycputimefilemat(603:903);
msfbfmat4=querycputimefilemat(904:1204);
msfbfmat5=querycputimefilemat(1205:1505);
mdmat=querycputimefilemat(1506:1806);
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

sfbfmat1=spline(sx1,sfbfmat1,sx1);
sfbfmat2=spline(sx2,sfbfmat2,sx2);
sfbfmat3=spline(sx3,sfbfmat3,sx3);
sfbfmat4=spline(sx4,sfbfmat4,sx4);
sfbfmat5=spline(sx5,sfbfmat5,sx5);
dmat=spline(dx,dmat,dx);

linewidth=1;
fontsize=15;
%title('Query Time without shifting','Fontsize',12);
xlabel('Data Set Size','Fontsize',fontsize);
ylabel('CPU Querying time','Fontsize',fontsize);
grid on;
set(gca, 'GridLineStyle' ,'-');
set(gca, 'Fontsize',fontsize);
hold on;
plot(sx1,sfbfmat1,'r-o','LineWidth',linewidth,'MarkerFaceColor','r');
%plot(sfbfmat2,'g-','LineWidth',linewidth);
plot(sx3,sfbfmat3,'c-^','LineWidth',linewidth,'MarkerFaceColor','c');
plot(sx4,sfbfmat4,'m-<','LineWidth',linewidth,'MarkerFaceColor','m');
plot(sx5,sfbfmat5,'b->','LineWidth',linewidth,'MarkerFaceColor','b'); 
%plot(dx,dmat,'k-s','LineWidth',linewidth,'MarkerFaceColor','k');  

legend('SFBF-1,2,3,4]-shift','SFBF-1,1+[1,2]-shift','SFBF-1,1+[1,2,3]]-shift','SFBF-1,1,2,2,3,3]-shift','DBF',2);
hold off;