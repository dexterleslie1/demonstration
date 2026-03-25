#include <arpa/inet.h>
#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <unistd.h>

/* 与 server.c 中监听端口一致 */
#define PORT 8888
#define BUF_SIZE 1024

int main(int argc, char *argv[]) {
    /*
     * argv[1]：服务器 IPv4 点分十进制地址；缺省 127.0.0.1（本机）。
     * argv[2]：要发送的报文内容（不含自动换行）；缺省 "ping"。
     */
    const char *host = (argc > 1) ? argv[1] : "192.168.1.181";
    const char *msg = (argc > 2) ? argv[2] : "ping";
    int fd;
    /*
     * srv：对端（服务器）地址，供 sendto / recvfrom 使用。
     * srv_len：recvfrom 入参为缓冲区最大长度，返回后为实际地址结构长度（须取址传入）。
     */
    struct sockaddr_in srv;
    socklen_t srv_len = sizeof(srv);
    char buf[BUF_SIZE];
    ssize_t n;

    /*
     * 创建 UDP 套接字：AF_INET + SOCK_DGRAM，第三个参数 0 表示默认 UDP 协议。
     */
    fd = socket(AF_INET, SOCK_DGRAM, 0);
    if (fd < 0) {
        perror("socket");
        return 1;
    }

    /*
     * 将 srv 清零后填写服务器地址：族、端口（网络序）、IPv4 地址。
     * inet_pton(AF_INET, host, &sin_addr)：把文本 IP 转为二进制网络序；返回 1 表示成功。
     */
    memset(&srv, 0, sizeof(srv));
    srv.sin_family = AF_INET;
    srv.sin_port = htons(PORT);
    if (inet_pton(AF_INET, host, &srv.sin_addr) != 1) {
        fprintf(stderr, "invalid address: %s\n", host);
        close(fd);
        return 1;
    }

    /*
     * sendto：向 srv 发送一帧 UDP；strlen(msg) 为载荷字节数（不含 '\0'）。
     * 客户端未 bind 时，内核会为此套接字隐式绑定临时本地地址。
     */
    if (sendto(fd, msg, strlen(msg), 0, (struct sockaddr *)&srv, sizeof(srv)) < 0) {
        perror("sendto");
        close(fd);
        return 1;
    }

    /*
     * recvfrom：阻塞等待应答；buf 最多收 sizeof(buf)-1 字节，留一字节写 '\0'。
     * srv / srv_len 可得到应答来源（应与服务器一致）；若仅收载荷也可传 NULL。
     */
    n = recvfrom(fd, buf, sizeof(buf) - 1, 0, (struct sockaddr *)&srv, &srv_len);
    if (n < 0) {
        perror("recvfrom");
        close(fd);
        return 1;
    }
    buf[n] = '\0';
    printf("[%s:%u] %s\n", inet_ntoa(srv.sin_addr), ntohs(srv.sin_port), buf);

    close(fd);
    return 0;
}
