'''
Created on Dec 1, 2017

@author: Taoheng
'''
from abc import abstractmethod

class Service(object):
    def getName(self):
        pass
    def execute(self):
        pass
    
    
class Service1(Service):
    @abstractmethod
    def execute(self):
        print("Executing Service1")
    def getName(self):
        return "Service1"
        
        
class Service2(Service):
    @abstractmethod
    def execute(self):
        print("Executing Service2")
    def getName(self):
        return "Service2"
        
             


class Cache(object):
    def __init__(self):
        self.__services=[]
        
    def getService(self,serviceName):
        for service in self.__services:
            if service.getName().lower()==serviceName.lower():
                print("Returning cached  ",serviceName," object")
                return service
        return None

    def addService(self,newService):
        exists=False
        for service in self.__services:
            if service.getName().lower()==newService.getName().lower():
                exists=True
            
        if exists is False:
            self.__services.append(newService)
            
            
class InitialContext(object):
    def lookup(self,jndiName):
        if jndiName.lower()=="SERVICE1".lower():
            print("Looking up and creating a new Service1 object")
            return Service1()
        elif jndiName.lower()=="SERVICE2".lower():
            print("Looking up and creating a new Service2 object")
            return Service2()
        else:
            print("None")
            return None
    

class ServiceLocator(object):
    cache=Cache()
    
    def getService(self,jndiName):
        service=self.cache.getService(jndiName)
        if service is not None:
            return service
        
        context=InitialContext()
        service1=context.lookup(jndiName)
        self.cache.addService(service1)
        return service1
        
    
    


def ServiceLocatorPatternDemo():
    serviceLocator=ServiceLocator()
    
    service=serviceLocator.getService("service1")
    service.execute()
    
    service=serviceLocator.getService("service1")
    service.execute()
    
    service=serviceLocator.getService("service2")
    service.execute()
    
    service=serviceLocator.getService("service1")
    service.execute()
    
    service=serviceLocator.getService("service2")
    service.execute()
    
ServiceLocatorPatternDemo()