#include <iostream>
#include <unistd.h>
#include <pthread.h>
#include <string>
#include <cstring>
#include <sys/timeb.h>
#include <time.h>
#include <assert.h>

using namespace std;

/**
 * 演示pthread_self函数使用
 */
void test_pthread_self() {
    pid_t pid;
    pthread_t tid;

    pid = getpid();
    tid = pthread_self();

    printf("pid=%d,tid=0x%x\n", pid, tid);
}

void* test_print_pid_and_pthread_info(void *args) {
    pid_t pid;
    pthread_t tid;

    pid = getpid();
    tid = pthread_self();

    printf("%s pid=%d,tid=0x%x\n", (char *)args, pid, tid);

    return NULL;
}

struct Student {
    string name;
    int age;
};

/**
 * 测试传递结构类型参数给线程
 * @param args
 * @return
 */
void* test_pthread_passing_struct_type_argument(void *args) {
    Student *student = (Student *)args;
    printf("学生名称：%s，年龄：%d\n", student->name.c_str(), student->age);
}

/**
 * 测试当主线程退出时子线程马上退出
 * @param args
 * @return
 */
void* test_pthread_exit_immediate_when_main_thread_exit(void *args) {
    sleep(2);
    printf("来自test_pthread_exit_immediate_when_main_thread_exit打印\n");
}

void* test_child_thread_exit_handler(void *arg) {
    char *pChar = (char *)arg;
    if(strcmp("1", pChar) == 0) {
        // 不会让主线程即时退出
        printf("test_child_thread_exit_handler使用return方式退出子线程\n");
        return NULL;
    } else if(strcmp("2", pChar) == 0) {
        // 不会让主线程即时退出
        printf("test_child_thread_exit_handler使用pthread_exit方式退出子线程\n");
        pthread_exit((void *)2);
    } else if(strcmp("3", pChar) == 0) {
        // 会导致主线程马上退出
        printf("test_child_thread_exit_handler使用exit方式退出子线程\n");
        exit(3);
    }
}
/**
 * 用于测试子线程不同退出方式对主线程的影响
 */
void test_child_thread_exit(char *arg) {
    pthread_t tid;
    int result = pthread_create(&tid, NULL, test_child_thread_exit_handler, arg);
    if(result != 0) {
        printf("创建线程失败，创建返回: %d\n", result);
        return;
    }

    sleep(1);
    printf("输出来自主线程test_child_thread_exit\n");
}

void* test_pthread_join_handler(void *arg) {
    sleep(2);
}

int main(int argc, char *argv[]) {
    // 测试pthread_self函数
    test_pthread_self();

    // 测试pthread_create函数
    test_print_pid_and_pthread_info((void *)"main thread");
    pthread_t tid;
    int result = pthread_create(&tid, NULL, test_print_pid_and_pthread_info, (void *)"new thread");
    if(result != 0) {
        printf("创建线程失败，创建返回: %d", result);
        return 0;
    }
    sleep(1);

    // 给线程传递结构类型参数
    Student student;
    student.name = "张三";
    student.age = 19;
    result = pthread_create(&tid, NULL, test_pthread_passing_struct_type_argument, &student);
    if(result != 0) {
        printf("创建线程失败，创建返回: %d", result);
        return 0;
    }
    sleep(1);

    // 主线程退出会导致子线程也马上退出
    result = pthread_create(&tid, NULL, test_pthread_exit_immediate_when_main_thread_exit, NULL);
    if(result != 0) {
        printf("创建线程失败，创建返回: %d", result);
        return 0;
    }

    // 子线程不同退出方式对主线程的影响
    //test_child_thread_exit(argv[1]);

    // 线程join
    struct timeb t1;
    ftime(&t1);
    time_t startTime = t1.millitm+t1.time*1000;
    result = pthread_create(&tid, NULL, test_pthread_join_handler, NULL);
    if(result != 0) {
        printf("创建线程失败，创建返回: %d", result);
        return 0;
    }
    void *reval;
    pthread_join(tid, &reval);
    ftime(&t1);
    time_t endTime = t1.millitm+t1.time*1000;
    assert((endTime - startTime) >= 2000);

    // 分离线程的作用
    // 分离状态的线程不能join
    // https://blog.51cto.com/youngyoungla/1765545

    return 0;
}
