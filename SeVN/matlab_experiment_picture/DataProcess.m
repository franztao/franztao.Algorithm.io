function [dataVectorSet] =DataProcess(FileName)
    dataVector=[];
    global AlgNum
    global AlgName
    global FileAbsolutePath
    global ExperimentPicturePlotNumber
    global ExperimentTimes
    for i=1:1:AlgNum
        data=load([FileAbsolutePath,FileName,strtrim(AlgName(i,:)),'.txt']);
        dataVector=[dataVector;data'];
    end
    [r,c]=size(dataVector);
    experimentData=zeros(AlgNum,ExperimentPicturePlotNumber);
    
    for i=1:1:AlgNum
        for j=1:1:c
            experimentData(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentData(i,mod(j-1,ExperimentPicturePlotNumber)+1)+dataVector(i,j);
        end
    end
    
    for i=1:1:AlgNum
        for j=1:1:ExperimentPicturePlotNumber
            experimentData(i,mod(j-1,ExperimentPicturePlotNumber)+1)= experimentData(i,mod(j-1,ExperimentPicturePlotNumber)+1)*1.0/ExperimentTimes;
        end
    end
    dataVectorSet=experimentData;
end