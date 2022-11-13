# 下面C参数声明编译c语言程序
# https://stackoverflow.com/questions/18099901/how-compile-only-c-with-cmake
PROJECT(HELLOWORLD C)

ADD_SUBDIRECTORY(src bin)

# 默认安装路径/usr/local/
# https://www.bilibili.com/video/BV1vR4y1u77h?p=6&spm_id_from=pageDriver&vd_source=872f70ee43293e5dbba94e3aecc154d2
INSTALL(FILES COPYRIGHT README DESTINATION share/doc/cmake/)
INSTALL(PROGRAMS build/bin/helloworld DESTINATION bin/)
INSTALL(DIRECTORY doc/ DESTINATION share/doc/cmake/)
