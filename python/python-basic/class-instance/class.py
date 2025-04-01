import json


class Student:
    # 类属性: 所有类或者类对象共享一个类属性
    classProperty = "类属性classProperty"

    # 构造函数
    def __init__(self, name="Dexter", age=30):
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


class ClassPrivateMember:
    def __init__(self, a):
        self.__a = a
        pass

    def show(self):
        print("私有成员self.__a=", self.__a)

    pass


# 单继承
class Person(object):
    def __init__(self, name, age):
        self.name = name
        self.age = age

    def info(self):
        print("我是{0}，今年{1}岁".format(self.name, self.age))

    def __str__(self):
        # return f"名称：{self.name}，年龄：{self.age}"
        # json_dumps(dict)时，如果dict包含有汉字，一定加上ensure_ascii=False。否则按参数默认值True，意思是保证dumps之后的结果里所有的字符都能够被ascii表示，汉字在ascii的字符集里面，因此经过dumps以后的str里，汉字会变成对应的unicode。
        # https://zhuanlan.zhihu.com/p/37504880
        return json.dumps(self.__dict__, indent=4, ensure_ascii=False)


class StudentSub(Person):
    def __init__(self, name, age, score):
        # 定义子类时必须在其构造函数中调用父类构造函数
        super().__init__(name, age)

        self.score = score

    # 方法重写
    def info(self):
        # 调用父级函数
        super().info()
        print("我是{0}，今年{1}岁，考试{2}分".format(self.name, self.age, self.score))


class TeacherSub(Person):
    def __init__(self, name, age, title):
        super().__init__(name, age)
        self.title = title

    # 方法重写
    def info(self):
        super().info()
        print("教师职称：", self.title)


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


def test_method():
    print("测试动态绑定方法")
    pass

# https://blog.csdn.net/weixin_42147967/article/details/123394838
from abc import abstractmethod, ABCMeta


class Base(metaclass=ABCMeta):
    @abstractmethod
    def method1(self):
        pass

    def method2(self):
        print("抽象类Base方法2实现")

    pass


class Children(Base):
    # 如果不实现父类抽象方法会如下错误
    # TypeError: Can't instantiate abstract class Children with abstract methods method1
    def method1(self):
        print("子类Children方法1实现")

    pass


if __name__ == "__main__":
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

    # 使用带参数构造函数创建对象
    student = Student("Dexterleslie")
    student.info()

    student = Student("Dexterleslie", 31)
    student.info()
    student.infoSex()

    print()
    print("类操作".center(50, "-"))
    # 类属性访问
    print("类属性Student.classProperty:{0}".format(Student.classProperty))
    # NOTE: 只能使用Student.classProperty修改类属性，
    # 不能使用student.classProperty修改，否则是给student对象动态绑定属性
    Student.classProperty = "1"
    print("使用Student.classProperty修改后的student.classProperty：", student.classProperty)
    print("使用Student.classProperty修改后的Student.classProperty：", Student.classProperty)

    # 类方法和静态方法调用，目前理解静态方法和类方法区别不大
    # https://blog.csdn.net/yin20100522/article/details/94455982
    Student.classMethod()
    Student.staticMethod()

    print()
    print("为对象动态绑定属性和方法".center(50, "-"))
    student1 = Student()
    student2 = Student()
    student1.prop1 = "动态绑定属性"
    print("student1动态绑定属性prop1=", student1.prop1)
    try:
        print("student2动态绑定属性prop1=", student2.prop1)
        assert 1 != 1
    except AttributeError:
        # student2没有prop2动态绑定prop1属性
        pass

    # 动态绑定方法
    student1.test_method = test_method
    student1.test_method()
    try:
        student2.test_method()
        assert 1 != 1
    except AttributeError:
        # student2没有prop2动态绑定prop1属性
        pass

    print()
    print("类私有成员".center(50, "-"))
    classPrivateMember = ClassPrivateMember(10)
    classPrivateMember.show()

    # 单继承和多继承
    print()
    print("类继承".center(50, "-"))
    student = StudentSub("Dexter1", 20, 98)
    student.info()
    varC = C()
    varC.methodA()
    varC.methodB()

    # 方法重写
    print()
    print("方法重写".center(50, "-"))
    student = StudentSub("dexter2", 21, 99)
    student.info()
    teacher = TeacherSub("teacher1", 33, "特级教师")
    teacher.info()

    print()
    print("抽象类".center(50, "-"))
    children = Children()
    children.method1()
    children.method2()

    # https://bobbyhadz.com/blog/python-print-object-as-string#:~:text=Use%20the%20__str__()%20method,string%20representation%20of%20the%20object.
    # https://bobbyhadz.com/blog/python-print-object-as-json
    print()
    print("以字符串或者JSON形式打印对象".center(50, "-"))
    student = StudentSub("中国", 32, 89)
    print(student)

    pass
