-- https://www.lua.org/manual/5.1/manual.html
-- https://www.cnblogs.com/aibox222/p/9052170.html
-- Issues an error when the value of its argument v is false (i.e., nil or false); otherwise, returns all its arguments. message is an error message; when absent, it defaults to "assertion failed!"

varN1 = 1
assert(varN1 == 1, "varN1~=1")