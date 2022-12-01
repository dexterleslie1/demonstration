//
// Created by dexterleslie on 22-11-30.
//
// 使用 nc 192.168.1.181 95533 测试程序

#include <stdio.h>
#include <stdlib.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <ctype.h>
#include <signal.h>
#include <sys/wait.h>

#define ServerPort 65533

void handler_sig_child(int signo) {
    pid_t pid;
    while(-1 != (pid = wait(NULL))) {
        printf("child process %d terminated\n", pid);
    }
}

int main() {
    struct sigaction newact, oldact;
    newact.sa_handler  = handler_sig_child;
    sigemptyset(&newact.sa_mask);
    newact.sa_flags = 0;
    int result = sigaction(SIGCHLD, &newact, &oldact);
    if(result == -1) {
        perror("sigaction error");
        exit(1);
    }

    int fd = socket(AF_INET, SOCK_STREAM, 0);
    if (fd == -1) {
        perror("socket error");
        exit(1);
    }

    struct sockaddr_in sockaddrIn;
    sockaddrIn.sin_family = AF_INET;
    sockaddrIn.sin_port = htons(ServerPort);
    sockaddrIn.sin_addr.s_addr = htonl(INADDR_ANY);
    socklen_t socklen = sizeof(sockaddrIn);
    result = bind(fd, (struct sockaddr *) &sockaddrIn, socklen);
    if (result == -1) {
        perror("bind error");
        exit(1);
    }

    result = listen(fd, 1024);
    if (result == -1) {
        perror("listen error");
        exit(1);
    }

    printf("server socket listen on port %d\n", ServerPort);

    int isChild = 0;
    int fdClient;
    struct sockaddr_in sockaddrClient;
    socklen = sizeof(sockaddrClient);
    while(1) {
        fdClient = accept(fd, (struct sockaddr *) &sockaddrClient, &socklen);
        if (fdClient == -1) {
            perror("accept error");
            exit(1);
        }

        pid_t pid = fork();
        if(pid == -1) {
            perror("fork error");
            exit(1);
        } else if(pid > 0) {
            // 父进程不需要处理子进程socket fd
            close(fdClient);
            continue;
        } else {
            isChild = 1;
            break;
        }
    }

    close(fd);

    if(isChild) {
        char clientIp[32];
        printf("client %s connected to server by using port %d, child process id %d\n",
               inet_ntop(AF_INET, &sockaddrClient.sin_addr.s_addr, clientIp, sizeof(clientIp)),
               ntohs(sockaddrClient.sin_port),
               getpid());

        char buffer[1024];
        while (1) {
            size_t length = read(fdClient, buffer, sizeof(buffer));
            if (length == -1) {
                perror("read error");
                exit(1);
            } else if(length == 0) {
                break;
            }

            for (int i = 0; i < length; i++) {
                buffer[i] = toupper(buffer[i]);
            }

            length = write(fdClient, buffer, length);
            if (length == -1) {
                perror("write error");
                exit(1);
            }
        }

        close(fdClient);
    }

    return 0;
}