'''
Created on Dec 2, 2017

@author: Taoheng
'''


class DependentObject1(object):
    def setData(self,data):
        self.__data=data
        
    def getData(self):
        return self.__data

class DependentObject2(object):
    def setData(self,data):
        self.__data=data
        
    def getData(self):
        return self.__data


class CoarseGrainedObject(object):
    do1=DependentObject1()
    do2=DependentObject2()
    
    def setData(self,data1,data2):
        self.do1.setData(data1)
        self.do2.setData(data1)
        
    def getData(self):
        return [self.do1.getData(),self.do2.getData()]
    
class CompositeEntity(object):
    cgo=CoarseGrainedObject()
    
    def setData(self,data1,data2):
            self.cgo.setData(data1, data2)
    
    def getData(self):
        return self.cgo.getData()
    
            
class Client(object):
    compositeEntity=CompositeEntity()
    
    def printData(self):
        for i in range(self.compositeEntity.getData().__len__()):
            print("Data: ",self.compositeEntity.getData()[i])
            
    def setData(self,data1,data2):
        self.compositeEntity.setData(data1, data2)


def CompositeEntityPatternDemo():
    client=Client()
    client.setData("First Test", "data2")
    client.printData()
    client.setData("Second Test","data2")
    client.printData()
    
CompositeEntityPatternDemo()