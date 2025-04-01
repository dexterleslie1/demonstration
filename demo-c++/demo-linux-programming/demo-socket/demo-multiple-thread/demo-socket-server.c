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
#include <pthread.h>

#define ServerPort 65533

void* routine_socket(void *args) {
    int fdClient = *((int *)args);

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

    printf("client socket thread %ld terminated\n", pthread_self());

    close(fdClient);
}

int main() {
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
    int result = bind(fd, (struct sockaddr *) &sockaddrIn, socklen);
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

    int fdClient;
    struct sockaddr_in sockaddrClient;
    socklen = sizeof(sockaddrClient);
    while(1) {
        fdClient = accept(fd, (struct sockaddr *) &sockaddrClient, &socklen);
        if (fdClient == -1) {
            perror("accept error");
            exit(1);
        }

        pthread_t tid;
        result = pthread_create(&tid, NULL, routine_socket, &fdClient);
        if(result != 0) {
            perror("pthread_create error");
            exit(1);
        }

        char clientIp[32];
        printf("client %s connected to server by using port %d, thread id %ld\n",
               inet_ntop(AF_INET, &sockaddrClient.sin_addr.s_addr, clientIp, sizeof(clientIp)),
               ntohs(sockaddrClient.sin_port),
               tid);

        result = pthread_detach(tid);
        if(result != 0) {
            perror("pthread_detach error");
            exit(1);
        }
    }

    close(fd);

    return 0;
}