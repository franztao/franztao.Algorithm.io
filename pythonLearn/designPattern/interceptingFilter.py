'''
Created on Dec 2, 2017

@author: Taoheng
'''


class Target(object):
    def execute(self,request):
        print("Executing request: ",request)

class AuthenticationFilter(object):
    def execute(self,request):
        print("Authenticating request: ",request)

class DebugFilter(object):
    def execute(self,request):
        print("request log: ",request)
class FilterChain(object):
    def __init__(self):
        self.__filters=[];
    
    def addFilter(self,filte):
        self.__filters.append(filte)
        
    def setTarget(self,target):
        self.__target=target
        
    def execute(self,request):
        for filte in self.__filters:
            filte.execute(request)
        self.__target.execute(request)


class FilterManager(object):
    
    def __init__(self, target):
        self.__filterChain=FilterChain()
        self.__filterChain.setTarget(target)
        
    def setFilter(self,filte):
        self.__filterChain.addFilter(filte)
        
    def filterRequest(self,request):
        self.__filterChain.execute(request)




class Client(object):
    def setFilterManager(self,filterManager):
        self.__filterManager=filterManager
        
    def sendRequest(self,request):
        self.__filterManager.filterRequest(request) 


def InterceptingFilterDemo():
    target=Target()
    filterManager=FilterManager(target)
    
    authenticationFilter=AuthenticationFilter()
    filterManager.setFilter(authenticationFilter)
    
    debugFilter=DebugFilter()
    filterManager.setFilter(debugFilter)
    
    client=Client()
    client.setFilterManager(filterManager)
    client.sendRequest("HOME")
    
InterceptingFilterDemo()