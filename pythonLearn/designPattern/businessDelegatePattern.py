'''
Created on Nov 30, 2017

@author: Taoheng
'''


class BusinessService(object):
    def doProcessing(self):
        pass


class EJBService(BusinessService):
    def doProcessing(self):
        print("Processing task by invoking EJB Service")


class JMSService(BusinessService):
    def doProcessing(self):
        print("Processing task by invoking JMS  Service")


class BusinessLookUp(object):
    def getBusinessService(self, serviceType):
        if serviceType.lower() == "EJB".lower():
            return EJBService()
        else:
            return JMSService()

class BusinessDelegate(object):
    def __init__(self):
        self.__lookupService = BusinessLookUp()
    def setServiceType(self,serviceType):
        self.__serviceType=serviceType
    def doTask(self):
        self.__businessService=self.__lookupService.getBusinessService(self.__serviceType)    
        self.__businessService.doProcessing()
            

class Client(object):
    
    def __init__(self, businessDelegate):
        self.__businessService=businessDelegate
        
    def doTask(self):
        self.__businessService.doTask()


def BusinessDelegatePatternDemo():
    businessDelegate = BusinessDelegate()
    businessDelegate.setServiceType("EJB")
    
    client=Client(businessDelegate)
    client.doTask()
    
    businessDelegate.setServiceType("JMS")
    client.doTask()
    
BusinessDelegatePatternDemo()
