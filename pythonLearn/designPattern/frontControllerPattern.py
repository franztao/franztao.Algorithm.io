'''
Created on Nov 30, 2017

@author: Taoheng
'''


class Dispatcher(object):
    pass


class FrontController(object):
    def __init__(self):
        self.__dispatcher=Dispatcher()
    
    def __isAuthenticUser(self):
        print("User is authenticated successfully.")
    
    def __trackRequest(self,request):
        print("Page requested: ",request)
        
    def dispa


def FrontControllerPatternDemo():
    frontController=FrontController()