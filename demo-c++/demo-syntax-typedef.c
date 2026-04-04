/*
 * demo-syntax-typedef.c — typedef：为已有类型起别名
 *
 * 编译: gcc -std=c11 -Wall -Wextra -o demo-syntax-typedef demo-syntax-typedef.c
 *
 * 要点：C 中 typedef 不产生「与原名不相容」的新类型，别名与原类型同一类型；
 * 作用主要是可读性、少打字、以及把复杂声明（指针、数组、函数指针）收成单个名字。
 */

#include <stdio.h>

/* 标量别名：uint 即 unsigned int，写法更短 */
typedef unsigned int uint;

/*
 * 指针别名：int_ptr 等价于 int *。
 * 注意：`int_ptr a, b;` 中 a、b 均为 int *；而 `int *a, b;` 里只有 a 是指针，b 是 int。
 */
typedef int *int_ptr;

/* 结构体常用写法：struct 与 { 之间无标签，类型名只在 typedef 末尾出现一次 */
typedef struct {
  int x;
  int y;
} Point;

/*
 * 数组别名：IntRow10 表示「含 10 个 int 的数组」类型；
 * 变量声明 IntRow10 row 时，row 的类型与 int row[10] 相同（退化传参时仍是指向首元素的指针）。
 */
typedef int IntRow10[10];

int main(void) {
  uint u = 100U;
  int n = 1;
  int_ptr p = &n;

  Point pt = {2, 3};

  IntRow10 row = {0};
  row[0] = 9;

  printf("uint=%u *p=%d Point=(%d,%d) row[0]=%d\n", u, *p, pt.x, pt.y, row[0]);
  return 0;
}
