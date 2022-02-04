
class Student:
    # 类属性
    classProperty = "类属性classProperty"

    # 构造函数
    def __init__(self, name = "Dexter", age = 30):
        self.name = name
        self.age = age
        # 私有对象属性
        self.__sex = "男"

    # 实例方法
    def info(self):
        print("我叫{0}，今年{1}岁".format(self.name, self.age))

    def infoSex(self):
        print("我的性别：{0}".format(self.__sex))

    # 静态方法
    @staticmethod
    def staticMethod():
        print("你调用了静态方法")

    # 类方法
    @classmethod
    def classMethod(cls):
        print("你调用了类方法，cls:{0}".format(cls))

    pass

print("类对象信息".center(50, "-"))
print("id:{0},type:{1},instance:{2}".format(id(Student), type(Student), Student))

print()
print("对象操作".center(50, "-"))
# 创建对象
student = Student()
# 调用实例方法
student.info()
# 另外一种方法调用对象方法，等价于student.info()
Student.info(student)
# 打印对象属性
print("你是{0}，今年{1}岁".format(student.name, student.age))

student = Student("Dexterleslie")
student.info()

student = Student("Dexterleslie", 31)
student.info()
student.infoSex()

print()
print("类操作".center(50, "-"))
# 类属性访问
print("类属性Student.classProperty:{0}".format(Student.classProperty))

# 类方法和静态方法调用
Student.classMethod()
Student.staticMethod()

print()
print("类继承".center(50, "-"))

# 单继承
class Person(object):
    def __init__(self, name, age):
        self.name = name
        self.age = age

    def info(self):
        print("我是{0}，今年{1}岁".format(self.name, self.age))

class StudentSub(Person):
    def __init__(self, name, age, score):
        super().__init__(name, age)
        self.score = score

    def info(self):
        print("我是{0}，今年{1}岁，考试{2}分".format(self.name, self.age, self.score))

student = StudentSub("Dexter1", 20, 98)
student.info()

# 多继承
class A:
    def methodA(self):
        print("你调用方法A")

    pass

class B:
    def methodB(self):
        print("你调用方法B")

    pass

class C(A, B):

    pass

varC = C()
varC.methodA()
varC.methodB()