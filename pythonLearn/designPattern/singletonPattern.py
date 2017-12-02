'''
Created on Nov 12, 2017

@author: Taoheng
'''
from bs4.tests.test_docs import __metaclass__

# 使用__new__
# __init__不是Python对象的构造方法,__init__只负责初始化实例对象,在调用__init__方法之前,会首先调用__new__方法生成对象,可以认为__new__方法充当了构造方法的角色。所以可以在__new__中加以控制,使得某个类只生成唯一对象。具体实现时可以实现一个父类,重载__new__方法,单例类只需要继承这个父类就好。
# class Singleton(object):
#     def __new__(cls,*args,**kwargs):
#         if not hasattr(cls,'_instance'):
#             cls._instance=super(Singleton,cls).__new__(cls,*args,**kwargs)
#         return cls._instance
# class Foo(Singleton):
#     a=1
    
# 使用类装饰器
def singleton(cls):
    _instance=None
    def _wrapper(*args,**kwargs):
        if _instance is None:
            _instance=cls(*args,**kwargs)
        return _instance
    return _wrapper


class Singleton(type):
    def __init__(self,name,bases,attrs):
        super(Singleton,self).__init__(name,bases,attrs)
        self._instance=None
    def __call__(self,*args,**kwargs):
        if self._instance is None:
            self._instance=super(Singleton,self).__call__(*args,**kwargs)
        return self._instance
class Foo(object):
    __metaclass__=Singleton
    