/*************************************************************************
#	> File Name:demo-process-exec.c
#	> Author: dexterleslie@gmail.com
#	> Mail: dexterleslie@gmail.com
#	> Created Time: 2022年11月20日 星期日 14时06分44秒
 ************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

int main() {
	pid_t pid = fork();
	if(pid == -1) {
		perror("fork错误");
		exit(1);
	} else if(pid > 0) {
		printf("父进程输出，pid=%d, parent pid=%d，child pid=%d\n", getpid(), getppid(), pid);
	} else if(pid == 0) {
		// PATH环境变量自动搜索可执行文件
		// execlp("ls", "ls", "-l", NULL);
		
		// 指定可执行文件路径
		execl("/bin/ls", "ls", "-l", NULL);
		perror("命令执行错误");
	}

	sleep(1);

	return 0;
}
