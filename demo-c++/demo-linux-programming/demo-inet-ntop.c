//
// Created by dexterleslie on 22-11-30.
//

#include <stdio.h>
#include <stdlib.h>
#include <arpa/inet.h>

int main() {
    // 点分格式的ip地址转换为十进制
    char *ptrIpAddr = "192.168.1.1";
    int ipAddrBinary = 0;
    int result = inet_pton(AF_INET, ptrIpAddr, &ipAddrBinary);
    if(result == 0) {
        printf("非法ip地址 %s\n", ptrIpAddr);
    } else if(result == -1) {
        perror("inet_pton error");
        exit(1);
    }
    printf("ip地址 %s 十六进制 %#X\n", ptrIpAddr, ipAddrBinary);

    // 十进制的ip地址转换为点分格式
    char ipAddrArr[32];
    const char *ptrResult = inet_ntop(AF_INET, &ipAddrBinary, ipAddrArr, sizeof(ipAddrArr));
    if(ptrResult == NULL) {
        perror("inet_ntop error");
        exit(1);
    }
    printf("十六进制 %#X ip地址 %s\n", ipAddrBinary, ipAddrArr);

    return 0;
}