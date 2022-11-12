cmake_minimum_required(VERSION 3.23)
project(demo_linux_pthread)

set(CMAKE_CXX_STANDARD 14)

add_executable(demo_linux_pthread main.cpp)

# cmake链接添加pthread
# https://stackoverflow.com/questions/1620918/cmake-and-libpthread
find_package(Threads REQUIRED)
if(THREADS_HAVE_PTHREAD_ARG)
    target_compile_options(demo_linux_pthread PUBLIC "-pthread")
endif()
if(CMAKE_THREAD_LIBS_INIT)
    target_link_libraries(demo_linux_pthread "${CMAKE_THREAD_LIBS_INIT}")
endif()