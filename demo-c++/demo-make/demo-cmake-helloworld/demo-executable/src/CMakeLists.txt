# 包含hello库的头文件hello_func.h路径，否则编译时会报告不能找到头文件错误
INCLUDE_DIRECTORIES(/usr/local/include/hello)

ADD_EXECUTABLE(helloworld main.c)

# 动态库方式链接
TARGET_LINK_LIBRARIES(helloworld libhello_func.so)
# 静态库方式链接
#TARGET_LINK_LIBRARIES(helloworld libhello_func.a)

