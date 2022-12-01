//
// Created by dexterleslie on 22-11-30.
//

#include <stdio.h>
#include <stdlib.h>
#include <arpa/inet.h>
#include <unistd.h>

#define TargetHost "192.168.1.183"
#define TargetPort 65533

int main() {
    int fd = socket(AF_INET, SOCK_STREAM, 0);
    if(fd == -1) {
        perror("socket error");
        exit(1);
    }

    in_addr_t sockAddrBinary;
    int result = inet_pton(AF_INET, TargetHost, &sockAddrBinary);
    if(result == -1) {
        perror("inet_pton error");
        exit(1);
    }
    struct sockaddr_in sockaddrIn;
    sockaddrIn.sin_family = AF_INET;
    sockaddrIn.sin_port = htons(TargetPort);
    sockaddrIn.sin_addr.s_addr = sockAddrBinary;
    result = connect(fd, (struct sockaddr *)&sockaddrIn, sizeof(sockaddrIn));
    if(result == -1) {
        perror("connect error");
        exit(1);
    }

    while(1) {
        char buffer[128 * 1024];
        result = write(fd, buffer, sizeof(buffer));
        if (result == -1) {
            perror("write error");
            exit(1);
        }
    }

    close(fd);

    return 0;
}