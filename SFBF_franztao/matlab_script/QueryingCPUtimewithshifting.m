querycputimefilemat=csvread('F:\\workspace\\SFBF_franztao\\src\\file\\Querying CPU time with shifting.txt');
sfbfmat1=querycputimefilemat(1:301);
sfbfmat2=querycputimefilemat(302:602);
sfbfmat3=querycputimefilemat(603:903);
sfbfmat4=querycputimefilemat(904:1204);
sfbfmat5=querycputimefilemat(1205:1505);
dmat=querycputimefilemat(1506:1806);
sfbfmat1=(sfbfmat1')./1000.0;
sfbfmat2=(sfbfmat2')./1000.0;
sfbfmat3=(sfbfmat3')./1000.0;
sfbfmat4=(sfbfmat4')./1000.0;
sfbfmat5=(sfbfmat5')./1000.0;
dmat=(dmat')./1000.0;
linewidth=1;
title('Query Time with shifting');
xlabel('Data Set Size');
ylabel('Querying CPU time');
grid on;
hold on;
plot(sfbfmat1,'r-','LineWidth',linewidth);
plot(sfbfmat2,'g-','LineWidth',linewidth);
plot(sfbfmat3,'c-','LineWidth',linewidth);
plot(sfbfmat4,'m-','LineWidth',linewidth);
plot(sfbfmat5,'b-','LineWidth',linewidth); 
plot(dmat,'k-','LineWidth',1);
legend('SFBF-1,2,3,4','SFBF-1,1,2,3','SFBF-1,1+[1,2]','SFBF-1,1+[1,2,3]','SFBF-1,1,2,2,3,3','DBF',2);
hold off;


