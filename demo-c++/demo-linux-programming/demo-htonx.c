//
// Created by dexterleslie on 22-11-30.
//

#include <stdio.h>
#include <arpa/inet.h>

int main() {
    // https://blog.csdn.net/m0_67400973/article/details/124487817

    // 本地字节顺序转换为网络字节顺序
    uint16_t aHost = 0x1234;
    uint16_t aNetwork = htons(aHost);
    printf("host byte order 0x%x convert to network byte order 0x%x\n", aHost, aNetwork);

    // 网络字节顺序转换为网络字节顺序
    aHost = ntohs(aNetwork);
    printf("network byte order 0x%x convert to host byte order 0x%x\n", aNetwork, aHost);

    return 0;
}
