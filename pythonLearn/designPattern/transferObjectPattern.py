'''
Created on Dec 1, 2017

@author: Taoheng
'''
from numpy import roll


class StudentVo(object):
    
    def __init__(self, name, rollNo):
        self.__name = name
        self.__rollNo = rollNo
    
    def getName(self):
        return self.__name
    
    def setName(self, name):
        self.__name = name
        
    def getRollNo(self):
        return self.__rollNo
    
    def setRollNo(self, rollNo):
        self.__rollNo = rollNo


class StudentBo(object):

    def __init__(self):
        self.__students = []
        student1 = StudentVo("Robert", 0)
        student2 = StudentVo("John", 1)
        self.__students.append(student1)
        self.__students.append(student2)
        
    def deleteStudent(self, student):
        self.__students.remove(student.getRollNo())
        print("Student: Roll No ", student.getRollNo(), ", deleted from database")
    
    def getAllStudents(self):
        return self.__students

    def getStudent(self, rollNo):
        return self.__students[rollNo]
    
    def updateStudent(self, student):
        self.__students[student.getRollNo()].setName(student.getName())
        print("Student: Roll No " , student.getRollNo(), ", updated in the database")


def TransferObjectPatternDemo():
    studentBusinessObject = StudentBo()
    for student in studentBusinessObject.getAllStudents():
        print("Student: [RollNo : ",student.getRollNo(),", Name : ",student.getName()," ]")
        
    student=studentBusinessObject.getAllStudents()[0]
    student.setName("Michael")
    studentBusinessObject.updateStudent(student)
    
    student=studentBusinessObject.getAllStudents()[0]
    print("Student: [RollNo : ",student.getRollNo(),", Name : ",student.getName()," ]")
    
    
    
TransferObjectPatternDemo()
