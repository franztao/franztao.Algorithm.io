'''
Created on Nov 30, 2017

@author: Taoheng
'''
from idlelib.textView import view_file


class Student(object):
#     def __init__(self,rollNo,name):
#         self.rollNo=rollNo
#         self.name=name
    
    
    def getRollNo(self):
        return self.rollNo
    def setRollNo(self,rollNo):
        self.rollNo=rollNo
    def getName(self):
        return self.name
    def setName(self,name):
        self.name=name
        
def retriveStudentFromDatabase():
    student=Student()
    student.setName("Robert")
    student.setRollNo("10")
    return student


class StudentView(object):
    def printStudentDetails(self,studentName,studentRollNo):
        print("Student: \n","Name: ",studentName,"\nRoll No:",studentRollNo)


class StudentController(object):
    
    def __init__(self, model, view):
        self.model=model
        self.view=view
        
    def setStudentName(self,name):
        self.model.setName(name)
    
    def getStudentName(self):
        return self.model.getName()
    
    def setStudentRollNo(self,RollNo):
        self.model.setRollNo(RollNo)
    
    def getStudentRollNo(self):
        return self.model.getRollNo()
    
    def updateView(self):
        self.view.printStudentDetails(self.model.getName(),self.model.getRollNo())
    
def MVCPatternDemo():
    model=retriveStudentFromDatabase()
    view=StudentView()
    controller=StudentController(model,view)
    controller.updateView()
    
    controller.setStudentName("John")
    controller.updateView()
    
MVCPatternDemo()