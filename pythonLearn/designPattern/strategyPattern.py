'''
Created on Nov 28, 2017

@author: Taoheng
'''
from abc import ABCMeta, abstractmethod
import types

class Stratege(object):
    __metaclass__ = ABCMeta
    @abstractmethod
    def doOperation(self,num1,num2):
        pass

class OperationAdd(Stratege):
    def doOperation(self,num1,num2):
        return num1+num2

class OperationSubstract(Stratege):
    def doOperation(self,num1,num2):
        return num1-num2

class OperationMultiply(Stratege):
    def doOperation(self,num1,num2):
        return num1*num2

class Context:
    def __init__(self,stratege):
        self.stratege=stratege
    def executeStrategy(self,num1,num2):
        res=self.stratege.doOperation(num1,num2)
        return res
    
def StrategePatternDemo():
    stratege=OperationAdd()
    context=Context(stratege)
    print(context.executeStrategy(10, 5))
    
    stratege=OperationSubstract()
    context=Context(stratege)
    print(context.executeStrategy(10, 5))
    
    stratege=OperationMultiply()
    context=Context(stratege)
    print(context.executeStrategy(10, 5))
StrategePatternDemo()