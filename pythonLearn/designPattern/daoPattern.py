'''
Created on Dec 2, 2017

@author: Taoheng
'''


class Student(object):
    def __init__(self,name,rollNo):
        self.__name=name
        self.__rollNo=rollNo
        
    def getName(self):
        return self.__name
    
    def setName(self,name):
        self.__name=name

    def getRollNo(self):
        return self.__rollNo
    
    def setRollNo(self,rollNo):
        self.__rollNo=rollNo


class StudentDao(object):
    def getAllStudents(self):
        pass
    def getStudent(self,rollNo):
        pass
    def updateStudent(self,student):
        pass
    def deleteStudent(self,student):
        pass

class StudentDaoImpl(StudentDao):
    def __init__(self):
        self.__students=[]
        student1=Student("Robert",0)
        student2=Student("John",1)
        self.__students.append(student1)
        self.__students.append(student2)
    
    def deleteStudent(self,student):
        self.__students.remove(student.getRollNo())
        print("Student: Roll No " ,student.getRollNo() ,", deleted from database")
        
    def getAllStudents(self):
        return self.__students
    
    def getStudent(self,rollNo):
        return self.__students[rollNo]
    
    def updateStudent(self,student):
        self.__students[student.getRollNo()].setName(student.getName())
        print("Student: Roll No " , student.getRollNo(),", updated in the database")
def DaoPatternDemo():
    studentDao=StudentDaoImpl()
    for student in studentDao.getAllStudents():
        print("Student: [RollNo : ",student.getRollNo(),", Name : "+student.getName()+" ]")
    
    student =studentDao.getAllStudents()[0]
    student.setName("Michael")
    studentDao.updateStudent(student)
    
    student =studentDao.getAllStudents()[0]
    print("Student: [RollNo : ",student.getRollNo(),", Name : "+student.getName()+" ]")
    
DaoPatternDemo()