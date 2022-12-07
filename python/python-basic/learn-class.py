#!/usr/bin/python

class Employee:
	InstanceCounter = 0
	
	# Protect attribute
	_attr_protect = 0
	# Private attribute
	__attr_private = 0
	
	def __init__(self, name, salary=500):
		self.name = name
		self.salary = salary
		Employee.InstanceCounter = Employee.InstanceCounter+1
	
	def display(self):
		print("Name=" + self.name + ",Salary=" + str(self.salary))
	
	def __del__(self):
		print("Deconstruct instance Employee name=" + self.name + ",salary=" + str(self.salary))
	
	@classmethod
	def displayTotalInstance(cls):
		print("Total Employee instance is " + str(cls.InstanceCounter))
	
	def increaseAttrPrivate(self):
		self.__attr_private = self.__attr_private+1
	
	def displayAttrPrivate(self):
		print("Instance " + str(self) + " attribute __attr_private value is " + str(self.__attr_private))


# Construct and deconstruct Employee instance
employee = Employee("Dexter", 1000)
employee.display()
del employee

employee = Employee("Dexter2")
employee.display()
del employee

# Class method
Employee.displayTotalInstance()

# Protect and private attribute
employee = Employee("Dexter3")
employee._attr_protect = employee._attr_protect+1
print("Instance " + str(employee) + " attribute _attr_protect value is " + str(employee._attr_protect))

employee.increaseAttrPrivate()
employee.displayAttrPrivate()


# Class extend
class Father(object):
	def __init__(self, name):
		self.name = name
		print("Father constructor is invoked")
	
	def displayName(self):
		print("Name is " + self.name)


class Son(Father):
	def __init__(self, name, age):
		super(Son, self).__init__(name)
		self.age = age
	
	def displayAge(self):
		print("Age is " + str(self.age))


son = Son("Dexter's son", 1)
son.displayName()
son.displayAge()
