#include <iostream>
#include <assert.h>
#include <string>

using namespace std;

int main(int argc, char *argv[]) {
	// 使用项目属性编辑窗口在调试菜单中编辑命令行参数
	// 给argc和argv传递参数
	// https://wenku.baidu.com/view/5b4639f3b24e852458fb770bf78a6529647d35ea.html?_wkts_=1667883244537&bdQuery=visual+studio+%E4%BC%A0%E9%80%92%E5%8F%82%E6%95%B0
	printf("程序有 %d 个参数传入\n" , argc);

	printf("程序传入参数如下：\n");
	for(int i=0; i<argc; i++) {
		printf("参数%d=%s\n", (i+1), argv[i]);
	}

	system("pause");
	return 0;
}