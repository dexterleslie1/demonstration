#include <iostream>
#include <assert.h>

using namespace std;

int main() {
	// http://m.biancheng.net/view/2035.html
	union data{
		int n;
		char ch;
		short m;
	};
	
	// 共用体占用的内存等于最长的成员占用的内存。共用体使用了内存覆盖技术，
	// 同一时刻只能保存一个成员的值，如果对新的成员赋值，就会把原来成员的值覆盖掉。
	union data a;
    printf("%d, %d\n", sizeof(a), sizeof(union data) );
    a.n = 0x40;
    printf("%X, %c, %hX\n", a.n, a.ch, a.m);
    a.ch = '9';
    printf("%X, %c, %hX\n", a.n, a.ch, a.m);
    a.m = 0x2059;
    printf("%X, %c, %hX\n", a.n, a.ch, a.m);
    a.n = 0x3E25AD54;
    printf("%X, %c, %hX\n", a.n, a.ch, a.m);

	// 共同体应用
	// 共用体在一般的编程中应用较少，在单片机中应用较多。对于 PC 机，
	// 经常使用到的一个实例是： 现有一张关于学生信息和教师信息的表格。
	// 学生信息包括姓名、编号、性别、职业、分数，教师的信息包括姓名、编号、性别、职业、教学科目。
	#define TOTAL 2  //人员总数
	struct{
		char name[20];
		int num;
		char sex;
		char profession;
		union{
			float score;
			char course[20];
		} sc;
	} bodys[TOTAL];

	int i;
    //输入人员信息
    for(i=0; i<TOTAL; i++){
        printf("Input info: ");
        scanf("%s %d %c %c", bodys[i].name, &(bodys[i].num), &(bodys[i].sex), &(bodys[i].profession));
        if(bodys[i].profession == 's'){  //如果是学生
            scanf("%f", &bodys[i].sc.score);
        }else{  //如果是老师
            scanf("%s", bodys[i].sc.course);
        }
        fflush(stdin);
    }
    //输出人员信息
    printf("\nName\t\tNum\tSex\tProfession\tScore / Course\n");
    for(i=0; i<TOTAL; i++){
        if(bodys[i].profession == 's'){  //如果是学生
            printf("%s\t%d\t%c\t%c\t\t%f\n", bodys[i].name, bodys[i].num, bodys[i].sex, bodys[i].profession, bodys[i].sc.score);
        }else{  //如果是老师
            printf("%s\t%d\t%c\t%c\t\t%s\n", bodys[i].name, bodys[i].num, bodys[i].sex, bodys[i].profession, bodys[i].sc.course);
        }
    }

	system("pause");
	return 0;
}