#include <iostream>
#include <string>
#include <sys/timeb.h>
#include <time.h>

using namespace std;

int main() {
	// https://blog.csdn.net/u012194446/article/details/103158664
	struct timeb t1;
    ftime(&t1);
    //t1.time是从UTC时间1970年1月1日午夜(00:00:00)起累计的秒数；t1.millitm是一秒内的毫秒数
    time_t ttt= t1.millitm+t1.time*1000;
    std::cout << ttt << std::endl;
    printf("%ld.%03d\n", t1.time, t1.millitm);

    std::cout << "1970 到目前经过秒数:" << t1.time << std::endl;
    tm *ltm2 = localtime(&t1.time);
    // 输出 tm 结构的各个组成部分
    std::cout << "年: "<< 1900 + ltm2->tm_year << std::endl;
    std::cout << "月: "<< 1 + ltm2->tm_mon<< std::endl;
    std::cout << "日: "<<  ltm2->tm_mday << std::endl;
    std::cout << "时间: "<< ltm2->tm_hour << ":" << ltm2->tm_min << ":" << ltm2->tm_sec << std::endl;

	system("pause");

	return 0;
}