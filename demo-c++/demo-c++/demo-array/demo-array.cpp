#include <iostream>
#include <stdlib.h>

using namespace std;

int main() {
	/* 数组定义 */
	// 数组定义方法1
	int arr[2];
	arr[0] = 1;
	arr[1] = 2;
	cout << "arr[0]=" << arr[0] << ",arr[1]=" << arr[1] << endl;

	// 数组定义方法2
	int arr1[2] = {11, 12};
	cout << "arr1[0]=" << arr1[0] << ",arr1[1]=" << arr1[1] << endl;

	// 数组定义方法3
	int arr2[] = {21, 22};
	cout << "arr2[0]=" << arr2[0] << ",arr2[1]=" << arr2[1] << endl;

	/* 数组内存中长度 */
	int testArr1[] = {1, 2, 3, 5};
	cout << "数组testArr1长度为: " << sizeof(testArr1) / sizeof(testArr1[0]) << endl;
	/* 数组内存首地址 */
	int testArr2[] = {1, 2, 3, 5};
	cout << "数组testArr2内存首地址: " << testArr2 << endl;
	cout << "数组testArr2第一个元素内存地址: " << &testArr2[0] << endl;

	/* 二维数组的四种定义方式 */
	// 方式1
	int twoDimensionalArr1[2][2];
	twoDimensionalArr1[0][0] = 1;
	twoDimensionalArr1[0][1] = 2;
	twoDimensionalArr1[1][0] = 3;
	twoDimensionalArr1[1][1] = 5;
	cout << "二维数组twoDimensionalArr1 [0][0]=" << twoDimensionalArr1[0][0] << ",[0][1]=" << twoDimensionalArr1[0][1] 
	<< ",[1][0]=" << twoDimensionalArr1[1][0] << ",[1][1]=" << twoDimensionalArr1[1][1] << endl;

	// 方式2
	int twoDimensionalArr2[2][2] = {{11, 12}, {13, 15}};
	cout << "二维数组twoDimensionalArr2 [0][0]=" << twoDimensionalArr2[0][0] << ",[0][1]=" << twoDimensionalArr2[0][1] 
	<< ",[1][0]=" << twoDimensionalArr2[1][0] << ",[1][1]=" << twoDimensionalArr2[1][1] << endl;

	// 方式3
	int twoDimensionalArr3[2][2] = {21, 22, 23, 25};
	cout << "二维数组twoDimensionalArr3 [0][0]=" << twoDimensionalArr3[0][0] << ",[0][1]=" << twoDimensionalArr3[0][1] 
	<< ",[1][0]=" << twoDimensionalArr3[1][0] << ",[1][1]=" << twoDimensionalArr3[1][1] << endl;

	// 方式4
	int twoDimensionalArr4[][2] = {31, 32, 33, 35};
	cout << "二维数组twoDimensionalArr4 [0][0]=" << twoDimensionalArr4[0][0] << ",[0][1]=" << twoDimensionalArr4[0][1] 
	<< ",[1][0]=" << twoDimensionalArr4[1][0] << ",[1][1]=" << twoDimensionalArr4[1][1] << endl;

	/* 二维数组占用内存空间 */
	cout << "二维数组twoDimensionalArr4占用内存空间: " << sizeof(twoDimensionalArr4) << endl;
	cout << "二维数组twoDimensionalArr4第一行元素个数：" << sizeof(twoDimensionalArr4[0]) / sizeof(twoDimensionalArr4[0][0]) << endl;

	/* 二维数组内存地址 */
	cout << "二维数组twoDimensionalArr4首地址：" << twoDimensionalArr4 << endl;
	cout << "二维数组twoDimensionalArr4元素[0][0]地址：" << &twoDimensionalArr4[0][0] << endl;
	cout << "二维数组twoDimensionalArr4第一行地址：" << twoDimensionalArr4[0] << endl;

	system("pause");
	return 0;
}