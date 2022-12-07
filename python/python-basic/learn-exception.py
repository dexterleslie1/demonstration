#!/usr/bin/python

# IO exception
try:
 fh = open("testfile.pyc", "w")
 fh.write("Testing line")
except IOError:
 print("IO exception no such file")
else:
 print("Write file successful")
 fh.close()
finally:
 print("Execute finally statement")

# Raise exception
def raiseException():
 raise Exception, "Error message"
pass

try:
 raiseException()
except Exception, err:
 print("Exception occurs " + str(err))
