'''
Created on Nov 27, 2017

@author: Taoheng
'''
from abc import ABCMeta, abstractmethod


class AbstractCustomer(object):
    __metaclass__ = ABCMeta
    def __init__(self):
        self.color = 'black'
    @abstractmethod
    def isNil(self):
        pass
    def getName(self):
        pass
class  RealCustomer(AbstractCustomer):
    def __init__(self, name):
        self.name = name
    def isNil(self):
        return False;
    def getName(self):
        return self.name;
class  NullCustomer(AbstractCustomer):
    def isNil(self):
        return False;
    def getName(self):
        return "Not Available in Customer Database";
class CustomerFactory:
    def __init__(self):
        self.names = ['Rob', 'Joe', 'Julie']
    def getCustomer(self, name):
        for i in range(len(self.names)):
            if self.names[i] == name:
                return RealCustomer(name)
        return NullCustomer()
#             if names[i]
            
def NUllPatternDemo():
    customer1 = CustomerFactory()
    customer1 = customer1.getCustomer(name='Rob')
    customer2 = CustomerFactory()
    customer2 = customer2.getCustomer(name='Bob')
    customer3 = CustomerFactory()
    customer3 = customer3.getCustomer(name='Julie')
    customer4 = CustomerFactory()
    customer4 = customer4.getCustomer(name='Laura')
    
    print('Customers')
    print(customer1.getName())
    print(customer2.getName())
    print(customer3.getName())
    print(customer4.getName())
