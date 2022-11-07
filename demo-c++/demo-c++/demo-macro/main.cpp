#include <iostream>
#include <assert.h>
#include <string>

using namespace std;

int main() {
	// https://blog.csdn.net/weixin_39269366/article/details/120481303
	// https://blog.csdn.net/fuhanghang/article/details/109381490

	// 简单使用
#define MAXTIME 100
	int a = 10;
	if(a < MAXTIME) {
		cout << "a < MAXTIME" << endl;
	}

	// 带参数宏
#define Max(x, y) (x>y?x:y)
	int a1 = 20;
	int b1 = 30;
	int c1 = Max(20, 30);
	cout << a1 << "和" << b1 << "最大值：" << c1 << endl;

	// 宏特殊用法
	// https://xue.baidu.com/okam/pages/strategy-tp/index?strategyId=128458212189081&source=natural
	// ##符号是记号粘黏符号，将前后的字符粘黏起来。
#define A(x,y) x##y
	// #@ 符号会将宏的参数进行字符串字面量化，并且加‘’号
#define B(x) #@x
	// #符号会将宏的参数进行字符串字面量化，并且加""号
#define C(x) #x
	string s1 = A("abc", "def");
	cout << "A(\"abc\", \"def\")=" << s1 << endl;
	char ch1 = B(a);
	cout << "ch1=" << ch1 << endl;
	string s2 = C(kkkkkk);
	cout << "s2=" << s2 << endl;

	// 宏多行定义
#define PrintIf(x, y) if(x>y)\
	cout << "x>y" << endl;\
else if(x<y)\
	cout <<"x<y" << endl;

	int a11 = 10;
	int b11 = 20;
	PrintIf(a11, b11)

	// 定义宏和取消宏定义
#define _Test_M 1
#ifdef _Test_M
	cout << "_Test_M宏已经定义" << endl;
#else
	cout << "_Test_M宏没有定义" << endl;
#endif

#undef _Test_M
#ifdef _Test_M
	cout << "_Test_M宏已经定义" << endl;
#else
	cout << "_Test_M宏没有定义" << endl;
#endif

	system("pause");
	return 0;
}