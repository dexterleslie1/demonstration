//
// Created by dexterleslie on 22-11-30.
//

#include <stdio.h>
#include <stdlib.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <ctype.h>

#define ServerPort 65533

int main() {
    int fd = socket(AF_INET, SOCK_STREAM, 0);
    if(fd == -1) {
        perror("socket error");
        exit(1);
    }

    struct sockaddr_in sockaddrIn;
    sockaddrIn.sin_family = AF_INET;
    sockaddrIn.sin_port = htons(ServerPort);
    sockaddrIn.sin_addr.s_addr = htonl(INADDR_ANY);
    int result = bind(fd, (struct sockaddr *)&sockaddrIn, sizeof(sockaddrIn));
    if(result == -1) {
        perror("bind error");
        exit(1);
    }

    result = listen(fd, 1024);
    if(result == -1) {
        perror("listen error");
        exit(1);
    }

    printf("server socket listen on port %d\n", ServerPort);

    struct sockaddr_in sockaddr_client;
    socklen_t socklen = sizeof(sockaddr_client);
    int fd_client = accept(fd, (struct sockaddr *)&sockaddr_client, &socklen);
    if(fd_client == -1) {
        perror("accept error");
        exit(1);
    }

    char clientIp[32];
    printf("client %s connected to server by using client port %d\n"
           , inet_ntop(AF_INET, &sockaddr_client.sin_addr.s_addr, clientIp, sizeof(clientIp))
           , ntohs(sockaddr_client.sin_port));

    char buffer[1024];
    while(1) {
        ssize_t length = read(fd_client, buffer, sizeof(buffer));
        if (length == -1) {
            perror("read error");
            exit(1);
        }

        for (int i = 0; i < length; i++) {
            buffer[i] = toupper(buffer[i]);
        }

        length = write(fd_client, buffer, length);
        if (length == -1) {
            perror("write error");
            exit(1);
        }
    }

    close(fd_client);
    close(fd);

    return 0;
}