//
// Created by dexterleslie on 22-11-29.
//
// 编译 g++ demo-array.cpp -o test

#include <iostream>

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
    int (*ptrTestArr2)[4] = &testArr2;
    cout << "数组testArr2内存首地址(表示第一个元素的地址，testArr2的指向是int类型): " << testArr2 << endl;
    cout << "数组&testArr2内存首地址(表示和二维数组对应的第一行地址，&testArr2的指向是int[4]类型): " << &testArr2 << endl;
    cout << "数组指针ptrTestArr2值(表示和二维数组对应的第一行地址，ptrTestArr2的指向是int[4]类型): " << ptrTestArr2 << endl;
    cout << "数组testArr2第一个元素内存地址: " << &testArr2[0] << endl;

    int *ptrTestArr21 = testArr2;
    cout << "数组指针ptrTestArr21=" << ptrTestArr21 << endl;
    cout << "数组指针*(ptrTestArr21+1)=" << *(ptrTestArr21 + 1) << endl;
    cout << "使用指针遍历数组: ";
    for (int i = 0; i < sizeof(testArr2) / sizeof(int); i++) {
        cout << "testArr2[" << i << "]=" << *ptrTestArr21 << ",";
        ptrTestArr21++;
    }
    cout << endl;

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
    int twoDimensionalArr2[2][2] = {{11, 12},
                                    {13, 15}};
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
    cout << "二维数组twoDimensionalArr4第一行元素个数："
         << sizeof(twoDimensionalArr4[0]) / sizeof(twoDimensionalArr4[0][0]) << endl;

    /* 二维数组内存地址 */
    cout << "二维数组twoDimensionalArr4首地址：" << twoDimensionalArr4 << endl;
    cout << "二维数组twoDimensionalArr4元素[0][0]地址：" << &twoDimensionalArr4[0][0] << endl;
    cout << "二维数组twoDimensionalArr4第二个元素地址，twoDimensionalArr4[0][1]=" << &twoDimensionalArr4[0][1] << endl;
    cout << "二维数组twoDimensionalArr4第一行地址：" << twoDimensionalArr4[0] << endl;
    cout << "二维数组twoDimensionalArr4第二行首地址，twoDimensionalArr4[1]=" << twoDimensionalArr4[1] << endl;
    cout << "二维数组twoDimensionalArr4第二行首地址，twoDimensionalArr4+1=" << twoDimensionalArr4 + 1 << endl;

    /*
     * 二维数组和指针 http://m.biancheng.net/view/2022.html
     * https://blog.csdn.net/men_wen/article/details/52694069
     * */

    // a是数组首元素首地址，pa存放的却是数组首地址，a的指向是char类型，a+1，a的值会实实在在的加1，而pa的指向是char[4]类型的，pa+1，pa则会加4，
    // 虽然数组的首地址和首元素首地址的值相同，但是两者操作不同，所以类型不匹配不能直接赋值，但是可以这样：pa = &a，pa相当与二维数组的行指针，现在它指向a[4]的地址。

    // 等价操作关系
    // twoDimensionalArr4+i == twoDimensionalArrPtr+i
    // twoDimensionalArr4[i] == twoDimensionalArrPtr[i] == *(twoDimensionalArr4+i) == *(twoDimensionalArrPtr+i)
    // twoDimensionalArr4[i][j] == twoDimensionalArrPtr[i][j] == *(twoDimensionalArr4[i]+j) == *(twoDimensionalArrPtr[i]+j) == *(*(twoDimensionalArr4+i)+j) == *(*(twoDimensionalArrPtr+i)+j)

    // 定义指向二维数组的指针变量
    // 括号中的*表明 p 是一个指针，它指向一个数组，数组的类型为int [4]，这正是 a 所包含的每个一维数组的类型。
    int (*twoDimensionalArrPtr)[2] = twoDimensionalArr4;
    // *(twoDimensionalArrPtr+1)表示取地址上的数据，也就是整个第 1 行数据。注意是一行数据，是多个数据，不是第 1 行中的第 0 个元素
    cout << "二维数组指针twoDimensionalArrPtr获取第1行数据，sizeof(*(twoDimensionalArrPtr+1))="
         << sizeof(*(twoDimensionalArrPtr + 1)) << endl;
    cout << "二维数组指针twoDimensionalArrPtr首地址，twoDimensionalArrPtr=" << twoDimensionalArrPtr << endl;
    cout << "二维数组指针twoDimensionalArrPtr第二行首地址，twoDimensionalArrPtr+1=" << twoDimensionalArrPtr + 1 << endl;
    cout << "二维数组指针twoDimensionalArrPtr第二行首地址，twoDimensionalArrPtr[1]=" << twoDimensionalArrPtr[1] << endl;
    cout << "二维数组指针twoDimensionalArrPtr第2行2列地址，twoDimensionalArrPtr[1]+1=" << twoDimensionalArrPtr[1] + 1
         << endl;
    cout << "二维数组指针twoDimensionalArrPtr第2行2列值，*(twoDimensionalArrPtr[1]+1)=" << *(twoDimensionalArrPtr[1] + 1)
         << endl;
    cout << "二维数组指针twoDimensionalArrPtr第1行1列值，twoDimensionalArrPtr[0][0]=" << twoDimensionalArrPtr[0][0]
         << endl;
    // **twoDimensionalArrPtr等价于*(*(twoDimensionalArrPtr+0)+0)
    // *(twoDimensionalArrPtr+0)单独使用时表示的是第 1 行数据，放在表达式中会被转换为第 1 行数据的首地址，
    // 也就是第 1 行第 0 个元素的地址，因为使用整行数据没有实际的含义，编译器遇到这种情况都会转换为指向该行第 0 个元素的指针；
    // 就像一维数组的名字，在定义时或者和 sizeof、& 一起使用时才表示整个数组，出现在表达式中就会被转换为指向数组第 0 个元素的指针。
    cout << "二维数组指针twoDimensionalArrPtr第1行1列值，**twoDimensionalArrPtr=" << **twoDimensionalArrPtr << endl;
    cout << "二维数组指针twoDimensionalArrPtr第2行2列值，*((*twoDimensionalArrPtr+1)+1)="
         << *(*(twoDimensionalArrPtr + 1) + 1) << endl;

    //指针数组和二维数组指针的区别
    //指针数组和二维数组指针在定义时非常相似，只是括号的位置不同：
    //int *(p1[5]);  //指针数组，可以去掉括号直接写作 int *p1[5];
    //int (*p2)[5];  //二维数组指针，不能去掉括号
    //指针数组和二维数组指针有着本质上的区别：指针数组是一个数组，只是每个元素保存的都是指针，以上面的 p1 为例，
    //在32位环境下它占用 4×5 = 20 个字节的内存。二维数组指针是一个指针，它指向一个二维数组，以上面的 p2 为例，它占用 4 个字节的内存。

    // 64位环境每个地址占用8个字节，因此sizeof(ptrArr)=24
    const char *ptrArr[] = {"hello", "world", "!"};
    cout << "指针数组sizeof(ptrArr)=" << sizeof(ptrArr) << endl;
    cout << "指针数组*(ptrArr+1)=" << *(ptrArr + 1) << endl;

    return 0;
}
