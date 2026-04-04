/*
 * 与 demo-c++-visual-studio/demo-struct/main.cpp 等价的 C 版示例。
 * C 无 std::string，姓名用定长 char 数组表示；其余逻辑一致。
 *
 * 编译: gcc -std=c11 -Wall -Wextra -o demo-struct demo-struct.c
 *
 * 含「无 struct 标签、仅 typedef 命名」的匿名结构体类型示例。
 */

#include <stdio.h>
#include <string.h>

#define NAME_LEN 64

typedef struct {
  char name[NAME_LEN];
  int age;
} student;

typedef struct {
  char name[NAME_LEN];
  student stu;
} teacher;

/*
 * 匿名 struct（无标签 typedef）：`struct` 与 `{` 之间无标签名，类型仅由 typedef 末尾的 range_box 给出；
 * C 里不能写 `struct range_box`。上文 student、teacher 也是同一写法（相对带标签的 `struct foo { ... } x;`）。
 */
typedef struct {
  int tag;
  int lo;
  int hi;
} range_box;

int main(void) {
  /* 结构体定义和使用 */
  student stu = {"张三", 18};
  printf("名字：%s,年龄：%d\n", stu.name, stu.age);

  /* 结构体数组 */
  student stu_arr[] = {
      {"张一", 19},
      {"张二", 20},
  };
  for (int i = 0; i < 2; i++) {
    printf("[%d]名字：%s,年龄：%d\n", i, stu_arr[i].name, stu_arr[i].age);
  }

  /* 结构体指针 */
  student stu1 = {"张五", 21};
  student *p_stu = &stu1;
  printf("名字：%s,年龄：%d\n", p_stu->name, p_stu->age);

  /* 结构体嵌套结构体（name 为数组，不能整体赋值，用 strncpy；嵌套 student 可用复合字面量） */
  teacher teach;
  strncpy(teach.name, "老师1", sizeof teach.name - 1);
  teach.name[sizeof teach.name - 1] = '\0';
  teach.stu = (student){"张六", 22};
  printf("老师名字：%s，辅导学生名字：%s,辅导学生年龄：%d\n", teach.name,
         teach.stu.name, teach.stu.age);

  range_box r = {.tag = 1, .lo = 0, .hi = 255};
  printf("typedef 匿名 struct: tag=%d lo=%d hi=%d\n", r.tag, r.lo, r.hi);

  return 0;
}
