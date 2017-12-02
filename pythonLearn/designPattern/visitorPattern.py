'''
Created on Dec 2, 2017

@author: Taoheng
'''


class ComputerPart(object):

    def accept(self, computerPartVisitor):
        pass


class KeyBoard(ComputerPart):

    def accept(self, computerPartVisitor):
        computerPartVisitor.visit(self)

        
class Monitor(ComputerPart):

    def accept(self, computerPartVisitor):
        computerPartVisitor.visit(self)


class Mouse(ComputerPart):

    def accept(self, computerPartVisitor):
        computerPartVisitor.visit(self)

        
class Computer(ComputerPart):
    def __init__(self):
        self.__parts=[Mouse(),KeyBoard(),Monitor()]
        
    def accept(self, computerPartVisitor):
        for i in range(self.__parts.__len__()):
            self.__parts[i].accept(computerPartVisitor)
            
        computerPartVisitor.visit(self)
        
def ComputerPartVisitor():
    def visit(self,computer):
        pass
    
def VisitorPatternDemo():
    computer = Computer()
