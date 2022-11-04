#include <stdio.h>
#include <stdlib.h>
#include <time.h>

// 文件操作
// https://www.programiz.com/c-programming/c-file-input-output

int main() {
	srand(time(NULL));

	// 创建一个1g空白随机字节数组的文件
	FILE *file;
	file = fopen(".file.dat", "w+");
	if(file == NULL) {
		printf("无法创建文件 .file.dat");
		exit(1);
	}
	
	int total = 1024;
	int j=0;
	for(j=0; j<total; j++)
	{
 		char ch[1024];
		int length = sizeof(ch)/sizeof(ch[0]);
		int i=0;
		for(i=0; i<length; i++)
		{
			ch[i] = rand();
		}
		fwrite(ch, sizeof(ch), 1, file);
	}
	
	rewind(file);

	// 复制 .file.dat 到文件 .file2.dat
	FILE *file2;
	file2 = fopen(".file2.dat", "w+");
	if(file2 == NULL)
	{
		printf("无法创建文件 .file2.dat");
		exit(1);
	}

	for(j=0; j<total; j++)
	{
		char ch[1024];
		fread(ch, sizeof(ch), 1, file);
		fwrite(ch, sizeof(ch), 1, file2);
	}

	fclose(file2);
	fclose(file);
	
	return 0;
}

