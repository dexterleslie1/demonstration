SET(HELLOLIB_SRC hello_func.c)

# SHARED: 动态链接库
ADD_LIBRARY(hello_func SHARED ${HELLOLIB_SRC})
SET_TARGET_PROPERTIES(hello_func PROPERTIES OUTPUT_NAME "hello_func")
SET_TARGET_PROPERTIES(hello_func PROPERTIES CLEAN_DIRECT_OUTPUT 1)

# STATIC: 静态链接库
ADD_LIBRARY(hello_func_static STATIC ${HELLOLIB_SRC})
SET_TARGET_PROPERTIES(hello_func_static PROPERTIES OUTPUT_NAME "hello_func")
SET_TARGET_PROPERTIES(hello_func_static PROPERTIES CLEAN_DIRECT_OUTPUT 1)

INSTALL(FILES hello_func.h DESTINATION include/hello)
INSTALL(TARGETS hello_func hello_func_static LIBRARY DESTINATION lib ARCHIVE DESTINATION lib)
# Ubuntu 中需要发布到 /usr/lib，否则会报告 so 文件不存在错误
INSTALL(TARGETS hello_func hello_func_static LIBRARY DESTINATION ../lib ARCHIVE DESTINATION ../lib)
# 动态和静态库发布到/usr/lib64目录，否则调用程序会报告找不到so库错误
INSTALL(TARGETS hello_func hello_func_static LIBRARY DESTINATION ../lib64 ARCHIVE DESTINATION ../lib64)

